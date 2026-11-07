import { TestBed } from '@angular/core/testing';

import { AdministradorService } from './administrador.service';

/** Pruebas básicas de creación del servicio de administradores. */
describe('AdministradorService', () => {
  let service: AdministradorService;

  /** Configura el entorno de pruebas e inyecta el servicio. */
  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdministradorService);
  });

  /** Comprueba que el servicio se haya creado correctamente. */
  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
