import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: false,
  styleUrl: './app.css'
})
/** Componente raíz de la aplicación. */
export class App {
  /** Título de la aplicación, disponible como señal reactiva. */
  protected readonly title = signal('Front-ProyectoNucleoHorario');
}
