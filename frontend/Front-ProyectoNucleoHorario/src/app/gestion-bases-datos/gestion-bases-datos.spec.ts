import { describe, it, expect, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';

import { GestionBasesDatos, RegistroBaseDatos } from './gestion-bases-datos';
import { ProfesorService } from '../services/profesor.service';
import { AdministradorService } from '../services/administrador.service';

describe('GestionBasesDatos', () => {
  let component: GestionBasesDatos;
  let fixture: ComponentFixture<GestionBasesDatos>;
  let router: Router;

  let mockProfesorService: any;
  let mockAdministradorService: any;

  let lastNavigatedRoute: any[] = [];
  let lastNavigatedExtras: any = null;
  let actualizarProfesorCalled = false;
  let actualizarAdminCalled = false;
  let eliminarProfesorCalled = false;
  let eliminarAdminCalled = false;

  const mockProfesores = [
    { id: 1, nombre: 'Juan', apellido: 'Perez', especialidades: ['MATEMATICAS'], horasMaximasSemanales: 20 }
  ];

  const mockAdministradores = [
    { id: 10, nombre: 'Maria', apellido: 'Gomez' }
  ];

  beforeEach(async () => {
    lastNavigatedRoute = [];
    lastNavigatedExtras = null;
    actualizarProfesorCalled = false;
    actualizarAdminCalled = false;
    eliminarProfesorCalled = false;
    eliminarAdminCalled = false;

    mockProfesorService = {
      mostrarTodo: () => of(new HttpResponse({ body: mockProfesores, status: 200 })),
      actualizarInformacionProfesor: () => {
        actualizarProfesorCalled = true;
        return of('Actualizado');
      },
      eliminarProfesor: () => {
        eliminarProfesorCalled = true;
        return of('Eliminado');
      }
    };

    mockAdministradorService = {
      mostrarTodo: () => of(new HttpResponse({ body: mockAdministradores, status: 200 })),
      actualizarAdministrador: () => {
        actualizarAdminCalled = true;
        return of('Actualizado');
      },
      eliminarAdministrador: () => {
        eliminarAdminCalled = true;
        return of('Eliminado');
      }
    };

    const mockRouter = {
      navigate: (path: any[], extras?: any) => {
        lastNavigatedRoute = path;
        lastNavigatedExtras = extras;
        return Promise.resolve(true);
      }
    };

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      declarations: [GestionBasesDatos],
      providers: [
        { provide: ProfesorService, useValue: mockProfesorService },
        { provide: AdministradorService, useValue: mockAdministradorService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(GestionBasesDatos);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create the component successfully', () => {
    expect(component).toBeTruthy();
  });

  it('should load records (professors and administrators) on ngOnInit', () => {
    component.ngOnInit();
    expect(component.registros().length).toBe(2);
    expect(component.registros()[0].nombre).toBe('Juan Perez');
    expect(component.registros()[1].nombre).toBe('Maria Gomez');
    expect(component.cargando()).toBe(false);
  });

  it('should handle loading errors gracefully', () => {
    mockProfesorService.mostrarTodo = () => throwError(() => new Error('Error'));
    mockAdministradorService.mostrarTodo = () => throwError(() => new Error('Error'));

    component.cargarRegistros();
    expect(component.error()).toContain('No se pudieron cargar');
    expect(component.cargando()).toBe(false);
  });

  it('should filter records correctly', () => {
    component.registros.set([
      { clave: 'Docente-1', id: 1, nombre: 'Ana Gomez', rol: 'Docente', especialidad: 'Sistemas', cargaAsignada: '10 horas/semana' },
      { clave: 'Administrador-2', id: 2, nombre: 'Carlos Ruiz', rol: 'Administrador', especialidad: '—', cargaAsignada: '—' }
    ]);

    component.onFiltroChange('Ana');
    expect(component.registrosFiltrados().length).toBe(1);
    expect(component.registrosFiltrados()[0].nombre).toBe('Ana Gomez');
  });

  it('should open and close creation modal correctly', () => {
    component.crearNuevoRegistro();
    expect(component.mostrarModalRegistro()).toBe(true);

    component.cerrarModalRegistro();
    expect(component.mostrarModalRegistro()).toBe(false);
  });

  it('should navigate to schedule view for teachers', () => {
    const docReg: RegistroBaseDatos = {
      clave: 'Docente-1',
      id: 1,
      nombre: 'Juan Perez',
      rol: 'Docente',
      especialidad: 'Matemáticas',
      cargaAsignada: '20 horas/semana'
    };

    component.visualizarHorario(docReg);
    expect(lastNavigatedRoute).toEqual(['/consulta-horario']);
    expect(lastNavigatedExtras).toEqual({ queryParams: { id: 1 } });
  });

  it('should handle updating records based on role', () => {
    component.ngOnInit();
    const adminReg = component.registros()[1]; // Administrador

    component.actualizarRegistro(adminReg);
    expect(component.registroEditando()).toEqual(adminReg);
    expect(component.editNombre()).toBe('Maria');

    component.cancelarEdicion();
    expect(component.registroEditando()).toBeNull();
  });

  it('should save administrator edit successfully', () => {
    component.ngOnInit();
    const adminReg = component.registros()[1];
    component.actualizarRegistro(adminReg);

    component.editNombre.set('Maria Updated');
    component.guardarEdicion();

    expect(actualizarAdminCalled).toBe(true);
  });

  it('should delete a professor successfully', () => {
    component.registros.set([
      { clave: 'Docente-1', id: 1, nombre: 'Juan Perez', rol: 'Docente', especialidad: 'Sistemas', cargaAsignada: '10 horas/semana' }
    ]);

    const originalConfirm = window.confirm;
    window.confirm = () => true;

    const reg = component.registros()[0];
    component.eliminarRegistro(reg);

    expect(eliminarProfesorCalled).toBe(true);
    expect(component.registros().length).toBe(0);

    window.confirm = originalConfirm;
  });

  it('should toggle user menu and close on outside click', () => {
    const event = new Event('click');
    expect(component.mostrarMenuUsuario()).toBe(false);

    component.toggleMenuUsuario(event);
    expect(component.mostrarMenuUsuario()).toBe(true);

    component.cerrarMenuAlHacerClicFuera();
    expect(component.mostrarMenuUsuario()).toBe(false);
  });

  it('should close session and navigate to login', () => {
    component.cerrarSesion();
    expect(component.mostrarMenuUsuario()).toBe(false);
    expect(lastNavigatedRoute).toEqual(['/loginn']);
  });
});
