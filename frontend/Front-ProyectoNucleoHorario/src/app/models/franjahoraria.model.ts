import {dia} from '../enums/dia.enum';

export interface FranjahorariaModel {
  id: number;
  dia: dia;
  horaInicio: string;
  horaFin: string;
}
