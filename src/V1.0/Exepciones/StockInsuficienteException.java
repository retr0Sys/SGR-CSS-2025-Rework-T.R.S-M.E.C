package Exepciones;

public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException() {
        super("Stock insuficiente para completar la operación.");
    }
}
