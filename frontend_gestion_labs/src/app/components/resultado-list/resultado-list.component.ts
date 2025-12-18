import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ResultadoService } from '../../services/resultado.service';
import { ResultadoExamen } from '../../models/resultado.model';

@Component({
  selector: 'app-resultado-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container-fluid py-4" style="background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%); min-height: 100vh;">
      <div class="container">
        <h2 class="text-white text-center mb-4">📊 Resultados de Exámenes</h2>
        
        <!-- Loading -->
        <div *ngIf="loading" class="text-center text-white">
          <div class="spinner-border" role="status">
            <span class="visually-hidden">Cargando...</span>
          </div>
          <p class="mt-2">Cargando resultados...</p>
        </div>
        
        <!-- Error Message -->
        <div *ngIf="errorMessage" class="alert alert-danger" role="alert">
          {{ errorMessage }}
        </div>

        <!-- Results Grid -->
        <div *ngIf="!loading && resultados.length > 0" class="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
          <div *ngFor="let resultado of resultados" class="col">
            <div class="card h-100 shadow-sm">
              <div class="card-body">
                <h5 class="card-title text-primary">Resultado ID: {{ resultado.id }}</h5>
                <p class="card-text"><strong>Paciente:</strong> {{ resultado.pacienteNombre || 'ID: ' + resultado.pacienteId }}</p>
                <p class="card-text"><strong>Examen:</strong> {{ resultado.examenNombre || 'ID: ' + resultado.examenId }}</p>
                <p class="card-text" *ngIf="resultado.valor"><strong>Valor:</strong> {{ resultado.valor }} {{ resultado.unidad }}</p>
                <p class="card-text"><strong>Estado:</strong>
                  <span class="badge" [class.bg-success]="resultado.estado === 'COMPLETADO'"
                        [class.bg-warning]="resultado.estado === 'PENDIENTE'"
                        [class.bg-danger]="resultado.estado === 'ANULADO'">
                    {{ resultado.estado }}
                  </span>
                </p>
                <p class="card-text" *ngIf="resultado.observacion"><strong>Observación:</strong> {{ resultado.observacion }}</p>
                <p class="card-text small text-muted" *ngIf="resultado.fechaResultado">
                  <strong>Fecha:</strong> {{ resultado.fechaResultado | date:'dd/MM/yyyy HH:mm' }}
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div *ngIf="!loading && resultados.length === 0" class="text-center">
          <div class="card mx-auto" style="max-width: 500px;">
            <div class="card-body py-5">
              <h5 class="card-title text-muted">No se encontraron resultados</h5>
              <p class="card-text">No hay resultados disponibles en este momento.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .card {
      border-radius: 12px;
      border: none;
      transition: transform 0.3s, box-shadow 0.3s;
    }
    
    .card:hover {
      transform: translateY(-5px);
      box-shadow: 0 10px 20px rgba(0,0,0,0.2) !important;
    }
    
    .card-title {
      font-size: 1.1rem;
      margin-bottom: 1rem;
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
