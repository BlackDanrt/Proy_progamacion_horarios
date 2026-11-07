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

  // Listas dinámicas obtenidas directamente de la BD (sin hardcode)
  listaMaterias: string[] = [];
  listaProfesoresObj: ProfesorModel[] = [];
  listaSalonesObj: SalonModel[] = [];
  listaAsignaturasObj: AsignaturaModel[] = [];

  listaProfesores: string[] = [];
  listaBloques: string[] = [];
  salonesDisponibles: string[] = [];
  mapaSalones: { [key: string]: string[] } = {};

  nuevaAsignatura: AsignaturaUI = {
    nombre: '',
    docente: '',
    bloque: '',
    aula: '',
    numEstudiantes: undefined
  };

  asignaturasDisponibles: AsignaturaUI[] = [];
  clasesAsignadas: AsignaturaUI[] = [];
  celdaActiva: string | null = null;

  dias: BloqueCalendario[] = [
    { nombre: 'LUNES', fecha: '12/05', clave: 'LUNES' },
    { nombre: 'MARTES', fecha: '13/05', clave: 'MARTES' },
    { nombre: 'MIÉRCOLES', fecha: '14/05', clave: 'MIERCOLES' },
    { nombre: 'JUEVES', fecha: '15/05', clave: 'JUEVES' },
    { nombre: 'VIERNES', fecha: '16/05', clave: 'VIERNES' },
    { nombre: 'SABADO', fecha: '17/05', clave: 'SABADO' }
  ];

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
    this.cargarDatosBackend();
  }

  cargarDatosBackend(): void {
    // 1. Cargar Asignaturas reales desde el Backend
    this.asignaturaService.mostrarTodo().subscribe({
      next: (response) => {
        if (response.body) {
          this.listaAsignaturasObj = response.body;
          this.listaMaterias = response.body.map((a) => a.nombre);

          const coloresPill = [
            { color: '#16a34a', bgLight: '#dcfce7' },
            { color: '#2563eb', bgLight: '#dbeafe' },
            { color: '#9333ea', bgLight: '#f3e8ff' },
            { color: '#ea580c', bgLight: '#ffedd5' },
            { color: '#eab308', bgLight: '#fef9c3' }
          ];

          this.asignaturasDisponibles = response.body.map((asig, index) => {
            const randCol = coloresPill[index % coloresPill.length];
            return {
              id: asig.id,
              nombre: asig.nombre,
              aula: 'Por asignar',
              bloque: 'Sin bloque',
              docente: 'Por asignar',
              color: randCol.color,
              bgLight: randCol.bgLight,
              numEstudiantes: 30
            };
          });
        }
      },
      error: (err) => console.error('Error al cargar asignaturas:', err)
    });

    // 2. Cargar Salones reales desde el Backend (Dinámico por Bloque)
    this.salonService.mostrarTodo().subscribe({
      next: (response) => {
        if (response.body) {
          this.listaSalonesObj = response.body;
          this.mapaSalones = {};
          const bloquesSet = new Set<string>();

          response.body.forEach((salon) => {
            const bloqueKey = salon.bloque; // Ej: 'A', 'B', 'F'
            bloquesSet.add(bloqueKey);
            const numeroStr = `${salon.bloque}${salon.numeroSalon}`;
            if (!this.mapaSalones[bloqueKey]) {
              this.mapaSalones[bloqueKey] = [];
            }
            this.mapaSalones[bloqueKey].push(numeroStr);
          });
          this.listaBloques = Array.from(bloquesSet);
        }
      },
      error: (err) => console.error('Error al cargar salones:', err)
    });

    // 3. Cargar Profesores reales desde el Backend
    this.profesorService.mostrarTodo().subscribe({
      next: (response) => {
        if (response.body) {
          this.listaProfesoresObj = response.body;
          this.listaProfesores = response.body.map((p) => `${p.nombre} ${p.apellido}`);
        }
      },
      error: (err) => console.error('Error al cargar profesores:', err)
    });
  }

  abrirModalCrear(): void {
    this.nuevaAsignatura = { nombre: '', docente: '', bloque: '', aula: '', numEstudiantes: undefined };
    this.salonesDisponibles = [];
    this.mostrarModalAsignatura = true;
  }

  cerrarModalCrear(): void {
    this.mostrarModalAsignatura = false;
  }

  onBloqueChange(): void {
    const bloqueSeleccionado = this.nuevaAsignatura.bloque;
    if (bloqueSeleccionado && this.mapaSalones[bloqueSeleccionado]) {
      this.salonesDisponibles = this.mapaSalones[bloqueSeleccionado];
    } else {
      this.salonesDisponibles = [];
    }
    this.nuevaAsignatura.aula = '';
  }

  agregarNuevaAsignatura(): void {
    if (!this.nuevaAsignatura.docente || !this.nuevaAsignatura.nombre || !this.nuevaAsignatura.bloque || !this.nuevaAsignatura.aula) {
      alert('Por favor complete todos los campos.');
      return;
    }

    const asigObj = this.listaAsignaturasObj.find(a => a.nombre === this.nuevaAsignatura.nombre);
    const profObj = this.listaProfesoresObj.find(p => `${p.nombre} ${p.apellido}` === this.nuevaAsignatura.docente);

    const bloqueLetra = this.nuevaAsignatura.aula.charAt(0) as any;
    const numSalonVal = parseInt(this.nuevaAsignatura.aula.slice(1));
    const salonObj = this.listaSalonesObj.find(s => s.bloque === bloqueLetra && s.numeroSalon === numSalonVal);

    const coloresPill = [
      { color: '#16a34a', bgLight: '#dcfce7' },
      { color: '#2563eb', bgLight: '#dbeafe' },
      { color: '#9333ea', bgLight: '#f3e8ff' },
      { color: '#ea580c', bgLight: '#ffedd5' }
    ];
    const colorRandom = coloresPill[Math.floor(Math.random() * coloresPill.length)];

    const nueva: AsignaturaUI = {
      id: asigObj ? asigObj.id : undefined,
      nombre: this.nuevaAsignatura.nombre,
      docente: this.nuevaAsignatura.docente,
      docenteId: profObj ? profObj.id : undefined,
      salonId: salonObj ? salonObj.id : undefined,
      bloque: `Bloque ${this.nuevaAsignatura.bloque}`,
      aula: this.nuevaAsignatura.aula,
      color: colorRandom.color,
      bgLight: colorRandom.bgLight,
      numEstudiantes: this.nuevaAsignatura.numEstudiantes || 30
    };

    this.asignaturasDisponibles.push(nueva);
    this.cerrarModalCrear();
  }

  // --- DRAG AND DROP Y VALIDACIÓN DE EXCEPCIONES LOCALES ---

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

    // Validación de Cruce de Docente
    const cruceDocente = this.clasesAsignadas.find(
      c => c.dia === dia && c.hora === hora && c.docente === asignatura.docente
    );
    if (cruceDocente) {
      alert(`⚠️ Excepción de cruce: El docente ${asignatura.docente} ya tiene clase asignada en este horario.`);
      return;
    }

    // Validación de Cruce de Salón
    const cruceSalon = this.clasesAsignadas.find(
      c => c.dia === dia && c.hora === hora && c.aula === asignatura.aula
    );
    if (cruceSalon) {
      alert(`⚠️ Excepción de cruce: El salón ${asignatura.aula} ya se encuentra ocupado en este horario.`);
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

  // --- GUARDAR EN EL BACKEND CON VALIDACIÓN DE EXCEPCIONES ---
  guardarCambios(): void {
    if (this.clasesAsignadas.length === 0) {
      alert('No hay clases asignadas en el calendario para guardar.');
      return;
    }

    let guardadosExitosos = 0;

    this.clasesAsignadas.forEach(clase => {
      const horasPartidas = clase.hora ? clase.hora.split(' - ') : ['07:00', '09:00'];
      const horaInicioStr = horasPartidas[0].length === 5 ? horasPartidas[0] + ':00' : horasPartidas[0];
      const horaFinStr = horasPartidas[1].length === 5 ? horasPartidas[1] + ':00' : horasPartidas[1];

      const grupoPayload = {
        numeroGrupo: 1,
        capacidadMinima: 10,
        capacidadMaxima: clase.numEstudiantes || 30,
        estado: 'ACTIVO',
        periodo: '2026-1',
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
            alert('¡Grupos y horarios guardados y validados correctamente en el backend!');
          }
        },
        error: (err) => {
          console.error('Error al guardar grupo en el backend:', err);
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
