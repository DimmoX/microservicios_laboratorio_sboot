import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UsuarioService } from './usuario.service';
import { Usuario } from '../models/usuario.model';
import { environment } from '../../environments/environment';

describe('UsuarioService', () => {
  let service: UsuarioService;
  let httpMock: HttpTestingController;

  const mockUsuarios: Usuario[] = [
    {
      id: 1,
      username: 'admin@test.com',
      password: '',
      rol: 'ADMIN',
      pnombre: 'Admin',
      papellido: 'Sistema',
      activo: true
    },
    {
      id: 2,
      username: 'paciente@test.com',
      password: '',
      rol: 'PATIENT',
      pnombre: 'Juan',
      papellido: 'Pérez',
      activo: true,
      pacienteId: 10
    }
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [UsuarioService]
    });
    service = TestBed.inject(UsuarioService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all usuarios', () => {
    service.getUsuarios().subscribe(usuarios => {
      expect(usuarios.length).toBe(2);
      expect(usuarios[0].username).toBe('admin@test.com');
      expect(usuarios[1].rol).toBe('PATIENT');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/users`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: mockUsuarios });
  });

  it('should get usuario by id', () => {
    service.getUsuario(1).subscribe(usuario => {
      expect(usuario).toBeTruthy();
      expect(usuario.id).toBe(1);
      expect(usuario.username).toBe('admin@test.com');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/users/1`);
    expect(req.request.method).toBe('GET');
    req.flush({ data: mockUsuarios[0] });
  });

  it('should register a new paciente', () => {
    const newPaciente: Usuario = {
      id: 0,
      username: 'nuevo@test.com',
      password: 'password123',
      rol: 'PATIENT',
      pnombre: 'María',
      papellido: 'González',
      activo: true
    };

    service.registrarUsuario(newPaciente).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/registro/paciente`);
    expect(req.request.method).toBe('POST');
    req.flush({ data: { id: 3, username: 'nuevo@test.com' } });
  });

  it('should register a new empleado', () => {
    const newEmpleado: Usuario = {
      id: 0,
      username: 'empleado@test.com',
      password: 'password123',
      rol: 'EMPLOYEE',
      pnombre: 'Carlos',
      papellido: 'López',
      cargo: 'Tecnólogo Médico',
      activo: true
    };

    service.registrarUsuario(newEmpleado).subscribe(response => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/registro/empleado`);
    expect(req.request.method).toBe('POST');
    req.flush({ data: { id: 4, username: 'empleado@test.com' } });
  });

  it('should create a new usuario with legacy method', () => {
    const newUsuario: Usuario = {
      id: 0,
      username: 'legacy@test.com',
      password: 'password123',
      rol: 'ADMIN',
      activo: true
    };

    service.crearUsuario(newUsuario).subscribe(usuario => {
      expect(usuario).toBeTruthy();
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/users`);
    expect(req.request.method).toBe('POST');
    req.flush({ data: { id: 5, username: 'legacy@test.com' } });
  });
});
