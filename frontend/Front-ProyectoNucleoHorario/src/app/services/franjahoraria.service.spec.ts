import { TestBed } from '@angular/core/testing';

import { FranjahorariaService } from './franjahoraria.service';

describe('FranjahorariaService', () => {
  let service: FranjahorariaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FranjahorariaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
