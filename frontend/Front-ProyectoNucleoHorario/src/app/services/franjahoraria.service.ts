import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { FranjahorariaModel } from '../models/franjahoraria.model';
import { Dia } from '../enums/dia.enum';

/**
 * Servicio que consume los endpoints de /franjahoraria del back.
 */
@Injectable({ providedIn: 'root' })
export class FranjaHorariaService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly urlBase: string = `${environment.apiUrl}/franjahoraria`;

  /** Busca franjas horarias correspondientes a un día. */
  buscarPorDia(dia: Dia): Observable<HttpResponse<FranjahorariaModel[]>> {
    return this.http.get<FranjahorariaModel[]>(`${this.urlBase}/buscarpordia`, {
      params: new HttpParams().set('dia', dia),
      observe: 'response',
    });
  }

  /** Busca franjas por su hora de inicio. */
  buscarPorHoraInicio(horaInicio: string): Observable<HttpResponse<FranjahorariaModel[]>> {
    return this.http.get<FranjahorariaModel[]>(`${this.urlBase}/buscarporhorainicio`, {
      params: new HttpParams().set('horaInicio', horaInicio),
      observe: 'response',
    });
  }

  /** Busca franjas por su hora de finalización. */
  buscarPorHoraFin(horaFin: string): Observable<HttpResponse<FranjahorariaModel[]>> {
    return this.http.get<FranjahorariaModel[]>(`${this.urlBase}/buscarporhorafin`, {
      params: new HttpParams().set('horaFin', horaFin),
      observe: 'response',
    });
  }

  /** Registra una nueva franja horaria. */
  crearFranjaHoraria(franja: FranjahorariaModel): Observable<string> {
    return this.http.post(`${this.urlBase}/crear`, franja, { responseType: 'text' });
  }

  /** Actualiza los datos de una franja horaria. */
  actualizarFranjaHoraria(franja: FranjahorariaModel): Observable<string> {
    return this.http.put(`${this.urlBase}/actualizar`, franja, { responseType: 'text' });
  }

  /** Elimina una franja usando su identificador. */
  eliminarFranjaHorariaPorId(franjaHorariaId: number): Observable<string> {
    return this.http.delete(`${this.urlBase}/eliminarporid`, {
      params: new HttpParams().set('franjaHorariaId', franjaHorariaId),
      responseType: 'text',
    });
  }

  /** Elimina las franjas asociadas a un día. */
  eliminarFranjaHorariaPorDia(dia: Dia): Observable<string> {
    return this.http.delete(`${this.urlBase}/eliminarpordia`, {
      params: new HttpParams().set('dia', dia),
      responseType: 'text',
    });
  }

  /** Obtiene todas las franjas horarias registradas. */
  mostrarTodo(): Observable<HttpResponse<FranjahorariaModel[]>> {
    return this.http.get<FranjahorariaModel[]>(`${this.urlBase}/mostrartodo`, {
      observe: 'response',
    });
  }
}
