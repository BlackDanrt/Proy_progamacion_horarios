import { Bloque } from '../enums/bloque.enum';

/** Representa un salón y sus características físicas y de equipamiento. */
export interface SalonModel {
  /** Identificador único del salón, asignado por el sistema. */
  id?: number;
  /** Bloque del campus en el que se encuentra el salón. */
  bloque: Bloque;
  /** Número del salón dentro del bloque. */
  numeroSalon: number;
  /** Capacidad máxima de personas del salón. */
  capacidad: number;
  /** Indica si el salón cuenta con computadores. */
  tieneComputadores: boolean;
  /** Indica si el salón cuenta con sillas móviles. */
  tieneSillasMoviles: boolean;
}
