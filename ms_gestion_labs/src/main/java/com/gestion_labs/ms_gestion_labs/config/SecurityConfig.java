package com.gestion_labs.ms_gestion_labs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad simplificada para ms_gestion_labs.
 * 
 * IMPORTANTE: Este microservicio está detrás del API Gateway.
 * - El Gateway valida JWT y agrega headers X-User-Id y X-User-Role
 * - Este servicio NO valida JWT, solo confía en los headers del Gateway
 * - Todos los endpoints están abiertos (el Gateway controla acceso)
 * 
 * SEGURIDAD: En producción, asegurar que SOLO el Gateway pueda acceder
 * a este microservicio (usar red interna, firewall, etc.)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Todos los endpoints abiertos - el Gateway controla el acceso
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}
