package com.gestion_labs.ms_gestion_labs.controller;

import com.gestion_labs.ms_gestion_labs.model.AgendaExamenModel;
import com.gestion_labs.ms_gestion_labs.service.agenda.AgendaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    @PreAuthorize("hasAnyRole('LAB_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllAppointments() {
        logger.info("GET: /agenda -> Listar todas las agendas de exámenes");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            List<AgendaExamenModel> agendas = service.findAll();
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
    @PreAuthorize("hasAnyRole('LAB_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAppointmentById(@PathVariable Long id) {
        logger.info("GET: /agenda/{} -> Obtener agenda de examen por ID", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            AgendaExamenModel agenda = service.findById(id);
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
     * LAB_EMPLOYEE y ADMIN: pueden ver agendas de cualquier paciente
     */
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'LAB_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAppointmentsByPatient(@PathVariable Long pacienteId) {
        logger.info("GET: /agenda/paciente/{} -> Listar agendas de exámenes por paciente", pacienteId);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            // La validación de ownership se realiza en el servicio
            List<AgendaExamenModel> agendas = service.findByPaciente(pacienteId);
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
     */
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Map<String, Object>> createAppointment(@RequestBody AgendaExamenModel m) {
        logger.info("POST: /agenda -> Crear nueva agenda de examen");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            // La validación de ownership se realiza en el servicio
            AgendaExamenModel created = service.create(m);
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
     * Actualizar agenda - Solo ADMIN
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateAppointment(@PathVariable Long id, @RequestBody AgendaExamenModel m) {
        logger.info("PUT: /agenda/{} -> Actualizar agenda de examen", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            AgendaExamenModel updated = service.update(id, m);
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
     * Eliminar agenda - Solo ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
