import { Component } from '@angular/core';
import {Router} from '@angular/router';

interface TarjetaAcceso {
  titulo: string;
  descripcion: string;
  icono: string;
  ruta: string;
  badge: string;
}

@Component({
  selector: 'app-inicio-administrador',
  standalone: false,
  templateUrl: './inicio-administrador.html',
  styleUrl: './inicio-administrador.css',
})
export class InicioAdministrador {
  nombreAdmin: string = 'Administrador';
  rol: string = 'Administrador General';

  metricas = [
    { titulo: 'Docentes Activos', valor: '48', icono: 'users' },
    { titulo: 'Asignaturas', valor: '32', icono: 'book' },
    { titulo: 'Aulas Asignadas', valor: '18', icono: 'building' },
    { titulo: 'Bases de Datos', valor: '10', icono: 'database' }
  ];

  accesosDirectos: TarjetaAcceso[] = [
    {
      titulo: 'Gestión de Bases de Datos',
      descripcion: 'Administra docentes, roles, especialidades y cargas académicas del sistema.',
      icono: 'database',
      ruta: '/gestion-bases-datos',
      badge: '10 Registros'
    },
    {
      titulo: 'Asignación de Horarios',
      descripcion: 'Organiza la disponibilidad horaria semanal por asignaturas y aulas.',
      icono: 'calendar',
      ruta: '/horarios',
      badge: 'Semana Actual'
    }
  ];

  constructor(private router: Router) { }

  ngOnInit(): void {}

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }
}
