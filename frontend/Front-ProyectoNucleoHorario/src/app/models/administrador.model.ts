import { PersonaModel } from './persona.model';

/** Datos de un administrador junto con la información personal heredada. */
export interface AdministradorModel extends PersonaModel {
  /** Código único asignado al administrador. */
  codigoAdministrador: string;
  /** Indica si la cuenta del administrador está activa. */
  estado: boolean;
}
