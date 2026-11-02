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

  buscarPorCodigoAdministrador(codigoAdministrador: string): Observable<HttpResponse<AdministradorModel[]>> {
    return this.http.get<AdministradorModel[]>(`${this.urlBase}/buscarporcodigo`, {
      params: new HttpParams().set('codigoAdministrador', codigoAdministrador),
      observe: 'response',
    });
  }

  buscarPorCorreo(correo: string): Observable<HttpResponse<AdministradorModel[]>> {
    return this.http.get<AdministradorModel[]>(`${this.urlBase}/buscarporcorreo`, {
      params: new HttpParams().set('correo', correo),
      observe: 'response',
    });
  }

  buscarPorDocumento(documento: number): Observable<HttpResponse<AdministradorModel[]>> {
    return this.http.get<AdministradorModel[]>(`${this.urlBase}/buscarpordocumento`, {
      params: new HttpParams().set('documento', documento),
      observe: 'response',
    });
  }

  mostrarTodo(): Observable<HttpResponse<AdministradorModel[]>> {
    return this.http.get<AdministradorModel[]>(`${this.urlBase}/mostrartodo`, {
      observe: 'response',
    });
  }

  /** [BACK] El back debe recibir este objeto como @RequestBody (hoy usa @RequestParam). */
  crearAdministrador(administrador: AdministradorModel): Observable<string> {
    return this.http.post(`${this.urlBase}/crear`, administrador, { responseType: 'text' });
  }

  actualizarAdministrador(administrador: AdministradorModel): Observable<string> {
    return this.http.put(`${this.urlBase}/actualizar`, administrador, { responseType: 'text' });
  }

  eliminarAdministrador(id: number): Observable<string> {
    return this.http.delete(`${this.urlBase}/eliminar`, {
      params: new HttpParams().set('id', id),
      responseType: 'text',
    });
  }

  /** Alias de compatibilidad: hace lo mismo que mostrarTodo(). */
  mostrarTodoAdmin(): Observable<HttpResponse<AdministradorModel[]>> {
    return this.mostrarTodo();
  }
}
