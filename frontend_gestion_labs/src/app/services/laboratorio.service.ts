import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { Laboratorio } from '../models/laboratorio.model';
import { MockDataService } from './mock-data.service';

@Injectable({
  providedIn: 'root'
})
export class LaboratorioService {
  constructor(private mockDataService: MockDataService) {}

  /**
   * Obtener todos los laboratorios
   * Simula endpoint: GET /labs
   */
  getLaboratorios(): Observable<Laboratorio[]> {
    return of(this.mockDataService.getLaboratorios()).pipe(delay(200));
  }

  /**
   * Buscar laboratorios por ciudad
   */
  buscarPorCiudad(ciudad: string): Observable<Laboratorio[]> {
    return this.getLaboratorios().pipe(
      map(labs => labs.filter(l =>
        l.direccion?.ciudad.toLowerCase().includes(ciudad.toLowerCase())
      ))
    );
  }

  /**
   * Buscar laboratorios por tipo
   */
  buscarPorTipo(tipo: string): Observable<Laboratorio[]> {
    return this.getLaboratorios().pipe(
      map(labs => labs.filter(l =>
        l.tipo.toLowerCase().includes(tipo.toLowerCase())
      ))
    );
  }

  /**
   * Obtener un laboratorio por ID
   * Simula endpoint: GET /labs/{id}
   */
  getLaboratorio(id: number): Observable<Laboratorio> {
    return of(null).pipe(
      delay(150),
      map(() => {
        const lab = this.mockDataService.getLaboratorios().find(l => l.id === id);
        if (!lab) {
          throw new Error('Laboratorio no encontrado');
        }
        return lab;
      })
    );
  }

  /**
   * Crear nuevo laboratorio
   * Simula endpoint: POST /labs (requiere ADMIN)
   */
  crearLaboratorio(laboratorio: Laboratorio): Observable<Laboratorio> {
    return of(null).pipe(
      delay(300),
      map(() => {
        return this.mockDataService.saveLaboratorio(laboratorio);
      })
    );
  }

  /**
   * Actualizar laboratorio
   * Simula endpoint: PUT /labs/{id} (requiere ADMIN)
   */
  actualizarLaboratorio(id: number, laboratorio: Laboratorio): Observable<Laboratorio> {
    return of(null).pipe(
      delay(300),
      map(() => {
        laboratorio.id = id;
        return this.mockDataService.saveLaboratorio(laboratorio);
      })
    );
  }

  /**
   * Eliminar laboratorio
   * Simula endpoint: DELETE /labs/{id} (requiere ADMIN)
   */
  eliminarLaboratorio(id: number): Observable<any> {
    return of(null).pipe(
      delay(200),
      map(() => {
        const success = this.mockDataService.deleteLaboratorio(id);
        if (!success) {
          throw new Error('Laboratorio no encontrado');
        }
        return { code: '000', description: 'Laboratorio eliminado exitosamente' };
      })
    );
  }
}
