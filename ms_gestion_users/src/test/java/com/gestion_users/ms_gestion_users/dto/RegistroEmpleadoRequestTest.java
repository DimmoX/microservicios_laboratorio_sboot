package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class RegistroEmpleadoRequestTest {

    @Test
    void testConstructorVacio() {
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        
        assertNotNull(request);
        assertNull(request.getPnombre());
        assertNull(request.getSnombre());
        assertNull(request.getPapellido());
        assertNull(request.getSapellido());
        assertNull(request.getRut());
        assertNull(request.getCargo());
        assertNull(request.getContacto());
        assertNull(request.getDireccion());
        assertNull(request.getPassword());
    }

    @Test
    void testSettersYGetters() {
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        ContactoDTO contacto = new ContactoDTO("+56912345678", null, "empleado@lab.com");
        DireccionDTO direccion = new DireccionDTO("Av. Principal", 123, "Santiago", "Centro", "Metropolitana");
        
        request.setPnombre("Luis");
        request.setSnombre("Alberto");
        request.setPapellido("Morales");
        request.setSapellido("Castro");
        request.setRut("15555555-5");
        request.setCargo("Analista");
        request.setContacto(contacto);
        request.setDireccion(direccion);
        request.setPassword("SecurePass123");
        
        assertEquals("Luis", request.getPnombre());
        assertEquals("Alberto", request.getSnombre());
        assertEquals("Morales", request.getPapellido());
        assertEquals("Castro", request.getSapellido());
        assertEquals("15555555-5", request.getRut());
        assertEquals("Analista", request.getCargo());
        assertEquals(contacto, request.getContacto());
        assertEquals(direccion, request.getDireccion());
        assertEquals("SecurePass123", request.getPassword());
    }

    @Test
    void testRegistroCompletoEmpleado() {
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        
        request.setPnombre("María");
        request.setSnombre("José");
        request.setPapellido("Fernández");
        request.setSapellido("López");
        request.setRut("16666666-6");
        request.setCargo("Técnico Senior");
        
        ContactoDTO contacto = new ContactoDTO("+56987654321", "+56912345678", "maria@lab.com");
        request.setContacto(contacto);
        
        DireccionDTO direccion = new DireccionDTO("Calle Ficticia", 456, "Valparaíso", "Viña del Mar", "Valparaíso");
        request.setDireccion(direccion);
        
        request.setPassword("Password2025!");
        
        assertNotNull(request.getContacto());
        assertEquals("maria@lab.com", request.getContacto().getEmail());
        assertNotNull(request.getDireccion());
        assertEquals("Valparaíso", request.getDireccion().getCiudad());
        assertEquals("Password2025!", request.getPassword());
    }

    @Test
    void testActualizarCargo() {
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        request.setCargo("Junior");
        
        request.setCargo("Senior");
        
        assertEquals("Senior", request.getCargo());
    }

    @Test
    void testActualizarPassword() {
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        request.setPassword("OldPass123");
        
        request.setPassword("NewPass456");
        
        assertEquals("NewPass456", request.getPassword());
    }

    @Test
    void testContactoNulo() {
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        request.setPnombre("Carlos");
        request.setPapellido("Díaz");
        
        assertNull(request.getContacto());
    }

    @Test
    void testDireccionNula() {
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        request.setPnombre("Pedro");
        request.setPapellido("Soto");
        
        assertNull(request.getDireccion());
    }

    @Test
    void testEmpleadoSinSnombre() {
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        request.setPnombre("Jorge");
        request.setPapellido("Ramírez");
        request.setRut("17777777-7");
        
        assertEquals("Jorge", request.getPnombre());
        assertNull(request.getSnombre());
        assertEquals("Ramírez", request.getPapellido());
    }

    @Test
    void testEmpleadoSinSapellido() {
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        request.setPapellido("Vargas");
        
        assertEquals("Vargas", request.getPapellido());
        assertNull(request.getSapellido());
    }

    @Test
    void testTodosLosCamposNulos() {
        RegistroEmpleadoRequest request = new RegistroEmpleadoRequest();
        
        assertNull(request.getPnombre());
        assertNull(request.getSnombre());
        assertNull(request.getPapellido());
        assertNull(request.getSapellido());
        assertNull(request.getRut());
        assertNull(request.getCargo());
        assertNull(request.getContacto());
        assertNull(request.getDireccion());
        assertNull(request.getPassword());
    }
}
