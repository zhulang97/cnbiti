package com.cnbjti.api.repository;

import com.cnbjti.api.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, String>, JpaSpecificationExecutor<AuditLogEntity> {
}
