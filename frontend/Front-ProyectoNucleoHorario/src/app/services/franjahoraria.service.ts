import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import {FranjahorariaModel} from '../models/franjahoraria.model';
import {dia} from '../enums/dia.enum';


@Injectable({
  providedIn: 'root',
})
export class FranjaHorariaService {

  private cliente: HttpClient = inject(HttpClient);

  private readonly urlbase: string = 'https://gpcueb.org/pokemon-pokedes/franjahoraria';

  buscarPorDia(dia: dia): Observable<HttpResponse<FranjahorariaModel[]>> {
    return this.cliente.get<FranjahorariaModel[]>(
      this.urlbase + '/buscarpordia?dia=' + encodeURIComponent(dia),
      {
        observe: 'response',
      }
    );
  }

  buscarPorHoraInicio(horaInicio: string): Observable<HttpResponse<FranjahorariaModel[]>> {
    return this.cliente.get<FranjahorariaModel[]>(
      this.urlbase + '/buscarporhorainicio?horaInicio=' + encodeURIComponent(horaInicio),
      {
        observe: 'response',
      }
    );
  }

  buscarPorHoraFin(horaFin: string): Observable<HttpResponse<FranjahorariaModel[]>> {
    return this.cliente.get<FranjahorariaModel[]>(
      this.urlbase + '/buscarporhorafin?horaFin=' + encodeURIComponent(horaFin),
      {
        observe: 'response',
      }
    );
  }

  crearFranjaHoraria(franjaHorariaDTO: FranjahorariaModel): Observable<string> {
    return this.cliente.post(
      this.urlbase + '/crear',
      franjaHorariaDTO,
      { responseType: 'text' }
    );
  }

  actualizarFranjaHoraria(franjaHorariaDTO: FranjahorariaModel): Observable<string> {
    return this.cliente.put(
      this.urlbase + '/actualizar',
      franjaHorariaDTO,
      { responseType: 'text' }
    );
  }

  eliminarFranjaHorariaPorId(franjaHorariaId: number): Observable<string> {
    return this.cliente.delete(
      this.urlbase + '/eliminarporid?franjaHorariaId=' + franjaHorariaId,
      { responseType: 'text' }
    );
  }

  eliminarFranjaHorariaPorDia(dia: dia): Observable<string> {
    return this.cliente.delete(
      this.urlbase + '/eliminarpordia?dia=' + encodeURIComponent(dia),
      { responseType: 'text' }
    );
  }

  mostrarTodo(): Observable<HttpResponse<FranjahorariaModel[]>> {
    return this.cliente.get<FranjahorariaModel[]>(this.urlbase + '/mostrartodo', {
      observe: 'response',
    });
  }

  mostrarTodoAdmin(): Observable<HttpResponse<FranjahorariaModel[]>> {
    const token: string | null = localStorage.getItem('admin_token');

    const headers: HttpHeaders = new HttpHeaders().set(
      'Authorization',
      `Bearer ${token || ''}`
    );

    return this.cliente.get<FranjahorariaModel[]>(`${this.urlbase}/mostrartodo`, {
      headers: headers,
      observe: 'response',
    });
  }
}
