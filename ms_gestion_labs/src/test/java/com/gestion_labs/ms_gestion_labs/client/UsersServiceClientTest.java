package com.gestion_labs.ms_gestion_labs.client;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.gestion_labs.ms_gestion_labs.dto.PacienteDTO;

@ExtendWith(MockitoExtension.class)
class UsersServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UsersServiceClient client;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "usersServiceUrl", "http://users-service:8083");
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetPacienteById() {
        // Arrange
        Long pacienteId = 100L;
        
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", 100L);
        data.put("pnombre", "Juan");
        data.put("snombre", "Carlos");
        data.put("papellido", "Pérez");
        data.put("sapellido", "González");
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", "000");
        response.put("data", data);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin");
        when(authentication.getAuthorities()).thenAnswer(invocation -> 
            java.util.Collections.singleton(new SimpleGrantedAuthority("ADMIN")));
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        // Act
        PacienteDTO result = client.getPacienteById(pacienteId);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Juan", result.getPnombre());
        assertEquals("Carlos", result.getSnombre());
        assertEquals("Pérez", result.getPapellido());
        assertEquals("González", result.getSapellido());
        
        verify(restTemplate, times(1)).exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        );
    }

    @Test
    void testGetPacienteByIdSinAutenticacion() {
        // Arrange
        Long pacienteId = 100L;
        
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", 100L);
        data.put("pnombre", "María");
        data.put("snombre", null);
        data.put("papellido", "López");
        data.put("sapellido", "Silva");
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", "000");
        response.put("data", data);
        
        when(securityContext.getAuthentication()).thenReturn(null);
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        // Act
        PacienteDTO result = client.getPacienteById(pacienteId);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("María", result.getPnombre());
        assertNull(result.getSnombre());
        
        verify(restTemplate, times(1)).exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        );
    }

    @Test
    void testGetPacienteByIdRespuestaSinData() {
        // Arrange
        Long pacienteId = 100L;
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", "001");
        response.put("description", "Paciente no encontrado");
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        // Act
        PacienteDTO result = client.getPacienteById(pacienteId);

        // Assert
        assertNull(result);
        
        verify(restTemplate, times(1)).exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        );
    }

    @Test
    void testGetPacienteByIdRespuestaNula() {
        // Arrange
        Long pacienteId = 100L;
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        // Act
        PacienteDTO result = client.getPacienteById(pacienteId);

        // Assert
        assertNull(result);
    }

    @Test
    void testGetPacienteByIdConError() {
        // Arrange
        Long pacienteId = 100L;
        
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenThrow(new RestClientException("Service unavailable"));

        // Act
        PacienteDTO result = client.getPacienteById(pacienteId);

        // Assert
        assertNull(result);
        
        verify(restTemplate, times(1)).exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        );
    }

    @Test
    void testGetPacienteByIdConException() {
        // Arrange
        Long pacienteId = 100L;
        
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenThrow(new RuntimeException("Unexpected error"));

        // Act
        PacienteDTO result = client.getPacienteById(pacienteId);

        // Assert
        assertNull(result);
    }

    @Test
    void testGetPacienteByIdConDataIncompleta() {
        // Arrange
        Long pacienteId = 100L;
        
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", 100L);
        data.put("pnombre", "Pedro");
        // Faltan otros campos
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", "000");
        response.put("data", data);
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        // Act
        PacienteDTO result = client.getPacienteById(pacienteId);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Pedro", result.getPnombre());
        assertNull(result.getSnombre());
        assertNull(result.getPapellido());
    }

    @Test
    void testGetPacienteByIdConAutenticacionSinRoles() {
        // Arrange
        Long pacienteId = 100L;
        
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", 100L);
        data.put("pnombre", "Ana");
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", "000");
        response.put("data", data);
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user123");
        when(authentication.getAuthorities()).thenAnswer(invocation -> 
            java.util.Collections.emptyList());
        
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(responseEntity);

        // Act
        PacienteDTO result = client.getPacienteById(pacienteId);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
    }
}
