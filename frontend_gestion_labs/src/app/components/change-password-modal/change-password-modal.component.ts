// Component: Modal para cambiar contraseña temporal
import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-change-password-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-backdrop" (click)="$event.stopPropagation()">
      <div class="modal-content" (click)="$event.stopPropagation()">
        <div class="modal-header">
          <h2>🔒 Cambiar Contraseña</h2>
          <p>Por seguridad, debes cambiar tu contraseña temporal antes de continuar.</p>
        </div>
        
        <form (ngSubmit)="onSubmit()" #changeForm="ngForm">
          <div class="form-group">
            <label>Contraseña Actual (temporal)</label>
            <input 
              type="password" 
              name="oldPassword" 
              [(ngModel)]="oldPassword" 
              required 
              class="form-control"
              placeholder="Tu contraseña temporal"
            />
          </div>

          <div class="form-group">
            <label>Nueva Contraseña</label>
            <input 
              type="password" 
              name="newPassword" 
              [(ngModel)]="newPassword" 
              required 
              minlength="6"
              class="form-control"
              placeholder="Mínimo 6 caracteres"
            />
          </div>

          <div class="form-group">
            <label>Confirmar Nueva Contraseña</label>
            <input 
              type="password" 
              name="confirmPassword" 
              [(ngModel)]="confirmPassword" 
              required 
              class="form-control"
              placeholder="Repite tu nueva contraseña"
            />
          </div>

          <div class="alert alert-error" *ngIf="errorMessage">
            ⚠️ {{ errorMessage }}
          </div>

          <div class="modal-actions">
            <button type="submit" class="btn btn-primary" [disabled]="loading || !changeForm.valid">
              {{ loading ? '⏳ Actualizando...' : 'Cambiar Contraseña' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .modal-backdrop {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.7);
      display: flex;
      align-items: flex-start;
      justify-content: center;
      z-index: 9999;
      padding-top: 100px;
      overflow-y: auto;
      animation: fadeIn 0.3s;
    }

    @keyframes fadeIn {
      from { opacity: 0; }
      to { opacity: 1; }
    }

    .modal-content {
      background: white;
      border-radius: 16px;
      box-shadow: 0 20px 60px rgba(0,0,0,0.3);
      width: 90%;
      max-width: 500px;
      padding: 2rem;
      animation: slideIn 0.3s;
    }

    @keyframes slideIn {
      from { transform: translateY(-50px); opacity: 0; }
      to { transform: translateY(0); opacity: 1; }
    }

    .modal-header {
      text-align: center;
      margin-bottom: 1.5rem;
    }

    .modal-header h2 {
      color: #333;
      font-size: 1.5rem;
      margin-bottom: 0.5rem;
    }

    .modal-header p {
      color: #666;
      font-size: 0.9rem;
    }

    .form-group {
      margin-bottom: 1rem;
    }

    label {
      display: block;
      font-weight: 600;
      margin-bottom: 0.5rem;
      color: #333;
    }

    .form-control {
      width: 100%;
      padding: 0.75rem;
      border: 2px solid #e0e0e0;
      border-radius: 8px;
      font-size: 1rem;
      box-sizing: border-box;
    }

    .form-control:focus {
      outline: none;
      border-color: #0369a1;
    }

    .alert {
      padding: 0.75rem;
      border-radius: 8px;
      margin-bottom: 1rem;
      font-size: 0.9rem;
    }

    .alert-error {
      background-color: #fee2e2;
      color: #991b1b;
    }

    .modal-actions {
      display: flex;
      justify-content: center;
      margin-top: 1.5rem;
    }

    .btn {
      padding: 0.875rem 2rem;
      border: none;
      border-radius: 8px;
      font-size: 1rem;
      font-weight: 600;
      cursor: pointer;
      background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%);
      color: white;
      transition: all 0.3s;
    }

    .btn:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(3, 105, 161, 0.4);
    }

    .btn:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  `]
})
export class ChangePasswordModalComponent {
  oldPassword = '';
  newPassword = '';
  confirmPassword = '';
  loading = false;
  errorMessage = '';

  @Output() passwordChanged = new EventEmitter<void>();

  constructor(private authService: AuthService) {}

  onSubmit(): void {
    // Validaciones
    if (!this.oldPassword || !this.newPassword || !this.confirmPassword) {
      this.errorMessage = 'Todos los campos son obligatorios';
      return;
    }

    if (this.newPassword.length < 6) {
      this.errorMessage = 'La nueva contraseña debe tener al menos 6 caracteres';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden';
      return;
    }

    if (this.oldPassword === this.newPassword) {
      this.errorMessage = 'La nueva contraseña debe ser diferente a la actual';
      return;
    }

    // Llamar al servicio
    this.loading = true;
    this.errorMessage = '';

    this.authService.changePassword(this.oldPassword, this.newPassword).subscribe({
      next: (response) => {
        this.loading = false;
        if (response.code === '000') {
          // Emitir evento de éxito
          this.passwordChanged.emit();
        } else {
          this.errorMessage = response.description || 'Error al cambiar contraseña';
        }
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.message || 'Error al cambiar contraseña';
      }
    });
  }
}
