package co.edu.unbosque.horarios.exception;

/**
 * Excepción personalizada que se lanza cuando se define o intenta registrar 
 * un rango de horario que no es válido (por ejemplo, cuando la hora de inicio 
 * es posterior o igual a la hora de fin) dentro del sistema de horarios.
 * 
 * <p>Esta clase extiende {@link RuntimeException}, por lo que se trata de una 
 * excepción no comprobada (unchecked exception).</p>
 * 
 * @author Prisma Web
 * @version 1.0
 */
public class RangoHorarioInvalidoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye una nueva instancia de {@code RangoHorarioInvalidoException} 
     * con el mensaje detallado especificado.
     * 
     * @param mensaje el mensaje detallado que explica la razón por la cual el rango horario es inválido
     */
    public RangoHorarioInvalidoException(String mensaje) {
        super(mensaje);
    }
}