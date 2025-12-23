package com.gestion_resultados.ms_gestion_resultados.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Tests para JwtUtil
 * Cobertura: Decodificación y extracción de claims de JWT
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String jwtSecret;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        jwtSecret = "mySecretKeyForTestingPurposesOnlyThisIsVeryLongSecretKey123456789";
        secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", jwtSecret);
    }

    @Test
    void testDecodeToken_ValidToken_ReturnsClaimsSuccessfully() {
        // Arrange
        String token = createTestToken("user@test.com", "ADMIN", 1L, 10L, 20L);

        // Act
        Claims claims = jwtUtil.decodeToken(token);

        // Assert
        assertNotNull(claims);
        assertEquals("user@test.com", claims.getSubject());
        assertEquals("ADMIN", claims.get("role", String.class));
        assertEquals(1L, claims.get("userId", Long.class));
        assertEquals(10L, claims.get("pacienteId", Long.class));
        assertEquals(20L, claims.get("empleadoId", Long.class));
    }

    @Test
    void testDecodeToken_WithBearerPrefix_RemovesAndDecodes() {
        // Arrange
        String token = createTestToken("user@test.com", "PATIENT", 1L, 10L, null);
        String bearerToken = "Bearer " + token;

        // Act
        Claims claims = jwtUtil.decodeToken(bearerToken);

        // Assert
        assertNotNull(claims);
        assertEquals("user@test.com", claims.getSubject());
        assertEquals("PATIENT", claims.get("role", String.class));
    }

    @Test
    void testDecodeToken_NullToken_ReturnsNull() {
        // Act
        Claims claims = jwtUtil.decodeToken(null);

        // Assert
        assertNull(claims);
    }

    @Test
    void testDecodeToken_BlankToken_ReturnsNull() {
        // Act
        Claims claims = jwtUtil.decodeToken("   ");

        // Assert
        assertNull(claims);
    }

    @Test
    void testDecodeToken_InvalidToken_ReturnsNull() {
        // Arrange
        String invalidToken = "this.is.not.a.valid.jwt.token";

        // Act
        Claims claims = jwtUtil.decodeToken(invalidToken);

        // Assert
        assertNull(claims);
    }

    @Test
    void testDecodeToken_ExpiredToken_ReturnsNull() {
        // Arrange
        String expiredToken = Jwts.builder()
            .subject("user@test.com")
            .claim("role", "ADMIN")
            .issuedAt(new Date(System.currentTimeMillis() - 3600000))
            .expiration(new Date(System.currentTimeMillis() - 1800000)) // Expired 30 minutes ago
            .signWith(secretKey)
            .compact();

        // Act
        Claims claims = jwtUtil.decodeToken(expiredToken);

        // Assert
        assertNull(claims);
    }

    @Test
    void testDecodeToken_WrongSecret_ReturnsNull() {
        // Arrange
        SecretKey wrongKey = Keys.hmacShaKeyFor("differentSecretKeyThatIsAlsoVeryLongForTesting123456789".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
            .subject("user@test.com")
            .claim("role", "ADMIN")
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(wrongKey)
            .compact();

        // Act
        Claims claims = jwtUtil.decodeToken(token);

        // Assert
        assertNull(claims);
    }

    @Test
    void testGetRole_ValidClaims_ReturnsRole() {
        // Arrange
        String token = createTestToken("user@test.com", "LAB_EMPLOYEE", 1L, null, 15L);
        Claims claims = jwtUtil.decodeToken(token);

        // Act
        String role = jwtUtil.getRole(claims);

        // Assert
        assertEquals("LAB_EMPLOYEE", role);
    }

    @Test
    void testGetRole_NullClaims_ReturnsNull() {
        // Act
        String role = jwtUtil.getRole(null);

        // Assert
        assertNull(role);
    }

    @Test
    void testGetPacienteId_ValidClaims_ReturnsPacienteId() {
        // Arrange
        String token = createTestToken("paciente@test.com", "PATIENT", 1L, 25L, null);
        Claims claims = jwtUtil.decodeToken(token);

        // Act
        Long pacienteId = jwtUtil.getPacienteId(claims);

        // Assert
        assertEquals(25L, pacienteId);
    }

    @Test
    void testGetPacienteId_NullClaims_ReturnsNull() {
        // Act
        Long pacienteId = jwtUtil.getPacienteId(null);

        // Assert
        assertNull(pacienteId);
    }

    @Test
    void testGetPacienteId_MissingClaim_ReturnsNull() {
        // Arrange
        String token = createTestToken("employee@test.com", "EMPLOYEE", 1L, null, 30L);
        Claims claims = jwtUtil.decodeToken(token);

        // Act
        Long pacienteId = jwtUtil.getPacienteId(claims);

        // Assert
        assertNull(pacienteId);
    }

    @Test
    void testGetEmpleadoId_ValidClaims_ReturnsEmpleadoId() {
        // Arrange
        String token = createTestToken("empleado@test.com", "EMPLOYEE", 1L, null, 35L);
        Claims claims = jwtUtil.decodeToken(token);

        // Act
        Long empleadoId = jwtUtil.getEmpleadoId(claims);

        // Assert
        assertEquals(35L, empleadoId);
    }

    @Test
    void testGetEmpleadoId_NullClaims_ReturnsNull() {
        // Act
        Long empleadoId = jwtUtil.getEmpleadoId(null);

        // Assert
        assertNull(empleadoId);
    }

    @Test
    void testGetUserId_ValidClaims_ReturnsUserId() {
        // Arrange
        String token = createTestToken("user@test.com", "ADMIN", 42L, null, null);
        Claims claims = jwtUtil.decodeToken(token);

        // Act
        Long userId = jwtUtil.getUserId(claims);

        // Assert
        assertEquals(42L, userId);
    }

    @Test
    void testGetUserId_NullClaims_ReturnsNull() {
        // Act
        Long userId = jwtUtil.getUserId(null);

        // Assert
        assertNull(userId);
    }

    @Test
    void testGetUsername_ValidClaims_ReturnsUsername() {
        // Arrange
        String token = createTestToken("test.user@example.com", "PATIENT", 1L, 5L, null);
        Claims claims = jwtUtil.decodeToken(token);

        // Act
        String username = jwtUtil.getUsername(claims);

        // Assert
        assertEquals("test.user@example.com", username);
    }

    @Test
    void testGetUsername_NullClaims_ReturnsNull() {
        // Act
        String username = jwtUtil.getUsername(null);

        // Assert
        assertNull(username);
    }

    @Test
    void testDecodeToken_AllClaimTypes_ExtractsCorrectly() {
        // Arrange
        String token = createTestToken("admin@test.com", "ADMIN", 100L, 200L, 300L);
        Claims claims = jwtUtil.decodeToken(token);

        // Act & Assert
        assertNotNull(claims);
        assertEquals("admin@test.com", jwtUtil.getUsername(claims));
        assertEquals("ADMIN", jwtUtil.getRole(claims));
        assertEquals(100L, jwtUtil.getUserId(claims));
        assertEquals(200L, jwtUtil.getPacienteId(claims));
        assertEquals(300L, jwtUtil.getEmpleadoId(claims));
    }

    private String createTestToken(String subject, String role, Long userId, Long pacienteId, Long empleadoId) {
        var builder = Jwts.builder()
            .subject(subject)
            .claim("role", role)
            .claim("userId", userId)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 3600000)); // 1 hour validity

        if (pacienteId != null) {
            builder.claim("pacienteId", pacienteId);
        }
        if (empleadoId != null) {
            builder.claim("empleadoId", empleadoId);
        }

        return builder.signWith(secretKey).compact();
    }
}
