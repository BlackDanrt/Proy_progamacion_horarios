import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SalonModel } from '../models/salon.model';
import { Bloque } from '../enums/bloque.enum';

@Injectable({
  providedIn: 'root',
})
export class SalonService {

  private cliente: HttpClient = inject(HttpClient);

  private readonly urlbase: string = 'https://gpcueb.org/pokemon-pokedes/salon';

  buscarPorBloque(bloque: Bloque): Observable<HttpResponse<SalonModel[]>> {
    return this.cliente.get<SalonModel[]>(
      this.urlbase + '/buscarporbloque?bloque=' + encodeURIComponent(bloque),
      {
        observe: 'response',
      }
    );
  }

  buscarPorCapacidad(capacidad: number): Observable<HttpResponse<SalonModel[]>> {
    return this.cliente.get<SalonModel[]>(
      this.urlbase + '/buscarporcapacidad?capacidad=' + capacidad,
      {
        observe: 'response',
      }
    );
  }

  buscarPorTieneSillasMoviles(tieneSillasMoviles: boolean): Observable<HttpResponse<SalonModel[]>> {
    return this.cliente.get<SalonModel[]>(
      this.urlbase + '/buscarporsillasmoviles?tieneSillasMoviles=' + tieneSillasMoviles,
      {
        observe: 'response',
      }
    );
  }

  buscarPorTieneComputadores(tieneComputadores: boolean): Observable<HttpResponse<SalonModel[]>> {
    return this.cliente.get<SalonModel[]>(
      this.urlbase + '/buscarporcomputadores?tieneComputadores=' + tieneComputadores,
      {
        observe: 'response',
      }
    );
  }

  buscarPorBloqueAndNumeroSalon(bloque: Bloque, numeroSalon: number): Observable<HttpResponse<SalonModel>> {
    return this.cliente.get<SalonModel>(
      this.urlbase + '/buscarporbloquenumero?bloque=' + encodeURIComponent(bloque) + '&numeroSalon=' + numeroSalon,
      {
        observe: 'response',
      }
    );
  }

  crearSalon(salonDTO: SalonModel): Observable<string> {
    return this.cliente.post(
      this.urlbase + '/crear',
      salonDTO,
      { responseType: 'text' }
    );
  }

  actualizarSalon(salonDTO: SalonModel): Observable<string> {
    return this.cliente.put(
      this.urlbase + '/actualizar',
      salonDTO,
      { responseType: 'text' }
    );
  }

  eliminarSalon(bloque: Bloque, numeroSalon: number): Observable<string> {
    return this.cliente.delete(
      this.urlbase + '/eliminar?bloque=' + encodeURIComponent(bloque) + '&numeroSalon=' + numeroSalon,
      { responseType: 'text' }
    );
  }

  mostrarTodo(): Observable<HttpResponse<SalonModel[]>> {
    return this.cliente.get<SalonModel[]>(this.urlbase + '/mostrartodo', {
      observe: 'response',
    });
  }

  mostrarTodoAdmin(): Observable<HttpResponse<SalonModel[]>> {
    const token: string | null = localStorage.getItem('admin_token');

    const headers: HttpHeaders = new HttpHeaders().set(
      'Authorization',
      `Bearer ${token || ''}`
    );

    return this.cliente.get<SalonModel[]>(`${this.urlbase}/mostrartodo`, {
      headers: headers,
      observe: 'response',
    });
  }
}
