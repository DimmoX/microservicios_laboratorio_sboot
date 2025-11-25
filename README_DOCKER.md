# Documentación Docker - Microservicios Laboratorio SBoot

## Descripción General
Este proyecto utiliza Docker para desplegar una arquitectura de microservicios compuesta por:
- Frontend Angular + Nginx
- API Gateway (Spring Boot)
- Gestión Labs (Spring Boot)
- Gestión Users (Spring Boot)

Cada servicio cuenta con su propio Dockerfile y todos se orquestan mediante `docker-compose`.

---

## Permisos de Ejecución para Scripts Bash
Antes de ejecutar los scripts para iniciar o detener los contenedores, asegúrate de darles permisos de ejecución:

```bash
chmod +x iniciar-app.sh detener-app.sh
```

---

## Uso de los Scripts

### Iniciar todos los contenedores
```bash
./iniciar-app.sh
```
Esto construye y levanta todos los servicios definidos en `docker-compose.yml`.

### Detener y eliminar todos los contenedores
```bash
./detener-app.sh
```
Esto detiene y elimina todos los contenedores definidos en `docker-compose.yml`.

---

## Descripción de los Archivos Docker

### Dockerfiles
- **frontend_gestion_labs/Dockerfile**: Construye la aplicación Angular y la sirve con Nginx.
- **ms_api_gateway/Dockerfile**: Construye y ejecuta el microservicio API Gateway con Spring Boot y Java 21.
- **ms_gestion_labs/Dockerfile**: Construye y ejecuta el microservicio Gestión Labs con Spring Boot y Java 21.
- **ms_gestion_users/Dockerfile**: Construye y ejecuta el microservicio Gestión Users con Spring Boot y Java 21.

### docker-compose.yml
- Ubicación: raíz del proyecto
- Orquesta todos los servicios, define los puertos y los nombres de los contenedores.

---

## Ubicación de los Archivos
- **frontend_gestion_labs/Dockerfile**
- **ms_api_gateway/Dockerfile**
- **ms_gestion_labs/Dockerfile**
- **ms_gestion_users/Dockerfile**
- **docker-compose.yml** (raíz)
- **iniciar-app.sh** (raíz)
- **detener-app.sh** (raíz)

---

## Puertos de los Servicios
- Frontend (Angular + Nginx):     http://localhost:4200
- API Gateway (Spring Cloud):     http://localhost:8080
- Gestión Labs (Spring Boot):     http://localhost:8081
- Gestión Users (Spring Boot):    http://localhost:8082

---

## Recomendaciones
- Verificar que Docker y Docker Compose estén instalados.
- Ejecutar los scripts desde la raíz del proyecto.
- Consultar los logs de los contenedores con `docker-compose logs <servicio>` si se necesitara depurar.