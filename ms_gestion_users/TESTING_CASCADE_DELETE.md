# 🧪 Prueba de CASCADE DELETE - USERS

## 📋 Objetivo
Verificar que al eliminar un PACIENTE o EMPLEADO, su USUARIO asociado se elimina automáticamente.

---

## ⚙️ Paso 1: Ejecutar Script de Migración

Ejecuta el script `agregar_cascade_delete.sql` en tu base de datos Oracle:

```bash
sqlplus usuario/password@conexion @agregar_cascade_delete.sql
```

Esto modificará las constraints de la tabla `USERS` para agregar `ON DELETE CASCADE`.

---

## 🧪 Paso 2: Prueba con PACIENTE

### 2.1. Registrar un paciente de prueba (con usuario)

```bash
curl -X POST http://localhost:8082/registro/paciente \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_ADMIN>" \
  -d '{
    "pnombre": "Test",
    "papellido": "Cascade",
    "rut": "99999999-9",
    "email": "test.cascade@gmail.com",
    "password": "test123",
    "telefono": "+56912345678",
    "direccion": "Calle Test 123"
  }'
```

**Respuesta esperada**:
```json
{
  "code": "000",
  "description": "Paciente y usuario registrados exitosamente",
  "data": {
    "paciente": {
      "id": 10,
      "pnombre": "Test",
      "papellido": "Cascade",
      "rut": "99999999-9"
    },
    "usuario": {
      "id": 15,
      "username": "test.cascade@gmail.com",
      "role": "PATIENT"
    }
  }
}
```

### 2.2. Verificar que el usuario existe

```bash
# Login con el usuario recién creado
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test.cascade@gmail.com",
    "password": "test123"
  }'
```

**Debería devolver**: ✅ Token JWT válido

### 2.3. Eliminar el PACIENTE

```bash
curl -X DELETE http://localhost:8082/pacientes/10 \
  -H "Authorization: Bearer <TOKEN_ADMIN>"
```

**Respuesta esperada**:
```json
{
  "code": "000",
  "description": "Paciente eliminado exitosamente",
  "data": {}
}
```

### 2.4. Verificar que el USUARIO también se eliminó (CASCADE)

```bash
# Intentar login nuevamente
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test.cascade@gmail.com",
    "password": "test123"
  }'
```

**Respuesta esperada**: ❌ 401 Unauthorized (usuario no existe)
```json
{
  "code": "401.1",
  "description": "Credenciales inválidas: Usuario o contraseña incorrectos",
  "data": {}
}
```

✅ **ÉXITO**: El usuario se eliminó automáticamente cuando se eliminó el paciente.

---

## 🧪 Paso 3: Prueba con EMPLEADO

### 3.1. Registrar un empleado de prueba (con usuario)

```bash
curl -X POST http://localhost:8082/registro/empleado \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_ADMIN>" \
  -d '{
    "pnombre": "Empleado",
    "papellido": "Test",
    "rut": "88888888-8",
    "cargo": "Tecnólogo Médico",
    "email": "empleado.test@laboratorioandino.cl",
    "password": "test123"
  }'
```

### 3.2. Verificar que el usuario existe

```bash
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "empleado.test@laboratorioandino.cl",
    "password": "test123"
  }'
```

**Debería devolver**: ✅ Token JWT válido

### 3.3. Eliminar el EMPLEADO

```bash
curl -X DELETE http://localhost:8082/empleados/5 \
  -H "Authorization: Bearer <TOKEN_ADMIN>"
```

### 3.4. Verificar que el USUARIO también se eliminó (CASCADE)

```bash
curl -X POST http://localhost:8082/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "empleado.test@laboratorioandino.cl",
    "password": "test123"
  }'
```

**Respuesta esperada**: ❌ 401 Unauthorized

✅ **ÉXITO**: El usuario se eliminó automáticamente cuando se eliminó el empleado.

---

## 🔍 Verificación en Base de Datos

Si tienes acceso directo a la base de datos, puedes verificar con SQL:

```sql
-- Ver constraints con CASCADE
SELECT 
  constraint_name,
  table_name,
  delete_rule
FROM user_constraints
WHERE table_name = 'USERS'
  AND constraint_type = 'R';

-- Resultado esperado:
-- FK_USERS_PACIENTE    USERS    CASCADE
-- FK_USERS_EMPLEADO    USERS    CASCADE
```

---

## 📊 Comportamiento Esperado

### Antes de CASCADE DELETE
```
Eliminar Paciente → Error: "Cannot delete - child record exists in USERS"
```

### Después de CASCADE DELETE
```
Eliminar Paciente → Paciente eliminado ✅
                  → Usuario asociado eliminado automáticamente ✅
```

---

## ⚠️ Consideraciones Importantes

1. **Eliminación en cascada**: 
   - Al eliminar un paciente → Se elimina su usuario automáticamente
   - Al eliminar un empleado → Se elimina su usuario automáticamente

2. **Integridad de datos**:
   - No quedará ningún usuario "huérfano" sin paciente/empleado
   - La operación es atómica (todo o nada)

3. **No afecta a ADMIN**:
   - Usuarios ADMIN (sin paciente_id ni empleado_id) NO se ven afectados
   - Solo se eliminan usuarios vinculados a pacientes/empleados eliminados

4. **Logs**:
   - Verifica los logs de la aplicación para confirmar las eliminaciones
   - Deberías ver: "Usuario con ID: X eliminado exitosamente"

---

## ✅ Checklist de Verificación

- [ ] Script `agregar_cascade_delete.sql` ejecutado
- [ ] Constraints verificadas con `SELECT` en base de datos
- [ ] Prueba de registro de paciente con usuario
- [ ] Prueba de eliminación de paciente → usuario se elimina
- [ ] Prueba de registro de empleado con usuario
- [ ] Prueba de eliminación de empleado → usuario se elimina
- [ ] Login fallido después de eliminar (usuario no existe)

---

**Autor**: Microservicio de Gestión de Usuarios  
**Fecha**: Noviembre 2024
