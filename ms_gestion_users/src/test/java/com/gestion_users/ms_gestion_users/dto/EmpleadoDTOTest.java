package com.gestion_users.ms_gestion_users.dto;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class EmpleadoDTOTest {

    @Test
    void testConstructorVacio() {
        EmpleadoDTO dto = new EmpleadoDTO();
        
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getPnombre());
        assertNull(dto.getSnombre());
        assertNull(dto.getPapellido());
        assertNull(dto.getSapellido());
        assertNull(dto.getRut());
        assertNull(dto.getCargo());
        assertNull(dto.getDirId());
        assertNull(dto.getContactoId());
        assertNull(dto.getCreadoEn());
    }

    @Test
    void testSettersYGetters() {
        EmpleadoDTO dto = new EmpleadoDTO();
        OffsetDateTime fecha = OffsetDateTime.now();
        
        dto.setId(1L);
        dto.setPnombre("Juan");
        dto.setSnombre("Carlos");
        dto.setPapellido("González");
        dto.setSapellido("Pérez");
        dto.setRut("12345678-9");
        dto.setCargo("Técnico de Laboratorio");
        dto.setDirId(10L);
        dto.setContactoId(20L);
        dto.setCreadoEn(fecha);
        
        assertEquals(1L, dto.getId());
        assertEquals("Juan", dto.getPnombre());
        assertEquals("Carlos", dto.getSnombre());
        assertEquals("González", dto.getPapellido());
        assertEquals("Pérez", dto.getSapellido());
        assertEquals("12345678-9", dto.getRut());
        assertEquals("Técnico de Laboratorio", dto.getCargo());
        assertEquals(10L, dto.getDirId());
        assertEquals(20L, dto.getContactoId());
        assertEquals(fecha, dto.getCreadoEn());
    }

    @Test
    void testActualizarPnombre() {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setPnombre("Juan");
        
        dto.setPnombre("Pedro");
        
        assertEquals("Pedro", dto.getPnombre());
    }

    @Test
    void testActualizarCargo() {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setCargo("Asistente");
        
        dto.setCargo("Supervisor");
        
        assertEquals("Supervisor", dto.getCargo());
    }

    @Test
    void testActualizarRut() {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setRut("11111111-1");
        
        dto.setRut("22222222-2");
        
        assertEquals("22222222-2", dto.getRut());
    }

    @Test
    void testEmpleadoSinSnombre() {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setPnombre("María");
        dto.setPapellido("López");
        
        assertEquals("María", dto.getPnombre());
        assertNull(dto.getSnombre());
        assertEquals("López", dto.getPapellido());
    }

    @Test
    void testEmpleadoSinSapellido() {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setPapellido("Martínez");
        
        assertEquals("Martínez", dto.getPapellido());
        assertNull(dto.getSapellido());
    }

    @Test
    void testActualizarDirId() {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setDirId(5L);
        
        dto.setDirId(15L);
        
        assertEquals(15L, dto.getDirId());
    }

    @Test
    void testActualizarContactoId() {
        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setContactoId(100L);
        
        dto.setContactoId(200L);
        
        assertEquals(200L, dto.getContactoId());
    }

    @Test
    void testCreadoEnSoloLectura() {
        EmpleadoDTO dto = new EmpleadoDTO();
        OffsetDateTime ahora = OffsetDateTime.now();
        
        dto.setCreadoEn(ahora);
        
        assertEquals(ahora, dto.getCreadoEn());
    }

    @Test
    void testCargoMaximo40Caracteres() {
        EmpleadoDTO dto = new EmpleadoDTO();
        String cargoLargo = "Jefe de Departamento de Análisis Clínico";
        
        dto.setCargo(cargoLargo);
        
        assertEquals(cargoLargo, dto.getCargo());
        assertTrue(dto.getCargo().length() <= 40);
    }
}
