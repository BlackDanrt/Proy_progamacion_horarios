import { Component, OnInit } from '@angular/core';
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
  registros: SalonModel[] = [];
  registrosFiltrados: SalonModel[] = [];
  filtroBusqueda: string = '';
  cargando: boolean = false;
  guardando: boolean = false;
  error: string | null = null;
  errorModal: string | null = null;

  // Opciones del Enum Bloque para los desplegables
  bloques: Bloque[] = Object.values(Bloque);

  // Control de paginación
  registrosPorPagina: number = 10;
  paginaActual: number = 1;

  // Control de modales y formulario
  mostrarModalRegistro: boolean = false;
  modoEdicion: boolean = false;
  mostrarMenu: boolean = false;

  nuevoRegistro: SalonModel = this.obtenerModeloVacio();

  constructor(
    private router: Router,
    private salonService: SalonService
  ) {}

  ngOnInit(): void {
    this.cargarSalones();
  }

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
  cargarSalones(): void {
    this.cargando = true;
    this.error = null;

    this.salonService.mostrarTodo().subscribe({
      next: (response) => {
        if (response.body) {
          this.registros = response.body;
          this.aplicarFiltro();
        }
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar salones:', err);
        this.error = 'No se pudieron cargar los registros de salones desde el servidor.';
        this.cargando = false;
      }
    });
  }

  /**
   * Filtra los salones en tiempo real por Bloque, Número de Salón o Capacidad.
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
  guardarRegistro(form: NgForm): void {
    if (form.invalid) return;

    this.guardando = true;
    this.errorModal = null;

    if (this.modoEdicion) {
      this.salonService.actualizarSalon(this.nuevoRegistro).subscribe({
        next: (respuesta) => {
          alert('Salón actualizado con éxito');
          this.guardando = false;
          this.cerrarModalRegistro();
          this.cargarSalones();
        },
        error: (err) => {
          console.error('Error al actualizar salón:', err);
          this.errorModal = 'Error al actualizar: ' + (err.error || err.message);
          this.guardando = false;
        }
      });
    } else {
      this.salonService.crearSalon(this.nuevoRegistro).subscribe({
        next: (respuesta) => {
          alert('Salón creado con éxito');
          this.guardando = false;
          this.cerrarModalRegistro();
          this.cargarSalones();
        },
        error: (err) => {
          console.error('Error al crear salón:', err);
          this.errorModal = 'Error al crear: ' + (err.error || err.message);
          this.guardando = false;
        }
      });
    }
  }

  // Modales y Edición
  crearNuevoRegistro(): void {
    this.modoEdicion = false;
    this.nuevoRegistro = this.obtenerModeloVacio();
    this.errorModal = null;
    this.mostrarModalRegistro = true;
  }

  abrirModalEdicion(reg: SalonModel): void {
    this.modoEdicion = true;
    this.nuevoRegistro = { ...reg };
    this.errorModal = null;
    this.mostrarModalRegistro = true;
  }

  cerrarModalRegistro(): void {
    this.mostrarModalRegistro = false;
  }

  // Control de paginación
  registrosPaginados(): SalonModel[] {
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

  // Navegación
  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenu = !this.mostrarMenu;
  }

  mostrarMenuUsuario(): boolean {
    return this.mostrarMenu;
  }

  cerrarSesion(): void {
    localStorage.removeItem('admin_token');
    this.router.navigate(['/login']);
  }
}
