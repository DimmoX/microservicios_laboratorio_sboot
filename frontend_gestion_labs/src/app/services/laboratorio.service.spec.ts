import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { LaboratorioService } from './laboratorio.service';
import { Laboratorio } from '../models/laboratorio.model';
import { environment } from '../../environments/environment';

describe('LaboratorioService', () => {
  let service: LaboratorioService;
  let httpMock: HttpTestingController;

  const mockLaboratorios: Laboratorio[] = [
    {
      id: 1,
      nombre: 'Laboratorio Central',
      tipo: 'CLINICO',
      direccion: { id: 1, calle: 'Av. Principal', numero: 100, ciudad: 'Santiago', comuna: 'Centro', region: 'RM' },
      contacto: { id: 1, fono1: '+56912345678', email: 'lab@test.com' }
    },
    {
      id: 2,
      nombre: 'Laboratorio Norte',
      tipo: 'QUIMICO',
      direccion: { id: 2, calle: 'Calle Norte', numero: 200, ciudad: 'Antofagasta', comuna: 'Norte', region: 'II' },
      contacto: { id: 2, fono1: '+56987654321', email: 'norte@test.com' }
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [LaboratorioService]
    });
    service = TestBed.inject(LaboratorioService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all laboratorios', () => {
    service.getLaboratorios().subscribe(labs => {
      expect(labs.length).toBe(2);
      expect(labs[0].nombre).toBe('Laboratorio Central');
      expect(labs[1].tipo).toBe('QUIMICO');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/labs`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: mockLaboratorios });
  });

  it('should get laboratorio by id', () => {
    const labId = 1;
    
    service.getLaboratorio(labId).subscribe(lab => {
      expect(lab).toBeTruthy();
      expect(lab.id).toBe(1);
      expect(lab.nombre).toBe('Laboratorio Central');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/labs/${labId}`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: mockLaboratorios[0] });
  });

  it('should create a new laboratorio', () => {
    const newLab: Laboratorio = {
      nombre: 'Nuevo Laboratorio',
      tipo: 'BIOLOGICO',
      direccion: { calle: 'Nueva Calle', numero: 300, ciudad: 'Valparaíso', comuna: 'Viña', region: 'V' },
      contacto: { fono1: '+56911111111', email: 'nuevo@test.com' }
    };

    service.crearLaboratorio(newLab).subscribe(lab => {
      expect(lab).toBeTruthy();
      expect(lab.nombre).toBe('Nuevo Laboratorio');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/labs`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newLab);
    req.flush({ data: { ...newLab, id: 3 } });
  });

  it('should update laboratorio', () => {
    const updatedLab: Laboratorio = {
      id: 1,
      nombre: 'Laboratorio Central Actualizado',
      tipo: 'CLINICO',
      direccion: { id: 1, calle: 'Av. Principal', numero: 100, ciudad: 'Santiago', comuna: 'Centro', region: 'RM' },
      contacto: { id: 1, fono1: '+56912345678', email: 'lab@test.com' }
    };

    service.actualizarLaboratorio(1, updatedLab).subscribe(lab => {
      expect(lab.nombre).toBe('Laboratorio Central Actualizado');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/labs/1`);
    expect(req.request.method).toBe('PUT');
    req.flush({ data: updatedLab });
  });

  it('should filter laboratorios by ciudad', () => {
    service.buscarPorCiudad('Santiago').subscribe(labs => {
      expect(labs.length).toBe(1);
      expect(labs[0].direccion?.ciudad).toBe('Santiago');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/labs`);
    req.flush({ data: mockLaboratorios });
  });
});
