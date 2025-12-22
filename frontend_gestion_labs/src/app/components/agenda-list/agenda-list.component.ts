import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { ExamenService } from '../../services/examen.service';
import { LaboratorioService } from '../../services/laboratorio.service';
import { CitaService } from '../../services/cita.service';
import { LabExamService } from '../../services/lab-exam.service';
import { PacienteService } from '../../services/paciente.service';
import { ResultadoService } from '../../services/resultado.service';
import { Examen } from '../../models/examen.model';
import { Laboratorio } from '../../models/laboratorio.model';
import { CitaAgendada, CrearCitaRequest } from '../../models/cita.model';
import { Usuario } from '../../models/usuario.model';
import { Paciente, RegistroPacienteRequest } from '../../models/paciente.model';

@Component({
  selector: 'app-agenda-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="agenda-list">
      <div class="header-section">
        <h2>📅 {{ getTitulo() }}</h2>
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
            <p *ngIf="esAdminOEmpleado()"><strong>👤 Paciente:</strong> {{ cita.pacienteNombre || 'Sin información' }}</p>
            <p><strong>🏥 Laboratorio:</strong> {{ cita.laboratorioNombre }}</p>
            <p><strong>📅 Fecha:</strong> {{ cita.fechaHora | date:"dd/MM/yyyy HH:mm" }}</p>
            <p><strong>💰 Precio:</strong> 
              <span *ngIf="cita.precio; else sinPrecio">\${{cita.precio | number:"1.0-0" }}</span>
              <ng-template #sinPrecio><span class="text-muted">Sin precio definido</span></ng-template>
            </p>
            <p *ngIf="cita.estado === 'ATENDIDA'">
              <strong>📋 Resultados:</strong> 
              <span *ngIf="tieneResultados(cita.id)" class="badge badge-success-sm">✅ Publicados</span>
              <span *ngIf="!tieneResultados(cita.id)" class="badge badge-warning-sm">⏳ Pendientes</span>
            </p>
            <div *ngIf="esAdminOEmpleado()" class="estado-control">
              <label><strong>🔄 Cambiar Estado:</strong></label>
              <select 
                class="form-select form-select-sm" 
                [(ngModel)]="cita.estado" 
                (change)="cambiarEstadoCita(cita)">
                <option value="PROGRAMADA">PROGRAMADA</option>
                <option value="ATENDIDA">ATENDIDA</option>
                <option value="CANCELADA">CANCELADA</option>
              </select>
            </div>
          </div>
          <div class="cita-actions" *ngIf="cita.estado === 'PROGRAMADA'">
            <button class="btn btn-info btn-sm" (click)="abrirModalEditar(cita)">
              ✏️ Editar
            </button>
            <button class="btn btn-warning btn-sm ms-2" (click)="cancelarCita(cita.id)">
              🚫 Cancelar
            </button>
            <button class="btn btn-danger btn-sm ms-2" (click)="eliminarCita(cita.id)" *ngIf="esAdmin()">
              🗑️ Eliminar
            </button>
          </div>
        </div>
      </div>

      <div *ngIf="!loading && citas.length === 0" class="empty-state">
        <div class="empty-card">
          <span style="font-size: 4rem;">📅</span>
          <h3>{{ getTextoVacio() }}</h3>
          <p>{{ getSubtituloVacio() }}</p>
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
              <!-- Selector de paciente para admin y employee -->
              <div class="form-group" *ngIf="esAdminOEmpleado()">
                <label for="pacienteId"><strong>Paciente *</strong></label>
                <div class="d-flex gap-2">
                  <select id="pacienteId" class="form-control" [(ngModel)]="nuevaCita.pacienteId" 
                          name="pacienteId" required>
                    <option value="">Seleccione un paciente</option>
                    <option *ngFor="let paciente of pacientes" [value]="paciente.id">
                      {{ paciente.pnombre }} {{ paciente.papellido }} - {{ paciente.rut }}
                    </option>
                  </select>
                  <button type="button" class="btn btn-secondary" (click)="abrirModalCrearPaciente()">
                    ➕ Nuevo
                  </button>
                </div>
              </div>

              <div class="form-group">
                <label for="examenId"><strong>Examen *</strong></label>
                <select id="examenId" class="form-control" [(ngModel)]="nuevaCita.examenId" 
                        name="examenId" required (change)="onExamenOLabChange()">
                  <option value="">Seleccione un examen</option>
                  <option *ngFor="let examen of examenes" [value]="examen.id">
                    {{ examen.nombre }} ({{ examen.tipo }})
                  </option>
                </select>
              </div>

              <div class="form-group">
                <label for="laboratorioId"><strong>Laboratorio *</strong></label>
                <select id="laboratorioId" class="form-control" [(ngModel)]="nuevaCita.laboratorioId" 
                        name="laboratorioId" required (ngModelChange)="onLaboratorioChange($event)" 
                        [disabled]="!nuevaCita.examenId">
                  <option value="">{{ nuevaCita.examenId ? 'Seleccione un laboratorio' : 'Primero seleccione un examen' }}</option>
                  <option *ngFor="let lab of laboratoriosFiltrados" [value]="lab.id">
                    {{ lab.nombre }} - {{ lab.direccion?.ciudad || 'Sin ciudad' }}
                  </option>
                </select>
              </div>

              <!-- Mostrar precio calculado o advertencia -->
              <div class="form-group" *ngIf="nuevaCita.examenId && nuevaCita.laboratorioId">
                <div *ngIf="verificandoPrecio" class="alert alert-info">
                  Verificando precio disponible...
                </div>
                <div *ngIf="!verificandoPrecio && precioCalculado" class="alert alert-success">
                  💰 <strong>Precio:</strong> \${{ precioCalculado | number:"1.0-0" }}
                </div>
                <div *ngIf="!verificandoPrecio && !precioCalculado" class="alert alert-danger">
                  ⚠️ <strong>No disponible:</strong> Este examen no está disponible en el laboratorio seleccionado. 
                  Por favor, seleccione otra combinación.
                </div>
              </div>

              <div class="form-group">
                <label for="fechaAgendada"><strong>Fecha *</strong></label>
                <input type="date" id="fechaAgendada" class="form-control" 
                       [(ngModel)]="nuevaCita.fecha" name="fechaAgendada" required
                       [min]="fechaMinima"
                       (ngModelChange)="onFechaHoraChange()">
              </div>

              <div class="form-group">
                <label for="horaAgendada"><strong>Hora *</strong></label>
                <select id="horaAgendada" class="form-control" 
                        [(ngModel)]="nuevaCita.hora" name="horaAgendada" required
                        (ngModelChange)="onFechaHoraChange()">
                  <option value="">Seleccione una hora</option>
                  <option *ngFor="let hora of horasDisponibles" [value]="hora">{{ hora }}</option>
                </select>
                <div *ngIf="!horarioValido && mensajeHorario" class="alert alert-warning mt-2">
                  {{ mensajeHorario }}
                </div>
                <small class="form-text text-muted">
                  📅 Lunes a Viernes: 07:00 AM - 05:00 PM | Sábados y Domingos: 07:00 AM - 02:00 PM
                </small>
              </div>

              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" (click)="cerrarModal()">Cancelar</button>
                <button type="submit" class="btn btn-primary" 
                        [disabled]="!nuevaCita.examenId || !nuevaCita.laboratorioId || !nuevaCita.fecha || !nuevaCita.hora || (esAdminOEmpleado() && !nuevaCita.pacienteId) || verificandoPrecio || !horarioValido">
                  Agendar Examen
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- Modal para editar fecha/hora -->
      <div *ngIf="mostrarModalEditar" class="modal-overlay" (click)="cerrarModalEditar()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>✏️ Editar Fecha y Hora</h3>
            <button class="btn-close" (click)="cerrarModalEditar()">&times;</button>
          </div>
          
          <div class="modal-body">
            <form (ngSubmit)="actualizarFechaHora()">
              <div class="form-group">
                <label><strong>Examen:</strong></label>
                <p class="text-muted">{{ citaEditando?.examenNombre }}</p>
              </div>

              <div class="form-group">
                <label><strong>Laboratorio:</strong></label>
                <p class="text-muted">{{ citaEditando?.laboratorioNombre }}</p>
              </div>

              <div class="form-group">
                <label for="nuevaFecha"><strong>Nueva Fecha *</strong></label>
                <input type="date" id="nuevaFecha" class="form-control" 
                       [(ngModel)]="edicionCita.fecha" name="nuevaFecha" required
                       [min]="fechaMinima"
                       (ngModelChange)="onFechaHoraEdicionChange()">
              </div>

              <div class="form-group">
                <label for="nuevaHora"><strong>Nueva Hora *</strong></label>
                <select id="nuevaHora" class="form-control" 
                        [(ngModel)]="edicionCita.hora" name="nuevaHora" required
                        (ngModelChange)="onFechaHoraEdicionChange()">
                  <option value="">Seleccione una hora</option>
                  <option *ngFor="let hora of horasDisponibles" [value]="hora">{{ hora }}</option>
                </select>
                <div *ngIf="!horarioValidoEdicion && mensajeHorarioEdicion" class="alert alert-warning mt-2">
                  {{ mensajeHorarioEdicion }}
                </div>
                <small class="text-muted d-block">Actual: {{ citaEditando?.fechaHora | date:'dd/MM/yyyy HH:mm' }}</small>
                <small class="form-text text-muted">
                  📅 Lunes a Viernes: 07:00 AM - 05:00 PM | Sábados y Domingos: 07:00 AM - 02:00 PM
                </small>
              </div>

              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" (click)="cerrarModalEditar()">Cancelar</button>
                <button type="button" class="btn btn-primary" 
                        [disabled]="!edicionCita.fecha || !edicionCita.hora || loadingModal || !horarioValidoEdicion" 
                        (click)="actualizarFechaHora()">
                  <span *ngIf="loadingModal" class="spinner-border spinner-border-sm mr-2"></span>
                  Confirmar Cambio
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <!-- Modal para crear paciente -->
      <div *ngIf="mostrarModalCrearPaciente" class="modal-overlay" (click)="cerrarModalCrearPaciente()">
        <div class="modal-content" (click)="$event.stopPropagation()">
          <div class="modal-header">
            <h3>👤 Registrar Nuevo Paciente</h3>
            <button class="btn-close" (click)="cerrarModalCrearPaciente()">&times;</button>
          </div>
          
          <div class="modal-body">
            <form (ngSubmit)="crearPaciente()">
              <div class="form-group">
                <label for="rut"><strong>RUT *</strong></label>
                <input type="text" id="rut" class="form-control" 
                       [(ngModel)]="nuevoPaciente.rut" name="rut" required
                       placeholder="12.345.678-9">
              </div>

              <div class="form-group">
                <label for="pnombre"><strong>Primer Nombre *</strong></label>
                <input type="text" id="pnombre" class="form-control" 
                       [(ngModel)]="nuevoPaciente.pnombre" name="pnombre" required>
              </div>

              <div class="form-group">
                <label for="snombre"><strong>Segundo Nombre</strong></label>
                <input type="text" id="snombre" class="form-control" 
                       [(ngModel)]="nuevoPaciente.snombre" name="snombre">
              </div>

              <div class="form-group">
                <label for="papellido"><strong>Apellido Paterno *</strong></label>
                <input type="text" id="papellido" class="form-control" 
                       [(ngModel)]="nuevoPaciente.papellido" name="papellido" required>
              </div>

              <div class="form-group">
                <label for="sapellido"><strong>Apellido Materno</strong></label>
                <input type="text" id="sapellido" class="form-control" 
                       [(ngModel)]="nuevoPaciente.sapellido" name="sapellido">
              </div>

              <div class="form-group">
                <label for="email"><strong>Email *</strong></label>
                <input type="email" id="email" class="form-control" 
                       [(ngModel)]="nuevoPaciente.contacto.email" name="email" required>
              </div>

              <div class="form-group">
                <label for="telefono"><strong>Teléfono *</strong></label>
                <input type="tel" id="telefono" class="form-control" 
                       [(ngModel)]="nuevoPaciente.contacto.fono1" name="telefono" required>
              </div>

              <div class="form-group">
                <label for="telefono2"><strong>Teléfono 2</strong></label>
                <input type="tel" id="telefono2" class="form-control" 
                       [(ngModel)]="nuevoPaciente.contacto.fono2" name="telefono2">
              </div>

              <div class="form-group">
                <label for="password"><strong>Contraseña *</strong></label>
                <input type="password" id="password" class="form-control" 
                       [(ngModel)]="nuevoPaciente.password" name="password" required>
              </div>

              <div class="form-group">
                <label for="calle"><strong>Calle</strong></label>
                <input type="text" id="calle" class="form-control" 
                       [(ngModel)]="nuevoPaciente.direccion.calle" name="calle">
              </div>

              <div class="form-group">
                <label for="numero"><strong>Número</strong></label>
                <input type="text" id="numero" class="form-control" 
                       [(ngModel)]="nuevoPaciente.direccion.numero" name="numero">
              </div>

              <div class="form-group">
                <label for="ciudad"><strong>Ciudad</strong></label>
                <input type="text" id="ciudad" class="form-control" 
                       [(ngModel)]="nuevoPaciente.direccion.ciudad" name="ciudad">
              </div>

              <div class="form-group">
                <label for="region"><strong>Región</strong></label>
                <input type="text" id="region" class="form-control" 
                       [(ngModel)]="nuevoPaciente.direccion.region" name="region">
              </div>

              <div class="form-group">
                <label for="pais"><strong>País</strong></label>
                <input type="text" id="pais" class="form-control" 
                       [(ngModel)]="nuevoPaciente.direccion.pais" name="pais" 
                       value="Chile">
              </div>

              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" (click)="cerrarModalCrearPaciente()">Cancelar</button>
                <button type="submit" class="btn btn-primary" [disabled]="loadingModalPaciente">
                  <span *ngIf="loadingModalPaciente" class="spinner-border spinner-border-sm mr-2"></span>
                  Crear Paciente
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

    .btn-info {
      background: #3b82f6;
      color: white;
      padding: 6px 12px;
      font-size: 12px;
    }

    .btn-info:hover {
      background: #2563eb;
    }

    .btn-danger {
      background: #ef4444;
      color: white;
      padding: 6px 12px;
      font-size: 12px;
    }

    .btn-danger:hover {
      background: #dc2626;
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

    .badge-success-sm {
      background: #d1fae5;
      color: #065f46;
      padding: 4px 8px;
      border-radius: 12px;
      font-size: 10px;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .badge-warning-sm {
      background: #fef3c7;
      color: #92400e;
      padding: 4px 8px;
      border-radius: 12px;
      font-size: 10px;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .cita-body {
      margin-bottom: 1rem;
    }

    .cita-body p {
      margin: 0.5rem 0;
      color: #374151;
      font-size: 14px;
    }

    .estado-control {
      margin-top: 1rem;
      padding: 0.75rem;
      background: #f9fafb;
      border-radius: 8px;
      border: 1px solid #e5e7eb;
    }

    .estado-control label {
      display: block;
      margin-bottom: 0.5rem;
      color: #374151;
      font-size: 13px;
    }

    .form-select {
      width: 100%;
      padding: 8px 12px;
      border: 2px solid #d1d5db;
      border-radius: 6px;
      font-size: 14px;
      font-weight: 600;
      color: #1f2937;
      background: white;
      cursor: pointer;
      transition: all 0.3s;
    }

    .form-select:hover {
      border-color: #3b82f6;
    }

    .form-select:focus {
      outline: none;
      border-color: #2563eb;
      box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
    }

    .form-select-sm {
      padding: 6px 10px;
      font-size: 13px;
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
      margin-top: 80px;
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

    .alert {
      padding: 12px 16px;
      border-radius: 6px;
      font-size: 14px;
      margin-bottom: 0;
    }

    .alert-info {
      background: #dbeafe;
      color: #1e40af;
      border: 1px solid #93c5fd;
    }

    .alert-success {
      background: #d1fae5;
      color: #065f46;
      border: 1px solid #6ee7b7;
    }

    .alert-danger {
      background: #fee2e2;
      color: #991b1b;
      border: 1px solid #fca5a5;
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

      .modal-content {
        margin-top: 100px;
        max-height: 85vh;
      }

      .modal-header h3 {
        font-size: 1.2rem;
      }
    }
  `]
})
export class AgendaListComponent implements OnInit {
  citas: CitaAgendada[] = [];
  loading = false;
  errorMessage = '';
  
  // Cache de resultados para evitar múltiples llamadas
  resultadosPorAgenda: Map<number, boolean> = new Map();
  
  // Modal
  mostrarModal = false;
  mostrarModalEditar = false;
  loadingModal = false;
  examenes: Examen[] = [];
  laboratorios: Laboratorio[] = [];
  laboratoriosFiltrados: Laboratorio[] = [];
  citaEditando: CitaAgendada | null = null;
  
  edicionCita = {
    fecha: '',
    hora: ''
  };
  
  nuevaCita = {
    examenId: '',
    laboratorioId: '',
    pacienteId: null as number | null,
    fecha: '',
    hora: '',
    fechaAgendada: '',
    observaciones: ''
  };
  
  horasDisponibles: string[] = [];
  precioCalculado: number | null = null;
  verificandoPrecio = false;
  horarioValido = true;
  mensajeHorario = '';
  horarioValidoEdicion = true;
  mensajeHorarioEdicion = '';
  fechaMinima: string = '';

  currentUser: Usuario | null = null;
  
  // Gestión de pacientes (para admin y employee)
  pacientes: Paciente[] = [];
  mostrarModalCrearPaciente = false;
  loadingModalPaciente = false;
  nuevoPaciente: RegistroPacienteRequest = {
    pnombre: '',
    snombre: '',
    papellido: '',
    sapellido: '',
    rut: '',
    password: '',
    contacto: {
      fono1: '',
      fono2: '',
      email: ''
    },
    direccion: {
      calle: '',
      numero: '',
      ciudad: '',
      region: '',
      pais: 'Chile'
    }
  };

  constructor(
    private authService: AuthService,
    private examenService: ExamenService,
    private laboratorioService: LaboratorioService,
    private citaService: CitaService,
    private labExamService: LabExamService,
    private pacienteService: PacienteService,
    private resultadoService: ResultadoService
  ) {}

  ngOnInit(): void {
    // Establecer fecha mínima como hoy en formato YYYY-MM-DD
    const hoy = new Date();
    const year = hoy.getFullYear();
    const month = String(hoy.getMonth() + 1).padStart(2, '0');
    const day = String(hoy.getDate()).padStart(2, '0');
    this.fechaMinima = `${year}-${month}-${day}`;
    
    this.authService.currentUser.subscribe(user => {
      this.currentUser = user;
      if (user && user.id) {
        this.cargarCitas();
        // Si es admin o employee, cargar lista de pacientes
        if (this.esAdminOEmpleado()) {
          this.cargarPacientes();
        }
      }
    });
    this.generarHorasDisponibles();
  }

  cargarCitas(): void {
    this.loading = true;
    this.errorMessage = '';
    
    // Admin y Employee ven todas las citas, Paciente solo las suyas
    if (this.esAdminOEmpleado()) {
      this.citaService.getAllCitas().subscribe({
        next: (data) => {
          this.citas = data;
          this.cargarEstadoResultados();
          this.loading = false;
        },
        error: (error) => {
          this.errorMessage = 'Error al cargar las citas: ' + error.message;
          this.loading = false;
          console.error('Error al cargar citas:', error);
        }
      });
    } else {
      // Para paciente
      if (!this.currentUser || !this.currentUser.pacienteId) {
        this.errorMessage = 'Usuario no tiene paciente asociado';
        this.loading = false;
        return;
      }
      
      this.citaService.getCitasByPaciente(this.currentUser.pacienteId).subscribe({
        next: (data) => {
          this.citas = data;
          this.cargarEstadoResultados();
          this.loading = false;
        },
        error: (error) => {
          this.errorMessage = 'Error al cargar las citas: ' + error.message;
          this.loading = false;
          console.error('Error al cargar citas:', error);
        }
      });
    }
  }

  cargarEstadoResultados(): void {
    this.resultadoService.getResultados().subscribe({
      next: (resultados) => {
        // Construir mapa de agendaId -> tiene resultado
        this.resultadosPorAgenda.clear();
        resultados.forEach(resultado => {
          if (resultado.agendaId) {
            this.resultadosPorAgenda.set(resultado.agendaId, true);
          }
        });
      },
      error: (error) => {
        console.error('Error al cargar estado de resultados:', error);
      }
    });
  }

  tieneResultados(agendaId: number): boolean {
    return this.resultadosPorAgenda.get(agendaId) === true;
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

  onExamenOLabChange(): void {
    // Si cambió el examen, filtrar laboratorios disponibles
    if (this.nuevaCita.examenId) {
      this.filtrarLaboratoriosPorExamen(Number(this.nuevaCita.examenId));
    } else {
      this.laboratoriosFiltrados = [];
      this.precioCalculado = null;
    }
    
    if (this.nuevaCita.examenId && this.nuevaCita.laboratorioId) {
      this.verificarPrecio();
    } else {
      this.precioCalculado = null;
    }
  }

  onLaboratorioChange(value: any): void {
    this.nuevaCita.laboratorioId = value;
    
    if (this.nuevaCita.examenId && this.nuevaCita.laboratorioId) {
      this.verificarPrecio();
    } else {
      this.precioCalculado = null;
    }
  }

  filtrarLaboratoriosPorExamen(examenId: number): void {
    console.log('Filtrando laboratorios para examen ID:', examenId);
    console.log('Total de laboratorios cargados:', this.laboratorios.length);
    
    // Limpiar selección de laboratorio al cambiar de examen
    this.nuevaCita.laboratorioId = '';
    this.precioCalculado = null;
    this.laboratoriosFiltrados = [];
    
    this.labExamService.getLabsPorExamen(examenId).subscribe({
      next: (labExams) => {
        console.log('LabExams recibidos del servicio:', labExams);
        
        // Obtener los IDs de laboratorios que tienen este examen
        // El backend devuelve la estructura con id embebido: { id: { idLaboratorio, idExamen }, precio, ... }
        const labIds = labExams.map(le => {
          // Acceder al idLaboratorio dentro del objeto id embebido
          const labId = (le as any).id?.idLaboratorio || le.idLaboratorio;
          console.log('Extrayendo laboratorio ID:', labId, 'del objeto:', le);
          return labId;
        }).filter(id => id !== undefined && id !== null);
        
        console.log('IDs de laboratorios con este examen:', labIds);
        
        // Filtrar la lista completa de laboratorios
        this.laboratoriosFiltrados = this.laboratorios.filter(lab => 
          lab.id !== undefined && labIds.includes(lab.id)
        );
        
        console.log('Laboratorios filtrados:', this.laboratoriosFiltrados);
      },
      error: (error) => {
        console.error('Error al filtrar laboratorios:', error);
        this.laboratoriosFiltrados = [];
      }
    });
  }

  verificarPrecio(): void {
    const labId = Number(this.nuevaCita.laboratorioId);
    const examenId = Number(this.nuevaCita.examenId);
    
    if (!labId || !examenId) return;
    
    this.verificandoPrecio = true;
    this.precioCalculado = null;
    
    this.labExamService.getPrecio(labId, examenId).subscribe({
      next: (labExam) => {
        this.verificandoPrecio = false;
        if (labExam && labExam.precio) {
          this.precioCalculado = labExam.precio;
        } else {
          this.precioCalculado = null;
        }
      },
      error: (error) => {
        this.verificandoPrecio = false;
        this.precioCalculado = null;
        console.error('Error al verificar precio:', error);
      }
    });
  }

  validarHorarioLaboral(): void {
    if (!this.nuevaCita.fechaAgendada) {
      this.horarioValido = true;
      this.mensajeHorario = '';
      return;
    }

    const fecha = new Date(this.nuevaCita.fechaAgendada);
    const diaSemana = fecha.getDay(); // 0 = Domingo, 6 = Sábado
    const hora = fecha.getHours();
    const minutos = fecha.getMinutes();
    const tiempoEnMinutos = hora * 60 + minutos;

    // Lunes a Viernes: 07:00 - 17:00 (inclusive)
    if (diaSemana >= 1 && diaSemana <= 5) {
      const inicioLV = 7 * 60;  // 07:00
      const finLV = 18 * 60;    // 18:00 (para incluir hasta 17:59)
      
      if (tiempoEnMinutos < inicioLV || tiempoEnMinutos >= finLV) {
        this.horarioValido = false;
        this.mensajeHorario = '⏰ Horario de atención Lunes a Viernes: 07:00 AM - 05:00 PM';
      } else {
        this.horarioValido = true;
        this.mensajeHorario = '';
      }
    }
    // Sábado y Domingo: 07:00 - 14:00 (inclusive)
    else if (diaSemana === 0 || diaSemana === 6) {
      const inicioSD = 7 * 60;  // 07:00
      const finSD = 15 * 60;    // 15:00 (para incluir hasta 14:59)
      
      if (tiempoEnMinutos < inicioSD || tiempoEnMinutos >= finSD) {
        this.horarioValido = false;
        this.mensajeHorario = '⏰ Horario de atención Sábados y Domingos: 07:00 AM - 02:00 PM';
      } else {
        this.horarioValido = true;
        this.mensajeHorario = '';
      }
    }
  }

  validarHorarioLaboralEdicion(): void {
    if (!this.edicionCita.fecha || !this.edicionCita.hora) {
      this.horarioValidoEdicion = true;
      this.mensajeHorarioEdicion = '';
      return;
    }

    const fecha = new Date(`${this.edicionCita.fecha}T${this.edicionCita.hora}`);
    const diaSemana = fecha.getDay();
    const hora = fecha.getHours();
    const minutos = fecha.getMinutes();
    const tiempoEnMinutos = hora * 60 + minutos;

    // Lunes a Viernes: 07:00 - 17:00 (inclusive)
    if (diaSemana >= 1 && diaSemana <= 5) {
      const inicioLV = 7 * 60;  // 07:00
      const finLV = 18 * 60;    // 18:00 (para incluir hasta 17:59)
      
      if (tiempoEnMinutos < inicioLV || tiempoEnMinutos >= finLV) {
        this.horarioValidoEdicion = false;
        this.mensajeHorarioEdicion = '⏰ Horario de atención Lunes a Viernes: 07:00 AM - 05:00 PM';
      } else {
        this.horarioValidoEdicion = true;
        this.mensajeHorarioEdicion = '';
      }
    }
    // Sábado y Domingo: 07:00 - 14:00 (inclusive)
    else if (diaSemana === 0 || diaSemana === 6) {
      const inicioSD = 7 * 60;  // 07:00
      const finSD = 15 * 60;    // 15:00 (para incluir hasta 14:59)
      
      if (tiempoEnMinutos < inicioSD || tiempoEnMinutos >= finSD) {
        this.horarioValidoEdicion = false;
        this.mensajeHorarioEdicion = '⏰ Horario de atención Sábados y Domingos: 07:00 AM - 02:00 PM';
      } else {
        this.horarioValidoEdicion = true;
        this.mensajeHorarioEdicion = '';
      }
    }
  }

  // Generar array de horas disponibles en intervalos de 15 minutos según el día
  generarHorasDisponibles(fechaStr?: string): void {
    const horas: string[] = [];
    let horaInicio = 0;
    let horaFin = 24;

    // Si se proporciona una fecha, filtrar según el día de la semana
    if (fechaStr) {
      // Crear fecha agregando "T00:00:00" para forzar zona horaria local
      const [year, month, day] = fechaStr.split('-').map(Number);
      const fecha = new Date(year, month - 1, day);
      const diaSemana = fecha.getDay();
      
      const ahora = new Date();
      const hoyYear = ahora.getFullYear();
      const hoyMonth = ahora.getMonth();
      const hoyDay = ahora.getDate();
      
      // Comparar solo año, mes y día
      const esHoy = (year === hoyYear && month - 1 === hoyMonth && day === hoyDay);

      // Lunes a Viernes (1-5): 07:00 - 17:00
      if (diaSemana >= 1 && diaSemana <= 5) {
        horaInicio = 7;
        horaFin = 17;
      }
      // Sábado y Domingo (0, 6): 07:00 - 14:00
      else if (diaSemana === 0 || diaSemana === 6) {
        horaInicio = 7;
        horaFin = 14;
      }

      // Si es hoy, ajustar hora de inicio a la hora actual + 1 hora
      if (esHoy) {
        const horaActual = ahora.getHours();
        const minutoActual = ahora.getMinutes();
        
        // Redondear a la siguiente hora + 1 (para dar tiempo de preparación)
        let proximaHora = horaActual + 1;
        let proximoMinuto = Math.ceil(minutoActual / 15) * 15;
        
        if (proximoMinuto >= 60) {
          proximaHora++;
          proximoMinuto = 0;
        }
        
        // Actualizar horaInicio solo si es mayor que el horario de atención
        if (proximaHora > horaInicio) {
          horaInicio = proximaHora;
        }
        
        // Si la próxima hora disponible supera el horario de cierre, no hay horas disponibles
        if (horaInicio > horaFin) {
          this.horasDisponibles = [];
          return;
        }
      }
    }

    // Generar horas en intervalos de 15 minutos dentro del rango (inclusivo)
    for (let h = horaInicio; h <= horaFin; h++) {
      for (let m = 0; m < 60; m += 15) {
        const hora = String(h).padStart(2, '0');
        const minuto = String(m).padStart(2, '0');
        horas.push(`${hora}:${minuto}`);
      }
    }
    this.horasDisponibles = horas;
  }

  onFechaHoraChange(): void {
    // Regenerar horas disponibles según la fecha seleccionada
    if (this.nuevaCita.fecha) {
      this.generarHorasDisponibles(this.nuevaCita.fecha);
      
      // Validar si la hora actual sigue siendo válida
      if (this.nuevaCita.hora && !this.horasDisponibles.includes(this.nuevaCita.hora)) {
        this.nuevaCita.hora = ''; // Limpiar hora si ya no es válida
      }
    }
    
    if (this.nuevaCita.fecha && this.nuevaCita.hora) {
      this.nuevaCita.fechaAgendada = `${this.nuevaCita.fecha}T${this.nuevaCita.hora}`;
      this.validarHorarioLaboral();
    }
  }

  onFechaHoraEdicionChange(): void {
    // Regenerar horas disponibles según la fecha seleccionada
    if (this.edicionCita.fecha) {
      this.generarHorasDisponibles(this.edicionCita.fecha);
      
      // Validar si la hora actual sigue siendo válida
      if (this.edicionCita.hora && !this.horasDisponibles.includes(this.edicionCita.hora)) {
        this.edicionCita.hora = ''; // Limpiar hora si ya no es válida
      }
    }
    
    if (this.edicionCita.fecha && this.edicionCita.hora) {
      this.validarHorarioLaboralEdicion();
    }
  }

  agendarExamen(): void {
    if (!this.nuevaCita.examenId || !this.nuevaCita.laboratorioId || !this.nuevaCita.fecha || !this.nuevaCita.hora) {
      alert('Por favor complete todos los campos obligatorios');
      return;
    }

    // Determinar el paciente ID según el rol
    let pacienteId: number;
    
    if (this.esAdminOEmpleado()) {
      // Admin y Employee deben seleccionar un paciente
      if (!this.nuevaCita.pacienteId) {
        alert('Por favor seleccione un paciente');
        return;
      }
      pacienteId = this.nuevaCita.pacienteId;
    } else {
      // Paciente usa su propio ID
      if (!this.currentUser || !this.currentUser.pacienteId) {
        alert('Usuario no tiene paciente asociado');
        return;
      }
      pacienteId = this.currentUser.pacienteId;
    }

    this.loadingModal = true;
    
    // Enviar fecha en formato ISO sin timezone: YYYY-MM-DDTHH:mm:ss
    const fechaHora = `${this.nuevaCita.fechaAgendada}:00`;
    
    const citaRequest: CrearCitaRequest = {
      pacienteId: pacienteId,
      labId: Number(this.nuevaCita.laboratorioId),
      examenId: Number(this.nuevaCita.examenId),
      fechaHora: fechaHora
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
      pacienteId: null,
      fecha: '',
      hora: '',
      fechaAgendada: '',
      observaciones: ''
    };
    this.precioCalculado = null;
    this.laboratoriosFiltrados = [];
    this.verificandoPrecio = false;
    this.horarioValido = true;
    this.mensajeHorario = '';
  }

  cancelarCita(id: number): void {
    const cita = this.citas.find(c => c.id === id);
    if (!cita) return;

    if (confirm(`¿Está seguro de cancelar la cita para ${cita.examenNombre}? (Cambiará el estado a CANCELADA pero mantendrá el registro)`)) {
      this.citaService.cancelarCita(id).subscribe({
        next: (citaActualizada) => {
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

  cambiarEstadoCita(cita: CitaAgendada): void {
    const estadoAnterior = this.citas.find(c => c.id === cita.id)?.estado;
    
    if (confirm(`¿Cambiar el estado de "${cita.examenNombre}" a ${cita.estado}?`)) {
      this.citaService.actualizarEstado(cita.id, cita.estado).subscribe({
        next: (citaActualizada) => {
          alert(`Estado actualizado a ${cita.estado}`);
          this.cargarCitas(); // Recargar lista para actualizar indicadores
        },
        error: (error) => {
          // Revertir cambio en caso de error
          if (estadoAnterior) {
            cita.estado = estadoAnterior;
          }
          alert('Error al actualizar el estado: ' + (error.error?.description || error.message));
          console.error('Error al cambiar estado:', error);
        }
      });
    } else {
      // Revertir cambio si el usuario cancela
      if (estadoAnterior) {
        cita.estado = estadoAnterior;
      }
    }
  }

  eliminarCita(id: number): void {
    const cita = this.citas.find(c => c.id === id);
    if (!cita) return;

    if (confirm(`¿Está seguro de ELIMINAR PERMANENTEMENTE la cita para ${cita.examenNombre}? Esta acción NO se puede deshacer.`)) {
      this.citaService.eliminarCita(id).subscribe({
        next: () => {
          alert('Cita eliminada permanentemente');
          this.cargarCitas(); // Recargar lista
        },
        error: (error) => {
          alert('Error al eliminar la cita: ' + error.message);
          console.error('Error al eliminar cita:', error);
        }
      });
    }
  }

  abrirModalEditar(cita: CitaAgendada): void {
    this.citaEditando = cita;
    // Convertir fecha ISO a formato fecha y hora separados
    const fecha = new Date(cita.fechaHora);
    const year = fecha.getFullYear();
    const month = String(fecha.getMonth() + 1).padStart(2, '0');
    const day = String(fecha.getDate()).padStart(2, '0');
    const hours = String(fecha.getHours()).padStart(2, '0');
    const minutes = String(fecha.getMinutes()).padStart(2, '0');
    this.edicionCita.fecha = `${year}-${month}-${day}`;
    this.edicionCita.hora = `${hours}:${minutes}`;
    
    // Regenerar horas disponibles para la fecha seleccionada
    this.generarHorasDisponibles(this.edicionCita.fecha);
    
    this.mostrarModalEditar = true;
    this.validarHorarioLaboralEdicion();
  }

  cerrarModalEditar(): void {
    this.mostrarModalEditar = false;
    this.citaEditando = null;
    this.edicionCita = {
      fecha: '',
      hora: ''
    };
    this.horarioValidoEdicion = true;
    this.mensajeHorarioEdicion = '';
  }

  actualizarFechaHora(): void {
    if (!this.citaEditando || !this.edicionCita.fecha || !this.edicionCita.hora) {
      alert('Por favor complete la fecha y hora');
      return;
    }

    this.loadingModal = true;

    // Enviar fecha en formato ISO sin timezone: YYYY-MM-DDTHH:mm:ss
    const fechaISO = `${this.edicionCita.fecha}T${this.edicionCita.hora}:00`;

    this.citaService.actualizarFechaHora(this.citaEditando.id, fechaISO).subscribe({
      next: (citaActualizada) => {
        alert('Fecha y hora actualizadas exitosamente');
        this.loadingModal = false;
        this.cerrarModalEditar();
        this.cargarCitas();
      },
      error: (error) => {
        alert('Error al actualizar la cita: ' + error.message);
        this.loadingModal = false;
        console.error('Error al actualizar cita:', error);
      }
    });
  }

  // ============= MÉTODOS HELPER PARA ROLES =============

  esAdmin(): boolean {
    return this.currentUser?.rol === 'ADMIN';
  }

  esAdminOEmpleado(): boolean {
    const rol = this.currentUser?.rol;
    return rol === 'ADMIN' || rol === 'EMPLOYEE' || rol === 'LAB_EMPLOYEE';
  }

  getTitulo(): string {
    if (this.esAdmin()) {
      return 'Gestión de Exámenes Agendados';
    } else if (this.currentUser?.rol === 'EMPLOYEE' || this.currentUser?.rol === 'LAB_EMPLOYEE') {
      return 'Exámenes Agendados de Pacientes';
    } else {
      return 'Mis Exámenes Agendados';
    }
  }

  getTextoVacio(): string {
    if (this.esAdminOEmpleado()) {
      return 'No hay exámenes agendados en el sistema';
    } else {
      return 'No tienes exámenes agendados';
    }
  }

  getSubtituloVacio(): string {
    if (this.esAdminOEmpleado()) {
      return 'Haz clic en "Agendar Examen" para crear un nuevo agendamiento para un paciente';
    } else {
      return 'Haz clic en "Agendar Examen" para programar tu primera cita';
    }
  }

  // ============= GESTIÓN DE PACIENTES =============

  cargarPacientes(): void {
    this.pacienteService.getPacientes().subscribe({
      next: (pacientes) => {
        this.pacientes = pacientes;
      },
      error: (error) => {
        console.error('Error al cargar pacientes:', error);
        alert('Error al cargar la lista de pacientes');
      }
    });
  }

  abrirModalCrearPaciente(): void {
    this.mostrarModalCrearPaciente = true;
    this.resetNuevoPaciente();
  }

  cerrarModalCrearPaciente(): void {
    this.mostrarModalCrearPaciente = false;
    this.resetNuevoPaciente();
  }

  resetNuevoPaciente(): void {
    this.nuevoPaciente = {
      pnombre: '',
      snombre: '',
      papellido: '',
      sapellido: '',
      rut: '',
      password: '',
      contacto: {
        fono1: '',
        fono2: '',
        email: ''
      },
      direccion: {
        calle: '',
        numero: '',
        ciudad: '',
        region: '',
        pais: 'Chile'
      }
    };
  }

  crearPaciente(): void {
    // Validar solo campos obligatorios (datos básicos de identificación y contacto)
    if (!this.nuevoPaciente.pnombre || !this.nuevoPaciente.papellido || 
        !this.nuevoPaciente.rut || !this.nuevoPaciente.password ||
        !this.nuevoPaciente.contacto.email || !this.nuevoPaciente.contacto.fono1) {
      alert('Por favor complete todos los campos obligatorios: RUT, Primer Nombre, Apellido Paterno, Email, Teléfono y Contraseña');
      return;
    }

    this.loadingModalPaciente = true;

    // Preparar el objeto para enviar: solo incluir fono2 si tiene valor
    const pacienteData = { ...this.nuevoPaciente };
    if (!pacienteData.contacto.fono2 || pacienteData.contacto.fono2.trim() === '') {
      delete pacienteData.contacto.fono2;
    }

    this.pacienteService.registrarPaciente(pacienteData).subscribe({
      next: (response) => {
        alert('Paciente creado exitosamente');
        this.loadingModalPaciente = false;
        
        // Recargar lista de pacientes
        this.cargarPacientes();
        
        // Auto-seleccionar el nuevo paciente en el formulario
        if (response.pacienteId) {
          this.nuevaCita.pacienteId = response.pacienteId;
        }
        
        this.cerrarModalCrearPaciente();
      },
      error: (error) => {
        alert('Error al crear paciente: ' + error.message);
        this.loadingModalPaciente = false;
        console.error('Error al crear paciente:', error);
      }
    });
  }
}
