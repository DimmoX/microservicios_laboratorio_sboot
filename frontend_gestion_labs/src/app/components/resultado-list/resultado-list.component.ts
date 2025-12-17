import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResultadoService } from '../../services/resultado.service';
import { ResultadoExamen } from '../../models/resultado.model';

@Component({
  selector: 'app-resultado-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="resultado-list">
      <h2>📊 Resultados de Exámenes</h2>
      
      <div *ngIf="loading" class="loading">Cargando resultados...</div>
      <div *ngIf="errorMessage" class="error">{{ errorMessage }}</div>

      <div *ngIf="!loading && resultados.length > 0" class="grid">
        <div *ngFor="let resultado of resultados" class="card">
          <h3>Resultado ID: {{ resultado.id }}</h3>
          <p><strong>Paciente:</strong> {{ resultado.pacienteNombre || 'ID: ' + resultado.pacienteId }}</p>
          <p><strong>Examen:</strong> {{ resultado.examenNombre || 'ID: ' + resultado.examenId }}</p>
          <p *ngIf="resultado.valor"><strong>Valor:</strong> {{ resultado.valor }} {{ resultado.unidad }}</p>
          <p><strong>Estado:</strong>
            <span class="badge" [ngClass]="{
              'badge-success': resultado.estado === 'COMPLETADO',
              'badge-warning': resultado.estado === 'PENDIENTE',
              'badge-danger': resultado.estado === 'ANULADO'
            }">{{ resultado.estado }}</span>
          </p>
          <p *ngIf="resultado.observacion"><strong>Observación:</strong> {{ resultado.observacion }}</p>
          <p *ngIf="resultado.fechaResultado"><strong>Fecha:</strong> {{ resultado.fechaResultado | date:'dd/MM/yyyy HH:mm' }}</p>
        </div>
      </div>

      <div *ngIf="!loading && resultados.length === 0" class="empty-state">
        <p>No se encontraron resultados.</p>
      </div>
    </div>
  `,
  styles: [`
    .resultado-list { background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%); min-height: 100vh; padding: 4rem 2rem; }
    h2 { color: white; font-size: 2rem; margin-bottom: 2rem; text-align: center; max-width: 1200px; margin: 0 auto 2rem; }
    .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 2rem; max-width: 1200px; margin: 0 auto; }
    .card { background: white; padding: 1.5rem; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
    .card h3 { color: #0369a1; margin-bottom: 1rem; font-size: 1.1rem; }
    .card p { margin: 8px 0; line-height: 1.6; color: #555; }
    .badge { padding: 4px 12px; border-radius: 12px; font-size: 12px; font-weight: 600; }
    .badge-success { background: #d1fae5; color: #065f46; }
    .badge-warning { background: #fed7aa; color: #92400e; }
    .badge-danger { background: #fee2e2; color: #991b1b; }
    .empty-state { text-align: center; padding: 60px 20px; background: white; border-radius: 12px; color: #6b7280; max-width: 600px; margin: 0 auto; }
    .loading, .error { color: white; padding: 20px; text-align: center; font-size: 1.2rem; }
    
    /* Mobile */
    @media (max-width: 767px) {
      .resultado-list { padding: 2rem 1rem; }
      h2 { font-size: 1.5rem; margin-bottom: 1.5rem; }
      .grid { grid-template-columns: 1fr; gap: 1.5rem; }
      .card { padding: 1.25rem; }
      .card h3 { font-size: 1rem; }
      .card p { font-size: 14px; }
      .empty-state { padding: 40px 15px; }
    }
    
    /* Tablet */
    @media (min-width: 768px) and (max-width: 1023px) {
      .resultado-list { padding: 3rem 1.5rem; }
      .grid { grid-template-columns: repeat(2, 1fr); gap: 1.75rem; }
    }
    
    /* Desktop */
    @media (min-width: 1024px) {
      .grid { grid-template-columns: repeat(3, 1fr); }
    }
  `]
})
export class ResultadoListComponent implements OnInit {
  resultados: ResultadoExamen[] = [];
  loading = false;
  errorMessage = '';

  constructor(private resultadoService: ResultadoService) {}

  ngOnInit(): void {
    this.loading = true;
    this.resultadoService.getResultados().subscribe({
      next: (data: any) => {
        this.resultados = data;
        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage = 'Error al cargar resultados: ' + error.message;
        this.loading = false;
      }
    });
  }
}
