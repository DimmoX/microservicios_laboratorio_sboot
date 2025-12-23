# 🏥 Sistema de Gestión de Laboratorios Clínicos

Sistema de gestión integral para laboratorios clínicos desarrollado con arquitectura de microservicios, implementando autenticación JWT centralizada y conexión a base de datos Oracle Autonomous Database en Oracle Cloud Infrastructure (OCI).

---

## 📋 Tabla de Contenidos

- [Descripción del Proyecto](#-descripción-del-proyecto)
- [Inicio Rápido](#-inicio-rápido)
- [Arquitectura de Microservicios](#️-arquitectura-de-microservicios)
  - [MS_API_GATEWAY](#-ms_api_gateway-puerto-8080)
  - [MS_GESTION_USERS](#-ms_gestion_users-puerto-8083)
  - [MS_GESTION_LABS](#-ms_gestion_labs-puerto-8081)
  - [MS_GESTION_RESULTADOS](#-ms_gestion_resultados-puerto-8082)
- [Scripts de Automatización](#-scripts-de-automatización)
- [Arquetipos de Microservicios](#-arquetipos-de-microservicios)
- [Tecnologías y Dependencias](#-tecnologías-y-dependencias)
- [Seguridad y Autenticación](#-seguridad-y-autenticación)
  - [Spring Boot Security - Implementación](#️-spring-boot-security---implementación-en-el-proyecto)
- [Base de Datos](#️-base-de-datos)
- [Configuración de Conexión](#️-configuración-de-conexión)
- [Endpoints de la API](#-endpoints-de-la-api)
- [Ejemplos de Uso](#-ejemplos-de-uso)
- [Ejecución del Proyecto](#-ejecución-del-proyecto)
- [Análisis de Cobertura con SonarQube](#-análisis-de-cobertura-con-sonarqube)
- [Tests Unitarios](#-tests-unitarios)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Troubleshooting](#-troubleshooting)
- [Changelog](#-changelog)

---

## 🎯 Descripción del Proyecto

El **Sistema de Gestión de Laboratorios Clínicos** permite administrar:

- ✅ **Usuarios**: Gestión de credenciales y roles (ADMIN, EMPLEADO, PACIENTE)
- ✅ **Pacientes**: Registro completo con datos personales, dirección y contacto
- ✅ **Empleados**: Gestión de personal del laboratorio con cargos y datos de contacto
- ✅ **Laboratorios**: Administración de múltiples laboratorios con ubicaciones
- ✅ **Exámenes**: Catálogo de exámenes médicos disponibles
- ✅ **Agendas**: Programación de citas para exámenes médicos
- ✅ **Resultados**: Registro y consulta de resultados con filtrado por rol (PATIENT, LAB_EMPLOYEE, ADMIN)

### Características Principales

- 🔐 **Autenticación JWT centralizada** en API Gateway
- 🛡️ **Spring Boot Security implementado** en todos los microservicios
- 🔑 **BCrypt para hash de contraseñas** (costo 10)
- 🚦 **Control de acceso basado en roles** (RBAC con @PreAuthorize)
- 🎯 **Filtrado contextual por rol** en resultados (PATIENT ve solo sus datos)
- 🌐 **Arquitectura de microservicios** escalable y desacoplada
- 📦 **Arquetipos reutilizables** para desarrollo ágil
- ☁️ **Base de datos en la nube** (Oracle Autonomous Database)
- 🔄 **Operaciones en cascada** automáticas
- 📝 **Validación de datos** completa
- 🚀 **CORS habilitado** para aplicaciones frontend
- 🐳 **Docker Compose** para orquestación de contenedores

---

## ⚡ Inicio Rápido

Para ejecutar el sistema completo con **Docker Compose** y **SonarQube**, sigue estos 3 pasos:

### 1️⃣ Iniciar Infraestructura

```bash
./iniciar-app.sh
```

**¿Qué hace?**
- Levanta PostgreSQL para SonarQube
- Construye y levanta todos los contenedores (Frontend + 4 microservicios Backend)
- Configura SonarQube (cambia contraseña, genera token, crea 4 proyectos)
- Todos los servicios quedan disponibles en:
  - Frontend: http://localhost:4200
  - API Gateway: http://localhost:8080
  - SonarQube: http://localhost:9000 (admin / Laboratorios#2025)

### 2️⃣ Ejecutar Análisis de Cobertura

```bash
./analisis-sonarqube.sh
```

**¿Qué hace?**
- Ejecuta tests con cobertura en los 4 microservicios backend (JUnit + JaCoCo)
- Ejecuta tests con cobertura en el frontend (Karma + Jasmine)
- Envía los análisis a SonarQube
- Los reportes quedan disponibles en http://localhost:9000

### 3️⃣ Detener Infraestructura

```bash
./detener-app.sh
```

**¿Qué hace?**
- Detiene todos los contenedores
- Elimina contenedores, imágenes y volúmenes
- Limpia la red Docker

> **💡 Nota:** Para más opciones de ejecución (sin Docker, desarrollo local, etc.), consulta la sección [Ejecución del Proyecto](#-ejecución-del-proyecto).

---

## 🚀 Scripts de Automatización

El proyecto incluye **4 scripts bash** para gestionar el ciclo de vida completo del sistema:

### 📜 Descripción de Scripts

| Script | Descripción | Uso Recomendado |
|--------|-------------|-----------------|
| **iniciar-app.sh** | Inicialización completa de infraestructura | Primera ejecución o después de detener |
| **analisis-sonarqube.sh** | Tests y análisis de cobertura | Después de cambios en código |
| **detener-app.sh** | Detención limpia de servicios | Finalizar sesión de trabajo |
| **limpiar-rebuild.sh** | Limpieza profunda y reconstrucción | Solución de problemas o errores |

---

### 1️⃣ iniciar-app.sh

**Propósito:** Levanta toda la infraestructura con configuración automática de SonarQube.

**Pasos ejecutados:**
```
PASO 1: Levantar PostgreSQL para SonarQube
PASO 2: Levantar todos los servicios (Frontend + Backend + SonarQube)
PASO 3: Esperar a que SonarQube esté disponible (polling 60 intentos)
PASO 4: Cambiar contraseña de admin (admin → Laboratorios#2025)
PASO 5: Generar token dinámico con timestamp único
        → Guarda token en archivo: .sonar_token
PASO 6: Crear 4 proyectos backend en SonarQube
```

**Ejecución:**
```bash
chmod +x iniciar-app.sh
./iniciar-app.sh
```

**Servicios levantados:**
- PostgreSQL (base de datos para SonarQube)
- SonarQube Community (http://localhost:9000)
- Frontend Angular (http://localhost:4200)
- MS_API_GATEWAY (http://localhost:8080)
- MS_GESTION_USERS (http://localhost:8083)
- MS_GESTION_LABS (http://localhost:8081)
- MS_GESTION_RESULTADOS (http://localhost:8082)

**Salida esperada:**
```
✓ Token generado y guardado en .sonar_token
✓ Proyectos creados en SonarQube:
  - ms-gestion-labs
  - ms-gestion-users
  - ms-gestion-resultados
  - ms-api-gateway

Acceso a servicios:
  Frontend:     http://localhost:4200
  API Gateway:  http://localhost:8080
  SonarQube:    http://localhost:9000
  Credenciales: admin / Laboratorios#2025
```

---

### 2️⃣ analisis-sonarqube.sh

**Propósito:** Ejecuta tests con cobertura y envía análisis a SonarQube.

**Pre-requisito:** Archivo `.sonar_token` (generado por `iniciar-app.sh`)

**Proceso de análisis:**
```
1. Verifica existencia de .sonar_token
2. Backend (cada microservicio):
   - Ejecuta: mvn clean verify sonar:sonar
   - Genera reportes JaCoCo (XML/HTML)
   - Envía análisis a SonarQube
3. Frontend:
   - Ejecuta: npm test --code-coverage --watch=false --browsers=ChromeHeadless
   - Genera reportes LCOV
   - Ejecuta: sonar-scanner
   - Envía análisis a SonarQube
```

**Ejecución:**
```bash
chmod +x analisis-sonarqube.sh
./analisis-sonarqube.sh
```

**Microservicios analizados:**
- ✅ ms_api_gateway (13 tests)
- ✅ ms_gestion_labs (11 tests)
- ✅ ms_gestion_users (11 tests)
- ✅ ms_gestion_resultados (9 tests)
- ✅ frontend_gestion_labs (25 tests)

**Total:** 69 tests unitarios

**Salida esperada:**
```
[1/5] Se está generando reporte para: ms_api_gateway
✓ Análisis completado

[2/5] Se está generando reporte para: ms_gestion_labs
✓ Análisis completado

[3/5] Se está generando reporte para: ms_gestion_users
✓ Análisis completado

[4/5] Se está generando reporte para: ms_gestion_resultados
✓ Análisis completado

[5/5] Se está generando reporte para: frontend_gestion_labs
✓ Análisis completado

Todos los reportes están listos en: http://localhost:9000
```

---

### 3️⃣ detener-app.sh

**Propósito:** Detiene y limpia todos los contenedores, imágenes y volúmenes.

**Operaciones realizadas:**
```
docker-compose down --rmi all --volumes --remove-orphans
```

**Elimina:**
- ✅ Todos los contenedores
- ✅ Imágenes locales del proyecto
- ✅ Volúmenes nombrados (sonarqube_data, sonarqube_extensions, sonarqube_logs, postgres_data)
- ✅ Contenedores huérfanos
- ✅ Red Docker

**Ejecución:**
```bash
chmod +x detener-app.sh
./detener-app.sh
```

**Salida esperada:**
```
Stopping gestion_labs_api_gateway        ... done
Stopping gestion_labs_labs_service       ... done
Stopping gestion_labs_users_service      ... done
Stopping gestion_labs_resultados_service ... done
Stopping gestion_labs_frontend           ... done
Stopping sonarqube                       ... done
Stopping sonarqube-db                    ... done

Removing containers, images, volumes and networks...
✓ Limpieza completada
```

---

### 4️⃣ limpiar-rebuild.sh

**Propósito:** Limpieza profunda y reconstrucción desde cero (troubleshooting).

**Cuándo usar:**
- 🔧 Errores persistentes en builds
- 🔧 Problemas de caché de Docker
- 🔧 Inconsistencias en node_modules o target/
- 🔧 Cambios mayores en dependencias

**Proceso de 7 pasos:**
```
PASO 1: docker-compose down --rmi all --volumes --remove-orphans
PASO 2: rm -rf frontend_gestion_labs/{dist,.angular,node_modules/.cache}
PASO 3: rm -rf ms_*/target/ (todos los builds Java)
PASO 4: docker system prune -f (limpieza de caché Docker)
PASO 5: cd ms_api_gateway && ./mvnw clean package -DskipTests
PASO 6: docker-compose build --no-cache (reconstruir sin caché)
PASO 7: docker-compose up -d (levantar servicios)
```

**Ejecución:**
```bash
chmod +x limpiar-rebuild.sh
./limpiar-rebuild.sh
```

**Salida esperada:**
```
[1/7] Deteniendo contenedores...
[2/7] Limpiando builds del frontend...
[3/7] Limpiando builds de Java...
[4/7] Limpiando caché de Docker...
[5/7] Reconstruyendo API Gateway...
[6/7] Reconstruyendo contenedores SIN caché...
[7/7] Levantando contenedores...

✓ Limpieza y rebuild completados

IMPORTANTE: Limpia el caché del navegador:
  - Presiona Ctrl+Shift+R (Cmd+Shift+R en Mac)
  - O ve a Herramientas de Desarrollador > Application > Clear storage
```

---

### 📝 Archivo .sonar_token

**Descripción:** Archivo generado automáticamente con el token de SonarQube.

**Ubicación:** Raíz del proyecto

**Contenido:**
```
sqa_d69c8e8542843d82a1b5c3f9e4d7a8c6b2f1e0d9
```

**Características:**
- 🔑 Token tipo: GLOBAL_ANALYSIS_TOKEN
- ⏱️ Nombre único: global-token-{timestamp}
- 🔐 Generado vía API: POST /api/user_tokens/generate
- ✅ Usado por: analisis-sonarqube.sh

**Importante:**
- ⚠️ NO subir a git (agregar a .gitignore)
- ⚠️ Regenerar con cada ejecución de iniciar-app.sh
- ⚠️ Requerido para analisis-sonarqube.sh

---

### 🔄 Flujo de Trabajo Típico

```bash
# 1. Primera vez o después de git pull
./iniciar-app.sh          # Levanta todo + configura SonarQube

# 2. Después de hacer cambios en código
./analisis-sonarqube.sh   # Tests + cobertura + SonarQube

# 3. Finalizar trabajo del día
./detener-app.sh          # Detiene todo y limpia

# 4. Solo si hay problemas
./limpiar-rebuild.sh      # Limpieza profunda + rebuild
```

---

## 🏗️ Arquitectura de Microservicios

El sistema está compuesto por **4 microservicios independientes**:

```
┌─────────────────────────────────────────────────────────────┐
│                     CLIENTE (Frontend)                       │
│                   http://localhost:8080                      │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                  🌐 MS_API_GATEWAY (8080)                    │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  • Validación JWT centralizada                       │   │
│  │  • Enrutamiento inteligente                          │   │
│  │  • Token blacklist (logout)                          │   │
│  │  • CORS global                                       │   │
│  │  • Spring Cloud Gateway (WebFlux)                    │   │
│  │  • Propagación de headers (X-User-Role, etc)        │   │
│  └──────────────────────────────────────────────────────┘   │
└───────┬────────────┬──────────────┬─────────────────┘
        │            │              │
        ▼            ▼              ▼
┌───────────────┐ ┌────────────┐ ┌─────────────────┐
│ 👥 MS_USERS   │ │ 🧪 MS_LABS │ │ 📊 MS_RESULTADOS │
│   (8083)      │ │   (8081)   │ │    (8082)        │
│               │ │            │ │                  │
│ • Usuarios    │ │ • Labs     │ │ • Resultados     │
│ • Pacientes   │ │ • Exámenes │ │ • Filtro por rol │
│ • Empleados   │ │ • Agendas  │ │ • Búsqueda       │
│ • Auth local  │ │ • Lab-Exams│ │ • Enriquecimiento│
│ • Registro    │ │            │ │ • CRUD completo  │
└───────┬───────┘ └──────┬─────┘ └────────┬────────┘
        │                │                 │
        └────────────────┴─────────────────┘
                         ▼
         ┌───────────────────────────────────┐
         │  🗄️ ORACLE AUTONOMOUS DATABASE    │
         │     (Oracle Cloud - OCI)          │
         │                                   │
         │  • 10 tablas relacionadas         │
         │  • Cascadas automáticas           │
         │  • Wallet de conexión segura      │
         └───────────────────────────────────┘
```

**Nota:** MS_GESTION_LABS ya no maneja resultados. Se creó un microservicio dedicado (MS_GESTION_RESULTADOS) para mejor separación de responsabilidades.
         └───────────────────────────────────┘
```

**Nota:** MS_GESTION_LABS ya no maneja resultados. Se creó un microservicio dedicado (MS_GESTION_RESULTADOS) para mejor separación de responsabilidades.

### 🌐 MS_API_GATEWAY (Puerto 8080)

**Función Principal:** Punto de entrada único para todas las peticiones del sistema.

**Responsabilidades:**
- ✅ **Validación JWT**: Verifica tokens en cada petición antes de enrutar
- ✅ **Enrutamiento**: Redirige peticiones a los microservicios correspondientes
- ✅ **Blacklist de tokens**: Invalida tokens al hacer logout
- ✅ **CORS**: Configuración centralizada para frontend
- ✅ **Autenticación**: Endpoint de login que delega a ms_gestion_users
- ✅ **Filtrado global**: JwtGlobalFilter ejecuta validaciones antes de cualquier ruta
- ✅ **Rutas públicas**: Permite acceso sin token a endpoints específicos

**Tecnologías:**
- Spring Cloud Gateway (WebFlux reactivo)
- Spring Security (configuración permitAll, confía en validación JWT)
- JSON Web Tokens (JWT) con algoritmo HS512
- Expiración de tokens: 120 minutos

**Flujo de autenticación:**
```
1. Cliente → POST /auth/login (credenciales)
2. Gateway → Delega a ms_gestion_users:8082
3. ms_gestion_users → Valida y genera JWT
4. Cliente recibe token
5. Cliente → Peticiones con header: Authorization: Bearer {token}
6. Gateway → JwtGlobalFilter valida token
7. Gateway → Enruta a microservicio correspondiente
8. Microservicio → Confía en Gateway, procesa sin re-validar
```

---

### 👥 MS_GESTION_USERS (Puerto 8083)

**Función Principal:** Gestión de usuarios, pacientes y empleados del sistema.

**Responsabilidades:**
- ✅ **Autenticación local**: Genera JWT tras validar credenciales
- ✅ **Gestión de usuarios**: CRUD de usuarios (solo lectura pública)
- ✅ **Registro completo**: Creación de pacientes/empleados con usuario automático
- ✅ **Gestión de pacientes**: CRUD completo con cascadas
- ✅ **Gestión de empleados**: CRUD completo con cascadas
- ✅ **Hash de contraseñas**: BCrypt con costo 10
- ✅ **Roles**: ADMIN, LAB_EMPLOYEE, PATIENT

**Tecnologías:**
- Spring Boot 3.5.7
- Spring Data JPA
- Spring Security
- BCryptPasswordEncoder
- Oracle JDBC Driver

---

### 🧪 MS_GESTION_LABS (Puerto 8081)

**Función Principal:** Gestión de laboratorios, exámenes y agendas.

**Responsabilidades:**
- ✅ **Laboratorios**: CRUD completo de laboratorios
- ✅ **Exámenes**: Catálogo de exámenes médicos
- ✅ **Lab-Exams**: Relación entre laboratorios y exámenes (precios)
- ✅ **Agendas**: Programación de citas médicas
- ✅ **Rutas públicas**: Listado de laboratorios sin autenticación

**Tecnologías:**
- Spring Boot 3.5.7
- Spring Data JPA
- Spring Security
- Oracle JDBC Driver

**Nota:** Los resultados de exámenes fueron migrados a MS_GESTION_RESULTADOS para mejor escalabilidad.

---

### 📊 MS_GESTION_RESULTADOS (Puerto 8082)

**Función Principal:** Gestión exclusiva de resultados de exámenes con filtrado por rol.

**Responsabilidades:**
- ✅ **CRUD completo de resultados**: Crear, leer, actualizar y eliminar resultados
- ✅ **Filtrado por rol contextual**:
  - **PATIENT**: Solo ve sus propios resultados (filtro por `pacienteId`)
  - **LAB_EMPLOYEE**: Ve todos los resultados
  - **ADMIN**: Ve todos los resultados
- ✅ **Búsqueda avanzada**:
  - Por paciente específico
  - Por laboratorio
  - Por examen
  - Por estado (PENDIENTE, COMPLETADO, CANCELADO)
- ✅ **Enriquecimiento de datos**: Llama a MS_USERS y MS_LABS para obtener información adicional
- ✅ **Validación de permisos**: Usa headers del Gateway (`X-User-Role`, `X-Patient-Id`)

**Tecnologías:**
- Spring Boot 3.5.7
- Spring Data JPA
- Spring Security
- RestTemplate para comunicación entre microservicios
- Oracle JDBC Driver

**Endpoints principales:**
- `GET /resultados` - Listar resultados (filtrado automático por rol)
- `GET /resultados/{id}` - Obtener resultado específico
- `GET /resultados/paciente/{pacienteId}` - Resultados de un paciente
- `GET /resultados/laboratorio/{labId}` - Resultados de un laboratorio
- `GET /resultados/examen/{examenId}` - Resultados de un tipo de examen
- `POST /resultados` - Crear nuevo resultado
- `PUT /resultados/{id}` - Actualizar resultado
- `DELETE /resultados/{id}` - Eliminar resultado

**Ejemplo de filtrado por rol:**
```java
@GetMapping
@PreAuthorize("hasAnyRole('PATIENT', 'LAB_EMPLOYEE', 'ADMIN')")
public ResponseEntity<Map<String, Object>> getAllResults(
        @RequestHeader(value = "X-User-Role", required = false) String userRole,
        @RequestHeader(value = "X-Patient-Id", required = false) String patientIdStr) {
    
    if ("PATIENT".equals(userRole) && patientIdStr != null) {
        // Paciente solo ve sus resultados
        Long patientId = Long.parseLong(patientIdStr);
        return service.findByPacienteId(patientId);
    } else {
        // LAB_EMPLOYEE y ADMIN ven todos
        return service.findAll();
    }
}
```

---

## 📦 Arquetipos de Microservicios

El proyecto incluye **arquetipos reutilizables** en la carpeta `arquetipo_backend/` para facilitar el desarrollo de nuevos microservicios siguiendo el patrón **Layered Architecture**.

### 🎯 Estructura de Arquetipos

```
arquetipo_backend/
├── ms_api_gateway/
├── ms_gestion_labs/
├── ms_gestion_resultados/
└── ms_gestion_users/
```

Cada arquetipo incluye:
- ✅ **ARQUETIPO_BACKEND.md**: Documentación técnica completa
- ✅ **README.md**: Guía rápida de uso
- ✅ **GUIA_IMPLEMENTACION.md**: Implementación paso a paso (algunos arquetipos)
- ✅ **pom.xml**: Dependencias Maven configuradas
- ✅ **Dockerfile**: Imagen Docker optimizada
- ✅ **src/**: Código fuente completo
- ✅ **.env.example**: Variables de entorno

### 📋 Arquetipos Disponibles

#### 1. Arquetipo MS_GESTION_USERS

**Características:**
- Autenticación con JWT local
- Registro de pacientes y empleados
- CRUD de usuarios con BCrypt
- Integración con Oracle Autonomous Database
- Spring Security con `@PreAuthorize`

**Uso:**
```bash
cp -r arquetipo_backend/ms_gestion_users nuevo_microservicio
cd nuevo_microservicio
# Actualizar nombres de paquetes, base de datos, puerto
mvn clean install
```

#### 2. Arquetipo MS_GESTION_LABS

**Características:**
- CRUD de laboratorios, exámenes, agendas
- Rutas públicas y privadas
- Relaciones complejas (Lab-Exams)
- DTOs para transferencia de datos

#### 3. Arquetipo MS_GESTION_RESULTADOS

**Características:**
- **Filtrado contextual por rol** (PATIENT, LAB_EMPLOYEE, ADMIN)
- Búsqueda avanzada con múltiples criterios
- Enriquecimiento de datos desde otros microservicios
- RestTemplate configurado para comunicación HTTP
- Headers del Gateway (`X-User-Role`, `X-Patient-Id`, etc.)
- Validación de permisos con `@PreAuthorize`

**Endpoints documentados:**
```
GET    /resultados                      # Filtrado automático por rol
GET    /resultados/{id}                 # Resultado específico
GET    /resultados/paciente/{id}        # Por paciente
GET    /resultados/laboratorio/{id}     # Por laboratorio
GET    /resultados/examen/{id}          # Por examen
POST   /resultados                      # Crear resultado
PUT    /resultados/{id}                 # Actualizar resultado
DELETE /resultados/{id}                 # Eliminar resultado
```

**Ejemplo de uso del arquetipo:**
```bash
# Copiar arquetipo
cp -r arquetipo_backend/ms_gestion_resultados mi_nuevo_servicio

# Configurar variables de entorno
cp mi_nuevo_servicio/.env.example mi_nuevo_servicio/.env

# Actualizar application.properties
# - Cambiar puerto
# - Configurar Oracle Wallet
# - Actualizar nombre del servicio

# Compilar y ejecutar
cd mi_nuevo_servicio
mvn clean install
mvn spring-boot:run
```

#### 4. Arquetipo MS_API_GATEWAY

**Características:**
- Spring Cloud Gateway configurado
- JwtGlobalFilter para validación centralizada
- Token blacklist service
- CORS global
- Enrutamiento a múltiples microservicios

### 🔧 Patrón Layered Architecture

Todos los arquetipos siguen la **Arquitectura en Capas**:

```
src/main/java/com/nombre_microservicio/
├── config/                    # Configuración (Security, CORS, etc)
│   ├── SecurityConfig.java
│   └── RestClientConfig.java
├── controller/                # Capa de presentación (REST API)
│   └── EntidadController.java
├── service/                   # Capa de lógica de negocio
│   ├── EntidadService.java
│   └── EntidadServiceImpl.java
├── repository/                # Capa de acceso a datos (JPA)
│   └── EntidadRepository.java
├── model/                     # Entidades JPA
│   └── EntidadModel.java
├── dto/                       # Data Transfer Objects
│   ├── EntidadRequest.java
│   └── EntidadResponse.java
└── exceptionHandler/          # Manejo global de errores
    └── GlobalExceptionHandler.java
```

### 📚 Ventajas de los Arquetipos

1. **Desarrollo ágil**: Nuevo microservicio en minutos
2. **Consistencia**: Todos siguen el mismo patrón arquitectónico
3. **Mejores prácticas**: Security, validación, DTOs incluidos
4. **Documentación**: Cada arquetipo está documentado
5. **Reutilización**: Código probado y funcional

---

## 💻 Tecnologías y Dependencias

### Versiones Core

| Tecnología | Versión |
|------------|---------|
| **Java** | 21 (LTS) |
| **Spring Boot** | 3.3.5 (Gateway), 3.5.7 (Microservicios) |
| **Spring Cloud** | 2023.0.3 |
| **Maven** | 3.9+ |
| **Oracle Database** | 19c (Autonomous) |

### Dependencias Principales

#### MS_API_GATEWAY
```xml
<!-- Spring Cloud Gateway (Reactive) -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>

<!-- Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT (JSON Web Tokens) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>

<!-- WebFlux (Reactive) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

#### MS_GESTION_USERS & MS_GESTION_LABS
```xml
<!-- Spring Web MVC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Spring Security (Configuración permitAll) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Validación -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Oracle JDBC Driver -->
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc11</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Oracle UCP (Universal Connection Pool) -->
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ucp</artifactId>
</dependency>

<!-- Oracle Security (Wallet) -->
<dependency>
    <groupId>com.oracle.database.security</groupId>
    <artifactId>oraclepki</artifactId>
</dependency>
<dependency>
    <groupId>com.oracle.database.security</groupId>
    <artifactId>osdt_core</artifactId>
</dependency>
<dependency>
    <groupId>com.oracle.database.security</groupId>
    <artifactId>osdt_cert</artifactId>
</dependency>

<!-- BCrypt (Hash de contraseñas) -->
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

---

## 🔐 Seguridad y Autenticación

### Modelo de Seguridad Centralizado

El sistema implementa **seguridad JWT centralizada** donde:

1. **API Gateway** es responsable de **TODA** la validación de seguridad
2. Los microservicios **confían completamente** en el Gateway
3. No existe re-validación de JWT en los microservicios

### 🛡️ Spring Boot Security - Implementación en el Proyecto

**⚠️ IMPORTANTE:** Este proyecto utiliza **Spring Boot Security en TODOS los microservicios**, pero con diferentes propósitos según la arquitectura de seguridad centralizada.

#### Implementación por Microservicio:

##### 1️⃣ MS_API_GATEWAY (Puerto 8080)

**Dependencia Maven:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

**Configuración de Spring Security:**
```java
@Configuration
@EnableWebFluxSecurity  // WebFlux (Reactivo)
public class SecurityConfig {
    
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchange -> exchange
                .anyRequest().permitAll()
            )
            .build();
    }
}
```

**Uso de Spring Security:**
- ✅ Configuración base de seguridad WebFlux
- ✅ Deshabilitar CSRF (API REST)
- ✅ Configuración `permitAll()` porque la seguridad real la maneja `JwtGlobalFilter` (filtro custom)

**Nota:** El Gateway NO usa Spring Security para validar JWT. La validación se hace con un **filtro personalizado** (`JwtGlobalFilter`) que se ejecuta antes de las rutas.

---

##### 2️⃣ MS_GESTION_USERS (Puerto 8082)

**Dependencia Maven:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

**Configuración de Spring Security:**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // ← Habilita @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()  // Confía en Gateway
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // ← Hash de passwords
    }
}
```

**Uso de Spring Security:**
- ✅ **BCryptPasswordEncoder**: Hash seguro de contraseñas
- ✅ **@EnableMethodSecurity**: Habilita anotaciones `@PreAuthorize` en controladores
- ✅ **CORS**: Configuración de orígenes permitidos
- ✅ **SessionManagement STATELESS**: Sin sesiones
- ✅ **permitAll()**: Confía en que el Gateway ya validó el JWT

**Ejemplo de uso de `@PreAuthorize`:**
```java
@PostMapping("/registro/paciente")
@PreAuthorize("hasRole('ADMIN')")  // ← Solo usuarios ADMIN
public ResponseEntity<Map<String, Object>> registrarPaciente(...) {
    // Solo se ejecuta si el usuario tiene rol ADMIN
}
```

---

##### 3️⃣ MS_GESTION_LABS (Puerto 8081)

**Dependencia Maven:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

**Configuración de Spring Security:**
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // ← Habilita @PreAuthorize
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // Confía en Gateway
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        return http.build();
    }
}
```

**Uso de Spring Security:**
- ✅ **@EnableMethodSecurity**: Habilita anotaciones `@PreAuthorize` en controladores
- ✅ **SessionManagement STATELESS**: Sin sesiones
- ✅ **CSRF deshabilitado**: Para API REST
- ✅ **permitAll()**: Confía en que el Gateway ya validó el JWT

---

#### 📊 Resumen: Uso de Spring Boot Security

| Microservicio | Spring Security | JWT Validation | BCrypt | @PreAuthorize | CORS |
|---------------|----------------|----------------|--------|---------------|------|
| **MS_API_GATEWAY** | ✅ WebFlux | ✅ (JwtGlobalFilter) | ❌ | ❌ | ✅ Global |
| **MS_GESTION_USERS** | ✅ Web MVC | ❌ (confía en Gateway) | ✅ | ✅ | ✅ Bean |
| **MS_GESTION_LABS** | ✅ Web MVC | ❌ (confía en Gateway) | ❌ | ✅ | ❌ |

**Arquitectura de Seguridad Perimetral:**
- El **API Gateway** es el **único punto de entrada** y valida JWT
- Los **microservicios internos** confían en el Gateway (`permitAll()`)
- En producción, los microservicios deben estar en **red interna** (no expuestos públicamente)

---

### Componentes de Seguridad

#### 1. JwtGlobalFilter (API Gateway)
```java
@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {
    
    @Override
    public int getOrder() {
        return -100; // Ejecuta ANTES que otros filtros
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. Extrae token del header Authorization
        // 2. Valida firma, expiración y blacklist
        // 3. Extrae username y role del token
        // 4. Almacena en exchange.getAttributes()
        // 5. Continúa la cadena sin modificar headers
    }
}
```

#### 2. TokenBlacklistService

**IMPORTANTE:** La blacklist SOLO existe en el API Gateway.

```java
@Service
public class TokenBlacklistService {
    private final Set<String> blacklistedTokens = 
        ConcurrentHashMap.newKeySet(); // Thread-safe
    
    public void blacklistToken(String token) { ... }
    public boolean isBlacklisted(String token) { ... }
}
```

**¿Por qué solo en el Gateway?**
- ✅ El Gateway es el **único punto de entrada** a todos los microservicios
- ✅ El Gateway es quien **valida los JWT** en cada request
- ✅ Los microservicios **confían en el Gateway** (usan `permitAll()`)
- ❌ NO hay sincronización entre microservicios (innecesaria y compleja)

#### 3. Configuración de Seguridad en Microservicios
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Confía en Gateway
            );
        return http.build();
    }
}
```

**Nota:** Los microservicios tienen un `JwtAuthenticationFilter` que solo extrae información del token para logging/debugging, pero **NO validan blacklist** (esa es responsabilidad exclusiva del Gateway).

### Proceso de Autenticación

#### Login
```bash
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "admin@laboratorioandino.cl",
  "password": "Admin123"
}
```

**Respuesta:**
```json
{
  "code": "000",
  "description": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBsYWJvcmF0b3Jpb2FuZGluby5jbCIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTY5OTEwNDAwMCwiZXhwIjoxNjk5MTExMjAwfQ...",
    "userId": 1,
    "username": "admin@laboratorioandino.cl",
    "role": "ADMIN"
  }
}
```

#### Token JWT - Estructura
```json
{
  "sub": "admin@laboratorioandino.cl",
  "role": "ADMIN",
  "iat": 1699104000,
  "exp": 1699111200
}
```

**Algoritmo:** HS512  
**Expiración:** 120 minutos  
**Secreto:** Compartido entre Gateway y ms_gestion_users

#### Logout
```bash
POST http://localhost:8080/auth/logout
Authorization: Bearer {token}
```

**Acción:** El token se agrega a la blacklist y queda invalidado inmediatamente.

### Roles del Sistema

| Rol | Permisos |
|-----|----------|
| **ADMIN** | Acceso total: puede registrar pacientes, empleados, gestionar laboratorios |
| **EMPLEADO** | Gestión de agendas, exámenes, resultados |
| **PACIENTE** | Consulta de sus propios datos y resultados |

---

## 🗄️ Base de Datos

Para información completa sobre la estructura, configuración y scripts de base de datos, consulta:

📘 **[Documentación de Base de Datos](base_de_datos/README_BASE_DE_DATOS.md)**

**Resumen:**
- **Tipo:** Oracle Autonomous Database (19c) en OCI
- **Conexión:** Oracle Wallet (SSL/TLS)
- **Tablas:** 10 tablas relacionadas con cascadas automáticas
- **Secuencias:** Auto-incremento para todas las PKs

---

## 📡 Endpoints de la API

### 🌐 Rutas Públicas (sin autenticación)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/auth/login` | Autenticación de usuarios |
| POST | `/auth/logout` | Cerrar sesión (invalida token) |
| GET | `/labs` | Listar laboratorios |
| GET | `/labs/{id}` | Ver detalle de laboratorio |

---

### 🔒 Rutas Privadas (requieren JWT)

#### 👥 Usuarios (SOLO LECTURA)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/users` | Listar usuarios | Cualquier autenticado |
| GET | `/users/{id}` | Ver usuario | Cualquier autenticado |

❌ **POST/PUT/DELETE `/users`** → **BLOQUEADO** (usuarios se crean automáticamente)

---

#### 📝 Registro (Creación de Usuarios)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| POST | `/registro/paciente` | Crear paciente + usuario | ADMIN |
| POST | `/registro/empleado` | Crear empleado + usuario | ADMIN |

---

#### 🩺 Pacientes

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/pacientes` | Listar pacientes | Cualquier autenticado |
| GET | `/pacientes/{id}` | Ver paciente | Cualquier autenticado |
| PUT | `/pacientes/{id}` | Actualizar paciente | ADMIN |
| DELETE | `/pacientes/{id}` | Eliminar paciente | ADMIN |

---

#### 👨‍💼 Empleados

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/empleados` | Listar empleados | Cualquier autenticado |
| GET | `/empleados/{id}` | Ver empleado | Cualquier autenticado |
| PUT | `/empleados/{id}` | Actualizar empleado | ADMIN |
| DELETE | `/empleados/{id}` | Eliminar empleado | ADMIN |

---

#### 🏥 Laboratorios

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| POST | `/labs` | Crear laboratorio | Cualquier autenticado |
| PUT | `/labs/{id}` | Actualizar laboratorio | Cualquier autenticado |
| DELETE | `/labs/{id}` | Eliminar laboratorio | ADMIN |

---

#### 🧪 Exámenes

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/exams` | Listar exámenes | Cualquier autenticado |
| GET | `/exams/{id}` | Ver examen | Cualquier autenticado |
| POST | `/exams` | Crear examen | ADMIN, EMPLEADO |
| PUT | `/exams/{id}` | Actualizar examen | ADMIN, EMPLEADO |
| DELETE | `/exams/{id}` | Eliminar examen | ADMIN |

---

#### 📅 Agendas

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/agendas` | Listar agendas | Cualquier autenticado |
| GET | `/agendas/{id}` | Ver agenda | Cualquier autenticado |
| POST | `/agendas` | Crear agenda | ADMIN, EMPLEADO |
| PUT | `/agendas/{id}` | Actualizar agenda | ADMIN, EMPLEADO |
| DELETE | `/agendas/{id}` | Eliminar agenda | ADMIN |

---

#### 📊 Resultados (MS_GESTION_RESULTADOS)

**Nota:** Resultados fueron migrados de MS_LABS a un microservicio dedicado para mejor escalabilidad y filtrado por rol.

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/resultados` | Listar resultados (filtrado automático) | PATIENT, LAB_EMPLOYEE, ADMIN |
| GET | `/resultados/{id}` | Ver resultado específico | PATIENT, LAB_EMPLOYEE, ADMIN |
| GET | `/resultados/paciente/{pacienteId}` | Resultados de un paciente | LAB_EMPLOYEE, ADMIN |
| GET | `/resultados/laboratorio/{labId}` | Resultados de un laboratorio | LAB_EMPLOYEE, ADMIN |
| GET | `/resultados/examen/{examenId}` | Resultados de un tipo de examen | LAB_EMPLOYEE, ADMIN |
| POST | `/resultados` | Crear nuevo resultado | LAB_EMPLOYEE, ADMIN |
| PUT | `/resultados/{id}` | Actualizar resultado | LAB_EMPLOYEE, ADMIN |
| DELETE | `/resultados/{id}` | Eliminar resultado | ADMIN |

**Filtrado contextual:**
- **PATIENT**: Solo ve sus propios resultados (automático por `pacienteId`)
- **LAB_EMPLOYEE**: Ve todos los resultados
- **ADMIN**: Ve todos los resultados

---

#### 🔗 Lab-Exams (Relación Laboratorio-Examen)

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/lab-exams` | Listar relaciones | Cualquier autenticado |
| GET | `/lab-exams/{id}` | Ver relación | Cualquier autenticado |
| POST | `/lab-exams` | Crear relación | ADMIN, EMPLEADO |
| PUT | `/lab-exams/{id}` | Actualizar relación | ADMIN, EMPLEADO |
| DELETE | `/lab-exams/{id}` | Eliminar relación | ADMIN |

---

## 📚 Ejemplos de Uso

### 1. Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@laboratorioandino.cl",
    "password": "Admin123"
  }'
```

**Respuesta:**
```json
{
  "code": "000",
  "description": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userId": 1,
    "username": "admin@laboratorioandino.cl",
    "role": "ADMIN"
  }
}
```

---

### 2. Crear Paciente (Registro Completo)

**⚠️ IMPORTANTE:** Usar `/registro/paciente`, NO `/pacientes`

```bash
curl -X POST http://localhost:8080/registro/paciente \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "pnombre": "María",
    "snombre": "Fernanda",
    "papellido": "González",
    "sapellido": "Pérez",
    "rut": "12.345.678-9",
    "contacto": {
      "fono1": "+56912345678",
      "fono2": "+56223456789",
      "email": "maria.gonzalez@ejemplo.cl"
    },
    "direccion": {
      "calle": "Av. Providencia",
      "numero": 1234,
      "ciudad": "Santiago",
      "comuna": "Providencia",
      "region": "Metropolitana"
    },
    "password": "Maria2025!"
  }'
```

**Respuesta:**
```json
{
  "code": "000",
  "description": "Paciente registrado exitosamente",
  "data": {
    "pacienteId": 5,
    "empleadoId": null,
    "usuarioId": 10,
    "username": "maria.gonzalez@ejemplo.cl",
    "role": "PACIENTE",
    "mensaje": "Paciente y usuario registrados correctamente"
  }
}
```

**Creaciones automáticas:**
- ✅ Registro en tabla `contactos`
- ✅ Registro en tabla `direcciones`
- ✅ Registro en tabla `pacientes`
- ✅ Registro en tabla `users` (password hasheado con BCrypt)

---

### 3. Actualizar Paciente

```bash
curl -X PUT http://localhost:8080/pacientes/5 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "pnombre": "María",
    "snombre": "Isabel",
    "papellido": "González",
    "sapellido": "Pérez",
    "rut": "12.345.678-9",
    "dirId": 8,
    "contactoId": 8
  }'
```

---

### 4. Crear Empleado

```bash
curl -X POST http://localhost:8080/registro/empleado \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "pnombre": "Carlos",
    "snombre": "Alberto",
    "papellido": "Ramírez",
    "sapellido": "Torres",
    "rut": "18.765.432-1",
    "cargo": "Técnico de Laboratorio",
    "contacto": {
      "fono1": "+56987654321",
      "fono2": "+56232345678",
      "email": "carlos.ramirez@laboratorioandino.cl"
    },
    "direccion": {
      "calle": "Av. Las Condes",
      "numero": 5678,
      "ciudad": "Santiago",
      "comuna": "Las Condes",
      "region": "Metropolitana"
    },
    "password": "Carlos2025!"
  }'
```

---

### 5. Crear Laboratorio

```bash
curl -X POST http://localhost:8080/labs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "nombre": "Laboratorio Central",
    "tipo": "Clínico",
    "direccion": {
      "calle": "Av. Apoquindo",
      "numero": 3000,
      "ciudad": "Santiago",
      "comuna": "Las Condes",
      "region": "Metropolitana"
    },
    "contacto": {
      "fono1": "+56223456789",
      "fono2": "+56223456790",
      "email": "contacto@labcentral.cl"
    }
  }'
```

---

### 6. Crear Examen

```bash
curl -X POST http://localhost:8080/exams \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "codigo": "HEM1",
    "nombre": "Hemograma Completo",
    "tipo": "Hematología"
  }'
```

---

### 7. Crear Agenda (Cita para Examen)

```bash
curl -X POST http://localhost:8080/agendas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "pacienteId": 5,
    "empleadoId": 3,
    "examenId": 2,
    "fecha": "2025-11-10T09:30:00",
    "estado": "PENDIENTE"
  }'
```

---

### 8. Crear Resultado de Examen

```bash
curl -X POST http://localhost:8080/results \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "agendaId": 8,
    "resultado": "Valores normales. Leucocitos: 7500/mm³, Eritrocitos: 4.8M/mm³",
    "observaciones": "Paciente en ayunas. Sin anomalías detectadas.",
    "estado": "COMPLETADO"
  }'
```

---

### 9. Listar Usuarios

```bash
curl -X GET http://localhost:8080/users \
  -H "Authorization: Bearer {TOKEN}"
```

---

### 10. Logout

```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer {TOKEN}"
```

**Resultado:** El token queda invalidado en la blacklist.

---

## 🚀 Ejecución del Proyecto

### ⚡ Inicio Rápido con Scripts Automatizados

El proyecto incluye scripts que automatizan completamente la inicialización y gestión de la infraestructura:

#### 🟢 1. Iniciar Infraestructura Completa

```bash
./iniciar-app.sh
```

**Este script realiza:**
- ✅ Levanta PostgreSQL para SonarQube
- ✅ Construye y levanta todos los contenedores Docker (Frontend + 4 Backend)
- ✅ Espera a que SonarQube esté disponible
- ✅ Cambia la contraseña de admin en SonarQube
- ✅ Genera un token global de análisis dinámicamente
- ✅ Crea 4 proyectos backend en SonarQube
- ✅ Guarda el token en `.sonar_token` para uso del script de análisis

**Servicios disponibles:**
```
Frontend (Angular):    http://localhost:4200
API Gateway:           http://localhost:8080
MS Gestion Labs:       http://localhost:8081
MS Gestion Resultados: http://localhost:8082
MS Gestion Users:      http://localhost:8083
SonarQube:             http://localhost:9000
```

#### 🧪 2. Ejecutar Análisis de Cobertura

```bash
./analisis-sonarqube.sh
```

**Este script realiza:**
- ✅ Lee el token desde `.sonar_token`
- ✅ Ejecuta tests con cobertura en **4 microservicios backend** (JUnit + JaCoCo)
- ✅ Ejecuta tests con cobertura en **frontend** (Karma + Jasmine)
- ✅ Envía análisis a SonarQube para cada proyecto

**Requisito:** Debe ejecutarse **después** de `iniciar-app.sh`

#### 🔴 3. Detener Infraestructura

```bash
./detener-app.sh
```

**Este script realiza:**
- ✅ Detiene todos los contenedores Docker
- ✅ Elimina contenedores, imágenes y volúmenes
- ✅ Limpia la red Docker

---

### 📋 Prerrequisitos

- ✅ **Docker & Docker Compose** instalados
- ✅ **Java 21** (solo si ejecutas fuera de Docker)
- ✅ **Maven 3.9+** (solo si ejecutas fuera de Docker)
- ✅ **Node.js 20+** y **npm** (solo para desarrollo del frontend)
- ✅ **Oracle Wallet** configurado en `/wallet/Wallet_databaseFullStack3/`
- ✅ **Base de datos Oracle** creada (ver [README_BASE_DE_DATOS.md](README_BASE_DE_DATOS.md))

---

### 🐳 Ejecución Manual con Docker Compose

Si prefieres ejecutar manualmente:

```bash
# Iniciar servicios
docker-compose up --build -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down
```

---

### 💻 Ejecución Manual Sin Docker (Desarrollo)

#### Paso 1: Compilar Microservicios

```bash
# Compilar ms_gestion_users
cd ms_gestion_users
mvn clean install -DskipTests

# Compilar ms_gestion_labs
cd ../ms_gestion_labs
mvn clean install -DskipTests

# Compilar ms_gestion_resultados
cd ../ms_gestion_resultados
mvn clean install -DskipTests

# Compilar ms_api_gateway
cd ../ms_api_gateway
mvn clean install -DskipTests
```

#### Paso 2: Ejecutar Microservicios

**Opción A: Usando Maven**

```bash
# Terminal 1 - MS Gestión Users (8083)
cd ms_gestion_users
mvn spring-boot:run

# Terminal 2 - MS Gestión Labs (8081)
cd ms_gestion_labs
mvn spring-boot:run

# Terminal 3 - MS Gestión Resultados (8082)
cd ms_gestion_resultados
mvn spring-boot:run

# Terminal 4 - API Gateway (8080)
cd ms_api_gateway
mvn spring-boot:run
```

**Opción B: Usando JAR**

```bash
# Terminal 1 - MS Gestión Users (8083)
java -jar ms_gestion_users/target/ms_gestion_users-0.0.1-SNAPSHOT.jar

# Terminal 2 - MS Gestión Labs (8081)
java -jar ms_gestion_labs/target/ms_gestion_labs-0.0.1-SNAPSHOT.jar

# Terminal 3 - MS Gestión Resultados (8082)
java -jar ms_gestion_resultados/target/ms_gestion_resultados-0.0.1-SNAPSHOT.jar

# Terminal 4 - API Gateway (8080)
java -jar ms_api_gateway/target/ms_api_gateway-0.0.1-SNAPSHOT.jar
```

#### Paso 3: Ejecutar Frontend

```bash
cd frontend_gestion_labs
npm install
npm start
```

---

### ✅ Verificar Ejecución

**Health checks:**

```bash
# MS Gestión Users
curl http://localhost:8083/actuator/health

# MS Gestión Labs
curl http://localhost:8081/actuator/health

# MS Gestión Resultados
curl http://localhost:8082/actuator/health

# API Gateway
curl http://localhost:8080/actuator/health
```

**Probar login:**

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@laboratorioandino.cl","password":"admin123"}'
```

### Paso 6: Verificar Ejecución

**Verificar que cada microservicio esté corriendo:**

```bash
# MS Gestión Users
curl http://localhost:8083/actuator/health

# MS Gestión Labs
curl http://localhost:8081/actuator/health

# MS Gestión Resultados
curl http://localhost:8082/actuator/health

# API Gateway
curl http://localhost:8080/actuator/health
```

**Probar login:**

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@laboratorioandino.cl",
    "password": "Admin123"
  }'
```

---

## 📁 Estructura del Proyecto

```
microservicios_laboratorio_sboot/
│
├── ms_api_gateway/                    # API Gateway (Puerto 8080)
│   ├── src/main/java/com/api_gateway/
│   │   ├── config/
│   │   │   ├── GatewayConfig.java           # Definición de rutas
│   │   │   ├── SecurityConfig.java          # Configuración Spring Security
│   │   │   └── CorsConfig.java              # CORS global
│   │   ├── controller/
│   │   │   └── AuthController.java          # Login y Logout
│   │   ├── filter/
│   │   │   ├── JwtGlobalFilter.java         # Validación JWT centralizada
│   │   │   └── AddUserHeadersFilter.java    # Propagación de headers
│   │   ├── service/
│   │   │   ├── TokenBlacklistService.java   # Gestión de blacklist
│   │   │   └── JwtService.java              # Utilidades JWT
│   │   ├── dto/
│   │   │   └── AuthRequest.java             # DTO de autenticación
│   │   └── properties/
│   │       └── JwtProperties.java           # Configuración JWT
│   ├── src/main/resources/
│   │   └── application.properties           # Configuración del Gateway
│   └── pom.xml                              # Dependencias Maven
│
├── ms_gestion_users/                  # Microservicio Usuarios (Puerto 8082)
│   ├── src/main/java/com/gestion_users/
│   │   ├── config/
│   │   │   └── SecurityConfig.java          # permitAll() - Confía en Gateway
│   │   ├── controller/
│   │   │   ├── AuthController.java          # Autenticación local
│   │   │   ├── UserController.java          # CRUD usuarios (solo GET)
│   │   │   ├── PacienteController.java      # CRUD pacientes
│   │   │   ├── EmpleadoController.java      # CRUD empleados
│   │   │   └── RegistroController.java      # Registro pacientes/empleados
│   │   ├── service/
│   │   │   ├── UserService.java             # Lógica de usuarios
│   │   │   ├── PacienteService.java         # Lógica de pacientes
│   │   │   ├── EmpleadoService.java         # Lógica de empleados
│   │   │   └── RegistroService.java         # Registro completo
│   │   ├── repository/
│   │   │   ├── UserRepository.java          # JPA Repository
│   │   │   ├── PacienteRepository.java      # JPA Repository
│   │   │   ├── EmpleadoRepository.java      # JPA Repository
│   │   │   ├── ContactoRepository.java      # JPA Repository
│   │   │   └── DireccionRepository.java     # JPA Repository
│   │   ├── model/
│   │   │   ├── UserModel.java               # Entidad JPA
│   │   │   ├── PacienteModel.java           # Entidad JPA
│   │   │   ├── EmpleadoModel.java           # Entidad JPA
│   │   │   ├── ContactoModel.java           # Entidad JPA
│   │   │   └── DireccionModel.java          # Entidad JPA
│   │   └── dto/
│   │       ├── RegistroPacienteRequest.java # DTO registro paciente
│   │       ├── RegistroEmpleadoRequest.java # DTO registro empleado
│   │       └── RegistroResponse.java        # DTO respuesta registro
│   ├── src/main/resources/
│   │   ├── application.properties           # Configuración + Oracle
│   │   └── ojdbc.properties                 # Propiedades Oracle
│   └── pom.xml                              # Dependencias Maven
│
├── ms_gestion_labs/                   # Microservicio Laboratorios (Puerto 8081)
│   ├── src/main/java/com/gestion_labs/
│   │   ├── config/
│   │   │   └── SecurityConfig.java          # permitAll() - Confía en Gateway
│   │   ├── controller/
│   │   │   ├── LaboratorioController.java   # CRUD laboratorios
│   │   │   ├── ExamenController.java        # CRUD exámenes
│   │   │   ├── AgendaController.java        # CRUD agendas
│   │   │   └── LabExamController.java       # CRUD relaciones lab-exam
│   │   │   # Nota: ResultadoController eliminado → migrado a MS_RESULTADOS
│   │   ├── service/
│   │   │   ├── LaboratorioService.java      # Lógica laboratorios
│   │   │   ├── ExamenService.java           # Lógica exámenes
│   │   │   ├── AgendaService.java           # Lógica agendas
│   │   │   └── LabExamService.java          # Lógica relaciones
│   │   ├── repository/
│   │   │   ├── LaboratorioRepository.java   # JPA Repository
│   │   │   ├── ExamenRepository.java        # JPA Repository
│   │   │   ├── AgendaRepository.java        # JPA Repository
│   │   │   └── LabExamRepository.java       # JPA Repository
│   │   ├── model/
│   │   │   ├── LaboratorioModel.java        # Entidad JPA
│   │   │   ├── ExamenModel.java             # Entidad JPA
│   │   │   ├── AgendaExamenModel.java       # Entidad JPA
│   │   │   ├── LabExamModel.java            # Entidad JPA
│   │   │   ├── ContactoModel.java           # Entidad JPA
│   │   │   └── DireccionModel.java          # Entidad JPA
│   │   └── dto/
│   │       ├── LaboratorioDTO.java          # DTO laboratorio
│   │       ├── ExamenDTO.java               # DTO examen
│   │       ├── AgendaExamenDTO.java         # DTO agenda
│   │       └── LabExamDTO.java              # DTO relación lab-exam
│   ├── src/main/resources/
│   │   ├── application.properties           # Configuración + Oracle
│   │   └── ojdbc.properties                 # Propiedades Oracle
│   └── pom.xml                              # Dependencias Maven
│
├── ms_gestion_resultados/             # Microservicio Resultados (Puerto 8082)
│   ├── src/main/java/com/gestion_resultados/
│   │   ├── config/
│   │   │   ├── SecurityConfig.java          # permitAll() - Confía en Gateway
│   │   │   └── RestClientConfig.java        # RestTemplate configurado
│   │   ├── controller/
│   │   │   └── ResultadoController.java     # CRUD con filtrado por rol
│   │   ├── service/
│   │   │   ├── ResultadoService.java        # Lógica de resultados
│   │   │   ├── ResultadoServiceImpl.java    # Implementación
│   │   │   └── EnrichmentService.java       # Enriquecimiento de datos
│   │   ├── repository/
│   │   │   └── ResultadoExamenRepository.java # JPA Repository
│   │   ├── model/
│   │   │   └── ResultadoExamenModel.java    # Entidad JPA
│   │   └── dto/
│   │       ├── ResultadoRequest.java        # DTO request
│   │       └── ResultadoResponse.java       # DTO response
│   ├── src/main/resources/
│   │   ├── application.properties           # Configuración + Oracle
│   │   └── ojdbc.properties                 # Propiedades Oracle
│   └── pom.xml                              # Dependencias Maven
│
├── arquetipo_backend/                 # 📦 Arquetipos Reutilizables
│   ├── ms_api_gateway/
│   │   ├── ARQUETIPO_BACKEND.md
│   │   ├── README.md
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── src/
│   ├── ms_gestion_users/
│   │   ├── ARQUETIPO_BACKEND.md
│   │   ├── README.md
│   │   ├── GUIA_IMPLEMENTACION.md
│   │   ├── .env.example
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── src/
│   ├── ms_gestion_labs/
│   │   ├── ARQUETIPO_BACKEND.md
│   │   ├── README.md
│   │   ├── pom.xml
│   │   ├── Dockerfile
│   │   └── src/
│   └── ms_gestion_resultados/         # 🆕 NUEVO
│       ├── ARQUETIPO_BACKEND.md
│       ├── README.md
│       ├── GUIA_IMPLEMENTACION.md
│       ├── .env.example
│       ├── pom.xml
│       ├── Dockerfile
│       └── src/
│
├── arquetipo_frontend/                # Arquetipo Angular
│   ├── ARQUETIPO_FRONTEND.md
│   ├── README.md
│   └── src/
│
├── frontend_gestion_labs/             # Frontend Angular (Puerto 4200)
│   ├── src/app/
│   │   ├── components/
│   │   ├── services/
│   │   ├── guards/
│   │   ├── interceptors/
│   │   └── models/
│   └── angular.json
│
├── base_de_datos/                     # Scripts SQL
│   ├── creacion_tablas.sql
│   └── LIMPIAR_BD_COMPLETO.sql
│
├── wallet/                            # Oracle Wallet (OCI)
│   └── Wallet_databaseFullStack3/
│       ├── cwallet.sso
│       ├── ewallet.p12
│       ├── tnsnames.ora
│       └── ...
│
├── postman/                           # Colecciones Postman
│   ├── collection_sumativa2.json
│   └── environment_FS3.postman_environment.json
│
├── docker-compose.yml                 # Orquestación Docker
├── Dockerfile                         # Imagen base
├── iniciar-app.sh                     # Script de inicio
├── detener-app.sh                     # Script de detención
├── limpiar-rebuild.sh                 # Script de limpieza
├── README.md                          # Este archivo
└── README_DOCKER.md                   # Documentación Docker
```

---

## 🎓 Información Adicional

### Formato de Respuestas

Todas las respuestas siguen el formato estándar:

```json
{
  "code": "000",
  "description": "Mensaje descriptivo",
  "data": { ... }
}
```

**Códigos de respuesta:**
- `000` - Operación exitosa
- `001` - Error genérico
- `002` - Error de validación
- `401` - No autorizado
- `403` - Prohibido
- `404` - No encontrado
- `500` - Error interno del servidor

### Logging

Los logs están configurados en nivel `INFO` para producción:

```properties
logging.level.com.api_gateway=INFO
logging.level.com.gestion_users=INFO
logging.level.com.gestion_labs=INFO
```

**Ejemplo de logs:**
```
15:30:45 INFO  | POST /auth/login - admin@laboratorioandino.cl
15:30:45 INFO  | ✓ GET /users - Usuario: admin@laboratorioandino.cl [ADMIN]
15:35:22 INFO  | ✓ POST /auth/logout - Token invalidado [blacklist: 1]
```

### Seguridad Adicional

- ✅ **Passwords hasheados** con BCrypt (costo 10)
- ✅ **Tokens JWT firmados** con HS512
- ✅ **Wallet Oracle** para conexión SSL/TLS
- ✅ **Validación de datos** con Spring Validation
- ✅ **CORS configurado** para dominios específicos
- ✅ **Blacklist de tokens** en memoria (migrar a Redis en producción)

---

**¿Necesitas ayuda?** Revisa los logs de cada microservicio para diagnóstico de errores.

**Punto de entrada único:** `http://localhost:8080` (API Gateway)

---

## � Análisis de Cobertura con SonarQube

### 🎯 Objetivo: ≥80% de Cobertura

El proyecto incluye análisis de calidad de código y cobertura de tests utilizando:

- **Backend:** JaCoCo para microservicios Spring Boot
- **Frontend:** Karma/Jasmine para Angular
- **Análisis:** SonarQube Community Edition

### 🚀 Inicio Rápido

```bash
# 1. Iniciar aplicación con SonarQube incluido
./iniciar-app.sh

# 2. Esperar a que SonarQube esté listo (2-3 minutos)
# SonarQube estará en: http://localhost:9000

# 3. Ejecutar análisis de cobertura
./analisis-sonarqube.sh
```

### 📊 Acceso a Reportes

- **SonarQube Dashboard:** http://localhost:9000
  - Usuario: `admin`
  - Contraseña: `admin`

- **Reportes Locales JaCoCo:**
  - `ms_gestion_users/target/site/jacoco/index.html`
  - `ms_gestion_labs/target/site/jacoco/index.html`
  - `ms_gestion_resultados/target/site/jacoco/index.html`
  - `ms_api_gateway/target/site/jacoco/index.html`

- **Reporte Angular:**
  - `frontend_gestion_labs/coverage/frontend-gestion-labs/index.html`

### 📚 Documentación Completa

Para instrucciones detalladas sobre:
- Configuración de SonarQube
- Creación de tests unitarios
- Interpretación de métricas
- Comandos avanzados
- Solución de problemas

**Ver:** [README_SONARQUBE.md](README_SONARQUBE.md)

### 🧪 Scripts Disponibles

```bash
# Análisis completo con verificaciones
./analisis-sonarqube.sh
```

### 📈 Métricas Configuradas

- **Líneas de código:** ≥ 80%
- **Ramas:** ≥ 80%
- **Funciones:** ≥ 80%
- **Sentencias:** ≥ 80%

El build fallará si la cobertura está por debajo del objetivo.

---

## 🧪 Tests Unitarios

El proyecto cuenta con **69 tests unitarios** distribuidos entre backend y frontend, todos con 100% de éxito.

### 📊 Resumen de Tests

| Componente | Tests | Herramientas | Cobertura |
|------------|-------|--------------|-----------|
| ms_api_gateway | 13 tests | JUnit 5 + Mockito | JaCoCo |
| ms_gestion_labs | 11 tests | JUnit 5 + Mockito | JaCoCo |
| ms_gestion_users | 11 tests | JUnit 5 + Mockito | JaCoCo |
| ms_gestion_resultados | 9 tests | JUnit 5 + Mockito | JaCoCo |
| **Total Backend** | **44 tests** | **Spring Boot Test** | **≥80%** |
| frontend_gestion_labs | 25 tests | Karma + Jasmine | LCOV |
| **Total General** | **69 tests** | - | **✅** |

### 🔬 Backend Tests (44 tests)

#### 1. MS_API_GATEWAY (13 tests)

**TokenBlacklistServiceTest.java** (7 tests)
- ✅ Agregar token a blacklist
- ✅ Verificar si token está blacklisted
- ✅ Remover token de blacklist
- ✅ Limpiar toda la blacklist
- ✅ Obtener tamaño de blacklist
- ✅ Manejo de tokens nulos/vacíos
- ✅ Thread-safety con múltiples hilos

**JwtPropertiesTest.java** (6 tests)
- ✅ Establecer y obtener secreto JWT
- ✅ Establecer y obtener tiempo de expiración
- ✅ Manejo de valores nulos
- ✅ Validación de diferentes longitudes de secreto
- ✅ Validación de diferentes valores de expiración
- ✅ Propiedades por defecto

#### 2. MS_GESTION_LABS (11 tests)

**ExamenServiceTest.java** (5 tests)
- ✅ Listar todos los exámenes
- ✅ Buscar examen por ID
- ✅ Crear nuevo examen
- ✅ Actualizar examen existente
- ✅ Manejo de examen no encontrado

**LaboratorioServiceTest.java** (6 tests)
- ✅ Crear laboratorio con dirección y contacto
- ✅ Buscar laboratorio por ID
- ✅ Listar todos los laboratorios
- ✅ Actualizar datos de laboratorio
- ✅ Eliminar laboratorio
- ✅ Manejo de laboratorio no encontrado

#### 3. MS_GESTION_USERS (11 tests)

**UserServiceTest.java** (5 tests)
- ✅ Listar todos los usuarios
- ✅ Buscar usuario por ID
- ✅ Crear usuario con password encriptado (BCrypt)
- ✅ Cambiar contraseña
- ✅ Manejo de usuario no encontrado

**AuthServiceTest.java** (6 tests)
- ✅ Login exitoso con generación de JWT
- ✅ Login con usuario no encontrado
- ✅ Login con contraseña incorrecta
- ✅ Cambio de contraseña exitoso
- ✅ Rechazo de cambio con contraseña incorrecta
- ✅ Validación de formato de token JWT

#### 4. MS_GESTION_RESULTADOS (9 tests)

**ResultadoServiceTest.java** (9 tests)
- ✅ Listar todos los resultados
- ✅ Buscar resultado por ID
- ✅ Buscar resultados por paciente
- ✅ Crear nuevo resultado
- ✅ Actualizar resultado
- ✅ Validación de campos obligatorios
- ✅ Eliminación de resultado
- ✅ Manejo de resultado no encontrado
- ✅ Establecimiento automático de fecha al emitir

### 🌐 Frontend Tests (25 tests)

**auth.service.spec.ts** (5 tests)
- ✅ Login exitoso con almacenamiento de sesión
- ✅ Manejo de error 401 (Unauthorized)
- ✅ Logout con limpieza de sesión
- ✅ Verificación de estado de autenticación
- ✅ Registro de paciente

**laboratorio.service.spec.ts** (5 tests)
- ✅ Obtener todos los laboratorios
- ✅ Obtener laboratorio por ID
- ✅ Crear laboratorio
- ✅ Actualizar laboratorio
- ✅ Eliminar laboratorio

**examen.service.spec.ts** (5 tests)
- ✅ Obtener todos los exámenes
- ✅ Obtener examen por ID
- ✅ Crear examen
- ✅ Actualizar examen
- ✅ Eliminar examen

**paciente.service.spec.ts** (5 tests)
- ✅ Obtener todos los pacientes
- ✅ Obtener paciente por ID
- ✅ Crear paciente
- ✅ Actualizar paciente
- ✅ Eliminar paciente

**resultado.service.spec.ts** (5 tests)
- ✅ Obtener todos los resultados
- ✅ Obtener resultado por ID
- ✅ Crear resultado
- ✅ Actualizar resultado
- ✅ Eliminar resultado

### 🚀 Ejecutar Tests

**Backend (individual):**
```bash
cd ms_api_gateway
mvn test                          # Solo tests
mvn clean verify                  # Tests + cobertura JaCoCo
```

**Frontend:**
```bash
cd frontend_gestion_labs
npm test                          # Tests en watch mode
npm test -- --watch=false         # Tests una vez
npm run test:coverage             # Tests + cobertura
```

**Todos los tests + SonarQube:**
```bash
./analisis-sonarqube.sh           # Automatizado
```

### 📚 Documentación Detallada

Para información completa sobre configuración, comandos avanzados y creación de nuevos tests:

- 📘 **[README_TESTS_COBERTURA.md](README_TESTS_COBERTURA.md)** - Guía completa de tests
- 📘 **[ANALISIS_TESTS_SONARQUBE.md](ANALISIS_TESTS_SONARQUBE.md)** - Resultados y análisis

---

## 🔧 Troubleshooting

### ❌ Error: "Archivo .sonar_token no encontrado"

**Problema:** El script `analisis-sonarqube.sh` no encuentra el archivo `.sonar_token`

**Solución:**
```bash
# Regenerar token ejecutando iniciar-app.sh
./iniciar-app.sh
```

El archivo `.sonar_token` se genera automáticamente en el PASO 5 de `iniciar-app.sh`.

---

### ❌ Error: "SonarQube not available after 120 seconds"

**Problema:** SonarQube tarda mucho en iniciar (puede ocurrir en equipos con pocos recursos)

**Soluciones:**

1. **Aumentar tiempo de espera** (editar iniciar-app.sh):
```bash
# Cambiar línea 86
MAX_ATTEMPTS=120  # en lugar de 60
```

2. **Verificar logs de SonarQube:**
```bash
docker logs sonarqube
```

3. **Verificar recursos de Docker:**
```bash
docker stats
```

**Recomendaciones:**
- Mínimo 4GB RAM para Docker
- Cerrar aplicaciones pesadas durante el inicio

---

### ❌ Error: "Tests failing en frontend (RouterLink)"

**Problema:** Tests de Angular fallan con error de RouterLink

**Solución:** Ya corregido en versión actual. Si persiste:
```bash
cd frontend_gestion_labs
rm -rf node_modules package-lock.json
npm install
npm test
```

---

### ❌ Error: "Build falló con código 1"

**Problema:** Errores de compilación en backend

**Solución:**
```bash
# Limpieza profunda y rebuild
./limpiar-rebuild.sh
```

Este script realiza:
- Detiene todos los contenedores
- Limpia caché de frontend (dist, .angular, node_modules/.cache)
- Limpia targets de Java (mvn clean)
- Limpia caché de Docker (docker system prune)
- Reconstruye API Gateway
- Reconstruye todos los contenedores sin caché
- Levanta servicios

---

### ❌ Error: "Port already in use"

**Problema:** Uno de los puertos está ocupado (4200, 8080, 8081, 8082, 8083, 9000)

**Solución 1 - Identificar proceso:**
```bash
# macOS/Linux
lsof -i :8080              # Reemplazar con el puerto problemático
kill -9 <PID>              # Matar proceso

# Detener contenedores existentes
./detener-app.sh
```

**Solución 2 - Cambiar puerto:**
Editar `docker-compose.yml` para cambiar el puerto del servicio afectado.

---

### ❌ Error: "Cannot connect to Oracle Database"

**Problema:** Microservicio no puede conectar a Oracle Autonomous Database

**Verificaciones:**

1. **Wallet configurado correctamente:**
```bash
# Verificar que existe wallet en cada microservicio
ls -la ms_gestion_users/src/main/resources/wallet/
```

2. **Variables de entorno correctas:**
```bash
# Revisar docker-compose.yml
grep -A5 "environment:" docker-compose.yml
```

3. **Credenciales válidas:**
```bash
# Verificar application.properties
cat ms_gestion_users/src/main/resources/application.properties | grep oracle
```

**Documentación completa:** [README_BASE_DE_DATOS.md](README_BASE_DE_DATOS.md)

---

### ❌ Error: "Frontend no carga (ERR_CONNECTION_REFUSED)"

**Problema:** No se puede acceder a http://localhost:4200

**Solución:**
```bash
# Verificar estado de contenedores
docker ps

# Si frontend no está corriendo
docker logs gestion_labs_frontend

# Reiniciar solo frontend
docker-compose restart frontend
```

---

### ❌ Error: "CORS error desde frontend"

**Problema:** Error de CORS al hacer peticiones desde Angular

**Causa:** CORS está configurado en API Gateway (puerto 8080)

**Verificación:**
```bash
# Frontend debe apuntar a API Gateway, no a microservicios directamente
cat frontend_gestion_labs/src/environments/environment.ts

# Debería contener:
# apiUrl: 'http://localhost:8080'
```

---

### 🆘 Comandos Útiles

```bash
# Ver logs de un servicio específico
docker logs gestion_labs_api_gateway -f

# Ver logs de todos los servicios
docker-compose logs -f

# Reiniciar un servicio específico
docker-compose restart api-gateway

# Ver estado de contenedores
docker ps -a

# Entrar a un contenedor
docker exec -it gestion_labs_api_gateway bash

# Verificar red Docker
docker network inspect microservicios_laboratorio_sboot_default

# Limpiar todo Docker (¡CUIDADO! Afecta otros proyectos)
docker system prune -a --volumes
```

---

### 📞 Recursos Adicionales

- 📘 [README_DOCKER.md](README_DOCKER.md) - Configuración de Docker
- 📘 [README_BASE_DE_DATOS.md](README_BASE_DE_DATOS.md) - Base de datos Oracle
- 📘 [README_TESTS_COBERTURA.md](README_TESTS_COBERTURA.md) - Tests y cobertura
- 📘 [ANALISIS_TESTS_SONARQUBE.md](ANALISIS_TESTS_SONARQUBE.md) - SonarQube

---

## 📝 Changelog

### ✨ Última Versión - Automatización y Calidad

#### 🚀 Nuevas Funcionalidades

- ✅ **SonarQube Community Edition integrado**
  - Contenedor Docker con PostgreSQL para persistencia
  - Puerto expuesto: 9000
  - Configuración automática vía API

- ✅ **Generación dinámica de tokens SonarQube**
  - Token generado automáticamente con timestamp único
  - Guardado en archivo `.sonar_token` para reutilización
  - Cambio automático de contraseña (admin → Laboratorios#2025)
  - Creación automática de 4 proyectos backend

- ✅ **Scripts de automatización completos**
  - `iniciar-app.sh`: Inicialización completa (305 líneas, 6 pasos)
  - `analisis-sonarqube.sh`: Tests y cobertura automatizados (343 líneas)
  - `detener-app.sh`: Detención limpia de servicios
  - `limpiar-rebuild.sh`: Limpieza profunda y reconstrucción (7 pasos)

- ✅ **JaCoCo para Backend**
  - Plugin configurado en los 4 microservicios
  - Generación automática de reportes XML/HTML
  - Verificación de cobertura mínima (80%)
  - Integración con SonarQube Maven Plugin

- ✅ **Karma/Jasmine para Frontend**
  - Configuración de cobertura en Angular 18
  - Generación de reportes LCOV
  - Chrome Headless para CI/CD
  - sonar-scanner para JavaScript/TypeScript

#### 🧪 Tests Implementados

- ✅ **Backend:** 44 tests unitarios distribuidos en:
  - ms_api_gateway: 13 tests (TokenBlacklist + JwtProperties)
  - ms_gestion_labs: 11 tests (Exámenes + Laboratorios)
  - ms_gestion_users: 11 tests (Usuarios + Autenticación)
  - ms_gestion_resultados: 9 tests (Resultados + Validaciones)

- ✅ **Frontend:** 25 tests unitarios en:
  - auth.service.spec.ts: 5 tests
  - laboratorio.service.spec.ts: 5 tests
  - examen.service.spec.ts: 5 tests
  - paciente.service.spec.ts: 5 tests
  - resultado.service.spec.ts: 5 tests

- ✅ **Total:** 69 tests con 100% passing

#### 📊 Análisis de Cobertura

- ✅ Reportes automáticos en cada análisis
- ✅ Dashboard visual en SonarQube (http://localhost:9000)
- ✅ Métricas configuradas: líneas, ramas, funciones, sentencias (≥80%)
- ✅ Build falla si cobertura < objetivo

#### 🔧 Configuraciones Técnicas

- **Backend (pom.xml):**
  - jacoco-maven-plugin v0.8.12
  - sonar-maven-plugin v4.0.0.4121
  - Propiedades de SonarQube por microservicio
  - Exclusiones configurables (DTOs, entidades)

- **Frontend:**
  - karma.conf.js con reportes LCOV
  - sonar-project.properties personalizado
  - Scripts npm: `test:coverage`, `sonar`
  - sonarqube-scanner v3.3.0

- **Docker Compose:**
  - Servicio SonarQube (imagen oficial community)
  - PostgreSQL 15 Alpine para persistencia
  - Volúmenes: datos, extensiones, logs
  - Red compartida con microservicios

#### 📚 Documentación Nueva

- ✅ **README_BASE_DE_DATOS.md**: Documentación separada de base de datos
  - 10 tablas con diagramas ASCII
  - Relaciones y cascadas
  - Configuración de wallets Oracle
  - Scripts SQL útiles

- ✅ **README_TESTS_COBERTURA.md**: Guía completa de tests
  - Descripción de 69 tests unitarios
  - Configuración de JaCoCo y Karma
  - Comandos de ejecución
  - Interpretación de reportes

- ✅ **ANALISIS_TESTS_SONARQUBE.md**: Análisis detallado
  - Resultados de tests por microservicio
  - Métricas de SonarQube
  - Problemas y soluciones
  - Mejores prácticas

- ✅ **Sección "Inicio Rápido" en README principal**
  - 3 pasos claros para ejecutar el sistema
  - Explicación de qué hace cada script
  - Enlaces a documentación detallada

- ✅ **Sección "Scripts de Automatización"**
  - Descripción completa de 4 scripts
  - Flujo de trabajo típico
  - Documentación de .sonar_token
  - Casos de uso y troubleshooting

#### 🐛 Correcciones

- ✅ Eliminación de token hardcodeado en iniciar-app.sh
- ✅ Eliminación de secciones duplicadas (PASO 4 y 5)
- ✅ Corrección de tests frontend (RouterLink con createUrlTree y serializeUrl)
- ✅ Logs silenciados en scripts (Maven y npm)
- ✅ Corrección de case-sensitive en project keys (Frontend-Gestion-Labs)
- ✅ Permisos de token elevados a GLOBAL_ANALYSIS_TOKEN

#### 🎯 Mejoras de Experiencia

- ✅ Output limpio en consola (sin logs verbose)
- ✅ Mensajes descriptivos: "Se está generando reporte para: {servicio}"
- ✅ Validación de pre-requisitos (.sonar_token file)
- ✅ Mensajes de éxito/error claros
- ✅ URLs y credenciales mostradas al finalizar

---

### 🆕 Versión 3.0 - Microservicio de Resultados

#### ✨ Nuevas Funcionalidades

- 🎯 **Creación de MS_GESTION_RESULTADOS**: Microservicio dedicado para gestión de resultados
  - Separación de responsabilidades desde MS_GESTION_LABS
  - Puerto asignado: 8082
  - Implementación de filtrado por rol (PATIENT, LAB_EMPLOYEE, ADMIN)
  - Búsqueda avanzada por paciente, laboratorio, examen
  - RestTemplate para enriquecimiento de datos

- 🔧 **Configuración de API Gateway para MS_RESULTADOS**
  - Enrutamiento `/resultados/**` → `http://resultados-service:8082`
  - Propagación de headers: `X-User-Role`, `X-Patient-Id`, `X-Employee-Id`, `X-User-Id`
  - Manejo de peticiones OPTIONS para CORS preflight

- 🐳 **Docker Compose actualizado**
  - Agregado servicio `resultados-service` con puerto 8082
  - Configuración de red compartida entre microservicios
  - Variables de entorno para MS_RESULTADOS

- 🔀 **Eliminación de endpoints de resultados de MS_LABS**
  - Migración completa a MS_GESTION_RESULTADOS
  - Actualización de dependencias entre servicios

#### 📦 Arquetipo MS_GESTION_RESULTADOS

- ✅ Estructura completa del arquetipo en `arquetipo_backend/ms_gestion_resultados/`
- ✅ Código fuente reutilizable con todas las capas (Controller, Service, Repository, Model, DTO)
- ✅ Configuración lista para Oracle Autonomous Database
- ✅ Dockerfile para despliegue en contenedores
- ✅ ARQUETIPO_BACKEND.md con especificación técnica completa
- ✅ README.md con guía rápida de endpoints y configuración
- ✅ GUIA_IMPLEMENTACION.md con implementación paso a paso
- ✅ .env.example con variables de entorno necesarias

#### 🔐 Mejoras en Seguridad

- ✅ Filtro global mejorado para extracción de claims del JWT
- ✅ Propagación automática de userId, pacienteId, empleadoId, role como headers HTTP
- ✅ Validación de roles con `@PreAuthorize` en todos los endpoints

#### 🔄 Cambios Estructurales

- 📊 **Arquitectura actualizada a 4 microservicios**:
  1. MS_API_GATEWAY (8080)
  2. MS_GESTION_USERS (8083) - Cambio de puerto desde 8082
  3. MS_GESTION_LABS (8081)
  4. MS_GESTION_RESULTADOS (8082) - **NUEVO**

- 🗂️ **Reorganización de responsabilidades**:
  - MS_LABS: Laboratorios, exámenes, agendas, lab-exams
  - MS_RESULTADOS: Resultados de exámenes exclusivamente
  - MS_USERS: Usuarios, pacientes, empleados, autenticación

#### 🐛 Correcciones

- ✅ Eliminación de console.log innecesarios en frontend
- ✅ Corrección de CORS duplicado entre Gateway y microservicios
- ✅ Ajuste de rutas en frontend para consumir desde MS_RESULTADOS
- ✅ Cambio de datos mock a datos reales desde endpoints
- ✅ Mejora en nomenclatura de métodos en servicios
- ✅ Deshabilitar caché en configuraciones para desarrollo

#### 📦 Arquetipos Completados

1. ✅ **ms_api_gateway**: Gateway con validación JWT, blacklist, CORS
2. ✅ **ms_gestion_users**: Autenticación, usuarios, pacientes, empleados
3. ✅ **ms_gestion_labs**: Laboratorios, exámenes, agendas, relaciones
4. ✅ **ms_gestion_resultados**: Resultados con filtrado por rol, búsqueda avanzada

#### 🚀 Mejoras de Rendimiento

- ⚡ Desacoplamiento de MS_LABS para mejor escalabilidad
- ⚡ Comunicación entre microservicios mediante RestTemplate
- ⚡ Enriquecimiento de datos bajo demanda (lazy loading)