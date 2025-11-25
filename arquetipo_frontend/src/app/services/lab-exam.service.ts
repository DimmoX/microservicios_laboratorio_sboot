
/**
 * Servicio LabExamService
 * ----------------------
 * Gestiona la obtención de exámenes de laboratorio.
 *
 * Ejemplo de uso en un componente:
 * ---------------------------------
 * import { LabExamService } from './services/lab-exam.service';
 * constructor(private labExamService: LabExamService) {}
 * ngOnInit() {
 *   const labExams = this.labExamService.getLabExams();
 * }
 *
 * Métodos:
 * --------
 * - getLabExams(): any[]
 *      Retorna un array de exámenes de laboratorio de ejemplo.
 */
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LabExamService {
  /**
   * Retorna un array de exámenes de laboratorio de ejemplo
   */
  getLabExams() {
    // Lógica para obtener exámenes de laboratorio
    return [
      { id: 1, idLaboratorio: 1, idExamen: 1, precio: 10000 },
      { id: 2, idLaboratorio: 2, idExamen: 2, precio: 15000 }
    ];
  }
}
