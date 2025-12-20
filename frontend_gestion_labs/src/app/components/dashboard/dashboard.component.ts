import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Usuario } from '../../models/usuario.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="dashboard-container py-5">
      <div class="container">
        <!-- Welcome Header -->
        <div class="welcome-section mb-5">
          <h1 class="display-4 mb-3">👋 Bienvenido, {{currentUser?.nombre}}</h1>
          <p class="lead text-muted">Panel de control - Laboratorio Clínico Andino</p>
          <span class="badge" [class.bg-warning]="isAdmin" [class.bg-info]="isLabEmployee" [class.bg-success]="isPatient" style="font-size: 1rem; color: white !important;">
            {{userRoleLabel}}
          </span>
        </div>

        <!-- Admin Dashboard -->
        <div *ngIf="isAdmin" class="row g-4">
          <div class="col-12">
            <h3 class="mb-4 text-white">📊 Panel de Administrador</h3>
          </div>

          <div class="col-md-4">
            <div class="card dashboard-card h-100 border-0 shadow-sm" routerLink="/laboratorios" style="cursor: pointer;">
              <div class="card-body text-center p-4">
                <div class="icon-wrapper mb-3">
                  <span style="font-size: 3rem;">🏥</span>
                </div>
                <h5 class="card-title">Gestión de Laboratorios</h5>
                <p class="card-text text-muted">Administrar laboratorios clínicos</p>
              </div>
            </div>
          </div>

          <div class="col-md-4">
            <div class="card dashboard-card h-100 border-0 shadow-sm" routerLink="/examenes" style="cursor: pointer;">
              <div class="card-body text-center p-4">
                <div class="icon-wrapper mb-3">
                  <span style="font-size: 3rem;">🧪</span>
                </div>
                <h5 class="card-title">Gestión de Exámenes</h5>
                <p class="card-text text-muted">Administrar tipos de exámenes</p>
              </div>
            </div>
          </div>

          <div class="col-md-4">
            <div class="card dashboard-card h-100 border-0 shadow-sm" routerLink="/usuarios" style="cursor: pointer;">
              <div class="card-body text-center p-4">
                <div class="icon-wrapper mb-3">
                  <span style="font-size: 3rem;">👥</span>
                </div>
                <h5 class="card-title">Gestión de Usuarios</h5>
                <p class="card-text text-muted">Administrar usuarios del sistema</p>
              </div>
            </div>
          </div>

          <div class="col-md-4">
            <div class="card dashboard-card h-100 border-0 shadow-sm" routerLink="/resultados" style="cursor: pointer;">
              <div class="card-body text-center p-4">
                <div class="icon-wrapper mb-3">
                  <span style="font-size: 3rem;">📋</span>
                </div>
                <h5 class="card-title">Gestión de Resultados</h5>
                <p class="card-text text-muted">Ver y administrar resultados</p>
              </div>
            </div>
          </div>

          <div class="col-md-4">
            <div class="card dashboard-card h-100 border-0 shadow-sm" routerLink="/precios" style="cursor: pointer;">
              <div class="card-body text-center p-4">
                <div class="icon-wrapper mb-3">
                  <span style="font-size: 3rem;">💰</span>
                </div>
                <h5 class="card-title">Precios de Exámenes</h5>
                <p class="card-text text-muted">Ver precios por laboratorio</p>
              </div>
            </div>
          </div>

          <div class="col-md-4">
            <div class="card dashboard-card h-100 border-0 shadow-sm" routerLink="/servicios" style="cursor: pointer;">
              <div class="card-body text-center p-4">
                <div class="icon-wrapper mb-3">
                  <span style="font-size: 3rem;">🔬</span>
                </div>
                <h5 class="card-title">Servicios</h5>
                <p class="card-text text-muted">Ver servicios disponibles</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Lab Employee Dashboard -->
        <div *ngIf="isLabEmployee" class="row g-4">
          <div class="col-12">
            <h3 class="mb-4 text-white">🔬 Panel de Empleado de Laboratorio</h3>
          </div>

          <div class="col-md-6">
            <div class="card dashboard-card h-100 border-0 shadow-sm" routerLink="/resultados" style="cursor: pointer;">
              <div class="card-body text-center p-4">
                <div class="icon-wrapper mb-3">
                  <span style="font-size: 3rem;">📋</span>
                </div>
                <h5 class="card-title">Gestión de Resultados</h5>
                <p class="card-text text-muted">Registrar y actualizar resultados de exámenes</p>
              </div>
            </div>
          </div>

          <div class="col-md-6">
            <div class="card dashboard-card h-100 border-0 shadow-sm" routerLink="/pacientes" style="cursor: pointer;">
              <div class="card-body text-center p-4">
                <div class="icon-wrapper mb-3">
                  <span style="font-size: 3rem;">👥</span>
                </div>
                <h5 class="card-title">Pacientes</h5>
                <p class="card-text text-muted">Ver información de pacientes</p>
              </div>
            </div>
          </div>

          <div class="col-md-6">
            <div class="card dashboard-card h-100 border-0 shadow-sm" routerLink="/examenes" style="cursor: pointer;">
              <div class="card-body text-center p-4">
                <div class="icon-wrapper mb-3">
                  <span style="font-size: 3rem;">🧪</span>
                </div>
                <h5 class="card-title">Exámenes Disponibles</h5>
                <p class="card-text text-muted">Ver catálogo de exámenes</p>
              </div>
            </div>
          </div>

          <div class="col-md-6">
            <div class="card dashboard-card h-100 border-0 shadow-sm" routerLink="/precios" style="cursor: pointer;">
              <div class="card-body text-center p-4">
                <div class="icon-wrapper mb-3">
                  <span style="font-size: 3rem;">💰</span>
                </div>
                <h5 class="card-title">Precios</h5>
                <p class="card-text text-muted">Consultar precios de exámenes</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Patient Dashboard -->
        <div *ngIf="isPatient" class="row g-4">
          <div class="col-12">
            <h3 class="mb-4">🩺 Panel de Paciente</h3>
          </div>

          <div class="col-md-6">
            <div class="card dashboard-card h-100 border-0 shadow-sm" routerLink="/mis-resultados" style="cursor: pointer;">
              <div class="card-body text-center p-4">
                <div class="icon-wrapper mb-3">
                  <span style="font-size: 3rem;">📊</span>
                </div>
                <h5 class="card-title">Mis Resultados</h5>
                <p class="card-text text-muted">Ver mis resultados de exámenes</p>
              </div>
            </div>
          </div>

          <div class="col-md-6">
            <div class="card dashboard-card h-100 border-0 shadow-sm" routerLink="/agendar" style="cursor: pointer;">
              <div class="card-body text-center p-4">
                <div class="icon-wrapper mb-3">
                  <span style="font-size: 3rem;">📅</span>
                </div>
                <h5 class="card-title">Agendar Examen</h5>
                <p class="card-text text-muted">Solicitar un nuevo examen</p>
              </div>
            </div>
          </div>

          <div class="col-md-6">
            <div class="card dashboard-card h-100 border-0 shadow-sm" routerLink="/examenes" style="cursor: pointer;">
              <div class="card-body text-center p-4">
                <div class="icon-wrapper mb-3">
                  <span style="font-size: 3rem;">🧪</span>
                </div>
                <h5 class="card-title">Exámenes Disponibles</h5>
                <p class="card-text text-muted">Ver exámenes que puedes solicitar</p>
              </div>
            </div>
          </div>

          <div class="col-md-6">
            <div class="card dashboard-card h-100 border-0 shadow-sm" routerLink="/precios" style="cursor: pointer;">
              <div class="card-body text-center p-4">
                <div class="icon-wrapper mb-3">
                  <span style="font-size: 3rem;">💰</span>
                </div>
                <h5 class="card-title">Precios</h5>
                <p class="card-text text-muted">Consultar precios de exámenes</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Quick Links Section -->
        <div class="row mt-5">
          <div class="col-12">
            <div class="card border-0 shadow-sm">
              <div class="card-body">
                <h5 class="card-title mb-3">🔗 Enlaces Rápidos</h5>
                <div class="d-flex flex-wrap gap-2">
                  <a routerLink="/profile" class="btn btn-outline-primary btn-sm">
                    <span class="me-1">👤</span> Mi Perfil
                  </a>
                  <a routerLink="/nosotros" class="btn btn-outline-secondary btn-sm">
                    <span class="me-1">ℹ️</span> Acerca de Nosotros
                  </a>
                  <a routerLink="/servicios" class="btn btn-outline-secondary btn-sm">
                    <span class="me-1">🔬</span> Servicios
                  </a>
                  <a routerLink="/contacto" class="btn btn-outline-secondary btn-sm">
                    <span class="me-1">📞</span> Contacto
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      min-height: calc(100vh - 60px);
      padding: 0;
      margin: 0;
      background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%);
    }

    .welcome-section {
      background: white;
      padding: 2rem;
      border-radius: 12px;
      box-shadow: 0 2px 12px rgba(0, 38, 62, 0.08);
    }

    .dashboard-card {
      transition: all 0.3s ease;
      background: white;
    }

    .dashboard-card:hover {
      transform: translateY(-8px);
      box-shadow: 0 8px 24px rgba(61, 118, 153, 0.25) !important;
    }

    .icon-wrapper {
      transition: transform 0.3s ease;
    }

    .dashboard-card:hover .icon-wrapper {
      transform: scale(1.1);
    }

    .card-title {
      color: #144766;
      font-weight: 600;
    }

    .badge {
      padding: 0.5rem 1rem;
      font-weight: 500;
    }

    @media (max-width: 767px) {
      .welcome-section {
        padding: 1.5rem;
      }

      .display-4 {
        font-size: 2rem;
      }

      .dashboard-card {
        margin-bottom: 1rem;
      }
    }
  `]
})
export class DashboardComponent implements OnInit {
  currentUser: Usuario | null = null;
  isAdmin = false;
  isLabEmployee = false;
  isPatient = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserData();
  }

  private loadUserData(): void {
    this.authService.currentUser.subscribe({
      next: (user: Usuario | null) => {
        this.currentUser = user;
        if (user && user.rol) {
          const rol = user.rol.toUpperCase();
          this.isAdmin = rol === 'ADMIN';
          this.isLabEmployee = rol === 'LAB_EMPLOYEE';
          this.isPatient = rol === 'PATIENT';
        }
      },
      error: () => {
        this.router.navigate(['/login']);
      }
    });
  }

  get userRoleLabel(): string {
    if (this.isAdmin) return 'Administrador';
    if (this.isLabEmployee) return 'Empleado de Laboratorio';
    if (this.isPatient) return 'Paciente';
    return 'Usuario';
  }
}
