import { PersonaModel } from './persona.model';

export interface AdministradorModel extends PersonaModel {
  codigoAdministrador: string;
  estado: boolean;
}
