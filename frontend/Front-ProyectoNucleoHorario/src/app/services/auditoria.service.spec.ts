import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { HttpResponse } from '@angular/common/http';

import { AuditoriaService } from './auditoria.service';
import { AuditoriaModel } from '../models/auditoria.model';
import { environment } from '../../environments/environment';

describe('AuditoriaService', () => {
  let service: AuditoriaService;
  let httpMock: HttpTestingController;
  const urlBase = `${environment.apiUrl}/auditoria`;

  const mockAuditorias = [
    { id: 1, accion: 'LOGIN', usuario: 'admin@unbosque.edu.co', fecha: '2026-11-08' }
  ] as unknown as AuditoriaModel[];

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuditoriaService]
    });

    service = TestBed.inject(AuditoriaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all auditorias using mostrarTodo', () => {
    service.mostrarTodo().subscribe(response => {
      expect(response).toBeInstanceOf(HttpResponse);
      expect(response.status).toBe(200);
      expect(response.body).toEqual(mockAuditorias);
    });

    const req = httpMock.expectOne(`${urlBase}/mostrar`);
    expect(req.request.method).toBe('GET');
    req.flush(mockAuditorias, { status: 200, statusText: 'OK' });
  });
});
