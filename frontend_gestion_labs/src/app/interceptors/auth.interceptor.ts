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
    
    console.log('🔍 Interceptor:', req.url);
    console.log('🔍 Token presente:', !!token);
    if (token) {
      console.log('🔍 Token (primeros 20 chars):', token.substring(0, 20));
    }

    // Clonar la petición y agregar el token si existe
    let authReq = req;
    if (token) {
      authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      console.log('✅ Header Authorization agregado');
    } else {
      console.log('❌ No hay token, petición sin autenticación');
    }

    // Enviar la petición y manejar errores
    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        // Si recibimos un 401, el token expiró o es inválido
        if (error.status === 401) {
          console.log('❌ Interceptor: Error 401, limpiando sesión y redirigiendo al login');
          console.log('❌ URL que causó el error:', error.url);
          console.log('❌ Método:', req.method);
          
          // Limpiar toda la sesión
          sessionStorage.removeItem('token');
          sessionStorage.removeItem('current_session');

          // Redirigir al login
          this.router.navigate(['/login']);
        }
        
        // Si es 403 (Forbidden), solo logueamos pero no cerramos sesión
        if (error.status === 403) {
          console.log('⚠️ Interceptor: Error 403 (Acceso Denegado)');
          console.log('⚠️ URL:', error.url);
          console.log('⚠️ Mensaje:', error.error?.description || error.message);
        }

        return throwError(() => error);
      })
    );
  }
}
