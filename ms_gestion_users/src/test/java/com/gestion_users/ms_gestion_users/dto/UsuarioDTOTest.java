package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class UsuarioDTOTest {

    @Test
    void testEmptyConstructor() {
        UsuarioDTO dto = new UsuarioDTO();
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
        assertNull(dto.getRole());
        assertNull(dto.getEstado());
        assertNull(dto.getPacienteId());
        assertNull(dto.getEmpleadoId());
    }

    @Test
    void testSettersAndGetters() {
        UsuarioDTO dto = new UsuarioDTO();
        
        dto.setId(1L);
        dto.setUsername("user@test.com");
        dto.setPassword("password123");
        dto.setRole("ADMIN");
        dto.setEstado("ACTIVO");
        dto.setPacienteId(100L);
        dto.setEmpleadoId(200L);

        assertEquals(1L, dto.getId());
        assertEquals("user@test.com", dto.getUsername());
        assertEquals("password123", dto.getPassword());
        assertEquals("ADMIN", dto.getRole());
        assertEquals("ACTIVO", dto.getEstado());
        assertEquals(100L, dto.getPacienteId());
        assertEquals(200L, dto.getEmpleadoId());
    }

    @Test
    void testRoleLabEmployee() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setRole("LAB_EMPLOYEE");
        dto.setEmpleadoId(500L);
        
        assertEquals("LAB_EMPLOYEE", dto.getRole());
        assertEquals(500L, dto.getEmpleadoId());
        assertNull(dto.getPacienteId());
    }

    @Test
    void testRolePatient() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setRole("PATIENT");
        dto.setPacienteId(300L);
        
        assertEquals("PATIENT", dto.getRole());
        assertEquals(300L, dto.getPacienteId());
        assertNull(dto.getEmpleadoId());
    }

    @Test
    void testEstadoInactivo() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setEstado("INACTIVO");
        assertEquals("INACTIVO", dto.getEstado());
    }

    @Test
    void testUpdateUsername() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setUsername("old@test.com");
        dto.setUsername("new@test.com");
        assertEquals("new@test.com", dto.getUsername());
    }

    @Test
    void testUpdatePassword() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setPassword("oldPass");
        dto.setPassword("newPass");
        assertEquals("newPass", dto.getPassword());
    }

    @Test
    void testNullValues() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(null);
        dto.setUsername(null);
        dto.setPassword(null);
        
        assertNull(dto.getId());
        assertNull(dto.getUsername());
        assertNull(dto.getPassword());
    }

    @Test
    void testWithEmailAsUsername() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setUsername("admin@hospital.com");
        assertEquals("admin@hospital.com", dto.getUsername());
    }

    @Test
    void testAllFieldsPopulated() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(999L);
        dto.setUsername("complete@test.com");
        dto.setPassword("SecurePass123");
        dto.setRole("ADMIN");
        dto.setEstado("ACTIVO");
        dto.setPacienteId(111L);
        dto.setEmpleadoId(222L);

        assertEquals(999L, dto.getId());
        assertEquals("complete@test.com", dto.getUsername());
        assertEquals("SecurePass123", dto.getPassword());
        assertEquals("ADMIN", dto.getRole());
        assertEquals("ACTIVO", dto.getEstado());
        assertEquals(111L, dto.getPacienteId());
        assertEquals(222L, dto.getEmpleadoId());
    }
}
