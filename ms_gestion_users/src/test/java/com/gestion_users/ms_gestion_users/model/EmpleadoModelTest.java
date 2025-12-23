package com.gestion_users.ms_gestion_users.model;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class EmpleadoModelTest {

    @Test
    void testEmptyConstructor() {
        EmpleadoModel empleado = new EmpleadoModel();
        assertNotNull(empleado);
        assertNull(empleado.getId());
        assertNull(empleado.getPnombre());
        assertNull(empleado.getSnombre());
        assertNull(empleado.getPapellido());
        assertNull(empleado.getSapellido());
        assertNull(empleado.getRut());
        assertNull(empleado.getCargo());
        assertNull(empleado.getDirId());
        assertNull(empleado.getContactoId());
        assertNull(empleado.getCreadoEn());
    }

    @Test
    void testSettersAndGetters() {
        EmpleadoModel empleado = new EmpleadoModel();
        OffsetDateTime now = OffsetDateTime.now();
        
        empleado.setId(1L);
        empleado.setPnombre("Juan");
        empleado.setSnombre("Carlos");
        empleado.setPapellido("Pérez");
        empleado.setSapellido("González");
        empleado.setRut("12345678-9");
        empleado.setCargo("Técnico de Laboratorio");
        empleado.setDirId(100L);
        empleado.setContactoId(200L);
        empleado.setCreadoEn(now);

        assertEquals(1L, empleado.getId());
        assertEquals("Juan", empleado.getPnombre());
        assertEquals("Carlos", empleado.getSnombre());
        assertEquals("Pérez", empleado.getPapellido());
        assertEquals("González", empleado.getSapellido());
        assertEquals("12345678-9", empleado.getRut());
        assertEquals("Técnico de Laboratorio", empleado.getCargo());
        assertEquals(100L, empleado.getDirId());
        assertEquals(200L, empleado.getContactoId());
        assertEquals(now, empleado.getCreadoEn());
    }

    @Test
    void testUpdatePnombre() {
        EmpleadoModel empleado = new EmpleadoModel();
        empleado.setPnombre("OldName");
        empleado.setPnombre("NewName");
        assertEquals("NewName", empleado.getPnombre());
    }

    @Test
    void testUpdateSnombre() {
        EmpleadoModel empleado = new EmpleadoModel();
        empleado.setSnombre("OldMiddle");
        empleado.setSnombre("NewMiddle");
        assertEquals("NewMiddle", empleado.getSnombre());
    }

    @Test
    void testUpdatePapellido() {
        EmpleadoModel empleado = new EmpleadoModel();
        empleado.setPapellido("OldLastName");
        empleado.setPapellido("NewLastName");
        assertEquals("NewLastName", empleado.getPapellido());
    }

    @Test
    void testUpdateSapellido() {
        EmpleadoModel empleado = new EmpleadoModel();
        empleado.setSapellido("OldSecondLastName");
        empleado.setSapellido("NewSecondLastName");
        assertEquals("NewSecondLastName", empleado.getSapellido());
    }

    @Test
    void testUpdateRut() {
        EmpleadoModel empleado = new EmpleadoModel();
        empleado.setRut("11111111-1");
        empleado.setRut("22222222-2");
        assertEquals("22222222-2", empleado.getRut());
    }

    @Test
    void testUpdateCargo() {
        EmpleadoModel empleado = new EmpleadoModel();
        empleado.setCargo("Analista");
        empleado.setCargo("Supervisor");
        assertEquals("Supervisor", empleado.getCargo());
    }

    @Test
    void testUpdateDirId() {
        EmpleadoModel empleado = new EmpleadoModel();
        empleado.setDirId(10L);
        empleado.setDirId(20L);
        assertEquals(20L, empleado.getDirId());
    }

    @Test
    void testUpdateContactoId() {
        EmpleadoModel empleado = new EmpleadoModel();
        empleado.setContactoId(30L);
        empleado.setContactoId(40L);
        assertEquals(40L, empleado.getContactoId());
    }

    @Test
    void testNullValues() {
        EmpleadoModel empleado = new EmpleadoModel();
        empleado.setId(null);
        empleado.setPnombre(null);
        empleado.setSnombre(null);
        
        assertNull(empleado.getId());
        assertNull(empleado.getPnombre());
        assertNull(empleado.getSnombre());
    }

    @Test
    void testWithoutSnombre() {
        EmpleadoModel empleado = new EmpleadoModel();
        empleado.setPnombre("Juan");
        empleado.setPapellido("Pérez");
        
        assertEquals("Juan", empleado.getPnombre());
        assertNull(empleado.getSnombre());
        assertEquals("Pérez", empleado.getPapellido());
    }

    @Test
    void testCreadoEnTimestamp() {
        EmpleadoModel empleado = new EmpleadoModel();
        OffsetDateTime timestamp = OffsetDateTime.now();
        empleado.setCreadoEn(timestamp);
        
        assertEquals(timestamp, empleado.getCreadoEn());
    }

    @Test
    void testCompleteEmpleado() {
        EmpleadoModel empleado = new EmpleadoModel();
        OffsetDateTime timestamp = OffsetDateTime.now();
        
        empleado.setId(999L);
        empleado.setPnombre("Complete");
        empleado.setSnombre("Middle");
        empleado.setPapellido("FirstLast");
        empleado.setSapellido("SecondLast");
        empleado.setRut("99999999-9");
        empleado.setCargo("Director");
        empleado.setDirId(500L);
        empleado.setContactoId(600L);
        empleado.setCreadoEn(timestamp);

        assertEquals(999L, empleado.getId());
        assertEquals("Complete", empleado.getPnombre());
        assertEquals("Middle", empleado.getSnombre());
        assertEquals("FirstLast", empleado.getPapellido());
        assertEquals("SecondLast", empleado.getSapellido());
        assertEquals("99999999-9", empleado.getRut());
        assertEquals("Director", empleado.getCargo());
        assertEquals(500L, empleado.getDirId());
        assertEquals(600L, empleado.getContactoId());
        assertEquals(timestamp, empleado.getCreadoEn());
    }
}
