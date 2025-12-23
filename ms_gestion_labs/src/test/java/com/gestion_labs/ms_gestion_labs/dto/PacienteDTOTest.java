package com.gestion_labs.ms_gestion_labs.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    }

    @Test
    void testIdGetterSetter() {
        PacienteDTO dto = new PacienteDTO();
        Long id = 123L;
        
        dto.setId(id);
        
        assertEquals(id, dto.getId());
    }

    @Test
    void testPnombreGetterSetter() {
        PacienteDTO dto = new PacienteDTO();
        String nombre = "Juan";
        
        dto.setPnombre(nombre);
        
        assertEquals(nombre, dto.getPnombre());
    }

    @Test
    void testSnombreGetterSetter() {
        PacienteDTO dto = new PacienteDTO();
        String nombre = "Carlos";
        
        dto.setSnombre(nombre);
        
        assertEquals(nombre, dto.getSnombre());
    }

    @Test
    void testPapellidoGetterSetter() {
        PacienteDTO dto = new PacienteDTO();
        String apellido = "Perez";
        
        dto.setPapellido(apellido);
        
        assertEquals(apellido, dto.getPapellido());
    }

    @Test
    void testSapellidoGetterSetter() {
        PacienteDTO dto = new PacienteDTO();
        String apellido = "Gonzalez";
        
        dto.setSapellido(apellido);
        
        assertEquals(apellido, dto.getSapellido());
    }

    @Test
    void testSettersConValoresNull() {
        PacienteDTO dto = new PacienteDTO();
        dto.setId(1L);
        dto.setPnombre("Juan");
        dto.setSnombre("Carlos");
        dto.setPapellido("Perez");
        dto.setSapellido("Gonzalez");
        
        dto.setId(null);
        dto.setPnombre(null);
        dto.setSnombre(null);
        dto.setPapellido(null);
        dto.setSapellido(null);
        
        assertNull(dto.getId());
        assertNull(dto.getPnombre());
        assertNull(dto.getSnombre());
        assertNull(dto.getPapellido());
        assertNull(dto.getSapellido());
    }

    @Test
    void testTodosLosCamposCompletos() {
        PacienteDTO dto = new PacienteDTO();
        
        dto.setId(456L);
        dto.setPnombre("Maria");
        dto.setSnombre("Isabel");
        dto.setPapellido("Rodriguez");
        dto.setSapellido("Martinez");
        
        assertEquals(456L, dto.getId());
        assertEquals("Maria", dto.getPnombre());
        assertEquals("Isabel", dto.getSnombre());
        assertEquals("Rodriguez", dto.getPapellido());
        assertEquals("Martinez", dto.getSapellido());
    }

    @Test
    void testGetNombreCompletoConTodosLosCampos() {
        PacienteDTO dto = new PacienteDTO();
        dto.setPnombre("Juan");
        dto.setSnombre("Carlos");
        dto.setPapellido("Perez");
        dto.setSapellido("Gonzalez");
        
        String nombreCompleto = dto.getNombreCompleto();
        
        assertEquals("Juan Carlos Perez Gonzalez", nombreCompleto);
    }

    @Test
    void testGetNombreCompletoSoloPrimerNombre() {
        PacienteDTO dto = new PacienteDTO();
        dto.setPnombre("Juan");
        
        String nombreCompleto = dto.getNombreCompleto();
        
        assertEquals("Juan", nombreCompleto);
    }

    @Test
    void testGetNombreCompletoSoloApellidos() {
        PacienteDTO dto = new PacienteDTO();
        dto.setPapellido("Perez");
        dto.setSapellido("Gonzalez");
        
        String nombreCompleto = dto.getNombreCompleto();
        
        assertEquals("Perez Gonzalez", nombreCompleto);
    }

    @Test
    void testGetNombreCompletoSinSegundoNombre() {
        PacienteDTO dto = new PacienteDTO();
        dto.setPnombre("Maria");
        dto.setPapellido("Rodriguez");
        dto.setSapellido("Martinez");
        
        String nombreCompleto = dto.getNombreCompleto();
        
        assertEquals("Maria Rodriguez Martinez", nombreCompleto);
    }

    @Test
    void testGetNombreCompletoSinSegundoApellido() {
        PacienteDTO dto = new PacienteDTO();
        dto.setPnombre("Pedro");
        dto.setSnombre("Jose");
        dto.setPapellido("Lopez");
        
        String nombreCompleto = dto.getNombreCompleto();
        
        assertEquals("Pedro Jose Lopez", nombreCompleto);
    }

    @Test
    void testGetNombreCompletoConCamposNull() {
        PacienteDTO dto = new PacienteDTO();
        
        String nombreCompleto = dto.getNombreCompleto();
        
        assertEquals("", nombreCompleto);
    }

    @Test
    void testGetNombreCompletoConCamposVacios() {
        PacienteDTO dto = new PacienteDTO();
        dto.setPnombre("");
        dto.setSnombre("");
        dto.setPapellido("");
        dto.setSapellido("");
        
        String nombreCompleto = dto.getNombreCompleto();
        
        assertEquals("", nombreCompleto);
    }

    @Test
    void testGetNombreCompletoConEspaciosExtra() {
        PacienteDTO dto = new PacienteDTO();
        dto.setPnombre("  Ana  ");
        dto.setSnombre("  Maria  ");
        dto.setPapellido("  Garcia  ");
        dto.setSapellido("  Ruiz  ");
        
        String nombreCompleto = dto.getNombreCompleto();
        
        // Los espacios se mantienen, pero trim() elimina espacios al final
        assertTrue(nombreCompleto.contains("Ana"));
        assertTrue(nombreCompleto.contains("Maria"));
        assertTrue(nombreCompleto.contains("Garcia"));
        assertTrue(nombreCompleto.contains("Ruiz"));
    }

    @Test
    void testGetNombreCompletoConAlgunosCamposVacios() {
        PacienteDTO dto = new PacienteDTO();
        dto.setPnombre("Carlos");
        dto.setSnombre("");
        dto.setPapellido("Diaz");
        dto.setSapellido("");
        
        String nombreCompleto = dto.getNombreCompleto();
        
        assertEquals("Carlos Diaz", nombreCompleto);
    }

    @Test
    void testGetNombreCompletoConAlgunosCamposNull() {
        PacienteDTO dto = new PacienteDTO();
        dto.setPnombre("Luis");
        dto.setSnombre(null);
        dto.setPapellido("Fernandez");
        dto.setSapellido(null);
        
        String nombreCompleto = dto.getNombreCompleto();
        
        assertEquals("Luis Fernandez", nombreCompleto);
    }

    @Test
    void testNombresVacios() {
        PacienteDTO dto = new PacienteDTO();
        
        dto.setPnombre("");
        dto.setSnombre("");
        dto.setPapellido("");
        dto.setSapellido("");
        
        assertEquals("", dto.getPnombre());
        assertEquals("", dto.getSnombre());
        assertEquals("", dto.getPapellido());
        assertEquals("", dto.getSapellido());
    }

    @Test
    void testIdCero() {
        PacienteDTO dto = new PacienteDTO();
        
        dto.setId(0L);
        
        assertEquals(0L, dto.getId());
    }

    @Test
    void testIdNegativo() {
        PacienteDTO dto = new PacienteDTO();
        
        dto.setId(-1L);
        
        assertEquals(-1L, dto.getId());
    }

    @Test
    void testIdGrande() {
        PacienteDTO dto = new PacienteDTO();
        Long idGrande = 999999999L;
        
        dto.setId(idGrande);
        
        assertEquals(idGrande, dto.getId());
    }

    @Test
    void testNombresConCaracteresEspeciales() {
        PacienteDTO dto = new PacienteDTO();
        
        dto.setPnombre("José");
        dto.setSnombre("María");
        dto.setPapellido("O'Brien");
        dto.setSapellido("Núñez");
        
        assertEquals("José", dto.getPnombre());
        assertEquals("María", dto.getSnombre());
        assertEquals("O'Brien", dto.getPapellido());
        assertEquals("Núñez", dto.getSapellido());
    }

    @Test
    void testGetNombreCompletoConCaracteresEspeciales() {
        PacienteDTO dto = new PacienteDTO();
        dto.setPnombre("José");
        dto.setSnombre("María");
        dto.setPapellido("O'Brien");
        dto.setSapellido("Núñez");
        
        String nombreCompleto = dto.getNombreCompleto();
        
        assertEquals("José María O'Brien Núñez", nombreCompleto);
    }

    @Test
    void testMultiplesCambios() {
        PacienteDTO dto = new PacienteDTO();
        
        dto.setPnombre("Primer");
        assertEquals("Primer", dto.getPnombre());
        
        dto.setPnombre("Segundo");
        assertEquals("Segundo", dto.getPnombre());
        
        dto.setPnombre("Tercero");
        assertEquals("Tercero", dto.getPnombre());
    }
}
