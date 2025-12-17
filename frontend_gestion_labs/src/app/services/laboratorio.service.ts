import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Laboratorio } from '../models/laboratorio.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LaboratorioService {
  private apiUrl = `${environment.apiUrl}/labs`;

  constructor(private http: HttpClient) {}

  /**
   * Obtener todos los laboratorios
   * Endpoint: GET /labs
   */
  getLaboratorios(): Observable<Laboratorio[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map(response => response.data || [])
    );
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
   * Endpoint: GET /labs/{id}
   */
  getLaboratorio(id: number): Observable<Laboratorio> {
    return this.http.get<any>(`${this.apiUrl}/${id}`).pipe(
      map(response => response.data)
    );
  }

  /**
   * Crear nuevo laboratorio
   * Endpoint: POST /labs (requiere ADMIN)
   */
  crearLaboratorio(laboratorio: Laboratorio): Observable<Laboratorio> {
    return this.http.post<any>(this.apiUrl, laboratorio).pipe(
      map(response => response.data)
    );
  }

  /**
   * Actualizar laboratorio
   * Endpoint: PUT /labs/{id} (requiere ADMIN)
   */
  actualizarLaboratorio(id: number, laboratorio: Laboratorio): Observable<Laboratorio> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, laboratorio).pipe(
      map(response => response.data)
    );
  }

  /**
   * Eliminar laboratorio
   * Endpoint: DELETE /labs/{id} (requiere ADMIN)
   */
  eliminarLaboratorio(id: number): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}
