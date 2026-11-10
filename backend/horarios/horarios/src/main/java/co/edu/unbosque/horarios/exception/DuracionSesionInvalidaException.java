package co.edu.unbosque.horarios.exception;

/**
 * Excepción personalizada que se lanza cuando se define o intenta registrar 
 * una duración de sesión que no cumple con los parámetros permitidos 
 * (por ejemplo, fuera del rango mínimo o máximo establecido) dentro del sistema de horarios.
 * 
 * <p>Esta clase extiende {@link RuntimeException}, por lo que se trata de una 
 * excepción no comprobada (unchecked exception).</p>
 * 
 * @author Prisma Web
 * @version 1.0
 */
public class DuracionSesionInvalidaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye una nueva instancia de {@code DuracionSesionInvalidaException} 
     * con el mensaje detallado especificado.
     * 
     * @param mensaje el mensaje detallado que explica la razón por la cual la duración de la sesión es inválida
     */
    public DuracionSesionInvalidaException(String mensaje) {
        super(mensaje);
    }
}