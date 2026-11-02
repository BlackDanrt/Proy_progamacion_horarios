import { Component, HostListener, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { ProfesorService } from '../services/profesor.service'; // ajusta la ruta
import { AdministradorService } from '../services/administrador.service'; // ajusta la ruta
import { SesionService } from '../services/sesion.service';

export interface RegistroBaseDatos {
  clave: string; // identificador único: rol + id
  id: number;
  nombre: string;
  rol: 'Docente' | 'Administrador';
  especialidad: string;
  cargaAsignada: string;
}

@Component({
  selector: 'app-gestion-bases-datos',
  standalone: false,
  templateUrl: './gestion-bases-datos.html',
  styleUrl: './gestion-bases-datos.css',
})
export class GestionBasesDatos implements OnInit {
  private profesorService = inject(ProfesorService);
  private administradorService = inject(AdministradorService);
  private router = inject(Router);
  private sesion = inject(SesionService);

  // Estado como signals
  registros = signal<RegistroBaseDatos[]>([]);
  cargando = signal<boolean>(false);
  error = signal<string>('');
  filtroBusqueda = signal<string>('');
  registrosPorPagina = signal<number>(10);
  paginaActual = signal<number>(1);
  mostrarModalRegistro = signal<boolean>(false);
  mostrarMenuUsuario = signal<boolean>(false);
  mensaje = signal<{ tipo: 'exito' | 'error'; texto: string } | null>(null);

  // Derivados con computed
  registrosFiltrados = computed(() => {
    const todos = this.registros();
    const termino = this.filtroBusqueda().trim().toLowerCase();
    if (!termino) return todos;
    return todos.filter(
      (reg) =>
        reg.nombre.toLowerCase().includes(termino) ||
        reg.rol.toLowerCase().includes(termino) ||
        reg.especialidad.toLowerCase().includes(termino),
    );
  });

  totalPaginas = computed(() =>
    Math.max(1, Math.ceil(this.registrosFiltrados().length / this.registrosPorPagina())),
  );

  paginas = computed(() => Array.from({ length: this.totalPaginas() }, (_, i) => i + 1));

  registrosPaginados = computed(() => {
    const inicio = (this.paginaActual() - 1) * this.registrosPorPagina();
    return this.registrosFiltrados().slice(inicio, inicio + this.registrosPorPagina());
  });

  ngOnInit(): void {
    this.cargarRegistros();
  }

  cargarRegistros(): void {
    this.cargando.set(true);
    this.error.set('');

    const mensajes: string[] = [];

    const profesores$ = this.profesorService.mostrarTodo().pipe(
      map((resp) => (resp.body ?? []) as any[]),
      catchError((err) => {
        console.error('Error cargando profesores', err);
        mensajes.push('No se pudieron cargar los profesores.');
        return of([] as any[]);
      }),
    );

    const administradores$ = this.administradorService.mostrarTodo().pipe(
      map((resp) => (resp.body ?? []) as any[]),
      catchError((err) => {
        console.error('Error cargando administradores', err);
        mensajes.push('No se pudieron cargar los administradores.');
        return of([] as any[]);
      }),
    );

    forkJoin({ profesores: profesores$, administradores: administradores$ }).subscribe({
      next: ({ profesores, administradores }) => {
        const docentes: RegistroBaseDatos[] = profesores.map((p) => ({
          clave: `Docente-${p.id}`,
          id: p.id,
          nombre: `${p.nombre} ${p.apellido}`,
          rol: 'Docente',
          especialidad: this.textoEspecialidades(p.especialidades),
          cargaAsignada:
            p.horasMaximasSemanales != null ? `${p.horasMaximasSemanales} horas/semana` : '—',
        }));

        const admins: RegistroBaseDatos[] = administradores.map((a) => ({
          clave: `Administrador-${a.id}`,
          id: a.id,
          nombre: `${a.nombre} ${a.apellido}`,
          rol: 'Administrador',
          especialidad: '—',
          cargaAsignada: '—',
        }));

        this.registros.set([...docentes, ...admins]);
        this.error.set(mensajes.join(' '));
        this.ajustarPagina();
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error inesperado', err);
        this.error.set('Ocurrió un error al cargar los registros.');
        this.cargando.set(false);
      },
    });
  }

  private textoEspecialidades(lista: any[] | null | undefined): string {
    if (!lista || lista.length === 0) return '—';
    return (
      lista
        .map((e) => (typeof e === 'string' ? this.formatearEnum(e) : (e?.nombre ?? '')))
        .filter((t) => t)
        .join(', ') || '—'
    );
  }

  private formatearEnum(valor: string): string {
    return valor
      .toLowerCase()
      .split('_')
      .map((w) => w.charAt(0).toUpperCase() + w.slice(1))
      .join(' ');
  }

  /** Evita que la página actual quede fuera de rango (tras filtrar, eliminar o recargar). */
  private ajustarPagina(): void {
    if (this.paginaActual() > this.totalPaginas()) {
      this.paginaActual.set(this.totalPaginas());
    }
  }

  esDocente(reg: RegistroBaseDatos): boolean {
    return reg.rol === 'Docente';
  }

  onFiltroChange(valor: string): void {
    this.filtroBusqueda.set(valor);
    this.paginaActual.set(1);
  }

  onRegistrosPorPaginaChange(valor: number): void {
    this.registrosPorPagina.set(Number(valor));
    this.paginaActual.set(1);
  }

  cambiarPagina(p: number): void {
    if (p >= 1 && p <= this.totalPaginas()) {
      this.paginaActual.set(p);
    }
  }

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  crearNuevoRegistro(): void {
    this.mostrarModalRegistro.set(true);
  }

  cerrarModalRegistro(): void {
    this.mostrarModalRegistro.set(false);
  }

  visualizarHorario(reg: RegistroBaseDatos): void {
    if (!this.esDocente(reg)) return;
    this.router.navigate(['/consulta-horario'], { queryParams: { id: reg.id } });
  }

  actualizarRegistro(reg: RegistroBaseDatos): void {
    this.mensaje.set({ tipo: 'error', texto: 'La edición de personas se agregará pronto.' });
  }

  eliminarRegistro(reg: RegistroBaseDatos): void {
    if (reg.rol === 'Administrador' && reg.id === Number(localStorage.getItem('admin_id'))) {
      this.mensaje.set({ tipo: 'error', texto: 'No puedes eliminar tu propia cuenta.' });
      return;
    }
    if (!confirm(`¿Eliminar a ${reg.nombre}? Esta acción no se puede deshacer.`)) {
      return;
    }

    const peticion =
      reg.rol === 'Docente'
        ? this.profesorService.eliminarProfesor(reg.id)
        : this.administradorService.eliminarAdministrador(reg.id);

    peticion.subscribe({
      next: (texto: string) => {
        this.registros.update((lista) => lista.filter((r) => r.clave !== reg.clave));
        this.ajustarPagina();
        this.mensaje.set({ tipo: 'exito', texto });
      },
      error: (err: HttpErrorResponse) => {
        this.mensaje.set({
          tipo: 'error',
          texto:
            typeof err.error === 'string' && err.error
              ? err.error
              : `No se pudo eliminar (código ${err.status}).`,
        });
      },
    });
  }

  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario.update((v) => !v);
  }

  cerrarSesion(): void {
    this.mostrarMenuUsuario.set(false);
    this.sesion.cerrarSesion();
    this.router.navigate(['/loginn']);
  }

  @HostListener('document:click')
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario()) {
      this.mostrarMenuUsuario.set(false);
    }
  }
}
