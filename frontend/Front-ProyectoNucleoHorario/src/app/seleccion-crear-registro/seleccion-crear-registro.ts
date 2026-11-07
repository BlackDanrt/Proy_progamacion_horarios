import { Component, EventEmitter, Output} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-seleccion-crear-registro',
  standalone: false,
  templateUrl: './seleccion-crear-registro.html',
  styleUrl: './seleccion-crear-registro.css',
})
/** Permite elegir qué tipo de usuario se desea registrar. */
export class SeleccionCrearRegistro {
  /** Emite el evento para cerrar el selector de tipo de registro. */
  @Output() cerrar = new EventEmitter<void>();

  /** Inicializa el enrutador para abrir el formulario seleccionado. */
  constructor(private router: Router) {}

  /** Cierra el selector y navega al formulario según el tipo elegido. */
  seleccionarTipo(tipo: 'docente' | 'administrador'): void {
    this.cerrarModal();
    if (tipo === 'docente') {
      this.router.navigate(['/registro-profesor'], { queryParams: { esAdmin: 'true' } });
    } else {
      this.router.navigate(['/crear-administrador']);
    }
  }

  /** Emite el evento de cierre del selector. */
  cerrarModal(): void {
    this.cerrar.emit();
  }
}
