package com.gestion_resultados.ms_gestion_resultados.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración del RestTemplate para llamadas REST.
 * Define el bean RestTemplate para inyectarlo donde se necesite.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
