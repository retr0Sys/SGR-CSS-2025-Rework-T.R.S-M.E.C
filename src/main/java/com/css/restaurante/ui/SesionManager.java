package com.css.restaurante.ui;

import com.css.restaurante.modelo.CargoEmpleado;
import com.css.restaurante.modelo.Empleado;

/**
 * Singleton que gestiona la sesión del empleado actualmente logueado.
 * Centraliza la verificación de permisos por cargo.
 * Implementa timeout de sesión por inactividad (30 min configurables).
 */
public final class SesionManager {

    private static Empleado empleadoActual;
    private static long ultimaActividad;
    private static final long TIMEOUT_MS = 30 * 60 * 1000; // 30 minutos

    private SesionManager() {
    }

    /**
     * Inicia sesión con el empleado autenticado.
     */
    public static void iniciarSesion(Empleado empleado) {
        empleadoActual = empleado;
        ultimaActividad = System.currentTimeMillis();
    }

    /**
     * Cierra la sesión actual.
     */
    public static void cerrarSesion() {
        empleadoActual = null;
        ultimaActividad = 0;
    }

    /**
     * Registra actividad del usuario (resetea el timer de inactividad).
     * Debe llamarse al cambiar de módulo o realizar acciones.
     */
    public static void registrarActividad() {
        if (empleadoActual != null) {
            ultimaActividad = System.currentTimeMillis();
        }
    }

    /**
     * Verifica si la sesión sigue activa (no expirada por inactividad).
     * Retorna false si expiró o no hay sesión.
     */
    public static boolean verificarSesion() {
        if (empleadoActual == null) return false;
        long inactividad = System.currentTimeMillis() - ultimaActividad;
        if (inactividad > TIMEOUT_MS) {
            // Sesión expirada — registrar y limpiar
            AuditLogger.registrar(empleadoActual.getUsuario(), "SESION_EXPIRADA",
                    "Sesión expirada por inactividad (" + (inactividad / 60000) + " min)");
            cerrarSesion();
            return false;
        }
        return true;
    }

    /**
     * Retorna el empleado actualmente logueado, o null si no hay sesión.
     */
    public static Empleado getEmpleado() {
        return empleadoActual;
    }

    /**
     * Verifica si hay una sesión activa (sin verificar timeout).
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
