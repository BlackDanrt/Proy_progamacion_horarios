package co.edu.unbosque.horarios.exception;

public class ProfesorNoCalificadoException extends RuntimeException {
    public ProfesorNoCalificadoException(String mensaje) {
        super(mensaje);
    }
}