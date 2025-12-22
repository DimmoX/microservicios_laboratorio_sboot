import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Paciente, RegistroPacienteRequest } from '../models/paciente.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PacienteService {
  private apiUrl = `${environment.apiUrl}/pacientes`;

  constructor(private http: HttpClient) {}

  /**
   * Obtener todos los pacientes
   * Endpoint: GET /pacientes
   */
  getPacientes(): Observable<Paciente[]> {
    return this.http.get<any>(`${this.apiUrl}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Obtener un paciente por ID
   * Endpoint: GET /pacientes/{id}
   */
  getPaciente(id: number): Observable<Paciente> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Crear un nuevo paciente (registro completo)
   * Endpoint: POST /registro/paciente
   */
  registrarPaciente(request: RegistroPacienteRequest): Observable<any> {
    return this.http.post<any>(`${environment.apiUrl}/registro/paciente`, request).pipe(
      map(response => response.data)
    );
  }

  /**
   * Actualizar un paciente
   * Endpoint: PUT /pacientes/{id}
   */
  actualizarPaciente(id: number, paciente: Partial<Paciente>): Observable<Paciente> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, paciente).pipe(
      map(response => response.data)
    );
  }

  /**
   * Eliminar un paciente
   * Endpoint: DELETE /pacientes/{id}
   */
  eliminarPaciente(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}
