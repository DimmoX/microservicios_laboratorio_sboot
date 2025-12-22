// Clave compuesta para LabExam
export interface LabExamKey {
  idLaboratorio: number;
  idExamen: number;
}

// Modelo: Relación Lab-Examen con precios
export interface LabExam {
  id?: LabExamKey;  // Clave embebida que viene del backend
  idLaboratorio?: number;  // Para compatibilidad al crear/actualizar
  idExamen?: number;  // Para compatibilidad al crear/actualizar
  precio: number;
  vigenteDesde?: string;
  vigenteHasta?: string;
  nombreLab?: string;
  nombreExamen?: string;
}
