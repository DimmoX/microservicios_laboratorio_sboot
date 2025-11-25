import { Routes } from '@angular/router';
import { EjemploComponent } from './ejemplo.component';
import { EjemploGuard } from './ejemplo.guard';

export const routes: Routes = [
  { path: '', component: EjemploComponent },
  { path: 'protegido', component: EjemploComponent, canActivate: [EjemploGuard] }
];
