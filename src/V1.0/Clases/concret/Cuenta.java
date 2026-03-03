package Clases.concret;

import java.sql.Timestamp;

// Representa una cuenta asociada a una mesa en el restaurante
public class Cuenta
{
    private int idCuenta;
    private int idMesa;
    private Timestamp fechaApertura;
    private Timestamp fechaCierre;
    private int estado; // 1 = abierta, 0 = cerrada

    // Constructor vacío
    public Cuenta()
    {
    }

    // Constructor con todos los campos
    public Cuenta(int idCuenta, int idMesa, Timestamp fechaApertura, Timestamp fechaCierre, int estado)
    {
        this.idCuenta = idCuenta;
        this.idMesa = idMesa;
        this.fechaApertura = fechaApertura;
        this.fechaCierre = fechaCierre;
        this.estado = estado;
    }

    // Constructor usado al crear una nueva cuenta
    public Cuenta(int idMesa, int estado)
    {
        this.idMesa = idMesa;
        this.estado = estado;
    }

    public int getIdCuenta()
    {
        return idCuenta;
    }

    public void setIdCuenta(int idCuenta)
    {
        this.idCuenta = idCuenta;
    }

    public int getIdMesa()
    {
        return idMesa;
    }

    public void setIdMesa(int idMesa)
    {
        this.idMesa = idMesa;
    }

    public Timestamp getFechaApertura()
    {
        return fechaApertura;
    }

    public void setFechaApertura(Timestamp fechaApertura)
    {
        this.fechaApertura = fechaApertura;
    }

    public Timestamp getFechaCierre()
    {
        return fechaCierre;
    }

    public void setFechaCierre(Timestamp fechaCierre)
    {
        this.fechaCierre = fechaCierre;
    }

    public int getEstado()
    {
        return estado;
    }

    public void setEstado(int estado)
    {
        this.estado = estado;
    }

    // Devuelve una representación en texto de la cuenta
    @Override
    public String toString()
    {
        return "Cuenta{" +
                "idCuenta=" + idCuenta +
                ", idMesa=" + idMesa +
                ", fechaApertura=" + fechaApertura +
                ", fechaCierre=" + fechaCierre +
                ", estado=" + estado +
                '}';
    }
}
