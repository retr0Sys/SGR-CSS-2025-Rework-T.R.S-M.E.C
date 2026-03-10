package com.css.restaurante.ui;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Logger de auditoría para eventos de seguridad del SGR.
 * Registra acciones sensibles en un archivo de log rotado por día.
 * Formato: [AUDIT] timestamp | usuario | acción | detalle
 *
 * NO registra contraseñas, hashes, ni datos sensibles.
 */
public final class AuditLogger {

    private static final String LOG_DIR = Paths.get(
            System.getProperty("user.home"), "Documents", "SGR_Logs").toString();

    private AuditLogger() {
    }

    /**
     * Registra un evento de auditoría.
     *
     * @param usuario  Nombre de usuario que realizó la acción (o "sistema")
     * @param accion   Tipo de acción (LOGIN_OK, LOGIN_FAIL, CREAR_EMPLEADO, etc.)
     * @param detalle  Detalle adicional (sin datos sensibles)
     */
    public static void registrar(String usuario, String accion, String detalle) {
        try {
            File dir = new File(LOG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String archivo = Paths.get(LOG_DIR, "audit_" + fecha + ".log").toString();

            try (PrintWriter pw = new PrintWriter(new FileWriter(archivo, true))) {
                // Sanitizar datos para evitar inyección de logs
                String usuarioLimpio = sanitizarLog(usuario);
                String accionLimpia = sanitizarLog(accion);
                String detalleLimpio = sanitizarLog(detalle);
                pw.println("[AUDIT] " + timestamp + " | " + usuarioLimpio
                        + " | " + accionLimpia + " | " + detalleLimpio);
            }
        } catch (Exception e) {
            // Si falla el logging, no interrumpir la aplicación
            System.err.println("[SGR] Error al escribir log de auditoría.");
        }
    }

    /**
     * Sanitiza un valor para prevenir inyección de logs (CRLF injection).
     */
    private static String sanitizarLog(String valor) {
        if (valor == null) return "-";
        return valor.replaceAll("[\\r\\n|]", "_").trim();
    }

    // ===== Métodos de conveniencia =====

    public static void loginExitoso(String usuario) {
        registrar(usuario, "LOGIN_OK", "Inicio de sesión exitoso");
    }

    public static void loginFallido(String usuario) {
        registrar(usuario != null ? usuario : "desconocido", "LOGIN_FAIL",
                "Intento de inicio de sesión fallido");
    }

    public static void cuentaBloqueada(String usuario) {
        registrar(usuario, "CUENTA_BLOQUEADA",
                "Cuenta bloqueada por exceso de intentos");
    }

    public static void cerrarSesion(String usuario) {
        registrar(usuario, "LOGOUT", "Cierre de sesión");
    }

    public static void crearEmpleado(String ejecutor, String nuevoUsuario) {
        registrar(ejecutor, "CREAR_EMPLEADO", "Nuevo empleado: " + nuevoUsuario);
    }

    public static void eliminarEmpleado(String ejecutor, int idEmpleado) {
        registrar(ejecutor, "ELIMINAR_EMPLEADO", "ID eliminado: " + idEmpleado);
    }

    public static void cambiarContrasena(String ejecutor, int idEmpleado) {
        registrar(ejecutor, "CAMBIAR_CONTRASENA", "ID empleado: " + idEmpleado);
    }

    public static void cambiarEstadoEmpleado(String ejecutor, int idEmpleado, boolean nuevoEstado) {
        registrar(ejecutor, "CAMBIAR_ESTADO_EMPLEADO",
                "ID: " + idEmpleado + " → " + (nuevoEstado ? "activo" : "inactivo"));
    }
}
