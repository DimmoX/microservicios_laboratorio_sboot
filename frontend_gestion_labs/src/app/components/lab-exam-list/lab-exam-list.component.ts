import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LabExamService } from '../../services/lab-exam.service';
import { LabExam } from '../../models/lab-exam.model';

@Component({
  selector: 'app-lab-exam-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="lab-exam-list">
      <h2>💰 Precios de Exámenes por Laboratorio</h2>
      
      <div *ngIf="loading" class="text-center text-white">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
        <p class="mt-2">Cargando datos...</p>
      </div>
      <div *ngIf="errorMessage" class="error">{{ errorMessage }}</div>

      <div *ngIf="!loading" class="table-container">
        <table>
          <thead>
            <tr>
              <th>Laboratorio</th>
              <th>Examen</th>
              <th>Precio</th>
              <th>Vigencia Desde</th>
              <th>Vigencia Hasta</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let item of labExams">
              <td>{{ item.nombreLab }}</td>
              <td>{{ item.nombreExamen }}</td>
              <td><strong>\${{ item.precio | number:'1.0-0' }}</strong></td>
              <td>{{ item.vigenteDesde | date:'dd/MM/yyyy' }}</td>
              <td>{{ item.vigenteHasta ? (item.vigenteHasta | date:'dd/MM/yyyy') : 'Vigente' }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`
    .lab-exam-list { background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%); min-height: 100vh; padding: 4rem 2rem; }
    h2 { color: white; font-size: 2rem; margin-bottom: 2rem; text-align: center; max-width: 1200px; margin: 0 auto 2rem; }
    .table-container { overflow-x: auto; box-shadow: 0 4px 6px rgba(0,0,0,0.1); border-radius: 12px; background: white; max-width: 1200px; margin: 0 auto; }
    table { width: 100%; min-width: 700px; border-collapse: collapse; }
    thead { background: #0369a1; }
    th { color: white; padding: 1rem; text-align: left; font-weight: 600; white-space: nowrap; }
    td { padding: 1rem; border-bottom: 1px solid #e5e7eb; color: #555; }
    tbody tr:hover { background: #f9fafb; }
    tbody tr:last-child td { border-bottom: none; }
    .loading, .error { color: white; padding: 20px; text-align: center; font-size: 1.2rem; }
    
    /* Mobile */
    @media (max-width: 767px) {
      .lab-exam-list { padding: 2rem 1rem; }
      h2 { font-size: 1.5rem; margin-bottom: 1.5rem; }
      .table-container { -webkit-overflow-scrolling: touch; }
      table { min-width: 600px; font-size: 13px; }
      th, td { padding: 8px 6px; }
    }
    
    /* Tablet */
    @media (min-width: 768px) and (max-width: 1023px) {
      .lab-exam-list { padding: 3rem 1.5rem; }
      th, td { padding: 10px 8px; }
    }
  `]
})
export class LabExamListComponent implements OnInit {
  labExams: LabExam[] = [];
  loading = false;
  errorMessage = '';

  constructor(private labExamService: LabExamService) {}

  ngOnInit(): void {
    this.loading = true;
    this.labExamService.getLabExams().subscribe({
      next: (data: any) => {
        this.labExams = data;
        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage = 'Error al cargar datos: ' + error.message;
        this.loading = false;
      }
    });
  }
}
