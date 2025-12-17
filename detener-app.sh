#!/bin/bash
# Script para detener y eliminar todos los contenedores de la app (docker-compose)

echo "Deteniendo y eliminando todos los contenedores de la app..."
docker-compose down --rmi local --volumes
echo "Todos los contenedores, imágenes locales y volúmenes han sido eliminados."
