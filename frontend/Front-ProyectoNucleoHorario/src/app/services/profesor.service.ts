import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ProfesorModel } from '../models/profesor.model';
import { Escalafon } from '../enums/escalafon.enum';
import { Especialidad } from '../enums/especialidad.enum';
import { TipoVinculacion } from '../enums/tipoViculacion.enum';
import { Dia } from '../enums/dia.enum';
import { FranjahorariaModel } from '../models/franjahoraria.model';

/**
 * Servicio que consume los endpoints de /profesor del back.
 */
@Injectable({ providedIn: 'root' })
export class ProfesorService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly urlBase: string = `${environment.apiUrl}/profesor`;

  /** Busca un profesor por correo electrónico. */
  buscarPorCorreo(correo: string): Observable<HttpResponse<ProfesorModel>> {
    return this.http.get<ProfesorModel>(`${this.urlBase}/buscarporcorreo`, {
      params: new HttpParams().set('correo', correo),
      observe: 'response',
    });
  }

  /** Busca un profesor por número de documento. */
  buscarPorDocumento(documento: number): Observable<HttpResponse<ProfesorModel>> {
    return this.http.get<ProfesorModel>(`${this.urlBase}/buscarpordocumento`, {
      params: new HttpParams().set('documento', documento),
      observe: 'response',
    });
  }

  /** Profesores disponibles en una franja (la franja debe quedar cubierta por su disponibilidad). */
  buscarPorDisponibilidad(dia: Dia, horaInicio: string, horaFin: string): Observable<HttpResponse<ProfesorModel[]>> {
    return this.http.get<ProfesorModel[]>(`${this.urlBase}/buscarpordisponibilidad`, {
      params: new HttpParams().set('dia', dia).set('horaInicio', horaInicio).set('horaFin', horaFin),
      observe: 'response',
    });
  }

  buscarPorEscalafon(escalafon: Escalafon): Observable<HttpResponse<ProfesorModel[]>> {
    return this.http.get<ProfesorModel[]>(`${this.urlBase}/buscarporescalafon`, {
      params: new HttpParams().set('escalafon', escalafon),
      observe: 'response',
    });
  }

  buscarPorEspecialidad(especialidad: Especialidad): Observable<HttpResponse<ProfesorModel[]>> {
    return this.http.get<ProfesorModel[]>(`${this.urlBase}/buscarporespecialidad`, {
      params: new HttpParams().set('especialidad', especialidad),
      observe: 'response',
    });
  }

  buscarPorTipoVinculacion(tipoVinculacion: TipoVinculacion): Observable<HttpResponse<ProfesorModel[]>> {
    return this.http.get<ProfesorModel[]>(`${this.urlBase}/buscarporvinculacion`, {
      params: new HttpParams().set('tipoVinculacion', tipoVinculacion),
      observe: 'response',
    });
  }

  /** Obtiene todos los profesores registrados. */
  mostrarTodo(): Observable<HttpResponse<ProfesorModel[]>> {
    return this.http.get<ProfesorModel[]>(`${this.urlBase}/mostrartodo`, {
      observe: 'response',
    });
  }

  /** [BACK] El back debe recibir este objeto como @RequestBody (hoy usa @RequestParam). */
  crearProfesor(profesor: ProfesorModel): Observable<string> {
    return this.http.post(`${this.urlBase}/crear`, profesor, { responseType: 'text' });
  }

  /**
   * Actualiza los datos del profesor conservando su disponibilidad.
   * [BACK] El back debe recibir este objeto como @RequestBody (hoy usa @RequestParam).
   */
  actualizarInformacionProfesor(profesor: ProfesorModel): Observable<string> {
    return this.http.put(`${this.urlBase}/actualizarinformacion`, profesor, { responseType: 'text' });
  }

  /** Agrega una franja de disponibilidad al profesor. */
  asignarDisponibilidadHoraria(idProfesor: number, dia: Dia, horaInicio: string, horaFin: string): Observable<string> {
    return this.http.put(`${this.urlBase}/asignardisponibilidadhoraria`, null, {
      params: new HttpParams()
        .set('idProfesor', idProfesor)
        .set('dia', dia)
        .set('horaInicio', horaInicio)
        .set('horaFin', horaFin),
      responseType: 'text',
    });
  }

  /** Modifica una franja de disponibilidad existente del profesor. */
  actualizarDisponibilidadHoraria(
    idProfesor: number, idFranjaHoraria: number, dia: Dia, horaInicio: string, horaFin: string
  ): Observable<string> {
    return this.http.put(`${this.urlBase}/actualizardisponibilidadhoraria`, null, {
      params: new HttpParams()
        .set('idProfesor', idProfesor)
        .set('idFranjaHoraria', idFranjaHoraria)
        .set('dia', dia)
        .set('horaInicio', horaInicio)
        .set('horaFin', horaFin),
      responseType: 'text',
    });
  }

  /** Elimina un profesor usando su identificador. */
  eliminarProfesor(id: number): Observable<string> {
    return this.http.delete(`${this.urlBase}/eliminar`, {
      params: new HttpParams().set('id', id),
      responseType: 'text',
    });
  }

  /**
   * Reemplaza toda la disponibilidad horaria del profesor por las franjas enviadas.
   * Un profesor solo puede modificar la suya.
   */
  guardarDisponibilidad(idProfesor: number, franjas: FranjahorariaModel[]): Observable<string> {
    return this.http.put(`${this.urlBase}/guardardisponibilidad`, franjas, {
      params: new HttpParams().set('idProfesor', idProfesor),
      responseType: 'text',
    });
  }

  /** Alias de compatibilidad: hace lo mismo que actualizarInformacionProfesor(). */
  actualizarProfesor(profesor: ProfesorModel): Observable<string> {
    return this.actualizarInformacionProfesor(profesor);
  }

  /** Alias de compatibilidad: hace lo mismo que mostrarTodo(). */
  mostrarTodoAdmin(): Observable<HttpResponse<ProfesorModel[]>> {
    return this.mostrarTodo();
  }
}
