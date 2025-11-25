-- =========================================================
-- SCRIPT PARA LIMPIAR COMPLETAMENTE LA BASE DE DATOS
-- Elimina: TABLAS, SECUENCIAS, ÍNDICES y PAPELERA
-- =========================================================
-- ADVERTENCIA: Este script elimina TODOS los datos permanentemente
-- Úsalo solo cuando quieras resetear completamente la BD
-- =========================================================

PROMPT ========================================;
PROMPT Limpiando Base de Datos...;
PROMPT ========================================;

-- =========================================================
-- 1. ELIMINAR TABLAS EN ORDEN (respetando FKs)
-- =========================================================
PROMPT Eliminando tablas...;

DROP TABLE users CASCADE CONSTRAINTS PURGE;
DROP TABLE resultado_examen CASCADE CONSTRAINTS PURGE;
DROP TABLE agenda_examen CASCADE CONSTRAINTS PURGE;
DROP TABLE lab_exam CASCADE CONSTRAINTS PURGE;
DROP TABLE empleados CASCADE CONSTRAINTS PURGE;
DROP TABLE pacientes CASCADE CONSTRAINTS PURGE;
DROP TABLE laboratorios CASCADE CONSTRAINTS PURGE;
DROP TABLE examenes CASCADE CONSTRAINTS PURGE;
DROP TABLE direcciones CASCADE CONSTRAINTS PURGE;
DROP TABLE contactos CASCADE CONSTRAINTS PURGE;

-- =========================================================
-- 2. ELIMINAR SECUENCIAS
-- =========================================================
PROMPT Eliminando secuencias...;

DROP SEQUENCE seq_contactos;
DROP SEQUENCE seq_direcciones;
DROP SEQUENCE seq_laboratorios;
DROP SEQUENCE seq_pacientes;
DROP SEQUENCE seq_empleados;
DROP SEQUENCE seq_examenes;
DROP SEQUENCE seq_lab_exam;
DROP SEQUENCE seq_agenda_examen;
DROP SEQUENCE seq_resultado_examen;
DROP SEQUENCE seq_users;

-- =========================================================
-- 3. ELIMINAR ÍNDICES EXPLÍCITOS
-- =========================================================
PROMPT Eliminando indices explicitos...;

-- DROP INDEX IX_AGENDA_EXAMEN;
-- DROP INDEX IX_AGENDA_LAB;
-- DROP INDEX IX_AGENDA_PACIENTE;
-- DROP INDEX IX_EMP_CONTACTO;
-- DROP INDEX IX_EMP_DIR;
-- DROP INDEX IX_LABS_CONTACTO;
-- DROP INDEX IX_LABS_DIR;
-- DROP INDEX IX_LAB_EXAM_EXA;
-- DROP INDEX IX_LAB_EXAM_LAB;
-- DROP INDEX IX_PAC_CONTACTO;
-- DROP INDEX IX_PAC_DIR;
-- DROP INDEX IX_RES_EXAMEN;
-- DROP INDEX IX_RES_LAB;
-- DROP INDEX IX_RES_PACIENTE;

-- =========================================================
-- 4. PURGAR PAPELERA
-- =========================================================
PROMPT Purgando papelera de reciclaje...;
PURGE RECYCLEBIN;

-- =========================================================
-- 5. VERIFICACIÓN FINAL
-- =========================================================
PROMPT ========================================;
PROMPT Verificacion de limpieza...;
PROMPT ========================================;

PROMPT;
PROMPT Tablas restantes en el esquema:;
SELECT table_name FROM user_tables ORDER BY table_name;

PROMPT;
PROMPT Secuencias restantes:;
SELECT sequence_name FROM user_sequences ORDER BY sequence_name;

PROMPT;
PROMPT Indices restantes (IX_*):;
SELECT index_name FROM user_indexes WHERE index_name LIKE 'IX_%' ORDER BY index_name;

PROMPT;
PROMPT ========================================;
PROMPT Limpieza completada;
PROMPT ========================================;
PROMPT;
PROMPT La base de datos esta limpia.;
PROMPT Ahora puedes ejecutar el script de creacion.;
PROMPT ========================================;

COMMIT;
