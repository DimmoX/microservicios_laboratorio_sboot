package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ResetPasswordResponseTest {

    @Test
    void testEmptyConstructor() {
        ResetPasswordResponse response = new ResetPasswordResponse();
        assertNotNull(response);
        assertNull(response.getUsername());
        assertNull(response.getMessage());
        assertNull(response.getNewHash());
    }

    @Test
    void testParameterizedConstructor() {
        String username = "user@test.com";
        String message = "Password reset successful";
        String newHash = "$2a$10$abcdefghijklmnopqrstuv";

        ResetPasswordResponse response = new ResetPasswordResponse(username, message, newHash);

        assertEquals(username, response.getUsername());
        assertEquals(message, response.getMessage());
        assertEquals(newHash, response.getNewHash());
    }

    @Test
    void testSettersAndGetters() {
        ResetPasswordResponse response = new ResetPasswordResponse();
        
        response.setUsername("admin@test.com");
        response.setMessage("Password updated successfully");
        response.setNewHash("$2a$10$newhashvalue12345");

        assertEquals("admin@test.com", response.getUsername());
        assertEquals("Password updated successfully", response.getMessage());
        assertEquals("$2a$10$newhashvalue12345", response.getNewHash());
    }

    @Test
    void testUpdateUsername() {
        ResetPasswordResponse response = new ResetPasswordResponse("old@test.com", "msg", "hash");
        response.setUsername("new@test.com");
        assertEquals("new@test.com", response.getUsername());
    }

    @Test
    void testUpdateMessage() {
        ResetPasswordResponse response = new ResetPasswordResponse("user", "old message", "hash");
        response.setMessage("new message");
        assertEquals("new message", response.getMessage());
    }

    @Test
    void testUpdateNewHash() {
        ResetPasswordResponse response = new ResetPasswordResponse("user", "msg", "oldhash");
        response.setNewHash("newhash");
        assertEquals("newhash", response.getNewHash());
    }

    @Test
    void testNullValues() {
        ResetPasswordResponse response = new ResetPasswordResponse(null, null, null);
        assertNull(response.getUsername());
        assertNull(response.getMessage());
        assertNull(response.getNewHash());
    }
}
