import { PersonaModel } from './persona.model';
import { FranjahorariaModel } from './franjahoraria.model';
import { TipoVinculacion } from '../enums/tipoViculacion.enum';
import { Escalafon } from '../enums/escalafon.enum';
import { Especialidad } from '../enums/especialidad.enum';

export interface ProfesorModel extends PersonaModel {
  tipoVinculacion: TipoVinculacion;
  escalafon: Escalafon;
  especialidades: Especialidad[];
  disponibilidadHoraria: FranjahorariaModel[];
  horasMaximasSemanales: number;
}
