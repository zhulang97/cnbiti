package com.cnbjti.api.controller;

import com.cnbjti.api.common.ApiResponse;
import com.cnbjti.api.model.ApiModels.AdminArticle;
import com.cnbjti.api.model.ApiModels.AdminArticleDetail;
import com.cnbjti.api.model.ApiModels.AdminAuditLogPage;
import com.cnbjti.api.model.ApiModels.AdminCategoryDetail;
import com.cnbjti.api.model.ApiModels.AdminContactMessage;
import com.cnbjti.api.model.ApiModels.AdminContentOptions;
import com.cnbjti.api.model.ApiModels.AdminDashboard;
import com.cnbjti.api.model.ApiModels.AdminGradeDetail;
import com.cnbjti.api.model.ApiModels.AdminProduct;
import com.cnbjti.api.model.ApiModels.AdminProductDetail;
import com.cnbjti.api.model.ApiModels.AdminRfq;
import com.cnbjti.api.model.ApiModels.AdminStandardDetail;
import com.cnbjti.api.model.ApiModels.AdminStoredFile;
import com.cnbjti.api.model.ApiModels.AdminUser;
import com.cnbjti.api.model.ApiModels.AdminManagedUser;
import com.cnbjti.api.model.ApiModels.AdminUserPasswordRequest;
import com.cnbjti.api.model.ApiModels.AdminUserSaveRequest;
import com.cnbjti.api.model.ApiModels.ArticleSaveRequest;
import com.cnbjti.api.model.ApiModels.CategorySaveRequest;
import com.cnbjti.api.model.ApiModels.Customer;
import com.cnbjti.api.model.ApiModels.CustomerDetail;
import com.cnbjti.api.model.ApiModels.GradeSaveRequest;
import com.cnbjti.api.model.ApiModels.NavigationItem;
import com.cnbjti.api.model.ApiModels.NavigationSaveRequest;
import com.cnbjti.api.model.ApiModels.ProductStatusRequest;
import com.cnbjti.api.model.ApiModels.ProductSaveRequest;
import com.cnbjti.api.model.ApiModels.SiteConfig;
import com.cnbjti.api.model.ApiModels.SiteConfigSaveRequest;
import com.cnbjti.api.model.ApiModels.StandardSaveRequest;
import com.cnbjti.api.model.ApiModels.UpdateCustomerRequest;
import com.cnbjti.api.model.ApiModels.UpdateNotesRequest;
import com.cnbjti.api.model.ApiModels.UpdateStatusRequest;
import com.cnbjti.api.service.AuditLogService;
import com.cnbjti.api.service.AuthService;
import com.cnbjti.api.service.AdminUserService;
import com.cnbjti.api.service.CatalogDataService;
import com.cnbjti.api.service.ContactMessageService;
import com.cnbjti.api.service.FileStorageService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final CatalogDataService dataService;
  private final AuthService authService;
  private final FileStorageService fileStorageService;
  private final AuditLogService auditLogService;
  private final ContactMessageService contactMessageService;
  private final AdminUserService adminUserService;

  public AdminController(CatalogDataService dataService, AuthService authService, FileStorageService fileStorageService,
      AuditLogService auditLogService, ContactMessageService contactMessageService, AdminUserService adminUserService) {
    this.dataService = dataService;
    this.authService = authService;
    this.fileStorageService = fileStorageService;
    this.auditLogService = auditLogService;
    this.contactMessageService = contactMessageService;
    this.adminUserService = adminUserService;
  }

  @GetMapping("/me")
  public ApiResponse<AdminUser> me(@AuthenticationPrincipal AdminUser user) {
    return ApiResponse.ok(user == null ? authService.adminUser() : user);
  }

  @GetMapping("/users")
  public ApiResponse<List<AdminManagedUser>> adminUsers() {
    return ApiResponse.ok(adminUserService.users());
  }

  @PostMapping("/users")
  public ApiResponse<AdminManagedUser> createAdminUser(@Valid @RequestBody AdminUserSaveRequest request) {
    return ApiResponse.ok(adminUserService.create(request));
  }

  @PutMapping("/users/{id}")
  public ApiResponse<AdminManagedUser> updateAdminUser(@PathVariable String id,
      @Valid @RequestBody AdminUserSaveRequest request, @AuthenticationPrincipal AdminUser user) {
    return ApiResponse.ok(adminUserService.update(id, request, user));
  }

  @PutMapping("/users/{id}/password")
  public ApiResponse<AdminManagedUser> resetAdminUserPassword(@PathVariable String id,
      @Valid @RequestBody AdminUserPasswordRequest request) {
    return ApiResponse.ok(adminUserService.resetPassword(id, request));
  }

  @GetMapping("/dashboard")
  public ApiResponse<AdminDashboard> dashboard() {
    return ApiResponse.ok(dataService.dashboard());
  }

  @GetMapping("/site-config")
  public ApiResponse<SiteConfig> siteConfig() {
    return ApiResponse.ok(dataService.siteConfig());
  }

  @PutMapping("/site-config")
  public ApiResponse<SiteConfig> updateSiteConfig(@Valid @RequestBody SiteConfigSaveRequest request) {
    return ApiResponse.ok(dataService.updateSiteConfig(request));
  }

  @GetMapping("/navigation")
  public ApiResponse<List<NavigationItem>> navigation() {
    return ApiResponse.ok(dataService.adminNavigation());
  }

  @PutMapping("/navigation")
  public ApiResponse<List<NavigationItem>> updateNavigation(@RequestBody NavigationSaveRequest request) {
    return ApiResponse.ok(dataService.updateNavigation(request));
  }

  @GetMapping("/products")
  public ApiResponse<List<AdminProduct>> products() {
    return ApiResponse.ok(dataService.adminProducts());
  }

  @GetMapping("/content-options")
  public ApiResponse<AdminContentOptions> contentOptions() {
    return ApiResponse.ok(dataService.adminContentOptions());
  }

  @GetMapping("/files")
  public ApiResponse<List<AdminStoredFile>> files() {
    return ApiResponse.ok(fileStorageService.adminFiles());
  }

  @GetMapping("/audit-logs")
  public ApiResponse<AdminAuditLogPage> auditLogs(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "20") int pageSize,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String action,
      @RequestParam(required = false) String targetType,
      @RequestParam(required = false) String status) {
    return ApiResponse.ok(auditLogService.adminLogs(keyword, action, targetType, status, page, pageSize));
  }

  @GetMapping("/contact-messages")
  public ApiResponse<List<AdminContactMessage>> contactMessages() {
    return ApiResponse.ok(contactMessageService.adminMessages());
  }

  @GetMapping("/contact-messages/{id}")
  public ApiResponse<AdminContactMessage> contactMessage(@PathVariable String id) {
    return ApiResponse.ok(contactMessageService.adminMessage(id));
  }

  @PutMapping("/contact-messages/{id}/status")
  public ApiResponse<AdminContactMessage> updateContactMessageStatus(@PathVariable String id,
      @Valid @RequestBody UpdateStatusRequest request) {
    return ApiResponse.ok(contactMessageService.updateStatus(id, request.status()));
  }

  @PutMapping("/contact-messages/{id}/notes")
  public ApiResponse<AdminContactMessage> updateContactMessageNotes(@PathVariable String id,
      @RequestBody UpdateNotesRequest request) {
    return ApiResponse.ok(contactMessageService.updateNotes(id, request.notes()));
  }

  @DeleteMapping("/files/{id}")
  public ApiResponse<Void> deleteFile(@PathVariable String id) {
    fileStorageService.deleteFile(id);
    return ApiResponse.ok(null);
  }

  @GetMapping("/categories")
  public ApiResponse<List<AdminCategoryDetail>> categories() {
    return ApiResponse.ok(dataService.adminCategories());
  }

  @GetMapping("/categories/{id}")
  public ApiResponse<AdminCategoryDetail> category(@PathVariable String id) {
    return ApiResponse.ok(dataService.adminCategoryDetail(id));
  }

  @PostMapping("/categories")
  public ApiResponse<AdminCategoryDetail> createCategory(@Valid @RequestBody CategorySaveRequest request) {
    return ApiResponse.ok(dataService.createCategory(request));
  }

  @PutMapping("/categories/{id}")
  public ApiResponse<AdminCategoryDetail> updateCategory(@PathVariable String id, @Valid @RequestBody CategorySaveRequest request) {
    return ApiResponse.ok(dataService.updateCategory(id, request));
  }

  @DeleteMapping("/categories/{id}")
  public ApiResponse<Void> deleteCategory(@PathVariable String id) {
    dataService.deleteCategory(id);
    return ApiResponse.ok(null);
  }

  @GetMapping("/grades")
  public ApiResponse<List<AdminGradeDetail>> grades() {
    return ApiResponse.ok(dataService.adminGrades());
  }

  @GetMapping("/grades/{id}")
  public ApiResponse<AdminGradeDetail> grade(@PathVariable String id) {
    return ApiResponse.ok(dataService.adminGradeDetail(id));
  }

  @PostMapping("/grades")
  public ApiResponse<AdminGradeDetail> createGrade(@Valid @RequestBody GradeSaveRequest request) {
    return ApiResponse.ok(dataService.createGrade(request));
  }

  @PutMapping("/grades/{id}")
  public ApiResponse<AdminGradeDetail> updateGrade(@PathVariable String id, @Valid @RequestBody GradeSaveRequest request) {
    return ApiResponse.ok(dataService.updateGrade(id, request));
  }

  @DeleteMapping("/grades/{id}")
  public ApiResponse<Void> deleteGrade(@PathVariable String id) {
    dataService.deleteGrade(id);
    return ApiResponse.ok(null);
  }

  @GetMapping("/standards")
  public ApiResponse<List<AdminStandardDetail>> standards() {
    return ApiResponse.ok(dataService.adminStandards());
  }

  @GetMapping("/standards/{id}")
  public ApiResponse<AdminStandardDetail> standard(@PathVariable String id) {
    return ApiResponse.ok(dataService.adminStandardDetail(id));
  }

  @PostMapping("/standards")
  public ApiResponse<AdminStandardDetail> createStandard(@Valid @RequestBody StandardSaveRequest request) {
    return ApiResponse.ok(dataService.createStandard(request));
  }

  @PutMapping("/standards/{id}")
  public ApiResponse<AdminStandardDetail> updateStandard(@PathVariable String id, @Valid @RequestBody StandardSaveRequest request) {
    return ApiResponse.ok(dataService.updateStandard(id, request));
  }

  @DeleteMapping("/standards/{id}")
  public ApiResponse<Void> deleteStandard(@PathVariable String id) {
    dataService.deleteStandard(id);
    return ApiResponse.ok(null);
  }

  @GetMapping("/products/{id}")
  public ApiResponse<AdminProductDetail> product(@PathVariable String id) {
    return ApiResponse.ok(dataService.adminProductDetail(id));
  }

  @PostMapping("/products")
  public ApiResponse<AdminProduct> createProduct(@Valid @RequestBody ProductSaveRequest request) {
    return ApiResponse.ok(dataService.createProduct(request));
  }

  @PutMapping("/products/{id}")
  public ApiResponse<AdminProduct> updateProduct(@PathVariable String id, @Valid @RequestBody ProductSaveRequest request) {
    return ApiResponse.ok(dataService.updateProduct(id, request));
  }

  @PutMapping("/products/{id}/status")
  public ApiResponse<AdminProduct> updateProductStatus(@PathVariable String id, @Valid @RequestBody ProductStatusRequest request) {
    return ApiResponse.ok(dataService.updateProductStatus(id, request.status()));
  }

  @DeleteMapping("/products/{id}")
  public ApiResponse<Void> deleteProduct(@PathVariable String id) {
    dataService.deleteProduct(id);
    return ApiResponse.ok(null);
  }

  @GetMapping("/rfqs")
  public ApiResponse<List<AdminRfq>> rfqs() {
    return ApiResponse.ok(dataService.adminRfqs());
  }

  @GetMapping(value = "/rfqs/export.csv", produces = "text/csv; charset=UTF-8")
  public ResponseEntity<String> exportRfqs() {
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"rfqs.csv\"")
        .body(dataService.adminRfqsCsv());
  }

  @GetMapping("/rfqs/{id}")
  public ApiResponse<AdminRfq> rfq(@PathVariable String id) {
    return ApiResponse.ok(dataService.adminRfq(id));
  }

  @PutMapping("/rfqs/{id}/status")
  public ApiResponse<AdminRfq> updateRfqStatus(@PathVariable String id, @Valid @RequestBody UpdateStatusRequest request) {
    return ApiResponse.ok(dataService.updateRfqStatus(id, request.status()));
  }

  @PutMapping("/rfqs/{id}/notes")
  public ApiResponse<AdminRfq> updateRfqNotes(@PathVariable String id, @RequestBody UpdateNotesRequest request) {
    return ApiResponse.ok(dataService.updateRfqNotes(id, request.notes()));
  }

  @GetMapping("/customers")
  public ApiResponse<List<Customer>> customers() {
    return ApiResponse.ok(dataService.customers());
  }

  @GetMapping("/customers/{id}")
  public ApiResponse<CustomerDetail> customer(@PathVariable String id) {
    return ApiResponse.ok(dataService.customerDetail(id));
  }

  @PutMapping("/customers/{id}")
  public ApiResponse<CustomerDetail> updateCustomer(@PathVariable String id, @RequestBody UpdateCustomerRequest request) {
    return ApiResponse.ok(dataService.updateCustomer(id, request));
  }

  @GetMapping("/articles")
  public ApiResponse<List<AdminArticle>> articles() {
    return ApiResponse.ok(dataService.adminArticles());
  }

  @GetMapping("/articles/{id}")
  public ApiResponse<AdminArticleDetail> article(@PathVariable String id) {
    return ApiResponse.ok(dataService.adminArticleDetail(id));
  }

  @PostMapping("/articles")
  public ApiResponse<AdminArticle> createArticle(@Valid @RequestBody ArticleSaveRequest request) {
    return ApiResponse.ok(dataService.createArticle(request));
  }

  @PutMapping("/articles/{id}")
  public ApiResponse<AdminArticle> updateArticle(@PathVariable String id, @Valid @RequestBody ArticleSaveRequest request) {
    return ApiResponse.ok(dataService.updateArticle(id, request));
  }

  @PutMapping("/articles/{id}/status")
  public ApiResponse<AdminArticle> updateArticleStatus(@PathVariable String id, @Valid @RequestBody UpdateStatusRequest request) {
    return ApiResponse.ok(dataService.updateArticleStatus(id, request.status()));
  }

  @DeleteMapping("/articles/{id}")
  public ApiResponse<Void> deleteArticle(@PathVariable String id) {
    dataService.deleteArticle(id);
    return ApiResponse.ok(null);
  }
}
