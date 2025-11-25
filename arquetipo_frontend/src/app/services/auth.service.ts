import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
/**
 * Servicio AuthService
 * --------------------
 * Gestiona la autenticación de usuarios en la aplicación.
 * Provee métodos para iniciar sesión, cerrar sesión y verificar el estado de autenticación.
 *
 * Ejemplo de uso en un componente:
 * ---------------------------------
 * import { AuthService } from './services/auth.service';
 * constructor(private authService: AuthService) {}
 * login() {
 *   const isLogged = this.authService.login('usuario', 'password');
 *   if (isLogged) {
 *     // Redirigir al dashboard
 *   }
 * }
 *
 * Métodos:
 * --------
 * - login(usuario: string, password: string): boolean
 *      Inicia sesión con las credenciales proporcionadas.
 *      Retorna true si la autenticación es exitosa, false en caso contrario.
 * - logout(): void
 *      Cierra la sesión del usuario actual.
 * - isAuthenticated(): boolean
 *      Verifica si el usuario está autenticado.
 */
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  /**
   * Inicia sesión con usuario y contraseña.
   * @param usuario Nombre de usuario
   * @param password Contraseña
   * @returns true si la autenticación es exitosa
   */
  login(usuario: string, password: string): boolean {
    // Lógica de autenticación
    return true;
  }

  /**
   * Cierra la sesión del usuario actual.
   */
  logout(): void {
    // Lógica de cierre de sesión
  }

  /**
   * Verifica si el usuario está autenticado.
   * @returns true si el usuario tiene sesión activa
   */
  isAuthenticated(): boolean {
    // Lógica de verificación
    return !!localStorage.getItem('token');
  }
}
