package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ContactoDTOTest {

    @Test
    void testConstructorVacio() {
        ContactoDTO dto = new ContactoDTO();
        
        assertNotNull(dto);
        assertNull(dto.getFono1());
        assertNull(dto.getFono2());
        assertNull(dto.getEmail());
    }

    @Test
    void testConstructorConParametros() {
        ContactoDTO dto = new ContactoDTO("+56912345678", "+56987654321", "test@example.com");
        
        assertEquals("+56912345678", dto.getFono1());
        assertEquals("+56987654321", dto.getFono2());
        assertEquals("test@example.com", dto.getEmail());
    }

    @Test
    void testSettersYGetters() {
        ContactoDTO dto = new ContactoDTO();
        
        dto.setFono1("+56911111111");
        dto.setFono2("+56922222222");
        dto.setEmail("contacto@test.cl");
        
        assertEquals("+56911111111", dto.getFono1());
        assertEquals("+56922222222", dto.getFono2());
        assertEquals("contacto@test.cl", dto.getEmail());
    }

    @Test
    void testActualizarFono1() {
        ContactoDTO dto = new ContactoDTO("+56912345678", null, "old@test.com");
        
        dto.setFono1("+56999999999");
        
        assertEquals("+56999999999", dto.getFono1());
        assertNull(dto.getFono2());
        assertEquals("old@test.com", dto.getEmail());
    }

    @Test
    void testActualizarEmail() {
        ContactoDTO dto = new ContactoDTO("+56912345678", "+56987654321", "old@example.com");
        
        dto.setEmail("new@example.com");
        
        assertEquals("new@example.com", dto.getEmail());
    }

    @Test
    void testContactoConFono2Nulo() {
        ContactoDTO dto = new ContactoDTO("+56912345678", null, "test@example.com");
        
        assertEquals("+56912345678", dto.getFono1());
        assertNull(dto.getFono2());
        assertEquals("test@example.com", dto.getEmail());
    }

    @Test
    void testTodosLosCamposNulos() {
        ContactoDTO dto = new ContactoDTO(null, null, null);
        
        assertNull(dto.getFono1());
        assertNull(dto.getFono2());
        assertNull(dto.getEmail());
    }
}
