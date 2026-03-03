package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Clases.concret.Mesa;

public class MesaDAO
{
    // Sentencias SQL utilizadas para las operaciones CRUD
    private static final String SQL_INSERT =
            "INSERT INTO mesa (idMesa, capacidad, estado) VALUES (?,?,?)";

    private static final String SQL_SELECT_ALL =
            "SELECT * FROM mesa";

    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM mesa WHERE idMesa=?";

    private static final String SQL_UPDATE =
            "UPDATE mesa SET capacidad=?, estado=? WHERE idMesa=?";

    private static final String SQL_DELETE =
            "DELETE FROM mesa WHERE idMesa=?";

    // Inserta una nueva mesa en la base de datos
    public void insertar(Mesa m) throws SQLException
    {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERT))
        {
            ps.setInt(1, m.getIdMesa());
            ps.setInt(2, m.getCapacidad());
            ps.setString(3, m.getEstado());
            ps.executeUpdate();
        }
    }

    // Retorna una lista con todas las mesas registradas en la base de datos
    public List<Mesa> listar() throws SQLException
    {
        List<Mesa> mesas = new ArrayList<>();
        try (Connection cn = ConexionDB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(SQL_SELECT_ALL))
        {
            while (rs.next())
            {
                // Crea un objeto Mesa con los datos obtenidos del registro actual
                Mesa m = new Mesa(
                        rs.getInt("idMesa"),
                        rs.getInt("capacidad"),
                        rs.getString("estado")
                );
                mesas.add(m);
            }
        }
        return mesas;
    }

    // Busca una mesa por su ID y devuelve un objeto Mesa, o null si no se encuentra
    public Mesa buscarPorId(int idMesa) throws SQLException
    {
        Mesa mesa = null;
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_SELECT_BY_ID))
        {
            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    mesa = new Mesa(
                            rs.getInt("idMesa"),
                            rs.getInt("capacidad"),
                            rs.getString("estado")
                    );
                }
            }
        }
        return mesa;
    }

    // Actualiza la capacidad y el estado de una mesa existente
    public void actualizar(Mesa m) throws SQLException
    {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_UPDATE))
        {
            ps.setInt(1, m.getCapacidad());
            ps.setString(2, m.getEstado());
            ps.setInt(3, m.getIdMesa());
            ps.executeUpdate();
        }
    }

    // Elimina una mesa de la base de datos según su ID
    public void eliminar(int idMesa) throws SQLException
    {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_DELETE))
        {
            ps.setInt(1, idMesa);
            ps.executeUpdate();
        }
    }

    // Asigna un mesero a una mesa específica
    public void asignarMesero(int idMesa, int idMesero) throws SQLException
    {
        String sql = "UPDATE mesa SET idMesero = ? WHERE idMesa = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql))
        {
            ps.setInt(1, idMesero);
            ps.setInt(2, idMesa);
            ps.executeUpdate();
        }
    }

    // Quita la asignación del mesero de una mesa
    public void desasignarMesero(int idMesa) throws SQLException
    {
        String sql = "UPDATE mesa SET idMesero = NULL WHERE idMesa = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql))
        {
            ps.setInt(1, idMesa);
            ps.executeUpdate();
        }
    }

    // Obtiene el nombre completo del mesero asignado a una mesa
    // Si no hay mesero asignado, devuelve null
    public String obtenerNombreMesero(int idMesa) throws SQLException
    {
        String sql = "SELECT CONCAT(m.nombre, ' ', m.apellido) AS mesero " +
                "FROM mesa me " +
                "LEFT JOIN mesero m ON me.idMesero = m.idMesero " +
                "WHERE me.idMesa = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql))
        {
            ps.setInt(1, idMesa);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
            {
                return rs.getString("mesero");
            }
        }
        return null;
    }

    // Actualiza únicamente el estado de una mesa (por ejemplo: "Libre", "Ocupada", "Reservada")
    public void actualizarEstado(int idMesa, String nuevoEstado) throws SQLException
    {
        String sql = "UPDATE mesa SET estado = ? WHERE idMesa = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql))
        {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idMesa);
            ps.executeUpdate();
        }
    }
}
