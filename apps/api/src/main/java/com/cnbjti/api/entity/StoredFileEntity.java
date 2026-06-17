package com.cnbjti.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "stored_files")
public class StoredFileEntity {

  @Id
  private String id;

  @Column(name = "object_name", nullable = false)
  private String objectName;

  @Column(name = "original_filename", nullable = false)
  private String originalFilename;

  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Column(name = "size_bytes", nullable = false)
  private long sizeBytes;

  @Column(name = "public_url", nullable = false)
  private String publicUrl;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  protected StoredFileEntity() {
  }

  public StoredFileEntity(String id, String objectName, String originalFilename, String contentType, long sizeBytes,
      String publicUrl, LocalDateTime createdAt) {
    this.id = id;
    this.objectName = objectName;
    this.originalFilename = originalFilename;
    this.contentType = contentType;
    this.sizeBytes = sizeBytes;
    this.publicUrl = publicUrl;
    this.createdAt = createdAt;
  }

  public String getId() {
    return id;
  }

  public String getObjectName() {
    return objectName;
  }

  public String getOriginalFilename() {
    return originalFilename;
  }

  public String getContentType() {
    return contentType;
  }

  public long getSizeBytes() {
    return sizeBytes;
  }

  public String getPublicUrl() {
    return publicUrl;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
