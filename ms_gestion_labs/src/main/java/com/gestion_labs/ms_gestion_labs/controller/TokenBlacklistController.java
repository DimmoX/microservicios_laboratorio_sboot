package com.gestion_labs.ms_gestion_labs.controller;

import com.gestion_labs.ms_gestion_labs.service.TokenBlacklistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controlador interno para sincronización de blacklist entre microservicios.
 * 
 * IMPORTANTE: Este controlador NO debe ser expuesto públicamente.
 * Solo debe ser accesible desde otros microservicios de confianza.
 * En producción, usar un API Gateway o red interna para proteger estos endpoints.
 */
@RestController
@RequestMapping("/internal/blacklist")
public class TokenBlacklistController {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistController.class);
    private static final String DESCRIPTION_KEY = "description";
    private final TokenBlacklistService blacklistService;

    public TokenBlacklistController(TokenBlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    /**
     * Endpoint interno para agregar un token a la blacklist.
     * Llamado por ms_gestion_users cuando un usuario hace logout.
     * 
     * POST /internal/blacklist/add
     * Body: { "token": "jwt.token.here" }
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToBlacklist(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            String token = request.get("token");
            
            if (token == null || token.isEmpty()) {
                response.put("code", "001");
                response.put(DESCRIPTION_KEY, "Token es requerido");
                response.put("data", new LinkedHashMap<>());
                return ResponseEntity.badRequest().body(response);
            }
            
            blacklistService.blacklistToken(token);
            logger.info("POST: /internal/blacklist/add -> Token agregado a blacklist (sincronización)");
            
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("message", "Token agregado exitosamente a blacklist");
            data.put("blacklistSize", blacklistService.size());
            
            response.put("code", "000");
            response.put(DESCRIPTION_KEY, "Token blacklisted exitosamente");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error agregando token a blacklist: {}", e.getMessage());
            
            response.put("code", "002");
            response.put(DESCRIPTION_KEY, "Error al agregar token a blacklist");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Endpoint para verificar el tamaño de la blacklist.
     * Útil para monitoreo y debugging.
     */
    @GetMapping("/size")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> getBlacklistSize() {
        Map<String, Object> response = new LinkedHashMap<>();
        
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("blacklistSize", blacklistService.size());
        
        response.put("code", "000");
        response.put(DESCRIPTION_KEY, "Tamaño de blacklist obtenido");
        response.put("data", data);
        
        return ResponseEntity.ok(response);
    }
}
