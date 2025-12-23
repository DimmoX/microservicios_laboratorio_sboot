package com.gestion_labs.ms_gestion_labs.dto;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ResultadoExamenDTOTest {

    @Test
    void testConstructorVacio() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getAgendaId());
        assertNull(dto.getPacienteId());
        assertNull(dto.getLabId());
        assertNull(dto.getExamenId());
        assertNull(dto.getEmpleadoId());
        assertNull(dto.getFechaMuestra());
        assertNull(dto.getFechaResultado());
        assertNull(dto.getValor());
        assertNull(dto.getUnidad());
        assertNull(dto.getObservacion());
        assertNull(dto.getEstado());
    }

    @Test
    void testIdGetterSetter() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        Long id = 100L;
        
        dto.setId(id);
        
        assertEquals(id, dto.getId());
    }

    @Test
    void testAgendaIdGetterSetter() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        Long agendaId = 200L;
        
        dto.setAgendaId(agendaId);
        
        assertEquals(agendaId, dto.getAgendaId());
    }

    @Test
    void testPacienteIdGetterSetter() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        Long pacienteId = 50L;
        
        dto.setPacienteId(pacienteId);
        
        assertEquals(pacienteId, dto.getPacienteId());
    }

    @Test
    void testLabIdGetterSetter() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        Long labId = 10L;
        
        dto.setLabId(labId);
        
        assertEquals(labId, dto.getLabId());
    }

    @Test
    void testExamenIdGetterSetter() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        Long examenId = 300L;
        
        dto.setExamenId(examenId);
        
        assertEquals(examenId, dto.getExamenId());
    }

    @Test
    void testEmpleadoIdGetterSetter() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        Long empleadoId = 75L;
        
        dto.setEmpleadoId(empleadoId);
        
        assertEquals(empleadoId, dto.getEmpleadoId());
    }

    @Test
    void testFechaMuestraGetterSetter() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        OffsetDateTime fecha = OffsetDateTime.of(2025, 6, 15, 10, 30, 0, 0, ZoneOffset.UTC);
        
        dto.setFechaMuestra(fecha);
        
        assertEquals(fecha, dto.getFechaMuestra());
    }

    @Test
    void testFechaResultadoGetterSetter() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        OffsetDateTime fecha = OffsetDateTime.of(2025, 6, 20, 14, 45, 0, 0, ZoneOffset.UTC);
        
        dto.setFechaResultado(fecha);
        
        assertEquals(fecha, dto.getFechaResultado());
    }

    @Test
    void testValorGetterSetter() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        String valor = "120.5";
        
        dto.setValor(valor);
        
        assertEquals(valor, dto.getValor());
    }

    @Test
    void testUnidadGetterSetter() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        String unidad = "mg/dL";
        
        dto.setUnidad(unidad);
        
        assertEquals(unidad, dto.getUnidad());
    }

    @Test
    void testObservacionGetterSetter() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        String observacion = "Valores dentro del rango normal";
        
        dto.setObservacion(observacion);
        
        assertEquals(observacion, dto.getObservacion());
    }

    @Test
    void testEstadoGetterSetter() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        String estado = "EMITIDO";
        
        dto.setEstado(estado);
        
        assertEquals(estado, dto.getEstado());
    }

    @Test
    void testSettersConValoresNull() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        dto.setId(1L);
        dto.setAgendaId(2L);
        dto.setPacienteId(3L);
        dto.setLabId(4L);
        dto.setExamenId(5L);
        dto.setEmpleadoId(6L);
        dto.setFechaMuestra(OffsetDateTime.now());
        dto.setFechaResultado(OffsetDateTime.now());
        dto.setValor("valor");
        dto.setUnidad("unidad");
        dto.setObservacion("observacion");
        dto.setEstado("estado");
        
        dto.setId(null);
        dto.setAgendaId(null);
        dto.setPacienteId(null);
        dto.setLabId(null);
        dto.setExamenId(null);
        dto.setEmpleadoId(null);
        dto.setFechaMuestra(null);
        dto.setFechaResultado(null);
        dto.setValor(null);
        dto.setUnidad(null);
        dto.setObservacion(null);
        dto.setEstado(null);
        
        assertNull(dto.getId());
        assertNull(dto.getAgendaId());
        assertNull(dto.getPacienteId());
        assertNull(dto.getLabId());
        assertNull(dto.getExamenId());
        assertNull(dto.getEmpleadoId());
        assertNull(dto.getFechaMuestra());
        assertNull(dto.getFechaResultado());
        assertNull(dto.getValor());
        assertNull(dto.getUnidad());
        assertNull(dto.getObservacion());
        assertNull(dto.getEstado());
    }

    @Test
    void testTodosLosCamposCompletos() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        Long id = 500L;
        Long agendaId = 250L;
        Long pacienteId = 80L;
        Long labId = 15L;
        Long examenId = 400L;
        Long empleadoId = 90L;
        OffsetDateTime fechaMuestra = OffsetDateTime.of(2025, 7, 10, 8, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime fechaResultado = OffsetDateTime.of(2025, 7, 15, 16, 30, 0, 0, ZoneOffset.UTC);
        String valor = "85.3";
        String unidad = "mmol/L";
        String observacion = "Resultado normal";
        String estado = "EMITIDO";
        
        dto.setId(id);
        dto.setAgendaId(agendaId);
        dto.setPacienteId(pacienteId);
        dto.setLabId(labId);
        dto.setExamenId(examenId);
        dto.setEmpleadoId(empleadoId);
        dto.setFechaMuestra(fechaMuestra);
        dto.setFechaResultado(fechaResultado);
        dto.setValor(valor);
        dto.setUnidad(unidad);
        dto.setObservacion(observacion);
        dto.setEstado(estado);
        
        assertEquals(id, dto.getId());
        assertEquals(agendaId, dto.getAgendaId());
        assertEquals(pacienteId, dto.getPacienteId());
        assertEquals(labId, dto.getLabId());
        assertEquals(examenId, dto.getExamenId());
        assertEquals(empleadoId, dto.getEmpleadoId());
        assertEquals(fechaMuestra, dto.getFechaMuestra());
        assertEquals(fechaResultado, dto.getFechaResultado());
        assertEquals(valor, dto.getValor());
        assertEquals(unidad, dto.getUnidad());
        assertEquals(observacion, dto.getObservacion());
        assertEquals(estado, dto.getEstado());
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDIENTE", "EMITIDO", "ANULADO"})
    void testEstadoValues(String estado) {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        
        dto.setEstado(estado);
        
        assertEquals(estado, dto.getEstado());
    }

    @Test
    void testEstadoAnuladoAdditional() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        
        dto.setEstado("ANULADO");
        
        assertEquals("ANULADO", dto.getEstado());
    }

    @Test
    void testValorComoJSON() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        String valorJson = "{\"resultado\": 98.6, \"rango\": \"normal\"}";
        
        dto.setValor(valorJson);
        
        assertEquals(valorJson, dto.getValor());
        assertTrue(dto.getValor().contains("resultado"));
        assertTrue(dto.getValor().contains("rango"));
    }

    @Test
    void testValorVacio() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        
        dto.setValor("");
        
        assertEquals("", dto.getValor());
    }

    @Test
    void testUnidadVacia() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        
        dto.setUnidad("");
        
        assertEquals("", dto.getUnidad());
    }

    @Test
    void testObservacionVacia() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        
        dto.setObservacion("");
        
        assertEquals("", dto.getObservacion());
    }

    @Test
    void testFechaMuestraAntesDeFechaResultado() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        OffsetDateTime fechaMuestra = OffsetDateTime.of(2025, 8, 1, 10, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime fechaResultado = OffsetDateTime.of(2025, 8, 5, 15, 0, 0, 0, ZoneOffset.UTC);
        
        dto.setFechaMuestra(fechaMuestra);
        dto.setFechaResultado(fechaResultado);
        
        assertTrue(dto.getFechaResultado().isAfter(dto.getFechaMuestra()));
    }

    @Test
    void testFechasConZonaHorariaDiferente() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        OffsetDateTime fechaUTC = OffsetDateTime.of(2025, 9, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime fechaMinus3 = OffsetDateTime.of(2025, 9, 1, 9, 0, 0, 0, ZoneOffset.ofHours(-3));
        
        dto.setFechaMuestra(fechaUTC);
        dto.setFechaResultado(fechaMinus3);
        
        assertEquals(fechaUTC, dto.getFechaMuestra());
        assertEquals(fechaMinus3, dto.getFechaResultado());
    }

    @Test
    void testIdsGrandes() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        
        dto.setId(999999L);
        dto.setAgendaId(888888L);
        dto.setPacienteId(777777L);
        dto.setLabId(666666L);
        dto.setExamenId(555555L);
        dto.setEmpleadoId(444444L);
        
        assertEquals(999999L, dto.getId());
        assertEquals(888888L, dto.getAgendaId());
        assertEquals(777777L, dto.getPacienteId());
        assertEquals(666666L, dto.getLabId());
        assertEquals(555555L, dto.getExamenId());
        assertEquals(444444L, dto.getEmpleadoId());
    }

    @Test
    void testIdsCero() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        
        dto.setId(0L);
        dto.setAgendaId(0L);
        dto.setPacienteId(0L);
        dto.setLabId(0L);
        dto.setExamenId(0L);
        dto.setEmpleadoId(0L);
        
        assertEquals(0L, dto.getId());
        assertEquals(0L, dto.getAgendaId());
        assertEquals(0L, dto.getPacienteId());
        assertEquals(0L, dto.getLabId());
        assertEquals(0L, dto.getExamenId());
        assertEquals(0L, dto.getEmpleadoId());
    }

    @Test
    void testObservacionLarga() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        String observacionLarga = "Este es un resultado de examen de laboratorio que requiere una observación "
                + "muy detallada y extensa para documentar todos los hallazgos importantes y relevantes "
                + "para el diagnóstico médico del paciente. Se recomienda seguimiento.";
        
        dto.setObservacion(observacionLarga);
        
        assertEquals(observacionLarga, dto.getObservacion());
        assertTrue(dto.getObservacion().length() > 100);
    }

    @Test
    void testMultiplesCambiosEstado() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        
        dto.setEstado("PENDIENTE");
        assertEquals("PENDIENTE", dto.getEstado());
        
        dto.setEstado("EMITIDO");
        assertEquals("EMITIDO", dto.getEstado());
        
        dto.setEstado("ANULADO");
        assertEquals("ANULADO", dto.getEstado());
    }

    @Test
    void testFechaActual() {
        ResultadoExamenDTO dto = new ResultadoExamenDTO();
        OffsetDateTime ahora = OffsetDateTime.now();
        
        dto.setFechaMuestra(ahora);
        
        assertNotNull(dto.getFechaMuestra());
        assertEquals(ahora, dto.getFechaMuestra());
    }
}
