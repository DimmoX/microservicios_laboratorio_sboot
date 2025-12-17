#!/bin/bash
# Script para limpiar TODO y reconstruir desde cero

echo "========================================="
echo "LIMPIEZA COMPLETA Y REBUILD"
echo "========================================="

# 1. Detener y eliminar TODOS los contenedores, imágenes y volúmenes
echo ""
echo "[1/6] Deteniendo contenedores..."
docker-compose down --rmi all --volumes --remove-orphans

# 2. Limpiar builds del frontend
echo ""
echo "[2/6] Limpiando builds del frontend..."
cd frontend_gestion_labs
rm -rf dist/ .angular/ node_modules/.cache/
cd ..

# 3. Limpiar builds de los microservicios Java
echo ""
echo "[3/6] Limpiando builds de Java..."
rm -rf ms_api_gateway/target/
rm -rf ms_gestion_labs/target/
rm -rf ms_gestion_resultados/target/
rm -rf ms_gestion_users/target/

# 4. Limpiar caché de Docker
echo ""
echo "[4/6] Limpiando caché de Docker..."
docker system prune -f

# 5. Reconstruir el API Gateway (con los cambios del JWT)
echo ""
echo "[5/6] Reconstruyendo API Gateway..."
cd ms_api_gateway
./mvnw clean package -DskipTests
cd ..

# 6. Reconstruir contenedores SIN caché de Docker
echo ""
echo "[6/6] Reconstruyendo contenedores SIN caché..."
docker-compose build --no-cache

# 7. Levantar contenedores
echo ""
echo "[7/7] Levantando contenedores..."
docker-compose up -d

echo ""
echo "========================================="
echo "✓ Limpieza y rebuild completados"
echo "========================================="
echo ""
echo "IMPORTANTE: Limpia el caché del navegador:"
echo "  - Presiona Ctrl+Shift+R (Cmd+Shift+R en Mac)"
echo "  - O ve a Herramientas de Desarrollador > Application > Clear storage"
echo ""
echo "Verifica el estado con: docker ps"
echo "Verifica logs con: docker logs gestion_labs_api_gateway"
