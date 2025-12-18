# ARQUETIPO BACKEND - ms_gestion_resultados

Este arquetipo está diseñado para microservicios de gestión de resultados de exámenes de laboratorio en Spring Boot. Incluye estructura base, ejemplos de controladores, servicios, modelos, DTOs, repositorios y configuración recomendada.

## Estructura
- `src/main/java/com/gestion_resultados/`
	- `controller/` - Controladores REST con endpoints CRUD
	- `service/` - Lógica de negocio y servicios
	- `model/` - Entidades JPA
	- `dto/` - Data Transfer Objects
	- `repository/` - Repositorios JPA
	- `exceptionHandler/` - Manejo global de excepciones
	- `config/` - Configuración de Spring Security y CORS
- `src/main/resources/`
	- `application.yml` - Configuración del microservicio
- `Dockerfile` - Imagen Docker multi-stage
- `pom.xml` - Dependencias Maven

## Características principales

### 1. Gestión de Resultados de Exámenes
- **CRUD completo** de resultados de exámenes
- **Filtrado por rol**: 
  - PATIENT: Solo ve sus propios resultados
  - LAB_EMPLOYEE y ADMIN: Ven todos los resultados
- **Búsqueda avanzada**: Por paciente, laboratorio, examen

### 2. Seguridad
- Integración con Spring Security
- Autenticación mediante JWT (validado por API Gateway)
- Control de acceso basado en roles (RBAC)
- Headers personalizados del Gateway: `X-User-Role`, `X-Patient-Id`

### 3. Arquitectura
- Patrón Repository-Service-Controller
- DTOs para separar modelo de datos de API
- Exception handling centralizado
- Validaciones con Jakarta Validation

## Endpoints principales

### Resultados de Exámenes
```
GET    /resultados              - Listar resultados (filtrado por rol)
GET    /resultados/{id}         - Obtener resultado por ID
POST   /resultados              - Crear nuevo resultado (LAB_EMPLOYEE, ADMIN)
PUT    /resultados/{id}         - Actualizar resultado (LAB_EMPLOYEE, ADMIN)
DELETE /resultados/{id}         - Eliminar resultado (ADMIN)
```

### Búsquedas específicas
```
GET    /resultados/paciente/{id}    - Resultados por paciente
GET    /resultados/lab/{id}         - Resultados por laboratorio
GET    /resultados/examen/{id}      - Resultados por tipo de examen
```

## Dependencias principales
- Spring Boot 3.5.7
- Spring Data JPA
- Spring Security
- Oracle JDBC Driver
- Jakarta Validation
- Lombok

## Configuración recomendada

### application.yml
```yaml
server:
  port: 8082

spring:
  application:
    name: ms-gestion-resultados
  datasource:
    url: jdbc:oracle:thin:@<HOST>:<PORT>/<SERVICE>
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false
```

### Variables de entorno
- `DB_USER`: Usuario de base de datos
- `DB_PASSWORD`: Contraseña de base de datos

## Modelo de datos

### ResultadoExamenModel
```java
@Entity
@Table(name = "resultados_examenes")
public class ResultadoExamenModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "paciente_id")
    private Long pacienteId;
    
    @Column(name = "lab_id")
    private Long labId;
    
    @Column(name = "examen_id")
    private Long examenId;
    
    @Column(name = "agenda_id")
    private Long agendaId;
    
    private String valor;
    private String estado;
    private String observacion;
    
    @Column(name = "fecha_resultado")
    private OffsetDateTime fechaResultado;
}
```

## Seguridad y filtrado por rol

### En el Controller
```java
@GetMapping
@PreAuthorize("hasAnyRole('PATIENT', 'LAB_EMPLOYEE', 'ADMIN')")
public ResponseEntity<Map<String, Object>> getAllResults(
        @RequestHeader(value = "X-User-Role", required = false) String userRole,
        @RequestHeader(value = "X-Patient-Id", required = false) String patientIdStr) {
    
    List<ResultadoExamenModel> resultados;
    
    if ("ROLE_PATIENT".equals(userRole) && patientIdStr != null) {
        Long pacienteId = Long.parseLong(patientIdStr);
        resultados = service.findByPaciente(pacienteId);
    } else {
        resultados = service.findAll();
    }
    
    return ResponseEntity.ok(response);
}
```

## Ejemplo de uso

1. **Copiar la estructura** a tu nuevo microservicio
2. **Adaptar los modelos** según tus entidades de negocio
3. **Configurar application.yml** con tus credenciales
4. **Ajustar los endpoints** según tus necesidades
5. **Implementar validaciones** específicas de tu dominio

## Mejores prácticas aplicadas

- ✅ Separación de responsabilidades (Controller-Service-Repository)
- ✅ DTOs para APIs REST
- ✅ Validación de entrada con Jakarta Validation
- ✅ Manejo de excepciones centralizado
- ✅ Logging estructurado con SLF4J
- ✅ Control de acceso basado en roles
- ✅ Filtrado de datos según permisos del usuario
- ✅ Responses estandarizados (code, description, data)

## Integración con otros microservicios

Este microservicio se integra con:
- **API Gateway** (puerto 8080): Enrutamiento y autenticación
- **ms_gestion_users** (puerto 8083): Información de usuarios y pacientes
- **ms_gestion_labs** (puerto 8081): Información de laboratorios y exámenes

## Notas importantes

⚠️ **Seguridad**: Este microservicio NO valida JWT directamente. Confía en los headers agregados por el API Gateway (`X-User-Role`, `X-Patient-Id`).

⚠️ **CORS**: No configures CORS en este microservicio. El API Gateway maneja toda la configuración CORS.

⚠️ **Base de datos**: Asegúrate de que las tablas existan antes de iniciar el servicio (`ddl-auto: none`).
