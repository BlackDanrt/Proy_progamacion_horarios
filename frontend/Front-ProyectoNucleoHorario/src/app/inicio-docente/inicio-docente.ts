import { Component, HostListener, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { SesionService } from '../services/sesion.service';
import { AuthProfesorService } from '../services/auth-profesor';

interface TarjetaAcceso {
  titulo: string;
  descripcion: string;
  icono: string;
  ruta: string;
  badge: string;
}

@Component({
  selector: 'app-inicio-docente',
  standalone: false,
  templateUrl: './inicio-docente.html',
  styleUrl: './inicio-docente.css'
})
export class InicioDocente implements OnInit {
  private router = inject(Router);
  private sesion = inject(SesionService);
  private authProfesor = inject(AuthProfesorService);

  nombreDocente: string = 'Docente';
  mostrarMenuUsuario: boolean = false;

  accesosDirectos: TarjetaAcceso[] = [
    {
      titulo: 'Mi Horario Completo',
      descripcion: 'Consulta tus clases asignadas, aulas y bloques para toda la semana.',
      icono: 'calendar',
      ruta: '/mi-horario',
      badge: 'Semana Actual'
    }
  ];

  // Datos de relleno para llenar el espacio de forma útil
  clasesDeHoy = [
    { materia: 'Cálculo Diferencial', hora: '08:00 AM - 10:00 AM', salon: 'Bloque A - A402' },
    { materia: 'Física I', hora: '11:00 AM - 01:00 PM', salon: 'Bloque B - B210' }
  ];

  ngOnInit(): void {
    if (!this.sesion.esProfesor()) {
      this.router.navigate(['/loginn']);
      return;
    }
    const nombre = this.authProfesor.getNombreProfesor();
    if (nombre) {
      this.nombreDocente = nombre;
    }
  }

  ejecutarAccion(item: TarjetaAcceso): void {
    this.router.navigate([item.ruta]);
  }

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario = !this.mostrarMenuUsuario;
  }

  cerrarSesion(): void {
    this.mostrarMenuUsuario = false;
    this.authProfesor.cerrarSesionProfesor();
    this.sesion.cerrarSesion();
    this.router.navigate(['/loginn']);
  }

  @HostListener('document:click')
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario) {
      this.mostrarMenuUsuario = false;
    }
  }
}
