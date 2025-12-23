package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class AuthResponseTest {

    @Test
    void testEmptyConstructor() {
        AuthResponse response = new AuthResponse();
        assertNotNull(response);
        assertNull(response.getToken());
        assertNull(response.getUsuario());
    }

    @Test
    void testParameterizedConstructor() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
        UsuarioResponse usuario = new UsuarioResponse();
        usuario.setId(1L);
        usuario.setUsername("test@user.com");

        AuthResponse response = new AuthResponse(token, usuario);

        assertEquals(token, response.getToken());
        assertNotNull(response.getUsuario());
        assertEquals(1L, response.getUsuario().getId());
        assertEquals("test@user.com", response.getUsuario().getUsername());
    }

    @Test
    void testSetToken() {
        AuthResponse response = new AuthResponse();
        String token = "jwt.token.here";
        response.setToken(token);
        assertEquals(token, response.getToken());
    }

    @Test
    void testSetUsuario() {
        AuthResponse response = new AuthResponse();
        UsuarioResponse usuario = new UsuarioResponse();
        usuario.setNombre("Juan Pérez");
        usuario.setRol("ADMIN");
        
        response.setUsuario(usuario);
        
        assertNotNull(response.getUsuario());
        assertEquals("Juan Pérez", response.getUsuario().getNombre());
        assertEquals("ADMIN", response.getUsuario().getRol());
    }

    @Test
    void testCompleteAuthResponse() {
        String token = "complete.jwt.token";
        UsuarioResponse usuario = new UsuarioResponse();
        usuario.setId(100L);
        usuario.setNombre("Complete User");
        usuario.setUsername("complete@test.com");
        usuario.setRol("LAB_EMPLOYEE");
        usuario.setActivo(true);

        AuthResponse response = new AuthResponse(token, usuario);

        assertEquals(token, response.getToken());
        assertNotNull(response.getUsuario());
        assertEquals(100L, response.getUsuario().getId());
        assertEquals("Complete User", response.getUsuario().getNombre());
        assertEquals("complete@test.com", response.getUsuario().getUsername());
        assertEquals("LAB_EMPLOYEE", response.getUsuario().getRol());
        assertTrue(response.getUsuario().getActivo());
    }

    @Test
    void testUpdateToken() {
        AuthResponse response = new AuthResponse("old.token", null);
        response.setToken("new.token");
        assertEquals("new.token", response.getToken());
    }

    @Test
    void testUpdateUsuario() {
        UsuarioResponse oldUser = new UsuarioResponse();
        oldUser.setNombre("Old User");
        
        UsuarioResponse newUser = new UsuarioResponse();
        newUser.setNombre("New User");
        
        AuthResponse response = new AuthResponse("token", oldUser);
        response.setUsuario(newUser);
        
        assertEquals("New User", response.getUsuario().getNombre());
    }

    @Test
    void testNullValues() {
        AuthResponse response = new AuthResponse(null, null);
        assertNull(response.getToken());
        assertNull(response.getUsuario());
    }

    @Test
    void testAuthResponseWithPatientUser() {
        UsuarioResponse usuario = new UsuarioResponse();
        usuario.setRol("PATIENT");
        usuario.setPacienteId(500L);
        
        AuthResponse response = new AuthResponse("patient.token", usuario);
        
        assertEquals("PATIENT", response.getUsuario().getRol());
        assertEquals(500L, response.getUsuario().getPacienteId());
    }
}
