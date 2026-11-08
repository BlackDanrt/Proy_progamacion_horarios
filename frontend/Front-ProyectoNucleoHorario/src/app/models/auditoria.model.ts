import { TipoAccionEnum } from '../enums/tipoAccion.enum';
import { ServicioEnum } from '../enums/servicio.enum';

export interface AuditoriaModel {
  id: number;
  idUsuario: number;
  nombreUsuario: string;
  accion: TipoAccionEnum;
  esAdmin: boolean;
  servicioUsado: ServicioEnum;
  fecha: string;
}
