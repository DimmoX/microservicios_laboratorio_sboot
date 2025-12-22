package com.gestion_users.ms_gestion_users.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion_users.ms_gestion_users.dto.ChangePasswordRequest;
import com.gestion_users.ms_gestion_users.dto.UsuarioResponse;
import com.gestion_users.ms_gestion_users.dto.UsuarioUpdateRequest;
import com.gestion_users.ms_gestion_users.model.UsuarioModel;
import com.gestion_users.ms_gestion_users.service.user.UserService;
import com.gestion_users.ms_gestion_users.service.user.UsuarioProfileService;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService service;
    private final UsuarioProfileService usuarioProfileService;

    public UserController(UserService service, UsuarioProfileService usuarioProfileService) {
        this.service = service;
        this.usuarioProfileService = usuarioProfileService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        logger.info("GET: /users -> Listar todos los usuarios");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            List<UsuarioModel> users = service.findAll();
            logger.info("Se encontraron {} usuarios", users.size());

            List<UsuarioResponse> data = users.stream()
                .map(usuarioProfileService::buildProfile)
                .collect(Collectors.toList());
            
            response.put("code", "000");
            response.put("description", "Usuarios obtenidos exitosamente");
            response.put("data", data);
            
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        logger.info("GET: /users/{} -> Obtener usuario por ID", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            UsuarioModel user = service.findById(id);
            logger.info("Usuario con ID: {} encontrado: {}", id, user.getUsername());

            UsuarioResponse data = usuarioProfileService.buildProfile(user);
            
            response.put("code", "000");
            response.put("description", "Usuario obtenido exitosamente");
            response.put("data", data);
            
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
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody UsuarioModel usuario) {
        logger.info("POST: /users -> Crear nuevo usuario: {} con rol: {}", usuario.getUsername(), usuario.getRole());
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            // Obtener el rol del usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserRole = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("UNKNOWN");
            
            logger.info("Usuario autenticado con rol: {}", currentUserRole);
            
            // Validar permisos según el rol del usuario que crea
            String targetRole = usuario.getRole() != null ? usuario.getRole().toUpperCase() : "";
            
            if (currentUserRole.equals("EMPLOYEE")) {
                // EMPLOYEE solo puede crear usuarios PATIENT
                if (!targetRole.equals("PATIENT")) {
                    logger.warn("EMPLOYEE intentó crear usuario con rol: {}", targetRole);
                    response.put("code", "403");
                    response.put("description", "No tiene permisos para crear usuarios con rol " + targetRole + ". Solo puede crear usuarios PATIENT.");
                    response.put("data", new LinkedHashMap<>());
                    return ResponseEntity.status(403).body(response);
                }
            }
            // ADMIN puede crear usuarios con cualquier rol (no necesita validación adicional)
            
            UsuarioModel nuevoUsuario = service.create(usuario);
            logger.info("Usuario creado exitosamente con ID: {} y rol: {}", nuevoUsuario.getId(), nuevoUsuario.getRole());

            UsuarioResponse data = usuarioProfileService.buildProfile(nuevoUsuario);
            
            response.put("code", "000");
            response.put("description", "Usuario creado exitosamente");
            response.put("data", data);
            
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
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody UsuarioUpdateRequest updates) {
        logger.info("PUT: /users/{} -> Actualizar usuario (ADMIN)", id);
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            UsuarioModel usuarioActualizado = service.update(id, updates);
            logger.info("Usuario con ID: {} actualizado exitosamente", id);

            UsuarioResponse data = usuarioProfileService.buildProfile(usuarioActualizado);
            
            response.put("code", "000");
            response.put("description", "Usuario actualizado exitosamente");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al actualizar usuario con ID: {}: {}", id, e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al actualizar usuario: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Endpoint para que un usuario actualice su propio perfil
     * Cualquier usuario autenticado puede actualizar su propia información
     */
    @PutMapping("/profile")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE', 'PATIENT')")
    public ResponseEntity<Map<String, Object>> updateOwnProfile(
            @RequestBody UsuarioUpdateRequest updates,
            @RequestHeader(value = "Authorization", required = true) String authHeader) {
        logger.info("PUT: /users/profile -> Actualizar perfil propio");
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            // Extraer userId del token JWT
            String token = authHeader.replace("Bearer ", "");
            Long userId = usuarioProfileService.extractUserIdFromToken(token);
            
            if (userId == null) {
                response.put("code", "401");
                response.put("description", "No se pudo obtener el ID del usuario del token");
                response.put("data", new LinkedHashMap<>());
                return ResponseEntity.status(401).body(response);
            }
            
            logger.info("Usuario con ID {} actualizando su propio perfil", userId);
            
            // Actualizar el perfil del usuario
            UsuarioModel usuarioActualizado = service.update(userId, updates);
            logger.info("Perfil del usuario con ID: {} actualizado exitosamente", userId);

            UsuarioResponse data = usuarioProfileService.buildProfile(usuarioActualizado);
            
            response.put("code", "000");
            response.put("description", "Perfil actualizado exitosamente");
            response.put("data", data);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al actualizar perfil: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al actualizar perfil: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
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

    @PutMapping("/{id}/password")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE', 'PATIENT')")
    public ResponseEntity<Map<String, Object>> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        logger.info("PUT: /users/{}/password -> Cambiar contraseña", id);

        Map<String, Object> response = new LinkedHashMap<>();

        try {
            service.changePassword(id, request);
            response.put("code", "000");
            response.put("description", "Contraseña actualizada exitosamente");
            response.put("data", new LinkedHashMap<>());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al cambiar contraseña para usuario {}: {}", id, e.getMessage(), e);
            response.put("code", "001");
            response.put("description", "Error al cambiar contraseña: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());
            return ResponseEntity.status(400).body(response);
        }
    }
}