package com.gestion_users.ms_gestion_users.controller;


import com.gestion_users.ms_gestion_users.dto.*;
import com.gestion_users.ms_gestion_users.service.BlacklistSyncService;
import com.gestion_users.ms_gestion_users.service.TokenBlacklistService;
import com.gestion_users.ms_gestion_users.service.auth.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService service;
    private final TokenBlacklistService blacklistService;
    private final BlacklistSyncService syncService;
    
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
}