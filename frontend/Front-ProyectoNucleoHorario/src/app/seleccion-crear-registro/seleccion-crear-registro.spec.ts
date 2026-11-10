import { describe, it, expect, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { SeleccionCrearRegistro } from './seleccion-crear-registro';

describe('SeleccionCrearRegistro', () => {
  let component: SeleccionCrearRegistro;
  let fixture: ComponentFixture<SeleccionCrearRegistro>;
  let router: Router;

  let lastNavigatedRoute: any[] = [];
  let lastNavigatedExtras: any = null;
  let cerrarEmitted = false;

  beforeEach(async () => {
    lastNavigatedRoute = [];
    lastNavigatedExtras = null;
    cerrarEmitted = false;

    const mockRouter = {
      navigate: (path: any[], extras?: any) => {
        lastNavigatedRoute = path;
        lastNavigatedExtras = extras;
        return Promise.resolve(true);
      }
    };

    await TestBed.configureTestingModule({
      declarations: [SeleccionCrearRegistro],
      providers: [
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SeleccionCrearRegistro);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);

    // Suscripción al EventEmitter de salida
    component.cerrar.subscribe(() => {
      cerrarEmitted = true;
    });

    fixture.detectChanges();
  });

  it('should create the component successfully', () => {
    expect(component).toBeTruthy();
  });

  it('should emit cerrar event and navigate to /registro-profesor with params when selecting docente', () => {
    component.seleccionarTipo('docente');

    expect(cerrarEmitted).toBe(true);
    expect(lastNavigatedRoute).toEqual(['/registro-profesor']);
    expect(lastNavigatedExtras).toEqual({ queryParams: { esAdmin: 'true' } });
  });

  it('should emit cerrar event and navigate to /crear-administrador when selecting administrador', () => {
    component.seleccionarTipo('administrador');

    expect(cerrarEmitted).toBe(true);
    expect(lastNavigatedRoute).toEqual(['/crear-administrador']);
  });

  it('should emit cerrar event directly when cerrarModal is called', () => {
    component.cerrarModal();
    expect(cerrarEmitted).toBe(true);
  });
});
