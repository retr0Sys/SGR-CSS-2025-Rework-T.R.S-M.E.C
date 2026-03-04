package com.css.restaurante.modelo;

/**
 * Representa un empleado del restaurante con credenciales de acceso.
 * No almacena contraseña ni salt en el modelo — solo datos de sesión.
 */
public class Empleado {

    private int idEmpleado;
    private String usuario;
    private String nombre;
    private String apellido;
    private CargoEmpleado cargo;
    private boolean activo;

    public Empleado() {
    }

    public Empleado(int idEmpleado, String usuario, String nombre,
            String apellido, CargoEmpleado cargo, boolean activo) {
        this.idEmpleado = idEmpleado;
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cargo = cargo;
        this.activo = activo;
    }

    // ===== Getters & Setters =====

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
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

    public CargoEmpleado getCargo() {
        return cargo;
    }

    public void setCargo(CargoEmpleado cargo) {
        this.cargo = cargo;
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

    public boolean esGerente() {
        return cargo == CargoEmpleado.GERENTE;
    }

    public boolean esCajero() {
        return cargo == CargoEmpleado.CAJERO;
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " (" + cargo + ")";
    }
}
