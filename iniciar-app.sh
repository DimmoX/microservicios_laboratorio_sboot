#!/bin/bash
cd "$(dirname "$0")"
set -e

# Construir y arrancar todos los servicios con docker-compose
echo "Construyendo y levantando todos los contenedores con docker-compose..."
docker-compose up --build -d

echo "\n========================================="
echo "Contenedores en ejecución:"
echo "-----------------------------------------"
echo "Frontend (Angular + Nginx):     http://localhost:4200"
echo "API Gateway (Spring Cloud):     http://localhost:8080"
echo "Gestión Labs (Spring Boot):     http://localhost:8081"
echo "Gestión Users (Spring Boot):    http://localhost:8082"
echo "========================================="
echo "Todos los contenedores están en ejecución."
