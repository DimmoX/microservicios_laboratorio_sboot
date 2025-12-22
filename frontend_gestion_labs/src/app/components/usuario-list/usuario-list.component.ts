import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UsuarioService } from '../../services/usuario.service';
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

  constructor(private usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.usuarioService.getUsuarios().subscribe({
      next: (data) => {
        this.usuarios = data;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Error al cargar usuarios: ' + error.message;
        this.loading = false;
      }
    });
  }

  getRolLabel(rol: string): string {
    const labels: any = {
      'ADMIN': 'Administrador',
      'EMPLOYEE': 'Empleado',
      'PATIENT': 'Paciente'
    };
    return labels[rol] || rol;
  }

  toggleEstado(usuario: Usuario): void {
    const accion = usuario.activo ? 'desactivar' : 'activar';
    if (confirm(`¿Está seguro de ${accion} a ${usuario.nombre}?`)) {
      this.usuarioService.actualizarUsuario(usuario.id!, { activo: !usuario.activo }).subscribe({
        next: () => {
          this.cargarUsuarios();
        },
        error: (error) => {
          alert('Error al actualizar usuario: ' + error.message);
        }
      });
    }
  }

  eliminarUsuario(id: number): void {
    const usuario = this.usuarios.find(u => u.id === id);
    if (!usuario) return;

    if (confirm(`¿Está seguro de eliminar a ${usuario.nombre}? Esta acción no se puede deshacer.`)) {
      this.usuarioService.eliminarUsuario(id).subscribe({
        next: () => {
          this.cargarUsuarios();
          alert('Usuario eliminado exitosamente');
        },
        error: (error) => {
          alert('Error al eliminar usuario: ' + error.message);
        }
      });
    }
  }
}
