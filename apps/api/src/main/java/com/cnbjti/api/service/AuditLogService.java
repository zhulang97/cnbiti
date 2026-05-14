package com.cnbjti.api.service;

import com.cnbjti.api.entity.AuditLogEntity;
import com.cnbjti.api.model.ApiModels.AdminAuditLog;
import com.cnbjti.api.model.ApiModels.AdminAuditLogPage;
import com.cnbjti.api.model.ApiModels.AdminAuditLogSummary;
import com.cnbjti.api.model.ApiModels.AdminUser;
import com.cnbjti.api.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

  private final AuditLogRepository auditLogRepository;

  public AuditLogService(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }

  public AdminAuditLogPage adminLogs(String keyword, String action, String targetType, String status,
      int page, int pageSize) {
    int safePage = Math.max(page, 1);
    int safePageSize = Math.min(Math.max(pageSize, 1), 100);
    Specification<AuditLogEntity> spec = auditLogSpec(keyword, action, targetType, status);
    Page<AuditLogEntity> result = auditLogRepository.findAll(spec,
        PageRequest.of(safePage - 1, safePageSize, Sort.by(Sort.Direction.DESC, "createdAt")));
    long total = result.getTotalElements();
    AdminAuditLogSummary summary = new AdminAuditLogSummary(
        total,
        auditLogRepository.count(spec.and(successStatusSpec())),
        auditLogRepository.count(spec.and(failedStatusSpec())),
        auditLogRepository.count(spec.and(targetTypeSpec("FILE"))));
    return new AdminAuditLogPage(
        result.getContent().stream().map(this::toAdminAuditLog).toList(),
        total,
        safePage,
        safePageSize,
        result.getTotalPages(),
        summary);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void record(AdminUser actor, String action, String targetType, String targetId, String method, String path,
      int statusCode, String ipAddress, String userAgent) {
    AdminUser safeActor = actor == null ? new AdminUser(null, "anonymous", "", "", null) : actor;
    AuditLogEntity entity = new AuditLogEntity(
        "audit_" + UUID.randomUUID().toString().replace("-", ""),
        safeActor.id(),
        text(safeActor.name(), "anonymous"),
        text(action, "UNKNOWN").toUpperCase(Locale.ROOT),
        text(targetType, "UNKNOWN").toUpperCase(Locale.ROOT),
        text(targetId, null),
        text(method, "UNKNOWN").toUpperCase(Locale.ROOT),
        trim(path, 500),
        statusCode,
        trim(ipAddress, 80),
        trim(userAgent, 500),
        LocalDateTime.now());
    auditLogRepository.save(entity);
  }

  public void recordUpload(AdminUser actor, String fileId, HttpServletRequest request) {
    record(actor, "UPLOAD", "FILE", fileId, request.getMethod(), request.getRequestURI(), 200,
        clientIp(request), request.getHeader("User-Agent"));
  }

  public String clientIp(HttpServletRequest request) {
    String forwarded = request.getHeader("X-Forwarded-For");
    if (forwarded != null && !forwarded.isBlank()) {
      return forwarded.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }

  private AdminAuditLog toAdminAuditLog(AuditLogEntity entity) {
    return new AdminAuditLog(
        entity.getId(),
        entity.getActorId(),
        entity.getActorName(),
        entity.getAction(),
        entity.getTargetType(),
        entity.getTargetId(),
        entity.getMethod(),
        entity.getPath(),
        entity.getStatusCode(),
        entity.getIpAddress(),
        entity.getUserAgent(),
        entity.getCreatedAt().toString());
  }

  private Specification<AuditLogEntity> auditLogSpec(String keyword, String action, String targetType, String status) {
    String safeKeyword = normalizedText(keyword);
    String safeAction = normalizedCode(action);
    String safeTargetType = normalizedCode(targetType);
    String safeStatus = normalizedStatus(status);
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (safeKeyword != null) {
        String like = "%" + safeKeyword.toLowerCase(Locale.ROOT) + "%";
        predicates.add(criteriaBuilder.or(
            criteriaBuilder.like(criteriaBuilder.lower(root.get("actorName")), like),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("action")), like),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("targetType")), like),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("targetId")), like),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("method")), like),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("path")), like),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("ipAddress")), like),
            criteriaBuilder.like(criteriaBuilder.lower(root.get("userAgent")), like)));
      }
      if (safeAction != null) {
        predicates.add(criteriaBuilder.equal(root.get("action"), safeAction));
      }
      if (safeTargetType != null) {
        predicates.add(criteriaBuilder.equal(root.get("targetType"), safeTargetType));
      }
      if ("SUCCESS".equals(safeStatus)) {
        predicates.add(criteriaBuilder.lt(root.get("statusCode"), 400));
      } else if ("FAILED".equals(safeStatus)) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("statusCode"), 400));
      }
      return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    };
  }

  private Specification<AuditLogEntity> successStatusSpec() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.lt(root.get("statusCode"), 400);
  }

  private Specification<AuditLogEntity> failedStatusSpec() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("statusCode"), 400);
  }

  private Specification<AuditLogEntity> targetTypeSpec(String targetType) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("targetType"), targetType);
  }

  private String text(String value, String fallback) {
    return value == null || value.isBlank() ? fallback : value;
  }

  private String normalizedText(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }

  private String normalizedCode(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim().toUpperCase(Locale.ROOT);
  }

  private String normalizedStatus(String value) {
    String status = normalizedCode(value);
    if ("OK".equals(status)) {
      return "SUCCESS";
    }
    if ("ERROR".equals(status) || "FAILURE".equals(status)) {
      return "FAILED";
    }
    return status;
  }

  private String trim(String value, int maxLength) {
    if (value == null) {
      return null;
    }
    String trimmed = value.trim();
    return trimmed.length() <= maxLength ? trimmed : trimmed.substring(0, maxLength);
  }
}
