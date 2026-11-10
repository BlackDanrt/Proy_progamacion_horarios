/** Datos personales compartidos por profesores y administradores. */
export interface PersonaModel {
  /** Identificador único de la persona, asignado por el sistema. */
  id?: number;
  /** Nombre de la persona. */
  nombre: string;
  /** Apellido de la persona. */
  apellido: string;
  /** Número de documento de identidad. */
  documento: number;
  /** Correo electrónico de contacto y acceso. */
  correo: string;
  /** Solo se envía al crear o actualizar; el back nunca la devuelve. */
  contrasenia?: string;
}
