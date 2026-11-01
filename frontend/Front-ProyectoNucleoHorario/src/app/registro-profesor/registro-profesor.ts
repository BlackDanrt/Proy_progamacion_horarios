import { ChangeDetectorRef, Component, HostListener, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { ProfesorService } from '../services/profesor.service';
import { SesionService } from '../services/sesion.service';
import { Dia } from '../enums/dia.enum';
import { Escalafon } from '../enums/escalafon.enum';
import { Especialidad } from '../enums/especialidad.enum';
import { TipoVinculacion } from '../enums/tipoViculacion.enum';
import { FranjahorariaModel } from '../models/franjahoraria.model';
import { ProfesorModel } from '../models/profesor.model';

/** Fila de la grilla de disponibilidad (una hora). */
interface FilaHora {
  inicio: string;
  fin: string;
  esAlmuerzo: boolean;
}

/** Mensaje que se muestra al usuario. */
interface Mensaje {
  tipo: 'exito' | 'error';
  texto: string;
}

@Component({
  selector: 'app-registro-profesor',
  standalone: false,
  templateUrl: './registro-profesor.html',
  styleUrl: './registro-profesor.css',
})
export class RegistroProfesor implements OnInit {

  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly profesorService = inject(ProfesorService);
  private readonly sesion = inject(SesionService);

  /** true = el administrador registra un profesor; false = el profesor registra su disponibilidad. */
  esAdministrador: boolean = false;

  // ---- Datos del profesor (modo administrador) ----
  nombre: string = '';
  apellido: string = '';
  documento: string = '';
  correo: string = '';
  contrasenia: string = '';
  tipoVinculacion: TipoVinculacion = TipoVinculacion.TIEMPO_COMPLETO;
  escalafon: Escalafon = Escalafon.INSTRUCTOR_ASISTENTE;
  horasMaximasSemanales: number = 16;
  especialidadesSeleccionadas: Especialidad[] = [];

  // ---- Profesor con sesión (modo profesor) ----
  profesor: ProfesorModel | null = null;

  // ---- Listas de opciones ----
  tiposVinculacion: TipoVinculacion[] = Object.values(TipoVinculacion);
  escalafones: Escalafon[] = Object.values(Escalafon);
  especialidades: Especialidad[] = Object.values(Especialidad);

  /** Tope de horas semanales según la dedicación (Estatuto Docente, art. 8). */
  private readonly horasPorVinculacion: Record<TipoVinculacion, number> = {
    [TipoVinculacion.TIEMPO_COMPLETO]: 40,
    [TipoVinculacion.TRES_CUARTOS_DE_TIEMPO]: 30,
    [TipoVinculacion.MEDIO_TIEMPO]: 20,
    [TipoVinculacion.CUARTO_DE_TIEMPO]: 10,
  };

  // ---- Grilla de disponibilidad ----
  dias: { clave: Dia; nombre: string }[] = [
    { clave: Dia.LUNES, nombre: 'Lunes' },
    { clave: Dia.MARTES, nombre: 'Martes' },
    { clave: Dia.MIERCOLES, nombre: 'Miércoles' },
    { clave: Dia.JUEVES, nombre: 'Jueves' },
    { clave: Dia.VIERNES, nombre: 'Viernes' },
    { clave: Dia.SABADO, nombre: 'Sábado' },
  ];

  /** Filas de 1 hora de 7:00 a 22:00; la de 12:00 a 13:00 es el almuerzo. */
  filas: FilaHora[] = this.construirFilas();

  private seleccion: Set<string> = new Set<string>();

  // ---- Estado de pantalla ----
  mensaje: Mensaje | null = null;
  guardando: boolean = false;
  mostrarMenuUsuario: boolean = false;

  ngOnInit(): void {
    this.esAdministrador = this.sesion.esAdministrador();

    if (!this.esAdministrador) {
      this.cargarProfesorConSesion();
    }
  }

  // ------------------------------------------------------------------
  // Opciones y etiquetas
  // ------------------------------------------------------------------

  /** Convierte un valor de enum (TIEMPO_COMPLETO) en texto legible (Tiempo completo). */
  etiqueta(valor: string): string {
    const texto: string = valor.replace(/_/g, ' ').toLowerCase();
    return texto.charAt(0).toUpperCase() + texto.slice(1);
  }

  etiquetaVinculacion(tipo: TipoVinculacion): string {
    return `${this.etiqueta(tipo)} (hasta ${this.horasPorVinculacion[tipo]} h)`;
  }

  get maximoHoras(): number {
    return this.horasPorVinculacion[this.tipoVinculacion];
  }

  alCambiarVinculacion(): void {
    if (this.horasMaximasSemanales > this.maximoHoras) {
      this.horasMaximasSemanales = this.maximoHoras;
    }
  }

  toggleEspecialidad(especialidad: Especialidad): void {
    const indice: number = this.especialidadesSeleccionadas.indexOf(especialidad);
    if (indice >= 0) {
      this.especialidadesSeleccionadas.splice(indice, 1);
    } else {
      this.especialidadesSeleccionadas.push(especialidad);
    }
  }

  // ------------------------------------------------------------------
  // Grilla de disponibilidad
  // ------------------------------------------------------------------

  private construirFilas(): FilaHora[] {
    const filas: FilaHora[] = [];
    for (let hora = 7; hora <= 21; hora++) {
      filas.push({
        inicio: this.formatoHora(hora),
        fin: this.formatoHora(hora + 1),
        esAlmuerzo: hora === 12,
      });
    }
    return filas;
  }

  private formatoHora(hora: number): string {
    return `${hora.toString().padStart(2, '0')}:00`;
  }

  private clave(dia: Dia, fila: FilaHora): string {
    return `${dia}|${fila.inicio}`;
  }

  /** El almuerzo y, el sábado, todo lo posterior a la 1:00 p. m. no se pueden marcar. */
  estaDeshabilitada(dia: Dia, fila: FilaHora): boolean {
    return fila.esAlmuerzo || (dia === Dia.SABADO && fila.inicio >= '13:00');
  }

  estaSeleccionada(dia: Dia, fila: FilaHora): boolean {
    return this.seleccion.has(this.clave(dia, fila));
  }

  toggleCelda(dia: Dia, fila: FilaHora): void {
    if (this.estaDeshabilitada(dia, fila)) {
      return;
    }
    const clave: string = this.clave(dia, fila);
    if (this.seleccion.has(clave)) {
      this.seleccion.delete(clave);
    } else {
      this.seleccion.add(clave);
    }
  }

  /** Une las horas seguidas de cada día en rangos (por ejemplo 07:00 a 12:00). */
  private construirFranjas(): FranjahorariaModel[] {
    const franjas: FranjahorariaModel[] = [];

    for (const dia of this.dias) {
      const horas: FilaHora[] = this.filas.filter(f => this.estaSeleccionada(dia.clave, f));
      let inicio: string | null = null;
      let fin: string | null = null;

      for (const fila of horas) {
        if (inicio !== null && fin === fila.inicio) {
          fin = fila.fin;
        } else {
          if (inicio !== null && fin !== null) {
            franjas.push({ dia: dia.clave, horaInicio: inicio, horaFin: fin });
          }
          inicio = fila.inicio;
          fin = fila.fin;
        }
      }
      if (inicio !== null && fin !== null) {
        franjas.push({ dia: dia.clave, horaInicio: inicio, horaFin: fin });
      }
    }
    return franjas;
  }

  /** Marca en la grilla las horas que cubren las franjas ya guardadas. */
  private cargarSeleccion(franjas: FranjahorariaModel[]): void {
    this.seleccion.clear();
    for (const franja of franjas) {
      const inicio: string = franja.horaInicio.slice(0, 5);
      const fin: string = franja.horaFin.slice(0, 5);
      for (const fila of this.filas) {
        if (!fila.esAlmuerzo && fila.inicio >= inicio && fila.fin <= fin) {
          this.seleccion.add(this.clave(franja.dia, fila));
        }
      }
    }
  }

  // ------------------------------------------------------------------
  // Carga del profesor con sesión
  // ------------------------------------------------------------------

  private cargarProfesorConSesion(): void {
    const id: number | null = this.sesion.getProfesorId();

    this.profesorService.mostrarTodo().subscribe({
      next: (respuesta) => {
        this.profesor = (respuesta.body ?? []).find(p => p.id === id) ?? null;
        if (this.profesor) {
          this.cargarSeleccion(this.profesor.disponibilidadHoraria ?? []);
        }
        this.cdr.detectChanges();
      },
      error: (error: HttpErrorResponse) => this.mostrarError(error),
    });
  }

  // ------------------------------------------------------------------
  // Guardar
  // ------------------------------------------------------------------

  guardar(): void {
    if (this.guardando) {
      return;
    }
    this.mensaje = null;

    if (this.esAdministrador) {
      this.crearProfesor();
    } else {
      this.guardarDisponibilidadPropia();
    }
  }

  private crearProfesor(): void {
    if (!this.nombre.trim() || !this.apellido.trim() || !this.documento || !this.correo.trim()
      || !this.contrasenia) {
      this.mostrarMensaje('error', 'Completa nombre, apellido, documento, correo y contraseña.');
      return;
    }
    if (this.especialidadesSeleccionadas.length === 0) {
      this.mostrarMensaje('error', 'Selecciona al menos una especialidad.');
      return;
    }
    if (this.horasMaximasSemanales < 1 || this.horasMaximasSemanales > this.maximoHoras) {
      this.mostrarMensaje('error', `Las horas máximas deben estar entre 1 y ${this.maximoHoras}.`);
      return;
    }

    const profesor: ProfesorModel = {
      nombre: this.nombre.trim(),
      apellido: this.apellido.trim(),
      documento: Number(this.documento),
      correo: this.correo.trim(),
      contrasenia: this.contrasenia,
      tipoVinculacion: this.tipoVinculacion,
      escalafon: this.escalafon,
      especialidades: this.especialidadesSeleccionadas,
      horasMaximasSemanales: Number(this.horasMaximasSemanales),
      disponibilidadHoraria: this.construirFranjas(),
    };

    this.guardando = true;
    this.profesorService.crearProfesor(profesor).subscribe({
      next: (texto: string) => {
        this.guardando = false;
        this.mostrarMensaje('exito', texto);
        this.limpiarFormulario();
      },
      error: (error: HttpErrorResponse) => {
        this.guardando = false;
        this.mostrarError(error);
      },
    });
  }

  private guardarDisponibilidadPropia(): void {
    const id: number | null = this.sesion.getProfesorId();
    if (id === null) {
      this.mostrarMensaje('error', 'No se encontró tu sesión. Inicia sesión de nuevo.');
      return;
    }

    const franjas: FranjahorariaModel[] = this.construirFranjas();
    if (franjas.length === 0) {
      this.mostrarMensaje('error', 'Marca al menos una hora de disponibilidad.');
      return;
    }

    this.guardando = true;
    this.profesorService.guardarDisponibilidad(id, franjas).subscribe({
      next: (texto: string) => {
        this.guardando = false;
        this.mostrarMensaje('exito', texto);
      },
      error: (error: HttpErrorResponse) => {
        this.guardando = false;
        this.mostrarError(error);
      },
    });
  }

  private limpiarFormulario(): void {
    this.nombre = '';
    this.apellido = '';
    this.documento = '';
    this.correo = '';
    this.contrasenia = '';
    this.tipoVinculacion = TipoVinculacion.TIEMPO_COMPLETO;
    this.escalafon = Escalafon.INSTRUCTOR_ASISTENTE;
    this.horasMaximasSemanales = 16;
    this.especialidadesSeleccionadas = [];
    this.seleccion.clear();
    this.cdr.detectChanges();
  }

  // ------------------------------------------------------------------
  // Mensajes
  // ------------------------------------------------------------------

  private mostrarMensaje(tipo: 'exito' | 'error', texto: string): void {
    this.mensaje = { tipo, texto };
    this.cdr.detectChanges();
  }

  /** Muestra el motivo que devuelve el back (texto) o un mensaje según el código HTTP. */
  private mostrarError(error: HttpErrorResponse): void {
    if (error.status === 0) {
      this.mostrarMensaje('error', 'No se pudo conectar con el servidor.');
    } else if (error.status === 403) {
      this.mostrarMensaje('error', typeof error.error === 'string' && error.error
        ? error.error : 'No tienes permiso para realizar esta acción.');
    } else {
      this.mostrarMensaje('error', typeof error.error === 'string' && error.error
        ? error.error : `Ocurrió un error (código ${error.status}).`);
    }
  }

  // ------------------------------------------------------------------
  // Navegación y sesión
  // ------------------------------------------------------------------

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario = !this.mostrarMenuUsuario;
  }

  cerrarSesion(): void {
    this.mostrarMenuUsuario = false;
    this.sesion.cerrarSesion();
    this.router.navigate(['/loginn']);
  }

  @HostListener('document:click')
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario) {
      this.mostrarMenuUsuario = false;
    }
  }
}
