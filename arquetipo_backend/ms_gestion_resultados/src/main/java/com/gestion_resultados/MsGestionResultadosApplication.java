package com.gestion_resultados.ms_gestion_resultados;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MsGestionResultadosApplication {

    private static final Logger logger = LoggerFactory.getLogger(MsGestionResultadosApplication.class);

    public static void main(String[] args) {
        logger.info("╔════════════════════════════════════════════════════════╗");
        logger.info("║   Iniciando Microservicio: Gestión de Resultados      ║");
        logger.info("╚════════════════════════════════════════════════════════╝");
        SpringApplication.run(MsGestionResultadosApplication.class, args);
        logger.info("✓ Microservicio Gestión de Resultados iniciado correctamente");
    }
}
