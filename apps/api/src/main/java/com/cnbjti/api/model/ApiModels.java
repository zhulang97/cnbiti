package com.cnbjti.api.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;

public final class ApiModels {

  private ApiModels() {
  }

  public record SeoMeta(
      String title,
      String description,
      String canonical,
      String ogTitle,
      String ogDescription,
      String ogImage,
      Boolean noIndex
  ) {
  }

  public record Language(String code, String name, String nativeName, String flag, boolean isDefault) {
  }

  public record MediaAsset(
      String id,
      String url,
      String thumbnailUrl,
      String alt,
      Integer width,
      Integer height,
      String mimeType,
      long size,
      String filename
  ) {
  }

  public record AdminStoredFile(
      String id,
      String filename,
      String objectName,
      String contentType,
      long size,
      String url,
      String createdAt
  ) {
  }

  public record AdminAuditLog(
      String id,
      String actorId,
      String actorName,
      String action,
      String targetType,
      String targetId,
      String method,
      String path,
      int statusCode,
      String ipAddress,
      String userAgent,
      String createdAt
  ) {
  }

  public record AdminAuditLogSummary(
      long total,
      long success,
      long failed,
      long fileActions
  ) {
  }

  public record AdminAuditLogPage(
      List<AdminAuditLog> items,
      long total,
      int page,
      int pageSize,
      int totalPages,
      AdminAuditLogSummary summary
  ) {
  }

  public record ProductCategory(
      String id,
      String slug,
      String name,
      String description,
      MediaAsset image,
      String icon,
      int productCount,
      SeoMeta seo
  ) {
  }

  public record TitaniumGrade(
      String id,
      String slug,
      String name,
      String shortName,
      String composition,
      String description,
      String tensileStrength,
      String yieldStrength,
      String elongation,
      Double density,
      List<String> applications
  ) {
  }

  public record Standard(
      String id,
      String slug,
      String code,
      String name,
      String description,
      List<String> productTypes
  ) {
  }

  public record ProductSpec(String label, String value, String unit) {
  }

  public record Product(
      String id,
      String slug,
      String name,
      String shortDescription,
      String description,
      ProductCategory category,
      List<TitaniumGrade> grades,
      List<Standard> standards,
      List<ProductSpec> specs,
      List<MediaAsset> images,
      SeoMeta seo,
      boolean featured,
      boolean inStock
  ) {
  }

  public record Article(
      String id,
      String slug,
      String title,
      String excerpt,
      String content,
      MediaAsset coverImage,
      String category,
      List<String> tags,
      String publishedAt,
      Integer readingTime,
      SeoMeta seo
  ) {
  }

  public record SiteConfig(
      String siteName,
      String tagline,
      String email,
      String phone,
      String whatsapp,
      String address,
      String city,
      String country,
      Map<String, String> socialLinks,
      AboutPageConfig aboutPage
  ) {
  }

  public record AboutStat(String value, String label) {
  }

  public record AboutFeature(String icon, String title, String desc) {
  }

  public record AboutTimelineEvent(String year, String title, String desc) {
  }

  public record AboutPageConfig(
      String heroLabel,
      String heroTitle,
      String heroIntro,
      String heroBody,
      List<AboutStat> stats,
      String locationLabel,
      String locationTitle,
      String locationDescription,
      List<AboutFeature> advantages,
      String timelineLabel,
      String timelineTitle,
      List<AboutTimelineEvent> timeline,
      String valuesLabel,
      String valuesTitle,
      List<AboutFeature> values,
      String ctaTitle,
      String ctaDescription,
      String seoTitle,
      String seoDescription
  ) {
  }

  public record SiteRuntime(
      SiteConfig siteConfig,
      List<NavigationItem> navigation,
      List<ProductCategory> categories
  ) {
  }

  public record SiteConfigSaveRequest(
      @NotBlank String siteName,
      String tagline,
      @Email @NotBlank String email,
      String phone,
      String whatsapp,
      String address,
      String city,
      String country,
      Map<String, String> socialLinks,
      AboutPageConfig aboutPage
  ) {
  }

  public record NavigationItem(String label, String href, List<NavigationItem> children, String badge, String icon) {
  }

  public record NavigationSaveRequest(
      List<NavigationItem> items
  ) {
  }

  public record PublicRfqRequest(
      @NotBlank String productType,
      String grade,
      @NotBlank String quantity,
      String dimensions,
      String message,
      @NotBlank String name,
      String company,
      @Email @NotBlank String email,
      String phone,
      String destinationCountry,
      List<MediaAsset> attachments
  ) {
  }

  public record RfqResponse(String rfqNo, String message, String estimatedResponseTime) {
  }

  public record PublicContactRequest(
      @NotBlank String name,
      String company,
      @Email @NotBlank String email,
      String phone,
      String subject,
      @NotBlank String message,
      String source
  ) {
  }

  public record ContactResponse(String id, String message, String estimatedResponseTime) {
  }

  public record AdminContactMessage(
      String id,
      String name,
      String company,
      String email,
      String phone,
      String subject,
      String message,
      String source,
      String status,
      String createdAt,
      String notes,
      String notesUpdatedAt
  ) {
  }

  public record AdminRfq(
      String id,
      String company,
      String country,
      String countryCode,
      String email,
      String phone,
      String product,
      String grade,
      String qty,
      String message,
      String status,
      String createdAt,
      String notes,
      String statusUpdatedAt,
      String notesUpdatedAt,
      List<MediaAsset> attachments
  ) {
  }

  public record AdminProduct(String id, String name, String category, String grade, String status, String updatedAt) {
  }

  public record AdminArticle(String id, String title, String category, String status, String publishedAt) {
  }

  public record AdminContentOptions(
      List<ProductCategory> categories,
      List<TitaniumGrade> grades,
      List<Standard> standards
  ) {
  }

  public record AdminCategoryDetail(
      String id,
      String slug,
      String name,
      String description,
      String imageUrl,
      String icon,
      int productCount,
      SeoMeta seo,
      String status,
      String updatedAt
  ) {
  }

  public record CategorySaveRequest(
      @NotBlank String slug,
      @NotBlank String name,
      String description,
      String imageUrl,
      String icon,
      Integer productCount,
      SeoMeta seo,
      @NotBlank String status
  ) {
  }

  public record AdminGradeDetail(
      String id,
      String slug,
      String name,
      String shortName,
      String composition,
      String description,
      String tensileStrength,
      String yieldStrength,
      String elongation,
      Double density,
      List<String> applications,
      String status,
      String updatedAt
  ) {
  }

  public record GradeSaveRequest(
      @NotBlank String slug,
      @NotBlank String name,
      @NotBlank String shortName,
      String composition,
      String description,
      String tensileStrength,
      String yieldStrength,
      String elongation,
      Double density,
      List<String> applications,
      @NotBlank String status
  ) {
  }

  public record AdminStandardDetail(
      String id,
      String slug,
      String code,
      String name,
      String description,
      List<String> productTypes,
      String status,
      String updatedAt
  ) {
  }

  public record StandardSaveRequest(
      @NotBlank String slug,
      @NotBlank String code,
      @NotBlank String name,
      String description,
      List<String> productTypes,
      @NotBlank String status
  ) {
  }

  public record AdminProductDetail(
      String id,
      String name,
      String slug,
      String categoryId,
      List<String> gradeIds,
      List<String> standardIds,
      String status,
      String shortDescription,
      String description,
      String imageUrl,
      List<MediaAsset> images,
      List<ProductSpec> specs,
      SeoMeta seo,
      Boolean featured,
      Boolean inStock
  ) {
  }

  public record ProductSaveRequest(
      @NotBlank String name,
      @NotBlank String slug,
      @NotBlank String categoryId,
      List<String> gradeIds,
      List<String> standardIds,
      @NotBlank String status,
      String shortDescription,
      String description,
      String imageUrl,
      List<MediaAsset> images,
      List<ProductSpec> specs,
      SeoMeta seo,
      Boolean featured,
      Boolean inStock
  ) {
  }

  public record AdminArticleDetail(
      String id,
      String title,
      String slug,
      String category,
      String status,
      String excerpt,
      String content,
      String coverImageUrl,
      List<String> tags,
      String publishedAt,
      Integer readingTime
  ) {
  }

  public record ArticleSaveRequest(
      @NotBlank String title,
      @NotBlank String slug,
      @NotBlank String category,
      @NotBlank String status,
      String excerpt,
      String content,
      String coverImageUrl,
      List<String> tags,
      String publishedAt,
      Integer readingTime
  ) {
  }

  public record Customer(
      String id,
      String company,
      String country,
      String countryCode,
      String email,
      int rfqCount,
      String lastContact
  ) {
  }

  public record CustomerDetail(
      String id,
      String company,
      String country,
      String countryCode,
      String email,
      int rfqCount,
      String lastContact,
      String notes,
      String notesUpdatedAt,
      List<AdminRfq> rfqs
  ) {
  }

  public record UpdateCustomerRequest(
      String notes,
      String lastContact
  ) {
  }

  public record CountryCount(String country, int count) {
  }

  public record ProductCount(String product, int count) {
  }

  public record AdminDashboard(
      int todayRfqs,
      int newRfqs,
      int needFollowUp,
      int pendingTranslation,
      int publishedProducts,
      List<Integer> rfqTrend,
      List<CountryCount> rfqByCountry,
      List<ProductCount> rfqByProduct
  ) {
  }

  public record AdminUser(String id, String name, String email, String role, String avatar) {
  }

  public record AdminManagedUser(
      String id,
      String username,
      String displayName,
      String email,
      String role,
      String status,
      String avatar,
      String createdAt,
      String updatedAt,
      String lastLoginAt
  ) {
  }

  public record AdminUserSaveRequest(
      @NotBlank String username,
      @NotBlank String displayName,
      @Email @NotBlank String email,
      @NotBlank String role,
      String status,
      String avatar,
      @Size(min = 8) String password
  ) {
  }

  public record AdminUserPasswordRequest(@NotBlank @Size(min = 8) String password) {
  }

  public record LoginRequest(@NotBlank String username, @NotBlank String password) {
  }

  public record LoginResponse(String token, AdminUser user) {
  }

  public record UpdateStatusRequest(@NotBlank String status) {
  }

  public record UpdateNotesRequest(String notes) {
  }

  public record ProductStatusRequest(@NotBlank String status) {
  }
}
