import { TestBed } from '@angular/core/testing';
import { ParametrosSistemaService } from './parametrossistema.service';



describe('ParametrossistemaService', () => {
  let service: ParametrosSistemaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ParametrosSistemaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
