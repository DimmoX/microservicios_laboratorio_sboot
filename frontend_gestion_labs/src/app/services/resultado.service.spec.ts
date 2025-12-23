import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ResultadoService } from './resultado.service';
import { environment } from '../../environments/environment';

describe('ResultadoService', () => {
  let service: ResultadoService;
  let httpMock: HttpTestingController;

  const mockResultados = [
    {
      id: 1,
      agendaId: 100,
      pacienteId: 10,
      labId: 1,
      examenId: 1,
      empleadoId: 5,
      fechaMuestra: '2024-12-22T10:00:00Z',
      valor: '120',
      unidad: 'mg/dL',
      estado: 'EMITIDO',
      observacion: 'Valores normales'
    },
    {
      id: 2,
      agendaId: 101,
      pacienteId: 10,
      labId: 1,
      examenId: 2,
      empleadoId: 5,
      fechaMuestra: '2024-12-22T11:00:00Z',
      valor: '85',
      unidad: 'mg/dL',
      estado: 'PENDIENTE',
      observacion: undefined
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ResultadoService]
    });
    service = TestBed.inject(ResultadoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all resultados', () => {
    service.getResultados().subscribe(resultados => {
      expect(resultados.length).toBe(2);
      expect(resultados[0].valor).toBe('120');
      expect(resultados[1].estado).toBe('PENDIENTE');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/resultados`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: mockResultados });
  });

  it('should get resultados by paciente id', () => {
    const pacienteId = 10;
    
    service.getResultadosPorPaciente(pacienteId).subscribe((resultados: any) => {
      expect(resultados.length).toBe(2);
      expect(resultados[0].pacienteId).toBe(10);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/resultados/paciente/${pacienteId}`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: mockResultados });
  });

  it('should get resultado by id', () => {
    service.getResultado(1).subscribe(resultado => {
      expect(resultado).toBeTruthy();
      expect(resultado.id).toBe(1);
      expect(resultado.valor).toBe('120');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/resultados/1`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: mockResultados[0] });
  });

  it('should create a new resultado', () => {
    const newResultado = {
      agendaId: 102,
      pacienteId: 11,
      labId: 1,
      examenId: 3,
      empleadoId: 5,
      fechaMuestra: '2024-12-22T12:00:00Z',
      valor: '95',
      unidad: 'mg/dL',
      estado: 'PENDIENTE'
    };

    service.crearResultado(newResultado).subscribe(resultado => {
      expect(resultado).toBeTruthy();
      expect(resultado.valor).toBe('95');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/resultados`);
    expect(req.request.method).toBe('POST');
    req.flush({ data: { ...newResultado, id: 3 } });
  });

  it('should update resultado estado to EMITIDO', () => {
    const updatedResultado = {
      ...mockResultados[1],
      estado: 'EMITIDO',
      fechaResultado: '2024-12-22T14:00:00Z'
    };

    service.actualizarResultado(2, updatedResultado).subscribe(resultado => {
      expect(resultado.estado).toBe('EMITIDO');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/resultados/2`);
    expect(req.request.method).toBe('PUT');
    req.flush({ data: updatedResultado });
  });
});
