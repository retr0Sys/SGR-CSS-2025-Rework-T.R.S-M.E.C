package com.css.restaurante.ui;

import com.css.restaurante.dao.ProductoDAO;
import com.css.restaurante.modelo.CategoriaProducto;
import com.css.restaurante.modelo.Producto;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de la Carta/Menú del restaurante.
 * Permite listar, crear y modificar productos.
 * Incluye validación de entradas con InputValidator.
 */
public class PanelCarta extends JPanel implements MenuPuntoVenta.Refrescable {

    private final ProductoDAO productoDAO = new ProductoDAO();
    private JTable tabla;
    private DefaultTableModel modelo;

    private JTextField txtNombre, txtPrecio, txtStock;
    private JComboBox<String> cbCategoria, cbEstado;
    private JButton btnCrear, btnModificar;
    private int productoSeleccionadoId = -1;

    public PanelCarta() {
        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeManager.bgContent());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // ====== Panel izquierdo: Tabla ======
        JPanel panelTabla = new JPanel(new BorderLayout(0, 10));
        panelTabla.setBackground(ThemeManager.bgContent());

        // Filtros de categoría
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.setBackground(ThemeManager.bgContent());
        filtros.add(crearFiltroBtn("Todos", null));
        filtros.add(crearFiltroBtn("🍔 Comida", CategoriaProducto.COMIDA));
        filtros.add(crearFiltroBtn("🥤 Bebida", CategoriaProducto.BEBIDA));
        filtros.add(crearFiltroBtn("🍰 Postre", CategoriaProducto.POSTRE));
        panelTabla.add(filtros, BorderLayout.NORTH);

        // Tabla
        modelo = new DefaultTableModel(
                new String[] { "ID", "Nombre", "Precio", "Categoría", "Stock", "Estado" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        estilizarTabla(tabla);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                cargarProductoSeleccionado();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(ThemeManager.bgCard());
        scroll.getViewport().setBackground(ThemeManager.bgCard());
        scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.border()));
        panelTabla.add(scroll, BorderLayout.CENTER);

        // ====== Panel derecho: Formulario ======
        JPanel panelForm = crearFormulario();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelTabla, panelForm);
        split.setDividerLocation(650);
        split.setDividerSize(4);
        split.setBackground(ThemeManager.bgContent());
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        cargarProductos(null);
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.bgCard());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        panel.setPreferredSize(new Dimension(300, 0));

        JLabel titulo = new JLabel("Producto");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(ThemeManager.textPrimary());
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        txtNombre = agregarCampo(panel, "Nombre");
        txtPrecio = agregarCampo(panel, "Precio");
        txtStock = agregarCampo(panel, "Stock");

        panel.add(crearLabel("Categoría"));
        cbCategoria = new JComboBox<>(new String[] { "Comida", "Bebida", "Postre" });
        cbCategoria.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cbCategoria.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(cbCategoria);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        panel.add(crearLabel("Estado"));
        cbEstado = new JComboBox<>(new String[] { "Disponible", "No disponible" });
        cbEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cbEstado.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(cbEstado);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        btnCrear = crearBotonAccion("✚ Crear Producto", ThemeManager.success());
        btnCrear.addActionListener(e -> crearProducto());
        panel.add(btnCrear);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        btnModificar = crearBotonAccion("✎ Modificar Producto", ThemeManager.accent());
        btnModificar.setEnabled(false);
        btnModificar.addActionListener(e -> modificarProducto());
        panel.add(btnModificar);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnLimpiar = crearBotonAccion("✕ Limpiar", ThemeManager.textMuted());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        panel.add(btnLimpiar);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JTextField agregarCampo(JPanel contenedor, String etiqueta) {
        contenedor.add(crearLabel(etiqueta));
        JTextField txt = new JTextField();
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txt.setAlignmentX(LEFT_ALIGNMENT);
        txt.setBackground(ThemeManager.inputBg());
        txt.setForeground(ThemeManager.textPrimary());
        txt.setCaretColor(ThemeManager.textPrimary());
        contenedor.add(txt);
        contenedor.add(Box.createRigidArea(new Dimension(0, 12)));
        return txt;
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.textSecondary());
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    private JButton crearFiltroBtn(String texto, CategoriaProducto cat) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setForeground(ThemeManager.textSecondary());
        btn.setBackground(ThemeManager.bgCard());
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> cargarProductos(cat));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(ThemeManager.hoverBg());
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(ThemeManager.bgCard());
            }
        });
        return btn;
    }

    private JButton crearBotonAccion(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return btn;
    }

    private void estilizarTabla(JTable t) {
        t.setBackground(ThemeManager.bgCard());
        t.setForeground(ThemeManager.textPrimary());
        t.setGridColor(ThemeManager.border());
        t.setSelectionBackground(ThemeManager.accentSoft());
        t.setSelectionForeground(ThemeManager.accent());
        t.setRowHeight(34);
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.getTableHeader().setBackground(ThemeManager.bgDark());
        t.getTableHeader().setForeground(ThemeManager.textSecondary());
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);

        t.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(CENTER);
                setForeground("Disponible".equals(value) ? ThemeManager.success() : ThemeManager.danger());
                setBackground(isSelected ? ThemeManager.accentSoft() : ThemeManager.bgCard());
                return this;
            }
        });
    }

    private void cargarProductos(CategoriaProducto filtro) {
        try {
            List<Producto> productos = (filtro == null)
                    ? productoDAO.listar()
                    : productoDAO.listarPorCategoria(filtro);
            modelo.setRowCount(0);
            for (Producto p : productos) {
                modelo.addRow(new Object[] {
                        p.getId(), p.getNombre(),
                        String.format("$%.2f", p.getPrecio()),
                        p.getCategoria().toString(), p.getStock(),
                        p.getEstado() == 1 ? "Disponible" : "No disponible"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarProductoSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row < 0)
            return;
        productoSeleccionadoId = (int) modelo.getValueAt(row, 0);
        txtNombre.setText((String) modelo.getValueAt(row, 1));
        txtPrecio.setText(((String) modelo.getValueAt(row, 2)).replace("$", ""));
        txtStock.setText(String.valueOf(modelo.getValueAt(row, 4)));
        cbCategoria.setSelectedItem(modelo.getValueAt(row, 3));
        cbEstado.setSelectedItem(modelo.getValueAt(row, 5));
        btnModificar.setEnabled(true);
    }

    /** Crea un producto con validación de seguridad completa */
    private void crearProducto() {
        String nombre = InputValidator.sanitizar(txtNombre.getText());
        String precioStr = InputValidator.sanitizar(txtPrecio.getText());
        String stockStr = InputValidator.sanitizar(txtStock.getText());

        // Validación centralizada
        String error = InputValidator.validarCampoProducto(nombre, precioStr, stockStr);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double precio = InputValidator.parsePrecio(precioStr);
            int stock = InputValidator.parseStock(stockStr);
            CategoriaProducto cat = CategoriaProducto.fromString(
                    ((String) cbCategoria.getSelectedItem()).toLowerCase());

            Producto p = new Producto(nombre, precio, cat, stock);
            productoDAO.crear(p);
            JOptionPane.showMessageDialog(this, "Producto creado exitosamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarProductos(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al crear producto: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Modifica un producto con validación de seguridad completa */
    private void modificarProducto() {
        if (productoSeleccionadoId < 0)
            return;
        String precioStr = InputValidator.sanitizar(txtPrecio.getText());
        String stockStr = InputValidator.sanitizar(txtStock.getText());

        if (!InputValidator.esPrecioValido(precioStr)) {
            JOptionPane.showMessageDialog(this, "Precio inválido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!InputValidator.esStockValido(stockStr)) {
            JOptionPane.showMessageDialog(this, "Stock inválido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double precio = InputValidator.parsePrecio(precioStr);
            int stock = InputValidator.parseStock(stockStr);
            int estado = "Disponible".equals(cbEstado.getSelectedItem()) ? 1 : 0;

            productoDAO.actualizar(productoSeleccionadoId, precio, estado);
            productoDAO.actualizarStock(productoSeleccionadoId, stock);
            JOptionPane.showMessageDialog(this, "Producto actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarProductos(null);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
        cbCategoria.setSelectedIndex(0);
        cbEstado.setSelectedIndex(0);
        productoSeleccionadoId = -1;
        btnModificar.setEnabled(false);
        tabla.clearSelection();
    }

    @Override
    public void refrescar() {
        cargarProductos(null);
    }
}
