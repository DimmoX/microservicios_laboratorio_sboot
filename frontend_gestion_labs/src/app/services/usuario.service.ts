import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Usuario } from '../models/usuario.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  /**
   * Crear nuevo usuario
   * Endpoint: POST /users
   */
  crearUsuario(usuario: Usuario): Observable<Usuario> {
    // Transformar 'rol' a 'role' para el backend
    const payload = {
      ...usuario,
      role: usuario.rol,
      username: usuario.username
    };
    // Remover 'rol' del payload (backend usa 'role')
    delete (payload as any).rol;
    
    return this.http.post<any>(this.apiUrl, payload).pipe(
      map(response => response.data)
    );
  }

  /**
   * Obtener todos los usuarios
   * Endpoint: GET /users
   */
  getUsuarios(): Observable<Usuario[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(response => response.data || [])
    );
  }

  /**
   * Obtener un usuario por ID
   * Endpoint: GET /users/{id}
   */
  getUsuario(id: number): Observable<Usuario> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Actualizar usuario
   * Endpoint: PUT /users/{id}
   */
  actualizarUsuario(id: number, usuario: Partial<Usuario>): Observable<Usuario> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, usuario).pipe(
      map(response => response.data)
    );
  }

  /**
   * Eliminar usuario
   * Endpoint: DELETE /users/{id}
   */
  eliminarUsuario(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }

  /**
   * Cambiar contraseña
   * Endpoint: PUT /users/{id}/password
   */
  cambiarContrasena(id: number, oldPassword: string, newPassword: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/password`, {
      oldPassword,
      newPassword
    });
  }
}
