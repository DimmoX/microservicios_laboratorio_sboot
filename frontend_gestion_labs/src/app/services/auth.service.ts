// Service: Autenticación con backend real
// IMPORTANTE: Ahora usa HttpClient para llamar al API Gateway

import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, BehaviorSubject, throwError } from 'rxjs';
import { delay, map, tap, catchError } from 'rxjs/operators';
import { Usuario, LoginRequest, SesionActual } from '../models/usuario.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly SESSION_KEY = 'current_session';
  private authStatusSubject = new BehaviorSubject<boolean>(this.isAuthenticated());
  public authStatus$ = this.authStatusSubject.asObservable();
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * Endpoint: POST /auth/login
   */
  login(request: LoginRequest): Observable<SesionActual> {
    return this.http.post<any>(`${this.apiUrl}/auth/login`, request).pipe(
      map(response => {

        if (!response.data || !response.data.token) {
          throw new Error('Respuesta inválida del servidor');
        }

        const token = response.data.token;
        const usuario = response.data.usuario;

        const sesion: SesionActual = {
          usuario: { ...usuario, password: '' },
          token,
          fechaLogin: new Date()
        };

        // Guardar sesión en sessionStorage
        sessionStorage.setItem(this.SESSION_KEY, JSON.stringify(sesion));
        sessionStorage.setItem('token', token);

        // Notificar cambio en el estado de autenticación
        this.authStatusSubject.next(true);

        return sesion;
      }),
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Error al iniciar sesión';
        
        if (error.status === 401) {
          errorMessage = 'Usuario o contraseña incorrectos';
        } else if (error.status === 0) {
          errorMessage = 'No se puede conectar con el servidor';
        } else if (error.error?.description) {
          errorMessage = error.error.description;
        } else if (error.message) {
          errorMessage = error.message;
        }

        return throwError(() => new Error(errorMessage));
      })
    );
  }


  /**
   * Registro de paciente - Solo ADMIN
   * Endpoint: POST /registro/paciente
   */
  registerPaciente(request: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/registro/paciente`, request);
  }

  /**
   * Registro de empleado - Solo ADMIN
   * Endpoint: POST /registro/empleado
   */
  registerEmpleado(request: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/registro/empleado`, request);
  }

  /**
   * Logout - Limpia sesión
   * Endpoint: POST /auth/logout
   */
  logout(): Observable<any> {
    // Primero limpiar sesión local
    sessionStorage.removeItem(this.SESSION_KEY);
    sessionStorage.removeItem('token');
    this.authStatusSubject.next(false);

    // Luego notificar al backend (sin esperar respuesta)
    return this.http.post<any>(`${this.apiUrl}/auth/logout`, {}).pipe(
      map(() => ({ code: '000', description: 'Logout exitoso' }))
    );
  }

  /**
   * Obtiene perfil del usuario actual desde sessionStorage
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
   * Endpoint: PUT /users/{id}/password
   */
  changePassword(userId: number, oldPassword: string, newPassword: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/users/${userId}/password`, {
      oldPassword,
      newPassword
    });
  }

  /**
   * Recuperar contraseña
   * Endpoint: POST /auth/forgot-password
   */
  forgotPassword(email: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/auth/forgot-password`, { email });
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
   * Endpoint: PUT /users/{id}
   */
  updateProfile(userId: number, updates: Partial<Usuario>): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/users/${userId}`, updates).pipe(
      tap(response => {
        // Actualizar sesión local si es el usuario actual
        const session = this.getCurrentSessionSync();
        if (session && session.usuario.id === userId && response.data) {
          session.usuario = { ...session.usuario, ...response.data };
          sessionStorage.setItem(this.SESSION_KEY, JSON.stringify(session));
        }
      })
    );
  }
}

