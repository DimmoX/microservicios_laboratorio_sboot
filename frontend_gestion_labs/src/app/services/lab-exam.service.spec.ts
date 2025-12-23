import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { LabExamService } from './lab-exam.service';
import { LabExam } from '../models/lab-exam.model';

describe('LabExamService', () => {
  let service: LabExamService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [LabExamService]
    });
    service = TestBed.inject(LabExamService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all lab-exams', () => {
    const mockExams: LabExam[] = [
      { idLaboratorio: 1, idExamen: 1, precio: 15000 }
    ];

    service.getLabExams().subscribe(exams => {
      expect(exams).toEqual(mockExams);
    });

    const req = httpMock.expectOne(request => request.url.includes('/lab-exams'));
    expect(req.request.method).toBe('GET');
    req.flush({ code: '200', description: 'OK', data: mockExams });
  });

  it('should get exams by lab', () => {
    const mockExams: LabExam[] = [
      { idLaboratorio: 1, idExamen: 1, precio: 15000 }
    ];

    service.getExamenesPorLab(1).subscribe(exams => {
      expect(exams).toEqual(mockExams);
    });

    const req = httpMock.expectOne(request => request.url.includes('/lab-exams/lab/1'));
    expect(req.request.method).toBe('GET');
    req.flush({ code: '200', description: 'OK', data: mockExams });
  });

  it('should create lab-exam', () => {
    const newExam: LabExam = { idLaboratorio: 1, idExamen: 1, precio: 15000 };

    service.crearLabExam(newExam).subscribe();

    const req = httpMock.expectOne(request => request.url.includes('/lab-exams'));
    expect(req.request.method).toBe('POST');
    req.flush({ code: '201', description: 'Created', data: newExam });
  });

  it('should update lab-exam', () => {
    const updated: LabExam = { idLaboratorio: 1, idExamen: 1, precio: 20000 };

    service.actualizarLabExam(1, 1, updated).subscribe();

    const req = httpMock.expectOne(request => request.url.includes('/lab-exams/lab/1/exam/1'));
    expect(req.request.method).toBe('PUT');
    req.flush({ code: '200', description: 'OK', data: updated });
  });

  it('should delete lab-exam', () => {
    service.eliminarLabExam(1, 1).subscribe();

    const req = httpMock.expectOne(request => request.url.includes('/lab-exams/lab/1/exam/1'));
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
