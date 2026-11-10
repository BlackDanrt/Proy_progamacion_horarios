import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { GrupoModel } from '../models/grupo.model';
import { EstadoGrupo } from '../enums/estadoGrupo.enum';

/**
 * @description
 * Servicio encargado de gestionar todas las peticiones HTTP hacia los endpoints
 * del controlador `/grupo` en el backend (Spring Boot).
 *
 * Permite realizar operaciones de consulta, creación, actualización y eliminación de grupos académicos,
 * aplicando los parámetros de URL correspondientes según los filtros requeridos.
 */
@Injectable({ providedIn: 'root' })
export class GrupoService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly urlBase: string = `${environment.apiUrl}/grupo`;

  /**
   * Busca y retorna los grupos académicos asociados a una asignatura específica.
   *
   * @param idAsignatura Identificador único de la asignatura.
   * @returns Un Observable con la respuesta HTTP que contiene un arreglo de `GrupoModel`.
   */
  buscarPorAsignatura(idAsignatura: number): Observable<HttpResponse<GrupoModel[]>> {
    return this.http.get<GrupoModel[]>(`${this.urlBase}/buscarporasignatura`, {
      params: new HttpParams().set('idAsignatura', idAsignatura),
      observe: 'response',
    });
  }

  /**
   * Busca grupos académicos filtrados por su estado actual en el sistema.
   *
   * @param estadoGrupo Estado del grupo (ej. ACTIVO, INACTIVO, etc.) según el enum `EstadoGrupo`.
   * @returns Un Observable con la respuesta HTTP que contiene un arreglo de `GrupoModel`.
   */
  buscarPorEstado(estadoGrupo: EstadoGrupo): Observable<HttpResponse<GrupoModel[]>> {
    return this.http.get<GrupoModel[]>(`${this.urlBase}/buscarporestado`, {
      params: new HttpParams().set('estadoGrupo', estadoGrupo),
      observe: 'response',
    });
  }

  /**
   * Busca grupos específicos basándose en su número de grupo asignado.
   *
   * @param numeroGrupo Número numérico del grupo (ej. 1, 2, etc.).
   * @returns Un Observable con la respuesta HTTP que contiene un arreglo de `GrupoModel`.
   */
  buscarPorNumeroGrupo(numeroGrupo: number): Observable<HttpResponse<GrupoModel[]>> {
    return this.http.get<GrupoModel[]>(`${this.urlBase}/buscarpornumero`, {
      params: new HttpParams().set('numeroGrupo', numeroGrupo),
      observe: 'response',
    });
  }

  /**
   * Busca los grupos académicos asignados a un docente en particular.
   *
   * @param idProfesor Identificador único del profesor.
   * @returns Un Observable con la respuesta HTTP que contiene un arreglo de `GrupoModel`.
   */
  buscarPorProfesor(idProfesor: number): Observable<HttpResponse<GrupoModel[]>> {
    return this.http.get<GrupoModel[]>(`${this.urlBase}/buscarporprofesor`, {
      params: new HttpParams().set('idProfesor', idProfesor),
      observe: 'response',
    });
  }

  /**
   * Registra un nuevo grupo académico en la base de datos.
   *
   * @param grupo Objeto de tipo `GrupoModel` que contiene la información del nuevo grupo.
   * @returns Un Observable con la respuesta en formato de texto plano emitida por el servidor.
   */
  crearGrupo(grupo: GrupoModel): Observable<string> {
    return this.http.post(`${this.urlBase}/crear`, grupo, { responseType: 'text' });
  }

  /**
   * Actualiza los datos de un grupo académico existente.
   * Es compatible tanto si se le pasa el objeto completo `GrupoModel` como si se pasa un ID junto con los datos parciales.
   *
   * @param grupoOrId Instancia completa de `GrupoModel` o el ID numérico del grupo.
   * @param grupoData Datos opcionales adicionales en caso de enviar el ID por separado.
   * @returns Un Observable con la respuesta en formato de texto plano emitida por el servidor.
   */
  actualizarGrupo(grupoOrId: GrupoModel | number, grupoData?: any): Observable<string> {
    if (typeof grupoOrId === 'number') {
      const payload = { ...grupoData, id: grupoOrId };
      return this.http.put(`${this.urlBase}/actualizar`, payload, { responseType: 'text' });
    }
    return this.http.put(`${this.urlBase}/actualizar`, grupoOrId, { responseType: 'text' });
  }

  /**
   * Elimina un grupo académico utilizando su identificador único (ID).
   * Funciona como un alias directo para `eliminarGrupoPorId`.
   *
   * @param grupoId Identificador único del grupo a eliminar.
   * @returns Un Observable con la respuesta en formato de texto plano emitida por el servidor.
   */
  eliminarGrupo(grupoId: number): Observable<string> {
    return this.eliminarGrupoPorId(grupoId);
  }

  /**
   * Elimina un grupo académico enviando su ID como parámetro en la URL hacia el backend.
   *
   * @param grupoId Identificador único del grupo.
   * @returns Un Observable con la respuesta en formato de texto plano emitida por el servidor.
   */
  eliminarGrupoPorId(grupoId: number): Observable<string> {
    return this.http.delete(`${this.urlBase}/eliminarporid`, {
      params: new HttpParams().set('grupoId', grupoId),
      responseType: 'text',
    });
  }

  /**
   * Elimina todos los grupos que coincidan con un número de grupo determinado.
   *
   * @param numeroGrupo Número de grupo a eliminar.
   * @returns Un Observable con la respuesta en formato de texto plano emitida por el servidor.
   */
  eliminarGrupoPorNumero(numeroGrupo: number): Observable<string> {
    return this.http.delete(`${this.urlBase}/eliminarpornumero`, {
      params: new HttpParams().set('numeroGrupo', numeroGrupo),
      responseType: 'text',
    });
  }

  /**
   * Obtiene la lista completa de todos los grupos registrados en el sistema.
   *
   * @returns Un Observable con la respuesta HTTP que contiene un arreglo con todos los `GrupoModel`.
   */
  mostrarTodo(): Observable<HttpResponse<GrupoModel[]>> {
    return this.http.get<GrupoModel[]>(`${this.urlBase}/mostrartodo`, {
      observe: 'response',
    });
  }
}
