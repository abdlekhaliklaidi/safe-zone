import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { ProductCardComponent } from './product-card.component';
import { Product } from '../../app/models/product';

describe('ProductCardComponent', () => {
  let component: ProductCardComponent;
  let fixture: ComponentFixture<ProductCardComponent>;
  let router: jasmine.SpyObj<Router>;

  const product: Product = {
    id: '1',
    name: 'Phone',
    description: 'This is a test phone',
    price: 100,
    quantity: 10,
    userId: 'user1',
    imageUrls: ['phone.jpg']
  };

  beforeEach(async () => {
    router = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [ProductCardComponent],
      providers: [
        { provide: Router, useValue: router }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductCardComponent);
    component = fixture.componentInstance;

    component.product = product;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should detect product owner', () => {
    component.currentUserId = 'user1';

    expect(component.isOwner).toBeTrue();
  });

  it('should return false when user is not owner', () => {
    component.currentUserId = 'user2';

    expect(component.isOwner).toBeFalse();
  });

  it('should return false when current user is missing', () => {
    component.currentUserId = null;

    expect(component.isOwner).toBeFalse();
  });

  it('should truncate long description', () => {
    component.product = {
      ...product,
      description: '123456789012345678901234567890'
    };

    expect(component.truncatedDescription)
      .toBe('12345678901234567890...');
  });

  it('should keep short description unchanged', () => {
    component.product = {
      ...product,
      description: 'Short description'
    };

    expect(component.truncatedDescription)
      .toBe('Short description');
  });

  it('should return empty description when missing', () => {
    component.product = {
      ...product,
      description: ''
    };

    expect(component.truncatedDescription).toBe('');
  });

  it('should return primary image', () => {
    expect(component.primaryImage).toBe('phone.jpg');
  });

  it('should return fallback image when there are no images', () => {
    component.product = {
      ...product,
      imageUrls: []
    };

    expect(component.primaryImage).toBe('noimage.png');
  });

  it('should return fallback image when imageUrls is missing', () => {
    component.product = {
      ...product,
      imageUrls: undefined as any
    };

    expect(component.primaryImage).toBe('noimage.png');
  });

  it('should set fallback image on image error', () => {
    const img = document.createElement('img');

    component.onImageError({
      target: img
    } as unknown as Event);

    expect(img.src).toContain('noimage.png');
  });

  it('should return product id', () => {
    expect(component.getProductId()).toBe('1');
  });

  it('should return _id when id is missing', () => {
    component.product = {
      ...product,
      id: undefined,
      _id: 'mongo-id'
    } as any;

    expect(component.getProductId()).toBe('mongo-id');
  });

  it('should return empty id when no id exists', () => {
    component.product = {
      ...product,
      id: undefined
    } as any;

    expect(component.getProductId()).toBe('');
  });

  it('should navigate to product details', () => {
    component.navigateToDetails();

    expect(router.navigate).toHaveBeenCalledWith([
      '/products',
      '1'
    ]);
  });

  it('should not navigate when product id is missing', () => {
    spyOn(console, 'error');

    component.product = {
      ...product,
      id: undefined
    } as any;

    component.navigateToDetails();

    expect(router.navigate).not.toHaveBeenCalled();
    expect(console.error).toHaveBeenCalled();
  });

  it('should emit edit event', () => {
    spyOn(component.edit, 'emit');

    const event = new MouseEvent('click');
    spyOn(event, 'stopPropagation');

    component.onEdit(event);

    expect(event.stopPropagation).toHaveBeenCalled();
    expect(component.edit.emit).toHaveBeenCalledWith(product);
  });

  it('should open delete confirmation', () => {
    const event = new MouseEvent('click');
    spyOn(event, 'stopPropagation');

    component.openDeleteConfirm(event);

    expect(event.stopPropagation).toHaveBeenCalled();
    expect(component.showDeleteConfirm).toBeTrue();
  });

  it('should cancel delete confirmation', () => {
    component.showDeleteConfirm = true;

    const event = new MouseEvent('click');
    spyOn(event, 'stopPropagation');

    component.cancelDelete(event);

    expect(event.stopPropagation).toHaveBeenCalled();
    expect(component.showDeleteConfirm).toBeFalse();
  });

  it('should emit delete event', () => {
    spyOn(component.delete, 'emit');

    const event = new MouseEvent('click');
    spyOn(event, 'stopPropagation');

    component.confirmDelete(event);

    expect(event.stopPropagation).toHaveBeenCalled();
    expect(component.delete.emit).toHaveBeenCalledWith('1');
    expect(component.showDeleteConfirm).toBeFalse();
  });

  it('should not emit delete when product id is missing', () => {
    spyOn(component.delete, 'emit');

    component.product = {
      ...product,
      id: undefined
    } as any;

    const event = new MouseEvent('click');
    spyOn(event, 'stopPropagation');

    component.confirmDelete(event);

    expect(component.delete.emit).not.toHaveBeenCalled();
    expect(component.showDeleteConfirm).toBeFalse();
  });
});