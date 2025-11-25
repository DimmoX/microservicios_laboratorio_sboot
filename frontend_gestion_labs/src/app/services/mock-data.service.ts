// Service: Mock Data - Simula datos del backend usando localStorage
// Este servicio inicializa y gestiona datos simulados para desarrollo sin backend

import { Injectable } from '@angular/core';
import { Usuario } from '../models/usuario.model';
import { Laboratorio, Direccion, Contacto } from '../models/laboratorio.model';
import { Examen } from '../models/examen.model';
import { ResultadoExamen } from '../models/resultado.model';

export interface MockDatabase {
  users: Usuario[];
  laboratorios: Laboratorio[];
  examenes: Examen[];
  resultados: ResultadoExamen[];
  labExams: any[];
  nextUserId: number;
  nextLabId: number;
  nextExamId: number;
  nextResultId: number;
  nextDirId: number;
  nextContactoId: number;
}

@Injectable({
  providedIn: 'root'
})
export class MockDataService {
  private readonly STORAGE_KEY = 'mock_lab_database';

  constructor() {
    this.initializeData();
  }

  /**
   * Inicializa datos de prueba si no existen en localStorage
   */
  private initializeData(): void {
    const existing = localStorage.getItem(this.STORAGE_KEY);
    if (!existing) {
      const initialData = this.createInitialData();
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(initialData));
    }
  }

  /**
   * Crea datos iniciales simulando la estructura del backend
   */
  private createInitialData(): MockDatabase {
    // USUARIOS DE PRUEBA
    const users: Usuario[] = [
      {
        id: 1,
        nombre: 'Felipe Muñoz',
        username: 'felipe.munoz@laboratorioandino.cl',
        password: '12345',
        rol: 'LAB_EMPLOYEE',
        telefono: '+56911111111',
        direccion: 'Providencia',
        fechaRegistro: new Date('2024-01-15'),
        activo: true,
        rut: '11111111-1',
        empleadoId: 1
      },
      {
        id: 2,
        nombre: 'Constanza Araya',
        username: 'constanza.araya@clinicabiosalud.cl',
        password: '12345',
        rol: 'LAB_EMPLOYEE',
        telefono: '+56922222222',
        direccion: 'Las Condes',
        fechaRegistro: new Date('2024-01-16'),
        activo: true,
        rut: '22222222-2',
        empleadoId: 2
      },
      {
        id: 3,
        nombre: 'Matias Carrasco',
        username: 'matias.carrasco@centrodiagnosticopacifico.cl',
        password: '12345',
        rol: 'LAB_EMPLOYEE',
        telefono: '+56933333333',
        direccion: 'Ñuñoa',
        fechaRegistro: new Date('2024-01-17'),
        activo: true,
        rut: '33333333-3',
        empleadoId: 3
      },
      {
        id: 4,
        nombre: 'Camila Rojas',
        username: 'camila.rojas@correo.cl',
        password: '12345',
        rol: 'PATIENT',
        telefono: '+56944444444',
        direccion: 'Peñalolén',
        fechaRegistro: new Date('2024-01-18'),
        activo: true,
        rut: '44444444-4',
        pacienteId: 1
      },
      {
        id: 5,
        nombre: 'Benjamín González',
        username: 'benjamin.gonzalez@correo.cl',
        password: '12345',
        rol: 'PATIENT',
        telefono: '+56955555555',
        direccion: 'La Florida',
        fechaRegistro: new Date('2024-01-19'),
        activo: true,
        rut: '55555555-5',
        pacienteId: 2
      },
      {
        id: 6,
        nombre: 'Isidora Muñoz',
        username: 'isidora.munoz@correo.cl',
        password: '12345',
        rol: 'PATIENT',
        telefono: '+56966666666',
        direccion: 'Macul',
        fechaRegistro: new Date('2024-01-20'),
        activo: true,
        rut: '66666666-6',
        pacienteId: 3
      },
      {
        id: 7,
        nombre: 'Administrador Sistema',
        username: 'admin@laboratorioandino.cl',
        password: 'admin123',
        rol: 'ADMIN',
        telefono: '+56977777777',
        direccion: 'Santiago Centro',
        fechaRegistro: new Date('2024-01-21'),
        activo: true,
        rut: '77777777-7'
      }
    ];

    // LABORATORIOS
    const laboratorios: Laboratorio[] = [
      {
        id: 1,
        nombre: 'Laboratorio Clínico Andino - Central',
        tipo: 'CLINICO',
        direccion: {
          id: 1,
          calle: 'Av. Providencia',
          numero: 1234,
          ciudad: 'Santiago',
          comuna: 'Providencia',
          region: 'Metropolitana'
        },
        contacto: {
          id: 1,
          fono1: '+56223456789',
          fono2: '+56223456790',
          email: 'central@laboratorioandino.cl'
        }
      },
      {
        id: 2,
        nombre: 'Laboratorio Clínico Andino - Maipú',
        tipo: 'CLINICO',
        direccion: {
          id: 2,
          calle: 'Av. Pajaritos',
          numero: 567,
          ciudad: 'Santiago',
          comuna: 'Maipú',
          region: 'Metropolitana'
        },
        contacto: {
          id: 2,
          fono1: '+56227891234',
          email: 'maipu@laboratorioandino.cl'
        }
      },
      {
        id: 3,
        nombre: 'Laboratorio Clínico Andino - Las Condes',
        tipo: 'ESPECIALIZADO',
        direccion: {
          id: 3,
          calle: 'Av. Apoquindo',
          numero: 4567,
          ciudad: 'Santiago',
          comuna: 'Las Condes',
          region: 'Metropolitana'
        },
        contacto: {
          id: 3,
          fono1: '+56223334444',
          email: 'lascondes@laboratorioandino.cl'
        }
      }
    ];

    // EXÁMENES
    const examenes: Examen[] = [
      {
        id: 1,
        codigo: 'HEM001',
        nombre: 'Hemograma Completo',
        tipo: 'HEMATOLOGIA'
      },
      {
        id: 2,
        codigo: 'GLU001',
        nombre: 'Glicemia en Ayunas',
        tipo: 'BIOQUIMICA'
      },
      {
        id: 3,
        codigo: 'COL001',
        nombre: 'Perfil Lipídico',
        tipo: 'BIOQUIMICA'
      },
      {
        id: 4,
        codigo: 'TSH001',
        nombre: 'TSH (Hormona Tiroestimulante)',
        tipo: 'ENDOCRINOLOGIA'
      },
      {
        id: 5,
        codigo: 'ORI001',
        nombre: 'Orina Completa',
        tipo: 'UROLOGIA'
      },
      {
        id: 6,
        codigo: 'HBA001',
        nombre: 'Hemoglobina Glicosilada (HbA1c)',
        tipo: 'BIOQUIMICA'
      }
    ];

    // RELACIÓN LABORATORIO-EXAMEN (LabExam)
    const labExams = [
      { id: 1, labId: 1, examenId: 1, precio: 8500, disponible: true },
      { id: 2, labId: 1, examenId: 2, precio: 3500, disponible: true },
      { id: 3, labId: 1, examenId: 3, precio: 12000, disponible: true },
      { id: 4, labId: 1, examenId: 4, precio: 15000, disponible: true },
      { id: 5, labId: 1, examenId: 5, precio: 5000, disponible: true },
      { id: 6, labId: 1, examenId: 6, precio: 18000, disponible: true },

      { id: 7, labId: 2, examenId: 1, precio: 8000, disponible: true },
      { id: 8, labId: 2, examenId: 2, precio: 3200, disponible: true },
      { id: 9, labId: 2, examenId: 5, precio: 4800, disponible: true },

      { id: 10, labId: 3, examenId: 3, precio: 13000, disponible: true },
      { id: 11, labId: 3, examenId: 4, precio: 16000, disponible: true },
      { id: 12, labId: 3, examenId: 6, precio: 19000, disponible: true }
    ];

    // RESULTADOS DE EXÁMENES
    const resultados: ResultadoExamen[] = [
      {
        id: 1,
        agendaId: 1,
        pacienteId: 1,
        labId: 1,
        examenId: 2,
        empleadoId: 1,
        fechaMuestra: '2025-01-15',
        fechaResultado: '2025-01-16',
        valor: '95',
        unidad: 'mg/dL',
        observacion: 'Glicemia en rango normal',
        estado: 'COMPLETADO'
      },
      {
        id: 2,
        agendaId: 2,
        pacienteId: 1,
        labId: 1,
        examenId: 1,
        empleadoId: 1,
        fechaMuestra: '2025-01-20',
        fechaResultado: '2025-01-21',
        valor: '14.5',
        unidad: 'g/dL',
        observacion: 'Hemoglobina normal',
        estado: 'COMPLETADO'
      },
      {
        id: 3,
        agendaId: 3,
        pacienteId: 2,
        labId: 2,
        examenId: 2,
        empleadoId: 1,
        fechaMuestra: '2025-01-18',
        fechaResultado: '2025-01-19',
        valor: '110',
        unidad: 'mg/dL',
        observacion: 'Glicemia levemente elevada',
        estado: 'COMPLETADO'
      },
      {
        id: 4,
        agendaId: 4,
        pacienteId: 1,
        labId: 1,
        examenId: 3,
        fechaMuestra: '2025-01-22',
        estado: 'PENDIENTE'
      }
    ];

    return {
      users,
      laboratorios,
      examenes,
      resultados,
      labExams,
      nextUserId: 5,
      nextLabId: 4,
      nextExamId: 7,
      nextResultId: 5,
      nextDirId: 4,
      nextContactoId: 4
    };
  }

  /**
   * Obtiene la base de datos completa
   */
  getDatabase(): MockDatabase {
    const data = localStorage.getItem(this.STORAGE_KEY);
    return data ? JSON.parse(data) : this.createInitialData();
  }

  /**
   * Guarda la base de datos completa
   */
  saveDatabase(db: MockDatabase): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(db));
  }

  /**
   * Resetea la base de datos a valores iniciales
   */
  resetDatabase(): void {
    const initialData = this.createInitialData();
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(initialData));
  }

  /**
   * Obtiene todos los usuarios
   */
  getUsers(): Usuario[] {
    return this.getDatabase().users;
  }

  /**
   * Encuentra usuario por username
   */
  findUserByUsername(username: string): Usuario | undefined {
    return this.getUsers().find(u => u.username === username);
  }

  /**
   * Valida credenciales de usuario
   */
  validateCredentials(username: string, password: string): Usuario | null {
    const user = this.findUserByUsername(username);
    if (user && user.password === password && user.activo) {
      return user;
    }
    return null;
  }

  /**
   * Obtiene todos los laboratorios
   */
  getLaboratorios(): Laboratorio[] {
    return this.getDatabase().laboratorios;
  }

  /**
   * Guarda un laboratorio (crear o actualizar)
   */
  saveLaboratorio(lab: Laboratorio): Laboratorio {
    const db = this.getDatabase();

    if (lab.id) {
      // Actualizar
      const index = db.laboratorios.findIndex(l => l.id === lab.id);
      if (index >= 0) {
        db.laboratorios[index] = lab;
      }
    } else {
      // Crear nuevo
      lab.id = db.nextLabId++;
      if (lab.direccion && !lab.direccion.id) {
        lab.direccion.id = db.nextDirId++;
      }
      if (lab.contacto && !lab.contacto.id) {
        lab.contacto.id = db.nextContactoId++;
      }
      db.laboratorios.push(lab);
    }

    this.saveDatabase(db);
    return lab;
  }

  /**
   * Elimina un laboratorio
   */
  deleteLaboratorio(id: number): boolean {
    const db = this.getDatabase();
    const index = db.laboratorios.findIndex(l => l.id === id);
    if (index >= 0) {
      db.laboratorios.splice(index, 1);
      this.saveDatabase(db);
      return true;
    }
    return false;
  }

  /**
   * Obtiene todos los exámenes
   */
  getExamenes(): Examen[] {
    return this.getDatabase().examenes;
  }

  /**
   * Guarda un examen (crear o actualizar)
   */
  saveExamen(examen: Examen): Examen {
    const db = this.getDatabase();

    if (examen.id) {
      // Actualizar
      const index = db.examenes.findIndex(e => e.id === examen.id);
      if (index >= 0) {
        db.examenes[index] = examen;
      }
    } else {
      // Crear nuevo
      examen.id = db.nextExamId++;
      db.examenes.push(examen);
    }

    this.saveDatabase(db);
    return examen;
  }

  /**
   * Elimina un examen
   */
  deleteExamen(id: number): boolean {
    const db = this.getDatabase();
    const index = db.examenes.findIndex(e => e.id === id);
    if (index >= 0) {
      db.examenes.splice(index, 1);
      this.saveDatabase(db);
      return true;
    }
    return false;
  }

  /**
   * Obtiene relaciones laboratorio-examen (precios)
   */
  getLabExams(): any[] {
    return this.getDatabase().labExams;
  }

  /**
   * Obtiene todos los resultados
   */
  getResultados(): ResultadoExamen[] {
    return this.getDatabase().resultados;
  }

  /**
   * Obtiene resultados por paciente
   */
  getResultadosByPaciente(pacienteId: number): ResultadoExamen[] {
    return this.getResultados().filter(r => r.pacienteId === pacienteId);
  }

  /**
   * Guarda un resultado (crear o actualizar)
   */
  saveResultado(resultado: ResultadoExamen): ResultadoExamen {
    const db = this.getDatabase();

    if (resultado.id) {
      // Actualizar
      const index = db.resultados.findIndex(r => r.id === resultado.id);
      if (index >= 0) {
        db.resultados[index] = resultado;
      }
    } else {
      // Crear nuevo
      resultado.id = db.nextResultId++;
      db.resultados.push(resultado);
    }

    this.saveDatabase(db);
    return resultado;
  }

  /**
   * Elimina un resultado
   */
  deleteResultado(id: number): boolean {
    const db = this.getDatabase();
    const index = db.resultados.findIndex(r => r.id === id);
    if (index >= 0) {
      db.resultados.splice(index, 1);
      this.saveDatabase(db);
      return true;
    }
    return false;
  }

  /**
   * Agrega un nuevo usuario (registro)
   */
  addUser(user: Usuario): Usuario {
    const db = this.getDatabase();
    user.id = db.nextUserId++;
    user.activo = true;
    user.fechaRegistro = new Date();
    db.users.push(user);
    this.saveDatabase(db);
    return user;
  }

  /**
   * Actualiza contraseña de usuario
   */
  updatePassword(userId: number, oldPassword: string, newPassword: string): boolean {
    const db = this.getDatabase();
    const user = db.users.find(u => u.id === userId);

    if (user && user.password === oldPassword) {
      user.password = newPassword;
      this.saveDatabase(db);
      return true;
    }
    return false;
  }

  /**
   * Actualiza perfil de usuario
   */
  updateUser(userId: number, updates: Partial<Usuario>): Usuario | null {
    const db = this.getDatabase();
    const index = db.users.findIndex(u => u.id === userId);

    if (index >= 0) {
      db.users[index] = { ...db.users[index], ...updates };
      this.saveDatabase(db);
      return db.users[index];
    }
    return null;
  }
}
