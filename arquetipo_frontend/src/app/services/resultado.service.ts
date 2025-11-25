
/**
 * Servicio ResultadoService
 * ------------------------
 * Gestiona la obtención de resultados de exámenes.
 *
 * Ejemplo de uso en un componente:
 * ---------------------------------
 * import { ResultadoService } from './services/resultado.service';
 * constructor(private resultadoService: ResultadoService) {}
 * ngOnInit() {
 *   const resultados = this.resultadoService.getResultados();
 * }
 *
 * Métodos:
 * --------
 * - getResultados(): any[]
 *      Retorna un array de resultados de ejemplo.
 */
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ResultadoService {
  /**
   * Retorna un array de resultados de ejemplo
   */
  getResultados() {
    // Lógica para obtener resultados
    return [
      { id: 1, valor: 'Negativo', examen: 'COVID-19' },
      { id: 2, valor: 'Positivo', examen: 'Glucosa' }
    ];
  }
}
