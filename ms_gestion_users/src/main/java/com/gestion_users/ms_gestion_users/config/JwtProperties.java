package com.gestion_users.ms_gestion_users.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret;
    private int expMin;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getExpMin() {
        return expMin;
    }

    public void setExpMin(int expMin) {
        this.expMin = expMin;
    }
}
