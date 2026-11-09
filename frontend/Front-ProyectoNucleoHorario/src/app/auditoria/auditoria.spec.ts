import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';

import { Auditoria } from './auditoria';
import { AuditoriaService } from '../services/auditoria.service';

describe('Auditoria', () => {
  let component: Auditoria;
  let fixture: ComponentFixture<Auditoria>;
  let mockAuditoriaService: any;

  const mockAuditoriaData = [
    {
      id: 1,
      idUsuario: 101,
      nombreUsuario: 'Juan Perez',
      accion: 'INICIO_SESION',
      esAdmin: true,
      servicioUsado: 'MODULO_HORARIOS',
      fecha: '2026-11-08T10:00:00'
    }
  ];

  beforeEach(async () => {
    mockAuditoriaService = {
      mostrarTodo: () => of(new HttpResponse({ body: mockAuditoriaData, status: 200 }))
    };

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      declarations: [Auditoria],
      providers: [
        { provide: AuditoriaService, useValue: mockAuditoriaService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Auditoria);
    component = fixture.componentInstance;
  });

  it('should create the component successfully', () => {
    expect(component).toBeTruthy();
  });

  it('should load records successfully on ngOnInit', () => {
    component.ngOnInit();

    expect(component.registros().length).toBe(1);
    expect(component.registros()[0].nombreUsuario).toBe('Juan Perez');
    expect(component.registros()[0].rol).toBe('Administrador');
    expect(component.cargando()).toBe(false);
  });

  it('should handle error when loading records fails', () => {
    mockAuditoriaService.mostrarTodo = () => throwError(() => new Error('Error de servidor'));

    component.cargarRegistros();

    expect(component.error()).toBe('No se pudieron cargar los registros de auditoría.');
    expect(component.cargando()).toBe(false);
  });

  it('should filter records correctly based on search term', () => {
    component.registros.set([
      { id: 1, idUsuario: 1, nombreUsuario: 'Ana Gomez', accion: 'Crear', rol: 'Docente', servicioUsado: 'Salones', fecha: '2026-11-08' },
      { id: 2, idUsuario: 2, nombreUsuario: 'Carlos Ruiz', accion: 'Editar', rol: 'Administrador', servicioUsado: 'Horarios', fecha: '2026-11-08' }
    ]);

    component.onFiltroChange('Ana');

    expect(component.registrosFiltrados().length).toBe(1);
    expect(component.registrosFiltrados()[0].nombreUsuario).toBe('Ana Gomez');
    expect(component.paginaActual()).toBe(1);
  });

  it('should change page correctly within available range', () => {
    component.registros.set(Array.from({ length: 15 }, (_, i) => ({
      id: i + 1,
      idUsuario: i,
      nombreUsuario: `Usuario ${i}`,
      accion: 'Accion',
      rol: 'Docente' as const,
      servicioUsado: 'Servicio',
      fecha: '2026-11-08'
    })));
    component.registrosPorPagina.set(5);

    expect(component.totalPaginas()).toBe(3);

    component.cambiarPagina(2);
    expect(component.paginaActual()).toBe(2);

    component.cambiarPagina(10);
    expect(component.paginaActual()).toBe(2);
  });

  it('should toggle user menu and handle close click outside', () => {
    const mockEvent = new Event('click');

    expect(component.mostrarMenuUsuario()).toBe(false);

    component.toggleMenuUsuario(mockEvent);
    expect(component.mostrarMenuUsuario()).toBe(true);

    component.cerrarMenuAlHacerClicFuera();
    expect(component.mostrarMenuUsuario()).toBe(false);
  });
});
