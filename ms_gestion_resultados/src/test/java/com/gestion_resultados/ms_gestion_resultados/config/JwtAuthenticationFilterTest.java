package com.gestion_resultados.ms_gestion_resultados.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Tests para JwtAuthenticationFilter
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;
    private static final String SECRET = "miSuperSecretoParaFirmarTokensJWTconHS512yDebeSerMuyLargoParaQueSeaSeguro";

    @BeforeEach
    void setUp() {
        lenient().when(jwtProperties.getSecret()).thenReturn(SECRET);
        filter = new JwtAuthenticationFilter(jwtProperties);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_ValidToken_SetsAuthentication() throws ServletException, IOException {
        // Arrange
        String token = createValidToken("testuser", "ADMIN", 123, null);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("testuser", auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ADMIN")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ValidTokenWithPacienteId_SetsAuthenticationWithDetails() throws ServletException, IOException {
        // Arrange
        String token = createValidToken("patient1", "PATIENT", null, 456);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("patient1", auth.getPrincipal());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        assertNotNull(details);
        assertEquals(456, details.get("pacienteId"));
        assertEquals("PATIENT", details.get("role"));
        
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoAuthorizationHeader_ContinuesWithoutAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_AuthorizationHeaderWithoutBearer_ContinuesWithoutAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidToken_LogsWarningAndContinues() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid.token.here");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_ExpiredToken_LogsWarningAndContinues() throws ServletException, IOException {
        // Arrange
        String expiredToken = createExpiredToken("testuser", "ADMIN");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_TokenWithWrongSecret_LogsWarningAndContinues() throws ServletException, IOException {
        // Arrange
        String tokenWithWrongSecret = createTokenWithDifferentSecret("testuser", "ADMIN");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + tokenWithWrongSecret);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_TokenWithBothEmployeeAndPatientId_SetsAllDetails() throws ServletException, IOException {
        // Arrange
        String token = createValidToken("dualuser", "EMPLOYEE", 789, 101);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> details = (Map<String, Object>) auth.getDetails();
        assertEquals(789, details.get("empleadoId"));
        assertEquals(101, details.get("pacienteId"));
        assertEquals("EMPLOYEE", details.get("role"));
        
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_TokenWithRolePrefixAndWithout_BothAuthoritiesSet() throws ServletException, IOException {
        // Arrange
        String token = createValidToken("testuser", "LAB_EMPLOYEE", 555, null);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        
        // Verificar que tiene ambas autoridades (con y sin prefijo ROLE_)
        assertTrue(auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("LAB_EMPLOYEE")));
        assertTrue(auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_LAB_EMPLOYEE")));
        assertEquals(2, auth.getAuthorities().size());
        
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_EmptyBearerToken_LogsWarningAndContinues() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer ");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNull(auth);
        verify(filterChain).doFilter(request, response);
    }

    // Helper methods
    private String createValidToken(String username, String role, Integer empleadoId, Integer pacienteId) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        
        var builder = Jwts.builder()
            .subject(username)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 3600000)); // 1 hora
        
        if (empleadoId != null) {
            builder.claim("empleadoId", empleadoId);
        }
        if (pacienteId != null) {
            builder.claim("pacienteId", pacienteId);
        }
        
        return builder.signWith(key).compact();
    }

    private String createExpiredToken(String username, String role) {
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
            .subject(username)
            .claim("role", role)
            .issuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 horas atrás
            .expiration(new Date(System.currentTimeMillis() - 3600000)) // Expiró hace 1 hora
            .signWith(key)
            .compact();
    }

    private String createTokenWithDifferentSecret(String username, String role) {
        String differentSecret = "unSecretoDiferenteQueNoCoincideConElQueUsaElFiltroParaValidarTokens";
        SecretKey key = Keys.hmacShaKeyFor(differentSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
            .subject(username)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(key)
            .compact();
    }
}
