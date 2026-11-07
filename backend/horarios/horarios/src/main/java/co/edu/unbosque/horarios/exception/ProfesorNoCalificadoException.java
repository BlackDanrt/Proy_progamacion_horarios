package co.edu.unbosque.horarios.exception;

/**
 * Excepción personalizada que se lanza cuando se intenta asignar un profesor 
 * a una materia o curso para el cual no cuenta con la cualificación, 
 * especialidad o permisos requeridos dentro del sistema de horarios.
 * 
 * <p>Esta clase extiende {@link RuntimeException}, por lo que se trata de una 
 * excepción no comprobada (unchecked exception).</p>
 * 
 * @author Prisma Web
 * @version 1.0
 */
public class ProfesorNoCalificadoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Construye una nueva instancia de {@code ProfesorNoCalificadoException} 
     * con el mensaje detallado especificado.
     * 
     * @param mensaje el mensaje detallado que explica la razón por la cual el profesor no está calificado
     */
    public ProfesorNoCalificadoException(String mensaje) {
        super(mensaje);
    }
}