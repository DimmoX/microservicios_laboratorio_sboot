package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class UsuarioUpdateRequestTest {

    @Test
    void testEmptyConstructor() {
        UsuarioUpdateRequest request = new UsuarioUpdateRequest();
        assertNotNull(request);
        assertNull(request.getNombre());
        assertNull(request.getTelefono());
        assertNull(request.getDireccion());
        assertNull(request.getActivo());
        assertNull(request.getRol());
    }

    @Test
    void testSettersAndGetters() {
        UsuarioUpdateRequest request = new UsuarioUpdateRequest();
        
        request.setNombre("Juan Pérez");
        request.setTelefono("+56912345678");
        request.setDireccion("Av. Principal 123");
        request.setActivo(true);
        request.setRol("LAB_EMPLOYEE");

        assertEquals("Juan Pérez", request.getNombre());
        assertEquals("+56912345678", request.getTelefono());
        assertEquals("Av. Principal 123", request.getDireccion());
        assertTrue(request.getActivo());
        assertEquals("LAB_EMPLOYEE", request.getRol());
    }

    @Test
    void testUpdateNombre() {
        UsuarioUpdateRequest request = new UsuarioUpdateRequest();
        request.setNombre("Old Name");
        request.setNombre("New Name");
        assertEquals("New Name", request.getNombre());
    }

    @Test
    void testUpdateTelefono() {
        UsuarioUpdateRequest request = new UsuarioUpdateRequest();
        request.setTelefono("111111111");
        request.setTelefono("999999999");
        assertEquals("999999999", request.getTelefono());
    }

    @Test
    void testUpdateDireccion() {
        UsuarioUpdateRequest request = new UsuarioUpdateRequest();
        request.setDireccion("Old Address");
        request.setDireccion("New Address");
        assertEquals("New Address", request.getDireccion());
    }

    @Test
    void testUpdateActivo() {
        UsuarioUpdateRequest request = new UsuarioUpdateRequest();
        request.setActivo(true);
        request.setActivo(false);
        assertFalse(request.getActivo());
    }

    @Test
    void testUpdateRol() {
        UsuarioUpdateRequest request = new UsuarioUpdateRequest();
        request.setRol("PATIENT");
        request.setRol("ADMIN");
        assertEquals("ADMIN", request.getRol());
    }

    @Test
    void testPartialUpdate() {
        UsuarioUpdateRequest request = new UsuarioUpdateRequest();
        request.setNombre("Partial User");
        request.setActivo(true);
        
        assertEquals("Partial User", request.getNombre());
        assertTrue(request.getActivo());
        assertNull(request.getTelefono());
        assertNull(request.getDireccion());
        assertNull(request.getRol());
    }

    @Test
    void testNullValues() {
        UsuarioUpdateRequest request = new UsuarioUpdateRequest();
        request.setNombre(null);
        request.setTelefono(null);
        request.setDireccion(null);
        request.setActivo(null);
        request.setRol(null);
        
        assertNull(request.getNombre());
        assertNull(request.getTelefono());
        assertNull(request.getDireccion());
        assertNull(request.getActivo());
        assertNull(request.getRol());
    }

    @Test
    void testActivoFalse() {
        UsuarioUpdateRequest request = new UsuarioUpdateRequest();
        request.setActivo(false);
        assertFalse(request.getActivo());
    }
}
