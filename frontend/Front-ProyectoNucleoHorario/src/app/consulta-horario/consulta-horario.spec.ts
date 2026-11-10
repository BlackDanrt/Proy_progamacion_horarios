import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { HttpResponse } from '@angular/common/http';
import { of, throwError, BehaviorSubject } from 'rxjs';

import { ConsultaHorario } from './consulta-horario';
import { GrupoService } from '../services/grupo.service';
import { SesionService } from '../services/sesion.service';

describe('ConsultaHorario', () => {
  let component: ConsultaHorario;
  let fixture: ComponentFixture<ConsultaHorario>;
  let queryParamMapSubject: BehaviorSubject<any>;

  let buscarPorProfesorCalled = false;
  const mockGruposData = [
    {
      numeroGrupo: 1,
      capacidadMaxima: 30,
      inscritos: 10,
      asignatura: { nombre: 'Matemáticas' },
      profesor: { id: 1, nombre: 'Juan', apellido: 'Perez', especialidades: ['Ingeniería'] },
      salon: { numeroSalon: 101, bloque: 'A' },
      sesiones: [
        { dia: 'LUNES', horaInicio: '08:00', horaFin: '10:00' }
      ]
    }
  ];

  const mockGrupoService = {
    buscarPorProfesor: () => {
      buscarPorProfesorCalled = true;
      return of(new HttpResponse({ body: mockGruposData, status: 200 }));
    }
  };

  const mockSesionService = {
    getProfesorId: () => 1,
    cerrarSesion: () => {}
  };

  beforeEach(async () => {
    buscarPorProfesorCalled = false;
    queryParamMapSubject = new BehaviorSubject(convertToParamMap({ id: '1' }));

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      declarations: [ConsultaHorario],
      providers: [
        { provide: GrupoService, useValue: mockGrupoService },
        { provide: SesionService, useValue: mockSesionService },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { data: { modo: 'admin' } },
            queryParamMap: queryParamMapSubject.asObservable()
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ConsultaHorario);
    component = fixture.componentInstance;
  });

  it('should create the component successfully', () => {
    expect(component).toBeTruthy();
  });

  it('should load schedule successfully on init as admin', () => {
    fixture.detectChanges();
    expect(buscarPorProfesorCalled).toBe(true);
    expect(component.clases.length).toBe(1);
    expect(component.docente?.nombre).toBe('Juan Perez');
    expect(component.cargando).toBe(false);
  });

  it('should handle error when loading schedule fails', () => {
    const originalService = mockGrupoService.buscarPorProfesor;
    mockGrupoService.buscarPorProfesor = () => throwError(() => new Error('Error de servidor'));

    component.ngOnInit();

    expect(component.mensajeVacio).toContain('No se pudo cargar el horario');
    expect(component.cargando).toBe(false);

    mockGrupoService.buscarPorProfesor = originalService;
  });

  it('should toggle user menu and close on outside click', () => {
    const event = new Event('click');
    expect(component.mostrarMenuUsuario).toBe(false);

    component.toggleMenuUsuario(event);
    expect(component.mostrarMenuUsuario).toBe(true);

    component.cerrarMenuAlHacerClicFuera();
    expect(component.mostrarMenuUsuario).toBe(false);
  });
});
