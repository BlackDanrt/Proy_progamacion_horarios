import { Component, HostListener, OnInit, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, map } from 'rxjs/operators';
import { of } from 'rxjs';

import { AuditoriaService } from '../services/auditoria.service';

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
export class Auditoria implements OnInit {

  private auditoriaService = inject(AuditoriaService);
  private router = inject(Router);

  registros = signal<RegistroAuditoria[]>([]);
  cargando = signal<boolean>(false);
  error = signal<string>('');
  filtroBusqueda = signal<string>('');
  registrosPorPagina = signal<number>(10);
  paginaActual = signal<number>(1);
  mostrarMenuUsuario = signal<boolean>(false);

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

  ngOnInit(): void {
    this.cargarRegistros();
  }

  cargarRegistros(): void {
    this.cargando.set(true);
    this.error.set('');

    this.auditoriaService.mostrarTodo().pipe(
      map(resp => (resp.body ?? []) as any[]),
      catchError(err => {
        console.error('Error cargando auditoría', err);
        this.error.set('No se pudieron cargar los registros de auditoría.');
        return of([] as any[]);
      })
    ).subscribe({
      next: (lista) => {
        const mapeados: RegistroAuditoria[] = lista.map(a => ({
          id: a.id,
          idUsuario: a.idUsuario,
          nombreUsuario: a.nombreUsuario,
          accion: this.formatearEnum(a.accion),
          rol: a.esAdmin ? 'Administrador' : 'Docente',
          servicioUsado: this.formatearEnum(a.servicioUsado),
          fecha: this.formatearFecha(a.fecha)
        }));

        this.registros.set(mapeados);
        this.ajustarPagina();
        this.cargando.set(false);
      },
      error: (err) => {
        console.error('Error inesperado', err);
        this.error.set('Ocurrió un error al cargar los registros.');
        this.cargando.set(false);
      }
    });
  }

  private formatearEnum(valor: string | null | undefined): string {
    if (!valor) return '—';
    return valor
      .toLowerCase()
      .split('_')
      .map(w => w.charAt(0).toUpperCase() + w.slice(1))
      .join(' ');
  }

  private formatearFecha(valor: string | null | undefined): string {
    if (!valor) return '—';
    const fecha = new Date(valor);
    if (isNaN(fecha.getTime())) return '—';
    return fecha.toLocaleString('es-CO', {
      day: '2-digit', month: '2-digit', year: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }

  /** Evita que la página actual quede fuera de rango (tras filtrar o recargar). */
  private ajustarPagina(): void {
    if (this.paginaActual() > this.totalPaginas()) {
      this.paginaActual.set(this.totalPaginas());
    }
  }

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
