package com.css.restaurante.ui;

import com.css.restaurante.dao.ConexionDB;
import com.css.restaurante.dao.MeseroDAO;
import com.css.restaurante.modelo.Mesero;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.util.List;

/**
 * Panel de desempeño por mesero.
 * Muestra estadísticas de ventas por mesero: total vendido,
 * cantidad de pedidos servidos, y top 3 productos del mesero.
 * Solo visible para usuarios con cargo de gerente.
 */
public class PanelDesempeno extends JPanel implements MenuPuntoVenta.Refrescable {

    private final MeseroDAO meseroDAO = new MeseroDAO();
    private JComboBox<String> cbMesero;
    private JLabel lblTotalVendido, lblPedidosServidos, lblTopProducto;
    private JTable tabla;
    private DefaultTableModel modelo;

    public PanelDesempeno() {
        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeManager.bgContent());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Barra superior: selector de mesero
        JPanel barSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        barSuperior.setBackground(ThemeManager.bgContent());

        JLabel lblTitulo = new JLabel("📊 Desempeño por Mesero");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitulo.setForeground(ThemeManager.textPrimary());
        barSuperior.add(lblTitulo);
        barSuperior.add(Box.createHorizontalStrut(20));

        JLabel lblMeseroLabel = new JLabel("Mesero:");
        lblMeseroLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblMeseroLabel.setForeground(ThemeManager.textSecondary());
        barSuperior.add(lblMeseroLabel);

        cbMesero = new JComboBox<>();
        cbMesero.setPreferredSize(new Dimension(200, 34));
        barSuperior.add(cbMesero);

        JButton btnConsultar = crearBotonConsulta("🔍 Consultar");
        btnConsultar.addActionListener(e -> cargarDesempeno());
        barSuperior.add(btnConsultar);

        JButton btnTop3 = crearBotonConsulta("🏆 Top 3 Productos");
        btnTop3.addActionListener(e -> cargarTop3Mesero());
        barSuperior.add(btnTop3);

        JButton btnRanking = crearBotonConsulta("📈 Ranking General");
        btnRanking.addActionListener(e -> cargarRanking());
        barSuperior.add(btnRanking);

        add(barSuperior, BorderLayout.NORTH);

        // Panel central
        JPanel centro = new JPanel(new BorderLayout(0, 15));
        centro.setBackground(ThemeManager.bgContent());

        // Tarjetas de estadísticas
        JPanel tarjetas = new JPanel(new GridLayout(1, 3, 15, 0));
        tarjetas.setBackground(ThemeManager.bgContent());

        lblTotalVendido = new JLabel("$0.00");
        tarjetas.add(crearTarjeta("💰 Total Vendido", lblTotalVendido, ThemeManager.success()));

        lblPedidosServidos = new JLabel("0");
        tarjetas.add(crearTarjeta("📋 Pedidos Servidos", lblPedidosServidos, ThemeManager.info()));

        lblTopProducto = new JLabel("—");
        tarjetas.add(crearTarjeta("🏆 Producto Top", lblTopProducto, ThemeManager.accent()));

        centro.add(tarjetas, BorderLayout.NORTH);

        // Tabla de detalle
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
        centro.add(scroll, BorderLayout.CENTER);

        add(centro, BorderLayout.CENTER);

        cargarMeseros();
    }

    private void cargarMeseros() {
        cbMesero.removeAllItems();
        try {
            List<Mesero> meseros = meseroDAO.listarActivos();
            for (Mesero m : meseros) {
                cbMesero.addItem(m.getIdMesero() + " - " + m.getNombreCompleto());
            }
        } catch (Exception ex) {
            System.err.println("Error cargando meseros: " + ex.getMessage());
        }
    }

    private int getIdMeseroSeleccionado() {
        String sel = (String) cbMesero.getSelectedItem();
        if (sel == null || sel.isEmpty())
            return -1;
        try {
            return Integer.parseInt(sel.split(" - ")[0].trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Carga las estadísticas del mesero seleccionado.
     */
    private void cargarDesempeno() {
        int idMesero = getIdMeseroSeleccionado();
        if (idMesero < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un mesero.");
            return;
        }

        try (Connection cn = ConexionDB.getConnection()) {
            // Total vendido por el mesero (pedidos no cancelados de cuentas asignadas)
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT COALESCE(SUM(cp.precio * p.cantidad), 0) AS total " +
                            "FROM pedido p " +
                            "JOIN catalogo_producto cp ON p.id_producto = cp.id_producto " +
                            "JOIN cuenta c ON p.id_cuenta = c.id_cuenta " +
                            "WHERE c.id_mesero = ? AND p.estado != 'Cancelado'")) {
                ps.setInt(1, idMesero);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        lblTotalVendido.setText("$" + String.format("%.0f", rs.getDouble("total")));
                }
            }

            // Pedidos servidos
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT COUNT(*) AS total FROM pedido p " +
                            "JOIN cuenta c ON p.id_cuenta = c.id_cuenta " +
                            "WHERE c.id_mesero = ? AND p.estado = 'Servido'")) {
                ps.setInt(1, idMesero);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next())
                        lblPedidosServidos.setText(String.valueOf(rs.getInt("total")));
                }
            }

            // Producto top del mesero
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT cp.nombre FROM pedido p " +
                            "JOIN catalogo_producto cp ON p.id_producto = cp.id_producto " +
                            "JOIN cuenta c ON p.id_cuenta = c.id_cuenta " +
                            "WHERE c.id_mesero = ? AND p.estado != 'Cancelado' " +
                            "GROUP BY cp.nombre ORDER BY SUM(p.cantidad) DESC LIMIT 1")) {
                ps.setInt(1, idMesero);
                try (ResultSet rs = ps.executeQuery()) {
                    lblTopProducto.setText(rs.next() ? rs.getString("nombre") : "—");
                }
            }

            // Detalle de ventas del mesero
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT cp.nombre AS producto, SUM(p.cantidad) AS cantidad, " +
                            "cp.precio, SUM(cp.precio * p.cantidad) AS subtotal, " +
                            "cp.categoria " +
                            "FROM pedido p " +
                            "JOIN catalogo_producto cp ON p.id_producto = cp.id_producto " +
                            "JOIN cuenta c ON p.id_cuenta = c.id_cuenta " +
                            "WHERE c.id_mesero = ? AND p.estado != 'Cancelado' " +
                            "GROUP BY cp.nombre, cp.precio, cp.categoria " +
                            "ORDER BY subtotal DESC")) {
                ps.setInt(1, idMesero);
                try (ResultSet rs = ps.executeQuery()) {
                    resetTabla("Producto", "Categoría", "Cant. Vendida", "Precio Unit.", "Ingresos");
                    while (rs.next()) {
                        modelo.addRow(new Object[] {
                                rs.getString("producto"),
                                rs.getString("categoria"),
                                rs.getInt("cantidad"),
                                String.format("$%.0f", rs.getDouble("precio")),
                                String.format("$%.0f", rs.getDouble("subtotal"))
                        });
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    /**
     * Top 3 productos vendidos por el mesero seleccionado.
     */
    private void cargarTop3Mesero() {
        int idMesero = getIdMeseroSeleccionado();
        if (idMesero < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un mesero.");
            return;
        }

        try (Connection cn = ConexionDB.getConnection();
                PreparedStatement ps = cn.prepareStatement(
                        "SELECT cp.nombre AS producto, SUM(p.cantidad) AS total_vendido, " +
                                "cp.precio, SUM(cp.precio * p.cantidad) AS ingresos " +
                                "FROM pedido p " +
                                "JOIN catalogo_producto cp ON p.id_producto = cp.id_producto " +
                                "JOIN cuenta c ON p.id_cuenta = c.id_cuenta " +
                                "WHERE c.id_mesero = ? AND p.estado != 'Cancelado' " +
                                "GROUP BY cp.nombre, cp.precio " +
                                "ORDER BY total_vendido DESC LIMIT 3")) {
            ps.setInt(1, idMesero);
            try (ResultSet rs = ps.executeQuery()) {
                resetTabla("🏆 Posición", "Producto", "Unidades Vendidas", "Precio Unit.", "Ingresos");
                int pos = 1;
                String[] medallas = { "🥇", "🥈", "🥉" };
                while (rs.next()) {
                    modelo.addRow(new Object[] {
                            medallas[pos - 1] + " #" + pos,
                            rs.getString("producto"),
                            rs.getInt("total_vendido"),
                            String.format("$%.0f", rs.getDouble("precio")),
                            String.format("$%.0f", rs.getDouble("ingresos"))
                    });
                    pos++;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    /**
     * Ranking general de meseros por total vendido.
     */
    private void cargarRanking() {
        try (Connection cn = ConexionDB.getConnection();
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT CONCAT(m.nombre, ' ', m.apellido) AS mesero, " +
                                "COUNT(DISTINCT p.id_pedido) AS pedidos, " +
                                "COALESCE(SUM(cp.precio * p.cantidad), 0) AS total_vendido " +
                                "FROM mesero m " +
                                "LEFT JOIN cuenta c ON m.id_mesero = c.id_mesero " +
                                "LEFT JOIN pedido p ON c.id_cuenta = p.id_cuenta AND p.estado != 'Cancelado' " +
                                "LEFT JOIN catalogo_producto cp ON p.id_producto = cp.id_producto " +
                                "WHERE m.activo = TRUE " +
                                "GROUP BY m.id_mesero, m.nombre, m.apellido " +
                                "ORDER BY total_vendido DESC")) {
            resetTabla("🏅 Posición", "Mesero", "Pedidos Atendidos", "Total Vendido");
            int pos = 1;
            while (rs.next()) {
                String medalla = pos <= 3 ? new String[] { "🥇", "🥈", "🥉" }[pos - 1] + " " : "";
                modelo.addRow(new Object[] {
                        medalla + "#" + pos,
                        rs.getString("mesero"),
                        rs.getInt("pedidos"),
                        String.format("$%.0f", rs.getDouble("total_vendido"))
                });
                pos++;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // ===== Componentes reutilizables =====

    private JPanel crearTarjeta(String titulo, JLabel lblValor, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.bgCard());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(accentColor);
                g2.fill(new RoundRectangle2D.Double(0, 0, 4, getHeight(), 14, 14));
                g2.fillRect(2, 0, 2, getHeight());
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 20));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTit.setForeground(ThemeManager.textSecondary());
        lblTit.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblTit);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        lblValor.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblValor.setForeground(accentColor);
        lblValor.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblValor);
        return card;
    }

    private void resetTabla(String... cols) {
        modelo.setColumnCount(0);
        modelo.setRowCount(0);
        for (String c : cols)
            modelo.addColumn(c);
    }

    private JButton crearBotonConsulta(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(ThemeManager.textPrimary());
        btn.setBackground(ThemeManager.bgCard());
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        cargarMeseros();
        lblTotalVendido.setText("$0.00");
        lblPedidosServidos.setText("0");
        lblTopProducto.setText("—");
        modelo.setRowCount(0);
        modelo.setColumnCount(0);
    }
}
