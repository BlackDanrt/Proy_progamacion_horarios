import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { AuditoriaModel } from '../models/auditoria.model';

@Injectable({ providedIn: 'root' })
export class AuditoriaService {

  private readonly http: HttpClient = inject(HttpClient);
  private readonly urlBase: string = `${environment.apiUrl}/auditoria`;

  mostrarTodo(): Observable<HttpResponse<AuditoriaModel[]>> {
    return this.http.get<AuditoriaModel[]>(`${this.urlBase}/mostrar`, {
      observe: 'response',
    });
  }

}
