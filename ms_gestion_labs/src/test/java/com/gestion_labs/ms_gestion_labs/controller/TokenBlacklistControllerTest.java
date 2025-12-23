package com.gestion_labs.ms_gestion_labs.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.gestion_labs.ms_gestion_labs.service.TokenBlacklistService;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistControllerTest {

    @Mock
    private TokenBlacklistService blacklistService;

    @InjectMocks
    private TokenBlacklistController controller;

    @Test
    void testAddToBlacklist() {
        // Arrange
        Map<String, String> request = new LinkedHashMap<>();
        request.put("token", "jwt.token.here");
        
        doNothing().when(blacklistService).blacklistToken(anyString());
        when(blacklistService.size()).thenReturn(10);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.addToBlacklist(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        assertNotNull(data);
        assertEquals(10, data.get("blacklistSize"));
        
        verify(blacklistService, times(1)).blacklistToken(anyString());
        verify(blacklistService, times(1)).size();
    }

    @Test
    void testAddToBlacklistTokenVacio() {
        // Arrange
        Map<String, String> request = new LinkedHashMap<>();
        request.put("token", "");

        // Act
        ResponseEntity<Map<String, Object>> response = controller.addToBlacklist(request);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("001", response.getBody().get("code"));
        assertTrue(response.getBody().get("description").toString().contains("Token es requerido"));
        
        verify(blacklistService, never()).blacklistToken(anyString());
    }

    @Test
    void testAddToBlacklistTokenNulo() {
        // Arrange
        Map<String, String> request = new LinkedHashMap<>();
        request.put("token", null);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.addToBlacklist(request);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("001", response.getBody().get("code"));
        
        verify(blacklistService, never()).blacklistToken(anyString());
    }

    @Test
    void testAddToBlacklistRequestSinToken() {
        // Arrange
        Map<String, String> request = new LinkedHashMap<>();
        // No se agrega el campo "token"

        // Act
        ResponseEntity<Map<String, Object>> response = controller.addToBlacklist(request);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertEquals("001", response.getBody().get("code"));
        
        verify(blacklistService, never()).blacklistToken(anyString());
    }

    @Test
    void testAddToBlacklistConError() {
        // Arrange
        Map<String, String> request = new LinkedHashMap<>();
        request.put("token", "jwt.token.error");
        
        doThrow(new RuntimeException("Service error")).when(blacklistService).blacklistToken(anyString());

        // Act
        ResponseEntity<Map<String, Object>> response = controller.addToBlacklist(request);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("002", response.getBody().get("code"));
        assertTrue(response.getBody().get("description").toString().contains("Error al agregar"));
        
        verify(blacklistService, times(1)).blacklistToken(anyString());
        verify(blacklistService, never()).size();
    }

    @Test
    void testAddToBlacklistConTokenLargo() {
        // Arrange
        String longToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        Map<String, String> request = new LinkedHashMap<>();
        request.put("token", longToken);
        
        doNothing().when(blacklistService).blacklistToken(anyString());
        when(blacklistService.size()).thenReturn(1);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.addToBlacklist(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        verify(blacklistService, times(1)).blacklistToken(longToken);
    }

    @Test
    void testGetBlacklistSize() {
        // Arrange
        when(blacklistService.size()).thenReturn(25);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getBlacklistSize();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        assertNotNull(data);
        assertEquals(25, data.get("blacklistSize"));
        
        verify(blacklistService, times(1)).size();
    }

    @Test
    void testGetBlacklistSizeVacia() {
        // Arrange
        when(blacklistService.size()).thenReturn(0);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getBlacklistSize();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        assertEquals(0, data.get("blacklistSize"));
        
        verify(blacklistService, times(1)).size();
    }
}
