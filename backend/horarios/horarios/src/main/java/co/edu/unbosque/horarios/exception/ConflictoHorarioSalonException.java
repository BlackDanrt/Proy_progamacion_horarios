package co.edu.unbosque.horarios.exception;

public class ConflictoHorarioSalonException extends RuntimeException {
    public ConflictoHorarioSalonException(String mensaje) {
        super(mensaje);
    }
}