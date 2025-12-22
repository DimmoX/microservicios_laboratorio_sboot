package com.gestion_labs.ms_gestion_labs.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion_labs.ms_gestion_labs.dto.AgendaExamenDTO;
import com.gestion_labs.ms_gestion_labs.service.agenda.AgendaService;

@RestController 
@RequestMapping("/agenda")
public class AgendaController {

    private static final Logger logger = LoggerFactory.getLogger(AgendaController.class);
    private final AgendaService service;
    
    public AgendaController(AgendaService service) { 
        this.service = service; 
    }

    /**
     * Listar todas las agendas - Solo LAB_EMPLOYEE y ADMIN
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllAppointments() {
        logger.info("GET: /agenda -> Listar todas las agendas de exámenes");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            List<AgendaExamenDTO> agendas = service.findAll();
            logger.info("Se encontraron {} agendas de exámenes", agendas.size());
            
            response.put("code", "000");
            response.put("description", "Agendas de exámenes obtenidas exitosamente");
            response.put("data", agendas);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener agendas de exámenes: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al obtener agendas de exámenes");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Obtener agenda por ID - Solo LAB_EMPLOYEE y ADMIN
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAppointmentById(@PathVariable Long id) {
        logger.info("GET: /agenda/{} -> Obtener agenda de examen por ID", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            AgendaExamenDTO agenda = service.findById(id);
            logger.info("Agenda de examen con ID: {} encontrada", id);
            
            response.put("code", "000");
            response.put("description", "Agenda de examen obtenida exitosamente");
            response.put("data", agenda);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener agenda de examen con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al obtener agenda de examen con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Listar agendas por paciente
     * PATIENT: solo puede ver sus propias agendas (validación en servicio)
     * EMPLOYEE y ADMIN: pueden ver agendas de cualquier paciente
     */
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAppointmentsByPatient(@PathVariable Long pacienteId) {
        logger.info("GET: /agenda/paciente/{} -> Listar agendas de exámenes por paciente", pacienteId);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            // La validación de ownership se realiza en el servicio
            List<AgendaExamenDTO> agendas = service.findByPaciente(pacienteId);
            logger.info("Se encontraron {} agendas para paciente ID: {}", agendas.size(), pacienteId);
            
            response.put("code", "000");
            response.put("description", "Agendas de exámenes del paciente obtenidas exitosamente");
            response.put("data", agendas);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener agendas del paciente {}: {}", pacienteId, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al obtener agendas del paciente");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Crear agenda de examen
     * PATIENT: puede agendar exámenes solo para sí mismo (validación en servicio)
     * EMPLOYEE y ADMIN: pueden agendar para cualquier paciente
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('PATIENT', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> createAppointment(@RequestBody AgendaExamenDTO m) {
        logger.info("POST: /agenda -> Crear nueva agenda de examen");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            // La validación de ownership se realiza en el servicio
            AgendaExamenDTO created = service.create(m);
            logger.info("Agenda de examen creada exitosamente con ID: {}", created.getId());
            
            response.put("code", "000");
            response.put("description", "Agenda de examen creada exitosamente");
            response.put("data", created);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al crear agenda de examen: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al crear agenda de examen");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Actualizar agenda - EMPLOYEE y ADMIN pueden actualizar fecha/hora
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> updateAppointment(@PathVariable Long id, @RequestBody AgendaExamenDTO m) {
        logger.info("PUT: /agenda/{} -> Actualizar agenda de examen", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            // Si viene fechaHora en el DTO, actualizar solo fecha/hora
            if (m.getFechaHora() != null) {
                AgendaExamenDTO updated = service.updateFechaHora(id, m.getFechaHora());
                logger.info("Fecha/hora de agenda con ID: {} actualizada exitosamente", id);
                
                response.put("code", "000");
                response.put("description", "Fecha/hora actualizada exitosamente");
                response.put("data", updated);
                
                return ResponseEntity.ok(response);
            }
            
            // Actualización completa
            AgendaExamenDTO updated = service.update(id, m);
            logger.info("Agenda de examen con ID: {} actualizada exitosamente", id);
            
            response.put("code", "000");
            response.put("description", "Agenda de examen actualizada exitosamente");
            response.put("data", updated);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al actualizar agenda de examen con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al actualizar agenda de examen con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Cancelar una cita - Cambia estado a CANCELADA
     * PATIENT, EMPLOYEE y ADMIN pueden cancelar
     */
    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> cancelAppointment(@PathVariable Long id) {
        logger.info("PUT: /agenda/{}/cancelar -> Cancelar agenda de examen", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            AgendaExamenDTO cancelled = service.cancelar(id);
            logger.info("Agenda de examen con ID: {} cancelada exitosamente", id);
            
            response.put("code", "000");
            response.put("description", "Agenda de examen cancelada exitosamente");
            response.put("data", cancelled);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al cancelar agenda de examen con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al cancelar agenda de examen con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Eliminar agenda - Solo ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteAppointment(@PathVariable Long id) {
        logger.info("DELETE: /agenda/{} -> Eliminar agenda de examen", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            service.delete(id);
            logger.info("Agenda de examen con ID: {} eliminada exitosamente", id);
            
            response.put("code", "000");
            response.put("description", "Agenda de examen eliminada exitosamente");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al eliminar agenda de examen con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al eliminar agenda de examen con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}
