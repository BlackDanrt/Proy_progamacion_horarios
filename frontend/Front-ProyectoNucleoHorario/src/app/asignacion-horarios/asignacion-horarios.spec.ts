import { describe, it, expect, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpResponse } from '@angular/common/http';
import { of } from 'rxjs';

import { AsignacionHorarios } from './asignacion-horarios';
import { AsignaturaService } from '../services/asignatura.service';
import { SalonService } from '../services/salon.service';
import { ProfesorService } from '../services/profesor.service';
import { GrupoService } from '../services/grupo.service';

describe('AsignacionHorarios', () => {
  let component: AsignacionHorarios;
  let fixture: ComponentFixture<AsignacionHorarios>;

  let asignaturaCalled = false;
  let salonCalled = false;
  let profesorCalled = false;
  let grupoCalled = false;

  const mockAsignaturaService = {
    mostrarTodo: () => {
      asignaturaCalled = true;
      return of(new HttpResponse({ body: [], status: 200 }));
    }
  };

  const mockSalonService = {
    mostrarTodo: () => {
      salonCalled = true;
      return of(new HttpResponse({ body: [], status: 200 }));
    }
  };

  const mockProfesorService = {
    mostrarTodo: () => {
      profesorCalled = true;
      return of(new HttpResponse({ body: [], status: 200 }));
    }
  };

  const mockGrupoService = {
    mostrarTodo: () => {
      grupoCalled = true;
      return of(new HttpResponse({ body: [], status: 200 }));
    }
  };

  beforeEach(async () => {
    asignaturaCalled = false;
    salonCalled = false;
    profesorCalled = false;
    grupoCalled = false;

    TestBed.resetTestingModule();

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      declarations: [AsignacionHorarios],
      providers: [
        { provide: AsignaturaService, useValue: mockAsignaturaService },
        { provide: SalonService, useValue: mockSalonService },
        { provide: ProfesorService, useValue: mockProfesorService },
        { provide: GrupoService, useValue: mockGrupoService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AsignacionHorarios);
    component = fixture.componentInstance;
  });

  it('should create the component successfully', () => {
    expect(component).toBeTruthy();
  });

  it('should load initial catalogs on ngOnInit', () => {
    component.ngOnInit();

    expect(asignaturaCalled).toBe(true);
    expect(salonCalled).toBe(true);
    expect(profesorCalled).toBe(true);
    expect(grupoCalled).toBe(true);
  });

  it('should correctly validate lunch hours', () => {
    expect(component.esAlmuerzo('12:00 - 13:00')).toBe(true);
    expect(component.esAlmuerzo('07:00 - 08:00')).toBe(false);
  });

  it('should handle toasts correctly', () => {
    component.mostrarToast('exito', 'Operación exitosa');
    expect(component.toasts.length).toBe(1);
    expect(component.toasts[0].mensaje).toBe('Operación exitosa');

    const idToast = component.toasts[0].id;
    component.cerrarToast(idToast);
    expect(component.toasts.length).toBe(0);
  });
});
