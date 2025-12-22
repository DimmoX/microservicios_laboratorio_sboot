import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ResultadoService } from '../../services/resultado.service';
import { ResultadoExamen } from '../../models/resultado.model';
import { AuthService } from '../../services/auth.service';
import { Usuario } from '../../models/usuario.model';
import { PacienteService } from '../../services/paciente.service';
import { CitaService } from '../../services/cita.service';
import { Paciente } from '../../models/paciente.model';
import { CitaAgendada } from '../../models/cita.model';

@Component({
  selector: 'app-resultado-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="resultado-list">
      <div class="header-section">
        <h2>📊 Resultados de Exámenes</h2>
        <button *ngIf="esAdminOEmpleado()" class="btn btn-primary" (click)="abrirModalRegistrar()">
          + Registrar Resultado
        </button>
      </div>

      <div *ngIf="loading" class="text-center text-white">
        <div class="spinner-border" role="status">
          <span class="visually-hidden">Cargando...</span>
        </div>
        <p class="mt-2">Cargando resultados...</p>
      </div>

      <div *ngIf="errorMessage" class="error">{{ errorMessage }}</div>

      <div *ngIf="!loading && resultados.length > 0" class="resultados-grid">
        <div *ngFor="let resultado of resultados" class="resultado-card">
          <div class="resultado-header">
            <h3>{{ resultado.examenNombre || 'Examen #' + resultado.examenId }}</h3>
            <span class="badge" [class.badge-emitido]="resultado.estado === 'EMITIDO'"
                  [class.badge-pendiente]="resultado.estado === 'PENDIENTE'"
                  [class.badge-cancelado]="resultado.estado === 'CANCELADO' || resultado.estado === 'ANULADO'">
              {{ resultado.estado }}
            </span>
          </div>
          <div class="resultado-body">
            <p *ngIf="esAdminOEmpleado()">
              <strong>👤 Paciente:</strong> {{ resultado.pacienteNombre || 'Cargando...' }}
            </p>
            <p *ngIf="resultado.fechaMuestra">
              <strong>📅 Fecha Toma Muestra:</strong><br>
              {{ resultado.fechaMuestra | date:'dd/MM/yyyy HH:mm' }}
            </p>
            <p class="small text-muted" *ngIf="resultado.fechaResultado">
              Resultado publicado: {{ resultado.fechaResultado | date:'dd/MM/yyyy HH:mm' }}
            </p>
          </div>
          <div class="resultado-actions">
            <button class="btn btn-primary btn-sm" (click)="verDetalle(resultado)">
              👁️ Ver Detalle
            </button>
          </div>
        </div>
      </div>

      <div *ngIf="!loading && resultados.length === 0" class="empty-state">
        <div class="empty-card">
          <span style="font-size: 4rem;">📊</span>
          <h3>No se encontraron resultados</h3>
          <p>{{ esAdminOEmpleado() ? 'No hay resultados disponibles en este momento.' : 'No tienes resultados de exámenes disponibles.' }}</p>
        </div>
      </div>

        <!-- Modal Ver Detalle -->
        <div *ngIf="mostrarModalDetalle" class="modal-overlay" (click)="cerrarModalDetalle()">
          <div class="modal-content" (click)="$event.stopPropagation()">
            <div class="modal-header">
              <h3>📊 Detalle del Resultado</h3>
              <button class="btn-close" (click)="cerrarModalDetalle()">&times;</button>
            </div>
            
            <div class="modal-body" *ngIf="resultadoSeleccionado">
              <div class="detail-section">
                <h5 class="section-title">Información del Examen</h5>
                <p><strong>Examen:</strong> {{ resultadoSeleccionado.examenNombre || 'N/A' }}</p>
                <p *ngIf="esAdminOEmpleado()"><strong>Paciente:</strong> {{ resultadoSeleccionado.pacienteNombre || 'N/A' }}</p>
                <p><strong>Estado:</strong>
                  <span class="badge" [class.badge-emitido]="resultadoSeleccionado.estado === 'EMITIDO'"
                        [class.badge-pendiente]="resultadoSeleccionado.estado === 'PENDIENTE'"
                        [class.badge-cancelado]="resultadoSeleccionado.estado === 'CANCELADO' || resultadoSeleccionado.estado === 'ANULADO'">
                    {{ resultadoSeleccionado.estado }}
                  </span>
                </p>
              </div>

              <div class="detail-section">
                <h5 class="section-title">Fechas</h5>
                <p *ngIf="resultadoSeleccionado.fechaMuestra">
                  <strong>Fecha Toma de Muestra:</strong><br>
                  {{ resultadoSeleccionado.fechaMuestra | date:'dd/MM/yyyy HH:mm' }}
                </p>
                <p *ngIf="resultadoSeleccionado.fechaResultado">
                  <strong>Fecha Publicación Resultado:</strong><br>
                  {{ resultadoSeleccionado.fechaResultado | date:'dd/MM/yyyy HH:mm' }}
                </p>
              </div>

              <div class="detail-section" *ngIf="resultadoSeleccionado.valor">
                <h5 class="section-title">Resultado</h5>
                <div class="result-box">
                  <h2 class="result-value">{{ resultadoSeleccionado.valor }} 
                    <span class="result-unit">{{ resultadoSeleccionado.unidad || '' }}</span>
                  </h2>
                </div>
              </div>

              <div class="detail-section" *ngIf="resultadoSeleccionado.observacion">
                <h5 class="section-title">Observación</h5>
                <div class="observation-box">
                  {{ resultadoSeleccionado.observacion }}
                </div>
              </div>
            </div>

            <div class="modal-footer">
              <button class="btn btn-secondary" (click)="cerrarModalDetalle()">Cerrar</button>
            </div>
          </div>
        </div>

        <!-- Modal Registrar Resultado (Solo ADMIN/EMPLOYEE) -->
        <div *ngIf="mostrarModalRegistrar && esAdminOEmpleado()" class="modal-overlay" (click)="cerrarModalRegistrar()">
          <div class="modal-content" (click)="$event.stopPropagation()">
            <div class="modal-header">
              <h3>📝 Registrar Resultado de Examen</h3>
              <button class="btn-close" (click)="cerrarModalRegistrar()">&times;</button>
            </div>
            
            <div class="modal-body">
              <form #registroForm="ngForm">
                <div class="form-group">
                  <label for="pacienteId">Paciente *</label>
                  <select 
                    id="pacienteId" 
                    name="pacienteId" 
                    class="form-control" 
                    [(ngModel)]="nuevoResultado.pacienteId" 
                    (ngModelChange)="onPacienteChange()"
                    required>
                    <option value="">Seleccione un paciente</option>
                    <option *ngFor="let paciente of pacientes" [value]="paciente.id">
                      {{ paciente.pnombre }} {{ paciente.snombre || '' }} {{ paciente.papellido }} {{ paciente.sapellido || '' }} - {{ paciente.rut }}
                    </option>
                  </select>
                </div>

                <div class="form-group">
                  <label for="citaId">Examen a Registrar Resultado *</label>
                  <select 
                    id="citaId" 
                    name="citaId" 
                    class="form-control" 
                    [(ngModel)]="nuevoResultado.citaId" 
                    (ngModelChange)="onCitaChange()"
                    required
                    [disabled]="!nuevoResultado.pacienteId || citasDisponibles.length === 0">
                    <option value="">{{ citasDisponibles.length === 0 ? 'No hay exámenes atendidos sin resultados' : 'Seleccione un examen' }}</option>
                    <option *ngFor="let cita of citasDisponibles" [value]="cita.id">
                      {{ cita.examenNombre }} - {{ cita.fechaHora | date:'dd/MM/yyyy HH:mm' }}
                    </option>
                  </select>
                  <small class="form-text text-muted">Solo se muestran exámenes en estado ATENDIDA sin resultados publicados</small>
                </div>

                <div class="row">
                  <div class="col-md-8">
                    <div class="form-group">
                      <label for="valor">Valor del Resultado *</label>
                      <input 
                        type="text" 
                        id="valor" 
                        name="valor" 
                        class="form-control" 
                        [(ngModel)]="nuevoResultado.valor" 
                        required
                        placeholder="Ej: 12.5 o Positivo">
                    </div>
                  </div>
                  <div class="col-md-4">
                    <div class="form-group">
                      <label for="unidad">Unidad *</label>
                      <input 
                        type="text" 
                        id="unidad" 
                        name="unidad" 
                        class="form-control" 
                        [(ngModel)]="nuevoResultado.unidad" 
                        required
                        placeholder="Ej: mg/dL">
                    </div>
                  </div>
                </div>

                <div class="row">
                  <div class="col-md-6">
                    <div class="form-group">
                      <label for="fechaMuestra">Fecha Toma de Muestra *</label>
                      <input 
                        type="datetime-local" 
                        id="fechaMuestra" 
                        name="fechaMuestra" 
                        class="form-control" 
                        [(ngModel)]="nuevoResultado.fechaMuestra" 
                        required>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="form-group">
                      <label for="fechaResultado">Fecha Resultado</label>
                      <input 
                        type="datetime-local" 
                        id="fechaResultado" 
                        name="fechaResultado" 
                        class="form-control" 
                        [(ngModel)]="nuevoResultado.fechaResultado">
                    </div>
                  </div>
                </div>

                <div class="form-group">
                  <label for="estado">Estado *</label>
                  <select 
                    id="estado" 
                    name="estado" 
                    class="form-control" 
                    [(ngModel)]="nuevoResultado.estado" 
                    required>
                    <option value="">Seleccione un estado</option>
                    <option value="PENDIENTE">PENDIENTE</option>
                    <option value="EMITIDO">EMITIDO</option>
                  </select>
                </div>

                <div class="form-group">
                  <label for="observaciones">Observaciones</label>
                  <textarea 
                    id="observaciones" 
                    name="observaciones" 
                    class="form-control" 
                    [(ngModel)]="nuevoResultado.observaciones" 
                    rows="3"
                    placeholder="Observaciones adicionales sobre el resultado..."></textarea>
                </div>

                <div *ngIf="mensajeError" class="alert alert-danger mt-3">
                  {{ mensajeError }}
                </div>

                <div *ngIf="mensajeExito" class="alert alert-success mt-3">
                  {{ mensajeExito }}
                </div>
              </form>
            </div>

            <div class="modal-footer">
              <button class="btn btn-secondary" (click)="cerrarModalRegistrar()" [disabled]="guardando">Cancelar</button>
              <button 
                class="btn btn-primary" 
                (click)="guardarResultado()" 
                [disabled]="!registroForm.form.valid || guardando">
                <span *ngIf="guardando">Guardando...</span>
                <span *ngIf="!guardando">💾 Guardar Resultado</span>
              </button>
            </div>
          </div>
        </div>
    </div>
  `,
  styles: [`
    .resultado-list {
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

    .btn-sm {
      padding: 6px 12px;
      font-size: 12px;
    }

    .resultados-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 2rem;
      max-width: 1400px;
      margin: 0 auto;
    }

    .resultado-card {
      background: white;
      border-radius: 12px;
      padding: 1.5rem;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      transition: all 0.3s;
    }

    .resultado-card:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    }

    .resultado-header {
      display: flex;
      justify-content: space-between;
      align-items: start;
      margin-bottom: 1rem;
      padding-bottom: 1rem;
      border-bottom: 2px solid #e5e7eb;
    }

    .resultado-header h3 {
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

    .badge-emitido {
      background: #d1fae5;
      color: #065f46;
    }

    .badge-pendiente {
      background: #fef3c7;
      color: #92400e;
    }

    .badge-cancelado {
      background: #fee2e2;
      color: #991b1b;
    }

    .resultado-body {
      margin-bottom: 1rem;
    }

    .resultado-body p {
      margin: 0.5rem 0;
      color: #374151;
      font-size: 14px;
    }

    .resultado-actions {
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

    .text-center {
      text-align: center;
    }

    .text-white {
      color: white;
    }

    .text-muted {
      color: #6b7280;
    }

    .small {
      font-size: 12px;
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
      position: sticky;
      top: 0;
      background: white;
      z-index: 1;
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

    .modal-footer {
      display: flex;
      justify-content: flex-end;
      gap: 10px;
      padding: 1.5rem;
      border-top: 1px solid #e5e7eb;
    }

    .btn-secondary {
      background: #6b7280;
      color: white;
    }

    .btn-secondary:hover {
      background: #4b5563;
    }

    .detail-section {
      margin-bottom: 1.5rem;
      padding-bottom: 1.5rem;
      border-bottom: 1px solid #e5e7eb;
    }

    .detail-section:last-child {
      border-bottom: none;
      margin-bottom: 0;
      padding-bottom: 0;
    }

    .section-title {
      color: #144766;
      font-size: 1.1rem;
      font-weight: 700;
      margin-bottom: 1rem;
    }

    .result-box {
      background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
      border-radius: 12px;
      padding: 2rem;
      text-align: center;
    }

    .result-value {
      color: #1e40af;
      font-size: 3rem;
      font-weight: 800;
      margin: 0;
    }

    .result-unit {
      font-size: 1.5rem;
      color: #3b82f6;
    }

    .observation-box {
      background: #f3f4f6;
      border-left: 4px solid #3b82f6;
      padding: 1rem;
      border-radius: 6px;
      color: #374151;
    }

    .form-text {
      font-size: 12px;
      margin-top: 4px;
      display: block;
    }

    .form-control:disabled {
      background-color: #e5e7eb;
      cursor: not-allowed;
    }

    .alert {
      padding: 12px 16px;
      border-radius: 6px;
      margin-top: 16px;
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

    @media (max-width: 768px) {
      .header-section {
        flex-direction: column;
        gap: 15px;
        align-items: stretch;
      }

      .header-section h2 {
        text-align: center;
        font-size: 1.5rem;
      }

      .result-value {
        font-size: 2rem;
      }
    }
  `]
})
export class ResultadoListComponent implements OnInit {
  resultados: ResultadoExamen[] = [];
  loading = false;
  errorMessage = '';
  currentUser: Usuario | null = null;
  
  // Modales
  mostrarModalDetalle = false;
  mostrarModalRegistrar = false;
  resultadoSeleccionado: ResultadoExamen | null = null;

  // Datos para formulario de registro
  pacientes: Paciente[] = [];
  citasDisponibles: CitaAgendada[] = [];
  citaSeleccionada: CitaAgendada | null = null;
  nuevoResultado: any = {
    pacienteId: '',
    citaId: '',
    valor: '',
    unidad: '',
    fechaMuestra: '',
    fechaResultado: '',
    estado: '',
    observaciones: ''
  };
  guardando = false;
  mensajeError = '';
  mensajeExito = '';

  constructor(
    private resultadoService: ResultadoService,
    private authService: AuthService,
    private pacienteService: PacienteService,
    private citaService: CitaService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser.subscribe(user => {
      this.currentUser = user;
      this.cargarResultados();
    });
  }

  cargarResultados(): void {
    this.loading = true;
    this.errorMessage = '';

    // Si es paciente, cargar solo sus resultados
    if (this.currentUser?.pacienteId && !this.esAdminOEmpleado()) {
      this.resultadoService.getResultadosPorPaciente(this.currentUser.pacienteId).subscribe({
        next: (data) => {
          this.resultados = data;
          this.cargarNombresYExamenes();
          this.loading = false;
        },
        error: (error) => {
          this.errorMessage = 'Error al cargar resultados: ' + error.message;
          this.loading = false;
        }
      });
    } else {
      // Admin y Employee ven todos los resultados
      this.resultadoService.getResultados().subscribe({
        next: (data) => {
          this.resultados = data;
          this.cargarNombresYExamenes();
          this.loading = false;
        },
        error: (error) => {
          this.errorMessage = 'Error al cargar resultados: ' + error.message;
          this.loading = false;
        }
      });
    }
  }

  cargarNombresYExamenes(): void {
    // Cargar nombres de pacientes para resultados
    this.resultados.forEach(resultado => {
      if (resultado.pacienteId && !resultado.pacienteNombre) {
        this.pacienteService.getPaciente(resultado.pacienteId).subscribe({
          next: (paciente) => {
            resultado.pacienteNombre = `${paciente.pnombre} ${paciente.snombre || ''} ${paciente.papellido} ${paciente.sapellido || ''}`.trim();
          },
          error: (error) => {
            console.error('Error al cargar paciente:', error);
            resultado.pacienteNombre = 'Paciente no disponible';
          }
        });
      }
    });
  }

  verDetalle(resultado: ResultadoExamen): void {
    this.resultadoSeleccionado = resultado;
    this.mostrarModalDetalle = true;
  }

  cerrarModalDetalle(): void {
    this.mostrarModalDetalle = false;
    this.resultadoSeleccionado = null;
  }

  abrirModalRegistrar(): void {
    this.limpiarFormulario();
    this.cargarPacientes();
    this.mostrarModalRegistrar = true;
  }

  cerrarModalRegistrar(): void {
    this.mostrarModalRegistrar = false;
    this.limpiarFormulario();
  }

  cargarPacientes(): void {
    this.pacienteService.getPacientes().subscribe({
      next: (data) => {
        this.pacientes = data;
      },
      error: (error) => {
        console.error('Error al cargar pacientes:', error);
        this.mensajeError = 'Error al cargar lista de pacientes';
      }
    });
  }

  onPacienteChange(): void {
    this.citasDisponibles = [];
    this.citaSeleccionada = null;
    this.nuevoResultado.citaId = '';
    
    if (!this.nuevoResultado.pacienteId) {
      return;
    }

    // Cargar citas del paciente en estado ATENDIDA
    this.citaService.getCitasByPaciente(Number(this.nuevoResultado.pacienteId)).subscribe({
      next: (citas) => {
        // Filtrar solo citas ATENDIDAS
        const citasAtendidas = citas.filter(c => c.estado === 'ATENDIDA');
        
        // Para cada cita, verificar si ya tiene resultados
        const verificaciones = citasAtendidas.map(cita => 
          this.resultadoService.getResultados().toPromise().then(resultados => {
            const tieneResultado = resultados?.some(r => r.agendaId === cita.id);
            return tieneResultado ? null : cita;
          })
        );

        Promise.all(verificaciones).then(citasVerificadas => {
          this.citasDisponibles = citasVerificadas.filter(c => c !== null) as CitaAgendada[];
          
          if (this.citasDisponibles.length === 0) {
            this.mensajeError = 'Este paciente no tiene exámenes atendidos sin resultados publicados';
          }
        });
      },
      error: (error) => {
        console.error('Error al cargar citas del paciente:', error);
        this.mensajeError = 'Error al cargar exámenes del paciente';
      }
    });
  }

  onCitaChange(): void {
    if (!this.nuevoResultado.citaId) {
      this.citaSeleccionada = null;
      return;
    }
    
    this.citaSeleccionada = this.citasDisponibles.find(c => c.id === Number(this.nuevoResultado.citaId)) || null;
  }

  guardarResultado(): void {
    this.guardando = true;
    this.mensajeError = '';
    this.mensajeExito = '';

    console.log('🔍 Iniciando guardarResultado()');
    console.log('🔍 Token en sessionStorage:', !!sessionStorage.getItem('token'));

    if (!this.citaSeleccionada) {
      this.mensajeError = 'Debe seleccionar una cita válida';
      this.guardando = false;
      return;
    }

    // Convertir fechas a formato ISO 8601 con offset (requerido por OffsetDateTime en el backend)
    const convertirFechaISO = (fechaStr: string): string | null => {
      if (!fechaStr) return null;
      // datetime-local produce "2025-12-21T07:20", necesitamos agregar offset
      const fecha = new Date(fechaStr);
      return fecha.toISOString(); // Produce "2025-12-21T07:20:00.000Z"
    };

    // Preparar el objeto para enviar usando datos de la cita
    const resultadoParaGuardar = {
      pacienteId: this.citaSeleccionada.pacienteId,
      examenId: this.citaSeleccionada.examenId,
      agendaId: this.citaSeleccionada.id,
      labId: this.citaSeleccionada.labId,
      valor: this.nuevoResultado.valor,
      unidad: this.nuevoResultado.unidad,
      fechaMuestra: convertirFechaISO(this.nuevoResultado.fechaMuestra),
      fechaResultado: convertirFechaISO(this.nuevoResultado.fechaResultado),
      estado: this.nuevoResultado.estado,
      observacion: this.nuevoResultado.observaciones || null
    };

    console.log('📦 Datos a enviar:', resultadoParaGuardar);

    this.resultadoService.crearResultado(resultadoParaGuardar as ResultadoExamen).subscribe({
      next: (data) => {
        console.log('✅ Resultado creado exitosamente:', data);
        this.mensajeExito = '✅ Resultado registrado exitosamente';
        this.guardando = false;
        
        // Esperar 1.5 segundos y cerrar modal
        setTimeout(() => {
          this.cerrarModalRegistrar();
          this.cargarResultados(); // Recargar la lista
        }, 1500);
      },
      error: (error) => {
        console.error('❌ Error completo:', error);
        console.error('❌ Status:', error.status);
        console.error('❌ Error body:', error.error);
        console.error('❌ Message:', error.message);
        
        let mensajeDetallado = 'Error al guardar el resultado';
        if (error.status === 401) {
          mensajeDetallado = 'Error de autenticación. Verifique que su sesión esté activa.';
        } else if (error.status === 403) {
          mensajeDetallado = 'No tiene permisos para registrar resultados.';
        } else if (error.error?.description) {
          mensajeDetallado = error.error.description;
        } else if (error.message) {
          mensajeDetallado = error.message;
        }
        
        this.mensajeError = mensajeDetallado;
        this.guardando = false;
      }
    });
  }

  limpiarFormulario(): void {
    this.nuevoResultado = {
      pacienteId: '',
      citaId: '',
      valor: '',
      unidad: '',
      fechaMuestra: '',
      fechaResultado: '',
      estado: '',
      observaciones: ''
    };
    this.citasDisponibles = [];
    this.citaSeleccionada = null;
    this.mensajeError = '';
    this.mensajeExito = '';
  }

  esAdmin(): boolean {
    return this.currentUser?.rol === 'ADMIN';
  }

  esAdminOEmpleado(): boolean {
    const rol = this.currentUser?.rol;
    return rol === 'ADMIN' || rol === 'EMPLOYEE' || rol === 'LAB_EMPLOYEE';
  }
}
