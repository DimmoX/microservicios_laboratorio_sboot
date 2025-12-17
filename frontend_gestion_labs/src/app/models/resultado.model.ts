// Modelo: Resultado de Examen
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
  pacienteNombre?: string;
  examenNombre?: string;
}
