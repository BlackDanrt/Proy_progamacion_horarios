import { Component, HostListener, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { ProfesorService } from '../services/profesor.service';             // ajusta la ruta
import { AdministradorService } from '../services/administrador.service';   // ajusta la ruta

export interface RegistroBaseDatos {
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

  // Estado como signals
  registros = signal<RegistroBaseDatos[]>([]);
  cargando = signal<boolean>(false);
  error = signal<string>('');
  filtroBusqueda = signal<string>('');
  registrosPorPagina = signal<number>(10);
  paginaActual = signal<number>(1);
  mostrarModalRegistro = signal<boolean>(false);
  mostrarMenuUsuario = signal<boolean>(false);

  // Derivados con computed
  registrosFiltrados = computed(() => {
    const todos = this.registros();
    const termino = this.filtroBusqueda().trim().toLowerCase();
    if (!termino) return todos;
    return todos.filter(reg =>
      reg.nombre.toLowerCase().includes(termino) ||
      reg.rol.toLowerCase().includes(termino) ||
      reg.especialidad.toLowerCase().includes(termino)
    );
  });

  totalPaginas = computed(() =>
    Math.max(1, Math.ceil(this.registrosFiltrados().length / this.registrosPorPagina()))
  );

  paginas = computed(() =>
    Array.from({ length: this.totalPaginas() }, (_, i) => i + 1)
  );

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

    const profesores$ = this.profesorService.mostrarTodo().pipe(
      map(resp => (resp.body ?? []) as any[]),
      catchError(err => {
        console.error('Error cargando profesores', err);
        this.error.update(e => e + 'No se pudieron cargar los profesores. ');
        return of([] as any[]);
      })
    );

    const administradores$ = this.administradorService.mostrarTodo().pipe(
      map(resp => (resp.body ?? []) as any[]),
      catchError(err => {
        console.error('Error cargando administradores', err);
        this.error.update(e => e + 'No se pudieron cargar los administradores.');
        return of([] as any[]);
      })
    );

    forkJoin({ profesores: profesores$, administradores: administradores$ }).subscribe({
      next: ({ profesores, administradores }) => {
        const docentes: RegistroBaseDatos[] = profesores.map(p => ({
          id: p.id,
          nombre: `${p.nombre} ${p.apellido}`,
          rol: 'Docente',
          especialidad: this.textoEspecialidades(p.especialidades),
          cargaAsignada: `${p.horasMaximasSemanales} horas/semana`
        }));

        const admins: RegistroBaseDatos[] = administradores.map(a => ({
          id: a.id,
          nombre: `${a.nombre} ${a.apellido}`,
          rol: 'Administrador',
          especialidad: '—',
          cargaAsignada: '—'
        }));

        this.registros.set([...docentes, ...admins]);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error inesperado', err);
        this.error.set('Ocurrió un error al cargar los registros.');
        this.cargando.set(false);
      }
    });
  }

  private textoEspecialidades(lista: any[] | null | undefined): string {
    if (!lista || lista.length === 0) return '—';
    return lista
      .map(e => typeof e === 'string' ? this.formatearEnum(e) : (e?.nombre ?? ''))
      .filter(t => t)
      .join(', ') || '—';
  }

  private formatearEnum(valor: string): string {
    return valor
      .toLowerCase()
      .split('_')
      .map(w => w.charAt(0).toUpperCase() + w.slice(1))
      .join(' ');
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

  visualizarHorario(idDocente: number): void {
    this.router.navigate(['/consulta-horario'], { queryParams: { id: idDocente } });
  }

  actualizarRegistro(id: number): void {
    console.log('Actualizar registro ID:', id);
  }

  eliminarRegistro(id: number): void {
    console.log('Eliminar registro ID:', id);
  }

  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario.update(v => !v);
  }

  cerrarSesion(): void {
    this.mostrarMenuUsuario.set(false);
    this.router.navigate(['/loginn']);
  }

  @HostListener('document:click')
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario()) {
      this.mostrarMenuUsuario.set(false);
    }
  }
}
