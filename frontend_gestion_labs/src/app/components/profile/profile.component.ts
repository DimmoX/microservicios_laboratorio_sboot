// Component: Profile
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Usuario } from '../../models/usuario.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="profile-container">
      <div class="profile-card">
        <div class="profile-header">
          <div class="avatar">{{ usuario?.rol === 'ADMIN' ? '👨‍💼' : '👤' }}</div>
          <h1>Mi Perfil</h1>
          <span class="badge" [class.admin]="usuario?.rol === 'ADMIN'">
            {{ usuario?.rol === 'ADMIN' ? '⭐ Administrador' : '👤 Usuario' }}
          </span>
        </div>
        <form (ngSubmit)="onUpdateProfile()" #profileForm="ngForm" class="profile-form" *ngIf="usuario">
          <div class="form-group">
            <label>👤 Nombre Completo</label>
            <input type="text" [(ngModel)]="usuario.nombre" name="nombre" required class="form-control" />
          </div>
          <div class="form-group">
            <label>👤 Usuario</label>
            <input type="text" [(ngModel)]="usuario.username" name="username" required disabled class="form-control" />
          </div>
          <div class="form-group">
            <label>📱 Teléfono</label>
            <input type="tel" [(ngModel)]="usuario.telefono" name="telefono" class="form-control" />
          </div>
          <div class="form-group">
            <label>🏠 Dirección</label>
            <input type="text" [(ngModel)]="usuario.direccion" name="direccion" class="form-control" />
          </div>
          <div class="alert alert-success" *ngIf="successMessage">✅ {{ successMessage }}</div>
          <button type="submit" class="btn btn-primary" [disabled]="loading">
            {{ loading ? '⏳ Guardando...' : '💾 Guardar Cambios' }}
          </button>
        </form>
        <hr>
        <h3>🔒 Cambiar Contraseña</h3>
        <form (ngSubmit)="onChangePassword()" #passwordForm="ngForm" class="password-form">
          <div class="form-group">
            <label>Contraseña Actual</label>
            <input type="password" [(ngModel)]="oldPassword" name="oldPassword" required class="form-control" />
          </div>
          <div class="form-group">
            <label>Nueva Contraseña</label>
            <input type="password" [(ngModel)]="newPassword" name="newPassword" required minlength="6" class="form-control" />
          </div>
          <div class="alert alert-error" *ngIf="errorMessage">⚠️ {{ errorMessage }}</div>
          <button type="submit" class="btn btn-secondary">🔐 Cambiar Contraseña</button>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .profile-container { min-height: 100vh; padding: 2rem; background-color: #f5f5f5; }
    .profile-card { max-width: 600px; margin: 0 auto; background: white; border-radius: 16px;
      box-shadow: 0 4px 6px rgba(0,0,0,0.1); padding: 2rem; }
    .profile-header { text-align: center; margin-bottom: 2rem; }
    .avatar { font-size: 4rem; margin-bottom: 1rem; }
    h1 { color: #333; margin-bottom: 0.5rem; }
    .badge { display: inline-block; padding: 0.5rem 1rem; border-radius: 20px; font-size: 0.9rem;
      background-color: #e0e7ff; color: #4338ca; }
    .badge.admin { background-color: #fef3c7; color: #92400e; }
    .profile-form, .password-form { display: flex; flex-direction: column; gap: 1rem; }
    .form-group { display: flex; flex-direction: column; gap: 0.5rem; }
    label { font-weight: 600; color: #333; }
    .form-control { padding: 0.75rem; border: 2px solid #e0e0e0; border-radius: 8px; font-size: 1rem; }
    .form-control:disabled { background-color: #f5f5f5; }
    .btn { padding: 0.875rem; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; }
    .btn-primary { background: #0369a1; color: white; }
    .btn-secondary { background: #6366f1; color: white; }
    hr { margin: 2rem 0; border: none; border-top: 1px solid #e0e0e0; }
    h3 { color: #333; margin-bottom: 1rem; }
    .alert { padding: 0.75rem; border-radius: 8px; margin-bottom: 1rem; }
    .alert-success { background-color: #d1fae5; color: #065f46; }
    .alert-error { background-color: #fee2e2; color: #991b1b; }
    @media (max-width: 767px) {
      .profile-container { padding: 1rem; }
      .profile-card { padding: 1.5rem; }
    }
  `]
})
export class ProfileComponent implements OnInit {
  usuario: Usuario | null = null;
  oldPassword = '';
  newPassword = '';
  loading = false;
  successMessage = '';
  errorMessage = '';

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }
    this.authService.getProfile().subscribe({
      next: (user) => {
        this.usuario = user;
      },
      error: () => {
        this.router.navigate(['/login']);
      }
    });
  }

  onUpdateProfile(): void {
    if (!this.usuario) return;
    this.loading = true;
    this.successMessage = '';
    this.errorMessage = '';
    
    // Preparar datos a actualizar
    const updates = {
      nombre: this.usuario.nombre,
      telefono: this.usuario.telefono,
      direccion: this.usuario.direccion
    };
    
    // Llamar al servicio de actualización
    this.authService.updateProfile(this.usuario.id, updates).subscribe({
      next: (response) => {
        this.loading = false;
        this.successMessage = 'Perfil actualizado correctamente';
        // Actualizar los datos locales con la respuesta del servidor
        if (response.data) {
          this.usuario = { ...this.usuario!, ...response.data };
        }
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = 'Error al actualizar el perfil: ' + (error.error?.description || error.message);
      }
    });
  }

  onChangePassword(): void {
    if (!this.usuario || !this.oldPassword || !this.newPassword) {
      this.errorMessage = 'Complete todos los campos';
      return;
    }
    this.errorMessage = '';
    this.authService.changePassword(this.oldPassword, this.newPassword).subscribe({
      next: () => {
        this.successMessage = 'Contraseña cambiada correctamente';
        this.oldPassword = '';
        this.newPassword = '';
      },
      error: (error: any) => {
        this.errorMessage = error.message;
      }
    });
  }
}
