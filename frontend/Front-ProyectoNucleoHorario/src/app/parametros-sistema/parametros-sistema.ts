import { ChangeDetectorRef, Component, HostListener, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { ParametrosSistemaService } from '../services/parametrossistema.service';
import { SesionService } from '../services/sesion.service';
import { ParametrosSistemaModel } from '../models/parametrossistema.model';

interface Mensaje {
  tipo: 'exito' | 'error';
  texto: string;
}

@Component({
  selector: 'app-parametros-sistema',
  standalone: false,
  templateUrl: './parametros-sistema.html',
  styleUrl: './parametros-sistema.css',
})
export class ParametrosSistema implements OnInit {

  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly parametrosService = inject(ParametrosSistemaService);
  private readonly sesion = inject(SesionService);

  id: number = 1;
  horaInicioEntreSemana: string = '07:00';
  horaFinEntreSemana: string = '22:00';
  horaInicioSabado: string = '07:00';
  horaFinSabado: string = '13:00';
  horaInicioAlmuerzo: string = '12:00';
  horaFinAlmuerzo: string = '13:00';
  maxSesionesPorSemana: number = 4;
  duracionSesionMinutos: number = 120;
  capacidadMaximaGrupo: number = 40;
  toleranciaCapacidad: number = 10;
  minEstudiantesParaAbrir: number = 10;
  exigirDiasNoConsecutivos: boolean = true;

  cargando: boolean = true;
  guardando: boolean = false;
  mensaje: Mensaje | null = null;
  mostrarMenuUsuario: boolean = false;

  ngOnInit(): void {
    if (!this.sesion.esAdministrador()) {
      this.router.navigate(['/inicio-administrador']);
      return;
    }
    this.cargarParametros();
  }

  private cargarParametros(): void {
    this.cargando = true;
    this.parametrosService.obtenerParametros().subscribe({
      next: (respuesta) => {
        const parametros = respuesta.body;
        if (parametros) {
          this.aplicarParametros(parametros);
        }
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (error: HttpErrorResponse) => {
        this.cargando = false;
        this.mostrarError(error);
      },
    });
  }

  private aplicarParametros(p: ParametrosSistemaModel): void {
    this.id = p.id;
    this.horaInicioEntreSemana = p.horaInicioEntreSemana?.slice(0, 5) ?? this.horaInicioEntreSemana;
    this.horaFinEntreSemana = p.horaFinEntreSemana?.slice(0, 5) ?? this.horaFinEntreSemana;
    this.horaInicioSabado = p.horaInicioSabado?.slice(0, 5) ?? this.horaInicioSabado;
    this.horaFinSabado = p.horaFinSabado?.slice(0, 5) ?? this.horaFinSabado;
    this.horaInicioAlmuerzo = p.horaInicioAlmuerzo?.slice(0, 5) ?? this.horaInicioAlmuerzo;
    this.horaFinAlmuerzo = p.horaFinAlmuerzo?.slice(0, 5) ?? this.horaFinAlmuerzo;
    this.maxSesionesPorSemana = p.maxSesionesPorSemana;
    this.duracionSesionMinutos = p.duracionSesionMinutos;
    this.capacidadMaximaGrupo = p.capacidadMaximaGrupo;
    this.toleranciaCapacidad = p.toleranciaCapacidad;
    this.minEstudiantesParaAbrir = p.minEstudiantesParaAbrir;
    this.exigirDiasNoConsecutivos = p.exigirDiasNoConsecutivos;
  }

  guardar(): void {
    if (this.guardando) {
      return;
    }
    this.mensaje = null;

    if (this.horaInicioEntreSemana >= this.horaFinEntreSemana) {
      this.mostrarMensaje('error', 'La hora de inicio entre semana debe ser anterior a la de fin.');
      return;
    }
    if (this.horaInicioSabado >= this.horaFinSabado) {
      this.mostrarMensaje('error', 'La hora de inicio del sábado debe ser anterior a la de fin.');
      return;
    }
    if (this.horaInicioAlmuerzo >= this.horaFinAlmuerzo) {
      this.mostrarMensaje('error', 'La hora de inicio del almuerzo debe ser anterior a la de fin.');
      return;
    }
    if (this.maxSesionesPorSemana < 1 || this.duracionSesionMinutos < 1) {
      this.mostrarMensaje('error', 'Las sesiones máximas y la duración deben ser mayores a 0.');
      return;
    }
    if (this.capacidadMaximaGrupo < 1) {
      this.mostrarMensaje('error', 'La capacidad máxima del grupo debe ser mayor a 0.');
      return;
    }
    if (this.toleranciaCapacidad < 0 || this.toleranciaCapacidad > 100) {
      this.mostrarMensaje('error', 'La tolerancia de capacidad debe estar entre 0 y 100.');
      return;
    }
    if (this.minEstudiantesParaAbrir < 1) {
      this.mostrarMensaje('error', 'El mínimo de estudiantes para abrir debe ser mayor a 0.');
      return;
    }

    const parametros: ParametrosSistemaModel = {
      id: this.id,
      horaInicioEntreSemana: this.horaInicioEntreSemana,
      horaFinEntreSemana: this.horaFinEntreSemana,
      horaInicioSabado: this.horaInicioSabado,
      horaFinSabado: this.horaFinSabado,
      horaInicioAlmuerzo: this.horaInicioAlmuerzo,
      horaFinAlmuerzo: this.horaFinAlmuerzo,
      maxSesionesPorSemana: Number(this.maxSesionesPorSemana),
      duracionSesionMinutos: Number(this.duracionSesionMinutos),
      capacidadMaximaGrupo: Number(this.capacidadMaximaGrupo),
      toleranciaCapacidad: Number(this.toleranciaCapacidad),
      minEstudiantesParaAbrir: Number(this.minEstudiantesParaAbrir),
      exigirDiasNoConsecutivos: this.exigirDiasNoConsecutivos,
    };

    this.guardando = true;
    this.parametrosService.actualizarParametros(parametros).subscribe({
      next: (texto: string) => {
        this.guardando = false;
        this.mostrarMensaje('exito', texto);
      },
      error: (error: HttpErrorResponse) => {
        this.guardando = false;
        this.mostrarError(error);
      },
    });
  }


  private mostrarMensaje(tipo: 'exito' | 'error', texto: string): void {
    this.mensaje = { tipo, texto };
    this.cdr.detectChanges();
  }

  /** Muestra el motivo que devuelve el back (texto) o un mensaje según el código HTTP. */
  private mostrarError(error: HttpErrorResponse): void {
    if (error.status === 0) {
      this.mostrarMensaje('error', 'No se pudo conectar con el servidor.');
    } else if (error.status === 403) {
      this.mostrarMensaje('error', typeof error.error === 'string' && error.error
        ? error.error : 'No tienes permiso para realizar esta acción.');
    } else {
      this.mostrarMensaje('error', typeof error.error === 'string' && error.error
        ? error.error : `Ocurrió un error (código ${error.status}).`);
    }
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
