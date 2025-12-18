import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ExamenService } from '../../services/examen.service';
import { Examen } from '../../models/examen.model';

@Component({
  selector: 'app-examen-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="examen-list">
      <div class="header-section">
        <h2>🧪 Gestión de Exámenes</h2>
        <a routerLink="/examenes/nuevo" class="btn btn-primary">+ Nuevo Examen</a>
      </div>

      <div *ngIf="loading" class="loading">Cargando exámenes...</div>
      <div *ngIf="errorMessage" class="error">{{ errorMessage }}</div>

      <div *ngIf="!loading && examenes.length > 0" class="grid">
        <div *ngFor="let examen of examenes" class="card">
          <h3>{{ examen.nombre }}</h3>
          <p><strong>Código:</strong> {{ examen.codigo }}</p>
          <p><strong>Tipo:</strong> <span class="badge badge-info">{{ examen.tipo }}</span></p>
          <div class="action-buttons">
            <a [routerLink]="['/examenes/editar', examen.id]" class="btn btn-success btn-sm">✏️ Editar</a>
            <button (click)="eliminarExamen(examen.id!)" class="btn btn-danger btn-sm">🗑️ Eliminar</button>
          </div>
        </div>
      </div>

      <div *ngIf="!loading && examenes.length === 0" class="empty-state">
        <p>No se encontraron exámenes.</p>
      </div>
    </div>
  `,
  styles: [`
    .examen-list { background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%); min-height: 100vh; padding: 4rem 2rem; }
    .header-section { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; max-width: 1200px; margin: 0 auto 30px; }
    .header-section h2 { color: white; font-size: 2rem; }
    .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 2rem; max-width: 1200px; margin: 0 auto; }
    .card { background: white; padding: 1.5rem; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
    .card h3 { color: #0369a1; margin-bottom: 1rem; }
    .card p { color: #555; margin: 0.5rem 0; }
    .badge-info { background: #0369a1; color: white; padding: 4px 12px; border-radius: 4px; font-size: 14px; }
    .btn-sm { padding: 6px 12px; font-size: 12px; }
    .action-buttons { display: flex; gap: 8px; margin-top: 15px; }
    .loading, .error { color: white; text-align: center; font-size: 1.2rem; max-width: 1200px; margin: 0 auto; }
    .empty-state { text-align: center; padding: 60px 20px; background: white; border-radius: 8px; max-width: 600px; margin: 0 auto; }
    
    /* Mobile */
    @media (max-width: 767px) {
      .examen-list { padding: 2rem 1rem; }
      .header-section { flex-direction: column; gap: 15px; align-items: stretch; }
      .header-section h2 { font-size: 1.5rem; text-align: center; }
      .grid { grid-template-columns: 1fr; gap: 1.5rem; }
      .card { padding: 1.25rem; }
      .action-buttons { flex-direction: column; }
      .action-buttons .btn-sm { width: 100%; }
    }
    
    /* Tablet */
    @media (min-width: 768px) and (max-width: 1023px) {
      .examen-list { padding: 3rem 1.5rem; }
      .grid { grid-template-columns: repeat(2, 1fr); gap: 1.75rem; }
    }
  `]
})
export class ExamenListComponent implements OnInit {
  examenes: Examen[] = [];
  loading = false;
  errorMessage = '';

  constructor(private examenService: ExamenService) {}

  ngOnInit(): void {
    this.cargarExamenes();
  }

  cargarExamenes(): void {
    this.loading = true;
    this.examenService.getExamenes().subscribe({
      next: (data: any) => {
        this.examenes = data;
        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage = 'Error al cargar exámenes: ' + error.message;
        this.loading = false;
      }
    });
  }

  eliminarExamen(id: number): void {
    if (confirm('¿Está seguro de eliminar este examen?')) {
      this.examenService.eliminarExamen(id).subscribe({
        next: () => {
          this.cargarExamenes();
          alert('Examen eliminado exitosamente');
        },
        error: (error: any) => alert('Error al eliminar: ' + error.message)
      });
    }
  }
}
