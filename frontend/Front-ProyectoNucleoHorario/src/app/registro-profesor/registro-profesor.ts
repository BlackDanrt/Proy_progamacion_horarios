import { Component, OnInit } from '@angular/core';

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
  nombreCompleto: string = '';
  correoInstitucional: string = '';
  especialidad: string = '';
  tipoVinculacion: string = 'Tiempo completo';

  opcionesVinculacion: string[] = [
    'Tiempo completo',
    'Medio tiempo',
    'Cátedra',
    'Ocasional'
  ];

  // Matriz de Días (Lunes a Sábado)
  diasSemana: string[] = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'];

  // BLOQUES HORARIOS (7:00 AM a 10:00 PM con almuerzo de 1:00 PM a 2:00 PM)
  horas: BloqueHorario[] = [
    { horaInicio: '07:00', horaFin: '09:00' },
    { horaInicio: '09:00', horaFin: '11:00' },
    { horaInicio: '11:00', horaFin: '13:00' },
    { horaInicio: '13:00', horaFin: '14:00', esAlmuerzo: true }, // Franja no seleccionable
    { horaInicio: '14:00', horaFin: '16:00' },
    { horaInicio: '16:00', horaFin: '18:00' },
    { horaInicio: '18:00', horaFin: '20:00' },
    { horaInicio: '20:00', horaFin: '22:00' }
  ];

  // Inicia vacío: aquí se guardarán las celdas marcadas manualmente
  celdasSeleccionadas: Set<string> = new Set<string>();

  fechaActualNavegacion: Date = new Date();
  rangoSemanaTexto: string = '';
  fechaInputPicker: string = '';

  ngOnInit(): void {
    // Solo actualiza la semana actual al cargar
    this.actualizarRangoSemana();

    // NOTA: Se eliminó la carga previa de seleccionadasIniciales
    // para que el profesor inicie desde cero y seleccione sus horarios.
  }

  // --- MÉTODOS DEL CALENDARIO ---
  actualizarRangoSemana(): void {
    const fecha = new Date(this.fechaActualNavegacion);
    const diaSemana = fecha.getDay();

    const diferenciaLunes = fecha.getDate() - diaSemana + (diaSemana === 0 ? -6 : 1);

    const lunes = new Date(fecha.setDate(diferenciaLunes));
    const domingo = new Date(lunes);
    domingo.setDate(lunes.getDate() + 6);

    const opcionesDiaMes: Intl.DateTimeFormatOptions = { day: 'numeric', month: 'long' };
    const stringLunes = lunes.toLocaleDateString('es-ES', opcionesDiaMes);
    const stringDomingo = domingo.toLocaleDateString('es-ES', opcionesDiaMes);
    const anio = domingo.getFullYear();

    this.rangoSemanaTexto = `${stringLunes} – ${stringDomingo}, ${anio}`;
    this.fechaInputPicker = lunes.toISOString().split('T')[0];
  }

  cambiarSemana(direccion: number): void {
    this.fechaActualNavegacion.setDate(this.fechaActualNavegacion.getDate() + (direccion * 7));
    this.actualizarRangoSemana();
  }

  alSeleccionarFecha(event: any): void {
    const valorFecha = event.target.value;
    if (valorFecha) {
      const partes = valorFecha.split('-');
      this.fechaActualNavegacion = new Date(Number(partes[0]), Number(partes[1]) - 1, Number(partes[2]));
      this.actualizarRangoSemana();
    }
  }

  // --- MÉTODOS DE SELECCIÓN DE GRILLA ---
  toggleCelda(diaIndex: number, horaIndex: number): void {
    // Si es la franja de almuerzo, ignorar el clic
    if (this.horas[horaIndex].esAlmuerzo) {
      return;
    }

    const clave = `${diaIndex}-${horaIndex}`;
    if (this.celdasSeleccionadas.has(clave)) {
      this.celdasSeleccionadas.delete(clave);
    } else {
      this.celdasSeleccionadas.add(clave);
    }
  }

  isCeldaSeleccionada(diaIndex: number, horaIndex: number): boolean {
    return this.celdasSeleccionadas.has(`${diaIndex}-${horaIndex}`);
  }

  guardarDisponibilidad(): void {
    console.log('Semana:', this.rangoSemanaTexto);
    console.log('Bloques horarios seleccionados por el usuario:', Array.from(this.celdasSeleccionadas));
  }
}
