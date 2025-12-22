import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LabExamService } from '../../services/lab-exam.service';
import { ExamenService } from '../../services/examen.service';
import { LaboratorioService } from '../../services/laboratorio.service';
import { LabExam } from '../../models/lab-exam.model';
import { Examen } from '../../models/examen.model';
import { Laboratorio } from '../../models/laboratorio.model';

@Component({
  selector: 'app-admin-lab-exam',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="admin-lab-exam">
      <div class="header-section">
        <h2>💰 Gestión de Precios y Asignaciones</h2>
        <button class="btn btn-primary" (click)="abrirModal()">
          + Asignar Examen a Laboratorio
        </button>
      </div>

      <div *ngIf="loading" class="text-center text-white">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
        <p class="mt-2">Cargando datos...</p>
      </div>

      <div *ngIf="errorMessage" class="alert alert-danger">{{ errorMessage }}</div>
      <div *ngIf="successMessage" class="alert alert-success">{{ successMessage }}</div>

      <div *ngIf="!loading && labExams.length > 0" class="table-container">
        <table class="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Laboratorio</th>
              <th>Examen</th>
              <th>Precio</th>
              <th>Vigente Desde</th>
              <th>Vigente Hasta</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let item of labExams">
              <td>{{ getIdLaboratorio(item) }}-{{ getIdExamen(item) }}</td>
              <td>{{ item.nombreLab }}</td>
              <td>{{ item.nombreExamen }}</td>
              <td><strong>\${{ item.precio | number:'1.0-0' }}</strong></td>
              <td>{{ item.vigenteDesde | date:'dd/MM/yyyy' }}</td>
              <td>{{ item.vigenteHasta ? (item.vigenteHasta | date:'dd/MM/yyyy') : 'Vigente' }}</td>
              <td>
                <button class="btn btn-sm btn-info me-2" (click)="abrirModalEditar(item)">
                  ✏️ Editar
                </button>
                <button class="btn btn-sm btn-danger" (click)="eliminar(item)">
                  🗑️ Eliminar
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div *ngIf="!loading && labExams.length === 0" class="empty-state">
        <div class="empty-card">
          <span style="font-size: 4rem;">💰</span>
          <h3>No hay asignaciones de precios</h3>
          <p>Comienza asignando exámenes a laboratorios con sus respectivos precios.</p>
          <button class="btn btn-primary" (click)="abrirModal()">
            + Crear Primera Asignación
          </button>
        </div>
      </div>

      <!-- Modal para crear/editar -->
      <div *ngIf="mostrarModal" class="modal-overlay" (click)="cerrarModal()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>{{ modoEdicion ? '✏️ Editar Asignación' : '+ Nueva Asignación' }}</h3>
            <button class="btn-close" (click)="cerrarModal()">&times;</button>
          </div>
          
          <div class="modal-body">
            <div *ngIf="loadingModal" class="text-center">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Cargando...</span>
              </div>
              <p class="mt-2">Cargando opciones...</p>
            </div>

            <form *ngIf="!loadingModal" (ngSubmit)="guardar()">
              <div class="form-group">
                <label for="laboratorioId"><strong>Laboratorio *</strong></label>
                <select 
                  id="laboratorioId" 
                  class="form-control" 
                  [(ngModel)]="formData.idLaboratorio" 
                  name="laboratorioId" 
                  [disabled]="modoEdicion"
                  required>
                  <option value="">Seleccione un laboratorio</option>
                  <option *ngFor="let lab of laboratorios" [value]="lab.id">
                    {{ lab.nombre }} - {{ lab.direccion?.ciudad }}
                  </option>
                </select>
                <small class="text-muted" *ngIf="modoEdicion">
                  No se puede cambiar el laboratorio en modo edición
                </small>
              </div>

              <div class="form-group">
                <label for="examenId"><strong>Examen *</strong></label>
                <select 
                  id="examenId" 
                  class="form-control" 
                  [(ngModel)]="formData.idExamen" 
                  name="examenId" 
                  [disabled]="modoEdicion"
                  required>
                  <option value="">Seleccione un examen</option>
                  <option *ngFor="let examen of examenes" [value]="examen.id">
                    {{ examen.nombre }} ({{ examen.tipo }})
                  </option>
                </select>
                <small class="text-muted" *ngIf="modoEdicion">
                  No se puede cambiar el examen en modo edición
                </small>
              </div>

              <div class="form-group">
                <label for="precio"><strong>Precio (CLP) *</strong></label>
                <input 
                  type="number" 
                  id="precio" 
                  class="form-control" 
                  [(ngModel)]="formData.precio" 
                  name="precio"
                  min="0"
                  step="100"
                  placeholder="Ej: 15000"
                  required>
              </div>

              <div class="form-group">
                <label for="vigenteDesde"><strong>Vigente Desde *</strong></label>
                <input 
                  type="date" 
                  id="vigenteDesde" 
                  class="form-control" 
                  [(ngModel)]="formData.vigenteDesde" 
                  name="vigenteDesde"
                  required>
              </div>

              <div class="form-group">
                <label for="vigenteHasta"><strong>Vigente Hasta</strong></label>
                <input 
                  type="date" 
                  id="vigenteHasta" 
                  class="form-control" 
                  [(ngModel)]="formData.vigenteHasta" 
                  name="vigenteHasta">
                <small class="text-muted">
                  Dejar vacío si no tiene fecha de término
                </small>
              </div>

              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" (click)="cerrarModal()">
                  Cancelar
                </button>
                <button 
                  type="submit" 
                  class="btn btn-primary" 
                  [disabled]="!formData.idLaboratorio || !formData.idExamen || !formData.precio || !formData.vigenteDesde">
                  {{ modoEdicion ? 'Actualizar' : 'Crear' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .admin-lab-exam {
      background: linear-gradient(135deg, #0369a1 0%, #06b6d4 100%);
      min-height: 100vh;
      padding: 4rem 2rem;
    }

    .header-section {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30px;
      max-width: 1400px;
      margin: 0 auto 30px;
    }

    .header-section h2 {
      color: white;
      font-size: 2rem;
      margin: 0;
    }

    .btn {
      padding: 10px 20px;
      border: none;
      border-radius: 6px;
      cursor: pointer;
      font-weight: 600;
      text-decoration: none;
      display: inline-block;
      transition: all 0.3s;
    }

    .btn-primary {
      background: linear-gradient(135deg, #10b981 0%, #059669 100%);
      color: white;
    }

    .btn-primary:hover {
      background: linear-gradient(135deg, #059669 0%, #047857 100%);
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
    }

    .btn-info {
      background: #3b82f6;
      color: white;
      padding: 6px 12px;
    }

    .btn-info:hover {
      background: #2563eb;
    }

    .btn-danger {
      background: #ef4444;
      color: white;
      padding: 6px 12px;
    }

    .btn-danger:hover {
      background: #dc2626;
    }

    .btn-secondary {
      background: #6b7280;
      color: white;
    }

    .btn-secondary:hover {
      background: #4b5563;
    }

    .table-container {
      background: white;
      border-radius: 12px;
      padding: 1.5rem;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      max-width: 1400px;
      margin: 0 auto;
      overflow-x: auto;
    }

    .table {
      width: 100%;
      margin-bottom: 0;
    }

    .table thead {
      background: #f3f4f6;
    }

    .table th {
      padding: 12px;
      text-align: left;
      font-weight: 600;
      color: #374151;
      border-bottom: 2px solid #e5e7eb;
    }

    .table td {
      padding: 12px;
      border-bottom: 1px solid #e5e7eb;
      color: #374151;
    }

    .table tbody tr:hover {
      background: #f9fafb;
    }

    .alert {
      max-width: 1400px;
      margin: 0 auto 20px;
      padding: 16px;
      border-radius: 8px;
    }

    .alert-danger {
      background: #fee2e2;
      color: #991b1b;
      border: 1px solid #fca5a5;
    }

    .alert-success {
      background: #d1fae5;
      color: #065f46;
      border: 1px solid #6ee7b7;
    }

    .empty-state {
      max-width: 600px;
      margin: 60px auto;
    }

    .empty-card {
      background: white;
      border-radius: 12px;
      padding: 60px 40px;
      text-align: center;
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    }

    .empty-card h3 {
      color: #144766;
      font-size: 24px;
      margin: 16px 0;
    }

    .empty-card p {
      color: #6b7280;
      margin-bottom: 24px;
      font-size: 16px;
    }

    .modal-overlay {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.7);
      display: flex;
      align-items: flex-start;
      justify-content: center;
      z-index: 1000;
      padding: 100px 20px 20px 20px;
      overflow-y: auto;
    }

    .modal-content {
      background: white;
      border-radius: 12px;
      max-width: 600px;
      width: 100%;
      max-height: 90vh;
      overflow-y: auto;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    }

    .modal-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1.5rem;
      border-bottom: 2px solid #e5e7eb;
    }

    .modal-header h3 {
      margin: 0;
      color: #144766;
      font-size: 1.5rem;
    }

    .btn-close {
      background: none;
      border: none;
      font-size: 2rem;
      color: #6b7280;
      cursor: pointer;
      line-height: 1;
      padding: 0;
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 4px;
      transition: all 0.3s;
    }

    .btn-close:hover {
      background: #f3f4f6;
      color: #144766;
    }

    .modal-body {
      padding: 1.5rem;
    }

    .form-group {
      margin-bottom: 1.5rem;
    }

    .form-group label {
      display: block;
      margin-bottom: 0.5rem;
      color: #374151;
      font-size: 14px;
    }

    .form-control {
      width: 100%;
      padding: 0.75rem;
      border: 2px solid #e5e7eb;
      border-radius: 6px;
      font-size: 14px;
      transition: all 0.3s;
    }

    .form-control:focus {
      outline: none;
      border-color: #0369a1;
      box-shadow: 0 0 0 3px rgba(3, 105, 161, 0.1);
    }

    .form-control:disabled {
      background: #f3f4f6;
      cursor: not-allowed;
    }

    .text-muted {
      color: #6b7280;
      font-size: 12px;
      margin-top: 4px;
      display: block;
    }

    .modal-footer {
      display: flex;
      justify-content: flex-end;
      gap: 10px;
      margin-top: 2rem;
      padding-top: 1.5rem;
      border-top: 1px solid #e5e7eb;
    }

    .btn-primary:disabled {
      background: #9ca3af;
      cursor: not-allowed;
      transform: none;
    }

    @media (max-width: 768px) {
      .admin-lab-exam {
        padding: 2rem 1rem;
      }

      .header-section {
        flex-direction: column;
        gap: 20px;
        align-items: stretch;
      }

      .header-section h2 {
        font-size: 1.5rem;
        text-align: center;
      }

      .table-container {
        padding: 1rem;
      }

      .empty-card {
        padding: 40px 20px;
      }
    }
  `]
})
export class AdminLabExamComponent implements OnInit {
  labExams: LabExam[] = [];
  examenes: Examen[] = [];
  laboratorios: Laboratorio[] = [];
  
  loading = false;
  loadingModal = false;
  errorMessage = '';
  successMessage = '';
  
  mostrarModal = false;
  modoEdicion = false;
  
  formData = {
    idLaboratorio: '',
    idExamen: '',
    precio: null as number | null,
    vigenteDesde: '',
    vigenteHasta: ''
  };

  constructor(
    private labExamService: LabExamService,
    private examenService: ExamenService,
    private laboratorioService: LaboratorioService
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.loading = true;
    this.errorMessage = '';
    
    this.labExamService.getLabExams().subscribe({
      next: (data) => {
        this.labExams = data;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Error al cargar las asignaciones: ' + error.message;
        this.loading = false;
        console.error('Error:', error);
      }
    });
  }

  abrirModal(): void {
    this.modoEdicion = false;
    this.mostrarModal = true;
    this.resetForm();
    this.cargarOpcionesModal();
  }

  abrirModalEditar(item: LabExam): void {
    this.modoEdicion = true;
    this.mostrarModal = true;
    
    // Cargar datos del item en el formulario
    // El backend devuelve la estructura con id embebido
    const labId = this.getIdLaboratorio(item);
    const examId = this.getIdExamen(item);
    
    this.formData = {
      idLaboratorio: labId?.toString() || '',
      idExamen: examId?.toString() || '',
      precio: item.precio,
      vigenteDesde: this.convertirFecha(item.vigenteDesde),
      vigenteHasta: item.vigenteHasta ? this.convertirFecha(item.vigenteHasta) : ''
    };
    
    this.cargarOpcionesModal();
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.resetForm();
  }

  cargarOpcionesModal(): void {
    this.loadingModal = true;
    
    Promise.all([
      this.examenService.getExamenes().toPromise(),
      this.laboratorioService.getLaboratorios().toPromise()
    ]).then(([examenes, laboratorios]) => {
      this.examenes = examenes || [];
      this.laboratorios = laboratorios || [];
      this.loadingModal = false;
    }).catch(error => {
      console.error('Error al cargar opciones:', error);
      this.errorMessage = 'Error al cargar opciones del formulario';
      this.loadingModal = false;
    });
  }

  guardar(): void {
    if (!this.formData.idLaboratorio || !this.formData.idExamen || !this.formData.precio || !this.formData.vigenteDesde) {
      alert('Por favor complete todos los campos obligatorios');
      return;
    }

    const labExam: LabExam = {
      idLaboratorio: Number(this.formData.idLaboratorio),
      idExamen: Number(this.formData.idExamen),
      precio: this.formData.precio,
      vigenteDesde: this.formData.vigenteDesde,
      vigenteHasta: this.formData.vigenteHasta || undefined
    };

    if (this.modoEdicion) {
      // Actualizar
      if (!labExam.idLaboratorio || !labExam.idExamen) {
        this.errorMessage = 'ID de laboratorio o examen no válido';
        return;
      }
      this.labExamService.actualizarLabExam(labExam.idLaboratorio, labExam.idExamen, labExam).subscribe({
        next: () => {
          this.successMessage = 'Asignación actualizada exitosamente';
          setTimeout(() => this.successMessage = '', 3000);
          this.cerrarModal();
          this.cargarDatos();
        },
        error: (error) => {
          this.errorMessage = 'Error al actualizar: ' + error.message;
          console.error('Error:', error);
        }
      });
    } else {
      // Crear
      this.labExamService.crearLabExam(labExam).subscribe({
        next: () => {
          this.successMessage = 'Asignación creada exitosamente';
          setTimeout(() => this.successMessage = '', 3000);
          this.cerrarModal();
          this.cargarDatos();
        },
        error: (error) => {
          this.errorMessage = 'Error al crear: ' + error.message;
          console.error('Error:', error);
        }
      });
    }
  }

  eliminar(item: LabExam): void {
    if (!confirm(`¿Está seguro de eliminar la asignación de "${item.nombreExamen}" en "${item.nombreLab}"?`)) {
      return;
    }
    
    const labId = this.getIdLaboratorio(item);
    const examId = this.getIdExamen(item);
    if (!labId || !examId) {
      this.errorMessage = 'No se pudo obtener el ID del laboratorio o examen';
      return;
    }

    this.labExamService.eliminarLabExam(labId, examId).subscribe({
      next: () => {
        this.successMessage = 'Asignación eliminada exitosamente';
        setTimeout(() => this.successMessage = '', 3000);
        this.cargarDatos();
      },
      error: (error) => {
        this.errorMessage = 'Error al eliminar: ' + error.message;
        console.error('Error:', error);
      }
    });
  }

  resetForm(): void {
    this.formData = {
      idLaboratorio: '',
      idExamen: '',
      precio: null,
      vigenteDesde: '',
      vigenteHasta: ''
    };
  }

  convertirFecha(fecha: any): string {
    if (!fecha) return '';
    const d = new Date(fecha);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
  
  // Métodos helper para extraer IDs del objeto embebido
  getIdLaboratorio(item: LabExam): number | undefined {
    return (item as any).id?.idLaboratorio || item.idLaboratorio;
  }
  
  getIdExamen(item: LabExam): number | undefined {
    return (item as any).id?.idExamen || item.idExamen;
  }
}
