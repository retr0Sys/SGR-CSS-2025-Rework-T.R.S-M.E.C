package Clases.concret;

import Clases.abstractas.Producto;

// Representa un producto del tipo bebida
public class Bebida extends Producto
{
    // Constructor que inicializa los datos de la bebida
    public Bebida(int id, String nombre, double precio, int estado)
    {
        super(id, nombre, precio, estado);
    }

    // Aplica un impuesto del 22% al precio de la bebida
    @Override
    public void calcularImpuesto()
    {
        this.precio = this.precio * 1.22;
    }

    // Retorna la categoría del producto
    @Override
    public String getCategoria()
    {
        return "bebida";
    }
}
