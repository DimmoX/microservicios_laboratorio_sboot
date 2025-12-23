import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PacienteService } from './paciente.service';
import { Paciente, RegistroPacienteRequest } from '../models/paciente.model';

describe('PacienteService', () => {
  let service: PacienteService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PacienteService]
    });
    service = TestBed.inject(PacienteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get paciente by ID', () => {
    const mockPaciente: Paciente = {
      id: 1,
      pnombre: 'Juan',
      papellido: 'Perez',
      sapellido: 'Gomez',
      rut: '12345678-9',
      telefono: '+56912345678',
      email: 'juan@test.com'
    };

    service.getPaciente(1).subscribe(paciente => {
      expect(paciente).toEqual(mockPaciente);
    });

    const req = httpMock.expectOne(request => request.url.includes('/pacientes/1'));
    expect(req.request.method).toBe('GET');
    req.flush({ code: '200', description: 'OK', data: mockPaciente });
  });

  it('should register new paciente', () => {
    const newPaciente: RegistroPacienteRequest = {
      pnombre: 'Juan',
      papellido: 'Perez',
      sapellido: 'Gomez',
      rut: '12345678-9',
      password: 'Password123',
      contacto: {
        fono1: '+56912345678',
        email: 'juan@test.com'
      },
      direccion: {
        calle: 'Calle Principal',
        numero: '123',
        ciudad: 'Santiago',
        region: 'Metropolitana',
        pais: 'Chile'
      }
    };

    service.registrarPaciente(newPaciente).subscribe();

    const req = httpMock.expectOne(request => request.url.includes('/registro/paciente'));
    expect(req.request.method).toBe('POST');
    req.flush({ code: '201', description: 'Created', data: {} });
  });

  it('should update paciente', () => {
    const updated: Partial<Paciente> = { telefono: '+56987654321' };

    service.actualizarPaciente(1, updated).subscribe();

    const req = httpMock.expectOne(request => request.url.includes('/pacientes/1'));
    expect(req.request.method).toBe('PUT');
    req.flush({ code: '200', description: 'OK', data: updated });
  });

  it('should delete paciente', () => {
    service.eliminarPaciente(1).subscribe();

    const req = httpMock.expectOne(request => request.url.includes('/pacientes/1'));
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
