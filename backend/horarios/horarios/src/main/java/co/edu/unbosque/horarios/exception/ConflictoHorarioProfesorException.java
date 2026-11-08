package co.edu.unbosque.horarios.exception;

/**
 * Excepción personalizada que se lanza cuando se detecta un cruce o 
 * conflicto en el horario de un profesor (asignación simultánea en dos 
 * o más espacios) dentro del sistema de horarios.
 * 
 * <p>Esta clase extiende {@link RuntimeException}, por lo que se trata de una 
 * excepción no comprobada (unchecked exception).</p>
 * 
 * @author Prisma Web
 * @version 1.0
 */
public class ConflictoHorarioProfesorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye una nueva instancia de {@code ConflictoHorarioProfesorException} 
     * con el mensaje detallado especificado.
     * 
     * @param mensaje el mensaje detallado que explica la razón por la cual se presenta el conflicto de horario del profesor
     */
    public ConflictoHorarioProfesorException(String mensaje) {
        super(mensaje);
    }
}