package com.gestion_users.ms_gestion_users.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maneja errores de autenticación (401 Unauthorized)
 * Se activa cuando:
 * - No se envía token JWT
 * - El token JWT es inválido
 * - El token JWT ha expirado
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        logger.warn("Error de autenticación en {}: {}", request.getRequestURI(), authException.getMessage());
        
        // Estructura estándar de respuesta JSON
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("code", "401");
        responseBody.put("description", "No autenticado: Debe enviar un token JWT válido");
        responseBody.put("data", new LinkedHashMap<>());
        
        // Configurar respuesta HTTP
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Escribir JSON en la respuesta
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
