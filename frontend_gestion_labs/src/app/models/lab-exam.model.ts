// Modelo: Relación Lab-Examen con precios
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
