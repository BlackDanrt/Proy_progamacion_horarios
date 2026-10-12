import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Loginn } from './loginn/loginn';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RegistroProfesor } from './registro-profesor/registro-profesor';
import { InicioAdministrador } from './inicio-administrador/inicio-administrador';
import { GestionBasesDatos } from './gestion-bases-datos/gestion-bases-datos';
import { SeleccionCrearRegistro } from './seleccion-crear-registro/seleccion-crear-registro';

@NgModule({
  declarations: [
    App,
    Loginn,
    RegistroProfesor,
    InicioAdministrador,
    GestionBasesDatos,
    SeleccionCrearRegistro,
  ],
  imports: [BrowserModule, AppRoutingModule, RouterModule, FormsModule, HttpClientModule],
  providers: [provideBrowserGlobalErrorListeners()],
  bootstrap: [App],
})
export class AppModule {}
