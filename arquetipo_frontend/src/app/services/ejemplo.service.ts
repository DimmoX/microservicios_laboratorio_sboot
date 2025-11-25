import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class EjemploService {
  getMensaje(): string {
    return 'Servicio funcionando';
  }
}
