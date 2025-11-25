package com.api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiGatewayController {
    @GetMapping("/status")
    public String status() {
        return "API Gateway activo";
    }
}
