import { Component } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-crear-administrador',
  standalone: false,
  templateUrl: './crear-administrador.html',
  styleUrl: './crear-administrador.css',
})
export class CrearAdministrador {

  nombreCompleto: string = '';
  correoInstitucional: string = '';
  cargaAsignada: string = '';
  especialidad: string = '';
  contrasenia: string = '';

  mostrarContrasenia: boolean = false;

  constructor(private router: Router) {}

  ngOnInit(): void {}

  toggleMostrarContrasenia(): void {
    this.mostrarContrasenia = !this.mostrarContrasenia;
  }

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  guardarAdministrador(): void {
    const nuevoAdmin = {
      nombre: this.nombreCompleto,
      correo: this.correoInstitucional,
      carga: this.cargaAsignada,
      especialidad: this.especialidad,
      contrasenia: this.contrasenia,
      rol: 'Administrador'
    };

    console.log('Nuevo administrador guardado:', nuevoAdmin);
    this.router.navigate(['/gestion-bases-datos']);
  }

  cancelar(): void {
    this.router.navigate(['/gestion-bases-datos']);
  }
}
