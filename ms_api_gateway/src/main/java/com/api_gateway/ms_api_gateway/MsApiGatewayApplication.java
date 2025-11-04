package com.api_gateway.ms_api_gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MsApiGatewayApplication {

	private static final Logger logger = LoggerFactory.getLogger(MsApiGatewayApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MsApiGatewayApplication.class, args);
	}

	@Bean
	public CommandLineRunner startupBanner() {
		return args -> {
			logger.info("\n" +
				"╔════════════════════════════════════════════════════════════════════╗\n" +
				"║                      API GATEWAY INICIADO                          ║\n" +
				"╠════════════════════════════════════════════════════════════════════╣\n" +
				"║  Puerto:                    8080                                   ║\n" +
				"║  Spring Cloud Gateway:      ACTIVO                                 ║\n" +
				"║  Seguridad JWT:             CENTRALIZADA                           ║\n" +
				"╠════════════════════════════════════════════════════════════════════╣\n" +
				"║  🌐 RUTAS PÚBLICAS (sin autenticación):                            ║\n" +
				"║                                                                    ║\n" +
				"║    POST   /auth/login          -> Autenticación (8082)            ║\n" +
				"║    POST   /auth/logout         -> Cerrar sesión (Gateway)         ║\n" +
				"║    GET    /labs                -> Listar laboratorios (8081)      ║\n" +
				"║    GET    /labs/{id}           -> Ver laboratorio (8081)          ║\n" +
				"╠════════════════════════════════════════════════════════════════════╣\n" +
				"║  🔒 USUARIOS (requiere JWT - SOLO LECTURA):                        ║\n" +
				"║                                                                    ║\n" +
				"║    GET    /users               -> Listar usuarios (8082)          ║\n" +
				"║    GET    /users/{id}          -> Ver usuario (8082)              ║\n" +
				"║    ❌ POST/PUT/DELETE /users   -> BLOQUEADO (usar registro)       ║\n" +
				"╠════════════════════════════════════════════════════════════════════╣\n" +
				"║  👥 REGISTRO (requiere JWT - ADMIN):                               ║\n" +
				"║                                                                    ║\n" +
				"║    POST   /registro/paciente   -> Crear paciente + usuario (8082) ║\n" +
				"║    POST   /registro/empleado   -> Crear empleado + usuario (8082) ║\n" +
				"╠════════════════════════════════════════════════════════════════════╣\n" +
				"║  🩺 PACIENTES (requiere JWT):                                      ║\n" +
				"║                                                                    ║\n" +
				"║    GET    /pacientes           -> Listar pacientes (8082)         ║\n" +
				"║    GET    /pacientes/{id}      -> Ver paciente (8082)             ║\n" +
				"║    PUT    /pacientes/{id}      -> Actualizar paciente (8082)      ║\n" +
				"║    DELETE /pacientes/{id}      -> Eliminar paciente (8082)        ║\n" +
				"╠════════════════════════════════════════════════════════════════════╣\n" +
				"║  👨‍💼 EMPLEADOS (requiere JWT):                                      ║\n" +
				"║                                                                    ║\n" +
				"║    GET    /empleados           -> Listar empleados (8082)         ║\n" +
				"║    GET    /empleados/{id}      -> Ver empleado (8082)             ║\n" +
				"║    PUT    /empleados/{id}      -> Actualizar empleado (8082)      ║\n" +
				"║    DELETE /empleados/{id}      -> Eliminar empleado (8082)        ║\n" +
				"╠════════════════════════════════════════════════════════════════════╣\n" +
				"║  🏥 LABORATORIOS (requiere JWT para POST/PUT/DELETE):              ║\n" +
				"║                                                                    ║\n" +
				"║    POST   /labs                -> Crear laboratorio (8081)        ║\n" +
				"║    PUT    /labs/{id}           -> Actualizar laboratorio (8081)   ║\n" +
				"║    DELETE /labs/{id}           -> Eliminar laboratorio (8081)     ║\n" +
				"╠════════════════════════════════════════════════════════════════════╣\n" +
				"║  🧪 EXÁMENES (requiere JWT):                                       ║\n" +
				"║                                                                    ║\n" +
				"║    GET    /exams               -> Listar exámenes (8081)          ║\n" +
				"║    GET    /exams/{id}          -> Ver examen (8081)               ║\n" +
				"║    POST   /exams               -> Crear examen (8081)             ║\n" +
				"║    PUT    /exams/{id}          -> Actualizar examen (8081)        ║\n" +
				"║    DELETE /exams/{id}          -> Eliminar examen (8081)          ║\n" +
				"╠════════════════════════════════════════════════════════════════════╣\n" +
				"║  📅 AGENDAS (requiere JWT):                                        ║\n" +
				"║                                                                    ║\n" +
				"║    GET    /agendas             -> Listar agendas (8081)           ║\n" +
				"║    GET    /agendas/{id}        -> Ver agenda (8081)               ║\n" +
				"║    POST   /agendas             -> Crear agenda (8081)             ║\n" +
				"║    PUT    /agendas/{id}        -> Actualizar agenda (8081)        ║\n" +
				"║    DELETE /agendas/{id}        -> Eliminar agenda (8081)          ║\n" +
				"╠════════════════════════════════════════════════════════════════════╣\n" +
				"║  📊 RESULTADOS (requiere JWT):                                     ║\n" +
				"║                                                                    ║\n" +
				"║    GET    /results             -> Listar resultados (8081)        ║\n" +
				"║    GET    /results/{id}        -> Ver resultado (8081)            ║\n" +
				"║    POST   /results             -> Crear resultado (8081)          ║\n" +
				"║    PUT    /results/{id}        -> Actualizar resultado (8081)     ║\n" +
				"║    DELETE /results/{id}        -> Eliminar resultado (8081)       ║\n" +
				"╠════════════════════════════════════════════════════════════════════╣\n" +
				"║  🔗 LAB-EXAMS (requiere JWT):                                      ║\n" +
				"║                                                                    ║\n" +
				"║    GET    /lab-exams           -> Listar lab-exams (8081)         ║\n" +
				"║    GET    /lab-exams/{id}      -> Ver lab-exam (8081)             ║\n" +
				"║    POST   /lab-exams           -> Crear lab-exam (8081)           ║\n" +
				"║    PUT    /lab-exams/{id}      -> Actualizar lab-exam (8081)      ║\n" +
				"║    DELETE /lab-exams/{id}      -> Eliminar lab-exam (8081)        ║\n" +
				"╠════════════════════════════════════════════════════════════════════╣\n" +
				"║  ⚙️  CARACTERÍSTICAS:                                              ║\n" +
				"║                                                                    ║\n" +
				"║    ✓ Validación JWT centralizada en Gateway                       ║\n" +
				"║    ✓ Token blacklist para logout                                  ║\n" +
				"║    ✓ Expiración de token: 120 minutos                             ║\n" +
				"║    ✓ CORS habilitado globalmente                                  ║\n" +
				"║    ✓ Usuarios creados automáticamente al registrar                ║\n" +
				"╚════════════════════════════════════════════════════════════════════╝"
			);
		};
	}
}

