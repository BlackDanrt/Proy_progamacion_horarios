import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AsignaturaModel } from '../models/asignatura.model'


@Injectable({
  providedIn: 'root',
})
export class AsignaturaService {

  private cliente: HttpClient = inject(HttpClient);

  private readonly urlbase: string = 'https://gpcueb.org/pokemon-pokedes/asignatura';

  buscarPorNombre(nombre: string): Observable<HttpResponse<AsignaturaModel[]>> {
    return this.cliente.get<AsignaturaModel[]>(
      this.urlbase + '/buscarpornombre?nombre=' + encodeURIComponent(nombre),
      {
        observe: 'response',
      }
    );
  }

  buscarPorFrecuenciaSemanal(frecuenciaSemanal: number): Observable<HttpResponse<AsignaturaModel[]>> {
    return this.cliente.get<AsignaturaModel[]>(
      this.urlbase + '/buscarporfrecuencia?frecuenciaSemanal=' + frecuenciaSemanal,
      {
        observe: 'response',
      }
    );
  }

  buscarPorNumCreditos(numCreditos: number): Observable<HttpResponse<AsignaturaModel[]>> {
    return this.cliente.get<AsignaturaModel[]>(
      this.urlbase + '/buscarporcreditos?numCreditos=' + numCreditos,
      {
        observe: 'response',
      }
    );
  }

  crearAsignatura(asignaturaDTO: AsignaturaModel): Observable<string> {
    return this.cliente.post(
      this.urlbase + '/crear',
      asignaturaDTO,
      { responseType: 'text' }
    );
  }

  actualizarAsignatura(asignaturaDTO: AsignaturaModel): Observable<string> {
    return this.cliente.put(
      this.urlbase + '/actualizar',
      asignaturaDTO,
      { responseType: 'text' }
    );
  }


  eliminarAsignatura(nombre: string): Observable<string> {
    return this.cliente.delete(
      this.urlbase + '/eliminar?nombre=' + encodeURIComponent(nombre),
      { responseType: 'text' }
    );
  }


  mostrarTodo(): Observable<HttpResponse<AsignaturaModel[]>> {
    return this.cliente.get<AsignaturaModel[]>(this.urlbase + '/mostrartodo', {
      observe: 'response',
    });
  }


  mostrarTodoAdmin(): Observable<HttpResponse<AsignaturaModel[]>> {
    const token: string | null = localStorage.getItem('admin_token');

    const headers: HttpHeaders = new HttpHeaders().set(
      'Authorization',
      `Bearer ${token || ''}`
    );

    return this.cliente.get<AsignaturaModel[]>(`${this.urlbase}/mostrartodo`, {
      headers: headers,
      observe: 'response',
    });
  }
}
