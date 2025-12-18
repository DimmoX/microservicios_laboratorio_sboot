# Microservicio de Gestión de Resultados

Microservicio Spring Boot para gestionar resultados de exámenes de laboratorio con control de acceso basado en roles.

## Características

- ✅ CRUD completo de resultados de exámenes
- ✅ Filtrado automático por rol (PATIENT ve solo sus resultados)
- ✅ Búsqueda por paciente, laboratorio y examen
- ✅ Integración con Spring Security
- ✅ Validación de datos
- ✅ Exception handling global

## Tecnologías

- Java 21
- Spring Boot 3.5.7
- Spring Data JPA
- Spring Security
- Oracle Database
- Docker

## Endpoints

| Método | Ruta | Descripción | Roles |
|--------|------|-------------|-------|
| GET | `/resultados` | Listar resultados | PATIENT, LAB_EMPLOYEE, ADMIN |
| GET | `/resultados/{id}` | Obtener por ID | PATIENT, LAB_EMPLOYEE, ADMIN |
| POST | `/resultados` | Crear resultado | LAB_EMPLOYEE, ADMIN |
| PUT | `/resultados/{id}` | Actualizar resultado | LAB_EMPLOYEE, ADMIN |
| DELETE | `/resultados/{id}` | Eliminar resultado | ADMIN |
| GET | `/resultados/paciente/{id}` | Por paciente | LAB_EMPLOYEE, ADMIN |
| GET | `/resultados/lab/{id}` | Por laboratorio | LAB_EMPLOYEE, ADMIN |
| GET | `/resultados/examen/{id}` | Por examen | LAB_EMPLOYEE, ADMIN |

## Configuración

### Variables de entorno
```bash
DB_USER=admin
DB_PASSWORD=your_password
```

### Puerto
```
8082
```

## Ejecución

### Con Docker
```bash
docker compose up -d resultados-service
```

### Con Maven
```bash
mvn spring-boot:run
```

## Estructura del proyecto

```
src/main/java/com/gestion_resultados/
├── controller/          # Endpoints REST
├── service/            # Lógica de negocio
├── repository/         # Acceso a datos
├── model/              # Entidades JPA
├── dto/                # DTOs
├── config/             # Configuración
└── exceptionHandler/   # Manejo de errores
```

## Seguridad

- Las peticiones son autenticadas por el API Gateway
- Los headers `X-User-Role` y `X-Patient-Id` son proporcionados por el Gateway
- Los PATIENT solo pueden ver sus propios resultados
- LAB_EMPLOYEE y ADMIN pueden ver todos los resultados

## Base de datos

### Tabla: resultados_examenes
```sql
CREATE TABLE resultados_examenes (
    id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    paciente_id NUMBER,
    lab_id NUMBER,
    examen_id NUMBER,
    agenda_id NUMBER,
    valor VARCHAR2(500),
    estado VARCHAR2(50),
    observacion VARCHAR2(500),
    fecha_resultado TIMESTAMP,
    FOREIGN KEY (paciente_id) REFERENCES pacientes(id),
    FOREIGN KEY (lab_id) REFERENCES laboratorios(id),
    FOREIGN KEY (examen_id) REFERENCES examenes(id),
    FOREIGN KEY (agenda_id) REFERENCES agendas(id)
);
```

## Ejemplo de uso

### Crear un resultado (LAB_EMPLOYEE o ADMIN)
```bash
curl -X POST http://localhost:8080/resultados \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 1,
    "labId": 1,
    "examenId": 1,
    "agendaId": 1,
    "valor": "13.5 g/dL",
    "estado": "EMITIDO",
    "observacion": "Valores dentro de rango"
  }'
```

### Listar resultados
```bash
# Como PATIENT - Ve solo sus resultados
curl http://localhost:8080/resultados \
  -H "Authorization: Bearer <patient_token>"

# Como LAB_EMPLOYEE o ADMIN - Ve todos
curl http://localhost:8080/resultados \
  -H "Authorization: Bearer <employee_token>"
```

## Notas

- Este microservicio NO valida JWT. Confía en el API Gateway.
- No configures CORS aquí. El Gateway lo maneja.
- Asegúrate de que las tablas existan antes de iniciar.
