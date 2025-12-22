#!/bin/bash

echo "=========================================="
echo "🧪 EJECUTANDO TESTS Y ANÁLISIS SONARQUBE"
echo "=========================================="
echo ""

# Token de SonarQube (reemplazar con tu token real)
SONAR_TOKEN=${SONAR_TOKEN:-"sqp_99a9c7b78b90737c4b644c43ba28549363d90219"}
SONAR_HOST="http://localhost:9000"

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Función para ejecutar tests y análisis en un microservicio
analyze_microservice() {
    local service_name=$1
    local service_path=$2
    
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}📦 Analizando: ${service_name}${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    
    cd "$service_path" || exit
    
    echo -e "${YELLOW}🧹 Limpiando proyecto...${NC}"
    mvn clean
    
    echo ""
    echo -e "${YELLOW}🧪 Ejecutando tests y generando reporte JaCoCo...${NC}"
    mvn test jacoco:report
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ Error en tests de ${service_name}${NC}"
        cd - > /dev/null
        return 1
    fi
    
    echo ""
    echo -e "${YELLOW}📊 Enviando análisis a SonarQube...${NC}"
    mvn sonar:sonar \
        -Dsonar.host.url=${SONAR_HOST} \
        -Dsonar.token=${SONAR_TOKEN}
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ ${service_name} analizado exitosamente${NC}"
    else
        echo -e "${RED}❌ Error en análisis SonarQube de ${service_name}${NC}"
    fi
    
    echo ""
    cd - > /dev/null
}

# Función para analizar el frontend
analyze_frontend() {
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}🌐 Analizando: Frontend Angular${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    
    cd frontend_gestion_labs || exit
    
    echo -e "${YELLOW}🧪 Ejecutando tests con cobertura...${NC}"
    npm run test:coverage
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ Error en tests del frontend${NC}"
        cd - > /dev/null
        return 1
    fi
    
    echo ""
    echo -e "${YELLOW}📊 Enviando análisis a SonarQube...${NC}"
    npm run sonar
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Frontend analizado exitosamente${NC}"
    else
        echo -e "${RED}❌ Error en análisis SonarQube del frontend${NC}"
    fi
    
    echo ""
    cd - > /dev/null
}

# Verificar que SonarQube esté corriendo
echo -e "${YELLOW}🔍 Verificando conexión con SonarQube...${NC}"
if ! curl -s -o /dev/null -w "%{http_code}" "${SONAR_HOST}" | grep -q "200\|301\|302"; then
    echo -e "${RED}❌ Error: SonarQube no está disponible en ${SONAR_HOST}${NC}"
    echo -e "${YELLOW}💡 Asegúrate de que SonarQube esté corriendo (docker-compose up sonarqube)${NC}"
    exit 1
fi
echo -e "${GREEN}✅ SonarQube está disponible${NC}"
echo ""

# Obtener directorio base
BASE_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$BASE_DIR" || exit

# Analizar cada microservicio
echo -e "${GREEN}🚀 Iniciando análisis de microservicios...${NC}"
echo ""

analyze_microservice "MS Gestion Labs" "${BASE_DIR}/ms_gestion_labs"
analyze_microservice "MS Gestion Users" "${BASE_DIR}/ms_gestion_users"
analyze_microservice "MS Gestion Resultados" "${BASE_DIR}/ms_gestion_resultados"
analyze_microservice "MS API Gateway" "${BASE_DIR}/ms_api_gateway"

# Analizar frontend
echo -e "${GREEN}🚀 Iniciando análisis del frontend...${NC}"
echo ""
analyze_frontend

echo ""
echo "=========================================="
echo -e "${GREEN}✅ ANÁLISIS COMPLETADO${NC}"
echo "=========================================="
echo ""
echo -e "${BLUE}📊 Ver resultados en: ${SONAR_HOST}${NC}"
echo ""
echo "Proyectos analizados:"
echo "  • ms_gestion_labs"
echo "  • ms_gestion_users"
echo "  • ms_gestion_resultados"
echo "  • ms_api_gateway"
echo "  • frontend_gestion_labs"
echo ""
