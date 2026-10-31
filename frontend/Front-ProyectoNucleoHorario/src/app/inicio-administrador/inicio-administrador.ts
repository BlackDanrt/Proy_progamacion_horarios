import {Component, HostListener, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AuthAdminService} from '../services/auth-admin';

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
export class InicioAdministrador implements OnInit {

  constructor(
    private router: Router,
    private authAdminService: AuthAdminService
  ) {}

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

  ngOnInit(): void {
    // Si no hay sesión de administrador, se regresa al login
    if (!this.authAdminService.estaAutenticadoAdmin()) {
      this.router.navigate(['/loginn']);
      return;
    }

    // Muestra el nombre real guardado al iniciar sesión
    const nombre = this.authAdminService.getNombreAdmin();
    if (nombre) {
      this.nombreAdmin = nombre;
    }
  }

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
    this.authAdminService.cerrarSesionAdmin();
    this.router.navigate(['/loginn']);
  }

  @HostListener('document:click')
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario) {
      this.mostrarMenuUsuario = false;
    }
  }

}
