package com.api_gateway.ms_api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de rutas para Spring Cloud Gateway.
 * Define todas las rutas públicas y privadas del sistema.
 *
 * NOTA: La validación JWT se maneja con JwtGlobalFilter,
 * y AddUserHeadersFilter (global) agrega los headers X-User-Id y X-User-Role.
 */
@Configuration
public class GatewayConfig {

    @Value("${app.services.users}")
    private String usersServiceUrl;

    @Value("${app.services.labs}")
    private String labsServiceUrl;

    @Value("${app.services.resultados}")
    private String resultadosServiceUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // ==========================================
            // RUTAS PÚBLICAS (sin JWT)
            // ==========================================
            
            // Login
            .route("auth-login", r -> r
                .path("/auth/login")
                .and().method("POST")
                .uri(usersServiceUrl))

            // Listar laboratorios
            .route("labs-public-list", r -> r
                .path("/labs")
                .and().method("GET")
                .uri(labsServiceUrl))

            // Obtener laboratorio por su id
            .route("labs-public-detail", r -> r
                .path("/labs/{id}")
                .and().method("GET")
                .uri(labsServiceUrl))

            // ==========================================
            // RUTAS PRIVADAS (requieren JWT)
            // ==========================================

            // Usuarios (GET, POST, PUT, DELETE)
            .route("users-service", r -> r
                .path("/users/**")
                .and().method("GET", "POST", "PUT", "DELETE")
                .uri(usersServiceUrl))

            // Registro de empleado
            .route("registro-empleado", r -> r
                .path("/registro/empleado")
                .and().method("POST")
                .uri(usersServiceUrl))

            // Registro de paciente
            .route("registro-paciente", r -> r
                .path("/registro/paciente")
                .and().method("POST")
                .uri(usersServiceUrl))

            // Empleados
            .route("empleados-service", r -> r
                .path("/empleados/**")
                .uri(usersServiceUrl))

            // Pacientes (GET, POST, PUT, DELETE)
            .route("pacientes-service", r -> r
                .path("/pacientes/**")
                .uri(usersServiceUrl))

            // Laboratorios (POST, PUT, DELETE)
            .route("labs-service-private", r -> r
                .path("/labs", "/labs/**")
                .and().method("POST", "PUT", "DELETE")
                .uri(labsServiceUrl))

            // Exámenes (GET, POST, PUT, DELETE)
            .route("exams-service", r -> r
                .path("/exams/**")
                .uri(labsServiceUrl))

            // Agendas (GET, POST, PUT, DELETE)
            .route("agendas-service", r -> r
                .path("/agenda/**")
                .uri(labsServiceUrl))

            // Resultados (GET, POST, PUT, DELETE)
            // El microservicio decodifica el JWT desde el header Authorization
            .route("results-service", r -> r
                .path("/resultados/**")
                .uri(resultadosServiceUrl))

            // Lab-Exams (GET, POST, PUT, DELETE)
            .route("lab-exams-service", r -> r
                .path("/lab-exams/**")
                .uri(labsServiceUrl))
            
            .build();
    }
}
