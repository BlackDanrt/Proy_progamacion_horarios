import { TestBed } from '@angular/core/testing';

import { AsignaturaService } from './asignatura.service';

/** Pruebas básicas de creación del servicio de asignaturas. */
describe('AsignaturaService', () => {
  let service: AsignaturaService;

  /** Configura el entorno de pruebas e inyecta el servicio. */
  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AsignaturaService);
  });

  /** Comprueba que el servicio se haya creado correctamente. */
  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
