package com.cnbjti.api.service;

import com.cnbjti.api.config.StorageConfig.MinioProperties;
import com.cnbjti.api.entity.StoredFileEntity;
import com.cnbjti.api.model.ApiModels.AdminStoredFile;
import com.cnbjti.api.model.ApiModels.MediaAsset;
import com.cnbjti.api.repository.CatalogContentRepository;
import com.cnbjti.api.repository.RfqRepository;
import com.cnbjti.api.repository.StoredFileRepository;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

  private final MinioClient minioClient;
  private final MinioProperties properties;
  private final StoredFileRepository storedFileRepository;
  private final CatalogContentRepository catalogContentRepository;
  private final RfqRepository rfqRepository;
  private volatile boolean bucketReady;

  public FileStorageService(MinioClient minioClient, MinioProperties properties, StoredFileRepository storedFileRepository,
      CatalogContentRepository catalogContentRepository, RfqRepository rfqRepository) {
    this.minioClient = minioClient;
    this.properties = properties;
    this.storedFileRepository = storedFileRepository;
    this.catalogContentRepository = catalogContentRepository;
    this.rfqRepository = rfqRepository;
  }

  public MediaAsset store(MultipartFile file) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("Uploaded file is empty");
    }
    validateFile(file);
    String id = "file_" + UUID.randomUUID().toString().replace("-", "");
    String originalFilename = cleanFilename(file.getOriginalFilename());
    String contentType = file.getContentType() == null ? "application/octet-stream" : file.getContentType();
    String objectName = LocalDate.now() + "/" + id + extension(originalFilename);

    try (InputStream inputStream = file.getInputStream()) {
      ensureBucket();
      minioClient.putObject(PutObjectArgs.builder()
          .bucket(properties.bucket())
          .object(objectName)
          .stream(inputStream, file.getSize(), -1)
          .contentType(contentType)
          .build());
    } catch (Exception ex) {
      throw new IllegalStateException("Unable to store uploaded file: " + ex.getMessage(), ex);
    }

    String publicUrl = trimSlash(properties.publicUrl()) + "/" + objectName;
    storedFileRepository.save(new StoredFileEntity(id, objectName, originalFilename, contentType, file.getSize(),
        publicUrl, LocalDateTime.now()));
    return new MediaAsset(id, publicUrl, null, originalFilename, null, null, contentType, file.getSize(), originalFilename);
  }

  public List<AdminStoredFile> adminFiles() {
    return storedFileRepository.findAllByOrderByCreatedAtDesc().stream()
        .map(this::toAdminStoredFile)
        .toList();
  }

  public void deleteFile(String id) {
    StoredFileEntity file = storedFileRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("Stored file not found: " + id));
    if (isReferenced(file)) {
      throw new IllegalArgumentException("Stored file is referenced by content or RFQs and cannot be deleted");
    }
    try {
      minioClient.removeObject(RemoveObjectArgs.builder()
          .bucket(properties.bucket())
          .object(file.getObjectName())
          .build());
    } catch (Exception ex) {
      throw new IllegalStateException("Unable to delete stored file: " + ex.getMessage(), ex);
    }
    storedFileRepository.delete(file);
  }

  private void ensureBucket() throws Exception {
    if (bucketReady) {
      return;
    }
    synchronized (this) {
      if (bucketReady) {
        return;
      }
      ensureBucketExists();
      bucketReady = true;
    }
  }

  private void ensureBucketExists() throws Exception {
    boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.bucket()).build());
    if (!exists) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.bucket()).build());
    }
    minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
        .bucket(properties.bucket())
        .config(publicReadPolicy(properties.bucket()))
        .build());
  }

  private String publicReadPolicy(String bucket) {
    return """
        {
          "Version": "2012-10-17",
          "Statement": [
            {
              "Effect": "Allow",
              "Principal": "*",
              "Action": ["s3:GetObject"],
              "Resource": ["arn:aws:s3:::%s/*"]
            }
          ]
        }
        """.formatted(bucket);
  }

  private String cleanFilename(String filename) {
    if (filename == null || filename.isBlank()) {
      return "upload";
    }
    return filename.replace("\\", "/").substring(filename.replace("\\", "/").lastIndexOf('/') + 1);
  }

  private String extension(String filename) {
    int dot = filename.lastIndexOf('.');
    if (dot < 0) {
      return "";
    }
    String ext = filename.substring(dot).toLowerCase(Locale.ROOT);
    return ext.matches("\\.[a-z0-9]{1,12}") ? ext : "";
  }

  private String trimSlash(String value) {
    return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
  }

  private void validateFile(MultipartFile file) {
    if (properties.maxSizeBytes() > 0 && file.getSize() > properties.maxSizeBytes()) {
      throw new IllegalArgumentException("Uploaded file exceeds the size limit");
    }
    String contentType = file.getContentType() == null ? "application/octet-stream" : file.getContentType().toLowerCase(Locale.ROOT);
    List<String> allowedTypes = properties.allowedContentTypes() == null ? List.of() : properties.allowedContentTypes();
    if (!allowedTypes.isEmpty() && allowedTypes.stream().map(type -> type.toLowerCase(Locale.ROOT)).noneMatch(type -> matchesType(contentType, type))) {
      throw new IllegalArgumentException("Uploaded file type is not allowed");
    }
  }

  private boolean matchesType(String contentType, String allowedType) {
    if (allowedType.endsWith("/*")) {
      return contentType.startsWith(allowedType.substring(0, allowedType.length() - 1));
    }
    return contentType.equals(allowedType);
  }

  private boolean isReferenced(StoredFileEntity file) {
    return isReferencedToken(file.getId())
        || isReferencedToken(file.getPublicUrl())
        || isReferencedToken(file.getObjectName());
  }

  private boolean isReferencedToken(String token) {
    if (token == null || token.isBlank()) {
      return false;
    }
    return catalogContentRepository.countByPayloadJsonContaining(token) > 0
        || rfqRepository.countByAttachmentsJsonContaining(token) > 0;
  }

  private AdminStoredFile toAdminStoredFile(StoredFileEntity file) {
    return new AdminStoredFile(
        file.getId(),
        file.getOriginalFilename(),
        file.getObjectName(),
        file.getContentType(),
        file.getSizeBytes(),
        file.getPublicUrl(),
        file.getCreatedAt().toString());
  }
}
