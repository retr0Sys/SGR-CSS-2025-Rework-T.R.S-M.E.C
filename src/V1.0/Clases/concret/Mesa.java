package Clases.concret;

// Representa una mesa dentro del restaurante
public class Mesa
{
    public String JPMesasIni; // Referencia posible a un componente gráfico (puede eliminarse si no se usa)
    private int idMesa;
    private int capacidad;
    private String estado; // disponible, ocupada, reservada, limpieza, etc.

    // Constructor vacío
    public Mesa()
    {
    }

    // Constructor con parámetros
    public Mesa(int idMesa, int capacidad, String estado)
    {
        this.idMesa = idMesa;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    public int getIdMesa()
    {
        return idMesa;
    }

    public void setIdMesa(int idMesa)
    {
        this.idMesa = idMesa;
    }

    public int getCapacidad()
    {
        return capacidad;
    }

    public void setCapacidad(int capacidad)
    {
        this.capacidad = capacidad;
    }

    public String getEstado()
    {
        return estado;
    }

    public void setEstado(String estado)
    {
        this.estado = estado;
    }

    // Representación en texto del objeto Mesa
    @Override
    public String toString()
    {
        return "Mesa{" +
                "idMesa=" + idMesa +
                ", capacidad=" + capacidad +
                ", estado='" + estado + '\'' +
                '}';
    }
}
