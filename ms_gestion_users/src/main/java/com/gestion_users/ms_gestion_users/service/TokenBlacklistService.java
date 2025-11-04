package com.gestion_users.ms_gestion_users.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio para gestionar tokens invalidados (blacklist).
 * 
 * NOTA: Esta implementación usa memoria local.
 * Para producción con múltiples instancias, usar Redis o una base de datos.
 */
@Service
public class TokenBlacklistService {
    
    // Thread-safe set para almacenar tokens invalidados
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    
    /**
     * Agrega un token a la blacklist
     */
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }
    
    /**
     * Verifica si un token está en la blacklist
     */
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
    
    /**
     * Elimina un token de la blacklist (útil para limpiar tokens expirados)
     */
    public void removeToken(String token) {
        blacklistedTokens.remove(token);
    }
    
    /**
     * Limpia toda la blacklist (útil para mantenimiento)
     */
    public void clearAll() {
        blacklistedTokens.clear();
    }
    
    /**
     * Obtiene el tamaño de la blacklist
     */
    public int size() {
        return blacklistedTokens.size();
    }
}
