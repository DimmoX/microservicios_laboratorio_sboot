// Mock Data - Datos estáticos para la aplicación
import { Laboratorio } from '../models/laboratorio.model';
import { Examen } from '../models/examen.model';
import { LabExam } from '../models/lab-exam.model';
import { ResultadoExamen } from '../models/resultado.model';
import { Usuario } from '../models/usuario.model';

// Usuarios predefinidos (simulando base de datos)
export const USUARIOS_MOCK: Usuario[] = [
  {
    id: 1,
    nombre: 'Admin Sistema',
    email: 'admin@laboratorio.cl',
    password: '123456', // En producción debería estar encriptado
    rol: 'ADMIN',
    telefono: '+56 9 8765 4321',
    direccion: 'Av. Principal 123, Santiago',
    fechaRegistro: new Date('2024-01-15'),
    activo: true
  },
  {
    id: 2,
    nombre: 'Juan Pérez',
    email: 'juan.perez@gmail.com',
    password: '123456',
    rol: 'USUARIO',
    telefono: '+56 9 1234 5678',
    direccion: 'Calle Los Robles 456, Providencia',
    fechaRegistro: new Date('2024-06-20'),
    activo: true
  },
  {
    id: 3,
    nombre: 'María González',
    email: 'maria.gonzalez@gmail.com',
    password: '123456',
    rol: 'USUARIO',
    telefono: '+56 9 8888 7777',
    direccion: 'Pasaje Las Flores 789, Las Condes',
    fechaRegistro: new Date('2024-08-10'),
    activo: true
  }
];

// Laboratorios mock
export const LABORATORIOS_MOCK: Laboratorio[] = [
  {
    id: 1,
    nombre: 'Laboratorio Clínico Andino',
    tipo: 'Privado',
    direccion: {
      id: 1,
      calle: 'Av. Libertador Bernardo O\'Higgins',
      numero: 1234,
      comuna: 'Santiago Centro',
      ciudad: 'Santiago',
      region: 'Metropolitana'
    },
    contacto: {
      id: 1,
      fono1: '+56 2 2345 6789',
      email: 'contacto@labandino.cl'
    }
  },
  {
    id: 2,
    nombre: 'Centro Médico Salud Total',
    tipo: 'Público',
    direccion: {
      id: 2,
      calle: 'Av. Providencia',
      numero: 2500,
      comuna: 'Providencia',
      ciudad: 'Santiago',
      region: 'Metropolitana'
    },
    contacto: {
      id: 2,
      fono1: '+56 2 3456 7890',
      email: 'info@saludtotal.cl'
    }
  },
  {
    id: 3,
    nombre: 'Laboratorio Clínico del Norte',
    tipo: 'Privado',
    direccion: {
      id: 3,
      calle: 'Calle 21 de Mayo',
      numero: 567,
      comuna: 'Iquique',
      ciudad: 'Iquique',
      region: 'Tarapacá'
    },
    contacto: {
      id: 3,
      fono1: '+56 57 234 5678',
      email: 'contacto@labnorte.cl'
    }
  }
];

// Exámenes mock
export const EXAMENES_MOCK: Examen[] = [
  {
    id: 1,
    codigo: 'HEMO-001',
    nombre: 'Hemograma Completo',
    tipo: 'Sangre'
  },
  {
    id: 2,
    codigo: 'GLUC-002',
    nombre: 'Glicemia en Ayunas',
    tipo: 'Sangre'
  },
  {
    id: 3,
    codigo: 'ORIN-003',
    nombre: 'Orina Completa',
    tipo: 'Orina'
  },
  {
    id: 4,
    codigo: 'COLE-004',
    nombre: 'Perfil Lipídico',
    tipo: 'Sangre'
  },
  {
    id: 5,
    codigo: 'TIRO-005',
    nombre: 'Perfil Tiroideo',
    tipo: 'Sangre'
  }
];

// Relación Laboratorio-Examen con precios
export const LAB_EXAM_MOCK: LabExam[] = [
  { id: 1, idLaboratorio: 1, idExamen: 1, precio: 15000, vigenteDesde: '2024-01-01', nombreLab: 'Laboratorio Clínico Andino', nombreExamen: 'Hemograma Completo' },
  { id: 2, idLaboratorio: 1, idExamen: 2, precio: 8000, vigenteDesde: '2024-01-01', nombreLab: 'Laboratorio Clínico Andino', nombreExamen: 'Glicemia en Ayunas' },
  { id: 3, idLaboratorio: 1, idExamen: 3, precio: 10000, vigenteDesde: '2024-01-01', nombreLab: 'Laboratorio Clínico Andino', nombreExamen: 'Orina Completa' },
  { id: 4, idLaboratorio: 2, idExamen: 1, precio: 12000, vigenteDesde: '2024-01-01', nombreLab: 'Centro Médico Salud Total', nombreExamen: 'Hemograma Completo' },
  { id: 5, idLaboratorio: 2, idExamen: 4, precio: 18000, vigenteDesde: '2024-01-01', nombreLab: 'Centro Médico Salud Total', nombreExamen: 'Perfil Lipídico' },
  { id: 6, idLaboratorio: 3, idExamen: 5, precio: 25000, vigenteDesde: '2024-01-01', nombreLab: 'Laboratorio Clínico del Norte', nombreExamen: 'Perfil Tiroideo' }
];

// Resultados mock
export const RESULTADOS_MOCK: ResultadoExamen[] = [
  {
    id: 1,
    agendaId: 1,
    pacienteId: 2,
    labId: 1,
    examenId: 1,
    empleadoId: 1,
    fechaMuestra: '2024-11-15',
    fechaResultado: '2024-11-16',
    estado: 'EMITIDO',
    valor: '4.5 millones/mm³',
    unidad: 'células/mm³',
    observacion: 'Valores dentro de rango normal. Hemoglobina: 14.5 g/dL'
  },
  {
    id: 2,
    agendaId: 2,
    pacienteId: 3,
    labId: 2,
    examenId: 2,
    empleadoId: 2,
    fechaMuestra: '2024-11-18',
    fechaResultado: '2024-11-18',
    estado: 'EMITIDO',
    valor: '95 mg/dL',
    unidad: 'mg/dL',
    observacion: 'Glicemia normal. Rango referencia: 70-100 mg/dL'
  }
];
