import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EjemploComponent } from '../components/ejemplo.component';
import { EjemploInputComponent } from '../components/ejemplo-input.component';
import { EjemploPipe } from '../pipes/ejemplo.pipe';

@NgModule({
  declarations: [EjemploComponent, EjemploInputComponent, EjemploPipe],
  imports: [CommonModule],
  exports: [EjemploComponent, EjemploInputComponent, EjemploPipe]
})
export class EjemploModule {}
