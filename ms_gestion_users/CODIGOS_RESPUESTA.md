# 📋 Códigos de Respuesta JSON

## 🎯 Convención de Códigos Internos

Este microservicio utiliza códigos internos en el campo `code` del JSON de respuesta, siguiendo la **Opción 2: Basados en HTTP con pequeñas variaciones**.

---

## 📊 Tabla de Códigos

| Code | HTTP Status | Significado | Uso |
|------|-------------|-------------|-----|
| `"000"` | 200 OK | ✅ Operación exitosa | Todas las operaciones exitosas |
| `"001"` | 500 Internal Server Error | ❌ Error de negocio/servidor | Fallos en consultas, creación, actualización, eliminación |
| `"401"` | 401 Unauthorized | ❌ No autenticado | Sin JWT, JWT inválido, JWT expirado |
| `"401.1"` | 401 Unauthorized | ❌ Credenciales incorrectas | Login fallido (usuario/password incorrectos) |
| `"403"` | 403 Forbidden | ❌ No autorizado | Usuario autenticado pero sin permisos de rol |

---

## 📝 Ejemplos de Respuestas

### ✅ Éxito (code: "000")
```json
{
  "code": "000",
  "description": "Usuarios obtenidos exitosamente",
  "data": [
    { "id": 1, "username": "admin@laboratorioandino.cl" }
  ]
}
```

### ❌ Error de Negocio (code: "001")
```json
{
  "code": "001",
  "description": "Error al crear usuario: El email ya existe",
  "data": {}
}
```

### ❌ No Autenticado - Sin JWT (code: "401")
```json
{
  "code": "401",
  "description": "No autenticado: Debe enviar un token JWT válido",
  "data": {}
}
```

### ❌ Credenciales Incorrectas (code: "401.1")
```json
{
  "code": "401.1",
  "description": "Credenciales inválidas: Usuario o contraseña incorrectos",
  "data": {}
}
```

### ❌ No Autorizado - Sin Permisos (code: "403")
```json
{
  "code": "403",
  "description": "No autorizado: No tiene permisos para realizar esta acción",
  "data": {}
}
```

---

## 🔍 ¿Cuándo se usa cada código?

### `"000"` - Éxito
**Controladores**: Todos los endpoints cuando la operación es exitosa
```java
response.put("code", "000");
response.put("description", "Usuario creado exitosamente");
response.put("data", nuevoUsuario);
return ResponseEntity.ok(response);
```

### `"001"` - Error de Negocio/Servidor
**Controladores**: Bloque `catch` de todos los endpoints
```java
catch (Exception e) {
    response.put("code", "001");
    response.put("description", "Error al obtener usuarios");
    response.put("data", new LinkedHashMap<>());
    return ResponseEntity.status(500).body(response);
}
```

### `"401"` - No Autenticado
**Manejador**: `CustomAuthenticationEntryPoint.java`
- Usuario no envía header `Authorization`
- Token JWT es inválido o malformado
- Token JWT ha expirado

### `"401.1"` - Credenciales Incorrectas
**Manejador**: `GlobalExceptionHandler.handleBadCredentials()`
- Usuario intenta login con password incorrecta
- Usuario intenta login con username inexistente

### `"403"` - No Autorizado
**Manejador**: `CustomAccessDeniedHandler.java`
- Usuario autenticado (JWT válido) pero sin rol adecuado
- Ejemplo: `LAB_EMPLOYEE` intenta endpoint con `@PreAuthorize("hasRole('ADMIN')")`

---

## 🧩 Estructura JSON Completa

Todas las respuestas siguen esta estructura:

```typescript
{
  "code": string,        // "000", "001", "401", "401.1", "403"
  "description": string, // Mensaje descriptivo
  "data": object        // Datos (objeto vacío {} en errores)
}
```

**Reglas**:
1. ✅ **Éxito**: `data` contiene la información solicitada/creada
2. ❌ **Error**: `data` es siempre un objeto vacío `{}`
3. 📝 **description**: Mensaje claro y descriptivo en español

---

## 🎨 Buenas Prácticas

### ✅ DO (Hacer)
```java
// Éxito: data con información
response.put("code", "000");
response.put("description", "Paciente creado exitosamente");
response.put("data", nuevoPaciente);

// Error: data vacío
response.put("code", "001");
response.put("description", "Error al crear paciente: RUT duplicado");
response.put("data", new LinkedHashMap<>());
```

### ❌ DON'T (No hacer)
```java
// ❌ No usar "error" en lugar de "code"
response.put("error", "Usuario no encontrado");

// ❌ No dejar data como null
response.put("data", null); // Debe ser {} en errores

// ❌ No mezclar estructuras
response.put("success", false); // No usar campos adicionales
```

---

## 📚 Referencias

- **Controladores**: `/controller/*Controller.java` - Usan `"000"` y `"001"`
- **Seguridad**: 
  - `CustomAuthenticationEntryPoint.java` - Usa `"401"`
  - `CustomAccessDeniedHandler.java` - Usa `"403"`
  - `GlobalExceptionHandler.java` - Usa `"401.1"`

---

## 🔄 Futuras Extensiones

Si necesitas agregar más códigos en el futuro, sigue este patrón:

| Code | HTTP | Propósito |
|------|------|-----------|
| `"400"` | 400 Bad Request | Datos de entrada inválidos |
| `"404"` | 404 Not Found | Recurso no encontrado |
| `"409"` | 409 Conflict | Conflicto (ej: email duplicado) |
| `"500"` | 500 Internal Server Error | Error inesperado del servidor |

**Subcategorías** (cuando necesites distinguir):
- `"401.2"` - Token expirado específico
- `"401.3"` - Token revocado
- `"403.1"` - Acceso a recurso de otro usuario

---

**Autor**: Microservicio de Gestión de Usuarios  
**Versión**: 1.0  
**Fecha**: Noviembre 2024
