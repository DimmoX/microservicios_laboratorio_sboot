package com.gestion_labs.ms_gestion_labs.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class AgendaExamenDTOTest {

    @Test
    void testConstructorVacio() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        assertNotNull(dto);
    }

    @Test
    void testIdGetterSetter() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        Long id = 1L;
        
        dto.setId(id);
        
        assertEquals(id, dto.getId());
    }

    @Test
    void testPacienteIdGetterSetter() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        Long pacienteId = 100L;
        
        dto.setPacienteId(pacienteId);
        
        assertEquals(pacienteId, dto.getPacienteId());
    }

    @Test
    void testPacienteNombreGetterSetter() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        String nombre = "Juan Pérez";
        
        dto.setPacienteNombre(nombre);
        
        assertEquals(nombre, dto.getPacienteNombre());
    }

    @Test
    void testLabIdGetterSetter() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        Long labId = 5L;
        
        dto.setLabId(labId);
        
        assertEquals(labId, dto.getLabId());
    }

    @Test
    void testLaboratorioNombreGetterSetter() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        String nombre = "Laboratorio Central";
        
        dto.setLaboratorioNombre(nombre);
        
        assertEquals(nombre, dto.getLaboratorioNombre());
    }

    @Test
    void testExamenIdGetterSetter() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        Long examenId = 20L;
        
        dto.setExamenId(examenId);
        
        assertEquals(examenId, dto.getExamenId());
    }

    @Test
    void testExamenNombreGetterSetter() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        String nombre = "Hemograma Completo";
        
        dto.setExamenNombre(nombre);
        
        assertEquals(nombre, dto.getExamenNombre());
    }

    @Test
    void testEmpleadoIdGetterSetter() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        Long empleadoId = 50L;
        
        dto.setEmpleadoId(empleadoId);
        
        assertEquals(empleadoId, dto.getEmpleadoId());
    }

    @Test
    void testFechaHoraGetterSetter() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        LocalDateTime fechaHora = LocalDateTime.of(2025, 12, 22, 14, 30);
        
        dto.setFechaHora(fechaHora);
        
        assertEquals(fechaHora, dto.getFechaHora());
    }

    @Test
    void testEstadoGetterSetter() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        String estado = "PROGRAMADA";
        
        dto.setEstado(estado);
        
        assertEquals(estado, dto.getEstado());
    }

    @Test
    void testCreadoEnGetterSetter() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        LocalDateTime creadoEn = LocalDateTime.now();
        
        dto.setCreadoEn(creadoEn);
        
        assertEquals(creadoEn, dto.getCreadoEn());
    }

    @Test
    void testPrecioGetterSetter() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        BigDecimal precio = new BigDecimal("25000.50");
        
        dto.setPrecio(precio);
        
        // El getter retorna Integer (convierte BigDecimal a int)
        assertEquals(25000, dto.getPrecio());
    }

    @Test
    void testSettersConValoresNull() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        
        dto.setId(null);
        dto.setPacienteId(null);
        dto.setPacienteNombre(null);
        dto.setLabId(null);
        dto.setLaboratorioNombre(null);
        dto.setExamenId(null);
        dto.setExamenNombre(null);
        dto.setEmpleadoId(null);
        dto.setFechaHora(null);
        dto.setEstado(null);
        dto.setCreadoEn(null);
        dto.setPrecio(null);
        
        assertNull(dto.getId());
        assertNull(dto.getPacienteId());
        assertNull(dto.getPacienteNombre());
        assertNull(dto.getLabId());
        assertNull(dto.getLaboratorioNombre());
        assertNull(dto.getExamenId());
        assertNull(dto.getExamenNombre());
        assertNull(dto.getEmpleadoId());
        assertNull(dto.getFechaHora());
        assertNull(dto.getEstado());
        assertNull(dto.getCreadoEn());
        assertNull(dto.getPrecio());
    }

    @Test
    void testTodosLosCamposEnCadena() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        LocalDateTime ahora = LocalDateTime.now();
        BigDecimal precio = new BigDecimal("35000.00");
        
        dto.setId(10L);
        dto.setPacienteId(200L);
        dto.setPacienteNombre("María González");
        dto.setLabId(3L);
        dto.setLaboratorioNombre("Lab San José");
        dto.setExamenId(15L);
        dto.setExamenNombre("Perfil Lipídico");
        dto.setEmpleadoId(25L);
        dto.setFechaHora(ahora);
        dto.setEstado("CONFIRMADA");
        dto.setCreadoEn(ahora);
        dto.setPrecio(precio);
        
        assertEquals(10L, dto.getId());
        assertEquals(200L, dto.getPacienteId());
        assertEquals("María González", dto.getPacienteNombre());
        assertEquals(3L, dto.getLabId());
        assertEquals("Lab San José", dto.getLaboratorioNombre());
        assertEquals(15L, dto.getExamenId());
        assertEquals("Perfil Lipídico", dto.getExamenNombre());
        assertEquals(25L, dto.getEmpleadoId());
        assertEquals(ahora, dto.getFechaHora());
        assertEquals("CONFIRMADA", dto.getEstado());
        assertEquals(ahora, dto.getCreadoEn());
        assertEquals(35000, dto.getPrecio()); // getPrecio retorna Integer
    }

    @Test
    void testPrecioConDecimales() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        BigDecimal precio = new BigDecimal("15750.99");
        
        dto.setPrecio(precio);
        
        // getPrecio trunca los decimales y retorna Integer
        assertEquals(15750, dto.getPrecio());
    }

    @Test
    void testPrecioCero() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        BigDecimal precioCero = BigDecimal.ZERO;
        
        dto.setPrecio(precioCero);
        
        // getPrecio retorna Integer
        assertEquals(0, dto.getPrecio());
    }

    @Test
    void testEstadosValidos() {
        AgendaExamenDTO dto = new AgendaExamenDTO();
        String[] estadosValidos = {"PROGRAMADA", "CONFIRMADA", "CANCELADA", "COMPLETADA"};
        
        for (String estado : estadosValidos) {
            dto.setEstado(estado);
            assertEquals(estado, dto.getEstado());
        }
    }
}
