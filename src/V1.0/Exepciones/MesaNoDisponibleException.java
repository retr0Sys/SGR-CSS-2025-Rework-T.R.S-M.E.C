package Exepciones;

// Esta exepción muestra un mensaje al ocurrir el error
public class MesaNoDisponibleException extends RuntimeException {
    public MesaNoDisponibleException() {
        super("Esta mesa no se encuentra disponible.");
    }
}
