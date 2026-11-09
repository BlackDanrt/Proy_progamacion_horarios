import { describe, it, expect, beforeEach, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { HttpResponse } from '@angular/common/http';

import { EdicionHorarios } from './edicion-horarios';
import { GrupoService } from '../services/grupo.service';
import { SalonService } from '../services/salon.service';
import { ProfesorService } from '../services/profesor.service';
import { SesionService } from '../services/sesion.service';

describe('EdicionHorarios', () => {
  let component: EdicionHorarios;
  let fixture: ComponentFixture<EdicionHorarios>;
  let router: Router;
  let mockGrupoService: any;
  let mockSalonService: any;
  let mockProfesorService: any;
  let mockSesionService: any;
  let lastNavigatedRoute: any[] = [];
  let updateCalled = false;
  let deleteCalled = false;

  beforeEach(async () => {
    TestBed.resetTestingModule();
    lastNavigatedRoute = [];
    updateCalled = false;
    deleteCalled = false;

    mockGrupoService = {
      mostrarTodo: () => of(new HttpResponse({ body: [], status: 200 })),
      actualizarGrupo: () => {
        updateCalled = true;
        return of('Grupo actualizado');
      },
      eliminarGrupo: () => {
        deleteCalled = true;
        return of('Grupo eliminado');
      }
    };

    mockSalonService = {
      mostrarTodo: () => of(new HttpResponse({
        body: [
          { id: 1, bloque: 'A' as any, numeroSalon: 101, capacidad: 30, tieneComputadores: true, tieneSillasMoviles: false },
          { id: 2, bloque: 'B' as any, numeroSalon: 201, capacidad: 40, tieneComputadores: false, tieneSillasMoviles: true }
        ],
        status: 200
      }))
    };

    mockProfesorService = {
      mostrarTodo: () => of(new HttpResponse({
        body: [{ id: 1, nombre: 'Juan', apellido: 'Pérez' }],
        status: 200
      }))
    };

    mockSesionService = {
      cerrarSesion: () => {}
    };

    const mockRouter = {
      navigate: (path: any[]) => {
        lastNavigatedRoute = path;
        return Promise.resolve(true);
      },
      navigateByUrl: () => Promise.resolve(true)
    };

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule],
      declarations: [EdicionHorarios],
      providers: [
        { provide: GrupoService, useValue: mockGrupoService },
        { provide: SalonService, useValue: mockSalonService },
        { provide: ProfesorService, useValue: mockProfesorService },
        { provide: SesionService, useValue: mockSesionService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EdicionHorarios);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create the component successfully', () => {
    expect(component).toBeTruthy();
  });

  it('should load catalogs and groups on ngOnInit', () => {
    component.ngOnInit();
    expect(component.listaProfesores.length).toBe(1);
    expect(component.listaBloques.length).toBeGreaterThan(0);
  });

  it('should open and close edit modal correctly', () => {
    const grupoMock: any = { id: 1, nombre: 'Grupo A', capacidadMaxima: 30, salon: { id: 1, bloque: 'A' } };
    component.abrirModalEditar(grupoMock);
    expect(component.mostrarModalEditar).toBe(true);
    expect(component.grupoEnEdicion).toEqual(grupoMock);

    component.cerrarModalEditar();
    expect(component.mostrarModalEditar).toBe(false);
    expect(component.grupoEnEdicion).toBeNull();
  });

  it('should filter available salons on block change and validate requirements', () => {
    component.grupoEnEdicion = {
      asignatura: { requiereComputadores: true, requiereSillasMoviles: false }
    };
    component.bloqueSeleccionadoModal = 'A';
    component.mapaSalones = {
      'A': [
        { id: 1, bloque: 'A' as any, numeroSalon: 101, capacidad: 30, tieneComputadores: true, tieneSillasMoviles: false },
        { id: 2, bloque: 'A' as any, numeroSalon: 102, capacidad: 30, tieneComputadores: false, tieneSillasMoviles: false }
      ]
    };

    component.onBloqueChange();
    expect(component.salonesDisponibles.length).toBe(1);
    expect(component.salonesDisponibles[0].id).toBe(1);
  });

  it('should validate capacity with tolerance when saving group edit', () => {
    component.listaSalonesObj = [
      { id: 1, bloque: 'A' as any, numeroSalon: 101, capacidad: 20, tieneComputadores: false, tieneSillasMoviles: false }
    ];
    component.grupoEnEdicion = { id: 1, asignatura: {} };
    component.salonSeleccionadoModalId = 1;
    component.nuevaCapacidad = 35; // Supera la capacidad con tolerancia

    component.guardarEdicionGrupo();
    expect(component.mensajeErrorCapacidad).not.toBeNull();
    expect(updateCalled).toBe(false);
  });

  it('should successfully save group edit when capacity and requirements are valid', () => {
    component.listaSalonesObj = [
      { id: 1, bloque: 'A' as any, numeroSalon: 101, capacidad: 40, tieneComputadores: false, tieneSillasMoviles: false }
    ];
    component.grupoEnEdicion = { id: 1, asignatura: {} };
    component.salonSeleccionadoModalId = 1;
    component.nuevaCapacidad = 30;

    component.guardarEdicionGrupo();
    expect(updateCalled).toBe(true);
  });

  it('should handle group deletion workflow via confirmation modal', () => {
    component.solicitarEliminarGrupo(5);
    expect(component.confirmacion).not.toBeNull();

    component.aceptarConfirmacion();
    expect(deleteCalled).toBe(true);
    expect(component.confirmacion).toBeNull();
  });

  it('should manage toasts correctly', () => {
    component.mostrarToast('exito', 'Prueba toast');
    expect(component.toasts.length).toBe(1);
    expect(component.toasts[0].mensaje).toBe('Prueba toast');

    const id = component.toasts[0].id;
    component.cerrarToast(id);
    expect(component.toasts.length).toBe(0);
  });

  it('should toggle user menu and close on outside click', () => {
    expect(component.mostrarMenuUsuario).toBe(false);
    const mockEvent = new Event('click');
    vi.spyOn(mockEvent, 'stopPropagation').mockImplementation(() => {});

    component.toggleMenuUsuario(mockEvent);
    expect(component.mostrarMenuUsuario).toBe(true);

    component.cerrarMenuAlHacerClicFuera();
    expect(component.mostrarMenuUsuario).toBe(false);
  });

  it('should close session and navigate to login', () => {
    component.cerrarSesion();
    expect(lastNavigatedRoute).toEqual(['/loginn']);
  });
});
