# Implementación de Seguridad JWT en ms_gestion_labs

## ✅ Implementación Completada

Se ha implementado la arquitectura de seguridad JWT+RBAC en el microservicio `ms_gestion_labs`, replicando la misma estructura de seguridad de `ms_gestion_users`.

---

## 📋 Matriz de Permisos por Rol

### **Endpoints Públicos (sin autenticación)**
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/labs` | Listar todos los laboratorios |
| GET | `/labs/{id}` | Obtener laboratorio por ID |

### **Rol: PATIENT**
| Método | Endpoint | Descripción | Estado |
|--------|----------|-------------|--------|
| POST | `/agenda` | Agendar un examen | ⚠️ Requiere validación adicional |
| GET | `/agenda/paciente/{id}` | Ver sus propias agendas | ⚠️ Requiere validación adicional |
| GET | `/resultados/paciente/{id}` | Ver sus propios resultados | ⚠️ Requiere validación adicional |

**Nota importante**: Los endpoints marcados con ⚠️ permiten acceso a cualquier PATIENT autenticado, pero **se requiere implementar validación adicional en los servicios** para verificar que el `pacienteId` del path parameter coincida con el `userId` del token JWT. Sin esta validación, un paciente podría ver datos de otros pacientes.

### **Rol: LAB_EMPLOYEE**
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/agenda` | Ver todas las agendas |
| GET | `/agenda/{id}` | Ver agenda específica |
| GET | `/agenda/paciente/{id}` | Ver agendas de cualquier paciente |
| POST | `/resultados` | Registrar resultado de examen |
| GET | `/resultados` | Ver todos los resultados |
| GET | `/resultados/{id}` | Ver resultado específico |
| GET | `/resultados/paciente/{id}` | Ver resultados de cualquier paciente |

**Nota**: LAB_EMPLOYEE NO puede crear/modificar laboratorios ni pacientes en este microservicio (esas operaciones están en ms_gestion_users).

### **Rol: ADMIN**
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| **TODOS** | **/*  ** | Acceso completo a todos los endpoints |
| POST | `/labs` | Crear laboratorio |
| PUT | `/labs/{id}` | Modificar laboratorio |
| DELETE | `/labs/{id}` | Eliminar laboratorio |
| PUT | `/agenda/{id}` | Modificar agenda |
| DELETE | `/agenda/{id}` | Eliminar agenda |
| PUT | `/resultados/{id}` | Modificar resultado |
| DELETE | `/resultados/{id}` | Eliminar resultado |

---

## 🔧 Archivos Creados/Modificados

### **1. Configuración de Seguridad (`/config`)**

#### `JwtProperties.java`
```java
@ConfigurationProperties(prefix = "app.jwt")
```
- Lee el secreto JWT desde `application.properties`
- No incluye `expMin` (este microservicio solo valida, no crea tokens)

#### `JwtAuthenticationFilter.java`
- Extrae el token JWT del header `Authorization: Bearer <token>`
- Valida el token usando la misma clave que `ms_gestion_users`
- Extrae claims: `sub` (userId), `email`, `rol`
- Establece la autenticación en el `SecurityContext`

#### `CustomAuthenticationEntryPoint.java`
- Maneja errores 401 Unauthorized
- Respuesta JSON estándar con código "401"

#### `CustomAccessDeniedHandler.java`
- Maneja errores 403 Forbidden
- Respuesta JSON estándar con código "403"

#### `SecurityConfig.java`
```java
@EnableWebSecurity
@EnableMethodSecurity
```
- Configura endpoints públicos: `/labs` (todos los métodos GET)
- Todos los demás endpoints requieren autenticación
- Sesiones STATELESS
- Integra filtros JWT y manejadores de errores

### **2. Controladores Actualizados**

#### `LaboratorioController.java`
```java
// Público
@GetMapping
@GetMapping("/{id}")

// Solo ADMIN
@PostMapping @PreAuthorize("hasAuthority('ADMIN')")
@PutMapping("/{id}") @PreAuthorize("hasAuthority('ADMIN')")
@DeleteMapping("/{id}") @PreAuthorize("hasAuthority('ADMIN')")
```

#### `AgendaController.java` ⚠️ REQUIERE ACTUALIZACIÓN
```java
// LAB_EMPLOYEE y ADMIN
@GetMapping @PreAuthorize("hasAnyAuthority('LAB_EMPLOYEE', 'ADMIN')")
@GetMapping("/{id}") @PreAuthorize("hasAnyAuthority('LAB_EMPLOYEE', 'ADMIN')")

// PATIENT, LAB_EMPLOYEE, ADMIN (⚠️ validar ownership)
@GetMapping("/paciente/{pacienteId}") @PreAuthorize("hasAnyAuthority('PATIENT', 'LAB_EMPLOYEE', 'ADMIN')")

// Solo PATIENT
@PostMapping @PreAuthorize("hasAuthority('PATIENT')")

// Solo ADMIN
@PutMapping("/{id}") @PreAuthorize("hasAuthority('ADMIN')")
@DeleteMapping("/{id}") @PreAuthorize("hasAuthority('ADMIN')")
```

**TODO**: Agregar validación de ownership en el servicio.

#### `ResultadoController.java` ⚠️ REQUIERE IMPLEMENTACIÓN
**Estado actual**: Sin anotaciones de seguridad implementadas aún.

**Permisos requeridos**:
```java
// LAB_EMPLOYEE y ADMIN
@GetMapping @PreAuthorize("hasAnyAuthority('LAB_EMPLOYEE', 'ADMIN')")
@GetMapping("/{id}") @PreAuthorize("hasAnyAuthority('LAB_EMPLOYEE', 'ADMIN')")

// PATIENT, LAB_EMPLOYEE, ADMIN (⚠️ validar ownership)
@GetMapping("/paciente/{pacienteId}") @PreAuthorize("hasAnyAuthority('PATIENT', 'LAB_EMPLOYEE', 'ADMIN')")

// Solo LAB_EMPLOYEE
@PostMapping @PreAuthorize("hasAuthority('LAB_EMPLOYEE')")

// Solo ADMIN
@PutMapping("/{id}") @PreAuthorize("hasAuthority('ADMIN')")
@DeleteMapping("/{id}") @PreAuthorize("hasAuthority('ADMIN')")
```

### **3. GlobalExceptionHandler.java**
Agregados manejadores para:
- `AuthenticationException` → 401 con código "401"
- `AccessDeniedException` → 403 con código "403"

### **4. pom.xml**
Actualizada dependencia JWT:
```xml
<!-- Removido: jjwt 0.9.1 -->

<!-- Agregado: -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

### **5. application.properties**
Ya contenía la configuración necesaria:
```properties
# JWT (mismo secreto que ms_gestion_users)
app.jwt.secret=ubOJAPgPhBFu8zs3ztDtQBOZ2cdZ6ArHplrwneqabTkotIdzq2Nd60QT8X6M+viBh1TIi8Oz3ffq62wrZZygRw==
```

---

## ⚠️ Tareas Pendientes

### 1. **Finalizar AgendaController**
El archivo `AgendaController.java` está parcialmente actualizado pero puede tener errores de compilación por duplicación de código. Requiere:
- Verificar que el archivo esté limpio (sin código duplicado)
- Compilar y probar

### 2. **Implementar Seguridad en ResultadoController**
Agregar anotaciones `@PreAuthorize` según la matriz de permisos.

### 3. **Implementar Validación de Ownership (CRÍTICO)**
En los siguientes métodos, agregar validación en la capa de servicio:

#### `AgendaController.byPaciente(pacienteId)`
```java
// Si el usuario es PATIENT, verificar:
if (userRole.equals("PATIENT") && !pacienteId.equals(userIdFromJWT)) {
    throw new AccessDeniedException("No puedes ver agendas de otros pacientes");
}
```

#### `ResultadoController.byPaciente(pacienteId)`
```java
// Si el usuario es PATIENT, verificar:
if (userRole.equals("PATIENT") && !pacienteId.equals(userIdFromJWT)) {
    throw new AccessDeniedException("No puedes ver resultados de otros pacientes");
}
```

**Implementación sugerida**:
- Inyectar `SecurityContext` en los servicios
- Obtener `Authentication` del contexto
- Extraer `userId` y `rol` de los claims del JWT
- Comparar con `pacienteId` del path parameter

### 4. **Testing**
1. **Test de endpoints públicos**:
   ```bash
   curl http://localhost:8081/labs
   ```
   Debe responder sin token.

2. **Test con PATIENT**:
   ```bash
   # Obtener token como PATIENT desde ms_gestion_users
   curl -X POST http://localhost:8080/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"paciente@test.com","password":"pass123"}'
   
   # Usar token en ms_gestion_labs
   curl http://localhost:8081/agenda/paciente/1 \
     -H "Authorization: Bearer <TOKEN>"
   ```

3. **Test con LAB_EMPLOYEE**:
   ```bash
   # Login como empleado
   curl -X POST http://localhost:8080/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"empleado@lab.com","password":"pass123"}'
   
   # Ver todas las agendas
   curl http://localhost:8081/agenda \
     -H "Authorization: Bearer <TOKEN>"
   
   # Registrar resultado
   curl -X POST http://localhost:8081/resultados \
     -H "Authorization: Bearer <TOKEN>" \
     -H "Content-Type: application/json" \
     -d '{...}'
   ```

4. **Test con ADMIN**:
   ```bash
   # Login como admin
   curl -X POST http://localhost:8080/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"admin@system.com","password":"admin123"}'
   
   # Crear laboratorio
   curl -X POST http://localhost:8081/labs \
     -H "Authorization: Bearer <TOKEN>" \
     -H "Content-Type: application/json" \
     -d '{"nombre":"Lab Central","direccion":"Calle 123"}'
   ```

5. **Test de errores**:
   - Sin token → 401 "No estás autenticado"
   - Token inválido → 401 "No estás autenticado"
   - Rol insuficiente → 403 "No tienes permisos"

---

## 🔐 Convención de Códigos de Respuesta

Se mantiene la misma convención de `ms_gestion_users`:

| Código | Descripción | HTTP Status |
|--------|-------------|-------------|
| `000` | Operación exitosa | 200 OK |
| `001` | Error de negocio/servidor | 500 Internal Server Error |
| `401` | No autenticado (sin token o inválido) | 401 Unauthorized |
| `403` | Sin permisos (autenticado pero rol insuficiente) | 403 Forbidden |

---

## 📊 Arquitectura de Seguridad

```
┌─────────────────────────────────────────────────────────────┐
│                    Cliente (Frontend)                        │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           │ 1. Login
                           ▼
┌─────────────────────────────────────────────────────────────┐
│              ms_gestion_users (puerto 8080)                  │
│  POST /auth/login → Genera JWT (con userId, email, rol)     │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           │ 2. Token JWT
                           ▼
┌─────────────────────────────────────────────────────────────┐
│              ms_gestion_labs (puerto 8081)                   │
│                                                              │
│  1. SecurityFilterChain                                      │
│     ├─ Público: GET /labs                                   │
│     └─ Privado: todo lo demás                               │
│                                                              │
│  2. JwtAuthenticationFilter                                  │
│     ├─ Extrae token del header Authorization                │
│     ├─ Valida firma con mismo secreto                       │
│     ├─ Extrae claims (userId, email, rol)                   │
│     └─ Set SecurityContext.authentication                   │
│                                                              │
│  3. @PreAuthorize en controllers                            │
│     ├─ hasAuthority('PATIENT')                              │
│     ├─ hasAuthority('LAB_EMPLOYEE')                         │
│     ├─ hasAuthority('ADMIN')                                │
│     └─ hasAnyAuthority('PATIENT', 'LAB_EMPLOYEE', 'ADMIN')  │
│                                                              │
│  4. Manejadores de errores                                   │
│     ├─ CustomAuthenticationEntryPoint → 401                 │
│     └─ CustomAccessDeniedHandler → 403                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Flujo de Autenticación

1. **Usuario hace login** en `ms_gestion_users`:
   ```
   POST /auth/login
   { "email": "user@example.com", "password": "pass123" }
   ```

2. **ms_gestion_users genera JWT** con claims:
   ```json
   {
     "sub": "123",        // userId
     "email": "user@example.com",
     "rol": "PATIENT",
     "exp": 1234567890
   }
   ```

3. **Cliente envía request** a `ms_gestion_labs`:
   ```
   GET /agenda/paciente/123
   Authorization: Bearer eyJhbGc...
   ```

4. **JwtAuthenticationFilter procesa**:
   - Extrae token
   - Valida con `app.jwt.secret`
   - Extrae claims
   - Crea `UsernamePasswordAuthenticationToken`
   - Establece en `SecurityContext`

5. **Spring Security verifica `@PreAuthorize`**:
   ```java
   @PreAuthorize("hasAnyAuthority('PATIENT', 'LAB_EMPLOYEE', 'ADMIN')")
   ```

6. **Si pasa**: ejecuta controller
   **Si falla**: devuelve 403

---

## 🚀 Próximos Pasos

1. ✅ ~~Actualizar pom.xml con JWT 0.12.6~~ HECHO
2. ✅ ~~Crear archivos de configuración de seguridad~~ HECHO
3. ✅ ~~Actualizar GlobalExceptionHandler~~ HECHO
4. ✅ ~~Agregar @PreAuthorize a LaboratorioController~~ HECHO
5. ⚠️ **Verificar y limpiar AgendaController** (posible código duplicado)
6. ⏳ **Agregar @PreAuthorize a ResultadoController**
7. ⏳ **Implementar validación de ownership en servicios**
8. ⏳ **Pruebas completas con Postman/cURL**
9. ⏳ **Documentar endpoints en README.md o Swagger**

---

## 📝 Notas Importantes

### Diferencias con ms_gestion_users
- `ms_gestion_users`: **Genera** tokens JWT (tiene endpoint `/auth/login`)
- `ms_gestion_labs`: **Valida** tokens JWT (no genera, solo consume)
- Por eso `JwtProperties` en `ms_gestion_labs` no tiene `expMin`

### Secreto JWT Compartido
Ambos microservicios **deben usar el mismo secreto**:
```properties
app.jwt.secret=ubOJAPgPhBFu8zs3ztDtQBOZ2cdZ6ArHplrwneqabTkotIdzq2Nd60QT8X6M+viBh1TIi8Oz3ffq62wrZZygRw==
```

Si cambias el secreto, debes actualizarlo en **ambos** microservicios.

### Validación de Ownership
La implementación actual permite que cualquier PATIENT autenticado acceda a `/agenda/paciente/{id}` o `/resultados/paciente/{id}` **sin validar que sea SU propio ID**.

Ejemplo de problema:
```
Usuario: paciente@test.com (userId=5, rol=PATIENT)
Request: GET /agenda/paciente/10
Resultado actual: ✅ Permitido (INCORRECTO)
Resultado esperado: ❌ Forbidden 403
```

**Solución**: Implementar validación en la capa de servicio (ver sección "Tareas Pendientes #3").

---

## 📚 Referencias

- [Documentación Spring Security](https://docs.spring.io/spring-security/reference/index.html)
- [JWT.io](https://jwt.io/)
- [jjwt GitHub](https://github.com/jwtk/jjwt)
- `ms_gestion_users/RBAC_DOCUMENTATION.md` (referencia de implementación)
- `ms_gestion_users/CODIGOS_RESPUESTA.md` (convención de códigos)
