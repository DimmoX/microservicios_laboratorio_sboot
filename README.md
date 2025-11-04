# 🏥 Sistema de Gestión de Laboratorios Clínicos

Sistema de gestión integral para laboratorios clínicos desarrollado con arquitectura de microservicios, implementando autenticación JWT centralizada y conexión a base de datos Oracle Autonomous Database en Oracle Cloud Infrastructure (OCI).

---

## 📋 Tabla de Contenidos

- [Descripción del Proyecto](#-descripción-del-proyecto)
- [Arquitectura de Microservicios](#-arquitectura-de-microservicios)
- [Tecnologías y Dependencias](#-tecnologías-y-dependencias)
- [Seguridad y Autenticación](#-seguridad-y-autenticación)
  - [Spring Boot Security - Implementación](#️-spring-boot-security---implementación-en-el-proyecto)
- [Base de Datos](#-base-de-datos)
- [Configuración de Conexión](#-configuración-de-conexión)
- [Endpoints de la API](#-endpoints-de-la-api)
- [Ejemplos de Uso](#-ejemplos-de-uso)
- [Ejecución del Proyecto](#-ejecución-del-proyecto)
- [Estructura del Proyecto](#-estructura-del-proyecto)

---

## 🎯 Descripción del Proyecto

El **Sistema de Gestión de Laboratorios Clínicos** permite administrar:

- ✅ **Usuarios**: Gestión de credenciales y roles (ADMIN, EMPLEADO, PACIENTE)
- ✅ **Pacientes**: Registro completo con datos personales, dirección y contacto
- ✅ **Empleados**: Gestión de personal del laboratorio con cargos y datos de contacto
- ✅ **Laboratorios**: Administración de múltiples laboratorios con ubicaciones
- ✅ **Exámenes**: Catálogo de exámenes médicos disponibles
- ✅ **Agendas**: Programación de citas para exámenes médicos
- ✅ **Resultados**: Registro y consulta de resultados de exámenes

### Características Principales

- 🔐 **Autenticación JWT centralizada** en API Gateway
- 🛡️ **Spring Boot Security implementado** en todos los microservicios
- 🔑 **BCrypt para hash de contraseñas** (costo 10)
- 🚦 **Control de acceso basado en roles** (RBAC con @PreAuthorize)
- 🌐 **Arquitectura de microservicios** escalable
- ☁️ **Base de datos en la nube** (Oracle Autonomous Database)
- 🔄 **Operaciones en cascada** automáticas
- 📝 **Validación de datos** completa
- 🚀 **CORS habilitado** para aplicaciones frontend

---

## 🏗️ Arquitectura de Microservicios

El sistema está compuesto por **3 microservicios independientes**:

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
│  └──────────────────────────────────────────────────────┘   │
└───────────┬──────────────────────────────┬──────────────────┘
            │                              │
            ▼                              ▼
┌───────────────────────────┐  ┌──────────────────────────────┐
│  👥 MS_GESTION_USERS      │  │  🧪 MS_GESTION_LABS          │
│     (Puerto 8082)         │  │     (Puerto 8081)            │
│                           │  │                              │
│  • Usuarios               │  │  • Laboratorios              │
│  • Pacientes              │  │  • Exámenes                  │
│  • Empleados              │  │  • Agendas                   │
│  • Autenticación          │  │  • Resultados                │
│  • Registro completo      │  │  • Lab-Exams (relaciones)    │
└───────────┬───────────────┘  └──────────────┬───────────────┘
            │                                 │
            └────────────┬────────────────────┘
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
- ✅ **BCryptPasswordEncoder**: Hash seguro de contraseñas (costo 10)
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
```java
@Service
public class TokenBlacklistService {
    private final Set<String> blacklistedTokens = 
        ConcurrentHashMap.newKeySet(); // Thread-safe
    
    public void blacklistToken(String token) { ... }
    public boolean isBlacklisted(String token) { ... }
}
```

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

### Oracle Autonomous Database (OCI)

**Configuración:**
- **Tipo:** Oracle Autonomous Database (19c)
- **Ubicación:** Oracle Cloud Infrastructure (OCI)
- **Conexión:** Mediante Oracle Wallet (SSL/TLS)
- **Pool de conexiones:** Oracle UCP (Universal Connection Pool)

### Estructura de Tablas

El sistema cuenta con **10 tablas relacionadas**:

```sql
┌─────────────────┐
│   CONTACTOS     │
│─────────────────│
│ id (PK)         │
│ fono1           │
│ fono2           │
│ email           │
└─────────────────┘
        ▲
        │
        ├──────────────────┬──────────────────┬────────────────┐
        │                  │                  │                │
┌───────┴────────┐  ┌──────┴───────┐  ┌──────┴───────┐  ┌────┴──────────┐
│   PACIENTES    │  │  EMPLEADOS   │  │ LABORATORIOS │  │  DIRECCIONES  │
│────────────────│  │──────────────│  │──────────────│  │───────────────│
│ id (PK)        │  │ id (PK)      │  │ id (PK)      │  │ id (PK)       │
│ pnombre        │  │ pnombre      │  │ nombre       │  │ calle         │
│ snombre        │  │ snombre      │  │ tipo         │  │ numero        │
│ papellido      │  │ papellido    │  │ dir_id (FK)  │  │ ciudad        │
│ sapellido      │  │ sapellido    │  │ contacto_id  │  │ comuna        │
│ rut            │  │ rut          │  └──────────────┘  │ region        │
│ dir_id (FK)    │  │ cargo        │                    └───────────────┘
│ contacto_id    │  │ dir_id (FK)  │
│ creado_en      │  │ contacto_id  │
└───┬────────────┘  │ creado_en    │
    │               └───┬──────────┘
    │                   │
    │ ┌─────────────────┴──────────────────┐
    │ │            USERS                   │
    │ │────────────────────────────────────│
    │ │ id (PK)                            │
    │ │ username (email único)             │
    │ │ password (BCrypt hash)             │
    │ │ role (ADMIN, EMPLEADO, PACIENTE)   │
    │ │ estado (ACTIVO, INACTIVO)          │
    │ │ paciente_id (FK, nullable)         │
    │ │ empleado_id (FK, nullable)         │
    │ │ creado_en                          │
    │ └────────────────────────────────────┘
    │
    ├────────────────┬────────────────┐
    ▼                ▼                ▼
┌──────────────┐ ┌─────────┐  ┌───────────────┐
│ EXAMENES     │ │ LAB_EXAM│  │ AGENDA_EXAMEN │
│──────────────│ │─────────│  │───────────────│
│ id (PK)      │ │ id (PK) │  │ id (PK)       │
│ codigo       │ │ lab_id  │  │ paciente_id   │
│ nombre       │ │ exam_id │  │ empleado_id   │
│ tipo         │ └─────────┘  │ examen_id     │
└──────────────┘              │ fecha         │
                              │ estado        │
                              │ creado_en     │
                              └───────┬───────┘
                                      │
                                      ▼
                              ┌───────────────────┐
                              │ RESULTADO_EXAMEN  │
                              │───────────────────│
                              │ id (PK)           │
                              │ agenda_id (FK)    │
                              │ resultado (TEXT)  │
                              │ observaciones     │
                              │ estado            │
                              │ creado_en         │
                              └───────────────────┘
```

### Relaciones y Cascadas

#### Eliminación en Cascada Automática

**Al eliminar un PACIENTE:**
```
PACIENTE (eliminado)
  ├── USERS (eliminado automáticamente)
  ├── AGENDA_EXAMEN (eliminadas automáticamente)
  │   └── RESULTADO_EXAMEN (eliminados automáticamente)
  ├── DIRECCIONES (eliminada automáticamente)
  └── CONTACTOS (eliminado automáticamente)
```

**Al eliminar un EMPLEADO:**
```
EMPLEADO (eliminado)
  ├── USERS (eliminado automáticamente)
  ├── DIRECCIONES (eliminada automáticamente)
  ├── CONTACTOS (eliminado automáticamente)
  ├── AGENDA_EXAMEN.empleado_id → NULL (conserva historial)
  └── RESULTADO_EXAMEN.empleado_id → NULL (conserva historial)
```

### Secuencias Oracle

```sql
-- 10 secuencias para auto-incremento de PKs
CREATE SEQUENCE seq_contactos        START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_direcciones      START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_laboratorios     START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_pacientes        START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_empleados        START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_examenes         START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_lab_exam         START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_agenda_examen    START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_resultado_examen START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_users            START WITH 1 INCREMENT BY 1 CACHE 100;
```

---

## ⚙️ Configuración de Conexión

### Wallet de Oracle (OCI)

El proyecto utiliza **Oracle Wallet** para conexión segura a la base de datos en la nube.

**Ubicación del Wallet:**
```
/wallet/Wallet_databaseFullStack3/
├── cwallet.sso
├── ewallet.p12
├── ewallet.pem
├── keystore.jks
├── ojdbc.properties
├── README
├── sqlnet.ora
├── tnsnames.ora
└── truststore.jks
```

### Configuración de `application.properties`

#### MS_GESTION_USERS (8082)
```properties
# Puerto
server.port=8082

# Base de datos Oracle
spring.datasource.url=jdbc:oracle:thin:@databasefullstack3_high?TNS_ADMIN=/ruta/al/wallet/Wallet_databaseFullStack3
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect
spring.jpa.properties.hibernate.format_sql=true

# JWT
jwt.secret=tu_secreto_super_seguro_de_minimo_512_bits
jwt.expiration=7200000

# Logging
logging.level.com.gestion_users=INFO
```

#### MS_GESTION_LABS (8081)
```properties
# Puerto
server.port=8081

# Base de datos Oracle (misma configuración que ms_gestion_users)
spring.datasource.url=jdbc:oracle:thin:@databasefullstack3_high?TNS_ADMIN=/ruta/al/wallet/Wallet_databaseFullStack3
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect

# Logging
logging.level.com.gestion_labs=INFO
```

#### MS_API_GATEWAY (8080)
```properties
# Puerto
server.port=8080

# JWT (mismo secreto que ms_gestion_users)
jwt.secret=tu_secreto_super_seguro_de_minimo_512_bits
jwt.expiration=7200000

# Servicios (URLs de los microservicios)
app.services.users=http://localhost:8082
app.services.labs=http://localhost:8081

# CORS
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedOrigins=*
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedMethods=*
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedHeaders=*

# Logging
logging.level.com.api_gateway=INFO
logging.level.org.springframework.cloud.gateway=WARN
logging.pattern.console=%d{HH:mm:ss} %-5level | %msg%n
```

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

#### 📊 Resultados

| Método | Endpoint | Descripción | Rol Requerido |
|--------|----------|-------------|---------------|
| GET | `/results` | Listar resultados | Cualquier autenticado |
| GET | `/results/{id}` | Ver resultado | Cualquier autenticado |
| POST | `/results` | Crear resultado | ADMIN, EMPLEADO |
| PUT | `/results/{id}` | Actualizar resultado | ADMIN, EMPLEADO |
| DELETE | `/results/{id}` | Eliminar resultado | ADMIN |

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

### Prerrequisitos

- ✅ **Java 21** instalado
- ✅ **Maven 3.9+** instalado
- ✅ **Oracle Wallet** configurado en `/wallet/Wallet_databaseFullStack3/`
- ✅ **Base de datos Oracle** creada y accesible

### Paso 1: Configurar Base de Datos

Ejecuta el script de creación de tablas:

```bash
# Conectar a Oracle SQL Developer o SQLcl
sql usuario/password@databasefullstack3_high

# Ejecutar script
@creacion_tablas_sumativa1_fs3.sql
```

### Paso 2: Crear Usuario Administrador

```bash
# Ejecutar script de creación de usuario admin
@crear_usuario_admin.sql
```

**Usuario creado:**
- Email: `admin@laboratorioandino.cl`
- Password: `Admin123`
- Role: `ADMIN`

### Paso 3: Configurar Wallets y Properties

Actualiza en cada microservicio el archivo `application.properties`:

```properties
# Actualizar ruta al wallet
spring.datasource.url=jdbc:oracle:thin:@databasefullstack3_high?TNS_ADMIN=/ruta/completa/al/wallet/Wallet_databaseFullStack3

# Actualizar credenciales
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD

# Actualizar secreto JWT (mismo en Gateway y ms_gestion_users)
jwt.secret=tu_secreto_super_seguro_de_minimo_512_bits
```

### Paso 4: Compilar Microservicios

```bash
# Compilar ms_gestion_users
cd ms_gestion_users
mvn clean install -DskipTests

# Compilar ms_gestion_labs
cd ../ms_gestion_labs
mvn clean install -DskipTests

# Compilar ms_api_gateway
cd ../ms_api_gateway
mvn clean install -DskipTests
```

### Paso 5: Ejecutar Microservicios

**Opción A: Usando Maven (Desarrollo)**

```bash
# Terminal 1 - MS Gestión Users (8082)
cd ms_gestion_users
mvn spring-boot:run

# Terminal 2 - MS Gestión Labs (8081)
cd ms_gestion_labs
mvn spring-boot:run

# Terminal 3 - API Gateway (8080)
cd ms_api_gateway
mvn spring-boot:run
```

**Opción B: Usando JAR (Producción)**

```bash
# Terminal 1 - MS Gestión Users (8082)
java -jar ms_gestion_users/target/ms_gestion_users-0.0.1-SNAPSHOT.jar

# Terminal 2 - MS Gestión Labs (8081)
java -jar ms_gestion_labs/target/ms_gestion_labs-0.0.1-SNAPSHOT.jar

# Terminal 3 - API Gateway (8080)
java -jar ms_api_gateway/target/ms_api_gateway-0.0.1-SNAPSHOT.jar
```

### Paso 6: Verificar Ejecución

**Verificar que cada microservicio esté corriendo:**

```bash
# MS Gestión Users
curl http://localhost:8082/actuator/health

# MS Gestión Labs
curl http://localhost:8081/actuator/health

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
│   │   │   └── JwtGlobalFilter.java         # Validación JWT centralizada
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
│   │   │   ├── ResultadoController.java     # CRUD resultados
│   │   │   └── LabExamController.java       # CRUD relaciones lab-exam
│   │   ├── service/
│   │   │   ├── LaboratorioService.java      # Lógica laboratorios
│   │   │   ├── ExamenService.java           # Lógica exámenes
│   │   │   ├── AgendaService.java           # Lógica agendas
│   │   │   ├── ResultadoService.java        # Lógica resultados
│   │   │   └── LabExamService.java          # Lógica relaciones
│   │   ├── repository/
│   │   │   ├── LaboratorioRepository.java   # JPA Repository
│   │   │   ├── ExamenRepository.java        # JPA Repository
│   │   │   ├── AgendaRepository.java        # JPA Repository
│   │   │   ├── ResultadoRepository.java     # JPA Repository
│   │   │   └── LabExamRepository.java       # JPA Repository
│   │   ├── model/
│   │   │   ├── LaboratorioModel.java        # Entidad JPA
│   │   │   ├── ExamenModel.java             # Entidad JPA
│   │   │   ├── AgendaExamenModel.java       # Entidad JPA
│   │   │   ├── ResultadoExamenModel.java    # Entidad JPA
│   │   │   ├── LabExamModel.java            # Entidad JPA
│   │   │   ├── ContactoModel.java           # Entidad JPA
│   │   │   └── DireccionModel.java          # Entidad JPA
│   │   └── dto/
│   │       ├── LaboratorioDTO.java          # DTO laboratorio
│   │       ├── ExamenDTO.java               # DTO examen
│   │       ├── AgendaExamenDTO.java         # DTO agenda
│   │       └── ResultadoExamenDTO.java      # DTO resultado
│   ├── src/main/resources/
│   │   ├── application.properties           # Configuración + Oracle
│   │   └── ojdbc.properties                 # Propiedades Oracle
│   └── pom.xml                              # Dependencias Maven
│
├── wallet/                            # Oracle Wallet (OCI)
│   └── Wallet_databaseFullStack3/
│       ├── cwallet.sso
│       ├── ewallet.p12
│       ├── tnsnames.ora
│       └── ...
│
├── creacion_tablas_sumativa1_fs3.sql # Script SQL de creación de BD
├── crear_usuario_admin.sql           # Script de usuario admin
├── LIMPIAR_BD_COMPLETO.sql          # Script para limpiar BD
└── README.md                         # Este archivo
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
