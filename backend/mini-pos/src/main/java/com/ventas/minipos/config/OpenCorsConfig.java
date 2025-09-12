package com.ventas.minipos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class OpenCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // La forma correcta de permitir credenciales
        config.setAllowCredentials(true);

        // Especifica el origen exacto de tu frontend.
        // **IMPORTANTE**: Cambia http://localhost:8081 a la URL real de tu aplicación Vue.js en producción.
        config.setAllowedOriginPatterns(List.of("https://merry-sfogliatella-cc2627.netlify.app"));

        // Estos métodos y cabeceras son seguros con un origen específico.
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}