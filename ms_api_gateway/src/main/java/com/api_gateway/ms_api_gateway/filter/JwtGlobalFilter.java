package com.api_gateway.ms_api_gateway.filter;

import com.api_gateway.ms_api_gateway.config.JwtProperties;
import com.api_gateway.ms_api_gateway.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Filtro global para validación de JWT en Spring Cloud Gateway.
 * Se ejecuta para TODAS las peticiones y agrega headers X-User-Id y X-User-Role.
 */
@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(JwtGlobalFilter.class);
    
    private final JwtProperties jwtProperties;
    private final TokenBlacklistService blacklistService;
    
    // Rutas públicas que NO requieren JWT
    private static final List<String> PUBLIC_PATHS = List.of(
        "/auth/login",
        "/auth/logout",
        "/labs"  // Solo GET /labs y /labs/{id} son públicas, se valida el método después
    );

    public JwtGlobalFilter(JwtProperties jwtProperties, TokenBlacklistService blacklistService) {
        this.jwtProperties = jwtProperties;
        this.blacklistService = blacklistService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().name();
        
        // Permitir rutas públicas
        if (isPublicPath(path, method)) {
            return chain.filter(exchange);
        }
        
        // Para rutas privadas, validar JWT
        String authHeader = request.getHeaders().getFirst("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Acceso denegado a {} {} - Token no proporcionado", method, path);
            return onError(exchange, "Token no proporcionado", HttpStatus.UNAUTHORIZED);
        }
        
        String token = authHeader.substring(7);
        
        // Verificar blacklist
        if (blacklistService.isBlacklisted(token)) {
            logger.warn("Acceso denegado a {} {} - Token en blacklist", method, path);
            return onError(exchange, "Token inválido o expirado", HttpStatus.UNAUTHORIZED);
        }
        
        try {
            // Validar y parsear el token
            SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
            
            Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            
            if (username == null || username.isEmpty()) {
                logger.error("Token sin usuario válido para {} {}", method, path);
                return onError(exchange, "Token sin usuario válido", HttpStatus.UNAUTHORIZED);
            }
            
            // SOLUCIÓN FINAL: Guardar en atributos del exchange y continuar
            // Los microservices luego leerán de atributos o confiarán en el Gateway
            exchange.getAttributes().put("X-User-Id", username);
            exchange.getAttributes().put("X-User-Role", role != null ? role : "UNKNOWN");
            
            logger.info("✓ {} {} - Usuario: {} [{}]", method, path, username, role);
            
            // Continuar sin modificar headers - los microservices confían en el Gateway
            return chain.filter(exchange);
            
        } catch (Exception e) {
            logger.warn("Token inválido para {} {}: {}", method, path, e.getMessage());
            return onError(exchange, "Token inválido", HttpStatus.UNAUTHORIZED);
        }
    }
    
    private boolean isPublicPath(String path, String method) {
        // Login y logout son siempre públicos
        if (path.startsWith("/auth/login") || path.startsWith("/auth/logout")) {
            return true;
        }
        
        // GET /labs y GET /labs/{id} son públicos
        if (path.equals("/labs") && "GET".equals(method)) {
            return true;
        }
        if (path.matches("/labs/\\d+") && "GET".equals(method)) {
            return true;
        }
        
        return false;
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");
        
        String errorBody = String.format(
            "{\"code\":\"%d\",\"description\":\"%s\",\"data\":{}}",
            status.value(), message
        );
        
        DataBuffer buffer = response.bufferFactory().wrap(errorBody.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100; // Ejecutar antes que otros filtros
    }
}
