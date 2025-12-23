package com.gestion_users.ms_gestion_users.dto;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class PacienteDTOTest {

    @Test
    void testConstructorVacio() {
        PacienteDTO dto = new PacienteDTO();
        
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getPnombre());
        assertNull(dto.getSnombre());
        assertNull(dto.getPapellido());
        assertNull(dto.getSapellido());
        assertNull(dto.getRut());
        assertNull(dto.getDirId());
        assertNull(dto.getContactoId());
        assertNull(dto.getCreadoEn());
    }

    @Test
    void testSettersYGetters() {
        PacienteDTO dto = new PacienteDTO();
        OffsetDateTime fecha = OffsetDateTime.now();
        
        dto.setId(1L);
        dto.setPnombre("Ana");
        dto.setSnombre("María");
        dto.setPapellido("Rodríguez");
        dto.setSapellido("Silva");
        dto.setRut("98765432-1");
        dto.setDirId(5L);
        dto.setContactoId(10L);
        dto.setCreadoEn(fecha);
        
        assertEquals(1L, dto.getId());
        assertEquals("Ana", dto.getPnombre());
        assertEquals("María", dto.getSnombre());
        assertEquals("Rodríguez", dto.getPapellido());
        assertEquals("Silva", dto.getSapellido());
        assertEquals("98765432-1", dto.getRut());
        assertEquals(5L, dto.getDirId());
        assertEquals(10L, dto.getContactoId());
        assertEquals(fecha, dto.getCreadoEn());
    }

    @Test
    void testActualizarPnombre() {
        PacienteDTO dto = new PacienteDTO();
        dto.setPnombre("Ana");
        
        dto.setPnombre("María");
        
        assertEquals("María", dto.getPnombre());
    }

    @Test
    void testActualizarRut() {
        PacienteDTO dto = new PacienteDTO();
        dto.setRut("11111111-1");
        
        dto.setRut("22222222-2");
        
        assertEquals("22222222-2", dto.getRut());
    }

    @Test
    void testPacienteSinSnombre() {
        PacienteDTO dto = new PacienteDTO();
        dto.setPnombre("Carlos");
        dto.setPapellido("Fuentes");
        
        assertEquals("Carlos", dto.getPnombre());
        assertNull(dto.getSnombre());
        assertEquals("Fuentes", dto.getPapellido());
    }

    @Test
    void testPacienteSinSapellido() {
        PacienteDTO dto = new PacienteDTO();
        dto.setPapellido("Torres");
        
        assertEquals("Torres", dto.getPapellido());
        assertNull(dto.getSapellido());
    }

    @Test
    void testActualizarDirId() {
        PacienteDTO dto = new PacienteDTO();
        dto.setDirId(3L);
        
        dto.setDirId(8L);
        
        assertEquals(8L, dto.getDirId());
    }

    @Test
    void testActualizarContactoId() {
        PacienteDTO dto = new PacienteDTO();
        dto.setContactoId(50L);
        
        dto.setContactoId(150L);
        
        assertEquals(150L, dto.getContactoId());
    }

    @Test
    void testCreadoEnSoloLectura() {
        PacienteDTO dto = new PacienteDTO();
        OffsetDateTime ahora = OffsetDateTime.now();
        
        dto.setCreadoEn(ahora);
        
        assertEquals(ahora, dto.getCreadoEn());
    }

    @Test
    void testIdNegativo() {
        PacienteDTO dto = new PacienteDTO();
        
        dto.setId(-1L);
        
        assertEquals(-1L, dto.getId());
    }

    @Test
    void testTodosLosCamposNulos() {
        PacienteDTO dto = new PacienteDTO();
        
        assertNull(dto.getId());
        assertNull(dto.getPnombre());
        assertNull(dto.getSnombre());
        assertNull(dto.getPapellido());
        assertNull(dto.getSapellido());
        assertNull(dto.getRut());
        assertNull(dto.getDirId());
        assertNull(dto.getContactoId());
        assertNull(dto.getCreadoEn());
    }
}
