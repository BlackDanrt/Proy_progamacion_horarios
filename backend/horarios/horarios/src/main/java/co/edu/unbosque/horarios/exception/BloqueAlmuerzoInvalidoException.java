package co.edu.unbosque.horarios.exception;

/**
 * Excepción personalizada que se lanza cuando se detecta o intenta configurar
 * un bloque de almuerzo inválido dentro del sistema de horarios.
 * 
 * <p>Esta clase extiende {@link RuntimeException}, por lo que se trata de una 
 * excepción no comprobada (unchecked exception).</p>
 * 
 * @author Prisma Web
 * @version 1.0
 */
public class BloqueAlmuerzoInvalidoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye una nueva instancia de {@code BloqueAlmuerzoInvalidoException} 
     * con el mensaje detallado especificado.
     * 
     * @param mensaje el mensaje detallado que explica la razón por la cual el bloque de almuerzo es inválido
     */
    public BloqueAlmuerzoInvalidoException(String mensaje) {
        super(mensaje);
    }
}