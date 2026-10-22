package co.edu.unbosque.horarios.exception;

public class CapacidadSalonInsuficienteException extends RuntimeException {
    public CapacidadSalonInsuficienteException(String mensaje) {
        super(mensaje);
    }
}