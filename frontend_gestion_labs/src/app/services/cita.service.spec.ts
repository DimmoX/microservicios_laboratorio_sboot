import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CitaService } from './cita.service';
import { environment } from '../../environments/environment';

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

  it('should get citas', () => {
    const mockCitas = [{ id: 1, fecha: '2024-01-01' }];
    
    service.getCitas().subscribe(citas => {
      expect(citas).toEqual(mockCitas);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/citas`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: mockCitas });
  });

  it('should create cita', () => {
    const newCita = { fecha: '2024-01-01', pacienteId: 1 };
    
    service.createCita(newCita).subscribe(cita => {
      expect(cita).toEqual(newCita);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/citas`);
    expect(req.request.method).toBe('POST');
    req.flush({ data: newCita });
  });

  it('should update cita', () => {
    const updatedCita = { id: 1, fecha: '2024-01-02' };
    
    service.updateCita(1, updatedCita).subscribe(cita => {
      expect(cita).toEqual(updatedCita);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/citas/1`);
    expect(req.request.method).toBe('PUT');
    req.flush({ data: updatedCita });
  });

  it('should delete cita', () => {
    service.deleteCita(1).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/citas/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });
});
