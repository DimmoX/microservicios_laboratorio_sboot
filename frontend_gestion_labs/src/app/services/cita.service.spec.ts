import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CitaService } from './cita.service';
import { CitaAgendada, CrearCitaRequest } from '../models/cita.model';

describe('CitaService', () => {
  let service: CitaService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CitaService]
    });
    service = TestBed.inject(CitaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all citas', () => {
    const mockCitas: CitaAgendada[] = [
      { id: 1, pacienteId: 1, labId: 1, laboratorioNombre: 'Lab 1', examenId: 1, examenNombre: 'Examen 1', fechaHora: '2025-12-25T10:00:00', estado: 'PROGRAMADA' }
    ];

    service.getAllCitas().subscribe(citas => {
      expect(citas).toEqual(mockCitas);
    });

    const req = httpMock.expectOne(request => request.url.includes('/agenda'));
    expect(req.request.method).toBe('GET');
    req.flush({ code: '200', description: 'OK', data: mockCitas });
  });

  it('should create cita', () => {
    const newCita: CrearCitaRequest = {
      pacienteId: 1,
      labId: 1,
      examenId: 1,
      fechaHora: '2025-12-25T10:00:00'
    };

    service.crearCita(newCita).subscribe();

    const req = httpMock.expectOne(request => request.url.includes('/agenda'));
    expect(req.request.method).toBe('POST');
    req.flush({ code: '201', description: 'Created', data: { ...newCita, id: 1, estado: 'PROGRAMADA' } });
  });

  it('should cancel cita', () => {
    service.cancelarCita(1).subscribe();

    const req = httpMock.expectOne(request => request.url.includes('/agenda/1/cancelar'));
    expect(req.request.method).toBe('PUT');
    req.flush({ code: '200', description: 'OK', data: {} });
  });

  it('should delete cita', () => {
    service.eliminarCita(1).subscribe();

    const req = httpMock.expectOne(request => request.url.includes('/agenda/1'));
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
