package co.edu.unbosque.horarios.exception;

public class CargaHorariaExcedidaException extends RuntimeException {
    public CargaHorariaExcedidaException(String mensaje) {
        super(mensaje);
    }
}