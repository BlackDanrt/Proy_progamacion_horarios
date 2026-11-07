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
import { adminGuard, profesorGuard, sesionGuard } from './services/auth.guards';

const routes: Routes = [
  { path: '',                     redirectTo: 'loginn', pathMatch: 'full' },
  { path: 'loginn',               component: Loginn },
  { path: 'registro-profesor',    component: RegistroProfesor,    canActivate: [sesionGuard] },
  { path: 'inicio-administrador', component: InicioAdministrador, canActivate: [adminGuard] },
  { path: 'gestion-bases-datos',  component: GestionBasesDatos,   canActivate: [adminGuard] },
  { path: 'crear-administrador',  component: CrearAdministrador,  canActivate: [adminGuard] },
  { path: 'horarios',             component: AsignacionHorarios,  canActivate: [adminGuard] },
  { path: 'consulta-horario',     component: ConsultaHorario,     canActivate: [adminGuard] },
  { path: 'auditoria', component: Auditoria, canActivate: [adminGuard] },
  { path: 'parametros-sistema', component: ParametrosSistema, canActivate: [adminGuard] },
  { path: 'mi-horario',           component: ConsultaHorario,     canActivate: [profesorGuard], data: { modo: 'docente' } },
  { path: '**',                   redirectTo: 'loginn' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
