// Modelo: Entidad Laboratorio
export interface Laboratorio {
  id?: number;
  nombre: string;
  tipo: string;
  direccion?: Direccion;
  contacto?: Contacto;
}

// Modelo: Entidad Dirección
export interface Direccion {
  id?: number;
  calle: string;
  numero: number;
  ciudad: string;
  comuna: string;
  region: string;
}

// Modelo: Entidad Contacto
export interface Contacto {
  id?: number;
  fono1: string;
  fono2?: string;
  email: string;
}
