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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Tests para CustomAuthenticationEntryPoint
 */
@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private CustomAuthenticationEntryPoint entryPoint;
    private StringWriter stringWriter;
    private PrintWriter writer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        entryPoint = new CustomAuthenticationEntryPoint();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCommence_Unauthorized_Returns401() throws IOException, ServletException {
        // Arrange
        AuthenticationException exception = new InsufficientAuthenticationException("Full authentication required");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/protected");

        // Act
        entryPoint.commence(request, response, exception);
        writer.flush();

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        
        String jsonResponse = stringWriter.toString();
        assertNotNull(jsonResponse);
        assertFalse(jsonResponse.isEmpty());
    }

    @Test
    void testCommence_Unauthorized_ReturnsCorrectJsonStructure() throws IOException, ServletException {
        // Arrange
        AuthenticationException exception = new BadCredentialsException("Bad credentials");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/login");

        // Act
        entryPoint.commence(request, response, exception);
        writer.flush();

        // Assert
        String jsonResponse = stringWriter.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
        
        assertEquals("401", responseMap.get("code"));
        assertEquals("No estás autenticado. Por favor, proporciona un token JWT válido.", responseMap.get("description"));
        assertTrue(responseMap.containsKey("data"));
        assertTrue(responseMap.get("data") instanceof Map);
    }

    @Test
    void testCommence_DifferentHttpMethods_AllHandledCorrectly() throws IOException, ServletException {
        // Test GET
        AuthenticationException exception = new InsufficientAuthenticationException("Not authenticated");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/users");

        entryPoint.commence(request, response, exception);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Reset mocks
        reset(response);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Test POST
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/create");

        entryPoint.commence(request, response, exception);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Reset mocks
        reset(response);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Test PUT
        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/api/update/123");

        entryPoint.commence(request, response, exception);
        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void testCommence_NullExceptionMessage_StillReturnsValidResponse() throws IOException, ServletException {
        // Arrange
        AuthenticationException exception = new InsufficientAuthenticationException(null);
        when(request.getMethod()).thenReturn("DELETE");
        when(request.getRequestURI()).thenReturn("/api/delete/456");

        // Act
        entryPoint.commence(request, response, exception);
        writer.flush();

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String jsonResponse = stringWriter.toString();
        assertNotNull(jsonResponse);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
        assertEquals("401", responseMap.get("code"));
    }

    @Test
    void testCommence_BadCredentialsException_HandledCorrectly() throws IOException, ServletException {
        // Arrange
        AuthenticationException exception = new BadCredentialsException("Invalid username or password");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        // Act
        entryPoint.commence(request, response, exception);
        writer.flush();

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        
        String jsonResponse = stringWriter.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
        assertEquals("401", responseMap.get("code"));
    }

    @Test
    void testCommence_LongUri_HandledCorrectly() throws IOException, ServletException {
        // Arrange
        AuthenticationException exception = new InsufficientAuthenticationException("Authentication required");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/secured/resource/that/requires/authentication/with/multiple/path/segments");

        // Act
        entryPoint.commence(request, response, exception);
        writer.flush();

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        
        String jsonResponse = stringWriter.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
        assertNotNull(responseMap.get("description"));
    }

    @Test
    void testCommence_ResponseHasCorrectHeaders() throws IOException, ServletException {
        // Arrange
        AuthenticationException exception = new InsufficientAuthenticationException("No token");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/secure");

        // Act
        entryPoint.commence(request, response, exception);

        // Assert
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
    }

    @Test
    void testCommence_DataFieldIsEmptyMap() throws IOException, ServletException {
        // Arrange
        AuthenticationException exception = new InsufficientAuthenticationException("Auth required");
        when(request.getMethod()).thenReturn("PATCH");
        when(request.getRequestURI()).thenReturn("/api/patch/resource");

        // Act
        entryPoint.commence(request, response, exception);
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
    void testCommence_MultipleSequentialCalls_EachHandledIndependently() throws IOException, ServletException {
        // Primera llamada
        AuthenticationException exception1 = new InsufficientAuthenticationException("First auth failure");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/first");

        entryPoint.commence(request, response, exception1);
        writer.flush();

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Reset para segunda llamada
        reset(response);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // Segunda llamada
        AuthenticationException exception2 = new BadCredentialsException("Second auth failure");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/second");

        entryPoint.commence(request, response, exception2);
        writer.flush();

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String jsonResponse = stringWriter.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
        assertEquals("401", responseMap.get("code"));
    }

    @Test
    void testCommence_ExceptionWithDetailedMessage_MessageLoggedButNotExposed() throws IOException, ServletException {
        // Arrange
        String detailedMessage = "JWT token has expired at 2024-12-22T10:30:00Z";
        AuthenticationException exception = new InsufficientAuthenticationException(detailedMessage);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/data");

        // Act
        entryPoint.commence(request, response, exception);
        writer.flush();

        // Assert - El mensaje público no debe exponer detalles técnicos
        String jsonResponse = stringWriter.toString();
        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
        
        String publicDescription = (String) responseMap.get("description");
        assertEquals("No estás autenticado. Por favor, proporciona un token JWT válido.", publicDescription);
        assertFalse(publicDescription.contains("expired"));
        assertFalse(publicDescription.contains("2024"));
    }

    @Test
    void testCommence_DifferentAuthenticationExceptionTypes_AllReturn401() throws IOException, ServletException {
        // InsufficientAuthenticationException
        AuthenticationException exception1 = new InsufficientAuthenticationException("Insufficient");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test1");
        
        entryPoint.commence(request, response, exception1);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Reset
        reset(response);
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        // BadCredentialsException
        AuthenticationException exception2 = new BadCredentialsException("Bad creds");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/test2");
        
        entryPoint.commence(request, response, exception2);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
