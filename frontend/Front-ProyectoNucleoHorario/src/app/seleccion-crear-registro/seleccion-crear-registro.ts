import { Component, EventEmitter, Output} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-seleccion-crear-registro',
  standalone: false,
  templateUrl: './seleccion-crear-registro.html',
  styleUrl: './seleccion-crear-registro.css',
})
export class SeleccionCrearRegistro {
  @Output() cerrar = new EventEmitter<void>();

  constructor(private router: Router) {}

  seleccionarTipo(tipo: 'docente' | 'administrador'): void {
    this.cerrarModal();
    if (tipo === 'docente') {
      this.router.navigate(['/registro-profesor'], { queryParams: { esAdmin: 'true' } });
    } else {
      this.router.navigate(['/crear-administrador']);
    }
  }

  cerrarModal(): void {
    this.cerrar.emit();
  }
}
