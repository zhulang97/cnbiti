package com.cnbjti.api;

import com.cnbjti.api.config.AppProperties.AdminBootstrapProperties;
import com.cnbjti.api.config.AppProperties.CorsProperties;
import com.cnbjti.api.config.AppProperties.SecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AdminBootstrapProperties.class, CorsProperties.class, SecurityProperties.class})
public class CnbjtiApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(CnbjtiApiApplication.class, args);
  }
}
