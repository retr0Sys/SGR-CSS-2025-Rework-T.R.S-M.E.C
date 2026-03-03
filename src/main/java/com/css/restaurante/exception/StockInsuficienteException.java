package com.css.restaurante.exception;

/**
 * Excepción lanzada cuando se intenta realizar una operación
 * que requiere más stock del disponible.
 */
public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException() {
        super("Stock insuficiente para realizar la operación.");
    }

    public StockInsuficienteException(String nombreProducto, int stockActual, int cantidadSolicitada) {
        super(String.format("Stock insuficiente para '%s': disponible %d, solicitado %d.",
                nombreProducto, stockActual, cantidadSolicitada));
    }
}
