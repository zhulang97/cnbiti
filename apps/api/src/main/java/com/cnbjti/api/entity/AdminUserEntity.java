package com.cnbjti.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_users")
public class AdminUserEntity {

  @Id
  private String id;

  @Column(nullable = false)
  private String username;

  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String role;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  private String avatar;

  @Column(nullable = false)
  private String status;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  protected AdminUserEntity() {
  }

  public AdminUserEntity(String id, String username, String displayName, String email, String role, String passwordHash,
      String avatar) {
    this(id, username, displayName, email, role, passwordHash, avatar, "active", LocalDateTime.now(), LocalDateTime.now(),
        null);
  }

  public AdminUserEntity(String id, String username, String displayName, String email, String role, String passwordHash,
      String avatar, String status, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLoginAt) {
    this.id = id;
    this.username = username;
    this.displayName = displayName;
    this.email = email;
    this.role = role;
    this.passwordHash = passwordHash;
    this.avatar = avatar;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.lastLoginAt = lastLoginAt;
  }

  public String getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getEmail() {
    return email;
  }

  public String getRole() {
    return role;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public String getAvatar() {
    return avatar;
  }

  public String getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public LocalDateTime getLastLoginAt() {
    return lastLoginAt;
  }

  public void updateProfile(String username, String displayName, String email, String role, String status, String avatar) {
    this.username = username;
    this.displayName = displayName;
    this.email = email;
    this.role = role;
    this.status = status;
    this.avatar = avatar;
    this.updatedAt = LocalDateTime.now();
  }

  public void updatePasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
    this.updatedAt = LocalDateTime.now();
  }

  public void markLoggedIn() {
    this.lastLoginAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }
}
