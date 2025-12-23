package com.gestion_resultados.ms_gestion_resultados.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.gestion_resultados.ms_gestion_resultados.model.ResultadoExamenModel;

/**
 * Tests para EnrichmentService
 * Cobertura: Enriquecimiento de resultados con datos de otros microservicios
 */
@ExtendWith(MockitoExtension.class)
class EnrichmentServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EnrichmentService enrichmentService;

    private ResultadoExamenModel resultado;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(enrichmentService, "usersServiceUrl", "http://users-service:8083");
        ReflectionTestUtils.setField(enrichmentService, "labsServiceUrl", "http://labs-service:8081");

        resultado = new ResultadoExamenModel();
        resultado.setId(1L);
        resultado.setPacienteId(10L);
        resultado.setExamenId(5L);
        resultado.setValor("Normal");
        resultado.setUnidad("mg/dL");

        jwtToken = "Bearer token123";
    }

    @Test
    void testEnrichResultado_BothNamesFound_EnrichesSuccessfully() {
        // Arrange
        Map<String, Object> pacienteResponse = createPacienteResponse("Juan", "Carlos", "Pérez", "González");
        Map<String, Object> examenResponse = createExamenResponse("Hemograma Completo");

        when(restTemplate.exchange(
            eq("http://users-service:8083/pacientes/10"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(ResponseEntity.ok(pacienteResponse));

        when(restTemplate.exchange(
            eq("http://labs-service:8081/exams/5"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(ResponseEntity.ok(examenResponse));

        // Act
        enrichmentService.enrichResultado(resultado, jwtToken);

        // Assert
        assertEquals("Juan Carlos Pérez González", resultado.getPacienteNombre());
        assertEquals("Hemograma Completo", resultado.getExamenNombre());
    }

    @Test
    void testEnrichResultado_PacienteNotFound_OnlyExamenEnriched() {
        // Arrange
        Map<String, Object> examenResponse = createExamenResponse("Hemograma Completo");

        when(restTemplate.exchange(
            contains("/pacientes/"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenThrow(new RuntimeException("Patient not found"));

        when(restTemplate.exchange(
            contains("/exams/"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(ResponseEntity.ok(examenResponse));

        // Act
        enrichmentService.enrichResultado(resultado, jwtToken);

        // Assert
        assertNull(resultado.getPacienteNombre());
        assertEquals("Hemograma Completo", resultado.getExamenNombre());
    }

    @Test
    void testEnrichResultado_ExamenNotFound_OnlyPacienteEnriched() {
        // Arrange
        Map<String, Object> pacienteResponse = createPacienteResponse("María", "Elena", "López", "Martínez");

        when(restTemplate.exchange(
            contains("/pacientes/"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(ResponseEntity.ok(pacienteResponse));

        when(restTemplate.exchange(
            contains("/exams/"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenThrow(new RuntimeException("Exam not found"));

        // Act
        enrichmentService.enrichResultado(resultado, jwtToken);

        // Assert
        assertEquals("María Elena López Martínez", resultado.getPacienteNombre());
        assertNull(resultado.getExamenNombre());
    }

    @Test
    void testEnrichResultado_BothFail_NamesRemainNull() {
        // Arrange
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenThrow(new RuntimeException("Service unavailable"));

        // Act
        enrichmentService.enrichResultado(resultado, jwtToken);

        // Assert
        assertNull(resultado.getPacienteNombre());
        assertNull(resultado.getExamenNombre());
    }

    @Test
    void testEnrichResultados_MultipleResults_AllEnriched() {
        // Arrange
        ResultadoExamenModel resultado2 = new ResultadoExamenModel();
        resultado2.setId(2L);
        resultado2.setPacienteId(11L);
        resultado2.setExamenId(6L);

        List<ResultadoExamenModel> resultados = Arrays.asList(resultado, resultado2);

        Map<String, Object> pacienteResponse1 = createPacienteResponse("Juan", null, "Pérez", null);
        Map<String, Object> pacienteResponse2 = createPacienteResponse("Ana", null, "García", null);
        Map<String, Object> examenResponse1 = createExamenResponse("Hemograma");
        Map<String, Object> examenResponse2 = createExamenResponse("Glicemia");

        when(restTemplate.exchange(
            eq("http://users-service:8083/pacientes/10"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(ResponseEntity.ok(pacienteResponse1));

        when(restTemplate.exchange(
            eq("http://users-service:8083/pacientes/11"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(ResponseEntity.ok(pacienteResponse2));

        when(restTemplate.exchange(
            eq("http://labs-service:8081/exams/5"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(ResponseEntity.ok(examenResponse1));

        when(restTemplate.exchange(
            eq("http://labs-service:8081/exams/6"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(ResponseEntity.ok(examenResponse2));

        // Act
        List<ResultadoExamenModel> enriched = enrichmentService.enrichResultados(resultados, jwtToken);

        // Assert
        assertEquals(2, enriched.size());
        assertEquals("Juan Pérez", enriched.get(0).getPacienteNombre());
        assertEquals("Hemograma", enriched.get(0).getExamenNombre());
        assertEquals("Ana García", enriched.get(1).getPacienteNombre());
        assertEquals("Glicemia", enriched.get(1).getExamenNombre());
    }

    @Test
    void testEnrichResultados_EmptyList_ReturnsEmpty() {
        // Arrange
        List<ResultadoExamenModel> resultados = Collections.emptyList();

        // Act
        List<ResultadoExamenModel> enriched = enrichmentService.enrichResultados(resultados, jwtToken);

        // Assert
        assertTrue(enriched.isEmpty());
        verify(restTemplate, never()).exchange(anyString(), any(), any(), any(Class.class));
    }

    @Test
    void testEnrichResultado_PacienteResponseWithoutData_NullName() {
        // Arrange
        Map<String, Object> response = new HashMap<>();
        response.put("code", "000");
        // No data field

        when(restTemplate.exchange(
            contains("/pacientes/"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(ResponseEntity.ok(response));

        when(restTemplate.exchange(
            contains("/exams/"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(ResponseEntity.ok(createExamenResponse("Test")));

        // Act
        enrichmentService.enrichResultado(resultado, jwtToken);

        // Assert
        assertNull(resultado.getPacienteNombre());
    }

    @Test
    void testEnrichResultado_ExamenResponseWithoutNombre_NullName() {
        // Arrange
        Map<String, Object> response = new HashMap<>();
        response.put("code", "000");
        response.put("data", new HashMap<>());

        when(restTemplate.exchange(
            contains("/pacientes/"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(ResponseEntity.ok(createPacienteResponse("Test", null, "User", null)));

        when(restTemplate.exchange(
            contains("/exams/"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(ResponseEntity.ok(response));

        // Act
        enrichmentService.enrichResultado(resultado, jwtToken);

        // Assert
        assertNull(resultado.getExamenNombre());
    }

    @Test
    void testEnrichResultado_PacienteWithPartialName_TrimsCorrectly() {
        // Arrange
        Map<String, Object> pacienteResponse = createPacienteResponse("Juan", null, null, "González");

        when(restTemplate.exchange(
            contains("/pacientes/"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(ResponseEntity.ok(pacienteResponse));

        when(restTemplate.exchange(
            contains("/exams/"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(Map.class)
        )).thenReturn(ResponseEntity.ok(createExamenResponse("Test")));

        // Act
        enrichmentService.enrichResultado(resultado, jwtToken);

        // Assert
        assertEquals("Juan González", resultado.getPacienteNombre());
    }

    private Map<String, Object> createPacienteResponse(String pnombre, String snombre, String papellido, String sapellido) {
        Map<String, Object> data = new HashMap<>();
        if (pnombre != null) data.put("pnombre", pnombre);
        if (snombre != null) data.put("snombre", snombre);
        if (papellido != null) data.put("papellido", papellido);
        if (sapellido != null) data.put("sapellido", sapellido);

        Map<String, Object> response = new HashMap<>();
        response.put("code", "000");
        response.put("data", data);
        return response;
    }

    private Map<String, Object> createExamenResponse(String nombre) {
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", nombre);

        Map<String, Object> response = new HashMap<>();
        response.put("code", "000");
        response.put("data", data);
        return response;
    }
}
