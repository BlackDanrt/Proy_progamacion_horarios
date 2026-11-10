import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';

import { ProfesorService } from './profesor.service';
import { environment } from '../../environments/environment';
import { Escalafon } from '../enums/escalafon.enum';
import { Especialidad } from '../enums/especialidad.enum';
import { TipoVinculacion } from '../enums/tipoViculacion.enum';
import { Dia } from '../enums/dia.enum';

describe('ProfesorService', () => {
  let service: ProfesorService;
  let httpMock: HttpTestingController;
  const urlBase = `${environment.apiUrl}/profesor`;

  beforeEach(() => {
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProfesorService]
    });
    service = TestBed.inject(ProfesorService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should search profesor by correo', () => {
    const correo = 'docente@unbosque.edu.co';
    service.buscarPorCorreo(correo).subscribe(response => {
      expect(response.status).toBe(200);
    });

    const req = httpMock.expectOne(r => r.url === `${urlBase}/buscarporcorreo` && r.method === 'GET');
    expect(req.request.params.get('correo')).toBe(correo);
    req.flush({}, { status: 200, statusText: 'OK' });
  });

  it('should search profesor by documento', () => {
    const documento = 123456;
    service.buscarPorDocumento(documento).subscribe(response => {
      expect(response.status).toBe(200);
    });

    const req = httpMock.expectOne(r => r.url === `${urlBase}/buscarpordocumento` && r.method === 'GET');
    expect(req.request.params.get('documento')).toBe(documento.toString());
    req.flush({}, { status: 200, statusText: 'OK' });
  });

  it('should search profesores by disponibilidad', () => {
    service.buscarPorDisponibilidad(Dia.LUNES, '08:00', '10:00').subscribe(response => {
      expect(response.status).toBe(200);
    });

    const req = httpMock.expectOne(r => r.url === `${urlBase}/buscarpordisponibilidad` && r.method === 'GET');
    expect(req.request.params.get('dia')).toBe(Dia.LUNES);
    req.flush([], { status: 200, statusText: 'OK' });
  });

  it('should search profesores by escalafon', () => {
    service.buscarPorEscalafon(Escalafon.PROFESOR_TITULAR).subscribe(response => {
      expect(response.status).toBe(200);
    });

    const req = httpMock.expectOne(r => r.url === `${urlBase}/buscarporescalafon` && r.method === 'GET');
    expect(req.request.params.get('escalafon')).toBe(Escalafon.PROFESOR_TITULAR);
    req.flush([], { status: 200, statusText: 'OK' });
  });

  it('should search profesores by especialidad', () => {
    service.buscarPorEspecialidad(Especialidad.DESARROLLO_SOFTWARE).subscribe(response => {
      expect(response.status).toBe(200);
    });

    const req = httpMock.expectOne(r => r.url === `${urlBase}/buscarporespecialidad` && r.method === 'GET');
    expect(req.request.params.get('especialidad')).toBe(Especialidad.DESARROLLO_SOFTWARE);
    req.flush([], { status: 200, statusText: 'OK' });
  });

  it('should search profesores by tipoVinculacion', () => {
    service.buscarPorTipoVinculacion(TipoVinculacion.TIEMPO_COMPLETO).subscribe(response => {
      expect(response.status).toBe(200);
    });

    const req = httpMock.expectOne(r => r.url === `${urlBase}/buscarporvinculacion` && r.method === 'GET');
    expect(req.request.params.get('tipoVinculacion')).toBe(TipoVinculacion.TIEMPO_COMPLETO);
    req.flush([], { status: 200, statusText: 'OK' });
  });

  it('should get all profesores using mostrarTodo and mostrarTodoAdmin alias', () => {
    service.mostrarTodo().subscribe(response => {
      expect(response.status).toBe(200);
    });

    let req = httpMock.expectOne(`${urlBase}/mostrartodo`);
    expect(req.request.method).toBe('GET');
    req.flush([], { status: 200, statusText: 'OK' });

    service.mostrarTodoAdmin().subscribe(response => {
      expect(response.status).toBe(200);
    });

    req = httpMock.expectOne(`${urlBase}/mostrartodo`);
    expect(req.request.method).toBe('GET');
    req.flush([], { status: 200, statusText: 'OK' });
  });

  it('should create a profesor', () => {
    const profesor: any = { nombre: 'Test' };
    service.crearProfesor(profesor).subscribe(res => {
      expect(res).toBe('Creado');
    });

    const req = httpMock.expectOne(`${urlBase}/crear`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(profesor);
    req.flush('Creado');
  });

  it('should update profesor information using actualizarInformacionProfesor and actualizarProfesor alias', () => {
    const profesor: any = { nombre: 'Test Update' };

    service.actualizarInformacionProfesor(profesor).subscribe(res => {
      expect(res).toBe('Actualizado');
    });
    let req = httpMock.expectOne(`${urlBase}/actualizarinformacion`);
    expect(req.request.method).toBe('PUT');
    req.flush('Actualizado');

    service.actualizarProfesor(profesor).subscribe(res => {
      expect(res).toBe('Actualizado');
    });
    req = httpMock.expectOne(`${urlBase}/actualizarinformacion`);
    expect(req.request.method).toBe('PUT');
    req.flush('Actualizado');
  });

  it('should assign disponibilidad horaria', () => {
    service.asignarDisponibilidadHoraria(1, Dia.LUNES, '08:00', '10:00').subscribe(res => {
      expect(res).toBe('Asignado');
    });

    const req = httpMock.expectOne(r => r.url === `${urlBase}/asignardisponibilidadhoraria` && r.method === 'PUT');
    expect(req.request.params.get('idProfesor')).toBe('1');
    req.flush('Asignado');
  });

  it('should update disponibilidad horaria', () => {
    service.actualizarDisponibilidadHoraria(1, 10, Dia.MARTES, '09:00', '11:00').subscribe(res => {
      expect(res).toBe('Actualizado');
    });

    const req = httpMock.expectOne(r => r.url === `${urlBase}/actualizardisponibilidadhoraria` && r.method === 'PUT');
    expect(req.request.params.get('idProfesor')).toBe('1');
    req.flush('Actualizado');
  });

  it('should delete a profesor by id', () => {
    service.eliminarProfesor(5).subscribe(res => {
      expect(res).toBe('Eliminado');
    });

    const req = httpMock.expectOne(r => r.url === `${urlBase}/eliminar` && r.method === 'DELETE');
    expect(req.request.params.get('id')).toBe('5');
    req.flush('Eliminado');
  });

  it('should save disponibilidad', () => {
    const franjas: any[] = [];
    service.guardarDisponibilidad(1, franjas).subscribe(res => {
      expect(res).toBe('Guardado');
    });

    const req = httpMock.expectOne(r => r.url === `${urlBase}/guardardisponibilidad` && r.method === 'PUT');
    expect(req.request.params.get('idProfesor')).toBe('1');
    expect(req.request.body).toEqual(franjas);
    req.flush('Guardado');
  });
});
