
package co.edu.unbosque.horarios.exception;

/**
 * Excepcion que se lanza cuando un periodo academico
 * no cumple con el formato establecido.
 *
 * El formato permitido es AAAA-N, donde AAAA representa
 * un año de cuatro digitos y N corresponde a 1 o 2.
 *
 * Ejemplos validos: 2026-1, 2026-2.
 */
public class PeriodoInvalidoException extends RuntimeException {

    /**
     * Constructor de la excepcion.
     *
     * @param mensaje descripcion del error.
     */
    public PeriodoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
