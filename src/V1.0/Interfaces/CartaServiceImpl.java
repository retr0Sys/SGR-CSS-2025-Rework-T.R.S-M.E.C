package Interfaces;

import Clases.abstractas.Producto;
import DAO.ProductoDAO;

import java.sql.SQLException;
import java.util.List;

public class CartaServiceImpl implements CartaService {

    //Permite actualizar la cantidad de un producto por su id y su nuevo stock
    @Override
    public void actualizarStock(int idProducto, int nuevoStock) throws SQLException {
        ProductoDAO.actualizarStock(idProducto, nuevoStock);
    }

    // Permite saber el stock de un producto por su id
    @Override
    public int obtenerStock(int idProducto) throws SQLException {
        return ProductoDAO.obtenerStock(idProducto);
    }

    // Devuelve todos los productos
    @Override
    public List<Producto> listarTodos() throws SQLException {
        return ProductoDAO.listar();
    }
    // Lista todos los productos disponibles
    @Override
    public List<Producto> listarDisponibles() throws SQLException {
        return ProductoDAO.listarDisponibles();
    }

    // Permite actualizar el producto pro su id con el nuevo precio y el estado (disponible, no disponible)
    @Override
    public void actualizarProducto(int idProducto, double nuevoPrecio, int estado) throws SQLException {
        ProductoDAO.actualizarProducto(idProducto, nuevoPrecio, estado);
    }

    //Crear un nuevo producto con todos sus valores
    @Override
    public void crearProducto(Producto producto) throws SQLException {
        ProductoDAO.crearProducto(producto);
    }

    // Permite buscar un producto por su nombre
    @Override
    public Producto buscarPorNombre(String nombre) throws SQLException {
        for (Producto p : ProductoDAO.listar()) {
            if (p.getNombre().equalsIgnoreCase(nombre)) {
                return p;
            }
        }
        return null;
    }
}
