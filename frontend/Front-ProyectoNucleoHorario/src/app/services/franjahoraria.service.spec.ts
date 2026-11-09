import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpResponse } from '@angular/common/http';

import { FranjahorariaModel } from '../models/franjahoraria.model';
import { Dia } from '../enums/dia.enum';
import { environment } from '../../environments/environment';
import { FranjaHorariaService } from './franjahoraria.service';

describe('FranjaHorariaService', () => {
  let service: FranjaHorariaService;
  let httpMock: HttpTestingController;
  const urlBase = `${environment.apiUrl}/franjahoraria`;

  const mockFranja: FranjahorariaModel = {
    id: 1,
    dia: Dia.LUNES,
    horaInicio: '07:00',
    horaFin: '09:00'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [FranjaHorariaService]
    });

    service = TestBed.inject(FranjaHorariaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should search franja horaria by dia', () => {
    const mockResponse = [mockFranja];

    service.buscarPorDia(Dia.LUNES).subscribe((response: HttpResponse<FranjahorariaModel[]>) => {
      expect(response).toBeInstanceOf(HttpResponse);
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/buscarpordia?dia=LUNES`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });

  it('should search franja horaria by horaInicio', () => {
    const mockResponse = [mockFranja];

    service.buscarPorHoraInicio('07:00').subscribe((response: HttpResponse<FranjahorariaModel[]>) => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/buscarporhorainicio?horaInicio=07:00`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });

  it('should search franja horaria by horaFin', () => {
    const mockResponse = [mockFranja];

    service.buscarPorHoraFin('09:00').subscribe((response: HttpResponse<FranjahorariaModel[]>) => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/buscarporhorafin?horaFin=09:00`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });

  it('should create a franja horaria', () => {
    const successMessage = 'Franja horaria creada con éxito';

    service.crearFranjaHoraria(mockFranja).subscribe((res: string) => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/crear`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockFranja);
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should update a franja horaria', () => {
    const successMessage = 'Franja horaria actualizada con éxito';

    service.actualizarFranjaHoraria(mockFranja).subscribe((res: string) => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/actualizar`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(mockFranja);
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should delete a franja horaria by id', () => {
    const successMessage = 'Franja eliminada correctamente';

    service.eliminarFranjaHorariaPorId(1).subscribe((res: string) => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/eliminarporid?franjaHorariaId=1`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should delete franjas horarias by dia', () => {
    const successMessage = 'Franjas eliminadas por día';

    service.eliminarFranjaHorariaPorDia(Dia.LUNES).subscribe((res: string) => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/eliminarpordia?dia=LUNES`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should get all franjas horarias using mostrarTodo', () => {
    const mockResponse = [mockFranja];

    service.mostrarTodo().subscribe((response: HttpResponse<FranjahorariaModel[]>) => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/mostrartodo`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });
});
