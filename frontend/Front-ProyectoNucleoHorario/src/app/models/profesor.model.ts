import { PersonaModel } from './persona.model';
import { FranjahorariaModel } from './franjahoraria.model';
import { TipoVinculacion } from '../enums/tipoViculacion.enum';
import { Escalafon } from '../enums/escalafon.enum';
import { Especialidad } from '../enums/especialidad.enum';

/** Datos de un profesor, incluidos su perfil académico y disponibilidad. */
export interface ProfesorModel extends PersonaModel {
  /** Tipo de vinculación laboral del profesor. */
  tipoVinculacion: TipoVinculacion;
  /** Nivel del profesor dentro del escalafón institucional. */
  escalafon: Escalafon;
  /** Áreas o temas de especialidad del profesor. */
  especialidades: Especialidad[];
  /** Franjas horarias en las que el profesor está disponible. */
  disponibilidadHoraria: FranjahorariaModel[];
  /** Máximo de horas que puede dictar el profesor por semana. */
  horasMaximasSemanales: number;
}
