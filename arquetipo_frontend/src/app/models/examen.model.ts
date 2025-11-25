

/**
 * Modelo: Examen
 * --------------
 * Representa la información básica de un examen de laboratorio.
 *
 * Ejemplo de uso:
 * const examen: Examen = { codigo: 'GLU', nombre: 'Glucosa', tipo: 'Sangre' };
 *
 * Propiedades:
 * ------------
 * - id?: number
 *      Identificador único del examen.
 * - codigo: string
 *      Código del examen.
 * - nombre: string
 *      Nombre del examen.
 * - tipo: string
 *      Tipo de muestra del examen.
 */
export interface Examen {
  /** Identificador único del examen */
  id?: number;
  /** Código del examen */
  codigo: string;
  /** Nombre del examen */
  nombre: string;
  /** Tipo de muestra */
  tipo: string;
}
