package co.edu.unbosque.horarios.exception;

/**
 * Excepción personalizada que se lanza cuando los requisitos específicos de un salón 
 * (como equipos especiales, recursos tecnológicos o características de accesibilidad) 
 * no son válidos o no coinciden con lo solicitado dentro del sistema de horarios.
 * 
 * <p>Esta clase extiende {@link RuntimeException}, por lo que se trata de una 
 * excepción no comprobada (unchecked exception).</p>
 * 
 * @author Prisma Web
 * @version 1.0
 */
public class RequisitosSalonInvalidosException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye una nueva instancia de {@code RequisitosSalonInvalidosException} 
     * con el mensaje detallado especificado.
     * 
     * @param mensaje el mensaje detallado que explica la razón por la cual los requisitos del salón son inválidos
     */
    public RequisitosSalonInvalidosException(String mensaje) {
        super(mensaje);
    }
}