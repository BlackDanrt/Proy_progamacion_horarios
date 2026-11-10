package co.edu.unbosque.horarios.exception;

/**
 * Excepción personalizada que se lanza cuando la capacidad de un salón 
 * resulta insuficiente para albergar la cantidad de estudiantes o asistentes 
 * requeridos en el sistema de horarios.
 * 
 * <p>Esta clase extiende {@link RuntimeException}, por lo que se trata de una 
 * excepción no comprobada (unchecked exception).</p>
 * 
 * @author Prisma Web
 * @version 1.0
 */
public class CapacidadSalonInsuficienteException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye una nueva instancia de {@code CapacidadSalonInsuficienteException} 
     * con el mensaje detallado especificado.
     * 
     * @param mensaje el mensaje detallado que explica la razón por la cual la capacidad del salón es insuficiente
     */
    public CapacidadSalonInsuficienteException(String mensaje) {
        super(mensaje);
    }
}