import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { LabExam } from '../models/lab-exam.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LabExamService {
  private apiUrl = `${environment.apiUrl}/lab-exams`;

  constructor(private http: HttpClient) {}

  /**
   * Obtener todas las relaciones lab-exam (precios)
   * Endpoint: GET /lab-exams
   */
  getLabExams(): Observable<LabExam[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(response => response.data || [])
    );
  }

  /**
   * Obtener exámenes de un laboratorio
   * Endpoint: GET /lab-exams/lab/{labId}
   */
  getExamenesPorLab(labId: number): Observable<LabExam[]> {
    return this.http.get<any>(`${this.apiUrl}/lab/${labId}`).pipe(
      map(response => response.data || [])
    );
  }

  /**
   * Obtener laboratorios que ofrecen un examen
   * Endpoint: GET /lab-exams/exam/{examId}
   */
  getLabsPorExamen(examId: number): Observable<LabExam[]> {
    return this.http.get<any>(`${this.apiUrl}/exam/${examId}`).pipe(
      map(response => response.data || [])
    );
  }

  /**
   * Crear nueva relación lab-exam
   * Endpoint: POST /lab-exams (requiere ADMIN)
   */
  crearLabExam(labExam: LabExam): Observable<LabExam> {
    return this.http.post<any>(this.apiUrl, labExam).pipe(
      map(response => response.data)
    );
  }

  /**
   * Actualizar relación lab-exam
   * Endpoint: PUT /lab-exams/{id} (requiere ADMIN)
   */
  actualizarLabExam(id: number, labExam: LabExam): Observable<LabExam> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, labExam).pipe(
      map(response => response.data)
    );
  }

  /**
   * Eliminar relación
   * Endpoint: DELETE /lab-exams/{id} (requiere ADMIN)
   */
  eliminarLabExam(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}
