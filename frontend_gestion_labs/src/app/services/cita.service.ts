import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { CitaAgendada, CrearCitaRequest } from '../models/cita.model';
import { environment } from '../../environments/environment';

interface ApiResponse<T> {
  code: string;
  description: string;
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class CitaService {
  private apiUrl = `${environment.apiUrl}/agenda`;

  constructor(private http: HttpClient) {}

  /**
   * Obtener todas las citas agendadas de un paciente
   */
  getCitasByPaciente(pacienteId: number): Observable<CitaAgendada[]> {
    return this.http.get<ApiResponse<CitaAgendada[]>>(`${this.apiUrl}/paciente/${pacienteId}`)
      .pipe(map(response => response.data));
  }

  /**
   * Obtener todas las citas
   */
  getAllCitas(): Observable<CitaAgendada[]> {
    return this.http.get<ApiResponse<CitaAgendada[]>>(this.apiUrl)
      .pipe(map(response => response.data));
  }

  /**
   * Obtener una cita por ID
   */
  getCitaById(id: number): Observable<CitaAgendada> {
    return this.http.get<ApiResponse<CitaAgendada>>(`${this.apiUrl}/${id}`)
      .pipe(map(response => response.data));
  }

  /**
   * Crear una nueva cita
   */
  crearCita(cita: CrearCitaRequest): Observable<CitaAgendada> {
    return this.http.post<ApiResponse<CitaAgendada>>(this.apiUrl, cita)
      .pipe(map(response => response.data));
  }

  /**
   * Cancelar una cita (soft delete - cambia estado a CANCELADA)
   */
  cancelarCita(id: number): Observable<CitaAgendada> {
    return this.http.put<ApiResponse<CitaAgendada>>(`${this.apiUrl}/${id}/cancelar`, {})
      .pipe(map(response => response.data));
  }

  /**
   * Eliminar físicamente una cita (hard delete - borra de BD)
   */
  eliminarCita(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Actualizar fecha/hora de una cita
   */
  actualizarFechaHora(id: number, fechaHora: string): Observable<CitaAgendada> {
    return this.http.put<ApiResponse<CitaAgendada>>(`${this.apiUrl}/${id}`, { fechaHora })
      .pipe(map(response => response.data));
  }

  /**
   * Actualizar estado de una cita (PROGRAMADA, ATENDIDA, CANCELADA)
   */
  actualizarEstado(id: number, estado: 'PROGRAMADA' | 'ATENDIDA' | 'CANCELADA'): Observable<CitaAgendada> {
    return this.http.put<ApiResponse<CitaAgendada>>(`${this.apiUrl}/${id}`, { estado })
      .pipe(map(response => response.data));
  }
}
