package co.edu.unbosque.horarios.exception;

/**
 * Excepción personalizada que se lanza cuando se detecta una franja horaria
 * con datos, formato o valores inválidos dentro del sistema de horarios.
 *
 * <p>Esta excepción extiende {@link RuntimeException}, por lo que es una excepción
 * no verificada (<i>unchecked exception</i>) y no requiere ser declarada
 * explícitamente en la firma de los métodos que la lancen.</p>
 *
 * @version 1.0
 * @since 1.0
 * @see RuntimeException
 */
public class FranjaHorariaInvalidaException extends RuntimeException {

    /**
     * Construye una nueva instancia de {@code FranjaHorariaInvalidaException}
     * con el mensaje de detalle especificado.
     *
     * <p>El mensaje puede ser recuperado posteriormente mediante el método
     * {@link Throwable#getMessage()}.</p>
     *
     * @param mensaje descripción detallada del motivo por el cual la franja
     *                horaria se considera inválida; no debe ser {@code null}.
     */
    public FranjaHorariaInvalidaException(String mensaje) {
        super(mensaje);
    }
}