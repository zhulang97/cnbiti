package com.cnbjti.api.service;

import com.cnbjti.api.entity.AdminUserEntity;
import com.cnbjti.api.model.ApiModels.AdminManagedUser;
import com.cnbjti.api.model.ApiModels.AdminUser;
import com.cnbjti.api.model.ApiModels.AdminUserPasswordRequest;
import com.cnbjti.api.model.ApiModels.AdminUserSaveRequest;
import com.cnbjti.api.repository.AdminUserRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserService {

  private static final Set<String> ROLES = Set.of("ADMIN", "EDITOR", "SALES");
  private static final Set<String> STATUSES = Set.of("active", "disabled");

  private final AdminUserRepository adminUserRepository;
  private final PasswordEncoder passwordEncoder;

  public AdminUserService(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
    this.adminUserRepository = adminUserRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public List<AdminManagedUser> users() {
    return adminUserRepository.findAll().stream()
        .sorted(Comparator.comparing(AdminUserEntity::getUsername, String.CASE_INSENSITIVE_ORDER))
        .map(this::toManagedUser)
        .toList();
  }

  @Transactional
  public AdminManagedUser create(AdminUserSaveRequest request) {
    String username = username(request.username());
    String email = email(request.email());
    String role = role(request.role());
    String status = status(request.status());
    String password = text(request.password());
    if (password.isBlank() || password.length() < 8) {
      throw new IllegalArgumentException("Password must be at least 8 characters.");
    }
    ensureUniqueUsername(null, username);
    ensureUniqueEmail(null, email);
    LocalDateTime now = LocalDateTime.now();
    AdminUserEntity entity = new AdminUserEntity(
        "admin_" + UUID.randomUUID().toString().replace("-", ""),
        username,
        text(request.displayName()),
        email,
        role,
        passwordEncoder.encode(password),
        nullableText(request.avatar()),
        status,
        now,
        now,
        null);
    return toManagedUser(adminUserRepository.save(entity));
  }

  @Transactional
  public AdminManagedUser update(String id, AdminUserSaveRequest request, AdminUser actor) {
    AdminUserEntity entity = find(id);
    String username = username(request.username());
    String email = email(request.email());
    String role = role(request.role());
    String status = status(request.status());
    ensureSelfSafety(entity, role, status, actor);
    ensureLastAdmin(entity, role, status);
    ensureUniqueUsername(id, username);
    ensureUniqueEmail(id, email);
    entity.updateProfile(username, text(request.displayName()), email, role, status, nullableText(request.avatar()));
    return toManagedUser(adminUserRepository.save(entity));
  }

  @Transactional
  public AdminManagedUser resetPassword(String id, AdminUserPasswordRequest request) {
    AdminUserEntity entity = find(id);
    entity.updatePasswordHash(passwordEncoder.encode(request.password().trim()));
    return toManagedUser(adminUserRepository.save(entity));
  }

  private AdminUserEntity find(String id) {
    return adminUserRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Admin user not found."));
  }

  private void ensureUniqueUsername(String id, String username) {
    adminUserRepository.findByUsernameIgnoreCase(username)
        .filter(existing -> !existing.getId().equals(id))
        .ifPresent(existing -> {
          throw new IllegalArgumentException("Username already exists.");
        });
  }

  private void ensureUniqueEmail(String id, String email) {
    adminUserRepository.findByEmailIgnoreCase(email)
        .filter(existing -> !existing.getId().equals(id))
        .ifPresent(existing -> {
          throw new IllegalArgumentException("Email already exists.");
        });
  }

  private void ensureSelfSafety(AdminUserEntity entity, String role, String status, AdminUser actor) {
    if (actor == null || !entity.getId().equals(actor.id())) {
      return;
    }
    if (!entity.getRole().equals(role) || !entity.getStatus().equals(status)) {
      throw new IllegalArgumentException("You cannot change your own role or status.");
    }
  }

  private void ensureLastAdmin(AdminUserEntity entity, String role, String status) {
    boolean currentlyActiveAdmin = "ADMIN".equals(entity.getRole()) && "active".equals(entity.getStatus());
    boolean remainsActiveAdmin = "ADMIN".equals(role) && "active".equals(status);
    if (currentlyActiveAdmin && !remainsActiveAdmin && adminUserRepository.countByRoleAndStatus("ADMIN", "active") <= 1) {
      throw new IllegalArgumentException("At least one active administrator is required.");
    }
  }

  private AdminManagedUser toManagedUser(AdminUserEntity entity) {
    return new AdminManagedUser(
        entity.getId(),
        entity.getUsername(),
        entity.getDisplayName(),
        entity.getEmail(),
        entity.getRole(),
        entity.getStatus(),
        entity.getAvatar(),
        date(entity.getCreatedAt()),
        date(entity.getUpdatedAt()),
        date(entity.getLastLoginAt()));
  }

  private String username(String value) {
    String username = text(value).toLowerCase(Locale.ROOT);
    if (!username.matches("[a-z0-9._-]{3,40}")) {
      throw new IllegalArgumentException("Username must be 3-40 characters using letters, numbers, dots, underscores, or dashes.");
    }
    return username;
  }

  private String email(String value) {
    return text(value).toLowerCase(Locale.ROOT);
  }

  private String role(String value) {
    String role = text(value).toUpperCase(Locale.ROOT);
    if (!ROLES.contains(role)) {
      throw new IllegalArgumentException("Unsupported admin role.");
    }
    return role;
  }

  private String status(String value) {
    String status = text(value).isBlank() ? "active" : text(value).toLowerCase(Locale.ROOT);
    if (!STATUSES.contains(status)) {
      throw new IllegalArgumentException("Unsupported admin status.");
    }
    return status;
  }

  private String text(String value) {
    return value == null ? "" : value.trim();
  }

  private String nullableText(String value) {
    String text = text(value);
    return text.isBlank() ? null : text;
  }

  private String date(LocalDateTime value) {
    return value == null ? null : value.toString();
  }
}
