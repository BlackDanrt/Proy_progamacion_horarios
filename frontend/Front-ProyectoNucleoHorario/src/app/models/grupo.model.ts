export interface GrupoModel {
  id: number;
  asignatura: string;
  numeroGrupo: number;
  capacidadMinima: number;
  capacidadMaxima: number;
  profesor: string;
  sesiones: number;
  estado: boolean;
}
