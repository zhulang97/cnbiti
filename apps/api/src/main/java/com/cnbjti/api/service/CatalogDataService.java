package com.cnbjti.api.service;

import com.cnbjti.api.entity.CatalogContentEntity;
import com.cnbjti.api.entity.CustomerEntity;
import com.cnbjti.api.entity.RfqEntity;
import com.cnbjti.api.model.ApiModels.AdminArticle;
import com.cnbjti.api.model.ApiModels.AdminArticleDetail;
import com.cnbjti.api.model.ApiModels.AdminCategoryDetail;
import com.cnbjti.api.model.ApiModels.AdminContentOptions;
import com.cnbjti.api.model.ApiModels.AdminDashboard;
import com.cnbjti.api.model.ApiModels.AdminGradeDetail;
import com.cnbjti.api.model.ApiModels.AdminIndustryDetail;
import com.cnbjti.api.model.ApiModels.AdminProduct;
import com.cnbjti.api.model.ApiModels.AdminProductDetail;
import com.cnbjti.api.model.ApiModels.AdminRfq;
import com.cnbjti.api.model.ApiModels.AdminStandardDetail;
import com.cnbjti.api.model.ApiModels.AboutFeature;
import com.cnbjti.api.model.ApiModels.AboutPageConfig;
import com.cnbjti.api.model.ApiModels.AboutStat;
import com.cnbjti.api.model.ApiModels.AboutTimelineEvent;
import com.cnbjti.api.model.ApiModels.ArticleSaveRequest;
import com.cnbjti.api.model.ApiModels.Article;
import com.cnbjti.api.model.ApiModels.CategorySaveRequest;
import com.cnbjti.api.model.ApiModels.CountryCount;
import com.cnbjti.api.model.ApiModels.Customer;
import com.cnbjti.api.model.ApiModels.CustomerDetail;
import com.cnbjti.api.model.ApiModels.GradeSaveRequest;
import com.cnbjti.api.model.ApiModels.GalleryPageConfig;
import com.cnbjti.api.model.ApiModels.HomeCapability;
import com.cnbjti.api.model.ApiModels.HomeFeature;
import com.cnbjti.api.model.ApiModels.HomePageConfig;
import com.cnbjti.api.model.ApiModels.HomeQualityItem;
import com.cnbjti.api.model.ApiModels.HomeStat;
import com.cnbjti.api.model.ApiModels.IndustryProductLink;
import com.cnbjti.api.model.ApiModels.IndustryProfile;
import com.cnbjti.api.model.ApiModels.IndustrySaveRequest;
import com.cnbjti.api.model.ApiModels.ManagedGalleryItem;
import com.cnbjti.api.model.ApiModels.MediaAsset;
import com.cnbjti.api.model.ApiModels.NavigationItem;
import com.cnbjti.api.model.ApiModels.NavigationSaveRequest;
import com.cnbjti.api.model.ApiModels.Product;
import com.cnbjti.api.model.ApiModels.ProductCategory;
import com.cnbjti.api.model.ApiModels.ProductCount;
import com.cnbjti.api.model.ApiModels.ProductSaveRequest;
import com.cnbjti.api.model.ApiModels.ProductSpec;
import com.cnbjti.api.model.ApiModels.PublicRfqRequest;
import com.cnbjti.api.model.ApiModels.RfqResponse;
import com.cnbjti.api.model.ApiModels.SeoMeta;
import com.cnbjti.api.model.ApiModels.SiteConfig;
import com.cnbjti.api.model.ApiModels.SiteConfigSaveRequest;
import com.cnbjti.api.model.ApiModels.Standard;
import com.cnbjti.api.model.ApiModels.StandardSaveRequest;
import com.cnbjti.api.model.ApiModels.TitaniumGrade;
import com.cnbjti.api.model.ApiModels.UpdateCustomerRequest;
import com.cnbjti.api.repository.CatalogContentRepository;
import com.cnbjti.api.repository.CustomerRepository;
import com.cnbjti.api.repository.RfqRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogDataService {

  static final String TYPE_SITE_CONFIG = "SITE_CONFIG";
  static final String TYPE_NAVIGATION = "NAVIGATION";
  static final String TYPE_CATEGORY = "CATEGORY";
  static final String TYPE_GRADE = "GRADE";
  static final String TYPE_STANDARD = "STANDARD";
  static final String TYPE_PRODUCT = "PRODUCT";
  static final String TYPE_ARTICLE = "ARTICLE";
  static final String TYPE_INDUSTRY = "INDUSTRY";
  static final String STATUS_PUBLISHED = "published";

  private static final Set<String> RFQ_STATUSES = Set.of("new", "in_progress", "quoted", "won", "lost");
  private static final Set<String> PRODUCT_STATUSES = Set.of("published", "draft");

  private final CatalogContentRepository catalogRepository;
  private final RfqRepository rfqRepository;
  private final CustomerRepository customerRepository;
  private final JsonCodec jsonCodec;

  public CatalogDataService(CatalogContentRepository catalogRepository, RfqRepository rfqRepository,
      CustomerRepository customerRepository, JsonCodec jsonCodec) {
    this.catalogRepository = catalogRepository;
    this.rfqRepository = rfqRepository;
    this.customerRepository = customerRepository;
    this.jsonCodec = jsonCodec;
  }

  public SiteConfig siteConfig() {
    return catalogRepository.findByContentTypeAndItemId(TYPE_SITE_CONFIG, "site")
        .map(item -> withDefaultSitePages(jsonCodec.read(item.getPayloadJson(), SiteConfig.class)))
        .orElseThrow(() -> new NoSuchElementException("Site config not found"));
  }

  @Transactional
  public SiteConfig updateSiteConfig(SiteConfigSaveRequest request) {
    CatalogContentEntity existing = catalogRepository.findByContentTypeAndItemId(TYPE_SITE_CONFIG, "site").orElse(null);
    SiteConfig previous = existing == null ? null : withDefaultSitePages(jsonCodec.read(existing.getPayloadJson(), SiteConfig.class));
    AboutPageConfig aboutPage = request.aboutPage() == null && previous != null
        ? previous.aboutPage()
        : normalizeAboutPage(request.aboutPage());
    HomePageConfig homePage = request.homePage() == null && previous != null
        ? previous.homePage()
        : normalizeHomePage(request.homePage());
    GalleryPageConfig certificatesPage = request.certificatesPage() == null && previous != null
        ? previous.certificatesPage()
        : normalizeGalleryPage(request.certificatesPage());
    GalleryPageConfig factoryTourPage = request.factoryTourPage() == null && previous != null
        ? previous.factoryTourPage()
        : normalizeGalleryPage(request.factoryTourPage());
    SiteConfig siteConfig = new SiteConfig(
        request.siteName().trim(),
        text(request.tagline(), ""),
        request.email().trim(),
        text(request.phone(), ""),
        text(request.whatsapp(), ""),
        text(request.address(), ""),
        text(request.city(), ""),
        text(request.country(), ""),
        cleanLinks(request.socialLinks()),
        aboutPage == null ? defaultAboutPage() : aboutPage,
        homePage == null ? defaultHomePage() : homePage,
        certificatesPage == null ? defaultCertificatesPage() : certificatesPage,
        factoryTourPage == null ? defaultFactoryTourPage() : factoryTourPage
    );
    CatalogContentEntity entity = new CatalogContentEntity(TYPE_SITE_CONFIG + ":site", TYPE_SITE_CONFIG, "site", "site",
        siteConfig.siteName(), STATUS_PUBLISHED, existing == null ? 0 : existing.getSortOrder(),
        jsonCodec.write(siteConfig), LocalDateTime.now());
    return withDefaultSitePages(jsonCodec.read(catalogRepository.save(entity).getPayloadJson(), SiteConfig.class));
  }

  public List<NavigationItem> navigation() {
    return catalogRepository.findByContentTypeAndItemId(TYPE_NAVIGATION, "main")
        .map(item -> listNavigation(item.getPayloadJson()))
        .orElseGet(this::defaultNavigation);
  }

  public List<NavigationItem> adminNavigation() {
    return navigation();
  }

  @Transactional
  public List<NavigationItem> updateNavigation(NavigationSaveRequest request) {
    List<NavigationItem> items = normalizeNavigationItems(request.items());
    if (items.isEmpty()) {
      throw new IllegalArgumentException("Navigation items are required");
    }
    CatalogContentEntity existing = catalogRepository.findByContentTypeAndItemId(TYPE_NAVIGATION, "main").orElse(null);
    CatalogContentEntity entity = new CatalogContentEntity(TYPE_NAVIGATION + ":main", TYPE_NAVIGATION, "main", "main",
        "Main Navigation", STATUS_PUBLISHED, existing == null ? 0 : existing.getSortOrder(),
        jsonCodec.write(items), LocalDateTime.now());
    return listNavigation(catalogRepository.save(entity).getPayloadJson());
  }

  private List<NavigationItem> defaultNavigation() {
    return List.of(
        new NavigationItem("Titanium Products", null, categories().stream()
            .map(category -> new NavigationItem(category.name(), "/products/" + category.slug(), null, null, category.icon()))
            .toList(), null, null),
        nav("Certificates", "/certificates"),
        nav("Factory Tour", "/factory-tour"),
        nav("Processing", "/processing"),
        nav("Industries", "/industries"),
        nav("Resources", "/resources"),
        nav("Quality", "/quality"),
        nav("Grades", "/grades"),
        nav("Standards", "/standards"),
        nav("About", "/about"),
        nav("Contact", "/contact")
    );
  }

  public List<ProductCategory> categories() {
    return catalogRepository.findByContentTypeAndStatusOrderBySortOrderAsc(TYPE_CATEGORY, STATUS_PUBLISHED).stream()
        .map(this::toProductCategory)
        .toList();
  }

  public ProductCategory category(String slug) {
    CatalogContentEntity entity = catalogRepository.findByContentTypeAndSlug(TYPE_CATEGORY, slug)
        .filter(item -> STATUS_PUBLISHED.equals(item.getStatus()))
        .orElseThrow(() -> new NoSuchElementException("category not found"));
    return toProductCategory(entity);
  }

  public List<Product> products() {
    return catalogList(TYPE_PRODUCT, Product.class, true);
  }

  public Product product(String slug) {
    return catalogBySlug(TYPE_PRODUCT, slug, Product.class);
  }

  public List<TitaniumGrade> grades() {
    return catalogList(TYPE_GRADE, TitaniumGrade.class, true);
  }

  public TitaniumGrade gradeBySlug(String slug) {
    return catalogBySlug(TYPE_GRADE, slug, TitaniumGrade.class);
  }

  public List<Standard> standards() {
    return catalogList(TYPE_STANDARD, Standard.class, true);
  }

  public Standard standardBySlug(String slug) {
    return catalogBySlug(TYPE_STANDARD, slug, Standard.class);
  }

  public List<Article> articles() {
    return catalogList(TYPE_ARTICLE, Article.class, true).stream()
        .map(this::articleSummary)
        .toList();
  }

  public Article articleBySlug(String slug) {
    return catalogBySlug(TYPE_ARTICLE, slug, Article.class);
  }

  public List<IndustryProfile> industries() {
    List<IndustryProfile> stored = catalogList(TYPE_INDUSTRY, IndustryProfile.class, true);
    return stored.isEmpty() ? defaultIndustries() : stored;
  }

  public IndustryProfile industryBySlug(String slug) {
    return industries().stream()
        .filter(industry -> slug(industry.slug()).equals(slug(slug)))
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("Industry not found"));
  }

  @Transactional
  public RfqResponse createPublicRfq(PublicRfqRequest request) {
    String rfqNo = nextRfqNo();
    RfqEntity rfq = new RfqEntity(
        rfqNo,
        text(request.company(), request.name()),
        text(request.destinationCountry(), "Unknown"),
        "--",
        request.email(),
        request.phone(),
        request.productType(),
        text(request.grade(), "Not sure"),
        request.quantity(),
        buildRfqMessage(request),
        "new",
        LocalDate.now(),
        LocalDateTime.now(),
        "",
        null,
        jsonCodec.write(listMedia(request.attachments()))
    );
    rfqRepository.save(rfq);
    upsertCustomer(rfq);
    return new RfqResponse(rfqNo, "RFQ submitted successfully", "24 hours");
  }

  public AdminDashboard dashboard() {
    List<RfqEntity> rfqs = rfqRepository.findAll();
    int published = (int) catalogRepository.findByContentTypeAndStatusOrderBySortOrderAsc(TYPE_PRODUCT, STATUS_PUBLISHED).size();
    return new AdminDashboard(
        (int) rfqRepository.countByCreatedAt(LocalDate.now()),
        (int) rfqRepository.countByStatus("new"),
        (int) rfqRepository.countByStatus("in_progress"),
        8,
        published,
        List.of(4, 6, 3, 8, 5, 9, 7, 11, 6, 8, 12, Math.max(10, rfqs.size())),
        topCountries(rfqs),
        topProducts(rfqs)
    );
  }

  public List<AdminRfq> adminRfqs() {
    return rfqRepository.findAllByOrderByCreatedAtDescIdDesc().stream().map(this::toAdminRfq).toList();
  }

  public String adminRfqsCsv() {
    StringBuilder csv = new StringBuilder("\uFEFF");
    csv.append("RFQ No,Company,Country,Email,Phone,Product,Grade,Quantity,Status,Created At,Notes,Attachments\n");
    rfqRepository.findAllByOrderByCreatedAtDescIdDesc().forEach(rfq -> csv
        .append(csvCell(rfq.getId())).append(',')
        .append(csvCell(rfq.getCompany())).append(',')
        .append(csvCell(rfq.getCountry())).append(',')
        .append(csvCell(rfq.getEmail())).append(',')
        .append(csvCell(rfq.getPhone())).append(',')
        .append(csvCell(rfq.getProduct())).append(',')
        .append(csvCell(rfq.getGrade())).append(',')
        .append(csvCell(rfq.getQty())).append(',')
        .append(csvCell(rfq.getStatus())).append(',')
        .append(csvCell(rfq.getCreatedAt().toString())).append(',')
        .append(csvCell(text(rfq.getNotes(), ""))).append(',')
        .append(csvCell(attachmentsSummary(listAttachments(rfq.getAttachmentsJson()))))
        .append('\n'));
    return csv.toString();
  }

  public AdminRfq adminRfq(String id) {
    return toAdminRfq(findRfq(id));
  }

  @Transactional
  public AdminRfq updateRfqStatus(String id, String status) {
    if (!RFQ_STATUSES.contains(status)) {
      throw new IllegalArgumentException("Unsupported RFQ status");
    }
    RfqEntity rfq = findRfq(id);
    rfq.setStatus(status);
    rfq.setStatusUpdatedAt(LocalDateTime.now());
    return toAdminRfq(rfqRepository.save(rfq));
  }

  @Transactional
  public AdminRfq updateRfqNotes(String id, String notes) {
    RfqEntity rfq = findRfq(id);
    rfq.setNotes(text(notes, ""));
    rfq.setNotesUpdatedAt(LocalDateTime.now());
    return toAdminRfq(rfqRepository.save(rfq));
  }

  public List<AdminProduct> adminProducts() {
    return catalogRepository.findByContentTypeOrderBySortOrderAsc(TYPE_PRODUCT).stream()
        .map(this::toAdminProduct)
        .toList();
  }

  public AdminContentOptions adminContentOptions() {
    return new AdminContentOptions(
        catalogRepository.findByContentTypeOrderBySortOrderAsc(TYPE_CATEGORY).stream()
            .map(this::toProductCategory)
            .toList(),
        catalogList(TYPE_GRADE, TitaniumGrade.class, false),
        catalogList(TYPE_STANDARD, Standard.class, false)
    );
  }

  public List<AdminCategoryDetail> adminCategories() {
    return catalogRepository.findByContentTypeOrderBySortOrderAsc(TYPE_CATEGORY).stream()
        .map(this::toAdminCategoryDetail)
        .toList();
  }

  public AdminCategoryDetail adminCategoryDetail(String id) {
    return toAdminCategoryDetail(findCatalog(TYPE_CATEGORY, id, "Product category not found"));
  }

  @Transactional
  public AdminCategoryDetail createCategory(CategorySaveRequest request) {
    validateStatus(request.status());
    ensureSlugAvailable(TYPE_CATEGORY, request.slug(), null);
    String id = nextCatalogItemId(TYPE_CATEGORY, "cat");
    return toAdminCategoryDetail(saveCategoryEntity(null, id, request));
  }

  @Transactional
  public AdminCategoryDetail updateCategory(String id, CategorySaveRequest request) {
    validateStatus(request.status());
    CatalogContentEntity existing = findCatalog(TYPE_CATEGORY, id, "Product category not found");
    ensureSlugAvailable(TYPE_CATEGORY, request.slug(), id);
    CatalogContentEntity saved = saveCategoryEntity(existing, id, request);
    propagateCategory(jsonCodec.read(saved.getPayloadJson(), ProductCategory.class));
    return toAdminCategoryDetail(saved);
  }

  @Transactional
  public void deleteCategory(String id) {
    CatalogContentEntity category = findCatalog(TYPE_CATEGORY, id, "Product category not found");
    if (isCategoryUsed(id)) {
      throw new IllegalArgumentException("Product category is used by products and cannot be deleted");
    }
    catalogRepository.delete(category);
  }

  public List<AdminIndustryDetail> adminIndustries() {
    List<CatalogContentEntity> items = catalogRepository.findByContentTypeOrderBySortOrderAsc(TYPE_INDUSTRY);
    if (items.isEmpty()) {
      return defaultIndustries().stream()
          .map(this::toDefaultAdminIndustryDetail)
          .toList();
    }
    return items.stream()
        .map(this::toAdminIndustryDetail)
        .toList();
  }

  public AdminIndustryDetail adminIndustryDetail(String id) {
    CatalogContentEntity entity = catalogRepository.findByContentTypeAndItemId(TYPE_INDUSTRY, id).orElse(null);
    if (entity != null) {
      return toAdminIndustryDetail(entity);
    }
    return defaultIndustries().stream()
        .filter(industry -> id.equals(industry.id()))
        .findFirst()
        .map(this::toDefaultAdminIndustryDetail)
        .orElseThrow(() -> new NoSuchElementException("Industry not found"));
  }

  @Transactional
  public AdminIndustryDetail createIndustry(IndustrySaveRequest request) {
    validateStatus(request.status());
    ensureSlugAvailable(TYPE_INDUSTRY, request.slug(), null);
    String id = nextCatalogItemId(TYPE_INDUSTRY, "ind");
    return toAdminIndustryDetail(saveIndustryEntity(null, id, request));
  }

  @Transactional
  public AdminIndustryDetail updateIndustry(String id, IndustrySaveRequest request) {
    validateStatus(request.status());
    CatalogContentEntity existing = catalogRepository.findByContentTypeAndItemId(TYPE_INDUSTRY, id).orElse(null);
    ensureSlugAvailable(TYPE_INDUSTRY, request.slug(), id);
    return toAdminIndustryDetail(saveIndustryEntity(existing, id, request));
  }

  @Transactional
  public void deleteIndustry(String id) {
    catalogRepository.findByContentTypeAndItemId(TYPE_INDUSTRY, id).ifPresent(catalogRepository::delete);
  }

  public List<AdminGradeDetail> adminGrades() {
    return catalogRepository.findByContentTypeOrderBySortOrderAsc(TYPE_GRADE).stream()
        .map(this::toAdminGradeDetail)
        .toList();
  }

  public AdminGradeDetail adminGradeDetail(String id) {
    return toAdminGradeDetail(findCatalog(TYPE_GRADE, id, "Titanium grade not found"));
  }

  @Transactional
  public AdminGradeDetail createGrade(GradeSaveRequest request) {
    validateStatus(request.status());
    ensureSlugAvailable(TYPE_GRADE, request.slug(), null);
    String id = nextCatalogItemId(TYPE_GRADE, "gr");
    return toAdminGradeDetail(saveGradeEntity(null, id, request));
  }

  @Transactional
  public AdminGradeDetail updateGrade(String id, GradeSaveRequest request) {
    validateStatus(request.status());
    CatalogContentEntity existing = findCatalog(TYPE_GRADE, id, "Titanium grade not found");
    ensureSlugAvailable(TYPE_GRADE, request.slug(), id);
    CatalogContentEntity saved = saveGradeEntity(existing, id, request);
    propagateGrade(jsonCodec.read(saved.getPayloadJson(), TitaniumGrade.class));
    return toAdminGradeDetail(saved);
  }

  @Transactional
  public void deleteGrade(String id) {
    CatalogContentEntity grade = findCatalog(TYPE_GRADE, id, "Titanium grade not found");
    if (isGradeUsed(id)) {
      throw new IllegalArgumentException("Titanium grade is used by products and cannot be deleted");
    }
    catalogRepository.delete(grade);
  }

  public List<AdminStandardDetail> adminStandards() {
    return catalogRepository.findByContentTypeOrderBySortOrderAsc(TYPE_STANDARD).stream()
        .map(this::toAdminStandardDetail)
        .toList();
  }

  public AdminStandardDetail adminStandardDetail(String id) {
    return toAdminStandardDetail(findCatalog(TYPE_STANDARD, id, "Standard not found"));
  }

  @Transactional
  public AdminStandardDetail createStandard(StandardSaveRequest request) {
    validateStatus(request.status());
    ensureSlugAvailable(TYPE_STANDARD, request.slug(), null);
    String id = nextCatalogItemId(TYPE_STANDARD, "s");
    return toAdminStandardDetail(saveStandardEntity(null, id, request));
  }

  @Transactional
  public AdminStandardDetail updateStandard(String id, StandardSaveRequest request) {
    validateStatus(request.status());
    CatalogContentEntity existing = findCatalog(TYPE_STANDARD, id, "Standard not found");
    ensureSlugAvailable(TYPE_STANDARD, request.slug(), id);
    CatalogContentEntity saved = saveStandardEntity(existing, id, request);
    propagateStandard(jsonCodec.read(saved.getPayloadJson(), Standard.class));
    return toAdminStandardDetail(saved);
  }

  @Transactional
  public void deleteStandard(String id) {
    CatalogContentEntity standard = findCatalog(TYPE_STANDARD, id, "Standard not found");
    if (isStandardUsed(id)) {
      throw new IllegalArgumentException("Standard is used by products and cannot be deleted");
    }
    catalogRepository.delete(standard);
  }

  public AdminProductDetail adminProductDetail(String id) {
    CatalogContentEntity entity = findCatalog(TYPE_PRODUCT, id, "Product not found");
    return toAdminProductDetail(entity);
  }

  @Transactional
  public AdminProduct createProduct(ProductSaveRequest request) {
    validateStatus(request.status());
    ensureSlugAvailable(TYPE_PRODUCT, request.slug(), null);
    String id = nextCatalogItemId(TYPE_PRODUCT, "p");
    CatalogContentEntity entity = saveProductEntity(null, id, request);
    return toAdminProduct(entity);
  }

  @Transactional
  public AdminProduct updateProduct(String id, ProductSaveRequest request) {
    validateStatus(request.status());
    CatalogContentEntity existing = findCatalog(TYPE_PRODUCT, id, "Product not found");
    ensureSlugAvailable(TYPE_PRODUCT, request.slug(), id);
    CatalogContentEntity entity = saveProductEntity(existing, id, request);
    return toAdminProduct(entity);
  }

  @Transactional
  public AdminProduct updateProductStatus(String id, String status) {
    validateStatus(status);
    CatalogContentEntity productEntity = findCatalog(TYPE_PRODUCT, id, "Product not found");
    productEntity.setStatus(status);
    productEntity.setUpdatedAt(LocalDateTime.now());
    catalogRepository.save(productEntity);
    return toAdminProduct(productEntity);
  }

  @Transactional
  public void deleteProduct(String id) {
    catalogRepository.delete(findCatalog(TYPE_PRODUCT, id, "Product not found"));
  }

  public List<AdminArticle> adminArticles() {
    return catalogRepository.findByContentTypeOrderBySortOrderAsc(TYPE_ARTICLE).stream()
        .map(this::toAdminArticle)
        .toList();
  }

  public AdminArticleDetail adminArticleDetail(String id) {
    CatalogContentEntity entity = findCatalog(TYPE_ARTICLE, id, "Article not found");
    return toAdminArticleDetail(entity);
  }

  @Transactional
  public AdminArticle createArticle(ArticleSaveRequest request) {
    validateStatus(request.status());
    ensureSlugAvailable(TYPE_ARTICLE, request.slug(), null);
    String id = nextCatalogItemId(TYPE_ARTICLE, "a");
    CatalogContentEntity entity = saveArticleEntity(null, id, request);
    return toAdminArticle(entity);
  }

  @Transactional
  public AdminArticle updateArticle(String id, ArticleSaveRequest request) {
    validateStatus(request.status());
    CatalogContentEntity existing = findCatalog(TYPE_ARTICLE, id, "Article not found");
    ensureSlugAvailable(TYPE_ARTICLE, request.slug(), id);
    CatalogContentEntity entity = saveArticleEntity(existing, id, request);
    return toAdminArticle(entity);
  }

  @Transactional
  public AdminArticle updateArticleStatus(String id, String status) {
    validateStatus(status);
    CatalogContentEntity articleEntity = findCatalog(TYPE_ARTICLE, id, "Article not found");
    Article article = jsonCodec.read(articleEntity.getPayloadJson(), Article.class);
    Article updatedArticle = new Article(
        article.id(),
        article.slug(),
        article.title(),
        article.excerpt(),
        article.content(),
        article.coverImage(),
        article.category(),
        article.tags(),
        "published".equals(status) ? text(article.publishedAt(), LocalDate.now().toString()) : text(article.publishedAt(), ""),
        article.readingTime(),
        article.seo()
    );
    CatalogContentEntity updatedEntity = new CatalogContentEntity(articleEntity.getId(), TYPE_ARTICLE, id, article.slug(),
        article.title(), status, articleEntity.getSortOrder(), jsonCodec.write(updatedArticle), LocalDateTime.now());
    return toAdminArticle(catalogRepository.save(updatedEntity));
  }

  @Transactional
  public void deleteArticle(String id) {
    catalogRepository.delete(findCatalog(TYPE_ARTICLE, id, "Article not found"));
  }

  public List<Customer> customers() {
    return customerRepository.findAllByOrderByLastContactDescCompanyAsc().stream()
        .map(this::toCustomer)
        .toList();
  }

  public CustomerDetail customerDetail(String id) {
    CustomerEntity customer = findCustomer(id);
    List<AdminRfq> rfqs = rfqRepository.findAllByEmailIgnoreCaseOrderByCreatedAtDescIdDesc(customer.getEmail())
        .stream()
        .map(this::toAdminRfq)
        .toList();
    return new CustomerDetail(customer.getId(), customer.getCompany(), customer.getCountry(), customer.getCountryCode(),
        customer.getEmail(), customer.getRfqCount(), customer.getLastContact().toString(), text(customer.getNotes(), ""),
        dateTime(customer.getNotesUpdatedAt()), rfqs);
  }

  @Transactional
  public CustomerDetail updateCustomer(String id, UpdateCustomerRequest request) {
    CustomerEntity customer = findCustomer(id);
    if (request.lastContact() != null && !request.lastContact().isBlank()) {
      customer.setLastContact(LocalDate.parse(request.lastContact()));
    }
    customer.setNotes(text(request.notes(), ""));
    customer.setNotesUpdatedAt(LocalDateTime.now());
    customerRepository.save(customer);
    return customerDetail(id);
  }

  private CatalogContentEntity saveCategoryEntity(CatalogContentEntity existing, String id, CategorySaveRequest request) {
    ProductCategory previous = existing == null ? null : jsonCodec.read(existing.getPayloadJson(), ProductCategory.class);
    String imageUrl = text(request.imageUrl(), previous == null || previous.image() == null ? "" : previous.image().url());
    int sortOrder = existing == null ? nextSortOrder(TYPE_CATEGORY) : existing.getSortOrder();
    ProductCategory category = new ProductCategory(
        id,
        slug(request.slug()),
        request.name().trim(),
        text(request.description(), ""),
        imageUrl.isBlank() ? null : media("cm" + id, imageUrl, request.name(), slug(request.slug()) + ".jpg"),
        text(request.icon(), previous == null ? "" : previous.icon()),
        request.productCount() == null ? (previous == null ? 0 : previous.productCount()) : request.productCount(),
        request.seo() == null ? (previous == null ? null : previous.seo()) : request.seo(),
        request.showOnHome() == null ? previous == null || !Boolean.FALSE.equals(previous.showOnHome()) : request.showOnHome(),
        request.homeSort() == null ? (previous == null || previous.homeSort() == null ? sortOrder : previous.homeSort()) : request.homeSort()
    );
    CatalogContentEntity entity = new CatalogContentEntity(TYPE_CATEGORY + ":" + id, TYPE_CATEGORY, id, category.slug(),
        category.name(), request.status(), sortOrder,
        jsonCodec.write(category), LocalDateTime.now());
    return catalogRepository.save(entity);
  }

  private CatalogContentEntity saveIndustryEntity(CatalogContentEntity existing, String id, IndustrySaveRequest request) {
    IndustryProfile previous = existing == null ? null : jsonCodec.read(existing.getPayloadJson(), IndustryProfile.class);
    IndustryProfile industry = new IndustryProfile(
        id,
        slug(request.slug()),
        request.name().trim(),
        text(request.kicker(), ""),
        text(request.summary(), ""),
        text(request.image(), previous == null ? "" : previous.image()),
        text(request.imageAlt(), request.name().trim()),
        list(request.grades()),
        list(request.standards()),
        list(request.applications()),
        list(request.requirements()),
        listProductLinks(request.productLinks()),
        list(request.articleKeywords())
    );
    CatalogContentEntity entity = new CatalogContentEntity(TYPE_INDUSTRY + ":" + id, TYPE_INDUSTRY, id,
        industry.slug(), industry.name(), request.status(), existing == null ? nextSortOrder(TYPE_INDUSTRY) : existing.getSortOrder(),
        jsonCodec.write(industry), LocalDateTime.now());
    return catalogRepository.save(entity);
  }

  private CatalogContentEntity saveGradeEntity(CatalogContentEntity existing, String id, GradeSaveRequest request) {
    TitaniumGrade previous = existing == null ? null : jsonCodec.read(existing.getPayloadJson(), TitaniumGrade.class);
    TitaniumGrade grade = new TitaniumGrade(
        id,
        slug(request.slug()),
        request.name().trim(),
        request.shortName().trim(),
        text(request.composition(), ""),
        text(request.description(), ""),
        text(request.tensileStrength(), previous == null ? "" : previous.tensileStrength()),
        text(request.yieldStrength(), previous == null ? "" : previous.yieldStrength()),
        text(request.elongation(), previous == null ? "" : previous.elongation()),
        request.density() == null ? (previous == null ? null : previous.density()) : request.density(),
        list(request.applications())
    );
    CatalogContentEntity entity = new CatalogContentEntity(TYPE_GRADE + ":" + id, TYPE_GRADE, id, grade.slug(),
        grade.name(), request.status(), existing == null ? nextSortOrder(TYPE_GRADE) : existing.getSortOrder(),
        jsonCodec.write(grade), LocalDateTime.now());
    return catalogRepository.save(entity);
  }

  private CatalogContentEntity saveStandardEntity(CatalogContentEntity existing, String id, StandardSaveRequest request) {
    Standard standard = new Standard(
        id,
        slug(request.slug()),
        request.code().trim(),
        request.name().trim(),
        text(request.description(), ""),
        list(request.productTypes())
    );
    CatalogContentEntity entity = new CatalogContentEntity(TYPE_STANDARD + ":" + id, TYPE_STANDARD, id, standard.slug(),
        standard.code(), request.status(), existing == null ? nextSortOrder(TYPE_STANDARD) : existing.getSortOrder(),
        jsonCodec.write(standard), LocalDateTime.now());
    return catalogRepository.save(entity);
  }

  private void propagateCategory(ProductCategory category) {
    catalogRepository.findByContentTypeOrderBySortOrderAsc(TYPE_PRODUCT).forEach(entity -> {
      Product product = jsonCodec.read(entity.getPayloadJson(), Product.class);
      if (product.category() == null || !category.id().equals(product.category().id())) {
        return;
      }
      Product updated = new Product(product.id(), product.slug(), product.name(), product.shortDescription(),
          product.description(), category, product.grades(), product.standards(), product.specs(), product.images(),
          product.seo(), product.featured(), product.inStock());
      saveProductPayload(entity, updated);
    });
  }

  private void propagateGrade(TitaniumGrade grade) {
    catalogRepository.findByContentTypeOrderBySortOrderAsc(TYPE_PRODUCT).forEach(entity -> {
      Product product = jsonCodec.read(entity.getPayloadJson(), Product.class);
      List<TitaniumGrade> grades = product.grades().stream()
          .map(item -> grade.id().equals(item.id()) ? grade : item)
          .toList();
      if (grades.equals(product.grades())) {
        return;
      }
      Product updated = new Product(product.id(), product.slug(), product.name(), product.shortDescription(),
          product.description(), product.category(), grades, product.standards(), product.specs(), product.images(),
          product.seo(), product.featured(), product.inStock());
      saveProductPayload(entity, updated);
    });
  }

  private void propagateStandard(Standard standard) {
    catalogRepository.findByContentTypeOrderBySortOrderAsc(TYPE_PRODUCT).forEach(entity -> {
      Product product = jsonCodec.read(entity.getPayloadJson(), Product.class);
      List<Standard> standards = product.standards().stream()
          .map(item -> standard.id().equals(item.id()) ? standard : item)
          .toList();
      if (standards.equals(product.standards())) {
        return;
      }
      Product updated = new Product(product.id(), product.slug(), product.name(), product.shortDescription(),
          product.description(), product.category(), product.grades(), standards, product.specs(), product.images(),
          product.seo(), product.featured(), product.inStock());
      saveProductPayload(entity, updated);
    });
  }

  private void saveProductPayload(CatalogContentEntity entity, Product product) {
    catalogRepository.save(new CatalogContentEntity(entity.getId(), TYPE_PRODUCT, product.id(), product.slug(),
        product.name(), entity.getStatus(), entity.getSortOrder(), jsonCodec.write(product), LocalDateTime.now()));
  }

  private boolean isCategoryUsed(String categoryId) {
    return catalogRepository.findByContentTypeOrderBySortOrderAsc(TYPE_PRODUCT).stream()
        .map(entity -> jsonCodec.read(entity.getPayloadJson(), Product.class))
        .anyMatch(product -> product.category() != null && categoryId.equals(product.category().id()));
  }

  private boolean isGradeUsed(String gradeId) {
    return catalogRepository.findByContentTypeOrderBySortOrderAsc(TYPE_PRODUCT).stream()
        .map(entity -> jsonCodec.read(entity.getPayloadJson(), Product.class))
        .anyMatch(product -> product.grades() != null
            && product.grades().stream().anyMatch(grade -> gradeId.equals(grade.id())));
  }

  private boolean isStandardUsed(String standardId) {
    return catalogRepository.findByContentTypeOrderBySortOrderAsc(TYPE_PRODUCT).stream()
        .map(entity -> jsonCodec.read(entity.getPayloadJson(), Product.class))
        .anyMatch(product -> product.standards() != null
            && product.standards().stream().anyMatch(standard -> standardId.equals(standard.id())));
  }

  private CatalogContentEntity saveProductEntity(CatalogContentEntity existing, String id, ProductSaveRequest request) {
    Product previous = existing == null ? null : jsonCodec.read(existing.getPayloadJson(), Product.class);
    ProductCategory category = productCategoryById(request.categoryId());
    List<TitaniumGrade> grades = list(request.gradeIds()).stream().map(this::gradeById).toList();
    List<Standard> standards = list(request.standardIds()).stream().map(this::standardById).toList();
    List<ProductSpec> specs = productSpecs(request.specs(), previous);
    List<MediaAsset> images = productImages(request.images(), request.imageUrl(), previous, id, request.name(), request.slug());
    Product product = new Product(
        id,
        slug(request.slug()),
        request.name().trim(),
        text(request.shortDescription(), request.name().trim() + " with ASTM certification and MTR available."),
        text(request.description(), null),
        category,
        grades,
        standards,
        specs,
        images,
        request.seo() == null ? (previous == null ? null : previous.seo()) : request.seo(),
        bool(request.featured(), previous == null || Boolean.TRUE.equals(previous.featured())),
        bool(request.inStock(), previous == null || Boolean.TRUE.equals(previous.inStock()))
    );
    CatalogContentEntity entity = new CatalogContentEntity(TYPE_PRODUCT + ":" + id, TYPE_PRODUCT, id, product.slug(),
        product.name(), request.status(), existing == null ? nextSortOrder(TYPE_PRODUCT) : existing.getSortOrder(),
        jsonCodec.write(product), LocalDateTime.now());
    return catalogRepository.save(entity);
  }

  private CatalogContentEntity saveArticleEntity(CatalogContentEntity existing, String id, ArticleSaveRequest request) {
    Article previous = existing == null ? null : jsonCodec.read(existing.getPayloadJson(), Article.class);
    String publishedAt = "published".equals(request.status())
        ? text(request.publishedAt(), LocalDate.now().toString())
        : text(request.publishedAt(), "");
    Article article = new Article(
        id,
        slug(request.slug()),
        request.title().trim(),
        text(request.excerpt(), ""),
        text(request.content(), null),
        imageUrl(request.coverImageUrl(), id, request.title()).isBlank()
            ? null
            : media("am" + id, imageUrl(request.coverImageUrl(), id, request.title()), request.title(), slug(request.slug()) + ".jpg"),
        request.category().trim(),
        list(request.tags()),
        publishedAt,
        request.readingTime() == null ? (previous == null ? 5 : previous.readingTime()) : request.readingTime(),
        previous == null ? null : previous.seo()
    );
    CatalogContentEntity entity = new CatalogContentEntity(TYPE_ARTICLE + ":" + id, TYPE_ARTICLE, id, article.slug(),
        article.title(), request.status(), existing == null ? nextSortOrder(TYPE_ARTICLE) : existing.getSortOrder(),
        jsonCodec.write(article), LocalDateTime.now());
    return catalogRepository.save(entity);
  }

  private AdminCategoryDetail toAdminCategoryDetail(CatalogContentEntity entity) {
    ProductCategory category = toProductCategory(entity);
    return new AdminCategoryDetail(category.id(), category.slug(), category.name(), text(category.description(), ""),
        category.image() == null ? "" : category.image().url(), text(category.icon(), ""), category.productCount(),
        category.seo(), category.showOnHome(), category.homeSort(), entity.getStatus(), entity.getUpdatedAt().toLocalDate().toString());
  }

  private AdminIndustryDetail toAdminIndustryDetail(CatalogContentEntity entity) {
    IndustryProfile industry = jsonCodec.read(entity.getPayloadJson(), IndustryProfile.class);
    return new AdminIndustryDetail(
        industry.id(),
        industry.slug(),
        industry.name(),
        text(industry.kicker(), ""),
        text(industry.summary(), ""),
        text(industry.image(), ""),
        text(industry.imageAlt(), ""),
        list(industry.grades()),
        list(industry.standards()),
        list(industry.applications()),
        list(industry.requirements()),
        listProductLinks(industry.productLinks()),
        list(industry.articleKeywords()),
        entity.getStatus(),
        entity.getUpdatedAt().toLocalDate().toString()
    );
  }

  private AdminIndustryDetail toDefaultAdminIndustryDetail(IndustryProfile industry) {
    return new AdminIndustryDetail(
        industry.id(),
        industry.slug(),
        industry.name(),
        text(industry.kicker(), ""),
        text(industry.summary(), ""),
        text(industry.image(), ""),
        text(industry.imageAlt(), ""),
        list(industry.grades()),
        list(industry.standards()),
        list(industry.applications()),
        list(industry.requirements()),
        listProductLinks(industry.productLinks()),
        list(industry.articleKeywords()),
        STATUS_PUBLISHED,
        LocalDate.now().toString()
    );
  }

  private ProductCategory toProductCategory(CatalogContentEntity entity) {
    ProductCategory category = jsonCodec.read(entity.getPayloadJson(), ProductCategory.class);
    return new ProductCategory(
        category.id(),
        category.slug(),
        category.name(),
        text(category.description(), ""),
        category.image(),
        text(category.icon(), ""),
        category.productCount(),
        category.seo(),
        category.showOnHome() == null || Boolean.TRUE.equals(category.showOnHome()),
        category.homeSort() == null ? entity.getSortOrder() : category.homeSort()
    );
  }

  private AdminGradeDetail toAdminGradeDetail(CatalogContentEntity entity) {
    TitaniumGrade grade = jsonCodec.read(entity.getPayloadJson(), TitaniumGrade.class);
    return new AdminGradeDetail(grade.id(), grade.slug(), grade.name(), grade.shortName(),
        text(grade.composition(), ""), text(grade.description(), ""), text(grade.tensileStrength(), ""),
        text(grade.yieldStrength(), ""), text(grade.elongation(), ""), grade.density(), list(grade.applications()),
        entity.getStatus(), entity.getUpdatedAt().toLocalDate().toString());
  }

  private AdminStandardDetail toAdminStandardDetail(CatalogContentEntity entity) {
    Standard standard = jsonCodec.read(entity.getPayloadJson(), Standard.class);
    return new AdminStandardDetail(standard.id(), standard.slug(), standard.code(), standard.name(),
        text(standard.description(), ""), list(standard.productTypes()), entity.getStatus(),
        entity.getUpdatedAt().toLocalDate().toString());
  }

  private AdminProduct toAdminProduct(CatalogContentEntity entity) {
    Product product = jsonCodec.read(entity.getPayloadJson(), Product.class);
    String grade = product.grades().isEmpty() ? "" : product.grades().getFirst().shortName();
    return new AdminProduct(product.id(), product.name(), product.category().name(), grade, entity.getStatus(),
        entity.getUpdatedAt().toLocalDate().toString());
  }

  private AdminProductDetail toAdminProductDetail(CatalogContentEntity entity) {
    Product product = jsonCodec.read(entity.getPayloadJson(), Product.class);
    List<MediaAsset> images = listMedia(product.images());
    List<ProductSpec> specs = listProductSpecs(product.specs());
    return new AdminProductDetail(product.id(), product.name(), product.slug(), product.category().id(),
        product.grades().stream().map(TitaniumGrade::id).toList(),
        product.standards().stream().map(Standard::id).toList(),
        entity.getStatus(), product.shortDescription(), text(product.description(), ""),
        images.isEmpty() ? "" : images.getFirst().url(),
        images, specs, product.seo(),
        product.featured(), product.inStock());
  }

  private AdminArticle toAdminArticle(CatalogContentEntity entity) {
    Article article = jsonCodec.read(entity.getPayloadJson(), Article.class);
    return new AdminArticle(article.id(), article.title(), text(article.category(), ""), entity.getStatus(),
        text(article.publishedAt(), ""));
  }

  private Article articleSummary(Article article) {
    return new Article(
        article.id(),
        article.slug(),
        article.title(),
        article.excerpt(),
        null,
        article.coverImage(),
        article.category(),
        article.tags(),
        article.publishedAt(),
        article.readingTime(),
        article.seo()
    );
  }

  private AdminArticleDetail toAdminArticleDetail(CatalogContentEntity entity) {
    Article article = jsonCodec.read(entity.getPayloadJson(), Article.class);
    return new AdminArticleDetail(article.id(), article.title(), article.slug(), text(article.category(), ""), entity.getStatus(),
        text(article.excerpt(), ""), text(article.content(), ""),
        article.coverImage() == null ? "" : article.coverImage().url(), list(article.tags()),
        text(article.publishedAt(), ""), article.readingTime());
  }

  private ProductCategory productCategoryById(String id) {
    CatalogContentEntity entity = findCatalog(TYPE_CATEGORY, id, "Product category not found");
    return toProductCategory(entity);
  }

  private TitaniumGrade gradeById(String id) {
    CatalogContentEntity entity = findCatalog(TYPE_GRADE, id, "Titanium grade not found");
    return jsonCodec.read(entity.getPayloadJson(), TitaniumGrade.class);
  }

  private Standard standardById(String id) {
    CatalogContentEntity entity = findCatalog(TYPE_STANDARD, id, "Standard not found");
    return jsonCodec.read(entity.getPayloadJson(), Standard.class);
  }

  private CatalogContentEntity findCatalog(String type, String itemId, String message) {
    return catalogRepository.findByContentTypeAndItemId(type, itemId).orElseThrow(() -> new NoSuchElementException(message));
  }

  private void validateStatus(String status) {
    if (!PRODUCT_STATUSES.contains(status)) {
      throw new IllegalArgumentException("Unsupported status");
    }
  }

  private void ensureSlugAvailable(String type, String slug, String currentItemId) {
    catalogRepository.findByContentTypeAndSlug(type, slug(slug))
        .filter(entity -> currentItemId == null || !currentItemId.equals(entity.getItemId()))
        .ifPresent(entity -> {
          throw new IllegalArgumentException("Slug is already in use");
        });
  }

  private int nextSortOrder(String type) {
    return catalogRepository.findByContentTypeOrderBySortOrderAsc(type).stream()
        .mapToInt(CatalogContentEntity::getSortOrder)
        .max()
        .orElse(-1) + 1;
  }

  private String nextCatalogItemId(String type, String prefix) {
    int next = catalogRepository.findByContentTypeOrderBySortOrderAsc(type).stream()
        .map(CatalogContentEntity::getItemId)
        .filter(id -> id.startsWith(prefix))
        .map(id -> id.substring(prefix.length()))
        .filter(value -> value.matches("\\d+"))
        .mapToInt(Integer::parseInt)
        .max()
        .orElse(0) + 1;
    return prefix + next;
  }

  private <T> List<T> catalogList(String type, Class<T> itemType, boolean publishedOnly) {
    List<CatalogContentEntity> items = publishedOnly
        ? catalogRepository.findByContentTypeAndStatusOrderBySortOrderAsc(type, STATUS_PUBLISHED)
        : catalogRepository.findByContentTypeOrderBySortOrderAsc(type);
    return items.stream()
        .map(item -> jsonCodec.read(item.getPayloadJson(), itemType))
        .toList();
  }

  private <T> T catalogBySlug(String type, String slug, Class<T> itemType) {
    CatalogContentEntity entity = catalogRepository.findByContentTypeAndSlug(type, slug)
        .filter(item -> STATUS_PUBLISHED.equals(item.getStatus()))
        .orElseThrow(() -> new NoSuchElementException(type.toLowerCase().replace('_', ' ') + " not found"));
    return jsonCodec.read(entity.getPayloadJson(), itemType);
  }

  private RfqEntity findRfq(String id) {
    return rfqRepository.findById(id).orElseThrow(() -> new NoSuchElementException("RFQ not found"));
  }

  private CustomerEntity findCustomer(String id) {
    return customerRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Customer not found"));
  }

  private AdminRfq toAdminRfq(RfqEntity rfq) {
    return new AdminRfq(rfq.getId(), rfq.getCompany(), rfq.getCountry(), rfq.getCountryCode(), rfq.getEmail(),
        rfq.getPhone(), rfq.getProduct(), rfq.getGrade(), rfq.getQty(), rfq.getMessage(), rfq.getStatus(),
        rfq.getCreatedAt().toString(), text(rfq.getNotes(), ""), dateTime(rfq.getStatusUpdatedAt()),
        dateTime(rfq.getNotesUpdatedAt()), listAttachments(rfq.getAttachmentsJson()));
  }

  private Customer toCustomer(CustomerEntity customer) {
    return new Customer(customer.getId(), customer.getCompany(), customer.getCountry(), customer.getCountryCode(),
        customer.getEmail(), customer.getRfqCount(), customer.getLastContact().toString());
  }

  private void upsertCustomer(RfqEntity rfq) {
    CustomerEntity existing = customerRepository.findByEmailIgnoreCase(rfq.getEmail()).orElse(null);
    if (existing == null) {
      customerRepository.save(new CustomerEntity("c" + System.currentTimeMillis(), rfq.getCompany(), rfq.getCountry(),
          rfq.getCountryCode(), rfq.getEmail(), 1, rfq.getCreatedAt()));
      return;
    }
    existing.recordContact(rfq.getCreatedAt());
    customerRepository.save(existing);
  }

  private String nextRfqNo() {
    int next = rfqRepository.findAll().stream()
        .map(RfqEntity::getId)
        .map(id -> id.replaceFirst("^RFQ-\\d{4}-", ""))
        .filter(value -> value.matches("\\d+"))
        .mapToInt(Integer::parseInt)
        .max()
        .orElse(0) + 1;
    return "RFQ-" + LocalDate.now().getYear() + "-" + "%03d".formatted(next);
  }

  private List<CountryCount> topCountries(List<RfqEntity> rfqs) {
    return rfqs.stream()
        .collect(Collectors.groupingBy(RfqEntity::getCountry, Collectors.summingInt(rfq -> 1)))
        .entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
        .limit(6)
        .map(entry -> new CountryCount(entry.getKey(), entry.getValue()))
        .toList();
  }

  private List<ProductCount> topProducts(List<RfqEntity> rfqs) {
    return rfqs.stream()
        .collect(Collectors.groupingBy(RfqEntity::getProduct, Collectors.summingInt(rfq -> 1)))
        .entrySet().stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
        .limit(6)
        .map(entry -> new ProductCount(entry.getKey(), entry.getValue()))
        .toList();
  }

  private String buildRfqMessage(PublicRfqRequest request) {
    List<String> parts = new ArrayList<>();
    parts.add(text(request.message(), "No additional message."));
    if (request.dimensions() != null && !request.dimensions().isBlank()) {
      parts.add("Dimensions: " + request.dimensions());
    }
    return String.join("\n", parts);
  }

  private List<MediaAsset> listAttachments(String json) {
    if (json == null || json.isBlank()) {
      return List.of();
    }
    MediaAsset[] attachments = jsonCodec.read(json, MediaAsset[].class);
    return Arrays.stream(attachments)
        .filter(attachment -> attachment != null && attachment.url() != null && !attachment.url().isBlank())
        .toList();
  }

  private List<ProductSpec> productSpecs(List<ProductSpec> requestedSpecs, Product previous) {
    if (requestedSpecs != null) {
      return listProductSpecs(requestedSpecs);
    }
    if (previous != null && previous.specs() != null && !previous.specs().isEmpty()) {
      return listProductSpecs(previous.specs());
    }
    return List.of(new ProductSpec("Surface", "Mill finish, polished", null));
  }

  private List<MediaAsset> productImages(List<MediaAsset> requestedImages, String requestedImageUrl,
      Product previous, String id, String name, String requestedSlug) {
    List<MediaAsset> normalizedImages = requestedImages == null ? null : listMedia(requestedImages);
    if (normalizedImages != null && !normalizedImages.isEmpty()) {
      return normalizedImages;
    }

    String singleImageUrl = imageUrl(requestedImageUrl, id, name);
    if (!singleImageUrl.isBlank()) {
      return List.of(media("pm" + id, singleImageUrl, name, slug(requestedSlug) + ".jpg"));
    }

    if (normalizedImages != null) {
      return List.of();
    }
    return previous == null ? List.of() : listMedia(previous.images());
  }

  private List<ProductSpec> listProductSpecs(List<ProductSpec> values) {
    return values == null ? List.of() : values.stream()
        .filter(value -> value != null && value.label() != null && !value.label().isBlank()
            && value.value() != null && !value.value().isBlank())
        .map(value -> new ProductSpec(value.label().trim(), value.value().trim(), text(value.unit(), null)))
        .toList();
  }

  private List<MediaAsset> listMedia(List<MediaAsset> values) {
    return values == null ? List.of() : values.stream()
        .filter(value -> value != null && value.url() != null && !value.url().isBlank())
        .toList();
  }

  private List<NavigationItem> listNavigation(String json) {
    if (json == null || json.isBlank()) {
      return defaultNavigation();
    }
    return normalizeNavigationItems(Arrays.asList(jsonCodec.read(json, NavigationItem[].class)));
  }

  private List<NavigationItem> normalizeNavigationItems(List<NavigationItem> items) {
    if (items == null) {
      return List.of();
    }
    return items.stream()
        .filter(item -> item != null)
        .map(this::normalizeNavigationItem)
        .toList();
  }

  private NavigationItem normalizeNavigationItem(NavigationItem item) {
    if (item.label() == null || item.label().isBlank()) {
      throw new IllegalArgumentException("Navigation label is required");
    }
    List<NavigationItem> children = normalizeNavigationItems(item.children());
    return new NavigationItem(
        item.label().trim(),
        text(item.href(), null),
        children.isEmpty() ? null : children,
        text(item.badge(), null),
        text(item.icon(), null)
    );
  }

  private Map<String, String> cleanLinks(Map<String, String> values) {
    if (values == null || values.isEmpty()) {
      return Map.of();
    }
    return values.entrySet().stream()
        .filter(entry -> entry.getKey() != null && !entry.getKey().isBlank())
        .filter(entry -> entry.getValue() != null && !entry.getValue().isBlank())
        .collect(Collectors.toMap(
            entry -> entry.getKey().trim(),
            entry -> entry.getValue().trim(),
            (left, right) -> right
        ));
  }

  private SiteConfig withDefaultSitePages(SiteConfig value) {
    return new SiteConfig(
        value.siteName(),
        value.tagline(),
        value.email(),
        value.phone(),
        value.whatsapp(),
        value.address(),
        value.city(),
        value.country(),
        value.socialLinks(),
        value.aboutPage() == null ? defaultAboutPage() : value.aboutPage(),
        value.homePage() == null ? defaultHomePage() : value.homePage(),
        value.certificatesPage() == null ? defaultCertificatesPage() : value.certificatesPage(),
        value.factoryTourPage() == null ? defaultFactoryTourPage() : value.factoryTourPage()
    );
  }

  private AboutPageConfig defaultAboutPage() {
    return new AboutPageConfig(
        "Our Story",
        "Baoji's Titanium Specialists Since 2008",
        "CNBJTI was founded in Baoji - China's titanium capital - with a single mission: make certified titanium materials accessible to global buyers with the documentation and service they need.",
        "Today we supply titanium bar, sheet, tube, wire, forgings and custom CNC parts to customers in over 50 countries. Every order ships with a full EN 10204 3.1 MTR and is backed by our technical team.",
        List.of(
            new AboutStat("2008", "Founded in Baoji"),
            new AboutStat("50+", "Countries Served"),
            new AboutStat("100+", "Titanium Specifications"),
            new AboutStat("15+", "Years Export Experience")),
        "Location Advantage",
        "Why Baoji?",
        "Baoji, Shaanxi produces over 60% of China's titanium output. Being here means direct access to the best mills, fastest lead times and most competitive pricing.",
        List.of(
            new AboutFeature("Ti", "Direct Mill Access", "Located minutes from Baoji's major titanium mills. We source directly, cutting out middlemen and reducing lead times."),
            new AboutFeature("QA", "Certified Supply Chain", "All mills are ISO 9001 certified. We audit suppliers regularly and maintain approved vendor lists for each product type."),
            new AboutFeature("EX", "Export Expertise", "Over 15 years exporting to North America, Europe, Southeast Asia and the Middle East. We handle all customs documentation.")),
        "Milestones",
        "Our Journey",
        List.of(
            new AboutTimelineEvent("2008", "Founded in Baoji", "CNBJTI established as a titanium materials trading company, focusing on export to Southeast Asia."),
            new AboutTimelineEvent("2012", "ISO 9001 Certification", "Achieved ISO 9001:2008 quality management certification. Expanded product range to include forgings and CNC parts."),
            new AboutTimelineEvent("2015", "European Market Entry", "First major contracts with German and Dutch chemical processing companies. Began supplying EN 10204 3.1 MTRs as standard."),
            new AboutTimelineEvent("2018", "CNC Machining Division", "Launched in-house CNC machining capability for custom titanium components. Added aerospace-grade Ti-6Al-4V to stock."),
            new AboutTimelineEvent("2021", "North America Expansion", "Established relationships with US aerospace and medical device manufacturers. AMS 4928 supply capability added."),
            new AboutTimelineEvent("2024", "50+ Countries Milestone", "Reached the milestone of supplying customers in over 50 countries. Launched multilingual website and 24-hour RFQ response commitment.")),
        "What We Stand For",
        "Our Values",
        List.of(
            new AboutFeature("TC", "Technical Accuracy", "We know titanium. Our team can advise on grade selection, processing and certification requirements."),
            new AboutFeature("MTR", "Documentation First", "Every order ships with complete MTR and certification. No exceptions, no shortcuts."),
            new AboutFeature("24h", "Fast Response", "24-hour quote turnaround. We respect your time and your project deadlines."),
            new AboutFeature("LT", "Long-term Partnership", "We build relationships, not just transactions. Many customers have been with us for over 10 years.")),
        "Ready to Work Together?",
        "Send us your requirements and our team will respond within 24 hours with a quote and technical recommendations.",
        "About CNBJTI - Baoji Titanium Materials Supplier Since 2008",
        "CNBJTI is a Baoji-based titanium materials supplier with 15+ years of export experience. We supply certified titanium to 50+ countries with EN 10204 3.1 MTR.");
  }

  private HomePageConfig defaultHomePage() {
    return new HomePageConfig(
        "Baoji Titanium Materials Manufacturer & Custom Processing Supplier",
        "CNBJTI is based in Baoji, China, focused on certified titanium materials and custom processing for global buyers.",
        "We supply titanium bar, sheet, plate, tube, wire, forgings, fittings, fasteners, anodes and CNC titanium parts with MTR, export packing and drawing-based processing support.",
        List.of(
            new HomeStat("2008", "Founded in Baoji"),
            new HomeStat("50+", "Countries Served"),
            new HomeStat("100+", "Specifications"),
            new HomeStat("24h", "RFQ Response")),
        List.of(
            new HomeFeature("MTR", "MTR Available", "Heat number, chemistry and mechanical values"),
            new HomeFeature("MOQ", "Small MOQ", "Prototype, cut-to-size and export orders"),
            new HomeFeature("OEM", "Custom Parts", "Machining, forging and drawing-based supply")),
        List.of(
            new HomeFeature("MTR", "Certified Documentation", "EN 10204 3.1 MTR, heat number traceability and third-party inspection available."),
            new HomeFeature("CUT", "Cut-to-size Supply", "Bar, plate, sheet and tube can be cut or processed before export packing."),
            new HomeFeature("OEM", "Drawing-based Parts", "CNC machining, forging and special titanium components quoted from drawings.")),
        List.of(
            new HomeCapability("Cut-to-size", "Saw, waterjet, laser", "", ""),
            new HomeCapability("CNC Machining", "Turning, milling, drilling", "", ""),
            new HomeCapability("Grinding & Polishing", "Ra 0.4 achievable", "", ""),
            new HomeCapability("Welding & Fabrication", "TIG, electron beam", "", ""),
            new HomeCapability("Surface Treatment", "Anodizing, passivation", "", ""),
            new HomeCapability("Inspection & Testing", "UT, PMI, hardness", "", "")),
        List.of(
            new HomeQualityItem("MTR", "Mill Test Report (MTR)", "EN 10204 3.1 certified, traceable to heat number"),
            new HomeQualityItem("CHEM", "Chemical Composition", "Full elemental analysis per ASTM/AMS requirements"),
            new HomeQualityItem("MECH", "Mechanical Properties", "Tensile, yield, elongation, hardness test results"),
            new HomeQualityItem("SGS", "Third-Party Inspection", "SGS, BV, TUV available on request"))
    );
  }

  private GalleryPageConfig defaultCertificatesPage() {
    return new GalleryPageConfig(
        "Certificates",
        "Certificates",
        "Our certificate gallery can be managed from the admin panel. Upload ISO, compliance, business license or third-party inspection documents here so buyers can review them before sending an RFQ.",
        List.of(),
        "Certificates - Titanium Quality Documents | CNBJTI",
        "View CNBJTI certificate images and quality documents for titanium materials, export supply and custom processing.");
  }

  private GalleryPageConfig defaultFactoryTourPage() {
    return new GalleryPageConfig(
        "Factory Tour",
        "Factory Tour",
        "A visual look at titanium processing, cutting, CNC machining, tube production, surface treatment and inspection resources used for export orders.",
        List.of(
            new ManagedGalleryItem("Titanium CNC Factory", "CNC machining and drawing-based titanium parts.",
                "https://cnbjti.com/cnbjti-assets/2026-05-14/file_535475e4c15a489dae12d2a841b4e878.jpg", "Titanium CNC factory"),
            new ManagedGalleryItem("Titanium Sheet Factory", "Sheet, plate and foil handling for export supply.",
                "https://cnbjti.com/cnbjti-assets/2026-05-14/file_07a15b16c4134c07816d6b6b56d377e7.png", "Titanium sheet factory"),
            new ManagedGalleryItem("Cutting Workshop", "Cut-to-size service for bar, plate and tube orders.",
                "https://cnbjti.com/cnbjti-assets/2026-05-14/file_37c2349a8a274995acd1fbab1cc85bcc.webp", "Titanium cutting workshop"),
            new ManagedGalleryItem("Surface Treatment", "Grinding, polishing and surface treatment support.",
                "https://cnbjti.com/cnbjti-assets/2026-05-14/file_2577ed70f8764881bc9b430246fc4bd8.png", "Titanium surface treatment"),
            new ManagedGalleryItem("Tube Production", "Titanium welded tube production and inspection.",
                "https://cnbjti.com/cnbjti-assets/2026-05-14/file_845cb1487b3a4b57a7f269a3d82e748c.jpg", "Titanium welded tube production"),
            new ManagedGalleryItem("Pipe Equipment", "Equipment for pipe, fittings and related titanium products.",
                "https://cnbjti.com/cnbjti-assets/2026-05-14/file_dee5b328751444b6aa1d811a5adc00fb.png", "Titanium pipe equipment")),
        "Factory Tour - Titanium Processing Workshop | CNBJTI",
        "View CNBJTI factory tour images for titanium CNC machining, cutting, tube production, surface treatment and inspection.");
  }

  private AboutPageConfig normalizeAboutPage(AboutPageConfig value) {
    if (value == null) {
      return null;
    }
    return new AboutPageConfig(
        text(value.heroLabel(), ""),
        text(value.heroTitle(), ""),
        text(value.heroIntro(), ""),
        text(value.heroBody(), ""),
        normalizeAboutStats(value.stats()),
        text(value.locationLabel(), ""),
        text(value.locationTitle(), ""),
        text(value.locationDescription(), ""),
        normalizeAboutFeatures(value.advantages()),
        text(value.timelineLabel(), ""),
        text(value.timelineTitle(), ""),
        normalizeAboutTimeline(value.timeline()),
        text(value.valuesLabel(), ""),
        text(value.valuesTitle(), ""),
        normalizeAboutFeatures(value.values()),
        text(value.ctaTitle(), ""),
        text(value.ctaDescription(), ""),
        text(value.seoTitle(), ""),
        text(value.seoDescription(), "")
    );
  }

  private List<AboutStat> normalizeAboutStats(List<AboutStat> values) {
    if (values == null) {
      return List.of();
    }
    return values.stream()
        .filter(value -> value != null && value.value() != null && !value.value().isBlank()
            && value.label() != null && !value.label().isBlank())
        .map(value -> new AboutStat(value.value().trim(), value.label().trim()))
        .toList();
  }

  private HomePageConfig normalizeHomePage(HomePageConfig value) {
    if (value == null) {
      return null;
    }
    return new HomePageConfig(
        text(value.heroTitle(), ""),
        text(value.heroIntro(), ""),
        text(value.heroBody(), ""),
        normalizeHomeStats(value.stats()),
        normalizeHomeFeatures(value.proofPoints()),
        normalizeHomeFeatures(value.buyerNotes()),
        normalizeHomeCapabilities(value.capabilities()),
        normalizeHomeQualityItems(value.qualityItems())
    );
  }

  private List<HomeStat> normalizeHomeStats(List<HomeStat> values) {
    if (values == null) {
      return List.of();
    }
    return values.stream()
        .filter(value -> value != null && value.value() != null && !value.value().isBlank()
            && value.label() != null && !value.label().isBlank())
        .map(value -> new HomeStat(value.value().trim(), value.label().trim()))
        .toList();
  }

  private List<HomeFeature> normalizeHomeFeatures(List<HomeFeature> values) {
    if (values == null) {
      return List.of();
    }
    return values.stream()
        .filter(value -> value != null && value.title() != null && !value.title().isBlank()
            && value.desc() != null && !value.desc().isBlank())
        .map(value -> new HomeFeature(text(value.code(), ""), value.title().trim(), value.desc().trim()))
        .toList();
  }

  private List<HomeCapability> normalizeHomeCapabilities(List<HomeCapability> values) {
    if (values == null) {
      return List.of();
    }
    return values.stream()
        .filter(value -> value != null
            && ((value.title() != null && !value.title().isBlank())
            || (value.imageUrl() != null && !value.imageUrl().isBlank())))
        .map(value -> new HomeCapability(text(value.title(), ""), text(value.desc(), ""), text(value.imageUrl(), ""), text(value.imageAlt(), "")))
        .toList();
  }

  private List<HomeQualityItem> normalizeHomeQualityItems(List<HomeQualityItem> values) {
    if (values == null) {
      return List.of();
    }
    return values.stream()
        .filter(value -> value != null && value.title() != null && !value.title().isBlank()
            && value.desc() != null && !value.desc().isBlank())
        .map(value -> new HomeQualityItem(text(value.code(), ""), value.title().trim(), value.desc().trim()))
        .toList();
  }

  private GalleryPageConfig normalizeGalleryPage(GalleryPageConfig value) {
    if (value == null) {
      return null;
    }
    return new GalleryPageConfig(
        text(value.heroLabel(), ""),
        text(value.heroTitle(), ""),
        text(value.heroIntro(), ""),
        normalizeGalleryItems(value.items()),
        text(value.seoTitle(), ""),
        text(value.seoDescription(), "")
    );
  }

  private List<ManagedGalleryItem> normalizeGalleryItems(List<ManagedGalleryItem> values) {
    if (values == null) {
      return List.of();
    }
    return values.stream()
        .filter(value -> value != null && value.imageUrl() != null && !value.imageUrl().isBlank())
        .map(value -> new ManagedGalleryItem(
            text(value.title(), ""),
            text(value.desc(), ""),
            value.imageUrl().trim(),
            text(value.imageAlt(), "")))
        .toList();
  }

  private List<AboutFeature> normalizeAboutFeatures(List<AboutFeature> values) {
    if (values == null) {
      return List.of();
    }
    return values.stream()
        .filter(value -> value != null && value.title() != null && !value.title().isBlank()
            && value.desc() != null && !value.desc().isBlank())
        .map(value -> new AboutFeature(text(value.icon(), ""), value.title().trim(), value.desc().trim()))
        .toList();
  }

  private List<AboutTimelineEvent> normalizeAboutTimeline(List<AboutTimelineEvent> values) {
    if (values == null) {
      return List.of();
    }
    return values.stream()
        .filter(value -> value != null && value.year() != null && !value.year().isBlank()
            && value.title() != null && !value.title().isBlank()
            && value.desc() != null && !value.desc().isBlank())
        .map(value -> new AboutTimelineEvent(value.year().trim(), value.title().trim(), value.desc().trim()))
        .toList();
  }

  private String attachmentsSummary(List<MediaAsset> attachments) {
    return attachments.stream()
        .map(attachment -> text(attachment.filename(), attachment.url()) + " " + attachment.url())
        .collect(Collectors.joining(" | "));
  }

  private String csvCell(String value) {
    String text = text(value, "");
    return "\"" + text.replace("\"", "\"\"").replace("\r\n", "\n").replace("\r", "\n") + "\"";
  }

  private String dateTime(LocalDateTime value) {
    return value == null ? "" : value.toString();
  }

  private NavigationItem nav(String label, String href) {
    return new NavigationItem(label, href, null, null, null);
  }

  private MediaAsset media(String id, String url, String alt, String filename) {
    return new MediaAsset(id, url, null, alt, null, null, "image/jpeg", 0, filename);
  }

  private List<IndustryProfile> defaultIndustries() {
    return List.of(
        industry("industry-chemical-processing", "chemical-processing", "Chemical Processing", "Reactors, piping and anodes",
            "Titanium is specified for corrosive chemical service where chloride attack, oxidizing media and long maintenance intervals matter.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_58a53e1a37ce4e8b8607c380292bd0bd.png", "Titanium Chemical Industry Application",
            List.of("Gr.1", "Gr.2", "Gr.7", "Gr.12"), List.of("ASTM B338", "ASTM B265", "ASTM B348", "ASME SB338"),
            List.of("Heat exchangers", "Pressure vessels", "Electrolyzer parts", "Process piping"),
            List.of("Heat number traceability", "PMI or third-party test options", "Pickled or polished surfaces", "Export crating for tubes and sheets"),
            List.of(link("Titanium Tubes", "/products/titanium-tube"), link("Titanium Sheets", "/products/titanium-sheet"), link("Titanium Fittings", "/products/titanium-fittings")),
            List.of("chemical", "reactor", "piping", "anode", "electrolyzer", "water treatment")),
        industry("industry-marine-desalination", "marine-desalination", "Marine and Desalination", "Seawater corrosion resistance",
            "Commercially pure titanium is used for ship systems, offshore structures and desalination equipment exposed to seawater and chlorides.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_2db6859fc7914665b067b94f9dc60da0.jpg", "Shipbuilding and Desalination Application",
            List.of("Gr.1", "Gr.2", "Gr.7", "Gr.12"), List.of("ASTM B338", "ASTM B265", "ASTM B381"),
            List.of("Condenser tubing", "Seawater piping", "Offshore fasteners", "Plate heat exchangers"),
            List.of("Clean tube ends", "UT or eddy-current testing when required", "MTR with chemical and mechanical values", "Moisture-proof export packing"),
            List.of(link("Titanium Tubes", "/products/titanium-tube"), link("Titanium Fasteners", "/products/titanium-fasteners"), link("Titanium Plates", "/products/titanium-sheet")),
            List.of("marine", "ship", "offshore", "desalination", "seawater", "water treatment")),
        industry("industry-medical", "medical", "Medical and Dental", "Biocompatible titanium supply",
            "Medical projects typically require tight chemistry control, clean surfaces and full documentation for implant, dental and instrument components.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_0cd3cdf4a59044799351dcd285ef728d.jpg", "Titanium Medical Application",
            List.of("Gr.2", "Gr.4", "Gr.5", "Gr.23"), List.of("ASTM F67", "ASTM F136", "ASTM B348"),
            List.of("Dental implant blanks", "Surgical instruments", "Orthopedic parts", "Precision machined components"),
            List.of("Grade 23 ELI availability", "Low interstitial chemistry control", "Fine machining surface finish", "Lot-level MTR documentation"),
            List.of(link("Titanium Bars", "/products/titanium-bar"), link("CNC Titanium Parts", "/products/cnc-parts"), link("Titanium Fasteners", "/products/titanium-fasteners")),
            List.of("medical", "dental", "implant", "medicine", "knee", "biocompatible")),
        industry("industry-aerospace", "aerospace", "Aerospace and Defense", "High strength with lower weight",
            "Aerospace buyers use titanium alloys for structural parts, fasteners and machined components where strength-to-weight ratio is critical.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_747a102ba40e42f7ba78e2efa2b16b74.jpg", "Titanium Forging",
            List.of("Gr.5", "Gr.9", "Gr.23"), List.of("AMS 4928", "ASTM B348", "ASTM B381"),
            List.of("Fasteners", "Forged blanks", "CNC parts", "Hydraulic tubing"),
            List.of("Drawing review before quotation", "Tight dimensional tolerance", "Optional third-party inspection", "Batch traceability"),
            List.of(link("Titanium Bars", "/products/titanium-bar"), link("CNC Titanium Parts", "/products/cnc-parts"), link("Titanium Fasteners", "/products/titanium-fasteners")),
            List.of("aerospace", "aircraft", "drone", "performance", "strength", "fastener")),
        industry("industry-energy", "energy", "Energy and Hydrogen", "Electrochemical and power projects",
            "Titanium is used in energy storage, hydrogen, fuel cell and electrolyzer applications that need corrosion resistance and stable conductivity support.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_5e341e461ece4a9086ff293b260ffde9.webp", "Titanium Heat Treatment Furnace",
            List.of("Gr.1", "Gr.2", "Gr.5", "Gr.7"), List.of("ASTM B265", "ASTM B348", "ASTM B381"),
            List.of("Electrolyzer plates", "Titanium anodes", "Battery pack hardware", "Fuel cell components"),
            List.of("Surface treatment options", "Flatness and thickness control", "Custom cutting service", "Consistent batch documentation"),
            List.of(link("Titanium Sheets", "/products/titanium-sheet"), link("Titanium Electrolyzer", "/products/cnc-parts/titanium-electrolyzer-for-water-treatment"), link("CNC Titanium Parts", "/products/cnc-parts")),
            List.of("energy", "battery", "fuel cell", "hydrogen", "anode", "electrolyzer")),
        industry("industry-industrial-machining", "industrial-machining", "Industrial Machining", "Custom parts and processed blanks",
            "For industrial buyers, CNBJTI can combine stock titanium material with cutting, CNC machining, forging and finishing support.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_842b07feb73b4582b2c4d57ede43978b.png", "Titanium CNC Machining",
            List.of("Gr.2", "Gr.5", "Gr.9", "Gr.12"), List.of("ASTM B348", "ASTM B265", "ASTM B381"),
            List.of("Machined housings", "Custom washers", "Forged blanks", "Cut-to-size plate"),
            List.of("STEP, DXF, DWG or PDF drawings", "Tolerance review", "First article confirmation", "Protective packing for finished surfaces"),
            List.of(link("CNC Titanium Parts", "/products/cnc-parts"), link("Titanium Bars", "/products/titanium-bar"), link("Titanium Plates", "/products/titanium-sheet")),
            List.of("machining", "casting", "manufacturing", "custom", "industrial", "component")),
        industry("industry-automotive", "automotive", "Automotive and Motorsport", "Weight reduction and heat resistance",
            "Motorsport and advanced automotive projects use titanium for exhaust, fasteners and structural parts that need lower mass without weak hardware.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_535475e4c15a489dae12d2a841b4e878.jpg", "Titanium CNC Factory",
            List.of("Gr.5", "Gr.9", "Gr.2"), List.of("ASTM B348", "ASTM B338", "ASTM B265"),
            List.of("Exhaust tubing", "Bolts and nuts", "Bracket blanks", "CNC lightweight parts"),
            List.of("Reliable alloy grade control", "Thread and tolerance inspection", "Surface finish choice", "Small-batch manufacturing support"),
            List.of(link("Titanium Fasteners", "/products/titanium-fasteners"), link("Titanium Tubes", "/products/titanium-tube"), link("CNC Titanium Parts", "/products/cnc-parts")),
            List.of("automotive", "motorsport", "racing", "vehicle", "nev", "battery")),
        industry("industry-electronics", "electronics", "Electronics and Consumer Devices", "Durable housings and fine finishes",
            "Consumer electronics projects use titanium when housings, buttons and small precision parts need strength, premium finish and corrosion stability.",
            "https://cnbjti.com/cnbjti-assets/2026-05-14/file_2577ed70f8764881bc9b430246fc4bd8.png", "Titanium Sheet Surface Treatment",
            List.of("Gr.2", "Gr.5"), List.of("ASTM B348", "ASTM B265"),
            List.of("3C device housings", "Precision screws", "Machined buttons", "Thin sheet components"),
            List.of("Consistent surface appearance", "Fine machining support", "Scratch protection packing", "Prototype to batch supply"),
            List.of(link("CNC Titanium Parts", "/products/cnc-parts"), link("Titanium Fasteners", "/products/titanium-fasteners"), link("Titanium Sheets", "/products/titanium-sheet")),
            List.of("3c", "electronic", "housing", "consumer", "device", "screw"))
    );
  }

  private IndustryProfile industry(String id, String slug, String name, String kicker, String summary, String image,
      String imageAlt, List<String> grades, List<String> standards, List<String> applications, List<String> requirements,
      List<IndustryProductLink> productLinks, List<String> articleKeywords) {
    return new IndustryProfile(id, slug, name, kicker, summary, image, imageAlt, grades, standards, applications,
        requirements, productLinks, articleKeywords);
  }

  private IndustryProductLink link(String label, String href) {
    return new IndustryProductLink(label, href);
  }

  private List<String> list(List<String> values) {
    return values == null ? List.of() : values.stream()
        .filter(value -> value != null && !value.isBlank())
        .map(String::trim)
        .toList();
  }

  private List<IndustryProductLink> listProductLinks(List<IndustryProductLink> values) {
    return values == null ? List.of() : values.stream()
        .filter(value -> value != null && value.label() != null && !value.label().isBlank()
            && value.href() != null && !value.href().isBlank())
        .map(value -> new IndustryProductLink(value.label().trim(), value.href().trim()))
        .toList();
  }

  private boolean bool(Boolean value, boolean fallback) {
    return value == null ? fallback : value;
  }

  private String imageUrl(String value, String id, String title) {
    return text(value, "");
  }

  private String slug(String value) {
    String normalized = text(value, "").trim().toLowerCase()
        .replaceAll("[^a-z0-9]+", "-")
        .replaceAll("(^-|-$)", "");
    if (normalized.isBlank()) {
      throw new IllegalArgumentException("Slug is required");
    }
    return normalized;
  }

  private String text(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }
}
