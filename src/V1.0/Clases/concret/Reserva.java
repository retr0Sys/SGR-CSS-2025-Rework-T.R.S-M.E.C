package Clases.concret;

import java.sql.Date;
import java.sql.Time;

// Representa una reserva realizada para una mesa en una fecha y hora determinadas
public class Reserva
{
    private int idReserva;
    private int idMesa;
    private String nombre;
    private String apellido;
    private Date fecha;
    private Time hora;

    // Constructor vacío
    public Reserva()
    {
    }

    // Constructor completo
    public Reserva(int idReserva, int idMesa, String nombre, String apellido, Date fecha, Time hora)
    {
        this.idReserva = idReserva;
        this.idMesa = idMesa;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fecha = fecha;
        this.hora = hora;
    }

    // Constructor sin idReserva (para inserciones)
    public Reserva(int idMesa, String nombre, String apellido, Date fecha, Time hora)
    {
        this.idMesa = idMesa;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getIdReserva()
    {
        return idReserva;
    }

    public void setIdReserva(int idReserva)
    {
        this.idReserva = idReserva;
    }

    public int getIdMesa()
    {
        return idMesa;
    }

    public void setIdMesa(int idMesa)
    {
        this.idMesa = idMesa;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public String getApellido()
    {
        return apellido;
    }

    public void setApellido(String apellido)
    {
        this.apellido = apellido;
    }

    public Date getFecha()
    {
        return fecha;
    }

    public void setFecha(Date fecha)
    {
        this.fecha = fecha;
    }

    public Time getHora()
    {
        return hora;
    }

    public void setHora(Time hora)
    {
        this.hora = hora;
    }

    @Override
    public String toString()
    {
        return "Reserva{" +
                "idReserva=" + idReserva +
                ", idMesa=" + idMesa +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", fecha=" + fecha +
                ", hora=" + hora +
                '}';
    }
}
