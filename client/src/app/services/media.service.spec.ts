import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import {
  HttpTestingController,
  provideHttpClientTesting
} from '@angular/common/http/testing';

import { MediaService } from './media.service';
import { environment } from '../../environments/environment';

describe('MediaService', () => {
  let service: MediaService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        MediaService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(MediaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should upload public avatar', () => {
    const file = new File(['avatar'], 'avatar.png', {
      type: 'image/png'
    });

    service.uploadPublicAvatar(file).subscribe(response => {
      expect(response.avatarUrl).toBe('/avatar.png');
    });

    const req = httpMock.expectOne(
      `${environment.apiUrl}/api/media/avatars/public`
    );

    expect(req.request.method).toBe('POST');
    expect(req.request.body instanceof FormData).toBeTrue();
    expect(req.request.body.get('avatar')).toBe(file);

    req.flush({ avatarUrl: '/avatar.png' });
  });

  it('should upload multiple images', () => {
    const file1 = new File(['one'], 'one.png');
    const file2 = new File(['two'], 'two.png');

    service.uploadImages([file1, file2]).subscribe(response => {
      expect(response).toEqual(['/one.png', '/two.png']);
    });

    const req = httpMock.expectOne(
      `${environment.apiUrl}/api/media/images`
    );

    expect(req.request.method).toBe('POST');
    expect(req.request.body instanceof FormData).toBeTrue();

    const body = req.request.body as FormData;

    expect(body.getAll('images').length).toBe(2);

    req.flush(['/one.png', '/two.png']);
  });

  it('should delete image', () => {
    service.deleteImage('test.png').subscribe();

    const req = httpMock.expectOne(
      `${environment.apiUrl}/api/media/images/test.png`
    );

    expect(req.request.method).toBe('DELETE');

    req.flush(null);
  });

  it('should generate image URL', () => {
    const result = service.getImageUrl('test.png');

    expect(result)
      .toBe(`${environment.apiUrl}/api/media/images/test.png`);
  });
});