package com.gestion_users.ms_gestion_users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Configuración de seguridad simplificada para ms_gestion_users.
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
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
           .csrf(csrf -> csrf.disable())
           .cors(Customizer.withDefaults())
           .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
           .authorizeHttpRequests(authz -> authz
               // Todos los endpoints abiertos - el Gateway controla el acceso
               .anyRequest().permitAll()
           );
       return http.build();
    }
    
   @Bean
   public CorsConfigurationSource corsConfigurationSource() {
       CorsConfiguration configuration = new CorsConfiguration();
       configuration.addAllowedOrigin("http://localhost:3000");
       configuration.addAllowedOrigin("http://localhost:8080"); // API Gateway
       configuration.addAllowedOrigin("http://duoc.cl");
       configuration.addAllowedMethod("*");
       configuration.addAllowedHeader("*");
       configuration.setAllowCredentials(true);
       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
       source.registerCorsConfiguration("/**", configuration);
       return source;
   }

   @Bean
   public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
       UserDetails user = User.builder()
           .username("user")
           .password(passwordEncoder.encode("password"))
           .roles("USER")
           .build();
       return new InMemoryUserDetailsManager(user);
   }

   @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
