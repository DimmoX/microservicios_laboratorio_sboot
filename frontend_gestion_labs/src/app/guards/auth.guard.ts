// Guards: Proteger rutas según roles de usuario

import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map } from 'rxjs/operators';

/**
 * Guard básico de autenticación - verifica que el usuario esté logueado
 */
export const authGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};

/**
 * Guard para rutas exclusivas de ADMIN
 */
export const adminGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  return authService.isAdmin().pipe(
    map(isAdmin => {
      if (isAdmin) {
        return true;
      }
      router.navigate(['/']);
      alert('Acceso denegado. Se requieren permisos de administrador.');
      return false;
    })
  );
};

/**
 * Guard para rutas de LAB_EMPLOYEE y ADMIN
 */
export const labEmployeeGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  return authService.isLabEmployeeOrAdmin().pipe(
    map(hasAccess => {
      if (hasAccess) {
        return true;
      }
      router.navigate(['/']);
      alert('Acceso denegado. Se requieren permisos de empleado o administrador.');
      return false;
    })
  );
};

/**
 * Guard para rutas de PATIENT (paciente)
 */
export const patientGuard = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  return authService.isPatient().pipe(
    map(isPatient => {
      if (isPatient) {
        return true;
      }
      router.navigate(['/']);
      alert('Acceso denegado. Esta ruta es solo para pacientes.');
      return false;
    })
  );
};
