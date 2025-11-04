package com.api_gateway.ms_api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de rutas para Spring Cloud Gateway.
 * Define todas las rutas públicas y privadas del sistema.
 * 
 * NOTA: La validación JWT se maneja con JwtGlobalFilter,
 * por lo que las rutas aquí solo definen el enrutamiento.
 */
@Configuration
public class GatewayConfig {

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
                .uri("http://localhost:8082"))
            
            // Listar laboratorios
            .route("labs-public-list", r -> r
                .path("/labs")
                .and().method("GET")
                .uri("http://localhost:8081"))
            
            // Obtener laboratorio por su id
            .route("labs-public-detail", r -> r
                .path("/labs/{id}")
                .and().method("GET")
                .uri("http://localhost:8081"))
            
            // ==========================================
            // RUTAS PRIVADAS (requieren JWT)
            // ==========================================
            
            // Obtener lista de usuarios / usuario por id 
            .route("users-service-read-only", r -> r
                .path("/users/**")
                .and().method("GET")
                .uri("http://localhost:8082"))
            
            // Registro de empleado
            .route("registro-empleado", r -> r
                .path("/registro/empleado")
                .and().method("POST")
                .uri("http://localhost:8082"))
            
            // Registro de paciente
            .route("registro-paciente", r -> r
                .path("/registro/paciente")
                .and().method("POST")
                .uri("http://localhost:8082"))
            
            // Empleados
            .route("empleados-service", r -> r
                .path("/empleados/**")
                .uri("http://localhost:8082"))
            
            // Pacientes (GET, POST, PUT, DELETE)
            .route("pacientes-service", r -> r
                .path("/pacientes/**")
                .uri("http://localhost:8082"))
            
            // Laboratorios (POST, PUT, DELETE)
            .route("labs-service-private", r -> r
                .path("/labs", "/labs/**")
                .and().method("POST", "PUT", "DELETE")
                .uri("http://localhost:8081"))
            
            // Exámenes (GET, POST, PUT, DELETE)
            .route("exams-service", r -> r
                .path("/exams/**")
                .uri("http://localhost:8081"))
            
            // Agendas (GET, POST, PUT, DELETE)
            .route("agendas-service", r -> r
                .path("/agendas/**")
                .uri("http://localhost:8081"))
            
            // Resultados (GET, POST, PUT, DELETE)
            .route("results-service", r -> r
                .path("/results/**")
                .uri("http://localhost:8081"))
            
            // Lab-Exams (GET, POST, PUT, DELETE)
            .route("lab-exams-service", r -> r
                .path("/lab-exams/**")
                .uri("http://localhost:8081"))
            
            .build();
    }
}
