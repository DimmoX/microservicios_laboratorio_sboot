// Controller: Formulario para crear/editar laboratorios
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { LaboratorioService } from '../../services/laboratorio.service';
import { Laboratorio } from '../../models/laboratorio.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-laboratorio-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './laboratorio-form.component.html',
  styleUrls: ['./laboratorio-form.component.css']
})
export class LaboratorioFormComponent implements OnInit {
  laboratorio: Laboratorio = {
    nombre: '',
    tipo: '',
    direccion: {
      calle: '',
      numero: 0,
      ciudad: '',
      comuna: '',
      region: ''
    },
    contacto: {
      fono1: '',
      fono2: '',
      email: ''
    }
  };
  
  isEditMode = false;
  loading = false;
  errorMessage = '';

  constructor(
    private laboratorioService: LaboratorioService,
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.cargarLaboratorio(Number(id));
    }
  }

  cargarLaboratorio(id: number): void {
    this.loading = true;
    this.laboratorioService.getLaboratorio(id).subscribe({
      next: (data: any) => {
        this.laboratorio = data;
        this.loading = false;
      },
      error: (error: any) => {
        this.errorMessage = 'Error al cargar laboratorio: ' + error.message;
        this.loading = false;
      }
    });
  }

  guardarLaboratorio(): void {
    this.loading = true;

      if (this.isEditMode && this.laboratorio.id) {
        this.laboratorioService.actualizarLaboratorio(this.laboratorio.id, this.laboratorio).subscribe({
        next: () => {
          alert('Laboratorio actualizado exitosamente');
          this.router.navigate(['/laboratorios']);
        },
        error: (error: any) => {
          this.errorMessage = 'Error al actualizar: ' + error.message;
          this.loading = false;
        }
      });
    } else {
        this.laboratorioService.crearLaboratorio(this.laboratorio).subscribe({
        next: () => {
          alert('Laboratorio creado exitosamente');
          this.router.navigate(['/laboratorios']);
        },
        error: (error: any) => {
          this.errorMessage = 'Error al crear: ' + error.message;
          this.loading = false;
        }
      });
    }
  }
}
