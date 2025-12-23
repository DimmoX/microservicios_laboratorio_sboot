package com.api_gateway.ms_api_gateway.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para AuthRequest DTO
 * Cobertura completa de constructores, getters y setters
 */
class AuthRequestTest {

    @Test
    void testConstructorVacio() {
        // Given/When
        AuthRequest request = new AuthRequest();
        
        // Then
        assertNotNull(request);
        assertNull(request.getUsername());
        assertNull(request.getPassword());
    }

    @Test
    void testConstructorConParametros() {
        // Given
        String username = "testuser";
        String password = "testpass123";
        
        // When
        AuthRequest request = new AuthRequest(username, password);
        
        // Then
        assertNotNull(request);
        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
    }

    @Test
    void testSettersYGetters() {
        // Given
        AuthRequest request = new AuthRequest();
        String username = "admin";
        String password = "admin123";
        
        // When
        request.setUsername(username);
        request.setPassword(password);
        
        // Then
        assertEquals(username, request.getUsername());
        assertEquals(password, request.getPassword());
    }

    @Test
    void testEmailAlias_GetEmail() {
        // Given
        String email = "user@example.com";
        AuthRequest request = new AuthRequest();
        request.setUsername(email);
        
        // When
        String resultado = request.getEmail();
        
        // Then
        assertEquals(email, resultado);
        assertEquals(request.getUsername(), request.getEmail());
    }

    @Test
    void testEmailAlias_SetEmail() {
        // Given
        String email = "test@example.com";
        AuthRequest request = new AuthRequest();
        
        // When
        request.setEmail(email);
        
        // Then
        assertEquals(email, request.getEmail());
        assertEquals(email, request.getUsername());
    }

    @Test
    void testSetEmailActualizaUsername() {
        // Given
        AuthRequest request = new AuthRequest("olduser", "pass123");
        String nuevoEmail = "nuevo@example.com";
        
        // When
        request.setEmail(nuevoEmail);
        
        // Then
        assertEquals(nuevoEmail, request.getUsername());
        assertEquals(nuevoEmail, request.getEmail());
    }

    @Test
    void testSetUsernameActualizaEmail() {
        // Given
        AuthRequest request = new AuthRequest();
        String username = "usuario123";
        
        // When
        request.setUsername(username);
        
        // Then
        assertEquals(username, request.getEmail());
        assertEquals(username, request.getUsername());
    }

    @Test
    void testValoresNulos() {
        // Given/When
        AuthRequest request = new AuthRequest(null, null);
        
        // Then
        assertNull(request.getUsername());
        assertNull(request.getPassword());
        assertNull(request.getEmail());
    }

    @Test
    void testValoresVacios() {
        // Given
        String usernameVacio = "";
        String passwordVacio = "";
        
        // When
        AuthRequest request = new AuthRequest(usernameVacio, passwordVacio);
        
        // Then
        assertEquals("", request.getUsername());
        assertEquals("", request.getPassword());
    }

    @Test
    void testActualizacionDeCampos() {
        // Given
        AuthRequest request = new AuthRequest("user1", "pass1");
        
        // When - Primera actualización
        request.setUsername("user2");
        request.setPassword("pass2");
        
        // Then
        assertEquals("user2", request.getUsername());
        assertEquals("pass2", request.getPassword());
        
        // When - Segunda actualización usando email alias
        request.setEmail("user3@example.com");
        
        // Then
        assertEquals("user3@example.com", request.getUsername());
        assertEquals("user3@example.com", request.getEmail());
    }
}
