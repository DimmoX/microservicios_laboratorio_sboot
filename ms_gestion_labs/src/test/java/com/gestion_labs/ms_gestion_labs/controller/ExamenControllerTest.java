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

import com.gestion_labs.ms_gestion_labs.model.ExamenModel;
import com.gestion_labs.ms_gestion_labs.service.examen.ExamenService;

@ExtendWith(MockitoExtension.class)
class ExamenControllerTest {

    @Mock
    private ExamenService service;

    @InjectMocks
    private ExamenController controller;

    private ExamenModel examen;

    @BeforeEach
    void setUp() {
        examen = new ExamenModel();
        examen.setId(1L);
        examen.setNombre("Hemograma");
        examen.setTipo("LABORATORIO");
    }

    @Test
    void testGetAllExams() {
        // Arrange
        ExamenModel examen2 = new ExamenModel();
        examen2.setId(2L);
        examen2.setNombre("Glucosa");
        
        when(service.findAll()).thenReturn(Arrays.asList(examen, examen2));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getAllExams();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        @SuppressWarnings("unchecked")
        List<ExamenModel> data = (List<ExamenModel>) response.getBody().get("data");
        assertEquals(2, data.size());
        
        verify(service, times(1)).findAll();
    }

    @Test
    void testGetAllExamsConError() {
        // Arrange
        when(service.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getAllExams();

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("001", response.getBody().get("code"));
        
        verify(service, times(1)).findAll();
    }

    @Test
    void testGetAllExamsListaVacia() {
        // Arrange
        when(service.findAll()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getAllExams();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        @SuppressWarnings("unchecked")
        List<ExamenModel> data = (List<ExamenModel>) response.getBody().get("data");
        assertTrue(data.isEmpty());
        
        verify(service, times(1)).findAll();
    }

    @Test
    void testGetExamById() {
        // Arrange
        when(service.findById(1L)).thenReturn(examen);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getExamById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        ExamenModel data = (ExamenModel) response.getBody().get("data");
        assertNotNull(data);
        assertEquals(1L, data.getId());
        assertEquals("Hemograma", data.getNombre());
        
        verify(service, times(1)).findById(1L);
    }

    @Test
    void testGetExamByIdConError() {
        // Arrange
        when(service.findById(999L)).thenThrow(new RuntimeException("Not found"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getExamById(999L);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("001", response.getBody().get("code"));
        
        verify(service, times(1)).findById(999L);
    }

    @Test
    void testCreateExam() {
        // Arrange
        when(service.create(any(ExamenModel.class))).thenReturn(examen);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.createExam(examen);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        ExamenModel data = (ExamenModel) response.getBody().get("data");
        assertNotNull(data);
        assertEquals(1L, data.getId());
        
        verify(service, times(1)).create(any(ExamenModel.class));
    }

    @Test
    void testCreateExamConError() {
        // Arrange
        when(service.create(any(ExamenModel.class))).thenThrow(new RuntimeException("Creation failed"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.createExam(examen);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("001", response.getBody().get("code"));
        
        verify(service, times(1)).create(any(ExamenModel.class));
    }

    @Test
    void testUpdateExam() {
        // Arrange
        ExamenModel updated = new ExamenModel();
        updated.setId(1L);
        updated.setNombre("Hemograma Completo");
        updated.setTipo("LABORATORIO");
        
        when(service.update(anyLong(), any(ExamenModel.class))).thenReturn(updated);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.updateExam(1L, updated);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        ExamenModel data = (ExamenModel) response.getBody().get("data");
        assertNotNull(data);
        assertEquals("Hemograma Completo", data.getNombre());
        
        verify(service, times(1)).update(anyLong(), any(ExamenModel.class));
    }

    @Test
    void testUpdateExamConError() {
        // Arrange
        when(service.update(anyLong(), any(ExamenModel.class))).thenThrow(new RuntimeException("Update failed"));

        // Act
        ResponseEntity<Map<String, Object>> response = controller.updateExam(1L, examen);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("001", response.getBody().get("code"));
        
        verify(service, times(1)).update(anyLong(), any(ExamenModel.class));
    }

    @Test
    void testDeleteExam() {
        // Arrange
        doNothing().when(service).delete(1L);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.deleteExam(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("000", response.getBody().get("code"));
        
        verify(service, times(1)).delete(1L);
    }

    @Test
    void testDeleteExamConError() {
        // Arrange
        doThrow(new RuntimeException("Delete failed")).when(service).delete(999L);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.deleteExam(999L);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("001", response.getBody().get("code"));
        
        verify(service, times(1)).delete(999L);
    }
}
