package com.css.restaurante.ui;

import com.css.restaurante.dao.PedidoDAO;
import com.css.restaurante.modelo.EstadoPedido;
import com.css.restaurante.modelo.Pedido;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de cocina (comanda digital).
 * Muestra pedidos pendientes/en preparación con estados color-coded.
 * Auto-refresh cada 15 segundos.
 */
public class PanelCocina extends JPanel implements MenuPuntoVenta.Refrescable {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private JTable tabla;
    private DefaultTableModel modelo;
    private Timer autoRefresh;

    public PanelCocina() {
        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeManager.bgContent());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Acciones
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        acciones.setBackground(ThemeManager.bgContent());
        acciones.add(crearBotonAccion("🔥 En preparación", ThemeManager.warning(),
                () -> actualizarEstado(EstadoPedido.EN_PREPARACION)));
        acciones.add(
                crearBotonAccion("✅ Servido", ThemeManager.success(), () -> actualizarEstado(EstadoPedido.SERVIDO)));
        acciones.add(
                crearBotonAccion("❌ Cancelar", ThemeManager.danger(), () -> actualizarEstado(EstadoPedido.CANCELADO)));
        acciones.add(Box.createHorizontalStrut(30));
        acciones.add(crearBotonAccion("🔄 Refrescar", ThemeManager.info(), this::cargarPedidos));
        add(acciones, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
                new String[] { "ID", "Mesa", "Producto", "Cant.", "Estado", "Hora" }, 0) {
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

        cargarPedidos();
        autoRefresh = new Timer(15000, e -> cargarPedidos());
        autoRefresh.start();
    }

    private void cargarPedidos() {
        try {
            List<Pedido> pedidos = pedidoDAO.listarPendientes();
            modelo.setRowCount(0);
            for (Pedido p : pedidos) {
                // OPT-3: idMesa ya viene cargado por JOIN en listarPendientes()
                int mesa = p.getIdMesa();
                modelo.addRow(new Object[] {
                        p.getIdPedido(), "Mesa " + mesa, p.getNombreProducto(),
                        p.getCantidad(), p.getEstado().toString(),
                        p.getFechaHora() != null
                                ? new java.text.SimpleDateFormat("HH:mm").format(p.getFechaHora())
                                : ""
                });
            }
        } catch (Exception ex) {
            if (modelo.getRowCount() == 0)
                System.err.println("Error al cargar pedidos: " + ex.getMessage());
        }
    }

    private void actualizarEstado(EstadoPedido estado) {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idPedido = (int) modelo.getValueAt(row, 0);
        try {
            pedidoDAO.actualizarEstado(idPedido, estado);
            JOptionPane.showMessageDialog(this,
                    "Pedido #" + idPedido + " → " + estado.toString(),
                    "Estado actualizado", JOptionPane.INFORMATION_MESSAGE);
            cargarPedidos();
        } catch (Exception ex) {
            System.err.println("[SGR] Error al actualizar estado: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error al actualizar estado. Contacte al administrador.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void estilizarTabla(JTable t) {
        t.setBackground(ThemeManager.bgCard());
        t.setForeground(ThemeManager.textPrimary());
        t.setGridColor(ThemeManager.border());
        t.setSelectionBackground(ThemeManager.accentSoft());
        t.setSelectionForeground(ThemeManager.accent());
        t.setRowHeight(38);
        t.setFont(new Font("SansSerif", Font.PLAIN, 14));
        t.getTableHeader().setBackground(ThemeManager.bgDark());
        t.getTableHeader().setForeground(ThemeManager.textSecondary());
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);

        t.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(CENTER);
                if ("Pendiente".equals(value))
                    setForeground(ThemeManager.warning());
                else if ("En preparación".equals(value))
                    setForeground(new Color(255, 152, 0));
                else if ("Servido".equals(value))
                    setForeground(ThemeManager.success());
                else
                    setForeground(ThemeManager.danger());
                setBackground(isSelected ? ThemeManager.accentSoft() : ThemeManager.bgCard());
                return this;
            }
        });
    }

    private JButton crearBotonAccion(String texto, Color color, Runnable action) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(175, 40));
        btn.addActionListener(e -> action.run());
        return btn;
    }

    @Override
    public void refrescar() {
        cargarPedidos();
    }
}
