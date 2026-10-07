package co.edu.unbosque.horarios.exception;

/**
 * Excepción personalizada que se lanza cuando se detecta una contraseña que no cumple
 * con los criterios de seguridad o formato establecidos en el sistema de mensajería.
 *
 * <p>Esta excepción extiende {@link RuntimeException}, por lo que es una excepción
 * no verificada (unchecked exception).</p>
 *
 * @version 1.0
 * @see RuntimeException
 */
public class ContraseniaInvalidaException extends RuntimeException {

	/**
	 * Identificador único de serialización para la clase.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construye una nueva instancia de {@code ContraseniaInvalidaException} con el
	 * mensaje de detalle especificado.
	 *
	 * @param mensaje descripción detallada del motivo por el cual la contraseña
	 * se considera inválida.
	 */
	public ContraseniaInvalidaException(String mensaje) {
		super(mensaje);
	}
}