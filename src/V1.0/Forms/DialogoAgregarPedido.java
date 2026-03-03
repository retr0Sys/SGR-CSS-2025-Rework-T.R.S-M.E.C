package Forms;

import Clases.abstractas.Producto;
import DAO.ProductoDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase DialogoAgregarPedido
 *
 * Ventana emergente (JDialog) que permite al usuario seleccionar productos
 * desde una tabla y agregarlos al pedido activo de una mesa.
 *
 * Presenta:
 *  - Un listado de productos obtenidos desde la base de datos.
 *  - Un campo para indicar la cantidad.
 *  - Botones para confirmar o cancelar la selección.
 *
 * Al confirmar, el producto se agrega a la tabla de pedidos del formulario principal
 * y se actualiza el subtotal correspondiente.
 */
public class DialogoAgregarPedido extends JDialog
{
    // Componentes de la interfaz
    private JTable tblProductos;
    private JButton btnAgregar;
    private JButton btnCancelar;
    private JLabel txtSubtotal;
    private JPanel contentPane;
    private JSpinner spnCantidad;

    /**
     * Constructor del diálogo.
     *
     * @param parent               Ventana principal que invoca el diálogo.
     * @param tablaDestino         Tabla donde se agregará el producto seleccionado.
     * @param txtSubtotalDestino   Label que muestra el subtotal del pedido actual.
     */
    public DialogoAgregarPedido(JFrame parent, JTable tablaDestino, JLabel txtSubtotalDestino)
    {
        super(parent, true); // true indica que es un diálogo modal
        this.txtSubtotal = txtSubtotalDestino;

        // ==============================
        // Estética general del formulario
        // ==============================
        Color fondo = new Color(245, 245, 245);
        Color acento = new Color(255, 159, 101);
        Font fuenteGeneral = new Font("Segoe UI", Font.PLAIN, 14);
        Font fuenteBoton = new Font("Segoe UI", Font.BOLD, 14);

        setTitle("Agregar Productos");
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(fondo);

        // ==============================
        // Tabla principal de productos
        // ==============================
        tblProductos = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblProductos);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Configuración visual de la tabla
        tblProductos.setFont(fuenteGeneral);
        tblProductos.setRowHeight(28);
        tblProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblProductos.getTableHeader().setBackground(acento);
        tblProductos.getTableHeader().setForeground(Color.WHITE);

        // Agregar la tabla al centro de la ventana
        add(scrollPane, BorderLayout.CENTER);

        // ==============================
        // Panel inferior (botones y cantidad)
        // ==============================
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelInferior.setBackground(fondo);

        // Etiqueta de cantidad
        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setFont(fuenteGeneral);
        panelInferior.add(lblCantidad);

        // Selector numérico de cantidad (de 1 a 100)
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spnCantidad.setFont(fuenteGeneral);
        ((JSpinner.DefaultEditor) spnCantidad.getEditor()).getTextField().setColumns(3);
        panelInferior.add(spnCantidad);

        // Botón "Agregar"
        btnAgregar = new JButton("Agregar");
        btnAgregar.setFont(fuenteBoton);
        btnAgregar.setBackground(acento);
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelInferior.add(btnAgregar);

        // Botón "Cancelar"
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(fuenteBoton);
        btnCancelar.setBackground(new Color(200, 200, 200));
        btnCancelar.setForeground(Color.BLACK);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelInferior.add(btnCancelar);

        // Agregar el panel inferior a la ventana
        add(panelInferior, BorderLayout.SOUTH);

        // ==============================
        // Cargar productos desde la base
        // ==============================
        cargarProductosDesdeDB();

        // ==============================
        // Eventos de los botones
        // ==============================

        // Evento botón "Agregar"
        btnAgregar.addActionListener(e ->
        {
            int fila = tblProductos.getSelectedRow();

            // Validar que se haya seleccionado un producto
            if (fila == -1)
            {
                JOptionPane.showMessageDialog(this, "Seleccione un producto.");
                return;
            }

            // Obtener el modelo de la tabla destino (en FormMesa)
            DefaultTableModel modelDestino = (DefaultTableModel) tablaDestino.getModel();

            // Obtener los datos del producto seleccionado
            int idProducto = (int) tblProductos.getValueAt(fila, 0);
            String nombre = tblProductos.getValueAt(fila, 1).toString();
            double precio = Double.parseDouble(tblProductos.getValueAt(fila, 2).toString());
            int cantidad = (Integer) spnCantidad.getValue();

            // Agregar el producto seleccionado con su cantidad y subtotal
            modelDestino.addRow(new Object[]
                    {
                            idProducto,
                            nombre,
                            cantidad,
                            precio * cantidad
                    });

            // ==============================
            // Actualizar subtotal en pantalla
            // ==============================
            double subtotalActual = Double.parseDouble(this.txtSubtotal.getText());
            subtotalActual += precio * cantidad;
            this.txtSubtotal.setText(String.valueOf(subtotalActual));

            // Cerrar el diálogo
            dispose();
        });

        // Evento botón "Cancelar"
        btnCancelar.addActionListener(e -> dispose());
    }

    /**
     * Carga los productos disponibles desde la base de datos.
     *
     * Utiliza la clase DAO `ProductoDAO` para obtener una lista de productos
     * y los muestra en una tabla no editable.
     */
    private void cargarProductosDesdeDB()
    {
        // Crear modelo de tabla no editable
        DefaultTableModel model = new DefaultTableModel(new String[]
                {
                        "ID", "Nombre", "Precio", "Stock"
                }, 0)
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false; // Ninguna celda editable
            }
        };

        try
        {
            // Obtener lista de productos desde la base de datos
            List<Producto> productos = ProductoDAO.listarDisponibles();

            // Agregar productos al modelo de tabla
            for (Producto p : productos)
            {
                model.addRow(new Object[]
                        {
                                p.getId(),
                                p.getNombre(),
                                p.getPrecio(),
                                p.getStock() // Mostrar stock disponible
                        });
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(this, "Error cargando productos: " + e.getMessage());
        }

        // Aplicar el modelo a la tabla
        tblProductos.setModel(model);
    }
}
