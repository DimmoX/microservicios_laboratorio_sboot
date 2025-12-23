package com.gestion_users.ms_gestion_users.exceptionHandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private MethodArgumentNotValidException methodArgumentNotValidException;

    @Mock
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        // Setup común si es necesario
    }

    @Test
    void testHandleValidationExceptions_SingleError() {
        // Arrange
        FieldError fieldError = new FieldError("object", "username", "El username es requerido");
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertEquals(1, errors.size());
        assertEquals("El username es requerido", errors.get("username"));
    }

    @Test
    void testHandleValidationExceptions_MultipleErrors() {
        // Arrange
        FieldError error1 = new FieldError("object", "username", "El username es requerido");
        FieldError error2 = new FieldError("object", "email", "El email no es válido");
        FieldError error3 = new FieldError("object", "password", "La contraseña debe tener al menos 8 caracteres");
        
        when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(error1, error2, error3));

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody();
        assertEquals(3, errors.size());
        assertEquals("El username es requerido", errors.get("username"));
        assertEquals("El email no es válido", errors.get("email"));
        assertEquals("La contraseña debe tener al menos 8 caracteres", errors.get("password"));
    }

    @Test
    void testHandleBadCredentials() {
        // Arrange
        BadCredentialsException exception = new BadCredentialsException("Bad credentials");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleBadCredentials(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("401.1", body.get("code"));
        assertEquals("Credenciales inválidas: Usuario o contraseña incorrectos", body.get("description"));
        assertTrue(body.containsKey("data"));
    }

    @Test
    void testHandleAuthenticationException() {
        // Arrange
        AuthenticationException exception = new AuthenticationException("Authentication failed") {};

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleAuthenticationException(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("401", body.get("code"));
        assertEquals("No autenticado: Debe enviar un token JWT válido", body.get("description"));
        assertTrue(body.containsKey("data"));
    }

    @Test
    void testHandleAccessDenied() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleAccessDenied(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("403", body.get("code"));
        assertEquals("No autorizado: No tiene permisos para realizar esta acción", body.get("description"));
        assertTrue(body.containsKey("data"));
    }

    @Test
    void testHandleAllExceptions() {
        // Arrange
        Exception exception = new RuntimeException("Unexpected error occurred");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleAllExceptions(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertNotNull(body);
        assertTrue(body.containsKey("error"));
        assertTrue(body.get("error").contains("Unexpected error occurred"));
    }

    @Test
    void testHandleAllExceptions_WithDifferentMessages() {
        // Arrange
        String errorMessage1 = "Database connection failed";
        String errorMessage2 = "Network timeout";
        Exception exception1 = new RuntimeException(errorMessage1);
        Exception exception2 = new RuntimeException(errorMessage2);

        // Act
        ResponseEntity<Object> response1 = globalExceptionHandler.handleAllExceptions(exception1);
        ResponseEntity<Object> response2 = globalExceptionHandler.handleAllExceptions(exception2);

        // Assert
        @SuppressWarnings("unchecked")
        Map<String, String> body1 = (Map<String, String>) response1.getBody();
        @SuppressWarnings("unchecked")
        Map<String, String> body2 = (Map<String, String>) response2.getBody();
        
        assertTrue(body1.get("error").contains(errorMessage1));
        assertTrue(body2.get("error").contains(errorMessage2));
    }

    @Test
    void testHandleBadCredentials_ResponseStructure() {
        // Arrange
        BadCredentialsException exception = new BadCredentialsException("Invalid credentials");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleBadCredentials(exception);

        // Assert
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertTrue(body.get("data") instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertTrue(data.isEmpty());
    }

    @Test
    void testHandleAuthenticationException_ResponseStructure() {
        // Arrange
        AuthenticationException exception = new AuthenticationException("Token expired") {};

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleAuthenticationException(exception);

        // Assert
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(3, body.size()); // code, description, data
        assertTrue(body.get("data") instanceof Map);
    }

    @Test
    void testHandleAccessDenied_ResponseStructure() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Insufficient permissions");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleAccessDenied(exception);

        // Assert
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(3, body.size()); // code, description, data
        assertTrue(body.get("data") instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertTrue(data.isEmpty());
    }
}
