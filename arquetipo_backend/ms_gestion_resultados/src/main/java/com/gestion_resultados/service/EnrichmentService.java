package com.gestion_resultados.ms_gestion_resultados.service;

import com.gestion_resultados.ms_gestion_resultados.model.ResultadoExamenModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Servicio para enriquecer resultados con nombres de pacientes y exámenes
 * consultando a los otros microservicios
 */
@Service
public class EnrichmentService {

    private static final Logger logger = LoggerFactory.getLogger(EnrichmentService.class);

    private final RestTemplate restTemplate;

    @Value("${app.services.users:http://users-service:8083}")
    private String usersServiceUrl;

    @Value("${app.services.labs:http://labs-service:8081}")
    private String labsServiceUrl;

    public EnrichmentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Enriquece un resultado con los nombres de paciente y examen
     */
    public void enrichResultado(ResultadoExamenModel resultado) {
        try {
            // Obtener nombre del paciente
            String pacienteNombre = obtenerNombrePaciente(resultado.getPacienteId());
            resultado.setPacienteNombre(pacienteNombre);
        } catch (Exception e) {
            logger.warn("No se pudo obtener nombre del paciente ID {}: {}",
                    resultado.getPacienteId(), e.getMessage());
        }

        try {
            // Obtener nombre del examen
            String examenNombre = obtenerNombreExamen(resultado.getExamenId());
            resultado.setExamenNombre(examenNombre);
        } catch (Exception e) {
            logger.warn("No se pudo obtener nombre del examen ID {}: {}",
                    resultado.getExamenId(), e.getMessage());
        }
    }

    /**
     * Enriquece una lista de resultados
     */
    public void enrichResultados(List<ResultadoExamenModel> resultados) {
        resultados.forEach(this::enrichResultado);
    }

    /**
     * Obtiene el nombre de un paciente desde el microservicio de usuarios
     */
    private String obtenerNombrePaciente(Long pacienteId) {
        try {
            String url = usersServiceUrl + "/pacientes/" + pacienteId;
            logger.info("Consultando nombre de paciente en: {}", url);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null && data.containsKey("nombre")) {
                    String nombre = (String) data.get("nombre");
                    logger.info("Nombre de paciente {} encontrado: {}", pacienteId, nombre);
                    return nombre;
                }
            }
        } catch (Exception e) {
            logger.error("Error al obtener paciente {}: {}", pacienteId, e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene el nombre de un examen desde el microservicio de laboratorios
     */
    private String obtenerNombreExamen(Long examenId) {
        try {
            String url = labsServiceUrl + "/exams/" + examenId;
            logger.info("Consultando nombre de examen en: {}", url);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null && data.containsKey("nombre")) {
                    String nombre = (String) data.get("nombre");
                    logger.info("Nombre de examen {} encontrado: {}", examenId, nombre);
                    return nombre;
                }
            }
        } catch (Exception e) {
            logger.error("Error al obtener examen {}: {}", examenId, e.getMessage());
        }
        return null;
    }
}
