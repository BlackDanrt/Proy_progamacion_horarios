import { Component, HostListener, inject, OnInit, ChangeDetectorRef} from '@angular/core';
import { Router } from '@angular/router';
import { SesionService } from '../services/sesion.service';
import { AsignaturaService } from '../services/asignatura.service';
import { SalonService } from '../services/salon.service';
import { ProfesorService } from '../services/profesor.service';
import { GrupoService } from '../services/grupo.service';
import { AsignaturaModel } from '../models/asignatura.model';
import { ProfesorModel } from '../models/profesor.model';
import { SalonModel } from '../models/salon.model';
import { reglaDe } from '../utils/reglas-contrato';

/** Reglas institucionales de horario */
const ALMUERZO_INICIO = '12:00';
const ALMUERZO_FIN = '13:00';
const SABADO_FIN = '13:00'; // el sábado solo hay clases hasta la 1:00 PM
const HORAS_POR_CLASE = 2; // cada clase dura 2 h
const MIN_POR_BLOQUE = 60; // cada fila del calendario es de 1 h
const BLOQUES_POR_CLASE = (HORAS_POR_CLASE * 60) / MIN_POR_BLOQUE; // una clase ocupa 2 filas seguidas

/** Reglas de tamaño de grupo (documento "Programación de horarios de clases") */
const CUPO_MAXIMO_GRUPO = 40; // estudiantes por grupo
const TOLERANCIA_CUPO = 0.1; // tolerancia del 10 %
const MAXIMO_CON_TOLERANCIA = Math.floor(CUPO_MAXIMO_GRUPO * (1 + TOLERANCIA_CUPO)); // 44
const MINIMO_ESTUDIANTES = 11; // con 10 o menos no se abre el grupo

/** Reglas de periodos académicos */
const PREFIJO_INTERSEMESTRAL = 'Intersemestral';
const ANIOS_ADELANTE_PERMITIDOS = 1; // año actual y el siguiente (hoy: 2026 y 2027)
const CLAVE_PERIODOS_STORAGE = 'horarios.periodosCreados';

/** Representación de una asignatura o grupo en el calendario de asignación. */
export interface AsignaturaUI {
  /** Identificador de la asignatura. */
  id?: number;
  /** Identificador del grupo persistido en el backend. */
  grupoId?: number;
  /** Nombre de la asignatura. */
  nombre: string;
  /** Salón asignado. */
  aula: string;
  /** Bloque del salón asignado. */
  bloque: string;
  /** Nombre del docente asignado. */
  docente: string;
  /** Identificador del docente asignado. */
  docenteId?: number;
  /** Identificador del salón asignado. */
  salonId?: number;
  /** Número del grupo dentro de la asignatura. */
  numeroGrupo?: number;
  /** Color usado para identificar la asignatura. */
  color?: string;
  /** Color claro de fondo usado en el calendario. */
  bgLight?: string;
  /** Cantidad de estudiantes del grupo. */
  numEstudiantes?: number;
  persistido?: boolean; // true = ya existe en la base de datos
  dia?: string;
  hora?: string; // bloque de INICIO, ej. '07:00 - 09:00'
}

/** Franja de disponibilidad del profesor ya normalizada (minutos desde las 00:00) */
/** Intervalo de disponibilidad del docente expresado en minutos del día. */
export interface FranjaDisponible {
  /** Día de la semana normalizado. */
  dia: string; // LUNES, MARTES, MIERCOLES...
  /** Minuto de inicio de la disponibilidad. */
  inicio: number;
  /** Minuto de fin de la disponibilidad. */
  fin: number;
}

/** Identificación visible de una columna del calendario. */
export interface BloqueCalendario {
  /** Nombre mostrado para el día o bloque. */
  nombre: string;
  /** Clave utilizada para comparar el bloque. */
  clave: string;
}

/** Tipos de notificación mostrados en la interfaz. */
export type TipoToast = 'exito' | 'error' | 'advertencia' | 'info';

/** Notificación temporal visible para el usuario. */
export interface Toast {
  /** Identificador usado para cerrar la notificación. */
  id: number;
  /** Estilo asociado al tipo de mensaje. */
  tipo: TipoToast;
  /** Texto que se muestra en la notificación. */
  mensaje: string;
}

@Component({
  selector: 'app-asignacion-horarios',
  standalone: false,
  templateUrl: './asignacion-horarios.html',
  styleUrls: ['./asignacion-horarios.css'],
})
/** Administra la asignación de grupos, docentes, salones y franjas horarias. */
export class AsignacionHorarios implements OnInit {
  private asignaturaService = inject(AsignaturaService);
  private salonService = inject(SalonService);
  private profesorService = inject(ProfesorService);
  private grupoService = inject(GrupoService);
  private router = inject(Router);
  private sesion = inject(SesionService);
  private cdr = inject(ChangeDetectorRef);
  // Modales
  mostrarModalProfesor: boolean = false; // elegir profesor
  mostrarModalAsignatura: boolean = false; // vincular materia libre
  mostrarModalPeriodo: boolean = false; // crear periodo
  mostrarMenuUsuario: boolean = false;

  // Notificaciones visuales (reemplazan a alert / confirm)
  toasts: Toast[] = [];
  private toastSeq = 0;
  confirmacion: { titulo: string; mensaje: string; textoAceptar: string } | null = null;
  avisoExito: { titulo: string; mensaje: string } | null = null;
  private accionAceptar?: () => void;
  private accionCancelar?: () => void;

  listaMaterias: AsignaturaModel[] = [];
  listaProfesoresObj: ProfesorModel[] = [];
  listaSalonesObj: SalonModel[] = [];

  listaProfesores: string[] = [];
  listaBloques: string[] = [];
  salonesDisponibles: string[] = [];
  mapaSalones: { [key: string]: string[] } = {};

  /** Todos los grupos existentes (para numerar el siguiente grupo de una materia) */
  gruposGlobales: any[] = [];

  /** IDs de asignaturas que ya tienen un profesor vinculado en el periodo seleccionado */
  idsMateriasConProfesor: Set<number> = new Set<number>();

  // Filtros de Docente y Periodo Académico
  docenteSeleccionFiltro: string = '';
  docenteActual: string = ''; // docente cuyo horario está cargado

  anioActual: number = new Date().getFullYear();
  periodoSeleccionado: string = `${new Date().getFullYear()}-1`;
  periodoActual: string = this.periodoSeleccionado; // periodo cuyo horario está cargado
  listaPeriodos: string[] = [];

  // Campos del modal de periodo
  tipoPeriodoModal: 'SEMESTRAL' | 'INTERSEMESTRAL' = 'SEMESTRAL';
  anioPeriodoModal: number = new Date().getFullYear();
  semestrePeriodoModal: number = 1;
  aniosPermitidos: number[] = [];

  // Campos del modal de asignatura
  docenteSeleccionadoModal: string = '';
  materiaSeleccionadaModal: AsignaturaModel | null = null;
  numeroGrupoModal: number = 1;
  bloqueSeleccionadoModal: string = '';
  salonSeleccionadoModal: string = '';
  numEstudiantesModal: number = 30;
  necesitaComputadoresModal: boolean = false;
  necesitaSillasModal: boolean = false;
  readonly minEstudiantes = MINIMO_ESTUDIANTES;
  readonly maxEstudiantes = MAXIMO_CON_TOLERANCIA;

  /** Profesor cuyo horario está cargado (para leer su contrato y sus especialidades) */
  profesorActual: ProfesorModel | null = null;

  /** Disponibilidad horaria del profesor cuyo horario está cargado */
  disponibilidadDocente: FranjaDisponible[] = [];

  asignaturasDisponibles: AsignaturaUI[] = [];
  clasesAsignadas: AsignaturaUI[] = [];
  celdaActiva: string | null = null;

  /** El horario es del semestre completo: de lunes a sábado, sin fechas */
  dias: BloqueCalendario[] = [
    { nombre: 'LUNES', clave: 'LUNES' },
    { nombre: 'MARTES', clave: 'MARTES' },
    { nombre: 'MIÉRCOLES', clave: 'MIERCOLES' },
    { nombre: 'JUEVES', clave: 'JUEVES' },
    { nombre: 'VIERNES', clave: 'VIERNES' },
    { nombre: 'SÁBADO', clave: 'SABADO' },
  ];

  textoAlmuerzo: string = `Almuerzo ${ALMUERZO_INICIO} - ${ALMUERZO_FIN} · no se pueden asignar clases`;

  /**
   * Bloques del calendario. El bloque 11:00 - 13:00 se cruza con el almuerzo (12:00 - 13:00),
   * por eso se pinta deshabilitado automáticamente (ver esAlmuerzo()).
   */
  horasBloques: string[] = [
    '07:00 - 08:00',
    '08:00 - 09:00',
    '09:00 - 10:00',
    '10:00 - 11:00',
    '11:00 - 12:00',
    '12:00 - 13:00',
    '13:00 - 14:00',
    '14:00 - 15:00',
    '15:00 - 16:00',
    '16:00 - 17:00',
    '17:00 - 18:00',
    '18:00 - 19:00',
    '19:00 - 20:00',
    '20:00 - 21:00',
    '21:00 - 22:00',
  ];

  /** Inicializa los periodos académicos y carga los datos del backend. */
  ngOnInit(): void {
    this.inicializarPeriodos();
    this.cargarDatosBackend();
  }

  /* ===================== NOTIFICACIONES VISUALES ===================== */

  /** Muestra una notificación temporal del tipo indicado. */
  mostrarToast(tipo: TipoToast, mensaje: string, duracionMs: number = 6000): void {
    const id = ++this.toastSeq;
    this.toasts.push({ id, tipo, mensaje });
    this.cdr.detectChanges();
    setTimeout(() => this.cerrarToast(id), duracionMs);
  }

  /** Cierra la notificación identificada. */
  cerrarToast(id: number): void {
    this.toasts = this.toasts.filter((t) => t.id !== id);
    this.cdr.detectChanges();
  }

  /** Abre una confirmación y registra las acciones para aceptar o cancelar. */
  pedirConfirmacion(
    titulo: string,
    mensaje: string,
    onAceptar: () => void,
    onCancelar?: () => void,
    textoAceptar: string = 'Continuar',
  ): void {
    this.confirmacion = { titulo, mensaje, textoAceptar };
    this.accionAceptar = onAceptar;
    this.accionCancelar = onCancelar;
  }

  /** Ejecuta la acción asociada a la aceptación de la confirmación. */
  aceptarConfirmacion(): void {
    const accion = this.accionAceptar;
    this.confirmacion = null;
    this.accionAceptar = undefined;
    this.accionCancelar = undefined;
    if (accion) accion();
  }

  /** Ejecuta la acción asociada a la cancelación de la confirmación. */
  cancelarConfirmacion(): void {
    const accion = this.accionCancelar;
    this.confirmacion = null;
    this.accionAceptar = undefined;
    this.accionCancelar = undefined;
    if (accion) accion();
  }

  /** Ventana emergente de éxito (misma apariencia que las ventanas de confirmación) */
  mostrarAvisoExito(titulo: string, mensaje: string): void {
    this.avisoExito = { titulo, mensaje };
  }

  /** Cierra la ventana de aviso de éxito. */
  cerrarAvisoExito(): void {
    this.avisoExito = null;
  }

  /** Obtiene un mensaje corto y legible de un error HTTP (sin volcar el JSON ni el stack trace) */
  private mensajeError(err: any): string {
    let cuerpo: any = err?.error;
    if (typeof cuerpo === 'string') {
      try {
        cuerpo = JSON.parse(cuerpo);
      } catch {
        /* ya es texto plano */
      }
    }
    let m: string =
      typeof cuerpo === 'string' ? cuerpo : cuerpo?.message || cuerpo?.error || err?.message || '';
    const trace: string = String(cuerpo?.trace ?? '');
    const crudo = `${m} ${trace}`;

    if (/Cannot map `null` into type `?(int|long|double|boolean)/i.test(crudo)) {
      m =
        'El servidor recibió un dato vacío donde esperaba un número. Revise grupo, salón y número de estudiantes y vuelva a intentarlo.';
    }
    if (!m) m = `Error ${err?.status ?? ''} al comunicarse con el servidor.`;
    m = String(m).split(/\r?\n/)[0].trim();
    return m.length > 280 ? m.substring(0, 280) + '…' : m;
  }

  /* ===================== PERIODOS ACADÉMICOS ===================== */

  /** Construye la lista de periodos permitidos y guardados localmente. */
  private inicializarPeriodos(): void {
    this.aniosPermitidos = [];
    for (let i = 0; i <= ANIOS_ADELANTE_PERMITIDOS; i++) {
      this.aniosPermitidos.push(this.anioActual + i);
    }

    const base = [
      `${this.anioActual}-1`,
      `${this.anioActual}-2`,
      `${PREFIJO_INTERSEMESTRAL} ${this.anioActual}-1`,
      `${PREFIJO_INTERSEMESTRAL} ${this.anioActual}-2`,
    ];
    let guardados: string[] = [];
    try {
      guardados = JSON.parse(localStorage.getItem(CLAVE_PERIODOS_STORAGE) || '[]');
    } catch {
      guardados = [];
    }

    this.listaPeriodos = [];
    [...base, ...guardados].forEach((p) => this.agregarPeriodoSiNoExiste(p));
    this.ordenarPeriodos();
  }

  /** Valida el formato: 2027-1 | 2027-2 | Intersemestral 2027-1 | Intersemestral 2027-2 */
  private formatoPeriodoValido(p: string): boolean {
    return new RegExp(`^(${PREFIJO_INTERSEMESTRAL} )?\\d{4}-[12]$`).test(p);
  }

  /** Añade el periodo si su formato es válido y aún no está registrado. */
  private agregarPeriodoSiNoExiste(p: string): void {
    if (p && this.formatoPeriodoValido(p) && !this.existePeriodo(p)) {
      this.listaPeriodos.push(p);
    }
  }

  /** Indica si el periodo ya aparece en la lista. */
  existePeriodo(p: string): boolean {
    return this.listaPeriodos.some((x) => x.toLowerCase() === p.toLowerCase());
  }

  private clavePeriodo(p: string): number {
    const inter = p.startsWith(PREFIJO_INTERSEMESTRAL) ? 1 : 0;
    const m = p.match(/(\d{4})-([12])$/);
    const anio = m ? parseInt(m[1]) : 0;
    const sem = m ? parseInt(m[2]) : 0;
    return anio * 100 + sem * 10 + inter;
  }

  /** Ordena los periodos por año, semestre y tipo. */
  private ordenarPeriodos(): void {
    this.listaPeriodos.sort((a, b) => this.clavePeriodo(a) - this.clavePeriodo(b));
  }

  /** Inicializa y abre el formulario para crear un periodo. */
  abrirModalPeriodo(): void {
    this.tipoPeriodoModal = 'SEMESTRAL';
    this.anioPeriodoModal = this.anioActual;
    this.semestrePeriodoModal = 1;
    this.mostrarModalPeriodo = true;
  }

  /** Cierra el formulario de creación de periodos. */
  cerrarModalPeriodo(): void {
    this.mostrarModalPeriodo = false;
  }

  get nombrePeriodoModal(): string {
    const base = `${this.anioPeriodoModal}-${this.semestrePeriodoModal}`;
    return this.tipoPeriodoModal === 'INTERSEMESTRAL' ? `${PREFIJO_INTERSEMESTRAL} ${base}` : base;
  }

  /** Valida y agrega el periodo académico seleccionado. */
  crearPeriodo(): void {
    const nombre = this.nombrePeriodoModal;
    const anio = Number(this.anioPeriodoModal);
    const sem = Number(this.semestrePeriodoModal);

    if (!this.aniosPermitidos.includes(anio)) {
      this.mostrarToast(
        'error',
        `Solo se pueden crear periodos del ${this.aniosPermitidos[0]} al ${this.aniosPermitidos[this.aniosPermitidos.length - 1]}.`,
      );
      return;
    }
    if (sem !== 1 && sem !== 2) {
      this.mostrarToast('error', 'El periodo debe terminar en -1 o -2.');
      return;
    }
    if (!this.formatoPeriodoValido(nombre)) {
      this.mostrarToast('error', 'Formato de periodo no válido.');
      return;
    }
    if (this.existePeriodo(nombre)) {
      this.mostrarToast('advertencia', `El periodo ${nombre} ya existe.`);
      return;
    }

    this.listaPeriodos.push(nombre);
    this.ordenarPeriodos();
    this.guardarPeriodosCreados();
    this.mostrarModalPeriodo = false;
    this.mostrarToast('exito', `Periodo ${nombre} creado correctamente.`);

    this.periodoSeleccionado = nombre;
    this.onPeriodoChange();
  }

  private guardarPeriodosCreados(): void {
    try {
      localStorage.setItem(CLAVE_PERIODOS_STORAGE, JSON.stringify(this.listaPeriodos));
    } catch {
      /* almacenamiento no disponible: el periodo vive solo en esta sesión */
    }
  }

  /** Cambia de periodo, solicitando confirmación si hay cambios pendientes. */
  onPeriodoChange(): void {
    const nuevo = this.periodoSeleccionado;
    if (nuevo === this.periodoActual) return;

    const aplicar = () => {
      this.periodoActual = nuevo;
      this.periodoSeleccionado = nuevo;
      this.cargarGruposGlobales();
      if (this.docenteActual) {
        this.cargarHorarioDocente(this.docenteActual);
      } else {
        this.asignaturasDisponibles = [];
        this.clasesAsignadas = [];
      }
    };

    if (this.hayCambiosSinGuardar()) {
      this.pedirConfirmacion(
        'Cambios sin guardar',
        'Si cambia de periodo se perderán los cambios que no ha guardado. ¿Desea continuar?',
        aplicar,
        () => {
          this.periodoSeleccionado = this.periodoActual;
        },
      );
    } else {
      aplicar();
    }
  }

  /* ===================== UTILIDADES DE HORA ===================== */

  /** Convierte una hora HH:mm a minutos desde medianoche. */
  private aMin(h: string): number {
    const [hh, mm] = h.trim().split(':').map(Number);
    return hh * 60 + (mm || 0);
  }

  /** Convierte minutos desde medianoche a una cadena de hora. */
  private aTexto(min: number): string {
    const hh = String(Math.floor(min / 60)).padStart(2, '0');
    const mm = String(min % 60).padStart(2, '0');
    return `${hh}:${mm}:00`;
  }

  /** Convierte la etiqueta de un bloque en sus minutos de inicio y fin. */
  private rango(bloque: string): [number, number] {
    const [ini, fin] = bloque.split(' - ');
    return [this.aMin(ini), this.aMin(fin)];
  }

  /** true si el bloque se cruza con la hora de almuerzo */
  esAlmuerzo(bloque: string): boolean {
    const [ini, fin] = this.rango(bloque);
    return ini < this.aMin(ALMUERZO_FIN) && this.aMin(ALMUERZO_INICIO) < fin;
  }

  /** Filas (bloques de 1 h) que ocupa una clase de 2 h que empieza en `hora`; null si no cabe en el calendario */
  /** Obtiene los bloques horarios consecutivos ocupados por una clase. */
  private bloquesQueOcupa(hora: string): string[] | null {
    const idx = this.horasBloques.indexOf(hora);
    if (idx < 0) return null;
    const bloques = this.horasBloques.slice(idx, idx + BLOQUES_POR_CLASE);
    return bloques.length === BLOQUES_POR_CLASE ? bloques : null;
  }

  /** Valida que NINGUNA de las filas que ocupará la clase sea almuerzo ni esté cerrada */
  /** Valida que una clase quepa y cumpla almuerzo, cierre y disponibilidad. */
  private validarBloque(dia: string, hora: string): string | null {
    const bloques = this.bloquesQueOcupa(hora);
    if (!bloques) {
      return `La clase dura ${HORAS_POR_CLASE} horas y no cabe en el calendario a partir de ese horario.`;
    }
    if (bloques.some((b) => this.esAlmuerzo(b))) {
      return `La clase de ${HORAS_POR_CLASE} horas se cruzaría con el almuerzo (${ALMUERZO_INICIO} - ${ALMUERZO_FIN}).`;
    }
    if (bloques.some((b) => this.esCerrado(dia, b))) {
      return 'El sábado solo se pueden asignar clases hasta la 1:00 PM.';
    }
    if (bloques.some((b) => this.esNoDisponible(dia, b))) {
      return `${this.docenteActual} no tiene disponibilidad horaria para una clase de ${HORAS_POR_CLASE} horas en ese horario.`;
    }
    return null;
  }

  /* ===================== DISPONIBILIDAD DEL PROFESOR ===================== */

  /** Quita tildes y pasa a mayúsculas: 'Miércoles' -> 'MIERCOLES' */
  private normalizarDia(d: any): string {
    return String(d ?? '')
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .toUpperCase()
      .trim();
  }

  /** Convierte la hora que llega del backend a minutos. Acepta '07:00:00' o [7, 0] (LocalTime serializado como arreglo) */
  private horaAMinutos(h: any): number {
    if (Array.isArray(h)) return (Number(h[0]) || 0) * 60 + (Number(h[1]) || 0);
    return this.aMin(String(h));
  }

  /** Carga en memoria las franjas en las que el profesor dijo que puede dar clase */
  private cargarDisponibilidad(prof: ProfesorModel | undefined): void {
    const franjas: any[] = (prof as any)?.disponibilidadHoraria || [];
    this.disponibilidadDocente = franjas
      .filter((f) => f && f.dia && f.horaInicio != null && f.horaFin != null)
      .map((f) => ({
        dia: this.normalizarDia(f.dia),
        inicio: this.horaAMinutos(f.horaInicio),
        fin: this.horaAMinutos(f.horaFin),
      }));
  }

  /** true si el bloque de 1 h cabe completo dentro de alguna franja de disponibilidad del profesor */
  private estaDisponible(dia: string, bloque: string): boolean {
    const [ini, fin] = this.rango(bloque);
    return this.disponibilidadDocente.some((f) => f.dia === dia && f.inicio <= ini && fin <= f.fin);
  }

  /** true si hay un profesor cargado y NO está disponible en esa celda (se pinta bloqueada) */
  esNoDisponible(dia: string, hora: string): boolean {
    if (!this.docenteActual) return false;
    return !this.estaDisponible(dia, hora);
  }

  /** Busca una clase que se solape con la nueva (ignorando la que se está moviendo) */
  /** Busca una clase que se solape con la posición propuesta. */
  private buscarCruce(
    dia: string,
    hora: string,
    ignorar: { dia: string; hora: string } | null,
  ): AsignaturaUI | undefined {
    const ini = this.rango(hora)[0];
    const fin = ini + HORAS_POR_CLASE * 60;
    return this.clasesAsignadas.find((c) => {
      if (c.dia !== dia || !c.hora) return false;
      if (ignorar && c.dia === ignorar.dia && c.hora === ignorar.hora) return false;
      const cIni = this.rango(c.hora)[0];
      const cFin = cIni + HORAS_POR_CLASE * 60;
      return cIni < fin && ini < cFin;
    });
  }

  /* ===================== HORAS SEGÚN CONTRATO ===================== */

  /** Mínimo semanal de horas permitido por el contrato del docente. */
  get minimoHoras(): number {
    return reglaDe(this.profesorActual?.tipoVinculacion).minHoras;
  }
  /** Máximo semanal de horas permitido por el contrato del docente. */
  get maximoHoras(): number {
    return reglaDe(this.profesorActual?.tipoVinculacion).maxHoras;
  }

  /** Horas de clase que ya tiene el docente en el calendario (guardadas + pendientes) */
  get horasAsignadas(): number {
    return this.clasesAsignadas.length * HORAS_POR_CLASE;
  }

  /** Especialidades del profesor y de la asignatura como texto comparable */
  /** Normaliza especialidades para facilitar su comparación. */
  private normEsp(v: any): string[] {
    const arr = Array.isArray(v) ? v : v ? [v] : [];
    return arr.map((x) =>
      String(x)
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toUpperCase()
        .replace(/[\s_]+/g, '_')
        .trim(),
    );
  }

  /**
   * true si la asignatura corresponde a alguna especialidad del profesor.
   * Si la asignatura no trae especialidad en el modelo, no se puede filtrar y se permite.
   */
  /** Indica si la asignatura coincide con las especialidades del docente. */
  private coincideEspecialidad(m: AsignaturaModel): boolean {
    const deMateria = this.normEsp((m as any).especialidad ?? (m as any).especialidades);
    if (deMateria.length === 0) return true;
    const deProfe = this.normEsp((this.profesorActual as any)?.especialidades);
    return deMateria.some((e) => deProfe.includes(e));
  }

  /* ===================== CARGA DE DATOS ===================== */

  /** Carga las asignaturas, profesores y salones necesarios para asignar horarios. */
  cargarDatosBackend(): void {
    this.asignaturaService.mostrarTodo().subscribe({
      next: (response) => {
        if (response.body) this.listaMaterias = response.body;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al cargar asignaturas:', err),
    });

    this.salonService.mostrarTodo().subscribe({
      next: (response) => {
        if (response.body) {
          this.listaSalonesObj = response.body;
          this.mapaSalones = {};
          const bloquesSet = new Set<string>();
          response.body.forEach((salon) => {
            bloquesSet.add(salon.bloque);
            if (!this.mapaSalones[salon.bloque]) this.mapaSalones[salon.bloque] = [];
            this.mapaSalones[salon.bloque].push(`${salon.bloque}${salon.numeroSalon}`);
          });
          this.cdr.detectChanges();
          this.listaBloques = Array.from(bloquesSet);
        }
      },
      error: (err) => console.error('Error al cargar salones:', err),
    });

    this.profesorService.mostrarTodo().subscribe({
      next: (response) => {
        if (response.body) {
          this.listaProfesoresObj = response.body;
          this.listaProfesores = response.body.map((p) => `${p.nombre} ${p.apellido}`);

          // Al entrar, primero se elige el profesor
          if (!this.docenteSeleccionFiltro) {
            this.abrirModalProfesor();
          }
          this.cdr.detectChanges();
        }
      },
      error: (err) => console.error('Error al cargar profesores:', err),
    });

    this.cargarGruposGlobales();
  }

  /**
   * Consulta todos los grupos para saber qué asignaturas ya tienen profesor en el periodo
   * seleccionado y, de paso, descubre los periodos que ya existen en la base de datos.
   */
  /** Carga los grupos existentes para el periodo seleccionado. */
  cargarGruposGlobales(): void {
    this.grupoService.mostrarTodo().subscribe({
      next: (response: any) => {
        const grupos: any[] = response?.body && response.status !== 204 ? response.body : [];
        this.gruposGlobales = grupos;

        // Periodos que ya existen en el backend
        let huboNuevos = false;
        grupos.forEach((g) => {
          if (g.periodo && this.formatoPeriodoValido(g.periodo) && !this.existePeriodo(g.periodo)) {
            this.listaPeriodos.push(g.periodo);
            huboNuevos = true;
          }
        });
        if (huboNuevos) this.ordenarPeriodos();

        const ids = grupos
          .filter(
            (g) =>
              g.profesor?.id &&
              (!this.periodoSeleccionado || g.periodo === this.periodoSeleccionado),
          )
          .map((g) => g.asignatura?.id)
          .filter((id) => id !== undefined && id !== null);
        this.idsMateriasConProfesor = new Set<number>(ids);
      },
      error: (err: any) => console.error('Error al cargar grupos:', err),
    });
  }

  /** Materias que NO tienen profesor vinculado (ni guardado ni pendiente en pantalla) */
  /** Materias sin profesor asignado en el periodo seleccionado. */
  get materiasSinProfesor(): AsignaturaModel[] {
    return this.listaMaterias.filter(
      (m) =>
        !this.idsMateriasConProfesor.has(m.id as number) &&
        !this.asignaturasDisponibles.some((a) => a.id === m.id) &&
        this.coincideEspecialidad(m),
    );
  }

  /* ===================== CAMBIO DE DOCENTE ===================== */

  /** Indica si el calendario contiene cambios aún no persistidos. */
  hayCambiosSinGuardar(): boolean {
    return (
      this.clasesAsignadas.some((c) => !c.persistido) ||
      this.asignaturasDisponibles.some((a) => !a.persistido)
    );
  }

  /** Cambia de profesor (con confirmación si hay cambios sin guardar) y carga sus materias */
  /** Cambia el docente activo y carga su horario correspondiente. */
  private cambiarDocente(nombre: string): void {
    if (!nombre) {
      this.docenteActual = '';
      this.profesorActual = null;
      this.disponibilidadDocente = [];
      this.asignaturasDisponibles = [];
      this.clasesAsignadas = [];
      return;
    }
    if (nombre === this.docenteActual) return;

    const aplicar = () => {
      this.docenteActual = nombre;
      this.docenteSeleccionFiltro = nombre;
      this.cargarHorarioDocente(nombre);
    };

    if (this.hayCambiosSinGuardar()) {
      this.pedirConfirmacion(
        'Cambios sin guardar',
        'Si cambia de docente se perderán los cambios que no ha guardado. ¿Desea continuar?',
        aplicar,
        () => {
          this.docenteSeleccionFiltro = this.docenteActual;
        },
      );
    } else {
      aplicar();
    }
  }

  /** Actualiza la selección de docente según el filtro actual. */
  onDocenteFiltroChange(): void {
    this.cambiarDocente(this.docenteSeleccionFiltro);
  }

  /** Carga el horario y disponibilidad del docente seleccionado. */
  cargarHorarioDocente(nombreDocente: string): void {
    const profObj = this.listaProfesoresObj.find(
      (p) => `${p.nombre} ${p.apellido}` === nombreDocente,
    );
    if (!profObj || !profObj.id) return;

    this.profesorActual = profObj;

    // Disponibilidad: lo que no esté dentro de sus franjas queda bloqueado en el calendario
    this.cargarDisponibilidad(profObj);
    if (this.disponibilidadDocente.length === 0) {
      this.mostrarToast(
        'advertencia',
        `${nombreDocente} no tiene disponibilidad horaria registrada, por lo que todo el calendario está bloqueado.`,
      );
    }

    this.grupoService.buscarPorProfesor(profObj.id).subscribe({
      next: (response) => {
        this.asignaturasDisponibles = [];
        this.clasesAsignadas = [];

        if (response.body && response.status !== 204) {
          const grupos = response.body.filter(
            (g) => !this.periodoSeleccionado || g.periodo === this.periodoSeleccionado,
          );
          const coloresPill = [
            { color: '#16a34a', bgLight: '#dcfce7' },
            { color: '#2563eb', bgLight: '#dbeafe' },
            { color: '#9333ea', bgLight: '#f3e8ff' },
            { color: '#ea580c', bgLight: '#ffedd5' },
          ];

          grupos.forEach((grupo, index) => {
            const col = coloresPill[index % coloresPill.length];
            const aulaStr = grupo.salon
              ? `${grupo.salon.bloque}${grupo.salon.numeroSalon}`
              : 'Por asignar';

            const asigUI: AsignaturaUI = {
              id: grupo.asignatura?.id,
              grupoId: (grupo as any).id,
              nombre: `${grupo.asignatura?.nombre} (G${grupo.numeroGrupo})`,
              aula: aulaStr,
              bloque: grupo.salon ? `Bloque ${grupo.salon.bloque}` : 'Sin bloque',
              docente: nombreDocente,
              docenteId: profObj.id,
              salonId: grupo.salon?.id,
              numeroGrupo: grupo.numeroGrupo,
              color: col.color,
              bgLight: col.bgLight,
              numEstudiantes: grupo.capacidadMaxima,
              persistido: true,
            };

            this.asignaturasDisponibles.push(asigUI);

            (grupo.sesiones || []).forEach((sesion) => {
              const ini = sesion.horaInicio.substring(0, 5);
              const fin = sesion.horaFin.substring(0, 5);
              const bloqueInicio = this.horasBloques.find((b) => b.startsWith(ini));
              if (!bloqueInicio) {
                console.warn(
                  `La sesión ${ini}-${fin} no coincide con ningún bloque del calendario.`,
                );
                return;
              }
              this.clasesAsignadas.push({
                ...asigUI,
                dia: sesion.dia,
                hora: bloqueInicio,
              });
            });
          });
        }
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al consultar horario:', err);
        this.asignaturasDisponibles = [];
        this.clasesAsignadas = [];
        this.mostrarToast('error', 'No se pudo cargar el horario del docente.');
        this.cdr.detectChanges();
      },
    });
  }

  /* ===================== MODAL: ELEGIR PROFESOR ===================== */

  /** Abre la selección de docente para cargar su horario. */
  abrirModalProfesor(): void {
    this.docenteSeleccionadoModal = this.docenteActual || '';
    this.mostrarModalProfesor = true;
  }

  /** Cierra el modal de selección de docente. */
  cerrarModalProfesor(): void {
    this.mostrarModalProfesor = false;
  }

  /** Continúa con el profesor elegido: carga sus materias y horario de una vez */
  /** Confirma el docente seleccionado y carga su horario. */
  continuarConDocente(): void {
    if (!this.docenteSeleccionadoModal) {
      this.mostrarToast('advertencia', 'Seleccione un profesor para continuar.');
      return;
    }
    this.mostrarModalProfesor = false;
    this.cambiarDocente(this.docenteSeleccionadoModal);
  }

  /* ===================== MODAL: VINCULAR MATERIA LIBRE (+) ===================== */

  /** Abre el formulario para asignar una asignatura a un grupo. */
  abrirModalCrear(): void {
    // Sin profesor no se puede vincular nada: primero se elige el profesor
    if (!this.docenteActual) {
      this.abrirModalProfesor();
      return;
    }
    this.materiaSeleccionadaModal = null;
    this.numeroGrupoModal = 1;
    this.bloqueSeleccionadoModal = '';
    this.salonSeleccionadoModal = '';
    this.numEstudiantesModal = 30;
    this.necesitaComputadoresModal = false;
    this.necesitaSillasModal = false;
    this.salonesDisponibles = [];
    this.cargarGruposGlobales(); // refresca qué materias siguen libres
    this.mostrarModalAsignatura = true;
  }

  /** Cierra el formulario de creación de asignación. */
  cerrarModalCrear(): void {
    this.mostrarModalAsignatura = false;
  }

  /** Al elegir la materia: se precargan sus requisitos y se calcula el número de grupo */
  onMateriaChange(): void {
    const m = this.materiaSeleccionadaModal;
    this.necesitaComputadoresModal = !!m?.requiereComputadores;
    this.necesitaSillasModal = !!m?.requiereSillasMoviles;
    this.numeroGrupoModal = m ? this.siguienteNumeroGrupo(m) : 1;
    this.onRequisitosChange();
  }

  /** Cambió un requisito: se vuelve a filtrar bloque y salón */
  onRequisitosChange(): void {
    this.bloqueSeleccionadoModal = '';
    this.salonSeleccionadoModal = '';
    this.salonesDisponibles = [];
  }

  /** Siguiente número de grupo de la materia en el periodo (guardados + pendientes en pantalla) */
  /** Calcula el siguiente número de grupo disponible para la asignatura. */
  private siguienteNumeroGrupo(m: AsignaturaModel): number {
    const guardados = this.gruposGlobales
      .filter((g) => g.asignatura?.id === m.id && g.periodo === this.periodoSeleccionado)
      .map((g) => Number(g.numeroGrupo) || 0);
    const pendientes = this.asignaturasDisponibles
      .filter((a) => a.id === m.id)
      .map((a) => Number(a.numeroGrupo) || 0);
    return Math.max(0, ...guardados, ...pendientes) + 1;
  }

  /** El salón cumple exactamente lo que la materia pide (con o sin computadores, con o sin sillas móviles) */
  /** Comprueba si el salón cumple los requisitos de la materia seleccionada. */
  private cumpleRequisitos(s: SalonModel): boolean {
    return (
      !!s.tieneComputadores === this.necesitaComputadoresModal &&
      !!s.tieneSillasMoviles === this.necesitaSillasModal
    );
  }

  /** Bloques que tienen al menos un salón que cumple los requisitos */
  /** Bloques que contienen salones compatibles con los requisitos elegidos. */
  get bloquesFiltrados(): string[] {
    const set = new Set<string>();
    this.listaSalonesObj.filter((s) => this.cumpleRequisitos(s)).forEach((s) => set.add(s.bloque));
    return Array.from(set);
  }

  /** Actualiza los salones disponibles al cambiar el bloque seleccionado. */
  onBloqueChange(): void {
    this.salonesDisponibles = this.listaSalonesObj
      .filter((s) => s.bloque === this.bloqueSeleccionadoModal && this.cumpleRequisitos(s))
      .map((s) => `${s.bloque}${s.numeroSalon}`);
    this.salonSeleccionadoModal = '';
  }

  /** Valida y agrega una asignatura al calendario del periodo actual. */
  agregarNuevaAsignatura(): void {
    if (
      !this.docenteActual ||
      !this.materiaSeleccionadaModal ||
      !this.bloqueSeleccionadoModal ||
      !this.salonSeleccionadoModal ||
      !this.numeroGrupoModal
    ) {
      this.mostrarToast('advertencia', 'Complete todos los campos obligatorios.');
      return;
    }

    if (!this.coincideEspecialidad(this.materiaSeleccionadaModal)) {
      this.mostrarToast(
        'error',
        `${this.docenteActual} no tiene la especialidad de esta asignatura.`,
      );
      return;
    }

    // El número de grupo lo asigna el sistema (se recalcula por si cambió algo mientras el modal estaba abierto)
    this.numeroGrupoModal = this.siguienteNumeroGrupo(this.materiaSeleccionadaModal);

    // La materia debe seguir sin profesor
    if (!this.materiasSinProfesor.some((m) => m.id === this.materiaSeleccionadaModal?.id)) {
      this.mostrarToast('error', 'Esta asignatura ya tiene un profesor vinculado.');
      return;
    }

    const n = Number(this.numEstudiantesModal);
    if (!n || n < MINIMO_ESTUDIANTES) {
      this.mostrarToast(
        'error',
        `No se puede abrir un grupo con 10 estudiantes o menos. Mínimo: ${MINIMO_ESTUDIANTES}.`,
      );
      return;
    }
    if (n > MAXIMO_CON_TOLERANCIA) {
      this.mostrarToast(
        'error',
        `Un grupo admite máximo ${CUPO_MAXIMO_GRUPO} estudiantes con tolerancia del 10 % (${MAXIMO_CON_TOLERANCIA}).`,
      );
      return;
    }

    const profObj = this.listaProfesoresObj.find(
      (p) => `${p.nombre} ${p.apellido}` === this.docenteActual,
    );
    const bloqueLetra = this.salonSeleccionadoModal.charAt(0) as any;
    const numSalonVal = parseInt(this.salonSeleccionadoModal.slice(1));
    const salonObj = this.listaSalonesObj.find(
      (s) => s.bloque === bloqueLetra && s.numeroSalon === numSalonVal,
    );

    if (!salonObj || !this.materiaSeleccionadaModal) {
      this.mostrarToast('error', 'No se pudo identificar el salón o la asignatura.');
      return;
    }

    if (this.materiaSeleccionadaModal.requiereComputadores && !salonObj.tieneComputadores) {
      this.mostrarToast(
        'error',
        'La asignatura requiere equipos de cómputo y el salón no cuenta con ellos.',
      );
      return;
    }

    if (this.materiaSeleccionadaModal.requiereSillasMoviles && !salonObj.tieneSillasMoviles) {
      this.mostrarToast('error', 'La asignatura requiere sillas móviles y el salón no las posee.');
      return;
    }

    const tope = Math.floor(salonObj.capacidad * (1 + TOLERANCIA_CUPO));
    if (this.numEstudiantesModal > tope) {
      this.mostrarToast(
        'error',
        `Supera la capacidad del salón (${salonObj.capacidad} + 10 % de tolerancia = ${tope}).`,
      );
      return;
    }

    const coloresPill = [
      { color: '#16a34a', bgLight: '#dcfce7' },
      { color: '#2563eb', bgLight: '#dbeafe' },
      { color: '#9333ea', bgLight: '#f3e8ff' },
      { color: '#ea580c', bgLight: '#ffedd5' },
    ];
    const col = coloresPill[this.asignaturasDisponibles.length % coloresPill.length];

    this.asignaturasDisponibles.push({
      id: this.materiaSeleccionadaModal.id,
      nombre: `${this.materiaSeleccionadaModal.nombre} (G${this.numeroGrupoModal})`,
      docente: this.docenteActual,
      docenteId: profObj ? profObj.id : undefined,
      salonId: salonObj.id,
      numeroGrupo: this.numeroGrupoModal,
      bloque: `Bloque ${this.bloqueSeleccionadoModal}`,
      aula: this.salonSeleccionadoModal,
      color: col.color,
      bgLight: col.bgLight,
      numEstudiantes: this.numEstudiantesModal,
      persistido: false,
    });

    this.mostrarModalAsignatura = false;
    this.mostrarToast(
      'info',
      'Grupo vinculado. Arrástrelo al calendario para asignarle el horario.',
    );
  }

  /* ===================== DRAG & DROP ===================== */

  /** Inicia el arrastre de una asignatura pendiente de ubicar. */
  onDragStartAsignatura(event: DragEvent, asig: AsignaturaUI): void {
    event.dataTransfer?.setData('text/plain', JSON.stringify(asig));
  }

  /** Inicia el arrastre de una clase ya colocada en el calendario. */
  onDragStartClase(event: DragEvent, dia: string, hora: string): void {
    const clase = this.getClase(dia, hora);
    if (clase) {
      event.dataTransfer?.setData('text/plain', JSON.stringify(clase));
    }
  }

  /** Permite soltar un elemento y marca la celda destino. */
  onDragOver(event: DragEvent, dia: string, hora: string): void {
    event.preventDefault(); // se permite el drop para que onDrop explique por qué no se puede
    this.celdaActiva =
      this.esNoDisponible(dia, hora) || this.esCerrado(dia, hora) ? null : `${dia}|${hora}`;
  }

  /** Limpia el indicador visual al salir de una celda durante el arrastre. */
  onDragLeave(): void {
    this.celdaActiva = null;
  }

  /** Procesa la asignatura o clase soltada en una celda del calendario. */
  onDrop(event: DragEvent, dia: string, hora: string): void {
    event.preventDefault();
    this.celdaActiva = null;

    const data = event.dataTransfer?.getData('text/plain');
    if (!data) return;

    const asignatura: AsignaturaUI = JSON.parse(data);

    if (asignatura.persistido) {
      this.mostrarToast(
        'advertencia',
        'Este grupo ya está guardado en el sistema y no se puede modificar desde aquí.',
      );
      return;
    }

    // 1) Almuerzo / sábado
    const errorBloques = this.validarBloque(dia, hora);
    if (errorBloques) {
      this.mostrarToast('error', errorBloques);
      return;
    }

    // 2) Cruces (ignorando la clase que se está moviendo)
    const origen =
      asignatura.dia && asignatura.hora ? { dia: asignatura.dia, hora: asignatura.hora } : null;

    // 2.1) Las sesiones de un mismo grupo deben ser en días distintos
    const mismoDia = this.clasesAsignadas.some(
      (c) =>
        c.id === asignatura.id &&
        c.numeroGrupo === asignatura.numeroGrupo &&
        c.dia === dia &&
        !(origen && c.dia === origen.dia && c.hora === origen.hora),
    );
    if (mismoDia) {
      this.mostrarToast('error', 'Las sesiones de un mismo grupo deben ser en días distintos.');
      return;
    }
    const cruce = this.buscarCruce(dia, hora, origen);
    if (cruce) {
      this.mostrarToast(
        'error',
        `Cruce de horario: el docente ya tiene "${cruce.nombre}" en ese horario.`,
      );
      return;
    }

    // 2.2) Tope de horas del contrato (solo cuenta si es una clase nueva en el calendario)
    if (!origen && this.horasAsignadas + HORAS_POR_CLASE > this.maximoHoras) {
      this.mostrarToast(
        'error',
        `${this.docenteActual} ya tiene ${this.horasAsignadas} h y su contrato permite máximo ${this.maximoHoras} h por semana.`,
      );
      return;
    }

    // 3) Si venía del calendario, se quita de su posición anterior
    if (origen) {
      this.quitarClase(origen.dia, origen.hora);
    }

    this.clasesAsignadas.push({ ...asignatura, dia, hora });
  }

  /** true si la celda no admite clases (sábado después de la 1:00 PM) */
  /** Indica si el bloque horario está cerrado para el día especificado. */
  esCerrado(dia: string, hora: string): boolean {
    return dia === 'SABADO' && this.rango(hora)[1] > this.aMin(SABADO_FIN);
  }

  /** Clase que INICIA en esta celda */
  /** Obtiene la clase que comienza en la celda indicada. */
  getClase(dia: string, hora: string): AsignaturaUI | null {
    return this.clasesAsignadas.find((c) => c.dia === dia && c.hora === hora) || null;
  }

  /** Clase que empezó en la fila anterior y sigue ocupando esta celda (su 2.ª hora) */
  /** Obtiene la clase que ocupa la celda como continuación de su bloque. */
  getClaseContinuacion(dia: string, hora: string): AsignaturaUI | null {
    const ini = this.rango(hora)[0];
    return (
      this.clasesAsignadas.find((c) => {
        if (c.dia !== dia || !c.hora) return false;
        const cIni = this.rango(c.hora)[0];
        return cIni < ini && ini < cIni + HORAS_POR_CLASE * 60;
      }) || null
    );
  }

  /** Quita del calendario la clase ubicada en la celda indicada. */
  quitarClase(dia: string, hora: string): void {
    this.clasesAsignadas = this.clasesAsignadas.filter((c) => !(c.dia === dia && c.hora === hora));
  }

  /** Botón × del calendario */
  /** Quita una clase del calendario y la devuelve a la lista pendiente. */
  quitarClaseDesdeCalendario(dia: string, hora: string): void {
    const c = this.getClase(dia, hora);
    if (c?.persistido) {
      this.mostrarToast(
        'advertencia',
        'Este grupo ya está guardado en el sistema y no se puede quitar desde aquí.',
      );
      return;
    }
    this.quitarClase(dia, hora);
  }

  /* ===================== GUARDAR ===================== */

  /** Valida y persiste los cambios del calendario de asignación. */
  guardarCambios(): void {
    const nuevas = this.clasesAsignadas.filter((c) => !c.persistido);
    const gruposSinUbicar = this.asignaturasDisponibles.filter(
      (a) => !a.persistido && !nuevas.some((c) => c.id === a.id && c.numeroGrupo === a.numeroGrupo),
    );

    if (nuevas.length === 0) {
      this.mostrarToast('advertencia', 'No hay clases nuevas en el calendario para guardar.');
      return;
    }

    if (this.horasAsignadas > this.maximoHoras) {
      this.mostrarToast(
        'error',
        `Se superan las ${this.maximoHoras} h máximas del contrato (${this.horasAsignadas} h).`,
      );
      return;
    }
    if (this.horasAsignadas < this.minimoHoras) {
      this.pedirConfirmacion(
        'Horas por debajo del mínimo',
        `${this.docenteActual} tendría ${this.horasAsignadas} h y su contrato exige mínimo ${this.minimoHoras} h por semana. ¿Desea guardar de todos modos?`,
        () => this.ejecutarGuardado(nuevas, gruposSinUbicar),
        undefined,
        'Guardar',
      );
      return;
    }
    this.ejecutarGuardado(nuevas, gruposSinUbicar);
  }

  /** Persiste los grupos modificados y comunica los resultados al usuario. */
  private ejecutarGuardado(nuevas: AsignaturaUI[], gruposSinUbicar: AsignaturaUI[]): void {
    // Un grupo = un registro, con todas sus sesiones
    const porGrupo = new Map<string, AsignaturaUI[]>();
    nuevas.forEach((c) => {
      const key = `${c.id}|${c.numeroGrupo}`;
      if (!porGrupo.has(key)) porGrupo.set(key, []);
      porGrupo.get(key)!.push(c);
    });

    let pendientes = porGrupo.size;
    let errores = 0;
    let exitosos = 0;

    const finalizar = () => {
      if (pendientes > 0) return;
      if (exitosos > 0) {
        this.mostrarAvisoExito(
          'Guardado con éxito',
          `${exitosos} grupo(s) guardado(s) con éxito en el periodo ${this.periodoSeleccionado}.`,
        );
      }
      if (errores === 0 && gruposSinUbicar.length > 0) {
        this.mostrarToast(
          'info',
          `${gruposSinUbicar.length} grupo(s) no se guardaron porque no tienen clase en el calendario.`,
        );
      }
      this.cargarGruposGlobales();
      // Solo se recarga el horario si todo salió bien; si hubo errores se conservan los cambios pendientes
      if (errores === 0 && this.docenteActual) this.cargarHorarioDocente(this.docenteActual);
    };

    porGrupo.forEach((clases) => {
      const base = clases[0];


      // Se envían los objetos completos (no solo {id}): el back falla con
      // "Cannot map null into type int" cuando a la entidad anidada le faltan campos numéricos.
      const asignaturaObj = this.listaMaterias.find((m) => m.id === base.id);
      const profesorObj = this.listaProfesoresObj.find((p) => p.id === base.docenteId);
      const salonObj = this.listaSalonesObj.find((sl) => sl.id === base.salonId);
      if (!asignaturaObj || !profesorObj || !salonObj) {
        errores++;
        pendientes--;
        this.mostrarToast(
          'error',
          `${base.nombre}: no se encontró la asignatura, el docente o el salón. Recargue la página.`,
          10000,
        );
        finalizar();
        return;
      }

      const grupoPayload = {
        numeroGrupo: Number(base.numeroGrupo) || 1,
        capacidadMinima: MINIMO_ESTUDIANTES,
        capacidadMaxima: Number(base.numEstudiantes) || 30,
        estado: 'ACTIVO',
        periodo: this.periodoSeleccionado,
        inscritos: 0,
        asignatura: asignaturaObj,
        profesor: profesorObj,
        salon: salonObj,
        sesiones: clases.map((c) => {
          const inicioMin = this.rango(c.hora!)[0];
          return {
            dia: c.dia,
            horaInicio: this.aTexto(inicioMin),
            horaFin: this.aTexto(inicioMin + HORAS_POR_CLASE * 60),
          };
        }),
      };

      this.grupoService.crearGrupo(this.sinNulos(grupoPayload) as any).subscribe({
        next: () => {
          exitosos++;
          pendientes--;
          finalizar();
        },
        error: (err) => {
          console.error('Error al guardar grupo:', err);
          errores++;
          pendientes--;
          const mensajeBackend = typeof err.error === 'string' ? err.error : this.mensajeError(err);

          this.mostrarToast(
            'error',`No se pudo guardar ${base.nombre}: ${mensajeBackend}`,
          10000
        );

          finalizar();
        },
      });
    });
  }

  /** Quita null / NaN de un objeto (el back rechaza null en campos numéricos primitivos) */
  /** Elimina propiedades nulas del objeto antes de enviarlo al backend. */
  private sinNulos<T>(obj: T): T {
    return JSON.parse(
      JSON.stringify(obj, (_k, v) =>
        v === null || (typeof v === 'number' && isNaN(v)) ? undefined : v,
      ),
    );
  }

  /* ===================== NAVEGACIÓN / SESIÓN ===================== */

  /** Navega a la ruta indicada. */
  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  /** Alterna el menú de usuario y evita propagar el clic. */
  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario = !this.mostrarMenuUsuario;
  }

  /** Cierra la sesión y vuelve a la pantalla de acceso. */
  cerrarSesion(): void {
    this.mostrarMenuUsuario = false;
    this.sesion.cerrarSesion();
    this.router.navigate(['/loginn']);
  }

  @HostListener('document:click')
  /** Cierra el menú de usuario al hacer clic fuera de él. */
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario) {
      this.mostrarMenuUsuario = false;
    }
  }
}
