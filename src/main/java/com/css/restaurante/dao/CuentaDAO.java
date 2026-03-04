package com.css.restaurante.dao;

import com.css.restaurante.modelo.Cuenta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones sobre la tabla cuenta.
 */
public class CuentaDAO {

    public void insertar(Cuenta c) throws SQLException {
        String sql = "INSERT INTO cuenta (id_mesa, id_mesero, estado) VALUES (?, ?, ?)";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, c.getIdMesa());
            if (c.getIdMesero() != null) {
                ps.setInt(2, c.getIdMesero());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setInt(3, c.getEstado());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    c.setIdCuenta(keys.getInt(1));
                }
            }
        }
    }

    public boolean tieneCuentaAbierta(int idMesa) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cuenta WHERE id_mesa = ? AND estado = 1";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public int obtenerIdCuentaAbierta(int idMesa) throws SQLException {
        String sql = "SELECT id_cuenta FROM cuenta WHERE id_mesa = ? AND estado = 1 LIMIT 1";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_cuenta");
                }
            }
        }
        return -1;
    }

    public void cerrarCuenta(int idMesa) throws SQLException {
        String sql = "UPDATE cuenta SET estado = 0, fecha_cierre = NOW() WHERE id_mesa = ? AND estado = 1";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            ps.executeUpdate();
        }
    }

    public void actualizarMesero(int idMesa, int idMesero) throws SQLException {
        String sql = "UPDATE cuenta SET id_mesero = ? WHERE id_mesa = ? AND estado = 1";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMesero);
            ps.setInt(2, idMesa);
            ps.executeUpdate();
        }
    }

    public void desasignarMesero(int idMesa) throws SQLException {
        String sql = "UPDATE cuenta SET id_mesero = NULL WHERE id_mesa = ? AND estado = 1";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            ps.executeUpdate();
        }
    }

    public List<Cuenta> listar() throws SQLException {
        String sql = "SELECT * FROM cuenta ORDER BY fecha_apertura DESC";
        List<Cuenta> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.getConnection();
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearCuenta(rs));
            }
        }
        return lista;
    }

    private Cuenta mapearCuenta(ResultSet rs) throws SQLException {
        int idMesero = rs.getInt("id_mesero");
        Integer mesero = rs.wasNull() ? null : idMesero;
        return new Cuenta(
                rs.getInt("id_cuenta"),
                rs.getInt("id_mesa"),
                mesero,
                rs.getTimestamp("fecha_apertura"),
                rs.getTimestamp("fecha_cierre"),
                rs.getInt("estado"));
    }
}
