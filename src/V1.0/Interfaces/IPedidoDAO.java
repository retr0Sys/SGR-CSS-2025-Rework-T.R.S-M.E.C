package Interfaces;

import Clases.concret.Pedido;
import java.sql.SQLException;
import java.util.List;

public interface IPedidoDAO {

    //Permite agregar un pedido por el id de la cuenta, el id del producto y la cantidad
    void agregarPedido(int idCuenta, int idProducto, int cantidad) throws SQLException;

    //Este método permite listar por cada cuenta abierta
    List<Pedido> listarPorCuenta(int idCuenta) throws SQLException;

    //Este método lista todas las cuentas
    List<Pedido> listar() throws SQLException;

    //Este método permite caluclar el tota por cada cuenta, con su respectivo id
    double calcularTotalCuenta(int idCuenta) throws SQLException;

    // Permite listar la cuenta por nombre
    List<Pedido> listarPorCuentaConNombre(int idCuenta) throws SQLException;

    //Permite listas las cuentas pendientes
    List<Pedido> listarPendientes() throws SQLException; // Para la vista Cocina

    //Permite actualizar el estado de cada mesa
    void actualizarEstado(int idPedido, String nuevoEstado) throws SQLException;
}
