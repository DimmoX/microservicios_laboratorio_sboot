
/**
 * Guard EjemploGuard
 * ------------------
 * Ejemplo de guard para protección de rutas personalizadas.
 *
 * Ejemplo de uso en rutas:
 * ------------------------
 * { path: 'ejemplo', component: EjemploComponent, canActivate: [EjemploGuard] }
 *
 * Métodos:
 * --------
 * - canActivate(): boolean
 *      Permite acceso solo si el usuario está autenticado (token en localStorage).
 */
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class EjemploGuard implements CanActivate {
  constructor(private router: Router) {}

  /**
   * Permite acceso solo si el usuario está autenticado (token en localStorage).
   * @returns true si el usuario tiene token, false si no
   */
  canActivate(): boolean {
    // Ejemplo: solo permite acceso si hay un token en localStorage
    if (localStorage.getItem('token')) {
      return true;
    } else {
      this.router.navigate(['/']);
      return false;
    }
  }
}
