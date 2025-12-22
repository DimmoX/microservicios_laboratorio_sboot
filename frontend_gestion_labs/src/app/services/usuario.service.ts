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
  private registroUrl = `${environment.apiUrl}/registro`;

  constructor(private http: HttpClient) {}

  /**
   * Registrar nuevo usuario (paciente o empleado) con campos separados
   * Usa los endpoints /registro/paciente o /registro/empleado
   */
  registrarUsuario(usuario: Usuario): Observable<any> {
    const rol = usuario.rol?.toUpperCase();
    
    // Preparar el payload según el tipo de usuario
    const payload = {
      pnombre: usuario.pnombre,
      snombre: usuario.snombre || null,
      papellido: usuario.papellido,
      sapellido: usuario.sapellido || null,
      rut: usuario.rut || null,
      contacto: {
        email: usuario.username,
        fono1: usuario.telefono || null
      },
      direccion: usuario.direccion ? {
        calle: usuario.direccion,
        numero: null,
        ciudad: null,
        comuna: null,
        region: null
      } : null,
      password: usuario.password
    };
    
    if (rol === 'PATIENT') {
      return this.http.post<any>(`${this.registroUrl}/paciente`, payload).pipe(
        map(response => response.data)
      );
    } else {
      // Para EMPLOYEE o ADMIN, agregar cargo y rol
      const empleadoPayload = {
        ...payload,
        cargo: usuario.cargo || 'Empleado',
        rol: rol === 'ADMIN' ? 'ADMIN' : 'TM' // TM = Tecnólogo Médico por defecto
      };
      return this.http.post<any>(`${this.registroUrl}/empleado`, empleadoPayload).pipe(
        map(response => response.data)
      );
    }
  }

  /**
   * Crear nuevo usuario (método legacy)
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
