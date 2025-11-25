
/**
 * Guard AuthGuard
 * ---------------
 * Protege rutas que requieren autenticación.
 *
 * Ejemplo de uso en rutas:
 * ------------------------
 * { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] }
 *
 * Métodos:
 * --------
 * - canActivate(): boolean
 *      Permite acceso solo si el usuario está autenticado.
 */
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  /**
   * Permite acceso solo si el usuario está autenticado.
   * @returns true si el usuario tiene token, false si no
   */
  canActivate(): boolean {
    // Ejemplo: solo permite acceso si hay un token en localStorage
    if (localStorage.getItem('token')) {
      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }
}
