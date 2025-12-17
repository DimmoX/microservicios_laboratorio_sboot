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
    <div class="app-container">
      <header class="header">
        <div class="header-content">
          <div class="logo" routerLink="/">
            <div class="logo-icon">🧬</div>
            <div class="logo-text">
              <span class="logo-title">Laboratorio Clínico</span>
              <span class="logo-subtitle">Andino</span>
            </div>
          </div>
          <nav class="nav">
            <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact: true}">Inicio</a>
            <a routerLink="/nosotros" routerLinkActive="active">Nosotros</a>
            <a routerLink="/servicios" routerLinkActive="active">Servicios</a>
            <a routerLink="/contacto" routerLinkActive="active">Contacto</a>

            <!-- Navegación para usuarios autenticados -->
            <ng-container *ngIf="isAuthenticated">
              <!-- Opciones para TODOS los usuarios autenticados -->
              <a routerLink="/examenes" routerLinkActive="active">Exámenes</a>
              <a routerLink="/precios" routerLinkActive="active">Precios</a>

              <!-- Opciones para ADMIN -->
              <ng-container *ngIf="isAdmin">
                <a routerLink="/laboratorios" routerLinkActive="active">Laboratorios</a>
                <a routerLink="/resultados" routerLinkActive="active">Resultados</a>
                <a routerLink="/usuarios" routerLinkActive="active">Usuarios</a>
              </ng-container>

              <!-- Opciones para LAB_EMPLOYEE -->
              <ng-container *ngIf="isLabEmployee">
                <a routerLink="/resultados" routerLinkActive="active">Resultados</a>
                <a routerLink="/usuarios" routerLinkActive="active">Usuarios</a>
              </ng-container>

              <!-- Opciones para PATIENT -->
              <ng-container *ngIf="isPatient">
                <a routerLink="/mis-resultados" routerLinkActive="active">Mis Resultados</a>
                <a routerLink="/agendar" routerLinkActive="active">Agendar Examen</a>
              </ng-container>
            </ng-container>

            <div class="nav-divider"></div>

            <!-- Botones para usuarios NO autenticados -->
            <ng-container *ngIf="!isAuthenticated">
              <a routerLink="/login" class="nav-btn">Ingresar</a>
              <a routerLink="/register" class="nav-btn primary">Registro</a>
            </ng-container>

            <!-- Dropdown de usuario autenticado -->
            <ng-container *ngIf="isAuthenticated">
              <div class="user-dropdown" (click)="toggleDropdown()">
                <div class="user-info">
                  <span class="user-icon">👤</span>
                  <div class="user-details">
                    <span class="user-name">{{currentUser?.nombre}}</span>
                    <span class="user-role"
                          [class.admin]="isAdmin"
                          [class.lab-employee]="isLabEmployee"
                          [class.patient]="isPatient">
                      {{userRoleLabel}}
                    </span>
                  </div>
                  <span class="dropdown-arrow" [class.open]="dropdownOpen">▼</span>
                </div>
                <div class="dropdown-menu" *ngIf="dropdownOpen">
                  <a routerLink="/profile" class="dropdown-item" (click)="closeDropdown()">
                    <span class="item-icon">👤</span>
                    <span>Mi Perfil</span>
                  </a>
                  <a (click)="logout()" class="dropdown-item logout">
                    <span class="item-icon">🚪</span>
                    <span>Cerrar Sesión</span>
                  </a>
                </div>
              </div>
            </ng-container>
          </nav>
        </div>
      </header>
      
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
      
      <footer class="footer">
        <div class="footer-content">
          <div class="footer-section">
            <h3>🏥 Laboratorio Clínico Andino</h3>
            <p>Excelencia en diagnóstico clínico desde 1995</p>
          </div>
          <div class="footer-section">
            <h4>Contacto</h4>
            <p>📞 +56 2 2345 6789</p>
            <p>✉️ contacto&#64;laboratorioandino.cl</p>
          </div>
          <div class="footer-section">
            <h4>Enlaces Rápidos</h4>
            <a routerLink="/nosotros">Nosotros</a>
            <a routerLink="/servicios">Servicios</a>
            <a routerLink="/contacto">Contacto</a>
          </div>
        </div>
        <p class="copyright">© 2025 Laboratorio Clínico Andino - DUOC UC</p>
      </footer>
    </div>
  `,
  styles: [`
    .app-container { display: flex; flex-direction: column; min-height: 100vh; }
    .header { background: white; box-shadow: 0 2px 8px rgba(3,105,161,0.15); position: sticky; top: 0; z-index: 100; 
      padding: 1rem 5rem; }
    .header-content { max-width: 95rem; margin: 0 auto; display: flex;
      justify-content: space-between; align-items: center; gap: 1rem; }
    .logo { display: flex; align-items: center; gap: 12px; cursor: pointer; transition: transform 0.3s; flex-shrink: 0; }
    .logo:hover { transform: scale(1.02); }
    .logo-icon { font-size: 2.5rem; line-height: 1; }
    .logo-text { display: flex; flex-direction: column; }
    .logo-title { font-size: 1.1rem; font-weight: 700; color: #0369a1; line-height: 1.2; }
    .logo-subtitle { font-size: 1.4rem; font-weight: 800; color: #075985; letter-spacing: -0.5px; }
    .nav { display: flex; align-items: center; gap: 0.75rem; flex: 1; justify-content: flex-end; }
    .nav a { text-decoration: none; color: #4b5563; font-weight: 500; padding: 0.5rem 0.875rem;
      border-radius: 6px; transition: all 0.3s; position: relative; white-space: nowrap; font-size: 0.95rem; }
    .nav a::after { content: ''; position: absolute; bottom: 0; left: 50%; transform: translateX(-50%);
      width: 0; height: 2px; background: #0369a1; transition: width 0.3s; }
    .nav a:hover { background: #f0f9ff; color: #0369a1; }
    .nav a:hover::after { width: 80%; }
    .nav a.active { background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%); color: white; }
    .nav a.active::after { width: 0; }
    .nav-divider { width: 1px; height: 30px; background: #e5e7eb; margin: 0 0.5rem; }
    .nav-btn { text-decoration: none; font-weight: 600; padding: 0.5rem 1rem;
      border-radius: 6px; transition: all 0.3s; white-space: nowrap; font-size: 0.95rem; }
    .nav-btn:not(.primary) { color: #0369a1; border: 2px solid #0369a1; }
    .nav-btn:not(.primary):hover { background: #0369a1; color: white; }
    .nav-btn.primary { background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%);
      color: white; border: none; }
    .nav-btn.primary:hover { background: linear-gradient(135deg, #075985 0%, #0891b2 100%);
      box-shadow: 0 4px 12px rgba(3,105,161,0.3); color: white; }
    .user-dropdown { position: relative; cursor: pointer; }
    .user-info { display: flex; align-items: center; gap: 0.625rem; padding: 0.5rem 1rem;
      background: #f8fafc; border: 2px solid #e5e7eb; border-radius: 8px; transition: all 0.3s; }
    .user-info:hover { border-color: #0369a1; background: #f0f9ff; }
    .user-icon { font-size: 1.25rem; }
    .user-details { display: flex; flex-direction: column; line-height: 1.2; }
    .user-name { font-weight: 600; color: #1f2937; font-size: 0.875rem; }
    .user-role { font-size: 0.7rem; color: #6b7280; font-weight: 500; }
    .user-role.admin { color: #d97706; }
    .user-role.lab-employee { color: #0369a1; }
    .user-role.patient { color: #059669; }
    .dropdown-arrow { font-size: 0.6rem; color: #6b7280; transition: transform 0.3s; }
    .dropdown-arrow.open { transform: rotate(180deg); }
    .dropdown-menu { position: absolute; top: calc(100% + 0.5rem); right: 0; background: white;
      border: 2px solid #e5e7eb; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      min-width: 200px; overflow: hidden; z-index: 1000; animation: slideDown 0.2s ease-out; }
    @keyframes slideDown { from { opacity: 0; transform: translateY(-10px); }
      to { opacity: 1; transform: translateY(0); } }
    .dropdown-item { display: flex; align-items: center; gap: 0.75rem; padding: 0.875rem 1rem;
      color: #374151; text-decoration: none; transition: all 0.2s; cursor: pointer; border: none;
      background: none; width: 100%; text-align: left; font-size: 0.95rem; font-weight: 500; }
    .dropdown-item:hover { background: #f0f9ff; color: #0369a1; }
    .dropdown-item.logout { color: #dc2626; }
    .dropdown-item.logout:hover { background: #fef2f2; color: #b91c1c; }
    .item-icon { font-size: 1.1rem; }
    .main-content { flex: 1; width: 100%; }
    .footer { background: linear-gradient(135deg, #0c4a6e 0%, #164e63 100%); color: white; padding: 3rem 5rem 1rem; }
    .footer-content { max-width: 95rem; margin: 0 auto; display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 2rem; margin-bottom: 2rem; }
    .footer-section h3, .footer-section h4 { margin-bottom: 1rem; color: #67e8f9; }
    .footer-section p { margin: 0.5rem 0; color: #e0f2fe; }
    .footer-section a { display: block; color: #bae6fd; text-decoration: none; margin: 0.5rem 0; 
      transition: color 0.3s; }
    .footer-section a:hover { color: #67e8f9; }
    .copyright { text-align: center; padding-top: 2rem; border-top: 1px solid #374151; color: #9ca3af; }
    @media (max-width: 1200px) {
      .header-content { flex-wrap: wrap; }
      .nav { order: 3; width: 100%; justify-content: center; }
    }
    @media (max-width: 767px) {
      .header-content { flex-direction: column; padding: 1rem; gap: 1rem; }
      .logo { margin: 0 auto; }
      .nav { flex-wrap: wrap; gap: 0.5rem; justify-content: center; }
      .nav a, .nav-btn { padding: 0.5rem 0.75rem; font-size: 0.875rem; }
      .nav-divider { display: none; }
      .user-info { padding: 0.5rem 0.75rem; }
      .user-name { font-size: 0.8rem; }
      .user-role { font-size: 0.65rem; }
    }
  `]
})
export class AppComponent implements OnInit {
  currentUser: Usuario | null = null;
  isAuthenticated = false;
  isAdmin = false;
  isLabEmployee = false;
  isPatient = false;
  dropdownOpen = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit() {
    // Verificar autenticación inicial
    this.isAuthenticated = this.authService.isAuthenticated();

    if (this.isAuthenticated) {
      // Cargar datos del usuario inmediatamente si está autenticado
      this.loadUserData();
    }

    // Suscribirse a cambios en el estado de autenticación
    this.authService.authStatus$.subscribe(isAuth => {
      this.isAuthenticated = isAuth;
      if (isAuth) {
        // Obtener datos del usuario cuando está autenticado
        this.loadUserData();
      } else {
        // Limpiar datos cuando no está autenticado
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
        console.log('Usuario actual:', user);
        this.currentUser = user;
        if (user && user.rol) {
          const rol = user.rol.toUpperCase();
          // Detectar rol del usuario
          this.isAdmin = rol === 'ADMIN';
          this.isLabEmployee = rol === 'LAB_EMPLOYEE';
          this.isPatient = rol === 'PATIENT';
        } else {
          this.isAdmin = false;
          this.isLabEmployee = false;
          this.isPatient = false;
        }
      },
      error: (error) => {
        this.currentUser = null;
        this.isAdmin = false;
        this.isLabEmployee = false;
        this.isPatient = false;
      }
    });
  }

  get userRoleLabel(): string {
    if (this.isAdmin) return 'Administrador';
    if (this.isLabEmployee) return 'Empleado de Laboratorio';
    if (this.isPatient) return 'Paciente';
    return 'Usuario';
  }

  toggleDropdown() {
    this.dropdownOpen = !this.dropdownOpen;
  }

  closeDropdown() {
    this.dropdownOpen = false;
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.dropdownOpen = false;
        this.router.navigate(['/']);
      },
      error: () => {
        sessionStorage.removeItem('token');
        this.dropdownOpen = false;
        this.router.navigate(['/']);
      }
    });
  }
}
