
package co.edu.unbosque.horarios.exception;

/**
 * Excepcion que se lanza cuando la cantidad de inscritos
 * se encuentra fuera del rango permitido.
 *
 * El valor debe estar entre 0 y 44.
 */
public class InscritosInvalidosException extends RuntimeException {

    /**
     * Constructor de la excepcion.
     *
     * @param mensaje descripcion del error.
     */
    public InscritosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
