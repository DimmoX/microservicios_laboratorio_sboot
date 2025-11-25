# Dockerfile para el microservicio ms_gestion_labs (Backend Spring Boot)
# Arquitectura: Patrón Layered Architecture con Multistage Build

# ======================================
# STAGE 1: Build - Compilación con Maven
# ======================================
FROM maven:3.9.5-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml ./
COPY mvnw ./
COPY mvnw.cmd ./
COPY .mvn ./.mvn

# Descargar dependencias (capa de caché)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación
RUN mvn clean package -DskipTests -B

# ======================================
# STAGE 2: Runtime - Ejecución con JRE
# ======================================
FROM eclipse-temurin:21-jre

WORKDIR /app

RUN groupadd -r spring && useradd -r -g spring spring

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Crear directorio para wallet de Oracle
RUN mkdir -p /app/wallet && chown -R spring:spring /app

USER spring:spring

# Exponer el puerto del microservicio
EXPOSE 8081

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=default
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Comando de ejecución
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Metadata
LABEL maintainer="DUOC UC"
LABEL description="Microservicio de Gestión de Laboratorios - Spring Boot"
LABEL version="1.0.0"
