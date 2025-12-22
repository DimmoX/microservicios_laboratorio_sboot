// Modelo de Usuario con roles

export interface Usuario {
  id: number;
  nombre?: string; // Nombre completo (calculado)
  pnombre?: string; // Primer nombre
  snombre?: string; // Segundo nombre
  papellido?: string; // Primer apellido
  sapellido?: string; // Segundo apellido
  username: string;
  password: string;
  rol: 'ADMIN' | 'EMPLOYEE' | 'LAB_EMPLOYEE' | 'PATIENT' | string;
  telefono?: string;
  direccion?: string;
  fechaRegistro?: Date;
  activo: boolean;
  rut?: string;
  cargo?: string;
  pacienteId?: number;
  empleadoId?: number;
  contactoId?: number;
  dirId?: number;
}

// Interface para Login
export interface LoginRequest {
  username: string;
  password: string;
}

// Interface para Registro
export interface RegisterRequest {
  nombre: string;
  username: string;
  password: string;
  telefono?: string;
  direccion?: string;
}

// Interface para la sesión actual
export interface SesionActual {
  usuario: Usuario;
  token: string; // Token simulado
  fechaLogin: Date;
}
