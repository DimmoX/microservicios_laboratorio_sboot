import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ExamenService } from './examen.service';
import { Examen } from '../models/examen.model';
import { environment } from '../../environments/environment';

describe('ExamenService', () => {
  let service: ExamenService;
  let httpMock: HttpTestingController;

  const mockExamenes: Examen[] = [
    { id: 1, codigo: 'EX01', nombre: 'Hemograma Completo', tipo: 'SANGRE' },
    { id: 2, codigo: 'EX02', nombre: 'Glicemia', tipo: 'SANGRE' },
    { id: 3, codigo: 'EX03', nombre: 'Orina Completa', tipo: 'ORINA' }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ExamenService]
    });
    service = TestBed.inject(ExamenService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all examenes', () => {
    service.getExamenes().subscribe(examenes => {
      expect(examenes.length).toBe(3);
      expect(examenes[0].nombre).toBe('Hemograma Completo');
      expect(examenes[2].tipo).toBe('ORINA');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/exams`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: mockExamenes });
  });

  it('should get examen by id', () => {
    const examenId = 1;
    
    service.getExamen(examenId).subscribe(examen => {
      expect(examen).toBeTruthy();
      expect(examen.id).toBe(1);
      expect(examen.codigo).toBe('EX01');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/exams/${examenId}`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: mockExamenes[0] });
  });

  it('should create a new examen', () => {
    const newExamen: Examen = {
      codigo: 'EX04',
      nombre: 'Perfil Lipídico',
      tipo: 'SANGRE'
    };

    service.crearExamen(newExamen).subscribe(examen => {
      expect(examen).toBeTruthy();
      expect(examen.codigo).toBe('EX04');
      expect(examen.nombre).toBe('Perfil Lipídico');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/exams`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newExamen);
    req.flush({ data: { ...newExamen, id: 4 } });
  });

  it('should update an examen', () => {
    const updatedExamen: Examen = {
      id: 1,
      codigo: 'EX01',
      nombre: 'Hemograma Completo Actualizado',
      tipo: 'SANGRE_VENOSA'
    };

    service.actualizarExamen(1, updatedExamen).subscribe(examen => {
      expect(examen.nombre).toBe('Hemograma Completo Actualizado');
      expect(examen.tipo).toBe('SANGRE_VENOSA');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/exams/1`);
    expect(req.request.method).toBe('PUT');
    req.flush({ data: updatedExamen });
  });

  it('should delete an examen', () => {
    service.eliminarExamen(1).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/exams/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({ code: '000', description: 'Examen eliminado' });
  });
});
