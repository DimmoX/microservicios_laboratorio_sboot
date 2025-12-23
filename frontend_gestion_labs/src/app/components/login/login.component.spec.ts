import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { Router, ActivatedRoute, NavigationEnd } from '@angular/router';
import { of, throwError } from 'rxjs';
import { SesionActual, Usuario } from '../../models/usuario.model';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', [
      'login', 
      'isAuthenticated', 
      'requiresPasswordChange'
    ]);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate', 'createUrlTree', 'serializeUrl'], {
      events: of(new NavigationEnd(0, '/', '/'))
    });
    routerSpy.createUrlTree.and.returnValue({} as any);
    routerSpy.serializeUrl.and.returnValue('/');
    const activatedRouteSpy = jasmine.createSpyObj('ActivatedRoute', [], {
      snapshot: { params: {} }
    });

    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: activatedRouteSpy }
      ]
    }).compileComponents();

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    authService.isAuthenticated.and.returnValue(false);
    authService.requiresPasswordChange.and.returnValue(false);
    
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with empty loginData', () => {
    expect(component.loginData.username).toBe('');
    expect(component.loginData.password).toBe('');
  });

  it('should call authService.login on submit', () => {
    const mockUser: Usuario = { 
      id: 1, 
      username: 'test@test.com', 
      nombre: 'Test', 
      password: 'pass', 
      activo: true, 
      rol: 'USER' 
    };
    const mockSession: SesionActual = { 
      usuario: mockUser, 
      token: 'test-token', 
      fechaLogin: new Date() 
    };
    authService.login.and.returnValue(of(mockSession));
    
    component.loginData = { username: 'test@test.com', password: 'pass123' };
    component.onSubmit();
    fixture.detectChanges();
    
    expect(authService.login).toHaveBeenCalledWith({ 
      username: 'test@test.com', 
      password: 'pass123' 
    });
  });

  it('should navigate to dashboard on successful login', () => {
    const mockUser: Usuario = { 
      id: 1, 
      username: 'test@test.com', 
      nombre: 'Test', 
      password: 'pass', 
      activo: true, 
      rol: 'USER' 
    };
    const mockSession: SesionActual = { 
      usuario: mockUser, 
      token: 'test-token', 
      fechaLogin: new Date() 
    };
    authService.login.and.returnValue(of(mockSession));
    
    component.loginData = { username: 'test@test.com', password: 'pass123' };
    component.onSubmit();
    fixture.detectChanges();
    
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should show error message on failed login', () => {
    const errorMessage = 'Invalid credentials';
    authService.login.and.returnValue(throwError(() => ({ message: errorMessage })));
    
    component.loginData = { username: 'test@test.com', password: 'wrong' };
    component.onSubmit();
    fixture.detectChanges();
    
    expect(component.errorMessage).toBe(errorMessage);
  });
});
