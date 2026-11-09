import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpResponse } from '@angular/common/http';

import { ParametrosSistemaModel } from '../models/parametrossistema.model';
import { environment } from '../../environments/environment';
import { ParametrosSistemaService } from './parametrossistema.service';

describe('ParametrosSistemaService', () => {
  let service: ParametrosSistemaService;
  let httpMock: HttpTestingController;
  const urlBase = `${environment.apiUrl}/parametrossistema`;

  const mockParametros = {
    id: 1,
    horaInicioEntreSemana: '07:00',
    horaFinEntreSemana: '22:00',
    horaInicioSabado: '07:00',
    horaFinSabado: '13:00',
    horaInicioAlmuerzo: '12:00',
    horaFinAlmuerzo: '13:00',
    maxSesionesPorSemana: 4,
    duracionSesionMinutos: 120,
    capacidadMaximaGrupo: 40,
    toleranciaCapacidad: 10,
    minEstudiantesParaAbrir: 10,
    exigirDiasNoConsecutivos: true
  } as unknown as ParametrosSistemaModel;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ParametrosSistemaService]
    });

    service = TestBed.inject(ParametrosSistemaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get parameters using obtenerParametros', () => {
    service.obtenerParametros().subscribe((response: HttpResponse<ParametrosSistemaModel>) => {
      expect(response).toBeInstanceOf(HttpResponse);
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockParametros);
    });

    const req = httpMock.expectOne(`${urlBase}/traerparametros`);
    expect(req.request.method).toBe('GET');
    req.flush(mockParametros, { status: 200, statusText: 'OK' });
  });

  it('should update parameters using actualizarParametros', () => {
    const successMessage = 'Parámetros actualizados con éxito';

    service.actualizarParametros(mockParametros).subscribe((res: string) => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/actualizar`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(mockParametros);
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should get parameters using traerParametros alias', () => {
    service.traerParametros().subscribe((response: HttpResponse<ParametrosSistemaModel>) => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockParametros);
    });

    const req = httpMock.expectOne(`${urlBase}/traerparametros`);
    expect(req.request.method).toBe('GET');
    req.flush(mockParametros, { status: 200, statusText: 'OK' });
  });
});
