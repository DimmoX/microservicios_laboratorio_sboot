# 🧪 Ejemplos de Testing RBAC con cURL

Este documento contiene ejemplos prácticos para probar el control de acceso basado en roles (RBAC) implementado en el microservicio de gestión de usuarios.

---

## 📋 Pre-requisitos

1. **Microservicio iniciado**: El servicio debe estar corriendo en `http://localhost:8082`
2. **Usuario ADMIN creado**: Ejecutar el script `crear_usuario_admin.sql`
3. **Variables de entorno** (opcional para simplificar):
```bash
export BASE_URL="http://localhost:8082"
export ADMIN_USER="admin@laboratorioandino.cl"
export ADMIN_PASS="admin123"
```

---

## 🔑 1. Autenticación

### 1.1. Login como ADMIN
```bash
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@laboratorioandino.cl",
    "password": "admin123"
  }'
```

**Respuesta esperada**:
```json
{
  "code": "000",
  "description": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbkBsYWJvcmF0b3Jpb2FuZGluby5jbCIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTcwOTQ1OTIwMCwiZXhwIjoxNzA5NDY2NDAwfQ.signature"
  }
}
```

**Guardar el token en variable**:
```bash
export TOKEN="eyJhbGciOiJIUzUxMiJ9..."
```

---

### 1.2. Login como LAB_EMPLOYEE
```bash
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "felipe.munoz@laboratorioandino.cl",
    "password": "12345"
  }'
```

**Guardar token**:
```bash
export LAB_TOKEN="eyJhbGciOiJIUzUxMiJ9..."
```

---

### 1.3. Login como PATIENT
```bash
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan.perez@gmail.com",
    "password": "12345"
  }'
```

**Guardar token**:
```bash
export PATIENT_TOKEN="eyJhbGciOiJIUzUxMiJ9..."
```

---

## ✅ 2. Casos de Éxito (Permitidos)

### 2.1. ADMIN - Listar usuarios
```bash
curl -X GET http://localhost:8082/users \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta esperada**: ✅ 200 OK con lista de usuarios

---

### 2.2. ADMIN - Crear nuevo usuario
```bash
curl -X POST http://localhost:8082/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "username": "nuevo.empleado@laboratorioandino.cl",
    "password": "$2a$10$jOX1cjWTiVqGXUtD/0AQ4OqZg9Zv8YdXz8/YdXz8/YdXz8/YdXz8Y",
    "role": "LAB_EMPLOYEE",
    "estado": "ACTIVO"
  }'
```

**Respuesta esperada**: ✅ 200 OK con usuario creado

---

### 2.3. ADMIN - Actualizar usuario
```bash
curl -X PUT http://localhost:8082/users/2 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "username": "felipe.munoz@laboratorioandino.cl",
    "estado": "INACTIVO"
  }'
```

**Respuesta esperada**: ✅ 200 OK con usuario actualizado

---

### 2.4. ADMIN - Eliminar usuario
```bash
curl -X DELETE http://localhost:8082/users/7 \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta esperada**: ✅ 200 OK con confirmación de eliminación

---

### 2.5. LAB_EMPLOYEE - Listar usuarios (solo lectura)
```bash
curl -X GET http://localhost:8082/users \
  -H "Authorization: Bearer $LAB_TOKEN"
```

**Respuesta esperada**: ✅ 200 OK con lista de usuarios

---

### 2.6. LAB_EMPLOYEE - Obtener usuario por ID (solo lectura)
```bash
curl -X GET http://localhost:8082/users/1 \
  -H "Authorization: Bearer $LAB_TOKEN"
```

**Respuesta esperada**: ✅ 200 OK con datos del usuario

---

### 2.7. LAB_EMPLOYEE - Listar pacientes
```bash
curl -X GET http://localhost:8082/pacientes \
  -H "Authorization: Bearer $LAB_TOKEN"
```

**Respuesta esperada**: ✅ 200 OK con lista de pacientes

---

### 2.8. ADMIN - Registrar paciente
```bash
curl -X POST http://localhost:8082/registro/paciente \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "pnombre": "Carlos",
    "snombre": "Alberto",
    "papellido": "Gonzalez",
    "sapellido": "Rojas",
    "rut": "12345678-9",
    "email": "carlos.gonzalez@gmail.com",
    "password": "password123",
    "telefono": "+56912345678",
    "direccion": "Avenida Principal 123"
  }'
```

**Respuesta esperada**: ✅ 200 OK con paciente y usuario creados

---

### 2.9. ADMIN - Registrar empleado
```bash
curl -X POST http://localhost:8082/registro/empleado \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "pnombre": "María",
    "snombre": "Isabel",
    "papellido": "Torres",
    "sapellido": "Muñoz",
    "rut": "98765432-1",
    "cargo": "Tecnóloga Médica",
    "email": "maria.torres@laboratorioandino.cl",
    "password": "password123"
  }'
```

**Respuesta esperada**: ✅ 200 OK con empleado y usuario creados

---

## ❌ 3. Casos de Error (Denegados)

### 3.1. LAB_EMPLOYEE - Intentar crear usuario (❌ Denegado)
```bash
curl -X POST http://localhost:8082/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $LAB_TOKEN" \
  -d '{
    "username": "intento.crear@laboratorioandino.cl",
    "password": "$2a$10$jOX1cjWTiVqGXUtD/0AQ4...",
    "role": "PATIENT",
    "estado": "ACTIVO"
  }'
```

**Respuesta esperada**: ❌ 403 Forbidden
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

### 3.2. LAB_EMPLOYEE - Intentar actualizar usuario (❌ Denegado)
```bash
curl -X PUT http://localhost:8082/users/2 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $LAB_TOKEN" \
  -d '{
    "estado": "INACTIVO"
  }'
```

**Respuesta esperada**: ❌ 403 Forbidden

---

### 3.3. LAB_EMPLOYEE - Intentar eliminar usuario (❌ Denegado)
```bash
curl -X DELETE http://localhost:8082/users/2 \
  -H "Authorization: Bearer $LAB_TOKEN"
```

**Respuesta esperada**: ❌ 403 Forbidden

---

### 3.4. PATIENT - Intentar listar usuarios (❌ Denegado)
```bash
curl -X GET http://localhost:8082/users \
  -H "Authorization: Bearer $PATIENT_TOKEN"
```

**Respuesta esperada**: ❌ 403 Forbidden

---

### 3.5. PATIENT - Intentar crear usuario (❌ Denegado)
```bash
curl -X POST http://localhost:8082/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $PATIENT_TOKEN" \
  -d '{
    "username": "intento.paciente@gmail.com",
    "password": "$2a$10$jOX1cjWTiVqGXUtD/0AQ4...",
    "role": "PATIENT",
    "estado": "ACTIVO"
  }'
```

**Respuesta esperada**: ❌ 403 Forbidden

---

### 3.6. Sin Token JWT - Intentar listar usuarios (❌ Denegado)
```bash
curl -X GET http://localhost:8082/users
```

**Respuesta esperada**: ❌ 401 Unauthorized
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/users"
}
```

---

### 3.7. Sin Token JWT - Intentar registrar paciente (❌ Denegado)
```bash
curl -X POST http://localhost:8082/registro/paciente \
  -H "Content-Type: application/json" \
  -d '{
    "pnombre": "Test",
    "papellido": "User",
    "rut": "11111111-1",
    "email": "test@test.cl",
    "password": "test123"
  }'
```

**Respuesta esperada**: ❌ 401 Unauthorized

---

### 3.8. LAB_EMPLOYEE - Intentar registrar empleado (❌ Denegado)
```bash
curl -X POST http://localhost:8082/registro/empleado \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $LAB_TOKEN" \
  -d '{
    "pnombre": "Test",
    "papellido": "Employee",
    "rut": "22222222-2",
    "cargo": "Test Cargo",
    "email": "test.employee@laboratorioandino.cl",
    "password": "test123"
  }'
```

**Respuesta esperada**: ❌ 403 Forbidden

---

### 3.9. PATIENT - Intentar registrar paciente (❌ Denegado)
```bash
curl -X POST http://localhost:8082/registro/paciente \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $PATIENT_TOKEN" \
  -d '{
    "pnombre": "Test",
    "papellido": "Patient",
    "rut": "33333333-3",
    "email": "test.patient@gmail.com",
    "password": "test123"
  }'
```

**Respuesta esperada**: ❌ 403 Forbidden

---

## 🔍 4. Verificación de Tokens JWT

### 4.1. Decodificar token JWT (sin verificar firma)
```bash
echo "$TOKEN" | cut -d'.' -f2 | base64 -d 2>/dev/null | jq .
```

**Salida esperada**:
```json
{
  "sub": "admin@laboratorioandino.cl",
  "role": "ADMIN",
  "iat": 1709459200,
  "exp": 1709466400
}
```

---

### 4.2. Verificar expiración del token
```bash
# Extraer timestamp de expiración
EXP=$(echo "$TOKEN" | cut -d'.' -f2 | base64 -d 2>/dev/null | jq -r '.exp')

# Comparar con timestamp actual
NOW=$(date +%s)

if [ $NOW -lt $EXP ]; then
  echo "✅ Token válido (expira en $(( ($EXP - $NOW) / 60 )) minutos)"
else
  echo "❌ Token expirado"
fi
```

---

## 📊 5. Matriz de Testing

| Endpoint | ADMIN | LAB_EMPLOYEE | PATIENT | Sin JWT |
|----------|-------|--------------|---------|---------|
| `POST /registro/paciente` | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 401 |
| `POST /registro/empleado` | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 401 |
| `GET /users` | ✅ 200 | ✅ 200 | ❌ 403 | ❌ 401 |
| `POST /users` | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 401 |
| `PUT /users/{id}` | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 401 |
| `DELETE /users/{id}` | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 401 |
| `GET /pacientes` | ✅ 200 | ✅ 200 | ❌ 403 | ❌ 401 |
| `POST /pacientes` | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 401 |
| `POST /auth/login` | ✅ 200 | ✅ 200 | ✅ 200 | ✅ 200 |
| `POST /auth/generate-hash` | ✅ 200 | ✅ 200 | ✅ 200 | ✅ 200 |

---

## 🧹 6. Cleanup

### Limpiar variables de entorno
```bash
unset TOKEN
unset LAB_TOKEN
unset PATIENT_TOKEN
unset BASE_URL
unset ADMIN_USER
unset ADMIN_PASS
```

---

## 🚀 7. Script de Testing Automatizado

```bash
#!/bin/bash

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

BASE_URL="http://localhost:8082"

echo "🧪 Testing RBAC - Microservicio Gestión de Usuarios"
echo "=================================================="

# 1. Login como ADMIN
echo -n "1️⃣  Login ADMIN... "
RESPONSE=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin@laboratorioandino.cl",
    "password": "admin123"
  }')

TOKEN=$(echo $RESPONSE | jq -r '.data.token')

if [ "$TOKEN" != "null" ]; then
  echo -e "${GREEN}✅ OK${NC}"
else
  echo -e "${RED}❌ FAIL${NC}"
  exit 1
fi

# 2. ADMIN puede listar usuarios
echo -n "2️⃣  ADMIN GET /users... "
STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X GET $BASE_URL/users \
  -H "Authorization: Bearer $TOKEN")

if [ "$STATUS" == "200" ]; then
  echo -e "${GREEN}✅ OK (200)${NC}"
else
  echo -e "${RED}❌ FAIL ($STATUS)${NC}"
fi

# 3. Login como LAB_EMPLOYEE
echo -n "3️⃣  Login LAB_EMPLOYEE... "
RESPONSE=$(curl -s -X POST $BASE_URL/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "felipe.munoz@laboratorioandino.cl",
    "password": "12345"
  }')

LAB_TOKEN=$(echo $RESPONSE | jq -r '.data.token')

if [ "$LAB_TOKEN" != "null" ]; then
  echo -e "${GREEN}✅ OK${NC}"
else
  echo -e "${RED}❌ FAIL${NC}"
fi

# 4. LAB_EMPLOYEE puede leer usuarios
echo -n "4️⃣  LAB_EMPLOYEE GET /users... "
STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X GET $BASE_URL/users \
  -H "Authorization: Bearer $LAB_TOKEN")

if [ "$STATUS" == "200" ]; then
  echo -e "${GREEN}✅ OK (200)${NC}"
else
  echo -e "${RED}❌ FAIL ($STATUS)${NC}"
fi

# 5. LAB_EMPLOYEE NO puede crear usuarios
echo -n "5️⃣  LAB_EMPLOYEE POST /users (debe fallar)... "
STATUS=$(curl -s -o /dev/null -w "%{http_code}" -X POST $BASE_URL/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $LAB_TOKEN" \
  -d '{
    "username": "test@test.cl",
    "password": "$2a$10$test",
    "role": "PATIENT",
    "estado": "ACTIVO"
  }')

if [ "$STATUS" == "403" ]; then
  echo -e "${GREEN}✅ OK (403 Forbidden)${NC}"
else
  echo -e "${RED}❌ FAIL ($STATUS - esperado 403)${NC}"
fi

echo ""
echo "=================================================="
echo "✅ Testing RBAC completado"
```

**Guardar como** `test_rbac.sh` y ejecutar:
```bash
chmod +x test_rbac.sh
./test_rbac.sh
```

---

## 📝 Notas

1. **Generar hash de contraseña**: Usa el endpoint `POST /auth/generate-hash`
```bash
curl -X POST http://localhost:8082/auth/generate-hash \
  -H "Content-Type: application/json" \
  -d '{
    "password": "mi_nueva_contraseña"
  }'
```

2. **Token expirado**: Los tokens JWT expiran en 120 minutos. Si recibes error 401, obtén un nuevo token.

3. **Formato de Authorization**: Siempre usar `Bearer <token>` con espacio entre Bearer y el token.

4. **Pretty print JSON**: Instalar `jq` para formatear JSON:
```bash
# Linux
sudo apt-get install jq

# macOS
brew install jq
```

---

**Autor**: Microservicio de Gestión de Usuarios  
**Versión**: 1.0  
**Fecha**: 2024
