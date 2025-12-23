package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class DireccionDTOTest {

    @Test
    void testConstructorVacio() {
        DireccionDTO dto = new DireccionDTO();
        
        assertNotNull(dto);
        assertNull(dto.getCalle());
        assertNull(dto.getNumero());
        assertNull(dto.getCiudad());
        assertNull(dto.getComuna());
        assertNull(dto.getRegion());
    }

    @Test
    void testConstructorConParametros() {
        DireccionDTO dto = new DireccionDTO("Av. Libertador", 1234, "Santiago", "Providencia", "Metropolitana");
        
        assertEquals("Av. Libertador", dto.getCalle());
        assertEquals(1234, dto.getNumero());
        assertEquals("Santiago", dto.getCiudad());
        assertEquals("Providencia", dto.getComuna());
        assertEquals("Metropolitana", dto.getRegion());
    }

    @Test
    void testSettersYGetters() {
        DireccionDTO dto = new DireccionDTO();
        
        dto.setCalle("Av. Apoquindo");
        dto.setNumero(4567);
        dto.setCiudad("Santiago");
        dto.setComuna("Las Condes");
        dto.setRegion("Metropolitana");
        
        assertEquals("Av. Apoquindo", dto.getCalle());
        assertEquals(4567, dto.getNumero());
        assertEquals("Santiago", dto.getCiudad());
        assertEquals("Las Condes", dto.getComuna());
        assertEquals("Metropolitana", dto.getRegion());
    }

    @Test
    void testActualizarCalle() {
        DireccionDTO dto = new DireccionDTO("Calle Antigua", 100, "Santiago", "Centro", "Metropolitana");
        
        dto.setCalle("Calle Nueva");
        
        assertEquals("Calle Nueva", dto.getCalle());
        assertEquals(100, dto.getNumero());
    }

    @Test
    void testActualizarNumero() {
        DireccionDTO dto = new DireccionDTO("Av. Principal", 100, "Santiago", "Centro", "Metropolitana");
        
        dto.setNumero(500);
        
        assertEquals(500, dto.getNumero());
    }

    @Test
    void testActualizarCiudad() {
        DireccionDTO dto = new DireccionDTO("Calle 1", 100, "Santiago", "Centro", "Metropolitana");
        
        dto.setCiudad("Valparaíso");
        
        assertEquals("Valparaíso", dto.getCiudad());
    }

    @Test
    void testActualizarComuna() {
        DireccionDTO dto = new DireccionDTO("Calle 1", 100, "Santiago", "Centro", "Metropolitana");
        
        dto.setComuna("Ñuñoa");
        
        assertEquals("Ñuñoa", dto.getComuna());
    }

    @Test
    void testActualizarRegion() {
        DireccionDTO dto = new DireccionDTO("Calle 1", 100, "Santiago", "Centro", "Metropolitana");
        
        dto.setRegion("Valparaíso");
        
        assertEquals("Valparaíso", dto.getRegion());
    }

    @Test
    void testDireccionConNumeroNulo() {
        DireccionDTO dto = new DireccionDTO("Av. Sin Número", null, "Santiago", "Centro", "Metropolitana");
        
        assertEquals("Av. Sin Número", dto.getCalle());
        assertNull(dto.getNumero());
    }

    @Test
    void testTodosLosCamposNulos() {
        DireccionDTO dto = new DireccionDTO(null, null, null, null, null);
        
        assertNull(dto.getCalle());
        assertNull(dto.getNumero());
        assertNull(dto.getCiudad());
        assertNull(dto.getComuna());
        assertNull(dto.getRegion());
    }
}
