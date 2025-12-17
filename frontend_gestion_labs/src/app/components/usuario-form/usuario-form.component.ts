import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { UsuarioService } from '../../services/usuario.service';
import { Usuario } from '../../models/usuario.model';

@Component({
  selector: 'app-usuario-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuario-form.component.html',
  styleUrl: './usuario-form.component.css'
})
export class UsuarioFormComponent implements OnInit {
  usuario: Usuario = {
    id: 0,
    nombre: '',
    username: '',
    password: '',
    rol: 'PATIENT',
    telefono: '',
    direccion: '',
    activo: true,
    rut: ''
  };

  isEditMode = false;
  loading = false;
  errorMessage = '';
  successMessage = '';

  roles = [
    { value: 'ADMIN', label: 'Administrador' },
    { value: 'LAB_EMPLOYEE', label: 'Empleado de Laboratorio' },
    { value: 'PATIENT', label: 'Paciente' }
  ];

  constructor(
    private usuarioService: UsuarioService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    if (id) {
      this.isEditMode = true;
      this.cargarUsuario(parseInt(id));
    }
  }

  cargarUsuario(id: number): void {
    this.usuarioService.getUsuario(id).subscribe({
      next: (usuario) => {
        this.usuario = { ...usuario };
      },
      error: (error) => {
        this.errorMessage = 'Usuario no encontrado: ' + error.message;
      }
    });
  }

  onSubmit(): void {
    if (!this.usuario.nombre || !this.usuario.username) {
      this.errorMessage = 'Por favor complete los campos obligatorios';
      return;
    }

    if (!this.isEditMode && !this.usuario.password) {
      this.errorMessage = 'La contraseña es obligatoria para nuevos usuarios';
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    if (this.isEditMode) {
      // Actualizar usuario existente
      const updates: Partial<Usuario> = {
        nombre: this.usuario.nombre,
        telefono: this.usuario.telefono,
        direccion: this.usuario.direccion,
        activo: this.usuario.activo
      };

      this.usuarioService.actualizarUsuario(this.usuario.id!, updates).subscribe({
        next: () => {
          this.successMessage = 'Usuario actualizado exitosamente';
          this.loading = false;
          setTimeout(() => {
            this.router.navigate(['/usuarios']);
          }, 1000);
        },
        error: (error) => {
          this.errorMessage = 'Error al actualizar usuario: ' + error.message;
          this.loading = false;
        }
      });
    } else {
      // Crear nuevo usuario
      this.usuarioService.crearUsuario(this.usuario).subscribe({
        next: () => {
          this.successMessage = 'Usuario creado exitosamente';
          this.loading = false;
          setTimeout(() => {
            this.router.navigate(['/usuarios']);
          }, 1000);
        },
        error: (error) => {
          if (error.error?.code === '403') {
            this.errorMessage = 'No tiene permisos para crear usuarios. Solo los administradores pueden crear usuarios.';
          } else {
            this.errorMessage = 'Error al crear usuario: ' + (error.error?.description || error.message);
          }
          this.loading = false;
        }
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/usuarios']);
  }
}
