import { ChangeDetectorRef, Component, HostListener, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SesionService } from '../services/sesion.service';
import { GrupoService } from '../services/grupo.service';
import { GrupoModel } from '../models/grupo.model';

interface Clase {
  dia: string;
  inicio: number; // minutos desde las 00:00
  fin: number;
  nombre: string;
  grupo: string;  // "Grupo 1"
  aula: string;   // "Aula 313 - Bloque B"
  detalle: string; // "Grupo 1 · Aula 313 - Bloque B"
  color: string;
  bg: string;
}

interface DocenteInfo {
  id: number;
  nombre: string;
  especialidad: string;
}

interface FilaHora {
  etiqueta: string;
  inicio: number;
}

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
export class ConsultaHorario implements OnInit {

  // true = administrador (ruta /consulta-horario), false = docente (ruta /mi-horario)
  esAdministrador: boolean = true;

  docente: DocenteInfo | null = null;

  cargando: boolean = false;
  /** Si tiene valor, se muestra la ventana emergente con el aviso. */
  mensajeVacio: string | null = null;

  dias: { clave: string; nombre: string; fecha: string }[] = [];
  semanaTexto: string = '';
  horas: FilaHora[] = [];
  clases: Clase[] = [];
  leyenda: { nombre: string; color: string }[] = [];
  cuposDisponibles: number = 0;

  /** Cantidad de grupos (clases) asignados al docente. */
  totalClases: number = 0;

  mostrarMenuUsuario: boolean = false;

  private sesion = inject(SesionService);
  private grupoService = inject(GrupoService);
  private cdr = inject(ChangeDetectorRef);

  constructor(private route: ActivatedRoute, private router: Router) {}

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
  private aMinutos(valor: any): number {
    if (Array.isArray(valor)) {
      return valor[0] * 60 + (valor[1] ?? 0);
    }
    const [h, m] = String(valor).split(':').map(Number);
    return h * 60 + (m || 0);
  }

  /** "MIÉRCOLES" / "MIERCOLES" / "Miercoles" -> "miercoles" */
  private normalizarDia(dia: string): string {
    return String(dia).normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();
  }

  private formatearMinutos(min: number): string {
    return `${String(Math.floor(min / 60)).padStart(2, '0')}:${String(min % 60).padStart(2, '0')}`;
  }

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
  getClase(dia: string, inicioFila: number): Clase | null {
    return this.clases.find(c => c.dia === dia && c.inicio < inicioFila + 60 && c.fin > inicioFila) ?? null;
  }

  // ---------- navegación y menú ----------

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  volver(): void {
    this.router.navigate(['/gestion-bases-datos']);
  }

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
