import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { SesionService } from './sesion.service';

describe('SesionService', () => {
  let service: SesionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SesionService]
    });

    service = TestBed.inject(SesionService);
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return null when no token is present, and return correct token otherwise', () => {
    expect(service.getToken()).toBeNull();

    localStorage.setItem('profesor_token', 'token-profesor-123');
    expect(service.getToken()).toBe('token-profesor-123');

    localStorage.setItem('admin_token', 'token-admin-123');
    expect(service.getToken()).toBe('token-admin-123');
  });

  it('should correctly identify if user esAdministrador', () => {
    expect(service.esAdministrador()).toBe(false);

    localStorage.setItem('admin_token', 'token-admin-123');
    expect(service.esAdministrador()).toBe(true);
  });

  it('should correctly identify if user esProfesor', () => {
    expect(service.esProfesor()).toBe(false);

    localStorage.setItem('profesor_token', 'token-profesor-123');
    expect(service.esProfesor()).toBe(true);

    // Si también existe admin_token, esProfesor debe retornar falso
    localStorage.setItem('admin_token', 'token-admin-123');
    expect(service.esProfesor()).toBe(false);
  });

  it('should correctly checkestaAutenticado status', () => {
    expect(service.estaAutenticado()).toBe(false);

    localStorage.setItem('profesor_token', 'token-profesor-123');
    expect(service.estaAutenticado()).toBe(true);
  });

  it('should return profesor id as number or null', () => {
    expect(service.getProfesorId()).toBeNull();

    localStorage.setItem('profesor_id', '15');
    expect(service.getProfesorId()).toBe(15);
  });

  it('should remove all session keys when cerrarSesion is called', () => {
    localStorage.setItem('admin_token', 'admin-token');
    localStorage.setItem('admin_rol', 'ADMIN');
    localStorage.setItem('profesor_id', '10');

    service.cerrarSesion();

    expect(localStorage.getItem('admin_token')).toBeNull();
    expect(localStorage.getItem('admin_rol')).toBeNull();
    expect(localStorage.getItem('profesor_id')).toBeNull();
  });
});
