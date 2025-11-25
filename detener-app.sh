#!/bin/bash
# Script para detener y eliminar todos los contenedores de la app (docker-compose)

echo "Deteniendo y eliminando todos los contenedores de la app..."
docker-compose down
echo "Todos los contenedores han sido detenidos y eliminados."
