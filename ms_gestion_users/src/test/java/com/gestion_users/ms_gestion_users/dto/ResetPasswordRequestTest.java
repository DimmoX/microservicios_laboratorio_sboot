package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ResetPasswordRequestTest {

    @Test
    void testConstructorVacio() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        
        assertNotNull(request);
        assertNull(request.getUsername());
        assertNull(request.getNewPassword());
    }

    @Test
    void testConstructorConParametros() {
        ResetPasswordRequest request = new ResetPasswordRequest("usuario123", "NewPass456!");
        
        assertEquals("usuario123", request.getUsername());
        assertEquals("NewPass456!", request.getNewPassword());
    }

    @Test
    void testSettersYGetters() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        
        request.setUsername("testuser");
        request.setNewPassword("SecurePass123");
        
        assertEquals("testuser", request.getUsername());
        assertEquals("SecurePass123", request.getNewPassword());
    }

    @Test
    void testActualizarUsername() {
        ResetPasswordRequest request = new ResetPasswordRequest("olduser", "password");
        
        request.setUsername("newuser");
        
        assertEquals("newuser", request.getUsername());
        assertEquals("password", request.getNewPassword());
    }

    @Test
    void testActualizarNewPassword() {
        ResetPasswordRequest request = new ResetPasswordRequest("user", "oldPassword");
        
        request.setNewPassword("newSecurePassword");
        
        assertEquals("newSecurePassword", request.getNewPassword());
    }

    @Test
    void testTodosLosCamposNulos() {
        ResetPasswordRequest request = new ResetPasswordRequest(null, null);
        
        assertNull(request.getUsername());
        assertNull(request.getNewPassword());
    }

    @Test
    void testPasswordComplejo() {
        String complexPassword = "P@ssw0rd!#$%2025";
        ResetPasswordRequest request = new ResetPasswordRequest("admin", complexPassword);
        
        assertEquals("admin", request.getUsername());
        assertEquals(complexPassword, request.getNewPassword());
    }
}
