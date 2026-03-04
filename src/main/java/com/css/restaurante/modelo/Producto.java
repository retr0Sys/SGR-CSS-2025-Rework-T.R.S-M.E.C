package com.css.restaurante.modelo;

/**
 * Representa un producto del catálogo del restaurante.
 * Unifica las antiguas clases Comida, Bebida y Postre.
 */
public class Producto {

    private int id;
    private String nombre;
    private double precio;
    private CategoriaProducto categoria;
    private int stock;
    private int estado; // 1 = disponible, 0 = no disponible

    public Producto() {
    }

    public Producto(int id, String nombre, double precio, CategoriaProducto categoria, int stock, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.stock = stock;
        this.estado = estado;
    }

    public Producto(String nombre, double precio, CategoriaProducto categoria, int stock) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.stock = stock;
        this.estado = 1;
    }

    // ===== Getters & Setters =====

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public CategoriaProducto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaProducto categoria) {
        this.categoria = categoria;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public boolean isDisponible() {
        return estado == 1 && stock > 0;
    }

    @Override
    public String toString() {
        return nombre + " ($" + String.format("%.0f", precio) + ")";
    }
}
