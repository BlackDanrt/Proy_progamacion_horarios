import { describe, it, expect, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

import { CrearAdministrador } from './crear-administrador';
import { AdministradorService } from '../services/administrador.service';
import { AuthAdminService } from '../services/auth-admin';

describe('CrearAdministrador', () => {
  let component: CrearAdministrador;
  let fixture: ComponentFixture<CrearAdministrador>;
  let router: Router;
  let mockAdministradorService: any;
  let mockAuthAdminService: any;
  let lastNavigatedRoute: any[] = [];
  let crearAdminCalled = false;

  beforeEach(async () => {
    lastNavigatedRoute = [];
    crearAdminCalled = false;

    mockAdministradorService = {
      crearAdministrador: () => {
        crearAdminCalled = true;
        return of({ message: 'Administrador creado con éxito' });
      }
    };

    mockAuthAdminService = {
      cerrarSesionAdmin: () => {}
    };

    const mockRouter = {
      navigate: (path: any[]) => {
        lastNavigatedRoute = path;
        return Promise.resolve(true);
      }
    };

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule],
      declarations: [CrearAdministrador],
      providers: [
        { provide: AdministradorService, useValue: mockAdministradorService },
        { provide: AuthAdminService, useValue: mockAuthAdminService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CrearAdministrador);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create the component successfully', () => {
    expect(component).toBeTruthy();
  });

  it('should show error modal if required fields are missing', () => {
    component.nombre = '';
    component.apellido = '';
    component.guardarAdministrador();

    expect(component.modalError.visible).toBe(true);
    expect(component.modalError.titulo).toBe('Campos incompletos');
  });

  it('should successfully create administrator when all fields are valid', () => {
    component.nombre = 'Carlos';
    component.apellido = 'Pérez';
    component.documento = 12345678;
    component.codigoAdministrador = 'ADMIN01';
    component.correo = 'carlos@unbosque.edu.co';
    component.contrasenia = 'password123';

    component.guardarAdministrador();

    expect(crearAdminCalled).toBe(true);
    expect(component.modalError.visible).toBe(true);
    expect(component.modalError.titulo).toBe('¡Registro Exitoso!');
  });

  it('should handle error response when crearAdministrador fails', () => {
    mockAdministradorService.crearAdministrador = () =>
      throwError(() => new HttpErrorResponse({ error: 'Error de servidor', status: 500 }));

    component.nombre = 'Carlos';
    component.apellido = 'Pérez';
    component.documento = 12345678;
    component.codigoAdministrador = 'ADMIN01';
    component.correo = 'carlos@unbosque.edu.co';
    component.contrasenia = 'password123';

    component.guardarAdministrador();

    expect(component.modalError.visible).toBe(true);
    expect(component.modalError.titulo).toBe('Error de validación');
  });

  it('should toggle password visibility', () => {
    expect(component.mostrarContrasenia).toBe(false);
    component.toggleMostrarContrasenia();
    expect(component.mostrarContrasenia).toBe(true);
  });

  it('should toggle user menu and close on outside click', () => {
    expect(component.mostrarMenuUsuario()).toBe(false);

    const fakeEvent = new Event('click');
    component.toggleMenuUsuario(fakeEvent);
    expect(component.mostrarMenuUsuario()).toBe(true);

    component.cerrarMenuAlHacerClicFuera();
    expect(component.mostrarMenuUsuario()).toBe(false);
  });

  it('should navigate to routes correctly', () => {
    component.navegarA('/inicio-administrador');
    expect(lastNavigatedRoute).toEqual(['/inicio-administrador']);
  });

  it('should handle cancelar and volverAGestion', () => {
    component.cancelar();
    expect(lastNavigatedRoute).toEqual(['/gestion-bases-datos']);

    component.volverAGestion();
    expect(lastNavigatedRoute).toEqual(['/gestion-bases-datos']);
  });

  it('should close modal', () => {
    component.modalError.visible = true;
    component.cerrarModal();
    expect(component.modalError.visible).toBe(false);
  });

  it('should close session and navigate to login', () => {
    component.cerrarSesion();
    expect(lastNavigatedRoute).toEqual(['/loginn']);
  });
});
