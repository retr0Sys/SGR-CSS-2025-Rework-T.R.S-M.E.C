package com.css.restaurante.dao;

import com.css.restaurante.modelo.CargoEmpleado;
import com.css.restaurante.modelo.Empleado;
import com.css.restaurante.ui.InputValidator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Arrays;

/**
 * DAO para autenticación de empleados.
 * Implementa SHA-256 + salt, comparación de tiempo constante,
 * y rate limiting (bloqueo tras 5 intentos fallidos en 1 minuto).
 */
public class EmpleadoDAO {

    private static final int MAX_INTENTOS = 5;
    private static final long COOLDOWN_MS = 60_000; // 1 minuto

    /**
     * Autentica un empleado por usuario y contraseña.
     * Retorna el Empleado si las credenciales son válidas, null si no.
     * Implementa protección contra timing attacks y rate limiting.
     */
    public Empleado autenticar(String usuario, char[] contrasena) throws SQLException {
        // Sanitizar entrada
        String usuarioLimpio = InputValidator.sanitizar(usuario);
        if (usuarioLimpio.isEmpty() || contrasena == null || contrasena.length == 0) {
            return null;
        }

        String sql = "SELECT id_empleado, usuario, contrasena_hash, salt, nombre, apellido, " +
                "cargo, activo, intentos_fallidos, ultimo_intento " +
                "FROM empleado WHERE usuario = ?";

        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuarioLimpio);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    // Realizar hash dummy para evitar timing attack (usuario no existe)
                    hashSHA256("dummy_salt", contrasena);
                    return null;
                }

                int idEmpleado = rs.getInt("id_empleado");
                boolean activo = rs.getBoolean("activo");
                int intentosFallidos = rs.getInt("intentos_fallidos");
                Timestamp ultimoIntento = rs.getTimestamp("ultimo_intento");

                // Verificar si la cuenta está activa
                if (!activo) {
                    return null;
                }

                // Rate limiting: verificar bloqueo
                if (estaBloqueado(intentosFallidos, ultimoIntento)) {
                    return null;
                }

                String hashAlmacenado = rs.getString("contrasena_hash");
                String salt = rs.getString("salt");
                String hashCalculado = hashSHA256(salt, contrasena);

                // Comparación de tiempo constante
                if (MessageDigest.isEqual(
                        hashAlmacenado.getBytes(StandardCharsets.UTF_8),
                        hashCalculado.getBytes(StandardCharsets.UTF_8))) {
                    // Login exitoso — resetear intentos
                    resetearIntentos(idEmpleado);
                    return new Empleado(
                            idEmpleado,
                            rs.getString("usuario"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            CargoEmpleado.fromString(rs.getString("cargo")),
                            activo);
                } else {
                    // Login fallido — incrementar intentos
                    incrementarIntentos(idEmpleado);
                    return null;
                }
            }
        } finally {
            // Limpiar contraseña de la memoria
            Arrays.fill(contrasena, '\0');
        }
    }

    /**
     * Verifica si la cuenta está bloqueada por rate limiting.
     */
    private boolean estaBloqueado(int intentosFallidos, Timestamp ultimoIntento) {
        if (intentosFallidos < MAX_INTENTOS) {
            return false;
        }
        if (ultimoIntento == null) {
            return false;
        }
        long tiempoDesdeUltimoIntento = System.currentTimeMillis() - ultimoIntento.getTime();
        return tiempoDesdeUltimoIntento < COOLDOWN_MS;
    }

    /**
     * Verifica si el usuario está actualmente bloqueado (para mostrar en UI).
     */
    public boolean verificarBloqueo(String usuario) throws SQLException {
        String sql = "SELECT intentos_fallidos, ultimo_intento FROM empleado WHERE usuario = ?";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, InputValidator.sanitizar(usuario));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return estaBloqueado(rs.getInt("intentos_fallidos"),
                            rs.getTimestamp("ultimo_intento"));
                }
            }
        }
        return false;
    }

    /**
     * Resetea el contador de intentos fallidos tras un login exitoso.
     */
    private void resetearIntentos(int idEmpleado) throws SQLException {
        String sql = "UPDATE empleado SET intentos_fallidos = 0, ultimo_intento = NULL WHERE id_empleado = ?";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.executeUpdate();
        }
    }

    /**
     * Incrementa el contador de intentos fallidos.
     */
    private void incrementarIntentos(int idEmpleado) throws SQLException {
        String sql = "UPDATE empleado SET intentos_fallidos = intentos_fallidos + 1, " +
                "ultimo_intento = NOW() WHERE id_empleado = ?";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.executeUpdate();
        }
    }

    /**
     * Calcula SHA-256(salt + contraseña) y retorna el hash en hexadecimal.
     */
    static String hashSHA256(String salt, char[] contrasena) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            // Construir salt + contraseña sin crear String de la contraseña
            byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
            byte[] passBytes = new String(contrasena).getBytes(StandardCharsets.UTF_8);
            byte[] entrada = new byte[saltBytes.length + passBytes.length];
            System.arraycopy(saltBytes, 0, entrada, 0, saltBytes.length);
            System.arraycopy(passBytes, 0, entrada, saltBytes.length, passBytes.length);
            byte[] hash = md.digest(entrada);
            // Limpiar bytes intermedios
            Arrays.fill(passBytes, (byte) 0);
            Arrays.fill(entrada, (byte) 0);
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 no disponible", e);
        }
    }

    /**
     * Convierte un array de bytes a su representación hexadecimal.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
