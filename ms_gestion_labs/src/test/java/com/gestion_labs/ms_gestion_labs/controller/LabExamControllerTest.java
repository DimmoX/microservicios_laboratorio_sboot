package com.gestion_labs.ms_gestion_labs.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.gestion_labs.ms_gestion_labs.dto.LaboratorioDTO;
import com.gestion_labs.ms_gestion_labs.model.ExamenModel;
import com.gestion_labs.ms_gestion_labs.model.LabExamKey;
import com.gestion_labs.ms_gestion_labs.model.LabExamModel;
import com.gestion_labs.ms_gestion_labs.service.examen.ExamenService;
import com.gestion_labs.ms_gestion_labs.service.lab_exam.LabExamService;
import com.gestion_labs.ms_gestion_labs.service.laboratorio.LaboratorioService;

@ExtendWith(MockitoExtension.class)
class LabExamControllerTest {

    @Mock
    private LabExamService labExamService;

    @Mock
    private LaboratorioService laboratorioService;

    @Mock
    private ExamenService examenService;

    private LabExamController controller;
    private LabExamModel labExamModel;
    private LaboratorioDTO laboratorioDTO;
    private ExamenModel examenModel;

    @BeforeEach
    void setUp() {
        controller = new LabExamController(labExamService, laboratorioService, examenService);

        LabExamKey key = new LabExamKey(1L, 10L);
        labExamModel = new LabExamModel();
        labExamModel.setId(key);
        labExamModel.setPrecio(new BigDecimal("25000.00"));
        labExamModel.setVigenteDesde(LocalDate.of(2025, 1, 1));
        labExamModel.setVigenteHasta(LocalDate.of(2025, 12, 31));

        laboratorioDTO = new LaboratorioDTO();
        laboratorioDTO.setId(1L);
        laboratorioDTO.setNombre("Lab Central");

        examenModel = new ExamenModel();
        examenModel.setId(10L);
        examenModel.setNombre("Hemograma");
    }

    @Test
    void testGetAllLabExamRelations() {
        when(labExamService.listAll()).thenReturn(Arrays.asList(labExamModel));
        when(laboratorioService.findById(1L)).thenReturn(laboratorioDTO);
        when(examenService.findById(10L)).thenReturn(examenModel);

        ResponseEntity<Map<String, Object>> response = controller.getAllLabExamRelations();

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        assertTrue(body.get("data") instanceof List);
        verify(labExamService).listAll();
    }

    @Test
    void testGetAllLabExamRelationsConError() {
        when(labExamService.listAll()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Map<String, Object>> response = controller.getAllLabExamRelations();

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("001", body.get("code"));
    }

    @Test
    void testGetAllLabExamRelationsConLaboratorioNoEncontrado() {
        when(labExamService.listAll()).thenReturn(Arrays.asList(labExamModel));
        when(laboratorioService.findById(1L)).thenThrow(new RuntimeException("Lab not found"));
        when(examenService.findById(10L)).thenReturn(examenModel);

        ResponseEntity<Map<String, Object>> response = controller.getAllLabExamRelations();

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
    }

    @Test
    void testGetExamsByLab() {
        when(labExamService.listByLaboratorio(1L)).thenReturn(Arrays.asList(labExamModel));

        ResponseEntity<Map<String, Object>> response = controller.getExamsByLab(1L);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        verify(labExamService).listByLaboratorio(1L);
    }

    @Test
    void testGetExamsByLabConError() {
        when(labExamService.listByLaboratorio(1L)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<Map<String, Object>> response = controller.getExamsByLab(1L);

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("001", body.get("code"));
    }

    @Test
    void testGetLabsByExam() {
        when(labExamService.listByExamen(10L)).thenReturn(Arrays.asList(labExamModel));

        ResponseEntity<Map<String, Object>> response = controller.getLabsByExam(10L);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        verify(labExamService).listByExamen(10L);
    }

    @Test
    void testGetLabsByExamConError() {
        when(labExamService.listByExamen(10L)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<Map<String, Object>> response = controller.getLabsByExam(10L);

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("001", body.get("code"));
    }

    @Test
    void testGetLabExamRelation() {
        when(labExamService.get(1L, 10L)).thenReturn(labExamModel);

        ResponseEntity<Map<String, Object>> response = controller.getLabExamRelation(1L, 10L);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        verify(labExamService).get(1L, 10L);
    }

    @Test
    void testGetLabExamRelationConError() {
        when(labExamService.get(1L, 10L)).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<Map<String, Object>> response = controller.getLabExamRelation(1L, 10L);

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("001", body.get("code"));
    }

    @Test
    void testUpsertLabExamRelationConIdAnidado() {
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> idMap = new HashMap<>();
        idMap.put("idLaboratorio", 1);
        idMap.put("idExamen", 10);
        payload.put("id", idMap);
        payload.put("precio", 25000);
        payload.put("vigenteDesde", "2025-01-01");
        payload.put("vigenteHasta", "2025-12-31");

        when(labExamService.upsert(any(LabExamModel.class))).thenReturn(labExamModel);

        ResponseEntity<Map<String, Object>> response = controller.upsertLabExamRelation(payload);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("000", body.get("code"));
        verify(labExamService).upsert(any(LabExamModel.class));
    }

    @Test
    void testUpsertLabExamRelationConIdsDirectos() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("idLaboratorio", 1L);
        payload.put("idExamen", 10L);
        payload.put("precio", new BigDecimal("25000.00"));
        payload.put("vigenteDesde", "2025-01-01");

        when(labExamService.upsert(any(LabExamModel.class))).thenReturn(labExamModel);

        ResponseEntity<Map<String, Object>> response = controller.upsertLabExamRelation(payload);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("000", body.get("code"));
        verify(labExamService).upsert(any(LabExamModel.class));
    }

    @Test
    void testUpsertLabExamRelationSinIds() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("precio", 25000);

        ResponseEntity<Map<String, Object>> response = controller.upsertLabExamRelation(payload);

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("001", body.get("code"));
        assertTrue(body.get("description").toString().contains("Error"));
    }

    @Test
    void testUpsertLabExamRelationConError() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("idLaboratorio", 1L);
        payload.put("idExamen", 10L);

        when(labExamService.upsert(any(LabExamModel.class))).thenThrow(new RuntimeException("Save error"));

        ResponseEntity<Map<String, Object>> response = controller.upsertLabExamRelation(payload);

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("001", body.get("code"));
    }

    @Test
    void testUpdateLabExamRelation() {
        LabExamModel updateModel = new LabExamModel();
        updateModel.setPrecio(new BigDecimal("30000.00"));

        when(labExamService.upsert(any(LabExamModel.class))).thenReturn(labExamModel);

        ResponseEntity<Map<String, Object>> response = controller.updateLabExamRelation(1L, 10L, updateModel);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("000", body.get("code"));
        verify(labExamService).upsert(any(LabExamModel.class));
    }

    @Test
    void testUpdateLabExamRelationConError() {
        LabExamModel updateModel = new LabExamModel();

        when(labExamService.upsert(any(LabExamModel.class))).thenThrow(new RuntimeException("Update error"));

        ResponseEntity<Map<String, Object>> response = controller.updateLabExamRelation(1L, 10L, updateModel);

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("001", body.get("code"));
    }

    @Test
    void testDeleteLabExamRelation() {
        doNothing().when(labExamService).delete(1L, 10L);

        ResponseEntity<Map<String, Object>> response = controller.deleteLabExamRelation(1L, 10L);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("000", body.get("code"));
        verify(labExamService).delete(1L, 10L);
    }

    @Test
    void testDeleteLabExamRelationConError() {
        doThrow(new RuntimeException("Delete error")).when(labExamService).delete(1L, 10L);

        ResponseEntity<Map<String, Object>> response = controller.deleteLabExamRelation(1L, 10L);

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("001", body.get("code"));
    }

    @Test
    void testUpsertLabExamRelationConPrecioString() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("idLaboratorio", "1");
        payload.put("idExamen", "10");
        payload.put("precio", "25000.50");

        when(labExamService.upsert(any(LabExamModel.class))).thenReturn(labExamModel);

        ResponseEntity<Map<String, Object>> response = controller.upsertLabExamRelation(payload);

        assertEquals(200, response.getStatusCode().value());
        verify(labExamService).upsert(any(LabExamModel.class));
    }
}
