package com.css.restaurante.dao;

import com.css.restaurante.modelo.CategoriaProducto;
import com.css.restaurante.modelo.Producto;
import com.css.restaurante.exception.StockInsuficienteException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD sobre la tabla catalogo_producto.
 */
public class ProductoDAO {

    // ===== CONSULTAS =====

    public List<Producto> listar() throws SQLException {
        String sql = "SELECT * FROM catalogo_producto ORDER BY nombre";
        return obtenerProductos(sql);
    }

    public List<Producto> listarDisponibles() throws SQLException {
        String sql = "SELECT * FROM catalogo_producto WHERE estado = 1 ORDER BY nombre";
        return obtenerProductos(sql);
    }

    public List<Producto> listarPorCategoria(CategoriaProducto categoria) throws SQLException {
        String sql = "SELECT * FROM catalogo_producto WHERE categoria = ? AND estado = 1 ORDER BY nombre";
        List<Producto> productos = new ArrayList<>();
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, categoria.getValor());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        }
        return productos;
    }

    public Producto buscarPorId(int idProducto) throws SQLException {
        String sql = "SELECT * FROM catalogo_producto WHERE id_producto = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        }
        return null;
    }

    public Producto buscarPorNombre(String nombre) throws SQLException {
        String sql = "SELECT * FROM catalogo_producto WHERE LOWER(nombre) = LOWER(?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
            }
        }
        return null;
    }

    public int obtenerStock(int idProducto) throws SQLException {
        String sql = "SELECT stock FROM catalogo_producto WHERE id_producto = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock");
                }
            }
        }
        return 0;
    }

    // ===== MODIFICACIONES =====

    public void crear(Producto p) throws SQLException {
        String sql = "INSERT INTO catalogo_producto (nombre, precio, categoria, stock, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setString(3, p.getCategoria().getValor());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getEstado());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    p.setId(keys.getInt(1));
                }
            }
        }
    }

    public void actualizar(int idProducto, double nuevoPrecio, int estado) throws SQLException {
        String sql = "UPDATE catalogo_producto SET precio = ?, estado = ? WHERE id_producto = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setDouble(1, nuevoPrecio);
            ps.setInt(2, estado);
            ps.setInt(3, idProducto);
            ps.executeUpdate();
        }
    }

    public void actualizarStock(int idProducto, int nuevoStock) throws SQLException {
        if (nuevoStock < 0) {
            throw new StockInsuficienteException();
        }
        String sql = "UPDATE catalogo_producto SET stock = ? WHERE id_producto = ?";
        try (Connection cn = ConexionDB.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }

    // ===== MAPEO =====

    private List<Producto> obtenerProductos(String sql) throws SQLException {
        List<Producto> productos = new ArrayList<>();
        try (Connection cn = ConexionDB.getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        }
        return productos;
    }

    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre"),
                rs.getDouble("precio"),
                CategoriaProducto.fromString(rs.getString("categoria")),
                rs.getInt("stock"),
                rs.getInt("estado")
        );
    }
}
