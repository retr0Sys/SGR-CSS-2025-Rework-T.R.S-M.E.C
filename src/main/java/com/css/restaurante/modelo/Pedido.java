package com.css.restaurante.modelo;

import java.sql.Timestamp;

/**
 * Representa un pedido asociado a una cuenta y un producto.
 */
public class Pedido {

    private int idPedido;
    private int idCuenta;
    private int idProducto;
    private int cantidad;
    private EstadoPedido estado;
    private Timestamp fechaHora;

    // Datos auxiliares del producto (cargados por JOIN)
    private String nombreProducto;
    private double precioProducto;

    public Pedido() {}

    public Pedido(int idPedido, int idCuenta, int idProducto, int cantidad,
                  EstadoPedido estado, Timestamp fechaHora) {
        this.idPedido = idPedido;
        this.idCuenta = idCuenta;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.estado = estado;
        this.fechaHora = fechaHora;
    }

    public Pedido(int idPedido, int idCuenta, int idProducto, int cantidad,
                  EstadoPedido estado, Timestamp fechaHora,
                  String nombreProducto, double precioProducto) {
        this(idPedido, idCuenta, idProducto, cantidad, estado, fechaHora);
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
    }

    // ===== Getters & Setters =====

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public int getIdCuenta() { return idCuenta; }
    public void setIdCuenta(int idCuenta) { this.idCuenta = idCuenta; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public Timestamp getFechaHora() { return fechaHora; }
    public void setFechaHora(Timestamp fechaHora) { this.fechaHora = fechaHora; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public double getPrecioProducto() { return precioProducto; }
    public void setPrecioProducto(double precioProducto) { this.precioProducto = precioProducto; }

    public double getSubtotal() { return precioProducto * cantidad; }

    @Override
    public String toString() {
        return nombreProducto + " x" + cantidad + " (" + estado + ")";
    }
}
