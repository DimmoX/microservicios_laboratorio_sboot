package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class RegistroPacienteRequestTest {

    @Test
    void testConstructorVacio() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        
        assertNotNull(request);
        assertNull(request.getPnombre());
        assertNull(request.getSnombre());
        assertNull(request.getPapellido());
        assertNull(request.getSapellido());
        assertNull(request.getRut());
        assertNull(request.getContacto());
        assertNull(request.getDireccion());
        assertNull(request.getPassword());
    }

    @Test
    void testSettersYGetters() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        ContactoDTO contacto = new ContactoDTO("+56987654321", null, "paciente@email.com");
        DireccionDTO direccion = new DireccionDTO("Calle Los Álamos", 789, "Santiago", "La Reina", "Metropolitana");
        
        request.setPnombre("Claudia");
        request.setSnombre("Isabel");
        request.setPapellido("Núñez");
        request.setSapellido("Vega");
        request.setRut("18888888-8");
        request.setContacto(contacto);
        request.setDireccion(direccion);
        request.setPassword("MyPass2025");
        
        assertEquals("Claudia", request.getPnombre());
        assertEquals("Isabel", request.getSnombre());
        assertEquals("Núñez", request.getPapellido());
        assertEquals("Vega", request.getSapellido());
        assertEquals("18888888-8", request.getRut());
        assertEquals(contacto, request.getContacto());
        assertEquals(direccion, request.getDireccion());
        assertEquals("MyPass2025", request.getPassword());
    }

    @Test
    void testRegistroCompletoPaciente() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        
        request.setPnombre("Roberto");
        request.setSnombre("Antonio");
        request.setPapellido("Gutiérrez");
        request.setSapellido("Pinto");
        request.setRut("19999999-9");
        
        ContactoDTO contacto = new ContactoDTO("+56911223344", "+56922334455", "roberto@mail.com");
        request.setContacto(contacto);
        
        DireccionDTO direccion = new DireccionDTO("Pasaje Norte", 101, "Concepción", "Talcahuano", "Bío Bío");
        request.setDireccion(direccion);
        
        request.setPassword("RobertoPass123!");
        
        assertNotNull(request.getContacto());
        assertEquals("roberto@mail.com", request.getContacto().getEmail());
        assertNotNull(request.getDireccion());
        assertEquals("Concepción", request.getDireccion().getCiudad());
        assertEquals("RobertoPass123!", request.getPassword());
    }

    @Test
    void testActualizarRut() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setRut("11111111-1");
        
        request.setRut("22222222-2");
        
        assertEquals("22222222-2", request.getRut());
    }

    @Test
    void testActualizarPassword() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setPassword("OldPassword");
        
        request.setPassword("NewSecurePassword");
        
        assertEquals("NewSecurePassword", request.getPassword());
    }

    @Test
    void testContactoNulo() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setPnombre("Daniel");
        request.setPapellido("Muñoz");
        
        assertNull(request.getContacto());
    }

    @Test
    void testDireccionNula() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setPnombre("Sofía");
        request.setPapellido("Bravo");
        
        assertNull(request.getDireccion());
    }

    @Test
    void testPacienteSinSnombre() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setPnombre("Valentina");
        request.setPapellido("Cortés");
        request.setRut("12000000-0");
        
        assertEquals("Valentina", request.getPnombre());
        assertNull(request.getSnombre());
        assertEquals("Cortés", request.getPapellido());
    }

    @Test
    void testPacienteSinSapellido() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setPapellido("Rojas");
        
        assertEquals("Rojas", request.getPapellido());
        assertNull(request.getSapellido());
    }

    @Test
    void testTodosLosCamposNulos() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        
        assertNull(request.getPnombre());
        assertNull(request.getSnombre());
        assertNull(request.getPapellido());
        assertNull(request.getSapellido());
        assertNull(request.getRut());
        assertNull(request.getContacto());
        assertNull(request.getDireccion());
        assertNull(request.getPassword());
    }

    @Test
    void testContactoConUnSoloTelefono() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        ContactoDTO contacto = new ContactoDTO("+56912345678", null, "single@mail.com");
        request.setContacto(contacto);
        
        assertNotNull(request.getContacto());
        assertEquals("+56912345678", request.getContacto().getFono1());
        assertNull(request.getContacto().getFono2());
    }
}
