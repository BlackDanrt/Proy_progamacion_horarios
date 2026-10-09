import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {Loginn} from './loginn/loginn';
import {RegistroProfesor} from './registro-profesor/registro-profesor';
import {InicioAdministrador} from './inicio-administrador/inicio-administrador';
import {GestionBasesDatos} from './gestion-bases-datos/gestion-bases-datos';

const routes: Routes = [
  { path: '',                     component: Loginn },
  { path: 'loginn',               component: Loginn },
  { path: 'registro-profesor',    component: RegistroProfesor },
  { path: 'inicio-administrador',    component: InicioAdministrador },
  { path: 'gestion-bases-datos',    component: GestionBasesDatos },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
