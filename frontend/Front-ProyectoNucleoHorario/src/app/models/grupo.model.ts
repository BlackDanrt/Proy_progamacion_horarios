import {AsignaturaModel} from './asignatura.model';
import {ProfesorModel} from './profesor.model';
import {FranjahorariaModel} from './franjahoraria.model';
import {EstadoGrupo} from '../enums/estadoGrupo.enum';

export interface GrupoModel {
  id: number;
  asignatura: AsignaturaModel;
  numeroGrupo: number;
  capacidadMinima: number;
  capacidadMaxima: number;
  profesor: ProfesorModel;
  sesiones: FranjahorariaModel[];
  estado: EstadoGrupo;
}
