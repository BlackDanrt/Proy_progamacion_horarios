export interface AsignaturaModel {
  id?: number;
  nombre: string;
  numCreditos: number;
  frecuenciaSemanal: number;
  requiereComputadores: boolean;
  requiereSillasMoviles: boolean;
}
