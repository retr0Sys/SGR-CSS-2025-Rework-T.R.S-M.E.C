package com.css.restaurante.ui;

import com.css.restaurante.modelo.CargoEmpleado;
import com.css.restaurante.modelo.Empleado;

/**
 * Singleton que gestiona la sesión del empleado actualmente logueado.
 * Centraliza la verificación de permisos por cargo.
 */
public final class SesionManager {

    private static Empleado empleadoActual;

    private SesionManager() {
    }

    /**
     * Inicia sesión con el empleado autenticado.
     */
    public static void iniciarSesion(Empleado empleado) {
        empleadoActual = empleado;
    }

    /**
     * Cierra la sesión actual.
     */
    public static void cerrarSesion() {
        empleadoActual = null;
    }

    /**
     * Retorna el empleado actualmente logueado, o null si no hay sesión.
     */
    public static Empleado getEmpleado() {
        return empleadoActual;
    }

    /**
     * Verifica si hay una sesión activa.
     */
    public static boolean haySesion() {
        return empleadoActual != null;
    }

    /**
     * Verifica si el usuario logueado es gerente.
     */
    public static boolean esGerente() {
        return empleadoActual != null && empleadoActual.getCargo() == CargoEmpleado.GERENTE;
    }

    /**
     * Verifica si el usuario logueado es cajero.
     */
    public static boolean esCajero() {
        return empleadoActual != null && empleadoActual.getCargo() == CargoEmpleado.CAJERO;
    }

    /**
     * Verifica si el usuario puede acceder al módulo de facturación (cajero o
     * gerente).
     */
    public static boolean puedeFacturar() {
        return esGerente() || esCajero();
    }

    /**
     * Retorna el nombre para mostrar del empleado logueado.
     */
    public static String getNombreDisplay() {
        if (empleadoActual == null)
            return "Sin sesión";
        return empleadoActual.getNombre();
    }
}
