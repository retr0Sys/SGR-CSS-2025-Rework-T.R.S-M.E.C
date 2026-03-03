package DAO;

import Clases.concret.Mesero;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MeseroDAO
{
    // Agrega un nuevo mesero a la base de datos
    public void insertar(Mesero mesero) throws SQLException
    {
        String sql = "INSERT INTO mesero (nombre, apellido, telefono, activo) VALUES (?, ?, ?, ?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql))
        {
            ps.setString(1, mesero.getNombre());
            ps.setString(2, mesero.getApellido());
            ps.setString(3, mesero.getTelefono());
            ps.setBoolean(4, mesero.isActivo());
            ps.executeUpdate();
        }
    }

    // Modifica los datos de un mesero existente
    public void actualizar(Mesero mesero) throws SQLException
    {
        String sql = "UPDATE mesero SET nombre = ?, apellido = ?, telefono = ?, activo = ? WHERE idMesero = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql))
        {
            ps.setString(1, mesero.getNombre());
            ps.setString(2, mesero.getApellido());
            ps.setString(3, mesero.getTelefono());
            ps.setBoolean(4, mesero.isActivo());
            ps.setInt(5, mesero.getIdMesero());
            ps.executeUpdate();
        }
    }

    // Elimina un mesero por su ID
    public void eliminar(int idMesero) throws SQLException
    {
        String sql = "DELETE FROM mesero WHERE idMesero = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql))
        {
            ps.setInt(1, idMesero);
            ps.executeUpdate();
        }
    }

    // Busca un mesero por su ID
    public Mesero buscarPorId(int idMesero) throws SQLException
    {
        Mesero mesero = null;
        String sql = "SELECT * FROM mesero WHERE idMesero = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql))
        {
            ps.setInt(1, idMesero);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                mesero = new Mesero(
                        rs.getInt("idMesero"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getBoolean("activo")
                );
            }
        }
        return mesero;
    }

    // Obtiene una lista con todos los meseros registrados
    public List<Mesero> listar() throws SQLException
    {
        List<Mesero> lista = new ArrayList<>();
        String sql = "SELECT * FROM mesero";

        try (Connection cn = ConexionDB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql))
        {
            while (rs.next())
            {
                Mesero mesero = new Mesero(
                        rs.getInt("idMesero"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getBoolean("activo")
                );
                lista.add(mesero);
            }
        }
        return lista;
    }
}
