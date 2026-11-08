import { TestBed } from '@angular/core/testing';

import { ProfesorService } from './profesor.service';

/** Pruebas básicas de creación del servicio de profesores. */
describe('ProfesorService', () => {
  let service: ProfesorService;

  /** Configura el entorno de pruebas e inyecta el servicio. */
  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProfesorService);
  });

  /** Comprueba que el servicio se haya creado correctamente. */
  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
