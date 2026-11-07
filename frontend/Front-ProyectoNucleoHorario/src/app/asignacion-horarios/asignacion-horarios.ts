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

export interface AsignaturaUI {
  id?: number;
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
  dia?: string;
  hora?: string;
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
  styleUrls: ['./asignacion-horarios.css']
})
export class AsignacionHorarios implements OnInit {

  private asignaturaService = inject(AsignaturaService);
  private salonService = inject(SalonService);
  private profesorService = inject(ProfesorService);
  private grupoService = inject(GrupoService);
  private router = inject(Router);
  private sesion = inject(SesionService);

  mostrarModalAsignatura: boolean = false;
  mostrarMenuUsuario: boolean = false;

  listaMaterias: AsignaturaModel[] = [];
  listaProfesoresObj: ProfesorModel[] = [];
  listaSalonesObj: SalonModel[] = [];

  listaProfesores: string[] = [];
  listaBloques: string[] = [];
  salonesDisponibles: string[] = [];
  mapaSalones: { [key: string]: string[] } = {};

  // Filtros de Docente y Periodo Académico
  docenteSeleccionFiltro: string = '';
  periodoSeleccionado: string = '2026-1'; // Periodo por defecto

  listaPeriodos: string[] = [
    '2026-1',
    '2026-2',
    'Intersemestral 2026-1',
    'Intersemestral 2026-2'
  ];

  // Campos del Modal
  docenteSeleccionadoModal: string = '';
  materiaSeleccionadaModal: AsignaturaModel | null = null;
  numeroGrupoModal: number = 1;
  bloqueSeleccionadoModal: string = '';
  salonSeleccionadoModal: string = '';
  numEstudiantesModal: number = 30;

  asignaturasDisponibles: AsignaturaUI[] = [];
  clasesAsignadas: AsignaturaUI[] = [];
  celdaActiva: string | null = null;

  fechaBaseSemana: Date = new Date(2026, 4, 12);
  textoRangoSemana: string = '12–16 de mayo, 2026';
  dias: BloqueCalendario[] = [];

  horasBloques: string[] = [
    '07:00 - 09:00',
    '09:00 - 11:00',
    '11:00 - 13:00',
    '14:00 - 16:00',
    '16:00 - 18:00',
    '18:00 - 20:00',
    '20:00 - 22:00'
  ];

  ngOnInit(): void {
    this.calcularFechasSemana();
    this.cargarDatosBackend();
  }

  calcularFechasSemana(): void {
    const clavesDias = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO'];
    const nombresDias = ['LUNES', 'MARTES', 'MIÉRCOLES', 'JUEVES', 'VIERNES', 'SABADO'];

    const curr = new Date(this.fechaBaseSemana);
    const firstDay = curr.getDate() - curr.getDay() + (curr.getDay() === 0 ? -6 : 1);
    const lunes = new Date(curr.setDate(firstDay));

    this.dias = [];
    const meses = ['enero', 'febrero', 'marzo', 'abril', 'mayo', 'junio', 'julio', 'agosto', 'septiembre', 'octubre', 'noviembre', 'diciembre'];

    for (let i = 0; i < 6; i++) {
      const d = new Date(lunes);
      d.setDate(lunes.getDate() + i);
      const diaStr = String(d.getDate()).padStart(2, '0');
      const mesStr = String(d.getMonth() + 1).padStart(2, '0');

      this.dias.push({
        nombre: nombresDias[i],
        fecha: `${diaStr}/${mesStr}`,
        clave: clavesDias[i]
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

          if (this.asignaturasDisponibles.length === 0) {
            this.abrirModalCrear();
          }
        }
      },
      error: (err) => console.error('Error al cargar profesores:', err)
    });
  }

  onDocenteFiltroChange(): void {
    if (!this.docenteSeleccionFiltro) {
      this.asignaturasDisponibles = [];
      this.clasesAsignadas = [];
      return;
    }
    this.cargarHorarioDocente(this.docenteSeleccionFiltro);
  }

  onPeriodoChange(): void {
    if (this.docenteSeleccionFiltro) {
      this.cargarHorarioDocente(this.docenteSeleccionFiltro);
    }
  }

  cargarHorarioDocente(nombreDocente: string): void {
    const profObj = this.listaProfesoresObj.find(p => `${p.nombre} ${p.apellido}` === nombreDocente);
    if (!profObj || !profObj.id) return;

    this.grupoService.buscarPorProfesor(profObj.id).subscribe({
      next: (response) => {
        this.asignaturasDisponibles = [];
        this.clasesAsignadas = [];

        if (response.body && response.status !== 204) {
          // Filtrar opcionalmente por periodo seleccionado si el backend lo retorna
          const grupos = response.body.filter(g => !this.periodoSeleccionado || g.periodo === this.periodoSeleccionado);
          const coloresPill = [
            { color: '#16a34a', bgLight: '#dcfce7' },
            { color: '#2563eb', bgLight: '#dbeafe' },
            { color: '#9333ea', bgLight: '#f3e8ff' },
            { color: '#ea580c', bgLight: '#ffedd5' }
          ];

          grupos.forEach((grupo, index) => {
            const randCol = coloresPill[index % coloresPill.length];
            const aulaStr = grupo.salon ? `${grupo.salon.bloque}${grupo.salon.numeroSalon}` : 'Por asignar';

            const asigUI: AsignaturaUI = {
              id: grupo.asignatura?.id,
              nombre: `${grupo.asignatura?.nombre} (G${grupo.numeroGrupo})`,
              aula: aulaStr,
              bloque: grupo.salon ? `Bloque ${grupo.salon.bloque}` : 'Sin bloque',
              docente: nombreDocente,
              docenteId: profObj.id,
              salonId: grupo.salon?.id,
              numeroGrupo: grupo.numeroGrupo,
              color: randCol.color,
              bgLight: randCol.bgLight,
              numEstudiantes: grupo.capacidadMaxima
            };

            this.asignaturasDisponibles.push(asigUI);

            if (grupo.sesiones && grupo.sesiones.length > 0) {
              grupo.sesiones.forEach(sesion => {
                this.clasesAsignadas.push({
                  ...asigUI,
                  dia: sesion.dia,
                  hora: `${sesion.horaInicio.substring(0, 5)} - ${sesion.horaFin.substring(0, 5)}`
                });
              });
            }
          });
        }
      },
      error: (err) => {
        console.error('Error al consultar horario:', err);
        this.asignaturasDisponibles = [];
        this.clasesAsignadas = [];
      }
    });
  }

  cargarDocenteDesdeModal(): void {
    if (!this.docenteSeleccionadoModal) {
      alert('⚠️ Por favor seleccione un profesor para cargar su horario.');
      return;
    }
    this.docenteSeleccionFiltro = this.docenteSeleccionadoModal;
    this.cargarHorarioDocente(this.docenteSeleccionadoModal);
    this.mostrarModalAsignatura = false;
  }

  abrirModalCrear(): void {
    this.docenteSeleccionadoModal = this.docenteSeleccionFiltro || '';
    this.materiaSeleccionadaModal = null;
    this.numeroGrupoModal = 1;
    this.bloqueSeleccionadoModal = '';
    this.salonSeleccionadoModal = '';
    this.numEstudiantesModal = 30;
    this.salonesDisponibles = [];
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
    if (!this.docenteSeleccionadoModal || !this.materiaSeleccionadaModal || !this.bloqueSeleccionadoModal || !this.salonSeleccionadoModal || !this.numeroGrupoModal) {
      alert('⚠️ Advertencia: Por favor complete todos los campos obligatorios.');
      return;
    }

    const CAPACIDAD_MINIMA_INSTITUCIONAL = 10;
    if (this.numEstudiantesModal < CAPACIDAD_MINIMA_INSTITUCIONAL) {
      alert(`⚠️ Excepción de Negocio: No se puede abrir un grupo con ${this.numEstudiantesModal} estudiante(s). Mínimo requerido: ${CAPACIDAD_MINIMA_INSTITUCIONAL}.`);
      return;
    }

    const grupoDuplicado = this.asignaturasDisponibles.find(
      a => a.id === this.materiaSeleccionadaModal?.id && a.numeroGrupo === this.numeroGrupoModal
    );
    if (grupoDuplicado) {
      alert(`⚠️ Excepción de Duplicidad: Ya se encuentra registrada la asignatura con el Grupo ${this.numeroGrupoModal} para este docente.`);
      return;
    }

    const profObj = this.listaProfesoresObj.find(p => `${p.nombre} ${p.apellido}` === this.docenteSeleccionadoModal);
    const bloqueLetra = this.salonSeleccionadoModal.charAt(0) as any;
    const numSalonVal = parseInt(this.salonSeleccionadoModal.slice(1));
    const salonObj = this.listaSalonesObj.find(s => s.bloque === bloqueLetra && s.numeroSalon === numSalonVal);

    if (!salonObj || !this.materiaSeleccionadaModal) {
      alert('⚠️ Error: No se pudo identificar el salón o la asignatura.');
      return;
    }

    if (this.materiaSeleccionadaModal.requiereComputadores && !salonObj.tieneComputadores) {
      alert(`⚠️ Excepción de Infraestructura: La asignatura requiere equipos de cómputo y el salón no cuenta con ellos.`);
      return;
    }

    if (this.materiaSeleccionadaModal.requiereSillasMoviles && !salonObj.tieneSillasMoviles) {
      alert(`⚠️ Excepción de Infraestructura: La asignatura requiere sillas móviles y el salón no las posee.`);
      return;
    }

    if (this.numEstudiantesModal > salonObj.capacidad) {
      alert(`⚠️ Excepción de Capacidad: Supera la capacidad máxima del salón (${salonObj.capacidad}).`);
      return;
    }

    const coloresPill = [
      { color: '#16a34a', bgLight: '#dcfce7' },
      { color: '#2563eb', bgLight: '#dbeafe' },
      { color: '#9333ea', bgLight: '#f3e8ff' },
      { color: '#ea580c', bgLight: '#ffedd5' }
    ];
    const colorRandom = coloresPill[Math.floor(Math.random() * coloresPill.length)];

    const nueva: AsignaturaUI = {
      id: this.materiaSeleccionadaModal.id,
      nombre: `${this.materiaSeleccionadaModal.nombre} (G${this.numeroGrupoModal})`,
      docente: this.docenteSeleccionadoModal,
      docenteId: profObj ? profObj.id : undefined,
      salonId: salonObj.id,
      numeroGrupo: this.numeroGrupoModal,
      bloque: `Bloque ${this.bloqueSeleccionadoModal}`,
      aula: this.salonSeleccionadoModal,
      color: colorRandom.color,
      bgLight: colorRandom.bgLight,
      numEstudiantes: this.numEstudiantesModal
    };

    if (this.docenteSeleccionadoModal !== this.docenteSeleccionFiltro) {
      this.docenteSeleccionFiltro = this.docenteSeleccionadoModal;
    }

    this.asignaturasDisponibles.push(nueva);
    this.mostrarModalAsignatura = false;
  }

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

    const cruceDocente = this.clasesAsignadas.find(
      c => c.dia === dia && c.hora === hora && c.docente === asignatura.docente
    );
    if (cruceDocente) {
      alert(`⚠️ Excepción de cruce: El docente ya tiene clase en este horario.`);
      return;
    }

    const cruceSalon = this.clasesAsignadas.find(
      c => c.dia === dia && c.hora === hora && c.aula === asignatura.aula
    );
    if (cruceSalon) {
      alert(`⚠️ Excepción de cruce: El salón ya se encuentra ocupado en este horario.`);
      return;
    }

    if (asignatura.dia && asignatura.hora) {
      this.quitarClase(asignatura.dia, asignatura.hora);
    }

    this.quitarClase(dia, hora);

    this.clasesAsignadas.push({
      ...asignatura,
      dia: dia,
      hora: hora
    });
  }

  getClase(dia: string, hora: string): AsignaturaUI | null {
    return this.clasesAsignadas.find(c => c.dia === dia && c.hora === hora) || null;
  }

  quitarClase(dia: string, hora: string): void {
    this.clasesAsignadas = this.clasesAsignadas.filter(c => !(c.dia === dia && c.hora === hora));
  }

  guardarCambios(): void {
    if (this.clasesAsignadas.length === 0) {
      alert('⚠️ Advertencia: No hay clases asignadas en el calendario para guardar.');
      return;
    }

    let guardadosExitosos = 0;

    this.clasesAsignadas.forEach(clase => {
      const horasPartidas = clase.hora ? clase.hora.split(' - ') : ['07:00', '09:00'];
      const horaInicioStr = horasPartidas[0].length === 5 ? horasPartidas[0] + ':00' : horasPartidas[0];
      const horaFinStr = horasPartidas[1].length === 5 ? horasPartidas[1] + ':00' : horasPartidas[1];

      const grupoPayload = {
        numeroGrupo: clase.numeroGrupo || 1,
        capacidadMinima: 10,
        capacidadMaxima: clase.numEstudiantes || 30,
        estado: 'ACTIVO',
        periodo: this.periodoSeleccionado, // Envía el periodo académico o intersemestral seleccionado
        inscritos: 0,
        asignatura: { id: clase.id },
        profesor: { id: clase.docenteId },
        salon: { id: clase.salonId },
        sesiones: [
          {
            dia: clase.dia,
            horaInicio: horaInicioStr,
            horaFin: horaFinStr
          }
        ]
      };

      this.grupoService.crearGrupo(grupoPayload as any).subscribe({
        next: (res) => {
          guardadosExitosos++;
          if (guardadosExitosos === this.clasesAsignadas.length) {
            alert(`¡Grupos y horarios guardados con éxito para el periodo ${this.periodoSeleccionado}!`);
          }
        },
        error: (err) => {
          console.error('Error al guardar grupo:', err);
          alert(`⚠️ Excepción del Servidor:\n${err.error || err.message}`);
        }
      });
    });
  }

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
