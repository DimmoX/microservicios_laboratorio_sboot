package com.gestion_labs.ms_gestion_labs.controller;

import java.time.LocalDateTime;
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
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.gestion_labs.ms_gestion_labs.dto.AgendaExamenDTO;
import com.gestion_labs.ms_gestion_labs.service.agenda.AgendaService;

@ExtendWith(MockitoExtension.class)
class AgendaControllerTest {

    @Mock
    private AgendaService agendaService;

    private AgendaController controller;
    private AgendaExamenDTO agendaDTO;

    @BeforeEach
    void setUp() {
        controller = new AgendaController(agendaService);

        agendaDTO = new AgendaExamenDTO();
        agendaDTO.setId(1L);
        agendaDTO.setPacienteId(100L);
        agendaDTO.setLabId(10L);
        agendaDTO.setExamenId(50L);
        agendaDTO.setEmpleadoId(5L);
        agendaDTO.setFechaHora(LocalDateTime.of(2025, 12, 25, 10, 0));
        agendaDTO.setEstado("PROGRAMADA");
    }

    @Test
    void testGetAllAppointments() {
        when(agendaService.findAll()).thenReturn(Arrays.asList(agendaDTO));

        ResponseEntity<Map<String, Object>> response = controller.getAllAppointments();

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        assertTrue(body.get("data") instanceof List);
        verify(agendaService).findAll();
    }

    @Test
    void testGetAllAppointmentsConError() {
        when(agendaService.findAll()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<Map<String, Object>> response = controller.getAllAppointments();

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("001", body.get("code"));
    }

    @Test
    void testGetAppointmentById() {
        when(agendaService.findById(1L)).thenReturn(agendaDTO);

        ResponseEntity<Map<String, Object>> response = controller.getAppointmentById(1L);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        assertEquals(agendaDTO, body.get("data"));
        verify(agendaService).findById(1L);
    }

    @Test
    void testGetAppointmentByIdConError() {
        when(agendaService.findById(999L)).thenThrow(new RuntimeException("Not found"));

        ResponseEntity<Map<String, Object>> response = controller.getAppointmentById(999L);

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("001", body.get("code"));
    }

    @Test
    void testGetAppointmentsByPatient() {
        when(agendaService.findByPaciente(100L)).thenReturn(Arrays.asList(agendaDTO));

        ResponseEntity<Map<String, Object>> response = controller.getAppointmentsByPatient(100L);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        assertTrue(body.get("data") instanceof List);
        verify(agendaService).findByPaciente(100L);
    }

    @Test
    void testGetAppointmentsByPatientConError() {
        when(agendaService.findByPaciente(100L)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<Map<String, Object>> response = controller.getAppointmentsByPatient(100L);

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("001", body.get("code"));
    }

    @Test
    void testGetAppointmentsByPatientListaVacia() {
        when(agendaService.findByPaciente(999L)).thenReturn(Collections.emptyList());

        ResponseEntity<Map<String, Object>> response = controller.getAppointmentsByPatient(999L);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("000", body.get("code"));
        assertTrue(((List<?>) body.get("data")).isEmpty());
    }

    @Test
    void testCreateAppointment() {
        when(agendaService.create(any(AgendaExamenDTO.class))).thenReturn(agendaDTO);

        ResponseEntity<Map<String, Object>> response = controller.createAppointment(agendaDTO);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        assertEquals(agendaDTO, body.get("data"));
        verify(agendaService).create(any(AgendaExamenDTO.class));
    }

    @Test
    void testCreateAppointmentConError() {
        when(agendaService.create(any(AgendaExamenDTO.class))).thenThrow(new RuntimeException("Creation error"));

        ResponseEntity<Map<String, Object>> response = controller.createAppointment(agendaDTO);

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("001", body.get("code"));
    }

    @Test
    void testUpdateAppointmentConFechaHora() {
        AgendaExamenDTO updateDTO = new AgendaExamenDTO();
        LocalDateTime nuevaFecha = LocalDateTime.of(2025, 12, 30, 15, 0);
        updateDTO.setFechaHora(nuevaFecha);

        when(agendaService.updateFechaHora(1L, nuevaFecha)).thenReturn(agendaDTO);

        ResponseEntity<Map<String, Object>> response = controller.updateAppointment(1L, updateDTO);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("000", body.get("code"));
        assertTrue(body.get("description").toString().contains("Fecha/hora"));
        verify(agendaService).updateFechaHora(1L, nuevaFecha);
        verify(agendaService, never()).update(anyLong(), any(AgendaExamenDTO.class));
    }

    @Test
    void testUpdateAppointmentCompleto() {
        AgendaExamenDTO updateDTO = new AgendaExamenDTO();
        updateDTO.setEstado("ATENDIDA");
        // No tiene fechaHora, entonces hace update completo

        when(agendaService.update(1L, updateDTO)).thenReturn(agendaDTO);

        ResponseEntity<Map<String, Object>> response = controller.updateAppointment(1L, updateDTO);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("000", body.get("code"));
        verify(agendaService).update(1L, updateDTO);
        verify(agendaService, never()).updateFechaHora(anyLong(), any(LocalDateTime.class));
    }

    @Test
    void testUpdateAppointmentConError() {
        AgendaExamenDTO updateDTO = new AgendaExamenDTO();
        when(agendaService.update(1L, updateDTO)).thenThrow(new RuntimeException("Update error"));

        ResponseEntity<Map<String, Object>> response = controller.updateAppointment(1L, updateDTO);

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("001", body.get("code"));
    }

    @Test
    void testCancelAppointment() {
        when(agendaService.cancelar(1L)).thenReturn(agendaDTO);

        ResponseEntity<Map<String, Object>> response = controller.cancelAppointment(1L);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        assertTrue(body.get("description").toString().contains("cancelada"));
        verify(agendaService).cancelar(1L);
    }

    @Test
    void testCancelAppointmentConError() {
        when(agendaService.cancelar(1L)).thenThrow(new RuntimeException("Cannot cancel"));

        ResponseEntity<Map<String, Object>> response = controller.cancelAppointment(1L);

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("001", body.get("code"));
    }

    @Test
    void testDeleteAppointment() {
        doNothing().when(agendaService).delete(1L);

        ResponseEntity<Map<String, Object>> response = controller.deleteAppointment(1L);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("000", body.get("code"));
        verify(agendaService).delete(1L);
    }

    @Test
    void testDeleteAppointmentConError() {
        doThrow(new RuntimeException("Delete error")).when(agendaService).delete(1L);

        ResponseEntity<Map<String, Object>> response = controller.deleteAppointment(1L);

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("001", body.get("code"));
    }

    @Test
    void testGetAllAppointmentsListaVacia() {
        when(agendaService.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<Map<String, Object>> response = controller.getAllAppointments();

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("000", body.get("code"));
        assertTrue(((List<?>) body.get("data")).isEmpty());
    }

    @Test
    void testUpdateAppointmentConFechaHoraYError() {
        AgendaExamenDTO updateDTO = new AgendaExamenDTO();
        LocalDateTime nuevaFecha = LocalDateTime.of(2025, 12, 30, 15, 0);
        updateDTO.setFechaHora(nuevaFecha);

        when(agendaService.updateFechaHora(1L, nuevaFecha)).thenThrow(new RuntimeException("Cannot update"));

        ResponseEntity<Map<String, Object>> response = controller.updateAppointment(1L, updateDTO);

        assertEquals(500, response.getStatusCode().value());
        Map<String, Object> body = response.getBody();
        assertEquals("001", body.get("code"));
    }
}
