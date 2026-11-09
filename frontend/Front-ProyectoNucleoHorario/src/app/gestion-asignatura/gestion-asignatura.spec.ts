import { describe, it, expect, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { FormsModule, NgForm } from '@angular/forms';

import { GestionAsignatura } from './gestion-asignatura';
import { AsignaturaService } from '../services/asignatura.service';
import { AsignaturaModel } from '../models/asignatura.model';

describe('GestionAsignatura', () => {
  let component: GestionAsignatura;
  let fixture: ComponentFixture<GestionAsignatura>;
  let router: Router;

  let mockAsignaturaService: any;
  let lastNavigatedRoute: any[] = [];
  let crearCalled = false;
  let actualizarCalled = false;
  let eliminarCalled = false;

  const mockAsignaturas: AsignaturaModel[] = [
    { nombre: 'Matemáticas', numCreditos: 4, frecuenciaSemanal: 3, requiereComputadores: false, requiereSillasMoviles: true },
    { nombre: 'Física', numCreditos: 3, frecuenciaSemanal: 2, requiereComputadores: true, requiereSillasMoviles: false }
  ];

  beforeEach(async () => {
    lastNavigatedRoute = [];
    crearCalled = false;
    actualizarCalled = false;
    eliminarCalled = false;

    mockAsignaturaService = {
      mostrarTodo: () => of(new HttpResponse({ body: mockAsignaturas, status: 200 })),
      crearAsignatura: () => {
        crearCalled = true;
        return of('Asignatura creada');
      },
      actualizarAsignatura: () => {
        actualizarCalled = true;
        return of('Asignatura actualizada');
      },
      eliminarAsignatura: () => {
        eliminarCalled = true;
        return of('Asignatura eliminada');
      }
    };

    const mockRouter = {
      navigate: (path: any[]) => {
        lastNavigatedRoute = path;
        return Promise.resolve(true);
      }
    };

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule],
      declarations: [GestionAsignatura],
      providers: [
        { provide: AsignaturaService, useValue: mockAsignaturaService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(GestionAsignatura);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create the component successfully', () => {
    expect(component).toBeTruthy();
  });

  it('should load asignaturas on ngOnInit', () => {
    component.ngOnInit();
    expect(component.registros.length).toBe(2);
    expect(component.registrosFiltrados.length).toBe(2);
    expect(component.cargando).toBe(false);
  });

  it('should handle error when loading asignaturas fails', () => {
    mockAsignaturaService.mostrarTodo = () => throwError(() => new Error('Server error'));
    component.cargarAsignaturas();
    expect(component.error).toBe('No se pudieron cargar los registros del servidor.');
    expect(component.cargando).toBe(false);
  });

  it('should filter asignaturas correctly', () => {
    component.registros = mockAsignaturas;
    component.onFiltroChange('Matemáticas');
    expect(component.registrosFiltrados.length).toBe(1);
    expect(component.registrosFiltrados[0].nombre).toBe('Matemáticas');
  });

  it('should open and close modal correctly', () => {
    component.abrirModalRegistro();
    expect(component.mostrarModalRegistro).toBe(true);
    expect(component.modoEdicion).toBe(false);

    component.cerrarModalRegistro();
    expect(component.mostrarModalRegistro).toBe(false);
  });

  it('should set up update modal correctly', () => {
    const asignatura = mockAsignaturas[0];
    component.actualizarRegistro(asignatura);
    expect(component.mostrarModalRegistro).toBe(true);
    expect(component.modoEdicion).toBe(true);
    expect(component.nuevoRegistro.nombre).toBe('Matemáticas');
  });

  it('should toggle user menu and handle click outside', () => {
    const event = new Event('click');
    expect(component.mostrarMenuUsuario()).toBe(false);

    component.toggleMenuUsuario(event);
    expect(component.mostrarMenuUsuario()).toBe(true);

    component.cerrarMenuUsuario();
    expect(component.mostrarMenuUsuario()).toBe(false);
  });

  it('should close session correctly', () => {
    component.cerrarSesion();
    expect(lastNavigatedRoute).toEqual(['/login']);
  });

  it('should handle pagination correctly', () => {
    component.registrosFiltrados = Array.from({ length: 15 }, (_, i) => ({
      nombre: `Asignatura ${i + 1}`,
      numCreditos: 3,
      frecuenciaSemanal: 2,
      requiereComputadores: false,
      requiereSillasMoviles: false
    }));
    component.onRegistrosPorPaginaChange(5);

    expect(component.paginas().length).toBe(3);
    expect(component.registrosPaginados().length).toBe(5);

    component.cambiarPagina(2);
    expect(component.paginaActual).toBe(2);
  });

  it('should save new record when form is valid', () => {
    component.modoEdicion = false;
    component.nuevoRegistro = {
      nombre: 'Química',
      numCreditos: 3,
      frecuenciaSemanal: 2,
      requiereComputadores: false,
      requiereSillasMoviles: false
    };

    const mockForm = { invalid: false } as NgForm;
    component.guardarRegistro(mockForm);

    expect(crearCalled).toBe(true);
    expect(component.guardando).toBe(false);
  });
});
