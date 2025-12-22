// Component: Register - Registro completo de pacientes
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="register-container">
      <div class="register-card">
        <div class="register-header">
          <div class="logo">🏥</div>
          <h1>Registro de Paciente</h1>
          <p>Complete sus datos para crear su cuenta</p>
        </div>

        <form (ngSubmit)="onSubmit()" #registerForm="ngForm" class="register-form">
          
          <!-- Datos Personales -->
          <h3 class="section-title">📋 Datos Personales</h3>
          
          <div class="form-row">
            <div class="form-group">
              <label for="pnombre">Primer Nombre *</label>
              <input type="text" id="pnombre" name="pnombre" [(ngModel)]="registerData.pnombre" 
                     required maxlength="20" placeholder="Juan" class="form-control" />
            </div>
            <div class="form-group">
              <label for="snombre">Segundo Nombre</label>
              <input type="text" id="snombre" name="snombre" [(ngModel)]="registerData.snombre" 
                     maxlength="20" placeholder="Carlos" class="form-control" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="papellido">Primer Apellido *</label>
              <input type="text" id="papellido" name="papellido" [(ngModel)]="registerData.papellido" 
                     required maxlength="20" placeholder="Pérez" class="form-control" />
            </div>
            <div class="form-group">
              <label for="sapellido">Segundo Apellido</label>
              <input type="text" id="sapellido" name="sapellido" [(ngModel)]="registerData.sapellido" 
                     maxlength="20" placeholder="González" class="form-control" />
            </div>
          </div>

          <div class="form-group">
            <label for="rut">RUT</label>
            <input type="text" id="rut" name="rut" [(ngModel)]="registerData.rut" 
                   maxlength="10" placeholder="12345678-9" class="form-control" />
          </div>

          <!-- Contacto -->
          <h3 class="section-title">📞 Información de Contacto</h3>
          
          <div class="form-group">
            <label for="email">Email *</label>
            <input type="email" id="email" name="email" [(ngModel)]="registerData.email" 
                   required placeholder="paciente@ejemplo.com" class="form-control" />
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="fono1">Teléfono Principal *</label>
              <input type="tel" id="fono1" name="fono1" [(ngModel)]="registerData.fono1" 
                     required maxlength="12" placeholder="+56912345678" class="form-control" />
            </div>
            <div class="form-group">
              <label for="fono2">Teléfono Secundario</label>
              <input type="tel" id="fono2" name="fono2" [(ngModel)]="registerData.fono2" 
                     maxlength="12" placeholder="+56987654321" class="form-control" />
            </div>
          </div>

          <!-- Dirección -->
          <h3 class="section-title">🏠 Dirección</h3>
          
          <div class="form-row">
            <div class="form-group">
              <label for="calle">Calle</label>
              <input type="text" id="calle" name="calle" [(ngModel)]="registerData.calle" 
                     maxlength="50" placeholder="Av. Principal" class="form-control" />
            </div>
            <div class="form-group form-group-small">
              <label for="numero">Número</label>
              <input type="number" id="numero" name="numero" [(ngModel)]="registerData.numero" 
                     placeholder="123" class="form-control" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label for="ciudad">Ciudad</label>
              <input type="text" id="ciudad" name="ciudad" [(ngModel)]="registerData.ciudad" 
                     maxlength="40" placeholder="Santiago" class="form-control" />
            </div>
            <div class="form-group">
              <label for="comuna">Comuna</label>
              <input type="text" id="comuna" name="comuna" [(ngModel)]="registerData.comuna" 
                     maxlength="40" placeholder="Providencia" class="form-control" />
            </div>
          </div>

          <div class="form-group">
            <label for="region">Región</label>
            <input type="text" id="region" name="region" [(ngModel)]="registerData.region" 
                   maxlength="60" placeholder="Región Metropolitana" class="form-control" />
          </div>

          <!-- Contraseña -->
          <h3 class="section-title">🔒 Credenciales de Acceso</h3>
          
          <div class="form-group">
            <label for="password">Contraseña *</label>
            <input type="password" id="password" name="password" [(ngModel)]="registerData.password" 
                   required minlength="6" placeholder="Mínimo 6 caracteres" class="form-control" />
          </div>

          <div class="form-group">
            <label for="confirmPassword">Confirmar Contraseña *</label>
            <input type="password" id="confirmPassword" name="confirmPassword" [(ngModel)]="confirmPassword" 
                   required minlength="6" placeholder="Repita su contraseña" class="form-control" />
          </div>

          <div class="alert alert-error" *ngIf="errorMessage">
            ⚠️ {{ errorMessage }}
          </div>

          <div class="alert alert-success" *ngIf="successMessage">
            ✅ {{ successMessage }}
          </div>

          <button type="submit" class="btn btn-primary btn-block" [disabled]="loading || !registerForm.valid">
            <span *ngIf="!loading">Registrarse</span>
            <span *ngIf="loading">⏳ Registrando...</span>
          </button>

          <div class="form-footer">
            <span>¿Ya tienes cuenta?</span>
            <a routerLink="/login" class="link">Iniciar Sesión</a>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .register-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%);
      padding: 2rem 1rem;
    }
    .register-card {
      background: white;
      border-radius: 16px;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
      width: 100%;
      max-width: 700px;
      padding: 2.5rem;
      max-height: 90vh;
      overflow-y: auto;
    }
    .register-header {
      text-align: center;
      margin-bottom: 2rem;
    }
    .logo {
      font-size: 3.5rem;
      margin-bottom: 0.5rem;
    }
    .register-header h1 {
      color: #333;
      font-size: 1.8rem;
      margin-bottom: 0.5rem;
    }
    .register-header p {
      color: #666;
      font-size: 0.95rem;
    }
    .register-form {
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
    }
    .section-title {
      color: #0369a1;
      font-size: 1.1rem;
      margin: 1rem 0 0.5rem 0;
      padding-bottom: 0.5rem;
      border-bottom: 2px solid #e0e0e0;
    }
    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 1rem;
    }
    .form-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }
    .form-group-small {
      grid-column: span 1;
    }
    .form-group label {
      font-weight: 600;
      color: #333;
      font-size: 0.9rem;
    }
    .form-control {
      padding: 0.75rem;
      border: 2px solid #e0e0e0;
      border-radius: 8px;
      font-size: 1rem;
      transition: border-color 0.3s;
    }
    .form-control:focus {
      outline: none;
      border-color: #0369a1;
    }
    .alert {
      padding: 0.75rem;
      border-radius: 8px;
      font-size: 0.9rem;
      margin: 0.5rem 0;
    }
    .alert-error {
      background-color: #fee2e2;
      color: #991b1b;
    }
    .alert-success {
      background-color: #d1fae5;
      color: #065f46;
    }
    .btn {
      padding: 0.875rem;
      border: none;
      border-radius: 8px;
      font-size: 1rem;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s;
    }
    .btn-primary {
      background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%);
      color: white;
    }
    .btn-primary:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
    }
    .btn-primary:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
    .btn-block {
      width: 100%;
    }
    .form-footer {
      text-align: center;
      margin-top: 1rem;
      color: #666;
    }
    .link {
      color: #0369a1;
      text-decoration: none;
      margin-left: 0.5rem;
    }
    .link:hover {
      text-decoration: underline;
    }
    @media (max-width: 767px) {
      .form-row {
        grid-template-columns: 1fr;
      }
      .register-card {
        padding: 1.5rem;
        max-height: 95vh;
      }
      .register-header h1 {
        font-size: 1.4rem;
      }
      .section-title {
        font-size: 1rem;
      }
    }
  `]
})
export class RegisterComponent {
  registerData = {
    pnombre: '',
    snombre: '',
    papellido: '',
    sapellido: '',
    rut: '',
    email: '',
    fono1: '',
    fono2: '',
    calle: '',
    numero: null as number | null,
    ciudad: '',
    comuna: '',
    region: '',
    password: ''
  };

  confirmPassword = '';
  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    // Validaciones
    if (!this.registerData.pnombre || !this.registerData.papellido || 
        !this.registerData.email || !this.registerData.fono1 || !this.registerData.password) {
      this.errorMessage = 'Por favor complete todos los campos obligatorios (*)';
      return;
    }

    if (this.registerData.password !== this.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden';
      return;
    }

    if (this.registerData.password.length < 6) {
      this.errorMessage = 'La contraseña debe tener al menos 6 caracteres';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    // Construir el payload para el endpoint /registro/paciente
    const registroData = {
      pnombre: this.registerData.pnombre,
      snombre: this.registerData.snombre || null,
      papellido: this.registerData.papellido,
      sapellido: this.registerData.sapellido || null,
      rut: this.registerData.rut || null,
      username: this.registerData.email, // Email como username
      password: this.registerData.password,
      contacto: {
        fono1: this.registerData.fono1,
        fono2: this.registerData.fono2 || null,
        email: this.registerData.email
      },
      direccion: {
        calle: this.registerData.calle || null,
        numero: this.registerData.numero,
        ciudad: this.registerData.ciudad || null,
        comuna: this.registerData.comuna || null,
        region: this.registerData.region || null
      }
    };

    this.authService.registerPaciente(registroData).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = '¡Registro exitoso! Redirigiendo al login...';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (error: any) => {
        this.loading = false;
        this.errorMessage = error.message || 'Error al registrar usuario';
      }
    });
  }
}
