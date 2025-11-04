package com.gestion_labs.ms_gestion_labs.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador personalizado para errores de autenticación (401 Unauthorized).
 * Se ejecuta cuando un usuario no autenticado intenta acceder a un recurso protegido.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("code", "401");
        errorResponse.put("description", "No estás autenticado. Por favor, proporciona un token JWT válido.");
        errorResponse.put("data", new LinkedHashMap<>());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
