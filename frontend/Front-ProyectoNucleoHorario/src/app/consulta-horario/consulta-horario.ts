import {Component, HostListener, OnInit} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

interface Clase {
  dia: string;
  hora: string;
  nombre: string;
  aula: string;
  color: string;
  bg: string;
}

interface DocenteInfo {
  id: number;
  nombre: string;
  especialidad: string;
}

@Component({
  selector: 'app-consulta-horario',
  standalone: false,
  templateUrl: './consulta-horario.html',
  styleUrl: './consulta-horario.css',
})
export class ConsultaHorario implements OnInit {

  // true = administrador (ruta /consulta-horario), false = docente (ruta /mi-horario)
  esAdministrador: boolean = true;

  // Datos de ejemplo (luego vendrán del backend)
  private docentes: DocenteInfo[] = [
    { id: 1, nombre: 'Dr. Carlos Mendoza López', especialidad: 'Ingeniería de Software' },
    { id: 2, nombre: 'Dra. Ana Lucía Ramírez Torres', especialidad: 'Base de Datos' },
    { id: 3, nombre: 'Javier Solís Herrera', especialidad: 'Redes y Comunicaciones' },
    { id: 5, nombre: 'Dr. Ricardo Vega Paredes', especialidad: 'Inteligencia Artificial' },
    { id: 6, nombre: 'Mtra. Sofía Delgado Morales', especialidad: 'Sistemas Operativos' },
    { id: 7, nombre: 'Lic. Pablo Estrada Gómez', especialidad: 'Ciencias de la Computación' },
  ];

  docente: DocenteInfo | null = null;

  dias = [
    { clave: 'lunes', nombre: 'LUNES', fecha: '12/05' },
    { clave: 'martes', nombre: 'MARTES', fecha: '13/05' },
    { clave: 'miercoles', nombre: 'MIÉRCOLES', fecha: '14/05' },
    { clave: 'jueves', nombre: 'JUEVES', fecha: '15/05' },
    { clave: 'viernes', nombre: 'VIERNES', fecha: '16/05' },
  ];

  horas = [
    '08:00 - 09:00',
    '09:00 - 10:00',
    '10:00 - 11:00',
    '11:00 - 12:00',
    '12:00 - 13:00',
    '13:00 - 14:00',
    '14:00 - 15:00',
    '15:00 - 16:00',
  ];

  leyenda = [
    { nombre: 'Desarrollo de sistemas de información 1', color: '#16a34a' },
    { nombre: 'Estructuras de datos', color: '#2563eb' },
    { nombre: 'Modelización Informacional', color: '#9333ea' },
    { nombre: 'Bases de Datos 1', color: '#f97316' },
    { nombre: 'Proyecto núcleo 1', color: '#eab308' },
    { nombre: 'Seguridad de la información', color: '#0d9488' },
  ];

  clases: Clase[] = [
    { dia: 'lunes', hora: '08:00 - 09:00', nombre: 'Desarrollo de sistemas de información 1', aula: 'Aula 313 - Bloque B', color: '#16a34a', bg: '#dcfce7' },
    { dia: 'miercoles', hora: '08:00 - 09:00', nombre: 'Programación I', aula: 'Lab. 1', color: '#9333ea', bg: '#f3e8ff' },
    { dia: 'viernes', hora: '08:00 - 09:00', nombre: 'Seguridad de la información', aula: 'Aula 313 - Bloque B', color: '#0d9488', bg: '#ccfbf1' },
    { dia: 'martes', hora: '09:00 - 10:00', nombre: 'Desarrollo de sistemas de información 1', aula: 'Aula 313 - Bloque B', color: '#16a34a', bg: '#dcfce7' },
    { dia: 'jueves', hora: '09:00 - 10:00', nombre: 'Estructuras de datos', aula: 'Aula 210 - Bloque B', color: '#2563eb', bg: '#dbeafe' },
    { dia: 'lunes', hora: '10:00 - 11:00', nombre: 'Estructuras de datos', aula: 'Aula 210 - Bloque B', color: '#2563eb', bg: '#dbeafe' },
    { dia: 'miercoles', hora: '10:00 - 11:00', nombre: 'Desarrollo de sistemas de información 1', aula: 'Aula 313 - Bloque B', color: '#16a34a', bg: '#dcfce7' },
    { dia: 'martes', hora: '11:00 - 12:00', nombre: 'Estructuras de datos', aula: 'Aula 210 - Bloque B', color: '#2563eb', bg: '#dbeafe' },
    { dia: 'jueves', hora: '11:00 - 12:00', nombre: 'Bases de Datos 1', aula: 'Aula 403 - Bloque A', color: '#f97316', bg: '#ffedd5' },
    { dia: 'lunes', hora: '12:00 - 13:00', nombre: 'Modelización Informacional', aula: 'Aula 402 - Bloque A', color: '#9333ea', bg: '#f3e8ff' },
    { dia: 'viernes', hora: '12:00 - 13:00', nombre: 'Proyecto núcleo 1', aula: 'Aula 402 - Bloque A', color: '#eab308', bg: '#fef9c3' },
    { dia: 'miercoles', hora: '13:00 - 14:00', nombre: 'Proyecto núcleo 1', aula: 'Aula 402 - Bloque A', color: '#eab308', bg: '#fef9c3' },
    { dia: 'martes', hora: '14:00 - 15:00', nombre: 'Seguridad de la información', aula: 'Aula 313 - Bloque B', color: '#0d9488', bg: '#ccfbf1' },
  ];

  cuposDisponibles = 30;

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit(): void {
    // En /mi-horario entra el docente; en /consulta-horario entra el admin
    this.esAdministrador = this.route.snapshot.data['modo'] !== 'docente';

    this.route.queryParamMap.subscribe(params => {
      let id: number;
      if (this.esAdministrador) {
        id = Number(params.get('id'));
      } else {
        // Por ahora de ejemplo; luego será el id del docente logueado
        id = Number(localStorage.getItem('docenteId')) || 1;
      }
      this.docente = this.docentes.find(d => d.id === id) ?? this.docentes[0];
    });
  }

  get totalClases(): number {
    return this.clases.length;
  }

  getClase(dia: string, hora: string): Clase | null {
    return this.clases.find(c => c.dia === dia && c.hora === hora) ?? null;
  }

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  volver(): void {
    this.router.navigate(['/gestion-bases-datos']);
  }

  exportarHorario(): void {
    console.log('Exportar horario del docente:', this.docente?.nombre);
  }

  mostrarMenuUsuario: boolean = false;

  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario = !this.mostrarMenuUsuario;
  }

  cerrarSesion(): void {
    this.mostrarMenuUsuario = false;
    this.router.navigate(['/loginn']); // Cambia '/login' por la ruta de tu login
  }

  @HostListener('document:click')
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario) {
      this.mostrarMenuUsuario = false;
    }
  }
}
