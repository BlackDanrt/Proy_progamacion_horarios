package co.edu.unbosque.horarios.exception;

/**
 * Excepción personalizada que se lanza cuando se define o intenta registrar 
 * una frecuencia de sesiones que no cumple con las reglas o parámetros permitidos 
 * dentro del sistema de horarios.
 * 
 * <p>Esta clase extiende {@link RuntimeException}, por lo que se trata de una 
 * excepción no comprobada (unchecked exception).</p>
 * 
 * @author Prisma Web
 * @version 1.0
 */
public class SesionesFrecuenciaInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye una nueva instancia de {@code SesionesFrecuenciaInvalidaException} 
     * con el mensaje detallado especificado.
     * 
     * @param mensaje el mensaje detallado que explica la razón por la cual la frecuencia de las sesiones es inválida
     */
    public SesionesFrecuenciaInvalidaException(String mensaje) {
        super(mensaje);
    }
}