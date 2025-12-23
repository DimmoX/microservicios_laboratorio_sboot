package com.gestion_labs.ms_gestion_labs.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class LabExamModelTest {

    @Test
    void testConstructorVacio() {
        LabExamModel model = new LabExamModel();
        
        assertNotNull(model);
        assertNull(model.getId());
        assertNull(model.getPrecio());
        assertNull(model.getVigenteDesde());
        assertNull(model.getVigenteHasta());
    }

    @Test
    void testConstructorConLabExamKey() {
        LabExamKey key = new LabExamKey(1L, 100L);
        
        LabExamModel model = new LabExamModel(key);
        
        assertNotNull(model);
        assertEquals(key, model.getId());
        assertNull(model.getPrecio());
        assertNull(model.getVigenteDesde());
        assertNull(model.getVigenteHasta());
    }

    @Test
    void testIdGetterSetter() {
        LabExamModel model = new LabExamModel();
        LabExamKey key = new LabExamKey(5L, 200L);
        
        model.setId(key);
        
        assertEquals(key, model.getId());
    }

    @Test
    void testPrecioGetterSetter() {
        LabExamModel model = new LabExamModel();
        BigDecimal precio = new BigDecimal("15750.50");
        
        model.setPrecio(precio);
        
        assertEquals(precio, model.getPrecio());
    }

    @Test
    void testVigenteDesdeeGetterSetter() {
        LabExamModel model = new LabExamModel();
        LocalDate fecha = LocalDate.of(2025, 1, 1);
        
        model.setVigenteDesde(fecha);
        
        assertEquals(fecha, model.getVigenteDesde());
    }

    @Test
    void testVigenteHastaGetterSetter() {
        LabExamModel model = new LabExamModel();
        LocalDate fecha = LocalDate.of(2025, 12, 31);
        
        model.setVigenteHasta(fecha);
        
        assertEquals(fecha, model.getVigenteHasta());
    }

    @Test
    void testSettersConValoresNull() {
        LabExamModel model = new LabExamModel(new LabExamKey(1L, 100L));
        model.setPrecio(new BigDecimal("1000.00"));
        model.setVigenteDesde(LocalDate.now());
        model.setVigenteHasta(LocalDate.now().plusDays(30));
        
        model.setId(null);
        model.setPrecio(null);
        model.setVigenteDesde(null);
        model.setVigenteHasta(null);
        
        assertNull(model.getId());
        assertNull(model.getPrecio());
        assertNull(model.getVigenteDesde());
        assertNull(model.getVigenteHasta());
    }

    @Test
    void testModeloCompleto() {
        LabExamKey key = new LabExamKey(3L, 150L);
        BigDecimal precio = new BigDecimal("25000.00");
        LocalDate vigenteDesde = LocalDate.of(2025, 1, 1);
        LocalDate vigenteHasta = LocalDate.of(2025, 12, 31);
        
        LabExamModel model = new LabExamModel();
        model.setId(key);
        model.setPrecio(precio);
        model.setVigenteDesde(vigenteDesde);
        model.setVigenteHasta(vigenteHasta);
        
        assertEquals(key, model.getId());
        assertEquals(precio, model.getPrecio());
        assertEquals(vigenteDesde, model.getVigenteDesde());
        assertEquals(vigenteHasta, model.getVigenteHasta());
    }

    @Test
    void testPrecioConDecimales() {
        LabExamModel model = new LabExamModel();
        BigDecimal precio = new BigDecimal("12345.67");
        
        model.setPrecio(precio);
        
        assertEquals(precio, model.getPrecio());
        assertEquals(0, precio.compareTo(model.getPrecio()));
    }

    @Test
    void testPrecioCero() {
        LabExamModel model = new LabExamModel();
        BigDecimal precio = BigDecimal.ZERO;
        
        model.setPrecio(precio);
        
        assertEquals(BigDecimal.ZERO, model.getPrecio());
    }

    @Test
    void testPrecioGrande() {
        LabExamModel model = new LabExamModel();
        BigDecimal precio = new BigDecimal("999999.99");
        
        model.setPrecio(precio);
        
        assertEquals(precio, model.getPrecio());
    }

    @Test
    void testFechasVigencia() {
        LabExamModel model = new LabExamModel();
        LocalDate vigenteDesde = LocalDate.of(2025, 6, 1);
        LocalDate vigenteHasta = LocalDate.of(2025, 12, 31);
        
        model.setVigenteDesde(vigenteDesde);
        model.setVigenteHasta(vigenteHasta);
        
        assertEquals(vigenteDesde, model.getVigenteDesde());
        assertEquals(vigenteHasta, model.getVigenteHasta());
        assertTrue(model.getVigenteHasta().isAfter(model.getVigenteDesde()));
    }

    @Test
    void testFechaActual() {
        LabExamModel model = new LabExamModel();
        LocalDate hoy = LocalDate.now();
        
        model.setVigenteDesde(hoy);
        model.setVigenteHasta(hoy.plusMonths(6));
        
        assertEquals(hoy, model.getVigenteDesde());
        assertEquals(hoy.plusMonths(6), model.getVigenteHasta());
    }

    @Test
    void testFechasIguales() {
        LabExamModel model = new LabExamModel();
        LocalDate fecha = LocalDate.of(2025, 7, 15);
        
        model.setVigenteDesde(fecha);
        model.setVigenteHasta(fecha);
        
        assertEquals(model.getVigenteDesde(), model.getVigenteHasta());
    }

    @Test
    void testToStringContenidoCompleto() {
        LabExamKey key = new LabExamKey(7L, 350L);
        LabExamModel model = new LabExamModel(key);
        model.setPrecio(new BigDecimal("18500.50"));
        model.setVigenteDesde(LocalDate.of(2025, 3, 1));
        model.setVigenteHasta(LocalDate.of(2025, 9, 30));
        
        String resultado = model.toString();
        
        assertNotNull(resultado);
        assertTrue(resultado.contains("LabExamModel"));
        assertTrue(resultado.contains("id="));
        assertTrue(resultado.contains("precio=18500.50"));
        assertTrue(resultado.contains("vigenteDesde=2025-03-01"));
        assertTrue(resultado.contains("vigenteHasta=2025-09-30"));
    }

    @Test
    void testToStringConValoresNull() {
        LabExamModel model = new LabExamModel();
        
        String resultado = model.toString();
        
        assertNotNull(resultado);
        assertTrue(resultado.contains("LabExamModel"));
        assertTrue(resultado.contains("id=null"));
        assertTrue(resultado.contains("precio=null"));
        assertTrue(resultado.contains("vigenteDesde=null"));
        assertTrue(resultado.contains("vigenteHasta=null"));
    }

    @Test
    void testToStringConKeyValida() {
        LabExamKey key = new LabExamKey(10L, 500L);
        LabExamModel model = new LabExamModel(key);
        
        String resultado = model.toString();
        
        assertTrue(resultado.contains("LabExamKey"));
        assertTrue(resultado.contains("10"));
        assertTrue(resultado.contains("500"));
    }

    @Test
    void testMultiplesCambiosPrecio() {
        LabExamModel model = new LabExamModel();
        
        model.setPrecio(new BigDecimal("1000.00"));
        assertEquals(new BigDecimal("1000.00"), model.getPrecio());
        
        model.setPrecio(new BigDecimal("2000.00"));
        assertEquals(new BigDecimal("2000.00"), model.getPrecio());
        
        model.setPrecio(new BigDecimal("3000.00"));
        assertEquals(new BigDecimal("3000.00"), model.getPrecio());
    }

    @Test
    void testMultiplesCambiosFechas() {
        LabExamModel model = new LabExamModel();
        
        LocalDate fecha1 = LocalDate.of(2025, 1, 1);
        model.setVigenteDesde(fecha1);
        assertEquals(fecha1, model.getVigenteDesde());
        
        LocalDate fecha2 = LocalDate.of(2025, 6, 1);
        model.setVigenteDesde(fecha2);
        assertEquals(fecha2, model.getVigenteDesde());
    }

    @Test
    void testModeloConKeyNull() {
        LabExamModel model = new LabExamModel(null);
        
        assertNull(model.getId());
    }

    @Test
    void testPrecioConDosDecimales() {
        LabExamModel model = new LabExamModel();
        BigDecimal precio = new BigDecimal("100.99");
        
        model.setPrecio(precio);
        
        assertEquals(2, model.getPrecio().scale());
    }

    @Test
    void testPrecioConOchoDigitos() {
        LabExamModel model = new LabExamModel();
        // Máximo según @Column(precision = 8, scale = 2) -> 999999.99
        BigDecimal precio = new BigDecimal("999999.99");
        
        model.setPrecio(precio);
        
        assertEquals(precio, model.getPrecio());
        assertTrue(model.getPrecio().compareTo(new BigDecimal("1000000.00")) < 0);
    }

    @Test
    void testVigenciaConUnAno() {
        LabExamModel model = new LabExamModel();
        LocalDate inicio = LocalDate.of(2025, 1, 1);
        LocalDate fin = LocalDate.of(2026, 1, 1);
        
        model.setVigenteDesde(inicio);
        model.setVigenteHasta(fin);
        
        assertEquals(365, java.time.temporal.ChronoUnit.DAYS.between(
            model.getVigenteDesde(), 
            model.getVigenteHasta()
        ));
    }

    @Test
    void testKeyConIdsGrandes() {
        LabExamKey key = new LabExamKey(999999L, 888888L);
        LabExamModel model = new LabExamModel(key);
        
        assertEquals(999999L, model.getId().getIdLaboratorio());
        assertEquals(888888L, model.getId().getIdExamen());
    }
}
