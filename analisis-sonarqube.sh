#!/bin/bash

echo "=========================================="
echo "🧪 EJECUTANDO TESTS Y ANÁLISIS SONARQUBE"
echo "   (Proyectos Individuales)"
echo "=========================================="
echo ""

# Token de SonarQube
SONAR_TOKEN=${SONAR_TOKEN:-"sqa_a088d3845350d8295b81b338c4122f619041021a"}
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
    local project_key=$3
    
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}📦 Analizando: ${service_name}${NC}"
    echo -e "${BLUE}   ProjectKey: ${project_key}${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    
    cd "$service_path" || exit
    
    echo -e "${YELLOW}🧪 Ejecutando tests, JaCoCo y SonarQube...${NC}"
    mvn clean verify sonar:sonar \
        -Dsonar.projectKey=${project_key} \
        -Dsonar.projectName="${service_name}" \
        -Dsonar.host.url=${SONAR_HOST} \
        -Dsonar.token=${SONAR_TOKEN}
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Análisis de ${service_name} completado${NC}"
        echo -e "${GREEN}   Dashboard: ${SONAR_HOST}/dashboard?id=${project_key}${NC}"
        cd - > /dev/null
        return 0
    else
        echo -e "${RED}❌ Error en análisis de ${service_name}${NC}"
        cd - > /dev/null
        return 1
    fi
}

# Función especial para API Gateway (usa mvnw)
analyze_api_gateway() {
    local service_name=$1
    local service_path=$2
    local project_key=$3
    
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}📦 Analizando: ${service_name}${NC}"
    echo -e "${BLUE}   ProjectKey: ${project_key}${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    
    cd "$service_path" || exit
    
    echo -e "${YELLOW}🧪 Ejecutando tests, JaCoCo y SonarQube...${NC}"
    ./mvnw clean verify sonar:sonar \
        -Dsonar.projectKey=${project_key} \
        -Dsonar.projectName="${service_name}" \
        -Dsonar.host.url=${SONAR_HOST} \
        -Dsonar.token=${SONAR_TOKEN}
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Análisis de ${service_name} completado${NC}"
        echo -e "${GREEN}   Dashboard: ${SONAR_HOST}/dashboard?id=${project_key}${NC}"
        cd - > /dev/null
        return 0
    else
        echo -e "${RED}❌ Error en análisis de ${service_name}${NC}"
        cd - > /dev/null
        return 1
    fi
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

# Directorio raíz del proyecto
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

# Array de microservicios con sus projectKeys
declare -a services=(
    "MS Gestion Labs:ms_gestion_labs:ms-gestion-labs"
    "MS Gestion Users:ms_gestion_users:ms-gestion-users"
    "MS Gestion Resultados:ms_gestion_resultados:ms-gestion-resultados"
)

# Contador de éxitos y fallos
success_count=0
fail_count=0

# Analizar cada microservicio
for service in "${services[@]}"; do
    IFS=':' read -r name path key <<< "$service"
    
    if analyze_microservice "$name" "$ROOT_DIR/$path" "$key"; then
        ((success_count++))
    else
        ((fail_count++))
    fi
    
    echo ""
done

# Analizar API Gateway (caso especial con mvnw)
if analyze_api_gateway "MS API Gateway" "$ROOT_DIR/ms_api_gateway" "ms-api-gateway"; then
    ((success_count++))
else
    ((fail_count++))
fi

echo ""
# Resumen final
echo -e "${BLUE}=========================================="
echo "📊 RESUMEN DE ANÁLISIS"
echo -e "==========================================${NC}"
echo -e "${GREEN}✅ Exitosos: ${success_count}${NC}"
echo -e "${RED}❌ Fallidos: ${fail_count}${NC}"
echo ""
echo -e "${BLUE}🔗 Dashboards individuales:${NC}"
echo "   ${SONAR_HOST}/dashboard?id=ms-gestion-labs"
echo "   ${SONAR_HOST}/dashboard?id=ms-gestion-users"
echo "   ${SONAR_HOST}/dashboard?id=ms-gestion-resultados"
echo "   ${SONAR_HOST}/dashboard?id=ms-api-gateway"
echo ""
echo -e "${BLUE}📋 Ver todos los proyectos:${NC}"
echo "   ${SONAR_HOST}/projects"
echo ""

if [ $fail_count -eq 0 ]; then
    echo -e "${GREEN}🎉 ¡Todos los análisis se completaron exitosamente!${NC}"
    exit 0
else
    echo -e "${YELLOW}⚠️  Algunos análisis fallaron. Revisa los logs arriba.${NC}"
    exit 1
fi
