import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { ExamenService } from '../../services/examen.service';
import { Examen } from '../../models/examen.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-examen-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="form-container">
      <h2>{{ isEditMode ? '✏️ Editar Examen' : '➕ Nuevo Examen' }}</h2>
      
      <form (ngSubmit)="guardarExamen()" #exForm="ngForm" class="card">
        <div class="form-group">
          <label for="codigo">Código *</label>
          <input type="text" id="codigo" name="codigo" [(ngModel)]="examen.codigo" required maxlength="4">
        </div>
        
        <div class="form-group">
          <label for="nombre">Nombre *</label>
          <input type="text" id="nombre" name="nombre" [(ngModel)]="examen.nombre" required>
        </div>
        
        <div class="form-group">
          <label for="tipo">Tipo *</label>
          <select id="tipo" name="tipo" [(ngModel)]="examen.tipo" required>
            <option value="">Seleccione...</option>
            <option value="Sangre">Sangre</option>
            <option value="Orina">Orina</option>
            <option value="Heces">Heces</option>
            <option value="Imagen">Imagen</option>
          </select>
        </div>
        
        <div class="button-group">
          <button type="submit" class="btn btn-primary" [disabled]="!exForm.valid || loading">
            {{ loading ? 'Guardando...' : 'Guardar' }}
          </button>
          <a routerLink="/examenes" class="btn btn-secondary">Cancelar</a>
        </div>
      </form>
    </div>
  `,
  styles: [`
    .form-container { max-width: 700px; margin: 0 auto; padding: 20px; }
    h2 { color: #0369a1; margin-bottom: 24px; }
    .card { background: white; padding: 30px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
    .form-group { margin-bottom: 20px; }
    .button-group { margin-top: 30px; display: flex; gap: 10px; justify-content: flex-end; flex-wrap: wrap; }
    
    /* Mobile */
    @media (max-width: 767px) {
      .form-container { padding: 10px; }
      h2 { font-size: 1.25rem; margin-bottom: 16px; }
      .card { padding: 20px; }
      .form-group { margin-bottom: 16px; }
      .button-group { flex-direction: column; margin-top: 20px; }
      .button-group .btn { width: 100%; margin: 0; }
    }
    
    /* Tablet */
    @media (min-width: 768px) and (max-width: 1023px) {
      .form-container { padding: 15px; }
      .card { padding: 25px; }
      .button-group { justify-content: center; }
    }
  `]
})
export class ExamenFormComponent implements OnInit {
  examen: Examen = { codigo: '', nombre: '', tipo: '' };
  isEditMode = false;
  loading = false;

  constructor(
    private examenService: ExamenService,
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.examenService.getExamen(Number(id)).subscribe({
        next: (data: any) => this.examen = data,
        error: (error: any) => alert('Error: ' + error.message)
      });
    }
  }

  guardarExamen(): void {
    this.loading = true;
    const obs = this.isEditMode && this.examen.id
      ? this.examenService.actualizarExamen(this.examen.id, this.examen)
      : this.examenService.crearExamen(this.examen);
    obs.subscribe({
      next: () => {
        alert('Examen guardado exitosamente');
        this.router.navigate(['/examenes']);
      },
      error: (error: any) => {
        alert('Error: ' + error.message);
        this.loading = false;
      }
    });
  }
}
