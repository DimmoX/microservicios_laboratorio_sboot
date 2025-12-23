package com.gestion_labs.ms_gestion_labs.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.gestion_labs.ms_gestion_labs.dto.LaboratorioDTO;
import com.gestion_labs.ms_gestion_labs.service.laboratorio.LaboratorioService;

@ExtendWith(MockitoExtension.class)
class LaboratorioControllerTest {

    @Mock
    private LaboratorioService service;

    @InjectMocks
    private LaboratorioController controller;

    private LaboratorioDTO laboratorio;

    @BeforeEach
    void setUp() {
        laboratorio = new LaboratorioDTO();
        laboratorio.setId(1L);
        laboratorio.setNombre("Lab Central");
        laboratorio.setTipo("CLINICA");
    }

    @Test
    void testGetAllLabs() {
        // Arrange
        LaboratorioDTO lab2 = new LaboratorioDTO();
        lab2.setId(2L);
        lab2.setNombre("Lab Norte");
        
        when(service.findAll()).thenReturn(Arrays.asList(laboratorio, lab2));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getAllLabs();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        @SuppressWarnings("unchecked")
        List<LaboratorioDTO> data = (List<LaboratorioDTO>) response.getBody().get("data");
        assertEquals(2, data.size());
        
        verify(service, times(1)).findAll();
    }

    @Test
    void testGetAllLabsConError() {
        // Arrange
        when(service.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getAllLabs();

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("001", response.getBody().get("code"));
        
        verify(service, times(1)).findAll();
    }

    @Test
    void testGetAllLabsListaVacia() {
        // Arrange
        when(service.findAll()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getAllLabs();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        @SuppressWarnings("unchecked")
        List<LaboratorioDTO> data = (List<LaboratorioDTO>) response.getBody().get("data");
        assertTrue(data.isEmpty());
        
        verify(service, times(1)).findAll();
    }

    @Test
    void testGetLabById() {
        // Arrange
        when(service.findById(1L)).thenReturn(laboratorio);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getLabById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        LaboratorioDTO data = (LaboratorioDTO) response.getBody().get("data");
        assertNotNull(data);
        assertEquals(1L, data.getId());
        assertEquals("Lab Central", data.getNombre());
        
        verify(service, times(1)).findById(1L);
    }

    @Test
    void testGetLabByIdConError() {
        // Arrange
        when(service.findById(999L)).thenThrow(new RuntimeException("Not found"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getLabById(999L);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("001", response.getBody().get("code"));
        
        verify(service, times(1)).findById(999L);
    }

    @Test
    void testCreateLab() {
        // Arrange
        when(service.create(any(LaboratorioDTO.class))).thenReturn(laboratorio);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.createLab(laboratorio);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        LaboratorioDTO data = (LaboratorioDTO) response.getBody().get("data");
        assertNotNull(data);
        assertEquals(1L, data.getId());
        
        verify(service, times(1)).create(any(LaboratorioDTO.class));
    }

    @Test
    void testCreateLabConError() {
        // Arrange
        when(service.create(any(LaboratorioDTO.class))).thenThrow(new RuntimeException("Creation failed"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.createLab(laboratorio);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("001", response.getBody().get("code"));
        
        verify(service, times(1)).create(any(LaboratorioDTO.class));
    }

    @Test
    void testUpdateLab() {
        // Arrange
        LaboratorioDTO updated = new LaboratorioDTO();
        updated.setId(1L);
        updated.setNombre("Lab Central Actualizado");
        updated.setTipo("HOSPITAL");
        
        when(service.update(anyLong(), any(LaboratorioDTO.class))).thenReturn(updated);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.updateLab(1L, updated);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        LaboratorioDTO data = (LaboratorioDTO) response.getBody().get("data");
        assertNotNull(data);
        assertEquals("Lab Central Actualizado", data.getNombre());
        
        verify(service, times(1)).update(anyLong(), any(LaboratorioDTO.class));
    }

    @Test
    void testUpdateLabConError() {
        // Arrange
        when(service.update(anyLong(), any(LaboratorioDTO.class))).thenThrow(new RuntimeException("Update failed"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.updateLab(1L, laboratorio);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("001", response.getBody().get("code"));
        
        verify(service, times(1)).update(anyLong(), any(LaboratorioDTO.class));
    }

    @Test
    void testDeleteLab() {
        // Arrange
        doNothing().when(service).delete(1L);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.deleteLab(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        verify(service, times(1)).delete(1L);
    }

    @Test
    void testDeleteLabConError() {
        // Arrange
        doThrow(new RuntimeException("Delete failed")).when(service).delete(999L);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.deleteLab(999L);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("001", response.getBody().get("code"));
        
        verify(service, times(1)).delete(999L);
    }
}
