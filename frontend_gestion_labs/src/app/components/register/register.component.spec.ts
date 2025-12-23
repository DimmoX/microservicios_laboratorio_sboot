import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['register']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [RegisterComponent, FormsModule],
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have empty register data initially', () => {
    expect(component.registerData.username).toBe('');
    expect(component.registerData.email).toBe('');
  });

  it('should call authService.register on submit', () => {
    authService.register.and.returnValue(of({ id: 1, username: 'newuser' }));
    component.registerData = { username: 'newuser', email: 'new@test.com', password: 'pass123' };
    
    component.onSubmit();
    
    expect(authService.register).toHaveBeenCalled();
  });

  it('should navigate to login on successful registration', () => {
    authService.register.and.returnValue(of({ id: 1, username: 'newuser' }));
    component.registerData = { username: 'newuser', email: 'new@test.com', password: 'pass123' };
    
    component.onSubmit();
    
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should handle registration error', () => {
    authService.register.and.returnValue(throwError(() => new Error('Registration failed')));
    component.registerData = { username: 'test', email: 'test@test.com', password: 'pass' };
    
    component.onSubmit();
    
    expect(component.errorMessage).toBeTruthy();
  });
});
