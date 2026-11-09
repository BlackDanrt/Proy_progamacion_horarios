import { ChangeDetectorRef, Component, HostListener, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AsignaturaModel } from '../models/asignatura.model';
import { AsignaturaService } from '../services/asignatura.service';

@Component({
  selector: 'app-gestion-asignatura',
  standalone: false,
  templateUrl: './gestion-asignatura.html',
  styleUrl: './gestion-asignatura.css',
})
export class GestionAsignatura implements OnInit {
  /** Asignaturas cargadas desde el backend. */
  registros: AsignaturaModel[] = [];
  /** Asignaturas que coinciden con el filtro de búsqueda. */
  registrosFiltrados: AsignaturaModel[] = [];
  /** Texto usado para filtrar las asignaturas. */
  filtroBusqueda: string = '';
  /** Indica si se están cargando asignaturas. */
  cargando: boolean = false;
  /** Mensaje de error asociado a la carga de registros. */
  error: string | null = null;

  // Control de paginación
  /** Cantidad de registros mostrados por página. */
  registrosPorPagina: number = 10;
  /** Número de la página visible actualmente. */
  paginaActual: number = 1;

  // Control de modales y menú
  /** Controla la visibilidad del modal de creación o edición. */
  mostrarModalRegistro: boolean = false;
  /** Controla la apertura del menú de usuario. */
  menuUsuarioAbierto: boolean = false;

  // Formulario de creación
  /** Datos del registro que se está creando o editando. */
  nuevoRegistro: AsignaturaModel = this.registroVacio();
  /** Indica si el modal está editando un registro existente. */
  modoEdicion: boolean = false;
  /** Indica si se está guardando el formulario. */
  guardando: boolean = false;
  /** Mensaje de error mostrado dentro del modal. */
  errorModal: string | null = null;

  // Modal de confirmación de eliminación
  /** Controla la visibilidad del modal de confirmación de eliminación. */
  mostrarModalEliminar: boolean = false;
  /** Asignatura pendiente de eliminar. */
  registroAEliminar: AsignaturaModel | null = null;
  /** Indica si se está eliminando. */
  eliminando: boolean = false;
  /** Mensaje de error del modal de eliminación. */
  errorEliminar: string | null = null;

  constructor(
    private router: Router,
    private asignaturaService: AsignaturaService,
    private cdr: ChangeDetectorRef
  ) {}

  /** Carga las asignaturas al inicializar el componente. */
  ngOnInit(): void {
    this.cargarAsignaturas();
  }

  /**
   * Obtiene todas las asignaturas desde el backend usando el servicio.
   */
  cargarAsignaturas(): void {
    this.cargando = true;
    this.error = null;
    this.cdr.markForCheck();

    this.asignaturaService.mostrarTodo().subscribe({
      next: (response) => {
        // Si el back responde sin cuerpo (ej. lista vacía), la tabla debe quedar vacía
        this.registros = response.body ?? [];
        this.aplicarFiltro();
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error al cargar asignaturas:', err);
        this.error = 'No se pudieron cargar los registros del servidor.';
        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }

  /**
   * Filtra las asignaturas según el término de búsqueda.
   */
  /** Filtra las asignaturas según el texto ingresado. */
  onFiltroChange(termino: string): void {
    this.filtroBusqueda = termino;
    this.aplicarFiltro();
  }

  /** Aplica el filtro actual y vuelve a la primera página. */
  aplicarFiltro(): void {
    if (!this.filtroBusqueda.trim()) {
      this.registrosFiltrados = this.registros;
    } else {
      const termino = this.filtroBusqueda.toLowerCase();
      this.registrosFiltrados = this.registros.filter(reg =>
        reg.nombre.toLowerCase().includes(termino) ||
        reg.numCreditos.toString().includes(termino) ||
        reg.frecuenciaSemanal.toString().includes(termino)
      );
    }
    this.paginaActual = 1; // Reiniciar a la primera página al buscar
  }

  /** Abre el modal de confirmación para eliminar una asignatura. */
  eliminarRegistro(reg: AsignaturaModel): void {
    this.registroAEliminar = reg;
    this.errorEliminar = null;
    this.mostrarModalEliminar = true;
  }

  /** Cierra el modal de confirmación sin eliminar. */
  cancelarEliminacion(): void {
    if (this.eliminando) return;
    this.mostrarModalEliminar = false;
    this.registroAEliminar = null;
    this.errorEliminar = null;
  }

  /** Elimina la asignatura seleccionada tras la confirmación. */
  confirmarEliminacion(): void {
    if (!this.registroAEliminar) return;

    this.eliminando = true;
    this.errorEliminar = null;

    this.asignaturaService.eliminarAsignatura(this.registroAEliminar.nombre).subscribe({
      next: (mensaje) => {
        console.log(mensaje);
        this.eliminando = false;
        this.mostrarModalEliminar = false;
        this.registroAEliminar = null;
        this.cdr.markForCheck();
        this.cargarAsignaturas();
      },
      error: (err) => {
        console.error('Error al eliminar:', err);
        this.errorEliminar = 'No se pudo eliminar la asignatura: ' + this.extraerMensaje(err);
        this.eliminando = false;
        this.cdr.markForCheck();
      }
    });
  }

  /**
   * Abre el modal en modo edición con una copia de la asignatura.
   * El PUT al back se hace al guardar el formulario.
   */
  /** Abre el formulario con los datos de una asignatura para editarla. */
  actualizarRegistro(reg: AsignaturaModel): void {
    this.nuevoRegistro = { ...reg };
    this.modoEdicion = true;
    this.errorModal = null;
    this.mostrarModalRegistro = true;
  }

  // Métodos de navegación y control de UI
  /** Navega a la ruta indicada. */
  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  /** Alterna el menú de usuario y detiene la propagación del clic. */
  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.menuUsuarioAbierto = !this.menuUsuarioAbierto;
  }

  /** Indica si el menú de usuario está abierto. */
  mostrarMenuUsuario(): boolean {
    return this.menuUsuarioAbierto;
  }

  // Cierra el menú de usuario al hacer clic fuera
  @HostListener('document:click')
  /** Cierra el menú de usuario ante un clic en el documento. */
  cerrarMenuUsuario(): void {
    this.menuUsuarioAbierto = false;
  }

  /** Cierra la sesión administrativa y vuelve al inicio de sesión. */
  cerrarSesion(): void {
    localStorage.removeItem('admin_token');
    this.router.navigate(['/login']);
  }

  /** Muestra el punto de entrada para consultar el horario de una asignatura. */
  visualizarHorario(reg: AsignaturaModel): void {
    console.log('Visualizar horario para:', reg.nombre);
  }

  // Control de paginación
  /** Obtiene los registros de la página actual. */
  registrosPaginados(): AsignaturaModel[] {
    const inicio = (this.paginaActual - 1) * this.registrosPorPagina;
    return this.registrosFiltrados.slice(inicio, inicio + this.registrosPorPagina);
  }

  /** Devuelve los números de página disponibles. */
  paginas(): number[] {
    const totalPaginas = Math.ceil(this.registrosFiltrados.length / this.registrosPorPagina);
    return Array.from({ length: totalPaginas }, (_, i) => i + 1);
  }

  /** Cambia a una página válida de resultados. */
  cambiarPagina(pagina: number): void {
    const totalPaginas = Math.ceil(this.registrosFiltrados.length / this.registrosPorPagina);
    if (pagina >= 1 && pagina <= totalPaginas) {
      this.paginaActual = pagina;
    }
  }

  /** Actualiza el tamaño de página y reinicia la paginación. */
  onRegistrosPorPaginaChange(cantidad: number): void {
    this.registrosPorPagina = cantidad;
    this.paginaActual = 1;
  }

  // Modal de creación
  /** Inicia el flujo de creación de una asignatura. */
  crearNuevoRegistro(): void {
    this.abrirModalRegistro();
  }

  /** Prepara y muestra el modal para crear una asignatura. */
  abrirModalRegistro(): void {
    this.nuevoRegistro = this.registroVacio();
    this.modoEdicion = false;
    this.errorModal = null;
    this.mostrarModalRegistro = true;
  }

  /** Oculta el modal de creación o edición. */
  cerrarModalRegistro(): void {
    this.mostrarModalRegistro = false;
  }

  /**
   * Guarda el formulario: POST al back si es nueva, PUT si se está editando.
   */
  /** Crea o actualiza la asignatura según el modo del formulario. */
  guardarRegistro(form: NgForm): void {
    if (form.invalid) {
      return;
    }

    this.guardando = true;
    this.errorModal = null;

    const peticion = this.modoEdicion
      ? this.asignaturaService.actualizarAsignatura(this.nuevoRegistro)
      : this.asignaturaService.crearAsignatura(this.nuevoRegistro);

    peticion.subscribe({
      next: (mensaje) => {
        console.log(mensaje);
        this.guardando = false;
        this.mostrarModalRegistro = false;
        this.cdr.markForCheck();
        this.cargarAsignaturas(); // Recargar desde el back para reflejar el cambio real
      },
      error: (err) => {
        console.error('Error al guardar:', err);
        this.errorModal = 'No se pudo guardar la asignatura: ' + this.extraerMensaje(err);
        this.guardando = false;
        this.cdr.markForCheck();
      }
    });
  }

  /** Crea un modelo de asignatura con valores iniciales. */
  private registroVacio(): AsignaturaModel {
    return {
      nombre: '',
      numCreditos: 1,
      frecuenciaSemanal: 1,
      requiereComputadores: false,
      requiereSillasMoviles: false
    };
  }

  /**
   * El back responde con un JSON de error; se extrae solo el mensaje legible.
   */
  /** Extrae un mensaje legible del error devuelto por el backend. */
  private extraerMensaje(err: any): string {
    try {
      const cuerpo = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
      return cuerpo?.message || err.message;
    } catch {
      return (typeof err.error === 'string' && err.error) ? err.error : err.message;
    }
  }
}
