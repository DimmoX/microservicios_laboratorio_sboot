package com.api_gateway.ms_api_gateway.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests unitarios para TokenBlacklistService")
class TokenBlacklistServiceTest {

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService();
    }

    @Test
    @DisplayName("Debe agregar un token a la blacklist")
    void testBlacklistToken() {
        // Arrange
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test";

        // Act
        tokenBlacklistService.blacklistToken(token);

        // Assert
        assertTrue(tokenBlacklistService.isBlacklisted(token));
        assertEquals(1, tokenBlacklistService.size());
    }

    @Test
    @DisplayName("Debe verificar que un token está en la blacklist")
    void testIsBlacklisted() {
        // Arrange
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.blacklisted";
        String tokenNoBlacklisted = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.valid";

        // Act
        tokenBlacklistService.blacklistToken(token);

        // Assert
        assertTrue(tokenBlacklistService.isBlacklisted(token));
        assertFalse(tokenBlacklistService.isBlacklisted(tokenNoBlacklisted));
    }

    @Test
    @DisplayName("Debe remover un token de la blacklist")
    void testRemoveToken() {
        // Arrange
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.toRemove";
        tokenBlacklistService.blacklistToken(token);
        assertTrue(tokenBlacklistService.isBlacklisted(token));

        // Act
        tokenBlacklistService.removeToken(token);

        // Assert
        assertFalse(tokenBlacklistService.isBlacklisted(token));
        assertEquals(0, tokenBlacklistService.size());
    }

    @Test
    @DisplayName("Debe limpiar toda la blacklist")
    void testClearAll() {
        // Arrange
        tokenBlacklistService.blacklistToken("token1");
        tokenBlacklistService.blacklistToken("token2");
        tokenBlacklistService.blacklistToken("token3");
        assertEquals(3, tokenBlacklistService.size());

        // Act
        tokenBlacklistService.clearAll();

        // Assert
        assertEquals(0, tokenBlacklistService.size());
        assertFalse(tokenBlacklistService.isBlacklisted("token1"));
    }

    @Test
    @DisplayName("Debe retornar el tamaño correcto de la blacklist")
    void testSize() {
        // Arrange & Act
        assertEquals(0, tokenBlacklistService.size());
        
        tokenBlacklistService.blacklistToken("token1");
        assertEquals(1, tokenBlacklistService.size());
        
        tokenBlacklistService.blacklistToken("token2");
        assertEquals(2, tokenBlacklistService.size());
        
        tokenBlacklistService.removeToken("token1");
        assertEquals(1, tokenBlacklistService.size());
    }

    @Test
    @DisplayName("Debe manejar tokens nulos o vacíos sin error")
    void testNullAndEmptyTokens() {
        // Act & Assert - No debe lanzar excepciones
        assertDoesNotThrow(() -> tokenBlacklistService.blacklistToken(null));
        assertDoesNotThrow(() -> tokenBlacklistService.blacklistToken(""));
        assertDoesNotThrow(() -> tokenBlacklistService.removeToken(null));
        
        // Verificar que no se agregaron tokens inválidos
        assertFalse(tokenBlacklistService.isBlacklisted(null));
        assertEquals(0, tokenBlacklistService.size());
    }

    @Test
    @DisplayName("Debe ser thread-safe al agregar múltiples tokens")
    void testThreadSafety() throws InterruptedException {
        // Arrange
        int numThreads = 10;
        int tokensPerThread = 100;
        Thread[] threads = new Thread[numThreads];

        // Act
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < tokensPerThread; j++) {
                    tokenBlacklistService.blacklistToken("token_" + threadId + "_" + j);
                }
            });
            threads[i].start();
        }

        // Esperar a que todos los threads terminen
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        assertEquals(numThreads * tokensPerThread, tokenBlacklistService.size());
    }
}
