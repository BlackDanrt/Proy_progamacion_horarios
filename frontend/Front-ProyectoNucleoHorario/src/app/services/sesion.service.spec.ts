import { TestBed } from '@angular/core/testing';

import { SesionService } from './sesion.service';

/** Pruebas básicas de creación del servicio de sesión. */
describe('SesionService', () => {
  let service: SesionService;

  /** Configura el entorno de pruebas e inyecta el servicio. */
  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SesionService);
  });

  /** Comprueba que el servicio se haya creado correctamente. */
  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
