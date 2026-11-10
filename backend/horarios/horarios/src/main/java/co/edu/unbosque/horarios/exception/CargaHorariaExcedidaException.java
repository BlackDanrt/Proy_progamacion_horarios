package co.edu.unbosque.horarios.exception;

/**
 * Excepción personalizada que se lanza cuando se intenta asignar o registrar 
 * una carga horaria que excede el límite máximo permitido (para un docente, estudiante 
 * o programa) dentro del sistema de horarios.
 * 
 * <p>Esta clase extiende {@link RuntimeException}, por lo que se trata de una 
 * excepción no comprobada (unchecked exception).</p>
 * 
 * @author Prisma Web
 * @version 1.0
 */
public class CargaHorariaExcedidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye una nueva instancia de {@code CargaHorariaExcedidaException} 
     * con el mensaje detallado especificado.
     * 
     * @param mensaje el mensaje detallado que explica la razón por la cual se ha excedido la carga horaria
     */
    public CargaHorariaExcedidaException(String mensaje) {
        super(mensaje);
    }
}