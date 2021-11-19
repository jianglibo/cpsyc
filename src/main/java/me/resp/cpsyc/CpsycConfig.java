package me.resp.cpsyc;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class CpsycConfig {
  private String faunaAdminSecret;
  private String faunaServerSecret;
  private String faunaEndpoint;
}
