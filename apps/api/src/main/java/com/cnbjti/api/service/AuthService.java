package com.cnbjti.api.service;

import com.cnbjti.api.config.AppProperties.SecurityProperties;
import com.cnbjti.api.entity.AdminUserEntity;
import com.cnbjti.api.model.ApiModels.AdminUser;
import com.cnbjti.api.model.ApiModels.LoginResponse;
import com.cnbjti.api.repository.AdminUserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AdminUserRepository adminUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final SecurityProperties securityProperties;
  private final ObjectMapper objectMapper;

  public AuthService(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder,
      SecurityProperties securityProperties, ObjectMapper objectMapper) {
    this.adminUserRepository = adminUserRepository;
    this.passwordEncoder = passwordEncoder;
    this.securityProperties = securityProperties;
    this.objectMapper = objectMapper;
  }

  public LoginResponse login(String username, String password) {
    AdminUserEntity userEntity = adminUserRepository.findByUsernameIgnoreCase(text(username))
        .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
    if (!"active".equals(userEntity.getStatus())) {
      throw new BadCredentialsException("Invalid credentials");
    }
    if (!passwordEncoder.matches(password, userEntity.getPasswordHash())) {
      throw new BadCredentialsException("Invalid credentials");
    }
    userEntity.markLoggedIn();
    adminUserRepository.save(userEntity);
    AdminUser adminUser = toAdminUser(userEntity);
    String token = createToken(adminUser.id());
    return new LoginResponse(token, adminUser);
  }

  public Optional<AdminUser> authenticate(String token) {
    Optional<String> userId = verifyToken(token);
    if (userId.isEmpty()) {
      return Optional.empty();
    }
    return adminUserRepository.findById(userId.get())
        .filter(entity -> "active".equals(entity.getStatus()))
        .map(this::toAdminUser);
  }

  public AdminUser adminUser() {
    return adminUserRepository.findByUsername("admin")
        .map(this::toAdminUser)
        .orElse(new AdminUser("u1", "admin", "admin@cnbjti.com", "ADMIN", null));
  }

  private AdminUser toAdminUser(AdminUserEntity userEntity) {
    return new AdminUser(userEntity.getId(), userEntity.getDisplayName(), userEntity.getEmail(), userEntity.getRole(),
        userEntity.getAvatar());
  }

  private String createToken(String userId) {
    long expiresAt = Instant.now().plus(tokenTtl()).getEpochSecond();
    String payload = encode("{\"sub\":\"%s\",\"exp\":%d,\"jti\":\"%s\"}".formatted(
        userId, expiresAt, UUID.randomUUID().toString().replace("-", "")));
    return "adm_" + payload + "." + sign(payload);
  }

  private Optional<String> verifyToken(String token) {
    if (token == null || !token.startsWith("adm_")) {
      return Optional.empty();
    }
    String unsigned = token.substring(4);
    int separator = unsigned.lastIndexOf('.');
    if (separator <= 0 || separator == unsigned.length() - 1) {
      return Optional.empty();
    }
    String payload = unsigned.substring(0, separator);
    String signature = unsigned.substring(separator + 1);
    if (!constantTimeEquals(sign(payload), signature)) {
      return Optional.empty();
    }
    try {
      JsonNode json = objectMapper.readTree(Base64.getUrlDecoder().decode(payload));
      if (json.path("exp").asLong(0) < Instant.now().getEpochSecond()) {
        return Optional.empty();
      }
      String subject = json.path("sub").asText("");
      return subject.isBlank() ? Optional.empty() : Optional.of(subject);
    } catch (Exception ex) {
      return Optional.empty();
    }
  }

  private String encode(String value) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
  }

  private String sign(String payload) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(tokenSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
      return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
      throw new IllegalStateException("Unable to sign admin token.");
    }
  }

  private boolean constantTimeEquals(String expected, String actual) {
    byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
    byte[] actualBytes = actual.getBytes(StandardCharsets.UTF_8);
    if (expectedBytes.length != actualBytes.length) {
      return false;
    }
    int result = 0;
    for (int i = 0; i < expectedBytes.length; i++) {
      result |= expectedBytes[i] ^ actualBytes[i];
    }
    return result == 0;
  }

  private String tokenSecret() {
    String secret = text(securityProperties.tokenSecret());
    if (secret.length() < 32) {
      throw new IllegalStateException("ADMIN_TOKEN_SECRET must be at least 32 characters.");
    }
    return secret;
  }

  private java.time.Duration tokenTtl() {
    return securityProperties.tokenTtl() == null ? java.time.Duration.ofHours(12) : securityProperties.tokenTtl();
  }

  private String text(String value) {
    return value == null ? "" : value.trim();
  }
}
