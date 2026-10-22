package co.edu.unbosque.horarios.exception;

public class DiasConsecutivosInvalidoException extends RuntimeException {
    public DiasConsecutivosInvalidoException(String mensaje) {
        super(mensaje);
    }
}