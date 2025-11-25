import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { ResultadoExamen } from '../models/resultado.model';
import { MockDataService } from './mock-data.service';

@Injectable({
  providedIn: 'root'
})
export class ResultadoService {
  constructor(private mockDataService: MockDataService) {}

  /**
   * Obtener todos los resultados
   * Simula endpoint: GET /resultados
   */
  getResultados(): Observable<ResultadoExamen[]> {
    return of(this.mockDataService.getResultados()).pipe(delay(200));
  }

  /**
   * Obtener un resultado por ID
   * Simula endpoint: GET /resultados/{id}
   */
  getResultado(id: number): Observable<ResultadoExamen> {
    return of(null).pipe(
      delay(150),
      map(() => {
        const resultado = this.mockDataService.getResultados().find(r => r.id === id);
        if (!resultado) {
          throw new Error('Resultado no encontrado');
        }
        return resultado;
      })
    );
  }

  /**
   * Obtener resultados por paciente
   * Simula endpoint: GET /resultados/paciente/{id}
   */
  getResultadosPorPaciente(pacienteId: number): Observable<ResultadoExamen[]> {
    return of(this.mockDataService.getResultadosByPaciente(pacienteId)).pipe(delay(200));
  }

  /**
   * Crear nuevo resultado
   * Simula endpoint: POST /resultados (requiere LAB_EMPLOYEE o ADMIN)
   */
  crearResultado(resultado: ResultadoExamen): Observable<ResultadoExamen> {
    return of(null).pipe(
      delay(300),
      map(() => {
        return this.mockDataService.saveResultado(resultado);
      })
    );
  }

  /**
   * Actualizar resultado
   * Simula endpoint: PUT /resultados/{id} (requiere ADMIN)
   */
  actualizarResultado(id: number, resultado: ResultadoExamen): Observable<ResultadoExamen> {
    return of(null).pipe(
      delay(300),
      map(() => {
        resultado.id = id;
        return this.mockDataService.saveResultado(resultado);
      })
    );
  }

  /**
   * Eliminar resultado
   * Simula endpoint: DELETE /resultados/{id} (requiere ADMIN)
   */
  eliminarResultado(id: number): Observable<any> {
    return of(null).pipe(
      delay(200),
      map(() => {
        const success = this.mockDataService.deleteResultado(id);
        if (!success) {
          throw new Error('Resultado no encontrado');
        }
        return { code: '000', description: 'Resultado eliminado exitosamente' };
      })
    );
  }
}
