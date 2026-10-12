import {AsignaturaModel} from './asignatura.model';
import {FranjahorariaModel} from './franjahoraria.model';
import {TipoVinculacion} from '../enums/tipoViculacion.enum';
import {Escalafon} from '../enums/escalafon.enum';

export interface ProfesorModel {
  tipoVinculacion: TipoVinculacion;
  escalafon: Escalafon;
  especialidades: AsignaturaModel[];
  disponibilidadHoraria: FranjahorariaModel[];
  horasMaximasSemanales: number;
}
