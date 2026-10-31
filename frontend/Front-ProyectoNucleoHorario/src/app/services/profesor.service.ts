import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProfesorModel } from '../models/profesor.model';
import { AsignaturaModel } from '../models/asignatura.model';
import { Escalafon } from '../enums/escalafon.enum';
import { FranjahorariaModel } from '../models/franjahoraria.model';
import { TipoVinculacion } from '../enums/tipoViculacion.enum';

@Injectable({
  providedIn: 'root',
})
export class ProfesorService {

  private cliente: HttpClient = inject(HttpClient);

  private readonly urlbase: string = 'http://localhost:8080/profesor';

  /**
   * Encabezados para consultas: las pueden hacer administrador y profesor.
   * Usa el token de administrador si existe; si no, el del profesor.
   */
  private headersConsulta(): HttpHeaders {
    const token: string | null =
      localStorage.getItem('admin_token') || localStorage.getItem('profesor_token');

    return new HttpHeaders().set('Authorization', `Bearer ${token || ''}`);
  }

  /**
   * Encabezados para crear, actualizar y eliminar: solo administrador.
   */
  private headersAdmin(): HttpHeaders {
    const token: string | null = localStorage.getItem('admin_token');

    return new HttpHeaders().set('Authorization', `Bearer ${token || ''}`);
  }

  buscarPorCorreo(correo: string): Observable<HttpResponse<ProfesorModel>> {
    return this.cliente.get<ProfesorModel>(
      this.urlbase + '/buscarporcorreo?correo=' + encodeURIComponent(correo),
      {
        headers: this.headersConsulta(),
        observe: 'response',
      }
    );
  }

  buscarPorDocumento(documento: number): Observable<HttpResponse<ProfesorModel>> {
    return this.cliente.get<ProfesorModel>(
      this.urlbase + '/buscarpordocumento?documento=' + documento,
      {
        headers: this.headersConsulta(),
        observe: 'response',
      }
    );
  }

  buscarPorDisponibilidad(franjaHorariaDTO: FranjahorariaModel): Observable<HttpResponse<ProfesorModel[]>> {
    return this.cliente.get<ProfesorModel[]>(
      this.urlbase + '/buscarpordisponibilidad?franjaHorariaDTO=' + encodeURIComponent(JSON.stringify(franjaHorariaDTO)),
      {
        headers: this.headersConsulta(),
        observe: 'response',
      }
    );
  }

  buscarPorEscalafon(escalafon: Escalafon): Observable<HttpResponse<ProfesorModel[]>> {
    return this.cliente.get<ProfesorModel[]>(
      this.urlbase + '/buscarporescalafon?escalafon=' + encodeURIComponent(escalafon),
      {
        headers: this.headersConsulta(),
        observe: 'response',
      }
    );
  }

  buscarPorEspecialidad(asignaturaDTO: AsignaturaModel): Observable<HttpResponse<ProfesorModel[]>> {
    return this.cliente.get<ProfesorModel[]>(
      this.urlbase + '/buscarporespecialidad?asignaturaDTO=' + encodeURIComponent(JSON.stringify(asignaturaDTO)),
      {
        headers: this.headersConsulta(),
        observe: 'response',
      }
    );
  }

  buscarPorTipoVinculacion(tipoVinculacion: TipoVinculacion): Observable<HttpResponse<ProfesorModel[]>> {
    return this.cliente.get<ProfesorModel[]>(
      this.urlbase + '/buscarporvinculacion?tipoVinculacion=' + encodeURIComponent(tipoVinculacion),
      {
        headers: this.headersConsulta(),
        observe: 'response',
      }
    );
  }

  mostrarTodo(): Observable<HttpResponse<ProfesorModel[]>> {
    return this.cliente.get<ProfesorModel[]>(this.urlbase + '/mostrartodo', {
      headers: this.headersConsulta(),
      observe: 'response',
    });
  }

  crearProfesor(profesorDTO: ProfesorModel): Observable<string> {
    return this.cliente.post(
      this.urlbase + '/crear',
      profesorDTO,
      {
        headers: this.headersAdmin(),
        responseType: 'text',
      }
    );
  }

  actualizarProfesor(profesorDTO: ProfesorModel): Observable<string> {
    return this.cliente.put(
      this.urlbase + '/actualizar',
      profesorDTO,
      {
        headers: this.headersAdmin(),
        responseType: 'text',
      }
    );
  }

  eliminarProfesor(id: number): Observable<string> {
    return this.cliente.delete(
      this.urlbase + '/eliminar?id=' + id,
      {
        headers: this.headersAdmin(),
        responseType: 'text',
      }
    );
  }

  /**
   * Se mantiene por compatibilidad con los componentes que ya lo usan.
   */
  mostrarTodoAdmin(): Observable<HttpResponse<ProfesorModel[]>> {
    return this.mostrarTodo();
  }
}
