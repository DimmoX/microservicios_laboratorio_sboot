

/**
 * Servicio ExamenService
 * ----------------------
 * Provee métodos para gestionar exámenes de laboratorio.
 *
 * Ejemplo de uso en un componente:
 * ---------------------------------
 * import { ExamenService } from './services/examen.service';
 * constructor(private examenService: ExamenService) {}
 * ngOnInit() {
 *   const examenes = this.examenService.getExamenes();
 * }
 *
 * Métodos:
 * --------
 * - getExamenes(): any[]
 *      Retorna un array de exámenes de ejemplo.
 */
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ExamenService {
  /**
   * Retorna un array de exámenes de ejemplo
   */
  getExamenes() {
    // Lógica para obtener exámenes
    return [
      { id: 1, codigo: 'GLU', nombre: 'Glucosa', tipo: 'Sangre' },
      { id: 2, codigo: 'COVID', nombre: 'COVID-19', tipo: 'PCR' }
    ];
  }
}
