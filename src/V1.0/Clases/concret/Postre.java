package Clases.concret;

import Clases.abstractas.Producto;

// Representa un producto del tipo postre
public class Postre extends Producto
{
    public Postre(int id, String nombre, double precio, int estado)
    {
        super(id, nombre, precio, estado);
    }

    @Override
    public void calcularImpuesto()
    {
        this.precio = this.precio * 1.15; // 15%
    }

    @Override
    public String getCategoria()
    {
        return "postre";
    }
}
