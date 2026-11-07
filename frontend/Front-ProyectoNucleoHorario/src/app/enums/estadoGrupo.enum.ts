/** Estados posibles para un grupo académico. */
export enum EstadoGrupo {
  /** Grupo habilitado y en funcionamiento. */
  ACTIVO = 'ACTIVO',
  /** Grupo deshabilitado temporalmente. */
  INACTIVO = 'INACTIVO',
  /** Grupo cancelado. */
  CANCELADO = 'CANCELADO',
  /** Grupo que alcanzó su capacidad. */
  COMPLETO = 'COMPLETO'
}
