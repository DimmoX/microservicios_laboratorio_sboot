package com.gestion_resultados.ms_gestion_resultados.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.gestion_resultados.ms_gestion_resultados.model.ResultadoExamenModel;

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
    public void enrichResultado(ResultadoExamenModel resultado, String jwtToken) {
        try {
            // Obtener nombre del paciente
            String pacienteNombre = obtenerNombrePaciente(resultado.getPacienteId(), jwtToken);
            resultado.setPacienteNombre(pacienteNombre);
        } catch (Exception e) {
            logger.warn("No se pudo obtener nombre del paciente ID {}: {}",
                    resultado.getPacienteId(), e.getMessage());
        }

        try {
            // Obtener nombre del examen
            String examenNombre = obtenerNombreExamen(resultado.getExamenId(), jwtToken);
            resultado.setExamenNombre(examenNombre);
        } catch (Exception e) {
            logger.warn("No se pudo obtener nombre del examen ID {}: {}",
                    resultado.getExamenId(), e.getMessage());
        }
    }

    /**
     * Enriquece una lista de resultados
     */
    public List<ResultadoExamenModel> enrichResultados(List<ResultadoExamenModel> resultados, String jwtToken) {
        resultados.forEach(resultado -> enrichResultado(resultado, jwtToken));
        return resultados;
    }

    /**
     * Obtiene el nombre de un paciente desde el microservicio de usuarios
     */
    private String obtenerNombrePaciente(Long pacienteId, String jwtToken) {
        try {
            String url = usersServiceUrl + "/pacientes/" + pacienteId;
            logger.info("Consultando nombre de paciente en: {}", url);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", jwtToken);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            org.springframework.http.ResponseEntity<Map> responseEntity = restTemplate.exchange(
                url, 
                org.springframework.http.HttpMethod.GET, 
                entity, 
                Map.class
            );
            
            Map<String, Object> response = responseEntity.getBody();

            if (response != null && response.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null) {
                    // Construir nombre completo desde los campos individuales
                    StringBuilder nombreCompleto = new StringBuilder();
                    if (data.containsKey("pnombre")) nombreCompleto.append(data.get("pnombre")).append(" ");
                    if (data.containsKey("snombre")) nombreCompleto.append(data.get("snombre")).append(" ");
                    if (data.containsKey("papellido")) nombreCompleto.append(data.get("papellido")).append(" ");
                    if (data.containsKey("sapellido")) nombreCompleto.append(data.get("sapellido"));
                    
                    String nombre = nombreCompleto.toString().trim();
                    if (!nombre.isEmpty()) {
                        logger.info("Nombre de paciente {} encontrado: {}", pacienteId, nombre);
                        return nombre;
                    }
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
    private String obtenerNombreExamen(Long examenId, String jwtToken) {
        try {
            String url = labsServiceUrl + "/exams/" + examenId;
            logger.info("Consultando nombre de examen en: {}", url);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", jwtToken);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            org.springframework.http.ResponseEntity<Map> responseEntity = restTemplate.exchange(
                url, 
                org.springframework.http.HttpMethod.GET, 
                entity, 
                Map.class
            );
            
            Map<String, Object> response = responseEntity.getBody();

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
