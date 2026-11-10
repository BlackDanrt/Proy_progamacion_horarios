import { describe, it, expect, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';

import { InicioDocente } from './inicio-docente';
import { SesionService } from '../services/sesion.service';
import { AuthProfesorService } from '../services/auth-profesor';

describe('InicioDocente', () => {
  let component: InicioDocente;
  let fixture: ComponentFixture<InicioDocente>;
  let router: Router;

  let mockSesionService: any;
  let mockAuthProfesorService: any;
  let lastNavigatedRoute: any[] = [];
  let esProfesorFlag = true;
  let profesorNameMock: string | null = 'Ana Docente';
  let cerrarSesionCalled = false;
  let cerrarSesionProfesorCalled = false;

  beforeEach(async () => {
    lastNavigatedRoute = [];
    esProfesorFlag = true;
    profesorNameMock = 'Ana Docente';
    cerrarSesionCalled = false;
    cerrarSesionProfesorCalled = false;

    mockSesionService = {
      esProfesor: () => esProfesorFlag,
      cerrarSesion: () => {
        cerrarSesionCalled = true;
      }
    };

    mockAuthProfesorService = {
      getNombreProfesor: () => profesorNameMock,
      cerrarSesionProfesor: () => {
        cerrarSesionProfesorCalled = true;
      }
    };

    const mockRouter = {
      navigate: (path: any[]) => {
        lastNavigatedRoute = path;
        return Promise.resolve(true);
      }
    };

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      declarations: [InicioDocente],
      providers: [
        { provide: SesionService, useValue: mockSesionService },
        { provide: AuthProfesorService, useValue: mockAuthProfesorService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(InicioDocente);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
  });

  it('should create the component successfully', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to /loginn on ngOnInit if not a professor', () => {
    esProfesorFlag = false;
    component.ngOnInit();
    expect(lastNavigatedRoute).toEqual(['/loginn']);
  });

  it('should load professor name on ngOnInit if professor session is active', () => {
    component.ngOnInit();
    expect(component.nombreDocente).toBe('Ana Docente');
  });

  it('should navigate correctly using ejecutarAccion', () => {
    const tarjeta = {
      titulo: 'Test',
      descripcion: 'Test desc',
      icono: 'icon',
      ruta: '/mi-horario',
      badge: 'badge'
    };
    component.ejecutarAccion(tarjeta);
    expect(lastNavigatedRoute).toEqual(['/mi-horario']);
  });

  it('should navigate correctly using navegarA', () => {
    component.navegarA('/mi-horario');
    expect(lastNavigatedRoute).toEqual(['/mi-horario']);
  });

  it('should toggle user menu and close on outside click', () => {
    const event = new Event('click');
    expect(component.mostrarMenuUsuario).toBe(false);

    component.toggleMenuUsuario(event);
    expect(component.mostrarMenuUsuario).toBe(true);

    component.cerrarMenuAlHacerClicFuera();
    expect(component.mostrarMenuUsuario).toBe(false);
  });

  it('should close session, reset menu, and navigate to login', () => {
    component.mostrarMenuUsuario = true;
    component.cerrarSesion();

    expect(component.mostrarMenuUsuario).toBe(false);
    expect(cerrarSesionProfesorCalled).toBe(true);
    expect(cerrarSesionCalled).toBe(true);
    expect(lastNavigatedRoute).toEqual(['/loginn']);
  });
});
