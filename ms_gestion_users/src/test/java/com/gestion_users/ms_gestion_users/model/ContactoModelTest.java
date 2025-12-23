package com.gestion_users.ms_gestion_users.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ContactoModelTest {

    @Test
    void testEmptyConstructor() {
        ContactoModel contacto = new ContactoModel();
        assertNotNull(contacto);
        assertNull(contacto.getId());
        assertNull(contacto.getFono1());
        assertNull(contacto.getFono2());
        assertNull(contacto.getEmail());
    }

    @Test
    void testParameterizedConstructor() {
        ContactoModel contacto = new ContactoModel("+56912345678", "+56987654321", "test@email.com");
        
        assertNull(contacto.getId());
        assertEquals("+56912345678", contacto.getFono1());
        assertEquals("+56987654321", contacto.getFono2());
        assertEquals("test@email.com", contacto.getEmail());
    }

    @Test
    void testSettersAndGetters() {
        ContactoModel contacto = new ContactoModel();
        
        contacto.setId(1L);
        contacto.setFono1("+56912345678");
        contacto.setFono2("+56987654321");
        contacto.setEmail("usuario@test.com");

        assertEquals(1L, contacto.getId());
        assertEquals("+56912345678", contacto.getFono1());
        assertEquals("+56987654321", contacto.getFono2());
        assertEquals("usuario@test.com", contacto.getEmail());
    }

    @Test
    void testUpdateFono1() {
        ContactoModel contacto = new ContactoModel("+56900000000", null, null);
        contacto.setFono1("+56911111111");
        assertEquals("+56911111111", contacto.getFono1());
    }

    @Test
    void testUpdateFono2() {
        ContactoModel contacto = new ContactoModel(null, "+56900000000", null);
        contacto.setFono2("+56922222222");
        assertEquals("+56922222222", contacto.getFono2());
    }

    @Test
    void testUpdateEmail() {
        ContactoModel contacto = new ContactoModel(null, null, "old@email.com");
        contacto.setEmail("new@email.com");
        assertEquals("new@email.com", contacto.getEmail());
    }

    @Test
    void testNullValues() {
        ContactoModel contacto = new ContactoModel(null, null, null);
        assertNull(contacto.getFono1());
        assertNull(contacto.getFono2());
        assertNull(contacto.getEmail());
    }

    @Test
    void testWithOnlyFono1() {
        ContactoModel contacto = new ContactoModel("+56912345678", null, null);
        assertEquals("+56912345678", contacto.getFono1());
        assertNull(contacto.getFono2());
        assertNull(contacto.getEmail());
    }

    @Test
    void testWithAllFields() {
        ContactoModel contacto = new ContactoModel();
        contacto.setId(100L);
        contacto.setFono1("+56912345678");
        contacto.setFono2("+56987654321");
        contacto.setEmail("complete@test.com");

        assertEquals(100L, contacto.getId());
        assertEquals("+56912345678", contacto.getFono1());
        assertEquals("+56987654321", contacto.getFono2());
        assertEquals("complete@test.com", contacto.getEmail());
    }
}
