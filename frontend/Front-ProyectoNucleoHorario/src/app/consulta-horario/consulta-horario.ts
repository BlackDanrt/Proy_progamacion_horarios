import { ChangeDetectorRef, Component, HostListener, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SesionService } from '../services/sesion.service';
import { GrupoService } from '../services/grupo.service';
import { GrupoModel } from '../models/grupo.model';

/** Clase del horario con sus datos de presentación y posición temporal. */
interface Clase {
  /** Día normalizado al que pertenece la clase. */
  dia: string;
  /** Minuto del día en que comienza la clase. */
  inicio: number; // minutos desde las 00:00
  /** Minuto del día en que termina la clase. */
  fin: number;
  /** Nombre de la asignatura. */
  nombre: string;
  /** Etiqueta del grupo al que pertenece la clase. */
  grupo: string;  // "Grupo 1"
  /** Salón y bloque asignados. */
  aula: string;   // "Aula 313 - Bloque B"
  /** Texto auxiliar con el grupo y el aula. */
  detalle: string; // "Grupo 1 · Aula 313 - Bloque B"
  /** Color del texto de la clase en el horario. */
  color: string;
  /** Color de fondo de la clase en el horario. */
  bg: string;
}

/** Información del docente cuyo horario se está consultando. */
interface DocenteInfo {
  /** Identificador del docente. */
  id: number;
  /** Nombre completo del docente. */
  nombre: string;
  /** Especialidades del docente presentadas como texto. */
  especialidad: string;
}

/** Fila de tiempo que se muestra en la cuadrícula del horario. */
interface FilaHora {
  /** Rango horario visible en la fila. */
  etiqueta: string;
  /** Minuto del día correspondiente al inicio de la fila. */
  inicio: number;
}

/** Colores alternos para distinguir asignaturas en el horario. */
const PALETA: { color: string; bg: string }[] = [
  { color: '#16a34a', bg: '#dcfce7' },
  { color: '#2563eb', bg: '#dbeafe' },
  { color: '#9333ea', bg: '#f3e8ff' },
  { color: '#f97316', bg: '#ffedd5' },
  { color: '#eab308', bg: '#fef9c3' },
  { color: '#0d9488', bg: '#ccfbf1' },
  { color: '#e11d48', bg: '#ffe4e6' },
  { color: '#0891b2', bg: '#cffafe' },
];

@Component({
  selector: 'app-consulta-horario',
  standalone: false,
  templateUrl: './consulta-horario.html',
  styleUrl: './consulta-horario.css',
})
/** Carga y presenta el horario semanal de un docente. */
export class ConsultaHorario implements OnInit {

  // true = administrador (ruta /consulta-horario), false = docente (ruta /mi-horario)
  /** Indica si la consulta se realiza desde la vista administrativa. */
  esAdministrador: boolean = true;

  /** Datos del docente consultado, o null si no se pudieron obtener. */
  docente: DocenteInfo | null = null;

  /** Indica si se están cargando los grupos del docente. */
  cargando: boolean = false;
  /** Si tiene valor, se muestra la ventana emergente con el aviso. */
  mensajeVacio: string | null = null;

  /** Días que forman la semana que se presenta en el horario. */
  dias: { clave: string; nombre: string; fecha: string }[] = [];
  /** Texto descriptivo del rango semanal. */
  semanaTexto: string = '';
  /** Filas horarias visibles en la cuadrícula. */
  horas: FilaHora[] = [];
  /** Clases programadas para la semana. */
  clases: Clase[] = [];
  /** Colores asociados a las asignaturas del horario. */
  leyenda: { nombre: string; color: string }[] = [];
  /** Total de cupos disponibles en los grupos del docente. */
  cuposDisponibles: number = 0;

  /** Cantidad de grupos (clases) asignados al docente. */
  totalClases: number = 0;

  /** Controla la visibilidad del menú de usuario. */
  mostrarMenuUsuario: boolean = false;

  private sesion = inject(SesionService);
  private grupoService = inject(GrupoService);
  private cdr = inject(ChangeDetectorRef);

  constructor(private route: ActivatedRoute, private router: Router) {}

  /** Inicializa la semana, las filas horarias y la consulta del docente. */
  ngOnInit(): void {
    // En /mi-horario entra el docente; en /consulta-horario entra el admin
    this.esAdministrador = this.route.snapshot.data['modo'] !== 'docente';
    this.construirSemana();
    this.horas = this.generarHoras(8 * 60, 16 * 60);

    this.route.queryParamMap.subscribe(params => {
      // Admin: id que llega por query param. Docente: su propio id de sesión.
      const id: number | null = this.esAdministrador
        ? Number(params.get('id')) || null
        : this.sesion.getProfesorId();

      if (!id) {
        this.mensajeVacio = 'No se pudo identificar al docente.';
        return;
      }
      this.cargarHorario(id);
    });
  }

  // ---------- carga de datos ----------

  /** Solicita al backend los grupos asignados al docente indicado. */
  private cargarHorario(idProfesor: number): void {
    this.cargando = true;
    this.mensajeVacio = null;
    this.cdr.markForCheck();

    this.grupoService.buscarPorProfesor(idProfesor).subscribe({
      next: res => {
        // El back responde 204 (sin cuerpo) cuando no hay grupos
        this.procesarGrupos(res.body ?? [], idProfesor);
        this.cargando = false;
        this.cdr.markForCheck(); // refresca la vista al llegar la respuesta
      },
      error: () => {
        this.procesarGrupos([], idProfesor);
        this.mensajeVacio = 'No se pudo cargar el horario. Intenta de nuevo más tarde.';
        this.cargando = false;
        this.cdr.markForCheck();
      },
    });
  }

  /** Convierte los grupos en clases, leyenda y datos para la vista. */
  private procesarGrupos(grupos: GrupoModel[], idProfesor: number): void {
    this.clases = [];
    this.leyenda = [];
    this.cuposDisponibles = 0;
    this.totalClases = grupos.length; // clases asignadas = grupos del docente

    if (grupos.length === 0) {
      this.docente = null;
      this.horas = this.generarHoras(8 * 60, 16 * 60);
      this.mensajeVacio = 'No hay grupos asignados.';
      return;
    }

    // Datos del docente a partir del primer grupo
    const p: any = grupos[0].profesor;
    this.docente = {
      id: idProfesor,
      nombre: `${p?.nombre ?? ''} ${p?.apellido ?? ''}`.trim(),
      especialidad: (p?.especialidades ?? []).join(', '),
    };

    const colores = new Map<string, { color: string; bg: string }>();

    for (const g of grupos) {
      const nombreAsig: string = g.asignatura?.nombre ?? 'Asignatura';

      if (!colores.has(nombreAsig)) {
        const c = PALETA[colores.size % PALETA.length];
        colores.set(nombreAsig, c);
        this.leyenda.push({ nombre: nombreAsig, color: c.color });
      }
      const paleta = colores.get(nombreAsig)!;

      const aula: string = g.salon
        ? `Aula ${g.salon.numeroSalon} - Bloque ${g.salon.bloque}`
        : 'Sin aula';

      this.cuposDisponibles += Math.max(0, (g.capacidadMaxima ?? 0) - (g.inscritos ?? 0));

      for (const s of g.sesiones ?? []) {
        this.clases.push({
          dia: this.normalizarDia(s.dia),
          inicio: this.aMinutos(s.horaInicio),
          fin: this.aMinutos(s.horaFin),
          nombre: nombreAsig,
          grupo: `Grupo ${g.numeroGrupo}`,
          aula,
          detalle: `Grupo ${g.numeroGrupo} · ${aula}`,
          color: paleta.color,
          bg: paleta.bg,
        });
      }
    }

    if (this.clases.length === 0) {
      this.mensajeVacio = 'Los grupos de este docente aún no tienen sesiones programadas.';
      return;
    }

    // Rango de horas: 08:00–16:00 como mínimo, ampliado si hay sesiones fuera de ese rango
    const minInicio = Math.min(8 * 60, ...this.clases.map(c => c.inicio));
    const maxFin = Math.max(16 * 60, ...this.clases.map(c => c.fin));
    this.horas = this.generarHoras(minInicio, maxFin);
  }

  // ---------- helpers ----------

  /** Acepta "08:00", "08:00:00" o [8, 0] (por si Jackson serializa LocalTime como arreglo). */
  /** Convierte una hora serializada a minutos desde la medianoche. */
  private aMinutos(valor: any): number {
    if (Array.isArray(valor)) {
      return valor[0] * 60 + (valor[1] ?? 0);
    }
    const [h, m] = String(valor).split(':').map(Number);
    return h * 60 + (m || 0);
  }

  /** "MIÉRCOLES" / "MIERCOLES" / "Miercoles" -> "miercoles" */
  /** Normaliza el nombre de un día para compararlo en el horario. */
  private normalizarDia(dia: string): string {
    return String(dia).normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();
  }

  /** Convierte minutos desde medianoche al formato HH:mm. */
  private formatearMinutos(min: number): string {
    return `${String(Math.floor(min / 60)).padStart(2, '0')}:${String(min % 60).padStart(2, '0')}`;
  }

  /** Genera filas horarias entre los límites recibidos. */
  private generarHoras(desde: number, hasta: number): FilaHora[] {
    const filas: FilaHora[] = [];
    for (let m = Math.floor(desde / 60) * 60; m < hasta; m += 60) {
      filas.push({
        etiqueta: `${this.formatearMinutos(m)} - ${this.formatearMinutos(m + 60)}`,
        inicio: m,
      });
    }
    return filas;
  }

  /** Construye los días y el rango de fechas de la semana actual. */
  private construirSemana(): void {
    const hoy = new Date();
    const lunes = new Date(hoy);
    lunes.setDate(hoy.getDate() - ((hoy.getDay() + 6) % 7));

    const claves: string[][] = [
      ['lunes', 'LUNES'],
      ['martes', 'MARTES'],
      ['miercoles', 'MIÉRCOLES'],
      ['jueves', 'JUEVES'],
      ['viernes', 'VIERNES'],
      ['sabado', 'SABADO'],


    ];

    this.dias = claves.map(([clave, nombre], i) => {
      const d = new Date(lunes);
      d.setDate(lunes.getDate() + i);
      const fecha = `${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}`;
      return { clave, nombre, fecha };
    });

    const viernes = new Date(lunes);
    viernes.setDate(lunes.getDate() + 4);
    const mes = viernes.toLocaleDateString('es-CO', { month: 'long' });
    this.semanaTexto = `Semana del ${lunes.getDate()} al ${viernes.getDate()} de ${mes}, ${viernes.getFullYear()}`;
  }

  /** Clase que se cruza con la fila de una hora (inicioFila en minutos). */
  /** Busca la clase que se cruza con el día y la fila indicados. */
  getClase(dia: string, inicioFila: number): Clase | null {
    return this.clases.find(c => c.dia === dia && c.inicio < inicioFila + 60 && c.fin > inicioFila) ?? null;
  }

  // ---------- navegación y menú ----------

  /** Navega a la ruta indicada. */
  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  /** Vuelve a la pantalla de gestión de bases de datos. */
  volver(): void {
    this.router.navigate(['/gestion-bases-datos']);
  }

  /** Cierra el aviso y, en la vista administrativa, regresa a la gestión. */
  cerrarAviso(): void {
    this.mensajeVacio = null;
    if (this.esAdministrador) {
      this.volver();
    }
  }

  // ---------- exportar ----------

  /** Descarga el horario del docente como CSV (compatible con Excel). */
  exportarHorario(): void {
    if (this.clases.length === 0) {
      this.mensajeVacio = 'No hay clases para exportar.';
      return;
    }

    const orden = ['lunes', 'martes', 'miercoles', 'jueves', 'viernes', 'sabado', 'domingo'];
    const nombreDia = (clave: string): string =>
      this.dias.find(d => d.clave === clave)?.nombre ?? clave.toUpperCase();
    const esc = (v: string): string => `"${String(v).replace(/"/g, '""')}"`;

    const ordenadas = [...this.clases].sort((a, b) =>
      orden.indexOf(a.dia) - orden.indexOf(b.dia) || a.inicio - b.inicio
    );

    const filas: string[][] = [
      ['Día', 'Hora inicio', 'Hora fin', 'Asignatura', 'Grupo', 'Aula'],
      ...ordenadas.map(c => [
        nombreDia(c.dia),
        this.formatearMinutos(c.inicio),
        this.formatearMinutos(c.fin),
        c.nombre,
        c.grupo,
        c.aula,
      ]),
    ];

    // BOM UTF-8 + separador ";" para que Excel abra bien las tildes
    const csv = '\uFEFF' + filas.map(f => f.map(esc).join(';')).join('\r\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);

    const nombreArchivo = `horario-${(this.docente?.nombre ?? 'docente')
      .normalize('NFD').replace(/[\u0300-\u036f]/g, '')
      .replace(/\s+/g, '-').toLowerCase()}.csv`;

    const a = document.createElement('a');
    a.href = url;
    a.download = nombreArchivo;
    a.click();
    URL.revokeObjectURL(url);
  }

  /** Alterna el menú de usuario y evita propagar el clic. */
  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario = !this.mostrarMenuUsuario;
  }

  /** Cierra la sesión actual y vuelve a la pantalla de acceso. */
  cerrarSesion(): void {
    this.mostrarMenuUsuario = false;
    this.sesion.cerrarSesion();
    this.router.navigate(['/loginn']);
  }

  @HostListener('document:click')
  /** Cierra el menú cuando se hace clic fuera de él. */
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario) {
      this.mostrarMenuUsuario = false;
    }
  }
}
