package com.gestion_resultados.ms_gestion_resultados.config;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Tests para CustomAccessDeniedHandler
 */
@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private CustomAccessDeniedHandler handler;
    private StringWriter stringWriter;
    private PrintWriter writer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        handler = new CustomAccessDeniedHandler();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testHandle_AccessDenied_Returns403() throws IOException, ServletException {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Insufficient permissions");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/admin/users");

        // Act
        handler.handle(request, response, exception);
        writer.flush();

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        
        String jsonResponse = stringWriter.toString();
        assertNotNull(jsonResponse);
        assertFalse(jsonResponse.isEmpty());
    }

    @Test
    void testHandle_AccessDenied_ReturnsCorrectJsonStructure() throws IOException, ServletException {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Access denied");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/admin/delete");

        // Act
        handler.handle(request, response, exception);
        writer.flush();

        // Assert
        String jsonResponse = stringWriter.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
        
        assertEquals("403", responseMap.get("code"));
        assertEquals("No tienes permisos para acceder a este recurso.", responseMap.get("description"));
        assertTrue(responseMap.containsKey("data"));
        assertTrue(responseMap.get("data") instanceof Map);
    }

    @Test
    void testHandle_DifferentMethods_AllLoggedCorrectly() throws IOException, ServletException {
        // Test GET
        AccessDeniedException exception = new AccessDeniedException("Denied");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/resource");

        handler.handle(request, response, exception);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Reset mocks
        reset(response);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Test POST
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/create");

        handler.handle(request, response, exception);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Reset mocks
        reset(response);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Test DELETE
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn("/api/delete/123");

        handler.handle(request, response, exception);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    void testHandle_NullExceptionMessage_StillReturnsValidResponse() throws IOException, ServletException {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException(null);
        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/api/update/456");

        // Act
        handler.handle(request, response, exception);
        writer.flush();

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        String jsonResponse = stringWriter.toString();
        assertNotNull(jsonResponse);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
        assertEquals("403", responseMap.get("code"));
    }

    @Test
    void testHandle_LongUri_HandledCorrectly() throws IOException, ServletException {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Forbidden");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/very/long/path/to/some/protected/resource/that/requires/admin/permissions");

        // Act
        handler.handle(request, response, exception);
        writer.flush();

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType("application/json");
        
        String jsonResponse = stringWriter.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
        assertNotNull(responseMap.get("description"));
    }

    @Test
    void testHandle_ResponseHasCorrectHeaders() throws IOException, ServletException {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("No access");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/secure");

        // Act
        handler.handle(request, response, exception);

        // Assert
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    void testHandle_DataFieldIsEmptyMap() throws IOException, ServletException {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Forbidden");
        when(request.getMethod()).thenReturn("PATCH");
        when(request.getRequestURI()).thenReturn("/api/patch/resource");

        // Act
        handler.handle(request, response, exception);
        writer.flush();

        // Assert
        String jsonResponse = stringWriter.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) responseMap.get("data");
        assertNotNull(data);
        assertTrue(data.isEmpty());
    }

    @Test
    void testHandle_MultipleCallsInSequence_EachHandledIndependently() throws IOException, ServletException {
        // Primera llamada
        AccessDeniedException exception1 = new AccessDeniedException("First denial");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/first");

        handler.handle(request, response, exception1);
        writer.flush();

        verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);

        // Reset para segunda llamada
        reset(response);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Segunda llamada
        AccessDeniedException exception2 = new AccessDeniedException("Second denial");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/second");

        handler.handle(request, response, exception2);
        writer.flush();

        verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        String jsonResponse = stringWriter.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
        assertEquals("403", responseMap.get("code"));
    }

    @Test
    void testHandle_ExceptionWithDetailedMessage_MessageLoggedButNotExposed() throws IOException, ServletException {
        // Arrange
        String detailedMessage = "User with role EMPLOYEE tried to access ADMIN-only resource";
        AccessDeniedException exception = new AccessDeniedException(detailedMessage);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/admin/settings");

        // Act
        handler.handle(request, response, exception);
        writer.flush();

        // Assert - El mensaje de error público no debe exponer detalles internos
        String jsonResponse = stringWriter.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
        
        String publicDescription = (String) responseMap.get("description");
        assertEquals("No tienes permisos para acceder a este recurso.", publicDescription);
        assertFalse(publicDescription.contains("EMPLOYEE"));
        assertFalse(publicDescription.contains("ADMIN"));
    }
}
