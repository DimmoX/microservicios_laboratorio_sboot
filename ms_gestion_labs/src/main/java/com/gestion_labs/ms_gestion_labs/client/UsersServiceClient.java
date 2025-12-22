package com.gestion_labs.ms_gestion_labs.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.gestion_labs.ms_gestion_labs.dto.PacienteDTO;

/**
 * Cliente para comunicación con el microservicio de usuarios
 */
@Component
public class UsersServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(UsersServiceClient.class);
    
    private final RestTemplate restTemplate;
    
    @Value("${app.users-service.url:http://users-service:8083}")
    private String usersServiceUrl;

    public UsersServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Obtiene un paciente por ID desde users-service
     * @param pacienteId ID del paciente
     * @return PacienteDTO con la información del paciente, o null si no se encuentra
     */
    @SuppressWarnings("unchecked")
    public PacienteDTO getPacienteById(Long pacienteId) {
        try {
            String url = usersServiceUrl + "/pacientes/" + pacienteId;
            logger.debug("Consultando paciente en: {}", url);
            
            // Construir headers para llamada inter-servicio
            HttpHeaders headers = new HttpHeaders();
            // Header especial para identificar llamadas internas entre microservicios
            headers.set("X-Internal-Service", "labs-service");
            
            // También pasar contexto del usuario si está disponible
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                String userId = auth.getName();
                String role = auth.getAuthorities().stream()
                    .map(a -> a.getAuthority())
                    .findFirst()
                    .orElse("");
                
                headers.set("X-User-Id", userId);
                headers.set("X-User-Role", role);
            }
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                Map.class
            );
            
            Map<String, Object> response = responseEntity.getBody();
            
            if (response != null && response.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                
                PacienteDTO paciente = new PacienteDTO();
                paciente.setId(pacienteId);
                paciente.setPnombre((String) data.get("pnombre"));
                paciente.setSnombre((String) data.get("snombre"));
                paciente.setPapellido((String) data.get("papellido"));
                paciente.setSapellido((String) data.get("sapellido"));
                
                return paciente;
            }
            
            return null;
            
        } catch (Exception e) {
            logger.error("Error al obtener paciente {}: {}", pacienteId, e.getMessage());
            return null;
        }
    }
}
