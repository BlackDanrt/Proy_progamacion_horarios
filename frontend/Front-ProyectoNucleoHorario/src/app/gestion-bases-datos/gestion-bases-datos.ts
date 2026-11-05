import { Component, HostListener, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { ProfesorService } from '../services/profesor.service';             // ajusta la ruta
import { AdministradorService } from '../services/administrador.service';   // ajusta la ruta

export interface RegistroBaseDatos {
  clave: string;                       // identificador único: rol + id
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

  // Objetos originales del back (para no perder campos al actualizar)
  private originales = new Map<string, any>();

  // Estado como signals
  registros = signal<RegistroBaseDatos[]>([]);
  cargando = signal<boolean>(false);
  error = signal<string>('');
  filtroBusqueda = signal<string>('');
  registrosPorPagina = signal<number>(10);
  paginaActual = signal<number>(1);
  mostrarModalRegistro = signal<boolean>(false);
  mostrarMenuUsuario = signal<boolean>(false);

  // Estado del modal de edición
  registroEditando = signal<RegistroBaseDatos | null>(null);
  editNombre = signal<string>('');
  editApellido = signal<string>('');
  editHoras = signal<string>('');
  guardando = signal<boolean>(false);

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

    const mensajes: string[] = [];

    const profesores$ = this.profesorService.mostrarTodo().pipe(
      map(resp => (resp.body ?? []) as any[]),
      catchError(err => {
        console.error('Error cargando profesores', err);
        mensajes.push('No se pudieron cargar los profesores.');
        return of([] as any[]);
      })
    );

    const administradores$ = this.administradorService.mostrarTodo().pipe(
      map(resp => (resp.body ?? []) as any[]),
      catchError(err => {
        console.error('Error cargando administradores', err);
        mensajes.push('No se pudieron cargar los administradores.');
        return of([] as any[]);
      })
    );

    forkJoin({ profesores: profesores$, administradores: administradores$ }).subscribe({
      next: ({ profesores, administradores }) => {
        const docentes: RegistroBaseDatos[] = profesores.map(p => ({
          clave: `Docente-${p.id}`,
          id: p.id,
          nombre: `${p.nombre} ${p.apellido}`,
          rol: 'Docente',
          especialidad: this.textoEspecialidades(p.especialidades),
          cargaAsignada: p.horasMaximasSemanales != null
            ? `${p.horasMaximasSemanales} horas/semana`
            : '—'
        }));

        const admins: RegistroBaseDatos[] = administradores.map(a => ({
          clave: `Administrador-${a.id}`,
          id: a.id,
          nombre: `${a.nombre} ${a.apellido}`,
          rol: 'Administrador',
          especialidad: '—',
          cargaAsignada: '—'
        }));

        // Guardamos los objetos originales para poder actualizarlos sin perder datos
        this.originales.clear();
        profesores.forEach(p => this.originales.set(`Docente-${p.id}`, p));
        administradores.forEach(a => this.originales.set(`Administrador-${a.id}`, a));

        this.registros.set([...docentes, ...admins]);
        this.error.set(mensajes.join(' '));
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

  // ───────────────────────── EDITAR ─────────────────────────

  actualizarRegistro(reg: RegistroBaseDatos): void {
    // Docente: se edita en la pantalla de registro (datos + malla horaria)
    if (reg.rol === 'Docente') {
      this.router.navigate(['/registro-profesor'], { queryParams: { id: reg.id } }); // ajusta la ruta
      return;
    }

    // Administrador: modal simple (nombre y apellido)
    const original = this.originales.get(reg.clave);
    if (!original) return;

    this.error.set('');
    this.editNombre.set(original.nombre ?? '');
    this.editApellido.set(original.apellido ?? '');
    this.editHoras.set(
      original.horasMaximasSemanales != null ? String(original.horasMaximasSemanales) : ''
    );
    this.registroEditando.set(reg);
  }

  cancelarEdicion(): void {
    this.registroEditando.set(null);
  }

  guardarEdicion(): void {
    const reg = this.registroEditando();
    if (!reg) return;

    const original = this.originales.get(reg.clave);
    if (!original) return;

    const nombre = this.editNombre().trim();
    const apellido = this.editApellido().trim();
    if (!nombre || !apellido) {
      this.error.set('El nombre y el apellido son obligatorios.');
      return;
    }

    this.guardando.set(true);
    this.error.set('');

    const onSuccess = () => {
      this.guardando.set(false);
      this.registroEditando.set(null);
      this.cargarRegistros(); // recarga la tabla con los datos del back
    };
    const onError = (err: any) => {
      console.error('Error actualizando registro', err);
      this.guardando.set(false);
      this.error.set(`No se pudo actualizar a ${reg.nombre}.`);
    };

    if (reg.rol === 'Docente') {
      const horas = this.editHoras().trim();
      const payload = {
        ...original,
        nombre,
        apellido,
        horasMaximasSemanales: horas !== '' ? Number(horas) : original.horasMaximasSemanales,
      };
      this.profesorService.actualizarInformacionProfesor(payload)
        .subscribe({ next: onSuccess, error: onError });
    } else {
      const payload = { ...original, nombre, apellido };
      this.administradorService.actualizarAdministrador(payload)
        .subscribe({ next: onSuccess, error: onError });
    }
  }

  // ───────────────────────── ELIMINAR ─────────────────────────

  eliminarRegistro(reg: RegistroBaseDatos): void {
    if (!confirm(`¿Seguro que deseas eliminar a ${reg.nombre}?`)) return;

    const onSuccess = () => {
      this.registros.update(lista => lista.filter(r => r.clave !== reg.clave));
      this.originales.delete(reg.clave);
      this.ajustarPagina();
    };
    const onError = (err: any) => {
      console.error('Error eliminando registro', err);
      this.error.set(`No se pudo eliminar a ${reg.nombre}.`);
    };

    const peticion$ = reg.rol === 'Docente'
      ? this.profesorService.eliminarProfesor(reg.id)
      : this.administradorService.eliminarAdministrador(reg.id);

    peticion$.subscribe({ next: onSuccess, error: onError });
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
