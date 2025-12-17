import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResultadoExamen } from '../models/resultado.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ResultadoService {
  private apiUrl = `${environment.apiUrl}/resultados`;

  constructor(private http: HttpClient) {}

  /**
   * Obtener todos los resultados
   * Endpoint: GET /resultados
   */
  getResultados(): Observable<ResultadoExamen[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(response => {
        return response.data || [];
      })
    );
  }

  /**
   * Obtener un resultado por ID
   * Endpoint: GET /resultados/{id}
   */
  getResultado(id: number): Observable<ResultadoExamen> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Obtener resultados por paciente
   * Endpoint: GET /resultados/paciente/{id}
   */
  getResultadosPorPaciente(pacienteId: number): Observable<ResultadoExamen[]> {
    return this.http.get<any>(`${this.apiUrl}/paciente/${pacienteId}`).pipe(
      map(response => response.data || [])
    );
  }

  /**
   * Crear nuevo resultado
   * Endpoint: POST /resultados (requiere LAB_EMPLOYEE o ADMIN)
   */
  crearResultado(resultado: ResultadoExamen): Observable<ResultadoExamen> {
    return this.http.post<any>(this.apiUrl, resultado).pipe(
      map(response => response.data)
    );
  }

  /**
   * Actualizar resultado
   * Endpoint: PUT /resultados/{id} (requiere ADMIN)
   */
  actualizarResultado(id: number, resultado: ResultadoExamen): Observable<ResultadoExamen> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, resultado).pipe(
      map(response => response.data)
    );
  }

  /**
   * Eliminar resultado
   * Endpoint: DELETE /resultados/{id} (requiere ADMIN)
   */
  eliminarResultado(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}
