package com.css.restaurante.ui;

import com.css.restaurante.dao.*;
import com.css.restaurante.modelo.Mesa;
import com.css.restaurante.modelo.Pedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel de facturacion.
 * Seleccion de mesa con cuenta abierta, desglose de pedidos,
 * descuento, IVA y cierre de cuenta con generacion de PDF.
 */
public class PanelFacturacion extends JPanel implements MenuPuntoVenta.Refrescable {

    private final MesaDAO mesaDAO = new MesaDAO();
    private final CuentaDAO cuentaDAO = new CuentaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();

    private JComboBox<String> cbMesa;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JLabel lblSubtotal, lblDescuento, lblIVA, lblTotal;
    private JComboBox<String> cbDescuento, cbMetodoPago;
    private double subtotalActual = 0;
    private List<Pedido> pedidosActuales = new ArrayList<>();

    public PanelFacturacion() {
        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeManager.bgContent());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Selector de mesa
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setBackground(ThemeManager.bgContent());
        topPanel.add(crearLabel("Mesa:"));
        cbMesa = new JComboBox<>();
        cbMesa.setPreferredSize(new Dimension(220, 35));
        cbMesa.addActionListener(e -> cargarPedidosDeMesa());
        topPanel.add(cbMesa);
        add(topPanel, BorderLayout.NORTH);

        // Tabla
        modelo = new DefaultTableModel(
                new String[] { "Producto", "Cant.", "Precio Unit.", "Subtotal" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        estilizarTabla(tabla);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(ThemeManager.bgCard());
        scroll.getViewport().setBackground(ThemeManager.bgCard());
        scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.border()));
        add(scroll, BorderLayout.CENTER);

        add(crearPanelResumen(), BorderLayout.EAST);
        cargarMesas();
    }

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.bgCard());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));
        panel.setPreferredSize(new Dimension(280, 0));

        JLabel titulo = new JLabel("Resumen de Cuenta");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(ThemeManager.textPrimary());
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        lblSubtotal = crearLabelMonto("Subtotal: $0.00");
        panel.add(lblSubtotal);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(crearLabel("Descuento:"));
        cbDescuento = new JComboBox<>(new String[] { "0%", "5%", "10%", "15%" });
        cbDescuento.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cbDescuento.setAlignmentX(LEFT_ALIGNMENT);
        cbDescuento.addActionListener(e -> actualizarTotales());
        panel.add(cbDescuento);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        lblDescuento = crearLabelMonto("Descuento: -$0.00");
        panel.add(lblDescuento);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        lblIVA = crearLabelMonto("IVA (22%): $0.00");
        panel.add(lblIVA);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.border());
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(sep);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        lblTotal = new JLabel("Total: $0.00");
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTotal.setForeground(ThemeManager.accent());
        lblTotal.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(lblTotal);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(crearLabel("Metodo de pago:"));
        cbMetodoPago = new JComboBox<>(new String[] { "Efectivo", "Tarjeta", "Transferencia" });
        cbMetodoPago.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cbMetodoPago.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(cbMetodoPago);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnCerrar = new JButton("Cerrar Cuenta y Facturar");
        btnCerrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setBackground(ThemeManager.success());
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setAlignmentX(LEFT_ALIGNMENT);
        btnCerrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        // Cargar icono de facturacion
        try {
            java.net.URL url = getClass().getResource("/assets/pic-fact.png");
            if (url != null) {
                Image img = new ImageIcon(url).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
                btnCerrar.setIcon(new ImageIcon(img));
            }
        } catch (Exception ignored) {
        }
        btnCerrar.addActionListener(e -> cerrarCuenta());
        panel.add(btnCerrar);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void cargarMesas() {
        cbMesa.removeAllItems();
        cbMesa.addItem("-- Seleccionar mesa --");
        try {
            for (Mesa m : mesaDAO.listar()) {
                if (cuentaDAO.tieneCuentaAbierta(m.getIdMesa())) {
                    cbMesa.addItem("Mesa " + m.getIdMesa() + " (Cuenta abierta)");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void cargarPedidosDeMesa() {
        modelo.setRowCount(0);
        subtotalActual = 0;
        pedidosActuales.clear();
        String sel = (String) cbMesa.getSelectedItem();
        if (sel == null || sel.startsWith("--")) {
            actualizarTotales();
            return;
        }

        try {
            int idMesa = Integer.parseInt(sel.split(" ")[1]);
            int idCuenta = cuentaDAO.obtenerIdCuentaAbierta(idMesa);
            if (idCuenta < 0)
                return;

            for (Pedido p : pedidoDAO.listarPorCuenta(idCuenta)) {
                pedidosActuales.add(p);
                if (!"Cancelado".equals(p.getEstado().getValor())) {
                    modelo.addRow(new Object[] {
                            p.getNombreProducto(), p.getCantidad(),
                            String.format("$%.2f", p.getPrecioProducto()),
                            String.format("$%.2f", p.getSubtotal())
                    });
                    subtotalActual += p.getSubtotal();
                }
            }
            actualizarTotales();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void actualizarTotales() {
        String descStr = (String) cbDescuento.getSelectedItem();
        double descPct = descStr != null ? Double.parseDouble(descStr.replace("%", "")) / 100.0 : 0;
        double descuento = subtotalActual * descPct;
        double baseImponible = subtotalActual - descuento;
        double iva = baseImponible * 0.22;
        double total = baseImponible + iva;

        lblSubtotal.setText("Subtotal: $" + String.format("%.2f", subtotalActual));
        lblDescuento.setText("Descuento: -$" + String.format("%.2f", descuento));
        lblIVA.setText("IVA (22%): $" + String.format("%.2f", iva));
        lblTotal.setText("Total: $" + String.format("%.2f", total));
    }

    private void cerrarCuenta() {
        String sel = (String) cbMesa.getSelectedItem();
        if (sel == null || sel.startsWith("--")) {
            JOptionPane.showMessageDialog(this, "Seleccione una mesa primero.");
            return;
        }
        try {
            int idMesa = Integer.parseInt(sel.split(" ")[1]);

            // Calcular totales para el PDF
            String descStr = (String) cbDescuento.getSelectedItem();
            double descPct = descStr != null ? Double.parseDouble(descStr.replace("%", "")) / 100.0 : 0;
            double descuento = subtotalActual * descPct;
            double baseImponible = subtotalActual - descuento;
            double iva = baseImponible * 0.22;
            double total = baseImponible + iva;
            String metodoPago = (String) cbMetodoPago.getSelectedItem();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Cerrar cuenta de la Mesa " + idMesa + "?\nTotal: $" + String.format("%.2f", total),
                    "Confirmar cierre", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION)
                return;

            // Generar PDF de factura
            String pdfPath = FacturaPDF.generar(idMesa, pedidosActuales,
                    subtotalActual, descuento, iva, total, metodoPago != null ? metodoPago : "Efectivo");

            // Cerrar cuenta en BD
            cuentaDAO.cerrarCuenta(idMesa);
            mesaDAO.actualizarEstado(idMesa, "Limpieza");
            mesaDAO.desasignarMesero(idMesa);

            JOptionPane.showMessageDialog(this,
                    "Cuenta cerrada exitosamente.\nMesa " + idMesa + " -> Limpieza\n\nFactura PDF generada en:\n"
                            + pdfPath,
                    "Facturacion completada", JOptionPane.INFORMATION_MESSAGE);

            modelo.setRowCount(0);
            subtotalActual = 0;
            pedidosActuales.clear();
            actualizarTotales();
            cargarMesas();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.textSecondary());
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    private JLabel crearLabelMonto(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lbl.setForeground(ThemeManager.textPrimary());
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
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
    }

    @Override
    public void refrescar() {
        cargarMesas();
    }
}
