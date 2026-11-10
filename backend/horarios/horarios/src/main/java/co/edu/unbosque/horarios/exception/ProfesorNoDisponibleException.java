package co.edu.unbosque.horarios.exception;

/**
 * Excepción personalizada que se lanza cuando se intenta asignar o programar 
 * a un profesor en un horario o fecha en la cual no se encuentra disponible 
 * (debido a licencias, restricciones de jornada u otros compromisos) dentro del sistema de horarios.
 * 
 * <p>Esta clase extiende {@link RuntimeException}, por lo que se trata de una 
 * excepción no comprobada (unchecked exception).</p>
 * 
 * @author Prisma Web
 * @version 1.0
 */
public class ProfesorNoDisponibleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye una nueva instancia de {@code ProfesorNoDisponibleException} 
     * con el mensaje detallado especificado.
     * 
     * @param mensaje el mensaje detallado que explica la razón por la cual el profesor no está disponible
     */
    public ProfesorNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}