import { Component } from '@angular/core';
import { Router } from '@angular/router';

interface Asignatura {
  nombre: string;
  aula: string;
  bloque: string;
  docente: string;
  color: string;
  bgLight: string;
}

@Component({
  selector: 'app-asignacion-horarios',
  standalone: false,
  templateUrl: './asignacion-horarios.html',
  styleUrls: ['./asignacion-horarios.css']
})
export class AsignacionHorarios {

  constructor(private router: Router) {}

  dias = [
    { clave: 'lunes', nombre: 'LUNES', fecha: '12/05' },
    { clave: 'martes', nombre: 'MARTES', fecha: '13/05' },
    { clave: 'miercoles', nombre: 'MIÉRCOLES', fecha: '14/05' },
    { clave: 'jueves', nombre: 'JUEVES', fecha: '15/05' },
    { clave: 'viernes', nombre: 'VIERNES', fecha: '16/05' },
  ];

  horasBloques = [
    '08:00 - 09:00',
    '09:00 - 10:00',
    '10:00 - 11:00',
    '11:00 - 12:00',
    '12:00 - 13:00',
    '13:00 - 14:00',
    '14:00 - 15:00',
    '15:00 - 16:00',
  ];

  asignaturasDisponibles: Asignatura[] = [
    { nombre: 'Desarrollo de sistemas de información 1', aula: 'B313', bloque: 'Bloque B', docente: 'Ana López', color: '#16a34a', bgLight: '#dcfce7' },
    { nombre: 'Estructuras de datos', aula: 'B210', bloque: 'Bloque B', docente: 'Carlos Pérez', color: '#2563eb', bgLight: '#dbeafe' },
    { nombre: 'Modelización Informacional', aula: 'A402', bloque: 'Bloque A', docente: 'María Gómez', color: '#9333ea', bgLight: '#f3e8ff' },
    { nombre: 'Bases de Datos 1', aula: 'A403', bloque: 'Bloque A', docente: 'Javier Ruiz', color: '#f97316', bgLight: '#ffedd5' },
    { nombre: 'Proyecto núcleo 1', aula: 'A402', bloque: 'Bloque A', docente: 'Laura Torres', color: '#eab308', bgLight: '#fef9c3' },
    { nombre: 'Seguridad de la información', aula: 'B313', bloque: 'Bloque B', docente: 'Pedro Díaz', color: '#0d9488', bgLight: '#ccfbf1' },
  ];

  // Clases ubicadas en el horario. Clave: "dia|hora"
  horario: { [clave: string]: any } = {};

  celdaActiva: string | null = null;

  private arrastrando: { tipo: 'asignatura' | 'clase'; asig?: Asignatura; origen?: string } | null = null;

  ngOnInit(): void {
    this.cargarEjemplo();
  }

  private cargarEjemplo(): void {
    const ponerClase = (dia: string, hora: string, nombreAsig: string) => {
      const a = this.asignaturasDisponibles.find(x => x.nombre.toLowerCase() === nombreAsig.toLowerCase());
      if (a) {
        this.horario[`${dia}|${hora}`] = {
          nombre: a.nombre, aula: a.aula, bloque: a.bloque, color: a.color, docente: a.docente
        };
      }
    };

    ponerClase('lunes', '08:00 - 09:00', 'Desarrollo de sistemas de información 1');
    ponerClase('viernes', '08:00 - 09:00', 'Seguridad de la información');
    ponerClase('martes', '09:00 - 10:00', 'Desarrollo de sistemas de información 1');
    ponerClase('jueves', '09:00 - 10:00', 'Estructuras de datos');
    ponerClase('lunes', '10:00 - 11:00', 'Estructuras de datos');
    ponerClase('martes', '10:00 - 11:00', 'Estructuras de datos');
    ponerClase('miercoles', '10:00 - 11:00', 'Desarrollo de sistemas de información 1');
    ponerClase('lunes', '12:00 - 13:00', 'Modelización Informacional');
    ponerClase('viernes', '12:00 - 13:00', 'Proyecto núcleo 1');
    ponerClase('miercoles', '13:00 - 14:00', 'Proyecto núcleo 1');
    ponerClase('martes', '14:00 - 15:00', 'Seguridad de la información');
  }

  getClase(dia: string, hora: string) {
    return this.horario[`${dia}|${hora}`] || null;
  }

  // ===== DRAG & DROP =====

  // Arrastrar desde la lista de asignaturas
  onDragStartAsignatura(event: DragEvent, asig: Asignatura): void {
    this.arrastrando = { tipo: 'asignatura', asig };
    event.dataTransfer!.effectAllowed = 'copy';
    event.dataTransfer!.setData('text/plain', asig.nombre); // necesario en Firefox
  }

  // Arrastrar una clase que ya está en el horario (para moverla)
  onDragStartClase(event: DragEvent, dia: string, hora: string): void {
    this.arrastrando = { tipo: 'clase', origen: `${dia}|${hora}` };
    event.dataTransfer!.effectAllowed = 'move';
    event.dataTransfer!.setData('text/plain', `${dia}|${hora}`);
  }

  onDragOver(event: DragEvent, dia: string, hora: string): void {
    event.preventDefault(); // obligatorio para permitir el drop
    this.celdaActiva = `${dia}|${hora}`;
  }

  onDragLeave(): void {
    this.celdaActiva = null;
  }

  onDrop(event: DragEvent, dia: string, hora: string): void {
    event.preventDefault();
    this.celdaActiva = null;
    if (!this.arrastrando) return;

    const destino = `${dia}|${hora}`;

    if (this.arrastrando.tipo === 'asignatura' && this.arrastrando.asig) {
      const a = this.arrastrando.asig;
      this.horario[destino] = {
        nombre: a.nombre, aula: a.aula, bloque: a.bloque, color: a.color, docente: a.docente
      };
    } else if (this.arrastrando.origen && this.arrastrando.origen !== destino) {
      const origen = this.arrastrando.origen;
      const clase = this.horario[origen];
      const claseDestino = this.horario[destino];
      this.horario[destino] = clase;
      if (claseDestino) {
        this.horario[origen] = claseDestino; // intercambia
      } else {
        delete this.horario[origen];         // mueve
      }
    }
    this.arrastrando = null;
  }

  quitarClase(dia: string, hora: string): void {
    delete this.horario[`${dia}|${hora}`];
  }

  guardarCambios(): void {
    // Aquí luego puedes enviar this.horario al backend
    console.log('Horario a guardar:', JSON.stringify(this.horario, null, 2));
    alert('Cambios guardados (por ahora solo en consola).');
  }

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }
}
