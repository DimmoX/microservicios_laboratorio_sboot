package com.gestion_users.ms_gestion_users.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maneja errores de autorización (403 Forbidden)
 * Se activa cuando:
 * - El usuario está autenticado (tiene JWT válido)
 * - Pero no tiene el rol necesario para acceder al endpoint
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request,
                      HttpServletResponse response,
                      AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        logger.warn("Acceso denegado para usuario en {}: {}", 
                   request.getRequestURI(), 
                   accessDeniedException.getMessage());
        
        // Estructura estándar de respuesta JSON
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("code", "403");
        responseBody.put("description", "No autorizado: No tiene permisos para realizar esta acción");
        responseBody.put("data", new LinkedHashMap<>());
        
        // Configurar respuesta HTTP
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Escribir JSON en la respuesta
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
