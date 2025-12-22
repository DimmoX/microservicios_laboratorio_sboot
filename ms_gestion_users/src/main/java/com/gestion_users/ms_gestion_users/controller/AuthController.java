package com.gestion_users.ms_gestion_users.controller;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion_users.ms_gestion_users.dto.AuthRequest;
import com.gestion_users.ms_gestion_users.dto.AuthResponse;
import com.gestion_users.ms_gestion_users.dto.ForgotPasswordRequest;
import com.gestion_users.ms_gestion_users.dto.ForgotPasswordResponse;
import com.gestion_users.ms_gestion_users.dto.HashRequest;
import com.gestion_users.ms_gestion_users.dto.HashResponse;
import com.gestion_users.ms_gestion_users.dto.ResetPasswordRequest;
import com.gestion_users.ms_gestion_users.dto.ResetPasswordResponse;
import com.gestion_users.ms_gestion_users.service.BlacklistSyncService;
import com.gestion_users.ms_gestion_users.service.TokenBlacklistService;
import com.gestion_users.ms_gestion_users.service.auth.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService service;
    private final TokenBlacklistService blacklistService;
    private final BlacklistSyncService syncService;
    
    // Rate limiting para forgot-password: máximo 3 intentos por email en 15 minutos
    private final Map<String, RateLimitInfo> forgotPasswordAttempts = new ConcurrentHashMap<>();
    private static final int MAX_FORGOT_PASSWORD_ATTEMPTS = 3;
    private static final long RATE_LIMIT_WINDOW_MS = 15 * 60 * 1000L; // 15 minutos
    
    public AuthController(AuthService service, TokenBlacklistService blacklistService, BlacklistSyncService syncService) { 
        this.service = service;
        this.blacklistService = blacklistService;
        this.syncService = syncService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequest request) {
        logger.info("POST: /auth/login -> Intento de login para usuario: {}", request.getUsername());
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            AuthResponse authResponse = service.login(request);
            logger.info("Login exitoso para usuario: {}", request.getUsername());
            
            response.put("code", "000");
            response.put("description", "Login exitoso");
            response.put("data", authResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Login fallido para {}: {}", request.getUsername(), e.getMessage());
            
            response.put("code", "001");
            response.put("description", "Error en login: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * Endpoint para generar el hash BCrypt de una contraseña.
     * Útil para desarrollo: te permite generar hashes para actualizar la BD.
     * 
     * POST /auth/generate-hash
     * Body: { "password": "miContraseña123" }
     * Respuesta: { "code": "000", "description": "...", "data": { "password": "...", "hash": "$2a$10$..." } }
     */
    @PostMapping("/generate-hash")
    public ResponseEntity<Map<String, Object>> generateHash(@RequestBody HashRequest request) {
        logger.info("POST: /auth/generate-hash -> Generar hash para contraseña");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            HashResponse hashResponse = service.generatePasswordHash(request);
            logger.info("Hash generado exitosamente");
            
            response.put("code", "000");
            response.put("description", "Hash generado exitosamente");
            response.put("data", hashResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al generar hash: {}", e.getMessage());
            
            response.put("code", "001");
            response.put("description", "Error al generar hash");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Endpoint para solicitar recuperación de contraseña.
     * Genera un token temporal válido por 15 minutos.
     * Implementa rate limiting: máximo 3 intentos por email en 15 minutos.
     * 
     * POST /auth/forgot-password
     * Body: { "email": "usuario@ejemplo.com" }
     * Respuesta: { "code": "000", "description": "...", "data": { "email": "...", "message": "...", "temporaryToken": "..." } }
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        logger.info("POST: /auth/forgot-password -> Solicitud de recuperación para: {}", request.getEmail());
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            // Validar rate limiting
            String email = request.getEmail();
            if (email != null) {
                RateLimitInfo limitInfo = forgotPasswordAttempts.computeIfAbsent(email, k -> new RateLimitInfo());
                
                // Limpiar intentos antiguos
                long now = System.currentTimeMillis();
                if (now - limitInfo.firstAttemptTime > RATE_LIMIT_WINDOW_MS) {
                    limitInfo.reset();
                }
                
                // Verificar si excede el límite
                if (limitInfo.attempts >= MAX_FORGOT_PASSWORD_ATTEMPTS) {
                    long remainingTime = (limitInfo.firstAttemptTime + RATE_LIMIT_WINDOW_MS - now) / 1000 / 60;
                    logger.warn("Rate limit excedido para email: {}. Intentos: {}", email, limitInfo.attempts);
                    
                    response.put("code", "429");
                    response.put("description", "Demasiados intentos. Por favor espera " + remainingTime + " minutos.");
                    response.put("data", new LinkedHashMap<>());
                    
                    return ResponseEntity.status(429).body(response);
                }
                
                limitInfo.incrementAttempts();
            }
            
            ForgotPasswordResponse forgotResponse = service.forgotPassword(request);
            logger.info("Token de recuperación generado exitosamente para: {}", request.getEmail());
            
            response.put("code", "000");
            response.put("description", "Si el email existe, recibirás instrucciones para recuperar tu contraseña");
            response.put("data", forgotResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error en forgot-password para {}: {}", request.getEmail(), e.getMessage());
            
            // Por seguridad, no revelar si el email existe o no
            response.put("code", "000");
            response.put("description", "Si el email existe, recibirás instrucciones para recuperar tu contraseña");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Endpoint para resetear la contraseña de un usuario.
     * Actualiza la contraseña en la base de datos con el nuevo hash BCrypt.
     * 
     * POST /auth/reset-password
     * Body: { "username": "felipe.munoz@laboratorioandino.cl", "newPassword": "nueva123" }
     * Respuesta: { "code": "000", "description": "...", "data": { "username": "...", "message": "...", "newHash": "$2a$10$..." } }
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody ResetPasswordRequest request) {
        logger.info("POST: /auth/reset-password -> Resetear contraseña para usuario: {}", request.getUsername());
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            ResetPasswordResponse resetResponse = service.resetPassword(request);
            logger.info("Contraseña reseteada exitosamente para usuario: {}", request.getUsername());
            
            response.put("code", "000");
            response.put("description", "Contraseña reseteada exitosamente");
            response.put("data", resetResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al resetear contraseña para {}: {}", request.getUsername(), e.getMessage());
            
            response.put("code", "001");
            response.put("description", "Error al resetear contraseña: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Endpoint para cerrar sesión (logout).
     * Invalida el token actual agregándolo a la blacklist.
     * El token no podrá ser usado nuevamente hasta que expire naturalmente.
     * Además, sincroniza la invalidación con ms_gestion_labs.
     * 
     * POST /auth/logout
     * Header: Authorization: Bearer {token}
     * Respuesta: { "code": "000", "description": "Logout exitoso", "data": { "username": "...", "message": "..." } }
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            // Obtener información del usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : "desconocido";
            
            logger.info("POST: /auth/logout -> Logout para usuario: {}", username);
            
            // Extraer el token del header Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                // Agregar el token a la blacklist local
                blacklistService.blacklistToken(token);
                logger.info("Token agregado a blacklist local para usuario: {}", username);
                
                // Sincronizar con ms_gestion_labs
                syncService.syncTokenToLabs(token);
            }
            
            // Limpiar el contexto de seguridad
            SecurityContextHolder.clearContext();
            
            Map<String, Object> logoutData = new LinkedHashMap<>();
            logoutData.put("username", username);
            logoutData.put("message", "Sesión cerrada correctamente. El token ha sido invalidado en todos los servicios.");
            
            response.put("code", "000");
            response.put("description", "Logout exitoso");
            response.put("data", logoutData);
            
            logger.info("Logout exitoso para usuario: {}", username);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error en logout: {}", e.getMessage());
            
            response.put("code", "001");
            response.put("description", "Error en logout");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Endpoint para cambiar contraseña (usado cuando la contraseña es temporal).
     * 
     * POST /auth/change-password
     * Body: { "oldPassword": "abc123", "newPassword": "miNuevaContraseña" }
     * Respuesta: { "code": "000", "description": "...", "data": { "username": "...", "message": "...", "success": true } }
     */
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody com.gestion_users.ms_gestion_users.dto.ChangePasswordRequest request) {
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            // Obtener el usuario autenticado del contexto de seguridad
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth != null ? auth.getName() : null;
            
            if (username == null) {
                throw new RuntimeException("Usuario no autenticado");
            }
            
            logger.info("POST: /auth/change-password -> Cambio de contraseña para: {}", username);
            
            com.gestion_users.ms_gestion_users.dto.ChangePasswordResponse changeResponse = service.changePassword(username, request);
            logger.info("Contraseña actualizada exitosamente para: {}", username);
            
            response.put("code", "000");
            response.put("description", "Contraseña actualizada exitosamente");
            response.put("data", changeResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al cambiar contraseña: {}", e.getMessage());
            
            response.put("code", "001");
            response.put("description", "Error: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(400).body(response);
        }
    }
}

/**
 * Clase auxiliar para control de rate limiting
 */
class RateLimitInfo {
    int attempts = 0;
    long firstAttemptTime = System.currentTimeMillis();
    
    void incrementAttempts() {
        attempts++;
    }
    
    void reset() {
        attempts = 0;
        firstAttemptTime = System.currentTimeMillis();
    }
}