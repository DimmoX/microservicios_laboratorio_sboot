export interface Paciente {
  id?: number;
  pnombre: string;
  snombre?: string;
  papellido: string;
  sapellido?: string;
  rut: string;
  contactoId?: number;
  dirId?: number;
  // Datos adicionales del perfil completo
  nombre?: string; // Nombre completo formateado
  email?: string;
  telefono?: string;
}

export interface RegistroPacienteRequest {
  pnombre: string;
  snombre?: string;
  papellido: string;
  sapellido?: string;
  rut: string;
  password: string;
  contacto: {
    fono1: string;
    fono2?: string;
    email: string;
  };
  direccion: {
    calle: string;
    numero: string;
    ciudad: string;
    region: string;
    pais: string;
  };
}
