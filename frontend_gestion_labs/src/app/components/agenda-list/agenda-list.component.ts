import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { ExamenService } from '../../services/examen.service';
import { LaboratorioService } from '../../services/laboratorio.service';
import { CitaService } from '../../services/cita.service';
import { Examen } from '../../models/examen.model';
import { Laboratorio } from '../../models/laboratorio.model';
import { CitaAgendada, CrearCitaRequest } from '../../models/cita.model';
import { Usuario } from '../../models/usuario.model';

@Component({
  selector: 'app-agenda-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="agenda-list">
      <div class="header-section">
        <h2>📅 Mis Exámenes Agendados</h2>
        <button class="btn btn-primary" (click)="abrirModal()">+ Agendar Nuevo Examen</button>
      </div>

      <div *ngIf="loading" class="text-center text-white">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
        <p class="mt-2">Cargando citas agendadas...</p>
      </div>

      <div *ngIf="errorMessage" class="error">{{ errorMessage }}</div>

      <div *ngIf="!loading && citas.length > 0" class="citas-grid">
        <div *ngFor="let cita of citas" class="cita-card">
          <div class="cita-header">
            <h3>{{ cita.examenNombre }}</h3>
            <span class="badge" 
                  [class.badge-warning]="cita.estado === 'PROGRAMADA'"
                  [class.badge-success]="cita.estado === 'ATENDIDA'"
                  [class.badge-danger]="cita.estado === 'CANCELADA'">
              {{ cita.estado }}
            </span>
          </div>
          <div class="cita-body">
            <p><strong>🏥 Laboratorio:</strong> {{ cita.laboratorioNombre }}</p>
            <p><strong>📅 Fecha:</strong> {{ cita.fechaHora | date:'dd/MM/yyyy HH:mm' }}</p>
            <p><strong>💰 Precio:</strong> {{ cita.precio }}</p>
          </div>
          <div class="cita-actions" *ngIf="cita.estado === 'PROGRAMADA'">
            <button class="btn btn-warning btn-sm" (click)="cancelarCita(cita.id)">
              🚫 Cancelar
            </button>
          </div>
        </div>
      </div>

      <div *ngIf="!loading && citas.length === 0" class="empty-state">
        <div class="empty-card">
          <span style="font-size: 4rem;">📅</span>
          <h3>No tienes exámenes agendados</h3>
          <p>Agenda tu primer examen para comenzar con tu seguimiento médico.</p>
          <button class="btn btn-primary" (click)="abrirModal()">+ Agendar Primer Examen</button>
        </div>
      </div>

      <!-- Modal para agendar examen -->
      <div *ngIf="mostrarModal" class="modal-overlay" (click)="cerrarModal()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>📅 Agendar Nuevo Examen</h3>
            <button class="btn-close" (click)="cerrarModal()">&times;</button>
          </div>
          
          <div class="modal-body">
            <div *ngIf="loadingModal" class="text-center">
              <div class="spinner-border text-primary" role="status">
                <span class="visually-hidden">Cargando...</span>
              </div>
              <p class="mt-2">Cargando exámenes disponibles...</p>
            </div>

            <form *ngIf="!loadingModal" (ngSubmit)="agendarExamen()">
              <div class="form-group">
                <label for="examenId"><strong>Examen *</strong></label>
                <select id="examenId" class="form-control" [(ngModel)]="nuevaCita.examenId" name="examenId" required>
                  <option value="">Seleccione un examen</option>
                  <option *ngFor="let examen of examenes" [value]="examen.id">
                    {{ examen.nombre }} ({{ examen.tipo }})
                  </option>
                </select>
              </div>

              <div class="form-group">
                <label for="laboratorioId"><strong>Laboratorio *</strong></label>
                <select id="laboratorioId" class="form-control" [(ngModel)]="nuevaCita.laboratorioId" name="laboratorioId" required>
                  <option value="">Seleccione un laboratorio</option>
                  <option *ngFor="let lab of laboratorios" [value]="lab.id">
                    {{ lab.nombre }} - {{ lab.direccion?.ciudad || 'Sin ciudad' }}
                  </option>
                </select>
              </div>

              <div class="form-group">
                <label for="fechaAgendada"><strong>Fecha y Hora *</strong></label>
                <input type="datetime-local" id="fechaAgendada" class="form-control" 
                       [(ngModel)]="nuevaCita.fechaAgendada" name="fechaAgendada" required>
              </div>

              <div class="form-group">
                <label for="observaciones"><strong>Observaciones</strong></label>
                <textarea id="observaciones" class="form-control" rows="3" 
                          [(ngModel)]="nuevaCita.observaciones" name="observaciones"
                          placeholder="Indicaciones especiales o comentarios"></textarea>
              </div>

              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" (click)="cerrarModal()">Cancelar</button>
                <button type="submit" class="btn btn-primary" 
                        [disabled]="!nuevaCita.examenId || !nuevaCita.laboratorioId || !nuevaCita.fechaAgendada">
                  Agendar Examen
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .agenda-list {
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

    .btn-warning {
      background: #f59e0b;
      color: white;
      padding: 6px 12px;
      font-size: 12px;
    }

    .btn-warning:hover {
      background: #d97706;
    }

    .citas-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 2rem;
      max-width: 1400px;
      margin: 0 auto;
    }

    .cita-card {
      background: white;
      border-radius: 12px;
      padding: 1.5rem;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      transition: all 0.3s;
    }

    .cita-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    }

    .cita-header {
      display: flex;
      justify-content: space-between;
      align-items: start;
      margin-bottom: 1rem;
      padding-bottom: 1rem;
      border-bottom: 2px solid #e5e7eb;
    }

    .cita-header h3 {
      color: #144766;
      font-size: 1.25rem;
      margin: 0;
      flex: 1;
    }

    .badge {
      padding: 6px 12px;
      border-radius: 20px;
      font-size: 11px;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .badge-warning {
      background: #fef3c7;
      color: #92400e;
    }

    .badge-info {
      background: #dbeafe;
      color: #1e40af;
    }

    .badge-success {
      background: #d1fae5;
      color: #065f46;
    }

    .badge-danger {
      background: #fee2e2;
      color: #991b1b;
    }

    .cita-body {
      margin-bottom: 1rem;
    }

    .cita-body p {
      margin: 0.5rem 0;
      color: #374151;
      font-size: 14px;
    }

    .cita-actions {
      display: flex;
      justify-content: flex-end;
      padding-top: 1rem;
      border-top: 1px solid #e5e7eb;
    }

    .error {
      max-width: 1400px;
      margin: 0 auto 20px;
      background: #fee2e2;
      color: #991b1b;
      padding: 16px;
      border-radius: 8px;
      border: 1px solid #fca5a5;
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

    /* Modal styles */
    .modal-overlay {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.7);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
      padding: 20px;
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

    .modal-footer {
      display: flex;
      justify-content: flex-end;
      gap: 10px;
      margin-top: 2rem;
      padding-top: 1.5rem;
      border-top: 1px solid #e5e7eb;
    }

    .btn-secondary {
      background: #6b7280;
      color: white;
    }

    .btn-secondary:hover {
      background: #4b5563;
    }

    .btn-primary:disabled {
      background: #9ca3af;
      cursor: not-allowed;
      transform: none;
    }

    @media (max-width: 768px) {
      .agenda-list {
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

      .citas-grid {
        grid-template-columns: 1fr;
      }

      .empty-card {
        padding: 40px 20px;
      }
    }
  `]
})
export class AgendaListComponent implements OnInit {
  citas: CitaAgendada[] = [];
  loading = false;
  errorMessage = '';
  
  // Modal
  mostrarModal = false;
  loadingModal = false;
  examenes: Examen[] = [];
  laboratorios: Laboratorio[] = [];
  
  nuevaCita = {
    examenId: '',
    laboratorioId: '',
    fechaAgendada: '',
    observaciones: ''
  };

  currentUser: Usuario | null = null;

  constructor(
    private authService: AuthService,
    private examenService: ExamenService,
    private laboratorioService: LaboratorioService,
    private citaService: CitaService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser.subscribe(user => {
      this.currentUser = user;
      if (user && user.id) {
        this.cargarCitas();
      }
    });
  }

  cargarCitas(): void {
    if (!this.currentUser || !this.currentUser.pacienteId) {
      this.errorMessage = 'Usuario no tiene paciente asociado';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    
    this.citaService.getCitasByPaciente(this.currentUser.pacienteId).subscribe({
      next: (data) => {
        this.citas = data;
        this.loading = false;
      },
      error: (error) => {
        this.errorMessage = 'Error al cargar las citas: ' + error.message;
        this.loading = false;
        console.error('Error al cargar citas:', error);
      }
    });
  }

  abrirModal(): void {
    this.mostrarModal = true;
    this.cargarDatosModal();
  }

  cerrarModal(): void {
    this.mostrarModal = false;
    this.resetForm();
  }

  cargarDatosModal(): void {
    this.loadingModal = true;
    
    // Cargar exámenes y laboratorios en paralelo
    Promise.all([
      this.examenService.getExamenes().toPromise(),
      this.laboratorioService.getLaboratorios().toPromise()
    ]).then(([examenes, laboratorios]) => {
      this.examenes = examenes || [];
      this.laboratorios = laboratorios || [];
      this.loadingModal = false;
    }).catch(error => {
      console.error('Error al cargar datos:', error);
      alert('Error al cargar los datos disponibles');
      this.loadingModal = false;
    });
  }

  agendarExamen(): void {
    if (!this.nuevaCita.examenId || !this.nuevaCita.laboratorioId || !this.nuevaCita.fechaAgendada) {
      alert('Por favor complete todos los campos obligatorios');
      return;
    }

    if (!this.currentUser || !this.currentUser.pacienteId) {
      alert('Usuario no tiene paciente asociado');
      return;
    }

    this.loadingModal = true;
    
    const citaRequest: CrearCitaRequest = {
      pacienteId: this.currentUser.pacienteId,
      labId: Number(this.nuevaCita.laboratorioId),
      examenId: Number(this.nuevaCita.examenId),
      fechaHora: this.nuevaCita.fechaAgendada
    };

    this.citaService.crearCita(citaRequest).subscribe({
      next: (citaCreada) => {
        alert('Examen agendado exitosamente');
        this.loadingModal = false;
        this.cerrarModal();
        this.cargarCitas(); // Recargar lista
      },
      error: (error) => {
        alert('Error al agendar el examen: ' + error.message);
        this.loadingModal = false;
        console.error('Error al crear cita:', error);
      }
    });
  }

  resetForm(): void {
    this.nuevaCita = {
      examenId: '',
      laboratorioId: '',
      fechaAgendada: '',
      observaciones: ''
    };
  }

  cancelarCita(id: number): void {
    const cita = this.citas.find(c => c.id === id);
    if (!cita) return;

    if (confirm(`¿Está seguro de cancelar la cita para ${cita.examenNombre}?`)) {
      this.citaService.cancelarCita(id).subscribe({
        next: () => {
          alert('Cita cancelada exitosamente');
          this.cargarCitas(); // Recargar lista
        },
        error: (error) => {
          alert('Error al cancelar la cita: ' + error.message);
          console.error('Error al cancelar cita:', error);
        }
      });
    }
  }
}
