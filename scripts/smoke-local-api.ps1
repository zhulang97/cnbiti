param(
  [switch]$ResetMySqlData,
  [int]$ApiPort = 18080
)

$ErrorActionPreference = "Stop"
$RepoRoot = Split-Path -Parent $PSScriptRoot
$MysqlHome = if ($env:MYSQL_HOME) { $env:MYSQL_HOME } else { "C:\Program Files\MySQL\MySQL Server 8.4" }
$Mysql = Join-Path $MysqlHome "bin\mysql.exe"
$MysqlAdmin = Join-Path $MysqlHome "bin\mysqladmin.exe"
$ApiProcess = $null
$ApiOutTask = $null
$ApiErrTask = $null
$MysqlWasListening = $false

function Test-TcpPort {
  param([int]$Port)

  $client = [System.Net.Sockets.TcpClient]::new()
  try {
    return $client.ConnectAsync("127.0.0.1", $Port).Wait(500)
  } finally {
    $client.Dispose()
  }
}

function Wait-TcpPort {
  param([int]$Port, [string]$Name, [int]$Seconds)

  for ($i = 0; $i -lt $Seconds; $i++) {
    if (Test-TcpPort $Port) {
      Start-Sleep -Milliseconds 500
      return
    }
    Start-Sleep -Seconds 1
  }

  throw "$Name did not become ready on port $Port."
}

function Test-MinioHealth {
  param([string]$Endpoint)

  try {
    $response = Invoke-WebRequest -UseBasicParsing -Uri "$Endpoint/minio/health/live" -TimeoutSec 5
    return $response.StatusCode -eq 200
  } catch {
    return $false
  }
}

function Resolve-MinioEndpoint {
  if ($env:MINIO_ENDPOINT -and (Test-MinioHealth $env:MINIO_ENDPOINT)) {
    return $env:MINIO_ENDPOINT.TrimEnd("/")
  }

  foreach ($endpoint in @("http://127.0.0.1:9002", "http://127.0.0.1:9000")) {
    if (Test-MinioHealth $endpoint) {
      return $endpoint
    }
  }

  return $(if ($env:MINIO_ENDPOINT) { $env:MINIO_ENDPOINT.TrimEnd("/") } else { "http://127.0.0.1:9002" })
}

function Test-DeleteBlocked {
  param([string]$Uri, [hashtable]$Headers)

  try {
    Invoke-RestMethod -Uri $Uri -Method Delete -Headers $Headers -TimeoutSec 20 | Out-Null
    return $false
  } catch {
    $response = $_.Exception.Response
    if ($response -and $response.StatusCode) {
      return [int]$response.StatusCode -eq 400
    }
    return $false
  }
}

if ($ResetMySqlData) {
  $stamp = Get-Date -Format "yyyyMMdd-HHmmss"
  foreach ($name in @(".mysql-data", ".mysql-run")) {
    $path = Join-Path $RepoRoot $name
    if (Test-Path $path) {
      $resolved = (Resolve-Path $path).Path
      if (-not $resolved.StartsWith($RepoRoot)) {
        throw "Refusing to move path outside workspace: $resolved"
      }
      Move-Item -LiteralPath $resolved -Destination (Join-Path $RepoRoot "$name.bak-smoke-$stamp")
    }
  }
}

New-Item -ItemType Directory -Force (Join-Path $RepoRoot ".tmp") | Out-Null
$RunId = Get-Date -Format "yyyyMMddHHmmss"

try {
  $MysqlWasListening = Test-TcpPort 3306
  & (Join-Path $PSScriptRoot "start-local-mysql.ps1")

  if (Test-TcpPort $ApiPort) {
    $originalPort = $ApiPort
    $ApiPort = 0
    for ($candidate = 18081; $candidate -le 18120; $candidate++) {
      if (-not (Test-TcpPort $candidate)) {
        $ApiPort = $candidate
        break
      }
    }
    if ($ApiPort -eq 0) {
      throw "No free API smoke-test port was found."
    }
    Write-Host "API smoke port $originalPort is busy; using $ApiPort instead."
  }

  $env:MYSQL_URL = "jdbc:mysql://127.0.0.1:3306/cnbjti?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai&characterEncoding=utf8"
  $env:MYSQL_USER = "cnbjti"
  $env:MYSQL_PASSWORD = "cnbjti"
  $resolvedMinioEndpoint = Resolve-MinioEndpoint
  $env:MINIO_ENDPOINT = $resolvedMinioEndpoint
  if (-not $env:MINIO_ACCESS_KEY) { $env:MINIO_ACCESS_KEY = "minioadmin" }
  if (-not $env:MINIO_SECRET_KEY) { $env:MINIO_SECRET_KEY = "minioadmin" }
  if (-not $env:MINIO_BUCKET) { $env:MINIO_BUCKET = "cnbjti-assets" }
  if (-not $env:MINIO_PUBLIC_URL) { $env:MINIO_PUBLIC_URL = "$resolvedMinioEndpoint/$env:MINIO_BUCKET" }
  $minioHealthy = Test-MinioHealth $resolvedMinioEndpoint
  Write-Host "MinIO endpoint: $resolvedMinioEndpoint; healthy=$minioHealthy"

  $jar = Join-Path $RepoRoot "apps\api\target\api-0.1.0-SNAPSHOT.jar"
  $java = (Get-Command java -ErrorAction Stop).Source
  $apiStartInfo = [System.Diagnostics.ProcessStartInfo]::new()
  $apiStartInfo.FileName = $java
  $apiStartInfo.UseShellExecute = $false
  $apiStartInfo.CreateNoWindow = $true
  $apiStartInfo.RedirectStandardOutput = $true
  $apiStartInfo.RedirectStandardError = $true
  $apiStartInfo.Arguments = "-jar `"$jar`" --server.port=$ApiPort"
  $ApiProcess = [System.Diagnostics.Process]::new()
  $ApiProcess.StartInfo = $apiStartInfo
  $ApiProcess.Start() | Out-Null
  $ApiOutTask = $ApiProcess.StandardOutput.ReadToEndAsync()
  $ApiErrTask = $ApiProcess.StandardError.ReadToEndAsync()

  Wait-TcpPort $ApiPort "API" 90

  $baseUrl = "http://127.0.0.1:$ApiPort"
  Write-Host "Checking public site config..."
  $site = Invoke-RestMethod -Uri "$baseUrl/api/public/site-config" -Method Get -TimeoutSec 20

  Write-Host "Checking admin login..."
  $loginBody = @{ username = "admin"; password = "cnbjti2026" } | ConvertTo-Json
  $login = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -ContentType "application/json" -Body $loginBody -TimeoutSec 20
  $authHeaders = @{ Authorization = "Bearer $($login.data.token)" }

  Write-Host "Checking admin user workflow..."
  $smokeAdminUsername = "smokeuser$RunId"
  $smokeAdminInitialPassword = "SmokePass$RunId!"
  $smokeAdminResetPassword = "ResetPass$RunId!"
  $adminUserCreateBody = @{
    username = $smokeAdminUsername
    displayName = "Smoke Sales"
    email = "$smokeAdminUsername@cnbjti.com"
    role = "SALES"
    status = "active"
    password = $smokeAdminInitialPassword
  } | ConvertTo-Json -Depth 4
  $createdAdminUser = Invoke-RestMethod -Uri "$baseUrl/api/admin/users" -Method Post -Headers $authHeaders -ContentType "application/json" -Body $adminUserCreateBody -TimeoutSec 20
  $adminUsers = Invoke-RestMethod -Uri "$baseUrl/api/admin/users" -Method Get -Headers $authHeaders -TimeoutSec 20
  $salesLoginBody = @{ username = $smokeAdminUsername; password = $smokeAdminInitialPassword } | ConvertTo-Json
  $salesLogin = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -ContentType "application/json" -Body $salesLoginBody -TimeoutSec 20
  $salesHeaders = @{ Authorization = "Bearer $($salesLogin.data.token)" }
  $salesRfqAllowed = $false
  $salesProductsForbidden = $false
  try {
    Invoke-RestMethod -Uri "$baseUrl/api/admin/rfqs" -Method Get -Headers $salesHeaders -TimeoutSec 20 | Out-Null
    $salesRfqAllowed = $true
  } catch {
    $salesRfqAllowed = $false
  }
  try {
    Invoke-RestMethod -Uri "$baseUrl/api/admin/products" -Method Get -Headers $salesHeaders -TimeoutSec 20 | Out-Null
  } catch {
    $response = $_.Exception.Response
    $salesProductsForbidden = $response -and [int]$response.StatusCode -eq 403
  }
  $adminUserDisableBody = @{
    username = $smokeAdminUsername
    displayName = "Smoke Editor"
    email = "$smokeAdminUsername-editor@cnbjti.com"
    role = "EDITOR"
    status = "disabled"
  } | ConvertTo-Json -Depth 4
  $disabledAdminUser = Invoke-RestMethod -Uri "$baseUrl/api/admin/users/$($createdAdminUser.data.id)" -Method Put -Headers $authHeaders -ContentType "application/json" -Body $adminUserDisableBody -TimeoutSec 20
  $disabledAdminLoginRejected = $false
  try {
    $disabledLoginBody = @{ username = $smokeAdminUsername; password = $smokeAdminInitialPassword } | ConvertTo-Json
    Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -ContentType "application/json" -Body $disabledLoginBody -TimeoutSec 20 | Out-Null
  } catch {
    $response = $_.Exception.Response
    $disabledAdminLoginRejected = $response -and [int]$response.StatusCode -eq 401
  }
  $adminUserPasswordBody = @{ password = $smokeAdminResetPassword } | ConvertTo-Json
  $resetAdminUser = Invoke-RestMethod -Uri "$baseUrl/api/admin/users/$($createdAdminUser.data.id)/password" -Method Put -Headers $authHeaders -ContentType "application/json" -Body $adminUserPasswordBody -TimeoutSec 20
  $adminUserEnableBody = @{
    username = $smokeAdminUsername
    displayName = "Smoke Editor"
    email = "$smokeAdminUsername-editor@cnbjti.com"
    role = "EDITOR"
    status = "active"
  } | ConvertTo-Json -Depth 4
  $enabledAdminUser = Invoke-RestMethod -Uri "$baseUrl/api/admin/users/$($createdAdminUser.data.id)" -Method Put -Headers $authHeaders -ContentType "application/json" -Body $adminUserEnableBody -TimeoutSec 20
  $editorLoginBody = @{ username = $smokeAdminUsername; password = $smokeAdminResetPassword } | ConvertTo-Json
  $editorLogin = Invoke-RestMethod -Uri "$baseUrl/api/auth/login" -Method Post -ContentType "application/json" -Body $editorLoginBody -TimeoutSec 20
  $editorHeaders = @{ Authorization = "Bearer $($editorLogin.data.token)" }
  $editorUsersForbidden = $false
  $editorProductsAllowed = $false
  $editorRfqsForbidden = $false
  try {
    Invoke-RestMethod -Uri "$baseUrl/api/admin/users" -Method Get -Headers $editorHeaders -TimeoutSec 20 | Out-Null
  } catch {
    $response = $_.Exception.Response
    $editorUsersForbidden = $response -and [int]$response.StatusCode -eq 403
  }
  try {
    Invoke-RestMethod -Uri "$baseUrl/api/admin/products" -Method Get -Headers $editorHeaders -TimeoutSec 20 | Out-Null
    $editorProductsAllowed = $true
  } catch {
    $editorProductsAllowed = $false
  }
  try {
    Invoke-RestMethod -Uri "$baseUrl/api/admin/rfqs" -Method Get -Headers $editorHeaders -TimeoutSec 20 | Out-Null
  } catch {
    $response = $_.Exception.Response
    $editorRfqsForbidden = $response -and [int]$response.StatusCode -eq 403
  }
  $adminUserWorkflowOk = $createdAdminUser.data.username -eq $smokeAdminUsername `
    -and @($adminUsers.data | Where-Object { $_.username -eq $smokeAdminUsername }).Count -gt 0 `
    -and $salesLogin.data.user.role -eq "SALES" `
    -and $salesRfqAllowed `
    -and $salesProductsForbidden `
    -and $disabledAdminUser.data.status -eq "disabled" `
    -and $disabledAdminLoginRejected `
    -and $resetAdminUser.data.id -eq $createdAdminUser.data.id `
    -and $enabledAdminUser.data.status -eq "active" `
    -and $editorLogin.data.user.role -eq "EDITOR" `
    -and $editorUsersForbidden `
    -and $editorProductsAllowed `
    -and $editorRfqsForbidden

  Write-Host "Checking admin site config workflow..."
  $originalSiteConfig = Invoke-RestMethod -Uri "$baseUrl/api/admin/site-config" -Method Get -Headers $authHeaders -TimeoutSec 20
  $siteUpdateBody = @{
    siteName = "CNBJTI Smoke $RunId"
    tagline = "Smoke-tested titanium supplier"
    email = "smoke-sales-$RunId@cnbjti.com"
    phone = "+86-917-0000000"
    whatsapp = "+8613800000000"
    address = "Smoke Test Road 88"
    city = "Baoji, Shaanxi"
    country = "China"
    socialLinks = @{
      linkedin = "https://linkedin.com/company/cnbjti-smoke"
      youtube = " "
    }
  } | ConvertTo-Json -Depth 8
  $updatedSiteConfig = Invoke-RestMethod -Uri "$baseUrl/api/admin/site-config" -Method Put -Headers $authHeaders -ContentType "application/json" -Body $siteUpdateBody -TimeoutSec 20
  $publicUpdatedSiteConfig = Invoke-RestMethod -Uri "$baseUrl/api/public/site-config" -Method Get -TimeoutSec 20
  $restoreSiteBody = $originalSiteConfig.data | ConvertTo-Json -Depth 8
  Invoke-RestMethod -Uri "$baseUrl/api/admin/site-config" -Method Put -Headers $authHeaders -ContentType "application/json" -Body $restoreSiteBody -TimeoutSec 20 | Out-Null

  Write-Host "Checking admin navigation workflow..."
  $originalNavigation = Invoke-RestMethod -Uri "$baseUrl/api/admin/navigation" -Method Get -Headers $authHeaders -TimeoutSec 20
  $navigationUpdateBody = @{
    items = @(
      @{
        label = "Products"
        children = @(
          @{ label = "Titanium Bar"; href = "/products/titanium-bar"; icon = "bar" },
          @{ label = "Titanium Sheet"; href = "/products/titanium-sheet"; icon = "sheet" }
        )
      },
      @{ label = "Technical Guides"; href = "/resources"; badge = "New" },
      @{ label = "Contact"; href = "/contact" }
    )
  } | ConvertTo-Json -Depth 10
  $updatedNavigation = Invoke-RestMethod -Uri "$baseUrl/api/admin/navigation" -Method Put -Headers $authHeaders -ContentType "application/json" -Body $navigationUpdateBody -TimeoutSec 20
  $publicUpdatedNavigation = Invoke-RestMethod -Uri "$baseUrl/api/public/navigation" -Method Get -TimeoutSec 20
  $restoreNavigationBody = @{ items = $originalNavigation.data } | ConvertTo-Json -Depth 10
  Invoke-RestMethod -Uri "$baseUrl/api/admin/navigation" -Method Put -Headers $authHeaders -ContentType "application/json" -Body $restoreNavigationBody -TimeoutSec 20 | Out-Null

  Write-Host "Checking admin content options..."
  $contentOptions = Invoke-RestMethod -Uri "$baseUrl/api/admin/content-options" -Method Get -Headers $authHeaders -TimeoutSec 20

  Write-Host "Checking reference data workflow..."
  $categoryBody = @{
    slug = "smoke-test-category-$RunId"
    name = "Smoke Test Category"
    description = "Smoke test category"
    imageUrl = "https://example.com/smoke-category.jpg"
    icon = "smoke"
    productCount = 1
    seo = @{
      title = "Smoke Test Category | CNBJTI"
      description = "Smoke test category SEO"
      noIndex = $false
    }
    status = "published"
  } | ConvertTo-Json -Depth 8
  $category = Invoke-RestMethod -Uri "$baseUrl/api/admin/categories" -Method Post -Headers $authHeaders -ContentType "application/json" -Body $categoryBody -TimeoutSec 20
  $gradeBody = @{
    slug = "smoke-test-grade-$RunId"
    name = "Smoke Test Titanium Grade"
    shortName = "Gr.SM"
    composition = "Smoke composition"
    description = "Smoke test grade"
    tensileStrength = "345 MPa min"
    yieldStrength = "275 MPa min"
    elongation = "20% min"
    density = 4.52
    applications = @("Chemical processing", "Smoke testing")
    status = "published"
  } | ConvertTo-Json -Depth 8
  $grade = Invoke-RestMethod -Uri "$baseUrl/api/admin/grades" -Method Post -Headers $authHeaders -ContentType "application/json" -Body $gradeBody -TimeoutSec 20
  $standardBody = @{
    slug = "smoke-test-standard-$RunId"
    code = "ASTM SM-$RunId"
    name = "Smoke Test Titanium Standard"
    description = "Smoke test standard"
    productTypes = @("Sheet", "Anode")
    status = "published"
  } | ConvertTo-Json -Depth 8
  $standard = Invoke-RestMethod -Uri "$baseUrl/api/admin/standards" -Method Post -Headers $authHeaders -ContentType "application/json" -Body $standardBody -TimeoutSec 20
  $referenceProductSlug = "smoke-reference-product-$RunId"
  $referenceProductBody = @{
    name = "Smoke Reference Product"
    slug = $referenceProductSlug
    categoryId = $category.data.id
    gradeIds = @($grade.data.id)
    standardIds = @($standard.data.id)
    status = "published"
    shortDescription = "Smoke product using managed reference data"
    description = "Created by reference data smoke test"
    imageUrl = "https://example.com/smoke-reference-product.jpg"
    featured = $false
    inStock = $true
  } | ConvertTo-Json -Depth 8
  $referenceProduct = Invoke-RestMethod -Uri "$baseUrl/api/admin/products" -Method Post -Headers $authHeaders -ContentType "application/json" -Body $referenceProductBody -TimeoutSec 20
  $updatedCategorySlug = "updated-smoke-test-category-$RunId"
  $categoryUpdateBody = @{
    slug = $updatedCategorySlug
    name = "Updated Smoke Test Category"
    description = "Updated smoke test category"
    imageUrl = "https://example.com/smoke-category-updated.jpg"
    icon = "smoke"
    productCount = 2
    status = "published"
  } | ConvertTo-Json -Depth 8
  $updatedCategory = Invoke-RestMethod -Uri "$baseUrl/api/admin/categories/$($category.data.id)" -Method Put -Headers $authHeaders -ContentType "application/json" -Body $categoryUpdateBody -TimeoutSec 20
  $gradeUpdateBody = @{
    slug = "updated-smoke-test-grade-$RunId"
    name = "Updated Smoke Test Titanium Grade"
    shortName = "Gr.SU"
    composition = "Updated smoke composition"
    description = "Updated smoke test grade"
    tensileStrength = "350 MPa min"
    yieldStrength = "280 MPa min"
    elongation = "19% min"
    density = 4.52
    applications = @("Chemical processing")
    status = "published"
  } | ConvertTo-Json -Depth 8
  $updatedGrade = Invoke-RestMethod -Uri "$baseUrl/api/admin/grades/$($grade.data.id)" -Method Put -Headers $authHeaders -ContentType "application/json" -Body $gradeUpdateBody -TimeoutSec 20
  $standardUpdateBody = @{
    slug = "updated-smoke-test-standard-$RunId"
    code = "ASTM SMU-$RunId"
    name = "Updated Smoke Test Titanium Standard"
    description = "Updated smoke test standard"
    productTypes = @("Anode")
    status = "published"
  } | ConvertTo-Json -Depth 8
  $updatedStandard = Invoke-RestMethod -Uri "$baseUrl/api/admin/standards/$($standard.data.id)" -Method Put -Headers $authHeaders -ContentType "application/json" -Body $standardUpdateBody -TimeoutSec 20
  $publicReferenceProduct = Invoke-RestMethod -Uri "$baseUrl/api/public/products/$referenceProductSlug" -Method Get -TimeoutSec 20
  $categoryDeleteBlocked = Test-DeleteBlocked -Uri "$baseUrl/api/admin/categories/$($category.data.id)" -Headers $authHeaders
  $gradeDeleteBlocked = Test-DeleteBlocked -Uri "$baseUrl/api/admin/grades/$($grade.data.id)" -Headers $authHeaders
  $standardDeleteBlocked = Test-DeleteBlocked -Uri "$baseUrl/api/admin/standards/$($standard.data.id)" -Headers $authHeaders
  Invoke-RestMethod -Uri "$baseUrl/api/admin/products/$($referenceProduct.data.id)" -Method Delete -Headers $authHeaders -TimeoutSec 20 | Out-Null
  Invoke-RestMethod -Uri "$baseUrl/api/admin/categories/$($category.data.id)" -Method Delete -Headers $authHeaders -TimeoutSec 20 | Out-Null
  Invoke-RestMethod -Uri "$baseUrl/api/admin/grades/$($grade.data.id)" -Method Delete -Headers $authHeaders -TimeoutSec 20 | Out-Null
  Invoke-RestMethod -Uri "$baseUrl/api/admin/standards/$($standard.data.id)" -Method Delete -Headers $authHeaders -TimeoutSec 20 | Out-Null

  Write-Host "Checking customer detail workflow..."
  $customers = Invoke-RestMethod -Uri "$baseUrl/api/admin/customers" -Method Get -Headers $authHeaders -TimeoutSec 20
  $firstCustomerId = $customers.data[0].id
  $customerDetail = Invoke-RestMethod -Uri "$baseUrl/api/admin/customers/$firstCustomerId" -Method Get -Headers $authHeaders -TimeoutSec 20
  $customerUpdateBody = @{
    notes = "Smoke-test customer follow-up note"
    lastContact = "2026-05-03"
  } | ConvertTo-Json
  $updatedCustomer = Invoke-RestMethod -Uri "$baseUrl/api/admin/customers/$firstCustomerId" -Method Put -Headers $authHeaders -ContentType "application/json" -Body $customerUpdateBody -TimeoutSec 20

  Write-Host "Creating and deleting admin product..."
  $productBody = @{
    name = "Smoke Test Titanium Sheet"
    slug = "smoke-test-titanium-sheet-$RunId"
    categoryId = "2"
    gradeIds = @("gr2")
    standardIds = @("s2")
    status = "draft"
    shortDescription = "Smoke test product"
    description = "Created by scripts/smoke-local-api.ps1"
    imageUrl = "https://example.com/smoke-product.jpg"
    images = @(@{
      id = "smoke-product-front"
      url = "https://example.com/smoke-product-front.jpg"
      alt = "Smoke Test Titanium Sheet front"
      mimeType = "image/jpeg"
      size = 1000
      filename = "smoke-product-front.jpg"
    })
    specs = @(
      @{ label = "Thickness"; value = "1 - 20"; unit = "mm" },
      @{ label = "Surface"; value = "Mill finish"; unit = "" }
    )
    seo = @{
      title = "Smoke Test Titanium Sheet | CNBJTI"
      description = "Smoke test product SEO description"
      canonical = "https://example.com/smoke-test-titanium-sheet"
      ogTitle = "Smoke Test Titanium Sheet"
      ogDescription = "Smoke test product SEO description"
      ogImage = "https://example.com/smoke-product-front.jpg"
      noIndex = $false
    }
    featured = $false
    inStock = $true
  } | ConvertTo-Json -Depth 8
  $product = Invoke-RestMethod -Uri "$baseUrl/api/admin/products" -Method Post -Headers $authHeaders -ContentType "application/json" -Body $productBody -TimeoutSec 20
  $productUpdateSlug = "updated-smoke-test-titanium-sheet-$RunId"
  $productUpdateBody = @{
    name = "Updated Smoke Test Titanium Sheet"
    slug = $productUpdateSlug
    categoryId = "2"
    gradeIds = @("gr2", "gr5")
    standardIds = @("s2")
    status = "published"
    shortDescription = "Updated smoke test product"
    description = "Updated by scripts/smoke-local-api.ps1"
    imageUrl = "https://example.com/smoke-product-updated.jpg"
    images = @(
      @{
        id = "smoke-product-front-updated"
        url = "https://example.com/smoke-product-front-updated.jpg"
        alt = "Updated Smoke Test Titanium Sheet front"
        mimeType = "image/jpeg"
        size = 1100
        filename = "smoke-product-front-updated.jpg"
      },
      @{
        id = "smoke-product-detail-updated"
        url = "https://example.com/smoke-product-detail-updated.jpg"
        alt = "Updated Smoke Test Titanium Sheet detail"
        mimeType = "image/jpeg"
        size = 1200
        filename = "smoke-product-detail-updated.jpg"
      }
    )
    specs = @(
      @{ label = "Thickness"; value = "2 - 30"; unit = "mm" },
      @{ label = "Width"; value = "Up to 1500"; unit = "mm" }
    )
    seo = @{
      title = "Updated Smoke Test Titanium Sheet | CNBJTI"
      description = "Updated smoke test product SEO description"
      canonical = "https://example.com/updated-smoke-test-titanium-sheet"
      ogTitle = "Updated Smoke Test Titanium Sheet"
      ogDescription = "Updated smoke test product SEO description"
      ogImage = "https://example.com/smoke-product-front-updated.jpg"
      noIndex = $true
    }
    featured = $true
    inStock = $false
  } | ConvertTo-Json -Depth 8
  $updatedProduct = Invoke-RestMethod -Uri "$baseUrl/api/admin/products/$($product.data.id)" -Method Put -Headers $authHeaders -ContentType "application/json" -Body $productUpdateBody -TimeoutSec 20
  $productDetail = Invoke-RestMethod -Uri "$baseUrl/api/admin/products/$($product.data.id)" -Method Get -Headers $authHeaders -TimeoutSec 20
  $publicProduct = Invoke-RestMethod -Uri "$baseUrl/api/public/products/$productUpdateSlug" -Method Get -TimeoutSec 20
  Invoke-RestMethod -Uri "$baseUrl/api/admin/products/$($product.data.id)" -Method Delete -Headers $authHeaders -TimeoutSec 20 | Out-Null

  Write-Host "Creating, publishing, and deleting admin article..."
  $articleBody = @{
    title = "Smoke Test Titanium Buying Guide"
    slug = "smoke-test-titanium-buying-guide-$RunId"
    category = "Procurement"
    status = "draft"
    excerpt = "Smoke test article"
    content = "Created by scripts/smoke-local-api.ps1"
    coverImageUrl = "https://example.com/smoke-article.jpg"
    tags = @("Titanium", "Smoke")
    publishedAt = ""
    readingTime = 3
  } | ConvertTo-Json -Depth 5
  $article = Invoke-RestMethod -Uri "$baseUrl/api/admin/articles" -Method Post -Headers $authHeaders -ContentType "application/json" -Body $articleBody -TimeoutSec 20
  $articleStatusBody = @{ status = "published" } | ConvertTo-Json
  $publishedArticle = Invoke-RestMethod -Uri "$baseUrl/api/admin/articles/$($article.data.id)/status" -Method Put -Headers $authHeaders -ContentType "application/json" -Body $articleStatusBody -TimeoutSec 20
  Invoke-RestMethod -Uri "$baseUrl/api/admin/articles/$($article.data.id)" -Method Delete -Headers $authHeaders -TimeoutSec 20 | Out-Null

  Write-Host "Creating RFQ..."
  $rfqBody = @{
    productType = "Titanium Bar"
    grade = "Gr5"
    quantity = "10 kg"
    dimensions = "Smoke test"
    message = "Local MySQL smoke test"
    name = "Codex Smoke"
    company = "CNBJTI Dev"
    email = "smoke@example.com"
    phone = "+86 10000000000"
    destinationCountry = "China"
  } | ConvertTo-Json
  $rfq = Invoke-RestMethod -Uri "$baseUrl/api/public/rfqs" -Method Post -ContentType "application/json" -Body $rfqBody -TimeoutSec 20

  Write-Host "Checking contact message workflow..."
  $contactBody = @{
    name = "Codex Contact Smoke"
    company = "CNBJTI Dev"
    email = "contact-smoke@example.com"
    subject = "Smoke contact inquiry"
    message = "Please confirm Grade 2 titanium sheet availability."
    source = "smoke-local-api"
  } | ConvertTo-Json
  $contactMessage = Invoke-RestMethod -Uri "$baseUrl/api/public/contact-messages" -Method Post -ContentType "application/json" -Body $contactBody -TimeoutSec 20
  $contactMessages = Invoke-RestMethod -Uri "$baseUrl/api/admin/contact-messages" -Method Get -Headers $authHeaders -TimeoutSec 20
  $contactStatusBody = @{ status = "replied" } | ConvertTo-Json
  $updatedContactMessage = Invoke-RestMethod -Uri "$baseUrl/api/admin/contact-messages/$($contactMessage.data.id)/status" -Method Put -Headers $authHeaders -ContentType "application/json" -Body $contactStatusBody -TimeoutSec 20
  $contactNotesBody = @{ notes = "Smoke contact message handled." } | ConvertTo-Json
  $notedContactMessage = Invoke-RestMethod -Uri "$baseUrl/api/admin/contact-messages/$($contactMessage.data.id)/notes" -Method Put -Headers $authHeaders -ContentType "application/json" -Body $contactNotesBody -TimeoutSec 20

  $uploadOk = $false
  $uploadedUrl = ""
  $attachmentRfqOk = $false
  $mediaListOk = $false
  $mediaDeleteOk = $false
  $mediaReferenceBlocked = $false
  $auditLogOk = $false
  if ($minioHealthy) {
    Write-Host "Uploading smoke file to MinIO through API..."
    $uploadPath = Join-Path $RepoRoot ".tmp\minio-upload-smoke.txt"
    "CNBJTI MinIO upload smoke test" | Set-Content -Encoding ASCII -Path $uploadPath
    Write-Host "Smoke upload file ready: $uploadPath"
    $curl = (Get-Command curl.exe -ErrorAction Stop).Source
    Write-Host "Using curl: $curl"
    $uploadResponsePath = Join-Path $RepoRoot ".tmp\minio-upload-response.json"
    Remove-Item -LiteralPath $uploadResponsePath -ErrorAction SilentlyContinue
    Write-Host "curl started, waiting for response..."
    $statusLine = & $curl --silent --show-error --max-time 30 `
      --output $uploadResponsePath `
      --write-out "%{http_code}" `
      --header "Authorization: Bearer $($login.data.token)" `
      --form "file=@$uploadPath;type=text/plain" `
      "$baseUrl/api/public/uploads"
    if ($LASTEXITCODE -ne 0) {
      $uploadBody = if (Test-Path $uploadResponsePath) { Get-Content -Raw $uploadResponsePath } else { "" }
      throw "Upload smoke failed: curl exited with $LASTEXITCODE. $uploadBody"
    }
    Write-Host "curl exited with HTTP $statusLine"
    $responseBody = if (Test-Path $uploadResponsePath) { Get-Content -Raw $uploadResponsePath } else { "" }
    if ($statusLine -ne "200") {
      throw "Upload smoke failed: HTTP $statusLine $responseBody"
    }
    $upload = $responseBody | ConvertFrom-Json
    if ($upload.code -ne "OK") {
      throw "Upload smoke failed: $responseBody"
    }
    $uploadOk = [bool]$upload.data.id
    $uploadedUrl = $upload.data.url

    Write-Host "Checking admin media file list..."
    $mediaList = Invoke-RestMethod -Uri "$baseUrl/api/admin/files" -Method Get -Headers $authHeaders -TimeoutSec 20
    $mediaListOk = @($mediaList.data | Where-Object {
      $_.id -eq $upload.data.id -and $_.filename -eq "minio-upload-smoke.txt" -and $_.url -eq $upload.data.url
    }).Count -gt 0

    Write-Host "Checking media reference delete protection..."
    $mediaReferenceProductBody = @{
      name = "Smoke Media Reference Product"
      slug = "smoke-media-reference-product-$RunId"
      categoryId = "2"
      gradeIds = @("gr2")
      standardIds = @("s2")
      status = "draft"
      shortDescription = "Smoke product referencing an uploaded media file"
      description = "Created to verify media delete protection"
      imageUrl = $upload.data.url
      images = @($upload.data)
      featured = $false
      inStock = $true
    } | ConvertTo-Json -Depth 8
    $mediaReferenceProduct = Invoke-RestMethod -Uri "$baseUrl/api/admin/products" -Method Post -Headers $authHeaders -ContentType "application/json" -Body $mediaReferenceProductBody -TimeoutSec 20
    $mediaReferenceBlocked = Test-DeleteBlocked -Uri "$baseUrl/api/admin/files/$($upload.data.id)" -Headers $authHeaders
    Invoke-RestMethod -Uri "$baseUrl/api/admin/products/$($mediaReferenceProduct.data.id)" -Method Delete -Headers $authHeaders -TimeoutSec 20 | Out-Null

    Write-Host "Uploading and deleting disposable media file..."
    $deleteUploadPath = Join-Path $RepoRoot ".tmp\minio-delete-smoke.txt"
    "CNBJTI MinIO delete smoke test" | Set-Content -Encoding ASCII -Path $deleteUploadPath
    $deleteUploadResponsePath = Join-Path $RepoRoot ".tmp\minio-delete-upload-response.json"
    Remove-Item -LiteralPath $deleteUploadResponsePath -ErrorAction SilentlyContinue
    $deleteStatusLine = & $curl --silent --show-error --max-time 30 `
      --output $deleteUploadResponsePath `
      --write-out "%{http_code}" `
      --header "Authorization: Bearer $($login.data.token)" `
      --form "file=@$deleteUploadPath;type=text/plain" `
      "$baseUrl/api/public/uploads"
    if ($LASTEXITCODE -ne 0) {
      $deleteUploadBody = if (Test-Path $deleteUploadResponsePath) { Get-Content -Raw $deleteUploadResponsePath } else { "" }
      throw "Disposable media upload failed: curl exited with $LASTEXITCODE. $deleteUploadBody"
    }
    $deleteUploadBody = if (Test-Path $deleteUploadResponsePath) { Get-Content -Raw $deleteUploadResponsePath } else { "" }
    if ($deleteStatusLine -ne "200") {
      throw "Disposable media upload failed: HTTP $deleteStatusLine $deleteUploadBody"
    }
    $deleteUpload = $deleteUploadBody | ConvertFrom-Json
    if ($deleteUpload.code -ne "OK") {
      throw "Disposable media upload failed: $deleteUploadBody"
    }
    $mediaBeforeDelete = Invoke-RestMethod -Uri "$baseUrl/api/admin/files" -Method Get -Headers $authHeaders -TimeoutSec 20
    $listedBeforeDelete = @($mediaBeforeDelete.data | Where-Object { $_.id -eq $deleteUpload.data.id }).Count -gt 0
    Invoke-RestMethod -Uri "$baseUrl/api/admin/files/$($deleteUpload.data.id)" -Method Delete -Headers $authHeaders -TimeoutSec 20 | Out-Null
    $mediaAfterDelete = Invoke-RestMethod -Uri "$baseUrl/api/admin/files" -Method Get -Headers $authHeaders -TimeoutSec 20
    $listedAfterDelete = @($mediaAfterDelete.data | Where-Object { $_.id -eq $deleteUpload.data.id }).Count -gt 0
    $mediaDeleteOk = $listedBeforeDelete -and -not $listedAfterDelete

    Write-Host "Creating RFQ with uploaded attachment..."
    $attachmentRfqBody = @{
      productType = "Titanium Sheet"
      grade = "Gr2"
      quantity = "5 kg"
      dimensions = "Attachment smoke test"
      message = "RFQ with uploaded attachment"
      name = "Codex Attachment Smoke"
      company = "CNBJTI Dev"
      email = "attachment-smoke@example.com"
      phone = "+86 10000000000"
      destinationCountry = "China"
      attachments = @($upload.data)
    } | ConvertTo-Json -Depth 8
    $attachmentRfq = Invoke-RestMethod -Uri "$baseUrl/api/public/rfqs" -Method Post -ContentType "application/json" -Body $attachmentRfqBody -TimeoutSec 20
    $attachmentRfqOk = [bool]$attachmentRfq.data.rfqNo
  }

  Write-Host "Checking admin audit logs..."
  $auditLogsPage = Invoke-RestMethod -Uri "$baseUrl/api/admin/audit-logs?page=1&pageSize=100" -Method Get -Headers $authHeaders -TimeoutSec 20
  $auditLogs = @($auditLogsPage.data.items)
  $contactAuditKeyword = [uri]::EscapeDataString($contactMessage.data.id)
  $contactAuditLogsPage = Invoke-RestMethod -Uri "$baseUrl/api/admin/audit-logs?targetType=CONTACT_MESSAGE&keyword=$contactAuditKeyword&status=success&page=1&pageSize=10" -Method Get -Headers $authHeaders -TimeoutSec 20
  $auditFilterOk = $contactAuditLogsPage.data.total -gt 0 `
    -and $contactAuditLogsPage.data.summary.failed -eq 0 `
    -and @($contactAuditLogsPage.data.items | Where-Object { $_.targetType -eq "CONTACT_MESSAGE" -and $_.targetId -eq $contactMessage.data.id -and $_.statusCode -eq 200 }).Count -gt 0
  $auditLogOk = $auditLogsPage.data.total -ge $auditLogs.Count `
    -and $auditLogsPage.data.summary.total -eq $auditLogsPage.data.total `
    -and @($auditLogs | Where-Object { $_.targetType -eq "SITE_CONFIG" -and $_.action -eq "UPDATE" -and $_.statusCode -eq 200 }).Count -gt 0 `
    -and @($auditLogs | Where-Object { $_.targetType -eq "PRODUCT" -and $_.action -eq "CREATE" -and $_.statusCode -eq 200 }).Count -gt 0 `
    -and @($auditLogs | Where-Object { $_.targetType -eq "CONTACT_MESSAGE" -and $_.targetId -eq $contactMessage.data.id -and $_.statusCode -eq 200 }).Count -gt 0 `
    -and @($auditLogs | Where-Object { $_.targetType -eq "ADMIN_USER" -and $_.targetId -eq $createdAdminUser.data.id -and $_.statusCode -eq 200 }).Count -gt 0 `
    -and $auditFilterOk
  if ($minioHealthy) {
    $auditLogOk = $auditLogOk `
      -and @($auditLogs | Where-Object { $_.targetType -eq "FILE" -and $_.action -eq "UPLOAD" -and $_.statusCode -eq 200 }).Count -gt 0 `
      -and @($auditLogs | Where-Object { $_.targetType -eq "FILE" -and $_.action -eq "DELETE" -and $_.statusCode -eq 200 }).Count -gt 0 `
      -and @($auditLogs | Where-Object { $_.targetType -eq "FILE" -and $_.action -eq "DELETE" -and $_.statusCode -eq 400 }).Count -gt 0
  }

  Write-Host "Exporting RFQ CSV..."
  $csv = Invoke-WebRequest -UseBasicParsing -Uri "$baseUrl/api/admin/rfqs/export.csv" -Method Get -Headers $authHeaders -TimeoutSec 20
  $csvOk = $csv.StatusCode -eq 200 -and $csv.Content.Contains("RFQ No")
  if ($uploadOk) {
    $csvOk = $csvOk -and $csv.Content.Contains("minio-upload-smoke.txt")
  }

  $dbSummary = & $Mysql --protocol=tcp --host=127.0.0.1 --port=3306 --user=cnbjti --password=cnbjti --database=cnbjti -N -e @"
SHOW TABLES;
SELECT 'catalog_content', COUNT(*) FROM catalog_content
UNION ALL SELECT 'admin_users', COUNT(*) FROM admin_users
UNION ALL SELECT 'rfqs', COUNT(*) FROM rfqs
UNION ALL SELECT 'contact_messages', COUNT(*) FROM contact_messages
UNION ALL SELECT 'customers', COUNT(*) FROM customers
UNION ALL SELECT 'stored_files', COUNT(*) FROM stored_files;
"@

  [pscustomobject]@{
    SiteConfigOk = [bool]$site.data
    AdminLoginOk = [bool]$login.data.token
    AdminUserOk = $adminUserWorkflowOk
    SiteConfigAdminOk = $updatedSiteConfig.data.siteName -eq "CNBJTI Smoke $RunId" `
      -and $publicUpdatedSiteConfig.data.email -eq "smoke-sales-$RunId@cnbjti.com" `
      -and -not $updatedSiteConfig.data.socialLinks.PSObject.Properties.Name.Contains("youtube")
    NavigationAdminOk = $updatedNavigation.data[1].label -eq "Technical Guides" `
      -and $updatedNavigation.data[1].badge -eq "New" `
      -and $publicUpdatedNavigation.data[0].children[1].href -eq "/products/titanium-sheet"
    ContentOptionsOk = [bool]($contentOptions.data.categories.Count -gt 0)
    ReferenceDataOk = $updatedCategory.data.slug -eq $updatedCategorySlug `
      -and $updatedGrade.data.shortName -eq "Gr.SU" `
      -and $updatedStandard.data.name -eq "Updated Smoke Test Titanium Standard" `
      -and $publicReferenceProduct.data.category.slug -eq $updatedCategorySlug `
      -and $publicReferenceProduct.data.grades[0].shortName -eq "Gr.SU" `
      -and $publicReferenceProduct.data.standards[0].code -eq "ASTM SMU-$RunId" `
      -and $categoryDeleteBlocked `
      -and $gradeDeleteBlocked `
      -and $standardDeleteBlocked
    CustomerDetailOk = [bool]($customerDetail.data.email) -and $updatedCustomer.data.notes -eq "Smoke-test customer follow-up note"
    ProductCrudOk = $updatedProduct.data.status -eq "published" `
      -and $productDetail.data.images[1].filename -eq "smoke-product-detail-updated.jpg" `
      -and $productDetail.data.specs[0].label -eq "Thickness" `
      -and $productDetail.data.seo.noIndex -eq $true `
      -and $publicProduct.data.images[0].url -eq "https://example.com/smoke-product-front-updated.jpg"
    ArticleCrudOk = $publishedArticle.data.status -eq "published"
    ContactMessageOk = [bool]$contactMessage.data.id `
      -and @($contactMessages.data | Where-Object { $_.id -eq $contactMessage.data.id }).Count -gt 0 `
      -and $updatedContactMessage.data.status -eq "replied" `
      -and $notedContactMessage.data.notes -eq "Smoke contact message handled."
    RfqCsvOk = $csvOk
    CreatedRfq = $rfq.data.rfqNo
    UploadOk = $uploadOk
    MediaListOk = $mediaListOk
    MediaReferenceBlocked = $mediaReferenceBlocked
    MediaDeleteOk = $mediaDeleteOk
    AuditLogOk = $auditLogOk
    AuditLogFilterOk = $auditFilterOk
    AttachmentRfqOk = $attachmentRfqOk
    UploadedUrl = $uploadedUrl
    DatabaseSummary = ($dbSummary -join "; ")
  }
} catch {
  if ($ApiProcess -and $ApiProcess.HasExited) {
    Write-Host "API process exited with code $($ApiProcess.ExitCode)."
  }
  if ($ApiProcess -and -not $ApiProcess.HasExited) {
    $ApiProcess.Kill()
    $ApiProcess.WaitForExit()
  }
  if ($ApiOutTask -and $ApiOutTask.IsCompleted) {
    $out = $ApiOutTask.Result
    if ($out) { $out }
  }
  if ($ApiErrTask -and $ApiErrTask.IsCompleted) {
    $err = $ApiErrTask.Result
    if ($err) { $err }
  }
  throw
} finally {
  if ($ApiProcess -and -not $ApiProcess.HasExited) {
    $ApiProcess.Kill()
    $ApiProcess.WaitForExit()
  }

  if (-not $MysqlWasListening) {
    & $MysqlAdmin --protocol=tcp --host=127.0.0.1 --port=3306 --user=root shutdown 2>$null | Out-Null
  }
}
