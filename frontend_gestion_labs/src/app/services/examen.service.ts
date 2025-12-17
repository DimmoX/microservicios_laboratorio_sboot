import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Examen } from '../models/examen.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ExamenService {
  private apiUrl = `${environment.apiUrl}/exams`;

  constructor(private http: HttpClient) {}

  /**
   * Obtener todos los exámenes
   * Endpoint: GET /exams
   */
  getExamenes(): Observable<Examen[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(response => response.data || [])
    );
  }

  /**
   * Obtener un examen por ID
   * Endpoint: GET /exams/{id}
   */
  getExamen(id: number): Observable<Examen> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Crear nuevo examen
   * Endpoint: POST /exams (requiere ADMIN)
   */
  crearExamen(examen: Examen): Observable<Examen> {
    return this.http.post<any>(this.apiUrl, examen).pipe(
      map(response => response.data)
    );
  }

  /**
   * Actualizar examen
   * Endpoint: PUT /exams/{id} (requiere ADMIN)
   */
  actualizarExamen(id: number, examen: Examen): Observable<Examen> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, examen).pipe(
      map(response => response.data)
    );
  }

  /**
   * Eliminar examen
   * Endpoint: DELETE /exams/{id} (requiere ADMIN)
   */
  eliminarExamen(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}
