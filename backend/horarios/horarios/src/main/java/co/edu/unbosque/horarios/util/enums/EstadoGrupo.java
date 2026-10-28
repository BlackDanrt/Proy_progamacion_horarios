package co.edu.unbosque.horarios.util.enums;

/**
 * Enum que representa los posibles estados de un grupo dentro del sistema
 * de gestión de horarios.
 *
 * @author Prisma Web
 */
public enum EstadoGrupo {

    /** Indica que el grupo se encuentra activo. */
    ACTIVO,

    /** Indica que el grupo se encuentra inactivo. */
    INACTIVO,

    /** Indica que el grupo ha sido cancelado. */
    CANCELADO,

    /** Indica que el grupo ha alcanzado su capacidad máxima. */
    COMPLETO

}