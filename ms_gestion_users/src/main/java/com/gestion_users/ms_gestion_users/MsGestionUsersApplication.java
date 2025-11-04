package com.gestion_users.ms_gestion_users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class MsGestionUsersApplication {

	private static final Logger logger = LoggerFactory.getLogger(MsGestionUsersApplication.class);

	@Value("${server.port:8080}")
	private String port;

	public static void main(String[] args) {
		SpringApplication.run(MsGestionUsersApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		logger.info("╔════════════════════════════════════════════════════════════════╗");
		logger.info("║  🚀 Microservicio GESTIÓN DE USUARIOS iniciado exitosamente    ║");
		logger.info("║  📡 Escuchando en: http://localhost:{}                      ║", port);
		logger.info("║  � Control de Acceso Basado en Roles (RBAC) ACTIVO             ║");
		logger.info("║                                                                ║");
		logger.info("║  👥 Roles disponibles:                                         ║");
		logger.info("║     • ADMIN         - Control total del sistema                ║");
		logger.info("║     • LAB_EMPLOYEE  - Solo lectura (usuarios, pacientes, labs) ║");
		logger.info("║     • PATIENT       - Editar perfil propio, ver resultados     ║");
		logger.info("║                                                                ║");
		logger.info("║  📚 Endpoints públicos (sin JWT):                              ║");
		logger.info("║     - POST /auth/login                                         ║");
		logger.info("║     - POST /auth/generate-hash                                 ║");
		logger.info("║                                                                ║");
		logger.info("║  � Autenticación (requiere JWT válido):                       ║");
		logger.info("║     - POST /auth/logout                                        ║");
		logger.info("║                                                                ║");
		logger.info("║  �🔒 Endpoints protegidos (requiere JWT + rol):                 ║");
		logger.info("║     - POST   /registro/empleado  (ADMIN)                       ║");
		logger.info("║     - POST   /registro/paciente  (ADMIN)                       ║");
		logger.info("║     - GET    /users              (ADMIN, LAB_EMPLOYEE)         ║");
		logger.info("║     - POST   /users              (ADMIN)                       ║");
		logger.info("║     - GET    /empleados          (ADMIN, LAB_EMPLOYEE)         ║");
		logger.info("║     - POST   /empleados          (ADMIN)                       ║");
		logger.info("║     - GET    /pacientes          (ADMIN, LAB_EMPLOYEE)         ║");
		logger.info("║     - POST   /pacientes          (ADMIN)                       ║");
		logger.info("║                                                                ║");
		logger.info("║  📖 Ver RBAC_DOCUMENTATION.md para más detalles                ║");
		logger.info("╚════════════════════════════════════════════════════════════════╝");
	}

}

