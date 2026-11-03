import { Component, HostListener, computed, signal } from '@angular/core';
import { Router } from '@angular/router';

export interface RegistroAuditoria {
  id: number;
  idUsuario: number;
  nombreUsuario: string;
  accion: string;
  rol: 'Docente' | 'Administrador';
  servicioUsado: string;
  fecha: string;
}

@Component({
  selector: 'app-auditoria',
  standalone: false,
  templateUrl: './auditoria.html',
  styleUrl: './auditoria.css',
})
export class Auditoria {

  constructor(private router: Router) {}

  // Datos de ejemplo (temporal, mientras no hay service/entidad en el front)
  registros = signal<RegistroAuditoria[]>([
    { id: 1, idUsuario: 10, nombreUsuario: 'Juan Pérez', accion: 'Crear', rol: 'Administrador', servicioUsado: 'Horarios', fecha: '03/11/2026 09:15' },
    { id: 2, idUsuario: 22, nombreUsuario: 'Laura Gómez', accion: 'Actualizar', rol: 'Docente', servicioUsado: 'Profesores', fecha: '03/11/2026 10:02' },
    { id: 3, idUsuario: 10, nombreUsuario: 'Juan Pérez', accion: 'Eliminar', rol: 'Administrador', servicioUsado: 'Administradores', fecha: '02/11/2026 18:40' },
  ]);

  cargando = signal<boolean>(false);
  error = signal<string>('');
  filtroBusqueda = signal<string>('');
  registrosPorPagina = signal<number>(10);
  paginaActual = signal<number>(1);
  mostrarMenuUsuario = signal<boolean>(false);

  // Derivados con computed
  registrosFiltrados = computed(() => {
    const todos = this.registros();
    const termino = this.filtroBusqueda().trim().toLowerCase();
    if (!termino) return todos;
    return todos.filter(reg =>
      reg.nombreUsuario.toLowerCase().includes(termino) ||
      reg.accion.toLowerCase().includes(termino) ||
      reg.servicioUsado.toLowerCase().includes(termino) ||
      reg.rol.toLowerCase().includes(termino)
    );
  });

  totalPaginas = computed(() =>
    Math.max(1, Math.ceil(this.registrosFiltrados().length / this.registrosPorPagina()))
  );

  paginas = computed(() =>
    Array.from({ length: this.totalPaginas() }, (_, i) => i + 1)
  );

  registrosPaginados = computed(() => {
    const inicio = (this.paginaActual() - 1) * this.registrosPorPagina();
    return this.registrosFiltrados().slice(inicio, inicio + this.registrosPorPagina());
  });

  onFiltroChange(valor: string): void {
    this.filtroBusqueda.set(valor);
    this.paginaActual.set(1);
  }

  onRegistrosPorPaginaChange(valor: number): void {
    this.registrosPorPagina.set(Number(valor));
    this.paginaActual.set(1);
  }

  cambiarPagina(p: number): void {
    if (p >= 1 && p <= this.totalPaginas()) {
      this.paginaActual.set(p);
    }
  }

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  toggleMenuUsuario(event: Event): void {
    event.stopPropagation();
    this.mostrarMenuUsuario.update(v => !v);
  }

  cerrarSesion(): void {
    this.mostrarMenuUsuario.set(false);
    this.router.navigate(['/loginn']);
  }

  @HostListener('document:click')
  cerrarMenuAlHacerClicFuera(): void {
    if (this.mostrarMenuUsuario()) {
      this.mostrarMenuUsuario.set(false);
    }
  }
}
