# 🗄️ Base de Datos - Sistema de Gestión de Laboratorios

Documentación completa de la estructura y configuración de la base de datos Oracle Autonomous Database.

---

## 📋 Tabla de Contenidos

- [Configuración de Base de Datos](#-configuración-de-base-de-datos)
- [Estructura de Tablas](#-estructura-de-tablas)
- [Relaciones y Cascadas](#-relaciones-y-cascadas)
- [Secuencias Oracle](#-secuencias-oracle)
- [Configuración de Conexión](#️-configuración-de-conexión)
- [Scripts de Base de Datos](#-scripts-de-base-de-datos)

---

## 🔧 Configuración de Base de Datos

### Oracle Autonomous Database (OCI)

**Configuración:**
- **Tipo:** Oracle Autonomous Database (19c)
- **Ubicación:** Oracle Cloud Infrastructure (OCI)
- **Conexión:** Mediante Oracle Wallet (SSL/TLS)
- **Pool de conexiones:** Oracle UCP (Universal Connection Pool)

---

## 📊 Estructura de Tablas

El sistema cuenta con **10 tablas relacionadas**:

```sql
┌─────────────────┐
│   CONTACTOS     │
│─────────────────│
│ id (PK)         │
│ fono1           │
│ fono2           │
│ email           │
└─────────────────┘
        ▲
        │
        ├──────────────────┬──────────────────┬────────────────┐
        │                  │                  │                │
┌───────┴────────┐  ┌──────┴───────┐  ┌──────┴───────┐  ┌────┴──────────┐
│   PACIENTES    │  │  EMPLEADOS   │  │ LABORATORIOS │  │  DIRECCIONES  │
│────────────────│  │──────────────│  │──────────────│  │───────────────│
│ id (PK)        │  │ id (PK)      │  │ id (PK)      │  │ id (PK)       │
│ pnombre        │  │ pnombre      │  │ nombre       │  │ calle         │
│ snombre        │  │ snombre      │  │ tipo         │  │ numero        │
│ papellido      │  │ papellido    │  │ dir_id (FK)  │  │ ciudad        │
│ sapellido      │  │ sapellido    │  │ contacto_id  │  │ comuna        │
│ rut            │  │ rut          │  └──────────────┘  │ region        │
│ dir_id (FK)    │  │ cargo        │                    └───────────────┘
│ contacto_id    │  │ dir_id (FK)  │
│ creado_en      │  │ contacto_id  │
└───┬────────────┘  │ creado_en    │
    │               └───┬──────────┘
    │                   │
    │ ┌─────────────────┴──────────────────┐
    │ │            USERS                   │
    │ │────────────────────────────────────│
    │ │ id (PK)                            │
    │ │ username (email único)             │
    │ │ password (BCrypt hash)             │
    │ │ role (ADMIN, EMPLEADO, PACIENTE)   │
    │ │ estado (ACTIVO, INACTIVO)          │
    │ │ paciente_id (FK, nullable)         │
    │ │ empleado_id (FK, nullable)         │
    │ │ creado_en                          │
    │ └────────────────────────────────────┘
    │
    ├────────────────┬────────────────────┐
    ▼                ▼                    ▼
┌──────────────┐ ┌───────────────┐  ┌───────────────┐
│ EXAMENES     │ │ LAB_EXAM      │  │ AGENDA_EXAMEN │
│──────────────│ │─────────      │  │───────────────│
│ id (PK)      │ │ id (PK)       │  │ id (PK)       │
│ codigo       │ │ id_laboratorio│  │ paciente_id   │
│ nombre       │ │ id_examen     │  │ empleado_id   │
│ tipo         │ └───────────────┘  │ examen_id     │
└──────────────┘                    │ fecha         │
                                    │ estado        │
                                    │ creado_en     │
                                    └───────┬───────┘
                                            │
                                            ▼
                                    ┌───────────────────┐
                                    │ RESULTADO_EXAMEN  │
                                    │───────────────────│
                                    │ id (PK)           │
                                    │ agenda_id (FK)    │
                                    │ resultado (TEXT)  │
                                    │ observaciones     │
                                    │ estado            │
                                    │ creado_en         │
                                    └───────────────────┘
```

---

## 🔗 Relaciones y Cascadas

### Eliminación en Cascada Automática

**Al eliminar un PACIENTE:**
```
PACIENTE (eliminado)
  ├── USERS (eliminado automáticamente)
  ├── AGENDA_EXAMEN (eliminadas automáticamente)
  │   └── RESULTADO_EXAMEN (eliminados automáticamente)
  ├── DIRECCIONES (eliminada automáticamente)
  └── CONTACTOS (eliminado automáticamente)
```

**Al eliminar un EMPLEADO:**
```
EMPLEADO (eliminado)
  ├── USERS (eliminado automáticamente)
  ├── DIRECCIONES (eliminada automáticamente)
  ├── CONTACTOS (eliminado automáticamente)
  ├── AGENDA_EXAMEN.empleado_id → NULL (conserva historial)
  └── RESULTADO_EXAMEN.empleado_id → NULL (conserva historial)
```

---

## 🔢 Secuencias Oracle

```sql
-- 10 secuencias para auto-incremento de PKs
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
```

---

## ⚙️ Configuración de Conexión

### Wallet de Oracle (OCI)

El proyecto utiliza **Oracle Wallet** para conexión segura a la base de datos en la nube.

**Ubicación del Wallet:**
```
/wallet/Wallet_databaseFullStack3/
├── cwallet.sso
├── ewallet.p12
├── ewallet.pem
├── keystore.jks
├── ojdbc.properties
├── README
├── sqlnet.ora
├── tnsnames.ora
└── truststore.jks
```

### Configuración de `application.properties`

#### MS_GESTION_USERS (8083)
```properties
# Puerto
server.port=8083

# Base de datos Oracle
spring.datasource.url=jdbc:oracle:thin:@databasefullstack3_high?TNS_ADMIN=/ruta/al/wallet/Wallet_databaseFullStack3
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect
spring.jpa.properties.hibernate.format_sql=true

# JWT
jwt.secret=tu_secreto_super_seguro_de_minimo_512_bits
jwt.expiration=7200000

# Logging
logging.level.com.gestion_users=INFO
```

#### MS_GESTION_LABS (8081)
```properties
# Puerto
server.port=8081

# Base de datos Oracle
spring.datasource.url=jdbc:oracle:thin:@databasefullstack3_high?TNS_ADMIN=/ruta/al/wallet/Wallet_databaseFullStack3
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect

# Logging
logging.level.com.gestion_labs=INFO
```

#### MS_GESTION_RESULTADOS (8082)
```properties
# Puerto
server.port=8082

# Base de datos Oracle
spring.datasource.url=jdbc:oracle:thin:@databasefullstack3_high?TNS_ADMIN=/ruta/al/wallet/Wallet_databaseFullStack3
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.OracleDialect

# URLs de otros microservicios (para enriquecimiento)
app.services.users=http://localhost:8083
app.services.labs=http://localhost:8081

# Logging
logging.level.com.gestion_resultados=INFO
```

---

## 📝 Scripts de Base de Datos

### Ubicación

```
base_de_datos/
├── creacion_tablas.sql       # Script de creación de todas las tablas
├── LIMPIAR_BD_COMPLETO.sql  # Script para limpiar la base de datos
└── README.md                 # Esta documentación
```

### Ejecutar Scripts

**1. Creación de Tablas:**

```bash
# Conectar a Oracle SQL Developer o SQLcl
sql usuario/password@databasefullstack3_high

# Ejecutar script
@base_de_datos/creacion_tablas.sql
```

**2. Crear Usuario Administrador (opcional):**

```sql
-- Insertar admin manualmente
INSERT INTO CONTACTOS (id, email) VALUES (seq_contactos.NEXTVAL, 'admin@laboratorio.cl');
INSERT INTO EMPLEADOS (id, pnombre, papellido, rut, cargo, contacto_id) 
VALUES (seq_empleados.NEXTVAL, 'Admin', 'Sistema', '11111111-1', 'Administrador', seq_contactos.CURRVAL);
INSERT INTO USERS (id, username, password, role, estado, empleado_id)
VALUES (seq_users.NEXTVAL, 'admin@laboratorio.cl', '$2a$10$hashedpassword', 'ADMIN', 'ACTIVO', seq_empleados.CURRVAL);
COMMIT;
```

**3. Limpiar Base de Datos:**

```bash
sql usuario/password@databasefullstack3_high
@base_de_datos/LIMPIAR_BD_COMPLETO.sql
```

---

## 🔍 Consultas Útiles

### Ver todas las tablas

```sql
SELECT table_name FROM user_tables ORDER BY table_name;
```

### Ver secuencias

```sql
SELECT sequence_name, last_number FROM user_sequences ORDER BY sequence_name;
```

### Contar registros por tabla

```sql
SELECT 'USERS' as tabla, COUNT(*) as total FROM USERS
UNION ALL
SELECT 'PACIENTES', COUNT(*) FROM PACIENTES
UNION ALL
SELECT 'EMPLEADOS', COUNT(*) FROM EMPLEADOS
UNION ALL
SELECT 'LABORATORIOS', COUNT(*) FROM LABORATORIOS
UNION ALL
SELECT 'EXAMENES', COUNT(*) FROM EXAMENES
UNION ALL
SELECT 'AGENDA_EXAMEN', COUNT(*) FROM AGENDA_EXAMEN
UNION ALL
SELECT 'RESULTADO_EXAMEN', COUNT(*) FROM RESULTADO_EXAMEN;
```

---

## 📚 Referencias

- [Oracle Autonomous Database](https://www.oracle.com/autonomous-database/)
- [Oracle Wallet Configuration](https://docs.oracle.com/en/cloud/paas/autonomous-database/adbsa/wallet-configure.html)
- [Spring Data JPA with Oracle](https://spring.io/projects/spring-data-jpa)

---

[← Volver al README principal](README.md)
