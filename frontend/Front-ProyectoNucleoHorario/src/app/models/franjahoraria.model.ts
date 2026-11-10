import { Dia } from '../enums/dia.enum';

/** Representa un intervalo horario asociado a un día de la semana. */
export interface FranjahorariaModel {
  /** Identificador único de la franja, asignado por el sistema. */
  id?: number;
  /** Día en el que ocurre la franja. */
  dia: Dia;
  /** Formato HH:mm o HH:mm:ss (el back devuelve HH:mm:ss). */
  horaInicio: string;
  /** Hora en la que termina la franja, en formato HH:mm o HH:mm:ss. */
  horaFin: string;
}
