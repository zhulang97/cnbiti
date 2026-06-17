package com.cnbjti.api.repository;

import com.cnbjti.api.entity.CatalogContentEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogContentRepository extends JpaRepository<CatalogContentEntity, String> {

  List<CatalogContentEntity> findByContentTypeOrderBySortOrderAsc(String contentType);

  List<CatalogContentEntity> findByContentTypeAndStatusOrderBySortOrderAsc(String contentType, String status);

  Optional<CatalogContentEntity> findByContentTypeAndSlug(String contentType, String slug);

  Optional<CatalogContentEntity> findByContentTypeAndItemId(String contentType, String itemId);

  long countByPayloadJsonContaining(String value);
}
