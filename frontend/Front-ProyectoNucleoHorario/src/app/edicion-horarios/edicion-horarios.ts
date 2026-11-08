import { Component, HostListener, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SesionService } from '../services/sesion.service';
import { GrupoService } from '../services/grupo.service';
import { ProfesorService } from '../services/profesor.service';
import { SalonService } from '../services/salon.service';
import { ProfesorModel } from '../models/profesor.model';
import { SalonModel } from '../models/salon.model';

export type TipoToast = 'exito' | 'error' | 'advertencia' | 'info';

export interface Toast {
  id: number;
  tipo: TipoToast;
  mensaje: string;
}

/**
 * @description
 * Componente encargado de la interfaz de Edición y Gestión de Horarios Académicos.
 * Permite filtrar grupos, editarlos validando restricciones físicas de capacidad con tolerancia,
 * verificar disponibilidad de infraestructura y mostrar alertas visuales tanto flotantes como en el modal.
 */
@Component({
  selector: 'app-edicion-horarios',
  standalone: false,
  templateUrl: './edicion-horarios.html',
  styleUrls: ['./edicion-horarios.css']
})
export class EdicionHorarios implements OnInit {
  private grupoService = inject(GrupoService);
  private profesorService = inject(ProfesorService);
  private salonService = inject(SalonService);
  private router = inject(Router);
  private sesion = inject(SesionService);

  toasts: Toast[] = [];
  private toastSeq = 0;
  mostrarModalEditar: boolean = false;
  mostrarMenuUsuario: boolean = false;

  mensajeErrorCapacidad: string | null = null;

  confirmacion: { titulo: string; mensaje: string; textoAceptar: string } | null = null;
  private idGrupoAEliminar: number | null = null;

  listaProfesoresObj: ProfesorModel[] = [];
  listaProfesores: string[] = [];
  listaSalonesObj: SalonModel[] = [];
  listaBloques: string[] = [];
  salonesDisponibles: SalonModel[] = [];
  mapaSalones: { [key: string]: SalonModel[] } = {};

  periodoSeleccionado: string = `${new Date().getFullYear()}-1`;
  listaPeriodos: string[] = ['2026-1', '2026-2', 'Intersemestral 2026-1'];
  docenteFiltro: string = '';

  gruposGuardados: any[] = [];

  grupoEnEdicion: any = null;
  nuevaCapacidad: number = 30;
  bloqueSeleccionadoModal: string = '';
  salonSeleccionadoModalId: number | null = null;

  ngOnInit(): void {
    this.cargarCatalogos();
  }

  cargarCatalogos(): void {
    this.profesorService.mostrarTodo().subscribe({
      next: (res) => {
        if (res.body) {
          this.listaProfesoresObj = res.body;
          this.listaProfesores = res.body.map(p => `${p.nombre} ${p.apellido}`);
        }
      }
    });

    this.salonService.mostrarTodo().subscribe({
      next: (res) => {
        if (res.body) {
          this.listaSalonesObj = res.body;
          this.construirMapaSalones();
        }
      }
    });

    this.cargarGrupos();
  }

  construirMapaSalones(): void {
    this.mapaSalones = {};
    const bloquesSet = new Set<string>();

    this.listaSalonesObj.forEach(salon => {
      bloquesSet.add(salon.bloque);
      if (!this.mapaSalones[salon.bloque]) {
        this.mapaSalones[salon.bloque] = [];
      }
      this.mapaSalones[salon.bloque].push(salon);
    });
    this.listaBloques = Array.from(bloquesSet);
  }

  cargarGrupos(): void {
    this.grupoService.mostrarTodo().subscribe({
      next: (res: any) => {
        const todos = res?.body && res.status !== 204 ? res.body : [];
        this.gruposGuardados = todos.filter((g: any) => {
          const matchPeriodo = !this.periodoSeleccionado || g.periodo === this.periodoSeleccionado;
          const nombreProf = g.profesor ? `${g.profesor.nombre} ${g.profesor.apellido}` : '';
          const matchDocente = !this.docenteFiltro || nombreProf === this.docenteFiltro;
          return matchPeriodo && matchDocente;
        });
      },
      error: (err: any) => {
        console.error('Error al cargar grupos:', err);
        this.mostrarToast('error', 'No se pudieron cargar los grupos guardados.');
      }
    });
  }

  onFiltroChange(): void {
    this.cargarGrupos();
  }

  abrirModalEditar(grupo: any): void {
    this.grupoEnEdicion = grupo;
    this.nuevaCapacidad = grupo.capacidadMaxima || 30;
    this.mensajeErrorCapacidad = null;

    if (grupo.salon) {
      this.bloqueSeleccionadoModal = grupo.salon.bloque;
      this.salonSeleccionadoModalId = grupo.salon.id;
      this.onBloqueChange();
    } else {
      this.bloqueSeleccionadoModal = '';
      this.salonSeleccionadoModalId = null;
      this.salonesDisponibles = [];
    }
    this.mostrarModalEditar = true;
  }

  cerrarModalEditar(): void {
    this.mostrarModalEditar = false;
    this.grupoEnEdicion = null;
    this.mensajeErrorCapacidad = null;
  }

  onBloqueChange(): void {
    this.mensajeErrorCapacidad = null;
    if (this.bloqueSeleccionadoModal && this.mapaSalones[this.bloqueSeleccionadoModal]) {
      const salonesDelBloque = this.mapaSalones[this.bloqueSeleccionadoModal];

      const reqComputadores = Boolean(this.grupoEnEdicion?.asignatura?.requiereComputadores);
      const reqSillasMoviles = Boolean(this.grupoEnEdicion?.asignatura?.requiereSillasMoviles);

      this.salonesDisponibles = salonesDelBloque.filter((s: SalonModel) => {
        if (reqComputadores && !s.tieneComputadores) {
          return false;
        }
        if (reqSillasMoviles && !s.tieneSillasMoviles) {
          return false;
        }
        return true;
      });

      const salonActualValido = this.salonesDisponibles.some(s => s.id === Number(this.salonSeleccionadoModalId));
      if (!salonActualValido) {
        this.salonSeleccionadoModalId = null;
      }
    } else {
      this.salonesDisponibles = [];
      this.salonSeleccionadoModalId = null;
    }
  }

  guardarEdicionGrupo(): void {
    this.mensajeErrorCapacidad = null;

    if (!this.grupoEnEdicion) return;

    const salonObj = this.listaSalonesObj.find(s => s.id === Number(this.salonSeleccionadoModalId));

    if (!salonObj) {
      this.mostrarToast('advertencia', 'Por favor seleccione un salón válido.');
      return;
    }

    const capacidadConTolerancia = Math.min(Math.round(salonObj.capacidad * 1.10), 44);

    if (this.nuevaCapacidad > capacidadConTolerancia) {
      this.mensajeErrorCapacidad = `La capacidad del salón (${salonObj.bloque}${salonObj.numeroSalon}, máx. ${capacidadConTolerancia} con tolerancia) es más pequeña que la cantidad de estudiantes del grupo (${this.nuevaCapacidad}). Por favor seleccione otro salón.`;
      return;
    }

    const reqComputadores = this.grupoEnEdicion.asignatura?.requiereComputadores;
    const reqSillasMoviles = this.grupoEnEdicion.asignatura?.requiereSillasMoviles;

    if (reqComputadores && !salonObj.tieneComputadores) {
      this.mostrarToast('error', 'La asignatura exige equipos de cómputo y el salón seleccionado no cuenta con computadores.');
      return;
    }

    if (reqSillasMoviles && !salonObj.tieneSillasMoviles) {
      this.mostrarToast('error', 'La asignatura exige sillas móviles y el salón seleccionado no cuenta con ellas.');
      return;
    }

    const payload: any = {
      ...this.grupoEnEdicion,
      capacidadMaxima: this.nuevaCapacidad,
      salon: salonObj
    };

    this.grupoService.actualizarGrupo(payload).subscribe({
      next: () => {
        this.mostrarToast('exito', 'Grupo actualizado correctamente en la base de datos.');
        this.cerrarModalEditar();

        this.router.navigateByUrl('/', { skipLocationChange: true }).then(() => {
          this.router.navigate(['/edicion-horarios']);
        });
      },
      error: (err: any) => {
        console.error('Error al actualizar en el servidor:', err);
        const mensajeBackend = typeof err?.error === 'string' ? err.error : (err?.error?.message || 'No se pudo actualizar el grupo en el servidor.');
        this.mostrarToast('error', mensajeBackend);
      }
    });
  }

  solicitarEliminarGrupo(idGrupo: number): void {
    this.idGrupoAEliminar = idGrupo;
    this.confirmacion = {
      titulo: 'Confirmar eliminación',
      mensaje: '¿Está seguro de que desea eliminar este grupo del sistema? Esta acción liberará el horario y el salón.',
      textoAceptar: 'Sí, eliminar'
    };
  }

  aceptarConfirmacion(): void {
    if (this.idGrupoAEliminar !== null) {
      const id = this.idGrupoAEliminar;
      this.grupoService.eliminarGrupo(id).subscribe({
        next: () => {
          this.mostrarToast('exito', 'Grupo eliminado del sistema con éxito.');
          this.cargarGrupos();
        },
        error: (err: any) => {
          console.error('Error al eliminar:', err);
          this.mostrarToast('error', 'No se pudo eliminar el grupo.');
        }
      });
    }
    this.cancelarConfirmacion();
  }

  cancelarConfirmacion(): void {
    this.confirmacion = null;
    this.idGrupoAEliminar = null;
  }

  mostrarToast(tipo: TipoToast, mensaje: string): void {
    const id = ++this.toastSeq;
    this.toasts.push({ id, tipo, mensaje });
    setTimeout(() => this.cerrarToast(id), 5000);
  }

  cerrarToast(id: number): void {
    this.toasts = this.toasts.filter(t => t.id !== id);
  }

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario = !this.mostrarMenuUsuario;
  }

  cerrarSesion(): void {
    this.sesion.cerrarSesion();
    this.router.navigate(['/loginn']);
  }

  @HostListener('document:click')
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario) this.mostrarMenuUsuario = false;
  }
}
