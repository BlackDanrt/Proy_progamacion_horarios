import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-loginn',
  standalone: false,
  templateUrl: './loginn.html',
  styleUrl: './loginn.css',
})
export class Loginn {

  pestanaActiva: 'docente' | 'administrador' = 'docente';
  mostrarContrasenia: boolean = false;

  correo: string = '';
  contrasenia: string = '';

  logoUniversidad: string = 'https://images.squarespace-cdn.com/content/v1/5f46720407ab6957e16cb0c5/340ef60e-0fdf-4d0b-96d4-8a9a01d966b2/Logo_de_la_Universidad_El_Bosque.svg.png';
  escudoUniversidad: string = 'https://images.squarespace-cdn.com/content/v1/5f46720407ab6957e16cb0c5/1598452620676-V2SN83HBTNR4IZ354YR4/ciencias.jpg';
  imagenCampus: string = 'https://www.semana.com/resizer/v2/C7LTUNRGBFFHZCU7TSLVVV7PCE.jpeg?auth=6b3e2d3142679403466c2bd9f31c0ab3be783fd449b710b3bcde9fd1600dda3f&smart=true&quality=75&width=1920';

  constructor(private router: Router) {}

  cambiarPestana(pestana: 'docente' | 'administrador'): void {
    this.pestanaActiva = pestana;
  }
  toggleMostrarContrasenia(): void {
    this.mostrarContrasenia = !this.mostrarContrasenia;
  }

  iniciarSesion(): void {
    if (this.pestanaActiva === 'docente') {
      this.router.navigate(['/registro-profesor']);
    }else if (this.pestanaActiva === 'administrador'){
      this.router.navigate(['/inicio-administrador']);
    }
    else {
      console.log('Ingreso invalido');
    }
  }
}
