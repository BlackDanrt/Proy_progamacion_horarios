import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { AdministradorService } from './administrador.service';
import { environment } from '../../environments/environment';

describe('AdministradorService', () => {
  let service: AdministradorService;
  let httpMock: HttpTestingController;
  const urlBase = `${environment.apiUrl}/administrador`;

  beforeEach(() => {
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AdministradorService]
    });
    service = TestBed.inject(AdministradorService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should search administrador by codigoAdministrador', () => {
    service.buscarPorCodigoAdministrador('ADMIN01').subscribe(res => expect(res.status).toBe(200));

    const req = httpMock.expectOne(r => r.url === `${urlBase}/buscarporcodigo` && r.method === 'GET');
    expect(req.request.params.get('codigoAdministrador')).toBe('ADMIN01');
    req.flush([], { status: 200, statusText: 'OK' });
  });

  it('should search administrador by correo', () => {
    service.buscarPorCorreo('admin@unbosque.edu.co').subscribe(res => expect(res.status).toBe(200));

    const req = httpMock.expectOne(r => r.url === `${urlBase}/buscarporcorreo` && r.method === 'GET');
    expect(req.request.params.get('correo')).toBe('admin@unbosque.edu.co');
    req.flush([], { status: 200, statusText: 'OK' });
  });

  it('should search administrador by documento', () => {
    service.buscarPorDocumento(12345).subscribe(res => expect(res.status).toBe(200));

    const req = httpMock.expectOne(r => r.url === `${urlBase}/buscarpordocumento` && r.method === 'GET');
    expect(req.request.params.get('documento')).toBe('12345');
    req.flush([], { status: 200, statusText: 'OK' });
  });

  it('should get all administradores using mostrarTodo and mostrarTodoAdmin alias', () => {
    service.mostrarTodo().subscribe(res => expect(res.status).toBe(200));
    let req = httpMock.expectOne(`${urlBase}/mostrartodo`);
    expect(req.request.method).toBe('GET');
    req.flush([], { status: 200, statusText: 'OK' });

    service.mostrarTodoAdmin().subscribe(res => expect(res.status).toBe(200));
    req = httpMock.expectOne(`${urlBase}/mostrartodo`);
    expect(req.request.method).toBe('GET');
    req.flush([], { status: 200, statusText: 'OK' });
  });

  it('should create an administrador with request params', () => {
    const admin: any = {
      nombre: 'Juan',
      apellido: 'Pérez',
      documento: 123,
      correo: 'juan@unbosque.edu.co',
      contrasenia: '123456',
      codigoAdministrador: 'A01',
      estado: true
    };

    service.crearAdministrador(admin).subscribe(res => expect(res).toBeTruthy());

    const req = httpMock.expectOne(r => r.url === `${urlBase}/crear` && r.method === 'POST');
    expect(req.request.params.get('nombre')).toBe('Juan');
    expect(req.request.params.get('correo')).toBe('juan@unbosque.edu.co');
    req.flush('Creado');
  });

  it('should update an administrador', () => {
    const admin: any = { id: 1, nombre: 'Juan Actualizado' };

    service.actualizarAdministrador(admin).subscribe(res => expect(res).toBeTruthy());

    const req = httpMock.expectOne(`${urlBase}/actualizar`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(admin);
    req.flush('Actualizado');
  });

  it('should delete an administrador by id', () => {
    service.eliminarAdministrador(1).subscribe(res => expect(res).toBeTruthy());

    const req = httpMock.expectOne(r => r.url === `${urlBase}/eliminar` && r.method === 'DELETE');
    expect(req.request.params.get('id')).toBe('1');
    req.flush('Eliminado');
  });
});
