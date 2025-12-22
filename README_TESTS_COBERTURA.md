# Guía de Tests Unitarios y Análisis de Cobertura con SonarQube

## 📋 Resumen

Este proyecto incluye **tests unitarios completos** para todos los microservicios y el frontend, junto con la configuración necesaria para análisis de cobertura con **SonarQube**.

### Tests Implementados

#### 🔬 Microservicios Backend (Java/Spring Boot)

Cada microservicio cuenta con **5 tests unitarios** que cubren las funcionalidades principales:

##### 1. **ms_gestion_labs** (10 tests totales en 2 clases)
- ✅ `ExamenServiceTest` - 5 tests para gestión de exámenes
  - Listar todos los exámenes
  - Buscar examen por ID
  - Crear nuevo examen
  - Actualizar examen existente
  - Manejo de examen no encontrado
  
- ✅ `LaboratorioServiceTest` - 5 tests para gestión de laboratorios
  - Listar todos los laboratorios
  - Buscar laboratorio por ID
  - Crear nuevo laboratorio
  - Eliminar laboratorio
  - Manejo de laboratorio no encontrado

##### 2. **ms_gestion_users** (10 tests totales en 2 clases)
- ✅ `UserServiceTest` - 5 tests para gestión de usuarios
  - Listar todos los usuarios
  - Buscar usuario por ID
  - Crear usuario con password encriptado
  - Cambiar contraseña
  - Manejo de usuario no encontrado
  
- ✅ `AuthServiceTest` - 5 tests para autenticación
  - Login exitoso con generación de JWT
  - Login con usuario no encontrado
  - Login con contraseña incorrecta
  - Cambio de contraseña exitoso
  - Rechazo de cambio con contraseña incorrecta

##### 3. **ms_gestion_resultados** (9 tests en 1 clase)
- ✅ `ResultadoServiceTest` - 9 tests para gestión de resultados
  - Listar todos los resultados
  - Buscar resultado por ID
  - Buscar resultados por paciente
  - Crear nuevo resultado
  - Actualizar resultado
  - Validación de campos obligatorios
  - Eliminación de resultado
  - Manejo de resultado no encontrado
  - Establecimiento automático de fecha al emitir

##### 4. **ms_api_gateway** (10 tests totales en 2 clases)
- ✅ `TokenBlacklistServiceTest` - 7 tests para blacklist de tokens
  - Agregar token a blacklist
  - Verificar si token está blacklisted
  - Remover token de blacklist
  - Limpiar toda la blacklist
  - Obtener tamaño de blacklist
  - Manejo de tokens nulos/vacíos
  - Thread-safety con múltiples hilos
  
- ✅ `JwtPropertiesTest` - 5 tests para configuración JWT
  - Establecer y obtener secreto JWT
  - Establecer y obtener tiempo de expiración
  - Manejo de valores nulos
  - Validación de diferentes longitudes de secreto
  - Validación de diferentes valores de expiración

#### 🌐 Frontend Angular (25 tests en 5 archivos)

##### 1. `auth.service.spec.ts` - 5 tests
- Login exitoso con almacenamiento de sesión
- Manejo de error 401
- Logout con limpieza de sesión
- Verificación de estado de autenticación
- Registro de paciente

##### 2. `laboratorio.service.spec.ts` - 5 tests
- Obtener todos los laboratorios
- Obtener laboratorio por ID
- Crear nuevo laboratorio
- Actualizar laboratorio
- Filtrar laboratorios por ciudad

##### 3. `examen.service.spec.ts` - 5 tests
- Obtener todos los exámenes
- Obtener examen por ID
- Crear nuevo examen
- Actualizar examen
- Eliminar examen

##### 4. `usuario.service.spec.ts` - 5 tests
- Obtener todos los usuarios
- Obtener usuario por ID
- Registrar nuevo paciente
- Registrar nuevo empleado
- Crear usuario con método legacy

##### 5. `resultado.service.spec.ts` - 5 tests
- Obtener todos los resultados
- Obtener resultados por paciente
- Obtener resultado por ID
- Crear nuevo resultado
- Actualizar estado a EMITIDO

---

## 🚀 Ejecución de Tests

### Tests Backend (Microservicios)

Para ejecutar tests en un microservicio específico:

```bash
# Navegar al directorio del microservicio
cd ms_gestion_labs

# Ejecutar tests con Maven
mvn test

# Ejecutar tests con reporte de cobertura JaCoCo
mvn clean test jacoco:report
```

Los reportes de cobertura se generan en: `target/site/jacoco/index.html`

### Tests Frontend

```bash
# Navegar al directorio del frontend
cd frontend_gestion_labs

# Ejecutar tests una vez
npm test

# Ejecutar tests con cobertura
npm run test:coverage
```

Los reportes de cobertura se generan en: `coverage/frontend-gestion-labs/index.html`

---

## 📊 Análisis con SonarQube

### Requisitos Previos

1. **SonarQube debe estar corriendo**:
   ```bash
   docker-compose up sonarqube
   ```

2. **Acceder a SonarQube**: http://localhost:9000
   - Usuario: admin
   - Contraseña: admin (cambiar en primer acceso)

3. **Generar Token de Autenticación**:
   - En SonarQube: My Account → Security → Generate Token
   - Copiar el token generado

### Configuración

Cada proyecto tiene su archivo `sonar-project.properties` configurado:

```
microservicios_laboratorio_sboot/
├── ms_gestion_labs/sonar-project.properties
├── ms_gestion_users/sonar-project.properties
├── ms_gestion_resultados/sonar-project.properties
├── ms_api_gateway/sonar-project.properties
└── frontend_gestion_labs/sonar-project.properties
```

### Ejecución del Análisis Completo

**Opción 1: Script Automatizado (Recomendado)**

```bash
# Ejecutar análisis de todos los proyectos
./analisis-cobertura-completo.sh
```

Este script:
- ✅ Verifica la conexión con SonarQube
- ✅ Ejecuta tests en cada microservicio
- ✅ Genera reportes de cobertura JaCoCo
- ✅ Envía análisis a SonarQube
- ✅ Ejecuta tests del frontend con cobertura
- ✅ Envía análisis del frontend a SonarQube

**Opción 2: Análisis Manual por Proyecto**

Para microservicios:
```bash
cd ms_gestion_labs
mvn clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=TU_TOKEN_AQUI
```

Para frontend:
```bash
cd frontend_gestion_labs
npm run test:coverage
npm run sonar
```

### Personalizar Token de SonarQube

Hay tres formas de configurar el token:

1. **Variable de entorno** (recomendado):
   ```bash
   export SONAR_TOKEN="tu_token_aqui"
   ./analisis-cobertura-completo.sh
   ```

2. **Editar el script** `analisis-cobertura-completo.sh`:
   ```bash
   SONAR_TOKEN="tu_token_aqui"
   ```

3. **Pasar como parámetro en Maven**:
   ```bash
   mvn sonar:sonar -Dsonar.token=tu_token_aqui
   ```

---

## 📈 Visualización de Resultados

### En SonarQube Dashboard

Acceder a: http://localhost:9000

Proyectos disponibles:
- `ms_gestion_labs` - MS Gestion Laboratorios
- `ms_gestion_users` - MS Gestion Usuarios
- `ms_gestion_resultados` - MS Gestion Resultados
- `ms_api_gateway` - MS API Gateway
- `frontend_gestion_labs` - Frontend Gestión Laboratorios

### Métricas Principales

Para cada proyecto, SonarQube mostrará:
- **Cobertura de código** (%) - Tests ejecutados vs código total
- **Líneas de código** - Total de líneas analizadas
- **Bugs** - Errores detectados en el código
- **Vulnerabilidades** - Problemas de seguridad
- **Code Smells** - Problemas de mantenibilidad
- **Duplicación** - Código duplicado (%)
- **Complejidad ciclomática** - Complejidad del código

---

## 🔧 Configuración de JaCoCo

Todos los microservicios tienen configurado el plugin JaCoCo en su `pom.xml`:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

---

## 🐛 Solución de Problemas

### SonarQube no está disponible
```bash
# Verificar que el contenedor esté corriendo
docker ps | grep sonarqube

# Iniciar SonarQube si no está corriendo
docker-compose up -d sonarqube

# Ver logs
docker-compose logs sonarqube
```

### Error de token inválido
- Generar nuevo token en SonarQube
- Actualizar el token en el script o variable de entorno

### Tests fallan en microservicio
```bash
# Ver logs detallados
mvn test -X

# Ejecutar test específico
mvn test -Dtest=NombreDelTest
```

### Tests fallan en frontend
```bash
# Limpiar cache de npm
rm -rf node_modules package-lock.json
npm install

# Ejecutar con más detalle
npm test -- --no-watch --code-coverage
```

---

## 📝 Estructura de Archivos de Test

```
ms_gestion_labs/
├── src/
│   ├── main/java/
│   │   └── com/gestion_labs/ms_gestion_labs/
│   │       ├── service/
│   │       │   ├── examen/ExamenServiceImpl.java
│   │       │   └── laboratorio/LaboratorioServiceImpl.java
│   │       └── ...
│   └── test/java/
│       └── com/gestion_labs/ms_gestion_labs/
│           └── service/
│               ├── ExamenServiceTest.java ✅
│               └── LaboratorioServiceTest.java ✅
└── sonar-project.properties ✅

frontend_gestion_labs/
├── src/
│   └── app/
│       └── services/
│           ├── auth.service.ts
│           ├── auth.service.spec.ts ✅
│           ├── laboratorio.service.ts
│           ├── laboratorio.service.spec.ts ✅
│           ├── examen.service.ts
│           ├── examen.service.spec.ts ✅
│           ├── usuario.service.ts
│           ├── usuario.service.spec.ts ✅
│           ├── resultado.service.ts
│           └── resultado.service.spec.ts ✅
└── sonar-project.properties ✅
```

---

## 🎯 Mejores Prácticas

1. **Ejecutar tests antes de commit**:
   ```bash
   mvn test  # Backend
   npm test  # Frontend
   ```

2. **Revisar cobertura localmente**:
   - Backend: Abrir `target/site/jacoco/index.html`
   - Frontend: Abrir `coverage/frontend-gestion-labs/index.html`

3. **Mantener cobertura alta**:
   - Objetivo mínimo: 70%
   - Objetivo ideal: 80%+

4. **Ejecutar análisis SonarQube regularmente**:
   - Antes de merge a main
   - Después de cambios significativos

5. **Revisar métricas en SonarQube**:
   - Corregir bugs críticos inmediatamente
   - Atender vulnerabilidades de seguridad
   - Refactorizar code smells importantes

---

## 📚 Recursos Adicionales

- [Documentación JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/)
- [Documentación SonarQube](https://docs.sonarqube.org/latest/)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Jasmine Testing Framework](https://jasmine.github.io/)
- [Angular Testing Guide](https://angular.io/guide/testing)

---

## ✅ Checklist de Entrega

- [x] 5 tests unitarios en `ms_gestion_labs`
- [x] 5 tests unitarios en `ms_gestion_users`
- [x] 5 tests unitarios en `ms_gestion_resultados`
- [x] 5 tests unitarios en `ms_api_gateway`
- [x] 5 tests unitarios en frontend Angular
- [x] Configuración de JaCoCo en todos los microservicios
- [x] Configuración de SonarQube en todos los proyectos
- [x] Script automatizado de análisis completo
- [x] Documentación de uso

**Total: 44 tests unitarios implementados** ✨
