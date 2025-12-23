import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { AuthService } from '../../services/auth.service';
import { of } from 'rxjs';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    const authSpy = jasmine.createSpyObj('AuthService', ['getCurrentUser']);

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: AuthService, useValue: authSpy }
      ]
    }).compileComponents();

    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load current user on init', () => {
    const mockUser = { id: 1, username: 'test', rol: 'ADMIN' };
    authService.getCurrentUser.and.returnValue(of(mockUser));
    
    component.ngOnInit();
    
    expect(component.currentUser).toEqual(mockUser);
  });

  it('should display username', () => {
    component.currentUser = { id: 1, username: 'testuser', rol: 'ADMIN' };
    fixture.detectChanges();
    
    expect(component.currentUser.username).toBe('testuser');
  });

  it('should check if user is admin', () => {
    component.currentUser = { id: 1, username: 'admin', rol: 'ADMIN' };
    
    expect(component.isAdmin()).toBeTrue();
  });

  it('should check if user is not admin', () => {
    component.currentUser = { id: 1, username: 'user', rol: 'USER' };
    
    expect(component.isAdmin()).toBeFalse();
  });
});
