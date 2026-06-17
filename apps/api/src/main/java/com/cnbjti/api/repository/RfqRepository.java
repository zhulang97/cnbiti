package com.cnbjti.api.repository;

import com.cnbjti.api.entity.RfqEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RfqRepository extends JpaRepository<RfqEntity, String> {

  List<RfqEntity> findAllByOrderByCreatedAtDescIdDesc();

  List<RfqEntity> findAllByEmailIgnoreCaseOrderByCreatedAtDescIdDesc(String email);

  long countByStatus(String status);

  long countByCreatedAt(LocalDate createdAt);

  long countByAttachmentsJsonContaining(String value);
}
