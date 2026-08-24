import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { guestGuard } from './guest.guard';
import { AuthService } from '../app/services/auth.service';

describe('guestGuard', () => {

  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    authService = jasmine.createSpyObj('AuthService', ['isLoggedIn']);
    router = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router }
      ]
    });
  });

  it('should allow guest user', () => {
    authService.isLoggedIn.and.returnValue(false);

    const result = TestBed.runInInjectionContext(() =>
      guestGuard({} as any, {} as any)
    );

    expect(result).toBeTrue();
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('should redirect logged-in user and deny access', () => {
    authService.isLoggedIn.and.returnValue(true);

    const result = TestBed.runInInjectionContext(() =>
      guestGuard({} as any, {} as any)
    );

    expect(result).toBeFalse();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

});