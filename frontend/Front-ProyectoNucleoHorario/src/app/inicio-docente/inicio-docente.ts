import { Component, HostListener, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { SesionService } from '../services/sesion.service';
import { AuthProfesorService } from '../services/auth-profesor';

/** Información de una tarjeta de acceso rápido del panel docente. */
interface TarjetaAcceso {
  /** Título visible de la tarjeta. */
  titulo: string;
  /** Texto que explica el destino de la tarjeta. */
  descripcion: string;
  /** Nombre del icono que representa la acción. */
  icono: string;
  /** Ruta de navegación asociada a la tarjeta. */
  ruta: string;
  /** Etiqueta complementaria que se muestra en la tarjeta. */
  badge: string;
}

@Component({
  selector: 'app-inicio-docente',
  standalone: false,
  templateUrl: './inicio-docente.html',
  styleUrl: './inicio-docente.css'
})
/** Presenta el panel principal del docente y sus accesos rápidos. */
export class InicioDocente implements OnInit {
  private router = inject(Router);
  private sesion = inject(SesionService);
  private authProfesor = inject(AuthProfesorService);

  /** Nombre del docente mostrado en el panel. */
  nombreDocente: string = 'Docente';
  /** Controla la visibilidad del menú de usuario. */
  mostrarMenuUsuario: boolean = false;

  /** Accesos rápidos disponibles desde el panel docente. */
  accesosDirectos: TarjetaAcceso[] = [
    {
      titulo: 'Mi Horario Completo',
      descripcion: 'Consulta tus clases asignadas, aulas y bloques para toda la semana.',
      icono: 'calendar',
      ruta: '/mi-horario',
      badge: 'Semana Actual'
    }
  ];

  /** Clases de ejemplo que se muestran como contenido del panel. */
  clasesDeHoy = [
    { materia: 'Cálculo Diferencial', hora: '08:00 AM - 10:00 AM', salon: 'Bloque A - A402' },
    { materia: 'Física I', hora: '11:00 AM - 01:00 PM', salon: 'Bloque B - B210' }
  ];

  /** Verifica la sesión docente y carga el nombre al iniciar el componente. */
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

  /** Navega a la ruta asociada a una tarjeta de acceso. */
  ejecutarAccion(item: TarjetaAcceso): void {
    this.router.navigate([item.ruta]);
  }

  /** Navega a la ruta indicada. */
  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  /** Alterna el menú de usuario y evita que el clic se propague al documento. */
  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario = !this.mostrarMenuUsuario;
  }

  /** Cierra la sesión docente y vuelve a la pantalla de acceso. */
  cerrarSesion(): void {
    this.mostrarMenuUsuario = false;
    this.authProfesor.cerrarSesionProfesor();
    this.sesion.cerrarSesion();
    this.router.navigate(['/loginn']);
  }

  @HostListener('document:click')
  /** Cierra el menú de usuario cuando se hace clic fuera de él. */
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario) {
      this.mostrarMenuUsuario = false;
    }
  }
}
