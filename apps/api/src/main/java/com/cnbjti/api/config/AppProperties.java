package com.cnbjti.api.config;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

public final class AppProperties {

  private AppProperties() {
  }

  @ConfigurationProperties(prefix = "cnbjti.admin.bootstrap")
  public record AdminBootstrapProperties(
      String username,
      String displayName,
      String email,
      String password
  ) {
  }

  @ConfigurationProperties(prefix = "cnbjti.security")
  public record SecurityProperties(
      String tokenSecret,
      Duration tokenTtl
  ) {
  }

  @ConfigurationProperties(prefix = "cnbjti.cors")
  public record CorsProperties(List<String> allowedOriginPatterns) {
  }
}
