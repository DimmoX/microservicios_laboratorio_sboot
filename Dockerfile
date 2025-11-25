# Multi-stage build for frontend and backend microservices

# --- FRONTEND ---
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend_gestion_labs
COPY frontend_gestion_labs/package*.json ./
RUN npm ci --legacy-peer-deps
COPY frontend_gestion_labs/ ./
RUN npm run build -- --configuration=production

# --- BACKEND: ms_api_gateway ---
FROM maven:3.9.6-eclipse-temurin-21 AS ms_api_gateway-build
WORKDIR /app/ms_api_gateway
COPY ms_api_gateway/pom.xml ./
COPY ms_api_gateway/src ./src
RUN mvn clean package -DskipTests

# --- BACKEND: ms_gestion_labs ---
FROM maven:3.9.6-eclipse-temurin-21 AS ms_gestion_labs-build
WORKDIR /app/ms_gestion_labs
COPY ms_gestion_labs/pom.xml ./
COPY ms_gestion_labs/src ./src
RUN mvn clean package -DskipTests

# --- BACKEND: ms_gestion_users ---
FROM maven:3.9.6-eclipse-temurin-21 AS ms_gestion_users-build
WORKDIR /app/ms_gestion_users
COPY ms_gestion_users/pom.xml ./
COPY ms_gestion_users/src ./src
RUN mvn clean package -DskipTests

# --- FINAL IMAGE ---

# --- FINAL IMAGE ---
FROM nginx:1.25-alpine AS final
COPY --from=frontend-build /app/frontend_gestion_labs/dist/frontend-gestion-labs/browser /usr/share/nginx/html
COPY --from=ms_api_gateway-build /app/ms_api_gateway/target/*.jar /app/ms_api_gateway.jar
COPY --from=ms_gestion_labs-build /app/ms_gestion_labs/target/*.jar /app/ms_gestion_labs.jar
COPY --from=ms_gestion_users-build /app/ms_gestion_users/target/*.jar /app/ms_gestion_users.jar

# Expose ports: 80 (frontend), 8080 (api_gateway), 8081 (gestion_labs), 8082 (gestion_users)
EXPOSE 80 8080 8081 8082

# Start all services (simple, for demo; use docker-compose for real projects)
CMD ["/bin/sh", "-c", "nginx -g 'daemon off;' & java -jar /app/ms_api_gateway.jar & java -jar /app/ms_gestion_labs.jar & java -jar /app/ms_gestion_users.jar && wait"]
