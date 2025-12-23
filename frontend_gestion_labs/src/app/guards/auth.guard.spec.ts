import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const authSpy = jasmine.createSpyObj('AuthService', ['isAuthenticated']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    guard = TestBed.inject(AuthGuard);
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow access when authenticated', () => {
    authService.isAuthenticated.and.returnValue(true);
    
    const result = guard.canActivate();
    
    expect(result).toBeTrue();
  });

  it('should deny access when not authenticated', () => {
    authService.isAuthenticated.and.returnValue(false);
    
    const result = guard.canActivate();
    
    expect(result).toBeFalse();
  });

  it('should redirect to login when not authenticated', () => {
    authService.isAuthenticated.and.returnValue(false);
    
    guard.canActivate();
    
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should not redirect when authenticated', () => {
    authService.isAuthenticated.and.returnValue(true);
    
    guard.canActivate();
    
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
