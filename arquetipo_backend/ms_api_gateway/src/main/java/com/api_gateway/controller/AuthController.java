package com.api_gateway.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de autenticación en el API Gateway
 * Ejemplo: delega login y gestiona logout
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        logger.info("POST /auth/login - {}", request.get("email"));
        // Aquí se delegaría la autenticación a ms_gestion_users
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", "000");
        response.put("description", "Login exitoso (ejemplo)");
        response.put("data", new LinkedHashMap<>());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info("POST /auth/logout");
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", "000");
        response.put("description", "Logout exitoso (ejemplo)");
        response.put("data", new LinkedHashMap<>());
        return ResponseEntity.ok(response);
    }
}
