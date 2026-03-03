package com.css.restaurante.modelo;

/**
 * Estados posibles de un pedido en el flujo de cocina.
 */
public enum EstadoPedido {
    PENDIENTE("Pendiente"),
    EN_PREPARACION("En preparación"),
    SERVIDO("Servido"),
    CANCELADO("Cancelado");

    private final String valor;

    EstadoPedido(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoPedido fromString(String texto) {
        for (EstadoPedido e : values()) {
            if (e.valor.equalsIgnoreCase(texto)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado desconocido: " + texto);
    }

    @Override
    public String toString() {
        return valor;
    }
}
