import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { SalonService } from '../services/salon.service';
import { SalonModel } from '../models/salon.model';
import { Bloque } from '../enums/bloque.enum';

@Component({
  selector: 'app-gestion-aulas',
  standalone: false,
  templateUrl: './gestion-aulas.html',
  styleUrls: ['./gestion-aulas.css']
})
export class GestionAulas implements OnInit {

  /** Salones cargados desde el backend. */
  registros: SalonModel[] = [];
  /** Salones que coinciden con el filtro actual. */
  registrosFiltrados: SalonModel[] = [];
  /** Texto usado para buscar salones. */
  filtroBusqueda: string = '';
  /** Indica si se están cargando los salones. */
  cargando: boolean = false;
  /** Indica si se está guardando un salón. */
  guardando: boolean = false;
  /** Mensaje de error asociado a la carga de salones. */
  error: string | null = null;
  /** Mensaje de error mostrado en el formulario. */
  errorModal: string | null = null;

  /** Valores de bloque disponibles para seleccionar. */
  bloques: Bloque[] = Object.values(Bloque);

  /** Cantidad de salones mostrados en cada página. */
  registrosPorPagina: number = 10;
  /** Página de resultados visible actualmente. */
  paginaActual: number = 1;

  /** Controla la visibilidad del modal de registro. */
  mostrarModalRegistro: boolean = false;
  /** Controla la visibilidad del modal de confirmación de eliminación. */
  mostrarModalConfirmacion: boolean = false;
  /** Salón seleccionado para ser eliminado. */
  salonAEliminar: SalonModel | null = null;
  /** Mensaje de advertencia específico al intentar eliminar un salón vinculado. */
  errorEliminacion: string | null = null;

  /** Indica si el formulario está editando un salón existente. */
  modoEdicion: boolean = false;
  /** Controla la apertura del menú de usuario. */
  mostrarMenu: boolean = false;

  /** Datos del salón que se está creando o editando. */
  nuevoRegistro: SalonModel = this.obtenerModeloVacio();

  constructor(
    private router: Router,
    private salonService: SalonService,
    private cdr: ChangeDetectorRef
  ) {}

  /** Carga los salones al inicializar el componente. */
  ngOnInit(): void {
    this.cargarSalones();
  }

  /** Construye un salón con valores iniciales para el formulario. */
  obtenerModeloVacio(): SalonModel {
    return {
      bloque: Bloque.A,
      numeroSalon: 101,
      capacidad: 30,
      tieneComputadores: false,
      tieneSillasMoviles: false
    };
  }

  /** Obtiene los salones registrados desde el backend. */
  cargarSalones(): void {
    this.cargando = true;
    this.error = null;
    this.cdr.markForCheck();

    this.salonService.mostrarTodo().subscribe({
      next: (response) => {
        this.registros = response.body ?? [];
        this.aplicarFiltro();
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error('Error al cargar salones:', err);
        this.error = 'No se pudieron cargar los registros de salones desde el servidor.';
        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }

  /** Actualiza el filtro de búsqueda y filtra la lista. */
  onFiltroChange(termino: string): void {
    this.filtroBusqueda = termino;
    this.aplicarFiltro();
  }

  /** Aplica el filtro de texto y reinicia la paginación. */
  aplicarFiltro(): void {
    if (!this.filtroBusqueda.trim()) {
      this.registrosFiltrados = this.registros;
    } else {
      const termino = this.filtroBusqueda.toLowerCase();
      this.registrosFiltrados = this.registros.filter(reg =>
        reg.bloque.toString().toLowerCase().includes(termino) ||
        reg.numeroSalon.toString().includes(termino) ||
        reg.capacidad.toString().includes(termino)
      );
    }
    this.paginaActual = 1;
  }

  /** Prepara y abre el modal de confirmación para eliminar un salón. */
  eliminarRegistro(reg: SalonModel): void {
    this.salonAEliminar = reg;
    this.errorEliminacion = null;
    this.mostrarModalConfirmacion = true;
    this.cdr.markForCheck();
  }

  /** Ejecuta la eliminación del salón tras confirmar en el modal personalizado. */
  aceptarEliminacion(): void {
    if (!this.salonAEliminar) return;

    const reg = this.salonAEliminar;
    const bloqueValor = typeof reg.bloque === 'object' ? (reg.bloque as any).toString() : reg.bloque;

    this.salonService.eliminarSalon(bloqueValor, reg.numeroSalon).subscribe({
      next: () => {
        this.cerrarModalConfirmacion();
        this.cargarSalones();
      },
      error: (err) => {
        console.error('Error al eliminar el salón:', err);
        const mensajeBackend = typeof err.error === 'string' ? err.error : (err.error?.message || '');

        // Verificamos si el error indica que está vinculado a un grupo
        if (mensajeBackend.toLowerCase().includes('grupo') || err.status === 400 || err.status === 409) {
          this.errorEliminacion = 'Este salón no se puede eliminar porque está vinculado a un grupo.';
        } else {
          this.errorEliminacion = 'No se pudo eliminar el salón. Inténtalo de nuevo.';
        }
        this.cdr.markForCheck();
      }
    });
  }

  /** Cierra el modal de confirmación sin eliminar. */
  cerrarModalConfirmacion(): void {
    this.mostrarModalConfirmacion = false;
    this.salonAEliminar = null;
    this.errorEliminacion = null;
    this.cdr.markForCheck();
  }

  /** Crea o actualiza el salón según el modo del formulario. */
  guardarRegistro(form: NgForm): void {
    if (form.invalid) return;

    this.guardando = true;
    this.errorModal = null;

    const peticion = this.modoEdicion
      ? this.salonService.actualizarSalon(this.nuevoRegistro)
      : this.salonService.crearSalon(this.nuevoRegistro);

    peticion.subscribe({
      next: () => {
        this.guardando = false;
        this.mostrarModalRegistro = false;
        this.cdr.markForCheck();
        this.cargarSalones();
      },
      error: (err) => {
        console.error('Error al guardar salón:', err);
        this.errorModal = 'Error al guardar: ' + (err.error?.message || err.error || err.message);
        this.guardando = false;
        this.cdr.markForCheck();
      }
    });
  }

  /** Prepara y abre el formulario para crear un salón. */
  crearNuevoRegistro(): void {
    this.modoEdicion = false;
    this.nuevoRegistro = this.obtenerModeloVacio();
    this.errorModal = null;
    this.mostrarModalRegistro = true;
    this.cdr.markForCheck();
  }

  /** Abre el formulario con los datos del salón seleccionado. */
  abrirModalEdicion(reg: SalonModel): void {
    this.modoEdicion = true;
    this.nuevoRegistro = { ...reg };
    this.errorModal = null;
    this.mostrarModalRegistro = true;
    this.cdr.markForCheck();
  }

  /** Oculta el formulario de creación o edición. */
  cerrarModalRegistro(): void {
    this.mostrarModalRegistro = false;
    this.cdr.markForCheck();
  }

  /** Devuelve el número máximo de salón permitido según el bloque seleccionado. */
  obtenerMaximoSalon(): number {
    if (this.nuevoRegistro.bloque === Bloque.A || this.nuevoRegistro.bloque === Bloque.F) {
      return 700;
    }
    if (this.nuevoRegistro.bloque === Bloque.B) {
      return 300;
    }
    return 999;
  }

  /** Valida que el número de salón no supere el límite del bloque actual. */
  validarLimiteSalon(): void {
    const maximo = this.obtenerMaximoSalon();
    if (this.nuevoRegistro.numeroSalon > maximo) {
      this.nuevoRegistro.numeroSalon = maximo;
      this.cdr.markForCheck();
    }
  }

  /** Devuelve los salones correspondientes a la página actual. */
  registrosPaginados(): SalonModel[] {
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

  /** Actualiza el tamaño de página y vuelve a la primera página. */
  onRegistrosPorPaginaChange(cantidad: number): void {
    this.registrosPorPagina = cantidad;
    this.paginaActual = 1;
  }

  /** Navega a la ruta especificada. */
  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  /** Alterna el menú de usuario y evita propagar el clic. */
  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenu = !this.mostrarMenu;
  }

  /** Indica si el menú de usuario está abierto. */
  mostrarMenuUsuario(): boolean {
    return this.mostrarMenu;
  }

  /** Cierra la sesión administrativa y vuelve al inicio de sesión. */
  cerrarSesion(): void {
    localStorage.removeItem('admin_token');
    this.router.navigate(['/login']);
  }
}
