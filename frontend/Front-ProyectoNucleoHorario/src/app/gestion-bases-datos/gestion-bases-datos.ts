import { Component } from '@angular/core';
import {Router} from '@angular/router';

export interface RegistroBaseDatos {
  id: number;
  nombre: string;
  rol: 'Docente' | 'Administrador';
  especialidad: string;
  cargaAsignada: string;
}

@Component({
  selector: 'app-gestion-bases-datos',
  standalone: false,
  templateUrl: './gestion-bases-datos.html',
  styleUrl: './gestion-bases-datos.css',
})
export class GestionBasesDatos {
  filtroBusqueda: string = '';
  mostrarModalRegistro: boolean = false;

  registros: RegistroBaseDatos[] = [
    { id: 1, nombre: 'Dr. Carlos Mendoza López', rol: 'Docente', especialidad: 'Ingeniería de Software', cargaAsignada: '18 horas/semana' },
    { id: 2, nombre: 'Dra. Ana Lucía Ramírez Torres', rol: 'Docente', especialidad: 'Base de Datos', cargaAsignada: '16 horas/semana' },
    { id: 3, nombre: 'Javier Solís Herrera', rol: 'Docente', especialidad: 'Redes y Comunicaciones', cargaAsignada: '14 horas/semana' },
    { id: 4, nombre: 'Ing. Mariana Ortega Castillo', rol: 'Administrador', especialidad: 'Tecnologías de Información', cargaAsignada: '20 horas/semana' },
    { id: 5, nombre: 'Dr. Ricardo Vega Paredes', rol: 'Docente', especialidad: 'Inteligencia Artificial', cargaAsignada: '16 horas/semana' },
    { id: 6, nombre: 'Mtra. Sofía Delgado Morales', rol: 'Docente', especialidad: 'Sistemas Operativos', cargaAsignada: '14 horas/semana' },
    { id: 7, nombre: 'Lic. Pablo Estrada Gómez', rol: 'Docente', especialidad: 'Ciencias de la Computación', cargaAsignada: '18 horas/semana' }
  ];

  registrosPorPagina: number = 10;
  paginas: number[] = [1, 2, 3, 4, 5];
  paginaActual: number = 1;

  constructor(private router: Router) { }

  ngOnInit(): void {}

  get registrosFiltrados(): RegistroBaseDatos[] {
    if (!this.filtroBusqueda.trim()) {
      return this.registros;
    }
    const termino = this.filtroBusqueda.toLowerCase();
    return this.registros.filter(reg =>
      reg.nombre.toLowerCase().includes(termino) ||
      reg.rol.toLowerCase().includes(termino) ||
      reg.especialidad.toLowerCase().includes(termino)
    );
  }

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }



  crearNuevoRegistro(): void {
    this.mostrarModalRegistro = true;
  }

  cerrarModalRegistro(): void {
    this.mostrarModalRegistro = false;
  }

  actualizarRegistro(id: number): void {
    console.log('Actualizar registro ID:', id);
  }

  eliminarRegistro(id: number): void {
    console.log('Eliminar registro ID:', id);
  }

  cambiarPagina(p: number): void {
    this.paginaActual = p;
  }
}
