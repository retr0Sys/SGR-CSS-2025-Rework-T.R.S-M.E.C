package Clases.concret;

import java.sql.Timestamp;

// Representa un pedido asociado a una cuenta y un producto
public class Pedido
{
    private int idPedido;
    private int idCuenta;
    private int idProducto;
    private int cantidad;
    private Timestamp fechaHora;

    // Información adicional del producto
    private String nombreProducto;
    private double precioProducto;

    // Constructor base
    public Pedido(int idPedido, int idCuenta, int idProducto, int cantidad, Timestamp fechaHora)
    {
        this.idPedido = idPedido;
        this.idCuenta = idCuenta;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.fechaHora = fechaHora;
    }

    // Constructor extendido con datos del producto
    public Pedido(int idPedido, int idCuenta, int idProducto, int cantidad,
                  Timestamp fechaHora, String nombreProducto, double precioProducto)
    {
        this(idPedido, idCuenta, idProducto, cantidad, fechaHora);
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
    }

    public int getIdPedido()
    {
        return idPedido;
    }

    public int getIdCuenta()
    {
        return idCuenta;
    }

    public int getIdProducto()
    {
        return idProducto;
    }

    public int getCantidad()
    {
        return cantidad;
    }

    public Timestamp getFechaHora()
    {
        return fechaHora;
    }

    public String getNombreProducto()
    {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto)
    {
        this.nombreProducto = nombreProducto;
    }

    public double getPrecioProducto()
    {
        return precioProducto;
    }

    public void setPrecioProducto(double precioProducto)
    {
        this.precioProducto = precioProducto;
    }

    // Calcula el subtotal del pedido (precio * cantidad)
    public double getSubtotal()
    {
        return precioProducto * cantidad;
    }

    @Override
    public String toString()
    {
        return "Pedido{" +
                "idPedido=" + idPedido +
                ", idCuenta=" + idCuenta +
                ", idProducto=" + idProducto +
                ", cantidad=" + cantidad +
                ", fechaHora=" + fechaHora +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", precioProducto=" + precioProducto +
                '}';
    }
}
