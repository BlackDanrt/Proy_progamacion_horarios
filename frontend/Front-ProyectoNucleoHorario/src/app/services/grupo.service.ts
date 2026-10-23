import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GrupoModel } from '../models/grupo.model';
import { AsignaturaModel } from '../models/asignatura.model';
import { ProfesorModel } from '../models/profesor.model';
import {EstadoGrupo} from '../enums/estadoGrupo.enum';


@Injectable({
  providedIn: 'root',
})
export class GrupoService {

  private cliente: HttpClient = inject(HttpClient);

  private readonly urlbase: string = 'https://gpcueb.org/pokemon-pokedes/grupo';

  buscarPorAsignatura(asignaturaDTO: AsignaturaModel): Observable<HttpResponse<GrupoModel[]>> {
    return this.cliente.request<GrupoModel[]>('GET', this.urlbase + '/buscarporasignatura', {
      body: asignaturaDTO,
      observe: 'response',
    });
  }

  buscarPorEstado(estadoGrupo: EstadoGrupo): Observable<HttpResponse<GrupoModel[]>> {
    return this.cliente.request<GrupoModel[]>('GET', this.urlbase + '/buscarporestado', {
      body: estadoGrupo,
      observe: 'response',
    });
  }

  buscarPorNumeroGrupo(numeroGrupo: number): Observable<HttpResponse<GrupoModel[]>> {
    return this.cliente.request<GrupoModel[]>('GET', this.urlbase + '/buscarpornumero', {
      body: numeroGrupo,
      observe: 'response',
    });
  }

  buscarPorProfesor(profesorDTO: ProfesorModel): Observable<HttpResponse<GrupoModel[]>> {
    return this.cliente.request<GrupoModel[]>('GET', this.urlbase + '/buscarporprofesor', {
      body: profesorDTO,
      observe: 'response',
    });
  }

  crearGrupo(grupoDTO: GrupoModel): Observable<string> {
    return this.cliente.post(
      this.urlbase + '/crear',
      grupoDTO,
      { responseType: 'text' }
    );
  }

  actualizarGrupo(grupoDTO: GrupoModel): Observable<string> {
    return this.cliente.put(
      this.urlbase + '/actualizar',
      grupoDTO,
      { responseType: 'text' }
    );
  }

  eliminarGrupoPorId(grupoId: number): Observable<string> {
    return this.cliente.delete(
      this.urlbase + '/eliminarporid?grupoId=' + grupoId,
      { responseType: 'text' }
    );
  }

  eliminarGrupoPorNumero(numeroGrupo: number): Observable<string> {
    return this.cliente.delete(
      this.urlbase + '/eliminarpornumero?numeroGrupo=' + numeroGrupo,
      { responseType: 'text' }
    );
  }

  mostrarTodo(): Observable<HttpResponse<GrupoModel[]>> {
    return this.cliente.get<GrupoModel[]>(this.urlbase + '/mostrartodo', {
      observe: 'response',
    });
  }

  mostrarTodoAdmin(): Observable<HttpResponse<GrupoModel[]>> {
    const token: string | null = localStorage.getItem('admin_token');

    const headers: HttpHeaders = new HttpHeaders().set(
      'Authorization',
      `Bearer ${token || ''}`
    );

    return this.cliente.get<GrupoModel[]>(`${this.urlbase}/mostrartodo`, {
      headers: headers,
      observe: 'response',
    });
  }
}
