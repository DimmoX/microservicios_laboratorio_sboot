import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { UsuarioService } from '../../services/usuario.service';
import { Usuario } from '../../models/usuario.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-usuario-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuario-form.component.html',
  styleUrl: './usuario-form.component.css'
})
export class UsuarioFormComponent implements OnInit {
  usuario: Usuario = {
    id: 0,
    pnombre: '',
    snombre: '',
    papellido: '',
    sapellido: '',
    username: '',
    password: '',
    rol: 'PATIENT',
    telefono: '',
    direccion: '',
    activo: true,
    rut: ''
  };

  // Campos del formulario para nombres y apellidos
  nombres: string = '';
  apellidos: string = '';

  isEditMode = false;
  loading = false;
  errorMessage = '';
  successMessage = '';

  // Todos los roles disponibles
  allRoles = [
    { value: 'ADMIN', label: 'Administrador' },
    { value: 'EMPLOYEE', label: 'Empleado' },
    { value: 'PATIENT', label: 'Paciente' }
  ];

  // Roles filtrados según el usuario actual
  roles: { value: string; label: string }[] = [];
  
  // Rol del usuario actual
  currentUserRole: string = '';

  constructor(
    private usuarioService: UsuarioService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Obtener rol del usuario actual y filtrar roles disponibles
    this.authService.getUserRole().subscribe(role => {
      this.currentUserRole = role || '';
      
      // Filtrar roles según el rol del usuario actual
      if (this.currentUserRole === 'ADMIN') {
        // ADMIN puede crear cualquier tipo de usuario
        this.roles = [...this.allRoles];
      } else if (this.currentUserRole === 'EMPLOYEE') {
        // EMPLOYEE solo puede crear PATIENT
        this.roles = this.allRoles.filter(r => r.value === 'PATIENT');
      } else {
        // Otros roles no pueden crear usuarios
        this.roles = [];
      }
    });

    const id = this.route.snapshot.params['id'];
    if (id) {
      this.isEditMode = true;
      this.cargarUsuario(parseInt(id));
    }
  }

  cargarUsuario(id: number): void {
    this.usuarioService.getUsuario(id).subscribe({
      next: (usuario) => {
        this.usuario = { ...usuario };
        // Reconstruir nombres y apellidos para el formulario
        this.nombres = [usuario.pnombre, usuario.snombre].filter(n => n).join(' ');
        this.apellidos = [usuario.papellido, usuario.sapellido].filter(a => a).join(' ');
      },
      error: (error) => {
        this.errorMessage = 'Usuario no encontrado: ' + error.message;
      }
    });
  }

  onSubmit(): void {
    if (!this.nombres?.trim() || !this.apellidos?.trim() || !this.usuario.username) {
      this.errorMessage = 'Por favor complete los campos obligatorios (Nombres, Apellidos y Email)';
      return;
    }

    if (!this.isEditMode && !this.usuario.password) {
      this.errorMessage = 'La contraseña es obligatoria para nuevos usuarios';
      return;
    }

    // Parsear nombres y apellidos
    const nombresArray = this.nombres.trim().split(/\s+/);
    const apellidosArray = this.apellidos.trim().split(/\s+/);

    // Asignar primer y segundo nombre
    this.usuario.pnombre = nombresArray[0] || '';
    this.usuario.snombre = nombresArray.length > 1 ? nombresArray.slice(1).join(' ') : '';

    // Asignar primer y segundo apellido
    this.usuario.papellido = apellidosArray[0] || '';
    this.usuario.sapellido = apellidosArray.length > 1 ? apellidosArray.slice(1).join(' ') : '';

    this.loading = true;
    this.errorMessage = '';

    if (this.isEditMode) {
      // Actualizar usuario existente
      const updates: Partial<Usuario> = {
        pnombre: this.usuario.pnombre,
        snombre: this.usuario.snombre,
        papellido: this.usuario.papellido,
        sapellido: this.usuario.sapellido,
        telefono: this.usuario.telefono,
        direccion: this.usuario.direccion,
        activo: this.usuario.activo
      };

      this.usuarioService.actualizarUsuario(this.usuario.id!, updates).subscribe({
        next: () => {
          this.successMessage = 'Usuario actualizado exitosamente';
          this.loading = false;
          setTimeout(() => {
            this.router.navigate(['/usuarios']);
          }, 1000);
        },
        error: (error) => {
          this.errorMessage = 'Error al actualizar usuario: ' + error.message;
          this.loading = false;
        }
      });
    } else {
      // Crear nuevo usuario usando el endpoint de registro
      this.usuarioService.registrarUsuario(this.usuario).subscribe({
        next: () => {
          this.successMessage = 'Usuario creado exitosamente';
          this.loading = false;
          setTimeout(() => {
            // ADMIN va al listado de usuarios, EMPLOYEE va a lista de pacientes
            if (this.currentUserRole === 'ADMIN') {
              this.router.navigate(['/usuarios']);
            } else {
              this.router.navigate(['/pacientes']);
            }
          }, 1000);
        },
        error: (error) => {
          if (error.status === 403) {
            const rolSeleccionado = this.allRoles.find(r => r.value === this.usuario.rol)?.label || this.usuario.rol;
            if (this.currentUserRole === 'EMPLOYEE') {
              this.errorMessage = `Como Empleado, solo puede crear usuarios con rol Paciente. No puede crear usuarios ${rolSeleccionado}.`;
            } else {
              this.errorMessage = 'No tiene permisos para crear este tipo de usuario.';
            }
          } else {
            this.errorMessage = 'Error al crear usuario: ' + (error.error?.description || error.message);
          }
          this.loading = false;
        }
      });
    }
  }

  cancelar(): void {
    // ADMIN va al listado de usuarios, EMPLOYEE va a lista de pacientes
    if (this.currentUserRole === 'ADMIN') {
      this.router.navigate(['/usuarios']);
    } else {
      this.router.navigate(['/pacientes']);
    }
  }
}
