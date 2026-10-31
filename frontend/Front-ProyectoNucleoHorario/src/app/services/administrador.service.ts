import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdministradorModel } from '../models/administrador.model';

@Injectable({
  providedIn: 'root',
})
export class AdministradorService {

  private cliente: HttpClient = inject(HttpClient);

  /**
   * URL base de los endpoints relacionados con administrador.
   */
  private readonly urlbase: string = 'http://localhost:8080/administrador';

  /**
   * Construye los encabezados con el token JWT del administrador.
   * Todos los endpoints de administrador requieren este token.
   */
  private headersAdmin(): HttpHeaders {
    const token: string | null = localStorage.getItem('admin_token');

    return new HttpHeaders().set('Authorization', `Bearer ${token || ''}`);
  }

  buscarPorCodigoAdministrador(codigoAdministrador: string): Observable<HttpResponse<AdministradorModel[]>> {
    return this.cliente.get<AdministradorModel[]>(
      this.urlbase + '/buscarporcodigo?codigoAdministrador=' + encodeURIComponent(codigoAdministrador),
      {
        headers: this.headersAdmin(),
        observe: 'response',
      }
    );
  }

  buscarPorCorreo(correo: string): Observable<HttpResponse<AdministradorModel[]>> {
    return this.cliente.get<AdministradorModel[]>(
      this.urlbase + '/buscarporcorreo?correo=' + encodeURIComponent(correo),
      {
        headers: this.headersAdmin(),
        observe: 'response',
      }
    );
  }

  buscarPorDocumento(documento: number): Observable<HttpResponse<AdministradorModel[]>> {
    return this.cliente.get<AdministradorModel[]>(
      this.urlbase + '/buscarpordocumento?documento=' + documento,
      {
        headers: this.headersAdmin(),
        observe: 'response',
      }
    );
  }

  mostrarTodo(): Observable<HttpResponse<AdministradorModel[]>> {
    return this.cliente.get<AdministradorModel[]>(this.urlbase + '/mostrartodo', {
      headers: this.headersAdmin(),
      observe: 'response',
    });
  }

  crearAdministrador(administradorDTO: AdministradorModel): Observable<string> {
    return this.cliente.post(
      this.urlbase + '/crear',
      administradorDTO,
      {
        headers: this.headersAdmin(),
        responseType: 'text',
      }
    );
  }

  actualizarAdministrador(administradorDTO: AdministradorModel): Observable<string> {
    return this.cliente.put(
      this.urlbase + '/actualizar',
      administradorDTO,
      {
        headers: this.headersAdmin(),
        responseType: 'text',
      }
    );
  }

  eliminarAdministrador(id: number): Observable<string> {
    return this.cliente.delete(this.urlbase + '/eliminar?id=' + id, {
      headers: this.headersAdmin(),
      responseType: 'text',
    });
  }

  /**
   * Se mantiene por compatibilidad con los componentes que ya lo usan.
   * Hace lo mismo que mostrarTodo().
   */
  mostrarTodoAdmin(): Observable<HttpResponse<AdministradorModel[]>> {
    return this.mostrarTodo();
  }

}
