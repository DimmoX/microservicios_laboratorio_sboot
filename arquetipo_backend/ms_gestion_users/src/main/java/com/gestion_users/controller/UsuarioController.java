// Controlador REST para Usuario
package com.gestion_users.controller;

import com.gestion_users.dto.UsuarioDTO;
import com.gestion_users.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ejemplo de controlador CRUD para Usuario
 * Incluye endpoints básicos y respuesta estructurada
 */
@RestController
@RequestMapping("/users")
public class UsuarioController {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);
    private final UsuarioService service;
    public UsuarioController(UsuarioService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<Map<String, Object>> all() {
        logger.info("GET: /users -> Listar usuarios");
        Map<String, Object> response = new LinkedHashMap<>();
        List<UsuarioDTO> users = service.findAll();
        response.put("code", "000");
        response.put("description", "Usuarios obtenidos exitosamente");
        response.put("data", users);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> one(@PathVariable Long id) {
        logger.info("GET: /users/{} -> Obtener usuario", id);
        Map<String, Object> response = new LinkedHashMap<>();
        UsuarioDTO user = service.findById(id);
        response.put("code", "000");
        response.put("description", "Usuario obtenido exitosamente");
        response.put("data", user);
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody UsuarioDTO dto) {
        logger.info("POST: /users -> Crear usuario");
        Map<String, Object> response = new LinkedHashMap<>();
        UsuarioDTO created = service.create(dto);
        response.put("code", "000");
        response.put("description", "Usuario creado exitosamente");
        response.put("data", created);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        logger.info("PUT: /users/{} -> Actualizar usuario", id);
        Map<String, Object> response = new LinkedHashMap<>();
        UsuarioDTO updated = service.update(id, dto);
        response.put("code", "000");
        response.put("description", "Usuario actualizado exitosamente");
        response.put("data", updated);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        logger.info("DELETE: /users/{} -> Eliminar usuario", id);
        Map<String, Object> response = new LinkedHashMap<>();
        service.delete(id);
        response.put("code", "000");
        response.put("description", "Usuario eliminado exitosamente");
        response.put("data", new LinkedHashMap<>());
        return ResponseEntity.ok(response);
    }
}
