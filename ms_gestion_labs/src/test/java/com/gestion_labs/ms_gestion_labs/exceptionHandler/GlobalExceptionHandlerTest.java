package com.gestion_labs.ms_gestion_labs.exceptionHandler;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Test
    void testHandleAuthenticationException() {
        // Arrange
        AuthenticationException ex = new AuthenticationException("Invalid credentials") {};

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleAuthenticationException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("401", response.getBody().get("code"));
        assertTrue(response.getBody().get("description").toString().contains("Error de autenticación"));
    }

    @Test
    void testHandleAuthenticationExceptionConMensajeNulo() {
        // Arrange
        AuthenticationException ex = new AuthenticationException(null) {};

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleAuthenticationException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("401", response.getBody().get("code"));
    }

    @Test
    void testHandleAccessDeniedException() {
        // Arrange
        AccessDeniedException ex = new AccessDeniedException("Access forbidden");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleAccessDeniedException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("403", response.getBody().get("code"));
        assertTrue(response.getBody().get("description").toString().contains("No tienes permisos"));
    }

    @Test
    void testHandleAccessDeniedExceptionConMensajeNulo() {
        // Arrange
        AccessDeniedException ex = new AccessDeniedException(null);

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleAccessDeniedException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("403", response.getBody().get("code"));
    }

    @Test
    void testHandleValidationExceptions() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError error1 = new FieldError("user", "email", "Email no válido");
        FieldError error2 = new FieldError("user", "nombre", "Nombre es requerido");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(error1, error2));

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().get("code"));
        assertTrue(response.getBody().get("description").toString().contains("Error de validación"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("data");
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertTrue(errors.containsKey("email"));
        assertTrue(errors.containsKey("nombre"));
    }

    @Test
    void testHandleValidationExceptionsSinErrores() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleValidationExceptions(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("400", response.getBody().get("code"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("data");
        assertNotNull(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    void testHandleAllExceptions() {
        // Arrange
        Exception ex = new RuntimeException("Unexpected error");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleAllExceptions(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("500", response.getBody().get("code"));
        assertTrue(response.getBody().get("description").toString().contains("Ocurrió un error"));
    }

    @Test
    void testHandleAllExceptionsConNullPointerException() {
        // Arrange
        Exception ex = new NullPointerException("Null value detected");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleAllExceptions(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("500", response.getBody().get("code"));
    }

    @Test
    void testHandleAllExceptionsConIllegalArgumentException() {
        // Arrange
        Exception ex = new IllegalArgumentException("Invalid argument");

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleAllExceptions(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("500", response.getBody().get("code"));
        assertTrue(response.getBody().get("description").toString().contains("Invalid argument"));
    }

    @Test
    void testHandleAllExceptionsConMensajeNulo() {
        // Arrange
        Exception ex = new RuntimeException((String) null);

        // Act
        ResponseEntity<Map<String, Object>> response = handler.handleAllExceptions(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("500", response.getBody().get("code"));
    }
}
