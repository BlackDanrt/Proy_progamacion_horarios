import { TestBed } from '@angular/core/testing';

import { SalonService } from './salon.service';

/** Pruebas básicas de creación del servicio de salones. */
describe('SalonService', () => {
  let service: SalonService;

  /** Configura el entorno de pruebas e inyecta el servicio. */
  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SalonService);
  });

  /** Comprueba que el servicio se haya creado correctamente. */
  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
