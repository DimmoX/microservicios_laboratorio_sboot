package com.api_gateway.ms_api_gateway.filter;

import com.api_gateway.ms_api_gateway.config.JwtProperties;
import com.api_gateway.ms_api_gateway.service.TokenBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Filtro de autenticación JWT para Spring Cloud Gateway (WebFlux/Reactive).
 * 
 * Responsabilidades:
 * 1. Extraer token JWT del header Authorization
 * 2. Verificar si el token está en la blacklist
 * 3. Validar el token JWT
 * 4. Extraer username y role del token
 * 5. Agregar headers X-User-Id y X-User-Role a la petición
 * 6. Rechazar peticiones con tokens inválidos o blacklisted (401)
 */
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private final JwtProperties jwtProperties;
    private final TokenBlacklistService blacklistService;

    public JwtAuthenticationFilter(JwtProperties jwtProperties, TokenBlacklistService blacklistService) {
        super(Config.class);
        this.jwtProperties = jwtProperties;
        this.blacklistService = blacklistService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Extraer el header Authorization
            String authHeader = request.getHeaders().getFirst("Authorization");
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.debug("Token JWT no encontrado para {}", request.getURI().getPath());
                return onError(exchange, "Token no proporcionado", HttpStatus.UNAUTHORIZED);
            }
            
            String token = authHeader.substring(7);
            
            // Verificar si el token está en la blacklist
            if (blacklistService.isBlacklisted(token)) {
                logger.debug("Token en blacklist rechazado para {}", request.getURI().getPath());
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
                
                logger.debug("Token válido para usuario: {} con rol: {}", username, role);
                
                // Validar que los valores no sean nulos
                if (username == null || username.isEmpty()) {
                    logger.error("Username es nulo o vacío en el token");
                    return onError(exchange, "Token sin usuario válido", HttpStatus.UNAUTHORIZED);
                }
                
                // Agregar headers con información del usuario
                // SOLUCIÓN: No usar exchange.getRequest().mutate() porque devuelve headers inmutables
                // En su lugar, crear un nuevo request builder desde el exchange directamente
                ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(builder -> builder
                        .header("X-User-Id", username)
                        .header("X-User-Role", role != null ? role : ""))
                    .build();
                
                logger.debug("Headers agregados correctamente. Continuando con la petición...");
                
                return chain.filter(modifiedExchange);
                
            } catch (Exception e) {
                logger.error("Token JWT inválido para {}: {} - {}", request.getURI().getPath(), e.getClass().getName(), e.getMessage(), e);
                return onError(exchange, "Token inválido", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    /**
     * Maneja errores retornando una respuesta JSON con código 401.
     */
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

    /**
     * Clase de configuración para el filtro (requerida por AbstractGatewayFilterFactory).
     */
    public static class Config {
        // Configuración vacía - el filtro no requiere parámetros adicionales
    }
}
