
/**
 * Modelo: ResultadoExamen
 * -----------------------
 * Representa el resultado de un examen realizado en un laboratorio.
 *
 * Ejemplo de uso:
 * const resultado: ResultadoExamen = { agendaId: 1, pacienteId: 2, labId: 3, examenId: 4, estado: 'Completado' };
 *
 * Propiedades:
 * ------------
 * - id?: number
 *      Identificador único del resultado.
 * - agendaId: number
 *      ID de la agenda asociada.
 * - pacienteId: number
 *      ID del paciente.
 * - labId: number
 *      ID del laboratorio.
 * - examenId: number
 *      ID del examen.
 * - empleadoId?: number
 *      ID del empleado que realizó el examen.
 * - fechaMuestra?: string
 *      Fecha de toma de muestra.
 * - fechaResultado?: string
 *      Fecha de entrega del resultado.
 * - valor?: string
 *      Valor obtenido en el examen.
 * - unidad?: string
 *      Unidad de medida del valor.
 * - observacion?: string
 *      Observaciones adicionales.
 * - estado: string
 *      Estado del resultado (Completado, Pendiente, etc).
 */
export interface ResultadoExamen {
  id?: number;
  agendaId: number;
  pacienteId: number;
  labId: number;
  examenId: number;
  empleadoId?: number;
  fechaMuestra?: string;
  fechaResultado?: string;
  valor?: string;
  unidad?: string;
  observacion?: string;
  estado: string;
}
