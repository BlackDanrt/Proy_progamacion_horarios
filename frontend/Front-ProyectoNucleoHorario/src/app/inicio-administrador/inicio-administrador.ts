import {Component, HostListener} from '@angular/core';
import {Router} from '@angular/router';

interface TarjetaAcceso {
  titulo: string;
  descripcion: string;
  icono: string;
  ruta: string;
  badge: string;
}

@Component({
  selector: 'app-inicio-administrador',
  standalone: false,
  templateUrl: './inicio-administrador.html',
  styleUrl: './inicio-administrador.css',
})
export class InicioAdministrador {

  constructor(private router: Router) {}
  nombreAdmin: string = 'Administrador';
  rol: string = 'Administrador General';

  metricas = [
    { titulo: 'Docentes Activos', valor: '48', icono: 'users' },
    { titulo: 'Asignaturas', valor: '32', icono: 'book' },
    { titulo: 'Aulas Asignadas', valor: '18', icono: 'building' },
    { titulo: 'Personas en la Base de Datos', valor: '10', icono: 'database' }
  ];

  accesosDirectos: TarjetaAcceso[] = [
    {
      titulo: 'Gestión de Bases de Datos',
      descripcion: 'Administra docentes, roles, especialidades y cargas académicas del sistema.',
      icono: 'database',
      ruta: '/gestion-bases-datos',
      badge: '10 Registros'
    },
    {
      titulo: 'Asignación de Horarios',
      descripcion: 'Organiza la disponibilidad horaria semanal por asignaturas y aulas.',
      icono: 'calendar',
      ruta: '/horarios',
      badge: 'Semana Actual'
    }
  ];

  ngOnInit(): void {}

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  mostrarMenuUsuario: boolean = false;

  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario = !this.mostrarMenuUsuario;
  }

  cerrarSesion(): void {
    this.mostrarMenuUsuario = false;
    this.router.navigate(['/loginn']); // Cambia '/login' por la ruta de tu login
  }

  @HostListener('document:click')
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario) {
      this.mostrarMenuUsuario = false;
    }
  }

}
