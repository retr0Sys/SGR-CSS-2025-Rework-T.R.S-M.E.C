package com.css.restaurante.ui;

import com.css.restaurante.dao.ConexionDB;
import com.css.restaurante.dao.MesaDAO;
import com.css.restaurante.modelo.Mesa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.util.List;

/**
 * Panel de resumen/reportes del restaurante.
 * Tarjetas de estadísticas, ventas del día, top 3, pedidos por mesa.
 */
public class PanelResumen extends JPanel implements MenuPuntoVenta.Refrescable {

    private final MesaDAO mesaDAO = new MesaDAO();
    private JTable tabla;
    private DefaultTableModel modelo;
    private JComboBox<String> cbMesa;
    private JLabel lblVentasHoy, lblPedidosHoy, lblTopProducto;

    public PanelResumen() {
        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeManager.bgContent());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Tarjetas de estadísticas
        JPanel tarjetas = new JPanel(new GridLayout(1, 3, 15, 0));
        tarjetas.setBackground(ThemeManager.bgContent());
        lblVentasHoy = new JLabel("$0.00");
        tarjetas.add(crearTarjeta("💰 Ventas del Día", lblVentasHoy, ThemeManager.success()));
        lblPedidosHoy = new JLabel("0");
        tarjetas.add(crearTarjeta("📋 Pedidos Hoy", lblPedidosHoy, ThemeManager.info()));
        lblTopProducto = new JLabel("—");
        tarjetas.add(crearTarjeta("🏆 Producto Top", lblTopProducto, ThemeManager.accent()));
        add(tarjetas, BorderLayout.NORTH);

        // Tabla
        modelo = new DefaultTableModel() {
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

        // Acciones
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        acciones.setBackground(ThemeManager.bgContent());
        acciones.add(crearBotonConsulta("📊 Ventas del Día", this::cargarVentasDelDia));
        acciones.add(crearBotonConsulta("🏆 Top 3 Productos", this::cargarTop3));
        JLabel lblMesa = new JLabel("Mesa:");
        lblMesa.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblMesa.setForeground(ThemeManager.textSecondary());
        acciones.add(lblMesa);
        cbMesa = new JComboBox<>();
        cbMesa.setPreferredSize(new Dimension(130, 32));
        acciones.add(cbMesa);
        acciones.add(crearBotonConsulta("🪑 Pedidos por Mesa", this::cargarPedidosPorMesa));
        add(acciones, BorderLayout.SOUTH);

        cargarDatosIniciales();
    }

    private JPanel crearTarjeta(String titulo, JLabel lblValor, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.bgCard());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 14, 14));
                // Línea de acento izquierda
                g2.setColor(accentColor);
                g2.fill(new RoundRectangle2D.Double(0, 0, 4, getHeight(), 14, 14));
                g2.fillRect(2, 0, 2, getHeight());
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 20));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTitulo.setForeground(ThemeManager.textSecondary());
        lblTitulo.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblTitulo);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        lblValor.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblValor.setForeground(accentColor);
        lblValor.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblValor);
        return card;
    }

    private void cargarDatosIniciales() {
        cbMesa.removeAllItems();
        try {
            for (Mesa m : mesaDAO.listar())
                cbMesa.addItem("Mesa " + m.getIdMesa());
        } catch (Exception ignored) {
        }
        cargarEstadisticas();
        cargarVentasDelDia();
    }

    private void cargarEstadisticas() {
        try (Connection cn = ConexionDB.getConnection()) {
            try (Statement st = cn.createStatement();
                    ResultSet rs = st.executeQuery(
                            "SELECT COALESCE(SUM(cp.precio * p.cantidad), 0) AS total " +
                                    "FROM pedido p JOIN catalogo_producto cp ON p.id_producto = cp.id_producto " +
                                    "WHERE DATE(p.fecha_hora) = CURRENT_DATE AND p.estado != 'Cancelado'")) {
                if (rs.next())
                    lblVentasHoy.setText("$" + String.format("%.0f", rs.getDouble("total")));
            }
            try (Statement st = cn.createStatement();
                    ResultSet rs = st.executeQuery(
                            "SELECT COUNT(*) AS total FROM pedido WHERE DATE(fecha_hora) = CURRENT_DATE AND estado != 'Cancelado'")) {
                if (rs.next())
                    lblPedidosHoy.setText(String.valueOf(rs.getInt("total")));
            }
            try (Statement st = cn.createStatement();
                    ResultSet rs = st.executeQuery(
                            "SELECT cp.nombre, SUM(p.cantidad) AS vendido " +
                                    "FROM pedido p JOIN catalogo_producto cp ON p.id_producto = cp.id_producto " +
                                    "WHERE p.estado != 'Cancelado' " +
                                    "GROUP BY cp.nombre ORDER BY vendido DESC LIMIT 1")) {
                lblTopProducto.setText(rs.next() ? rs.getString("nombre") : "—");
            }
        } catch (Exception ex) {
            System.err.println("Error en estadísticas: " + ex.getMessage());
        }
    }

    private void cargarVentasDelDia() {
        try (Connection cn = ConexionDB.getConnection();
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT c.id_mesa AS mesa, cp.nombre AS producto, p.cantidad, " +
                                "cp.precio, (cp.precio * p.cantidad) AS subtotal, p.estado " +
                                "FROM pedido p " +
                                "JOIN catalogo_producto cp ON p.id_producto = cp.id_producto " +
                                "JOIN cuenta c ON p.id_cuenta = c.id_cuenta " +
                                "WHERE DATE(p.fecha_hora) = CURRENT_DATE ORDER BY p.fecha_hora DESC")) {
            resetTabla("Mesa", "Producto", "Cant.", "Precio", "Subtotal", "Estado");
            while (rs.next()) {
                modelo.addRow(new Object[] {
                        "Mesa " + rs.getInt("mesa"), rs.getString("producto"),
                        rs.getInt("cantidad"), String.format("$%.0f", rs.getDouble("precio")),
                        String.format("$%.0f", rs.getDouble("subtotal")), rs.getString("estado")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
        cargarEstadisticas();
    }

    private void cargarPedidosPorMesa() {
        String sel = (String) cbMesa.getSelectedItem();
        if (sel == null)
            return;
        int idMesa = Integer.parseInt(sel.replace("Mesa ", "").trim());
        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(
                        "SELECT cp.nombre AS producto, p.cantidad, cp.precio, " +
                                "(cp.precio * p.cantidad) AS subtotal, p.estado, p.fecha_hora " +
                                "FROM pedido p JOIN catalogo_producto cp ON p.id_producto = cp.id_producto " +
                                "JOIN cuenta c ON p.id_cuenta = c.id_cuenta " +
                                "WHERE c.id_mesa = ? ORDER BY p.fecha_hora DESC")) {
            ps.setInt(1, idMesa);
            try (ResultSet rs = ps.executeQuery()) {
                resetTabla("Producto", "Cant.", "Precio", "Subtotal", "Estado", "Hora");
                while (rs.next()) {
                    modelo.addRow(new Object[] {
                            rs.getString("producto"), rs.getInt("cantidad"),
                            String.format("$%.0f", rs.getDouble("precio")),
                            String.format("$%.0f", rs.getDouble("subtotal")), rs.getString("estado"),
                            rs.getTimestamp("fecha_hora") != null
                                    ? new java.text.SimpleDateFormat("HH:mm").format(rs.getTimestamp("fecha_hora"))
                                    : ""
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void cargarTop3() {
        try (Connection cn = ConexionDB.getConnection();
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT cp.nombre AS producto, SUM(p.cantidad) AS total_vendido, " +
                                "cp.precio, SUM(cp.precio * p.cantidad) AS ingresos " +
                                "FROM pedido p JOIN catalogo_producto cp ON p.id_producto = cp.id_producto " +
                                "WHERE p.estado != 'Cancelado' " +
                                "GROUP BY cp.nombre, cp.precio ORDER BY total_vendido DESC LIMIT 3")) {
            resetTabla("🏆 Posición", "Producto", "Unidades Vendidas", "Precio Unit.", "Ingresos Totales");
            int pos = 1;
            String[] medallas = { "🥇", "🥈", "🥉" };
            while (rs.next()) {
                modelo.addRow(new Object[] {
                        medallas[pos - 1] + " #" + pos, rs.getString("producto"),
                        rs.getInt("total_vendido"),
                        String.format("$%.0f", rs.getDouble("precio")),
                        String.format("$%.0f", rs.getDouble("ingresos"))
                });
                pos++;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void resetTabla(String... cols) {
        modelo.setColumnCount(0);
        modelo.setRowCount(0);
        for (String c : cols)
            modelo.addColumn(c);
    }

    private JButton crearBotonConsulta(String texto, Runnable action) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(ThemeManager.textPrimary());
        btn.setBackground(ThemeManager.bgCard());
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());
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
        cargarDatosIniciales();
    }
}
