# 🏗️ Arquitectura de Roles y Usuarios

## 📋 Descripción General

El sistema maneja **UN ÚNICO concepto de "rol"** que define los permisos de acceso a la aplicación.

**Rol del Sistema** (`USERS.role`) - Permisos de acceso a la aplicación
- Define QUÉ puede hacer un usuario en el sistema
- Valores: `ADMIN`, `LAB_EMPLOYEE`, `PATIENT`

> ⚠️ **NOTA**: La columna `EMPLEADOS.cargo` almacena el cargo profesional del empleado (ej: "Tecnólogo Médico", "Bioquímico", "Recepcionista") pero **NO afecta los permisos** del sistema.

---

## 🔐 1. Roles del Sistema (Tabla USERS)

### Tabla: `USERS`
```sql
CREATE TABLE users (
  id           NUMBER PRIMARY KEY,
  username     VARCHAR2(100) NOT NULL UNIQUE,
  password     VARCHAR2(255) NOT NULL,
  role         VARCHAR2(20),              -- 🔑 ROL DEL SISTEMA
  estado       VARCHAR2(20) DEFAULT 'ACTIVO',
  paciente_id  NUMBER,                    -- FK a PACIENTES (si es paciente)
  empleado_id  NUMBER,                    -- FK a EMPLEADOS (si es empleado)
  creado_en    TIMESTAMP
);
```

### Roles del Sistema Disponibles

| Rol | Valor | Descripción | Relación |
|-----|-------|-------------|----------|
| **Administrador** | `ADMIN` | Control total del sistema | Sin `empleado_id` ni `paciente_id` |
| **Empleado de Laboratorio** | `LAB_EMPLOYEE` | Empleado con acceso al sistema | Tiene `empleado_id` NOT NULL |
| **Paciente** | `PATIENT` | Usuario paciente | Tiene `paciente_id` NOT NULL |

### Lógica de Determinación del Rol

```sql
-- Un usuario es EMPLEADO si:
SELECT * FROM users WHERE empleado_id IS NOT NULL;

-- Un usuario es PACIENTE si:
SELECT * FROM users WHERE paciente_id IS NOT NULL;

-- Un usuario es ADMIN si:
SELECT * FROM users WHERE role = 'ADMIN';
-- (Nota: ADMIN puede tener empleado_id NULL o NOT NULL dependiendo del caso)
```

---

## 👨‍💼 2. Cargo del Empleado (Tabla EMPLEADOS)

### Tabla: `EMPLEADOS`
```sql
CREATE TABLE empleados (
  id           NUMBER PRIMARY KEY,
  pnombre      VARCHAR2(20),
  snombre      VARCHAR2(20),
  papellido    VARCHAR2(20),
  sapellido    VARCHAR2(20),
  rut          VARCHAR2(10),
  cargo        VARCHAR2(40),              -- DESCRIPCIÓN DEL CARGO
  creado_en    TIMESTAMP,
  dir_id       NUMBER,
  contacto_id  NUMBER
);
```

### Campo `cargo`
- **Propósito**: Almacenar el cargo o especialidad profesional del empleado
- **Ejemplos**: "Tecnólogo Médico", "Bioquímico", "Recepcionista", "Jefe de Laboratorio"
- **Importante**: Este campo es **solo informativo** y **NO afecta** los permisos del sistema
- **Permisos**: Se definen únicamente por `USERS.role`

---

## 🔄 3. Flujo de Creación de Usuarios

### 3.1. Registrar un Paciente

**Endpoint**: `POST /registro/paciente`

**Proceso**:
1. Crear registro en `CONTACTOS`
2. Crear registro en `DIRECCIONES`
3. Crear registro en `PACIENTES` (con `dir_id` y `contacto_id`)
4. Crear registro en `USERS`:
   - `username` = email del paciente
   - `password` = hash BCrypt
   - `role` = **'PATIENT'**
   - `paciente_id` = ID del paciente creado
   - `empleado_id` = **NULL**

**Ejemplo**:
```sql
-- 1. Insertar contacto
INSERT INTO contactos (...) VALUES (...) RETURNING id INTO v_contacto_id;

-- 2. Insertar dirección
INSERT INTO direcciones (...) VALUES (...) RETURNING id INTO v_dir_id;

-- 3. Insertar paciente
INSERT INTO pacientes (pnombre, ..., dir_id, contacto_id)
VALUES ('Juan', ..., v_dir_id, v_contacto_id)
RETURNING id INTO v_paciente_id;

-- 4. Insertar usuario
INSERT INTO users (username, password, role, paciente_id, empleado_id)
VALUES ('juan.perez@gmail.com', '$2a$10$...', 'PATIENT', v_paciente_id, NULL);
```

---

### 3.2. Registrar un Empleado

**Endpoint**: `POST /registro/empleado`

**Proceso**:
1. Crear registro en `CONTACTOS`
2. Crear registro en `DIRECCIONES`
3. Crear registro en `EMPLEADOS` (con `dir_id`, `contacto_id`, **y `cargo`**)
4. Crear registro en `USERS`:
   - `username` = email del empleado
   - `password` = hash BCrypt
   - `role` = **'LAB_EMPLOYEE'**
   - `empleado_id` = ID del empleado creado
   - `paciente_id` = **NULL**

**Ejemplo**:
```sql
-- 1. Insertar contacto
INSERT INTO contactos (...) VALUES (...) RETURNING id INTO v_contacto_id;

-- 2. Insertar dirección
INSERT INTO direcciones (...) VALUES (...) RETURNING id INTO v_dir_id;

-- 3. Insertar empleado (con CARGO profesional)
INSERT INTO empleados (pnombre, ..., cargo, dir_id, contacto_id)
VALUES ('Felipe', ..., 'Tecnólogo Médico', v_dir_id, v_contacto_id)
RETURNING id INTO v_empleado_id;

-- 4. Insertar usuario
INSERT INTO users (username, password, role, empleado_id, paciente_id)
VALUES ('felipe.munoz@laboratorioandino.cl', '$2a$10$...', 'LAB_EMPLOYEE', v_empleado_id, NULL);
```

---

### 3.3. Crear un Administrador (Solo ADMIN puede hacerlo)

**Endpoint**: `POST /users` (requiere rol ADMIN)

**Proceso**:
1. Crear registro en `USERS` directamente:
   - `username` = email del admin
   - `password` = hash BCrypt
   - `role` = **'ADMIN'**
   - `empleado_id` = **NULL** (o puede tener un empleado asociado)
   - `paciente_id` = **NULL**

**Ejemplo**:
```sql
-- Insertar usuario ADMIN (sin relación a empleado o paciente)
INSERT INTO users (username, password, role, empleado_id, paciente_id)
VALUES ('admin@laboratorioandino.cl', '$2a$10$...', 'ADMIN', NULL, NULL);
```

**Alternativa**: Un ADMIN puede ser también un empleado:
```sql
-- ADMIN que es también empleado
INSERT INTO users (username, password, role, empleado_id, paciente_id)
VALUES ('director@laboratorioandino.cl', '$2a$10$...', 'ADMIN', 1, NULL);
```

---

## 📊 4. Ejemplos de Datos

### Usuarios en la Base de Datos

| id | username | role | empleado_id | paciente_id | Tipo |
|----|----------|------|-------------|-------------|------|
| 1 | admin@laboratorioandino.cl | ADMIN | NULL | NULL | Administrador puro |
| 2 | felipe.munoz@laboratorioandino.cl | LAB_EMPLOYEE | 1 | NULL | Empleado (TM) |
| 3 | maria.gonzalez@laboratorioandino.cl | LAB_EMPLOYEE | 2 | NULL | Empleado (BQ) |
| 4 | juan.perez@gmail.com | PATIENT | NULL | 1 | Paciente |
| 5 | ana.lopez@gmail.com | PATIENT | NULL | 2 | Paciente |

### Empleados en la Base de Datos

| id | pnombre | papellido | cargo | Relación con USERS |
|----|---------|-----------|-------|-------------------|
| 1 | Felipe | Muñoz | Tecnólogo Médico | users.empleado_id = 1 |
| 2 | María | González | Bioquímico | users.empleado_id = 2 |
| 3 | Carlos | Ramírez | Recepcionista | users.empleado_id = 3 |

**Importante**: El campo `EMPLEADOS.cargo` es **solo informativo** y **NO afecta** los permisos del sistema.

---

## 🔑 5. Permisos por Rol del Sistema

### ADMIN
- ✅ Crear, leer, actualizar, eliminar **usuarios**
- ✅ Crear, leer, actualizar, eliminar **pacientes**
- ✅ Crear, leer, actualizar, eliminar **empleados**
- ✅ Acceso completo a todos los módulos

### LAB_EMPLOYEE
- ✅ Leer usuarios
- ✅ Leer pacientes
- ✅ Leer empleados
- ✅ Crear/leer resultados de exámenes
- ❌ NO puede crear/actualizar/eliminar usuarios, pacientes o empleados

### PATIENT
- ✅ Editar su propio perfil
- ✅ Consultar sus propios resultados de exámenes
- ✅ Solicitar exámenes
- ❌ NO puede acceder a datos de otros pacientes
- ❌ NO puede crear/actualizar/eliminar otros usuarios

---

## ⚠️ Errores Comunes a Evitar

### ❌ Error 1: Buscar empleados por cargo en lugar de por rol del sistema
```sql
-- ✅ CORRECTO: Buscar empleados por cargo profesional
SELECT * FROM empleados WHERE cargo LIKE '%Tecnólogo%';

-- ✅ CORRECTO: Buscar usuarios empleados
SELECT u.*, e.*
FROM users u
JOIN empleados e ON u.empleado_id = e.id
WHERE u.empleado_id IS NOT NULL;
```

---

### ❌ Error 2: Crear usuario sin relación a empleado/paciente
```sql
-- ❌ INCORRECTO: Usuario LAB_EMPLOYEE sin empleado_id
INSERT INTO users (username, password, role, empleado_id, paciente_id)
VALUES ('empleado@lab.cl', '$2a$10$...', 'LAB_EMPLOYEE', NULL, NULL);

-- ✅ CORRECTO: Usuario LAB_EMPLOYEE con empleado_id
INSERT INTO users (username, password, role, empleado_id, paciente_id)
VALUES ('empleado@lab.cl', '$2a$10$...', 'LAB_EMPLOYEE', 1, NULL);
```

---

### ❌ Error 3: Usuario con ambos IDs (empleado_id Y paciente_id)
```sql
-- ❌ INCORRECTO: No puede ser empleado Y paciente a la vez
INSERT INTO users (username, password, role, empleado_id, paciente_id)
VALUES ('usuario@lab.cl', '$2a$10$...', 'LAB_EMPLOYEE', 1, 1);

-- ✅ CORRECTO: Solo uno de los dos
INSERT INTO users (username, password, role, empleado_id, paciente_id)
VALUES ('usuario@lab.cl', '$2a$10$...', 'LAB_EMPLOYEE', 1, NULL);
```

---

## 🔍 6. Consultas Útiles

### Obtener todos los usuarios empleados con su cargo
```sql
SELECT 
  u.id,
  u.username,
  u.role AS rol_sistema,
  e.pnombre || ' ' || e.papellido AS nombre_completo,
  e.cargo,
  e.rol AS cargo_profesional
FROM users u
JOIN empleados e ON u.empleado_id = e.id
WHERE u.empleado_id IS NOT NULL;
```

### Obtener todos los usuarios pacientes
```sql
SELECT 
  u.id,
  u.username,
  u.role AS rol_sistema,
  p.pnombre || ' ' || p.papellido AS nombre_completo,
  p.fecha_nacimiento
FROM users u
JOIN pacientes p ON u.paciente_id = p.id
WHERE u.paciente_id IS NOT NULL;
```

### Verificar integridad de roles
```sql
-- Verificar que LAB_EMPLOYEE tenga empleado_id
SELECT * FROM users 
WHERE role = 'LAB_EMPLOYEE' AND empleado_id IS NULL;

-- Verificar que PATIENT tenga paciente_id
SELECT * FROM users 
WHERE role = 'PATIENT' AND paciente_id IS NULL;

-- Verificar que no haya usuarios con ambos IDs
SELECT * FROM users 
WHERE empleado_id IS NOT NULL AND paciente_id IS NOT NULL;
```

---

## 📚 Resumen

| Concepto | Tabla | Campo | Valores | Propósito |
|----------|-------|-------|---------|-----------|
| **Rol del Sistema** | `USERS` | `role` | ADMIN, LAB_EMPLOYEE, PATIENT | Permisos de acceso |
| **Cargo Profesional** | `EMPLEADOS` | `cargo` | Texto libre (ej: "Tecnólogo Médico") | Información del empleado (NO afecta permisos) |
| **Tipo de Usuario** | `USERS` | `empleado_id` / `paciente_id` | FK a EMPLEADOS / PACIENTES | Clasificación del usuario |

---

**Autor**: Microservicio de Gestión de Usuarios  
**Versión**: 1.0  
**Fecha**: Noviembre 2024
