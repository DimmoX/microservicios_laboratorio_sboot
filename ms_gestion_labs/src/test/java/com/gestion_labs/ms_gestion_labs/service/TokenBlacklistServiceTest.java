package com.gestion_labs.ms_gestion_labs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {

    @InjectMocks
    private TokenBlacklistService service;

    @BeforeEach
    void setUp() {
        service.clearAll();
    }

    @Test
    void testBlacklistToken() {
        // Arrange
        String token = "jwt.token.123";

        // Act
        service.blacklistToken(token);

        // Assert
        assertTrue(service.isBlacklisted(token));
        assertEquals(1, service.size());
    }

    @Test
    void testBlacklistTokenVacio() {
        // Arrange
        String token = "";

        // Act
        service.blacklistToken(token);

        // Assert
        assertFalse(service.isBlacklisted(token));
        assertEquals(0, service.size());
    }

    @Test
    void testBlacklistTokenNulo() {
        // Arrange
        String token = null;

        // Act
        service.blacklistToken(token);

        // Assert
        assertEquals(0, service.size());
    }

    @Test
    void testBlacklistMultiplesTokens() {
        // Arrange
        String token1 = "jwt.token.1";
        String token2 = "jwt.token.2";
        String token3 = "jwt.token.3";

        // Act
        service.blacklistToken(token1);
        service.blacklistToken(token2);
        service.blacklistToken(token3);

        // Assert
        assertTrue(service.isBlacklisted(token1));
        assertTrue(service.isBlacklisted(token2));
        assertTrue(service.isBlacklisted(token3));
        assertEquals(3, service.size());
    }

    @Test
    void testBlacklistTokenDuplicado() {
        // Arrange
        String token = "jwt.token.duplicate";

        // Act
        service.blacklistToken(token);
        service.blacklistToken(token);
        service.blacklistToken(token);

        // Assert
        assertTrue(service.isBlacklisted(token));
        assertEquals(1, service.size()); // Set no permite duplicados
    }

    @Test
    void testIsBlacklistedTokenNoExiste() {
        // Arrange
        String token = "jwt.token.nonexistent";

        // Act
        boolean result = service.isBlacklisted(token);

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsBlacklistedTokenNulo() {
        // Act
        boolean result = service.isBlacklisted(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void testRemoveToken() {
        // Arrange
        String token = "jwt.token.remove";
        service.blacklistToken(token);
        assertTrue(service.isBlacklisted(token));

        // Act
        service.removeToken(token);

        // Assert
        assertFalse(service.isBlacklisted(token));
        assertEquals(0, service.size());
    }

    @Test
    void testRemoveTokenNoExiste() {
        // Arrange
        String token = "jwt.token.nonexistent";
        service.blacklistToken("other.token");
        int initialSize = service.size();

        // Act
        service.removeToken(token);

        // Assert
        assertEquals(initialSize, service.size());
    }

    @Test
    void testRemoveTokenNulo() {
        // Arrange
        service.blacklistToken("jwt.token.1");
        int initialSize = service.size();

        // Act
        service.removeToken(null);

        // Assert
        assertEquals(initialSize, service.size());
    }

    @Test
    void testRemoveMultiplesTokens() {
        // Arrange
        String token1 = "jwt.token.1";
        String token2 = "jwt.token.2";
        String token3 = "jwt.token.3";
        
        service.blacklistToken(token1);
        service.blacklistToken(token2);
        service.blacklistToken(token3);

        // Act
        service.removeToken(token1);
        service.removeToken(token3);

        // Assert
        assertFalse(service.isBlacklisted(token1));
        assertTrue(service.isBlacklisted(token2));
        assertFalse(service.isBlacklisted(token3));
        assertEquals(1, service.size());
    }

    @Test
    void testClearAll() {
        // Arrange
        service.blacklistToken("jwt.token.1");
        service.blacklistToken("jwt.token.2");
        service.blacklistToken("jwt.token.3");
        assertEquals(3, service.size());

        // Act
        service.clearAll();

        // Assert
        assertEquals(0, service.size());
        assertFalse(service.isBlacklisted("jwt.token.1"));
        assertFalse(service.isBlacklisted("jwt.token.2"));
        assertFalse(service.isBlacklisted("jwt.token.3"));
    }

    @Test
    void testClearAllVacia() {
        // Act
        service.clearAll();

        // Assert
        assertEquals(0, service.size());
    }

    @Test
    void testSize() {
        // Arrange & Act & Assert
        assertEquals(0, service.size());
        
        service.blacklistToken("jwt.token.1");
        assertEquals(1, service.size());
        
        service.blacklistToken("jwt.token.2");
        assertEquals(2, service.size());
        
        service.removeToken("jwt.token.1");
        assertEquals(1, service.size());
        
        service.clearAll();
        assertEquals(0, service.size());
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        // Test básico de thread safety
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                service.blacklistToken("token.t1." + i);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                service.blacklistToken("token.t2." + i);
            }
        });

        // Act
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Assert
        assertEquals(200, service.size());
    }

    @Test
    void testTokensLargos() {
        // Arrange
        String longToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        // Act
        service.blacklistToken(longToken);

        // Assert
        assertTrue(service.isBlacklisted(longToken));
        assertEquals(1, service.size());
    }
}
