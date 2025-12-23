package com.gestion_resultados.ms_gestion_resultados.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gestion_resultados.ms_gestion_resultados.model.ResultadoExamenModel;
import com.gestion_resultados.ms_gestion_resultados.service.EnrichmentService;
import com.gestion_resultados.ms_gestion_resultados.service.resultados.ResultadoService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Tests para ResultadoController
 * Cobertura: endpoints de resultados de exámenes médicos
 */
@ExtendWith(MockitoExtension.class)
class ResultadoControllerTest {

    @Mock
    private ResultadoService service;

    @Mock
    private EnrichmentService enrichmentService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ResultadoController controller;

    private ResultadoExamenModel resultadoSample;
    private List<ResultadoExamenModel> resultadosList;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);

        resultadoSample = new ResultadoExamenModel();
        resultadoSample.setId(1L);
        resultadoSample.setPacienteId(10L);
        resultadoSample.setExamenId(5L);
        resultadoSample.setValor("Normal");
        resultadoSample.setUnidad("mg/dL");
        resultadoSample.setFechaMuestra(java.time.OffsetDateTime.now());

        resultadosList = Arrays.asList(resultadoSample);
    }

    @Test
    void testGetAllResults_AsAdmin_ReturnsAllResults() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Arrays.asList(new SimpleGrantedAuthority("ADMIN"))).when(authentication).getAuthorities();
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(service.findAll()).thenReturn(resultadosList);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getAllResults(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        assertEquals("Resultados de exámenes obtenidos exitosamente", body.get("description"));
        assertTrue(body.get("data") instanceof List);
        
        verify(service).findAll();
        verify(enrichmentService).enrichResultados(resultadosList, "Bearer token123");
    }

    @Test
    void testGetAllResults_AsPatient_ReturnsResults() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Arrays.asList(new SimpleGrantedAuthority("PATIENT"))).when(authentication).getAuthorities();
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(service.findAll()).thenReturn(resultadosList);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getAllResults(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        verify(service).findAll();
    }

    @Test
    void testGetAllResults_AsEmployee_ReturnsAllResults() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Arrays.asList(new SimpleGrantedAuthority("EMPLOYEE"))).when(authentication).getAuthorities();
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(service.findAll()).thenReturn(resultadosList);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getAllResults(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(service).findAll();
        verify(enrichmentService).enrichResultados(resultadosList, "Bearer token123");
    }

    @Test
    void testGetAllResults_NoJwtToken_DoesNotEnrich() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Arrays.asList(new SimpleGrantedAuthority("ADMIN"))).when(authentication).getAuthorities();
        when(request.getHeader("Authorization")).thenReturn(null);
        when(service.findAll()).thenReturn(resultadosList);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getAllResults(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(enrichmentService, never()).enrichResultados(any(), any());
    }

    @Test
    void testGetAllResults_EmptyList_DoesNotEnrich() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Arrays.asList(new SimpleGrantedAuthority("ADMIN"))).when(authentication).getAuthorities();
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");
        when(service.findAll()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getAllResults(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(enrichmentService, never()).enrichResultados(any(), any());
    }

    @Test
    void testGetAllResults_ServiceThrowsException_Returns500() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Arrays.asList(new SimpleGrantedAuthority("ADMIN"))).when(authentication).getAuthorities();
        when(service.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getAllResults(request);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("001", body.get("code"));
        assertEquals("Error al obtener resultados de exámenes", body.get("description"));
    }

    @Test
    void testGetResultById_Found_ReturnsResult() {
        // Arrange
        when(service.findById(1L)).thenReturn(resultadoSample);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getResultById(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        assertEquals("Resultado de examen obtenido exitosamente", body.get("description"));
        assertNotNull(body.get("data"));
        
        verify(service).findById(1L);
    }

    @Test
    void testGetResultById_NotFound_Returns404() {
        // Arrange
        when(service.findById(999L)).thenThrow(new RuntimeException("Resultado no encontrado"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getResultById(999L);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("404", body.get("code"));
        assertTrue(body.get("description").toString().contains("no encontrado"));
    }

    @Test
    void testGetResultById_AccessDenied_Returns403() {
        // Arrange
        when(service.findById(1L)).thenThrow(new AccessDeniedException("No tiene permisos"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getResultById(1L);

        // Assert
        assertEquals(403, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("403", body.get("code"));
    }

    @Test
    void testGetResultById_OtherException_Returns500() {
        // Arrange
        when(service.findById(1L)).thenThrow(new RuntimeException("Database connection failed"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getResultById(1L);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("500", body.get("code"));
    }

    @Test
    void testGetResultsByPatient_Found_ReturnsResults() {
        // Arrange
        when(service.findByPaciente(10L)).thenReturn(resultadosList);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getResultsByPatient(10L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        assertEquals("Resultados de exámenes del paciente obtenidos exitosamente", body.get("description"));
        assertTrue(body.get("data") instanceof List);
        
        verify(service).findByPaciente(10L);
    }

    @Test
    void testGetResultsByPatient_AccessDenied_Returns403() {
        // Arrange
        when(service.findByPaciente(10L)).thenThrow(new AccessDeniedException("No puede ver resultados de otro paciente"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getResultsByPatient(10L);

        // Assert
        assertEquals(403, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("403", body.get("code"));
    }

    @Test
    void testGetResultsByPatient_Exception_Returns500() {
        // Arrange
        when(service.findByPaciente(10L)).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getResultsByPatient(10L);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("code"));
    }

    @Test
    void testGetAllResults_NoAuthentication_HandlesGracefully() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        when(service.findAll()).thenReturn(resultadosList);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getAllResults(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(service).findAll();
    }

    // ============ Tests para createResult ============

    @Test
    void testCreateResult_Success_ReturnsCreatedResult() {
        // Arrange
        ResultadoExamenModel newResult = new ResultadoExamenModel();
        newResult.setPacienteId(10L);
        newResult.setExamenId(5L);
        newResult.setValor("Normal");

        ResultadoExamenModel createdResult = new ResultadoExamenModel();
        createdResult.setId(1L);
        createdResult.setPacienteId(10L);
        createdResult.setExamenId(5L);
        createdResult.setValor("Normal");

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("employee1");
        doReturn(Arrays.asList(new SimpleGrantedAuthority("EMPLOYEE"))).when(authentication).getAuthorities();
        when(authentication.getDetails()).thenReturn(Map.of("empleadoId", 123));
        when(service.create(any(ResultadoExamenModel.class))).thenReturn(createdResult);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.createResult(newResult);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        assertEquals("Resultado de examen creado exitosamente", body.get("description"));
        assertNotNull(body.get("data"));
        
        verify(service).create(any(ResultadoExamenModel.class));
    }

    @Test
    void testCreateResult_WithEmpleadoIdInJWT_SetsEmpleadoId() {
        // Arrange
        ResultadoExamenModel newResult = new ResultadoExamenModel();
        newResult.setPacienteId(10L);
        newResult.setExamenId(5L);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("employee1");
        doReturn(Arrays.asList(new SimpleGrantedAuthority("EMPLOYEE"))).when(authentication).getAuthorities();
        when(authentication.getDetails()).thenReturn(Map.of("empleadoId", 456));
        when(service.create(any(ResultadoExamenModel.class))).thenReturn(newResult);

        // Act
        controller.createResult(newResult);

        // Assert
        assertEquals(456L, newResult.getEmpleadoId());
        verify(service).create(newResult);
    }

    @Test
    void testCreateResult_NoEmpleadoIdInJWT_DoesNotSetEmpleadoId() {
        // Arrange
        ResultadoExamenModel newResult = new ResultadoExamenModel();
        newResult.setPacienteId(10L);
        newResult.setExamenId(5L);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("employee1");
        doReturn(Arrays.asList(new SimpleGrantedAuthority("EMPLOYEE"))).when(authentication).getAuthorities();
        when(authentication.getDetails()).thenReturn(Map.of("otherKey", "value"));
        when(service.create(any(ResultadoExamenModel.class))).thenReturn(newResult);

        // Act
        controller.createResult(newResult);

        // Assert
        verify(service).create(newResult);
    }

    @Test
    void testCreateResult_ValidationError_Returns400() {
        // Arrange
        ResultadoExamenModel invalidResult = new ResultadoExamenModel();
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("employee1");
        doReturn(Arrays.asList(new SimpleGrantedAuthority("EMPLOYEE"))).when(authentication).getAuthorities();
        when(service.create(any(ResultadoExamenModel.class)))
            .thenThrow(new IllegalArgumentException("Campos obligatorios faltantes"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.createResult(invalidResult);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("400", body.get("code"));
        assertTrue(body.get("description").toString().contains("Campos obligatorios"));
    }

    @Test
    void testCreateResult_ServiceException_Returns500() {
        // Arrange
        ResultadoExamenModel newResult = new ResultadoExamenModel();
        newResult.setPacienteId(10L);
        newResult.setExamenId(5L);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("employee1");
        doReturn(Arrays.asList(new SimpleGrantedAuthority("EMPLOYEE"))).when(authentication).getAuthorities();
        when(service.create(any(ResultadoExamenModel.class)))
            .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.createResult(newResult);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("500", body.get("code"));
        assertTrue(body.get("description").toString().contains("Error interno"));
    }

    @Test
    void testCreateResult_NoAuthentication_StillCreates() {
        // Arrange
        ResultadoExamenModel newResult = new ResultadoExamenModel();
        newResult.setPacienteId(10L);
        newResult.setExamenId(5L);

        when(securityContext.getAuthentication()).thenReturn(null);
        when(service.create(any(ResultadoExamenModel.class))).thenReturn(newResult);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.createResult(newResult);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        verify(service).create(newResult);
    }

    // ============ Tests para updateResult ============

    @Test
    void testUpdateResult_Success_ReturnsUpdatedResult() {
        // Arrange
        ResultadoExamenModel updateData = new ResultadoExamenModel();
        updateData.setValor("Anormal");
        updateData.setEstado("COMPLETADO");

        ResultadoExamenModel updatedResult = new ResultadoExamenModel();
        updatedResult.setId(1L);
        updatedResult.setValor("Anormal");
        updatedResult.setEstado("COMPLETADO");

        when(service.update(1L, updateData)).thenReturn(updatedResult);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.updateResult(1L, updateData);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        assertEquals("Resultado de examen actualizado exitosamente", body.get("description"));
        assertNotNull(body.get("data"));
        
        verify(service).update(1L, updateData);
    }

    @Test
    void testUpdateResult_NotFound_Returns404() {
        // Arrange
        ResultadoExamenModel updateData = new ResultadoExamenModel();
        updateData.setValor("Normal");

        when(service.update(999L, updateData))
            .thenThrow(new RuntimeException("Resultado no encontrado"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.updateResult(999L, updateData);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("404", body.get("code"));
        assertTrue(body.get("description").toString().contains("no encontrado"));
    }

    @Test
    void testUpdateResult_ValidationError_Returns400() {
        // Arrange
        ResultadoExamenModel updateData = new ResultadoExamenModel();

        when(service.update(1L, updateData))
            .thenThrow(new RuntimeException("Datos de actualización inválidos"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.updateResult(1L, updateData);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("400", body.get("code"));
    }

    // ============ Tests para deleteResult ============

    @Test
    void testDeleteResult_Success_ReturnsSuccess() {
        // Arrange - service.delete no lanza excepción

        // Act
        ResponseEntity<Map<String, Object>> response = controller.deleteResult(1L);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        assertEquals("Resultado de examen eliminado exitosamente", body.get("description"));
        
        verify(service).delete(1L);
    }

    @Test
    void testDeleteResult_ServiceException_Returns500() {
        // Arrange
        doThrow(new RuntimeException("Resultado no encontrado"))
            .when(service).delete(999L);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.deleteResult(999L);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("001", body.get("code"));
        assertTrue(body.get("description").toString().contains("Error al eliminar"));
        
        verify(service).delete(999L);
    }

    @Test
    void testDeleteResult_DatabaseError_Returns500() {
        // Arrange
        doThrow(new RuntimeException("Database connection lost"))
            .when(service).delete(1L);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.deleteResult(1L);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("001", body.get("code"));
    }

    @Test
    void testGetResultById_GenericException_Returns500() {
        // Arrange
        when(service.findById(1L))
            .thenThrow(new NullPointerException("Null value"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getResultById(1L);

        // Assert
        assertEquals(500, response.getStatusCodeValue());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("500", body.get("code"));
    }
}
