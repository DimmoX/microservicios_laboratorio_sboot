package com.api_gateway.ms_api_gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades de configuración para JWT.
 * Lee los valores desde application.yml (app.jwt.*)
 */
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    
    private String secret;
    private Integer expMin;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Integer getExpMin() {
        return expMin;
    }

    public void setExpMin(Integer expMin) {
        this.expMin = expMin;
    }
}
