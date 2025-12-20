export interface CitaAgendada {
  id: number;
  pacienteId: number;
  pacienteNombre?: string;
  labId: number;
  laboratorioNombre: string;
  examenId: number;
  examenNombre: string;
  empleadoId?: number;
  fechaHora: string;
  estado: 'PROGRAMADA' | 'CANCELADA' | 'ATENDIDA';
  precio?: number;
}

export interface CrearCitaRequest {
  pacienteId: number;
  labId: number;
  examenId: number;
  fechaHora: string;
}
