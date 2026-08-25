import { TestBed } from '@angular/core/testing';
import {
  provideHttpClient,
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';

import { ProductService } from './product.service';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;

  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authService = jasmine.createSpyObj('AuthService', [
      'getUserId',
      'getRole'
    ]);

    authService.getUserId.and.returnValue('user1');
    authService.getRole.and.returnValue('SELLER');

    TestBed.configureTestingModule({
      providers: [
        ProductService,
        { provide: AuthService, useValue: authService },
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all products', () => {
    const products: any[] = [
      {
        id: '1',
        name: 'Phone'
      }
    ];

    service.getAllProducts().subscribe(result => {
      expect(result).toEqual(products);
    });

    const req = httpMock.expectOne(
      `${environment.apiUrl}/api/products`
    );

    expect(req.request.method).toBe('GET');

    req.flush(products);
  });

  it('should get product by id', () => {
    const product = {
      id: '1',
      name: 'Phone'
    };

    service.getProduct('1').subscribe(result => {
      expect(result).toEqual(product);
    });

    const req = httpMock.expectOne(
      `${environment.apiUrl}/api/products/1`
    );

    expect(req.request.method).toBe('GET');

    req.flush(product);
  });

  it('should get product using getProductById', () => {
    const product = {
      id: '1',
      name: 'Phone'
    };

    service.getProductById('1').subscribe(result => {
      expect(result).toEqual(product);
    });

    const req = httpMock.expectOne(
      `${environment.apiUrl}/api/products/1`
    );

    expect(req.request.method).toBe('GET');

    req.flush(product);
  });

  it('should create product with user headers', () => {
    const formData = new FormData();
    formData.append('name', 'Phone');

    const product = {
      id: '1',
      name: 'Phone'
    };

    service.createProduct(formData).subscribe(result => {
      expect(result).toEqual(product);
    });

    const req = httpMock.expectOne(
      `${environment.apiUrl}/api/products`
    );

    expect(req.request.method).toBe('POST');
    expect(req.request.headers.get('X-User-Id')).toBe('user1');
    expect(req.request.headers.get('X-Role')).toBe('SELLER');

    req.flush(product);
  });

  it('should update product', () => {
    const formData = new FormData();

    service.updateProduct('1', formData).subscribe();

    const req = httpMock.expectOne(
      `${environment.apiUrl}/api/products/1`
    );

    expect(req.request.method).toBe('PUT');
    expect(req.request.headers.get('X-User-Id')).toBe('user1');
    expect(req.request.headers.get('X-Role')).toBe('SELLER');

    req.flush({
      id: '1',
      name: 'Updated'
    });
  });

  it('should delete product', () => {
    service.deleteProduct('1').subscribe();

    const req = httpMock.expectOne(
      `${environment.apiUrl}/api/products/1`
    );

    expect(req.request.method).toBe('DELETE');
    expect(req.request.headers.get('X-User-Id')).toBe('user1');
    expect(req.request.headers.get('X-Role')).toBe('SELLER');

    req.flush(null);
  });

  it('should use default headers when user data is missing', () => {
    authService.getUserId.and.returnValue(null);
    authService.getRole.and.returnValue(null);

    const formData = new FormData();

    service.createProduct(formData).subscribe();

    const req = httpMock.expectOne(
      `${environment.apiUrl}/api/products`
    );

    expect(req.request.headers.get('X-User-Id')).toBe('');
    expect(req.request.headers.get('X-Role')).toBe('CLIENT');

    req.flush({
      id: '1',
      name: 'Phone'
    });
  });
});