package com.gestion_users.ms_gestion_users.controller;

import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import com.gestion_users.ms_gestion_users.service.user.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService service;
    public UserController(UserService service) { this.service = service; }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> getAll() {
        logger.info("GET: /users -> Listar todos los usuarios");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            List<UsuarioModel> users = service.findAll();
            logger.info("Se encontraron {} usuarios", users.size());
            
            response.put("code", "000");
            response.put("description", "Usuarios obtenidos exitosamente");
            response.put("data", users);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener usuarios: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al obtener usuarios");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        logger.info("GET: /users/{} -> Obtener usuario por ID", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            UsuarioModel user = service.findById(id);
            logger.info("Usuario con ID: {} encontrado: {}", id, user.getUsername());
            
            response.put("code", "000");
            response.put("description", "Usuario obtenido exitosamente");
            response.put("data", user);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al obtener usuario con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al obtener usuario con ID: " + id);
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    // ========== ENDPOINTS SOLO PARA ADMIN ==========

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UsuarioModel usuario) {
        logger.info("POST: /users -> Crear nuevo usuario: {}", usuario.getUsername());
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            UsuarioModel nuevoUsuario = service.create(usuario);
            logger.info("Usuario creado exitosamente con ID: {}", nuevoUsuario.getId());
            
            response.put("code", "000");
            response.put("description", "Usuario creado exitosamente");
            response.put("data", nuevoUsuario);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al crear usuario: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al crear usuario: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody UsuarioModel usuario) {
        logger.info("PUT: /users/{} -> Actualizar usuario", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            UsuarioModel usuarioActualizado = service.update(id, usuario);
            logger.info("Usuario con ID: {} actualizado exitosamente", id);
            
            response.put("code", "000");
            response.put("description", "Usuario actualizado exitosamente");
            response.put("data", usuarioActualizado);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al actualizar usuario con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al actualizar usuario: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        logger.info("DELETE: /users/{} -> Eliminar usuario", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            service.delete(id);
            logger.info("Usuario con ID: {} eliminado exitosamente", id);
            
            response.put("code", "000");
            response.put("description", "Usuario eliminado exitosamente");
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al eliminar usuario con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al eliminar usuario: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}