import { AsignaturaModel } from './asignatura.model';
import { ProfesorModel } from './profesor.model';
import { SalonModel } from './salon.model';
import { FranjahorariaModel } from './franjahoraria.model';
import { EstadoGrupo } from '../enums/estadoGrupo.enum';

/** Representa un grupo académico y sus recursos y sesiones asignados. */
export interface GrupoModel {
  /** Identificador único del grupo, asignado por el sistema. */
  id?: number;
  /** Asignatura que se dicta en el grupo. */
  asignatura: AsignaturaModel;
  /** Número que distingue al grupo dentro de la asignatura. */
  numeroGrupo: number;
  /** Cantidad mínima de estudiantes requerida para mantener el grupo. */
  capacidadMinima: number;
  /** Cupos del grupo: máximo 44 (40 + 10 % de tolerancia) y sin superar la capacidad del salón. */
  capacidadMaxima: number;
  /** Profesor responsable de dictar el grupo. */
  profesor: ProfesorModel;
  /** Salón asignado para las sesiones del grupo. */
  salon: SalonModel;
  /** Franjas horarias programadas para el grupo. */
  sesiones: FranjahorariaModel[];
  /** Estado actual del grupo. */
  estado: EstadoGrupo;
  /** Semestre al que pertenece el grupo, por ejemplo "2026-2". [BACK pendiente] */
  periodo?: string;
  /** Estudiantes inscritos; si es menor al mínimo el grupo debe cerrarse. [BACK pendiente] */
  inscritos?: number;
}
