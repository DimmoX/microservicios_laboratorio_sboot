package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ForgotPasswordRequestTest {

    @Test
    void testConstructorVacio() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        
        assertNotNull(request);
        assertNull(request.getEmail());
    }

    @Test
    void testConstructorConParametros() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("user@example.com");
        
        assertEquals("user@example.com", request.getEmail());
    }

    @Test
    void testSetterYGetter() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        
        request.setEmail("forgot@test.cl");
        
        assertEquals("forgot@test.cl", request.getEmail());
    }

    @Test
    void testActualizarEmail() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("old@example.com");
        
        request.setEmail("new@example.com");
        
        assertEquals("new@example.com", request.getEmail());
    }

    @Test
    void testEmailNulo() {
        ForgotPasswordRequest request = new ForgotPasswordRequest(null);
        
        assertNull(request.getEmail());
    }

    @Test
    void testEmailVacio() {
        ForgotPasswordRequest request = new ForgotPasswordRequest("");
        
        assertEquals("", request.getEmail());
    }
}
