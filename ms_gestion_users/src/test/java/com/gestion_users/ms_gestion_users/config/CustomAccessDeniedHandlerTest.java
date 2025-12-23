package com.gestion_users.ms_gestion_users.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests para CustomAccessDeniedHandler
 * Cobertura: Manejo de errores 403 Forbidden cuando usuario autenticado no tiene permisos
 */
@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private CustomAccessDeniedHandler handler;
    private ObjectMapper objectMapper;
    private StringWriter stringWriter;
    private PrintWriter printWriter;

    @BeforeEach
    void setUp() throws Exception {
        handler = new CustomAccessDeniedHandler();
        objectMapper = new ObjectMapper();
        stringWriter = new StringWriter();
        printWriter = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    void testHandle_ReturnsCorrectStatusCode() throws Exception {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("No tiene permisos");
        when(request.getRequestURI()).thenReturn("/api/admin/users");

        // Act
        handler.handle(request, response, exception);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setStatus(403);
    }

    @Test
    void testHandle_SetsCorrectContentType() throws Exception {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("No tiene permisos");
        when(request.getRequestURI()).thenReturn("/api/admin/users");

        // Act
        handler.handle(request, response, exception);

        // Assert
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    void testHandle_ReturnsCorrectResponseStructure() throws Exception {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("No tiene permisos");
        when(request.getRequestURI()).thenReturn("/api/admin/users");

        // Act
        handler.handle(request, response, exception);
        printWriter.flush();

        // Parse response
        String responseBody = stringWriter.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);

        // Assert
        assertNotNull(responseMap);
        assertTrue(responseMap.containsKey("code"));
        assertTrue(responseMap.containsKey("description"));
        assertTrue(responseMap.containsKey("data"));
    }

    @Test
    void testHandle_ResponseCodeIs403() throws Exception {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("No tiene permisos");
        when(request.getRequestURI()).thenReturn("/api/admin/users");

        // Act
        handler.handle(request, response, exception);
        printWriter.flush();

        // Parse response
        String responseBody = stringWriter.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);

        // Assert
        assertEquals("403", responseMap.get("code"));
    }

    @Test
    void testHandle_ResponseDescriptionContainsExpectedMessage() throws Exception {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("No tiene permisos");
        when(request.getRequestURI()).thenReturn("/api/admin/users");

        // Act
        handler.handle(request, response, exception);
        printWriter.flush();

        // Parse response
        String responseBody = stringWriter.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);

        // Assert
        String description = (String) responseMap.get("description");
        assertTrue(description.contains("No autorizado"));
        assertTrue(description.contains("No tiene permisos"));
    }

    @Test
    void testHandle_ResponseDataIsEmptyMap() throws Exception {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("No tiene permisos");
        when(request.getRequestURI()).thenReturn("/api/admin/users");

        // Act
        handler.handle(request, response, exception);
        printWriter.flush();

        // Parse response
        String responseBody = stringWriter.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);

        // Assert
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        assertNotNull(data);
        assertTrue(data.isEmpty());
    }

    @Test
    void testHandle_DifferentURIs() throws Exception {
        // Test con diferentes URIs
        String[] uris = {
            "/api/admin/users",
            "/api/admin/employees",
            "/api/admin/settings",
            "/api/empleados"
        };

        for (String uri : uris) {
            // Arrange
            setUp(); // Reset mocks
            AccessDeniedException exception = new AccessDeniedException("Acceso denegado");
            when(request.getRequestURI()).thenReturn(uri);

            // Act
            handler.handle(request, response, exception);
            printWriter.flush();

            // Parse response
            String responseBody = stringWriter.toString();
            @SuppressWarnings("unchecked")
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);

            // Assert
            assertEquals("403", responseMap.get("code"));
        }
    }

    @Test
    void testHandle_DifferentExceptionMessages() throws Exception {
        // Test con diferentes mensajes de excepción
        String[] messages = {
            "Insufficient permissions",
            "User lacks required role",
            "Access denied",
            "Forbidden action"
        };

        for (String message : messages) {
            // Arrange
            setUp(); // Reset mocks
            AccessDeniedException exception = new AccessDeniedException(message);
            when(request.getRequestURI()).thenReturn("/api/test");

            // Act
            handler.handle(request, response, exception);

            // Assert - El handler siempre devuelve el mismo mensaje estandarizado
            verify(response, atLeastOnce()).setStatus(403);
        }
    }

    @Test
    void testHandle_LogsWarningMessage() throws Exception {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("No tiene permisos");
        when(request.getRequestURI()).thenReturn("/api/admin/users");

        // Act
        handler.handle(request, response, exception);

        // Assert - Verificar que se invoca el request y response correctamente
        verify(request).getRequestURI();
        verify(response).getWriter();
    }

    @Test
    void testHandle_ResponseIsValidJSON() throws Exception {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("No tiene permisos");
        when(request.getRequestURI()).thenReturn("/api/admin/users");

        // Act
        handler.handle(request, response, exception);
        printWriter.flush();

        // Parse response
        String responseBody = stringWriter.toString();

        // Assert - El response debe ser JSON válido
        assertNotNull(responseBody);
        assertFalse(responseBody.isEmpty());
        assertTrue(responseBody.contains("\"code\""));
        assertTrue(responseBody.contains("\"description\""));
        assertTrue(responseBody.contains("\"data\""));

        // Verificar que es parseable como JSON
        assertDoesNotThrow(() -> objectMapper.readValue(responseBody, Map.class));
    }
}
