# 📋 Guía de Registro de Pacientes y Empleados

Este documento explica cómo usar los nuevos endpoints de registro que crean pacientes/empleados con sus credenciales de login en una sola operación.

---

## 🎯 Problema Resuelto

Anteriormente, para crear un paciente o empleado con credenciales de login necesitabas:
1. Crear manualmente el registro en `contactos` (para obtener el email)
2. Crear manualmente el registro en `direcciones`
3. Crear el `paciente` o `empleado` con los IDs de contacto y dirección
4. Crear manualmente el `usuario` con el email del contacto

**Ahora todo esto se hace en una sola llamada** usando DTOs anidados.

---

## 📦 Estructura de DTOs Anidados

### ContactoDTO
```json
{
  "fono1": "+56912345678",
  "fono2": "+56987654321",  // Opcional
  "email": "usuario@correo.cl"
}
```

### DireccionDTO
```json
{
  "calle": "Av. Libertador",
  "numero": 1234,
  "ciudad": "Santiago",
  "comuna": "Providencia",
  "region": "Metropolitana"
}
```

---

## 🔵 Endpoint: Registrar Paciente

**POST** `/registro/paciente`

### Request Body Completo:
```json
{
  "pnombre": "Juan",
  "snombre": "Carlos",
  "papellido": "Pérez",
  "sapellido": "López",
  "rut": "12345678-9",
  "contacto": {
    "fono1": "+56912345678",
    "fono2": "+56987654321",
    "email": "juan.perez@correo.cl"
  },
  "direccion": {
    "calle": "Av. Libertador Bernardo O'Higgins",
    "numero": 1234,
    "ciudad": "Santiago",
    "comuna": "Santiago Centro",
    "region": "Metropolitana"
  },
  "password": "miPassword123"
}
```

### Response Exitosa (200 OK):
```json
{
  "code": "000",
  "description": "Paciente registrado exitosamente",
  "data": {
    "pacienteId": 7,
    "empleadoId": null,
    "usuarioId": 8,
    "username": "juan.perez@correo.cl",
    "role": "PATIENT",
    "mensaje": "Paciente registrado exitosamente"
  }
}
```

### Ejemplo con curl:
```bash
curl -X POST http://localhost:8082/registro/paciente \
  -H "Content-Type: application/json" \
  -d '{
    "pnombre": "Juan",
    "snombre": "Carlos",
    "papellido": "Pérez",
    "sapellido": "López",
    "rut": "12345678-9",
    "contacto": {
      "fono1": "+56912345678",
      "email": "juan.perez@correo.cl"
    },
    "direccion": {
      "calle": "Av. Libertador",
      "numero": 1234,
      "ciudad": "Santiago",
      "comuna": "Providencia",
      "region": "Metropolitana"
    },
    "password": "miPassword123"
  }'
```

---

## 🟢 Endpoint: Registrar Empleado

**POST** `/registro/empleado`

### Request Body Completo:
```json
{
  "pnombre": "María",
  "snombre": "Fernanda",
  "papellido": "González",
  "sapellido": "Rojas",
  "rut": "98765432-1",
  "cargo": "Tecnólogo Médico",
  "rol": "TM",
  "contacto": {
    "fono1": "+56987654321",
    "fono2": "+56912345678",
    "email": "maria.gonzalez@laboratorio.cl"
  },
  "direccion": {
    "calle": "Av. Providencia",
    "numero": 2500,
    "ciudad": "Santiago",
    "comuna": "Providencia",
    "region": "Metropolitana"
  },
  "password": "password456"
}
```

### Response Exitosa (200 OK):
```json
{
  "code": "000",
  "description": "Empleado registrado exitosamente",
  "data": {
    "pacienteId": null,
    "empleadoId": 4,
    "usuarioId": 9,
    "username": "maria.gonzalez@laboratorio.cl",
    "role": "LAB_EMPLOYEE",
    "mensaje": "Empleado registrado exitosamente"
  }
}
```

### Ejemplo con curl:
```bash
curl -X POST http://localhost:8082/registro/empleado \
  -H "Content-Type: application/json" \
  -d '{
    "pnombre": "María",
    "snombre": "Fernanda",
    "papellido": "González",
    "sapellido": "Rojas",
    "rut": "98765432-1",
    "cargo": "Tecnólogo Médico",
    "rol": "TM",
    "contacto": {
      "fono1": "+56987654321",
      "email": "maria.gonzalez@laboratorio.cl"
    },
    "direccion": {
      "calle": "Av. Providencia",
      "numero": 2500,
      "ciudad": "Santiago",
      "comuna": "Providencia",
      "region": "Metropolitana"
    },
    "password": "password456"
  }'
```

---

## 🔒 Roles de Empleados

El campo `rol` en empleados acepta:
- `TM` - Tecnólogo Médico
- `BQ` - Bioquímico
- `ADM` - Administrativo/Recepcionista

---

## ✅ ¿Qué Crea Cada Endpoint?

### Registro de Paciente crea:
1. ✅ Registro en tabla `contactos` (con el email)
2. ✅ Registro en tabla `direcciones`
3. ✅ Registro en tabla `pacientes` (vinculado a contacto y dirección)
4. ✅ Registro en tabla `users` con:
   - `username` = email del contacto
   - `password` = hash BCrypt de la contraseña
   - `role` = "PATIENT"
   - `paciente_id` = ID del paciente creado
   - `empleado_id` = NULL

### Registro de Empleado crea:
1. ✅ Registro en tabla `contactos` (con el email)
2. ✅ Registro en tabla `direcciones`
3. ✅ Registro en tabla `empleados` (vinculado a contacto y dirección)
4. ✅ Registro en tabla `users` con:
   - `username` = email del contacto
   - `password` = hash BCrypt de la contraseña
   - `role` = "LAB_EMPLOYEE"
   - `paciente_id` = NULL
   - `empleado_id` = ID del empleado creado

---

## 🔐 Después del Registro

Una vez registrado, el usuario puede hacer login usando:
- **Username**: El email proporcionado en `contacto.email`
- **Password**: La contraseña proporcionada en `password`

Ejemplo de login:
```bash
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan.perez@correo.cl",
    "password": "miPassword123"
  }'
```

---

## ⚠️ Validaciones

Los endpoints validan:
- ✅ **Email único**: El email no debe existir en `contactos` ni en `users`
- ✅ **Transaccionalidad**: Si falla cualquier paso, se hace rollback de todos los cambios
- ✅ **Password encoding**: La contraseña se encripta con BCrypt automáticamente

### Errores Comunes:

**Error: Email ya registrado**
```json
{
  "code": "001",
  "description": "Error al registrar paciente: El email ya está registrado: juan.perez@correo.cl",
  "data": {}
}
```

**Error: Usuario ya existe**
```json
{
  "code": "001",
  "description": "Error al registrar paciente: Ya existe un usuario con este email: juan.perez@correo.cl",
  "data": {}
}
```

---

## 🔄 Flujo Completo de Registro y Login

### 1. Registrar Paciente
```bash
POST /registro/paciente
{
  "pnombre": "Ana",
  "papellido": "Silva",
  "rut": "11223344-5",
  "contacto": {
    "fono1": "+56911223344",
    "email": "ana.silva@correo.cl"
  },
  "direccion": {
    "calle": "Calle Nueva",
    "numero": 100,
    "ciudad": "Santiago",
    "comuna": "Las Condes",
    "region": "Metropolitana"
  },
  "password": "ana123"
}
```

**Response:**
```json
{
  "code": "000",
  "description": "Paciente registrado exitosamente",
  "data": {
    "pacienteId": 10,
    "usuarioId": 11,
    "username": "ana.silva@correo.cl",
    "role": "PATIENT"
  }
}
```

### 2. Hacer Login
```bash
POST /auth/login
{
  "username": "ana.silva@correo.cl",
  "password": "ana123"
}
```

**Response:**
```json
{
  "code": "000",
  "description": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9..."
  }
}
```

### 3. Usar el Token
```bash
GET /api/pacientes
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

## 📊 Tablas Afectadas

| Tabla | Acción | Campos Importantes |
|-------|--------|-------------------|
| `contactos` | INSERT | email (usado como username), fono1, fono2 |
| `direcciones` | INSERT | calle, numero, ciudad, comuna, region |
| `pacientes` | INSERT | nombres, apellidos, rut, contacto_id, dir_id |
| `empleados` | INSERT | nombres, apellidos, rut, cargo, rol, contacto_id, dir_id |
| `users` | INSERT | username (=email), password (hash), role, paciente_id/empleado_id |

---

## 🎯 Ventajas de este Enfoque

✅ **Una sola llamada**: Todo el registro en un endpoint  
✅ **Transaccional**: Si falla algo, se deshace todo  
✅ **Validación automática**: Email único, contraseña encriptada  
✅ **DTOs anidados**: Estructura clara y fácil de usar  
✅ **Logging completo**: Trazabilidad de cada paso  
✅ **Respuesta estándar**: Mismo formato en todos los endpoints

---

## 🚀 Próximos Pasos

Para un ambiente de producción, considera:
- Agregar validaciones de formato (email, RUT, teléfono)
- Implementar envío de email de confirmación
- Agregar política de contraseñas (longitud mínima, complejidad)
- Implementar límites de intentos de registro
- Agregar verificación de email antes de activar la cuenta
