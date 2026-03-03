package Interfaces;

import Clases.abstractas.Producto;
import java.sql.SQLException;
import java.util.List;


public interface CartaService {

    // Retorna todos los productos, estén disponibles o no
    List<Producto> listarTodos() throws SQLException;

    // Retorna solo los productos disponibles
    List<Producto> listarDisponibles() throws SQLException;

    // Actualiza el precio y estado de un producto
    void actualizarProducto(int idProducto, double nuevoPrecio, int estado) throws SQLException;

    // Crea un nuevo producto
    void crearProducto(Producto producto) throws SQLException;

    // Busca un producto por nombre
    Producto buscarPorNombre(String nombre) throws SQLException;

    // Actualiza el stock mediante el id y el nuevo stock
    void actualizarStock(int idProducto, int nuevoStock) throws SQLException;

    // Cosulta para saber cuanto stock hay por un id
    int obtenerStock(int idProducto) throws SQLException;

}
