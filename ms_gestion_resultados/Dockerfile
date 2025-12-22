# Dockerfile para ms_gestion_resultados
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copiar POM del módulo
COPY ms_gestion_resultados/pom.xml ./pom.xml

# Copiar el microservicio
COPY ms_gestion_resultados ./ms_gestion_resultados

# Compilar solo este módulo
WORKDIR /workspace/ms_gestion_resultados
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/ms_gestion_resultados/target/*.jar /app/app.jar
EXPOSE 8082
CMD ["java", "-jar", "/app/app.jar"]
