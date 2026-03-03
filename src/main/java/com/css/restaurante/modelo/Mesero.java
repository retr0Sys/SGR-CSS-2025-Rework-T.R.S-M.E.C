package com.css.restaurante.modelo;

/**
 * Representa a un mesero del restaurante.
 */
public class Mesero {

    private int idMesero;
    private String nombre;
    private String apellido;
    private boolean activo;

    public Mesero() {
    }

    public Mesero(int idMesero, String nombre, String apellido, boolean activo) {
        this.idMesero = idMesero;
        this.nombre = nombre;
        this.apellido = apellido;
        this.activo = activo;
    }

    // ===== Getters & Setters =====

    public int getIdMesero() {
        return idMesero;
    }

    public void setIdMesero(int idMesero) {
        this.idMesero = idMesero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}
