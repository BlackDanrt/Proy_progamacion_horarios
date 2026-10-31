import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

/**
 * Servicio encargado de la autenticación
 * y gestión de sesión de administradores.
 *
 * Funcionalidades:
 * - Iniciar sesión.
 * - Guardar datos de sesión.
 * - Verificar autenticación.
 * - Cerrar sesión.
 *
 * Utiliza JWT para controlar acceso
 * a recursos protegidos.
 */
@Injectable({
  providedIn: 'root',
})
export class AuthAdminService {

  /**
   * Cliente HTTP utilizado para realizar
   * peticiones al backend.
   */
  private http = inject(HttpClient);

  /**
   * URL base del módulo de autenticación.
   */
  private apiUrl = 'http://localhost:8080/auth';

  /**
   * Realiza el proceso de autenticación
   * de un administrador.
   *
   * Envía credenciales al backend y,
   * si son válidas, almacena la sesión.
   *
   * @param correo Correo del administrador.
   * @param contrasenia Contraseña del administrador.
   * @returns Observable con respuesta del servidor.
   */
  login(
    correo: string,
    contrasenia: string
  ): Observable<any> {

    const loginRequest = {
      correo: correo,
      contrasenia: contrasenia
    };

    return this.http.post<any>(
      `${this.apiUrl}/login/admin`,
      loginRequest
    ).pipe(

      /**
       * Guarda automáticamente la sesión
       * cuando se recibe un token válido.
       */
      tap((respuesta) => {

        if (respuesta && respuesta.token) {

          this.guardarSesionAdmin(
            respuesta.token,
            respuesta.role || 'ADMINISTRADOR',
            respuesta.id,
            `${respuesta.nombre} ${respuesta.apellido}`
          );

        }
      })
    );
  }

  /**
   * Guarda información de sesión
   * del administrador en localStorage.
   *
   * @param token Token de autenticación.
   * @param rol Rol asignado.
   * @param id ID administrador.
   * @param nombre Nombre completo del administrador.
   */
  private guardarSesionAdmin(
    token: string,
    rol: string,
    id: number,
    nombre: string
  ) {

    localStorage.setItem('admin_token', token);
    localStorage.setItem('admin_rol', rol);
    localStorage.setItem('admin_id', id.toString());
    localStorage.setItem('admin_nombre', nombre);
  }

  /**
   * Obtiene el token JWT de la sesión actual.
   *
   * @returns Token o null si no hay sesión.
   */
  getToken(): string | null {
    return localStorage.getItem('admin_token');
  }

  /**
   * Obtiene el nombre guardado del administrador.
   *
   * @returns Nombre completo o cadena vacía.
   */
  getNombreAdmin(): string {
    return localStorage.getItem('admin_nombre') || '';
  }

  /**
   * Construye los encabezados con el token JWT.
   * Se usa en las peticiones a recursos protegidos.
   *
   * @returns Encabezados HTTP con Authorization.
   */
  getHeaders(): HttpHeaders {
    return new HttpHeaders({
      Authorization: `Bearer ${this.getToken()}`
    });
  }

  /**
   * Verifica si existe una sesión
   * activa de administrador.
   *
   * @returns true si existe token,
   * false en caso contrario.
   */
  estaAutenticadoAdmin(): boolean {
    return !!localStorage.getItem('admin_token');
  }

  /**
   * Elimina toda la información
   * almacenada de la sesión actual.
   */
  cerrarSesionAdmin() {
    localStorage.removeItem('admin_token');
    localStorage.removeItem('admin_rol');
    localStorage.removeItem('admin_id');
    localStorage.removeItem('admin_nombre');
  }
}
