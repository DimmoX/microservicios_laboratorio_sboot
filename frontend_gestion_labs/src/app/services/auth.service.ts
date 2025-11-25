// Service: Autenticación con localStorage
// IMPORTANTE: Funciona completamente sin backend
// Simula la estructura de respuestas del backend para mantener compatibilidad

import { Injectable } from '@angular/core';
import { Observable, of, BehaviorSubject, throwError } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { Usuario, LoginRequest, SesionActual } from '../models/usuario.model';
import { MockDataService } from './mock-data.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly SESSION_KEY = 'current_session';
  private authStatusSubject = new BehaviorSubject<boolean>(this.isAuthenticated());
  public authStatus$ = this.authStatusSubject.asObservable();

  constructor(private mockDataService: MockDataService) {}

  /**
   * Login simulado - valida contra localStorage
   * Simula respuesta del backend con delay para realismo
   */
  login(request: LoginRequest): Observable<SesionActual> {
    // Simular delay de red
    return of(null).pipe(
      delay(500),
      map(() => {
        const user = this.mockDataService.validateCredentials(request.username, request.password);

        if (!user) {
          throw new Error('Credenciales inválidas');
        }

        // Generar token simulado
        const token = this.generateMockToken(user);

        const sesion: SesionActual = {
          usuario: { ...user, password: '' }, // No exponer password
          token,
          fechaLogin: new Date()
        };

        // Guardar sesión en sessionStorage
        sessionStorage.setItem(this.SESSION_KEY, JSON.stringify(sesion));
        sessionStorage.setItem('token', token);

        // Notificar cambio en el estado de autenticación
        this.authStatusSubject.next(true);

        return sesion;
      })
    );
  }

  /**
   * Genera un token JWT simulado
   */
  private generateMockToken(user: Usuario): string {
    const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
    const payload = btoa(JSON.stringify({
      sub: user.username,
      userId: user.id,
      role: user.rol,
      iat: Date.now()
    }));
    const signature = btoa('mock-signature');
    return `${header}.${payload}.${signature}`;
  }

  /**
   * Registro de paciente - Solo ADMIN
   * Simula endpoint: POST /registro/paciente
   */
  registerPaciente(request: any): Observable<any> {
    return of(null).pipe(
      delay(300),
      map(() => {
        const currentUser = this.getCurrentUserSync();
        if (!currentUser || currentUser.rol !== 'ADMIN') {
          throw new Error('Acceso denegado. Solo administradores pueden registrar pacientes.');
        }

        const newUser: Usuario = {
          id: 0, // Se asigna automáticamente
          nombre: request.nombre,
          username: request.username,
          password: request.password,
          rol: 'PATIENT',
          telefono: request.telefono,
          direccion: request.direccion,
          activo: true,
          rut: request.rut,
          pacienteId: 0 // Se asigna automáticamente
        };

        const created = this.mockDataService.addUser(newUser);
        return { code: '000', description: 'Paciente registrado exitosamente', data: created };
      })
    );
  }

  /**
   * Registro de empleado - Solo ADMIN
   * Simula endpoint: POST /registro/empleado
   */
  registerEmpleado(request: any): Observable<any> {
    return of(null).pipe(
      delay(300),
      map(() => {
        const currentUser = this.getCurrentUserSync();
        if (!currentUser || currentUser.rol !== 'ADMIN') {
          throw new Error('Acceso denegado. Solo administradores pueden registrar empleados.');
        }

        const newUser: Usuario = {
          id: 0,
          nombre: request.nombre,
          username: request.username,
          password: request.password,
          rol: 'LAB_EMPLOYEE',
          telefono: request.telefono,
          direccion: request.direccion,
          activo: true,
          rut: request.rut,
          cargo: request.cargo,
          empleadoId: 0
        };

        const created = this.mockDataService.addUser(newUser);
        return { code: '000', description: 'Empleado registrado exitosamente', data: created };
      })
    );
  }

  /**
   * Logout - Limpia sesión
   * Simula endpoint: POST /auth/logout
   */
  logout(): Observable<any> {
    return of(null).pipe(
      delay(200),
      map(() => {
        sessionStorage.removeItem(this.SESSION_KEY);
        sessionStorage.removeItem('token');
        this.authStatusSubject.next(false);
        return { code: '000', description: 'Logout exitoso' };
      })
    );
  }

  /**
   * Obtiene perfil del usuario actual desde sessionStorage
   * Simula endpoint: GET /users/profile
   */
  getProfile(): Observable<Usuario> {
    return of(null).pipe(
      delay(100),
      map(() => {
        const session = this.getCurrentSessionSync();
        if (!session) {
          throw new Error('No hay sesión activa');
        }
        return session.usuario;
      })
    );
  }

  /**
   * Cambiar contraseña del usuario actual
   * Simula endpoint: PUT /users/{id}/password
   */
  changePassword(userId: number, oldPassword: string, newPassword: string): Observable<any> {
    return of(null).pipe(
      delay(300),
      map(() => {
        const success = this.mockDataService.updatePassword(userId, oldPassword, newPassword);
        if (!success) {
          throw new Error('Contraseña actual incorrecta');
        }

        // Actualizar sesión si es el usuario actual
        const session = this.getCurrentSessionSync();
        if (session && session.usuario.id === userId) {
          session.usuario.password = newPassword;
          sessionStorage.setItem(this.SESSION_KEY, JSON.stringify(session));
        }

        return { code: '000', description: 'Contraseña actualizada exitosamente' };
      })
    );
  }

  /**
   * Recuperar contraseña
   * Simula endpoint: POST /auth/forgot-password
   */
  forgotPassword(email: string): Observable<any> {
    return of(null).pipe(
      delay(500),
      map(() => {
        // Simular envío de email
        const user = this.mockDataService.findUserByUsername(email);
        if (!user) {
          throw new Error('Usuario no encontrado');
        }
        return { code: '000', description: 'Se ha enviado un email con instrucciones para recuperar tu contraseña' };
      })
    );
  }

  /**
   * Verifica si hay una sesión activa
   */
  isAuthenticated(): boolean {
    return sessionStorage.getItem('token') !== null;
  }

  /**
   * Obtiene el token de la sesión actual
   */
  getToken(): string | null {
    return sessionStorage.getItem('token');
  }

  /**
   * Obtiene la sesión actual de forma síncrona (uso interno)
   */
  private getCurrentSessionSync(): SesionActual | null {
    const sessionData = sessionStorage.getItem(this.SESSION_KEY);
    return sessionData ? JSON.parse(sessionData) : null;
  }

  /**
   * Obtiene el usuario actual de forma síncrona (uso interno)
   */
  private getCurrentUserSync(): Usuario | null {
    const session = this.getCurrentSessionSync();
    return session ? session.usuario : null;
  }

  /**
   * Verifica si el usuario actual es ADMIN
   */
  isAdmin(): Observable<boolean> {
    if (!this.isAuthenticated()) {
      return of(false);
    }
    return of(this.getCurrentUserSync()?.rol?.toUpperCase() === 'ADMIN').pipe(delay(50));
  }

  /**
   * Verifica si el usuario actual es LAB_EMPLOYEE o ADMIN
   */
  isLabEmployeeOrAdmin(): Observable<boolean> {
    if (!this.isAuthenticated()) {
      return of(false);
    }
    const user = this.getCurrentUserSync();
    const rol = user?.rol?.toUpperCase();
    return of(rol === 'ADMIN' || rol === 'LAB_EMPLOYEE').pipe(delay(50));
  }

  /**
   * Verifica si el usuario actual es PATIENT
   */
  isPatient(): Observable<boolean> {
    if (!this.isAuthenticated()) {
      return of(false);
    }
    return of(this.getCurrentUserSync()?.rol?.toUpperCase() === 'PATIENT').pipe(delay(50));
  }

  /**
   * Obtiene el rol del usuario actual
   */
  getUserRole(): Observable<string | null> {
    if (!this.isAuthenticated()) {
      return of(null);
    }
    return of(this.getCurrentUserSync()?.rol || null).pipe(delay(50));
  }

  /**
   * Observable para el usuario actual
   */
  get currentUser(): Observable<Usuario | null> {
    if (!this.isAuthenticated()) {
      return of(null);
    }
    return this.getProfile();
  }

  /**
   * Actualiza perfil del usuario
   * Simula endpoint: PUT /users/{id}
   */
  updateProfile(userId: number, updates: Partial<Usuario>): Observable<any> {
    return of(null).pipe(
      delay(300),
      map(() => {
        const updatedUser = this.mockDataService.updateUser(userId, updates);
        if (!updatedUser) {
          throw new Error('Usuario no encontrado');
        }

        // Actualizar sesión si es el usuario actual
        const session = this.getCurrentSessionSync();
        if (session && session.usuario.id === userId) {
          session.usuario = { ...session.usuario, ...updates };
          sessionStorage.setItem(this.SESSION_KEY, JSON.stringify(session));
        }

        return { code: '000', description: 'Perfil actualizado exitosamente', data: updatedUser };
      })
    );
  }
}

