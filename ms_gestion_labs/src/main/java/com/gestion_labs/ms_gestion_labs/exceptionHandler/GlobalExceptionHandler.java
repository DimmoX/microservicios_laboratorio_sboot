package com.gestion_labs.ms_gestion_labs.exceptionHandler;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
    private static final String DESCRIPTION_KEY = "description";

   /**
    * Maneja errores de autenticación (401 Unauthorized)
    */
   @ExceptionHandler(AuthenticationException.class)
   public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
      logger.error("Error de autenticación: {}", ex.getMessage());
      logger.debug("Detalle error: ", ex);
      
      Map<String, Object> response = new LinkedHashMap<>();
      response.put("code", "401");
      response.put(DESCRIPTION_KEY, "Error de autenticación: " + ex.getMessage());
      response.put("data", new LinkedHashMap<>());
      
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
   }

   /**
    * Maneja errores de autorización (403 Forbidden)
    */
   @ExceptionHandler(AccessDeniedException.class)
   public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
      logger.error("Acceso denegado: {}", ex.getMessage());
      logger.debug("Detalle error: ", ex);
      
      Map<String, Object> response = new LinkedHashMap<>();
      response.put("code", "403");
      response.put(DESCRIPTION_KEY, "No tienes permisos para acceder a este recurso.");
      response.put("data", new LinkedHashMap<>());
      
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
   }

   @ExceptionHandler(MethodArgumentNotValidException.class) // para errores de validación con @Valid
   public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
      logger.error("Error de validación: {}", ex.getMessage());
      logger.debug("Detalle error: {}", ex);
      
      Map<String, Object> response = new LinkedHashMap<>();
      response.put("code", "400");
      response.put(DESCRIPTION_KEY, "Error de validación en los datos enviados");
      
      Map<String, String> errors = new LinkedHashMap<>();
      ex.getBindingResult().getFieldErrors().forEach(error -> {
         errors.put(error.getField(), error.getDefaultMessage());
      });
      response.put("data", errors);
      
      return ResponseEntity.badRequest().body(response);
   }

   @ExceptionHandler(Exception.class)
   public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
      logger.error("Error inesperado: {}", ex.getMessage());
      logger.debug("Detalle error: {}", ex);
      
      Map<String, Object> response = new LinkedHashMap<>();
      response.put("code", "500");
      response.put(DESCRIPTION_KEY, "Ocurrió un error: " + ex.getMessage());
      response.put("data", new LinkedHashMap<>());
      
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
   }

}