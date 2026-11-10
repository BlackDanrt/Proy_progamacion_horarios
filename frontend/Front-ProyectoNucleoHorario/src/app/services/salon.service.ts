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

  /** Busca salones ubicados en un bloque. */
  buscarPorBloque(bloque: Bloque): Observable<HttpResponse<SalonModel[]>> {
    return this.http.get<SalonModel[]>(`${this.urlBase}/buscarporbloque`, {
      params: new HttpParams().set('bloque', bloque),
      observe: 'response',
    });
  }

  /** Busca salones con una capacidad determinada. */
  buscarPorCapacidad(capacidad: number): Observable<HttpResponse<SalonModel[]>> {
    return this.http.get<SalonModel[]>(`${this.urlBase}/buscarporcapacidad`, {
      params: new HttpParams().set('capacidad', capacidad),
      observe: 'response',
    });
  }

  /** Busca salones según si cuentan con sillas móviles. */
  buscarPorTieneSillasMoviles(tieneSillasMoviles: boolean): Observable<HttpResponse<SalonModel[]>> {
    return this.http.get<SalonModel[]>(`${this.urlBase}/buscarporsillasmoviles`, {
      params: new HttpParams().set('tieneSillasMoviles', tieneSillasMoviles),
      observe: 'response',
    });
  }

  /** Busca salones según si cuentan con computadores. */
  buscarPorTieneComputadores(tieneComputadores: boolean): Observable<HttpResponse<SalonModel[]>> {
    return this.http.get<SalonModel[]>(`${this.urlBase}/buscarporcomputadores`, {
      params: new HttpParams().set('tieneComputadores', tieneComputadores),
      observe: 'response',
    });
  }

  /** Busca un salón por bloque y número. */
  buscarPorBloqueAndNumeroSalon(bloque: Bloque, numeroSalon: number): Observable<HttpResponse<SalonModel>> {
    return this.http.get<SalonModel>(`${this.urlBase}/buscarporbloquenumero`, {
      params: new HttpParams().set('bloque', bloque).set('numeroSalon', numeroSalon),
      observe: 'response',
    });
  }

  /** Registra un nuevo salón. */
  crearSalon(salon: SalonModel): Observable<string> {
    return this.http.post(`${this.urlBase}/crear`, salon, { responseType: 'text' });
  }

  /** Actualiza los datos de un salón existente. */
  actualizarSalon(salon: SalonModel): Observable<string> {
    return this.http.put(`${this.urlBase}/actualizar`, salon, { responseType: 'text' });
  }

  /** Elimina un salón identificado por bloque y número. */
  eliminarSalon(bloque: Bloque, numeroSalon: number): Observable<string> {
    return this.http.delete(`${this.urlBase}/eliminar`, {
      params: new HttpParams().set('bloque', bloque).set('numeroSalon', numeroSalon),
      responseType: 'text',
    });
  }

  /** Obtiene todos los salones registrados. */
  mostrarTodo(): Observable<HttpResponse<SalonModel[]>> {
    return this.http.get<SalonModel[]>(`${this.urlBase}/mostrartodo`, {
      observe: 'response',
    });
  }
}
