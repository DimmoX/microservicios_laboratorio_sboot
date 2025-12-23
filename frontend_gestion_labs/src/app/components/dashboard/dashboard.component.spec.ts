import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { AuthService } from '../../services/auth.service';
import { Router, ActivatedRoute, NavigationEnd } from '@angular/router';
import { of } from 'rxjs';
import { Usuario } from '../../models/usuario.model';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', [], { 
      currentUser: of(null) 
    });
    const routerSpy = jasmine.createSpyObj('Router', ['navigate', 'createUrlTree', 'serializeUrl'], {
      events: of(new NavigationEnd(0, '/', '/'))
    });
    routerSpy.createUrlTree.and.returnValue({} as any);
    routerSpy.serializeUrl.and.returnValue('/');
    const activatedRouteSpy = jasmine.createSpyObj('ActivatedRoute', [], {
      snapshot: { params: {} }
    });

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: activatedRouteSpy }
      ]
    }).compileComponents();

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should create', () => {
    const mockUser: Usuario = { 
      id: 1, 
      username: 'test', 
      nombre: 'Test User', 
      password: 'pass', 
      activo: true, 
      rol: 'USER' 
    };
    Object.defineProperty(authService, 'currentUser', { 
      get: () => of(mockUser) 
    });
    
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    
    expect(component).toBeTruthy();
  });

  it('should load current user on init', () => {
    const mockUser: Usuario = { 
      id: 1, 
      username: 'test', 
      nombre: 'Test User', 
      password: 'pass', 
      activo: true, 
      rol: 'USER' 
    };
    Object.defineProperty(authService, 'currentUser', { 
      get: () => of(mockUser) 
    });
    
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    
    expect(component.currentUser).toEqual(mockUser);
  });

  it('should display user data', () => {
    const mockUser: Usuario = { 
      id: 1, 
      username: 'testuser', 
      nombre: 'Test User', 
      password: 'pass', 
      activo: true, 
      rol: 'USER' 
    };
    Object.defineProperty(authService, 'currentUser', { 
      get: () => of(mockUser) 
    });
    
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    
    expect(component.currentUser?.username).toBe('testuser');
  });

  it('should detect admin role', () => {
    const mockUser: Usuario = { 
      id: 1, 
      username: 'admin', 
      nombre: 'Admin User', 
      password: 'pass', 
      activo: true, 
      rol: 'ADMIN' 
    };
    Object.defineProperty(authService, 'currentUser', { 
      get: () => of(mockUser) 
    });
    
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    
    expect(component.isAdmin).toBeTrue();
  });

  it('should detect non-admin role', () => {
    const mockUser: Usuario = { 
      id: 1, 
      username: 'user', 
      nombre: 'Regular User', 
      password: 'pass', 
      activo: true, 
      rol: 'USER' 
    };
    Object.defineProperty(authService, 'currentUser', { 
      get: () => of(mockUser) 
    });
    
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    
    expect(component.isAdmin).toBeFalse();
  });
});
