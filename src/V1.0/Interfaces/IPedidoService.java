package Interfaces;

import Clases.concret.Pedido;
import java.sql.SQLException;
import java.util.List;

public interface IPedidoService {

    // Permite listar los pedidos pendientes
    List<Pedido> listarPendientes() throws SQLException;

    //Permite actualizar el estado del pedido
    void actualizarEstado(int idPedido, String nuevoEstado) throws SQLException;
}
