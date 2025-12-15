package com.gestion_labs.ms_gestion_labs.controller;

import com.gestion_labs.ms_gestion_labs.dto.LaboratorioDTO;
import com.gestion_labs.ms_gestion_labs.service.laboratorio.LaboratorioService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController 
@RequestMapping("/labs")
public class LaboratorioController {

    private static final Logger logger = LoggerFactory.getLogger(LaboratorioController.class);
    private final LaboratorioService service;
    
    public LaboratorioController(LaboratorioService service) { 
        this.service = service; 
    }

    /**
     * Listar todos los laboratorios - PÚBLICO
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllLabs() {
        logger.info("GET: /labs -> Listar todos los laboratorios");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            List<LaboratorioDTO> labs = service.findAll();
            logger.info("Se encontraron {} laboratorios", labs.size());
            
            response.put("code", "000");
            response.put("description", "Laboratorios obtenidos exitosamente");
            response.put("data", labs);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener laboratorios: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al obtener laboratorios");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Obtener laboratorio por ID - PÚBLICO
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getLabById(@PathVariable Long id) {
        logger.info("GET: /labs/{} -> Obtener laboratorio por ID", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            LaboratorioDTO lab = service.findById(id);
            logger.info("Laboratorio con ID: {} encontrado", id);
            
            response.put("code", "000");
            response.put("description", "Laboratorio obtenido exitosamente");
            response.put("data", lab);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener laboratorio con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al obtener laboratorio con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Crear laboratorio - Solo ADMIN
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createLab(@RequestBody LaboratorioDTO dto) {
        logger.info("POST: /labs -> Crear nuevo laboratorio");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            LaboratorioDTO created = service.create(dto);
            logger.info("Laboratorio creado exitosamente con ID: {}", created.getId());
            
            response.put("code", "000");
            response.put("description", "Laboratorio creado exitosamente");
            response.put("data", created);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al crear laboratorio: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al crear laboratorio");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Actualizar laboratorio - Solo ADMIN
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateLab(@PathVariable Long id, @RequestBody LaboratorioDTO dto) {
        logger.info("PUT: /labs/{} -> Actualizar laboratorio", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            LaboratorioDTO updated = service.update(id, dto);
            logger.info("Laboratorio con ID: {} actualizado exitosamente", id);
            
            response.put("code", "000");
            response.put("description", "Laboratorio actualizado exitosamente");
            response.put("data", updated);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al actualizar laboratorio con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al actualizar laboratorio con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Eliminar laboratorio - Solo ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteLab(@PathVariable Long id) {
        logger.info("DELETE: /labs/{} -> Eliminar laboratorio", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            service.delete(id);
            logger.info("Laboratorio con ID: {} eliminado exitosamente", id);
            
            response.put("code", "000");
            response.put("description", "Laboratorio eliminado exitosamente");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al eliminar laboratorio con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al eliminar laboratorio con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}