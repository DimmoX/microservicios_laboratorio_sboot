# 🔐 Endpoints de Gestión de Contraseñas

Este documento describe los endpoints disponibles para gestionar contraseñas en el microservicio de usuarios.

---

## 📋 Endpoints Disponibles

### 1. Login (Autenticación)
**POST** `/auth/login`

Autentica un usuario y retorna un token JWT.

**Request Body:**
```json
{
  "username": "felipe.munoz@laboratorioandino.cl",
  "password": "12345"
}
```

**Response (200 OK):**
```json
{
  "code": "000",
  "description": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmZWxpcGUubXVub3pAbGFib3..."
  }
}
```

**Response (401 Unauthorized):**
```json
{
  "code": "001",
  "description": "Error en login: Contraseña incorrecta",
  "data": {}
}
```

---

### 2. Generar Hash de Contraseña
**POST** `/auth/generate-hash`

Genera el hash BCrypt de una contraseña. **Útil para desarrollo.**

**Request Body:**
```json
{
  "password": "miNuevaContraseña123"
}
```

**Response (200 OK):**
```json
{
  "code": "000",
  "description": "Hash generado exitosamente",
  "data": {
    "password": "miNuevaContraseña123",
    "hash": "$2a$10$jOX1cjWTiVqGXUtD/0AQ4.c8jYCKLwpFQ9iNHCmzWec3I7jte4YDS"
  }
}
```

**Uso:**
1. Llamas a este endpoint con la contraseña que quieres usar
2. El endpoint te devuelve el hash BCrypt
3. Copias el hash y lo usas en tu INSERT/UPDATE de la base de datos

**Ejemplo con curl:**
```bash
curl -X POST http://localhost:8082/auth/generate-hash \
  -H "Content-Type: application/json" \
  -d '{"password":"nuevaPass456"}'
```

---

### 3. Resetear Contraseña de Usuario
**POST** `/auth/reset-password`

Cambia la contraseña de un usuario en la base de datos.

**Request Body:**
```json
{
  "username": "felipe.munoz@laboratorioandino.cl",
  "newPassword": "nuevaContraseña789"
}
```

**Response (200 OK):**
```json
{
  "code": "000",
  "description": "Contraseña reseteada exitosamente",
  "data": {
    "username": "felipe.munoz@laboratorioandino.cl",
    "message": "Contraseña actualizada exitosamente",
    "newHash": "$2a$10$kL9mN2oP3qR4sT5uV6wX7yZ8aB9cD0eF1gH2iJ3kL4mN5oP6qR7sT"
  }
}
```

**Uso:**
Este endpoint actualiza directamente la base de datos. Después de llamarlo:
- El usuario podrá hacer login con la nueva contraseña
- El hash se genera automáticamente
- La respuesta te muestra el nuevo hash por si lo necesitas

**Ejemplo con curl:**
```bash
curl -X POST http://localhost:8082/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "username":"camila.rojas@correo.cl",
    "newPassword":"password123"
  }'
```

---

## 🔍 Casos de Uso

### Caso 1: Quiero agregar un nuevo usuario a la BD
1. Usa `/auth/generate-hash` con la contraseña deseada
2. Copia el hash del response
3. Ejecuta tu INSERT:
```sql
INSERT INTO users (username, password, role, paciente_id, empleado_id)
VALUES ('nuevo.usuario@correo.cl', '$2a$10$...hash copiado...', 'PATIENT', 123, NULL);
```

### Caso 2: Un usuario olvidó su contraseña
1. Usa `/auth/reset-password` con el username y la nueva contraseña
2. El sistema actualiza automáticamente la BD
3. El usuario puede hacer login con la nueva contraseña

### Caso 3: Quiero saber qué contraseña tiene un usuario
❌ **No es posible recuperar la contraseña original** debido a que BCrypt es un hash unidireccional.

✅ **Solución:** Usa `/auth/reset-password` para establecer una nueva contraseña conocida.

---

## 📝 Usuarios Actuales en el Sistema

Todos estos usuarios tienen la contraseña: `12345`

| Username | Rol | Tipo |
|----------|-----|------|
| felipe.munoz@laboratorioandino.cl | LAB_EMPLOYEE | Empleado (TM) |
| constanza.araya@clinicabiosalud.cl | LAB_EMPLOYEE | Empleado (BQ) |
| matias.carrasco@centrodiagnosticopacifico.cl | LAB_EMPLOYEE | Empleado (ADM) |
| camila.rojas@correo.cl | PATIENT | Paciente |
| benjamin.gonzalez@correo.cl | PATIENT | Paciente |
| isidora.munoz@correo.cl | PATIENT | Paciente |

---

## ⚠️ Notas Importantes

1. **BCrypt es unidireccional:** No puedes "desencriptar" un hash para obtener la contraseña original
2. **Estos endpoints son para desarrollo:** En producción, deberías proteger `/generate-hash` y `/reset-password` con autenticación de administrador
3. **El hash cambia cada vez:** Aunque uses la misma contraseña, BCrypt genera un hash diferente cada vez (incluye un salt aleatorio)
4. **El login sigue funcionando:** A pesar de que los hashes son diferentes, BCrypt los puede validar correctamente

---

## 🚀 Próximos Pasos

Para un ambiente de producción, considera:

1. **Proteger los endpoints sensibles:**
```java
@PreAuthorize("hasRole('ADMIN')")
@PostMapping("/reset-password")
public ResponseEntity<ResetPasswordResponse> resetPassword(...) {
```

2. **Implementar recuperación de contraseña por email:**
   - Generar un token temporal
   - Enviar email con link de recuperación
   - Validar token antes de permitir el cambio

3. **Agregar políticas de contraseñas:**
   - Longitud mínima
   - Complejidad (mayúsculas, números, símbolos)
   - Expiración periódica
