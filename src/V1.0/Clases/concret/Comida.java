package Clases.concret;

import Clases.abstractas.Producto;

// Representa un producto del tipo comida
public class Comida extends Producto
{
    // Constructor que inicializa los datos de la comida
    public Comida(int id, String nombre, double precio, int estado)
    {
        super(id, nombre, precio, estado);
    }

    // Aplica un impuesto del 10% al precio de la comida
    @Override
    public void calcularImpuesto()
    {
        this.precio = this.precio * 1.10;
    }

    // Retorna la categoría del producto
    @Override
    public String getCategoria()
    {
        return "comida";
    }
}
