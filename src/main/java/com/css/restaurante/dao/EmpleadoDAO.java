package com.css.restaurante.dao;

import com.css.restaurante.modelo.CargoEmpleado;
import com.css.restaurante.modelo.Empleado;
import com.css.restaurante.ui.InputValidator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
            // Convertir char[] a byte[] SIN crear String intermedio (inmutable en heap)
            java.nio.CharBuffer charBuf = java.nio.CharBuffer.wrap(contrasena);
            java.nio.ByteBuffer byteBuf = StandardCharsets.UTF_8.encode(charBuf);
            byte[] passBytes = new byte[byteBuf.remaining()];
            byteBuf.get(passBytes);
            // Limpiar buffer intermedio
            Arrays.fill(byteBuf.array(), (byte) 0);

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

    // ===== GESTIÓN DE EMPLEADOS =====

    /**
     * Devuelve una lista de todos los empleados.
     */
    public List<Empleado> listar() throws SQLException {
        String sql = "SELECT id_empleado, usuario, nombre, apellido, cargo, activo FROM empleado ORDER BY id_empleado";
        List<Empleado> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.getConnection();
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Empleado(
                        rs.getInt("id_empleado"),
                        rs.getString("usuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        CargoEmpleado.fromString(rs.getString("cargo")),
                        rs.getBoolean("activo")));
            }
        }
        return lista;
    }

    /**
     * Busca un empleado por su nombre de usuario, insensible a
     * mayúsculas/minúsculas.
     */
    public Empleado buscarPorUsuario(String usuario) throws SQLException {
        String sql = "SELECT id_empleado, usuario, nombre, apellido, cargo, activo FROM empleado WHERE LOWER(usuario) = LOWER(?)";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Empleado(
                            rs.getInt("id_empleado"),
                            rs.getString("usuario"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            CargoEmpleado.fromString(rs.getString("cargo")),
                            rs.getBoolean("activo"));
                }
            }
        }
        return null;
    }

    /**
     * Genera un salt aleatorio seguro de 16 bytes.
     */
    private String generarSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return bytesToHex(salt);
    }

    /**
     * Crea un nuevo empleado e inicializa su historial de contraseñas.
     */
    public void crear(Empleado e, char[] contrasena) throws SQLException {
        String sqlEmpleado = "INSERT INTO empleado (usuario, contrasena_hash, salt, nombre, apellido, cargo, activo, intentos_fallidos) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 0)";
        String sqlHistorial = "INSERT INTO historial_contrasena (id_empleado, contrasena_hash, salt) VALUES (?, ?, ?)";

        String salt = generarSalt();
        String hash = hashSHA256(salt, contrasena);

        try (Connection cn = ConexionDB.getConnection()) {
            cn.setAutoCommit(false); // Iniciar transacción
            try (PreparedStatement psEmpleado = cn.prepareStatement(sqlEmpleado, Statement.RETURN_GENERATED_KEYS)) {
                psEmpleado.setString(1, e.getUsuario());
                psEmpleado.setString(2, hash);
                psEmpleado.setString(3, salt);
                psEmpleado.setString(4, e.getNombre());
                psEmpleado.setString(5, e.getApellido());
                psEmpleado.setString(6, e.getCargo().getValor());
                psEmpleado.setBoolean(7, e.isActivo());

                psEmpleado.executeUpdate();

                try (ResultSet keys = psEmpleado.getGeneratedKeys()) {
                    if (keys.next()) {
                        e.setIdEmpleado(keys.getInt(1));
                    }
                }

                // Guardar primer registro en historial
                try (PreparedStatement psHistorial = cn.prepareStatement(sqlHistorial)) {
                    psHistorial.setInt(1, e.getIdEmpleado());
                    psHistorial.setString(2, hash);
                    psHistorial.setString(3, salt);
                    psHistorial.executeUpdate();
                }

                cn.commit(); // Confirmar transacción
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(true);
            }
        } finally {
            Arrays.fill(contrasena, '\0');
        }
    }

    /**
     * Elimina físicamente un empleado por su ID.
     */
    public void eliminar(int idEmpleado) throws SQLException {
        String sql = "DELETE FROM empleado WHERE id_empleado = ?";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.executeUpdate();
        }
    }

    /**
     * Cambia el estado (activo/inactivo) de un empleado.
     */
    public void cambiarEstado(int idEmpleado, boolean activo) throws SQLException {
        String sql = "UPDATE empleado SET activo = ? WHERE id_empleado = ?";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, idEmpleado);
            ps.executeUpdate();
        }
    }

    /**
     * Verifica si una contraseña ya fue utilizada recientemente (últimas 10
     * contraseñas).
     */
    public boolean esContrasenaRepetida(int idEmpleado, char[] nuevaContrasena) throws SQLException {
        String sql = "SELECT contrasena_hash, salt FROM historial_contrasena WHERE id_empleado = ? ORDER BY fecha_cambio DESC LIMIT 10";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String hashGuardado = rs.getString("contrasena_hash");
                    String saltGuardado = rs.getString("salt");
                    String inputHash = hashSHA256(saltGuardado, nuevaContrasena);

                    if (MessageDigest.isEqual(
                            hashGuardado.getBytes(StandardCharsets.UTF_8),
                            inputHash.getBytes(StandardCharsets.UTF_8))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Cambia la contraseña de un empleado, validando contra su historial y
     * registrándola en él.
     */
    public void cambiarContrasena(int idEmpleado, char[] nuevaContrasena) throws SQLException {
        if (esContrasenaRepetida(idEmpleado, nuevaContrasena)) {
            throw new IllegalArgumentException("La nueva contraseña no puede ser igual a una de las últimas");
        }

        String sqlUpdate = "UPDATE empleado SET contrasena_hash = ?, salt = ? WHERE id_empleado = ?";
        String sqlHistorialInsert = "INSERT INTO historial_contrasena (id_empleado, contrasena_hash, salt) VALUES (?, ?, ?)";
        String sqlHistorialClean = "DELETE FROM historial_contrasena WHERE id_empleado = ? AND id_historial NOT IN " +
                "(SELECT id_historial FROM historial_contrasena WHERE id_empleado = ? ORDER BY fecha_cambio DESC LIMIT 10)";

        String nuevoSalt = generarSalt();
        String nuevoHash = hashSHA256(nuevoSalt, nuevaContrasena);

        try (Connection cn = ConexionDB.getConnection()) {
            cn.setAutoCommit(false);
            try {
                // Actualizar la contraseña en la tabla principal
                try (PreparedStatement psUpdate = cn.prepareStatement(sqlUpdate)) {
                    psUpdate.setString(1, nuevoHash);
                    psUpdate.setString(2, nuevoSalt);
                    psUpdate.setInt(3, idEmpleado);
                    psUpdate.executeUpdate();
                }

                // Guardar la nueva contraseña en el historial
                try (PreparedStatement psInsert = cn.prepareStatement(sqlHistorialInsert)) {
                    psInsert.setInt(1, idEmpleado);
                    psInsert.setString(2, nuevoHash);
                    psInsert.setString(3, nuevoSalt);
                    psInsert.executeUpdate();
                }

                // Limpiar el historial, dejando solo las últimas 10 contraseñas
                try (PreparedStatement psClean = cn.prepareStatement(sqlHistorialClean)) {
                    psClean.setInt(1, idEmpleado);
                    psClean.setInt(2, idEmpleado);
                    psClean.executeUpdate();
                }

                cn.commit();
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(true);
            }
        } finally {
            Arrays.fill(nuevaContrasena, '\0');
        }
    }
}
