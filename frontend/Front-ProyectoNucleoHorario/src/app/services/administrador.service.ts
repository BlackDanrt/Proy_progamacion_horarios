import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AdministradorModel } from '../models/administrador.model';

/**
 * Servicio que consume los endpoints de /administrador del back.
 * El token JWT lo agrega el interceptor de autenticación.
 */
@Injectable({ providedIn: 'root' })
export class AdministradorService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly urlBase: string = `${environment.apiUrl}/administrador`;

  /** Busca administradores por su código institucional. */
  buscarPorCodigoAdministrador(codigoAdministrador: string): Observable<HttpResponse<AdministradorModel[]>> {
    return this.http.get<AdministradorModel[]>(`${this.urlBase}/buscarporcodigo`, {
      params: new HttpParams().set('codigoAdministrador', codigoAdministrador),
      observe: 'response',
    });
  }

  /** Busca administradores por correo electrónico. */
  buscarPorCorreo(correo: string): Observable<HttpResponse<AdministradorModel[]>> {
    return this.http.get<AdministradorModel[]>(`${this.urlBase}/buscarporcorreo`, {
      params: new HttpParams().set('correo', correo),
      observe: 'response',
    });
  }

  /** Busca administradores por número de documento. */
  buscarPorDocumento(documento: number): Observable<HttpResponse<AdministradorModel[]>> {
    return this.http.get<AdministradorModel[]>(`${this.urlBase}/buscarpordocumento`, {
      params: new HttpParams().set('documento', documento),
      observe: 'response',
    });
  }

  /** Obtiene todos los administradores registrados. */
  mostrarTodo(): Observable<HttpResponse<AdministradorModel[]>> {
    return this.http.get<AdministradorModel[]>(`${this.urlBase}/mostrartodo`, {
      observe: 'response',
    });
  }

  /**
   * Crea un administrador enviando los datos mediante @RequestParam.
   */
  /** Registra un administrador enviando sus campos como parámetros HTTP. */
  crearAdministrador(administrador: AdministradorModel): Observable<string> {
    const params = new HttpParams()
      .set('nombre', administrador.nombre)
      .set('apellido', administrador.apellido)
      .set('documento', administrador.documento)
      .set('correo', administrador.correo)
      .set('contrasenia', administrador.contrasenia ?? '')
      .set('codigoAdministrador', administrador.codigoAdministrador)
      .set('estado', administrador.estado);

    return this.http.post(`${this.urlBase}/crear`, null, {
      params,
      responseType: 'text',
    });
  }

  /** Actualiza los datos de un administrador existente. */
  actualizarAdministrador(administrador: AdministradorModel): Observable<string> {
    return this.http.put(`${this.urlBase}/actualizar`, administrador, {
      responseType: 'text',
    });
  }

  /** Elimina un administrador usando su identificador. */
  eliminarAdministrador(id: number): Observable<string> {
    return this.http.delete(`${this.urlBase}/eliminar`, {
      params: new HttpParams().set('id', id),
      responseType: 'text',
    });
  }

  /**
   * Alias de compatibilidad: hace lo mismo que mostrarTodo().
   */
  mostrarTodoAdmin(): Observable<HttpResponse<AdministradorModel[]>> {
    return this.mostrarTodo();
  }
}
