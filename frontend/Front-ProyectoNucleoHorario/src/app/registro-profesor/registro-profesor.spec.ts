import { describe, it, expect, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { HttpResponse } from '@angular/common/http';

import { RegistroProfesor } from './registro-profesor';
import { ProfesorService } from '../services/profesor.service';
import { SesionService } from '../services/sesion.service';
import { Escalafon } from '../enums/escalafon.enum';
import { Especialidad } from '../enums/especialidad.enum';
import { TipoVinculacion } from '../enums/tipoViculacion.enum';
import { Dia } from '../enums/dia.enum';

describe('RegistroProfesor', () => {
  let component: RegistroProfesor;
  let fixture: ComponentFixture<RegistroProfesor>;
  let router: Router;
  let crearProfesorCalled = false;
  let actualizarProfesorCalled = false;
  let lastNavigatedRoute: any[] = [];
  let queryParamIdValue: string | null = null;

  const mockProfesorService = {
    mostrarTodo: () => of(new HttpResponse({
      body: [
        {
          id: 1,
          nombre: 'Carlos',
          apellido: 'Ruiz',
          documento: 123456,
          correo: 'carlos.ruiz@unbosque.edu.co',
          tipoVinculacion: TipoVinculacion.TIEMPO_COMPLETO,
          escalafon: Escalafon.PROFESOR_TITULAR,
          horasMaximasSemanales: 40,
          especialidades: [Especialidad.LOGICA_MATEMATICA],
          disponibilidadHoraria: [
            { dia: Dia.LUNES, horaInicio: '07:00', horaFin: '09:00' }
          ]
        }
      ],
      status: 200
    })),
    crearProfesor: () => {
      crearProfesorCalled = true;
      return of('Profesor creado exitosamente');
    },
    actualizarInformacionProfesor: () => {
      actualizarProfesorCalled = true;
      return of('Profesor actualizado');
    },
    guardarDisponibilidad: () => of('Disponibilidad guardada')
  };

  const mockSesionService = {
    esAdministrador: () => true,
    getProfesorId: () => 1,
    cerrarSesion: () => {}
  };

  beforeEach(async () => {
    TestBed.resetTestingModule();
    crearProfesorCalled = false;
    actualizarProfesorCalled = false;
    lastNavigatedRoute = [];
    queryParamIdValue = null;

    const mockRouter = {
      navigate: (path: any[]) => {
        lastNavigatedRoute = path;
        return Promise.resolve(true);
      }
    };

    const mockActivatedRoute = {
      snapshot: {
        queryParamMap: {
          get: (key: string) => key === 'id' ? queryParamIdValue : null
        }
      }
    };

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule, FormsModule],
      declarations: [RegistroProfesor],
      providers: [
        { provide: ProfesorService, useValue: mockProfesorService },
        { provide: SesionService, useValue: mockSesionService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegistroProfesor);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create the component successfully', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize in administrator creation mode when no id param is present', () => {
    queryParamIdValue = null;
    component.ngOnInit();
    expect(component.esAdministrador).toBe(true);
    expect(component.modoEdicion).toBe(false);
  });

  it('should initialize in administrator edit mode when id param is provided', () => {
    queryParamIdValue = '1';
    component.ngOnInit();
    expect(component.modoEdicion).toBe(true);
  });

  it('should initialize in professor mode when user is not an administrator', () => {
    (component as any).sesion = {
      esAdministrador: () => false,
      getProfesorId: () => 1,
      cerrarSesion: () => {}
    };

    component.ngOnInit();
    expect(component.esAdministrador).toBe(false);
    expect(component.profesor).toBeDefined();
  });

  it('should handle label and contract helpers correctly', () => {
    const label = component.etiqueta('TIEMPO_COMPLETO');
    expect(label).toBe('Tiempo completo');

    const vinculacionLabel = component.etiquetaVinculacion(TipoVinculacion.TIEMPO_COMPLETO);
    expect(vinculacionLabel).toContain('Tiempo completo');
    expect(component.minimoHoras).toBeDefined();
    expect(component.maximoHoras).toBeDefined();
  });

  it('should toggle specialities correctly', () => {
    expect(component.especialidadesSeleccionadas.length).toBe(0);
    component.toggleEspecialidad(Especialidad.LOGICA_MATEMATICA);
    expect(component.especialidadesSeleccionadas.length).toBe(1);
    component.toggleEspecialidad(Especialidad.LOGICA_MATEMATICA);
    expect(component.especialidadesSeleccionadas.length).toBe(0);
  });

  it('should toggle grid cells correctly respecting validation rules', () => {
    const fila = component.filas[0];
    component.toggleCelda(Dia.LUNES, fila);
    expect(component.horasSeleccionadas).toBe(2);

    component.toggleCelda(Dia.LUNES, fila);
    expect(component.horasSeleccionadas).toBe(0);
  });

  it('should show error message if creating professor with missing required fields', () => {
    component.esAdministrador = true;
    component.nombre = '';
    component.guardar();
    expect(component.mensaje).not.toBeNull();
    expect(component.mensaje?.tipo).toBe('error');
  });

  it('should successfully create a new professor as administrator', () => {
    component.esAdministrador = true;
    component.nombre = 'Carlos';
    component.apellido = 'Ruiz';
    component.documento = '123456';
    component.correo = 'carlos@unbosque.edu.co';
    component.contrasenia = '123456';
    component.tipoVinculacion = TipoVinculacion.TIEMPO_COMPLETO;
    component.escalafon = Escalafon.PROFESOR_TITULAR;
    component.especialidadesSeleccionadas = [Especialidad.LOGICA_MATEMATICA];

    component.guardar();
    expect(crearProfesorCalled).toBe(true);
    expect(component.exitoCreacion).toBeTruthy();
  });

  it('should successfully update an existing professor as administrator', () => {
    component.esAdministrador = true;
    component.modoEdicion = true;
    (component as any).idEdicion = 1;
    (component as any).profesorOriginal = { id: 1, disponibilidadHoraria: [] };

    component.nombre = 'Carlos Modificado';
    component.apellido = 'Ruiz';
    component.documento = '123456';
    component.correo = 'carlos@unbosque.edu.co';
    component.especialidadesSeleccionadas = [Especialidad.LOGICA_MATEMATICA];

    component.guardar();
    expect(actualizarProfesorCalled).toBe(true);
    expect(lastNavigatedRoute).toEqual(['/gestion-bases-datos']);
  });

  it('should toggle user menu and close on outside click', () => {
    const mockEvent = new Event('click');
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
