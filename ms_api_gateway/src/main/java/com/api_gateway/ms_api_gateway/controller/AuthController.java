package com.api_gateway.ms_api_gateway.controller;

import com.api_gateway.ms_api_gateway.dto.AuthRequest;
import com.api_gateway.ms_api_gateway.service.TokenBlacklistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controlador de autenticación en el API Gateway.
 * 
 * Responsabilidades:
 * - POST /auth/login: Delega autenticación a ms_gestion_users
 * - POST /auth/forgot-password: Delega recuperación de contraseña a ms_gestion_users
 * - POST /auth/logout: Invalida token agregándolo a blacklist
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final TokenBlacklistService blacklistService;
    private final WebClient.Builder webClientBuilder;
    
    @Value("${app.services.users}")
    private String usersServiceUrl;

    public AuthController(TokenBlacklistService blacklistService, WebClient.Builder webClientBuilder) {
        this.blacklistService = blacklistService;
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Endpoint de login - delega autenticación a ms_gestion_users.
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, Object>>> login(@RequestBody AuthRequest request) {
        logger.info("POST /auth/login - {}", request.getEmail());
        
        return webClientBuilder.build()
            .post()
            .uri(usersServiceUrl + "/auth/login")
            .bodyValue(request)
            .retrieve()
            .onStatus(
                status -> status.isError(),
                response -> response.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new RuntimeException("Credenciales inválidas")))
            )
            .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
            .map(ResponseEntity::ok)
            .onErrorResume(e -> {
                logger.warn("Login fallido para {}: {}", request.getEmail(), e.getMessage());
                Map<String, Object> errorResponse = new LinkedHashMap<>();
                errorResponse.put("code", "001");
                errorResponse.put("description", "Credenciales inválidas");
                errorResponse.put("data", new LinkedHashMap<>());
                return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
            });
    }

    /**
     * Endpoint de recuperación de contraseña - delega a ms_gestion_users.
     * 
     * POST /auth/forgot-password
     * Body: { "email": "usuario@ejemplo.com" }
     * Respuesta: { "code": "000", "description": "...", "data": { "email": "...", "message": "...", "temporaryToken": "..." } }
     */
    @PostMapping("/forgot-password")
    public Mono<ResponseEntity<Map<String, Object>>> forgotPassword(@RequestBody Map<String, String> request) {
        logger.info("POST /auth/forgot-password - {}", request.get("email"));
        
        return webClientBuilder.build()
            .post()
            .uri(usersServiceUrl + "/auth/forgot-password")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
            .map(ResponseEntity::ok)
            .onErrorResume(e -> {
                logger.warn("Forgot password fallido para {}: {}", request.get("email"), e.getMessage());
                Map<String, Object> errorResponse = new LinkedHashMap<>();
                errorResponse.put("code", "000");
                errorResponse.put("description", "Si el email existe, recibirás instrucciones para recuperar tu contraseña");
                errorResponse.put("data", new LinkedHashMap<>());
                return Mono.just(ResponseEntity.ok(errorResponse));
            });
    }

    /**
     * Endpoint de cambio de contraseña - delega a ms_gestion_users.
     * 
     * POST /change-password
     * Header: Authorization: Bearer {token}
     * Body: { "oldPassword": "abc123", "newPassword": "miNuevaContraseña" }
     * Respuesta: { "code": "000", "description": "...", "data": { "username": "...", "message": "...", "success": true } }
     */
    @PostMapping("/change-password")
    public Mono<ResponseEntity<Map<String, Object>>> changePassword(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        logger.info("POST /change-password");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("POST /change-password - Token no proporcionado");
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("code", "001");
            errorResponse.put("description", "Token de autenticación requerido");
            errorResponse.put("data", new LinkedHashMap<>());
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
        }
        
        return webClientBuilder.build()
            .post()
            .uri(usersServiceUrl + "/auth/change-password")
            .header("Authorization", authHeader)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {})
            .map(ResponseEntity::ok)
            .onErrorResume(e -> {
                logger.warn("Change password fallido: {}", e.getMessage());
                Map<String, Object> errorResponse = new LinkedHashMap<>();
                errorResponse.put("code", "001");
                errorResponse.put("description", e.getMessage().contains("401") ? "Contraseña actual incorrecta" : "Error al cambiar contraseña");
                errorResponse.put("data", new LinkedHashMap<>());
                return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
            });
    }

    /**
     * Endpoint de logout - invalida el token agregándolo a la blacklist.
     * 
     * POST /auth/logout
     * Header: Authorization: Bearer {token}
     * Respuesta: { "code": "000", "description": "Logout exitoso", "data": { "message": "..." } }
     */
    @PostMapping("/logout")
    public Mono<ResponseEntity<Map<String, Object>>> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("POST /auth/logout - Token no proporcionado");
                response.put("code", "001");
                response.put("description", "Token no proporcionado");
                response.put("data", new LinkedHashMap<>());
                return Mono.just(ResponseEntity.badRequest().body(response));
            }
            
            String token = authHeader.substring(7);
            
            // Agregar el token a la blacklist
            blacklistService.blacklistToken(token);
            logger.info("✓ POST /auth/logout - Token invalidado [blacklist: {}]", blacklistService.size());
            
            Map<String, Object> logoutData = new LinkedHashMap<>();
            logoutData.put("message", "Sesión cerrada correctamente");
            
            response.put("code", "000");
            response.put("description", "Logout exitoso");
            response.put("data", logoutData);
            
            return Mono.just(ResponseEntity.ok(response));
            
        } catch (Exception e) {
            logger.warn("POST /auth/logout - Error: {}", e.getMessage());
            
            response.put("code", "002");
            response.put("description", "Error en logout");
            response.put("data", new LinkedHashMap<>());
            
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
        }
    }
}
