import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { authGuard } from './auth.guard';
import { AuthService } from '../services/auth.service';

describe('authGuard', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const authSpy = jasmine.createSpyObj('AuthService', ['isAuthenticated']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should be a function', () => {
    expect(typeof authGuard).toBe('function');
  });

  it('should return true when authenticated', () => {
    authService.isAuthenticated.and.returnValue(true);
    
    const result = TestBed.runInInjectionContext(() => authGuard());
    
    expect(result).toBeTrue();
  });

  it('should return false when not authenticated', () => {
    authService.isAuthenticated.and.returnValue(false);
    
    const result = TestBed.runInInjectionContext(() => authGuard());
    
    expect(result).toBeFalse();
  });

  it('should redirect to login when not authenticated', () => {
    authService.isAuthenticated.and.returnValue(false);
    
    TestBed.runInInjectionContext(() => authGuard());
    
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should not redirect when authenticated', () => {
    authService.isAuthenticated.and.returnValue(true);
    
    TestBed.runInInjectionContext(() => authGuard());
    
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
