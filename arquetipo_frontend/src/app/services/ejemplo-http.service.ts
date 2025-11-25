import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EjemploHttpService {
  constructor(private http: HttpClient) {}

  getDatos(): Observable<any> {
    return this.http.get('https://jsonplaceholder.typicode.com/posts');
  }

/**
 * Servicio EjemploHttpService
 * --------------------------
 * Ejemplo de servicio para realizar peticiones HTTP simuladas.
 *
 * Ejemplo de uso en un componente:
 * ---------------------------------
 * import { EjemploHttpService } from './services/ejemplo-http.service';
 * constructor(private ejemploHttpService: EjemploHttpService) {}
 * ngOnInit() {
 *   const data = this.ejemploHttpService.getDatos();
 * }
 *
 * Métodos:
 * --------
 * - getDatos(): Observable<any>
 *      Realiza una petición HTTP y retorna los datos obtenidos.
 */

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EjemploHttpService {
  constructor(private http: HttpClient) {}

  /**
   * Realiza una petición HTTP y retorna los datos obtenidos.
   */
  getDatos(): Observable<any> {
    return this.http.get('https://jsonplaceholder.typicode.com/posts');
  }
}
