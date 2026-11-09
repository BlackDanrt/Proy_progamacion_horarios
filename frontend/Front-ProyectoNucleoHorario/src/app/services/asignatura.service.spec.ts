import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpResponse } from '@angular/common/http';

import { AsignaturaService } from './asignatura.service';
import { AsignaturaModel } from '../models/asignatura.model';
import { environment } from '../../environments/environment';

describe('AsignaturaService', () => {
  let service: AsignaturaService;
  let httpMock: HttpTestingController;
  const urlBase = `${environment.apiUrl}/asignatura`;

  const mockAsignatura: AsignaturaModel = {
    nombre: 'Matemáticas',
    numCreditos: 4,
    frecuenciaSemanal: 3,
    requiereComputadores: false,
    requiereSillasMoviles: true
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AsignaturaService]
    });

    service = TestBed.inject(AsignaturaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should search asignatura by nombre', () => {
    const mockResponse = [mockAsignatura];

    service.buscarPorNombre('Matemáticas').subscribe(response => {
      expect(response).toBeInstanceOf(HttpResponse);
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/buscarpornombre?nombre=Matem%C3%A1ticas`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });

  it('should search asignatura by frecuencia semanal', () => {
    const mockResponse = [mockAsignatura];

    service.buscarPorFrecuenciaSemanal(3).subscribe(response => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/buscarporfrecuencia?frecuenciaSemanal=3`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });

  it('should search asignatura by numero de creditos', () => {
    const mockResponse = [mockAsignatura];

    service.buscarPorNumCreditos(4).subscribe(response => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/buscarporcreditos?numCreditos=4`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });

  it('should create an asignatura', () => {
    const successMessage = 'Asignatura creada correctamente';

    service.crearAsignatura(mockAsignatura).subscribe(res => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/crear`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockAsignatura);
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should update an asignatura', () => {
    const successMessage = 'Asignatura actualizada correctamente';

    service.actualizarAsignatura(mockAsignatura).subscribe(res => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/actualizar`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(mockAsignatura);
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should delete an asignatura by nombre', () => {
    const successMessage = 'Asignatura eliminada con éxito';

    service.eliminarAsignatura('Matemáticas').subscribe(res => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/eliminar?nombre=Matem%C3%A1ticas`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should get all asignaturas using mostrarTodo', () => {
    const mockResponse = [mockAsignatura];

    service.mostrarTodo().subscribe(response => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/mostrartodo`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });
});
