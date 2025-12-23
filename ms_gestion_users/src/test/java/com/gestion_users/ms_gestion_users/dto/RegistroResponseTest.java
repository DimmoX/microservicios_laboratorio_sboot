package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class RegistroResponseTest {

    @Test
    void testConstructorVacio() {
        RegistroResponse response = new RegistroResponse();
        
        assertNotNull(response);
        assertNull(response.getPacienteId());
        assertNull(response.getEmpleadoId());
        assertNull(response.getUsuarioId());
        assertNull(response.getUsername());
        assertNull(response.getRole());
        assertNull(response.getMensaje());
    }

    @Test
    void testConstructorConParametros() {
        RegistroResponse response = new RegistroResponse(
            10L,
            null,
            100L,
            "patient@test.com",
            "PATIENT",
            "Paciente registrado exitosamente"
        );
        
        assertEquals(10L, response.getPacienteId());
        assertNull(response.getEmpleadoId());
        assertEquals(100L, response.getUsuarioId());
        assertEquals("patient@test.com", response.getUsername());
        assertEquals("PATIENT", response.getRole());
        assertEquals("Paciente registrado exitosamente", response.getMensaje());
    }

    @Test
    void testForPaciente() {
        RegistroResponse response = RegistroResponse.forPaciente(25L, 200L, "ana@example.com");
        
        assertEquals(25L, response.getPacienteId());
        assertNull(response.getEmpleadoId());
        assertEquals(200L, response.getUsuarioId());
        assertEquals("ana@example.com", response.getUsername());
        assertEquals("PATIENT", response.getRole());
        assertEquals("Paciente registrado exitosamente", response.getMensaje());
    }

    @Test
    void testForEmpleado() {
        RegistroResponse response = RegistroResponse.forEmpleado(50L, 300L, "empleado@lab.com");
        
        assertNull(response.getPacienteId());
        assertEquals(50L, response.getEmpleadoId());
        assertEquals(300L, response.getUsuarioId());
        assertEquals("empleado@lab.com", response.getUsername());
        assertEquals("LAB_EMPLOYEE", response.getRole());
        assertEquals("Empleado registrado exitosamente", response.getMensaje());
    }

    @Test
    void testSettersYGetters() {
        RegistroResponse response = new RegistroResponse();
        
        response.setPacienteId(15L);
        response.setEmpleadoId(null);
        response.setUsuarioId(150L);
        response.setUsername("user@test.com");
        response.setRole("ADMIN");
        response.setMensaje("Usuario registrado correctamente");
        
        assertEquals(15L, response.getPacienteId());
        assertNull(response.getEmpleadoId());
        assertEquals(150L, response.getUsuarioId());
        assertEquals("user@test.com", response.getUsername());
        assertEquals("ADMIN", response.getRole());
        assertEquals("Usuario registrado correctamente", response.getMensaje());
    }

    @Test
    void testActualizarMensaje() {
        RegistroResponse response = new RegistroResponse();
        response.setMensaje("Mensaje inicial");
        
        response.setMensaje("Mensaje actualizado");
        
        assertEquals("Mensaje actualizado", response.getMensaje());
    }

    @Test
    void testActualizarRole() {
        RegistroResponse response = RegistroResponse.forPaciente(1L, 10L, "user@test.com");
        
        response.setRole("ADMIN");
        
        assertEquals("ADMIN", response.getRole());
    }

    @Test
    void testRespuestaParaPaciente() {
        RegistroResponse response = RegistroResponse.forPaciente(100L, 500L, "paciente@email.com");
        
        assertNotNull(response.getPacienteId());
        assertNull(response.getEmpleadoId());
        assertEquals("PATIENT", response.getRole());
    }

    @Test
    void testRespuestaParaEmpleado() {
        RegistroResponse response = RegistroResponse.forEmpleado(200L, 600L, "empleado@email.com");
        
        assertNull(response.getPacienteId());
        assertNotNull(response.getEmpleadoId());
        assertEquals("LAB_EMPLOYEE", response.getRole());
    }

    @Test
    void testTodosLosCamposNulos() {
        RegistroResponse response = new RegistroResponse(null, null, null, null, null, null);
        
        assertNull(response.getPacienteId());
        assertNull(response.getEmpleadoId());
        assertNull(response.getUsuarioId());
        assertNull(response.getUsername());
        assertNull(response.getRole());
        assertNull(response.getMensaje());
    }

    @Test
    void testIdsPacienteYEmpleadoMutuamenteExcluyentes() {
        RegistroResponse responsePaciente = RegistroResponse.forPaciente(10L, 100L, "pac@test.com");
        RegistroResponse responseEmpleado = RegistroResponse.forEmpleado(20L, 200L, "emp@test.com");
        
        assertNotNull(responsePaciente.getPacienteId());
        assertNull(responsePaciente.getEmpleadoId());
        
        assertNull(responseEmpleado.getPacienteId());
        assertNotNull(responseEmpleado.getEmpleadoId());
    }
}
