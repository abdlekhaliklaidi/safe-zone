import { TestBed } from '@angular/core/testing';
import { HttpInterceptorFn, HttpRequest, HttpHandlerFn } from '@angular/common/http';

import { authInterceptor } from './jwt.interceptor';

describe('authInterceptor', () => {

  const interceptor: HttpInterceptorFn = (req, next) =>
    TestBed.runInInjectionContext(() => authInterceptor(req, next));

  beforeEach(() => {
    TestBed.configureTestingModule({});
    localStorage.clear();
  });

  it('should pass request without token', () => {
    const req = new HttpRequest('GET', '/test');

    const next: HttpHandlerFn = (request) => {
      expect(request.headers.has('Authorization')).toBeFalse();
      return undefined as any;
    };

    interceptor(req, next);
  });

  it('should add token to request', () => {
    localStorage.setItem('token', 'test-token');

    const req = new HttpRequest('GET', '/test');

    const next: HttpHandlerFn = (request) => {
      expect(request.headers.get('Authorization'))
        .toBe('Bearer test-token');

      return undefined as any;
    };

    interceptor(req, next);
  });

});