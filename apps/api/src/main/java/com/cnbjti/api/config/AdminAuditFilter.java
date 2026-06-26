package com.cnbjti.api.config;

import com.cnbjti.api.model.ApiModels.AdminUser;
import com.cnbjti.api.service.AuditLogService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class AdminAuditFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(AdminAuditFilter.class);
  private static final List<String> MUTATING_METHODS = List.of("POST", "PUT", "PATCH", "DELETE");

  private final AuditLogService auditLogService;

  public AdminAuditFilter(AuditLogService auditLogService) {
    this.auditLogService = auditLogService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } finally {
      if (shouldAudit(request)) {
        record(request, response);
      }
    }
  }

  private boolean shouldAudit(HttpServletRequest request) {
    return request.getRequestURI().startsWith("/api/admin/")
        && MUTATING_METHODS.contains(request.getMethod().toUpperCase(Locale.ROOT));
  }

  private void record(HttpServletRequest request, HttpServletResponse response) {
    try {
      String[] parts = request.getRequestURI().replaceFirst("^/api/admin/?", "").split("/");
      String resource = parts.length == 0 || parts[0].isBlank() ? "admin" : parts[0];
      auditLogService.record(
          currentUser(),
          action(request.getMethod()),
          targetType(resource),
          targetId(resource, parts),
          request.getMethod(),
          request.getRequestURI(),
          response.getStatus(),
          auditLogService.clientIp(request),
          request.getHeader("User-Agent"));
    } catch (Exception ex) {
      log.warn("Unable to write admin audit log: {}", ex.getMessage(), ex);
    }
  }

  private AdminUser currentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.getPrincipal() instanceof AdminUser user) {
      return user;
    }
    return null;
  }

  private String action(String method) {
    return switch (method.toUpperCase(Locale.ROOT)) {
      case "POST" -> "CREATE";
      case "PUT", "PATCH" -> "UPDATE";
      case "DELETE" -> "DELETE";
      default -> method;
    };
  }

  private String targetType(String resource) {
    return switch (resource) {
      case "articles" -> "ARTICLE";
      case "categories" -> "CATEGORY";
      case "contact-messages" -> "CONTACT_MESSAGE";
      case "customers" -> "CUSTOMER";
      case "files" -> "FILE";
      case "grades" -> "GRADE";
      case "industries" -> "INDUSTRY";
      case "navigation" -> "NAVIGATION";
      case "products" -> "PRODUCT";
      case "rfqs" -> "RFQ";
      case "site-config" -> "SITE_CONFIG";
      case "standards" -> "STANDARD";
      case "users" -> "ADMIN_USER";
      default -> resource.replace("-", "_").toUpperCase(Locale.ROOT);
    };
  }

  private String targetId(String resource, String[] parts) {
    if ("site-config".equals(resource) || "navigation".equals(resource)) {
      return "main";
    }
    return parts.length > 1 && !parts[1].isBlank() ? parts[1] : null;
  }
}
