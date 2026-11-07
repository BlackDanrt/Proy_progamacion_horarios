import { Component, HostListener, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SesionService } from '../services/sesion.service';
import { AsignaturaService } from '../services/asignatura.service';
import { SalonService } from '../services/salon.service';
import { ProfesorService } from '../services/profesor.service';
import { GrupoService } from '../services/grupo.service';
import { AsignaturaModel } from '../models/asignatura.model';
import { ProfesorModel } from '../models/profesor.model';
import { SalonModel } from '../models/salon.model';

/** Reglas institucionales de horario */
const ALMUERZO_INICIO = '12:00';
const ALMUERZO_FIN = '13:00';
const SABADO_FIN = '13:00';                 // el sábado solo hay clases hasta la 1:00 PM
const HORAS_POR_CLASE = 2;                  // cada clase dura 2 h
const MIN_POR_BLOQUE = 60;                  // cada fila del calendario es de 1 h
const BLOQUES_POR_CLASE = (HORAS_POR_CLASE * 60) / MIN_POR_BLOQUE;   // una clase ocupa 2 filas seguidas

/** Reglas de periodos académicos */
const PREFIJO_INTERSEMESTRAL = 'Intersemestral';
const ANIOS_ADELANTE_PERMITIDOS = 1;        // año actual y el siguiente (hoy: 2026 y 2027)
const CLAVE_PERIODOS_STORAGE = 'horarios.periodosCreados';

export interface AsignaturaUI {
  id?: number;
  grupoId?: number;
  nombre: string;
  aula: string;
  bloque: string;
  docente: string;
  docenteId?: number;
  salonId?: number;
  numeroGrupo?: number;
  color?: string;
  bgLight?: string;
  numEstudiantes?: number;
  persistido?: boolean;    // true = ya existe en la base de datos
  dia?: string;
  hora?: string;           // bloque de INICIO, ej. '07:00 - 09:00'
}

export interface BloqueCalendario {
  nombre: string;
  clave: string;
}

export type TipoToast = 'exito' | 'error' | 'advertencia' | 'info';

export interface Toast {
  id: number;
  tipo: TipoToast;
  mensaje: string;
}

@Component({
  selector: 'app-asignacion-horarios',
  standalone: false,
  templateUrl: './asignacion-horarios.html',
  styleUrls: ['./asignacion-horarios.css']
})
export class AsignacionHorarios implements OnInit {

  private asignaturaService = inject(AsignaturaService);
  private salonService = inject(SalonService);
  private profesorService = inject(ProfesorService);
  private grupoService = inject(GrupoService);
  private router = inject(Router);
  private sesion = inject(SesionService);

  // Modales
  mostrarModalProfesor: boolean = false;     // elegir profesor
  mostrarModalAsignatura: boolean = false;   // vincular materia libre
  mostrarModalPeriodo: boolean = false;      // crear periodo
  mostrarMenuUsuario: boolean = false;

  // Notificaciones visuales (reemplazan a alert / confirm)
  toasts: Toast[] = [];
  private toastSeq = 0;
  confirmacion: { titulo: string; mensaje: string; textoAceptar: string } | null = null;
  private accionAceptar?: () => void;
  private accionCancelar?: () => void;

  listaMaterias: AsignaturaModel[] = [];
  listaProfesoresObj: ProfesorModel[] = [];
  listaSalonesObj: SalonModel[] = [];

  listaProfesores: string[] = [];
  listaBloques: string[] = [];
  salonesDisponibles: string[] = [];
  mapaSalones: { [key: string]: string[] } = {};

  /** IDs de asignaturas que ya tienen un profesor vinculado en el periodo seleccionado */
  idsMateriasConProfesor: Set<number> = new Set<number>();

  // Filtros de Docente y Periodo Académico
  docenteSeleccionFiltro: string = '';
  docenteActual: string = '';                // docente cuyo horario está cargado

  anioActual: number = new Date().getFullYear();
  periodoSeleccionado: string = `${new Date().getFullYear()}-1`;
  periodoActual: string = this.periodoSeleccionado;   // periodo cuyo horario está cargado
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
    { nombre: 'SÁBADO', clave: 'SABADO' }
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

  ngOnInit(): void {
    this.inicializarPeriodos();
    this.cargarDatosBackend();
  }

  /* ===================== NOTIFICACIONES VISUALES ===================== */

  mostrarToast(tipo: TipoToast, mensaje: string, duracionMs: number = 6000): void {
    const id = ++this.toastSeq;
    this.toasts.push({ id, tipo, mensaje });
    setTimeout(() => this.cerrarToast(id), duracionMs);
  }

  cerrarToast(id: number): void {
    this.toasts = this.toasts.filter(t => t.id !== id);
  }

  pedirConfirmacion(titulo: string, mensaje: string, onAceptar: () => void, onCancelar?: () => void,
                    textoAceptar: string = 'Continuar'): void {
    this.confirmacion = { titulo, mensaje, textoAceptar };
    this.accionAceptar = onAceptar;
    this.accionCancelar = onCancelar;
  }

  aceptarConfirmacion(): void {
    const accion = this.accionAceptar;
    this.confirmacion = null;
    this.accionAceptar = undefined;
    this.accionCancelar = undefined;
    if (accion) accion();
  }

  cancelarConfirmacion(): void {
    const accion = this.accionCancelar;
    this.confirmacion = null;
    this.accionAceptar = undefined;
    this.accionCancelar = undefined;
    if (accion) accion();
  }

  /** Obtiene un mensaje corto y legible de un error HTTP (sin volcar el stack trace) */
  private mensajeError(err: any): string {
    let m: any = typeof err?.error === 'string' ? err.error : (err?.error?.message || err?.message || '');
    if (!m) m = `Error ${err?.status ?? ''} al comunicarse con el servidor.`;
    m = String(m).split(/\r?\n/)[0].trim();
    return m.length > 280 ? m.substring(0, 280) + '…' : m;
  }

  /* ===================== PERIODOS ACADÉMICOS ===================== */

  private inicializarPeriodos(): void {
    this.aniosPermitidos = [];
    for (let i = 0; i <= ANIOS_ADELANTE_PERMITIDOS; i++) {
      this.aniosPermitidos.push(this.anioActual + i);
    }

    const base = [
      `${this.anioActual}-1`,
      `${this.anioActual}-2`,
      `${PREFIJO_INTERSEMESTRAL} ${this.anioActual}-1`,
      `${PREFIJO_INTERSEMESTRAL} ${this.anioActual}-2`
    ];
    let guardados: string[] = [];
    try {
      guardados = JSON.parse(localStorage.getItem(CLAVE_PERIODOS_STORAGE) || '[]');
    } catch { guardados = []; }

    this.listaPeriodos = [];
    [...base, ...guardados].forEach(p => this.agregarPeriodoSiNoExiste(p));
    this.ordenarPeriodos();
  }

  /** Valida el formato: 2027-1 | 2027-2 | Intersemestral 2027-1 | Intersemestral 2027-2 */
  private formatoPeriodoValido(p: string): boolean {
    return new RegExp(`^(${PREFIJO_INTERSEMESTRAL} )?\\d{4}-[12]$`).test(p);
  }

  private agregarPeriodoSiNoExiste(p: string): void {
    if (p && this.formatoPeriodoValido(p) && !this.existePeriodo(p)) {
      this.listaPeriodos.push(p);
    }
  }

  existePeriodo(p: string): boolean {
    return this.listaPeriodos.some(x => x.toLowerCase() === p.toLowerCase());
  }

  private clavePeriodo(p: string): number {
    const inter = p.startsWith(PREFIJO_INTERSEMESTRAL) ? 1 : 0;
    const m = p.match(/(\d{4})-([12])$/);
    const anio = m ? parseInt(m[1]) : 0;
    const sem = m ? parseInt(m[2]) : 0;
    return anio * 100 + sem * 10 + inter;
  }

  private ordenarPeriodos(): void {
    this.listaPeriodos.sort((a, b) => this.clavePeriodo(a) - this.clavePeriodo(b));
  }

  abrirModalPeriodo(): void {
    this.tipoPeriodoModal = 'SEMESTRAL';
    this.anioPeriodoModal = this.anioActual;
    this.semestrePeriodoModal = 1;
    this.mostrarModalPeriodo = true;
  }

  cerrarModalPeriodo(): void {
    this.mostrarModalPeriodo = false;
  }

  get nombrePeriodoModal(): string {
    const base = `${this.anioPeriodoModal}-${this.semestrePeriodoModal}`;
    return this.tipoPeriodoModal === 'INTERSEMESTRAL' ? `${PREFIJO_INTERSEMESTRAL} ${base}` : base;
  }

  crearPeriodo(): void {
    const nombre = this.nombrePeriodoModal;
    const anio = Number(this.anioPeriodoModal);
    const sem = Number(this.semestrePeriodoModal);

    if (!this.aniosPermitidos.includes(anio)) {
      this.mostrarToast('error', `Solo se pueden crear periodos del ${this.aniosPermitidos[0]} al ${this.aniosPermitidos[this.aniosPermitidos.length - 1]}.`);
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
    } catch { /* almacenamiento no disponible: el periodo vive solo en esta sesión */ }
  }

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
        () => { this.periodoSeleccionado = this.periodoActual; }
      );
    } else {
      aplicar();
    }
  }

  /* ===================== UTILIDADES DE HORA ===================== */

  private aMin(h: string): number {
    const [hh, mm] = h.trim().split(':').map(Number);
    return hh * 60 + (mm || 0);
  }

  private aTexto(min: number): string {
    const hh = String(Math.floor(min / 60)).padStart(2, '0');
    const mm = String(min % 60).padStart(2, '0');
    return `${hh}:${mm}:00`;
  }

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
  private bloquesQueOcupa(hora: string): string[] | null {
    const idx = this.horasBloques.indexOf(hora);
    if (idx < 0) return null;
    const bloques = this.horasBloques.slice(idx, idx + BLOQUES_POR_CLASE);
    return bloques.length === BLOQUES_POR_CLASE ? bloques : null;
  }

  /** Valida que NINGUNA de las filas que ocupará la clase sea almuerzo ni esté cerrada */
  private validarBloque(dia: string, hora: string): string | null {
    const bloques = this.bloquesQueOcupa(hora);
    if (!bloques) {
      return `La clase dura ${HORAS_POR_CLASE} horas y no cabe en el calendario a partir de ese horario.`;
    }
    if (bloques.some(b => this.esAlmuerzo(b))) {
      return `La clase de ${HORAS_POR_CLASE} horas se cruzaría con el almuerzo (${ALMUERZO_INICIO} - ${ALMUERZO_FIN}).`;
    }
    if (bloques.some(b => this.esCerrado(dia, b))) {
      return 'El sábado solo se pueden asignar clases hasta la 1:00 PM.';
    }
    return null;
  }

  /** Busca una clase que se solape con la nueva (ignorando la que se está moviendo) */
  private buscarCruce(dia: string, hora: string, ignorar: { dia: string; hora: string } | null): AsignaturaUI | undefined {
    const ini = this.rango(hora)[0];
    const fin = ini + HORAS_POR_CLASE * 60;
    return this.clasesAsignadas.find(c => {
      if (c.dia !== dia || !c.hora) return false;
      if (ignorar && c.dia === ignorar.dia && c.hora === ignorar.hora) return false;
      const cIni = this.rango(c.hora)[0];
      const cFin = cIni + HORAS_POR_CLASE * 60;
      return cIni < fin && ini < cFin;
    });
  }

  /* ===================== CARGA DE DATOS ===================== */

  cargarDatosBackend(): void {
    this.asignaturaService.mostrarTodo().subscribe({
      next: (response) => { if (response.body) this.listaMaterias = response.body; },
      error: (err) => console.error('Error al cargar asignaturas:', err)
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
          this.listaBloques = Array.from(bloquesSet);
        }
      },
      error: (err) => console.error('Error al cargar salones:', err)
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
        }
      },
      error: (err) => console.error('Error al cargar profesores:', err)
    });

    this.cargarGruposGlobales();
  }

  /**
   * Consulta todos los grupos para saber qué asignaturas ya tienen profesor en el periodo
   * seleccionado y, de paso, descubre los periodos que ya existen en la base de datos.
   */
  cargarGruposGlobales(): void {
    this.grupoService.mostrarTodo().subscribe({
      next: (response: any) => {
        const grupos: any[] = response?.body && response.status !== 204 ? response.body : [];

        // Periodos que ya existen en el backend
        let huboNuevos = false;
        grupos.forEach(g => {
          if (g.periodo && this.formatoPeriodoValido(g.periodo) && !this.existePeriodo(g.periodo)) {
            this.listaPeriodos.push(g.periodo);
            huboNuevos = true;
          }
        });
        if (huboNuevos) this.ordenarPeriodos();

        const ids = grupos
          .filter(g => g.profesor?.id && (!this.periodoSeleccionado || g.periodo === this.periodoSeleccionado))
          .map(g => g.asignatura?.id)
          .filter(id => id !== undefined && id !== null);
        this.idsMateriasConProfesor = new Set<number>(ids);
      },
      error: (err: any) => console.error('Error al cargar grupos:', err)
    });
  }

  /** Materias que NO tienen profesor vinculado (ni guardado ni pendiente en pantalla) */
  get materiasSinProfesor(): AsignaturaModel[] {
    return this.listaMaterias.filter(m =>
      !this.idsMateriasConProfesor.has(m.id as number) &&
      !this.asignaturasDisponibles.some(a => a.id === m.id)
    );
  }

  /* ===================== CAMBIO DE DOCENTE ===================== */

  hayCambiosSinGuardar(): boolean {
    return this.clasesAsignadas.some(c => !c.persistido) ||
      this.asignaturasDisponibles.some(a => !a.persistido);
  }

  /** Cambia de profesor (con confirmación si hay cambios sin guardar) y carga sus materias */
  private cambiarDocente(nombre: string): void {
    if (!nombre) {
      this.docenteActual = '';
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
        () => { this.docenteSeleccionFiltro = this.docenteActual; }
      );
    } else {
      aplicar();
    }
  }

  onDocenteFiltroChange(): void {
    this.cambiarDocente(this.docenteSeleccionFiltro);
  }

  cargarHorarioDocente(nombreDocente: string): void {
    const profObj = this.listaProfesoresObj.find(p => `${p.nombre} ${p.apellido}` === nombreDocente);
    if (!profObj || !profObj.id) return;

    this.grupoService.buscarPorProfesor(profObj.id).subscribe({
      next: (response) => {
        this.asignaturasDisponibles = [];
        this.clasesAsignadas = [];

        if (response.body && response.status !== 204) {
          const grupos = response.body.filter(g => !this.periodoSeleccionado || g.periodo === this.periodoSeleccionado);
          const coloresPill = [
            { color: '#16a34a', bgLight: '#dcfce7' },
            { color: '#2563eb', bgLight: '#dbeafe' },
            { color: '#9333ea', bgLight: '#f3e8ff' },
            { color: '#ea580c', bgLight: '#ffedd5' }
          ];

          grupos.forEach((grupo, index) => {
            const col = coloresPill[index % coloresPill.length];
            const aulaStr = grupo.salon ? `${grupo.salon.bloque}${grupo.salon.numeroSalon}` : 'Por asignar';

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
              persistido: true
            };

            this.asignaturasDisponibles.push(asigUI);

            (grupo.sesiones || []).forEach(sesion => {
              const ini = sesion.horaInicio.substring(0, 5);
              const fin = sesion.horaFin.substring(0, 5);
              const bloqueInicio = this.horasBloques.find(b => b.startsWith(ini));
              if (!bloqueInicio) {
                console.warn(`La sesión ${ini}-${fin} no coincide con ningún bloque del calendario.`);
                return;
              }
              this.clasesAsignadas.push({
                ...asigUI,
                dia: sesion.dia,
                hora: bloqueInicio
              });
            });
          });
        }
      },
      error: (err) => {
        console.error('Error al consultar horario:', err);
        this.asignaturasDisponibles = [];
        this.clasesAsignadas = [];
        this.mostrarToast('error', 'No se pudo cargar el horario del docente.');
      }
    });
  }

  /* ===================== MODAL: ELEGIR PROFESOR ===================== */

  abrirModalProfesor(): void {
    this.docenteSeleccionadoModal = this.docenteActual || '';
    this.mostrarModalProfesor = true;
  }

  cerrarModalProfesor(): void {
    this.mostrarModalProfesor = false;
  }

  /** Continúa con el profesor elegido: carga sus materias y horario de una vez */
  continuarConDocente(): void {
    if (!this.docenteSeleccionadoModal) {
      this.mostrarToast('advertencia', 'Seleccione un profesor para continuar.');
      return;
    }
    this.mostrarModalProfesor = false;
    this.cambiarDocente(this.docenteSeleccionadoModal);
  }

  /* ===================== MODAL: VINCULAR MATERIA LIBRE (+) ===================== */

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
    this.salonesDisponibles = [];
    this.cargarGruposGlobales(); // refresca qué materias siguen libres
    this.mostrarModalAsignatura = true;
  }

  cerrarModalCrear(): void {
    this.mostrarModalAsignatura = false;
  }

  onBloqueChange(): void {
    if (this.bloqueSeleccionadoModal && this.mapaSalones[this.bloqueSeleccionadoModal]) {
      this.salonesDisponibles = this.mapaSalones[this.bloqueSeleccionadoModal];
    } else {
      this.salonesDisponibles = [];
    }
    this.salonSeleccionadoModal = '';
  }

  agregarNuevaAsignatura(): void {
    if (!this.docenteActual || !this.materiaSeleccionadaModal || !this.bloqueSeleccionadoModal || !this.salonSeleccionadoModal || !this.numeroGrupoModal) {
      this.mostrarToast('advertencia', 'Complete todos los campos obligatorios.');
      return;
    }

    // La materia debe seguir sin profesor
    if (!this.materiasSinProfesor.some(m => m.id === this.materiaSeleccionadaModal?.id)) {
      this.mostrarToast('error', 'Esta asignatura ya tiene un profesor vinculado.');
      return;
    }

    const CAPACIDAD_MINIMA_INSTITUCIONAL = 10;
    if (this.numEstudiantesModal < CAPACIDAD_MINIMA_INSTITUCIONAL) {
      this.mostrarToast('error', `No se puede abrir un grupo con ${this.numEstudiantesModal} estudiante(s). Mínimo requerido: ${CAPACIDAD_MINIMA_INSTITUCIONAL}.`);
      return;
    }

    const profObj = this.listaProfesoresObj.find(p => `${p.nombre} ${p.apellido}` === this.docenteActual);
    const bloqueLetra = this.salonSeleccionadoModal.charAt(0) as any;
    const numSalonVal = parseInt(this.salonSeleccionadoModal.slice(1));
    const salonObj = this.listaSalonesObj.find(s => s.bloque === bloqueLetra && s.numeroSalon === numSalonVal);

    if (!salonObj || !this.materiaSeleccionadaModal) {
      this.mostrarToast('error', 'No se pudo identificar el salón o la asignatura.');
      return;
    }

    if (this.materiaSeleccionadaModal.requiereComputadores && !salonObj.tieneComputadores) {
      this.mostrarToast('error', 'La asignatura requiere equipos de cómputo y el salón no cuenta con ellos.');
      return;
    }

    if (this.materiaSeleccionadaModal.requiereSillasMoviles && !salonObj.tieneSillasMoviles) {
      this.mostrarToast('error', 'La asignatura requiere sillas móviles y el salón no las posee.');
      return;
    }

    if (this.numEstudiantesModal > salonObj.capacidad) {
      this.mostrarToast('error', `Supera la capacidad máxima del salón (${salonObj.capacidad}).`);
      return;
    }

    const coloresPill = [
      { color: '#16a34a', bgLight: '#dcfce7' },
      { color: '#2563eb', bgLight: '#dbeafe' },
      { color: '#9333ea', bgLight: '#f3e8ff' },
      { color: '#ea580c', bgLight: '#ffedd5' }
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
      persistido: false
    });

    this.mostrarModalAsignatura = false;
    this.mostrarToast('info', 'Grupo vinculado. Arrástrelo al calendario para asignarle el horario.');
  }

  /* ===================== DRAG & DROP ===================== */

  onDragStartAsignatura(event: DragEvent, asig: AsignaturaUI): void {
    event.dataTransfer?.setData('text/plain', JSON.stringify(asig));
  }

  onDragStartClase(event: DragEvent, dia: string, hora: string): void {
    const clase = this.getClase(dia, hora);
    if (clase) {
      event.dataTransfer?.setData('text/plain', JSON.stringify(clase));
    }
  }

  onDragOver(event: DragEvent, dia: string, hora: string): void {
    event.preventDefault();
    this.celdaActiva = `${dia}|${hora}`;
  }

  onDragLeave(): void {
    this.celdaActiva = null;
  }

  onDrop(event: DragEvent, dia: string, hora: string): void {
    event.preventDefault();
    this.celdaActiva = null;

    const data = event.dataTransfer?.getData('text/plain');
    if (!data) return;

    const asignatura: AsignaturaUI = JSON.parse(data);

    if (asignatura.persistido) {
      this.mostrarToast('advertencia', 'Este grupo ya está guardado en el sistema y no se puede modificar desde aquí.');
      return;
    }

    // 1) Almuerzo / sábado
    const errorBloques = this.validarBloque(dia, hora);
    if (errorBloques) {
      this.mostrarToast('error', errorBloques);
      return;
    }

    // 2) Cruces (ignorando la clase que se está moviendo)
    const origen = asignatura.dia && asignatura.hora ? { dia: asignatura.dia, hora: asignatura.hora } : null;

    // 2.1) Las sesiones de un mismo grupo deben ser en días distintos
    const mismoDia = this.clasesAsignadas.some(c =>
      c.id === asignatura.id && c.numeroGrupo === asignatura.numeroGrupo && c.dia === dia &&
      !(origen && c.dia === origen.dia && c.hora === origen.hora));
    if (mismoDia) {
      this.mostrarToast('error', 'Las sesiones de un mismo grupo deben ser en días distintos.');
      return;
    }
    const cruce = this.buscarCruce(dia, hora, origen);
    if (cruce) {
      this.mostrarToast('error', `Cruce de horario: el docente ya tiene "${cruce.nombre}" en ese horario.`);
      return;
    }

    // 3) Si venía del calendario, se quita de su posición anterior
    if (origen) {
      this.quitarClase(origen.dia, origen.hora);
    }

    this.clasesAsignadas.push({ ...asignatura, dia, hora });
  }

  /** true si la celda no admite clases (sábado después de la 1:00 PM) */
  esCerrado(dia: string, hora: string): boolean {
    return dia === 'SABADO' && this.rango(hora)[1] > this.aMin(SABADO_FIN);
  }

  /** Clase que INICIA en esta celda */
  getClase(dia: string, hora: string): AsignaturaUI | null {
    return this.clasesAsignadas.find(c => c.dia === dia && c.hora === hora) || null;
  }

  /** Clase que empezó en la fila anterior y sigue ocupando esta celda (su 2.ª hora) */
  getClaseContinuacion(dia: string, hora: string): AsignaturaUI | null {
    const ini = this.rango(hora)[0];
    return this.clasesAsignadas.find(c => {
      if (c.dia !== dia || !c.hora) return false;
      const cIni = this.rango(c.hora)[0];
      return cIni < ini && ini < cIni + HORAS_POR_CLASE * 60;
    }) || null;
  }

  quitarClase(dia: string, hora: string): void {
    this.clasesAsignadas = this.clasesAsignadas.filter(c => !(c.dia === dia && c.hora === hora));
  }

  /** Botón × del calendario */
  quitarClaseDesdeCalendario(dia: string, hora: string): void {
    const c = this.getClase(dia, hora);
    if (c?.persistido) {
      this.mostrarToast('advertencia', 'Este grupo ya está guardado en el sistema y no se puede quitar desde aquí.');
      return;
    }
    this.quitarClase(dia, hora);
  }

  /* ===================== GUARDAR ===================== */

  guardarCambios(): void {
    const nuevas = this.clasesAsignadas.filter(c => !c.persistido);
    const gruposSinUbicar = this.asignaturasDisponibles.filter(a =>
      !a.persistido && !nuevas.some(c => c.id === a.id && c.numeroGrupo === a.numeroGrupo)
    );

    if (nuevas.length === 0) {
      this.mostrarToast('advertencia', 'No hay clases nuevas en el calendario para guardar.');
      return;
    }

    // Un grupo = un registro, con todas sus sesiones
    const porGrupo = new Map<string, AsignaturaUI[]>();
    nuevas.forEach(c => {
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
        this.mostrarToast('exito', `${exitosos} grupo(s) guardado(s) con éxito en el periodo ${this.periodoSeleccionado}.`);
      }
      if (errores === 0 && gruposSinUbicar.length > 0) {
        this.mostrarToast('info', `${gruposSinUbicar.length} grupo(s) no se guardaron porque no tienen clase en el calendario.`);
      }
      this.cargarGruposGlobales();
      // Solo se recarga el horario si todo salió bien; si hubo errores se conservan los cambios pendientes
      if (errores === 0 && this.docenteActual) this.cargarHorarioDocente(this.docenteActual);
    };

    porGrupo.forEach(clases => {
      const base = clases[0];

      // Regla del backend: con 3 sesiones o menos, no pueden ser en días consecutivos
      if (clases.length <= 3) {
        const idxDias = clases.map(c => this.dias.findIndex(d => d.clave === c.dia)).sort((a, b) => a - b);
        const consecutivos = idxDias.some((v, i) => i > 0 && v - idxDias[i - 1] === 1);
        if (consecutivos) {
          errores++;
          pendientes--;
          this.mostrarToast('error', `${base.nombre}: con dos o tres sesiones no pueden ser en días consecutivos.`, 10000);
          finalizar();
          return;
        }
      }

      const grupoPayload = {
        numeroGrupo: base.numeroGrupo || 1,
        capacidadMinima: 10,
        capacidadMaxima: base.numEstudiantes || 30,
        estado: 'ACTIVO',
        periodo: this.periodoSeleccionado,
        inscritos: 0,
        asignatura: { id: base.id },
        profesor: { id: base.docenteId },
        salon: { id: base.salonId },
        sesiones: clases.map(c => {
          const inicioMin = this.rango(c.hora!)[0];
          return {
            dia: c.dia,
            horaInicio: this.aTexto(inicioMin),
            horaFin: this.aTexto(inicioMin + HORAS_POR_CLASE * 60)
          };
        })
      };

      this.grupoService.crearGrupo(grupoPayload as any).subscribe({
        next: () => { exitosos++; pendientes--; finalizar(); },
        error: (err) => {
          console.error('Error al guardar grupo:', err);
          errores++;
          pendientes--;
          this.mostrarToast('error', `No se pudo guardar ${base.nombre}: ${this.mensajeError(err)}`, 10000);
          finalizar();
        }
      });
    });
  }

  /* ===================== NAVEGACIÓN / SESIÓN ===================== */

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
