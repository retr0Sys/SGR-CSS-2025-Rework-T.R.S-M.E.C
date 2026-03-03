package DAO;

import Clases.concret.Cuenta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CuentaDAO
{
    // Sentencias SQL utilizadas en los distintos métodos
    private static final String SQL_INSERT =
            "INSERT INTO cuenta (idMesa, estado) VALUES (?, ?)";

    private static final String SQL_SELECT_ABIERTA_COUNT =
            "SELECT COUNT(*) FROM cuenta WHERE idMesa = ? AND estado = 1";

    private static final String SQL_SELECT_ID_ABIERTA =
            "SELECT idCuenta FROM cuenta WHERE idMesa = ? AND estado = 1 LIMIT 1";

    private static final String SQL_CERRAR_CUENTA =
            "UPDATE cuenta SET estado = 0, fechaCierre = NOW() WHERE idMesa = ? AND estado = 1";

    private static final String SQL_SELECT_ABIERTA =
            "SELECT * FROM cuenta WHERE idMesa = ? AND estado = 1 LIMIT 1";

    private static final String SQL_SELECT_ALL =
            "SELECT * FROM cuenta ORDER BY fechaApertura DESC";

    // Inserta una nueva cuenta asociada a una mesa (por lo general con estado = 1 para abierta)
    // y asigna el ID generado al objeto Cuenta
    public void insertar(Cuenta c) throws SQLException
    {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS))
        {
            ps.setInt(1, c.getIdMesa());
            ps.setInt(2, c.getEstado());
            ps.executeUpdate();

            // Guarda el ID generado automáticamente
            try (ResultSet keys = ps.getGeneratedKeys())
            {
                if (keys.next())
                {
                    c.setIdCuenta(keys.getInt(1));
                }
            }
        }
    }

    // Verifica si una mesa tiene una cuenta abierta (estado = 1)
    public boolean tieneCuentaAbierta(int idMesa) throws SQLException
    {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_SELECT_ABIERTA_COUNT))
        {
            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // Devuelve el ID de la cuenta abierta para una mesa o -1 si no existe
    public int obtenerIdCuentaAbierta(int idMesa) throws SQLException
    {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_SELECT_ID_ABIERTA))
        {
            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    return rs.getInt("idCuenta");
                }
            }
        }
        return -1;
    }

    // Cierra la cuenta abierta de una mesa (cambia el estado y registra la fecha de cierre)
    public void cerrarCuenta(int idMesa) throws SQLException
    {
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_CERRAR_CUENTA))
        {
            ps.setInt(1, idMesa);
            ps.executeUpdate();
        }
    }

    // Devuelve una lista con todas las cuentas registradas (abiertas y cerradas)
    public List<Cuenta> listar() throws SQLException
    {
        List<Cuenta> lista = new ArrayList<>();
        try (Connection cn = ConexionDB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(SQL_SELECT_ALL))
        {
            while (rs.next())
            {
                Cuenta c = new Cuenta(
                        rs.getInt("idCuenta"),
                        rs.getInt("idMesa"),
                        rs.getTimestamp("fechaApertura"),
                        rs.getTimestamp("fechaCierre"),
                        rs.getInt("estado")
                );
                lista.add(c);
            }
        }
        return lista;
    }
}
