import {Bloque} from '../enums/bloque.enum';

export interface SalonModel {
  id: number;
  bloque: Bloque;
  numeroSalon: number;
  capacidad: number;
  tieneComputadores: boolean;
  tieneSillasMoviles: boolean;
}
