package com.cnbjti.api.config;

import com.cnbjti.api.config.AppProperties.CorsProperties;
import com.cnbjti.api.model.ApiModels.AdminUser;
import com.cnbjti.api.service.AuditLogService;
import com.cnbjti.api.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, AuthTokenFilter authTokenFilter,
      AdminAuditFilter adminAuditFilter, CorsConfigurationSource corsConfigurationSource) throws Exception {
    return http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/public/**", "/api/auth/**", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers("/api/admin/me", "/api/admin/dashboard").authenticated()
            .requestMatchers("/api/admin/users/**", "/api/admin/site-config/**", "/api/admin/navigation/**",
                "/api/admin/audit-logs/**").hasRole("ADMIN")
            .requestMatchers("/api/admin/products/**", "/api/admin/articles/**", "/api/admin/files/**",
                "/api/admin/categories/**", "/api/admin/grades/**", "/api/admin/standards/**",
                "/api/admin/industries/**",
                "/api/admin/content-options").hasAnyRole("ADMIN", "EDITOR")
            .requestMatchers("/api/admin/rfqs/**", "/api/admin/customers/**", "/api/admin/contact-messages/**")
                .hasAnyRole("ADMIN", "SALES")
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .anyRequest().permitAll())
        .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(adminAuditFilter, AuthTokenFilter.class)
        .build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource(CorsProperties properties) {
    CorsConfiguration config = new CorsConfiguration();
    List<String> allowedOrigins = properties.allowedOriginPatterns() == null || properties.allowedOriginPatterns().isEmpty()
        ? List.of("http://localhost:*", "http://127.0.0.1:*", "http://[::1]:*")
        : properties.allowedOriginPatterns();
    config.setAllowedOriginPatterns(allowedOrigins);
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  AuthTokenFilter authTokenFilter(AuthService authService) {
    return new AuthTokenFilter(authService);
  }

  @Bean
  AdminAuditFilter adminAuditFilter(AuditLogService auditLogService) {
    return new AdminAuditFilter(auditLogService);
  }

  @Bean
  FilterRegistrationBean<AdminAuditFilter> adminAuditFilterRegistration(AdminAuditFilter adminAuditFilter) {
    FilterRegistrationBean<AdminAuditFilter> registration = new FilterRegistrationBean<>(adminAuditFilter);
    registration.setEnabled(false);
    return registration;
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  static class AuthTokenFilter extends OncePerRequestFilter {

    private final AuthService authService;

    AuthTokenFilter(AuthService authService) {
      this.authService = authService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
      String header = request.getHeader(HttpHeaders.AUTHORIZATION);
      if (header != null && header.startsWith("Bearer ")) {
        String token = header.substring(7);
        authService.authenticate(token).ifPresent(this::authenticate);
      }
      filterChain.doFilter(request, response);
    }

    private void authenticate(AdminUser user) {
      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
          user,
          null,
          List.of(new SimpleGrantedAuthority("ROLE_" + user.role()))
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
  }
}
