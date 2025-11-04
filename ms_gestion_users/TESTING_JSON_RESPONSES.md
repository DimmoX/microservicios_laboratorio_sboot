# 🧪 Testing de Respuestas JSON Estandarizadas

Este documento contiene ejemplos de las respuestas JSON con estructura estándar para todos los casos de autenticación y autorización.

---

## 📋 Estructura JSON Estándar

Todos los endpoints devuelven esta estructura:

```json
{
  "code": "XXX",
  "description": "Descripción del resultado",
  "data": { }
}
```

- **code**: Código de estado interno
  - `"000"` = Operación exitosa (HTTP 200)
  - `"001"` = Error de negocio/servidor (HTTP 500)
  - `"401"` = No autenticado - Sin JWT o JWT inválido (HTTP 401)
  - `"401.1"` = Credenciales incorrectas - Login fallido (HTTP 401)
  - `"403"` = No autorizado - Sin permisos de rol (HTTP 403)
- **description**: Mensaje descriptivo del resultado
- **data**: Datos de respuesta (objeto vacío `{}` en caso de error)

---

## ✅ Caso 1: Operación Exitosa

### Request
```bash
curl -X GET http://localhost:8082/users \
  -H "Authorization: Bearer <TOKEN_ADMIN_VALIDO>"
```

### Response: ✅ 200 OK
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
    }
  ]
}
```

---

## ❌ Caso 2: Sin Token JWT (401 Unauthorized)

### Request - Sin header Authorization
```bash
curl -X GET http://localhost:8082/users
```

### Response: ❌ 401 Unauthorized
```json
{
  "code": "401",
  "description": "No autenticado: Debe enviar un token JWT válido",
  "data": {}
}
```

---

## ❌ Caso 3: Token JWT Inválido (401 Unauthorized)

### Request - Token malformado o firmado incorrectamente
```bash
curl -X GET http://localhost:8082/users \
  -H "Authorization: Bearer token_invalido_123"
```

### Response: ❌ 401 Unauthorized
```json
{
  "code": "401",
  "description": "No autenticado: Debe enviar un token JWT válido",
  "data": {}
}
```

---

## ❌ Caso 4: Token JWT Expirado (401 Unauthorized)

### Request - Token expirado (más de 120 minutos desde emisión)
```bash
curl -X GET http://localhost:8082/users \
  -H "Authorization: Bearer <TOKEN_EXPIRADO>"
```

### Response: ❌ 401 Unauthorized
```json
{
  "code": "401",
  "description": "No autenticado: Debe enviar un token JWT válido",
  "data": {}
}
```

---

## ❌ Caso 5: Sin Permisos - LAB_EMPLOYEE intenta crear usuario (403 Forbidden)

### Request - Usuario con rol LAB_EMPLOYEE intenta POST /users
```bash
curl -X POST http://localhost:8082/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_LAB_EMPLOYEE>" \
  -d '{
    "username": "nuevo@test.cl",
    "password": "$2a$10$...",
    "role": "PATIENT",
    "estado": "ACTIVO"
  }'
```

### Response: ❌ 403 Forbidden
```json
{
  "code": "403",
  "description": "No autorizado: No tiene permisos para realizar esta acción",
  "data": {}
}
```

---

## ❌ Caso 6: Sin Permisos - PATIENT intenta listar usuarios (403 Forbidden)

### Request - Usuario con rol PATIENT intenta GET /users
```bash
curl -X GET http://localhost:8082/users \
  -H "Authorization: Bearer <TOKEN_PATIENT>"
```

### Response: ❌ 403 Forbidden
```json
{
  "code": "403",
  "description": "No autorizado: No tiene permisos para realizar esta acción",
  "data": {}
}
```

---

## ❌ Caso 7: Sin Permisos - LAB_EMPLOYEE intenta registrar paciente (403 Forbidden)

### Request - Usuario con rol LAB_EMPLOYEE intenta POST /registro/paciente
```bash
curl -X POST http://localhost:8082/registro/paciente \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_LAB_EMPLOYEE>" \
  -d '{
    "pnombre": "Juan",
    "papellido": "Perez",
    "rut": "12345678-9",
    "email": "juan@gmail.com",
    "password": "12345"
  }'
```

### Response: ❌ 403 Forbidden
```json
{
  "code": "403",
  "description": "No autorizado: No tiene permisos para realizar esta acción",
  "data": {}
}
```

---

## ❌ Caso 8: Credenciales Inválidas en Login (401 Unauthorized)

### Request - Password incorrecta
```bash
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@laboratorioandino.cl",
    "password": "password_incorrecto"
  }'
```

### Response: ❌ 401 Unauthorized
```json
{
  "code": "401.1",
  "description": "Credenciales inválidas: Usuario o contraseña incorrectos",
  "data": {}
}
```

---

## 📊 Tabla Resumen de Respuestas

| Escenario | HTTP Status | code | description |
|-----------|-------------|------|-------------|
| ✅ Operación exitosa | 200 OK | `"000"` | Mensaje específico del endpoint |
| ❌ Error de negocio/servidor | 500 Internal Server Error | `"001"` | Mensaje específico del error |
| ❌ Sin token JWT | 401 Unauthorized | `"401"` | "No autenticado: Debe enviar un token JWT válido" |
| ❌ Token inválido | 401 Unauthorized | `"401"` | "No autenticado: Debe enviar un token JWT válido" |
| ❌ Token expirado | 401 Unauthorized | `"401"` | "No autenticado: Debe enviar un token JWT válido" |
| ❌ Credenciales incorrectas | 401 Unauthorized | `"401.1"` | "Credenciales inválidas: Usuario o contraseña incorrectos" |
| ❌ Sin permisos (rol insuficiente) | 403 Forbidden | `"403"` | "No autorizado: No tiene permisos para realizar esta acción" |

---

## 🔍 Puntos de Activación

### CustomAuthenticationEntryPoint (401)
Se activa cuando:
- No se envía header `Authorization`
- El token JWT es malformado
- El token JWT tiene firma inválida
- El token JWT ha expirado
- Cualquier otro error de autenticación

### CustomAccessDeniedHandler (403)
Se activa cuando:
- El usuario está autenticado (tiene JWT válido)
- Pero el rol del usuario NO cumple con `@PreAuthorize`
- Ejemplo: `LAB_EMPLOYEE` intenta `@PreAuthorize("hasRole('ADMIN')")`

---

## 🧪 Script de Testing Completo

```bash
#!/bin/bash

BASE_URL="http://localhost:8082"

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🧪 Testing Respuestas JSON Estandarizadas"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Test 1: Sin token JWT (401)
echo "📝 Test 1: GET /users sin token (esperado: 401)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s -X GET $BASE_URL/users | jq .
echo ""

# Test 2: Login correcto
echo "📝 Test 2: Login correcto (esperado: 200)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
RESPONSE=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@laboratorioandino.cl",
    "password": "admin123"
  }')
echo $RESPONSE | jq .
ADMIN_TOKEN=$(echo $RESPONSE | jq -r '.data.token')
echo ""

# Test 3: Login incorrecto (401)
echo "📝 Test 3: Login con password incorrecta (esperado: 401)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@laboratorioandino.cl",
    "password": "password_incorrecto"
  }' | jq .
echo ""

# Test 4: GET con token ADMIN (200)
echo "📝 Test 4: GET /users con token ADMIN (esperado: 200)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s -X GET $BASE_URL/users \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq .
echo ""

# Test 5: Login como LAB_EMPLOYEE
echo "📝 Test 5: Login como LAB_EMPLOYEE"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
RESPONSE=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "felipe.munoz@laboratorioandino.cl",
    "password": "12345"
  }')
LAB_TOKEN=$(echo $RESPONSE | jq -r '.data.token')
echo "Token LAB_EMPLOYEE obtenido"
echo ""

# Test 6: LAB_EMPLOYEE intenta crear usuario (403)
echo "📝 Test 6: LAB_EMPLOYEE POST /users (esperado: 403)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s -X POST $BASE_URL/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $LAB_TOKEN" \
  -d '{
    "username": "test@test.cl",
    "password": "$2a$10$test",
    "role": "PATIENT",
    "estado": "ACTIVO"
  }' | jq .
echo ""

# Test 7: LAB_EMPLOYEE intenta registrar paciente (403)
echo "📝 Test 7: LAB_EMPLOYEE POST /registro/paciente (esperado: 403)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s -X POST $BASE_URL/registro/paciente \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $LAB_TOKEN" \
  -d '{
    "pnombre": "Test",
    "papellido": "User",
    "rut": "11111111-1",
    "email": "test@gmail.com",
    "password": "12345"
  }' | jq .
echo ""

# Test 8: Token inválido (401)
echo "📝 Test 8: GET /users con token inválido (esperado: 401)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
curl -s -X GET $BASE_URL/users \
  -H "Authorization: Bearer token_invalido_123" | jq .
echo ""

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ Testing completado"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
```

**Guardar como**: `test_json_responses.sh`

**Ejecutar**:
```bash
chmod +x test_json_responses.sh
./test_json_responses.sh
```

---

## 📝 Notas Importantes

1. **Consistencia**: Todas las respuestas usan la misma estructura JSON con `code`, `description`, `data`

2. **Códigos HTTP vs code JSON**:
   - HTTP 200 → `"code": "000"`
   - HTTP 401 → `"code": "401"`
   - HTTP 403 → `"code": "403"`

3. **Campo data**: 
   - Éxito: Contiene los datos solicitados
   - Error: Objeto vacío `{}`

4. **Convención de códigos**:
   - `"000"` - Éxito
   - `"001"` - Error de negocio/servidor
   - `"401"` - No autenticado (sin JWT, JWT inválido, expirado)
   - `"401.1"` - Credenciales incorrectas (login)
   - `"403"` - No autorizado (sin permisos de rol)

5. **Orden de evaluación**:
   1. Autenticación (JWT válido) → 401 si falla
   2. Autorización (rol adecuado) → 403 si falla
   3. Lógica de negocio → 000 o 001

---

**Autor**: Microservicio de Gestión de Usuarios  
**Versión**: 2.0  
**Fecha**: 2024
