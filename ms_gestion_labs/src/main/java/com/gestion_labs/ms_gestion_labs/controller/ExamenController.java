package com.gestion_labs.ms_gestion_labs.controller;

import com.gestion_labs.ms_gestion_labs.model.ExamenModel;
import com.gestion_labs.ms_gestion_labs.service.examen.ExamenService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController 
@RequestMapping("/exams")
public class ExamenController {

    private static final Logger logger = LoggerFactory.getLogger(ExamenController.class);
    private static final String DESCRIPTION_KEY = "description";
    private final ExamenService service;
    
    public ExamenController(ExamenService service) { 
        this.service = service; 
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllExams() {
        logger.info("GET: /exams -> Listar todos los exámenes");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            List<ExamenModel> exams = service.findAll();
            logger.info("Se encontraron {} exámenes", exams.size());
            
            response.put("code", "000");
            response.put(DESCRIPTION_KEY, "Exámenes obtenidos exitosamente");
            response.put("data", exams);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener exámenes: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put(DESCRIPTION_KEY, "Error al obtener exámenes");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getExamById(@PathVariable Long id) {
        logger.info("GET: /exams/{} -> Obtener examen por ID", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            ExamenModel exam = service.findById(id);
            logger.info("Examen con ID: {} encontrado", id);
            
            response.put("code", "000");
            response.put(DESCRIPTION_KEY, "Examen obtenido exitosamente");
            response.put("data", exam);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener examen con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put(DESCRIPTION_KEY, "Error al obtener examen con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createExam(@RequestBody ExamenModel m) {
        logger.info("POST: /exams -> Crear nuevo examen");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            ExamenModel created = service.create(m);
            logger.info("Examen creado exitosamente con ID: {}", created.getId());
            
            response.put("code", "000");
            response.put(DESCRIPTION_KEY, "Examen creado exitosamente");
            response.put("data", created);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al crear examen: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put(DESCRIPTION_KEY, "Error al crear examen");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateExam(@PathVariable Long id, @RequestBody ExamenModel m) {
        logger.info("PUT: /exams/{} -> Actualizar examen", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            ExamenModel updated = service.update(id, m);
            logger.info("Examen con ID: {} actualizado exitosamente", id);
            
            response.put("code", "000");
            response.put(DESCRIPTION_KEY, "Examen actualizado exitosamente");
            response.put("data", updated);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al actualizar examen con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put(DESCRIPTION_KEY, "Error al actualizar examen con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteExam(@PathVariable Long id) {
        logger.info("DELETE: /exams/{} -> Eliminar examen", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            service.delete(id);
            logger.info("Examen con ID: {} eliminado exitosamente", id);
            
            response.put("code", "000");
            response.put(DESCRIPTION_KEY, "Examen eliminado exitosamente");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al eliminar examen con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put(DESCRIPTION_KEY, "Error al eliminar examen con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}