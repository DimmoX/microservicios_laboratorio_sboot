package com.api_gateway.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar tokens JWT invalidados (blacklist)
 */
@Service
public class TokenBlacklistService {
    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void blacklistToken(String token) {
        if (token != null && !token.isEmpty()) {
            blacklistedTokens.add(token);
            logger.info("Token agregado a blacklist. Total: {}", blacklistedTokens.size());
        }
    }
    public boolean isBlacklisted(String token) {
        return token != null && blacklistedTokens.contains(token);
    }
    public void removeToken(String token) {
        if (token != null) {
            blacklistedTokens.remove(token);
        }
    }
    public void clearAll() {
        blacklistedTokens.clear();
    }
    public int size() {
        return blacklistedTokens.size();
    }
}
