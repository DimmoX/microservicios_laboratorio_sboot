# Dockerfile para ms_gestion_labs
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copiar POM del módulo
COPY ms_gestion_labs/pom.xml ./pom.xml

# Copiar el microservicio
COPY ms_gestion_labs ./ms_gestion_labs

# Compilar solo este módulo
WORKDIR /workspace/ms_gestion_labs
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/ms_gestion_labs/target/*.jar /app/app.jar
EXPOSE 8081
CMD ["java", "-jar", "/app/app.jar"]
