package DAO;

import Clases.abstractas.Producto;
import Clases.concret.Comida;
import Clases.concret.Bebida;
import Clases.concret.Postre;
import Exepciones.StockInsuficienteException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO
{

    // Consultas SQL base
    private static final String SQL_SELECT_ALL =
            "SELECT * FROM CatalogoProducto";

    private static final String SQL_SELECT_DISPONIBLES =
            "SELECT * FROM CatalogoProducto WHERE estado = 1";

    private static final String SQL_UPDATE_PRODUCTO =
            "UPDATE CatalogoProducto SET precio = ?, estado = ? WHERE IdCatalogoProducto = ?";

    // Devuelve todos los productos del catálogo
    public static List<Producto> listar() throws SQLException
    {
        return obtenerProductos(SQL_SELECT_ALL);
    }

    // Devuelve solo los productos activos (estado = 1)
    public static List<Producto> listarDisponibles() throws SQLException
    {
        return obtenerProductos(SQL_SELECT_DISPONIBLES);
    }

    // Actualiza el precio y estado de un producto existente
    public static void actualizarProducto(int idProducto, double nuevoPrecio, int estado) throws SQLException
    {
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(SQL_UPDATE_PRODUCTO))
        {
            ps.setDouble(1, nuevoPrecio);
            ps.setInt(2, estado);
            ps.setInt(3, idProducto);
            ps.executeUpdate();
        }
    }

    // Inserta un nuevo producto en el catálogo
    // Por defecto, se marca como disponible (estado = 1)
    public static void crearProducto(Producto producto) throws SQLException
    {
        String sql = "INSERT INTO CatalogoProducto (nombre, precio, categoria, estado) VALUES (?, ?, ?, ?)";

        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql))
        {
            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());

            // Determina la categoría según la clase concreta (Comida, Bebida o Postre)
            String categoria = producto.getClass().getSimpleName().toLowerCase();
            ps.setString(3, categoria);

            ps.setInt(4, 1); // Estado disponible por defecto

            ps.executeUpdate();
        }
    }

    // Obtiene una lista de productos desde una consulta SQL
    // Crea instancias de Comida, Bebida o Postre según la categoría
    private static List<Producto> obtenerProductos(String sql) throws SQLException
    {
        List<Producto> productos = new ArrayList<>();

        try (Connection cn = ConexionDB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql))
        {
            while (rs.next())
            {
                int id = rs.getInt("IdCatalogoProducto");
                String nombre = rs.getString("nombre");
                double precio = rs.getDouble("precio");
                String categoria = rs.getString("categoria");
                int estado = rs.getInt("estado");
                int stock = rs.getInt("stock");

                Producto p = null;

                // Instancia según la categoría
                if ("comida".equalsIgnoreCase(categoria))
                {
                    p = new Comida(id, nombre, precio, estado);
                }
                else if ("bebida".equalsIgnoreCase(categoria))
                {
                    p = new Bebida(id, nombre, precio, estado);
                }
                else if ("postre".equalsIgnoreCase(categoria))
                {
                    p = new Postre(id, nombre, precio, estado);
                }

                if (p != null)
                {
                    p.setStock(stock);
                    productos.add(p);
                }
            }
        }

        return productos;
    }

    // Actualiza el stock de un producto
    // Lanza una excepción si el nuevo stock es negativo
    public static void actualizarStock(int idProducto, int nuevoStock) throws SQLException
    {
        if (nuevoStock < 0)
        {
            throw new StockInsuficienteException();
        }

        String sql = "UPDATE CatalogoProducto SET stock=? WHERE IdCatalogoProducto=?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }

    // Devuelve el stock actual de un producto por su ID
    public static int obtenerStock(int idProducto) throws SQLException
    {
        String sql = "SELECT stock FROM CatalogoProducto WHERE IdCatalogoProducto=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql))
        {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    return rs.getInt("stock");
                }
            }
        }
        return 0;
    }
}
