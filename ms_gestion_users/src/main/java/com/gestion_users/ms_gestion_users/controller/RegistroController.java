package com.gestion_users.ms_gestion_users.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion_users.ms_gestion_users.dto.RegistroEmpleadoRequest;
import com.gestion_users.ms_gestion_users.dto.RegistroPacienteRequest;
import com.gestion_users.ms_gestion_users.dto.RegistroResponse;
import com.gestion_users.ms_gestion_users.service.registro.RegistroService;

/**
 * Controlador para el registro completo de pacientes y empleados.
 * Estos endpoints crean:
 * - Contacto (incluye email)
 * - Dirección
 * - Paciente/Empleado
 * - Usuario (credenciales de login)
 * 
 * Todo en una sola transacción.
 */
@RestController
@RequestMapping("/registro")
public class RegistroController {

    private static final Logger logger = LoggerFactory.getLogger(RegistroController.class);
    private final RegistroService service;

    public RegistroController(RegistroService service) {
        this.service = service;
    }

    /**
     * Registra un nuevo paciente con todos sus datos.
     * 
     * RUTA PÚBLICA - No requiere autenticación
     * Los usuarios pueden registrarse a sí mismos como pacientes
     * 
     * POST /registro/paciente
     * Body: {
     *   "pnombre": "Juan",
     *   "papellido": "Pérez",
     *   "rut": "12345678-9",
     *   "contacto": {
     *     "fono1": "+56912345678",
     *     "email": "juan.perez@correo.cl"
     *   },
     *   "direccion": {
     *     "calle": "Av. Principal",
     *     "numero": 123,
     *     "ciudad": "Santiago",
     *     "comuna": "Providencia",
     *     "region": "Metropolitana"
     *   },
     *   "password": "miPassword123"
     * }
     */
    @PostMapping("/paciente")
    public ResponseEntity<Map<String, Object>> registrarPaciente(@RequestBody RegistroPacienteRequest request) {
        logger.info("POST: /registro/paciente -> Registrar nuevo paciente: {}", request.getContacto().getEmail());
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            RegistroResponse registroResponse = service.registrarPaciente(request);
            logger.info("Paciente registrado exitosamente - ID: {}, Usuario ID: {}", 
                registroResponse.getPacienteId(), registroResponse.getUsuarioId());
            
            response.put("code", "000");
            response.put("description", "Paciente registrado exitosamente");
            response.put("data", registroResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al registrar paciente: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al registrar paciente: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Registra un nuevo empleado con todos sus datos.
     * 
     * POST /registro/empleado
     * Body: {
     *   "pnombre": "María",
     *   "papellido": "González",
     *   "rut": "98765432-1",
     *   "cargo": "Tecnólogo Médico",
     *   "rol": "TM",
     *   "contacto": {
     *     "fono1": "+56987654321",
     *     "email": "maria.gonzalez@laboratorio.cl"
     *   },
     *   "direccion": {
     *     "calle": "Calle Secundaria",
     *     "numero": 456,
     *     "ciudad": "Santiago",
     *     "comuna": "Las Condes",
     *     "region": "Metropolitana"
     *   },
     *   "password": "miPassword456"
     * }
     */
    @PostMapping("/empleado")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Object>> registrarEmpleado(@RequestBody RegistroEmpleadoRequest request) {
        logger.info("POST: /registro/empleado -> Registrar nuevo empleado: {}", request.getContacto().getEmail());
        
        Map<String, Object> response = new LinkedHashMap<>();
        
        try {
            RegistroResponse registroResponse = service.registrarEmpleado(request);
            logger.info("Empleado registrado exitosamente - ID: {}, Usuario ID: {}", 
                registroResponse.getEmpleadoId(), registroResponse.getUsuarioId());
            
            response.put("code", "000");
            response.put("description", "Empleado registrado exitosamente");
            response.put("data", registroResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error al registrar empleado: {}", e.getMessage(), e);
            
            response.put("code", "001");
            response.put("description", "Error al registrar empleado: " + e.getMessage());
            response.put("data", new LinkedHashMap<>());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}
