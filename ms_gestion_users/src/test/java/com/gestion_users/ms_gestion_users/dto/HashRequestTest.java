package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class HashRequestTest {

    @Test
    void testConstructorVacio() {
        HashRequest request = new HashRequest();
        
        assertNotNull(request);
        assertNull(request.getPassword());
    }

    @Test
    void testConstructorConParametros() {
        HashRequest request = new HashRequest("mySecretPassword123");
        
        assertEquals("mySecretPassword123", request.getPassword());
    }

    @Test
    void testSetterYGetter() {
        HashRequest request = new HashRequest();
        
        request.setPassword("testPassword456");
        
        assertEquals("testPassword456", request.getPassword());
    }

    @Test
    void testActualizarPassword() {
        HashRequest request = new HashRequest("oldPassword");
        
        request.setPassword("newPassword");
        
        assertEquals("newPassword", request.getPassword());
    }

    @Test
    void testPasswordNulo() {
        HashRequest request = new HashRequest(null);
        
        assertNull(request.getPassword());
    }

    @Test
    void testPasswordVacio() {
        HashRequest request = new HashRequest("");
        
        assertEquals("", request.getPassword());
    }

    @Test
    void testPasswordComplejo() {
        String complexPassword = "P@ssw0rd!2025#$%";
        HashRequest request = new HashRequest(complexPassword);
        
        assertEquals(complexPassword, request.getPassword());
    }
}
