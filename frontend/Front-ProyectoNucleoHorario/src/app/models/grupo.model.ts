import { AsignaturaModel } from './asignatura.model';
import { ProfesorModel } from './profesor.model';
import { SalonModel } from './salon.model';
import { FranjahorariaModel } from './franjahoraria.model';
import { EstadoGrupo } from '../enums/estadoGrupo.enum';

export interface GrupoModel {
  id?: number;
  asignatura: AsignaturaModel;
  numeroGrupo: number;
  capacidadMinima: number;
  /** Cupos del grupo: máximo 44 (40 + 10 % de tolerancia) y sin superar la capacidad del salón. */
  capacidadMaxima: number;
  profesor: ProfesorModel;
  salon: SalonModel;
  sesiones: FranjahorariaModel[];
  estado: EstadoGrupo;
  /** Semestre al que pertenece el grupo, por ejemplo "2026-2". [BACK pendiente] */
  periodo?: string;
  /** Estudiantes inscritos; si es menor al mínimo el grupo debe cerrarse. [BACK pendiente] */
  inscritos?: number;
}
