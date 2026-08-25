import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, Subject, throwError } from 'rxjs';

import { NavbarComponent } from './navbar.component';
import { AuthService } from '../../app/services/auth.service';
import { UserService } from '../../app/services/user.service';

describe('NavbarComponent', () => {
  let component: NavbarComponent;
  let fixture: ComponentFixture<NavbarComponent>;

  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;
  let userSubject: Subject<any>;

  beforeEach(async () => {
    userSubject = new Subject<any>();

    authService = jasmine.createSpyObj('AuthService', [
      'getProfile',
      'logout'
    ]);

    router = jasmine.createSpyObj('Router', ['navigate']);

    authService.getProfile.and.returnValue(
      of({
        id: '1',
        name: 'Test User',
        email: 'test@test.com',
        role: 'CLIENT',
        avatarUrl: '',
        createdAt: '2026-01-01T00:00:00Z'
      } as any)
    );

    const userService = {
      user$: userSubject.asObservable()
    };

    await TestBed.configureTestingModule({
      imports: [NavbarComponent],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: UserService, useValue: userService },
        { provide: Router, useValue: router }
      ]
    })
      .overrideComponent(NavbarComponent, {
        set: {
          template: ''
        }
      })
      .compileComponents();

    fixture = TestBed.createComponent(NavbarComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize and load profile', () => {
    fixture.detectChanges();

    expect(authService.getProfile).toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
    expect(component.username).toBe('Curator');
    expect(component.avatarUrl).toBeNull();
  });

  it('should update username and avatar', () => {
    fixture.detectChanges();

    userSubject.next({
      name: 'John',
      avatarUrl: '/avatar.jpg'
    });

    expect(component.username).toBe('John');
    expect(component.avatarUrl).toBe('/avatar.jpg');
  });

  it('should use default values when user data is empty', () => {
    fixture.detectChanges();

    userSubject.next({
      name: '',
      avatarUrl: ''
    });

    expect(component.username).toBe('Curator');
    expect(component.avatarUrl).toBeNull();
  });

  it('should handle profile error', () => {
    authService.getProfile.and.returnValue(
      throwError(() => new Error('Failed'))
    );

    component.fetchUserProfile();

    expect(authService.getProfile).toHaveBeenCalled();
    expect(component.isLoading).toBeFalse();
  });

  it('should logout and navigate to login', () => {
    component.logout();

    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should unsubscribe on destroy', () => {
    fixture.detectChanges();

    const subscription = jasmine.createSpyObj('Subscription', [
      'unsubscribe'
    ]);

    (component as any).userSub = subscription;

    component.ngOnDestroy();

    expect(subscription.unsubscribe).toHaveBeenCalled();
  });

  it('should not fail if subscription is missing', () => {
    (component as any).userSub = undefined;

    expect(() => component.ngOnDestroy()).not.toThrow();
  });
});