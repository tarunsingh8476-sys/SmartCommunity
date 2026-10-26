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

  config.setAllowedOrigins(List.of(
          "http://localhost:5173",
          "http://localhost:5174",
          "https://group3-frontend-smart-community-tnaguzl7m-shruti-65e2.vercel.app"
  ));

  config.setAllowedMethods(List.of(
          "GET", "POST", "PUT", "PATCH",
          "DELETE", "OPTIONS"
  ));

  config.setAllowedHeaders(List.of(
          "Authorization", "Content-Type"
  ));

  config.setAllowCredentials(true);

  UrlBasedCorsConfigurationSource source =
          new UrlBasedCorsConfigurationSource();

  source.registerCorsConfiguration("/**", config);

  return source;
 }
}
