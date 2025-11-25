# 🏥 Sistema de Gestión de Laboratorios Clínicos

Sistema completo de gestión de laboratorios clínicos con arquitectura de microservicios, desarrollado con Spring Boot y Angular.

## 📋 Descripción del Proyecto

Este proyecto implementa un sistema de gestión integral para laboratorios clínicos, permitiendo administrar:
- Laboratorios y sus ubicaciones
- Catálogo de exámenes médicos
- Precios de exámenes por laboratorio
- Resultados de exámenes de pacientes

## 🏗️ Arquitectura y Patrones de Diseño

### Backend - Spring Boot (Arquetipos)
El backend implementa **Layered Architecture** con los siguientes arquetipos:

#### 1. **Controller Layer** (Presentación)
- `LaboratorioController`: Gestión de laboratorios
- `ExamenController`: Catálogo de exámenes
- `LabExamController`: Relación laboratorio-examen y precios
- `ResultadoController`: Gestión de resultados

#### 2. **Service Layer** (Lógica de Negocio)
- `LaboratorioService`: Reglas de negocio para laboratorios
- `ExamenService`: Validaciones de exámenes
- `ResultadoService`: Procesamiento de resultados

#### 3. **Repository Layer** (Acceso a Datos)
- Spring Data JPA repositories
- Conexión a Oracle Database
- Transacciones automáticas

### Frontend - Angular (Patrón MVC)

#### 1. **Model** (Modelos de Datos)
```
src/app/models/
├── laboratorio.model.ts    # Entidades de laboratorios
├── examen.model.ts          # Entidades de exámenes
├── lab-exam.model.ts        # Relación lab-examen
└── resultado.model.ts       # Resultados de exámenes
```

#### 2. **View** (Vistas HTML)
- Templates HTML con binding bidireccional
- Componentes reutilizables
- Estilos CSS modulares

#### 3. **Controller** (Componentes TypeScript)
```
src/app/components/
├── laboratorio-list/        # Listado y filtros
├── laboratorio-form/        # Formulario CRUD
├── examen-list/             # Gestión de exámenes
├── examen-form/             # Formulario de exámenes
├── lab-exam-list/           # Precios
└── resultado-list/          # Resultados
```

#### 4. **Services** (Comunicación con API)
```
src/app/services/
├── laboratorio.service.ts   # HTTP Client para laboratorios
├── examen.service.ts        # HTTP Client para exámenes
├── lab-exam.service.ts      # HTTP Client para precios
└── resultado.service.ts     # HTTP Client para resultados
```

## 🐳 Arquitectura Docker

### Dockerfile.backend (Multistage Build)
```
Stage 1: Build con Maven
- Compilación de código Java
- Gestión de dependencias
- Generación del JAR

Stage 2: Runtime con JRE
- Imagen ligera Alpine
- Usuario no-root (seguridad)
- Healthcheck configurado
```

### Dockerfile.frontend (Multistage Build)
```
Stage 1: Build con Node.js
- Compilación Angular AOT
- Optimización para producción
- Tree-shaking de módulos

Stage 2: Runtime con Nginx
- Servidor web ligero
- Configuración SPA
- Proxy reverso a backend
```

## 🚀 Instrucciones de Uso

### Pre-requisitos
- Docker 20.10+
- Docker Compose 2.0+
- Wallet de Oracle Database (configurado)

### 1. Clonar el repositorio
```bash
cd microservicios_laboratorio_sboot
```

### 2. Verificar la estructura del proyecto
```
microservicios_laboratorio_sboot/
├── ms_gestion_labs/              # Backend Spring Boot
│   ├── src/
│   ├── pom.xml
│   ├── Dockerfile                # ← Dockerfile del backend
│   ├── .dockerignore
│   └── README_DOCKER.md
├── frontend_gestion_labs/        # Frontend Angular
│   ├── src/
│   ├── package.json
│   ├── Dockerfile                # ← Dockerfile del frontend
│   ├── .dockerignore
│   └── README_DOCKER.md
├── wallet/                       # Oracle Wallet
├── docker-compose.yml            # Orquestación (solo para desarrollo)
└── README_DOCKER.md              # Este archivo
```

**Nota importante:** Cada proyecto tiene su propio Dockerfile en su directorio. Esto permite que cada uno vaya a su repositorio independiente.

### 3. Construir las imágenes Docker

#### Opción A: Con Docker Compose (desarrollo local)
```bash
docker-compose build
```

#### Opción B: Construir cada contenedor de forma independiente

**Backend:**
```bash
cd ms_gestion_labs
docker build -t ms-gestion-labs:1.0.0 .
```

**Frontend:**
```bash
cd frontend_gestion_labs
docker build -t frontend-gestion-labs:1.0.0 .
```

### 4. Iniciar los contenedores

#### Opción A: Con Docker Compose (desarrollo local)
```bash
# Iniciar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f backend
docker-compose logs -f frontend
```

#### Opción B: Ejecutar cada contenedor de forma independiente

**Backend (requiere wallet de Oracle):**
```bash
cd ms_gestion_labs
docker run -d \
  --name gestion-labs-backend \
  -p 8081:8081 \
  -e SPRING_DATASOURCE_URL="jdbc:oracle:thin:@databasefullstack3_tp?TNS_ADMIN=/app/wallet" \
  -e SPRING_DATASOURCE_USERNAME=ADMIN \
  -e SPRING_DATASOURCE_PASSWORD=DataBaseFullStack3# \
  -e APP_JWT_SECRET=ubOJAPgPhBFu8zs3ztDtQBOZ2cdZ6ArHplrwneqabTkotIdzq2Nd60QT8X6M+viBh1TIi8Oz3ffq62wrZZygRw== \
  -v "$(pwd)/../wallet/Wallet_databaseFullStack3:/app/wallet:ro" \
  --network labs_network \
  ms-gestion-labs:1.0.0
```

**Frontend:**
```bash
cd frontend_gestion_labs
docker run -d \
  --name gestion-labs-frontend \
  -p 4200:80 \
  -e API_URL=http://backend:8081 \
  --network labs_network \
  frontend-gestion-labs:1.0.0
```

**Crear la red (si no existe):**
```bash
docker network create labs_network
```

### 5. Verificar el estado de los servicios
```bash
# Estado de los contenedores
docker-compose ps

# Healthcheck
docker inspect gestion_labs_backend --format='{{.State.Health.Status}}'
docker inspect gestion_labs_frontend --format='{{.State.Health.Status}}'
```

### 6. Acceder a la aplicación

#### Frontend Angular
```
URL: http://localhost:4200
```

#### Backend API (Spring Boot)
```
URL: http://localhost:8081
Endpoints disponibles:
- GET /labs                    # Listar laboratorios
- GET /labs/{id}              # Obtener laboratorio
- POST /labs                  # Crear laboratorio
- GET /exams                  # Listar exámenes
- GET /lab-exam               # Listar precios
- GET /resultados             # Listar resultados
```

## 📊 Funcionalidades Implementadas

### ✅ Gestión de Laboratorios
- Crear, leer, actualizar y eliminar laboratorios
- Filtrar por ciudad y tipo
- Información completa de dirección y contacto

### ✅ Catálogo de Exámenes
- Administración de exámenes médicos
- Clasificación por tipo (Sangre, Orina, etc.)
- Códigos únicos de identificación

### ✅ Gestión de Precios
- Relación laboratorio-examen con precios
- Vigencia temporal de precios
- Consulta de precios actuales

### ✅ Resultados de Exámenes
- Visualización de resultados emitidos
- Estados: PENDIENTE, EMITIDO, ANULADO
- Información detallada con valores y unidades

## 🔧 Comandos Útiles

### Docker Compose
```bash
# Detener servicios
docker-compose stop

# Detener y eliminar contenedores
docker-compose down

# Eliminar también volúmenes
docker-compose down -v

# Reconstruir sin caché
docker-compose build --no-cache

# Ver logs en tiempo real
docker-compose logs -f

# Escalar servicios (si aplica)
docker-compose up -d --scale backend=2
```

### Acceso a contenedores
```bash
# Acceder al backend
docker exec -it gestion_labs_backend sh

# Acceder al frontend
docker exec -it gestion_labs_frontend sh

# Ver logs del backend
docker logs gestion_labs_backend

# Ver logs del frontend
docker logs gestion_labs_frontend
```

## 🔍 Troubleshooting

### El backend no inicia
```bash
# Verificar logs
docker-compose logs backend

# Problemas comunes:
# 1. Wallet de Oracle no configurado correctamente
# 2. Variables de entorno incorrectas
# 3. Puerto 8081 ocupado
```

### El frontend no puede conectarse al backend
```bash
# Verificar que backend esté saludable
docker-compose ps

# Verificar configuración de red
docker network inspect labs_network

# Verificar variables de entorno
docker exec gestion_labs_frontend env | grep API_URL
```

### Errores de compilación
```bash
# Limpiar y reconstruir
docker-compose down
docker system prune -a
docker-compose build --no-cache
docker-compose up -d
```

## 📈 Mejoras Futuras

- [ ] Implementar autenticación JWT en el frontend
- [ ] Agregar paginación en las tablas
- [ ] Implementar búsqueda avanzada
- [ ] Agregar gráficos de estadísticas
- [ ] Implementar notificaciones en tiempo real
- [ ] Agregar exportación de resultados a PDF

## 👥 Equipo de Desarrollo

- Desarrollo Backend: Spring Boot + JPA
- Desarrollo Frontend: Angular 18
- DevOps: Docker + Docker Compose

## 📄 Licencia

Este proyecto es parte de la evaluación sumativa del curso Desarrollo Full Stack III - DUOC UC

---

**Fecha de creación**: Noviembre 2025  
**Versión**: 1.0.0  
**Universidad**: DUOC UC  
**Curso**: Desarrollo Full Stack III
