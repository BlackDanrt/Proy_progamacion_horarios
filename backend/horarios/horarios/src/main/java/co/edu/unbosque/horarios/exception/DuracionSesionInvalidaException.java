package co.edu.unbosque.horarios.exception;

public class DuracionSesionInvalidaException extends RuntimeException {
    public DuracionSesionInvalidaException(String mensaje) {
        super(mensaje);
    }
}