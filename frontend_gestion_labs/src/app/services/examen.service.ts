import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { Examen } from '../models/examen.model';
import { MockDataService } from './mock-data.service';

@Injectable({
  providedIn: 'root'
})
export class ExamenService {
  constructor(private mockDataService: MockDataService) {}

  /**
   * Obtener todos los exámenes
   * Simula endpoint: GET /exams
   */
  getExamenes(): Observable<Examen[]> {
    return of(this.mockDataService.getExamenes()).pipe(delay(200));
  }

  /**
   * Obtener un examen por ID
   * Simula endpoint: GET /exams/{id}
   */
  getExamen(id: number): Observable<Examen> {
    return of(null).pipe(
      delay(150),
      map(() => {
        const exam = this.mockDataService.getExamenes().find(e => e.id === id);
        if (!exam) {
          throw new Error('Examen no encontrado');
        }
        return exam;
      })
    );
  }

  /**
   * Crear nuevo examen
   * Simula endpoint: POST /exams (requiere ADMIN)
   */
  crearExamen(examen: Examen): Observable<Examen> {
    return of(null).pipe(
      delay(300),
      map(() => {
        return this.mockDataService.saveExamen(examen);
      })
    );
  }

  /**
   * Actualizar examen
   * Simula endpoint: PUT /exams/{id} (requiere ADMIN)
   */
  actualizarExamen(id: number, examen: Examen): Observable<Examen> {
    return of(null).pipe(
      delay(300),
      map(() => {
        examen.id = id;
        return this.mockDataService.saveExamen(examen);
      })
    );
  }

  /**
   * Eliminar examen
   * Simula endpoint: DELETE /exams/{id} (requiere ADMIN)
   */
  eliminarExamen(id: number): Observable<any> {
    return of(null).pipe(
      delay(200),
      map(() => {
        const success = this.mockDataService.deleteExamen(id);
        if (!success) {
          throw new Error('Examen no encontrado');
        }
        return { code: '000', description: 'Examen eliminado exitosamente' };
      })
    );
  }
}
