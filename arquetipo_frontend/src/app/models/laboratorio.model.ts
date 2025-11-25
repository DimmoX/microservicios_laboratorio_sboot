
/**
 * Modelo: Laboratorio
 * -------------------
 * Representa la información de un laboratorio, incluyendo dirección y contacto.
 *
 * Ejemplo de uso:
 * const lab: Laboratorio = { nombre: 'Lab 1', tipo: 'Clínico' };
 *
 * Propiedades:
 * ------------
 * - id?: number
 *      Identificador único del laboratorio.
 * - nombre: string
 *      Nombre del laboratorio.
 * - tipo: string
 *      Tipo de laboratorio.
 * - direccion?: Direccion
 *      Dirección del laboratorio.
 * - contacto?: Contacto
 *      Información de contacto.
 */
export interface Laboratorio {
  id?: number;
  nombre: string;
  tipo: string;
  direccion?: Direccion;
  contacto?: Contacto;
}

/**
 * Modelo: Direccion
 * -----------------
 * Representa la dirección física de un laboratorio.
 */
export interface Direccion {
  id?: number;
  calle: string;
  numero: number;
  ciudad: string;
  comuna: string;
  region: string;
}

/**
 * Modelo: Contacto
 * ----------------
 * Representa la información de contacto de un laboratorio.
 */
export interface Contacto {
  id?: number;
  fono1: string;
  fono2?: string;
  email: string;
}
