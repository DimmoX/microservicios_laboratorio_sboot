// Component: Forgot Password
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="forgot-container">
      <div class="forgot-card">
        <div class="forgot-header">
          <div class="logo">🔐</div>
          <h1>Recuperar Contraseña</h1>
          <p>Ingresa tu email para recibir una contraseña temporal</p>
        </div>
        <form (ngSubmit)="onSubmit()" #forgotForm="ngForm">
          <div class="form-group">
            <label>📧 Email</label>
            <input type="email" name="email" [(ngModel)]="email" required email 
                   placeholder="correo@ejemplo.com" class="form-control" />
          </div>
          <div class="alert alert-error" *ngIf="errorMessage">⚠️ {{ errorMessage }}</div>
          <div class="alert alert-success" *ngIf="successMessage">
            ✅ {{ successMessage }}
          </div>
          <div class="password-box" *ngIf="tempPassword">
            <div class="password-label">Tu contraseña temporal es:</div>
            <div class="password-value">{{ tempPassword }}</div>
            <div class="password-hint">⚠️ Copia esta contraseña para iniciar sesión</div>
          </div>
          <button type="submit" class="btn btn-primary btn-block" [disabled]="loading" *ngIf="!tempPassword">
            {{ loading ? '⏳ Procesando...' : 'Recuperar Contraseña' }}
          </button>
          <a routerLink="/login" class="btn btn-primary btn-block btn-back-login" *ngIf="tempPassword">
            Volver al login
          </a>
          <div class="form-footer" *ngIf="!tempPassword">
            <a routerLink="/login" class="link">Volver al login</a>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .forgot-container { min-height: 100vh; display: flex; align-items: center; justify-content: center;
      background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%); padding: 1rem; }
    .forgot-card { background: white; border-radius: 16px; box-shadow: 0 20px 60px rgba(0,0,0,0.3);
      width: 100%; max-width: 450px; padding: 2rem; }
    .forgot-header { text-align: center; margin-bottom: 1.5rem; }
    .logo { font-size: 3rem; margin-bottom: 0.5rem; }
    h1 { color: #333; font-size: 1.5rem; margin-bottom: 0.5rem; }
    p { color: #666; font-size: 0.9rem; }
    .form-group { margin-bottom: 1rem; }
    label { display: block; font-weight: 600; margin-bottom: 0.5rem; color: #333; }
    .form-control { width: 100%; padding: 0.75rem; border: 2px solid #e0e0e0; border-radius: 8px;
      font-size: 1rem; }
    .form-control:focus { outline: none; border-color: #0369a1; }
    .alert { padding: 0.75rem; border-radius: 8px; margin-bottom: 1rem; font-size: 0.9rem; }
    .alert-error { background-color: #fee2e2; color: #991b1b; }
    .alert-success { background-color: #d1fae5; color: #065f46; }
    .password-box {
      background: linear-gradient(135deg, #3D7699 0%, #144766 100%);
      padding: 1.5rem;
      border-radius: 10px;
      margin: 1.5rem 0;
      text-align: center;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
    }
    .password-label {
      color: white;
      font-size: 0.9rem;
      margin-bottom: 0.5rem;
      opacity: 0.9;
    }
    .password-value {
      background: white;
      color: #3D7699;
      font-size: 1.8rem;
      font-weight: bold;
      padding: 1rem;
      border-radius: 8px;
      letter-spacing: 0.2rem;
      font-family: 'Courier New', monospace;
      margin: 0.5rem 0;
      word-break: break-all;
    }
    .password-hint {
      color: white;
      font-size: 0.85rem;
      margin-top: 0.5rem;
      font-weight: 500;
    }
    .btn { width: 100%; padding: 0.875rem; border: none; border-radius: 8px; font-size: 1rem;
      font-weight: 600; cursor: pointer; background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%);
      color: white; transition: all 0.3s; }
    .btn:hover:not(:disabled) { transform: translateY(-2px); }
    .btn:disabled { opacity: 0.6; cursor: not-allowed; }
    .btn-back-login { display: block; text-align: center; text-decoration: none; }
    .form-footer { text-align: center; margin-top: 1rem; }
    .link { color: #0369a1; text-decoration: none; }
    .link:hover { text-decoration: underline; }
  `]
})
export class ForgotPasswordComponent {
  email = '';
  loading = false;
  errorMessage = '';
  successMessage = '';
  tempPassword = '';

  constructor(private authService: AuthService) {}

  onSubmit(): void {
    if (!this.email) {
      this.errorMessage = 'Por favor ingrese su email';
      return;
    }
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.authService.forgotPassword(this.email).subscribe({
      next: (tempPwd) => {
        console.log('🔑 Password recibida del servicio:', tempPwd);
        console.log('🔑 Tipo de dato:', typeof tempPwd);
        console.log('🔑 Longitud:', tempPwd?.length);
        this.loading = false;
        this.tempPassword = tempPwd;
        console.log('🔑 Password asignada a this.tempPassword:', this.tempPassword);
        this.successMessage = 'Se ha enviado una contraseña temporal a tu correo electrónico. También puedes verla a continuación:';
      },
      error: (error: any) => {
        this.loading = false;
        this.errorMessage = error.message || 'Error al recuperar contraseña';
      }
    });
  }
}
