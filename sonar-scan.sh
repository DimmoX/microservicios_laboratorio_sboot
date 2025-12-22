#!/bin/bash

echo "=========================================="
echo "🔍 EJECUTANDO ANÁLISIS SONARQUBE"
echo "=========================================="
echo ""

# # Verificar que los contenedores estén corriendo
# if ! docker ps | grep -q "recetas_app"; then
#     echo "❌ Error: El contenedor recetas_app no está corriendo"
#     echo "   Ejecuta: ./docker-start.sh"
#     exit 1
# fi

# if ! docker ps | grep -q "sonarqube"; then
#     echo "❌ Error: El contenedor sonarqube no está corriendo"
#     echo "   Ejecuta: ./sonarqube-start.sh"
#     exit 1
# fi

# echo "✅ Contenedores activos"
# echo ""

# # Conectar recetas_app a la red de sonarqube si no está conectado
# echo "🔗 Verificando conectividad entre contenedores..."
# docker network connect sumativa1-semana3_sonarqube-network recetas_app 2>/dev/null || echo "   Ya está conectado a la red"

# echo ""
# echo "🚀 Ejecutando sonar-scanner en recetas_app..."
# echo ""

# # Crear directorio para cache de Maven si no existe
# echo "📦 Configurando cache de Maven..."
# docker exec recetas_app sh -c "mkdir -p /.m2/repository" 2>/dev/null || true

# Ejecutar análisis de SonarQube dentro del contenedor
# Usa /.m2 que se monta como volumen persistente (ver .devcontainer o docker-compose)
docker exec frontend sh -c "mvn clean verify sonar:sonar \
  -Dsonar.projectKey=Laboratorios \
  -Dsonar.projectName='Laboratorios' \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqp_f17cbead5e401ec4f3905d7f82c4e57d203586e5"

if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "✅ ANÁLISIS COMPLETADO"
    echo "=========================================="
    echo ""
    echo "📊 Ver resultados en: http://localhost:9000/dashboard?id=app_recetas_v2"
    echo ""
else
    echo ""
    echo "❌ Error en el análisis"
    echo ""
    echo "💡 Verifica:"
    echo "   1. Token correcto en SonarQube"
    echo "   2. Proyecto creado en SonarQube"
    echo "   3. Contenedores en la misma red"
    echo ""
fi
