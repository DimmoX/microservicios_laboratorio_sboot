// Component: Login
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/usuario.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
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

  constructor(
    private authService: AuthService,
    private router: Router
  ) {
    // Si ya está autenticado, redirigir
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/']);
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

        // Redirigir según el rol
        if (sesion.usuario.rol === 'ADMIN') {
          this.router.navigate(['/laboratorios']);
        } else if (sesion.usuario.rol === 'LAB_EMPLOYEE') {
          this.router.navigate(['/resultados']);
        } else if (sesion.usuario.rol === 'PATIENT') {
          this.router.navigate(['/mis-resultados']);
        } else {
          this.router.navigate(['/']);
        }
      },
      error: (error: any) => {
        this.loading = false;
        this.errorMessage = error.message || 'Error al iniciar sesión';
      }
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }
}
