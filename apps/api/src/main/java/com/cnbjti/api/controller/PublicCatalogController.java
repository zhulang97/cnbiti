package com.cnbjti.api.controller;

import com.cnbjti.api.common.ApiResponse;
import com.cnbjti.api.model.ApiModels.AdminUser;
import com.cnbjti.api.model.ApiModels.Article;
import com.cnbjti.api.model.ApiModels.ContactResponse;
import com.cnbjti.api.model.ApiModels.IndustryProfile;
import com.cnbjti.api.model.ApiModels.MediaAsset;
import com.cnbjti.api.model.ApiModels.NavigationItem;
import com.cnbjti.api.model.ApiModels.Product;
import com.cnbjti.api.model.ApiModels.ProductCategory;
import com.cnbjti.api.model.ApiModels.PublicContactRequest;
import com.cnbjti.api.model.ApiModels.PublicRfqRequest;
import com.cnbjti.api.model.ApiModels.RfqResponse;
import com.cnbjti.api.model.ApiModels.SiteConfig;
import com.cnbjti.api.model.ApiModels.SiteRuntime;
import com.cnbjti.api.model.ApiModels.Standard;
import com.cnbjti.api.model.ApiModels.TitaniumGrade;
import com.cnbjti.api.service.AuditLogService;
import com.cnbjti.api.service.CatalogDataService;
import com.cnbjti.api.service.ContactMessageService;
import com.cnbjti.api.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/public")
public class PublicCatalogController {

  private final CatalogDataService dataService;
  private final FileStorageService fileStorageService;
  private final AuditLogService auditLogService;
  private final ContactMessageService contactMessageService;

  public PublicCatalogController(CatalogDataService dataService, FileStorageService fileStorageService,
      AuditLogService auditLogService, ContactMessageService contactMessageService) {
    this.dataService = dataService;
    this.fileStorageService = fileStorageService;
    this.auditLogService = auditLogService;
    this.contactMessageService = contactMessageService;
  }

  @GetMapping("/site-config")
  public ApiResponse<SiteConfig> siteConfig() {
    return ApiResponse.ok(dataService.siteConfig());
  }

  @GetMapping("/site-runtime")
  public ApiResponse<SiteRuntime> siteRuntime() {
    return ApiResponse.ok(new SiteRuntime(dataService.siteConfig(), dataService.navigation(), dataService.categories()));
  }

  @GetMapping("/navigation")
  public ApiResponse<List<NavigationItem>> navigation() {
    return ApiResponse.ok(dataService.navigation());
  }

  @GetMapping("/categories")
  public ApiResponse<List<ProductCategory>> categories() {
    return ApiResponse.ok(dataService.categories());
  }

  @GetMapping("/categories/{slug}")
  public ApiResponse<ProductCategory> category(@PathVariable String slug) {
    return ApiResponse.ok(dataService.category(slug));
  }

  @GetMapping("/products")
  public ApiResponse<List<Product>> products() {
    return ApiResponse.ok(dataService.products());
  }

  @GetMapping("/products/{slug}")
  public ApiResponse<Product> product(@PathVariable String slug) {
    return ApiResponse.ok(dataService.product(slug));
  }

  @GetMapping("/grades")
  public ApiResponse<List<TitaniumGrade>> grades() {
    return ApiResponse.ok(dataService.grades());
  }

  @GetMapping("/grades/{slug}")
  public ApiResponse<TitaniumGrade> grade(@PathVariable String slug) {
    return ApiResponse.ok(dataService.gradeBySlug(slug));
  }

  @GetMapping("/standards")
  public ApiResponse<List<Standard>> standards() {
    return ApiResponse.ok(dataService.standards());
  }

  @GetMapping("/standards/{slug}")
  public ApiResponse<Standard> standard(@PathVariable String slug) {
    return ApiResponse.ok(dataService.standardBySlug(slug));
  }

  @GetMapping("/articles")
  public ApiResponse<List<Article>> articles() {
    return ApiResponse.ok(dataService.articles());
  }

  @GetMapping("/articles/{slug}")
  public ApiResponse<Article> article(@PathVariable String slug) {
    return ApiResponse.ok(dataService.articleBySlug(slug));
  }

  @GetMapping("/industries")
  public ApiResponse<List<IndustryProfile>> industries() {
    return ApiResponse.ok(dataService.industries());
  }

  @GetMapping("/industries/{slug}")
  public ApiResponse<IndustryProfile> industry(@PathVariable String slug) {
    return ApiResponse.ok(dataService.industryBySlug(slug));
  }

  @PostMapping("/rfqs")
  public ApiResponse<RfqResponse> createRfq(@Valid @RequestBody PublicRfqRequest request) {
    return ApiResponse.ok(dataService.createPublicRfq(request));
  }

  @PostMapping("/contact-messages")
  public ApiResponse<ContactResponse> createContactMessage(@Valid @RequestBody PublicContactRequest request) {
    return ApiResponse.ok(contactMessageService.create(request));
  }

  @PostMapping("/uploads/mock")
  public ApiResponse<String> uploadMock() {
    return ApiResponse.ok("mock-file-" + System.currentTimeMillis());
  }

  @PostMapping(value = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiResponse<MediaAsset> upload(@RequestParam("file") MultipartFile file,
      @AuthenticationPrincipal AdminUser user, HttpServletRequest request) {
    MediaAsset asset = fileStorageService.store(file);
    if (user != null) {
      auditLogService.recordUpload(user, asset.id(), request);
    }
    return ApiResponse.ok(asset);
  }
}
