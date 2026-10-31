import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

/**
 * Servicio encargado de la autenticación
 * y gestión de sesión de profesores.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthProfesorService {

  private http = inject(HttpClient);

  /** URL base del módulo de autenticación. */
  private apiUrl = 'http://localhost:8080/auth';

  /**
   * Autentica a un profesor y guarda la sesión.
   *
   * @param correo Correo del profesor.
   * @param contrasenia Contraseña del profesor.
   * @returns Observable con respuesta del servidor.
   */
  login(correo: string, contrasenia: string): Observable<any> {
    return this.http.post<any>(
      `${this.apiUrl}/login/profesor`,
      { correo, contrasenia }
    ).pipe(
      tap((respuesta) => {
        if (respuesta && respuesta.token) {
          localStorage.setItem('profesor_token', respuesta.token);
          localStorage.setItem('profesor_rol', respuesta.role || 'PROFESOR');
          localStorage.setItem('profesor_id', respuesta.id.toString());
          localStorage.setItem('profesor_nombre', `${respuesta.nombre} ${respuesta.apellido}`);
        }
      })
    );
  }

  getToken(): string | null {
    return localStorage.getItem('profesor_token');
  }

  getNombreProfesor(): string {
    return localStorage.getItem('profesor_nombre') || '';
  }

  getHeaders(): HttpHeaders {
    return new HttpHeaders({
      Authorization: `Bearer ${this.getToken()}`
    });
  }

  estaAutenticadoProfesor(): boolean {
    return !!localStorage.getItem('profesor_token');
  }

  cerrarSesionProfesor() {
    localStorage.removeItem('profesor_token');
    localStorage.removeItem('profesor_rol');
    localStorage.removeItem('profesor_id');
    localStorage.removeItem('profesor_nombre');
  }
}
