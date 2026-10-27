package com.smartcommunity.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.cors.*;
import org.springframework.web.filter.CorsFilter;
import java.util.List;
@Configuration
public class CorsConfig {
 @Bean
 public CorsConfigurationSource corsConfigurationSource() {
  CorsConfiguration config = new CorsConfiguration();

  config.setAllowedOrigins(List.of("*"
  ));

  config.setAllowedMethods(List.of(
          "GET", "POST", "PUT", "PATCH",
          "DELETE", "OPTIONS"
  ));

  config.setAllowedHeaders(List.of(
          "Authorization", "Content-Type"
  ));

  config.setAllowCredentials(false);

  UrlBasedCorsConfigurationSource source =
          new UrlBasedCorsConfigurationSource();

  source.registerCorsConfiguration("/**", config);

  return source;
 }
}
