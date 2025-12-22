package com.api_gateway.ms_api_gateway.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests unitarios para JwtProperties")
class JwtPropertiesTest {

    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
    }

    @Test
    @DisplayName("Debe establecer y obtener el secreto JWT correctamente")
    void testSetAndGetSecret() {
        // Arrange
        String secret = "miClaveSecretaDe32CaracteresMinimo12345678";

        // Act
        jwtProperties.setSecret(secret);

        // Assert
        assertEquals(secret, jwtProperties.getSecret());
    }

    @Test
    @DisplayName("Debe establecer y obtener el tiempo de expiración correctamente")
    void testSetAndGetExpMin() {
        // Arrange
        Integer expMin = 60;

        // Act
        jwtProperties.setExpMin(expMin);

        // Assert
        assertEquals(expMin, jwtProperties.getExpMin());
    }

    @Test
    @DisplayName("Debe manejar valores nulos")
    void testNullValues() {
        // Assert valores iniciales nulos
        assertNull(jwtProperties.getSecret());
        assertNull(jwtProperties.getExpMin());

        // Act - establecer valores nulos explícitamente
        jwtProperties.setSecret(null);
        jwtProperties.setExpMin(null);

        // Assert
        assertNull(jwtProperties.getSecret());
        assertNull(jwtProperties.getExpMin());
    }

    @Test
    @DisplayName("Debe aceptar secretos de diferentes longitudes")
    void testDifferentSecretLengths() {
        // Test con secreto corto (no recomendado pero válido sintácticamente)
        jwtProperties.setSecret("short");
        assertEquals("short", jwtProperties.getSecret());

        // Test con secreto largo
        String longSecret = "estaEsUnaClaveMuyLargaParaProbarQueNoHayLimiteDeCaracteresenElSecreto12345678901234567890";
        jwtProperties.setSecret(longSecret);
        assertEquals(longSecret, jwtProperties.getSecret());
    }

    @Test
    @DisplayName("Debe aceptar diferentes valores de expiración")
    void testDifferentExpirationValues() {
        // Test con valor pequeño
        jwtProperties.setExpMin(1);
        assertEquals(1, jwtProperties.getExpMin());

        // Test con valor grande (1 día en minutos)
        jwtProperties.setExpMin(1440);
        assertEquals(1440, jwtProperties.getExpMin());

        // Test con valor cero
        jwtProperties.setExpMin(0);
        assertEquals(0, jwtProperties.getExpMin());
    }
}
