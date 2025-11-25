
/**
 * Modelo: LabExam
 * ---------------
 * Representa la relación entre laboratorio y examen, incluyendo precios y vigencia.
 *
 * Ejemplo de uso:
 * const labExam: LabExam = { idLaboratorio: 1, idExamen: 2, precio: 15000 };
 *
 * Propiedades:
 * ------------
 * - id?: number
 *      Identificador único de la relación.
 * - idLaboratorio: number
 *      ID del laboratorio.
 * - idExamen: number
 *      ID del examen.
 * - precio: number
 *      Precio del examen en el laboratorio.
 * - vigenteDesde?: string
 *      Fecha de inicio de vigencia.
 * - vigenteHasta?: string
 *      Fecha de fin de vigencia.
 * - nombreLab?: string
 *      Nombre del laboratorio.
 * - nombreExamen?: string
 *      Nombre del examen.
 */
export interface LabExam {
  id?: number;
  idLaboratorio: number;
  idExamen: number;
  precio: number;
  vigenteDesde?: string;
  vigenteHasta?: string;
  nombreLab?: string;
  nombreExamen?: string;
}
