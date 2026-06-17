package com.cnbjti.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "catalog_content")
public class CatalogContentEntity {

  @Id
  private String id;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column(name = "item_id", nullable = false)
  private String itemId;

  private String slug;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String status;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder;

  @Column(name = "payload_json", nullable = false, columnDefinition = "text")
  private String payloadJson;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  protected CatalogContentEntity() {
  }

  public CatalogContentEntity(String id, String contentType, String itemId, String slug, String title, String status,
      int sortOrder, String payloadJson, LocalDateTime updatedAt) {
    this.id = id;
    this.contentType = contentType;
    this.itemId = itemId;
    this.slug = slug;
    this.title = title;
    this.status = status;
    this.sortOrder = sortOrder;
    this.payloadJson = payloadJson;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public String getContentType() {
    return contentType;
  }

  public String getItemId() {
    return itemId;
  }

  public String getSlug() {
    return slug;
  }

  public String getTitle() {
    return title;
  }

  public String getStatus() {
    return status;
  }

  public int getSortOrder() {
    return sortOrder;
  }

  public String getPayloadJson() {
    return payloadJson;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
