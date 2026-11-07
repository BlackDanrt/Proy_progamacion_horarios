import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { SalonService } from '../services/salon.service'; // Ajusta la ruta exacta a tu servicio
import { SalonModel } from '../models/salon.model';       // Ajusta la ruta a tu modelo
import { Bloque } from '../enums/bloque.enum';             // Ajusta la ruta a tu enum Bloque

@Component({
  selector: 'app-gestion-aulas',
  standalone: false,
  templateUrl: './gestion-aulas.html',
  styleUrls: ['./gestion-aulas.css']
})
export class GestionAulas implements OnInit {

  // Variables de estado
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

  // Opciones del Enum Bloque para los desplegables
  /** Valores de bloque disponibles para seleccionar. */
  bloques: Bloque[] = Object.values(Bloque);

  // Control de paginación
  /** Cantidad de salones mostrados en cada página. */
  registrosPorPagina: number = 10;
  /** Página de resultados visible actualmente. */
  paginaActual: number = 1;

  // Control de modales y formulario
  /** Controla la visibilidad del modal de registro. */
  mostrarModalRegistro: boolean = false;
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
      bloque: Bloque.A, // Valor por defecto del enum
      numeroSalon: 101,
      capacidad: 30,
      tieneComputadores: false,
      tieneSillasMoviles: false
    };
  }

  /**
   * Obtiene todos los salones desde el backend utilizando SalonService.
   */
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

  /**
   * Filtra los salones en tiempo real por Bloque, Número de Salón o Capacidad.
   */
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

  /**
   * Elimina un salón enviando bloque y numeroSalon al backend.
   */
  /** Elimina un salón después de pedir confirmación. */
  eliminarRegistro(reg: SalonModel): void {
    if (confirm(`¿Estás seguro de eliminar el salón ${reg.numeroSalon} del Bloque ${reg.bloque}?`)) {
      this.salonService.eliminarSalon(reg.bloque, reg.numeroSalon).subscribe({
        next: (mensaje) => {
          alert('Salón eliminado con éxito');
          this.cargarSalones();
        },
        error: (err) => {
          console.error('Error al eliminar el salón:', err);
          alert('Error al eliminar el salón: ' + (err.error || err.message));
        }
      });
    }
  }

  /**
   * Guarda o actualiza un salón según la bandera modoEdicion.
   */
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

  // Modales y Edición
  /** Prepara y abre el formulario para crear un salón. */
  crearNuevoRegistro(): void {
    this.modoEdicion = false;
    this.nuevoRegistro = this.obtenerModeloVacio();
    this.errorModal = null;
    this.mostrarModalRegistro = true;
  }

  /** Abre el formulario con los datos del salón seleccionado. */
  abrirModalEdicion(reg: SalonModel): void {
    this.modoEdicion = true;
    this.nuevoRegistro = { ...reg };
    this.errorModal = null;
    this.mostrarModalRegistro = true;
  }

  /** Oculta el formulario de creación o edición. */
  cerrarModalRegistro(): void {
    this.mostrarModalRegistro = false;
  }

  // Control de paginación
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

  // Navegación
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
