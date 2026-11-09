export interface ParametrosSistemaModel {
  id: number;
  horaInicioEntreSemana: string;
  horaFinEntreSemana: string;
  horaInicioSabado: string;
  horaFinSabado: string;
  horaInicioAlmuerzo: string;
  horaFinAlmuerzo: string;
  maxSesionesPorSemana: number;
  duracionSesionMinutos: number;
  capacidadMaximaGrupo: number;
  toleranciaCapacidad: number;
  minEstudiantesParaAbrir: number;
  exigirDiasNoConsecutivos: boolean;
}
