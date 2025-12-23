import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PacienteService } from './paciente.service';
import { environment } from '../../environments/environment';

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

  it('should get pacientes', () => {
    const mockPacientes = [{ id: 1, nombre: 'Juan' }];
    
    service.getPacientes().subscribe(pacientes => {
      expect(pacientes.length).toBe(1);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/pacientes`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: mockPacientes });
  });

  it('should get paciente by id', () => {
    const mockPaciente = { id: 1, nombre: 'Juan' };
    
    service.getPacienteById(1).subscribe(paciente => {
      expect(paciente.nombre).toBe('Juan');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/pacientes/1`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: mockPaciente });
  });

  it('should create paciente', () => {
    const newPaciente = { nombre: 'Pedro' };
    
    service.createPaciente(newPaciente).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/pacientes`);
    expect(req.request.method).toBe('POST');
    req.flush({ data: newPaciente });
  });

  it('should update paciente', () => {
    const updated = { id: 1, nombre: 'Pedro Updated' };
    
    service.updatePaciente(1, updated).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/pacientes/1`);
    expect(req.request.method).toBe('PUT');
    req.flush({ data: updated });
  });

  it('should delete paciente', () => {
    service.deletePaciente(1).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/pacientes/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });
});
