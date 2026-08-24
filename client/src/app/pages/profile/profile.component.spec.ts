import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProfileComponent } from './profile.component';
import { AuthService, ProfileResponse } from '../../services/auth.service';
import { MediaService } from '../../services/media.service';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;

  let authService: jasmine.SpyObj<AuthService>;
  let mediaService: jasmine.SpyObj<MediaService>;
  let userService: jasmine.SpyObj<UserService>;
  let router: jasmine.SpyObj<Router>;

  const user = {
    id: '1',
    name: 'John',
    email: 'john@test.com',
    avatarUrl: '/avatar.jpg',
    role: 'CLIENT'
  } as ProfileResponse;

  beforeEach(async () => {
    authService = jasmine.createSpyObj('AuthService', [
      'getProfile',
      'updateProfile',
      'logout'
    ]);

    mediaService = jasmine.createSpyObj('MediaService', [
      'uploadImages'
    ]);

    userService = jasmine.createSpyObj(
      'UserService',
      ['setUser'],
      { user$: of(user) }
    );

    router = jasmine.createSpyObj('Router', ['navigate']);

    authService.getProfile.and.returnValue(of(user));

    await TestBed.configureTestingModule({
      imports: [ProfileComponent],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: MediaService, useValue: mediaService },
        { provide: UserService, useValue: userService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
  });

  it('should create and load profile', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(component.loading).toBeFalse();
    expect(component.form.value.username).toBe('John');
  });

  it('should handle profile loading error', () => {
    authService.getProfile.and.returnValue(
      throwError(() => new Error('error'))
    );

    component.loadProfile();

    expect(component.loading).toBeFalse();
    expect(component.error).toBe('Cannot load profile');
  });

  it('should enable and cancel editing', () => {
    component.editProfile();

    expect(component.editing).toBeTrue();

    component.cancelEdit();

    expect(component.editing).toBeFalse();
  });

  it('should not save invalid form', () => {
    component.form.patchValue({
      username: '',
      email: '',
      role: ''
    });

    component.saveProfile();

    expect(authService.updateProfile).not.toHaveBeenCalled();
  });

  it('should save profile without new avatar', () => {
    component.user = user;

    component.form.patchValue({
      username: 'John',
      email: 'john@test.com',
      role: 'CLIENT'
    });

    authService.updateProfile.and.returnValue(of(user));

    component.saveProfile();

    expect(authService.updateProfile).toHaveBeenCalled();
    expect(userService.setUser).toHaveBeenCalled();
    expect(component.loading).toBeFalse();
  });

  it('should handle update error', () => {
    component.user = user;

    component.form.patchValue({
      username: 'John',
      email: 'john@test.com',
      role: 'CLIENT'
    });

    authService.updateProfile.and.returnValue(
      throwError(() => ({
        error: {
          errorMessage: 'Update failed'
        }
      }))
    );

    component.saveProfile();

    expect(component.error).toBe('Update failed');
    expect(component.loading).toBeFalse();
  });

  it('should logout', () => {
    component.logout();

    expect(authService.logout).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });

  it('should unsubscribe on destroy', () => {
    const sub = jasmine.createSpyObj('Subscription', ['unsubscribe']);

    (component as any).userSub = sub;

    component.ngOnDestroy();

    expect(sub.unsubscribe).toHaveBeenCalled();
  });
});