import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Loginn } from './loginn/loginn';
import { RegistroProfesor } from './registro-profesor/registro-profesor';
import { InicioAdministrador } from './inicio-administrador/inicio-administrador';
import { GestionBasesDatos } from './gestion-bases-datos/gestion-bases-datos';
import { CrearAdministrador } from './crear-administrador/crear-administrador';
import { AsignacionHorarios } from './asignacion-horarios/asignacion-horarios';
import { Auditoria } from './auditoria/auditoria';
import { ParametrosSistema } from './parametros-sistema/parametros-sistema';
import { ConsultaHorario } from './consulta-horario/consulta-horario';
import { adminGuard, disponibilidadGuard, profesorGuard, sesionGuard } from './services/auth.guards';
import { GestionAsignatura } from './gestion-asignatura/gestion-asignatura';
import { GestionAulas } from './gestion-aulas/gestion-aulas';
import { InicioDocente } from './inicio-docente/inicio-docente';
import { EdicionHorarios } from './edicion-horarios/edicion-horarios';


/** Rutas de navegación y controles de acceso de la aplicación. */
const routes: Routes = [
  /** Envía la ruta raíz a la pantalla de inicio de sesión. */
  { path: '',                     redirectTo: 'loginn', pathMatch: 'full' },
  /** Pantalla de autenticación para los usuarios. */
  { path: 'loginn',               component: Loginn },
  /** Formulario docente, sujeto a autenticación y registro de disponibilidad. */
  { path: 'registro-profesor',    component: RegistroProfesor,    canActivate: [sesionGuard, disponibilidadGuard], data: { pantalla: 'registro' }  },
  /** Panel principal de administración. */
  { path: 'inicio-administrador', component: InicioAdministrador, canActivate: [adminGuard] },
  /** Gestión administrativa de docentes y cuentas. */
  { path: 'gestion-bases-datos',  component: GestionBasesDatos,   canActivate: [adminGuard] },
  /** Formulario para crear cuentas de administrador. */
  { path: 'crear-administrador',  component: CrearAdministrador,  canActivate: [adminGuard] },
  /** Asignación de horarios por parte de un administrador. */
  { path: 'horarios',             component: AsignacionHorarios,  canActivate: [adminGuard] },
  /** Edición y gestión de horarios ya guardados en la base de datos. */
  { path: 'edicion-horarios',     component: EdicionHorarios,     canActivate: [adminGuard] },
  /** Consulta administrativa del horario de un docente. */
  { path: 'consulta-horario',     component: ConsultaHorario,     canActivate: [adminGuard] },
  { path: 'auditoria', component: Auditoria, canActivate: [adminGuard] },
  { path: 'parametros-sistema', component: ParametrosSistema, canActivate: [adminGuard] },
  { path: 'mi-horario',           component: ConsultaHorario,     canActivate: [profesorGuard], data: { modo: 'docente' } },
  /** Consulta del horario propio de un docente. */
  { path: 'mi-horario',           component: ConsultaHorario,     canActivate: [profesorGuard,disponibilidadGuard], data: { modo: 'docente' } },
  /** Gestión del catálogo de asignaturas. */
  { path: 'gestion-asignatura',   component: GestionAsignatura,   canActivate: [adminGuard] },
  /** Gestión del catálogo de salones. */
  { path: 'gestion-aulas',        component: GestionAulas,        canActivate: [adminGuard] },
  /** Panel principal del docente. */
  { path:'inicio-docente',        component: InicioDocente,       canActivate:[profesorGuard] },

  /** Redirige las rutas desconocidas a la pantalla de acceso. */
  { path: '**',                   redirectTo: 'loginn' },
];

/** Registra las rutas de la aplicación en el enrutador de Angular. */
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
/** Módulo que exporta el enrutador configurado para la aplicación. */
export class AppRoutingModule { }
