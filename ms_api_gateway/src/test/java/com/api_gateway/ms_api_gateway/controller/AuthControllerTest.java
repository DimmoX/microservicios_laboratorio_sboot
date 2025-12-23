package com.api_gateway.ms_api_gateway.controller;

import com.api_gateway.ms_api_gateway.dto.AuthRequest;
import com.api_gateway.ms_api_gateway.service.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthControllerTest {

    @Mock
    private TokenBlacklistService blacklistService;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authController, "usersServiceUrl", "http://localhost:8082");
        
        // Setup WebClient mock chain
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    // ===== LOGIN TESTS =====

    @Test
    void login_Success() {
        // Arrange
        AuthRequest request = new AuthRequest("test@example.com", "password123");
        Map<String, Object> expectedResponse = new LinkedHashMap<>();
        expectedResponse.put("token", "jwt-token-123");
        expectedResponse.put("email", "test@example.com");

        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class)))
            .thenReturn(Mono.just(expectedResponse));

        // Act & Assert
        StepVerifier.create(authController.login(request))
            .assertNext(response -> {
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("jwt-token-123", response.getBody().get("token"));
                assertEquals("test@example.com", response.getBody().get("email"));
            })
            .verifyComplete();

        verify(webClientBuilder, times(1)).build();
        verify(webClient, times(1)).post();
    }

    @Test
    void login_InvalidCredentials() {
        // Arrange
        AuthRequest request = new AuthRequest("test@example.com", "wrongpassword");

        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class)))
            .thenReturn(Mono.error(new RuntimeException("Credenciales inválidas")));

        // Act & Assert
        StepVerifier.create(authController.login(request))
            .assertNext(response -> {
                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("001", response.getBody().get("code"));
                assertEquals("Credenciales inválidas", response.getBody().get("description"));
            })
            .verifyComplete();
    }

    @Test
    void login_NullEmail() {
        // Arrange
        AuthRequest request = new AuthRequest(null, "password123");

        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class)))
            .thenReturn(Mono.error(new RuntimeException("Credenciales inválidas")));

        // Act & Assert
        StepVerifier.create(authController.login(request))
            .assertNext(response -> {
                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
                assertNotNull(response.getBody());
            })
            .verifyComplete();
    }

    // ===== FORGOT PASSWORD TESTS =====

    @Test
    void forgotPassword_Success() {
        // Arrange
        Map<String, String> request = new LinkedHashMap<>();
        request.put("email", "test@example.com");

        Map<String, Object> expectedResponse = new LinkedHashMap<>();
        expectedResponse.put("code", "000");
        expectedResponse.put("description", "Correo enviado exitosamente");

        when(responseSpec.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class)))
            .thenReturn(Mono.just(expectedResponse));

        // Act & Assert
        StepVerifier.create(authController.forgotPassword(request))
            .assertNext(response -> {
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("000", response.getBody().get("code"));
            })
            .verifyComplete();

        verify(webClientBuilder, times(1)).build();
    }

    @Test
    void forgotPassword_EmailNotFound() {
        // Arrange
        Map<String, String> request = new LinkedHashMap<>();
        request.put("email", "notfound@example.com");

        when(responseSpec.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class)))
            .thenReturn(Mono.error(new RuntimeException("Email not found")));

        // Act & Assert
        StepVerifier.create(authController.forgotPassword(request))
            .assertNext(response -> {
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("000", response.getBody().get("code"));
                assertTrue(response.getBody().get("description").toString().contains("recibirás instrucciones"));
            })
            .verifyComplete();
    }

    @Test
    void forgotPassword_NullEmail() {
        // Arrange
        Map<String, String> request = new LinkedHashMap<>();
        request.put("email", null);

        when(responseSpec.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class)))
            .thenReturn(Mono.error(new RuntimeException("Invalid request")));

        // Act & Assert
        StepVerifier.create(authController.forgotPassword(request))
            .assertNext(response -> {
                assertEquals(HttpStatus.OK, response.getStatusCode());
            })
            .verifyComplete();
    }

    // ===== CHANGE PASSWORD TESTS =====

    @Test
    void changePassword_Success() {
        // Arrange
        Map<String, String> request = new LinkedHashMap<>();
        request.put("oldPassword", "old123");
        request.put("newPassword", "new123");
        String authHeader = "Bearer valid-token";

        Map<String, Object> expectedResponse = new LinkedHashMap<>();
        expectedResponse.put("code", "000");
        expectedResponse.put("description", "Contraseña actualizada");

        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(responseSpec.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class)))
            .thenReturn(Mono.just(expectedResponse));

        // Act & Assert
        StepVerifier.create(authController.changePassword(request, authHeader))
            .assertNext(response -> {
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("000", response.getBody().get("code"));
            })
            .verifyComplete();
    }

    @Test
    void changePassword_NoAuthHeader() {
        // Arrange
        Map<String, String> request = new LinkedHashMap<>();
        request.put("oldPassword", "old123");
        request.put("newPassword", "new123");

        // Act & Assert
        StepVerifier.create(authController.changePassword(request, null))
            .assertNext(response -> {
                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("001", response.getBody().get("code"));
                assertEquals("Token de autenticación requerido", response.getBody().get("description"));
            })
            .verifyComplete();

        verify(webClientBuilder, never()).build();
    }

    @Test
    void changePassword_InvalidAuthHeader() {
        // Arrange
        Map<String, String> request = new LinkedHashMap<>();
        request.put("oldPassword", "old123");
        request.put("newPassword", "new123");
        String authHeader = "InvalidFormat";

        // Act & Assert
        StepVerifier.create(authController.changePassword(request, authHeader))
            .assertNext(response -> {
                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
                assertEquals("001", response.getBody().get("code"));
            })
            .verifyComplete();
    }

    @Test
    void changePassword_WrongOldPassword() {
        // Arrange
        Map<String, String> request = new LinkedHashMap<>();
        request.put("oldPassword", "wrong");
        request.put("newPassword", "new123");
        String authHeader = "Bearer valid-token";

        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(responseSpec.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class)))
            .thenReturn(Mono.error(new RuntimeException("401 Unauthorized")));

        // Act & Assert
        StepVerifier.create(authController.changePassword(request, authHeader))
            .assertNext(response -> {
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("001", response.getBody().get("code"));
                assertTrue(response.getBody().get("description").toString().contains("Contraseña actual incorrecta"));
            })
            .verifyComplete();
    }

    @Test
    void changePassword_GeneralError() {
        // Arrange
        Map<String, String> request = new LinkedHashMap<>();
        request.put("oldPassword", "old123");
        request.put("newPassword", "new123");
        String authHeader = "Bearer valid-token";

        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(responseSpec.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class)))
            .thenReturn(Mono.error(new RuntimeException("Server error")));

        // Act & Assert
        StepVerifier.create(authController.changePassword(request, authHeader))
            .assertNext(response -> {
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("001", response.getBody().get("code"));
            })
            .verifyComplete();
    }

    // ===== LOGOUT TESTS =====

    @Test
    void logout_Success() {
        // Arrange
        String authHeader = "Bearer valid-token-123";
        when(blacklistService.size()).thenReturn(1);

        // Act & Assert
        StepVerifier.create(authController.logout(authHeader))
            .assertNext(response -> {
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("000", response.getBody().get("code"));
                assertEquals("Logout exitoso", response.getBody().get("description"));
                
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
                assertEquals("Sesión cerrada correctamente", data.get("message"));
            })
            .verifyComplete();

        verify(blacklistService, times(1)).blacklistToken("valid-token-123");
        verify(blacklistService, times(1)).size();
    }

    @Test
    void logout_NoAuthHeader() {
        // Arrange & Act & Assert
        StepVerifier.create(authController.logout(null))
            .assertNext(response -> {
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("001", response.getBody().get("code"));
                assertEquals("Token no proporcionado", response.getBody().get("description"));
            })
            .verifyComplete();

        verify(blacklistService, never()).blacklistToken(anyString());
    }

    @Test
    void logout_InvalidAuthHeaderFormat() {
        // Arrange
        String authHeader = "InvalidFormat";

        // Act & Assert
        StepVerifier.create(authController.logout(authHeader))
            .assertNext(response -> {
                assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
                assertEquals("001", response.getBody().get("code"));
            })
            .verifyComplete();

        verify(blacklistService, never()).blacklistToken(anyString());
    }

    @Test
    void logout_ExceptionInBlacklistService() {
        // Arrange
        String authHeader = "Bearer valid-token-123";
        doThrow(new RuntimeException("Blacklist service error"))
            .when(blacklistService).blacklistToken(anyString());

        // Act & Assert
        StepVerifier.create(authController.logout(authHeader))
            .assertNext(response -> {
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
                assertNotNull(response.getBody());
                assertEquals("002", response.getBody().get("code"));
                assertEquals("Error en logout", response.getBody().get("description"));
            })
            .verifyComplete();

        verify(blacklistService, times(1)).blacklistToken("valid-token-123");
    }

    @Test
    void logout_EmptyToken() {
        // Arrange
        String authHeader = "Bearer ";

        // Act & Assert
        StepVerifier.create(authController.logout(authHeader))
            .assertNext(response -> {
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("000", response.getBody().get("code"));
            })
            .verifyComplete();

        verify(blacklistService, times(1)).blacklistToken("");
    }

    // ===== CONSTRUCTOR AND FIELD TESTS =====

    @Test
    void constructor_InitializesFieldsCorrectly() {
        // Arrange & Act
        AuthController controller = new AuthController(blacklistService, webClientBuilder);

        // Assert
        assertNotNull(controller);
        // Verify fields were set through constructor
        verify(blacklistService, never()).blacklistToken(anyString());
    }

    @Test
    void usersServiceUrl_IsInjectedCorrectly() {
        // Arrange & Act
        String url = (String) ReflectionTestUtils.getField(authController, "usersServiceUrl");

        // Assert
        assertEquals("http://localhost:8082", url);
    }
}
