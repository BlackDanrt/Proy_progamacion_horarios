import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { SesionService } from './sesion.service';

/**
 * Agrega el token JWT a todas las peticiones al back (excepto /auth) y, si el
 * back responde 401, cierra la sesión y envía al usuario al login.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const sesion: SesionService = inject(SesionService);
  const router: Router = inject(Router);

  const esApi: boolean = req.url.startsWith(environment.apiUrl);
  const esAuth: boolean = req.url.startsWith(`${environment.apiUrl}/auth`);
  const token: string | null = sesion.getToken();

  const peticion = esApi && !esAuth && token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(peticion).pipe(
    catchError((error: HttpErrorResponse) => {
      if (esApi && !esAuth && error.status === 401) {
        sesion.cerrarSesion();
        router.navigate(['/loginn']);
      }
      return throwError(() => error);
    })
  );
};
