// Controller: Lógica para gestionar la lista de laboratorios
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { LaboratorioService } from '../../services/laboratorio.service';
import { Laboratorio } from '../../models/laboratorio.model';

@Component({
  selector: 'app-laboratorio-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './laboratorio-list.component.html',
  styleUrls: ['./laboratorio-list.component.css']
})
export class LaboratorioListComponent implements OnInit {
  // Propiedades del controlador
  laboratorios: Laboratorio[] = [];
  filtroTipo: string = '';
  filtroCiudad: string = '';
  loading: boolean = false;
  errorMessage: string = '';

  constructor(private laboratorioService: LaboratorioService) { }

  ngOnInit(): void {
    this.cargarLaboratorios();
  }

  // Método para cargar todos los laboratorios
  cargarLaboratorios(): void {
    this.loading = true;
    this.errorMessage = '';
    this.laboratorioService.getLaboratorios().subscribe({
      next: (data: Laboratorio[]) => {
        this.laboratorios = data;
        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage = 'Error al cargar laboratorios: ' + error.message;
        this.loading = false;
      }
    });
  }

  // Método para filtrar por ciudad
  filtrarPorCiudad(): void {
    if (!this.filtroCiudad.trim()) {
      this.cargarLaboratorios();
      return;
    }

    this.loading = true;
    this.laboratorioService.buscarPorCiudad(this.filtroCiudad).subscribe({
      next: (data: Laboratorio[]) => {
        this.laboratorios = data;
        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage = 'Error al filtrar: ' + error.message;
        this.loading = false;
      }
    });
  }

  // Método para filtrar por tipo
  filtrarPorTipo(): void {
    if (!this.filtroTipo) {
      this.cargarLaboratorios();
      return;
    }

    this.loading = true;
    this.laboratorioService.buscarPorTipo(this.filtroTipo).subscribe({
      next: (data: Laboratorio[]) => {
        this.laboratorios = data;
        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage = 'Error al filtrar: ' + error.message;
        this.loading = false;
      }
    });
  }

  // Método para eliminar laboratorio - El interceptor agregará automáticamente el token
  eliminarLaboratorio(id: number): void {
    if (confirm('¿Está seguro de eliminar este laboratorio?')) {
      this.laboratorioService.eliminarLaboratorio(id).subscribe({
        next: () => {
          this.cargarLaboratorios();
          alert('Laboratorio eliminado exitosamente');
        },
        error: (error: any) => {
          alert('Error al eliminar: ' + error.message);
        }
      });
    }
  }

  // Método para limpiar filtros
  limpiarFiltros(): void {
    this.filtroTipo = '';
    this.filtroCiudad = '';
    this.cargarLaboratorios();
  }
}
