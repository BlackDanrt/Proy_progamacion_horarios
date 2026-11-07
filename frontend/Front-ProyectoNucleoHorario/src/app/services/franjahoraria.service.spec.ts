import { TestBed } from '@angular/core/testing';

import { FranjahorariaService } from './franjahoraria.service';

/** Pruebas básicas de creación del servicio de franjas horarias. */
describe('FranjahorariaService', () => {
  let service: FranjahorariaService;

  /** Configura el entorno de pruebas e inyecta el servicio. */
  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FranjahorariaService);
  });

  /** Comprueba que el servicio se haya creado correctamente. */
  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
