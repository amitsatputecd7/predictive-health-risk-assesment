package com.hackforjob.metlife.predictive_health_risk_assesment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*") // Use allowedOriginPatterns instead of allowedOrigins
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false) // Set to false for testing, true if credentials are needed
                .maxAge(3600); // Cache preflight response for 1 hour
    }

    // Alternative bean-based configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Use allowedOriginPatterns instead of allowedOrigins
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // Allowed methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Allowed headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Allow credentials (set to false for testing)
        configuration.setAllowCredentials(false);
        
        // Cache preflight response
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }
}