package com.gestion_users.ms_gestion_users.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class HashResponseTest {

    @Test
    void testConstructorVacio() {
        HashResponse response = new HashResponse();
        
        assertNotNull(response);
        assertNull(response.getPassword());
        assertNull(response.getHash());
    }

    @Test
    void testConstructorConParametros() {
        HashResponse response = new HashResponse("myPassword", "$2a$10$abcdefghijklmnopqrstuv");
        
        assertEquals("myPassword", response.getPassword());
        assertEquals("$2a$10$abcdefghijklmnopqrstuv", response.getHash());
    }

    @Test
    void testSettersYGetters() {
        HashResponse response = new HashResponse();
        
        response.setPassword("testPassword");
        response.setHash("$2a$10$hashedValue123");
        
        assertEquals("testPassword", response.getPassword());
        assertEquals("$2a$10$hashedValue123", response.getHash());
    }

    @Test
    void testActualizarPassword() {
        HashResponse response = new HashResponse("oldPass", "$2a$10$oldHash");
        
        response.setPassword("newPass");
        
        assertEquals("newPass", response.getPassword());
        assertEquals("$2a$10$oldHash", response.getHash());
    }

    @Test
    void testActualizarHash() {
        HashResponse response = new HashResponse("password", "$2a$10$oldHash");
        
        response.setHash("$2a$10$newHash");
        
        assertEquals("$2a$10$newHash", response.getHash());
    }

    @Test
    void testTodosLosCamposNulos() {
        HashResponse response = new HashResponse(null, null);
        
        assertNull(response.getPassword());
        assertNull(response.getHash());
    }

    @Test
    void testHashBcryptCompleto() {
        String password = "SecurePassword123";
        String bcryptHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        
        HashResponse response = new HashResponse(password, bcryptHash);
        
        assertEquals(password, response.getPassword());
        assertEquals(bcryptHash, response.getHash());
        assertTrue(response.getHash().startsWith("$2a$10$"));
    }
}
