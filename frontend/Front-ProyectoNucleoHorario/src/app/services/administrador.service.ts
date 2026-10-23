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
  private readonly urlbase: string = 'https://gpcueb.org/pokemon-pokedes/administrador';

  buscarPorCodigoAdministrador(codigoAdministrador: string): Observable<HttpResponse<AdministradorModel[]>> {
    return this.cliente.get<AdministradorModel[]>(
      this.urlbase + '/buscarporcodigo?codigoAdministrador=' + encodeURIComponent(codigoAdministrador),
      {
        observe: 'response',
      }
    );
  }

  buscarPorCorreo(correo: string): Observable<HttpResponse<AdministradorModel[]>> {
    return this.cliente.get<AdministradorModel[]>(
      this.urlbase + '/buscarporcorreo?correo=' + encodeURIComponent(correo),
      {
        observe: 'response',
      }
    );
  }


  buscarPorDocumento(documento: number): Observable<HttpResponse<AdministradorModel[]>> {
    return this.cliente.get<AdministradorModel[]>(
      this.urlbase + '/buscarpordocumento?documento=' + documento,
      {
        observe: 'response',
      }
    );
  }


  mostrarTodo(): Observable<HttpResponse<AdministradorModel[]>> {
    return this.cliente.get<AdministradorModel[]>(this.urlbase + '/mostrartodo', {
      observe: 'response',
    });
  }


  crearAdministrador(administradorDTO: AdministradorModel): Observable<string> {
    return this.cliente.post(
      this.urlbase + '/crear',
      administradorDTO,
      { responseType: 'text' }
    );
  }


  actualizarAdministrador(administradorDTO: AdministradorModel): Observable<string> {
    return this.cliente.put(
      this.urlbase + '/actualizar',
      administradorDTO,
      { responseType: 'text' }
    );
  }


  eliminarAdministrador(id: number): Observable<string> {
    return this.cliente.delete(this.urlbase + '/eliminar?id=' + id, {
      responseType: 'text',
    });
  }


  mostrarTodoAdmin(): Observable<HttpResponse<AdministradorModel[]>> {
    const token: string | null = localStorage.getItem('admin_token');

    const headers: HttpHeaders = new HttpHeaders().set(
      'Authorization',
      `Bearer ${token || ''}`
    );

    return this.cliente.get<AdministradorModel[]>(`${this.urlbase}/mostrartodo`, {
      headers: headers,
      observe: 'response',
    });
  }

}
