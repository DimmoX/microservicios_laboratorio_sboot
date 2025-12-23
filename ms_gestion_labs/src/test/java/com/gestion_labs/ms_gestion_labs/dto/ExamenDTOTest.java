package com.gestion_labs.ms_gestion_labs.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ExamenDTOTest {

    @Test
    void testConstructorVacio() {
        ExamenDTO dto = new ExamenDTO();
        assertNotNull(dto);
    }

    @Test
    void testIdGetterSetter() {
        ExamenDTO dto = new ExamenDTO();
        Long id = 1L;
        
        dto.setId(id);
        
        assertEquals(id, dto.getId());
    }

    @Test
    void testCodigoGetterSetter() {
        ExamenDTO dto = new ExamenDTO();
        String codigo = "HEM1";
        
        dto.setCodigo(codigo);
        
        assertEquals(codigo, dto.getCodigo());
    }

    @Test
    void testNombreGetterSetter() {
        ExamenDTO dto = new ExamenDTO();
        String nombre = "Hemograma Completo";
        
        dto.setNombre(nombre);
        
        assertEquals(nombre, dto.getNombre());
    }

    @Test
    void testTipoGetterSetter() {
        ExamenDTO dto = new ExamenDTO();
        String tipo = "Hematología";
        
        dto.setTipo(tipo);
        
        assertEquals(tipo, dto.getTipo());
    }

    @Test
    void testSettersConValoresNull() {
        ExamenDTO dto = new ExamenDTO();
        
        dto.setId(null);
        dto.setCodigo(null);
        dto.setNombre(null);
        dto.setTipo(null);
        
        assertNull(dto.getId());
        assertNull(dto.getCodigo());
        assertNull(dto.getNombre());
        assertNull(dto.getTipo());
    }

    @Test
    void testTodosLosCamposCompletos() {
        ExamenDTO dto = new ExamenDTO();
        
        dto.setId(10L);
        dto.setCodigo("GLU1");
        dto.setNombre("Glucosa en ayunas");
        dto.setTipo("Bioquímica");
        
        assertEquals(10L, dto.getId());
        assertEquals("GLU1", dto.getCodigo());
        assertEquals("Glucosa en ayunas", dto.getNombre());
        assertEquals("Bioquímica", dto.getTipo());
    }

    @Test
    void testCodigoMaximo4Caracteres() {
        ExamenDTO dto = new ExamenDTO();
        String codigoCorto = "H123";
        
        dto.setCodigo(codigoCorto);
        
        assertEquals(codigoCorto, dto.getCodigo());
        assertTrue(dto.getCodigo().length() <= 4);
    }

    @Test
    void testNombreMaximo50Caracteres() {
        ExamenDTO dto = new ExamenDTO();
        String nombreLargo = "Perfil Lipídico Completo con HDL, LDL y Total";
        
        dto.setNombre(nombreLargo);
        
        assertEquals(nombreLargo, dto.getNombre());
        assertTrue(dto.getNombre().length() <= 50);
    }

    @Test
    void testTipoMaximo20Caracteres() {
        ExamenDTO dto = new ExamenDTO();
        String tipo = "Microbiología";
        
        dto.setTipo(tipo);
        
        assertEquals(tipo, dto.getTipo());
        assertTrue(dto.getTipo().length() <= 20);
    }

    @Test
    void testCodigoVacio() {
        ExamenDTO dto = new ExamenDTO();
        dto.setCodigo("");
        
        assertEquals("", dto.getCodigo());
    }

    @Test
    void testNombreVacio() {
        ExamenDTO dto = new ExamenDTO();
        dto.setNombre("");
        
        assertEquals("", dto.getNombre());
    }

    @Test
    void testTipoVacio() {
        ExamenDTO dto = new ExamenDTO();
        dto.setTipo("");
        
        assertEquals("", dto.getTipo());
    }

    @Test
    void testDiferentesValoresId() {
        ExamenDTO dto = new ExamenDTO();
        Long[] ids = {1L, 100L, 999L, 1000000L};
        
        for (Long id : ids) {
            dto.setId(id);
            assertEquals(id, dto.getId());
        }
    }
}
