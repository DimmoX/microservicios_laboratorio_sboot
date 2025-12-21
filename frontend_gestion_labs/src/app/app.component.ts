import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { Usuario } from './models/usuario.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="d-flex flex-column min-vh-100" style="background: linear-gradient(135deg, #3D7699 0%, #7AADCC 100%);">
      <!-- Navbar Bootstrap Responsive -->
      <nav class="navbar navbar-expand-xl navbar-light bg-white shadow-sm sticky-top">
        <div class="container-fluid px-3 px-md-4 px-xl-5">
          <!-- Brand/Logo -->
          <a class="navbar-brand d-flex align-items-center" routerLink="/" style="cursor: pointer;">
            <span style="font-size: 2rem;">🧬</span>
            <div class="ms-2">
              <div class="fw-bold" style="font-size: 1rem; line-height: 1.2; color: #3D7699;">Laboratorio Clínico</div>
              <div class="fw-bold" style="font-size: 1.2rem; color: #144766; letter-spacing: -0.5px; line-height: 1;">Andino</div>
            </div>
          </a>
          
          <!-- Mobile Toggle Button -->
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" 
                  aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
          </button>
          
          <!-- Navbar Links -->
          <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto align-items-xl-center">
              <!-- Public Links -->
              <li class="nav-item">
                <a class="nav-link" routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact: true}">Inicio</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" routerLink="/nosotros" routerLinkActive="active">Nosotros</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" routerLink="/servicios" routerLinkActive="active">Servicios</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" routerLink="/contacto" routerLinkActive="active">Contacto</a>
              </li>

              <!-- Authenticated User Links -->
              <ng-container *ngIf="isAuthenticated">
                <li class="nav-item">
                  <a class="nav-link" routerLink="/precios" routerLinkActive="active">Precios</a>
                </li>

                <!-- Admin and Lab Employee Links -->
                <ng-container *ngIf="isAdmin || isLabEmployee">
                  <li class="nav-item">
                    <a class="nav-link" routerLink="/examenes" routerLinkActive="active">Exámenes</a>
                  </li>
                </ng-container>

                <!-- Admin Links -->
                <ng-container *ngIf="isAdmin">
                  <li class="nav-item">
                    <a class="nav-link" routerLink="/laboratorios" routerLinkActive="active">Laboratorios</a>
                  </li>
                  <li class="nav-item">
                    <a class="nav-link" routerLink="/admin/lab-exam" routerLinkActive="active">Asignación de Precios</a>
                  </li>
                  <li class="nav-item">
                    <a class="nav-link" routerLink="/resultados" routerLinkActive="active">Resultados</a>
                  </li>
                  <li class="nav-item">
                    <a class="nav-link" routerLink="/usuarios" routerLinkActive="active">Usuarios</a>
                  </li>
                </ng-container>

                <!-- Lab Employee Links -->
                <ng-container *ngIf="isLabEmployee">
                  <li class="nav-item">
                    <a class="nav-link" routerLink="/resultados" routerLinkActive="active">Resultados</a>
                  </li>
                </ng-container>

                <!-- Patient Links -->
                <ng-container *ngIf="isPatient">
                  <li class="nav-item">
                    <a class="nav-link" routerLink="/mis-resultados" routerLinkActive="active">Mis Resultados</a>
                  </li>
                  <li class="nav-item">
                    <a class="nav-link" routerLink="/agendar" routerLinkActive="active">Agendar</a>
                  </li>
                </ng-container>
              </ng-container>

              <!-- Guest User Buttons -->
              <ng-container *ngIf="!isAuthenticated">
                <li class="nav-item ms-xl-2 mt-2 mt-xl-0">
                  <a class="btn btn-outline-primary btn-sm w-100 w-xl-auto" routerLink="/login">Ingresar</a>
                </li>
                <li class="nav-item ms-xl-2 mt-2 mt-xl-0">
                  <a class="btn btn-primary btn-sm w-100 w-xl-auto" routerLink="/register">Registro</a>
                </li>
              </ng-container>

              <!-- Dashboard Link -->
              <ng-container *ngIf="isAuthenticated">
                <li class="nav-item ms-xl-2 mt-2 mt-xl-0">
                  <a class="btn btn-primary btn-sm" routerLink="/dashboard">Dashboard</a>
                </li>
              </ng-container>

              <!-- User Dropdown -->
              <ng-container *ngIf="isAuthenticated">
                <li class="nav-item dropdown ms-xl-2 mt-2 mt-xl-0">
                  <a class="nav-link dropdown-toggle d-flex align-items-center" href="#" role="button" 
                     data-bs-toggle="dropdown" aria-expanded="false" id="userDropdown">
                    <span class="me-2">👤</span>
                    <span>{{currentUser?.nombre || 'Usuario'}}</span>
                  </a>
                  <ul class="dropdown-menu dropdown-menu-end mt-2 shadow border-0" aria-labelledby="userDropdown">
                    <li><h6 class="dropdown-header text-truncate">{{currentUser?.nombre}}</h6></li>
                    <li><span class="dropdown-item-text small">
                      <span class="badge" [class.bg-warning]="isAdmin" [class.bg-info]="isLabEmployee" [class.bg-success]="isPatient">
                        {{userRoleLabel}}
                      </span>
                    </span></li>
                    <li><hr class="dropdown-divider"></li>
                    <li><a class="dropdown-item" routerLink="/profile"><span class="me-2">👤</span>Mi Perfil</a></li>
                    <li><a class="dropdown-item text-danger" (click)="logout()" style="cursor: pointer;">
                      <span class="me-2">🚪</span>Cerrar Sesión</a></li>
                  </ul>
                </li>
              </ng-container>
            </ul>
          </div>
        </div>
      </nav>
      
      <!-- Main Content Area -->
      <main class="flex-grow-1">
        <router-outlet></router-outlet>
      </main>
      
      <!-- Footer Bootstrap -->
      <footer class="py-4" style="background-color: #144766; color: white;">
        <div class="container">
          <div class="row g-4">
            <div class="col-md-4">
              <h5 style="color: #7AADCC;">🏥 Laboratorio Clínico Andino</h5>
              <p class="small">Excelencia en diagnóstico clínico desde 1995</p>
            </div>
            <div class="col-md-4">
              <h6 style="color: #7AADCC;">Contacto</h6>
              <p class="small mb-1">📞 +56 2 2345 6789</p>
              <p class="small mb-0">✉️ contacto&#64;laboratorioandino.cl</p>
            </div>
            <div class="col-md-4">
              <h6 style="color: #7AADCC;">Enlaces Rápidos</h6>
              <a routerLink="/nosotros" class="d-block text-decoration-none small mb-1" style="color: rgba(255,255,255,0.7);">Nosotros</a>
              <a routerLink="/servicios" class="d-block text-decoration-none small mb-1" style="color: rgba(255,255,255,0.7);">Servicios</a>
              <a routerLink="/contacto" class="d-block text-decoration-none small" style="color: rgba(255,255,255,0.7);">Contacto</a>
            </div>
          </div>
          <hr style="border-color: rgba(122, 173, 204, 0.3);" class="my-3">
          <p class="text-center small mb-0" style="color: rgba(255,255,255,0.6);">© 2025 Laboratorio Clínico Andino - DUOC UC</p>
        </div>
      </footer>
    </div>
  `,
  styles: [`
    .nav-link {
      color: #144766;
      font-weight: 500;
      padding: 0.5rem 0.875rem;
      border-radius: 6px;
      transition: all 0.3s;
    }
    
    .nav-link:hover {
      background-color: rgba(122, 173, 204, 0.1);
      color: #3D7699;
    }
    
    .nav-link.active {
      background: linear-gradient(135deg, #3D7699 0%, #144766 100%);
      color: white !important;
    }
    
    .navbar-brand:hover {
      transform: scale(1.02);
      transition: transform 0.3s;
    }
    
    .dropdown-item:hover {
      background-color: rgba(122, 173, 204, 0.15);
      color: #3D7699;
    }
    
    footer a:hover {
      color: #7AADCC !important;
    }

    .btn-primary {
      background: linear-gradient(135deg, #3D7699 0%, #144766 100%);
      border: none;
      color: white;
    }

    .btn-primary:hover {
      background: linear-gradient(135deg, #144766 0%, #00263E 100%);
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(61, 118, 153, 0.3);
    }

    .btn-outline-primary {
      color: #3D7699;
      border-color: #3D7699;
    }

    .btn-outline-primary:hover {
      background-color: #3D7699;
      border-color: #3D7699;
      color: white;
    }

    /* Asegurar que no haya espacios ni fondos extraños */
    nav.navbar {
      margin-bottom: 0 !important;
    }

    main {
      background: transparent !important;
      padding: 0;
      margin: 0;
    }
  `]
})
export class AppComponent implements OnInit {
  currentUser: Usuario | null = null;
  isAuthenticated = false;
  isAdmin = false;
  isLabEmployee = false;
  isPatient = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.isAuthenticated = this.authService.isAuthenticated();

    if (this.isAuthenticated) {
      this.loadUserData();
    }

    this.authService.authStatus$.subscribe(isAuth => {
      this.isAuthenticated = isAuth;
      if (isAuth) {
        this.loadUserData();
      } else {
        this.currentUser = null;
        this.isAdmin = false;
        this.isLabEmployee = false;
        this.isPatient = false;
      }
    });
  }

  private loadUserData() {
    this.authService.currentUser.subscribe({
      next: (user: Usuario | null) => {
        this.currentUser = user;
        if (user && user.rol) {
          const rol = user.rol.toUpperCase();
          this.isAdmin = rol === 'ADMIN';
          this.isLabEmployee = rol === 'EMPLOYEE';
          this.isPatient = rol === 'PATIENT';
        }
      },
      error: () => {
        this.currentUser = null;
        this.isAdmin = false;
        this.isLabEmployee = false;
        this.isPatient = false;
      }
    });
  }

  get userRoleLabel(): string {
    if (this.isAdmin) return 'Administrador';
    if (this.isLabEmployee) return 'Empleado';
    if (this.isPatient) return 'Paciente';
    return 'Usuario';
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/']),
      error: () => {
        sessionStorage.removeItem('token');
        this.router.navigate(['/']);
      }
    });
  }
}
