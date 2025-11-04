# Validación de Ownership (Propiedad de Datos) - ms_gestion_labs

## ✅ Implementación Completada

Se ha implementado la **validación de ownership** en los servicios de `ms_gestion_labs` para garantizar que los usuarios con rol `PATIENT` solo puedan acceder y modificar sus propios datos.

---

## 🔒 ¿Qué es la Validación de Ownership?

**Ownership Validation** es una capa adicional de seguridad que verifica que un usuario solo pueda acceder a recursos que le pertenecen, más allá de la validación de roles.

### Diferencia con @PreAuthorize:

| Aspecto | @PreAuthorize | Ownership Validation |
|---------|---------------|---------------------|
| **Qué valida** | Rol del usuario | ID del recurso |
| **Dónde se aplica** | Controlador | Servicio |
| **Ejemplo** | ¿Tiene rol PATIENT? | ¿El pacienteId es SU propio ID? |
| **Protege contra** | Usuarios no autenticados | Pacientes viendo datos de otros pacientes |

---

## 🎯 Escenarios Protegidos

### Caso 1: Ver Agendas de Otro Paciente

**Sin validación de ownership** (❌ VULNERABLE):
```bash
# Usuario: paciente@test.com (userId=5, rol=PATIENT)
GET /agenda/paciente/10
Authorization: Bearer <token-userId-5>

# Resultado: ✅ 200 OK (INCORRECTO - puede ver agendas del paciente 10)
```

**Con validación de ownership** (✅ SEGURO):
```bash
# Usuario: paciente@test.com (userId=5, rol=PATIENT)
GET /agenda/paciente/10
Authorization: Bearer <token-userId-5>

# Resultado: ❌ 403 Forbidden
{
  "code": "403",
  "description": "No tienes permiso para ver agendas de otros pacientes",
  "data": null
}
```

### Caso 2: Crear Agenda para Otro Paciente

**Sin validación** (❌ VULNERABLE):
```bash
POST /agenda
Authorization: Bearer <token-userId-5>
{
  "pacienteId": 10,  // ← ID de otro paciente!
  "examenId": 1,
  "fechaHora": "2025-11-10T10:00:00Z"
}

# Resultado: ✅ 201 Created (INCORRECTO)
```

**Con validación** (✅ SEGURO):
```bash
POST /agenda
Authorization: Bearer <token-userId-5>
{
  "pacienteId": 10,
  "examenId": 1,
  "fechaHora": "2025-11-10T10:00:00Z"
}

# Resultado: ❌ 403 Forbidden
{
  "code": "403",
  "description": "No puedes crear agendas para otros pacientes",
  "data": null
}
```

---

## 📝 Implementación Técnica

### 1. AgendaServiceImpl - Método `findByPaciente()`

```java
@Override 
public List<AgendaExamenModel> findByPaciente(Long pacienteId) {
    // 1. Obtener información del usuario autenticado
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    
    if (auth != null && auth.isAuthenticated()) {
        // 2. Extraer el rol
        String rol = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElse("");
        
        // 3. Validar ownership solo para PATIENT
        if ("PATIENT".equals(rol)) {
            String userId = auth.getName(); // El "sub" del JWT
            
            if (!userId.equals(pacienteId.toString())) {
                throw new AccessDeniedException(
                    "No tienes permiso para ver agendas de otros pacientes"
                );
            }
        }
        // LAB_EMPLOYEE y ADMIN pueden ver cualquier paciente
    }
    
    return repo.findByPacienteId(pacienteId);
}
```

**Flujo de validación**:
1. ✅ Extrae `Authentication` del `SecurityContext`
2. ✅ Obtiene el rol del usuario autenticado
3. ✅ Si es `PATIENT`, compara `userId` (del JWT) con `pacienteId` (del path)
4. ✅ Si no coinciden → lanza `AccessDeniedException` (403)
5. ✅ Si es `LAB_EMPLOYEE` o `ADMIN` → permite acceso sin restricción

### 2. AgendaServiceImpl - Método `create()`

```java
@Override 
public AgendaExamenModel create(AgendaExamenModel a) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    
    if (auth != null && auth.isAuthenticated()) {
        String rol = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElse("");
        
        if ("PATIENT".equals(rol)) {
            String userId = auth.getName();
            
            // Validar que el pacienteId en el body coincida con el userId
            if (a.getPacienteId() != null && !userId.equals(a.getPacienteId().toString())) {
                throw new AccessDeniedException(
                    "No puedes crear agendas para otros pacientes"
                );
            }
            
            // Si no viene pacienteId, asignarlo automáticamente
            if (a.getPacienteId() == null) {
                a.setPacienteId(Long.parseLong(userId));
            }
        }
    }
    
    return repo.save(a); 
}
```

**Características**:
- ✅ Valida que `pacienteId` en el body sea el mismo del usuario
- ✅ Si `pacienteId` es `null`, lo asigna automáticamente (comodidad para el frontend)
- ✅ Lanza excepción si intenta crear agenda para otro paciente

### 3. ResultadoServiceImpl - Método `findByPaciente()`

```java
@Override 
public List<ResultadoExamenModel> findByPaciente(Long pacienteId) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    
    if (auth != null && auth.isAuthenticated()) {
        String rol = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElse("");
        
        if ("PATIENT".equals(rol)) {
            String userId = auth.getName();
            
            if (!userId.equals(pacienteId.toString())) {
                throw new AccessDeniedException(
                    "No tienes permiso para ver resultados de otros pacientes"
                );
            }
        }
    }
    
    return repo.findByPacienteId(pacienteId);
}
```

**Idéntico comportamiento** al de `AgendaService`.

---

## 📊 Matriz de Acceso con Ownership

### Endpoint: `GET /agenda/paciente/{pacienteId}`

| Usuario | Rol | pacienteId solicitado | userId del JWT | ¿Permitido? | Resultado |
|---------|-----|----------------------|----------------|-------------|-----------|
| Juan | PATIENT | 5 | 5 | ✅ SÍ | 200 OK - Sus agendas |
| Juan | PATIENT | 10 | 5 | ❌ NO | 403 Forbidden |
| Dr. López | LAB_EMPLOYEE | 5 | 20 | ✅ SÍ | 200 OK - Cualquier paciente |
| Dr. López | LAB_EMPLOYEE | 10 | 20 | ✅ SÍ | 200 OK - Cualquier paciente |
| Admin | ADMIN | 5 | 1 | ✅ SÍ | 200 OK - Cualquier paciente |
| Admin | ADMIN | 10 | 1 | ✅ SÍ | 200 OK - Cualquier paciente |

### Endpoint: `POST /agenda`

| Usuario | Rol | pacienteId en body | userId del JWT | ¿Permitido? | Resultado |
|---------|-----|-------------------|----------------|-------------|-----------|
| Juan | PATIENT | 5 | 5 | ✅ SÍ | 201 Created |
| Juan | PATIENT | 10 | 5 | ❌ NO | 403 Forbidden |
| Juan | PATIENT | null | 5 | ✅ SÍ | 201 Created (auto-asigna pacienteId=5) |

---

## 🔐 Capas de Seguridad Implementadas

```
┌─────────────────────────────────────────────────────────────┐
│                    Request del Cliente                       │
│  GET /agenda/paciente/10                                     │
│  Authorization: Bearer eyJhbGc... (userId=5, rol=PATIENT)    │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│  CAPA 1: JwtAuthenticationFilter                            │
│  ✅ Valida token JWT                                        │
│  ✅ Extrae userId, email, rol                               │
│  ✅ Establece SecurityContext                               │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│  CAPA 2: @PreAuthorize (Controller)                         │
│  ✅ Verifica rol: PATIENT, LAB_EMPLOYEE, ADMIN              │
│  ✅ Si no tiene rol → 403 Forbidden                         │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│  CAPA 3: Ownership Validation (Service)                     │
│  ✅ Si es PATIENT: verifica userId == pacienteId            │
│  ✅ Si no coincide → 403 Forbidden                          │
│  ✅ Si es LAB_EMPLOYEE o ADMIN → permite acceso             │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│  CAPA 4: Repository (Acceso a BD)                           │
│  ✅ Ejecuta query con pacienteId validado                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 🧪 Ejemplos de Pruebas

### Test 1: PATIENT ve sus propias agendas ✅

```bash
# 1. Login como paciente
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan@paciente.com",
    "password": "pass123"
  }'

# Respuesta:
{
  "code": "000",
  "description": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",  # userId=5, rol=PATIENT
    "userId": 5,
    "email": "juan@paciente.com",
    "rol": "PATIENT"
  }
}

# 2. Ver SUS agendas (pacienteId=5 == userId=5)
curl http://localhost:8081/agenda/paciente/5 \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."

# Respuesta: ✅ 200 OK
{
  "code": "000",
  "description": "Agendas de exámenes del paciente obtenidas exitosamente",
  "data": [
    {
      "id": 1,
      "pacienteId": 5,
      "examenId": 1,
      "fechaHora": "2025-11-10T10:00:00Z"
    }
  ]
}
```

### Test 2: PATIENT intenta ver agendas de otro paciente ❌

```bash
# Mismo token de Juan (userId=5)
curl http://localhost:8081/agenda/paciente/10 \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."

# Respuesta: ❌ 403 Forbidden
{
  "code": "403",
  "description": "No tienes permiso para ver agendas de otros pacientes",
  "data": null
}
```

### Test 3: LAB_EMPLOYEE ve agendas de cualquier paciente ✅

```bash
# 1. Login como empleado de laboratorio
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "dr.lopez@lab.com",
    "password": "empleado123"
  }'

# Respuesta:
{
  "data": {
    "token": "eyJhbGc...",  # userId=20, rol=LAB_EMPLOYEE
    "rol": "LAB_EMPLOYEE"
  }
}

# 2. Ver agendas del paciente 5
curl http://localhost:8081/agenda/paciente/5 \
  -H "Authorization: Bearer eyJhbGc..."

# Respuesta: ✅ 200 OK (permitido porque es LAB_EMPLOYEE)

# 3. Ver agendas del paciente 10
curl http://localhost:8081/agenda/paciente/10 \
  -H "Authorization: Bearer eyJhbGc..."

# Respuesta: ✅ 200 OK (permitido porque es LAB_EMPLOYEE)
```

### Test 4: PATIENT crea agenda para sí mismo ✅

```bash
# Token de Juan (userId=5, rol=PATIENT)
curl -X POST http://localhost:8081/agenda \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 5,
    "examenId": 2,
    "labId": 1,
    "fechaHora": "2025-11-15T14:00:00Z",
    "estado": "AGENDADO"
  }'

# Respuesta: ✅ 201 Created
{
  "code": "000",
  "description": "Agenda de examen creada exitosamente",
  "data": {
    "id": 10,
    "pacienteId": 5,
    "examenId": 2,
    ...
  }
}
```

### Test 5: PATIENT intenta crear agenda para otro paciente ❌

```bash
# Token de Juan (userId=5, rol=PATIENT)
curl -X POST http://localhost:8081/agenda \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteId": 10,  # ← ID de otro paciente!
    "examenId": 2,
    "labId": 1,
    "fechaHora": "2025-11-15T14:00:00Z"
  }'

# Respuesta: ❌ 403 Forbidden
{
  "code": "403",
  "description": "No puedes crear agendas para otros pacientes",
  "data": null
}
```

### Test 6: PATIENT crea agenda sin enviar pacienteId ✅

```bash
# Token de Juan (userId=5, rol=PATIENT)
curl -X POST http://localhost:8081/agenda \
  -H "Authorization: Bearer eyJhbGc..." \
  -H "Content-Type: application/json" \
  -d '{
    "examenId": 2,
    "labId": 1,
    "fechaHora": "2025-11-15T14:00:00Z"
  }'

# Respuesta: ✅ 201 Created (auto-asigna pacienteId=5)
{
  "code": "000",
  "description": "Agenda de examen creada exitosamente",
  "data": {
    "id": 11,
    "pacienteId": 5,  # ← Asignado automáticamente
    "examenId": 2,
    ...
  }
}
```

---

## 🎓 Beneficios de esta Implementación

### 1. **Seguridad Multinivel**
- ✅ Autenticación (JWT válido)
- ✅ Autorización por rol (@PreAuthorize)
- ✅ Autorización por recurso (ownership)

### 2. **Separación de Responsabilidades**
- **Controller**: Valida rol
- **Service**: Valida ownership
- **Repository**: Accede a datos ya validados

### 3. **Facilidad de Uso para Frontend**
- Paciente puede omitir `pacienteId` en POST (se asigna automáticamente)
- Mensajes de error claros y específicos

### 4. **Cumplimiento de Privacidad**
- Protección de datos médicos (HIPAA, GDPR)
- Pacientes no pueden ver datos de otros pacientes
- Empleados y admins tienen acceso según su rol

### 5. **Mantenibilidad**
- Lógica centralizada en servicios
- Fácil de testear unitariamente
- Consistente en toda la aplicación

---

## 📌 Resumen

| Aspecto | Implementación |
|---------|----------------|
| **Controladores actualizados** | ✅ AgendaController, ResultadoController |
| **Servicios con validación** | ✅ AgendaServiceImpl, ResultadoServiceImpl |
| **Endpoints protegidos** | ✅ GET /agenda/paciente/{id}, POST /agenda, GET /resultados/paciente/{id} |
| **Excepción lanzada** | `AccessDeniedException` (403 Forbidden) |
| **Roles afectados** | Solo PATIENT (LAB_EMPLOYEE y ADMIN no tienen restricción) |
| **Auto-asignación** | ✅ Si PATIENT crea agenda sin pacienteId, se asigna automáticamente |

---

## ⚠️ Consideraciones Importantes

### 1. **Relación userId ↔ pacienteId**
Esta implementación asume que:
- `userId` (de la tabla USERS) == `pacienteId` (de la tabla PACIENTES)
- Si esta relación es diferente, debes ajustar la lógica de comparación

### 2. **Manejo de Excepciones**
`AccessDeniedException` es capturada por:
- `CustomAccessDeniedHandler` → Retorna 403 con JSON estándar
- `GlobalExceptionHandler` → Retorna 403 con JSON estándar

Ambos están configurados en el proyecto.

### 3. **Testing Unitario**
Para probar esta lógica:
```java
@Test
void testPatientCanOnlySeeOwnAgendas() {
    // Mock SecurityContext con userId=5, rol=PATIENT
    // Llamar service.findByPaciente(10)
    // Esperar AccessDeniedException
}
```

---

## 🚀 Próximos Pasos (Opcional)

1. **Logging de intentos de acceso no autorizado**:
   ```java
   logger.warn("Usuario {} (rol: {}) intentó acceder a datos del paciente {}",
       userId, rol, pacienteId);
   ```

2. **Métricas de seguridad**:
   - Contador de intentos de acceso denegados
   - Alertas si un usuario intenta acceder repetidamente a datos ajenos

3. **Auditoría**:
   - Registrar quién accedió a qué datos médicos (compliance HIPAA)

4. **Testing de integración**:
   - Crear tests E2E que validen todo el flujo de autenticación + autorización + ownership

---

**Implementado por**: AI Assistant  
**Fecha**: Noviembre 2025  
**Versión**: 1.0
