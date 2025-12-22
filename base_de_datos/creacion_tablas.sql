-- =========================================================
-- SCHEMA: LABS & USERS - ORACLE 19c (Autonomous)
-- Compatible con ms_gestion_users (8082) y ms_gestion_labs (8081)
-- Microservicios Spring Boot 3.5.7 + JPA + Hibernate
-- =========================================================
--
-- 🔥 ELIMINACIÓN EN CASCADA CONFIGURADA:
-- =========================================================
-- Al eliminar un PACIENTE, se eliminan automáticamente:
--   ✅ Usuario asociado (USERS)
--   ✅ Agendas de exámenes (AGENDA_EXAMEN)
--   ✅ Resultados de exámenes (RESULTADO_EXAMEN)
--   ✅ Dirección del paciente (DIRECCIONES)
--   ✅ Contacto del paciente (CONTACTOS)
--
-- Al eliminar un EMPLEADO, se eliminan automáticamente:
--   ✅ Usuario asociado (USERS)
--   ✅ Dirección del empleado (DIRECCIONES)
--   ✅ Contacto del empleado (CONTACTOS)
--   ⚠️ En AGENDA_EXAMEN.empleado_id → se pone NULL (conserva historial)
--   ⚠️ En RESULTADO_EXAMEN.empleado_id → se pone NULL (conserva historial)
-- =========================================================

------------------------------------------------------------
-- 1) SECUENCIAS (10 secuencias)
------------------------------------------------------------
CREATE SEQUENCE seq_contactos        START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_direcciones      START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_laboratorios     START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_pacientes        START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_empleados        START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_examenes         START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_lab_exam         START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_agenda_examen    START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_resultado_examen START WITH 1 INCREMENT BY 1 CACHE 100;
CREATE SEQUENCE seq_users            START WITH 1 INCREMENT BY 1 CACHE 100;

------------------------------------------------------------
-- 2) TABLAS BASE (contactos, direcciones)
------------------------------------------------------------
CREATE TABLE contactos (
  id          NUMBER DEFAULT seq_contactos.NEXTVAL PRIMARY KEY,
  fono1       VARCHAR2(12) NOT NULL,
  fono2       VARCHAR2(12),
  email       VARCHAR2(50)
);

CREATE TABLE direcciones (
  id       NUMBER DEFAULT seq_direcciones.NEXTVAL PRIMARY KEY,
  calle    VARCHAR2(50),
  numero   NUMBER(5),
  ciudad   VARCHAR2(40),
  comuna   VARCHAR2(40),
  region   VARCHAR2(60)
);

------------------------------------------------------------
-- 3) ENTIDADES PRINCIPALES (pacientes, empleados, laboratorios, examenes)
------------------------------------------------------------
CREATE TABLE pacientes (
  id           NUMBER DEFAULT seq_pacientes.NEXTVAL PRIMARY KEY,
  pnombre      VARCHAR2(20) NOT NULL,
  snombre      VARCHAR2(20),
  papellido    VARCHAR2(20) NOT NULL,
  sapellido    VARCHAR2(20),
  rut          VARCHAR2(10),
  creado_en    TIMESTAMP DEFAULT SYSTIMESTAMP,
  dir_id       NUMBER NOT NULL,
  contacto_id  NUMBER NOT NULL,
  CONSTRAINT fk_paci_dir  FOREIGN KEY (dir_id)      REFERENCES direcciones(id) ON DELETE CASCADE,
  CONSTRAINT fk_paci_cont FOREIGN KEY (contacto_id) REFERENCES contactos(id) ON DELETE CASCADE
);

CREATE TABLE empleados (
  id           NUMBER DEFAULT seq_empleados.NEXTVAL PRIMARY KEY,
  pnombre      VARCHAR2(20) NOT NULL,
  snombre      VARCHAR2(20),
  papellido    VARCHAR2(20) NOT NULL,
  sapellido    VARCHAR2(20),
  rut          VARCHAR2(10),
  cargo        VARCHAR2(40),
  creado_en    TIMESTAMP DEFAULT SYSTIMESTAMP,
  dir_id       NUMBER NOT NULL,
  contacto_id  NUMBER NOT NULL,
  CONSTRAINT fk_emple_dir  FOREIGN KEY (dir_id)      REFERENCES direcciones(id) ON DELETE CASCADE,
  CONSTRAINT fk_emple_cont FOREIGN KEY (contacto_id) REFERENCES contactos(id) ON DELETE CASCADE
);

CREATE TABLE laboratorios (
  id           NUMBER DEFAULT seq_laboratorios.NEXTVAL PRIMARY KEY,
  nombre       VARCHAR2(50),
  tipo         VARCHAR2(20),
  dir_id       NUMBER NOT NULL,
  contacto_id  NUMBER NOT NULL,
  CONSTRAINT fk_labs_dir  FOREIGN KEY (dir_id)      REFERENCES direcciones(id),
  CONSTRAINT fk_labs_cont FOREIGN KEY (contacto_id) REFERENCES contactos(id)
);

CREATE TABLE examenes (
  id      NUMBER DEFAULT seq_examenes.NEXTVAL PRIMARY KEY,
  codigo  VARCHAR2(4),
  nombre  VARCHAR2(50),
  tipo    VARCHAR2(20)
);

------------------------------------------------------------
-- 4) USUARIOS (tabla users para autenticación JWT)
------------------------------------------------------------
CREATE TABLE users (
  id           NUMBER DEFAULT seq_users.NEXTVAL PRIMARY KEY,
  username     VARCHAR2(100) NOT NULL UNIQUE,
  password     VARCHAR2(255) NOT NULL,
  role         VARCHAR2(20),
  estado       VARCHAR2(20) DEFAULT 'ACTIVO',
  paciente_id  NUMBER,
  empleado_id  NUMBER,
  creado_en    TIMESTAMP DEFAULT SYSTIMESTAMP,
  password_temporal CHAR(1) DEFAULT 'N' CHECK (password_temporal IN ('S', 'N')),
  CONSTRAINT fk_users_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE CASCADE,
  CONSTRAINT fk_users_empleado FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE CASCADE
);

------------------------------------------------------------
-- 5) RELACIÓN N:M ENTRE LABORATORIOS Y EXÁMENES
------------------------------------------------------------
CREATE TABLE lab_exam (
  id              NUMBER DEFAULT seq_lab_exam.NEXTVAL PRIMARY KEY,
  id_laboratorio  NUMBER NOT NULL,
  id_examen       NUMBER NOT NULL,
  precio          NUMBER(8,2) NOT NULL,
  vigente_desde   DATE,
  vigente_hasta   DATE,
  CONSTRAINT fk_lab_exam_lab FOREIGN KEY (id_laboratorio) REFERENCES laboratorios(id),
  CONSTRAINT fk_lab_exam_exa FOREIGN KEY (id_examen)       REFERENCES examenes(id),
  CONSTRAINT ck_lab_exam_vigencia CHECK (vigente_hasta IS NULL OR vigente_hasta > vigente_desde)
);

------------------------------------------------------------
-- 6) AGENDA DE EXÁMENES
------------------------------------------------------------
CREATE TABLE agenda_examen (
  id           NUMBER DEFAULT seq_agenda_examen.NEXTVAL PRIMARY KEY,
  paciente_id  NUMBER NOT NULL,
  lab_id       NUMBER NOT NULL,
  examen_id    NUMBER NOT NULL,
  empleado_id  NUMBER,
  fecha_hora   TIMESTAMP(6) NOT NULL,
  estado       VARCHAR2(20) DEFAULT 'PROGRAMADA' NOT NULL,
  creado_en    TIMESTAMP(6) DEFAULT SYSTIMESTAMP,
  CONSTRAINT fk_agenda_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE CASCADE,
  CONSTRAINT fk_agenda_lab      FOREIGN KEY (lab_id)      REFERENCES laboratorios(id),
  CONSTRAINT fk_agenda_examen   FOREIGN KEY (examen_id)   REFERENCES examenes(id),
  CONSTRAINT fk_agenda_empleado FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE SET NULL,
  CONSTRAINT ck_agenda_estado   CHECK (estado IN ('PROGRAMADA','CANCELADA','ATENDIDA'))
);

------------------------------------------------------------
-- 7) RESULTADOS DE EXÁMENES
------------------------------------------------------------
CREATE TABLE resultado_examen (
  id              NUMBER DEFAULT seq_resultado_examen.NEXTVAL PRIMARY KEY,
  agenda_id       NUMBER NOT NULL,
  paciente_id     NUMBER NOT NULL,
  lab_id          NUMBER NOT NULL,
  examen_id       NUMBER NOT NULL,
  empleado_id     NUMBER,  -- Puede ser NULL si el empleado se elimina
  fecha_muestra   TIMESTAMP WITH TIME ZONE,
  fecha_resultado TIMESTAMP WITH TIME ZONE,
  valor           VARCHAR2(2000),
  unidad          VARCHAR2(50),
  observacion     VARCHAR2(2000),
  estado          VARCHAR2(20) DEFAULT 'PENDIENTE' NOT NULL,
  CONSTRAINT uq_resultado_agenda UNIQUE (agenda_id),
  CONSTRAINT fk_res_agenda   FOREIGN KEY (agenda_id)   REFERENCES agenda_examen(id) ON DELETE CASCADE,
  CONSTRAINT fk_res_paciente FOREIGN KEY (paciente_id) REFERENCES pacientes(id) ON DELETE CASCADE,
  CONSTRAINT fk_res_lab      FOREIGN KEY (lab_id)      REFERENCES laboratorios(id),
  CONSTRAINT fk_res_examen   FOREIGN KEY (examen_id)   REFERENCES examenes(id),
  CONSTRAINT fk_res_empleado FOREIGN KEY (empleado_id) REFERENCES empleados(id) ON DELETE SET NULL,
  CONSTRAINT ck_res_estado   CHECK (estado IN ('PENDIENTE','EMITIDO','ANULADO'))
);

------------------------------------------------------------
-- 8) ÍNDICES DE RENDIMIENTO (claves foráneas)
------------------------------------------------------------
CREATE INDEX IX_LABS_DIR        ON laboratorios (dir_id);
CREATE INDEX IX_LABS_CONTACTO   ON laboratorios (contacto_id);
CREATE INDEX IX_PAC_DIR         ON pacientes (dir_id);
CREATE INDEX IX_PAC_CONTACTO    ON pacientes (contacto_id);
CREATE INDEX IX_EMP_DIR         ON empleados (dir_id);
CREATE INDEX IX_EMP_CONTACTO    ON empleados (contacto_id);
CREATE INDEX IX_LAB_EXAM_LAB    ON lab_exam (id_laboratorio);
CREATE INDEX IX_LAB_EXAM_EXA    ON lab_exam (id_examen);
CREATE INDEX IX_AGENDA_PACIENTE ON agenda_examen (paciente_id);
CREATE INDEX IX_AGENDA_LAB      ON agenda_examen (lab_id);
CREATE INDEX IX_AGENDA_EXAMEN   ON agenda_examen (examen_id);
CREATE INDEX IX_RES_PACIENTE    ON resultado_examen (paciente_id);
CREATE INDEX IX_RES_LAB         ON resultado_examen (lab_id);
CREATE INDEX IX_RES_EXAMEN      ON resultado_examen (examen_id);

PROMPT ========================================;
PROMPT Esquema creado: 10 Secuencias + 10 Tablas + 14 Indices;
PROMPT Insertando datos de prueba...;
PROMPT ========================================;

-- ======================================
-- POBLACIÓN INICIAL (DATOS DE PRUEBA)
-- ======================================

------------------------------------------------------------
-- 1) CONTACTOS (9)
------------------------------------------------------------
-- Laboratorios
INSERT INTO contactos (fono1, fono2, email) VALUES ('+56993000001', NULL,           'contacto@laboratorioandino.cl');
INSERT INTO contactos (fono1, fono2, email) VALUES ('+56993000002', '+56993010002', 'contacto@clinicabiosalud.cl');
INSERT INTO contactos (fono1, fono2, email) VALUES ('+56993000003', NULL,           'contacto@centrodiagnosticopacifico.cl');

-- Pacientes
INSERT INTO contactos (fono1, fono2, email) VALUES ('+56993000004', NULL,           'camila.rojas@correo.cl');
INSERT INTO contactos (fono1, fono2, email) VALUES ('+56993000005', NULL,           'benjamin.gonzalez@correo.cl');
INSERT INTO contactos (fono1, fono2, email) VALUES ('+56993000006', '+56993010006', 'isidora.munoz@correo.cl');

-- Empleados
INSERT INTO contactos (fono1, fono2, email) VALUES ('+56993000007', NULL,           'felipe.munoz@laboratorioandino.cl');
INSERT INTO contactos (fono1, fono2, email) VALUES ('+56993000008', NULL,           'constanza.araya@clinicabiosalud.cl');
INSERT INTO contactos (fono1, fono2, email) VALUES ('+56993000009', '+56993010009', 'matias.carrasco@cdpacifico.cl');

------------------------------------------------------------
-- 2) DIRECCIONES (9)
------------------------------------------------------------
-- Laboratorios (ciudades distintas)
INSERT INTO direcciones (calle, numero, ciudad, comuna, region)
VALUES ('Av. Libertador Bernardo O''Higgins', 1449, 'Santiago', 'Santiago Centro', 'Metropolitana');

INSERT INTO direcciones (calle, numero, ciudad, comuna, region)
VALUES ('Av. Alemania', 675, 'Temuco', 'Temuco', 'La Araucanía');

INSERT INTO direcciones (calle, numero, ciudad, comuna, region)
VALUES ('Av. Arturo Prat', 830, 'Concepción', 'Concepción', 'Biobío');

-- Pacientes (mismas ciudades que los laboratorios)
INSERT INTO direcciones (calle, numero, ciudad, comuna, region)
VALUES ('Av. Providencia', 3000, 'Santiago', 'Providencia', 'Metropolitana');

INSERT INTO direcciones (calle, numero, ciudad, comuna, region)
VALUES ('Av. Caupolicán', 1500, 'Temuco', 'Temuco', 'La Araucanía');

INSERT INTO direcciones (calle, numero, ciudad, comuna, region)
VALUES ('Calle O''Higgins', 220, 'Concepción', 'Concepción', 'Biobío');

-- Empleados (mismas ciudades que los laboratorios)
INSERT INTO direcciones (calle, numero, ciudad, comuna, region)
VALUES ('Av. Manuel Montt', 1010, 'Santiago', 'Providencia', 'Metropolitana');

INSERT INTO direcciones (calle, numero, ciudad, comuna, region)
VALUES ('Av. Caupolicán', 1435, 'Temuco', 'Temuco', 'La Araucanía');

INSERT INTO direcciones (calle, numero, ciudad, comuna, region)
VALUES ('Calle O''Higgins', 2020, 'Concepción', 'Concepción', 'Biobío');

------------------------------------------------------------
-- 3) LABORATORIOS (3)
------------------------------------------------------------
INSERT INTO laboratorios (nombre, tipo, dir_id, contacto_id)
VALUES (
  'Laboratorio Andino', 'Privado',
  (SELECT id FROM direcciones WHERE ciudad = 'Santiago' AND calle = 'Av. Libertador Bernardo O''Higgins'),
  (SELECT id FROM contactos   WHERE email = 'contacto@laboratorioandino.cl')
);

INSERT INTO laboratorios (nombre, tipo, dir_id, contacto_id)
VALUES (
  'Clínica BioSalud', 'Clínica',
  (SELECT id FROM direcciones WHERE ciudad = 'Temuco' AND calle = 'Av. Alemania'),
  (SELECT id FROM contactos   WHERE email = 'contacto@clinicabiosalud.cl')
);

INSERT INTO laboratorios (nombre, tipo, dir_id, contacto_id)
VALUES (
  'Centro Diagnóstico Pacífico', 'Centro',
  (SELECT id FROM direcciones WHERE ciudad = 'Concepción' AND calle = 'Av. Arturo Prat'),
  (SELECT id FROM contactos   WHERE email = 'contacto@centrodiagnosticopacifico.cl')
);

------------------------------------------------------------
-- 4) PACIENTES (3)  [alineados por ciudad]
------------------------------------------------------------
-- Santiago → Laboratorio Andino
INSERT INTO pacientes (pnombre, snombre, papellido, sapellido, rut, dir_id, contacto_id)
VALUES (
  'Camila', 'Andrea', 'Rojas', 'Díaz', '17345678-5',
  (SELECT id FROM direcciones WHERE ciudad = 'Santiago' AND calle = 'Av. Providencia'),
  (SELECT id FROM contactos   WHERE email = 'camila.rojas@correo.cl')
);

-- Temuco → Clínica BioSalud
INSERT INTO pacientes (pnombre, snombre, papellido, sapellido, rut, dir_id, contacto_id)
VALUES (
  'Benjamín', 'Alejandro', 'González', 'Pérez', '15234567-8',
  (SELECT id FROM direcciones WHERE ciudad = 'Temuco' AND calle = 'Av. Caupolicán' AND numero = 1500),
  (SELECT id FROM contactos   WHERE email = 'benjamin.gonzalez@correo.cl')
);

-- Concepción → Centro Diagnóstico Pacífico
INSERT INTO pacientes (pnombre, snombre, papellido, sapellido, rut, dir_id, contacto_id)
VALUES (
  'Isidora', 'Belén', 'Muñoz', 'Valdés', '20456789-3',
  (SELECT id FROM direcciones WHERE ciudad = 'Concepción' AND calle = 'Calle O''Higgins' AND numero = 220),
  (SELECT id FROM contactos   WHERE email = 'isidora.munoz@correo.cl')
);

------------------------------------------------------------
-- 5) EMPLEADOS (3)  [empleados de cada laboratorio/ciudad]
------------------------------------------------------------
-- Santiago → Laboratorio Andino
INSERT INTO empleados (pnombre, snombre, papellido, sapellido, rut, cargo, dir_id, contacto_id)
VALUES (
  'Felipe', 'Ignacio', 'Muñoz', 'Pérez', '19567834-1',
  'Tecnólogo Médico',
  (SELECT id FROM direcciones WHERE ciudad = 'Santiago' AND calle = 'Av. Manuel Montt'),
  (SELECT id FROM contactos   WHERE email = 'felipe.munoz@laboratorioandino.cl')
);

-- Temuco → Clínica BioSalud
INSERT INTO empleados (pnombre, snombre, papellido, sapellido, rut, cargo, dir_id, contacto_id)
VALUES (
  'Constanza', 'Soledad', 'Araya', 'Fuentes', '18345678-2',
  'Bioquímico',
  (SELECT id FROM direcciones WHERE ciudad = 'Temuco' AND calle = 'Av. Caupolicán' AND numero = 1435),
  (SELECT id FROM contactos   WHERE email = 'constanza.araya@clinicabiosalud.cl')
);

-- Concepción → Centro Diagnóstico Pacífico
INSERT INTO empleados (pnombre, snombre, papellido, sapellido, rut, cargo, dir_id, contacto_id)
VALUES (
  'Matías', 'Antonio', 'Carrasco', 'Silva', '16789012-7',
  'Recepcionista',
  (SELECT id FROM direcciones WHERE ciudad = 'Concepción' AND calle = 'Calle O''Higgins' AND numero = 2020),
  (SELECT id FROM contactos   WHERE email = 'matias.carrasco@cdpacifico.cl')
);

------------------------------------------------------------
-- 6) EXÁMENES (3)
------------------------------------------------------------
INSERT INTO examenes (codigo, nombre, tipo) VALUES ('HEMO', 'Hemograma completo',  'Sangre');
INSERT INTO examenes (codigo, nombre, tipo) VALUES ('GLUC', 'Glicemia en ayunas',  'Sangre');
INSERT INTO examenes (codigo, nombre, tipo) VALUES ('PCR',  'Proteína C Reactiva', 'Sangre');

------------------------------------------------------------
-- 7) LAB_EXAM (precios N:M)  [pocos, claros]
------------------------------------------------------------
INSERT INTO lab_exam (id_laboratorio, id_examen, precio, vigente_desde, vigente_hasta)
VALUES (
  (SELECT id FROM laboratorios WHERE nombre = 'Laboratorio Andino'),
  (SELECT id FROM examenes     WHERE codigo = 'HEMO'),
  8500, DATE '2025-11-01', NULL
);

INSERT INTO lab_exam (id_laboratorio, id_examen, precio, vigente_desde, vigente_hasta)
VALUES (
  (SELECT id FROM laboratorios WHERE nombre = 'Clínica BioSalud'),
  (SELECT id FROM examenes     WHERE codigo = 'GLUC'),
  3500, DATE '2025-11-01', NULL
);

INSERT INTO lab_exam (id_laboratorio, id_examen, precio, vigente_desde, vigente_hasta)
VALUES (
  (SELECT id FROM laboratorios WHERE nombre = 'Centro Diagnóstico Pacífico'),
  (SELECT id FROM examenes     WHERE codigo = 'PCR'),
  9000, DATE '2025-11-01', NULL
);

------------------------------------------------------------
-- 8) AGENDA_EXAMEN (3)  [una agenda por paciente]
------------------------------------------------------------
-- Camila (Santiago) – Hemograma en Laboratorio Andino con Felipe
INSERT INTO agenda_examen (paciente_id, lab_id, examen_id, empleado_id, fecha_hora, estado)
VALUES (
  (SELECT id FROM pacientes    WHERE rut = '17345678-5'),
  (SELECT id FROM laboratorios WHERE nombre = 'Laboratorio Andino'),
  (SELECT id FROM examenes     WHERE codigo = 'HEMO'),
  (SELECT id FROM empleados    WHERE rut = '19567834-1'),
  TIMESTAMP '2025-11-10 10:00:00 -03:00',
  'ATENDIDA'
);

-- Benjamín (Temuco) – Glicemia en Clínica BioSalud con Constanza
INSERT INTO agenda_examen (paciente_id, lab_id, examen_id, empleado_id, fecha_hora, estado)
VALUES (
  (SELECT id FROM pacientes    WHERE rut = '15234567-8'),
  (SELECT id FROM laboratorios WHERE nombre = 'Clínica BioSalud'),
  (SELECT id FROM examenes     WHERE codigo = 'GLUC'),
  (SELECT id FROM empleados    WHERE rut = '18345678-2'),
  TIMESTAMP '2025-11-10 11:00:00 -03:00',
  'ATENDIDA'
);

-- Isidora (Concepción) – PCR en Centro Diagnóstico Pacífico (aún programada)
INSERT INTO agenda_examen (paciente_id, lab_id, examen_id, empleado_id, fecha_hora, estado)
VALUES (
  (SELECT id FROM pacientes    WHERE rut = '20456789-3'),
  (SELECT id FROM laboratorios WHERE nombre = 'Centro Diagnóstico Pacífico'),
  (SELECT id FROM examenes     WHERE codigo = 'PCR'),
  (SELECT id FROM empleados    WHERE rut = '16789012-7'),
  TIMESTAMP '2025-11-12 09:30:00 -03:00',
  'PROGRAMADA'
);
------------------------------------------------------------
-- 9) RESULTADO_EXAMEN (2)  [no todas las agendas tienen resultado]
------------------------------------------------------------
-- Resultado de Camila – Hemograma (emitido)
INSERT INTO resultado_examen (
  agenda_id, paciente_id, lab_id, examen_id, empleado_id,
  fecha_muestra, fecha_resultado, valor, unidad, observacion, estado
)
VALUES (
  (SELECT id FROM agenda_examen WHERE paciente_id = (SELECT id FROM pacientes WHERE rut = '17345678-5')),
  (SELECT id FROM pacientes    WHERE rut = '17345678-5'),
  (SELECT id FROM laboratorios WHERE nombre = 'Laboratorio Andino'),
  (SELECT id FROM examenes     WHERE codigo = 'HEMO'),
  (SELECT id FROM empleados    WHERE rut = '19567834-1'),
  TIMESTAMP '2025-11-10 10:05:00 -03:00',
  TIMESTAMP '2025-11-10 10:30:00 -03:00',
  '13.5', 'g/dL', 'Valores dentro de rango', 'EMITIDO'
);

-- Resultado de Benjamín – Glicemia (emitido)
INSERT INTO resultado_examen (
  agenda_id, paciente_id, lab_id, examen_id, empleado_id,
  fecha_muestra, fecha_resultado, valor, unidad, observacion, estado
)
VALUES (
  (SELECT id FROM agenda_examen WHERE paciente_id = (SELECT id FROM pacientes WHERE rut = '15234567-8')),
  (SELECT id FROM pacientes    WHERE rut = '15234567-8'),
  (SELECT id FROM laboratorios WHERE nombre = 'Clínica BioSalud'),
  (SELECT id FROM examenes     WHERE codigo = 'GLUC'),
  (SELECT id FROM empleados    WHERE rut = '18345678-2'),
  TIMESTAMP '2025-11-10 11:05:00 -03:00',
  TIMESTAMP '2025-11-10 11:20:00 -03:00',
  '92', 'mg/dL', 'Ayunas correctas', 'EMITIDO'
);

-- (La agenda de Isidora queda PROGRAMADA sin resultado)

------------------------------------------------------------
-- 10) USUARIOS (6 users: 3 empleados + 3 pacientes)
------------------------------------------------------------
-- Password para todos: "12345" encriptado con BCrypt
-- $2a$10$jOX1cjWTiVqGXUtD/0AQ4.c8jYCKLwpFQ9iNHCmzWec3I7jte4YDS

-- Usuarios EMPLEADOS
INSERT INTO users (username, password, role, empleado_id)
VALUES (
  'felipe.munoz@laboratorioandino.cl',
  '$2a$10$jOX1cjWTiVqGXUtD/0AQ4.c8jYCKLwpFQ9iNHCmzWec3I7jte4YDS',
  'EMPLOYEE',
  (SELECT e.id FROM empleados e JOIN contactos c ON e.contacto_id = c.id WHERE c.email = 'felipe.munoz@laboratorioandino.cl')
);

INSERT INTO users (username, password, role, empleado_id)
VALUES (
  'constanza.araya@clinicabiosalud.cl',
  '$2a$10$jOX1cjWTiVqGXUtD/0AQ4.c8jYCKLwpFQ9iNHCmzWec3I7jte4YDS',
  'EMPLOYEE',
  (SELECT e.id FROM empleados e JOIN contactos c ON e.contacto_id = c.id WHERE c.email = 'constanza.araya@clinicabiosalud.cl')
);

INSERT INTO users (username, password, role, empleado_id)
VALUES (
  'matias.carrasco@cdpacifico.cl',
  '$2a$10$jOX1cjWTiVqGXUtD/0AQ4.c8jYCKLwpFQ9iNHCmzWec3I7jte4YDS',
  'EMPLOYEE',
  (SELECT e.id FROM empleados e JOIN contactos c ON e.contacto_id = c.id WHERE c.email = 'matias.carrasco@cdpacifico.cl')
);

-- Usuarios PACIENTES
INSERT INTO users (username, password, role, paciente_id)
VALUES (
  'camila.rojas@correo.cl',
  '$2a$10$jOX1cjWTiVqGXUtD/0AQ4.c8jYCKLwpFQ9iNHCmzWec3I7jte4YDS',
  'PATIENT',
  (SELECT p.id FROM pacientes p JOIN contactos c ON p.contacto_id = c.id WHERE c.email = 'camila.rojas@correo.cl')
);

INSERT INTO users (username, password, role, paciente_id)
VALUES (
  'benjamin.gonzalez@correo.cl',
  '$2a$10$jOX1cjWTiVqGXUtD/0AQ4.c8jYCKLwpFQ9iNHCmzWec3I7jte4YDS',
  'PATIENT',
  (SELECT p.id FROM pacientes p JOIN contactos c ON p.contacto_id = c.id WHERE c.email = 'benjamin.gonzalez@correo.cl')
);

INSERT INTO users (username, password, role, paciente_id)
VALUES (
  'isidora.munoz@correo.cl',
  '$2a$10$jOX1cjWTiVqGXUtD/0AQ4.c8jYCKLwpFQ9iNHCmzWec3I7jte4YDS',
  'PATIENT',
  (SELECT p.id FROM pacientes p JOIN contactos c ON p.contacto_id = c.id WHERE c.email = 'isidora.munoz@correo.cl')
);

-- Password: "admin123" encriptado con BCrypt
-- $2a$10$l17KXBDQ7UxPOX19bps2kOHIGNc6IxK0sX4QiLyozLYUTjErm7MDK
INSERT INTO users (id, username, password, role, estado, empleado_id, paciente_id, creado_en)
VALUES (
  seq_users.NEXTVAL,
  'admin@laboratorioandino.cl',
  '$2a$10$l17KXBDQ7UxPOX19bps2kOHIGNc6IxK0sX4QiLyozLYUTjErm7MDK',
  'ADMIN',              -- ROL DEL SISTEMA
  'ACTIVO',
  NULL,                 -- No tiene empleado_id (ADMIN puro)
  NULL,                 -- No tiene paciente_id
  SYSTIMESTAMP
);

COMMIT;

PROMPT ========================================;
PROMPT Base de datos inicializada exitosamente!;
PROMPT ========================================;
PROMPT;
PROMPT Resumen de datos insertados:;
PROMPT - 9 Contactos;
PROMPT - 9 Direcciones;
PROMPT - 3 Laboratorios;
PROMPT - 3 Pacientes;
PROMPT - 3 Empleados;
PROMPT - 3 Examenes;
PROMPT - 3 Lab_Exam (precios);
PROMPT - 3 Agendas;
PROMPT - 2 Resultados;
PROMPT - 7 Usuarios;
PROMPT ========================================;
PROMPT;
PROMPT Credenciales de prueba:;
PROMPT Username: felipe.munoz@laboratorioandino.cl;
PROMPT Password: 12345;
PROMPT ========================================;
PROMPT Credenciales de administrador:;
PROMPT Username: admin@laboratorioandino.cl;
PROMPT Password: admin123;
PROMPT ========================================;
PROMPT;
PROMPT 🔥 CASCADE DELETE CONFIGURADO:;
PROMPT ========================================;
PROMPT Al eliminar PACIENTE -> Se elimina:;
PROMPT   - Usuario asociado;
PROMPT   - Agendas de examenes;
PROMPT   - Resultados de examenes;
PROMPT   - Direccion y contacto;
PROMPT;
PROMPT Al eliminar EMPLEADO -> Se elimina:;
PROMPT   - Usuario asociado;
PROMPT   - Direccion y contacto;
PROMPT   - SET NULL en agendas/resultados;
PROMPT ========================================;