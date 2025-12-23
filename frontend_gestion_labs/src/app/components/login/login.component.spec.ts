import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['login']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [LoginComponent, FormsModule],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have empty credentials initially', () => {
    expect(component.credentials.username).toBe('');
    expect(component.credentials.password).toBe('');
  });

  it('should call authService.login on submit', () => {
    authService.login.and.returnValue(of({ token: 'test-token' }));
    component.credentials = { username: 'test@test.com', password: 'pass123' };
    
    component.onSubmit();
    
    expect(authService.login).toHaveBeenCalledWith('test@test.com', 'pass123');
  });

  it('should navigate to dashboard on successful login', () => {
    authService.login.and.returnValue(of({ token: 'test-token' }));
    component.credentials = { username: 'test', password: 'pass' };
    
    component.onSubmit();
    
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
  });

  it('should handle login error', () => {
    authService.login.and.returnValue(throwError(() => new Error('Login failed')));
    component.credentials = { username: 'test', password: 'wrong' };
    
    component.onSubmit();
    
    expect(component.errorMessage).toBeTruthy();
  });
});
