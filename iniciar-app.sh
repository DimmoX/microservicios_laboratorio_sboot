#!/bin/bash
set -e

# ============================================================================
# Script de Inicialización Completa del Sistema
# ============================================================================
# Este script hace TODO automáticamente:
# 1. Levanta infraestructura (Frontend + Backend + SonarQube + BD)
# 2. Crea proyectos en SonarQube
# ============================================================================

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m'

# Configuración SonarQube (hardcodeada - proyecto educativo)
SONAR_HOST="http://localhost:9000"
SONAR_USER="admin"
SONAR_PASS="Laboratorios#2025"
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RESULTS_DIR="$ROOT_DIR/resultados_test"

echo -e "${BLUE}╔═══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   🚀 INICIALIZACIÓN COMPLETA DEL SISTEMA                     ║${NC}"
echo -e "${BLUE}║      Frontend + Backend + SonarQube                          ║${NC}"
echo -e "${BLUE}╚═══════════════════════════════════════════════════════════════╝${NC}"
echo ""

# ============================================================================
# PASO 1: Levantar PostgreSQL primero
# ============================================================================
echo -e "${MAGENTA}╔═══════════════════════════════════════════════════╗${NC}"
echo -e "${MAGENTA}║   PASO 1: Levantando PostgreSQL                  ║${NC}"
echo -e "${MAGENTA}╚═══════════════════════════════════════════════════╝${NC}"
echo ""

echo -e "${YELLOW}🐘 Levantando base de datos PostgreSQL...${NC}"
docker-compose up -d sonarqube-db

echo -e "${YELLOW}⏳ Esperando PostgreSQL...${NC}"
sleep 10

echo -e "${GREEN}✅ PostgreSQL iniciado${NC}"
echo ""

# ============================================================================
# PASO 2: Levantar resto de servicios
# ============================================================================
echo -e "${MAGENTA}╔═══════════════════════════════════════════════════╗${NC}"
echo -e "${MAGENTA}║   PASO 2: Levantando Todos los Servicios         ║${NC}"
echo -e "${MAGENTA}╚═══════════════════════════════════════════════════╝${NC}"
echo ""

echo -e "${YELLOW}🐳 Construyendo y levantando contenedores...${NC}"
docker-compose up --build -d

echo -e "${GREEN}✅ Contenedores iniciados${NC}"
echo ""
echo -e "${CYAN}📋 Servicios en ejecución:${NC}"
echo "   Frontend (Angular):              http://localhost:4200"
echo "   API Gateway:                     http://localhost:8080"
echo "   MS Gestion Labs:                 http://localhost:8081"
echo "   MS Gestion Resultados:           http://localhost:8082"
echo "   MS Gestion Users:                http://localhost:8083"
echo "   SonarQube:                       http://localhost:9000"
echo ""

# ============================================================================
# PASO 3: Esperar a que SonarQube esté listo
# ============================================================================
echo -e "${MAGENTA}╔═══════════════════════════════════════════════════╗${NC}"
echo -e "${MAGENTA}║   PASO 3: Esperando SonarQube                    ║${NC}"
echo -e "${MAGENTA}╚═══════════════════════════════════════════════════╝${NC}"
echo ""

echo -e "${YELLOW}⏳ Esperando a que SonarQube esté disponible...${NC}"
max_attempts=60
attempt=0

while [ $attempt -lt $max_attempts ]; do
    if curl -s -f "${SONAR_HOST}/api/system/status" > /dev/null 2>&1; then
        status=$(curl -s "${SONAR_HOST}/api/system/status" | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
        if [ "$status" == "UP" ]; then
            echo -e "${GREEN}✅ SonarQube está disponible${NC}"
            break
        fi
    fi
    
    sleep 2
    ((attempt++))
    
    if [ $((attempt % 10)) -eq 0 ]; then
        echo -e "${YELLOW}   Esperando... ($attempt/$max_attempts)${NC}"
    fi
done

if [ $attempt -eq $max_attempts ]; then
    echo -e "${RED}❌ Timeout: SonarQube no respondió${NC}"
    exit 1
fi

echo ""

# ============================================================================
# PASO 4: Cambiar contraseña de SonarQube
# ============================================================================
echo -e "${MAGENTA}╔═══════════════════════════════════════════════════╗${NC}"
echo -e "${MAGENTA}║   PASO 4: Configurando SonarQube                 ║${NC}"
echo -e "${MAGENTA}╚═══════════════════════════════════════════════════╝${NC}"
echo ""

echo -e "${YELLOW}🔐 Cambiando contraseña de admin...${NC}"

# Cambiar contraseña por defecto
CHANGE_PASS_RESPONSE=$(curl -s -X POST "${SONAR_HOST}/api/users/change_password" \
    -u "admin:admin" \
    -d "login=admin" \
    -d "previousPassword=admin" \
    -d "password=${SONAR_PASS}" 2>&1)

# Si el cambio falla, probablemente ya se cambió antes
if echo "$CHANGE_PASS_RESPONSE" | grep -q "error"; then
    echo -e "${YELLOW}⚠️  Contraseña ya fue cambiada previamente${NC}"
else
    echo -e "${GREEN}✅ Contraseña actualizada${NC}"
fi

echo ""

# ============================================================================
# PASO 5: Generar Token de SonarQube
# ============================================================================
echo -e "${MAGENTA}╔═══════════════════════════════════════════════════╗${NC}"
echo -e "${MAGENTA}║   PASO 5: Generando Token de SonarQube           ║${NC}"
echo -e "${MAGENTA}╚═══════════════════════════════════════════════════╝${NC}"
echo ""

echo -e "${YELLOW}🔑 Generando nuevo token global...${NC}"

# Generar token con timestamp único
TIMESTAMP=$(date +%s)
TOKEN_NAME="global-token-${TIMESTAMP}"

TOKEN_RESPONSE=$(curl -s -X POST "${SONAR_HOST}/api/user_tokens/generate" \
    -u "${SONAR_USER}:${SONAR_PASS}" \
    -d "name=${TOKEN_NAME}" \
    -d "type=GLOBAL_ANALYSIS_TOKEN")

# Extraer el token de la respuesta JSON
SONAR_TOKEN=$(echo "$TOKEN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -z "$SONAR_TOKEN" ]; then
    echo -e "${RED}❌ Error al generar token${NC}"
    echo -e "${YELLOW}Respuesta: ${TOKEN_RESPONSE}${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Token generado exitosamente${NC}"
echo -e "${CYAN}   Token: ${SONAR_TOKEN:0:20}...${NC}"

# Guardar token en archivo para uso del script de análisis
echo "$SONAR_TOKEN" > "${ROOT_DIR}/.sonar_token"
echo -e "${CYAN}   Guardado en: .sonar_token${NC}"
echo ""

# ============================================================================
# PASO 6: Crear proyectos en SonarQube
# ============================================================================
echo -e "${MAGENTA}╔═══════════════════════════════════════════════════╗${NC}"
echo -e "${MAGENTA}║   PASO 6: Creando Proyectos en SonarQube         ║${NC}"
echo -e "${MAGENTA}╚═══════════════════════════════════════════════════╝${NC}"
echo ""

echo -e "${YELLOW}📦 Creando proyectos en SonarQube...${NC}"
echo ""

# Función para crear proyecto
create_project() {
    local project_key=$1
    local project_name=$2
    
    echo -e "${CYAN}   Creando: ${project_name}...${NC}"
    
    response=$(curl -s -X POST "${SONAR_HOST}/api/projects/create" \
        -H "Authorization: Bearer ${SONAR_TOKEN}" \
        -d "name=${project_name}" \
        -d "project=${project_key}")
    
    if echo "$response" | grep -q '"key"'; then
        echo -e "${GREEN}   ✅ ${project_name} creado${NC}"
    else
        echo -e "${YELLOW}   ⚠️  ${project_name} (posiblemente ya existe)${NC}"
    fi
}

# Crear proyectos backend
create_project "ms-gestion-labs" "MS Gestion Labs"
create_project "ms-gestion-users" "MS Gestion Users"
create_project "ms-gestion-resultados" "MS Gestion Resultados"
create_project "ms-api-gateway" "MS API Gateway"

echo ""
echo -e "${GREEN}✅ Proyectos configurados en SonarQube${NC}"
echo ""

# ============================================================================
# FINALIZACIÓN
# ============================================================================
echo -e "${GREEN}╔═══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║   🎉 ¡SISTEMA COMPLETAMENTE INICIALIZADO!                    ║${NC}"
echo -e "${GREEN}╚═══════════════════════════════════════════════════════════════╝${NC}"
echo ""

echo -e "${CYAN}╔═══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║           📋 SERVICIOS DISPONIBLES                           ║${NC}"
echo -e "${CYAN}╚═══════════════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${YELLOW}🌐 Aplicación Web:${NC}"
echo -e "   ${BLUE}http://localhost:4200${NC}"
echo ""
echo -e "${YELLOW}🔧 Microservicios:${NC}"
echo "   API Gateway:          http://localhost:8080"
echo "   MS Gestion Labs:      http://localhost:8081"
echo "   MS Gestion Resultados: http://localhost:8082"
echo "   MS Gestion Users:     http://localhost:8083"
echo ""
echo -e "${YELLOW}📊 SonarQube:${NC}"
echo "   URL:                  http://localhost:9000"
echo "   Usuario:              admin"
echo "   Contraseña:           ${SONAR_PASS}"
echo "   Token:                ${SONAR_TOKEN:0:20}..."
echo ""
echo -e "${MAGENTA}💡 Siguiente paso:${NC}"
echo -e "   Ejecuta ${CYAN}./analisis-sonarqube.sh${NC} para analizar el código"
echo "   (El token se pasará automáticamente desde .sonar_token)"
echo ""
