package com.gestion_users.ms_gestion_users.controller;

import com.gestion_users.ms_gestion_users.model.EmpleadoModel;
import com.gestion_users.ms_gestion_users.service.empleado.EmpleadoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoController.class);
    private final EmpleadoService service;

    public EmpleadoController(EmpleadoService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> getAll() {
        logger.info("GET: /empleados -> Listar todos los empleados");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            List<EmpleadoModel> empleados = service.findAll();
            logger.info("Se encontraron {} empleados", empleados.size());
            
            response.put("code", "000");
            response.put("description", "Empleados obtenidos exitosamente");
            response.put("data", empleados);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener empleados: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al obtener empleados");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        logger.info("GET: /empleados/{} -> Obtener empleado por ID", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            EmpleadoModel empleado = service.findById(id);
            logger.info("Empleado con ID: {} encontrado", id);
            
            response.put("code", "000");
            response.put("description", "Empleado obtenido exitosamente");
            response.put("data", empleado);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener empleado con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al obtener empleado con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> create(@RequestBody EmpleadoModel empleado) {
        logger.info("POST: /empleados -> Crear nuevo empleado");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            EmpleadoModel createdEmpleado = service.create(empleado);
            logger.info("Empleado creado exitosamente con ID: {}", createdEmpleado.getId());
            
            response.put("code", "000");
            response.put("description", "Empleado creado exitosamente");
            response.put("data", createdEmpleado);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al crear empleado: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al crear empleado");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody EmpleadoModel empleado) {
        logger.info("PUT: /empleados/{} -> Actualizar empleado", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            EmpleadoModel updatedEmpleado = service.update(id, empleado);
            logger.info("Empleado con ID: {} actualizado exitosamente", id);
            
            response.put("code", "000");
            response.put("description", "Empleado actualizado exitosamente");
            response.put("data", updatedEmpleado);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al actualizar empleado con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al actualizar empleado con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        logger.info("DELETE: /empleados/{} -> Eliminar empleado", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            service.delete(id);
            logger.info("Empleado con ID: {} eliminado exitosamente", id);
            
            response.put("code", "000");
            response.put("description", "Empleado eliminado exitosamente");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al eliminar empleado con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al eliminar empleado con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}