#!/usr/bin/env bash

# ============================================================
# Análisis SonarQube para todos los microservicios
# - Sin variables de entorno: todo configurado aquí (modo educacional)
# - Ejecuta cada análisis en un contenedor Maven con Java 21
# - Usa la red de Docker donde corre SonarQube
# ============================================================

set -euo pipefail

# ==========================
# Configuración SonarQube
# ==========================
SONAR_HOST_URL="http://sonarqube:9000"          # Nombre del contenedor en docker-compose
SONAR_HOST_URL_LOCAL="http://localhost:9000"    # Para validación desde la máquina host
SONAR_TOKEN="sqp_1c4400f5ca6e7bf5239aa731bd77016af63ac91c"

# Directorio raíz (para montar en los contenedores)
ROOT_DIR="$(pwd)"

# Red de Docker donde está SonarQube
SONAR_NETWORK="labs_network"

# Microservicios a analizar (formato: ruta:projectKey:projectName)
MODULES=(
  "ms_api_gateway:laboratorios-api-gateway:Laboratorios - API Gateway"
  "ms_gestion_labs:laboratorios-gestion-labs:Laboratorios - Gestión Labs"
  "ms_gestion_resultados:laboratorios-gestion-resultados:Laboratorios - Gestión Resultados"
  "ms_gestion_users:laboratorios-gestion-usuarios:Laboratorios - Gestión Usuarios"
)

# ==========================
# Helpers
# ==========================
echo_header() {
  echo "============================================================"
  echo "$1"
  echo "============================================================"
}

wait_for_sonar() {
  echo_header "Verificando disponibilidad de SonarQube..."
  local status=""

  for i in {1..60}; do
    status="$(curl -s "${SONAR_HOST_URL_LOCAL}/api/system/status" | sed -n 's/.*"status":"\([^"]*\)".*/\1/p' || true)"
    if [[ "$status" == "UP" ]]; then
      echo "✅ SonarQube OK (status=UP)"
      return 0
    fi
    echo "Esperando SonarQube... (status=${status:-N/A}) intento ${i}/60"
    sleep 2
  done

  echo "❌ ERROR: SonarQube no respondió en ${SONAR_HOST_URL_LOCAL}"
  exit 1
}

analyze_module() {
  local module_spec="$1"
  local module project_key project_name

  IFS=":" read -r module project_key project_name <<< "${module_spec}"

  echo_header "Analizando: ${project_name} (${module})"

  if [[ ! -d "${module}" ]]; then
    echo "⚠️  Directorio ${module} no existe. Saltando..."
    return 1
  fi

  docker run --rm \
    --name "sonar-analysis-${module}" \
    --network="${SONAR_NETWORK}" \
    -v "${ROOT_DIR}/${module}:/workspace" \
    -v "${HOME}/.m2:/root/.m2" \
    -w /workspace \
    maven:3.9.6-eclipse-temurin-21 \
    mvn clean verify sonar:sonar \
      -Dsonar.projectKey="${project_key}" \
      -Dsonar.projectName="${project_name}" \
      -Dsonar.host.url="${SONAR_HOST_URL}" \
      -Dsonar.token="${SONAR_TOKEN}"

  if [ $? -eq 0 ]; then
    echo "✅ ${project_name} analizado correctamente"
    return 0
  else
    echo "❌ Error al analizar ${project_name}"
    return 1
  fi
}

# ==========================
# Main
# ==========================
wait_for_sonar

echo_header "Iniciando análisis SonarQube"
echo "Host Sonar: ${SONAR_HOST_URL}"
echo "Red Docker: ${SONAR_NETWORK}"
echo ""

SUCCESS_COUNT=0
FAIL_COUNT=0

for module_spec in "${MODULES[@]}"; do
  if analyze_module "${module_spec}"; then
    SUCCESS_COUNT=$((SUCCESS_COUNT + 1))
  else
    FAIL_COUNT=$((FAIL_COUNT + 1))
  fi
  echo ""
done

echo_header "Resumen Final"
echo "✅ Analizados exitosamente: ${SUCCESS_COUNT}"
echo "❌ Con errores: ${FAIL_COUNT}"
echo ""
echo "📊 Ver resultados: ${SONAR_HOST_URL_LOCAL}/projects"
echo ""
