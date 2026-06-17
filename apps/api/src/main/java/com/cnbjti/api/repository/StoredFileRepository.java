package com.cnbjti.api.repository;

import com.cnbjti.api.entity.StoredFileEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredFileRepository extends JpaRepository<StoredFileEntity, String> {

  List<StoredFileEntity> findAllByOrderByCreatedAtDesc();
}
