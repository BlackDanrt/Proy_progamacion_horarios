package co.edu.unbosque.horarios.exception;

/**
 * Excepción personalizada que se lanza cuando se intenta superar la 
 * capacidad máxima permitida (por ejemplo, en salones, grupos o cupos) 
 * dentro del sistema de horarios.
 * 
 * <p>Esta clase extiende {@link RuntimeException}, por lo que se trata de una 
 * excepción no comprobada (unchecked exception).</p>
 * 
 * @author Prisma Web
 * @version 1.0
 */
public class CapacidadMaximaExcedidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye una nueva instancia de {@code CapacidadMaximaExcedidaException} 
     * con el mensaje detallado especificado.
     * 
     * @param mensaje el mensaje detallado que explica la razón por la cual se ha excedido la capacidad máxima
     */
    public CapacidadMaximaExcedidaException(String mensaje) {
        super(mensaje);
    }
}