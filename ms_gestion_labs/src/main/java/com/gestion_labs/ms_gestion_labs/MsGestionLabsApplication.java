package com.gestion_labs.ms_gestion_labs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class MsGestionLabsApplication {

	private static final Logger logger = LoggerFactory.getLogger(MsGestionLabsApplication.class);

	@Value("${server.port:8081}")
	private String port;

	public static void main(String[] args) {
		SpringApplication.run(MsGestionLabsApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		logger.info("╔════════════════════════════════════════════════════════════════╗");
		logger.info("║  🚀 Microservicio GESTIÓN DE LABORATORIOS iniciado exitosamente║");
		logger.info("║  📡 Puerto interno: {}                                      ║", port);
		logger.info("║  �️  Detrás del API Gateway (puerto 8080)                      ║");
		logger.info("║                                                                ║");
		logger.info("║  ⚠️  IMPORTANTE: Este servicio NO valida JWT                   ║");
		logger.info("║     La autenticación la maneja el API Gateway                  ║");
		logger.info("║     Headers recibidos: X-User-Id, X-User-Role                  ║");
		logger.info("║                                                                ║");
		logger.info("║  📚 Endpoints disponibles:                                     ║");
		logger.info("║     - GET    /labs                (Listar laboratorios)        ║");
		logger.info("║     - GET    /labs/{id}           (Ver detalle laboratorio)    ║");
		logger.info("║     - POST   /labs                (Crear laboratorio)          ║");
		logger.info("║     - PUT    /labs/{id}           (Actualizar laboratorio)     ║");
		logger.info("║     - DELETE /labs/{id}           (Eliminar laboratorio)       ║");
		logger.info("║     - GET    /exams               (Listar exámenes)            ║");
		logger.info("║     - POST   /exams               (Crear examen)               ║");
		logger.info("║     - GET    /agendas             (Listar agendas)             ║");
		logger.info("║     - POST   /agendas             (Crear agenda)               ║");
		logger.info("║     - GET    /results             (Listar resultados)          ║");
		logger.info("║     - POST   /results             (Crear resultado)            ║");
		logger.info("║                                                                ║");
		logger.info("║  🔐 Acceso SOLO vía API Gateway: http://localhost:8080         ║");
		logger.info("╚════════════════════════════════════════════════════════════════╝");
	}

}
