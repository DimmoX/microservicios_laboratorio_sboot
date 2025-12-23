package com.gestion_users.ms_gestion_users.model;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class PacienteModelTest {

    @Test
    void testEmptyConstructor() {
        PacienteModel paciente = new PacienteModel();
        assertNotNull(paciente);
        assertNull(paciente.getId());
        assertNull(paciente.getPnombre());
        assertNull(paciente.getSnombre());
        assertNull(paciente.getPapellido());
        assertNull(paciente.getSapellido());
        assertNull(paciente.getRut());
        assertNull(paciente.getDirId());
        assertNull(paciente.getContactoId());
        assertNull(paciente.getCreadoEn());
    }

    @Test
    void testSettersAndGetters() {
        PacienteModel paciente = new PacienteModel();
        OffsetDateTime now = OffsetDateTime.now();
        
        paciente.setId(1L);
        paciente.setPnombre("María");
        paciente.setSnombre("Isabel");
        paciente.setPapellido("López");
        paciente.setSapellido("Martínez");
        paciente.setRut("98765432-1");
        paciente.setDirId(100L);
        paciente.setContactoId(200L);
        paciente.setCreadoEn(now);

        assertEquals(1L, paciente.getId());
        assertEquals("María", paciente.getPnombre());
        assertEquals("Isabel", paciente.getSnombre());
        assertEquals("López", paciente.getPapellido());
        assertEquals("Martínez", paciente.getSapellido());
        assertEquals("98765432-1", paciente.getRut());
        assertEquals(100L, paciente.getDirId());
        assertEquals(200L, paciente.getContactoId());
        assertEquals(now, paciente.getCreadoEn());
    }

    @Test
    void testUpdatePnombre() {
        PacienteModel paciente = new PacienteModel();
        paciente.setPnombre("OldName");
        paciente.setPnombre("NewName");
        assertEquals("NewName", paciente.getPnombre());
    }

    @Test
    void testUpdateSnombre() {
        PacienteModel paciente = new PacienteModel();
        paciente.setSnombre("OldMiddle");
        paciente.setSnombre("NewMiddle");
        assertEquals("NewMiddle", paciente.getSnombre());
    }

    @Test
    void testUpdatePapellido() {
        PacienteModel paciente = new PacienteModel();
        paciente.setPapellido("OldLastName");
        paciente.setPapellido("NewLastName");
        assertEquals("NewLastName", paciente.getPapellido());
    }

    @Test
    void testUpdateSapellido() {
        PacienteModel paciente = new PacienteModel();
        paciente.setSapellido("OldSecondLastName");
        paciente.setSapellido("NewSecondLastName");
        assertEquals("NewSecondLastName", paciente.getSapellido());
    }

    @Test
    void testUpdateRut() {
        PacienteModel paciente = new PacienteModel();
        paciente.setRut("11111111-1");
        paciente.setRut("22222222-2");
        assertEquals("22222222-2", paciente.getRut());
    }

    @Test
    void testUpdateDirId() {
        PacienteModel paciente = new PacienteModel();
        paciente.setDirId(10L);
        paciente.setDirId(20L);
        assertEquals(20L, paciente.getDirId());
    }

    @Test
    void testUpdateContactoId() {
        PacienteModel paciente = new PacienteModel();
        paciente.setContactoId(30L);
        paciente.setContactoId(40L);
        assertEquals(40L, paciente.getContactoId());
    }

    @Test
    void testNullValues() {
        PacienteModel paciente = new PacienteModel();
        paciente.setId(null);
        paciente.setPnombre(null);
        paciente.setSnombre(null);
        
        assertNull(paciente.getId());
        assertNull(paciente.getPnombre());
        assertNull(paciente.getSnombre());
    }

    @Test
    void testWithoutSnombre() {
        PacienteModel paciente = new PacienteModel();
        paciente.setPnombre("Ana");
        paciente.setPapellido("Silva");
        
        assertEquals("Ana", paciente.getPnombre());
        assertNull(paciente.getSnombre());
        assertEquals("Silva", paciente.getPapellido());
    }

    @Test
    void testCreadoEnTimestamp() {
        PacienteModel paciente = new PacienteModel();
        OffsetDateTime timestamp = OffsetDateTime.now();
        paciente.setCreadoEn(timestamp);
        
        assertEquals(timestamp, paciente.getCreadoEn());
    }

    @Test
    void testCompletePaciente() {
        PacienteModel paciente = new PacienteModel();
        OffsetDateTime timestamp = OffsetDateTime.now();
        
        paciente.setId(999L);
        paciente.setPnombre("Complete");
        paciente.setSnombre("Middle");
        paciente.setPapellido("FirstLast");
        paciente.setSapellido("SecondLast");
        paciente.setRut("88888888-8");
        paciente.setDirId(500L);
        paciente.setContactoId(600L);
        paciente.setCreadoEn(timestamp);

        assertEquals(999L, paciente.getId());
        assertEquals("Complete", paciente.getPnombre());
        assertEquals("Middle", paciente.getSnombre());
        assertEquals("FirstLast", paciente.getPapellido());
        assertEquals("SecondLast", paciente.getSapellido());
        assertEquals("88888888-8", paciente.getRut());
        assertEquals(500L, paciente.getDirId());
        assertEquals(600L, paciente.getContactoId());
        assertEquals(timestamp, paciente.getCreadoEn());
    }
}
