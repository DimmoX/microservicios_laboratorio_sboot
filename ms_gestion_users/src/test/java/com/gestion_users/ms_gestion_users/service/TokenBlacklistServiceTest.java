package com.gestion_users.ms_gestion_users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para TokenBlacklistService
 * Cobertura: Gestión de tokens invalidados (blacklist)
 */
class TokenBlacklistServiceTest {

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService();
    }

    @Test
    void testBlacklistToken_AddsTokenToBlacklist() {
        // Arrange
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test";

        // Act
        tokenBlacklistService.blacklistToken(token);

        // Assert
        assertTrue(tokenBlacklistService.isBlacklisted(token));
        assertEquals(1, tokenBlacklistService.size());
    }

    @Test
    void testIsBlacklisted_ReturnsTrueForBlacklistedToken() {
        // Arrange
        String token = "token123";
        tokenBlacklistService.blacklistToken(token);

        // Act
        boolean result = tokenBlacklistService.isBlacklisted(token);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsBlacklisted_ReturnsFalseForNonBlacklistedToken() {
        // Arrange
        String token = "token123";

        // Act
        boolean result = tokenBlacklistService.isBlacklisted(token);

        // Assert
        assertFalse(result);
    }

    @Test
    void testRemoveToken_RemovesTokenFromBlacklist() {
        // Arrange
        String token = "token123";
        tokenBlacklistService.blacklistToken(token);

        // Act
        tokenBlacklistService.removeToken(token);

        // Assert
        assertFalse(tokenBlacklistService.isBlacklisted(token));
        assertEquals(0, tokenBlacklistService.size());
    }

    @Test
    void testClearAll_RemovesAllTokens() {
        // Arrange
        tokenBlacklistService.blacklistToken("token1");
        tokenBlacklistService.blacklistToken("token2");
        tokenBlacklistService.blacklistToken("token3");

        // Act
        tokenBlacklistService.clearAll();

        // Assert
        assertEquals(0, tokenBlacklistService.size());
        assertFalse(tokenBlacklistService.isBlacklisted("token1"));
        assertFalse(tokenBlacklistService.isBlacklisted("token2"));
        assertFalse(tokenBlacklistService.isBlacklisted("token3"));
    }

    @Test
    void testSize_ReturnsCorrectCount() {
        // Arrange
        assertEquals(0, tokenBlacklistService.size());

        // Act & Assert
        tokenBlacklistService.blacklistToken("token1");
        assertEquals(1, tokenBlacklistService.size());

        tokenBlacklistService.blacklistToken("token2");
        assertEquals(2, tokenBlacklistService.size());

        tokenBlacklistService.blacklistToken("token3");
        assertEquals(3, tokenBlacklistService.size());
    }

    @Test
    void testBlacklistToken_DuplicateTokenDoesNotIncreaseSize() {
        // Arrange
        String token = "token123";

        // Act
        tokenBlacklistService.blacklistToken(token);
        tokenBlacklistService.blacklistToken(token);
        tokenBlacklistService.blacklistToken(token);

        // Assert - Set no permite duplicados
        assertEquals(1, tokenBlacklistService.size());
        assertTrue(tokenBlacklistService.isBlacklisted(token));
    }

    @Test
    void testRemoveToken_NonExistentTokenDoesNotThrowException() {
        // Arrange
        String token = "nonexistent";

        // Act & Assert
        assertDoesNotThrow(() -> tokenBlacklistService.removeToken(token));
        assertEquals(0, tokenBlacklistService.size());
    }

    @Test
    void testBlacklistToken_MultipleTokens() {
        // Arrange
        String[] tokens = {
            "token1",
            "token2",
            "token3",
            "token4",
            "token5"
        };

        // Act
        for (String token : tokens) {
            tokenBlacklistService.blacklistToken(token);
        }

        // Assert
        assertEquals(5, tokenBlacklistService.size());
        for (String token : tokens) {
            assertTrue(tokenBlacklistService.isBlacklisted(token));
        }
    }

    @Test
    void testBlacklistToken_WithEmptyString() {
        // Arrange
        String emptyToken = "";

        // Act
        tokenBlacklistService.blacklistToken(emptyToken);

        // Assert
        assertTrue(tokenBlacklistService.isBlacklisted(emptyToken));
        assertEquals(1, tokenBlacklistService.size());
    }

    @Test
    void testBlacklistToken_WithNullValue() {
        // Arrange
        String nullToken = null;

        // Act & Assert - ConcurrentHashMap no permite null keys
        assertThrows(NullPointerException.class, () -> {
            tokenBlacklistService.blacklistToken(nullToken);
        });
    }

    @Test
    void testRemoveToken_ReducesSize() {
        // Arrange
        tokenBlacklistService.blacklistToken("token1");
        tokenBlacklistService.blacklistToken("token2");
        tokenBlacklistService.blacklistToken("token3");

        // Act
        tokenBlacklistService.removeToken("token2");

        // Assert
        assertEquals(2, tokenBlacklistService.size());
        assertTrue(tokenBlacklistService.isBlacklisted("token1"));
        assertFalse(tokenBlacklistService.isBlacklisted("token2"));
        assertTrue(tokenBlacklistService.isBlacklisted("token3"));
    }

    @Test
    void testBlacklistToken_LongToken() {
        // Arrange
        String longToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwicm9sZSI6IkFETUlOIiwiaWF0IjoxNjQ5NzQwMDAwLCJleHAiOjE2NDk3NDM2MDB9.abcdefghijklmnopqrstuvwxyz1234567890";

        // Act
        tokenBlacklistService.blacklistToken(longToken);

        // Assert
        assertTrue(tokenBlacklistService.isBlacklisted(longToken));
    }

    @Test
    void testThreadSafety_ConcurrentHashMapBehavior() {
        // Arrange
        String token1 = "token1";
        String token2 = "token2";

        // Act
        tokenBlacklistService.blacklistToken(token1);
        tokenBlacklistService.blacklistToken(token2);

        // Assert - ConcurrentHashMap permite operaciones concurrentes seguras
        assertTrue(tokenBlacklistService.isBlacklisted(token1));
        assertTrue(tokenBlacklistService.isBlacklisted(token2));
        assertEquals(2, tokenBlacklistService.size());
    }

    @Test
    void testClearAll_WithEmptyBlacklist() {
        // Arrange - Blacklist vacía

        // Act
        tokenBlacklistService.clearAll();

        // Assert
        assertEquals(0, tokenBlacklistService.size());
    }

    @Test
    void testSize_AfterVariousOperations() {
        // Arrange & Act
        assertEquals(0, tokenBlacklistService.size());

        tokenBlacklistService.blacklistToken("token1");
        assertEquals(1, tokenBlacklistService.size());

        tokenBlacklistService.blacklistToken("token2");
        assertEquals(2, tokenBlacklistService.size());

        tokenBlacklistService.removeToken("token1");
        assertEquals(1, tokenBlacklistService.size());

        tokenBlacklistService.blacklistToken("token3");
        assertEquals(2, tokenBlacklistService.size());

        tokenBlacklistService.clearAll();
        assertEquals(0, tokenBlacklistService.size());
    }
}
