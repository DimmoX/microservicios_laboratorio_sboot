package com.gestion_users.ms_gestion_users.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class DireccionModelTest {

    @Test
    void testEmptyConstructor() {
        DireccionModel direccion = new DireccionModel();
        assertNotNull(direccion);
        assertNull(direccion.getId());
        assertNull(direccion.getCalle());
        assertNull(direccion.getNumero());
        assertNull(direccion.getCiudad());
        assertNull(direccion.getComuna());
        assertNull(direccion.getRegion());
    }

    @Test
    void testParameterizedConstructor() {
        DireccionModel direccion = new DireccionModel("Av. Principal", 123, "Santiago", "Las Condes", "Metropolitana");
        
        assertNull(direccion.getId());
        assertEquals("Av. Principal", direccion.getCalle());
        assertEquals(123, direccion.getNumero());
        assertEquals("Santiago", direccion.getCiudad());
        assertEquals("Las Condes", direccion.getComuna());
        assertEquals("Metropolitana", direccion.getRegion());
    }

    @Test
    void testSettersAndGetters() {
        DireccionModel direccion = new DireccionModel();
        
        direccion.setId(1L);
        direccion.setCalle("Calle Test");
        direccion.setNumero(456);
        direccion.setCiudad("Valparaíso");
        direccion.setComuna("Viña del Mar");
        direccion.setRegion("Valparaíso");

        assertEquals(1L, direccion.getId());
        assertEquals("Calle Test", direccion.getCalle());
        assertEquals(456, direccion.getNumero());
        assertEquals("Valparaíso", direccion.getCiudad());
        assertEquals("Viña del Mar", direccion.getComuna());
        assertEquals("Valparaíso", direccion.getRegion());
    }

    @Test
    void testUpdateCalle() {
        DireccionModel direccion = new DireccionModel("Old Street", null, null, null, null);
        direccion.setCalle("New Street");
        assertEquals("New Street", direccion.getCalle());
    }

    @Test
    void testUpdateNumero() {
        DireccionModel direccion = new DireccionModel(null, 100, null, null, null);
        direccion.setNumero(200);
        assertEquals(200, direccion.getNumero());
    }

    @Test
    void testUpdateCiudad() {
        DireccionModel direccion = new DireccionModel(null, null, "Old City", null, null);
        direccion.setCiudad("New City");
        assertEquals("New City", direccion.getCiudad());
    }

    @Test
    void testUpdateComuna() {
        DireccionModel direccion = new DireccionModel(null, null, null, "Old Comuna", null);
        direccion.setComuna("New Comuna");
        assertEquals("New Comuna", direccion.getComuna());
    }

    @Test
    void testUpdateRegion() {
        DireccionModel direccion = new DireccionModel(null, null, null, null, "Old Region");
        direccion.setRegion("New Region");
        assertEquals("New Region", direccion.getRegion());
    }

    @Test
    void testNullValues() {
        DireccionModel direccion = new DireccionModel(null, null, null, null, null);
        assertNull(direccion.getCalle());
        assertNull(direccion.getNumero());
        assertNull(direccion.getCiudad());
        assertNull(direccion.getComuna());
        assertNull(direccion.getRegion());
    }

    @Test
    void testWithAllFields() {
        DireccionModel direccion = new DireccionModel();
        direccion.setId(999L);
        direccion.setCalle("Complete Street");
        direccion.setNumero(789);
        direccion.setCiudad("Complete City");
        direccion.setComuna("Complete Comuna");
        direccion.setRegion("Complete Region");

        assertEquals(999L, direccion.getId());
        assertEquals("Complete Street", direccion.getCalle());
        assertEquals(789, direccion.getNumero());
        assertEquals("Complete City", direccion.getCiudad());
        assertEquals("Complete Comuna", direccion.getComuna());
        assertEquals("Complete Region", direccion.getRegion());
    }
}
