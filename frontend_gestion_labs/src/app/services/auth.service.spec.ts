import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    
    // Limpiar sessionStorage antes de cada test
    sessionStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    sessionStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login successfully and store session', () => {
    const mockResponse = {
      data: {
        token: 'mock-jwt-token',
        usuario: {
          id: 1,
          username: 'test@test.com',
          rol: 'ADMIN'
        }
      }
    };

    const loginRequest = { username: 'test@test.com', password: 'password123' };

    service.login(loginRequest).subscribe(sesion => {
      expect(sesion).toBeTruthy();
      expect(sesion.token).toBe('mock-jwt-token');
      expect(sesion.usuario.username).toBe('test@test.com');
      expect(sessionStorage.getItem('token')).toBe('mock-jwt-token');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(loginRequest);
    req.flush(mockResponse);
  });

  it('should handle login error with 401 status', () => {
    const loginRequest = { username: 'test@test.com', password: 'wrongpassword' };

    service.login(loginRequest).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.message).toBe('Usuario o contraseña incorrectos');
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);
    req.flush({ error: 'Unauthorized' }, { status: 401, statusText: 'Unauthorized' });
  });

  it('should logout and clear session', () => {
    // Simular sesión activa
    sessionStorage.setItem('token', 'test-token');
    sessionStorage.setItem('current_session', JSON.stringify({ usuario: { id: 1 } }));

    service.logout().subscribe(() => {
      expect(sessionStorage.getItem('token')).toBeNull();
      expect(sessionStorage.getItem('current_session')).toBeNull();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/auth/logout`);
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('should return correct authentication status', () => {
    // Sin sesión
    expect(service.isAuthenticated()).toBeFalse();

    // Con sesión
    sessionStorage.setItem('current_session', JSON.stringify({ 
      usuario: { id: 1 },
      token: 'test-token',
      fechaLogin: new Date()
    }));
    
    // Crear nueva instancia para que lea sessionStorage
    const newService = TestBed.inject(AuthService);
    expect(newService.isAuthenticated()).toBeTrue();
  });

  it('should register paciente correctly', () => {
    const mockResponse = { data: { id: 1, username: 'paciente@test.com' } };
    const registerData = { 
      pnombre: 'Juan',
      papellido: 'Pérez',
      contacto: { email: 'paciente@test.com' },
      password: 'password123'
    };

    service.registerPaciente(registerData).subscribe(response => {
      expect(response).toBeTruthy();
      expect(response.data.username).toBe('paciente@test.com');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/registro/paciente`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });
});
