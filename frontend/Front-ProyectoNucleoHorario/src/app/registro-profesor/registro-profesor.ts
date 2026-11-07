import { ChangeDetectorRef, Component, HostListener, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { ProfesorService } from '../services/profesor.service';
import { SesionService } from '../services/sesion.service';
import { Dia } from '../enums/dia.enum';
import { Escalafon } from '../enums/escalafon.enum';
import { Especialidad } from '../enums/especialidad.enum';
import { TipoVinculacion } from '../enums/tipoViculacion.enum';
import { FranjahorariaModel } from '../models/franjahoraria.model';
import { ProfesorModel } from '../models/profesor.model';
import { reglaDe } from '../utils/reglas-contrato';

/** Ventana principal del administrador, a donde se vuelve tras crear un profesor. */
const RUTA_PRINCIPAL = '/inicio-administrador';

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
  private readonly route = inject(ActivatedRoute);
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
  horasMaximasSemanales: number = 40;
  especialidadesSeleccionadas: Especialidad[] = [];

  // ---- Modo edición (el administrador edita un profesor existente) ----
  /** true cuando se llega con ?id=<idProfesor> (desde Gestión de bases de datos). */
  modoEdicion: boolean = false;
  private idEdicion: number | null = null;
  /** Objeto original del back: se usa como base para no perder campos al guardar. */
  private profesorOriginal: ProfesorModel | null = null;

  // ---- Profesor con sesión (modo profesor) ----
  profesor: ProfesorModel | null = null;

  // ---- Listas de opciones ----
  tiposVinculacion: TipoVinculacion[] = Object.values(TipoVinculacion);
  escalafones: Escalafon[] = Object.values(Escalafon);
  especialidades: Especialidad[] = Object.values(Especialidad);

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

  /** Duración de una clase en horas. */
  private readonly HORAS_POR_CLASE = 2;

  // ---- Estado de pantalla ----
  mensaje: Mensaje | null = null;
  /** Aviso que se muestra junto a la grilla cuando un clic no es válido. */
  avisoGrilla: string | null = null;
  guardando: boolean = false;
  /** Mensaje de éxito que se muestra en una ventana centrada tras crear un profesor. */
  exitoCreacion: string | null = null;
  private temporizadorExito?: ReturnType<typeof setTimeout>;
  mostrarMenuUsuario: boolean = false;

  ngOnInit(): void {
    this.esAdministrador = this.sesion.esAdministrador();

    if (!this.esAdministrador) {
      this.cargarProfesorConSesion();
      return;
    }

    // Administrador: si viene ?id=..., se edita ese profesor
    const idParam = this.route.snapshot.queryParamMap.get('id');
    if (idParam !== null && !isNaN(Number(idParam))) {
      this.modoEdicion = true;
      this.idEdicion = Number(idParam);
      this.cargarProfesorParaEdicion(this.idEdicion);
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
    const r = reglaDe(tipo);
    return `${this.etiqueta(tipo)} (${r.minHoras} a ${r.maxHoras} h)`;
  }

  /** Las horas de clase salen del contrato: el administrador no las digita. */
  get minimoHoras(): number {
    return reglaDe(this.esAdministrador ? this.tipoVinculacion : this.profesor?.tipoVinculacion)
      .minHoras;
  }

  get maximoHoras(): number {
    return reglaDe(this.esAdministrador ? this.tipoVinculacion : this.profesor?.tipoVinculacion)
      .maxHoras;
  }

  alCambiarVinculacion(): void {
    this.horasMaximasSemanales = this.maximoHoras;
    // Si ya había marcado más horas de las que permite el nuevo contrato, se avisa
    if (this.horasSeleccionadas > this.maximoHoras) {
      this.avisoGrilla = `Con este tipo de vinculación el máximo es ${this.maximoHoras} h y tienes ${this.horasSeleccionadas} h marcadas. Quita algunos bloques.`;
    }
  }

  /** Valida las horas marcadas contra el contrato. `exigirMinimo` = false permite dejar la malla vacía (el docente la llena en su primer ingreso). */
  private validarHorasContrato(exigirMinimo: boolean): string | null {
    const h = this.horasSeleccionadas;
    if (h > this.maximoHoras) {
      return `Marcaste ${h} h y el máximo para este contrato es ${this.maximoHoras} h.`;
    }
    if (h < this.minimoHoras && (exigirMinimo || h > 0)) {
      return `Marcaste ${h} h y el mínimo para este contrato es ${this.minimoHoras} h.`;
    }
    return null;
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

  /** Tope de horas semanales: el del formulario (admin) o el del profesor con sesión. */
  get limiteHoras(): number {
    return this.maximoHoras;
  }

  get horasSeleccionadas(): number {
    return this.seleccion.size;
  }

  toggleCelda(dia: Dia, fila: FilaHora): void {
    if (this.estaDeshabilitada(dia, fila)) {
      return;
    }
    this.avisoGrilla = null;

    // Clic sobre un bloque ya marcado: se desmarca completo.
    if (this.estaSeleccionada(dia, fila)) {
      this.quitarBloque(dia, fila);
      return;
    }

    const problema: string | null = this.validarNuevoBloque(dia, fila);
    if (problema) {
      this.avisoGrilla = problema;
      return;
    }

    const i: number = this.filas.indexOf(fila);
    this.seleccion.add(this.clave(dia, this.filas[i]));
    this.seleccion.add(this.clave(dia, this.filas[i + 1]));
  }

  /** Devuelve el motivo por el que no se puede marcar el bloque, o null si es válido. */
  private validarNuevoBloque(dia: Dia, fila: FilaHora): string | null {
    if (!this.esAdministrador && !this.profesor) {
      return 'Espera a que cargue tu información.';
    }

    const i: number = this.filas.indexOf(fila);
    const siguiente: FilaHora | undefined = this.filas[i + 1];

    if (!siguiente) {
      return 'Las clases duran 2 horas y no alcanzan a terminar antes de las 10:00 p. m.';
    }
    if (this.estaDeshabilitada(dia, siguiente)) {
      return 'Una clase de 2 horas desde esta hora chocaría con el almuerzo o con el cierre del sábado.';
    }

    const marcada = (idx: number): boolean =>
      idx >= 0 && idx < this.filas.length && this.seleccion.has(this.clave(dia, this.filas[idx]));

    // Bloque nuevo = filas i e i+1. No puede tocar otro bloque (ni antes ni después).
    if (marcada(i - 1) || marcada(i + 1) || marcada(i + 2)) {
      return 'No puedes tener clases seguidas: deja al menos 1 hora libre entre bloques.';
    }

    if (this.horasSeleccionadas + this.HORAS_POR_CLASE > this.limiteHoras) {
      return `No puedes superar tus horas máximas por semana (${this.limiteHoras} h).`;
    }
    return null;
  }

  /** Quita de la grilla el bloque (horas seguidas) al que pertenece la celda. */
  private quitarBloque(dia: Dia, fila: FilaHora): void {
    const marcada = (idx: number): boolean =>
      idx >= 0 && idx < this.filas.length && this.seleccion.has(this.clave(dia, this.filas[idx]));

    const i: number = this.filas.indexOf(fila);
    let desde: number = i;
    let hasta: number = i;
    while (marcada(desde - 1)) desde--;
    while (marcada(hasta + 1)) hasta++;

    for (let k = desde; k <= hasta; k++) {
      this.seleccion.delete(this.clave(dia, this.filas[k]));
    }
  }

  /** Une las horas seguidas de cada día en rangos (por ejemplo 07:00 a 12:00). */
  private construirFranjas(): FranjahorariaModel[] {
    const franjas: FranjahorariaModel[] = [];

    for (const dia of this.dias) {
      const horas: FilaHora[] = this.filas.filter((f) => this.estaSeleccionada(dia.clave, f));
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
        this.profesor = (respuesta.body ?? []).find((p) => p.id === id) ?? null;
        if (this.profesor) {
          this.cargarSeleccion(this.profesor.disponibilidadHoraria ?? []);
        }
        this.cdr.detectChanges();
      },
      error: (error: HttpErrorResponse) => this.mostrarError(error),
    });
  }

  /** Carga los datos ya registrados del profesor en el formulario y su malla en la grilla. */
  private cargarProfesorParaEdicion(id: number): void {
    this.profesorService.mostrarTodo().subscribe({
      next: (respuesta) => {
        const encontrado = (respuesta.body ?? []).find((p) => p.id === id) ?? null;
        if (!encontrado) {
          this.mostrarMensaje('error', 'No se encontró el profesor que intentas editar.');
          return;
        }

        this.profesorOriginal = encontrado;
        this.nombre = encontrado.nombre ?? '';
        this.apellido = encontrado.apellido ?? '';
        this.documento = encontrado.documento != null ? String(encontrado.documento) : '';
        this.correo = encontrado.correo ?? '';
        this.contrasenia = ''; // vacía = no se cambia
        this.tipoVinculacion = encontrado.tipoVinculacion;
        this.escalafon = encontrado.escalafon;
        this.horasMaximasSemanales = this.maximoHoras;
        this.especialidadesSeleccionadas = [...(encontrado.especialidades ?? [])];
        this.cargarSeleccion(encontrado.disponibilidadHoraria ?? []);
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

    if (this.esAdministrador && this.modoEdicion) {
      this.actualizarProfesorExistente();
    } else if (this.esAdministrador) {
      this.crearProfesor();
    } else {
      this.guardarDisponibilidadPropia();
    }
  }

  private crearProfesor(): void {
    if (
      !this.nombre.trim() ||
      !this.apellido.trim() ||
      !this.documento ||
      !this.correo.trim() ||
      !this.contrasenia
    ) {
      this.mostrarMensaje('error', 'Completa nombre, apellido, documento, correo y contraseña.');
      return;
    }
    if (this.especialidadesSeleccionadas.length === 0) {
      this.mostrarMensaje('error', 'Selecciona al menos una especialidad.');
      return;
    }
    const errorHoras = this.validarHorasContrato(false);
    if (errorHoras) {
      this.mostrarMensaje('error', errorHoras);
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
      horasMaximasSemanales: this.maximoHoras,
      disponibilidadHoraria: this.construirFranjas(),
    };

    this.guardando = true;
    this.profesorService.crearProfesor(profesor).subscribe({
      next: (texto: string) => {
        this.guardando = false;
        this.limpiarFormulario();
        this.mostrarExitoCreacion(texto);
      },
      error: (error: HttpErrorResponse) => {
        this.guardando = false;
        this.mostrarError(error);
      },
    });
  }

  /** Edición por el administrador: actualiza los datos y luego reemplaza la malla horaria. */
  private actualizarProfesorExistente(): void {
    if (this.idEdicion === null || !this.profesorOriginal) {
      this.mostrarMensaje('error', 'Aún no se han cargado los datos del profesor.');
      return;
    }
    if (!this.nombre.trim() || !this.apellido.trim() || !this.documento || !this.correo.trim()) {
      this.mostrarMensaje('error', 'Completa nombre, apellido, documento y correo.');
      return;
    }
    if (this.especialidadesSeleccionadas.length === 0) {
      this.mostrarMensaje('error', 'Selecciona al menos una especialidad.');
      return;
    }
    const errorHoras = this.validarHorasContrato(false);
    if (errorHoras) {
      this.mostrarMensaje('error', errorHoras);
      return;
    }

    const idProfesor: number = this.idEdicion;
    const franjas: FranjahorariaModel[] = this.construirFranjas();
    const teniaMalla: boolean = (this.profesorOriginal.disponibilidadHoraria ?? []).length > 0;

    const profesor = {
      ...this.profesorOriginal,
      nombre: this.nombre.trim(),
      apellido: this.apellido.trim(),
      documento: Number(this.documento),
      correo: this.correo.trim(),
      tipoVinculacion: this.tipoVinculacion,
      escalafon: this.escalafon,
      especialidades: this.especialidadesSeleccionadas,
      horasMaximasSemanales: this.maximoHoras,
      disponibilidadHoraria: franjas,
    } as ProfesorModel;

    // Contraseña: solo se envía si el administrador escribió una nueva
    if (this.contrasenia) {
      profesor.contrasenia = this.contrasenia;
    } else {
      delete (profesor as any).contrasenia;
    }

    this.guardando = true;
    this.profesorService
      .actualizarInformacionProfesor(profesor)
      .pipe(
        switchMap(() =>
          franjas.length > 0 || teniaMalla
            ? this.profesorService.guardarDisponibilidad(idProfesor, franjas)
            : of(''),
        ),
      )
      .subscribe({
        next: () => {
          this.guardando = false;
          this.router.navigate(['/gestion-bases-datos']);
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
    const errorHorasPropias = this.validarHorasContrato(true);
    if (errorHorasPropias) {
      this.mostrarMensaje('error', errorHorasPropias);
      return;
    }

    this.guardando = true;
    this.profesorService.guardarDisponibilidad(id, franjas).subscribe({
      next: () => {
        this.guardando = false;
        this.router.navigate(['/inicio-docente']);
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
    this.horasMaximasSemanales = this.maximoHoras;
    this.especialidadesSeleccionadas = [];
    this.seleccion.clear();
    this.avisoGrilla = null;
    this.cdr.detectChanges();
  }

  // ------------------------------------------------------------------
  // Éxito al crear: ventana centrada y regreso a la ventana principal
  // ------------------------------------------------------------------

  private mostrarExitoCreacion(texto: string): void {
    this.exitoCreacion = texto && texto.trim() ? texto : 'Se ha creado el profesor con éxito.';
    this.cdr.detectChanges();
    this.temporizadorExito = setTimeout(() => this.cerrarExitoCreacion(), 2500);
  }

  cerrarExitoCreacion(): void {
    if (this.temporizadorExito) clearTimeout(this.temporizadorExito);
    this.exitoCreacion = null;
    this.router.navigate([RUTA_PRINCIPAL]);
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
      this.mostrarMensaje(
        'error',
        typeof error.error === 'string' && error.error
          ? error.error
          : 'No tienes permiso para realizar esta acción.',
      );
    } else {
      this.mostrarMensaje(
        'error',
        typeof error.error === 'string' && error.error
          ? error.error
          : `Ocurrió un error (código ${error.status}).`,
      );
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
