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
  registros: AsignaturaModel[] = [];
  registrosFiltrados: AsignaturaModel[] = [];
  filtroBusqueda: string = '';
  cargando: boolean = false;
  error: string | null = null;

  // Control de paginación
  registrosPorPagina: number = 10;
  paginaActual: number = 1;

  // Control de modales y menú
  mostrarModalRegistro: boolean = false;
  menuUsuarioAbierto: boolean = false;

  // Formulario de creación
  nuevoRegistro: AsignaturaModel = this.registroVacio();
  modoEdicion: boolean = false;
  guardando: boolean = false;
  errorModal: string | null = null;

  constructor(
    private router: Router,
    private asignaturaService: AsignaturaService,
    private cdr: ChangeDetectorRef
  ) {}

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
  onFiltroChange(termino: string): void {
    this.filtroBusqueda = termino;
    this.aplicarFiltro();
  }

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

  /**
   * Elimina una asignatura usando el endpoint del backend por nombre.
   */
  eliminarRegistro(reg: AsignaturaModel): void {
    if (confirm(`¿Estás seguro de eliminar la asignatura "${reg.nombre}"?`)) {
      this.asignaturaService.eliminarAsignatura(reg.nombre).subscribe({
        next: (mensaje) => {
          console.log(mensaje);
          alert('Asignatura eliminada con éxito');
          this.cargarAsignaturas(); // Recargar la tabla
        },
        error: (err) => {
          console.error('Error al eliminar:', err);
          alert('Error al eliminar la asignatura: ' + this.extraerMensaje(err));
        }
      });
    }
  }

  /**
   * Abre el modal en modo edición con una copia de la asignatura.
   * El PUT al back se hace al guardar el formulario.
   */
  actualizarRegistro(reg: AsignaturaModel): void {
    this.nuevoRegistro = { ...reg };
    this.modoEdicion = true;
    this.errorModal = null;
    this.mostrarModalRegistro = true;
  }

  // Métodos de navegación y control de UI
  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.menuUsuarioAbierto = !this.menuUsuarioAbierto;
  }

  mostrarMenuUsuario(): boolean {
    return this.menuUsuarioAbierto;
  }

  // Cierra el menú de usuario al hacer clic fuera
  @HostListener('document:click')
  cerrarMenuUsuario(): void {
    this.menuUsuarioAbierto = false;
  }

  cerrarSesion(): void {
    localStorage.removeItem('admin_token');
    this.router.navigate(['/login']);
  }

  visualizarHorario(reg: AsignaturaModel): void {
    console.log('Visualizar horario para:', reg.nombre);
  }

  // Control de paginación
  registrosPaginados(): AsignaturaModel[] {
    const inicio = (this.paginaActual - 1) * this.registrosPorPagina;
    return this.registrosFiltrados.slice(inicio, inicio + this.registrosPorPagina);
  }

  paginas(): number[] {
    const totalPaginas = Math.ceil(this.registrosFiltrados.length / this.registrosPorPagina);
    return Array.from({ length: totalPaginas }, (_, i) => i + 1);
  }

  cambiarPagina(pagina: number): void {
    const totalPaginas = Math.ceil(this.registrosFiltrados.length / this.registrosPorPagina);
    if (pagina >= 1 && pagina <= totalPaginas) {
      this.paginaActual = pagina;
    }
  }

  onRegistrosPorPaginaChange(cantidad: number): void {
    this.registrosPorPagina = cantidad;
    this.paginaActual = 1;
  }

  // Modal de creación
  crearNuevoRegistro(): void {
    this.abrirModalRegistro();
  }

  abrirModalRegistro(): void {
    this.nuevoRegistro = this.registroVacio();
    this.modoEdicion = false;
    this.errorModal = null;
    this.mostrarModalRegistro = true;
  }

  cerrarModalRegistro(): void {
    this.mostrarModalRegistro = false;
  }

  /**
   * Guarda el formulario: POST al back si es nueva, PUT si se está editando.
   */
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
  private extraerMensaje(err: any): string {
    try {
      const cuerpo = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
      return cuerpo?.message || err.message;
    } catch {
      return (typeof err.error === 'string' && err.error) ? err.error : err.message;
    }
  }
}
