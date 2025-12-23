package com.gestion_labs.ms_gestion_labs.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gestion_labs.ms_gestion_labs.service.TokenBlacklistService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtProperties jwtProperties;

    @Mock
    private TokenBlacklistService blacklistService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private String secretKey = "mySecretKeyForTestingPurposesOnly12345678901234567890";
    private SecretKey key;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        lenient().when(jwtProperties.getSecret()).thenReturn(secretKey);
    }

    private String generateValidToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }

    @Test
    void testDoFilterInternalConTokenValido() throws ServletException, IOException {
        // Arrange
        String token = generateValidToken("admin", "ADMIN");
        lenient().when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        lenient().when(blacklistService.isBlacklisted(token)).thenReturn(false);
        lenient().when(request.getRequestURI()).thenReturn("/api/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("admin", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain, times(1)).doFilter(request, response);
        verify(blacklistService, times(1)).isBlacklisted(token);
    }

    @Test
    void testDoFilterInternalSinAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verify(blacklistService, never()).isBlacklisted(anyString());
    }

    @Test
    void testDoFilterInternalConAuthorizationHeaderInvalido() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic user:password");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verify(blacklistService, never()).isBlacklisted(anyString());
    }

    @Test
    void testDoFilterInternalConTokenEnBlacklist() throws ServletException, IOException {
        // Arrange
        String token = generateValidToken("user", "EMPLOYEE");
        lenient().when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        lenient().when(blacklistService.isBlacklisted(token)).thenReturn(true);
        lenient().when(request.getRequestURI()).thenReturn("/api/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verify(blacklistService, times(1)).isBlacklisted(token);
    }

    @Test
    void testDoFilterInternalConTokenInvalido() throws ServletException, IOException {
        // Arrange
        String invalidToken = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(blacklistService.isBlacklisted(invalidToken)).thenReturn(false);
        when(request.getRequestURI()).thenReturn("/api/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalConTokenExpirado() throws ServletException, IOException {
        // Arrange
        String expiredToken = Jwts.builder()
                .subject("user")
                .claim("role", "PATIENT")
                .issuedAt(new Date(System.currentTimeMillis() - 7200000))
                .expiration(new Date(System.currentTimeMillis() - 3600000)) // Expirado hace 1 hora
                .signWith(key)
                .compact();
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + expiredToken);
        when(blacklistService.isBlacklisted(expiredToken)).thenReturn(false);
        when(request.getRequestURI()).thenReturn("/api/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalConTokenSinRole() throws ServletException, IOException {
        // Arrange
        String tokenSinRole = Jwts.builder()
                .subject("user")
                // No se incluye el claim "role"
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
        
        when(request.getHeader("Authorization")).thenReturn("Bearer " + tokenSinRole);
        when(blacklistService.isBlacklisted(tokenSinRole)).thenReturn(false);
        when(request.getRequestURI()).thenReturn("/api/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        // El filtro debe manejar el caso donde role es null
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalConMultiplesLlamadas() throws ServletException, IOException {
        // Arrange
        String token1 = generateValidToken("user1", "PATIENT");
        String token2 = generateValidToken("user2", "EMPLOYEE");
        
        lenient().when(blacklistService.isBlacklisted(anyString())).thenReturn(false);
        lenient().when(request.getRequestURI()).thenReturn("/api/test");

        // Act & Assert - Primera llamada
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token1);
        filter.doFilterInternal(request, response, filterChain);
        assertEquals("user1", SecurityContextHolder.getContext().getAuthentication().getName());
        
        SecurityContextHolder.clearContext();
        
        // Act & Assert - Segunda llamada
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token2);
        filter.doFilterInternal(request, response, filterChain);
        assertEquals("user2", SecurityContextHolder.getContext().getAuthentication().getName());
        
        verify(filterChain, times(2)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalConTokenVacio() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalConDiferentesRoles() throws ServletException, IOException {
        // Test con rol ADMIN
        String tokenAdmin = generateValidToken("admin", "ADMIN");
        lenient().when(request.getHeader("Authorization")).thenReturn("Bearer " + tokenAdmin);
        lenient().when(blacklistService.isBlacklisted(tokenAdmin)).thenReturn(false);
        lenient().when(request.getRequestURI()).thenReturn("/api/test");

        filter.doFilterInternal(request, response, filterChain);
        
        var authAdmin = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authAdmin);
        assertTrue(authAdmin.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN")));
        
        SecurityContextHolder.clearContext();
        
        // Test con rol EMPLOYEE
        String tokenEmployee = generateValidToken("employee", "EMPLOYEE");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + tokenEmployee);
        when(blacklistService.isBlacklisted(tokenEmployee)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);
        
        var authEmployee = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authEmployee);
        assertTrue(authEmployee.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("EMPLOYEE")));
    }

    @Test
    void testDoFilterInternalContinuaEnCasoDeError() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer malformed.token");
        when(blacklistService.isBlacklisted(anyString())).thenReturn(false);
        when(request.getRequestURI()).thenReturn("/api/test");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        // El filtro debe continuar aunque el token sea inválido
        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
