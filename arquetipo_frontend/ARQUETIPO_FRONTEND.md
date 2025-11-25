
# Arquetipo Frontend Angular

Este arquetipo replica la estructura y nomenclatura del proyecto original `frontend_gestion_labs` y contiene ejemplos funcionales para cada parte clave de una aplicación Angular moderna.

## Estructura Base
	- `home/home.component.ts`
	- `login/login.component.ts`
	- `laboratorio-list/laboratorio-list.component.ts`
	- `laboratorio-form/laboratorio-form.component.ts`
	- `profile/profile.component.ts`
	- `auth.service.ts`
	- `laboratorio.service.ts`
	- `ejemplo.service.ts`
	- `ejemplo-http.service.ts`
	- `auth.guard.ts`
	- `ejemplo.guard.ts`
    - `usuario.model.ts`: Modelo de usuario.
    - `laboratorio.model.ts`: Modelo de laboratorio y submodelos de dirección/contacto.
    - `examen.model.ts`: Modelo de examen de laboratorio.
    - `lab-exam.model.ts`: Relación laboratorio-examen y precios.
    - `resultado.model.ts`: Resultados de exámenes realizados.
- `src/app/data/`: Datos de ejemplo:
## Ejemplos incluidos y lo realizado
- Componentes funcionales: `home`, `login`, `laboratorio-list`, `laboratorio-form`, `profile` (cada uno con su archivo `.component.ts`)
- Servicios: `auth.service.ts` (autenticación), `laboratorio.service.ts` (gestión de laboratorios), `ejemplo.service.ts`, `ejemplo-http.service.ts`
- Guards: `auth.guard.ts` (protección de rutas), `ejemplo.guard.ts`
- Modelos: `usuario.model.ts`, `laboratorio.model.ts`, `examen.model.ts`, `lab-exam.model.ts`, `resultado.model.ts` (todos con comentarios extensos y ejemplos de uso)
- Datos: `mock-data.ts` (laboratorios de ejemplo)
- Pipe: `ejemplo.pipe.ts` (transformación de datos)
- Módulo: `ejemplo.module.ts` (modularización de funcionalidades)
- Componentes funcionales: `home`, `login`, `laboratorio-list`, `laboratorio-form`, `profile` (cada uno con su archivo `.component.ts`)
## Patrones de Diseño Implementados
- **Arquitectura basada en componentes**
- **Inyección de dependencias** en servicios y guards
- **Separación de responsabilidades** (componentes, servicios, modelos, guards, pipes)
- **Organización modular y por funcionalidad**
- **Documentación interna extensa y ejemplos de uso en todos los archivos clave**
- Pipe: `ejemplo.pipe.ts` (transformación de datos)
- Módulo: `ejemplo.module.ts` (modularización de funcionalidades)

## Patrones de Diseño Implementados
 Todos los archivos clave incluyen comentarios extensos y ejemplos de uso para facilitar la adaptación y extensión del arquetipo. Este arquetipo te permite escalar y mantener aplicaciones Angular siguiendo la misma estructura y convenciones del proyecto principal.
- **Inyección de dependencias** en servicios y guards
- **Separación de responsabilidades** (componentes, servicios, modelos, guards, pipes)
- **Organización modular y por funcionalidad**

## Uso
1. Copia la carpeta `arquetipo_frontend` para iniciar un nuevo proyecto Angular.
2. Personaliza los componentes, servicios, modelos y demás archivos según tus necesidades.
3. Ejecuta `npm install` y `npm start` para iniciar el desarrollo.

Este arquetipo te permite escalar y mantener aplicaciones Angular siguiendo la misma estructura y convenciones del proyecto principal. Todos los ejemplos incluidos están listos para ser adaptados y extendidos.