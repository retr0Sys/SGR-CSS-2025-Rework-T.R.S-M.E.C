package com.css.restaurante.modelo;

/**
 * Categorías de productos del restaurante.
 */
public enum CategoriaProducto {
    COMIDA("comida"),
    BEBIDA("bebida"),
    POSTRE("postre");

    private final String valor;

    CategoriaProducto(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    /**
     * Convierte un String a CategoriaProducto (case-insensitive).
     */
    public static CategoriaProducto fromString(String texto) {
        for (CategoriaProducto cat : values()) {
            if (cat.valor.equalsIgnoreCase(texto)) {
                return cat;
            }
        }
        throw new IllegalArgumentException("Categoría desconocida: " + texto);
    }

    @Override
    public String toString() {
        return valor.substring(0, 1).toUpperCase() + valor.substring(1);
    }
}
