package com.gestion_labs.ms_gestion_labs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio para gestionar tokens JWT invalidados (blacklist).
 * 
 * Funcionalidad:
 * - Almacena tokens que han sido invalidados mediante logout
 * - Permite verificar si un token está en la blacklist
 * - Thread-safe usando ConcurrentHashMap
 * 
 * NOTA: Esta implementación usa memoria local (in-memory).
 * Para producción con múltiples instancias, se recomienda usar Redis
 * para compartir la blacklist entre todos los servidores.
 */
@Service
public class TokenBlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);
    
    // Set thread-safe para almacenar tokens invalidados
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    /**
     * Agrega un token a la blacklist.
     * El token no podrá ser usado hasta que expire naturalmente.
     * 
     * @param token JWT token a invalidar
     */
    public void blacklistToken(String token) {
        if (token != null && !token.isEmpty()) {
            blacklistedTokens.add(token);
            logger.info("Token agregado a blacklist. Total tokens en blacklist: {}", blacklistedTokens.size());
        }
    }

    /**
     * Verifica si un token está en la blacklist.
     * 
     * @param token JWT token a verificar
     * @return true si el token está invalidado, false en caso contrario
     */
    public boolean isBlacklisted(String token) {
        return token != null && blacklistedTokens.contains(token);
    }

    /**
     * Elimina un token de la blacklist.
     * Útil cuando un token expira naturalmente y ya no es necesario mantenerlo en blacklist.
     * 
     * @param token JWT token a eliminar de la blacklist
     */
    public void removeToken(String token) {
        if (token != null) {
            blacklistedTokens.remove(token);
            logger.info("Token removido de blacklist. Total tokens en blacklist: {}", blacklistedTokens.size());
        }
    }

    /**
     * Limpia toda la blacklist.
     * Útil para mantenimiento o testing.
     */
    public void clearAll() {
        int size = blacklistedTokens.size();
        blacklistedTokens.clear();
        logger.info("Blacklist limpiada. {} tokens removidos", size);
    }

    /**
     * Obtiene el número de tokens en la blacklist.
     * 
     * @return cantidad de tokens invalidados
     */
    public int size() {
        return blacklistedTokens.size();
    }
}
