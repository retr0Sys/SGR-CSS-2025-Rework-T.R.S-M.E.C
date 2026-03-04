package com.css.restaurante.modelo;

/**
 * Cargos posibles para un empleado del sistema.
 * Determina el nivel de acceso y las funcionalidades disponibles.
 */
public enum CargoEmpleado {
    MESERO("mesero"),
    CAJERO("cajero"),
    GERENTE("gerente");

    private final String valor;

    CargoEmpleado(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    /**
     * Convierte un string de la BD al enum correspondiente.
     */
    public static CargoEmpleado fromString(String texto) {
        for (CargoEmpleado c : values()) {
            if (c.valor.equalsIgnoreCase(texto)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Cargo desconocido: " + texto);
    }

    @Override
    public String toString() {
        return valor;
    }
}
