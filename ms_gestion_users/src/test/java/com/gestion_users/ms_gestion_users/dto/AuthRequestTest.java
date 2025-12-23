package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class AuthRequestTest {

    @Test
    void testDefaultConstructor() {
        AuthRequest request = new AuthRequest();
        assertNotNull(request);
        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }

    @Test
    void testSetUsername() {
        AuthRequest request = new AuthRequest();
        request.setUsername("user@test.com");
        assertEquals("user@test.com", request.getUsername());
    }

    @Test
    void testSetPassword() {
        AuthRequest request = new AuthRequest();
        request.setPassword("password123");
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testGetEmailReturnsUsername() {
        AuthRequest request = new AuthRequest();
        request.setUsername("test@email.com");
        assertEquals("test@email.com", request.getEmail());
    }

    @Test
    void testSetEmailUpdatesUsername() {
        AuthRequest request = new AuthRequest();
        request.setEmail("email@test.com");
        assertEquals("email@test.com", request.getUsername());
        assertEquals("email@test.com", request.getEmail());
    }

    @Test
    void testCompleteAuthRequest() {
        AuthRequest request = new AuthRequest();
        request.setUsername("admin@hospital.com");
        request.setPassword("SecurePassword123!");
        
        assertEquals("admin@hospital.com", request.getUsername());
        assertEquals("SecurePassword123!", request.getPassword());
        assertEquals("admin@hospital.com", request.getEmail());
    }

    @Test
    void testUpdateUsername() {
        AuthRequest request = new AuthRequest();
        request.setUsername("old@test.com");
        request.setUsername("new@test.com");
        assertEquals("new@test.com", request.getUsername());
    }

    @Test
    void testUpdatePassword() {
        AuthRequest request = new AuthRequest();
        request.setPassword("oldPass");
        request.setPassword("newPass");
        assertEquals("newPass", request.getPassword());
    }

    @Test
    void testNullValues() {
        AuthRequest request = new AuthRequest();
        request.setUsername(null);
        request.setPassword(null);
        
        assertNull(request.getUsername());
        assertNull(request.getPassword());
        assertNull(request.getEmail());
    }
}
