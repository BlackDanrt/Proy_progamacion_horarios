import {Component, HostListener, Input, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

interface BloqueHorario {
  horaInicio: string;
  horaFin: string;
  esAlmuerzo?: boolean;
}

@Component({
  selector: 'app-registro-profesor',
  standalone: false,
  templateUrl: './registro-profesor.html',
  styleUrl: './registro-profesor.css',
})
export class RegistroProfesor implements OnInit {
  @Input() esAdministrador: boolean = false;

  nombreCompleto: string = '';
  correoInstitucional: string = '';
  especialidad: string = '';
  tipoVinculacion: string = 'Tiempo completo';
  contrasenia: string = '';

  opcionesVinculacion: string[] = [
    'Tiempo completo',
    'Medio tiempo',
    'Cátedra',
    'Ocasional'
  ];

  diasSemana: string[] = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];

  horas: BloqueHorario[] = [
    { horaInicio: '07:00', horaFin: '09:00' },
    { horaInicio: '09:00', horaFin: '11:00' },
    { horaInicio: '11:00', horaFin: '13:00' },
    { horaInicio: '13:00', horaFin: '14:00', esAlmuerzo: true },
    { horaInicio: '14:00', horaFin: '16:00' },
    { horaInicio: '16:00', horaFin: '18:00' },
    { horaInicio: '18:00', horaFin: '20:00' },
    { horaInicio: '20:00', horaFin: '22:00' }
  ];

  celdasSeleccionadas: Set<string> = new Set<string>();

  fechaActualNavegacion: Date = new Date();
  rangoSemanaTexto: string = '';
  fechaInputPicker: string = '';

  constructor(
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const urlActual: string = this.router.url.toLowerCase();
    if (urlActual.includes('admin') || urlActual.includes('administrador')) {
      this.esAdministrador = true;
    }

    this.route.queryParams.subscribe((params) => {
      if (params['esAdmin'] === 'true') {
        this.esAdministrador = true;
      }
    });

    this.actualizarRangoSemana();
  }

  actualizarRangoSemana(): void {
    const fecha: Date = new Date(this.fechaActualNavegacion);
    const diaSemana: number = fecha.getDay();

    const diferenciaLunes: number = fecha.getDate() - diaSemana + (diaSemana === 0 ? -6 : 1);

    const lunes: Date = new Date(fecha.setDate(diferenciaLunes));
    const domingo: Date = new Date(lunes);
    domingo.setDate(lunes.getDate() + 6);

    const opcionesDiaMes: Intl.DateTimeFormatOptions = { day: 'numeric', month: 'long' };
    const stringLunes: string = lunes.toLocaleDateString('es-ES', opcionesDiaMes);
    const stringDomingo: string = domingo.toLocaleDateString('es-ES', opcionesDiaMes);
    const anio: number = domingo.getFullYear();

    this.rangoSemanaTexto = `${stringLunes} – ${stringDomingo}, ${anio}`;
    this.fechaInputPicker = lunes.toISOString().split('T')[0];
  }

  cambiarSemana(direccion: number): void {
    this.fechaActualNavegacion.setDate(this.fechaActualNavegacion.getDate() + (direccion * 7));
    this.actualizarRangoSemana();
  }

  alSeleccionarFecha(event: Event): void {
    const inputElement: HTMLInputElement = event.target as HTMLInputElement;
    const valorFecha: string = inputElement.value;

    if (valorFecha) {
      const partes: string[] = valorFecha.split('-');
      this.fechaActualNavegacion = new Date(Number(partes[0]), Number(partes[1]) - 1, Number(partes[2]));
      this.actualizarRangoSemana();
    }
  }

  toggleCelda(diaIndex: number, horaIndex: number): void {
    if (this.horas[horaIndex].esAlmuerzo) {
      return;
    }

    const clave: string = `${diaIndex}-${horaIndex}`;
    if (this.celdasSeleccionadas.has(clave)) {
      this.celdasSeleccionadas.delete(clave);
    } else {
      this.celdasSeleccionadas.add(clave);
    }
  }

  isCeldaSeleccionada(diaIndex: number, horaIndex: number): boolean {
    return this.celdasSeleccionadas.has(`${diaIndex}-${horaIndex}`);
  }

  navegarA(ruta: string): void {
    this.router.navigate([ruta]);
  }

  guardarDisponibilidad(): void {
    const datosEnvio = {
      nombre: this.nombreCompleto,
      correo: this.correoInstitucional,
      especialidad: this.especialidad,
      tipoVinculacion: this.tipoVinculacion,
      contrasenia: this.esAdministrador ? this.contrasenia : null,
      semana: this.rangoSemanaTexto,
      horarios: Array.from(this.celdasSeleccionadas)
    };

    console.log('Registro guardado:', datosEnvio);
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
