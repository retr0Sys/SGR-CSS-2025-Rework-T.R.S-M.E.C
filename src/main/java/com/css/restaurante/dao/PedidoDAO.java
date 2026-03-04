package com.css.restaurante.dao;

import com.css.restaurante.modelo.EstadoPedido;
import com.css.restaurante.modelo.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones sobre la tabla pedido.
 * Gestiona la creación, listado y actualización de estados de pedidos.
 */
public class PedidoDAO {

    /**
     * Agrega un nuevo pedido y descuenta el stock del producto.
     */
    public void agregar(int idCuenta, int idProducto, int cantidad) throws SQLException {
        Connection cn = null;
        try {
            cn = ConexionDB.getConnection();
            cn.setAutoCommit(false);

            // 1. Verificar stock suficiente
            int stockActual;
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT stock FROM catalogo_producto WHERE id_producto = ?")) {
                ps.setInt(1, idProducto);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || (stockActual = rs.getInt("stock")) < cantidad) {
                        throw new SQLException("Stock insuficiente para el producto ID: " + idProducto);
                    }
                }
            }

            // 2. Insertar pedido
            try (PreparedStatement ps = cn.prepareStatement(
                    "INSERT INTO pedido (id_cuenta, id_producto, cantidad, estado) VALUES (?, ?, ?, ?)")) {
                ps.setInt(1, idCuenta);
                ps.setInt(2, idProducto);
                ps.setInt(3, cantidad);
                ps.setString(4, EstadoPedido.PENDIENTE.getValor());
                ps.executeUpdate();
            }

            // 3. Descontar stock
            try (PreparedStatement ps = cn.prepareStatement(
                    "UPDATE catalogo_producto SET stock = stock - ? WHERE id_producto = ?")) {
                ps.setInt(1, cantidad);
                ps.setInt(2, idProducto);
                ps.executeUpdate();
            }

            // 4. Actualizar estado si el stock se agota (<= 0)
            try (PreparedStatement ps = cn.prepareStatement(
                    "UPDATE catalogo_producto SET estado = 0 WHERE id_producto = ? AND stock <= 0")) {
                ps.setInt(1, idProducto);
                ps.executeUpdate();
            }

            cn.commit();
        } catch (SQLException e) {
            if (cn != null)
                cn.rollback();
            throw e;
        } finally {
            if (cn != null) {
                cn.setAutoCommit(true);
                cn.close();
            }
        }
    }

    /**
     * Lista pedidos de una cuenta específica (con datos del producto por JOIN).
     */
    public List<Pedido> listarPorCuenta(int idCuenta) throws SQLException {
        String sql = "SELECT p.*, cp.nombre AS nombre_producto, cp.precio AS precio_producto " +
                "FROM pedido p " +
                "JOIN catalogo_producto cp ON p.id_producto = cp.id_producto " +
                "WHERE p.id_cuenta = ? " +
                "ORDER BY p.fecha_hora";
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedidoConProducto(rs));
                }
            }
        }
        return pedidos;
    }

    /**
     * Lista todos los pedidos (con datos del producto por JOIN).
     */
    public List<Pedido> listar() throws SQLException {
        String sql = "SELECT p.*, cp.nombre AS nombre_producto, cp.precio AS precio_producto " +
                "FROM pedido p " +
                "JOIN catalogo_producto cp ON p.id_producto = cp.id_producto " +
                "ORDER BY p.fecha_hora DESC";
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection cn = ConexionDB.getConnection();
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                pedidos.add(mapearPedidoConProducto(rs));
            }
        }
        return pedidos;
    }

    /**
     * Lista pedidos pendientes o en preparación (para cocina).
     */
    public List<Pedido> listarPendientes() throws SQLException {
        String sql = "SELECT p.*, cp.nombre AS nombre_producto, cp.precio AS precio_producto, " +
                "c.id_mesa " +
                "FROM pedido p " +
                "JOIN catalogo_producto cp ON p.id_producto = cp.id_producto " +
                "JOIN cuenta c ON p.id_cuenta = c.id_cuenta " +
                "WHERE p.estado IN ('Pendiente', 'En preparación') " +
                "ORDER BY p.fecha_hora";
        List<Pedido> pedidos = new ArrayList<>();
        try (Connection cn = ConexionDB.getConnection();
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Pedido p = mapearPedidoConProducto(rs);
                pedidos.add(p);
            }
        }
        return pedidos;
    }

    /**
     * Calcula el total de una cuenta sumando (precio × cantidad) de todos sus
     * pedidos no cancelados.
     */
    public double calcularTotalCuenta(int idCuenta) throws SQLException {
        String sql = "SELECT COALESCE(SUM(cp.precio * p.cantidad), 0) AS total " +
                "FROM pedido p " +
                "JOIN catalogo_producto cp ON p.id_producto = cp.id_producto " +
                "WHERE p.id_cuenta = ? AND p.estado != 'Cancelado'";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0;
    }

    /**
     * Actualiza el estado de un pedido. Si se cancela, devuelve el stock.
     */
    public void actualizarEstado(int idPedido, EstadoPedido nuevoEstado) throws SQLException {
        Connection cn = null;
        try {
            cn = ConexionDB.getConnection();
            cn.setAutoCommit(false);

            // Obtener datos actuales del pedido
            String estadoActual;
            int idProducto, cantidad;
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT estado, id_producto, cantidad FROM pedido WHERE id_pedido = ?")) {
                ps.setInt(1, idPedido);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next())
                        throw new SQLException("Pedido no encontrado: " + idPedido);
                    estadoActual = rs.getString("estado");
                    idProducto = rs.getInt("id_producto");
                    cantidad = rs.getInt("cantidad");
                }
            }

            // Actualizar estado
            try (PreparedStatement ps = cn.prepareStatement(
                    "UPDATE pedido SET estado = ? WHERE id_pedido = ?")) {
                ps.setString(1, nuevoEstado.getValor());
                ps.setInt(2, idPedido);
                ps.executeUpdate();
            }

            // Si se cancela, devolver stock
            if (nuevoEstado == EstadoPedido.CANCELADO && !"Cancelado".equals(estadoActual)) {
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE catalogo_producto SET stock = stock + ? WHERE id_producto = ?")) {
                    ps.setInt(1, cantidad);
                    ps.setInt(2, idProducto);
                    ps.executeUpdate();
                }
            }

            cn.commit();
        } catch (SQLException e) {
            if (cn != null)
                cn.rollback();
            throw e;
        } finally {
            if (cn != null) {
                cn.setAutoCommit(true);
                cn.close();
            }
        }
    }

    /**
     * Obtiene el id_mesa asociado a un pedido (a través de la cuenta).
     */
    public int obtenerMesaDePedido(int idPedido) throws SQLException {
        String sql = "SELECT c.id_mesa FROM pedido p " +
                "JOIN cuenta c ON p.id_cuenta = c.id_cuenta " +
                "WHERE p.id_pedido = ?";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_mesa");
                }
            }
        }
        return -1;
    }

    // ===== ELIMINAR PEDIDO PENDIENTE =====

    /**
     * Elimina un pedido que aún está en estado Pendiente y devuelve el stock.
     * Solo se pueden eliminar pedidos pendientes; los demás estados requieren
     * el flujo de cancelación de cocina.
     */
    public void eliminar(int idPedido) throws SQLException {
        Connection cn = null;
        try {
            cn = ConexionDB.getConnection();
            cn.setAutoCommit(false);

            // Obtener datos del pedido
            String estadoActual;
            int idProducto, cantidad;
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT estado, id_producto, cantidad FROM pedido WHERE id_pedido = ?")) {
                ps.setInt(1, idPedido);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next())
                        throw new SQLException("Pedido no encontrado: " + idPedido);
                    estadoActual = rs.getString("estado");
                    idProducto = rs.getInt("id_producto");
                    cantidad = rs.getInt("cantidad");
                }
            }

            // Solo permitir eliminar pedidos pendientes
            if (!"Pendiente".equals(estadoActual)) {
                throw new SQLException("Solo se pueden eliminar pedidos en estado Pendiente. " +
                        "Estado actual: " + estadoActual);
            }

            // Eliminar el pedido
            try (PreparedStatement ps = cn.prepareStatement(
                    "DELETE FROM pedido WHERE id_pedido = ?")) {
                ps.setInt(1, idPedido);
                ps.executeUpdate();
            }

            // Devolver stock
            try (PreparedStatement ps = cn.prepareStatement(
                    "UPDATE catalogo_producto SET stock = stock + ? WHERE id_producto = ?")) {
                ps.setInt(1, cantidad);
                ps.setInt(2, idProducto);
                ps.executeUpdate();
            }

            cn.commit();
        } catch (SQLException e) {
            if (cn != null)
                cn.rollback();
            throw e;
        } finally {
            if (cn != null) {
                cn.setAutoCommit(true);
                cn.close();
            }
        }
    }

    // ===== ELIMINAR PEDIDO CANCELADO (sin devolver stock, ya fue devuelto al
    // cancelar) =====

    /**
     * Elimina un pedido que está en estado Cancelado.
     * No devuelve stock porque ya se devolvió al cancelar desde cocina.
     */
    public void eliminarCancelado(int idPedido) throws SQLException {
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(
                        "DELETE FROM pedido WHERE id_pedido = ? AND estado = 'Cancelado'")) {
            ps.setInt(1, idPedido);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("El pedido no está en estado Cancelado o no existe.");
            }
        }
    }

    // ===== ELIMINAR TODOS LOS PEDIDOS DE UNA CUENTA =====

    /**
     * Elimina todos los pedidos de una cuenta.
     * Devuelve stock solo para pedidos que NO están cancelados
     * (los cancelados ya tuvieron su stock devuelto).
     */
    public void eliminarPorCuenta(int idCuenta) throws SQLException {
        Connection cn = null;
        try {
            cn = ConexionDB.getConnection();
            cn.setAutoCommit(false);

            // Devolver stock de pedidos no cancelados
            try (PreparedStatement ps = cn.prepareStatement(
                    "UPDATE catalogo_producto SET stock = stock + sub.total_cant " +
                            "FROM (SELECT id_producto, SUM(cantidad) AS total_cant " +
                            "      FROM pedido WHERE id_cuenta = ? AND estado != 'Cancelado' " +
                            "      GROUP BY id_producto) sub " +
                            "WHERE catalogo_producto.id_producto = sub.id_producto")) {
                ps.setInt(1, idCuenta);
                ps.executeUpdate();
            }

            // Eliminar todos los pedidos de la cuenta
            try (PreparedStatement ps = cn.prepareStatement(
                    "DELETE FROM pedido WHERE id_cuenta = ?")) {
                ps.setInt(1, idCuenta);
                ps.executeUpdate();
            }

            cn.commit();
        } catch (SQLException e) {
            if (cn != null)
                cn.rollback();
            throw e;
        } finally {
            if (cn != null) {
                cn.setAutoCommit(true);
                cn.close();
            }
        }
    }

    // ===== VERIFICAR PEDIDOS NO CANCELADOS =====

    /**
     * Verifica si una cuenta tiene al menos un pedido con estado diferente a
     * Cancelado.
     */
    public boolean tienePedidosNoCancelados(int idCuenta) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pedido WHERE id_cuenta = ? AND estado != 'Cancelado'";
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCuenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // ===== MAPEO =====

    private Pedido mapearPedidoConProducto(ResultSet rs) throws SQLException {
        return new Pedido(
                rs.getInt("id_pedido"),
                rs.getInt("id_cuenta"),
                rs.getInt("id_producto"),
                rs.getInt("cantidad"),
                EstadoPedido.fromString(rs.getString("estado")),
                rs.getTimestamp("fecha_hora"),
                rs.getString("nombre_producto"),
                rs.getDouble("precio_producto"));
    }
}
