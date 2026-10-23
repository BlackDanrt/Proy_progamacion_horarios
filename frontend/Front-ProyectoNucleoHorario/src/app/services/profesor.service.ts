import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProfesorModel } from '../models/profesor.model';
import { AsignaturaModel } from '../models/asignatura.model';
import { Escalafon } from '../enums/escalafon.enum';
import {FranjahorariaModel} from '../models/franjahoraria.model';
import {TipoVinculacion} from '../enums/tipoViculacion.enum';

@Injectable({
  providedIn: 'root',
})
export class ProfesorService {

  private cliente: HttpClient = inject(HttpClient);

  private readonly urlbase: string = 'https://gpcueb.org/pokemon-pokedes/profesor';

  buscarPorCorreo(correo: string): Observable<HttpResponse<ProfesorModel>> {
    return this.cliente.get<ProfesorModel>(
      this.urlbase + '/buscarporcorreo?correo=' + encodeURIComponent(correo),
      {
        observe: 'response',
      }
    );
  }

  buscarPorDocumento(documento: number): Observable<HttpResponse<ProfesorModel>> {
    return this.cliente.get<ProfesorModel>(
      this.urlbase + '/buscarpordocumento?documento=' + documento,
      {
        observe: 'response',
      }
    );
  }

  buscarPorDisponibilidad(franjaHorariaDTO: FranjahorariaModel): Observable<HttpResponse<ProfesorModel[]>> {
    return this.cliente.get<ProfesorModel[]>(
      this.urlbase + '/buscarpordisponibilidad?franjaHorariaDTO=' + encodeURIComponent(JSON.stringify(franjaHorariaDTO)),
      {
        observe: 'response',
      }
    );
  }

  buscarPorEscalafon(escalafon: Escalafon): Observable<HttpResponse<ProfesorModel[]>> {
    return this.cliente.get<ProfesorModel[]>(
      this.urlbase + '/buscarporescalafon?escalafon=' + encodeURIComponent(escalafon),
      {
        observe: 'response',
      }
    );
  }

  buscarPorEspecialidad(asignaturaDTO: AsignaturaModel): Observable<HttpResponse<ProfesorModel[]>> {
    return this.cliente.get<ProfesorModel[]>(
      this.urlbase + '/buscarporespecialidad?asignaturaDTO=' + encodeURIComponent(JSON.stringify(asignaturaDTO)),
      {
        observe: 'response',
      }
    );
  }

  buscarPorTipoVinculacion(tipoVinculacion: TipoVinculacion): Observable<HttpResponse<ProfesorModel[]>> {
    return this.cliente.get<ProfesorModel[]>(
      this.urlbase + '/buscarporvinculacion?tipoVinculacion=' + encodeURIComponent(tipoVinculacion),
      {
        observe: 'response',
      }
    );
  }

  mostrarTodo(): Observable<HttpResponse<ProfesorModel[]>> {
    return this.cliente.get<ProfesorModel[]>(this.urlbase + '/mostrartodo', {
      observe: 'response',
    });
  }

  crearProfesor(profesorDTO: ProfesorModel): Observable<string> {
    return this.cliente.post(
      this.urlbase + '/crear',
      profesorDTO,
      { responseType: 'text' }
    );
  }

  actualizarProfesor(profesorDTO: ProfesorModel): Observable<string> {
    return this.cliente.put(
      this.urlbase + '/actualizar',
      profesorDTO,
      { responseType: 'text' }
    );
  }

  eliminarProfesor(id: number): Observable<string> {
    return this.cliente.delete(
      this.urlbase + '/eliminar?id=' + id,
      { responseType: 'text' }
    );
  }

  mostrarTodoAdmin(): Observable<HttpResponse<ProfesorModel[]>> {
    const token: string | null = localStorage.getItem('admin_token');

    const headers: HttpHeaders = new HttpHeaders().set(
      'Authorization',
      `Bearer ${token || ''}`
    );

    return this.cliente.get<ProfesorModel[]>(`${this.urlbase}/mostrartodo`, {
      headers: headers,
      observe: 'response',
    });
  }
}
