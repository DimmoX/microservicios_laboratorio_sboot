#!/usr/bin/env bash

# ============================================================================
# Script de Análisis de Tests y Cobertura con SonarQube
# ============================================================================
# Ejecuta tests de backend (JUnit + JaCoCo) y frontend (Karma + Jasmine)
# Envía análisis a SonarQube (reportes quedan en ubicaciones por defecto)
# ============================================================================

echo "=========================================="
echo "🧪 EJECUTANDO TESTS Y ANÁLISIS SONARQUBE"
echo "   (Backend + Frontend)"
echo "=========================================="
echo ""

# Directorio raíz del proyecto
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Leer token desde archivo generado por iniciar-app.sh
if [ -f "${ROOT_DIR}/.sonar_token" ]; then
    SONAR_TOKEN=$(cat "${ROOT_DIR}/.sonar_token")
    echo "✅ Token cargado desde .sonar_token"
else
    echo "❌ Error: No se encontró .sonar_token"
    echo "   Ejecuta primero: ./iniciar-app.sh"
    exit 1
fi

SONAR_HOST="http://localhost:9000"

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m' # No Color

RESULTS_DIR="$ROOT_DIR/resultados_test"

# Crear directorios
mkdir -p "$RESULTS_DIR/backend"
mkdir -p "$RESULTS_DIR/frontend"

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
    
    echo -e "${CYAN}📊 Se está generando reporte análisis cobertura en SonarQube${NC}"
    echo -e "${YELLOW}⏳ Por favor espere...${NC}"
    
    mvn clean verify sonar:sonar \
        -Dsonar.projectKey=${project_key} \
        -Dsonar.projectName="${service_name}" \
        -Dsonar.host.url=${SONAR_HOST} \
        -Dsonar.token=${SONAR_TOKEN} > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Análisis de ${service_name} completado${NC}"
        echo -e "${CYAN}   📍 Reportes locales: ${service_path}/target/site/jacoco/${NC}"
        echo -e "${CYAN}   🌐 URL SonarQube: ${SONAR_HOST}/dashboard?id=${project_key}${NC}"
        
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
    
    echo -e "${CYAN}📊 Se está generando reporte análisis cobertura en SonarQube${NC}"
    echo -e "${YELLOW}⏳ Por favor espere...${NC}"
    
    ./mvnw clean verify sonar:sonar \
        -Dsonar.projectKey=${project_key} \
        -Dsonar.projectName="${service_name}" \
        -Dsonar.host.url=${SONAR_HOST} \
        -Dsonar.token=${SONAR_TOKEN} > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Análisis de ${service_name} completado${NC}"
        echo -e "${CYAN}   📍 Reportes locales: ${service_path}/target/site/jacoco/${NC}"
        echo -e "${CYAN}   🌐 URL SonarQube: ${SONAR_HOST}/dashboard?id=${project_key}${NC}"
        
        cd - > /dev/null
        return 0
    else
        echo -e "${RED}❌ Error en análisis de ${service_name}${NC}"
        cd - > /dev/null
        return 1
    fi
}

# Función para analizar frontend (Angular)
analyze_frontend() {
    local service_name=$1
    local service_path=$2
    local project_key=$3
    
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}🎨 Analizando: ${service_name}${NC}"
    echo -e "${BLUE}   ProjectKey: ${project_key}${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    
    cd "$service_path" || exit
    
    echo -e "${CYAN}📊 Se está generando reporte análisis cobertura en SonarQube${NC}"
    echo -e "${YELLOW}⏳ Por favor espere...${NC}"
    
    npm run test -- --code-coverage --watch=false --browsers=ChromeHeadless > /dev/null 2>&1
    test_exit=$?
    
    if [ $test_exit -eq 0 ]; then
        echo -e "${GREEN}✅ Tests del frontend completados${NC}"
    else
        echo -e "${YELLOW}⚠️  Algunos tests fallaron (se enviará análisis de todos modos)${NC}"
    fi
    
    echo -e "${CYAN}   📍 Reportes locales: ${service_path}/coverage/${NC}"
    
    npx -y sonar-scanner \
        -Dsonar.projectKey=${project_key} \
        -Dsonar.projectName="${service_name}" \
        -Dsonar.host.url=${SONAR_HOST} \
        -Dsonar.token=${SONAR_TOKEN} > /dev/null 2>&1
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Análisis de ${service_name} completado${NC}"
        echo -e "${CYAN}   🌐 URL SonarQube: ${SONAR_HOST}/dashboard?id=${project_key}${NC}"
        cd - > /dev/null
        return 0
    else
        echo -e "${RED}❌ Error al enviar análisis a SonarQube${NC}"
        cd - > /dev/null
        return 1
    fi
}

# Función para obtener cobertura desde SonarQube API
get_coverage_from_sonarqube() {
    local project_key=$1
    local max_attempts=30
    local attempt=0
    
    # Esperar un poco antes de consultar (SonarQube necesita procesar)
    sleep 3
    
    echo -ne "${YELLOW}⏳ Consultando cobertura de ${project_key}...${NC}"
    
    while [ $attempt -lt $max_attempts ]; do
        local response=$(curl -s -u "${SONAR_TOKEN}:" \
            "${SONAR_HOST}/api/ce/component?component=${project_key}")
        
        local status=$(echo "$response" | grep -o '"status":"[^"]*"' | head -1 | cut -d'"' -f4)
        
        if [ "$status" == "SUCCESS" ]; then
            # Obtener métricas de cobertura
            local metrics=$(curl -s -u "${SONAR_TOKEN}:" \
                "${SONAR_HOST}/api/measures/component?component=${project_key}&metricKeys=coverage,line_coverage")
            
            local coverage=$(echo "$metrics" | grep -o '"coverage","value":"[^"]*"' | cut -d'"' -f6)
            local line_coverage=$(echo "$metrics" | grep -o '"line_coverage","value":"[^"]*"' | cut -d'"' -f6)
            
            if [ -z "$coverage" ]; then
                coverage="N/A"
            else
                coverage="${coverage}%"
            fi
            
            if [ -z "$line_coverage" ]; then
                line_coverage="N/A"
            else
                line_coverage="${line_coverage}%"
            fi
            
            echo -e " ${GREEN}✓${NC}"
            echo "${coverage}|${line_coverage}"
            return 0
        elif [ "$status" == "FAILED" ]; then
            echo -e " ${RED}✗${NC}"
            echo "ERROR|ERROR"
            return 1
        fi
        
        sleep 1
        ((attempt++))
    done
    
    echo -e " ${YELLOW}⏱${NC}"
    echo "TIMEOUT|TIMEOUT"
    return 1
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

cd "$ROOT_DIR"

# Array de microservicios backend con sus projectKeys
declare -a services=(
    "MS Gestion Labs:ms_gestion_labs:ms-gestion-labs"
    "MS Gestion Users:ms_gestion_users:ms-gestion-users"
    "MS Gestion Resultados:ms_gestion_resultados:ms-gestion-resultados"
)

# Contador de éxitos y fallos
success_count=0
fail_count=0

echo -e "${MAGENTA}╔═══════════════════════════════════════════════════╗${NC}"
echo -e "${MAGENTA}║           ANÁLISIS BACKEND (Maven)                ║${NC}"
echo -e "${MAGENTA}╚═══════════════════════════════════════════════════╝${NC}"
echo ""

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
echo -e "${MAGENTA}╔═══════════════════════════════════════════════════╗${NC}"
echo -e "${MAGENTA}║           ANÁLISIS FRONTEND (Angular)             ║${NC}"
echo -e "${MAGENTA}╚═══════════════════════════════════════════════════╝${NC}"
echo ""

# Analizar frontend
if analyze_frontend "Frontend Gestion Labs" "$ROOT_DIR/frontend_gestion_labs" "Frontend-Gestion-Labs"; then
    ((success_count++))
else
    ((fail_count++))
fi

echo ""

# Resumen de análisis
echo ""
echo -e "${BLUE}╔═══════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║              📋 RESUMEN DE ANÁLISIS               ║${NC}"
echo -e "${BLUE}╠═══════════════════════════════════════════════════╣${NC}"
echo -e "${BLUE}║${NC} ${GREEN}✅ Exitosos:${NC} ${success_count}                                   ${BLUE}║${NC}"
echo -e "${BLUE}║${NC} ${RED}❌ Fallidos:${NC} ${fail_count}                                   ${BLUE}║${NC}"
echo -e "${BLUE}╚═══════════════════════════════════════════════════╝${NC}"
echo ""
echo "   │   ├── ms_gestion_users/index.html"
echo "   │   ├── ms_gestion_resultados/index.html"
echo "   │   └── ms_api_gateway/index.html"
echo "   └── frontend/"
echo "       └── frontend_gestion_labs/index.html"
echo ""
echo -e "${YELLOW}🔗 Comandos para abrir reportes:${NC}"
echo -e "   ${CYAN}open resultados_test/backend/ms_gestion_users/index.html${NC}"
echo -e "   ${CYAN}open resultados_test/frontend/frontend_gestion_labs/index.html${NC}"
echo ""

# Dashboards de SonarQube
echo -e "${BLUE}╔═══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║           🔗 DASHBOARDS DE SONARQUBE                         ║${NC}"
echo -e "${BLUE}╚═══════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${CYAN}╔═══════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║         🔐 ACCESO A SONARQUBE                     ║${NC}"
echo -e "${CYAN}╠═══════════════════════════════════════════════════╣${NC}"
echo -e "${CYAN}║  URL:         http://localhost:9000              ║${NC}"
echo -e "${CYAN}║  Usuario:     admin                               ║${NC}"
echo -e "${CYAN}║  Contraseña:  Laboratorios#2025                  ║${NC}"
echo -e "${CYAN}╚═══════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${YELLOW}📊 Ver proyectos individuales:${NC}"
echo -e "   ${CYAN}Backend:${NC}"
echo "   ${SONAR_HOST}/dashboard?id=ms-gestion-labs"
echo "   ${SONAR_HOST}/dashboard?id=ms-gestion-users"
echo "   ${SONAR_HOST}/dashboard?id=ms-gestion-resultados"
echo "   ${SONAR_HOST}/dashboard?id=ms-api-gateway"
echo ""
echo -e "   ${CYAN}Frontend:${NC}"
echo "   ${SONAR_HOST}/dashboard?id=Frontend-Gestion-Labs"
echo ""
echo -e "${YELLOW}📋 Ver todos los proyectos:${NC}"
echo "   ${SONAR_HOST}/projects"
echo ""

if [ $fail_count -eq 0 ]; then
    echo -e "${GREEN}╔═══════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║   🎉 ¡Todos los análisis completados con éxito!  ║${NC}"
    echo -e "${GREEN}╚═══════════════════════════════════════════════════╝${NC}"
    exit 0
else
    echo -e "${YELLOW}╔═══════════════════════════════════════════════════╗${NC}"
    echo -e "${YELLOW}║   ⚠️  Algunos análisis fallaron                  ║${NC}"
    echo -e "${YELLOW}║      Revisa los logs arriba para más detalles    ║${NC}"
    echo -e "${YELLOW}╚═══════════════════════════════════════════════════╝${NC}"
    exit 1
fi
