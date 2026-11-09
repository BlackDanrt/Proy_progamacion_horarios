import { Component, HostListener, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, map } from 'rxjs/operators';
import { of } from 'rxjs';

import { AuditoriaService } from '../services/auditoria.service';

/** Representa una acción registrada por el sistema y mostrada en la tabla de auditoría. */
export interface RegistroAuditoria {
  id: number;
  idUsuario: number;
  nombreUsuario: string;
  accion: string;
  rol: 'Docente' | 'Administrador';
  servicioUsado: string;
  fecha: string;
}

@Component({
  selector: 'app-auditoria',
  standalone: false,
  templateUrl: './auditoria.html',
  styleUrl: './auditoria.css',
})
export class Auditoria implements OnInit {

  /** Servicios usados para consultar la auditoría y navegar entre vistas. */
  private auditoriaService = inject(AuditoriaService);
  private router = inject(Router);

  /** Estado de carga, mensajes y controles de la vista. */
  registros = signal<RegistroAuditoria[]>([]);
  cargando = signal<boolean>(false);
  error = signal<string>('');
  filtroBusqueda = signal<string>('');
  registrosPorPagina = signal<number>(10);
  paginaActual = signal<number>(1);
  mostrarMenuUsuario = signal<boolean>(false);

  /** Registros que coinciden con el término buscado en usuario, acción, servicio o rol. */
  registrosFiltrados = computed(() => {
    const todos = this.registros();
    const termino = this.filtroBusqueda().trim().toLowerCase();
    if (!termino) return todos;
    return todos.filter(reg =>
      reg.nombreUsuario.toLowerCase().includes(termino) ||
      reg.accion.toLowerCase().includes(termino) ||
      reg.servicioUsado.toLowerCase().includes(termino) ||
      reg.rol.toLowerCase().includes(termino)
    );
  });

  /** Cantidad de páginas necesarias para mostrar los resultados filtrados. */
  totalPaginas = computed(() =>
    Math.max(1, Math.ceil(this.registrosFiltrados().length / this.registrosPorPagina()))
  );

  /** Números de página disponibles para el control de paginación. */
  paginas = computed(() =>
    Array.from({ length: this.totalPaginas() }, (_, i) => i + 1)
  );

  /** Segmento de resultados correspondiente a la página actual. */
  registrosPaginados = computed(() => {
    const inicio = (this.paginaActual() - 1) * this.registrosPorPagina();
    return this.registrosFiltrados().slice(inicio, inicio + this.registrosPorPagina());
  });

  /** Carga los registros al iniciar la vista. */
  ngOnInit(): void {
    this.cargarRegistros();
  }

  /** Consulta la auditoría, adapta la respuesta del servicio y actualiza la tabla. */
  cargarRegistros(): void {
    this.cargando.set(true);
    this.error.set('');

    this.auditoriaService.mostrarTodo().pipe(
      map(resp => (resp.body ?? []) as any[]),
      catchError(err => {
        console.error('Error cargando auditoría', err);
        this.error.set('No se pudieron cargar los registros de auditoría.');
        return of([] as any[]);
      })
    ).subscribe({
      next: (lista) => {
        const mapeados: RegistroAuditoria[] = lista.map(a => ({
          id: a.id,
          idUsuario: a.idUsuario,
          nombreUsuario: a.nombreUsuario,
          accion: this.formatearEnum(a.accion),
          rol: a.esAdmin ? 'Administrador' : 'Docente',
          servicioUsado: this.formatearEnum(a.servicioUsado),
          fecha: this.formatearFecha(a.fecha)
        }));

        this.registros.set(mapeados);
        this.ajustarPagina();
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error inesperado', err);
        this.error.set('Ocurrió un error al cargar los registros.');
        this.cargando.set(false);
      }
    });
  }

  /** Convierte valores enumerados como `INICIO_SESION` en texto legible. */
  private formatearEnum(valor: string | null | undefined): string {
    if (!valor) return '—';
    return valor
      .toLowerCase()
      .split('_')
      .map(w => w.charAt(0).toUpperCase() + w.slice(1))
      .join(' ');
  }

  /** Formatea una fecha para mostrarla en la configuración regional de Colombia. */
  private formatearFecha(valor: string | null | undefined): string {
    if (!valor) return '—';
    const fecha = new Date(valor);
    if (isNaN(fecha.getTime())) return '—';
    return fecha.toLocaleString('es-CO', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }

  /** Evita que la página actual quede fuera de rango (tras filtrar o recargar). */
  private ajustarPagina(): void {
    if (this.paginaActual() > this.totalPaginas()) {
      this.paginaActual.set(this.totalPaginas());
    }
  }

  /** Actualiza la búsqueda y vuelve a la primera página para mostrar los resultados. */
  onFiltroChange(valor: string): void {
    this.filtroBusqueda.set(valor);
    this.paginaActual.set(1);
  }

  /** Cambia el tamaño de página y reinicia la paginación. */
  onRegistrosPorPaginaChange(valor: number): void {
    this.registrosPorPagina.set(Number(valor));
    this.paginaActual.set(1);
  }

  /** Cambia de página solo si el número solicitado está dentro del rango disponible. */
  cambiarPagina(p: number): void {
    if (p >= 1 && p <= this.totalPaginas()) {
      this.paginaActual.set(p);
    }
  }

  /** Navega a la ruta indicada por los controles de la vista. */
  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  /** Alterna el menú de usuario sin propagar el clic al documento. */
  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario.update(v => !v);
  }

  /** Oculta el menú de usuario y redirige al formulario de acceso. */
  cerrarSesion(): void {
    this.mostrarMenuUsuario.set(false);
    this.router.navigate(['/loginn']);
  }

  /** Cierra el menú de usuario cuando se hace clic fuera de él. */
  @HostListener('document:click')
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario()) {
      this.mostrarMenuUsuario.set(false);
    }
  }
}
