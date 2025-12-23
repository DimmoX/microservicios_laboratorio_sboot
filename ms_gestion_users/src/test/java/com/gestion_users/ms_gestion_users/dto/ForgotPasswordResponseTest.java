package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ForgotPasswordResponseTest {

    @Test
    void testConstructorVacio() {
        ForgotPasswordResponse response = new ForgotPasswordResponse();
        
        assertNotNull(response);
        assertNull(response.getEmail());
        assertNull(response.getMessage());
        assertNull(response.getTemporaryPassword());
    }

    @Test
    void testConstructorConParametros() {
        ForgotPasswordResponse response = new ForgotPasswordResponse(
            "user@example.com",
            "Contraseña temporal generada",
            "Temp123!"
        );
        
        assertEquals("user@example.com", response.getEmail());
        assertEquals("Contraseña temporal generada", response.getMessage());
        assertEquals("Temp123!", response.getTemporaryPassword());
    }

    @Test
    void testSettersYGetters() {
        ForgotPasswordResponse response = new ForgotPasswordResponse();
        
        response.setEmail("reset@test.cl");
        response.setMessage("Password reset successful");
        response.setTemporaryPassword("NewTemp456");
        
        assertEquals("reset@test.cl", response.getEmail());
        assertEquals("Password reset successful", response.getMessage());
        assertEquals("NewTemp456", response.getTemporaryPassword());
    }

    @Test
    void testActualizarEmail() {
        ForgotPasswordResponse response = new ForgotPasswordResponse("old@test.com", "Message", "Temp123");
        
        response.setEmail("new@test.com");
        
        assertEquals("new@test.com", response.getEmail());
    }

    @Test
    void testActualizarMensaje() {
        ForgotPasswordResponse response = new ForgotPasswordResponse("user@test.com", "Old message", "Temp123");
        
        response.setMessage("New message");
        
        assertEquals("New message", response.getMessage());
    }

    @Test
    void testActualizarTemporaryPassword() {
        ForgotPasswordResponse response = new ForgotPasswordResponse("user@test.com", "Message", "OldTemp");
        
        response.setTemporaryPassword("NewTempPassword");
        
        assertEquals("NewTempPassword", response.getTemporaryPassword());
    }

    @Test
    void testTodosLosCamposNulos() {
        ForgotPasswordResponse response = new ForgotPasswordResponse(null, null, null);
        
        assertNull(response.getEmail());
        assertNull(response.getMessage());
        assertNull(response.getTemporaryPassword());
    }
}
