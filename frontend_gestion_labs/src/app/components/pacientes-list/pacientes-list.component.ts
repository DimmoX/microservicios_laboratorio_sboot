import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { UsuarioService } from '../../services/usuario.service';
import { Usuario } from '../../models/usuario.model';

@Component({
  selector: 'app-pacientes-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './pacientes-list.component.html',
  styleUrl: './pacientes-list.component.css'
})
export class PacientesListComponent implements OnInit {
  pacientes: Usuario[] = [];
  loading = false;
  errorMessage = '';

  constructor(private usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.cargarPacientes();
  }

  cargarPacientes(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.usuarioService.getUsuarios().subscribe({
      next: (data) => {
        // Filtrar solo los usuarios con rol PATIENT
        this.pacientes = data.filter(usuario => usuario.rol === 'PATIENT');
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Error al cargar pacientes: ' + error.message;
        this.loading = false;
      }
    });
  }

  toggleEstado(paciente: Usuario): void {
    const accion = paciente.activo ? 'desactivar' : 'activar';
    if (confirm(`¿Está seguro de ${accion} a ${paciente.nombre}?`)) {
      this.usuarioService.actualizarUsuario(paciente.id!, { activo: !paciente.activo }).subscribe({
        next: () => {
          this.cargarPacientes();
        },
        error: (error) => {
          alert('Error al actualizar paciente: ' + error.message);
        }
      });
    }
  }

  eliminarPaciente(id: number): void {
    const paciente = this.pacientes.find(p => p.id === id);
    if (!paciente) return;

    if (confirm(`¿Está seguro de eliminar a ${paciente.nombre}? Esta acción no se puede deshacer.`)) {
      this.usuarioService.eliminarUsuario(id).subscribe({
        next: () => {
          this.cargarPacientes();
          alert('Paciente eliminado exitosamente');
        },
        error: (error) => {
          alert('Error al eliminar paciente: ' + error.message);
        }
      });
    }
  }
}
