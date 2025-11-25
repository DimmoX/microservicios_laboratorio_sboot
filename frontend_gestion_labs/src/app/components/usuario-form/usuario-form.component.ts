import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MockDataService } from '../../services/mock-data.service';
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
    private mockDataService: MockDataService,
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
    const usuarios = this.mockDataService.getUsers();
    const usuario = usuarios.find(u => u.id === id);
    if (usuario) {
      this.usuario = { ...usuario };
    } else {
      this.errorMessage = 'Usuario no encontrado';
    }
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

    try {
      if (this.isEditMode) {
        // Actualizar usuario existente
        const db = this.mockDataService.getDatabase();
        const index = db.users.findIndex(u => u.id === this.usuario.id);
        if (index >= 0) {
          // Si no se ingresó nueva contraseña, mantener la anterior
          if (!this.usuario.password) {
            this.usuario.password = db.users[index].password;
          }
          db.users[index] = this.usuario;
          this.mockDataService.saveDatabase(db);
          this.successMessage = 'Usuario actualizado exitosamente';
        }
      } else {
        // Crear nuevo usuario
        this.mockDataService.addUser(this.usuario);
        this.successMessage = 'Usuario creado exitosamente';
      }

      this.loading = false;

      // Redirigir después de 1 segundo
      setTimeout(() => {
        this.router.navigate(['/usuarios']);
      }, 1000);
    } catch (error: any) {
      this.errorMessage = error.message || 'Error al guardar usuario';
      this.loading = false;
    }
  }

  cancelar(): void {
    this.router.navigate(['/usuarios']);
  }
}
