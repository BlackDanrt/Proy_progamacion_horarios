package co.edu.unbosque.horarios.exception;

public class RangoHorarioInvalidoException extends RuntimeException {
    public RangoHorarioInvalidoException(String mensaje) {
        super(mensaje);
    }
}