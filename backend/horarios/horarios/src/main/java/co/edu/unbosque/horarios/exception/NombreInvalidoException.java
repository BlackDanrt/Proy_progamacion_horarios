
package co.edu.unbosque.horarios.exception;

/**
 * Excepción personalizada que se lanza cuando se detecta un nombre con formato
 * o valor inválido dentro del sistema de mensajería.
 *
 * <p>Esta excepción extiende {@link RuntimeException}, por lo que es una excepción
 * no verificada (<i>unchecked exception</i>) y no requiere ser declarada
 * explícitamente en la firma de los métodos que la lancen.</p>
 *
 * @version 1.0
 * @since 1.0
 * @see RuntimeException
 */
public class NombreInvalidoException extends RuntimeException {

    /**
     * Construye una nueva instancia de {@code NombreInvalidoException} con el
     * mensaje de detalle especificado.
     *
     * <p>El mensaje puede ser recuperado posteriormente mediante el método
     * {@link Throwable#getMessage()}.</p>
     *
     * @param mensaje descripción detallada del motivo por el cual el nombre
     *                se considera inválido; no debe ser {@code null}.
     */
    public NombreInvalidoException(String mensaje) {
        super(mensaje);
    }
}
