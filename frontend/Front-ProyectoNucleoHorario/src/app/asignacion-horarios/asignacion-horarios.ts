import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

// Interfaces
export interface Asignatura {
  id?: number;
  nombre: string;
  aula: string;
  bloque: string;
  docente: string;
  color?: string;
  bgLight?: string;
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

  // Control de visibilidad de la modal
  mostrarModalAsignatura: boolean = false;

  // Listas desplegables para los selectores
  listaMaterias: string[] = ['Desarrollo de sistemas de información 1', 'Principios de sistemas de información', 'Desarrollo de sistemas de información 2', 'Desarrollo de sistemas de información 3', 'Estructura de datos',
    'Estructuras de sistemas', 'Bases de datos 1','Infraestructura TI 1', 'Modelizacion informacional'
  ,'Proyecto núcleo 1', 'Ingenieria de software 1','Bases de datos 2',
    'Infraestructura TI 2',
    'Ingeniería de software',
    'Complejidad algorítmica',
    'Infraestructura TI 3',
    'Modelos y simulación de sistemas de información',
    'Proyecto núcleo 2',
    'Ingeniería de software 3',
    'Lenguajes de dominio específico',
    'Seguridad de la información',
    'Labor social',
    'Electiva de profundización en ingeniería de sistemas 1',
    'Política, sociedad y ciudadanía',
    'Preparación para la vida laboral',
    'Gestión de tecnología e innovación',
    'Diseño de proyectos de ingeniería',
    'Sistemas inteligentes',
    'Electiva de profundización en ingeniería de sistemas 2',
    'Electiva de profundización en ingeniería de sistemas 3'];

  listaBloques: string[] = ['A', 'B', 'F'];
  salonesDisponibles: string[] = [];

  // Mapeo dinámico de salones por bloque
  mapaSalones: { [key: string]: string[] } = {
    'A': ['A402', 'A403', 'A405'],
    'B': ['B210', 'B211', 'B212', 'B213', 'B313'],
    'F': ['F201', 'F202']
  };

  // Modelo temporal vinculado al formulario modal
  nuevaAsignatura: Asignatura = {
    nombre: '',
    docente: '',
    bloque: '',
    aula: ''
  };

  asignaturasDisponibles: Asignatura[] = [
    { nombre: 'Desarrollo de sistemas de información 1', aula: 'B313', bloque: 'Bloque B', docente: 'Ana López', color: '#16a34a', bgLight: '#dcfce7' },
    { nombre: 'Estructuras de datos', aula: 'B210', bloque: 'Bloque B', docente: 'Carlos Pérez', color: '#2563eb', bgLight: '#dbeafe' },
    { nombre: 'Modelización Informacional', aula: 'A402', bloque: 'Bloque A', docente: 'María Gómez', color: '#9333ea', bgLight: '#f3e8ff' },
    { nombre: 'Bases de Datos 1', aula: 'A403', bloque: 'Bloque A', docente: 'Javier Ruiz', color: '#ea580c', bgLight: '#ffedd5' },
    { nombre: 'Proyecto núcleo 1', aula: 'A402', bloque: 'Bloque A', docente: 'Laura Torres', color: '#eab308', bgLight: '#fef9c3' }
  ];

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

  celdaActiva: string | null = null;

  // Arreglo que almacena las clases ya ubicadas en la grilla del calendario
  clasesAsignadas: Asignatura[] = [];

  constructor(private router: Router) {}

  ngOnInit(): void {}

  // --- LÓGICA DEL MODAL ---
  abrirModalCrear(): void {
    this.nuevaAsignatura = {
      nombre: '',
      docente: '',
      bloque: '',
      aula: ''
    };
    this.salonesDisponibles = [];
    this.mostrarModalAsignatura = true;
  }

  cerrarModalCrear(): void {
    this.mostrarModalAsignatura = false;
  }

  onBloqueChange(): void {
    const bloqueSeleccionado: string = this.nuevaAsignatura.bloque;
    if (bloqueSeleccionado && this.mapaSalones[bloqueSeleccionado]) {
      this.salonesDisponibles = this.mapaSalones[bloqueSeleccionado];
    } else {
      this.salonesDisponibles = [];
    }
    this.nuevaAsignatura.aula = '';
  }

  agregarNuevaAsignatura(): void {
    if (
      !this.nuevaAsignatura.docente ||
      !this.nuevaAsignatura.nombre ||
      !this.nuevaAsignatura.bloque ||
      !this.nuevaAsignatura.aula
    ) {
      alert('Por favor complete todos los campos.');
      return;
    }

    const coloresPill = [
      { color: '#16a34a', bgLight: '#dcfce7' },
      { color: '#2563eb', bgLight: '#dbeafe' },
      { color: '#9333ea', bgLight: '#f3e8ff' },
      { color: '#ea580c', bgLight: '#ffedd5' }
    ];
    const colorRandom = coloresPill[Math.floor(Math.random() * coloresPill.length)];

    const nueva: Asignatura = {
      nombre: this.nuevaAsignatura.nombre,
      docente: this.nuevaAsignatura.docente,
      bloque: `Bloque ${this.nuevaAsignatura.bloque}`,
      aula: this.nuevaAsignatura.aula,
      color: colorRandom.color,
      bgLight: colorRandom.bgLight
    };

    this.asignaturasDisponibles.push(nueva);
    this.cerrarModalCrear();
  }

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  guardarCambios(): void {
    console.log('Cambios guardados:', this.clasesAsignadas);
  }

  // --- MÉTODOS DE ARRASTRAR Y SOLTAR (DRAG & DROP) ---

  onDragStartAsignatura(event: DragEvent, asig: Asignatura): void {
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
    if (data) {
      const asignatura = JSON.parse(data);

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
  }

  getClase(dia: string, hora: string): any {
    return this.clasesAsignadas.find(c => c.dia === dia && c.hora === hora) || null;
  }

  quitarClase(dia: string, hora: string): void {
    this.clasesAsignadas = this.clasesAsignadas.filter(c => !(c.dia === dia && c.hora === hora));
  }
}
