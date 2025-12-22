# Dockerfile para ms_api_gateway
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copiar POM del módulo
COPY ms_api_gateway/pom.xml ./pom.xml

# Copiar el microservicio
COPY ms_api_gateway ./ms_api_gateway

# Compilar solo este módulo
WORKDIR /workspace/ms_api_gateway
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/ms_api_gateway/target/*.jar /app/app.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/app.jar"]
