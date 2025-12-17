package com.gestion_users.ms_gestion_users.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Configuración de seguridad para ms_gestion_users.
 * 
 * JwtAuthenticationFilter (ya existente) extrae el rol del JWT y 
 * establece la autenticación con ROLE_ prefix para @PreAuthorize.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

   @Autowired
   private JwtAuthenticationFilter jwtAuthenticationFilter;

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
           .csrf(csrf -> csrf.disable())
           // CORS deshabilitado - El API Gateway maneja CORS globalmente
           .cors(cors -> cors.disable())
           .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
           // JwtAuthenticationFilter ya extrae rol del JWT
           .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
           .authorizeHttpRequests(authz -> authz
               // Endpoints de autenticación accesibles sin rol
               .requestMatchers("/auth/**", "/actuator/**").permitAll()
               // Otros endpoints requieren autenticación (rol del Gateway)
               .anyRequest().authenticated()
           );
       return http.build();
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
