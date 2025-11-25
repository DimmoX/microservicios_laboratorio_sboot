import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MockDataService } from '../../services/mock-data.service';
import { Usuario } from '../../models/usuario.model';

@Component({
  selector: 'app-usuario-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './usuario-list.component.html',
  styleUrl: './usuario-list.component.css'
})
export class UsuarioListComponent implements OnInit {
  usuarios: Usuario[] = [];
  loading = false;
  errorMessage = '';

  constructor(private mockDataService: MockDataService) {}

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.loading = true;
    try {
      this.usuarios = this.mockDataService.getUsers();
      this.loading = false;
    } catch (error: any) {
      this.errorMessage = 'Error al cargar usuarios: ' + error.message;
      this.loading = false;
    }
  }

  getRolLabel(rol: string): string {
    const labels: any = {
      'ADMIN': 'Administrador',
      'LAB_EMPLOYEE': 'Empleado de Laboratorio',
      'PATIENT': 'Paciente'
    };
    return labels[rol] || rol;
  }

  toggleEstado(usuario: Usuario): void {
    const accion = usuario.activo ? 'desactivar' : 'activar';
    if (confirm(`¿Está seguro de ${accion} a ${usuario.nombre}?`)) {
      const db = this.mockDataService.getDatabase();
      const index = db.users.findIndex(u => u.id === usuario.id);
      if (index >= 0) {
        db.users[index].activo = !db.users[index].activo;
        this.mockDataService.saveDatabase(db);
        this.cargarUsuarios();
      }
    }
  }

  eliminarUsuario(id: number): void {
    const usuario = this.usuarios.find(u => u.id === id);
    if (!usuario) return;

    if (confirm(`¿Está seguro de eliminar a ${usuario.nombre}? Esta acción no se puede deshacer.`)) {
      const db = this.mockDataService.getDatabase();
      const index = db.users.findIndex(u => u.id === id);
      if (index >= 0) {
        db.users.splice(index, 1);
        this.mockDataService.saveDatabase(db);
        this.cargarUsuarios();
        alert('Usuario eliminado exitosamente');
      }
    }
  }
}
