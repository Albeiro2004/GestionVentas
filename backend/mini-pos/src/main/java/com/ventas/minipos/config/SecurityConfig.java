package com.ventas.minipos.config;

import com.ventas.minipos.Jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider  authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authRequest ->
                        authRequest
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/Ventas/users/logout").permitAll()

                                // Ventas - Facturas
                                .requestMatchers(HttpMethod.GET, "/Ventas/sales/invoices/**").hasAnyAuthority("USER", "ADMIN","ENGINEER")
                                .requestMatchers("/Ventas/sales/invoices/**").hasAnyAuthority("ADMIN", "ENGINEER")

                                // Ventas generales
                                .requestMatchers("/Ventas/sales/**").hasAnyAuthority("USER", "ADMIN", "ENGINEER")
                                .requestMatchers(HttpMethod.POST, "/Ventas/sales").hasAnyAuthority("USER", "ADMIN", "ENGINEER")

                                // Productos
                                .requestMatchers("/Ventas/products/**").hasAnyAuthority("USER", "ADMIN", "ENGINEER")

                                // Debts (usuarios solo lectura, admin full)
                                .requestMatchers(HttpMethod.GET, "/Ventas/debts/**").hasAnyAuthority("USER", "ADMIN", "ENGINEER")
                                .requestMatchers("/Ventas/debts/**").hasAnyAuthority("ADMIN", "ENGINEER")

                                // Solo ADMIN
                                .requestMatchers(HttpMethod.GET, "/Ventas/users/**").hasAnyAuthority("USER", "ADMIN", "ENGINEER")
                                .requestMatchers("/Ventas/users/**").hasAuthority("ENGINEER")
                                .requestMatchers("/Ventas/purchases/**").hasAnyAuthority("ADMIN",  "ENGINEER")

                                // Cualquier otra solicitud autenticada
                                .anyRequest().authenticated()

                )

                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\": \"Acceso denegado\"}");
                        })
                )
                .sessionManagement(sessionManager ->
                        sessionManager
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }
}
