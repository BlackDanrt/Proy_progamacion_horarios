export interface PersonaModel {
  id?: number;
  nombre: string;
  apellido: string;
  documento: number;
  correo: string;
  /** Solo se envía al crear o actualizar; el back nunca la devuelve. */
  contrasenia?: string;
}
