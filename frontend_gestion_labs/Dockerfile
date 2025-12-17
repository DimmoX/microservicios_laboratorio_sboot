# Dockerfile para el Frontend Angular
# Patrón de diseño: MVC (Model-View-Controller) + Multistage Build

# ======================================
# STAGE 1: Build - Compilación con Node.js
# ======================================
FROM node:20-alpine AS build

WORKDIR /app

# Copiar archivos de configuración de dependencias
COPY package.json package-lock.json* ./

# Instalar dependencias
RUN npm ci --legacy-peer-deps

# Copiar código fuente completo
COPY . ./

# Compilar aplicación Angular para producción
RUN npm run build -- --configuration=production

# ======================================
# STAGE 2: Runtime - Servidor Nginx
# ======================================
FROM nginx:1.25-alpine

# Copiar archivos compilados desde la etapa de build
COPY --from=build /app/dist/frontend-gestion-labs/browser /usr/share/nginx/html

# Configurar Nginx para Angular (SPA routing)
RUN echo 'server { \
    listen 80; \
    server_name localhost; \
    root /usr/share/nginx/html; \
    index index.html; \
    \
    # Configuración para SPA (Single Page Application) \
    location / { \
        try_files $uri $uri/ /index.html; \
    } \
    \
    # Headers de seguridad \
    add_header X-Frame-Options "SAMEORIGIN" always; \
    add_header X-Content-Type-Options "nosniff" always; \
    add_header X-XSS-Protection "1; mode=block" always; \
    \
    # Configuración de caché \
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ { \
        add_header Cache-Control "no-store, no-cache, must-revalidate, proxy-revalidate, max-age=0"; \
        add_header Pragma "no-cache"; \
        add_header Expires "0"; \
    } \
}' > /etc/nginx/conf.d/default.conf

# Exponer puerto HTTP
EXPOSE 80


# Mostrar información del frontend al iniciar el contenedor
CMD ["nginx", "-g", "daemon off;"]

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost/ || exit 1

# Metadata
LABEL maintainer="DUOC UC"
LABEL description="Frontend Angular para Gestión de Laboratorios - Nginx"
LABEL version="1.0.0"
