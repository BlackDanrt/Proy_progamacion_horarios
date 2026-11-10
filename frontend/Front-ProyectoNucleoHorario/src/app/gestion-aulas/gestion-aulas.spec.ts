import { describe, it, expect, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { FormsModule, NgForm } from '@angular/forms';

import { GestionAulas } from './gestion-aulas';
import { SalonService } from '../services/salon.service';
import { SalonModel } from '../models/salon.model';
import { Bloque } from '../enums/bloque.enum';

describe('GestionAulas', () => {
  let component: GestionAulas;
  let fixture: ComponentFixture<GestionAulas>;
  let router: Router;

  let mockSalonService: any;
  let lastNavigatedRoute: any[] = [];
  let crearCalled = false;
  let actualizarCalled = false;
  let eliminarCalled = false;

  const mockSalones: SalonModel[] = [
    { id: 1, bloque: Bloque.A, numeroSalon: 101, capacidad: 30, tieneComputadores: true, tieneSillasMoviles: false },
    { id: 2, bloque: Bloque.B, numeroSalon: 202, capacidad: 40, tieneComputadores: false, tieneSillasMoviles: true }
  ];

  beforeEach(async () => {
    lastNavigatedRoute = [];
    crearCalled = false;
    actualizarCalled = false;
    eliminarCalled = false;

    mockSalonService = {
      mostrarTodo: () => of(new HttpResponse({ body: mockSalones, status: 200 })),
      crearSalon: () => {
        crearCalled = true;
        return of('Salón creado');
      },
      actualizarSalon: () => {
        actualizarCalled = true;
        return of('Salón actualizado');
      },
      eliminarSalon: () => {
        eliminarCalled = true;
        return of('Salón eliminado');
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
      declarations: [GestionAulas],
      providers: [
        { provide: SalonService, useValue: mockSalonService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(GestionAulas);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create the component successfully', () => {
    expect(component).toBeTruthy();
  });

  it('should load salones on ngOnInit', () => {
    component.ngOnInit();
    expect(component.registros.length).toBe(2);
    expect(component.registrosFiltrados.length).toBe(2);
    expect(component.cargando).toBe(false);
  });

  it('should handle error when loading salones fails', () => {
    mockSalonService.mostrarTodo = () => throwError(() => new Error('Server error'));
    component.cargarSalones();
    expect(component.error).toBe('No se pudieron cargar los registros de salones desde el servidor.');
    expect(component.cargando).toBe(false);
  });

  it('should filter salones correctly', () => {
    component.registros = mockSalones;
    component.onFiltroChange('101');
    expect(component.registrosFiltrados.length).toBe(1);
    expect(component.registrosFiltrados[0].numeroSalon).toBe(101);
  });

  it('should open and close modal correctly', () => {
    component.crearNuevoRegistro();
    expect(component.mostrarModalRegistro).toBe(true);
    expect(component.modoEdicion).toBe(false);

    component.cerrarModalRegistro();
    expect(component.mostrarModalRegistro).toBe(false);
  });

  it('should set up edit modal correctly', () => {
    const salon = mockSalones[0];
    component.abrirModalEdicion(salon);
    expect(component.mostrarModalRegistro).toBe(true);
    expect(component.modoEdicion).toBe(true);
    expect(component.nuevoRegistro.numeroSalon).toBe(101);
  });

  it('should toggle user menu and show status', () => {
    const event = new Event('click');
    expect(component.mostrarMenuUsuario()).toBe(false);

    component.toggleMenuUsuario(event);
    expect(component.mostrarMenuUsuario()).toBe(true);

    component.toggleMenuUsuario(event);
    expect(component.mostrarMenuUsuario()).toBe(false);
  });

  it('should close session and navigate to login', () => {
    component.cerrarSesion();
    expect(lastNavigatedRoute).toEqual(['/login']);
  });

  it('should handle pagination correctly', () => {
    component.registrosFiltrados = Array.from({ length: 15 }, (_, i) => ({
      bloque: Bloque.A,
      numeroSalon: 100 + i,
      capacidad: 30,
      tieneComputadores: false,
      tieneSillasMoviles: false
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
      bloque: Bloque.A,
      numeroSalon: 303,
      capacidad: 35,
      tieneComputadores: true,
      tieneSillasMoviles: true
    };

    const mockForm = { invalid: false } as NgForm;
    component.guardarRegistro(mockForm);

    expect(crearCalled).toBe(true);
    expect(component.guardando).toBe(false);
  });
});
