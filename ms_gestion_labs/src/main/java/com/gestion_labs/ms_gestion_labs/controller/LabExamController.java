package com.gestion_labs.ms_gestion_labs.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion_labs.ms_gestion_labs.dto.LabExamDTO;
import com.gestion_labs.ms_gestion_labs.dto.LaboratorioDTO;
import com.gestion_labs.ms_gestion_labs.model.ExamenModel;
import com.gestion_labs.ms_gestion_labs.model.LabExamKey;
import com.gestion_labs.ms_gestion_labs.model.LabExamModel;
import com.gestion_labs.ms_gestion_labs.service.examen.ExamenService;
import com.gestion_labs.ms_gestion_labs.service.lab_exam.LabExamService;
import com.gestion_labs.ms_gestion_labs.service.laboratorio.LaboratorioService;

@RestController 
@RequestMapping("/lab-exams")
public class LabExamController {

    private static final Logger logger = LoggerFactory.getLogger(LabExamController.class);
    private final LabExamService service;
    private final LaboratorioService laboratorioService;
    private final ExamenService examenService;
    
    public LabExamController(LabExamService service, LaboratorioService laboratorioService, ExamenService examenService) { 
        this.service = service;
        this.laboratorioService = laboratorioService;
        this.examenService = examenService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllLabExamRelations() {
        logger.info("GET: /lab-exams -> Listar todas las relaciones laboratorio-examen");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            List<LabExamModel> labExams = service.listAll();
            List<LabExamDTO> labExamDTOs = new ArrayList<>();
            
            for (LabExamModel labExam : labExams) {
                LabExamDTO dto = new LabExamDTO();
                dto.setIdLaboratorio(labExam.getId().getIdLaboratorio());
                dto.setIdExamen(labExam.getId().getIdExamen());
                dto.setPrecio(labExam.getPrecio());
                dto.setVigenteDesde(labExam.getVigenteDesde());
                dto.setVigenteHasta(labExam.getVigenteHasta());
                
                // Obtener nombres
                try {
                    LaboratorioDTO lab = laboratorioService.findById(labExam.getId().getIdLaboratorio());
                    dto.setNombreLab(lab.getNombre());
                } catch (Exception e) {
                    dto.setNombreLab("Laboratorio no encontrado");
                }
                
                try {
                    ExamenModel examen = examenService.findById(labExam.getId().getIdExamen());
                    dto.setNombreExamen(examen.getNombre());
                } catch (Exception e) {
                    dto.setNombreExamen("Examen no encontrado");
                }
                
                labExamDTOs.add(dto);
            }
            
            logger.info("Se encontraron {} relaciones laboratorio-examen", labExamDTOs.size());
            
            response.put("code", "000");
            response.put("description", "Relaciones laboratorio-examen obtenidas exitosamente");
            response.put("data", labExamDTOs);
            
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
    public ResponseEntity<Map<String, Object>> getExamsByLab(@PathVariable Long labId) {
        logger.info("GET: /lab-exams/lab/{} -> Listar exámenes de laboratorio", labId);
        
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
    public ResponseEntity<Map<String, Object>> getLabsByExam(@PathVariable Long examId) {
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
    public ResponseEntity<Map<String, Object>> getLabExamRelation(@PathVariable Long labId, @PathVariable Long examId) {
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
    public ResponseEntity<Map<String, Object>> upsertLabExamRelation(@RequestBody Map<String, Object> payload) {
        logger.info("POST: /lab-exam -> Crear/actualizar relación laboratorio-examen");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            LabExamModel m = new LabExamModel();
            
            // Soportar dos formatos: con id anidado o con idLaboratorio/idExamen directos
            if (payload.containsKey("id") && payload.get("id") instanceof Map) {
                Map<String, Object> idMap = (Map<String, Object>) payload.get("id");
                Long labId = getLongFromMap(idMap, "idLaboratorio");
                Long examId = getLongFromMap(idMap, "idExamen");
                m.setId(new LabExamKey(labId, examId));
            } else if (payload.containsKey("idLaboratorio") && payload.containsKey("idExamen")) {
                Long labId = getLongFromMap(payload, "idLaboratorio");
                Long examId = getLongFromMap(payload, "idExamen");
                m.setId(new LabExamKey(labId, examId));
            } else {
                throw new RuntimeException("Debe enviar id (idLaboratorio, idExamen)");
            }
            
            // Mapear precio
            if (payload.containsKey("precio")) {
                Object precioObj = payload.get("precio");
                if (precioObj instanceof Number) {
                    m.setPrecio(new BigDecimal(precioObj.toString()));
                }
            }
            
            // Mapear fechas vigentes
            if (payload.containsKey("vigenteDesde") && payload.get("vigenteDesde") != null) {
                m.setVigenteDesde(LocalDate.parse(payload.get("vigenteDesde").toString()));
            }
            if (payload.containsKey("vigenteHasta") && payload.get("vigenteHasta") != null) {
                m.setVigenteHasta(LocalDate.parse(payload.get("vigenteHasta").toString()));
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
    public ResponseEntity<Map<String, Object>> updateLabExamRelation(@PathVariable Long labId, @PathVariable Long examId, @RequestBody LabExamModel m) {
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
    public ResponseEntity<Map<String, Object>> deleteLabExamRelation(@PathVariable Long labId, @PathVariable Long examId) {
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
    
    // Método helper para convertir diferentes tipos numéricos a Long
    private Long getLongFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            return Long.parseLong((String) value);
        }
        throw new IllegalArgumentException("No se puede convertir " + value.getClass() + " a Long");
    }
}