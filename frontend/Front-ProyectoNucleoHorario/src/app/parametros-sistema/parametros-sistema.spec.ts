import { describe, it, expect, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';

import { ParametrosSistema } from './parametros-sistema';
import { ParametrosSistemaService } from '../services/parametrossistema.service';
import { SesionService } from '../services/sesion.service';
import { ParametrosSistemaModel } from '../models/parametrossistema.model';

describe('ParametrosSistema', () => {
  let component: ParametrosSistema;
  let fixture: ComponentFixture<ParametrosSistema>;
  let router: Router;

  let mockParametrosService: any;
  let mockSesionService: any;

  let lastNavigatedRoute: any[] = [];
  let esAdminFlag = true;
  let actualizarParametrosCalled = false;
  let cerrarSesionCalled = false;

  const mockParametros: ParametrosSistemaModel = {
    id: 1,
    horaInicioEntreSemana: '07:00:00',
    horaFinEntreSemana: '22:00:00',
    horaInicioSabado: '07:00:00',
    horaFinSabado: '13:00:00',
    horaInicioAlmuerzo: '12:00:00',
    horaFinAlmuerzo: '13:00:00',
    maxSesionesPorSemana: 4,
    duracionSesionMinutos: 120,
    capacidadMaximaGrupo: 40,
    toleranciaCapacidad: 10,
    minEstudiantesParaAbrir: 10,
    exigirDiasNoConsecutivos: true
  };

  beforeEach(async () => {
    lastNavigatedRoute = [];
    esAdminFlag = true;
    actualizarParametrosCalled = false;
    cerrarSesionCalled = false;

    mockParametrosService = {
      obtenerParametros: () => of(new HttpResponse({ body: mockParametros, status: 200 })),
      actualizarParametros: () => {
        actualizarParametrosCalled = true;
        return of('Parámetros actualizados con éxito');
      }
    };

    mockSesionService = {
      esAdministrador: () => esAdminFlag,
      cerrarSesion: () => {
        cerrarSesionCalled = true;
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
      declarations: [ParametrosSistema],
      providers: [
        { provide: ParametrosSistemaService, useValue: mockParametrosService },
        { provide: SesionService, useValue: mockSesionService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ParametrosSistema);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
  });

  it('should create the component successfully', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to /inicio-administrador on ngOnInit if not an admin', () => {
    esAdminFlag = false;
    component.ngOnInit();
    expect(lastNavigatedRoute).toEqual(['/inicio-administrador']);
  });

  it('should load parameters successfully on ngOnInit if admin', () => {
    component.ngOnInit();
    expect(component.cargando).toBe(false);
    expect(component.horaInicioEntreSemana).toBe('07:00');
    expect(component.capacidadMaximaGrupo).toBe(40);
  });

  it('should handle error when loading parameters fails', () => {
    const errorResponse = new HttpErrorResponse({ status: 500, error: 'Error del servidor' });
    mockParametrosService.obtenerParametros = () => throwError(() => errorResponse);

    component.ngOnInit();

    expect(component.cargando).toBe(false);
    expect(component.mensaje?.tipo).toBe('error');
    expect(component.mensaje?.texto).toBe('Error del servidor');
  });

  it('should show error message if start time is greater than or equal to end time', () => {
    component.horaInicioEntreSemana = '22:00';
    component.horaFinEntreSemana = '07:00';

    component.guardar();

    expect(component.mensaje?.tipo).toBe('error');
    expect(component.mensaje?.texto).toContain('La hora de inicio entre semana debe ser anterior');
    expect(actualizarParametrosCalled).toBe(false);
  });

  it('should successfully update parameters when form is valid', () => {
    component.horaInicioEntreSemana = '07:00';
    component.horaFinEntreSemana = '22:00';
    component.horaInicioSabado = '07:00';
    component.horaFinSabado = '13:00';
    component.horaInicioAlmuerzo = '12:00';
    component.horaFinAlmuerzo = '13:00';

    component.guardar();

    expect(actualizarParametrosCalled).toBe(true);
    expect(component.mensaje?.tipo).toBe('exito');
    expect(component.mensaje?.texto).toBe('Parámetros actualizados con éxito');
    expect(component.guardando).toBe(false);
  });

  it('should handle error response when updating parameters fails with status 0', () => {
    const errorResponse = new HttpErrorResponse({ status: 0 });
    mockParametrosService.actualizarParametros = () => throwError(() => errorResponse);

    component.guardar();

    expect(component.mensaje?.tipo).toBe('error');
    expect(component.mensaje?.texto).toBe('No se pudo conectar con el servidor.');
    expect(component.guardando).toBe(false);
  });

  it('should navigate correctly using navegarA', () => {
    component.navegarA('/gestion-bases-datos');
    expect(lastNavigatedRoute).toEqual(['/gestion-bases-datos']);
  });

  it('should toggle user menu and close on outside click', () => {
    const event = new Event('click');
    expect(component.mostrarMenuUsuario).toBe(false);

    component.toggleMenuUsuario(event);
    expect(component.mostrarMenuUsuario).toBe(true);

    component.cerrarMenuAlHacerClicFuera();
    expect(component.mostrarMenuUsuario).toBe(false);
  });

  it('should close session and navigate to login', () => {
    component.mostrarMenuUsuario = true;
    component.cerrarSesion();

    expect(component.mostrarMenuUsuario).toBe(false);
    expect(cerrarSesionCalled).toBe(true);
    expect(lastNavigatedRoute).toEqual(['/loginn']);
  });
});
