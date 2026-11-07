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

  /** Busca grupos asociados a una asignatura. */
  buscarPorAsignatura(idAsignatura: number): Observable<HttpResponse<GrupoModel[]>> {
    return this.http.get<GrupoModel[]>(`${this.urlBase}/buscarporasignatura`, {
      params: new HttpParams().set('idAsignatura', idAsignatura),
      observe: 'response',
    });
  }

  /** Busca grupos por su estado. */
  buscarPorEstado(estadoGrupo: EstadoGrupo): Observable<HttpResponse<GrupoModel[]>> {
    return this.http.get<GrupoModel[]>(`${this.urlBase}/buscarporestado`, {
      params: new HttpParams().set('estadoGrupo', estadoGrupo),
      observe: 'response',
    });
  }

  /** Busca grupos por número de grupo. */
  buscarPorNumeroGrupo(numeroGrupo: number): Observable<HttpResponse<GrupoModel[]>> {
    return this.http.get<GrupoModel[]>(`${this.urlBase}/buscarpornumero`, {
      params: new HttpParams().set('numeroGrupo', numeroGrupo),
      observe: 'response',
    });
  }

  /** Busca grupos asignados a un profesor. */
  buscarPorProfesor(idProfesor: number): Observable<HttpResponse<GrupoModel[]>> {
    return this.http.get<GrupoModel[]>(`${this.urlBase}/buscarporprofesor`, {
      params: new HttpParams().set('idProfesor', idProfesor),
      observe: 'response',
    });
  }

  /** Registra un nuevo grupo. */
  crearGrupo(grupo: GrupoModel): Observable<string> {
    return this.http.post(`${this.urlBase}/crear`, grupo, { responseType: 'text' });
  }

  /** Actualiza los datos de un grupo existente. */
  actualizarGrupo(grupo: GrupoModel): Observable<string> {
    return this.http.put(`${this.urlBase}/actualizar`, grupo, { responseType: 'text' });
  }

  /** Elimina un grupo usando su identificador. */
  eliminarGrupoPorId(grupoId: number): Observable<string> {
    return this.http.delete(`${this.urlBase}/eliminarporid`, {
      params: new HttpParams().set('grupoId', grupoId),
      responseType: 'text',
    });
  }

  /** Elimina grupos identificados por su número. */
  eliminarGrupoPorNumero(numeroGrupo: number): Observable<string> {
    return this.http.delete(`${this.urlBase}/eliminarpornumero`, {
      params: new HttpParams().set('numeroGrupo', numeroGrupo),
      responseType: 'text',
    });
  }

  /** Obtiene todos los grupos registrados. */
  mostrarTodo(): Observable<HttpResponse<GrupoModel[]>> {
    return this.http.get<GrupoModel[]>(`${this.urlBase}/mostrartodo`, {
      observe: 'response',
    });
  }
}
