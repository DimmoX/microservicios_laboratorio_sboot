import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { LabExamService } from './lab-exam.service';
import { environment } from '../../environments/environment';

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

  it('should get lab exams', () => {
    const mockExams = [{ id: 1, labId: 1, examenId: 1 }];
    
    service.getLabExams().subscribe(exams => {
      expect(exams.length).toBe(1);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/lab-exams`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: mockExams });
  });

  it('should get lab exam by id', () => {
    const mockExam = { id: 1, labId: 1, examenId: 1 };
    
    service.getLabExamById(1).subscribe(exam => {
      expect(exam.id).toBe(1);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/lab-exams/1`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: mockExam });
  });

  it('should create lab exam', () => {
    const newExam = { labId: 1, examenId: 1 };
    
    service.createLabExam(newExam).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/lab-exams`);
    expect(req.request.method).toBe('POST');
    req.flush({ data: newExam });
  });

  it('should update lab exam', () => {
    const updated = { id: 1, labId: 2, examenId: 2 };
    
    service.updateLabExam(1, updated).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/lab-exams/1`);
    expect(req.request.method).toBe('PUT');
    req.flush({ data: updated });
  });

  it('should delete lab exam', () => {
    service.deleteLabExam(1).subscribe();

    const req = httpMock.expectOne(`${environment.apiUrl}/lab-exams/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });
});
