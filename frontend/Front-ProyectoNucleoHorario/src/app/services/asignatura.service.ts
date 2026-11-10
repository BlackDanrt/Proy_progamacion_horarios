import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { AsignaturaModel } from '../models/asignatura.model';

/**
 * Servicio que consume los endpoints de /asignatura del back.
 */
@Injectable({ providedIn: 'root' })
export class AsignaturaService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly urlBase: string = `${environment.apiUrl}/asignatura`;

  /** Busca asignaturas por nombre. */
  buscarPorNombre(nombre: string): Observable<HttpResponse<AsignaturaModel[]>> {
    return this.http.get<AsignaturaModel[]>(`${this.urlBase}/buscarpornombre`, {
      params: new HttpParams().set('nombre', nombre),
      observe: 'response',
    });
  }

  /** Busca asignaturas por la cantidad de sesiones semanales. */
  buscarPorFrecuenciaSemanal(frecuenciaSemanal: number): Observable<HttpResponse<AsignaturaModel[]>> {
    return this.http.get<AsignaturaModel[]>(`${this.urlBase}/buscarporfrecuencia`, {
      params: new HttpParams().set('frecuenciaSemanal', frecuenciaSemanal),
      observe: 'response',
    });
  }

  /** Busca asignaturas por número de créditos. */
  buscarPorNumCreditos(numCreditos: number): Observable<HttpResponse<AsignaturaModel[]>> {
    return this.http.get<AsignaturaModel[]>(`${this.urlBase}/buscarporcreditos`, {
      params: new HttpParams().set('numCreditos', numCreditos),
      observe: 'response',
    });
  }

  /** Registra una asignatura. */
  crearAsignatura(asignatura: AsignaturaModel): Observable<string> {
    return this.http.post(`${this.urlBase}/crear`, asignatura, { responseType: 'text' });
  }

  /** Actualiza una asignatura existente. */
  actualizarAsignatura(asignatura: AsignaturaModel): Observable<string> {
    return this.http.put(`${this.urlBase}/actualizar`, asignatura, { responseType: 'text' });
  }

  /** El back elimina las asignaturas por nombre. */
  eliminarAsignatura(nombre: string): Observable<string> {
    return this.http.delete(`${this.urlBase}/eliminar`, {
      params: new HttpParams().set('nombre', nombre),
      responseType: 'text',
    });
  }

  /** Obtiene todas las asignaturas registradas. */
  mostrarTodo(): Observable<HttpResponse<AsignaturaModel[]>> {
    return this.http.get<AsignaturaModel[]>(`${this.urlBase}/mostrartodo`, {
      observe: 'response',
    });
  }
}
