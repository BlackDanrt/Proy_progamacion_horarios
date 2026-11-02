import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
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
