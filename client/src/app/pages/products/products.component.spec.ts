import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProductsComponent } from './products.component';
import { ProductService } from '../../services/product.service';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { ChangeDetectorRef } from '@angular/core';
import { of, Subject, throwError } from 'rxjs';

describe('ProductsComponent', () => {
  let component: ProductsComponent;
  let fixture: ComponentFixture<ProductsComponent>;

  let productService: jasmine.SpyObj<ProductService>;
  let authService: jasmine.SpyObj<AuthService>;
  let userService: any;

  beforeEach(async () => {
    productService = jasmine.createSpyObj('ProductService', [
      'getAllProducts',
      'deleteProduct'
    ]);

    authService = jasmine.createSpyObj('AuthService', [
      'getRole'
    ]);

    userService = {
      user$: of(null)
    };

    productService.getAllProducts.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [ProductsComponent],
      providers: [
        { provide: ProductService, useValue: productService },
        { provide: AuthService, useValue: authService },
        { provide: UserService, useValue: userService },
        { provide: ChangeDetectorRef, useValue: { detectChanges: () => {} } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductsComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load products', () => {
    productService.getAllProducts.and.returnValue(
      of([
        { id: '1', name: 'Phone' },
        { _id: '2', name: 'Laptop' }
      ])
    );

    component.loadProducts();

    expect(component.products.length).toBe(2);
    expect(component.products[0].id).toBe('1');
    expect(component.products[1].id).toBe('2');
    expect(component.isLoading).toBeFalse();
  });

  it('should handle products error', () => {
    productService.getAllProducts.and.returnValue(
      throwError(() => new Error('Failed'))
    );

    component.loadProducts();

    expect(component.isLoading).toBeFalse();
    expect(component.error)
      .toBe('Could not fetch products from backend.');
  });

  it('should allow seller to add product', () => {
    authService.getRole.and.returnValue('SELLER');

    expect(component.canAddProduct).toBeTrue();
  });

  it('should reject non seller', () => {
    authService.getRole.and.returnValue('CLIENT');

    expect(component.canAddProduct).toBeFalse();
  });

  it('should not delete without product id', () => {
    component.onDeleteProduct('');

    expect(productService.deleteProduct).not.toHaveBeenCalled();
  });

  it('should delete product', () => {
    component.products = [
      { id: '1' } as any,
      { id: '2' } as any
    ];

    productService.deleteProduct.and.returnValue(of(void 0));

    component.onDeleteProduct('1');

    expect(productService.deleteProduct)
      .toHaveBeenCalledWith('1');

    expect(component.products.length).toBe(1);
    expect(component.products[0].id).toBe('2');
  });

  it('should handle delete error', () => {
    productService.deleteProduct.and.returnValue(
      throwError(() => ({
        error: {
          message: 'Delete failed'
        }
      }))
    );

    component.onDeleteProduct('1');

    expect(component.error).toBe('Delete failed');
  });

  it('should unsubscribe on destroy', () => {
    const subscription = jasmine.createSpyObj('Subscription', ['unsubscribe']);

    (component as any).userSub = subscription;

    component.ngOnDestroy();

    expect(subscription.unsubscribe).toHaveBeenCalled();
  });

});