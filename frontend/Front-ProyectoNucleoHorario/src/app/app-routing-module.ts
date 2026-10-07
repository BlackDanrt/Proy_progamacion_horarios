import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {Loginn} from './loginn/loginn';
import {RegistroProfesor} from './registro-profesor/registro-profesor';

const routes: Routes = [
  { path: '',                     component: Loginn },
  { path: 'loginn',               component: Loginn },
  { path: 'registro-profesor',    component: RegistroProfesor },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
