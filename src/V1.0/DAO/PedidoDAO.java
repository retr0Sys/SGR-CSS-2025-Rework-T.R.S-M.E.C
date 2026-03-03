package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Clases.concret.Pedido;
import Exepciones.StockInsuficienteException;

public class PedidoDAO
{
    // =====================================
    // Agrega un nuevo pedido y descuenta stock
    // =====================================
    public static void agregarPedido(int idCuenta, int idProducto, int cantidad) throws SQLException
    {
        String sqlInsert = "INSERT INTO pedido (idCuenta, idProducto, cantidad, estado) VALUES (?, ?, ?, ?)";
        String sqlUpdateStock = "UPDATE CatalogoProducto SET stock = stock - ? WHERE IdCatalogoProducto = ? AND stock >= ?";

        try (Connection con = ConexionDB.getConnection())
        {
            try
            {
                con.setAutoCommit(false);

                // Inserta el pedido como "Pendiente"
                try (PreparedStatement ps = con.prepareStatement(sqlInsert))
                {
                    ps.setInt(1, idCuenta);
                    ps.setInt(2, idProducto);
                    ps.setInt(3, cantidad);
                    ps.setString(4, "Pendiente");
                    ps.executeUpdate();
                }

                // Actualiza el stock del producto
                try (PreparedStatement ps2 = con.prepareStatement(sqlUpdateStock))
                {
                    ps2.setInt(1, cantidad);
                    ps2.setInt(2, idProducto);
                    ps2.setInt(3, cantidad);
                    int filas = ps2.executeUpdate();
                    if (filas == 0)
                    {
                        con.rollback();
                        throw new StockInsuficienteException();
                    }
                }

                con.commit();
            }
            catch (SQLException e)
            {
                con.rollback();
                throw e;
            }
            finally
            {
                con.setAutoCommit(true);
            }
        }
    }

    // =====================================
    // Lista los pedidos de una cuenta específica
    // =====================================
    public static List<Pedido> listarPorCuenta(int idCuenta) throws SQLException
    {
        List<Pedido> lista = new ArrayList<>();
        String sql = """
            SELECT p.idPedido, p.idCuenta, p.idProducto, p.cantidad, p.fechaHora,
                   c.nombre AS nombreProducto, c.precio AS precioProducto, p.estado
            FROM pedido p
            JOIN CatalogoProducto c ON p.idProducto = c.IdCatalogoProducto
            WHERE p.idCuenta = ?
            ORDER BY p.fechaHora DESC
        """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, idCuenta);
            try (ResultSet rs = stmt.executeQuery())
            {
                while (rs.next())
                {
                    lista.add(new Pedido(
                            rs.getInt("idPedido"),
                            rs.getInt("idCuenta"),
                            rs.getInt("idProducto"),
                            rs.getInt("cantidad"),
                            rs.getTimestamp("fechaHora"),
                            rs.getString("nombreProducto"),
                            rs.getDouble("precioProducto")
                    ));
                }
            }
        }
        return lista;
    }

    // =====================================
    // Lista todos los pedidos del sistema
    // =====================================
    public static List<Pedido> listar() throws SQLException
    {
        List<Pedido> lista = new ArrayList<>();
        String sql = """
            SELECT p.idPedido, p.idCuenta, p.idProducto, p.cantidad, p.fechaHora,
                   c.nombre AS nombreProducto, c.precio AS precioProducto, p.estado
            FROM pedido p
            JOIN CatalogoProducto c ON p.idProducto = c.IdCatalogoProducto
            ORDER BY p.fechaHora DESC
        """;

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                lista.add(new Pedido(
                        rs.getInt("idPedido"),
                        rs.getInt("idCuenta"),
                        rs.getInt("idProducto"),
                        rs.getInt("cantidad"),
                        rs.getTimestamp("fechaHora"),
                        rs.getString("nombreProducto"),
                        rs.getDouble("precioProducto")
                ));
            }
        }
        return lista;
    }

    // =====================================
    // Calcula el total de una cuenta (precio × cantidad)
    // =====================================
    public static double calcularTotalCuenta(int idCuenta) throws SQLException
    {
        String sql = """
            SELECT SUM(c.precio * p.cantidad) AS total
            FROM pedido p
            JOIN CatalogoProducto c ON p.idProducto = c.IdCatalogoProducto
            WHERE p.idCuenta = ?
        """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, idCuenta);
            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }

    // =====================================
    // Lista pedidos pendientes (por fecha)
    // =====================================
    public static List<Pedido> listarPendientes() throws SQLException
    {
        List<Pedido> lista = new ArrayList<>();
        String sql = """
            SELECT p.idPedido, p.idCuenta, p.idProducto, p.cantidad, p.fechaHora,
                   c.nombre AS nombreProducto, c.precio AS precioProducto, p.estado
            FROM pedido p
            JOIN CatalogoProducto c ON p.idProducto = c.IdCatalogoProducto
            WHERE p.estado = 'Pendiente'
            ORDER BY p.fechaHora ASC
        """;

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql))
        {
            while (rs.next())
            {
                lista.add(new Pedido(
                        rs.getInt("idPedido"),
                        rs.getInt("idCuenta"),
                        rs.getInt("idProducto"),
                        rs.getInt("cantidad"),
                        rs.getTimestamp("fechaHora"),
                        rs.getString("nombreProducto"),
                        rs.getDouble("precioProducto")
                ));
            }
        }
        return lista;
    }

    // =====================================
    // Actualiza el estado del pedido y ajusta stock
    // =====================================
    public static void actualizarEstado(int idPedido, String nuevoEstado) throws SQLException
    {
        String sqlGetPedido = "SELECT idProducto, cantidad, estado FROM pedido WHERE idPedido = ?";
        String sqlUpdateEstado = "UPDATE pedido SET estado = ? WHERE idPedido = ?";
        String sqlUpdateStock = "UPDATE CatalogoProducto SET stock = stock + ? WHERE IdCatalogoProducto = ?";
        String sqlUpdateStockRestar = "UPDATE CatalogoProducto SET stock = stock - ? WHERE IdCatalogoProducto = ? AND stock >= ?";

        try (Connection con = ConexionDB.getConnection())
        {
            try
            {
                con.setAutoCommit(false);

                int idProducto = 0;
                int cantidad = 0;
                String estadoActual = "";

                // Obtiene los datos actuales del pedido
                try (PreparedStatement ps = con.prepareStatement(sqlGetPedido))
                {
                    ps.setInt(1, idPedido);
                    try (ResultSet rs = ps.executeQuery())
                    {
                        if (rs.next())
                        {
                            idProducto = rs.getInt("idProducto");
                            cantidad = rs.getInt("cantidad");
                            estadoActual = rs.getString("estado");
                        }
                        else
                        {
                            throw new SQLException("Pedido no encontrado");
                        }
                    }
                }

                // Si se cancela, devuelve el stock
                if ("Cancelado".equalsIgnoreCase(nuevoEstado) && "Pendiente".equalsIgnoreCase(estadoActual))
                {
                    try (PreparedStatement ps = con.prepareStatement(sqlUpdateStock))
                    {
                        ps.setInt(1, cantidad);
                        ps.setInt(2, idProducto);
                        ps.executeUpdate();
                    }
                }

                // Si se reactiva, descuenta stock
                if ("Pendiente".equalsIgnoreCase(nuevoEstado) && "Cancelado".equalsIgnoreCase(estadoActual))
                {
                    try (PreparedStatement ps = con.prepareStatement(sqlUpdateStockRestar))
                    {
                        ps.setInt(1, cantidad);
                        ps.setInt(2, idProducto);
                        ps.setInt(3, cantidad);
                        int filas = ps.executeUpdate();
                        if (filas == 0)
                        {
                            con.rollback();
                            throw new StockInsuficienteException();
                        }
                    }
                }

                // Actualiza el estado del pedido
                try (PreparedStatement ps = con.prepareStatement(sqlUpdateEstado))
                {
                    ps.setString(1, nuevoEstado);
                    ps.setInt(2, idPedido);
                    ps.executeUpdate();
                }

                con.commit();
            }
            catch (SQLException e)
            {
                con.rollback();
                throw e;
            }
            finally
            {
                con.setAutoCommit(true);
            }
        }
    }
}
