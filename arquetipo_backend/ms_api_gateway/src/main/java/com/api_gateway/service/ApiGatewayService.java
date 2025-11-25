package com.api_gateway.service;

import org.springframework.stereotype.Service;

@Service
public class ApiGatewayService {
    public String getStatus() {
        return "Servicio API Gateway operativo";
    }
}
