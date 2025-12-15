package com.gestion_resultados.ms_gestion_resultados.controller;

import com.gestion_resultados.ms_gestion_resultados.model.ResultadoExamenModel;
import com.gestion_resultados.ms_gestion_resultados.service.resultados.ResultadoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resultados")
public class ResultadoController {

    private static final Logger logger = LoggerFactory.getLogger(ResultadoController.class);
    private final ResultadoService service;

    public ResultadoController(ResultadoService service) {
        this.service = service;
    }

    /**
     * Listar todos los resultados - Solo LAB_EMPLOYEE y ADMIN
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('LAB_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllResults() {
        logger.info("GET: /resultados -> Listar todos los resultados de exámenes");

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            List<ResultadoExamenModel> resultados = service.findAll();
            logger.info("Se encontraron {} resultados de exámenes", resultados.size());

            response.put("code", "000");
            response.put("description", "Resultados de exámenes obtenidos exitosamente");
            response.put("data", resultados);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al obtener resultados de exámenes: {}", e.getMessage(), e);

            response.put("code", "001");
            response.put("description", "Error al obtener resultados de exámenes");
            response.put("data", new LinkedHashMap<>());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Obtener resultado por ID
     * PATIENT: solo puede ver sus propios resultados (validación en servicio)
     * LAB_EMPLOYEE y ADMIN: pueden ver cualquier resultado
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'LAB_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getResultById(@PathVariable Long id) {
        logger.info("GET: /resultados/{} -> Obtener resultado de examen por ID", id);

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            ResultadoExamenModel resultado = service.findById(id);
            logger.info("Resultado de examen con ID: {} encontrado", id);

            response.put("code", "000");
            response.put("description", "Resultado de examen obtenido exitosamente");
            response.put("data", resultado);

            return ResponseEntity.ok(response);

        } catch (org.springframework.security.access.AccessDeniedException e) {
            // Error de autorización (paciente intentando ver resultado de otro)
            logger.warn("Acceso denegado al resultado ID {}: {}", id, e.getMessage());

            response.put("code", "403");
            response.put("description", e.getMessage());
            response.put("data", new LinkedHashMap<>());

            return ResponseEntity.status(403).body(response);

        } catch (RuntimeException e) {
            // Resultado no encontrado
            if (e.getMessage().contains("no encontrado")) {
                logger.warn("Resultado no encontrado con ID: {}", id);

                response.put("code", "404");
                response.put("description", e.getMessage());
                response.put("data", new LinkedHashMap<>());

                return ResponseEntity.status(404).body(response);
            }

            // Otros errores
            logger.error("Error al obtener resultado con ID {}: {}", id, e.getMessage());

            response.put("code", "500");
            response.put("description", e.getMessage());
            response.put("data", new LinkedHashMap<>());

            return ResponseEntity.status(500).body(response);

        } catch (Exception e) {
            logger.error("Error inesperado al obtener resultado con ID {}: {}", id, e.getMessage(), e);

            response.put("code", "500");
            response.put("description", "Error interno del servidor: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Listar resultados por paciente
     * PATIENT: solo puede ver sus propios resultados (validación en servicio)
     * LAB_EMPLOYEE y ADMIN: pueden ver resultados de cualquier paciente
     */
    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'LAB_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getResultsByPatient(@PathVariable Long pacienteId) {
        logger.info("GET: /resultados/paciente/{} -> Listar resultados de exámenes por paciente", pacienteId);

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            // La validación de ownership se realiza en el servicio
            List<ResultadoExamenModel> resultados = service.findByPaciente(pacienteId);
            logger.info("Se encontraron {} resultados para paciente ID: {}", resultados.size(), pacienteId);

            response.put("code", "000");
            response.put("description", "Resultados de exámenes del paciente obtenidos exitosamente");
            response.put("data", resultados);

            return ResponseEntity.ok(response);

        } catch (org.springframework.security.access.AccessDeniedException e) {
            // Paciente intentando ver resultados de otro paciente
            logger.warn("Acceso denegado a resultados del paciente {}: {}", pacienteId, e.getMessage());

            response.put("code", "403");
            response.put("description", e.getMessage());
            response.put("data", new LinkedHashMap<>());

            return ResponseEntity.status(403).body(response);

        } catch (Exception e) {
            logger.error("Error al obtener resultados del paciente {}: {}", pacienteId, e.getMessage(), e);

            response.put("code", "500");
            response.put("description", "Error interno del servidor: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Crear resultado de examen - Solo LAB_EMPLOYEE y ADMIN
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('LAB_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> createResult(@RequestBody ResultadoExamenModel m) {
        logger.info("POST: /resultados -> Crear nuevo resultado de examen");

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            ResultadoExamenModel created = service.create(m);
            logger.info("Resultado de examen creado exitosamente con ID: {}", created.getId());

            response.put("code", "000");
            response.put("description", "Resultado de examen creado exitosamente");
            response.put("data", created);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            // Errores de validación (campos obligatorios, duplicados, etc.)
            logger.warn("Validación fallida al crear resultado: {}", e.getMessage());

            response.put("code", "400");
            response.put("description", e.getMessage());
            response.put("data", new LinkedHashMap<>());

            return ResponseEntity.status(400).body(response);

        } catch (Exception e) {
            // Otros errores inesperados
            logger.error("Error inesperado al crear resultado de examen: {}", e.getMessage(), e);

            response.put("code", "500");
            response.put("description", "Error interno del servidor: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Actualizar resultado - Solo LAB_EMPLOYEE y ADMIN
     * Se usa principalmente para cambiar el estado y establecer fechaResultado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('LAB_EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> updateResult(@PathVariable Long id, @RequestBody ResultadoExamenModel m) {
        logger.info("PUT: /resultados/{} -> Actualizar resultado de examen", id);

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            ResultadoExamenModel updated = service.update(id, m);
            logger.info("Resultado de examen con ID: {} actualizado exitosamente", id);

            response.put("code", "000");
            response.put("description", "Resultado de examen actualizado exitosamente");
            response.put("data", updated);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // Resultado no encontrado
            if (e.getMessage().contains("no encontrado")) {
                logger.warn("Resultado no encontrado con ID: {}", id);

                response.put("code", "404");
                response.put("description", e.getMessage());
                response.put("data", new LinkedHashMap<>());

                return ResponseEntity.status(404).body(response);
            }

            // Otros errores de validación
            logger.error("Error al actualizar resultado con ID {}: {}", id, e.getMessage());

            response.put("code", "400");
            response.put("description", e.getMessage());
            response.put("data", new LinkedHashMap<>());

            return ResponseEntity.status(400).body(response);

        } catch (Exception e) {
            logger.error("Error inesperado al actualizar resultado con ID {}: {}", id, e.getMessage(), e);

            response.put("code", "500");
            response.put("description", "Error interno del servidor: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());

            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Eliminar resultado - Solo ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteResult(@PathVariable Long id) {
        logger.info("DELETE: /resultados/{} -> Eliminar resultado de examen", id);

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            service.delete(id);
            logger.info("Resultado de examen con ID: {} eliminado exitosamente", id);

            response.put("code", "000");
            response.put("description", "Resultado de examen eliminado exitosamente");
            response.put("data", new LinkedHashMap<>());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al eliminar resultado de examen con ID: {}: {}", id, e.getMessage(), e);

            response.put("code", "001");
            response.put("description", "Error al eliminar resultado de examen con ID: " + id);
            response.put("data", new LinkedHashMap<>());

            return ResponseEntity.status(500).body(response);
        }
    }
}
