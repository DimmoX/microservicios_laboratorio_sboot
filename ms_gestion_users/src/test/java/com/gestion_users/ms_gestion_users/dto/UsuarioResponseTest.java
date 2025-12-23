package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class UsuarioResponseTest {

    @Test
    void testEmptyConstructor() {
        UsuarioResponse response = new UsuarioResponse();
        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getNombre());
        assertNull(response.getUsername());
        assertNull(response.getRol());
        assertNull(response.getActivo());
    }

    @Test
    void testSettersAndGettersBasicFields() {
        UsuarioResponse response = new UsuarioResponse();
        
        response.setId(1L);
        response.setNombre("Juan Pérez");
        response.setUsername("juan@test.com");
        response.setRol("ADMIN");
        response.setActivo(true);

        assertEquals(1L, response.getId());
        assertEquals("Juan Pérez", response.getNombre());
        assertEquals("juan@test.com", response.getUsername());
        assertEquals("ADMIN", response.getRol());
        assertTrue(response.getActivo());
    }

    @Test
    void testSettersAndGettersContactFields() {
        UsuarioResponse response = new UsuarioResponse();
        
        response.setTelefono("+56912345678");
        response.setDireccion("Av. Principal 123");

        assertEquals("+56912345678", response.getTelefono());
        assertEquals("Av. Principal 123", response.getDireccion());
    }

    @Test
    void testSettersAndGettersPersonalFields() {
        UsuarioResponse response = new UsuarioResponse();
        
        response.setRut("12345678-9");
        response.setCargo("Técnico Laboratorio");

        assertEquals("12345678-9", response.getRut());
        assertEquals("Técnico Laboratorio", response.getCargo());
    }

    @Test
    void testSettersAndGettersRelationFields() {
        UsuarioResponse response = new UsuarioResponse();
        
        response.setPacienteId(100L);
        response.setEmpleadoId(200L);
        response.setContactoId(300L);
        response.setDirId(400L);

        assertEquals(100L, response.getPacienteId());
        assertEquals(200L, response.getEmpleadoId());
        assertEquals(300L, response.getContactoId());
        assertEquals(400L, response.getDirId());
    }

    @Test
    void testPatientRole() {
        UsuarioResponse response = new UsuarioResponse();
        response.setRol("PATIENT");
        response.setPacienteId(500L);
        
        assertEquals("PATIENT", response.getRol());
        assertEquals(500L, response.getPacienteId());
    }

    @Test
    void testLabEmployeeRole() {
        UsuarioResponse response = new UsuarioResponse();
        response.setRol("LAB_EMPLOYEE");
        response.setEmpleadoId(600L);
        response.setCargo("Analista");
        
        assertEquals("LAB_EMPLOYEE", response.getRol());
        assertEquals(600L, response.getEmpleadoId());
        assertEquals("Analista", response.getCargo());
    }

    @Test
    void testActivoFalse() {
        UsuarioResponse response = new UsuarioResponse();
        response.setActivo(false);
        assertFalse(response.getActivo());
    }

    @Test
    void testCompleteUsuarioResponse() {
        UsuarioResponse response = new UsuarioResponse();
        
        response.setId(999L);
        response.setNombre("Complete User");
        response.setUsername("complete@test.com");
        response.setRol("ADMIN");
        response.setActivo(true);
        response.setTelefono("+56987654321");
        response.setDireccion("Complete Address 456");
        response.setRut("98765432-1");
        response.setCargo("Administrator");
        response.setPacienteId(111L);
        response.setEmpleadoId(222L);
        response.setContactoId(333L);
        response.setDirId(444L);

        assertEquals(999L, response.getId());
        assertEquals("Complete User", response.getNombre());
        assertEquals("complete@test.com", response.getUsername());
        assertEquals("ADMIN", response.getRol());
        assertTrue(response.getActivo());
        assertEquals("+56987654321", response.getTelefono());
        assertEquals("Complete Address 456", response.getDireccion());
        assertEquals("98765432-1", response.getRut());
        assertEquals("Administrator", response.getCargo());
        assertEquals(111L, response.getPacienteId());
        assertEquals(222L, response.getEmpleadoId());
        assertEquals(333L, response.getContactoId());
        assertEquals(444L, response.getDirId());
    }

    @Test
    void testNullValues() {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(null);
        response.setNombre(null);
        response.setActivo(null);
        
        assertNull(response.getId());
        assertNull(response.getNombre());
        assertNull(response.getActivo());
    }

    @Test
    void testUpdateFields() {
        UsuarioResponse response = new UsuarioResponse();
        response.setNombre("Old Name");
        response.setNombre("New Name");
        assertEquals("New Name", response.getNombre());
    }
}
