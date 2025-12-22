package com.gestion_resultados.ms_gestion_resultados.util;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Utilidad para decodificar JWT y extraer claims.
 * Usado para obtener información del usuario desde el token sin depender de headers personalizados.
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    public Claims decodeToken(String token) {
        try {
            if (token == null || token.isBlank()) {
                return null;
            }

            // Remover "Bearer " si está presente
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (Exception e) {
            logger.error("Error al decodificar JWT: {}", e.getMessage());
            return null;
        }
    }

    public String getRole(Claims claims) {
        return claims != null ? claims.get("role", String.class) : null;
    }

    public Long getPacienteId(Claims claims) {
        return claims != null ? claims.get("pacienteId", Long.class) : null;
    }

    public Long getEmpleadoId(Claims claims) {
        return claims != null ? claims.get("empleadoId", Long.class) : null;
    }

    public Long getUserId(Claims claims) {
        return claims != null ? claims.get("userId", Long.class) : null;
    }

    public String getUsername(Claims claims) {
        return claims != null ? claims.getSubject() : null;
    }
}
