package com.gestion_users.ms_gestion_users.controller;

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

import com.gestion_users.ms_gestion_users.model.PacienteModel;
import com.gestion_users.ms_gestion_users.service.paciente.PacienteService;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private static final Logger logger = LoggerFactory.getLogger(PacienteController.class);
    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> getAllPatients() {
        logger.info("GET: /pacientes -> Listar todos los pacientes");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            List<PacienteModel> pacientes = service.findAll();
            logger.info("Se encontraron {} pacientes", pacientes.size());
            
            response.put("code", "000");
            response.put("description", "Pacientes obtenidos exitosamente");
            response.put("data", pacientes);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener pacientes: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al obtener pacientes");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> getPatientById(@PathVariable Long id) {
        logger.info("GET: /pacientes/{} -> Obtener paciente por ID", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            PacienteModel paciente = service.findById(id);
            logger.info("Paciente con ID: {} encontrado", id);
            
            response.put("code", "000");
            response.put("description", "Paciente obtenido exitosamente");
            response.put("data", paciente);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener paciente con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al obtener paciente con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> createPatient(@RequestBody PacienteModel paciente) {
        logger.info("POST: /pacientes -> Crear nuevo paciente");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            PacienteModel createdPaciente = service.create(paciente);
            logger.info("Paciente creado exitosamente con ID: {}", createdPaciente.getId());
            
            response.put("code", "000");
            response.put("description", "Paciente creado exitosamente");
            response.put("data", createdPaciente);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al crear paciente: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al crear paciente");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updatePatient(@PathVariable Long id, @RequestBody PacienteModel paciente) {
        logger.info("PUT: /pacientes/{} -> Actualizar paciente", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            PacienteModel updatedPaciente = service.update(id, paciente);
            logger.info("Paciente con ID: {} actualizado exitosamente", id);
            
            response.put("code", "000");
            response.put("description", "Paciente actualizado exitosamente");
            response.put("data", updatedPaciente);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al actualizar paciente con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al actualizar paciente con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> deletePatient(@PathVariable Long id) {
        logger.info("DELETE: /pacientes/{} -> Eliminar paciente", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            service.delete(id);
            logger.info("Paciente con ID: {} eliminado exitosamente", id);
            
            response.put("code", "000");
            response.put("description", "Paciente eliminado exitosamente");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al eliminar paciente con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al eliminar paciente con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}