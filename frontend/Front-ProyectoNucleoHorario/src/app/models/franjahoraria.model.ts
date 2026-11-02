import { Dia } from '../enums/dia.enum';

export interface FranjahorariaModel {
  id?: number;
  dia: Dia;
  /** Formato HH:mm o HH:mm:ss (el back devuelve HH:mm:ss). */
  horaInicio: string;
  horaFin: string;
}
