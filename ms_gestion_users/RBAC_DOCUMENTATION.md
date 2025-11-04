# 🔐 Documentación de Control de Acceso Basado en Roles (RBAC)

## 📋 Descripción General

El sistema implementa un control de acceso basado en roles (Role-Based Access Control - RBAC) utilizando Spring Security 3.5 con anotaciones `@PreAuthorize` a nivel de método.

> ⚠️ **ACLARACIÓN IMPORTANTE SOBRE ROLES**
> 
> El sistema usa **UN ÚNICO concepto de "rol"** para definir permisos:
> 
> - **Rol del Sistema** (`USERS.role`): Define los **permisos de acceso** a la aplicación
>   - Valores: `ADMIN`, `LAB_EMPLOYEE`, `PATIENT`
>   - Ubicación: Tabla `USERS`, columna `role`
>   - Propósito: Control de acceso y autorización
> 
> - **Cargo Profesional** (`EMPLEADOS.cargo`): Información del empleado (NO afecta permisos)
>   - Valores: Texto libre como "Tecnólogo Médico", "Bioquímico", "Recepcionista"
>   - Ubicación: Tabla `EMPLEADOS`, columna `cargo`
>   - Propósito: Solo información descriptiva
> 
> 📖 **Ver [ARQUITECTURA_ROLES.md](ARQUITECTURA_ROLES.md) para más detalles.**

---

## 👥 Roles del Sistema Definidos

> ⚠️ **IMPORTANTE**: Solo existe `USERS.role` para definir permisos
> - `USERS.role`: ADMIN, LAB_EMPLOYEE, PATIENT (permisos de acceso)
> - `EMPLEADOS.cargo`: "Tecnólogo Médico", "Bioquímico", etc. (solo información, NO afecta permisos)

### 1. **ADMIN** (Administrador)
- **Descripción**: Tiene control total sobre el sistema
- **Valor en DB**: `USERS.role = 'ADMIN'`
- **Relación**: Puede tener `empleado_id` NULL (admin puro) o NOT NULL (admin que también es empleado)
- **Permisos**:
  - ✅ Crear, leer, actualizar y eliminar usuarios
  - ✅ Crear, leer, actualizar y eliminar pacientes
  - ✅ Crear, leer, actualizar y eliminar empleados
  - ✅ Acceso completo a todos los endpoints

### 2. **LAB_EMPLOYEE** (Empleado de Laboratorio)
- **Descripción**: Empleados que trabajan en el laboratorio
- **Valor en DB**: `USERS.role = 'LAB_EMPLOYEE'`
- **Relación**: Debe tener `empleado_id` NOT NULL y `paciente_id` NULL
- **Cargo**: El campo `EMPLEADOS.cargo` almacena información como "Tecnólogo Médico", "Bioquímico", etc. (NO afecta permisos)
- **Permisos**:
  - ✅ Leer usuarios (GET `/users`, GET `/users/{id}`)
  - ✅ Leer pacientes (GET `/pacientes`, GET `/pacientes/{id}`)
  - ✅ Leer empleados (GET `/empleados`, GET `/empleados/{id}`)
  - ✅ Crear y consultar resultados de exámenes
  - ❌ NO puede crear/actualizar/eliminar usuarios, pacientes o empleados

### 3. **PATIENT** (Paciente)
- **Descripción**: Usuarios que son pacientes del laboratorio
- **Valor en DB**: `USERS.role = 'PATIENT'`
- **Relación**: Debe tener `paciente_id` NOT NULL y `empleado_id` NULL
- **Permisos**:
  - ✅ Editar su propio perfil
  - ✅ Consultar sus propios resultados de exámenes
  - ✅ Solicitar exámenes
  - ❌ NO puede acceder a datos de otros pacientes
  - ❌ NO puede crear/actualizar/eliminar otros usuarios

---

## 🛡️ Configuración de Seguridad

### SecurityConfig.java
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    // ...
}
```

**Anotación clave**: `@EnableMethodSecurity(prePostEnabled = true)` habilita el uso de `@PreAuthorize` en los métodos de los controladores.

### Endpoints Públicos (Sin autenticación requerida)
- `POST /auth/login` - Login de usuarios
- `POST /auth/generate-hash` - Generar hash de contraseña

### Endpoints Protegidos (Requieren JWT)
Todos los demás endpoints requieren autenticación JWT válida y permisos según el rol.

---

## 📍 Permisos por Controlador

### RegistroController (`/registro`)

| Método | Endpoint | Roles Permitidos | Descripción |
|--------|----------|------------------|-------------|
| POST | `/registro/paciente` | `ADMIN` | Registrar nuevo paciente con usuario |
| POST | `/registro/empleado` | `ADMIN` | Registrar nuevo empleado con usuario |

**Ejemplo de anotación**:
```java
@PostMapping("/registro/paciente")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Map<String, Object>> registrarPaciente(@RequestBody RegistroPacienteRequest request) { ... }
```

---

### UserController (`/users`)

| Método | Endpoint | Roles Permitidos | Descripción |
|--------|----------|------------------|-------------|
| GET | `/users` | `ADMIN`, `LAB_EMPLOYEE` | Listar todos los usuarios |
| GET | `/users/{id}` | `ADMIN`, `LAB_EMPLOYEE` | Obtener usuario por ID |
| POST | `/users` | `ADMIN` | Crear nuevo usuario |
| PUT | `/users/{id}` | `ADMIN` | Actualizar usuario |
| DELETE | `/users/{id}` | `ADMIN` | Eliminar usuario |

**Ejemplo de anotación**:
```java
@GetMapping
@PreAuthorize("hasAnyRole('ADMIN', 'LAB_EMPLOYEE')")
public ResponseEntity<Map<String, Object>> getAll() { ... }
```

---

### PacienteController (`/pacientes`)

| Método | Endpoint | Roles Permitidos | Descripción |
|--------|----------|------------------|-------------|
| GET | `/pacientes` | `ADMIN`, `LAB_EMPLOYEE` | Listar todos los pacientes |
| GET | `/pacientes/{id}` | `ADMIN`, `LAB_EMPLOYEE` | Obtener paciente por ID |
| POST | `/pacientes` | `ADMIN` | Crear nuevo paciente |
| PUT | `/pacientes/{id}` | `ADMIN` | Actualizar paciente |
| DELETE | `/pacientes/{id}` | `ADMIN` | Eliminar paciente |

**Ejemplo de anotación**:
```java
@PostMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Map<String, Object>> create(@RequestBody PacienteModel paciente) { ... }
```

---

### EmpleadoController (`/empleados`)

| Método | Endpoint | Roles Permitidos | Descripción |
|--------|----------|------------------|-------------|
| GET | `/empleados` | `ADMIN`, `LAB_EMPLOYEE` | Listar todos los empleados |
| GET | `/empleados/{id}` | `ADMIN`, `LAB_EMPLOYEE` | Obtener empleado por ID |
| POST | `/empleados` | `ADMIN` | Crear nuevo empleado |
| PUT | `/empleados/{id}` | `ADMIN` | Actualizar empleado |
| DELETE | `/empleados/{id}` | `ADMIN` | Eliminar empleado |

---

## 🔑 Autenticación JWT

### Estructura del Token
El token JWT incluye:
- `sub`: Nombre de usuario (email)
- `role`: Rol del usuario (`ADMIN`, `LAB_EMPLOYEE`, `PATIENT`)
- `iat`: Fecha de emisión
- `exp`: Fecha de expiración (120 minutos)

### Ejemplo de Claims JWT
```json
{
  "sub": "admin@laboratorioandino.cl",
  "role": "ADMIN",
  "iat": 1609459200,
  "exp": 1609466400
}
```

### JwtAuthenticationFilter
El filtro `JwtAuthenticationFilter` extrae el rol del token y lo asigna como autoridad:
```java
UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
    username, 
    null, 
    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
);
```

**Nota**: Spring Security añade automáticamente el prefijo `ROLE_` a los roles.

---

## 📝 Ejemplos de Uso

### 1. Login como ADMIN
```bash
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@laboratorioandino.cl",
    "password": "admin123"
  }'
```

**Respuesta**:
```json
{
  "code": "000",
  "description": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9..."
  }
}
```

---

### 2. Crear Usuario (Solo ADMIN)
```bash
curl -X POST http://localhost:8082/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -d '{
    "username": "nuevo.usuario@laboratorioandino.cl",
    "password": "$2a$10$jOX1cjWTiVqGXUtD/0AQ4...",
    "role": "LAB_EMPLOYEE",
    "estado": "ACTIVO"
  }'
```

**Respuesta exitosa** (200 OK):
```json
{
  "code": "000",
  "description": "Usuario creado exitosamente",
  "data": {
    "id": 7,
    "username": "nuevo.usuario@laboratorioandino.cl",
    "role": "LAB_EMPLOYEE",
    "estado": "ACTIVO"
  }
}
```

**Respuesta denegada** (403 Forbidden) - Si el usuario no es ADMIN:
```json
{
  "error": "Acceso denegado"
}
```

---

### 3. Listar Usuarios (ADMIN o LAB_EMPLOYEE)
```bash
curl -X GET http://localhost:8082/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

**Respuesta**:
```json
{
  "code": "000",
  "description": "Usuarios obtenidos exitosamente",
  "data": [
    {
      "id": 1,
      "username": "admin@laboratorioandino.cl",
      "role": "ADMIN",
      "estado": "ACTIVO"
    },
    {
      "id": 2,
      "username": "felipe.munoz@laboratorioandino.cl",
      "role": "LAB_EMPLOYEE",
      "estado": "ACTIVO"
    }
  ]
}
```

---

### 4. Intento de Acceso No Autorizado (Paciente intentando crear usuario)
```bash
curl -X POST http://localhost:8082/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token_de_paciente>" \
  -d '{...}'
```

**Respuesta** (403 Forbidden):
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/users"
}
```

---

## 🚨 Manejo de Errores de Autorización

### GlobalExceptionHandler
El `GlobalExceptionHandler` captura excepciones de seguridad:

```java
@ExceptionHandler(AccessDeniedException.class)
public ResponseEntity<Object> handleAccessDenied(AccessDeniedException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Acceso denegado: No tienes permisos para realizar esta acción");
    return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
}
```

---

## 🧪 Testing de Roles

### Crear Usuario de Prueba ADMIN
```sql
INSERT INTO users (id, username, password, role, estado, creado_en)
VALUES (
  seq_users.NEXTVAL,
  'admin@laboratorioandino.cl',
  '$2a$10$jOX1cjWTiVqGXUtD/0AQ4OqZg9Zv8YdXz8YdXz8YdXz8YdXz8YdXz',
  'ADMIN',
  'ACTIVO',
  SYSTIMESTAMP
);
```

### Verificar Roles en Base de Datos
```sql
SELECT id, username, role, estado FROM users;
```

---

## 📊 Matriz de Permisos

| Endpoint | ADMIN | LAB_EMPLOYEE | PATIENT |
|----------|-------|--------------|---------|
| `POST /registro/paciente` | ✅ | ❌ | ❌ |
| `POST /registro/empleado` | ✅ | ❌ | ❌ |
| `POST /users` | ✅ | ❌ | ❌ |
| `GET /users` | ✅ | ✅ | ❌ |
| `PUT /users/{id}` | ✅ | ❌ | ❌ |
| `DELETE /users/{id}` | ✅ | ❌ | ❌ |
| `POST /pacientes` | ✅ | ❌ | ❌ |
| `GET /pacientes` | ✅ | ✅ | ❌ |
| `PUT /pacientes/{id}` | ✅ | ❌ | ⚠️ Solo propio |
| `POST /empleados` | ✅ | ❌ | ❌ |
| `GET /empleados` | ✅ | ✅ | ❌ |
| `POST /auth/login` | ✅ | ✅ | ✅ |

**Leyenda**:
- ✅ Permitido
- ❌ Denegado
- ⚠️ Permitido con restricciones

---

## 🔧 Troubleshooting

### Error: "Access Denied"
**Causa**: El usuario autenticado no tiene el rol requerido.

**Solución**:
1. Verificar el token JWT con [jwt.io](https://jwt.io)
2. Confirmar que el claim `role` contiene el rol correcto
3. Verificar que el usuario tiene el rol asignado en la base de datos

### Error: "Full authentication is required"
**Causa**: No se envió el token JWT en la cabecera `Authorization`.

**Solución**:
Añadir la cabecera:
```
Authorization: Bearer <tu_token_jwt>
```

### Error: "Invalid JWT signature"
**Causa**: El token JWT no es válido o fue modificado.

**Solución**:
1. Obtener un nuevo token usando `POST /auth/login`
2. Verificar que la clave secreta JWT (`app.jwt.secret`) es la misma

---

## 📌 Notas Importantes

1. **Prefijo ROLE_**: Spring Security añade automáticamente el prefijo `ROLE_` a los roles. En la base de datos se guarda `ADMIN`, pero en las anotaciones se usa `hasRole('ADMIN')`.

2. **hasRole vs hasAnyRole**:
   - `hasRole('ADMIN')`: Solo usuarios con rol ADMIN
   - `hasAnyRole('ADMIN', 'LAB_EMPLOYEE')`: Usuarios con rol ADMIN O LAB_EMPLOYEE

3. **Orden de Evaluación**: La seguridad se evalúa ANTES de ejecutar el método del controlador.

4. **Caché de Roles**: Los roles se obtienen del token JWT, no de la base de datos en cada request.

---

## 🚀 Próximas Mejoras

- [ ] Implementar control de acceso a nivel de registro (PATIENT solo puede editar su propio perfil)
- [ ] Añadir roles dinámicos desde base de datos
- [ ] Implementar auditoría de accesos
- [ ] Añadir permisos granulares (e.g., `CREATE_USER`, `DELETE_USER`)

---

## 📚 Referencias

- [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
- [Method Security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html)
- [JWT.io - Debugger](https://jwt.io)

---

**Autor**: Microservicio de Gestión de Usuarios  
**Versión**: 1.0  
**Fecha**: 2024
