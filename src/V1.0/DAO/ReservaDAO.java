package DAO;

import Clases.concret.Reserva;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO
{

    // --- Sentencias SQL ---
    private static final String SQL_INSERT =
            "INSERT INTO reserva (idMesa, nombre, apellido, fecha, hora) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_ALL =
            "SELECT * FROM reserva";

    private static final String SQL_SELECT_BY_ID =
            "SELECT * FROM reserva WHERE idReserva=?";

    private static final String SQL_DELETE =
            "DELETE FROM reserva WHERE idReserva=?";


    // --- Inserta una nueva reserva en la base de datos ---
    public void insertar(Reserva r) throws SQLException
    {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERT))
        {
            ps.setInt(1, r.getIdMesa());
            ps.setString(2, r.getNombre());
            ps.setString(3, r.getApellido());
            ps.setDate(4, r.getFecha());
            ps.setTime(5, r.getHora());
            ps.executeUpdate(); // Ejecuta la inserción
        }
    }


    // --- Devuelve una lista con todas las reservas registradas ---
    public List<Reserva> listar() throws SQLException
    {
        List<Reserva> reservas = new ArrayList<>();

        try (Connection cn = ConexionDB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next())
            {
                Reserva r = new Reserva(
                        rs.getInt("idReserva"),
                        rs.getInt("idMesa"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getDate("fecha"),
                        rs.getTime("hora")
                );
                reservas.add(r);
            }
        }
        return reservas;
    }


    // --- Busca y devuelve una reserva específica según su ID ---
    public Reserva buscarPorId(int idReserva) throws SQLException
    {
        Reserva reserva = null;

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_SELECT_BY_ID))
        {

            ps.setInt(1, idReserva);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    reserva = new Reserva(
                            rs.getInt("idReserva"),
                            rs.getInt("idMesa"),
                            rs.getString("nombre"),
                            rs.getString("apellido"),
                            rs.getDate("fecha"),
                            rs.getTime("hora")
                    );
                }
            }
        }
        return reserva;
    }


    // --- Obtiene las reservas asociadas a una mesa en una fecha específica ---
    public List<Reserva> obtenerPorMesaYFecha(int idMesa, Date fecha) throws SQLException
    {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reserva WHERE idMesa = ? AND fecha = ?";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement stmt = cn.prepareStatement(sql))
        {
            stmt.setInt(1, idMesa);
            stmt.setDate(2, fecha);

            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    Reserva r = new Reserva();
                    r.setIdReserva(rs.getInt("idReserva"));
                    r.setIdMesa(rs.getInt("idMesa"));
                    r.setNombre(rs.getString("nombre"));
                    r.setApellido(rs.getString("apellido"));
                    r.setFecha(rs.getDate("fecha"));
                    r.setHora(rs.getTime("hora"));
                    reservas.add(r);
                }
            }
        }

        return reservas;
    }


    // --- Elimina una reserva de la base de datos mediante su ID ---
    public void eliminar(int idReserva) throws SQLException
    {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_DELETE))
        {
            ps.setInt(1, idReserva);
            ps.executeUpdate(); // Ejecuta la eliminación
        }
    }
}
