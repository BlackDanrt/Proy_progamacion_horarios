import { Component, HostListener, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { ProfesorService } from '../services/profesor.service';             // ajusta la ruta
import { AdministradorService } from '../services/administrador.service';   // ajusta la ruta

/** Registro unificado de un docente o administrador mostrado en la tabla. */
export interface RegistroBaseDatos {
  /** Clave compuesta por rol e identificador para distinguir registros. */
  clave: string;                       // identificador único: rol + id
  /** Identificador asignado por el sistema. */
  id: number;
  /** Nombre completo de la persona. */
  nombre: string;
  /** Rol de la persona en el sistema. */
  rol: 'Docente' | 'Administrador';
  /** Especialidades del docente o valor indicativo para administradores. */
  especialidad: string;
  /** Resumen de la carga semanal asignada. */
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
  /** Copias de las entidades originales, usadas al actualizar registros. */
  private originales = new Map<string, any>();

  // Estado como signals
  /** Registros combinados de docentes y administradores. */
  registros = signal<RegistroBaseDatos[]>([]);
  /** Indica si se están cargando registros desde el backend. */
  cargando = signal<boolean>(false);
  /** Mensaje de carga o gestión que se debe mostrar en pantalla. */
  error = signal<string>('');
  /** Texto utilizado para filtrar los registros. */
  filtroBusqueda = signal<string>('');
  /** Cantidad de filas mostradas por página. */
  registrosPorPagina = signal<number>(10);
  /** Página visible actualmente. */
  paginaActual = signal<number>(1);
  /** Controla la visibilidad del modal para crear registros. */
  mostrarModalRegistro = signal<boolean>(false);
  /** Controla la apertura del menú de usuario. */
  mostrarMenuUsuario = signal<boolean>(false);

  // Estado del modal de edición
  /** Registro administrativo seleccionado para editar. */
  registroEditando = signal<RegistroBaseDatos | null>(null);
  /** Nombre editado en el modal. */
  editNombre = signal<string>('');
  /** Apellido editado en el modal. */
  editApellido = signal<string>('');
  /** Carga semanal editada para el docente. */
  editHoras = signal<string>('');
  /** Indica si se están guardando los cambios de edición. */
  guardando = signal<boolean>(false);

  // Derivados con computed
  /** Registros que coinciden con el texto de búsqueda. */
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

  /** Cantidad de páginas necesarias para mostrar los registros filtrados. */
  totalPaginas = computed(() =>
    Math.max(1, Math.ceil(this.registrosFiltrados().length / this.registrosPorPagina()))
  );

  /** Números de página disponibles. */
  paginas = computed(() =>
    Array.from({ length: this.totalPaginas() }, (_, i) => i + 1)
  );

  /** Registros filtrados que corresponden a la página visible. */
  registrosPaginados = computed(() => {
    const inicio = (this.paginaActual() - 1) * this.registrosPorPagina();
    return this.registrosFiltrados().slice(inicio, inicio + this.registrosPorPagina());
  });

  /** Carga los datos iniciales de docentes y administradores. */
  ngOnInit(): void {
    this.cargarRegistros();
  }

  /** Consulta y combina los docentes y administradores del backend. */
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

  /** Convierte la lista de especialidades a texto para mostrarla. */
  private textoEspecialidades(lista: any[] | null | undefined): string {
    if (!lista || lista.length === 0) return '—';
    return lista
      .map(e => typeof e === 'string' ? this.formatearEnum(e) : (e?.nombre ?? ''))
      .filter(t => t)
      .join(', ') || '—';
  }

  /** Presenta un valor de enum separado por guiones bajos como texto legible. */
  private formatearEnum(valor: string): string {
    return valor
      .toLowerCase()
      .split('_')
      .map(w => w.charAt(0).toUpperCase() + w.slice(1))
      .join(' ');
  }

  /** Evita que la página actual quede fuera de rango (tras filtrar, eliminar o recargar). */
  /** Ajusta la página actual para mantenerla dentro del rango disponible. */
  private ajustarPagina(): void {
    if (this.paginaActual() > this.totalPaginas()) {
      this.paginaActual.set(this.totalPaginas());
    }
  }

  /** Indica si el registro corresponde a un docente. */
  esDocente(reg: RegistroBaseDatos): boolean {
    return reg.rol === 'Docente';
  }

  /** Actualiza el filtro y vuelve a la primera página. */
  onFiltroChange(valor: string): void {
    this.filtroBusqueda.set(valor);
    this.paginaActual.set(1);
  }

  /** Cambia cuántos registros se muestran por página. */
  onRegistrosPorPaginaChange(valor: number): void {
    this.registrosPorPagina.set(Number(valor));
    this.paginaActual.set(1);
  }

  /** Navega a una página válida. */
  cambiarPagina(p: number): void {
    if (p >= 1 && p <= this.totalPaginas()) {
      this.paginaActual.set(p);
    }
  }

  /** Navega a la ruta indicada. */
  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  /** Abre el modal para seleccionar el tipo de nuevo registro. */
  crearNuevoRegistro(): void {
    this.mostrarModalRegistro.set(true);
  }

  /** Cierra el modal de creación. */
  cerrarModalRegistro(): void {
    this.mostrarModalRegistro.set(false);
  }

  /** Abre el horario del docente seleccionado. */
  visualizarHorario(reg: RegistroBaseDatos): void {
    if (!this.esDocente(reg)) return;
    this.router.navigate(['/consulta-horario'], { queryParams: { id: reg.id } });
  }

  // ───────────────────────── EDITAR ─────────────────────────

  /** Inicia la edición del registro seleccionado según su rol. */
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

  /** Descarta los cambios y cierra el modo de edición. */
  cancelarEdicion(): void {
    this.registroEditando.set(null);
  }

  /** Valida y guarda los cambios del docente o administrador seleccionado. */
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

  /** Elimina el registro seleccionado después de confirmar la acción. */
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

  /** Alterna el menú de usuario y evita propagar el clic. */
  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario.update(v => !v);
  }

  /** Cierra el menú y navega a la pantalla de inicio de sesión. */
  cerrarSesion(): void {
    this.mostrarMenuUsuario.set(false);
    this.router.navigate(['/loginn']);
  }

  @HostListener('document:click')
  /** Cierra el menú de usuario cuando se hace clic fuera de él. */
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario()) {
      this.mostrarMenuUsuario.set(false);
    }
  }
}
