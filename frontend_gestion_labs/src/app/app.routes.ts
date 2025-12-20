import { Routes } from '@angular/router';
import { LaboratorioListComponent } from './components/laboratorio-list/laboratorio-list.component';
import { LaboratorioFormComponent } from './components/laboratorio-form/laboratorio-form.component';
import { ExamenListComponent } from './components/examen-list/examen-list.component';
import { ExamenFormComponent } from './components/examen-form/examen-form.component';
import { LabExamListComponent } from './components/lab-exam-list/lab-exam-list.component';
import { ResultadoListComponent } from './components/resultado-list/resultado-list.component';
import { UsuarioListComponent } from './components/usuario-list/usuario-list.component';
import { UsuarioFormComponent } from './components/usuario-form/usuario-form.component';
import { PacientesListComponent } from './components/pacientes-list/pacientes-list.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';
import { ProfileComponent } from './components/profile/profile.component';
import { HomeComponent } from './components/home/home.component';
import { AboutComponent } from './components/about/about.component';
import { ServicesComponent } from './components/services/services.component';
import { ContactComponent } from './components/contact/contact.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AgendaListComponent } from './components/agenda-list/agenda-list.component';
import { authGuard, adminGuard, labEmployeeGuard, patientGuard } from './guards/auth.guard';

export const routes: Routes = [
  // Rutas públicas
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'nosotros', component: AboutComponent },
  { path: 'servicios', component: ServicesComponent },
  { path: 'contacto', component: ContactComponent },

  // Dashboard - Primera página después del login
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },

  // Rutas autenticadas (todos los roles)
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'precios', component: LabExamListComponent, canActivate: [authGuard] },

  // Rutas para LAB_EMPLOYEE y ADMIN - Gestión de exámenes
  { path: 'examenes', component: ExamenListComponent, canActivate: [authGuard, labEmployeeGuard] },

  // Rutas exclusivas ADMIN
  { path: 'laboratorios', component: LaboratorioListComponent, canActivate: [authGuard, adminGuard] },
  { path: 'laboratorios/nuevo', component: LaboratorioFormComponent, canActivate: [authGuard, adminGuard] },
  { path: 'laboratorios/editar/:id', component: LaboratorioFormComponent, canActivate: [authGuard, adminGuard] },
  { path: 'examenes/nuevo', component: ExamenFormComponent, canActivate: [authGuard, adminGuard] },
  { path: 'examenes/editar/:id', component: ExamenFormComponent, canActivate: [authGuard, adminGuard] },

  // Rutas para LAB_EMPLOYEE y ADMIN
  { path: 'resultados', component: ResultadoListComponent, canActivate: [authGuard, labEmployeeGuard] },
  
  // Rutas exclusivas ADMIN - Gestión de todos los usuarios
  { path: 'usuarios', component: UsuarioListComponent, canActivate: [authGuard, adminGuard] },
  { path: 'usuarios/nuevo', component: UsuarioFormComponent, canActivate: [authGuard, adminGuard] },
  { path: 'usuarios/editar/:id', component: UsuarioFormComponent, canActivate: [authGuard, adminGuard] },

  // Rutas para LAB_EMPLOYEE - Gestión de pacientes
  { path: 'pacientes', component: PacientesListComponent, canActivate: [authGuard, labEmployeeGuard] },
  { path: 'pacientes/nuevo', component: UsuarioFormComponent, canActivate: [authGuard, labEmployeeGuard] },
  { path: 'pacientes/editar/:id', component: UsuarioFormComponent, canActivate: [authGuard, labEmployeeGuard] },

  // Rutas para PATIENT
  { path: 'mis-resultados', component: ResultadoListComponent, canActivate: [authGuard, patientGuard] }, // TODO: Filtrar solo sus resultados
  { path: 'agendar', component: AgendaListComponent, canActivate: [authGuard, patientGuard] },

  { path: '**', redirectTo: '/' }
];
