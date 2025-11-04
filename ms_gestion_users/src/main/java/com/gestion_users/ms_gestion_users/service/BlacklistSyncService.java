package com.gestion_users.ms_gestion_users.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para sincronizar la blacklist de tokens con otros microservicios.
 * 
 * Cuando un usuario hace logout, este servicio notifica a ms_gestion_labs
 * para que también invalide el token en su blacklist local.
 */
@Service
public class BlacklistSyncService {

    private static final Logger logger = LoggerFactory.getLogger(BlacklistSyncService.class);
    private final RestTemplate restTemplate;

    @Value("${app.labs-service.url:http://localhost:8081}")
    private String labsServiceUrl;

    public BlacklistSyncService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Sincroniza un token blacklisted con el microservicio de laboratorios.
     * 
     * @param token JWT token que fue agregado a la blacklist
     */
    public void syncTokenToLabs(String token) {
        try {
            String url = labsServiceUrl + "/internal/blacklist/add";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> body = new HashMap<>();
            body.put("token", token);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            
            restTemplate.postForEntity(url, request, Map.class);
            logger.info("Token sincronizado exitosamente con ms_gestion_labs");
            
        } catch (Exception e) {
            // No fallar el logout si la sincronización falla
            // El token está blacklisted en ms_gestion_users de todos modos
            logger.warn("No se pudo sincronizar token con ms_gestion_labs: {}", e.getMessage());
        }
    }
}
