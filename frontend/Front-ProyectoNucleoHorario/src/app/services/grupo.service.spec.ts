import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpResponse } from '@angular/common/http';

import { GrupoService } from './grupo.service';
import { GrupoModel } from '../models/grupo.model';
import { EstadoGrupo } from '../enums/estadoGrupo.enum';
import { environment } from '../../environments/environment';

describe('GrupoService', () => {
  let service: GrupoService;
  let httpMock: HttpTestingController;
  const urlBase = `${environment.apiUrl}/grupo`;

  const mockGrupo = {
    id: 1,
    numeroGrupo: 101,
    idAsignatura: 10,
    idProfesor: 5,
    estadoGrupo: EstadoGrupo.ACTIVO,
    cupos: 30
  } as unknown as GrupoModel;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [GrupoService]
    });

    service = TestBed.inject(GrupoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should search grupos by idAsignatura', () => {
    const mockResponse = [mockGrupo];

    service.buscarPorAsignatura(10).subscribe((response: HttpResponse<GrupoModel[]>) => {
      expect(response).toBeInstanceOf(HttpResponse);
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/buscarporasignatura?idAsignatura=10`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });

  it('should search grupos by estadoGrupo', () => {
    const mockResponse = [mockGrupo];

    service.buscarPorEstado(EstadoGrupo.ACTIVO).subscribe((response: HttpResponse<GrupoModel[]>) => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/buscarporestado?estadoGrupo=ACTIVO`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });

  it('should search grupos by numeroGrupo', () => {
    const mockResponse = [mockGrupo];

    service.buscarPorNumeroGrupo(101).subscribe((response: HttpResponse<GrupoModel[]>) => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/buscarpornumero?numeroGrupo=101`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });

  it('should search grupos by idProfesor', () => {
    const mockResponse = [mockGrupo];

    service.buscarPorProfesor(5).subscribe((response: HttpResponse<GrupoModel[]>) => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/buscarporprofesor?idProfesor=5`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });

  it('should create a grupo', () => {
    const successMessage = 'Grupo creado con éxito';

    service.crearGrupo(mockGrupo).subscribe((res: string) => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/crear`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockGrupo);
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should update a grupo passing the full model object', () => {
    const successMessage = 'Grupo actualizado con éxito';

    service.actualizarGrupo(mockGrupo).subscribe((res: string) => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/actualizar`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(mockGrupo);
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should update a grupo passing id and partial data separately', () => {
    const successMessage = 'Grupo actualizado con éxito';
    const partialData = { cupos: 35 };

    service.actualizarGrupo(1, partialData).subscribe((res: string) => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/actualizar`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual({ ...partialData, id: 1 });
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should delete a grupo by id using eliminarGrupo alias', () => {
    const successMessage = 'Grupo eliminado correctamente';

    service.eliminarGrupo(1).subscribe((res: string) => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/eliminarporid?grupoId=1`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should delete a grupo by numero', () => {
    const successMessage = 'Grupos eliminados por número';

    service.eliminarGrupoPorNumero(101).subscribe((res: string) => {
      expect(res).toBe(successMessage);
    });

    const req = httpMock.expectOne(`${urlBase}/eliminarpornumero?numeroGrupo=101`);
    expect(req.request.method).toBe('DELETE');
    expect(req.request.responseType).toBe('text');

    req.flush(successMessage, { status: 200, statusText: 'OK' });
  });

  it('should get all grupos using mostrarTodo', () => {
    const mockResponse = [mockGrupo];

    service.mostrarTodo().subscribe((response: HttpResponse<GrupoModel[]>) => {
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${urlBase}/mostrartodo`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse, { status: 200, statusText: 'OK' });
  });
});
