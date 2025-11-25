
/**
 * Componente ResultadoListComponent
 * Muestra un listado de resultados de exámenes.
 * Ejemplo de uso:
 * <app-resultado-list></app-resultado-list>
 */
import { Component } from '@angular/core';

@Component({
  selector: 'app-resultado-list',
  template: `
    <ul>
      <!-- Listado de resultados -->
      <li *ngFor="let resultado of resultados">{{ resultado | json }}</li>
    </ul>
  `
})
export class ResultadoListComponent {
  /**
   * Array de resultados de ejemplo
   */
  resultados = [
    { id: 1, valor: 'Negativo', examen: 'COVID-19' },
    { id: 2, valor: 'Positivo', examen: 'Glucosa' }
  ];
}
