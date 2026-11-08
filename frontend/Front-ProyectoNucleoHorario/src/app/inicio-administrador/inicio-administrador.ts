import { Component, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthAdminService } from '../services/auth-admin';

/** Datos de una tarjeta de acceso rápido del panel administrativo. */
interface TarjetaAcceso {
  /** Título visible de la tarjeta. */
  titulo: string;
  /** Descripción de la acción disponible. */
  descripcion: string;
  /** Nombre del icono que representa la acción. */
  icono: string;
  /** Ruta asociada a la tarjeta. */
  ruta: string;
  /** Etiqueta complementaria de la tarjeta. */
  badge: string;
}

@Component({
  selector: 'app-inicio-administrador',
  standalone: false,
  templateUrl: './inicio-administrador.html',
  styleUrl: './inicio-administrador.css',
})
/** Presenta el panel principal y accesos de administración. */
export class InicioAdministrador implements OnInit {

  constructor(
    private router: Router,
    private authAdminService: AuthAdminService
  ) {}

  /** Nombre del administrador que se muestra en el panel. */
  nombreAdmin: string = 'Administrador';
  /** Descripción del rol visible en la pantalla. */
  rol: string = 'Administrador General';

  /** Indicadores resumidos que se muestran en el panel. */
  metricas = [
    { titulo: 'Docentes Activos', valor: '48', icono: 'users' },
    { titulo: 'Asignaturas', valor: '32', icono: 'book' },
    { titulo: 'Aulas Asignadas', valor: '18', icono: 'building' },
    { titulo: 'Personas en la Base de Datos', valor: '10', icono: 'database' }
  ];

  /** Accesos rápidos a las secciones principales de administración. */
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
    },
    {
      titulo: 'Edición de Horarios Guardados',
      descripcion: 'Modifica, actualiza salones, cupos o elimina grupos ya almacenados en el sistema.',
      icono: 'edit',
      ruta: '/edicion-horarios',
      badge: 'Gestión BD'
    },
    {
      titulo: 'Gestión de Aulas',
      descripcion: 'Crea, edita o consulta el catálogo de asignaturas y materias curriculares.',
      icono: 'book',
      ruta: '/gestion-asignatura',
      badge: 'Asignaturas'
    },
    {
      titulo: 'Gestión de Salones',
      descripcion: 'Administra los bloques físicos, salones y capacidad de las aulas institucionales.',
      icono: 'building',
      ruta: '/gestion-aulas',
      badge: 'Infraestructura'
    }
  ];

  /** Verifica la sesión administrativa y carga el nombre del usuario. */
  ngOnInit(): void {
    if (!this.authAdminService.estaAutenticadoAdmin()) {
      this.router.navigate(['/loginn']);
      return;
    }

    const nombre = this.authAdminService.getNombreAdmin();
    if (nombre) {
      this.nombreAdmin = nombre;
    }
  }

  // Navegación directa para todas las tarjetas
  /** Navega a la ruta configurada en la tarjeta seleccionada. */
  ejecutarAccion(item: TarjetaAcceso): void {
    this.router.navigate([item.ruta]);
  }

  /** Navega a la ruta indicada. */
  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  /** Controla la visibilidad del menú de usuario. */
  mostrarMenuUsuario: boolean = false;

  /** Alterna el menú y evita propagar el clic al documento. */
  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario = !this.mostrarMenuUsuario;
  }

  /** Cierra la sesión administrativa y vuelve a la pantalla de acceso. */
  cerrarSesion(): void {
    this.mostrarMenuUsuario = false;
    this.authAdminService.cerrarSesionAdmin();
    this.router.navigate(['/loginn']);
  }

  @HostListener('document:click')
  /** Cierra el menú cuando se hace clic fuera de él. */
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario) {
      this.mostrarMenuUsuario = false;
    }
  }

}
