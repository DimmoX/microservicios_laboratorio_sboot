package com.api_gateway.ms_api_gateway.filter;

import com.api_gateway.ms_api_gateway.config.JwtProperties;
import com.api_gateway.ms_api_gateway.service.TokenBlacklistService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import javax.crypto.SecretKey;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtGlobalFilterTest {

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private TokenBlacklistService blacklistService;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private GatewayFilterChain chain;

    @Mock
    private HttpHeaders headers;

    private JwtGlobalFilter filter;
    private String validToken;
    private final String secret = "my-secret-key-for-jwt-token-generation-minimum-256-bits-required";

    @BeforeEach
    void setUp() {
        when(jwtProperties.getSecret()).thenReturn(secret);

        filter = new JwtGlobalFilter(jwtProperties, blacklistService);

        // Setup common mock behaviors
        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getHeaders()).thenReturn(headers);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        when(response.bufferFactory()).thenReturn(bufferFactory);
        when(response.writeWith(any())).thenReturn(Mono.empty());
        when(response.getHeaders()).thenReturn(new HttpHeaders());

        // Generate valid token
        validToken = generateToken("testuser", "ADMIN", 1, 100, 200);
    }

    private String generateToken(String username, String role, Integer userId, Integer pacienteId, Integer empleadoId) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .claim("userId", userId)
                .claim("pacienteId", pacienteId)
                .claim("empleadoId", empleadoId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7200000))
                .signWith(key)
                .compact();
    }

    @Test
    void testOptionsRequestShouldPassWithoutValidation() {
        // Given
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/test"));
        when(request.getMethod()).thenReturn(HttpMethod.OPTIONS);

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(chain, times(1)).filter(exchange);
        verify(headers, never()).getFirst("Authorization");
    }

    @ParameterizedTest
    @CsvSource({
        "/auth/login, POST",
        "/auth/logout, POST",
        "/auth/forgot-password, POST",
        "/labs, GET",
        "/labs/123, GET",
        "/registro/paciente, POST"
    })
    void testPublicPathsShouldPassWithoutAuthentication(String path, String method) {
        // Given
        when(request.getURI()).thenReturn(URI.create("http://localhost" + path));
        when(request.getMethod()).thenReturn(HttpMethod.valueOf(method));

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(chain, times(1)).filter(exchange);
    }

    @Test
    void testPrivatePathGetRegistroPacienteShouldRequireToken() {
        // Given
        when(request.getURI()).thenReturn(URI.create("http://localhost/registro/paciente"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(chain, times(1)).filter(exchange);
    }

    @ParameterizedTest
    @CsvSource({
            ",",
            "InvalidFormat token123,",
            "Bearer ,"
    })
    void testPrivatePathWithInvalidAuthShouldReturnUnauthorized(String authHeader) {
        // Given
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/private"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(headers.getFirst("Authorization")).thenReturn(authHeader);

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void testPrivatePathWithBlacklistedTokenShouldReturnUnauthorized() {
        // Given
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/private"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(headers.getFirst("Authorization")).thenReturn("Bearer " + validToken);
        when(blacklistService.isBlacklisted(validToken)).thenReturn(true);

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void testPrivatePathWithValidTokenShouldPass() {
        // Given
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/private"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(headers.getFirst("Authorization")).thenReturn("Bearer " + validToken);
        when(blacklistService.isBlacklisted(validToken)).thenReturn(false);
        Map<String, Object> attributes = new java.util.HashMap<>();
        when(exchange.getAttributes()).thenReturn(attributes);

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(chain, times(1)).filter(exchange);
        assert attributes.get("X-User-Id").equals("testuser");
        assert attributes.get("X-User-Role").equals("ADMIN");
    }

    @Test
    void testPrivatePathWithValidTokenAndAllClaimsShouldPass() {
        // Given
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/private"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(headers.getFirst("Authorization")).thenReturn("Bearer " + validToken);
        when(blacklistService.isBlacklisted(validToken)).thenReturn(false);
        Map<String, Object> attributes = new java.util.HashMap<>();
        when(exchange.getAttributes()).thenReturn(attributes);

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(chain, times(1)).filter(exchange);
        assert attributes.get("X-User-Id").equals("testuser");
        assert attributes.get("X-User-Role").equals("ADMIN");
        assert attributes.get("X-User-DB-Id").equals("1");
        assert attributes.get("X-Patient-Id").equals("100");
        assert attributes.get("X-Employee-Id").equals("200");
    }

    @Test
    void testPrivatePathWithTokenWithoutRoleShouldSetUnknown() {
        // Given
        String tokenWithoutRole = generateTokenWithoutRole("testuser", 1);
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/private"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(headers.getFirst("Authorization")).thenReturn("Bearer " + tokenWithoutRole);
        when(blacklistService.isBlacklisted(tokenWithoutRole)).thenReturn(false);
        Map<String, Object> attributes = new java.util.HashMap<>();
        when(exchange.getAttributes()).thenReturn(attributes);

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(chain, times(1)).filter(exchange);
        assert attributes.get("X-User-Role").equals("UNKNOWN");
    }

    @Test
    void testPrivatePathWithTokenWithoutUsernameShouldReturnUnauthorized() {
        // Given
        String tokenWithoutUsername = generateTokenWithoutUsername();
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/private"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(headers.getFirst("Authorization")).thenReturn("Bearer " + tokenWithoutUsername);
        when(blacklistService.isBlacklisted(tokenWithoutUsername)).thenReturn(false);

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void testPrivatePathWithEmptyUsernameShouldReturnUnauthorized() {
        // Given
        String tokenWithEmptyUsername = generateToken("", "ADMIN", 1, null, null);
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/private"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(headers.getFirst("Authorization")).thenReturn("Bearer " + tokenWithEmptyUsername);
        when(blacklistService.isBlacklisted(tokenWithEmptyUsername)).thenReturn(false);

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void testPrivatePathWithInvalidTokenShouldReturnUnauthorized() {
        // Given
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/private"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(headers.getFirst("Authorization")).thenReturn("Bearer invalid.token.here");
        when(blacklistService.isBlacklisted("invalid.token.here")).thenReturn(false);

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void testPrivatePathPostLabsShouldRequireToken() {
        // Given
        when(request.getURI()).thenReturn(URI.create("http://localhost/labs"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(headers.getFirst("Authorization")).thenReturn(null);

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(response).setStatusCode(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(exchange);
    }

    @Test
    void testGetOrderShouldReturnNegative100() {
        // When
        int order = filter.getOrder();

        // Then
        assertEquals(-100, order);
    }

    @Test
    void testPrivatePathWithTokenWithOnlyPacienteIdShouldPass() {
        // Given
        String token = generateToken("testuser", "PATIENT", 1, 100, null);
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/private"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(headers.getFirst("Authorization")).thenReturn("Bearer " + token);
        when(blacklistService.isBlacklisted(token)).thenReturn(false);
        Map<String, Object> attributes = new java.util.HashMap<>();
        when(exchange.getAttributes()).thenReturn(attributes);

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(chain, times(1)).filter(exchange);
        assert attributes.get("X-Patient-Id").equals("100");
        assert attributes.get("X-Employee-Id") == null;
    }

    @Test
    void testPrivatePathWithTokenWithOnlyEmpleadoIdShouldPass() {
        // Given
        String token = generateToken("testuser", "EMPLOYEE", 1, null, 200);
        when(request.getURI()).thenReturn(URI.create("http://localhost/api/private"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(headers.getFirst("Authorization")).thenReturn("Bearer " + token);
        when(blacklistService.isBlacklisted(token)).thenReturn(false);
        Map<String, Object> attributes = new java.util.HashMap<>();
        when(exchange.getAttributes()).thenReturn(attributes);

        // When
        Mono<Void> result = filter.filter(exchange, chain);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(chain, times(1)).filter(exchange);
        assert attributes.get("X-Employee-Id").equals("200");
        assert attributes.get("X-Patient-Id") == null;
    }

    // Helper methods
    private String generateTokenWithoutRole(String username, Integer userId) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7200000))
                .signWith(key)
                .compact();
    }

    private String generateTokenWithoutUsername() {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claim("role", "ADMIN")
                .claim("userId", 1)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7200000))
                .signWith(key)
                .compact();
    }
}
