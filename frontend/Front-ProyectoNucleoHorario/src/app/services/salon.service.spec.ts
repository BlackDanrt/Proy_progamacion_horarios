import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpResponse } from '@angular/common/http';

import { SalonService } from './salon.service';
import { SalonModel } from '../models/salon.model';
import { Bloque } from '../enums/bloque.enum';
import { environment } from '../../environments/environment';

describe('SalonService', () => {
  let service: SalonService;
  let httpMock: HttpTestingController;
  const urlBase = `${environment.apiUrl}/salon`;

  const mockSalon = {
    id: 1,
    bloque: Object.values(Bloque)[0],
    numeroSalon: 101,
    capacidad: 40,
    tieneSillasMoviles: true,
    tieneComputadores: false
  } as unknown as SalonModel;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [SalonService]
    });

    service = TestBed.inject(SalonService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should search salones by bloque', () => {
    const mockResponse = [mockSalon];
    const bloqueTest = Object.values(Bloque)[0];

    service.buscarPorBloque(bloqueTest).subscribe((response: HttpResponse<SalonModel[]>) => {
      expect(response).toBeInstanceOf(HttpResponse);
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/buscarporbloque?bloque=${bloqueTest}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });

  it('should search salones by capacidad', () => {
    const mockResponse = [mockSalon];

    service.buscarPorCapacidad(40).subscribe((response: HttpResponse<SalonModel[]>) => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/buscarporcapacidad?capacidad=40`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });

  it('should search salones by tieneSillasMoviles', () => {
    const mockResponse = [mockSalon];

    service.buscarPorTieneSillasMoviles(true).subscribe((response: HttpResponse<SalonModel[]>) => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/buscarporsillasmoviles?tieneSillasMoviles=true`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });

  it('should search salones by tieneComputadores', () => {
    const mockResponse = [mockSalon];

    service.buscarPorTieneComputadores(false).subscribe((response: HttpResponse<SalonModel[]>) => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/buscarporcomputadores?tieneComputadores=false`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });

  it('should search salon by bloque and numeroSalon', () => {
    const bloqueTest = Object.values(Bloque)[0];

    service.buscarPorBloqueAndNumeroSalon(bloqueTest, 101).subscribe((response: HttpResponse<SalonModel>) => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockSalon);
    });

    const req = httpMock.expectOne(`${urlBase}/buscarporbloquenumero?bloque=${bloqueTest}&numeroSalon=101`);
    expect(req.request.method).toBe('GET');
    req.flush(mockSalon, { status: 200, statusText: 'OK' });
  });

  it('should create a salon', () => {
    const successMessage = 'Salón creado con éxito';

    service.crearSalon(mockSalon).subscribe((res: string) => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/crear`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockSalon);
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should update a salon', () => {
    const successMessage = 'Salón actualizado con éxito';

    service.actualizarSalon(mockSalon).subscribe((res: string) => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/actualizar`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(mockSalon);
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should delete a salon by bloque and numeroSalon', () => {
    const successMessage = 'Salón eliminado correctamente';
    const bloqueTest = Object.values(Bloque)[0];

    service.eliminarSalon(bloqueTest, 101).subscribe((res: string) => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/eliminar?bloque=${bloqueTest}&numeroSalon=101`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should get all salones using mostrarTodo', () => {
    const mockResponse = [mockSalon];

    service.mostrarTodo().subscribe((response: HttpResponse<SalonModel[]>) => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/mostrartodo`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });
});
