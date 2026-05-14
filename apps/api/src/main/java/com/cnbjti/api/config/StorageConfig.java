package com.cnbjti.api.config;

import io.minio.MinioClient;
import java.time.Duration;
import java.util.List;
import okhttp3.OkHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StorageConfig.MinioProperties.class)
public class StorageConfig {

  @Bean
  MinioClient minioClient(MinioProperties properties) {
    OkHttpClient httpClient = new OkHttpClient.Builder()
        .connectTimeout(Duration.ofSeconds(5))
        .readTimeout(Duration.ofSeconds(15))
        .writeTimeout(Duration.ofSeconds(15))
        .callTimeout(Duration.ofSeconds(20))
        .build();
    return MinioClient.builder()
        .endpoint(properties.endpoint())
        .credentials(properties.accessKey(), properties.secretKey())
        .httpClient(httpClient)
        .build();
  }

  @ConfigurationProperties(prefix = "cnbjti.storage.minio")
  public record MinioProperties(
      String endpoint,
      String accessKey,
      String secretKey,
      String bucket,
      String publicUrl,
      long maxSizeBytes,
      List<String> allowedContentTypes
  ) {
  }
}
