package com.gestion_resultados.ms_gestion_resultados.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtProperties jwtProperties;

    public JwtAuthenticationFilter(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        
        // DEBUG: Log para verificar si llega el header
        logger.info("🔍 {} {} - Authorization header: {}", 
            request.getMethod(), 
            request.getRequestURI(),
            authHeader != null ? "presente (" + authHeader.substring(0, Math.min(30, authHeader.length())) + "...)" : "NULL");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));

                Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

                String username = claims.getSubject();
                String role = claims.get("role", String.class);
                Integer empleadoId = claims.get("empleadoId", Integer.class);
                Integer pacienteId = claims.get("pacienteId", Integer.class);
                
                logger.info("✅ Token válido - Usuario: {}, Rol: {}, EmpleadoId: {}", username, role, empleadoId);

                // Registrar la autoridad tal cual y con prefijo ROLE_ para tolerar ambos esquemas
                List<SimpleGrantedAuthority> authorities = Arrays.asList(
                    new SimpleGrantedAuthority(role),
                    new SimpleGrantedAuthority("ROLE_" + role)
                );

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
                );
                
                // Guardar empleadoId y pacienteId en los details del Authentication
                Map<String, Object> details = new HashMap<>();
                details.put("empleadoId", empleadoId);
                details.put("pacienteId", pacienteId);
                details.put("role", role);
                auth.setDetails(details);

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                logger.warn("❌ Token JWT inválido para {}: {} - {}", request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
