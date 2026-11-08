import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ParametrosSistemaModel } from '../models/parametrossistema.model';

@Injectable({ providedIn: 'root' })
export class ParametrosSistemaService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly urlBase: string = `${environment.apiUrl}/parametrossistema`;

  obtenerParametros(): Observable<HttpResponse<ParametrosSistemaModel>> {
    return this.http.get<ParametrosSistemaModel>(`${this.urlBase}/traerparametros`, {
      observe: 'response',
    });
  }

  actualizarParametros(parametros: ParametrosSistemaModel): Observable<string> {
    return this.http.put(`${this.urlBase}/actualizar`, parametros, { responseType: 'text' });
  }

  traerParametros(): Observable<HttpResponse<ParametrosSistemaModel>> {
    return this.obtenerParametros();
  }
}
