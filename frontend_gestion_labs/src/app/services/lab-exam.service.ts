import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { LabExam } from '../models/lab-exam.model';
import { MockDataService } from './mock-data.service';

@Injectable({
  providedIn: 'root'
})
export class LabExamService {
  constructor(private mockDataService: MockDataService) {}

  /**
   * Obtener todas las relaciones lab-exam (precios)
   * Simula endpoint: GET /lab-exam
   */
  getLabExams(): Observable<LabExam[]> {
    return of(null).pipe(
      delay(200),
      map(() => {
        const labExams = this.mockDataService.getLabExams();
        const labs = this.mockDataService.getLaboratorios();
        const exams = this.mockDataService.getExamenes();

        // Enriquecer con nombres
        return labExams.map(le => ({
          id: le.id,
          idLaboratorio: le.labId,
          idExamen: le.examenId,
          precio: le.precio,
          nombreLab: labs.find(l => l.id === le.labId)?.nombre,
          nombreExamen: exams.find(e => e.id === le.examenId)?.nombre
        }));
      })
    );
  }

  /**
   * Obtener exámenes de un laboratorio
   */
  getExamenesPorLab(labId: number): Observable<LabExam[]> {
    return this.getLabExams().pipe(
      map(labExams => labExams.filter(le => le.idLaboratorio === labId))
    );
  }

  /**
   * Obtener laboratorios que ofrecen un examen
   */
  getLabsPorExamen(examId: number): Observable<LabExam[]> {
    return this.getLabExams().pipe(
      map(labExams => labExams.filter(le => le.idExamen === examId))
    );
  }

  /**
   * Crear nueva relación lab-exam
   * Simula endpoint: POST /lab-exam (requiere ADMIN)
   */
  crearLabExam(labExam: LabExam): Observable<LabExam> {
    return of(null).pipe(
      delay(300),
      map(() => {
        const db = this.mockDataService.getDatabase();
        const newLabExam = {
          id: db.labExams.length + 1,
          labId: labExam.idLaboratorio,
          examenId: labExam.idExamen,
          precio: labExam.precio,
          disponible: true
        };
        db.labExams.push(newLabExam);
        this.mockDataService.saveDatabase(db);
        return labExam;
      })
    );
  }

  /**
   * Actualizar relación lab-exam
   * Simula endpoint: PUT /lab-exam/{id} (requiere ADMIN)
   */
  actualizarLabExam(id: number, labExam: LabExam): Observable<LabExam> {
    return of(null).pipe(
      delay(300),
      map(() => {
        const db = this.mockDataService.getDatabase();
        const index = db.labExams.findIndex(le => le.id === id);
        if (index >= 0) {
          db.labExams[index] = {
            ...db.labExams[index],
            precio: labExam.precio
          };
          this.mockDataService.saveDatabase(db);
        }
        return labExam;
      })
    );
  }

  /**
   * Eliminar relación
   * Simula endpoint: DELETE /lab-exam/{id} (requiere ADMIN)
   */
  eliminarLabExam(id: number): Observable<any> {
    return of(null).pipe(
      delay(200),
      map(() => {
        const db = this.mockDataService.getDatabase();
        const index = db.labExams.findIndex(le => le.id === id);
        if (index >= 0) {
          db.labExams.splice(index, 1);
          this.mockDataService.saveDatabase(db);
        }
        return { code: '000', description: 'Relación eliminada exitosamente' };
      })
    );
  }
}
