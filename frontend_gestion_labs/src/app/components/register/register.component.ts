// Component: Register
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../models/usuario.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="register-container">
      <div class="register-card">
        <div class="register-header">
          <div class="logo">🏥</div>
          <h1>Crear Cuenta Nueva</h1>
          <p>Regístrate para acceder a nuestros servicios</p>
        </div>

        <form (ngSubmit)="onSubmit()" #registerForm="ngForm" class="register-form">
          <div class="form-group">
            <label for="nombre">👤 Nombre Completo</label>
            <input type="text" id="nombre" name="nombre" [(ngModel)]="registerData.nombre" 
                   required minlength="3" placeholder="Juan Pérez" class="form-control" />
          </div>

          <div class="form-group">
                 <label for="username">👤 Usuario</label>
                 <input type="text" id="username" name="username" [(ngModel)]="registerData.username" 
                   required placeholder="usuario@ejemplo.com" class="form-control" />
          </div>

          <div class="form-group">
            <label for="telefono">📱 Teléfono</label>
            <input type="tel" id="telefono" name="telefono" [(ngModel)]="registerData.telefono" 
                   placeholder="+56 9 1234 5678" class="form-control" />
          </div>

          <div class="form-group">
            <label for="direccion">🏠 Dirección</label>
            <input type="text" id="direccion" name="direccion" [(ngModel)]="registerData.direccion" 
                   placeholder="Av. Principal 123, Santiago" class="form-control" />
          </div>

          <div class="form-group">
            <label for="password">🔒 Contraseña</label>
            <input type="password" id="password" name="password" [(ngModel)]="registerData.password" 
                   required minlength="6" placeholder="Mínimo 6 caracteres" class="form-control" />
          </div>

          <div class="alert alert-error" *ngIf="errorMessage">
            ⚠️ {{ errorMessage }}
          </div>

          <div class="alert alert-success" *ngIf="successMessage">
            ✅ {{ successMessage }}
          </div>

          <button type="submit" class="btn btn-primary btn-block" [disabled]="loading">
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
      padding: 1rem;
    }
    .register-card {
      background: white;
      border-radius: 16px;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
      width: 100%;
      max-width: 500px;
      padding: 2rem;
    }
    .register-header {
      text-align: center;
      margin-bottom: 1.5rem;
    }
    .logo {
      font-size: 3rem;
      margin-bottom: 0.5rem;
    }
    .register-header h1 {
      color: #333;
      font-size: 1.6rem;
      margin-bottom: 0.5rem;
    }
    .register-header p {
      color: #666;
      font-size: 0.9rem;
    }
    .register-form {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }
    .form-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
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
      .register-card {
        padding: 1.5rem;
      }
      .register-header h1 {
        font-size: 1.4rem;
      }
    }
  `]
})
export class RegisterComponent {
  registerData: RegisterRequest = {
    nombre: '',
    username: '',
    password: '',
    telefono: '',
    direccion: ''
  };

  loading = false;
  errorMessage = '';
  successMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    // Por defecto, solo ADMIN puede registrar pacientes/empleados
    // Aquí podrías diferenciar entre paciente/empleado según el formulario
    this.authService.registerPaciente(this.registerData).subscribe({
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
