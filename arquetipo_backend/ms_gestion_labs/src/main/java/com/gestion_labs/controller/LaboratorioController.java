// Controlador REST para Laboratorio
package com.gestion_labs.controller;

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

import com.gestion_labs.dto.LaboratorioDTO;
import com.gestion_labs.service.LaboratorioService;

/**
 * Ejemplo de controlador CRUD para Laboratorio
 * Incluye endpoints básicos y respuesta estructurada
 */
@RestController
@RequestMapping("/labs")
public class LaboratorioController {
    private static final Logger logger = LoggerFactory.getLogger(LaboratorioController.class);
    private final LaboratorioService service;
    public LaboratorioController(LaboratorioService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<Map<String, Object>> all() {
        logger.info("GET: /labs -> Listar laboratorios");
        Map<String, Object> response = new LinkedHashMap<>();
        List<LaboratorioDTO> labs = service.findAll();
        response.put("code", "000");
        response.put("description", "Laboratorios obtenidos exitosamente");
        response.put("data", labs);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> one(@PathVariable Long id) {
        logger.info("GET: /labs/{} -> Obtener laboratorio", id);
        Map<String, Object> response = new LinkedHashMap<>();
        LaboratorioDTO lab = service.findById(id);
        response.put("code", "000");
        response.put("description", "Laboratorio obtenido exitosamente");
        response.put("data", lab);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody LaboratorioDTO dto) {
        logger.info("POST: /labs -> Crear laboratorio");
        Map<String, Object> response = new LinkedHashMap<>();
        LaboratorioDTO created = service.create(dto);
        response.put("code", "000");
        response.put("description", "Laboratorio creado exitosamente");
        response.put("data", created);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody LaboratorioDTO dto) {
        logger.info("PUT: /labs/{} -> Actualizar laboratorio", id);
        Map<String, Object> response = new LinkedHashMap<>();
        LaboratorioDTO updated = service.update(id, dto);
        response.put("code", "000");
        response.put("description", "Laboratorio actualizado exitosamente");
        response.put("data", updated);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        logger.info("DELETE: /labs/{} -> Eliminar laboratorio", id);
        Map<String, Object> response = new LinkedHashMap<>();
        service.delete(id);
        response.put("code", "000");
        response.put("description", "Laboratorio eliminado exitosamente");
        response.put("data", new LinkedHashMap<>());
        return ResponseEntity.ok(response);
    }
}
