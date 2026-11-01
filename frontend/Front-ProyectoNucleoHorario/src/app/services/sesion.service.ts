import { Injectable } from '@angular/core';

/**
 * Servicio que centraliza la sesión del usuario (administrador o profesor)
 * guardada en el navegador.
 */
@Injectable({ providedIn: 'root' })
export class SesionService {

  private readonly claves: string[] = [
    'admin_token', 'admin_rol', 'admin_id', 'admin_nombre',
    'profesor_token', 'profesor_rol', 'profesor_id', 'profesor_nombre',
  ];

  /** Token JWT de la sesión activa (administrador o profesor). */
  getToken(): string | null {
    return localStorage.getItem('admin_token') || localStorage.getItem('profesor_token');
  }

  esAdministrador(): boolean {
    return !!localStorage.getItem('admin_token');
  }

  esProfesor(): boolean {
    return !!localStorage.getItem('profesor_token') && !this.esAdministrador();
  }

  estaAutenticado(): boolean {
    return !!this.getToken();
  }

  /** Identificador del profesor con sesión activa, o null si no hay. */
  getProfesorId(): number | null {
    const id: string | null = localStorage.getItem('profesor_id');
    return id ? Number(id) : null;
  }

  /** Borra toda la información de sesión. */
  cerrarSesion(): void {
    this.claves.forEach((clave: string) => localStorage.removeItem(clave));
  }
}
