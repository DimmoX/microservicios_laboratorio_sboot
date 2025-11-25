import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-ejemplo-input',
  template: `
    <div>
      <p>Valor recibido: {{ valor }}</p>
      <button (click)="emitirEvento()">Emitir evento</button>
    </div>
  `
})
export class EjemploInputComponent {
  @Input() valor: string = '';
  @Output() evento = new EventEmitter<string>();

  emitirEvento() {
    this.evento.emit('Evento emitido desde EjemploInputComponent');
  }
}
