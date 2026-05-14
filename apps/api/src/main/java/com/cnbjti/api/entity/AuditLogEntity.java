package com.cnbjti.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLogEntity {

  @Id
  private String id;

  @Column(name = "actor_id")
  private String actorId;

  @Column(name = "actor_name", nullable = false)
  private String actorName;

  @Column(nullable = false)
  private String action;

  @Column(name = "target_type", nullable = false)
  private String targetType;

  @Column(name = "target_id")
  private String targetId;

  @Column(nullable = false)
  private String method;

  @Column(nullable = false)
  private String path;

  @Column(name = "status_code", nullable = false)
  private int statusCode;

  @Column(name = "ip_address")
  private String ipAddress;

  @Column(name = "user_agent")
  private String userAgent;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  protected AuditLogEntity() {
  }

  public AuditLogEntity(String id, String actorId, String actorName, String action, String targetType,
      String targetId, String method, String path, int statusCode, String ipAddress, String userAgent,
      LocalDateTime createdAt) {
    this.id = id;
    this.actorId = actorId;
    this.actorName = actorName;
    this.action = action;
    this.targetType = targetType;
    this.targetId = targetId;
    this.method = method;
    this.path = path;
    this.statusCode = statusCode;
    this.ipAddress = ipAddress;
    this.userAgent = userAgent;
    this.createdAt = createdAt;
  }

  public String getId() {
    return id;
  }

  public String getActorId() {
    return actorId;
  }

  public String getActorName() {
    return actorName;
  }

  public String getAction() {
    return action;
  }

  public String getTargetType() {
    return targetType;
  }

  public String getTargetId() {
    return targetId;
  }

  public String getMethod() {
    return method;
  }

  public String getPath() {
    return path;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getIpAddress() {
    return ipAddress;
  }

  public String getUserAgent() {
    return userAgent;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
