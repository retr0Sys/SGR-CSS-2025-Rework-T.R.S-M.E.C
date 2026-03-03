package com.css.restaurante.modelo;

/**
 * Representa una mesa física del restaurante.
 */
public class Mesa {

    private int idMesa;
    private int capacidad;
    private String estado; // Libre, Ocupada, Reservada, Limpieza
    private Integer idMesero; // Puede ser null si no hay mesero asignado

    public Mesa() {}

    public Mesa(int idMesa, int capacidad, String estado) {
        this.idMesa = idMesa;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    public Mesa(int idMesa, int capacidad, String estado, Integer idMesero) {
        this(idMesa, capacidad, estado);
        this.idMesero = idMesero;
    }

    // ===== Getters & Setters =====

    public int getIdMesa() { return idMesa; }
    public void setIdMesa(int idMesa) { this.idMesa = idMesa; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getIdMesero() { return idMesero; }
    public void setIdMesero(Integer idMesero) { this.idMesero = idMesero; }

    public boolean isLibre() { return "Libre".equalsIgnoreCase(estado); }
    public boolean isOcupada() { return "Ocupada".equalsIgnoreCase(estado); }

    @Override
    public String toString() {
        return "Mesa " + idMesa + " (Cap: " + capacidad + " | " + estado + ")";
    }
}
