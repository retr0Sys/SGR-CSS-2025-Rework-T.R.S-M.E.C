package com.css.restaurante.dao;

import com.css.restaurante.modelo.Mesa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD sobre la tabla mesa.
 */
public class MesaDAO {

    public List<Mesa> listar() throws SQLException {
        String sql = "SELECT * FROM mesa ORDER BY id_mesa";
        List<Mesa> mesas = new ArrayList<>();
        try (Connection cn = ConexionDB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                mesas.add(mapearMesa(rs));
            }
        }
        return mesas;
    }

    public Mesa buscarPorId(int idMesa) throws SQLException {
        String sql = "SELECT * FROM mesa WHERE id_mesa = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearMesa(rs);
                }
            }
        }
        return null;
    }

    public void actualizarEstado(int idMesa, String nuevoEstado) throws SQLException {
        String sql = "UPDATE mesa SET estado = ? WHERE id_mesa = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idMesa);
            ps.executeUpdate();
        }
    }

    public void asignarMesero(int idMesa, int idMesero) throws SQLException {
        String sql = "UPDATE mesa SET id_mesero = ? WHERE id_mesa = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMesero);
            ps.setInt(2, idMesa);
            ps.executeUpdate();
        }
    }

    public void desasignarMesero(int idMesa) throws SQLException {
        String sql = "UPDATE mesa SET id_mesero = NULL WHERE id_mesa = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            ps.executeUpdate();
        }
    }

    public String obtenerNombreMesero(int idMesa) throws SQLException {
        String sql = "SELECT CONCAT(m.nombre, ' ', m.apellido) AS mesero " +
                "FROM mesa me LEFT JOIN mesero m ON me.id_mesero = m.id_mesero " +
                "WHERE me.id_mesa = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("mesero");
                }
            }
        }
        return null;
    }

    private Mesa mapearMesa(ResultSet rs) throws SQLException {
        Mesa m = new Mesa(
                rs.getInt("id_mesa"),
                rs.getInt("capacidad"),
                rs.getString("estado")
        );
        int idMesero = rs.getInt("id_mesero");
        if (!rs.wasNull()) {
            m.setIdMesero(idMesero);
        }
        return m;
    }
}
