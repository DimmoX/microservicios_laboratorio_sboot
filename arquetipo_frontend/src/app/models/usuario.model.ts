
/**
 * Modelo: Usuario
 * ---------------
 * Representa la información básica de un usuario en la aplicación.
 *
 * Ejemplo de uso:
 * const usuario: Usuario = { id: 1, nombre: 'Juan', email: 'juan@mail.com' };
 *
 * Propiedades:
 * ------------
 * - id: number
 *      Identificador único del usuario.
 * - nombre: string
 *      Nombre completo del usuario.
 * - email: string
 *      Correo electrónico del usuario.
 */
export interface Usuario {
  /** Identificador único del usuario */
  id: number;
  /** Nombre completo del usuario */
  nombre: string;
  /** Correo electrónico del usuario */
  email: string;
}
