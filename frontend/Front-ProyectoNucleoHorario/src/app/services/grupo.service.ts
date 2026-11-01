import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { GrupoModel } from '../models/grupo.model';
import { EstadoGrupo } from '../enums/estadoGrupo.enum';

/**
 * Servicio que consume los endpoints de /grupo del back.
 * Las búsquedas son GET con parámetros en la URL.
 */
@Injectable({ providedIn: 'root' })
export class GrupoService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly urlBase: string = `${environment.apiUrl}/grupo`;

  buscarPorAsignatura(idAsignatura: number): Observable<HttpResponse<GrupoModel[]>> {
    return this.http.get<GrupoModel[]>(`${this.urlBase}/buscarporasignatura`, {
      params: new HttpParams().set('idAsignatura', idAsignatura),
      observe: 'response',
    });
  }

  buscarPorEstado(estadoGrupo: EstadoGrupo): Observable<HttpResponse<GrupoModel[]>> {
    return this.http.get<GrupoModel[]>(`${this.urlBase}/buscarporestado`, {
      params: new HttpParams().set('estadoGrupo', estadoGrupo),
      observe: 'response',
    });
  }

  buscarPorNumeroGrupo(numeroGrupo: number): Observable<HttpResponse<GrupoModel[]>> {
    return this.http.get<GrupoModel[]>(`${this.urlBase}/buscarpornumero`, {
      params: new HttpParams().set('numeroGrupo', numeroGrupo),
      observe: 'response',
    });
  }

  buscarPorProfesor(idProfesor: number): Observable<HttpResponse<GrupoModel[]>> {
    return this.http.get<GrupoModel[]>(`${this.urlBase}/buscarporprofesor`, {
      params: new HttpParams().set('idProfesor', idProfesor),
      observe: 'response',
    });
  }

  crearGrupo(grupo: GrupoModel): Observable<string> {
    return this.http.post(`${this.urlBase}/crear`, grupo, { responseType: 'text' });
  }

  actualizarGrupo(grupo: GrupoModel): Observable<string> {
    return this.http.put(`${this.urlBase}/actualizar`, grupo, { responseType: 'text' });
  }

  eliminarGrupoPorId(grupoId: number): Observable<string> {
    return this.http.delete(`${this.urlBase}/eliminarporid`, {
      params: new HttpParams().set('grupoId', grupoId),
      responseType: 'text',
    });
  }

  eliminarGrupoPorNumero(numeroGrupo: number): Observable<string> {
    return this.http.delete(`${this.urlBase}/eliminarpornumero`, {
      params: new HttpParams().set('numeroGrupo', numeroGrupo),
      responseType: 'text',
    });
  }

  mostrarTodo(): Observable<HttpResponse<GrupoModel[]>> {
    return this.http.get<GrupoModel[]>(`${this.urlBase}/mostrartodo`, {
      observe: 'response',
    });
  }
}
