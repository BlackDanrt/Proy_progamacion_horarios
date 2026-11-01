import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { SalonModel } from '../models/salon.model';
import { Bloque } from '../enums/bloque.enum';

/**
 * Servicio que consume los endpoints de /salon del back.
 */
@Injectable({ providedIn: 'root' })
export class SalonService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly urlBase: string = `${environment.apiUrl}/salon`;

  buscarPorBloque(bloque: Bloque): Observable<HttpResponse<SalonModel[]>> {
    return this.http.get<SalonModel[]>(`${this.urlBase}/buscarporbloque`, {
      params: new HttpParams().set('bloque', bloque),
      observe: 'response',
    });
  }

  buscarPorCapacidad(capacidad: number): Observable<HttpResponse<SalonModel[]>> {
    return this.http.get<SalonModel[]>(`${this.urlBase}/buscarporcapacidad`, {
      params: new HttpParams().set('capacidad', capacidad),
      observe: 'response',
    });
  }

  buscarPorTieneSillasMoviles(tieneSillasMoviles: boolean): Observable<HttpResponse<SalonModel[]>> {
    return this.http.get<SalonModel[]>(`${this.urlBase}/buscarporsillasmoviles`, {
      params: new HttpParams().set('tieneSillasMoviles', tieneSillasMoviles),
      observe: 'response',
    });
  }

  buscarPorTieneComputadores(tieneComputadores: boolean): Observable<HttpResponse<SalonModel[]>> {
    return this.http.get<SalonModel[]>(`${this.urlBase}/buscarporcomputadores`, {
      params: new HttpParams().set('tieneComputadores', tieneComputadores),
      observe: 'response',
    });
  }

  buscarPorBloqueAndNumeroSalon(bloque: Bloque, numeroSalon: number): Observable<HttpResponse<SalonModel>> {
    return this.http.get<SalonModel>(`${this.urlBase}/buscarporbloquenumero`, {
      params: new HttpParams().set('bloque', bloque).set('numeroSalon', numeroSalon),
      observe: 'response',
    });
  }

  crearSalon(salon: SalonModel): Observable<string> {
    return this.http.post(`${this.urlBase}/crear`, salon, { responseType: 'text' });
  }

  actualizarSalon(salon: SalonModel): Observable<string> {
    return this.http.put(`${this.urlBase}/actualizar`, salon, { responseType: 'text' });
  }

  eliminarSalon(bloque: Bloque, numeroSalon: number): Observable<string> {
    return this.http.delete(`${this.urlBase}/eliminar`, {
      params: new HttpParams().set('bloque', bloque).set('numeroSalon', numeroSalon),
      responseType: 'text',
    });
  }

  mostrarTodo(): Observable<HttpResponse<SalonModel[]>> {
    return this.http.get<SalonModel[]>(`${this.urlBase}/mostrartodo`, {
      observe: 'response',
    });
  }
}
