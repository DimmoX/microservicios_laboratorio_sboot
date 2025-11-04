package com.gestion_labs.ms_gestion_labs.controller;

import com.gestion_labs.ms_gestion_labs.model.LabExamKey;
import com.gestion_labs.ms_gestion_labs.model.LabExamModel;
import com.gestion_labs.ms_gestion_labs.service.lab_exam.LabExamService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController 
@RequestMapping("/lab-exam")
public class LabExamController {

    private static final Logger logger = LoggerFactory.getLogger(LabExamController.class);
    private final LabExamService service;
    
    public LabExamController(LabExamService service) { 
        this.service = service; 
    }

    @GetMapping 
    public ResponseEntity<Map<String, Object>> all() {
        logger.info("GET: /lab-exam -> Listar todas las relaciones laboratorio-examen");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            List<LabExamModel> labExams = service.listAll();
            logger.info("Se encontraron {} relaciones laboratorio-examen", labExams.size());
            
            response.put("code", "000");
            response.put("description", "Relaciones laboratorio-examen obtenidas exitosamente");
            response.put("data", labExams);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener relaciones laboratorio-examen: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al obtener relaciones laboratorio-examen");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/lab/{labId}") 
    public ResponseEntity<Map<String, Object>> byLab(@PathVariable Long labId) {
        logger.info("GET: /lab-exam/lab/{} -> Listar exámenes de laboratorio", labId);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            List<LabExamModel> labExams = service.listByLaboratorio(labId);
            logger.info("Se encontraron {} exámenes para laboratorio ID: {}", labExams.size(), labId);
            
            response.put("code", "000");
            response.put("description", "Exámenes del laboratorio obtenidos exitosamente");
            response.put("data", labExams);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener exámenes del laboratorio {}: {}", labId, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al obtener exámenes del laboratorio");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/exam/{examId}") 
    public ResponseEntity<Map<String, Object>> byExam(@PathVariable Long examId) {
        logger.info("GET: /lab-exam/exam/{} -> Listar laboratorios que ofrecen el examen", examId);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            List<LabExamModel> labExams = service.listByExamen(examId);
            logger.info("Se encontraron {} laboratorios para examen ID: {}", labExams.size(), examId);
            
            response.put("code", "000");
            response.put("description", "Laboratorios que ofrecen el examen obtenidos exitosamente");
            response.put("data", labExams);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener laboratorios del examen {}: {}", examId, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al obtener laboratorios del examen");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/lab/{labId}/exam/{examId}")
    public ResponseEntity<Map<String, Object>> one(@PathVariable Long labId, @PathVariable Long examId) {
        logger.info("GET: /lab-exam/lab/{}/exam/{} -> Obtener relación específica", labId, examId);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            LabExamModel labExam = service.get(labId, examId);
            logger.info("Relación laboratorio {} - examen {} encontrada", labId, examId);
            
            response.put("code", "000");
            response.put("description", "Relación laboratorio-examen obtenida exitosamente");
            response.put("data", labExam);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener relación lab {} - exam {}: {}", labId, examId, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al obtener relación laboratorio-examen");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> upsert(@RequestBody LabExamModel m) {
        logger.info("POST: /lab-exam -> Crear/actualizar relación laboratorio-examen");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            if (m.getId() == null) {
                throw new RuntimeException("Debe enviar id (idLaboratorio, idExamen)");
            }
            
            LabExamModel saved = service.upsert(m);
            logger.info("Relación laboratorio-examen guardada exitosamente");
            
            response.put("code", "000");
            response.put("description", "Relación laboratorio-examen guardada exitosamente");
            response.put("data", saved);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al guardar relación laboratorio-examen: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al guardar relación laboratorio-examen: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/lab/{labId}/exam/{examId}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long labId, @PathVariable Long examId, @RequestBody LabExamModel m) {
        logger.info("PUT: /lab-exam/lab/{}/exam/{} -> Actualizar relación", labId, examId);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            m.setId(new LabExamKey(labId, examId));
            LabExamModel updated = service.upsert(m);
            logger.info("Relación lab {} - exam {} actualizada exitosamente", labId, examId);
            
            response.put("code", "000");
            response.put("description", "Relación laboratorio-examen actualizada exitosamente");
            response.put("data", updated);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al actualizar relación lab {} - exam {}: {}", labId, examId, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al actualizar relación laboratorio-examen");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/lab/{labId}/exam/{examId}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long labId, @PathVariable Long examId) {
        logger.info("DELETE: /lab-exam/lab/{}/exam/{} -> Eliminar relación", labId, examId);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            service.delete(labId, examId);
            logger.info("Relación lab {} - exam {} eliminada exitosamente", labId, examId);
            
            response.put("code", "000");
            response.put("description", "Relación laboratorio-examen eliminada exitosamente");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al eliminar relación lab {} - exam {}: {}", labId, examId, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al eliminar relación laboratorio-examen");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}