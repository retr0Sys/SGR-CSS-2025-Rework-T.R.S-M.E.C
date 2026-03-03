package com.css.restaurante.dao;

import com.css.restaurante.modelo.Mesero;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones sobre la tabla mesero.
 */
public class MeseroDAO {

    public List<Mesero> listarActivos() throws SQLException {
        String sql = "SELECT * FROM mesero WHERE activo = TRUE ORDER BY nombre";
        List<Mesero> meseros = new ArrayList<>();
        try (Connection cn = ConexionDB.getConnection();
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                meseros.add(mapearMesero(rs));
            }
        }
        return meseros;
    }

    public List<Mesero> listar() throws SQLException {
        String sql = "SELECT * FROM mesero ORDER BY nombre";
        List<Mesero> meseros = new ArrayList<>();
        try (Connection cn = ConexionDB.getConnection();
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                meseros.add(mapearMesero(rs));
            }
        }
        return meseros;
    }

    public Mesero buscarPorId(int idMesero) throws SQLException {
        String sql = "SELECT * FROM mesero WHERE id_mesero = ?";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idMesero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearMesero(rs);
                }
            }
        }
        return null;
    }

    private Mesero mapearMesero(ResultSet rs) throws SQLException {
        return new Mesero(
                rs.getInt("id_mesero"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getBoolean("activo"));
    }
}
