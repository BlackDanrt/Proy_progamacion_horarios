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
const HORAS_POR_BLOQUE = 2; // cada fila del calendario dura 2 h
const DURACIONES_PERMITIDAS = [2, 4]; // una clase dura 2 h (1 bloque) o 4 h (2 bloques seguidos)

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
  duracion?: number; // 2 o 4 horas
  persistido?: boolean; // true = ya existe en la base de datos
  dia?: string;
  hora?: string; // bloque de INICIO, ej. '07:00 - 09:00'
}

export interface BloqueCalendario {
  nombre: string;
  fecha: string;
  clave: string;
}

@Component({
  selector: 'app-asignacion-horarios',
  standalone: false,
  templateUrl: './asignacion-horarios.html',
  styleUrls: ['./asignacion-horarios.css'],
})
export class AsignacionHorarios implements OnInit {
  private asignaturaService = inject(AsignaturaService);
  private salonService = inject(SalonService);
  private profesorService = inject(ProfesorService);
  private grupoService = inject(GrupoService);
  private router = inject(Router);
  private sesion = inject(SesionService);

  // Modales
  mostrarModalProfesor: boolean = false; // paso 1: elegir profesor
  mostrarModalAsignatura: boolean = false; // paso 2: vincular materia libre
  mostrarMenuUsuario: boolean = false;

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
  docenteActual: string = ''; // docente cuyo horario está cargado
  periodoSeleccionado: string = '2026-1';

  listaPeriodos: string[] = ['2026-1', '2026-2', 'Intersemestral 2026-1', 'Intersemestral 2026-2'];

  // Campos de los modales
  docenteSeleccionadoModal: string = '';
  materiaSeleccionadaModal: AsignaturaModel | null = null;
  numeroGrupoModal: number = 1;
  bloqueSeleccionadoModal: string = '';
  salonSeleccionadoModal: string = '';
  numEstudiantesModal: number = 30;
  duracionModal: number = 2;
  duracionesPermitidas: number[] = DURACIONES_PERMITIDAS;

  asignaturasDisponibles: AsignaturaUI[] = [];
  clasesAsignadas: AsignaturaUI[] = [];
  celdaActiva: string | null = null;

  fechaBaseSemana: Date = new Date(2026, 4, 12);
  textoRangoSemana: string = '12–16 de mayo, 2026';
  dias: BloqueCalendario[] = [];

  textoAlmuerzo: string = `Almuerzo ${ALMUERZO_INICIO} - ${ALMUERZO_FIN} · no se pueden asignar clases`;

  /**
   * Bloques del calendario. El bloque 11:00 - 13:00 se cruza con el almuerzo (12:00 - 13:00),
   * por eso se pinta deshabilitado automáticamente (ver esAlmuerzo()).
   */
  horasBloques: string[] = [
    '07:00 - 09:00',
    '09:00 - 11:00',
    '11:00 - 13:00',
    '14:00 - 16:00',
    '16:00 - 18:00',
    '18:00 - 20:00',
    '20:00 - 22:00',
  ];

  ngOnInit(): void {
    this.calcularFechasSemana();
    this.cargarDatosBackend();
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

  /** Cuántos bloques ocupa una clase según su duración */
  bloquesDe(duracion?: number): number {
    return Math.ceil((duracion || HORAS_POR_BLOQUE) / HORAS_POR_BLOQUE);
  }

  /** Valida que la clase quepa desde el bloque idx sin tocar almuerzo y en bloques consecutivos */
  private validarBloques(idx: number, n: number): string | null {
    if (idx < 0 || idx + n > this.horasBloques.length) {
      return 'La clase no cabe en los bloques disponibles de ese día.';
    }
    for (let k = idx; k < idx + n; k++) {
      if (this.esAlmuerzo(this.horasBloques[k])) {
        return `Una clase no puede ocupar la hora de almuerzo (${ALMUERZO_INICIO} - ${ALMUERZO_FIN}).`;
      }
      if (
        k > idx &&
        this.rango(this.horasBloques[k])[0] !== this.rango(this.horasBloques[k - 1])[1]
      ) {
        return 'Una clase de 4 horas debe ocupar dos bloques consecutivos, sin saltos entre ellos.';
      }
    }
    return null;
  }

  /** Busca una clase que se solape con [idx, idx+n) en ese día */
  private buscarCruce(
    dia: string,
    idx: number,
    n: number,
    ignorar: { dia: string; hora: string } | null,
  ): AsignaturaUI | undefined {
    return this.clasesAsignadas.find((c) => {
      if (c.dia !== dia || !c.hora) return false;
      if (ignorar && c.dia === ignorar.dia && c.hora === ignorar.hora) return false;
      const ci = this.horasBloques.indexOf(c.hora);
      const cn = this.bloquesDe(c.duracion);
      return idx < ci + cn && ci < idx + n;
    });
  }

  /* ===================== CALENDARIO ===================== */

  calcularFechasSemana(): void {
    const clavesDias = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO'];
    const nombresDias = ['LUNES', 'MARTES', 'MIÉRCOLES', 'JUEVES', 'VIERNES', 'SABADO'];

    const curr = new Date(this.fechaBaseSemana);
    const firstDay = curr.getDate() - curr.getDay() + (curr.getDay() === 0 ? -6 : 1);
    const lunes = new Date(curr.setDate(firstDay));

    this.dias = [];
    const meses = [
      'enero',
      'febrero',
      'marzo',
      'abril',
      'mayo',
      'junio',
      'julio',
      'agosto',
      'septiembre',
      'octubre',
      'noviembre',
      'diciembre',
    ];

    for (let i = 0; i < 6; i++) {
      const d = new Date(lunes);
      d.setDate(lunes.getDate() + i);
      const diaStr = String(d.getDate()).padStart(2, '0');
      const mesStr = String(d.getMonth() + 1).padStart(2, '0');

      this.dias.push({
        nombre: nombresDias[i],
        fecha: `${diaStr}/${mesStr}`,
        clave: clavesDias[i],
      });
    }

    const inicioLunes = new Date(lunes);
    const finViernes = new Date(lunes);
    finViernes.setDate(lunes.getDate() + 4);

    this.textoRangoSemana = `${inicioLunes.getDate()}–${finViernes.getDate()} de ${meses[finViernes.getMonth()]} , ${finViernes.getFullYear()}`;
  }

  semanaAnterior(): void {
    this.fechaBaseSemana.setDate(this.fechaBaseSemana.getDate() - 7);
    this.calcularFechasSemana();
  }

  semanaSiguiente(): void {
    this.fechaBaseSemana.setDate(this.fechaBaseSemana.getDate() + 7);
    this.calcularFechasSemana();
  }

  /* ===================== CARGA DE DATOS ===================== */

  cargarDatosBackend(): void {
    this.asignaturaService.mostrarTodo().subscribe({
      next: (response) => {
        if (response.body) this.listaMaterias = response.body;
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
        }
      },
      error: (err) => console.error('Error al cargar profesores:', err),
    });

    this.cargarGruposGlobales();
  }

  /**
   * Consulta todos los grupos del periodo para saber qué asignaturas ya tienen profesor.
   * NOTA: se asume que GrupoService tiene mostrarTodo() devolviendo grupos con
   * { asignatura: {id}, profesor: {id}, periodo }. Ajusta el nombre si es distinto.
   */
  cargarGruposGlobales(): void {
    this.grupoService.mostrarTodo().subscribe({
      next: (response: any) => {
        const grupos: any[] = response?.body && response.status !== 204 ? response.body : [];
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
  get materiasSinProfesor(): AsignaturaModel[] {
    return this.listaMaterias.filter(
      (m) =>
        !this.idsMateriasConProfesor.has(m.id as number) &&
        !this.asignaturasDisponibles.some((a) => a.id === m.id),
    );
  }

  /* ===================== CAMBIO DE DOCENTE / PERIODO ===================== */

  hayCambiosSinGuardar(): boolean {
    return (
      this.clasesAsignadas.some((c) => !c.persistido) ||
      this.asignaturasDisponibles.some((a) => !a.persistido)
    );
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

    if (
      this.hayCambiosSinGuardar() &&
      !confirm('Hay cambios sin guardar. Si cambia de docente se perderán. ¿Desea continuar?')
    ) {
      this.docenteSeleccionFiltro = this.docenteActual;
      return;
    }
    this.docenteActual = nombre;
    this.docenteSeleccionFiltro = nombre;
    this.cargarHorarioDocente(nombre);
  }

  onDocenteFiltroChange(): void {
    this.cambiarDocente(this.docenteSeleccionFiltro);
  }

  onPeriodoChange(): void {
    this.cargarGruposGlobales();
    if (this.docenteActual) {
      this.cargarHorarioDocente(this.docenteActual);
    }
  }

  cargarHorarioDocente(nombreDocente: string): void {
    const profObj = this.listaProfesoresObj.find(
      (p) => `${p.nombre} ${p.apellido}` === nombreDocente,
    );
    if (!profObj || !profObj.id) return;

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

            // Duración del grupo: la de su primera sesión (por defecto 2 h)
            let duracionGrupo = HORAS_POR_BLOQUE;
            if (grupo.sesiones && grupo.sesiones.length > 0) {
              const s0 = grupo.sesiones[0];
              duracionGrupo =
                (this.aMin(s0.horaFin.substring(0, 5)) - this.aMin(s0.horaInicio.substring(0, 5))) /
                60;
            }

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
              duracion: duracionGrupo,
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
                duracion: (this.aMin(fin) - this.aMin(ini)) / 60,
              });
            });
          });
        }
      },
      error: (err) => {
        console.error('Error al consultar horario:', err);
        this.asignaturasDisponibles = [];
        this.clasesAsignadas = [];
      },
    });
  }

  /* ===================== MODAL 1: ELEGIR PROFESOR ===================== */

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
      alert('⚠️ Por favor seleccione un profesor para continuar.');
      return;
    }
    this.cambiarDocente(this.docenteSeleccionadoModal);
    this.mostrarModalProfesor = false;
  }

  /* ===================== MODAL 2: VINCULAR MATERIA LIBRE (+) ===================== */

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
    this.duracionModal = 2;
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
    if (
      !this.docenteActual ||
      !this.materiaSeleccionadaModal ||
      !this.bloqueSeleccionadoModal ||
      !this.salonSeleccionadoModal ||
      !this.numeroGrupoModal
    ) {
      alert('⚠️ Advertencia: Por favor complete todos los campos obligatorios.');
      return;
    }

    // La materia debe seguir sin profesor
    if (!this.materiasSinProfesor.some((m) => m.id === this.materiaSeleccionadaModal?.id)) {
      alert('⚠️ Esta asignatura ya tiene un profesor vinculado.');
      return;
    }

    if (!DURACIONES_PERMITIDAS.includes(this.duracionModal)) {
      alert('⚠️ La duración de la clase debe ser de 2 o 4 horas.');
      return;
    }

    const CAPACIDAD_MINIMA_INSTITUCIONAL = 10;
    if (this.numEstudiantesModal < CAPACIDAD_MINIMA_INSTITUCIONAL) {
      alert(
        `⚠️ Excepción de Negocio: No se puede abrir un grupo con ${this.numEstudiantesModal} estudiante(s). Mínimo requerido: ${CAPACIDAD_MINIMA_INSTITUCIONAL}.`,
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
      alert('⚠️ Error: No se pudo identificar el salón o la asignatura.');
      return;
    }

    if (this.materiaSeleccionadaModal.requiereComputadores && !salonObj.tieneComputadores) {
      alert(
        `⚠️ Excepción de Infraestructura: La asignatura requiere equipos de cómputo y el salón no cuenta con ellos.`,
      );
      return;
    }

    if (this.materiaSeleccionadaModal.requiereSillasMoviles && !salonObj.tieneSillasMoviles) {
      alert(
        `⚠️ Excepción de Infraestructura: La asignatura requiere sillas móviles y el salón no las posee.`,
      );
      return;
    }

    if (this.numEstudiantesModal > salonObj.capacidad) {
      alert(
        `⚠️ Excepción de Capacidad: Supera la capacidad máxima del salón (${salonObj.capacidad}).`,
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
      duracion: this.duracionModal,
      persistido: false,
    });

    this.mostrarModalAsignatura = false;
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
      alert('⚠️ Este grupo ya está guardado en el sistema y no se puede modificar desde aquí.');
      return;
    }

    const n = this.bloquesDe(asignatura.duracion);
    const idx = this.horasBloques.indexOf(hora);

    // 1) Almuerzo / bloques consecutivos
    const errorBloques = this.validarBloques(idx, n);
    if (errorBloques) {
      alert(`⚠️ ${errorBloques}`);
      return;
    }

    // 2) Cruces (ignorando la clase que se está moviendo)
    const origen =
      asignatura.dia && asignatura.hora ? { dia: asignatura.dia, hora: asignatura.hora } : null;
    const cruce = this.buscarCruce(dia, idx, n, origen);
    if (cruce) {
      alert(`⚠️ Excepción de cruce: el docente ya tiene "${cruce.nombre}" en ese horario.`);
      return;
    }

    // 3) Si venía del calendario, se quita de su posición anterior
    if (origen) {
      this.quitarClase(origen.dia, origen.hora);
    }

    this.clasesAsignadas.push({ ...asignatura, dia, hora });
  }

  /** Clase que INICIA en esta celda */
  getClase(dia: string, hora: string): AsignaturaUI | null {
    return this.clasesAsignadas.find((c) => c.dia === dia && c.hora === hora) || null;
  }

  /** true si la celda está cubierta por una clase de 4 h que empezó en el bloque anterior */
  esContinuacion(dia: string, hora: string): boolean {
    const idx = this.horasBloques.indexOf(hora);
    return this.clasesAsignadas.some((c) => {
      if (c.dia !== dia || !c.hora) return false;
      const ci = this.horasBloques.indexOf(c.hora);
      return idx > ci && idx < ci + this.bloquesDe(c.duracion);
    });
  }

  /** Filas que ocupa la celda (2 para clases de 4 h) */
  getRowspan(dia: string, hora: string): number {
    const c = this.getClase(dia, hora);
    return c ? this.bloquesDe(c.duracion) : 1;
  }

  quitarClase(dia: string, hora: string): void {
    this.clasesAsignadas = this.clasesAsignadas.filter((c) => !(c.dia === dia && c.hora === hora));
  }

  /** Botón × del calendario */
  quitarClaseDesdeCalendario(dia: string, hora: string): void {
    const c = this.getClase(dia, hora);
    if (c?.persistido) {
      alert('⚠️ Este grupo ya está guardado en el sistema y no se puede quitar desde aquí.');
      return;
    }
    this.quitarClase(dia, hora);
  }

  /* ===================== GUARDAR ===================== */

  guardarCambios(): void {
    const nuevas = this.clasesAsignadas.filter((c) => !c.persistido);
    const gruposSinUbicar = this.asignaturasDisponibles.filter(
      (a) => !a.persistido && !nuevas.some((c) => c.id === a.id && c.numeroGrupo === a.numeroGrupo),
    );

    if (nuevas.length === 0) {
      alert('⚠️ Advertencia: No hay clases nuevas en el calendario para guardar.');
      return;
    }

    // Un grupo = un registro, con todas sus sesiones (antes se creaba un grupo por cada celda)
    const porGrupo = new Map<string, AsignaturaUI[]>();
    nuevas.forEach((c) => {
      const key = `${c.id}|${c.numeroGrupo}`;
      if (!porGrupo.has(key)) porGrupo.set(key, []);
      porGrupo.get(key)!.push(c);
    });

    let pendientes = porGrupo.size;
    let errores = 0;

    const finalizar = () => {
      if (pendientes > 0) return;
      if (errores === 0) {
        let msg = `¡Grupos y horarios guardados con éxito para el periodo ${this.periodoSeleccionado}!`;
        if (gruposSinUbicar.length > 0) {
          msg += `\n\nNota: ${gruposSinUbicar.length} grupo(s) no se guardaron porque no tienen clase en el calendario.`;
        }
        alert(msg);
      }
      this.cargarGruposGlobales();
      if (this.docenteActual) this.cargarHorarioDocente(this.docenteActual);
    };

    porGrupo.forEach((clases) => {
      const base = clases[0];

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
        sesiones: clases.map((c) => {
          const inicioMin = this.rango(c.hora!)[0];
          return {
            dia: c.dia,
            horaInicio: this.aTexto(inicioMin),
            horaFin: this.aTexto(inicioMin + (c.duracion || HORAS_POR_BLOQUE) * 60),
          };
        }),
      };

      this.grupoService.crearGrupo(grupoPayload as any).subscribe({
        next: () => {
          pendientes--;
          finalizar();
        },
        error: (err) => {
          console.error('Error al guardar grupo:', err);
          errores++;
          pendientes--;
          alert(`⚠️ Excepción del Servidor (${base.nombre}):\n${err.error || err.message}`);
          finalizar();
        },
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
