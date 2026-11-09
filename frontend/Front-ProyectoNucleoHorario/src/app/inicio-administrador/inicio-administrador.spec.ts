import { describe, it, expect, beforeEach } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';

import { InicioAdministrador } from './inicio-administrador';
import { AuthAdminService } from '../services/auth-admin';

describe('InicioAdministrador', () => {
  let component: InicioAdministrador;
  let fixture: ComponentFixture<InicioAdministrador>;
  let router: Router;

  let mockAuthAdminService: any;
  let lastNavigatedRoute: any[] = [];
  let cerrarSesionAdminCalled = false;
  let autenticadoFlag = true;
  let adminNameMock: string | null = 'Carlos Admin';

  beforeEach(async () => {
    lastNavigatedRoute = [];
    cerrarSesionAdminCalled = false;
    autenticadoFlag = true;
    adminNameMock = 'Carlos Admin';

    mockAuthAdminService = {
      estaAutenticadoAdmin: () => autenticadoFlag,
      getNombreAdmin: () => adminNameMock,
      cerrarSesionAdmin: () => {
        cerrarSesionAdminCalled = true;
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
      declarations: [InicioAdministrador],
      providers: [
        { provide: AuthAdminService, useValue: mockAuthAdminService },
        { provide: Router, useValue: mockRouter }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(InicioAdministrador);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
  });

  it('should create the component successfully', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect to /loginn on ngOnInit if not authenticated', () => {
    autenticadoFlag = false;
    component.ngOnInit();
    expect(lastNavigatedRoute).toEqual(['/loginn']);
  });

  it('should load admin name on ngOnInit if authenticated', () => {
    component.ngOnInit();
    expect(component.nombreAdmin).toBe('Carlos Admin');
  });

  it('should navigate correctly using ejecutarAccion', () => {
    const tarjeta = {
      titulo: 'Test',
      descripcion: 'Test desc',
      icono: 'icon',
      ruta: '/gestion-bases-datos',
      badge: 'badge'
    };
    component.ejecutarAccion(tarjeta);
    expect(lastNavigatedRoute).toEqual(['/gestion-bases-datos']);
  });

  it('should navigate correctly using navegarA', () => {
    component.navegarA('/horarios');
    expect(lastNavigatedRoute).toEqual(['/horarios']);
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
    expect(cerrarSesionAdminCalled).toBe(true);
    expect(lastNavigatedRoute).toEqual(['/loginn']);
  });
});
