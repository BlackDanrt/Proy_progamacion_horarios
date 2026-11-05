import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';
import { catchError, map, of } from 'rxjs';
import { ProfesorService } from './profesor.service';
import { SesionService } from './sesion.service';

/** Permite el acceso a cualquier usuario con sesión (administrador o profesor). */
export const sesionGuard: CanActivateFn = () => {
  const sesion: SesionService = inject(SesionService);
  const router: Router = inject(Router);
  return sesion.estaAutenticado() ? true : router.parseUrl('/loginn');
};

/** Permite el acceso solo a administradores. */
export const adminGuard: CanActivateFn = () => {
  const sesion: SesionService = inject(SesionService);
  const router: Router = inject(Router);
  return sesion.esAdministrador() ? true : router.parseUrl('/loginn');
};

/** Permite el acceso solo a profesores. */
export const profesorGuard: CanActivateFn = () => {
  const sesion: SesionService = inject(SesionService);
  const router: Router = inject(Router);
  if (sesion.esProfesor()) {
    return true;
  }
  return router.parseUrl(sesion.esAdministrador() ? '/inicio-administrador' : '/loginn');
};

/**
 * Obliga al docente a registrar su disponibilidad en el primer ingreso.
 *  - Pantalla de registro (data.pantalla = 'registro'): solo entra si aún no tiene disponibilidad.
 *  - Resto de pantallas del docente: solo entra si ya la registró.
 * El administrador no se ve afectado.
 */
export const disponibilidadGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const sesion: SesionService = inject(SesionService);
  const router: Router = inject(Router);
  const profesorService: ProfesorService = inject(ProfesorService);

  if (sesion.esAdministrador()) {
    return true;
  }

  const id: number | null = sesion.getProfesorId();
  if (id === null) {
    return router.parseUrl('/loginn');
  }

  const esPantallaRegistro: boolean = route.data['pantalla'] === 'registro';

  return profesorService.mostrarTodo().pipe(
    map((respuesta) => {
      const profesor = (respuesta.body ?? []).find(p => p.id === id);
      const tieneDisponibilidad: boolean = (profesor?.disponibilidadHoraria ?? []).length > 0;

      if (esPantallaRegistro) {
        return tieneDisponibilidad ? router.parseUrl('/mi-horario') : true;
      }
      return tieneDisponibilidad ? true : router.parseUrl('/registro-profesor');
    }),
    catchError(() => of(router.parseUrl('/loginn'))),
  );
};
