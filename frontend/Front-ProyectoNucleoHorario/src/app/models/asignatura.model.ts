/** Información académica y requisitos de espacio de una asignatura. */
export interface AsignaturaModel {
  /** Identificador único de la asignatura, asignado por el sistema. */
  id?: number;
  /** Nombre de la asignatura. */
  nombre: string;
  /** Cantidad de créditos académicos de la asignatura. */
  numCreditos: number;
  /** Número de sesiones semanales que debe tener la asignatura. */
  frecuenciaSemanal: number;
  /** Indica si la asignatura requiere un salón con computadores. */
  requiereComputadores: boolean;
  /** Indica si la asignatura requiere un salón con sillas móviles. */
  requiereSillasMoviles: boolean;
}
