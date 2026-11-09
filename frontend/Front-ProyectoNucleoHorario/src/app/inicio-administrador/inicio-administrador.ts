import { ChangeDetectorRef, Component, HostListener, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { AuthAdminService } from '../services/auth-admin';
import { ProfesorService } from '../services/profesor.service';
import { AdministradorService } from '../services/administrador.service';
import { AsignaturaService } from '../services/asignatura.service';
import { SalonService } from '../services/salon.service';
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
    private authAdminService: AuthAdminService,
    private profesorService: ProfesorService,
    private administradorService: AdministradorService,
    private asignaturaService: AsignaturaService,
    private salonService: SalonService,
    private cdr: ChangeDetectorRef
  ) {}

  /** Nombre del administrador que se muestra en el panel. */
  nombreAdmin: string = 'Administrador';
  /** Descripción del rol visible en la pantalla. */
  rol: string = 'Administrador General';

  /** Indicadores resumidos que se muestran en el panel. */
  metricas = [
    { titulo: 'Docentes Activos', valor: '...', icono: 'users' },
    { titulo: 'Asignaturas', valor: '...', icono: 'book' },
    { titulo: 'Aulas Asignadas', valor: '...', icono: 'building' },
    { titulo: 'Personas en la Base de Datos', valor: '...', icono: 'database' }
  ];

  /** Accesos rápidos a las secciones principales de administración. */
  accesosDirectos: TarjetaAcceso[] = [
    {
      titulo: 'Gestión de Bases de Datos',
      descripcion: 'Administra docentes, roles, especialidades y cargas académicas del sistema.',
      icono: 'database',
      ruta: '/gestion-bases-datos',
      badge: 'Registros'
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
    },
    {
      titulo: 'Parámetros del Sistema',
      descripcion: 'Configura las jornadas, horarios y reglas generales del sistema.',
      icono: 'settings',
      ruta: '/parametros-sistema',
      badge: 'Configuración'
    },
    {
      titulo: 'Auditoría',
      descripcion: 'Consulta el registro de acciones realizadas en el sistema.',
      icono: 'audit',
      ruta: '/auditoria',
      badge: 'Actividad'
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
    this.cargarMetricas();
  }

  /** Consulta los totales reales al backend y actualiza las tarjetas. */
  private cargarMetricas(): void {
    const contar = (obs: any) =>
      obs.pipe(
        map((resp: any) => (resp.body ?? []) as any[]),
        catchError(err => {
          console.error('Error cargando métricas', err);
          return of(null);
        })
      );

    forkJoin({
      profesores: contar(this.profesorService.mostrarTodo()),
      administradores: contar(this.administradorService.mostrarTodo()),
      asignaturas: contar(this.asignaturaService.mostrarTodo()),
      salones: contar(this.salonService.mostrarTodo())
    }).subscribe((r: any) => {
      const total = (lista: any[] | null) => lista ? String(lista.length) : '—';

      const personas = (r.profesores && r.administradores)
        ? String(r.profesores.length + r.administradores.length)
        : '—';

      this.metricas = [
        { titulo: 'Docentes Activos', valor: total(r.profesores), icono: 'users' },
        { titulo: 'Asignaturas', valor: total(r.asignaturas), icono: 'book' },
        { titulo: 'Aulas Asignadas', valor: total(r.salones), icono: 'building' },
        { titulo: 'Personas en la Base de Datos', valor: personas, icono: 'database' }
      ];
      this.cdr.markForCheck();
    });
  }

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
