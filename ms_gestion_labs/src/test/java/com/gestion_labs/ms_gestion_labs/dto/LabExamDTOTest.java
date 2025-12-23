package com.gestion_labs.ms_gestion_labs.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class LabExamDTOTest {

    @Test
    void testConstructorVacio() {
        LabExamDTO dto = new LabExamDTO();
        assertNotNull(dto);
    }

    @Test
    void testIdLaboratorioGetterSetter() {
        LabExamDTO dto = new LabExamDTO();
        Long idLaboratorio = 5L;
        
        dto.setIdLaboratorio(idLaboratorio);
        
        assertEquals(idLaboratorio, dto.getIdLaboratorio());
    }

    @Test
    void testIdExamenGetterSetter() {
        LabExamDTO dto = new LabExamDTO();
        Long idExamen = 20L;
        
        dto.setIdExamen(idExamen);
        
        assertEquals(idExamen, dto.getIdExamen());
    }

    @Test
    void testPrecioGetterSetter() {
        LabExamDTO dto = new LabExamDTO();
        BigDecimal precio = new BigDecimal("25000.00");
        
        dto.setPrecio(precio);
        
        assertEquals(precio, dto.getPrecio());
    }

    @Test
    void testVigenteDesdeGetterSetter() {
        LabExamDTO dto = new LabExamDTO();
        LocalDate fecha = LocalDate.of(2025, 1, 1);
        
        dto.setVigenteDesde(fecha);
        
        assertEquals(fecha, dto.getVigenteDesde());
    }

    @Test
    void testVigenteHastaGetterSetter() {
        LabExamDTO dto = new LabExamDTO();
        LocalDate fecha = LocalDate.of(2025, 12, 31);
        
        dto.setVigenteHasta(fecha);
        
        assertEquals(fecha, dto.getVigenteHasta());
    }

    @Test
    void testNombreLabGetterSetter() {
        LabExamDTO dto = new LabExamDTO();
        String nombreLab = "Laboratorio Central";
        
        dto.setNombreLab(nombreLab);
        
        assertEquals(nombreLab, dto.getNombreLab());
    }

    @Test
    void testNombreExamenGetterSetter() {
        LabExamDTO dto = new LabExamDTO();
        String nombreExamen = "Hemograma Completo";
        
        dto.setNombreExamen(nombreExamen);
        
        assertEquals(nombreExamen, dto.getNombreExamen());
    }

    @Test
    void testSettersConValoresNull() {
        LabExamDTO dto = new LabExamDTO();
        
        dto.setIdLaboratorio(null);
        dto.setIdExamen(null);
        dto.setPrecio(null);
        dto.setVigenteDesde(null);
        dto.setVigenteHasta(null);
        dto.setNombreLab(null);
        dto.setNombreExamen(null);
        
        assertNull(dto.getIdLaboratorio());
        assertNull(dto.getIdExamen());
        assertNull(dto.getPrecio());
        assertNull(dto.getVigenteDesde());
        assertNull(dto.getVigenteHasta());
        assertNull(dto.getNombreLab());
        assertNull(dto.getNombreExamen());
    }

    @Test
    void testTodosLosCamposCompletos() {
        LabExamDTO dto = new LabExamDTO();
        LocalDate fechaDesde = LocalDate.of(2025, 1, 1);
        LocalDate fechaHasta = LocalDate.of(2025, 12, 31);
        BigDecimal precio = new BigDecimal("35000.50");
        
        dto.setIdLaboratorio(3L);
        dto.setIdExamen(15L);
        dto.setPrecio(precio);
        dto.setVigenteDesde(fechaDesde);
        dto.setVigenteHasta(fechaHasta);
        dto.setNombreLab("Lab San José");
        dto.setNombreExamen("Perfil Lipídico");
        
        assertEquals(3L, dto.getIdLaboratorio());
        assertEquals(15L, dto.getIdExamen());
        assertEquals(precio, dto.getPrecio());
        assertEquals(fechaDesde, dto.getVigenteDesde());
        assertEquals(fechaHasta, dto.getVigenteHasta());
        assertEquals("Lab San José", dto.getNombreLab());
        assertEquals("Perfil Lipídico", dto.getNombreExamen());
    }

    @Test
    void testPrecioConDecimales() {
        LabExamDTO dto = new LabExamDTO();
        BigDecimal precio = new BigDecimal("15750.99");
        
        dto.setPrecio(precio);
        
        assertEquals(precio, dto.getPrecio());
    }

    @Test
    void testPrecioCero() {
        LabExamDTO dto = new LabExamDTO();
        BigDecimal precioCero = BigDecimal.ZERO;
        
        dto.setPrecio(precioCero);
        
        assertEquals(BigDecimal.ZERO, dto.getPrecio());
    }

    @Test
    void testFechasVigencia() {
        LabExamDTO dto = new LabExamDTO();
        LocalDate desde = LocalDate.of(2025, 6, 1);
        LocalDate hasta = LocalDate.of(2025, 6, 30);
        
        dto.setVigenteDesde(desde);
        dto.setVigenteHasta(hasta);
        
        assertTrue(dto.getVigenteHasta().isAfter(dto.getVigenteDesde()));
    }

    @Test
    void testNombresVacios() {
        LabExamDTO dto = new LabExamDTO();
        
        dto.setNombreLab("");
        dto.setNombreExamen("");
        
        assertEquals("", dto.getNombreLab());
        assertEquals("", dto.getNombreExamen());
    }

    @Test
    void testFechaActual() {
        LabExamDTO dto = new LabExamDTO();
        LocalDate hoy = LocalDate.now();
        
        dto.setVigenteDesde(hoy);
        
        assertEquals(hoy, dto.getVigenteDesde());
    }

    @Test
    void testPrecioGrande() {
        LabExamDTO dto = new LabExamDTO();
        BigDecimal precioGrande = new BigDecimal("999999.99");
        
        dto.setPrecio(precioGrande);
        
        assertEquals(precioGrande, dto.getPrecio());
    }

    @Test
    void testIdsGrandes() {
        LabExamDTO dto = new LabExamDTO();
        Long idLabGrande = 1000000L;
        Long idExamenGrande = 5000000L;
        
        dto.setIdLaboratorio(idLabGrande);
        dto.setIdExamen(idExamenGrande);
        
        assertEquals(idLabGrande, dto.getIdLaboratorio());
        assertEquals(idExamenGrande, dto.getIdExamen());
    }
}
