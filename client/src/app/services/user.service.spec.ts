import { TestBed } from '@angular/core/testing';
import { UserService } from './user.service';

describe('UserService', () => {
  let service: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with no current user', () => {
    expect(service.currentUser).toBeNull();
  });

  it('should set user', () => {
    service.setUser({
      id: '1',
      name: 'John',
      email: 'john@test.com',
      role: 'CLIENT',
      avatarUrl: '/avatar.jpg'
    });

    expect(service.currentUser).toBeTruthy();
    expect(service.currentUser?.id).toBe('1');
    expect(service.currentUser?.name).toBe('John');
    expect(service.currentUser?.email).toBe('john@test.com');
    expect(service.currentUser?.role).toBe('CLIENT');
  });

  it('should use username as name when name is missing', () => {
    service.setUser({
      id: '1',
      username: 'John'
    } as any);

    expect(service.currentUser?.name).toBe('John');
  });

  it('should use Curator when name and username are missing', () => {
    service.setUser({
      id: '1'
    });

    expect(service.currentUser?.name).toBe('Curator');
  });

  it('should format relative avatar URL', () => {
    service.setUser({
      id: '1',
      avatarUrl: '/avatar.jpg'
    });

    expect(service.currentUser?.avatarUrl)
      .toBe('https://localhost:8443/avatar.jpg');
  });

  it('should keep absolute avatar URL unchanged', () => {
    service.setUser({
      id: '1',
      avatarUrl: 'https://example.com/avatar.jpg'
    });

    expect(service.currentUser?.avatarUrl)
      .toBe('https://example.com/avatar.jpg');
  });

  it('should keep data URL unchanged', () => {
    const dataUrl = 'data:image/png;base64,test';

    service.setUser({
      id: '1',
      avatarUrl: dataUrl
    });

    expect(service.currentUser?.avatarUrl).toBe(dataUrl);
  });

  it('should clear user', () => {
    service.setUser({
      id: '1',
      name: 'John'
    });

    service.clearUser();

    expect(service.currentUser).toBeNull();
  });
});