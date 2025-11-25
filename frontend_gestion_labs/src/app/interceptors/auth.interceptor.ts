import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

/**
 * Interceptor HTTP para manejar automáticamente:
 * 1. Agregar el token JWT a todas las peticiones
 * 2. Manejar errores de autenticación (401)
 * 3. Redirigir al login cuando el token expira
 *
 * IMPORTANTE: Solo el token se guarda en sessionStorage
 * TODO lo demás viene del backend
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Obtener el token de sessionStorage
    const token = sessionStorage.getItem('token');

    // Clonar la petición y agregar el token si existe
    let authReq = req;
    if (token) {
      authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    // Enviar la petición y manejar errores
    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        // Si recibimos un 401, el token expiró o es inválido
        if (error.status === 401) {
          // Limpiar SOLO el token
          sessionStorage.removeItem('token');

          // Redirigir al login
          this.router.navigate(['/login']);
        }

        return throwError(() => error);
      })
    );
  }
}
