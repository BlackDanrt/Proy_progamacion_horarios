import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Loginn } from './loginn/loginn';
import { RouterModule } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RegistroProfesor } from './registro-profesor/registro-profesor';
import { InicioAdministrador } from './inicio-administrador/inicio-administrador';
import { GestionBasesDatos } from './gestion-bases-datos/gestion-bases-datos';
import { SeleccionCrearRegistro } from './seleccion-crear-registro/seleccion-crear-registro';
import { CrearAdministrador } from './crear-administrador/crear-administrador';
import { AsignacionHorarios } from './asignacion-horarios/asignacion-horarios';
import { ConsultaHorario } from './consulta-horario/consulta-horario';
import { authInterceptor } from './services/auth.interceptor';
import { GestionAsignatura } from './gestion-asignatura/gestion-asignatura';
import { GestionAulas } from './gestion-aulas/gestion-aulas';
import { InicioDocente } from './inicio-docente/inicio-docente';

@NgModule({
  declarations: [
    App,
    Loginn,
    RegistroProfesor,
    InicioAdministrador,
    GestionBasesDatos,
    SeleccionCrearRegistro,
    CrearAdministrador,
    AsignacionHorarios,
    ConsultaHorario,
    GestionAsignatura,
    GestionAulas,
    InicioDocente,
  ],
  imports: [BrowserModule, AppRoutingModule, RouterModule, FormsModule],
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideHttpClient(withInterceptors([authInterceptor])),
  ],
  bootstrap: [App],
})
export class AppModule {}
