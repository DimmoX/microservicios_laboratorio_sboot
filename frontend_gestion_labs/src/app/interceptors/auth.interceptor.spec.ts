import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { AuthInterceptor } from './auth.interceptor';
import { AuthService } from '../services/auth.service';

describe('AuthInterceptor', () => {
  let httpMock: HttpTestingController;
  let httpClient: HttpClient;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    const authSpy = jasmine.createSpyObj('AuthService', ['getToken']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        { provide: AuthService, useValue: authSpy },
        {
          provide: HTTP_INTERCEPTORS,
          useClass: AuthInterceptor,
          multi: true
        }
      ]
    });

    httpMock = TestBed.inject(HttpTestingController);
    httpClient = TestBed.inject(HttpClient);
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should add Authorization header when token exists', () => {
    authService.getToken.and.returnValue('test-token');
    
    httpClient.get('/api/test').subscribe();
    
    const req = httpMock.expectOne('/api/test');
    expect(req.request.headers.has('Authorization')).toBeTrue();
    expect(req.request.headers.get('Authorization')).toBe('Bearer test-token');
    req.flush({});
  });

  it('should not add Authorization header when token is null', () => {
    authService.getToken.and.returnValue(null);
    
    httpClient.get('/api/test').subscribe();
    
    const req = httpMock.expectOne('/api/test');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
  });

  it('should pass through request when no token', () => {
    authService.getToken.and.returnValue(null);
    
    httpClient.get('/api/data').subscribe();
    
    const req = httpMock.expectOne('/api/data');
    expect(req.request.method).toBe('GET');
    req.flush({ data: 'test' });
  });

  it('should handle multiple requests', () => {
    authService.getToken.and.returnValue('token123');
    
    httpClient.get('/api/request1').subscribe();
    httpClient.get('/api/request2').subscribe();
    
    const req1 = httpMock.expectOne('/api/request1');
    const req2 = httpMock.expectOne('/api/request2');
    
    expect(req1.request.headers.get('Authorization')).toBe('Bearer token123');
    expect(req2.request.headers.get('Authorization')).toBe('Bearer token123');
    
    req1.flush({});
    req2.flush({});
  });

  it('should update token for each request', () => {
    authService.getToken.and.returnValue('token1');
    httpClient.get('/api/test1').subscribe();
    const req1 = httpMock.expectOne('/api/test1');
    req1.flush({});
    
    authService.getToken.and.returnValue('token2');
    httpClient.get('/api/test2').subscribe();
    const req2 = httpMock.expectOne('/api/test2');
    
    expect(req2.request.headers.get('Authorization')).toBe('Bearer token2');
    req2.flush({});
  });
});
