// Component: Login
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/usuario.model';
import { ChangePasswordModalComponent } from '../change-password-modal/change-password-modal.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, ChangePasswordModalComponent],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginData: LoginRequest = {
    username: '',
    password: ''
  };

  loading: boolean = false;
  errorMessage: string = '';
  showPassword: boolean = false;
  showChangePasswordModal: boolean = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    // Si ya está autenticado, redirigir al dashboard
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  onSubmit(): void {
    if (!this.loginData.username || !this.loginData.password) {
      this.errorMessage = 'Por favor complete todos los campos';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.loginData).subscribe({
      next: (sesion) => {
        this.loading = false;
        
        // Verificar si requiere cambio de contraseña
        if (this.authService.requiresPasswordChange()) {
          this.showChangePasswordModal = true;
        } else {
          this.router.navigate(['/dashboard']);
        }
      },
      error: (error: any) => {
        this.loading = false;
        this.errorMessage = error.message || 'Error al iniciar sesión';
      }
    });
  }

  onPasswordChanged(): void {
    // Cerrar modal y redirigir al dashboard
    this.showChangePasswordModal = false;
    this.router.navigate(['/dashboard']);
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }
}

