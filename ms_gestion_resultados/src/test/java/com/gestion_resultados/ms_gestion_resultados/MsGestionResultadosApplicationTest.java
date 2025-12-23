package com.gestion_resultados.ms_gestion_resultados;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Tests para MsGestionResultadosApplication
 */
@ExtendWith(MockitoExtension.class)
class MsGestionResultadosApplicationTest {

    @Test
    void testMain_StartsSpringApplication() {
        // Arrange
        ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            mockedSpringApp.when(() -> SpringApplication.run(
                eq(MsGestionResultadosApplication.class), 
                any(String[].class)
            )).thenReturn(mockContext);

            // Act
            MsGestionResultadosApplication.main(new String[]{});

            // Assert
            mockedSpringApp.verify(() -> SpringApplication.run(
                eq(MsGestionResultadosApplication.class), 
                any(String[].class)
            ), times(1));
        }
    }

    @Test
    void testMain_WithArguments_PassesArgumentsToSpringApplication() {
        // Arrange
        String[] args = {"--server.port=8082", "--spring.profiles.active=test"};
        ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            mockedSpringApp.when(() -> SpringApplication.run(
                eq(MsGestionResultadosApplication.class), 
                eq(args)
            )).thenReturn(mockContext);

            // Act
            MsGestionResultadosApplication.main(args);

            // Assert
            mockedSpringApp.verify(() -> SpringApplication.run(
                eq(MsGestionResultadosApplication.class), 
                eq(args)
            ), times(1));
        }
    }

    @Test
    void testMain_WithEmptyArguments_StartsSuccessfully() {
        // Arrange
        String[] args = {};
        ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            mockedSpringApp.when(() -> SpringApplication.run(
                eq(MsGestionResultadosApplication.class), 
                eq(args)
            )).thenReturn(mockContext);

            // Act
            MsGestionResultadosApplication.main(args);

            // Assert
            mockedSpringApp.verify(() -> SpringApplication.run(
                eq(MsGestionResultadosApplication.class), 
                eq(args)
            ), times(1));
        }
    }

    @Test
    void testMain_LogsStartupMessages() {
        // Arrange
        ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            mockedSpringApp.when(() -> SpringApplication.run(
                eq(MsGestionResultadosApplication.class), 
                any(String[].class)
            )).thenReturn(mockContext);

            // Act - El método main contiene logs, pero no los podemos capturar fácilmente
            // Este test verifica que el método se ejecuta sin errores
            assertDoesNotThrow(() -> MsGestionResultadosApplication.main(new String[]{}));

            // Assert
            mockedSpringApp.verify(() -> SpringApplication.run(
                eq(MsGestionResultadosApplication.class), 
                any(String[].class)
            ), times(1));
        }
    }

    @Test
    void testMain_WithMultipleArguments_AllArgumentsPassedCorrectly() {
        // Arrange
        String[] args = {
            "--server.port=9999",
            "--spring.profiles.active=dev",
            "--logging.level.root=DEBUG"
        };
        ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            mockedSpringApp.when(() -> SpringApplication.run(
                eq(MsGestionResultadosApplication.class), 
                eq(args)
            )).thenReturn(mockContext);

            // Act
            MsGestionResultadosApplication.main(args);

            // Assert
            mockedSpringApp.verify(() -> SpringApplication.run(
                eq(MsGestionResultadosApplication.class), 
                eq(args)
            ), times(1));
        }
    }

    @Test
    void testApplicationClass_HasSpringBootApplicationAnnotation() {
        // Assert
        assertTrue(MsGestionResultadosApplication.class.isAnnotationPresent(
            org.springframework.boot.autoconfigure.SpringBootApplication.class
        ));
    }

    @Test
    void testApplicationClass_IsPublic() {
        // Assert
        assertTrue(java.lang.reflect.Modifier.isPublic(
            MsGestionResultadosApplication.class.getModifiers()
        ));
    }

    @Test
    void testMain_CanBeCalledMultipleTimes() {
        // Arrange
        ConfigurableApplicationContext mockContext = mock(ConfigurableApplicationContext.class);

        try (MockedStatic<SpringApplication> mockedSpringApp = mockStatic(SpringApplication.class)) {
            mockedSpringApp.when(() -> SpringApplication.run(
                eq(MsGestionResultadosApplication.class), 
                any(String[].class)
            )).thenReturn(mockContext);

            // Act - Llamar main dos veces
            MsGestionResultadosApplication.main(new String[]{});
            MsGestionResultadosApplication.main(new String[]{});

            // Assert - Verificar que se llamó dos veces
            mockedSpringApp.verify(() -> SpringApplication.run(
                eq(MsGestionResultadosApplication.class), 
                any(String[].class)
            ), times(2));
        }
    }
}
