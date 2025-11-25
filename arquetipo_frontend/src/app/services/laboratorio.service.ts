
/**
 * Servicio LaboratorioService
 * --------------------------
 * Gestiona la obtención y administración de laboratorios.
 *
 * Ejemplo de uso en un componente:
 * ---------------------------------
 * import { LaboratorioService } from './services/laboratorio.service';
 * constructor(private laboratorioService: LaboratorioService) {}
 * ngOnInit() {
 *   const labs = this.laboratorioService.getLaboratorios();
 * }
 *
 * Métodos:
 * --------
 * - getLaboratorios(): any[]
 *      Retorna un array de laboratorios de ejemplo.
 */
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LaboratorioService {
  /**
   * Retorna un array de laboratorios de ejemplo
   */
  getLaboratorios() {
    // Lógica para obtener laboratorios
    return [
      { id: 1, nombre: 'Lab 1', tipo: 'Clínico' },
      { id: 2, nombre: 'Lab 2', tipo: 'Especializado' }
    ];
  }
}
