package com.gestion_users.ms_gestion_users.exceptionHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @description Clase que maneja las excepciones globales
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    // Se crea un logger para manejar logs con excepciones
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

   @ExceptionHandler(MethodArgumentNotValidException.class) // para errores de validación con @Valid
   public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
      logger.error("Error de validación: {}", ex.getMessage());
      Map<String, String> errors = new HashMap<>();
      ex.getBindingResult().getFieldErrors().forEach(error -> {
         errors.put(error.getField(), error.getDefaultMessage());
      });
      return ResponseEntity.badRequest().body(errors);
   }

   @ExceptionHandler(BadCredentialsException.class)
   public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex) {
      logger.warn("Intento de login fallido: credenciales inválidas");
      
      Map<String, Object> response = new LinkedHashMap<>();
      response.put("code", "401.1");
      response.put("description", "Credenciales inválidas: Usuario o contraseña incorrectos");
      response.put("data", new LinkedHashMap<>());
      
      return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
   }

   @ExceptionHandler(AuthenticationException.class)
   public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
      logger.warn("Error de autenticación: {}", ex.getMessage());
      
      Map<String, Object> response = new LinkedHashMap<>();
      response.put("code", "401");
      response.put("description", "No autenticado: Debe enviar un token JWT válido");
      response.put("data", new LinkedHashMap<>());
      
      return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
   }

   @ExceptionHandler(AccessDeniedException.class)
   public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
      logger.warn("Acceso denegado: {}", ex.getMessage());
      
      Map<String, Object> response = new LinkedHashMap<>();
      response.put("code", "403");
      response.put("description", "No autorizado: No tiene permisos para realizar esta acción");
      response.put("data", new LinkedHashMap<>());
      
      return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
   }

   @ExceptionHandler(Exception.class)
   public ResponseEntity<Object> handleAllExceptions(Exception ex) {
      logger.error("Error inesperado: {}", ex.getMessage());
      Map<String, String> error = new HashMap<>();
      error.put("error", "Ocurrió un error: " + ex.getMessage());
      return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
   }

}
