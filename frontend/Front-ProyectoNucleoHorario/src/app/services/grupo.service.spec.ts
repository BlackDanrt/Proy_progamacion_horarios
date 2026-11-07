import { TestBed } from '@angular/core/testing';

import { GrupoService } from './grupo.service';

/** Pruebas básicas de creación del servicio de grupos. */
describe('GrupoService', () => {
  let service: GrupoService;

  /** Configura el entorno de pruebas e inyecta el servicio. */
  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GrupoService);
  });

  /** Comprueba que el servicio se haya creado correctamente. */
  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
