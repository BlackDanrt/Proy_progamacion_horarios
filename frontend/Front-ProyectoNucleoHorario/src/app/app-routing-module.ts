import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {Loginn} from './loginn/loginn';

const routes: Routes = [
  { path: '',                     component: Loginn },
  { path: 'loginn',               component: Loginn },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
