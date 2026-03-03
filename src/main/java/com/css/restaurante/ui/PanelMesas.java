package com.css.restaurante.ui;

import com.css.restaurante.dao.*;
import com.css.restaurante.modelo.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Panel interactivo de mesas del restaurante.
 * Muestra las mesas como tarjetas con colores por estado,
 * diálogos para gestión de cuentas y pedidos.
 */
public class PanelMesas extends JPanel implements MenuPuntoVenta.Refrescable {

    private final MesaDAO mesaDAO = new MesaDAO();
    private final MeseroDAO meseroDAO = new MeseroDAO();
    private final CuentaDAO cuentaDAO = new CuentaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    private JPanel gridPanel;

    public PanelMesas() {
        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeManager.bgContent());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Leyenda
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        leyenda.setBackground(ThemeManager.bgContent());
        leyenda.add(crearLeyendaItem("Libre", ThemeManager.success()));
        leyenda.add(crearLeyendaItem("Ocupada", ThemeManager.danger()));
        leyenda.add(crearLeyendaItem("Reservada", ThemeManager.warning()));
        leyenda.add(crearLeyendaItem("Limpieza", ThemeManager.info()));
        add(leyenda, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(3, 4, 15, 15));
        gridPanel.setBackground(ThemeManager.bgContent());

        JScrollPane scroll = new JScrollPane(gridPanel);
        scroll.setBackground(ThemeManager.bgContent());
        scroll.getViewport().setBackground(ThemeManager.bgContent());
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        cargarMesas();
    }

    private JPanel crearLeyendaItem(String texto, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(ThemeManager.bgContent());
        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(1, 1, 10, 10);
                g2.dispose();
            }
        };
        dot.setOpaque(false);
        dot.setPreferredSize(new Dimension(12, 12));
        item.add(dot);
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(ThemeManager.textSecondary());
        item.add(lbl);
        return item;
    }

    private void cargarMesas() {
        gridPanel.removeAll();
        try {
            List<Mesa> mesas = mesaDAO.listar();
            for (Mesa mesa : mesas) {
                gridPanel.add(crearTarjetaMesa(mesa));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar mesas: " + ex.getMessage());
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel crearTarjetaMesa(Mesa mesa) {
        Color colorEstado = getColorEstado(mesa.getEstado());

        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                // Barra de acento superior
                g2.setColor(colorEstado);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), 4, 16, 16));
                g2.fillRect(0, 2, getWidth(), 2);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(ThemeManager.bgCard());
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 15, 18));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setOpaque(false);

        // Número de mesa
        JLabel lblNum = new JLabel("Mesa " + mesa.getIdMesa());
        lblNum.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblNum.setForeground(ThemeManager.textPrimary());
        lblNum.setAlignmentX(LEFT_ALIGNMENT);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(lblNum);
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        JLabel lblCap = new JLabel("👥 Capacidad: " + mesa.getCapacidad());
        lblCap.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblCap.setForeground(ThemeManager.textSecondary());
        lblCap.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblCap);
        card.add(Box.createRigidArea(new Dimension(0, 4)));

        JLabel lblEstado = new JLabel("● " + mesa.getEstado());
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblEstado.setForeground(colorEstado);
        lblEstado.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblEstado);
        card.add(Box.createRigidArea(new Dimension(0, 4)));

        try {
            String mesero = mesaDAO.obtenerNombreMesero(mesa.getIdMesa());
            if (mesero != null && !mesero.trim().isEmpty()) {
                JLabel lblMesero = new JLabel("👤 " + mesero);
                lblMesero.setFont(new Font("SansSerif", Font.ITALIC, 11));
                lblMesero.setForeground(ThemeManager.textMuted());
                lblMesero.setAlignmentX(LEFT_ALIGNMENT);
                card.add(lblMesero);
            }
        } catch (Exception ignored) {
        }

        card.addMouseListener(new MouseAdapter() {
            Color originalBg = ThemeManager.bgCard();

            public void mouseEntered(MouseEvent e) {
                card.setBackground(ThemeManager.hoverBg());
                card.repaint();
            }

            public void mouseExited(MouseEvent e) {
                card.setBackground(ThemeManager.bgCard());
                card.repaint();
            }

            public void mouseClicked(MouseEvent e) {
                abrirDialogoMesa(mesa);
            }
        });

        return card;
    }

    private void abrirDialogoMesa(Mesa mesa) {
        JDialog dialog = new JDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Mesa " + mesa.getIdMesa(), true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.bgCard());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel titulo = new JLabel("Mesa " + mesa.getIdMesa() + " — " + mesa.getEstado());
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(ThemeManager.textPrimary());
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        panel.add(crearLabel("Cambiar estado:"));
        JComboBox<String> cbEstado = new JComboBox<>(new String[] { "Libre", "Ocupada", "Reservada", "Limpieza" });
        cbEstado.setSelectedItem(mesa.getEstado());
        cbEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cbEstado.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(cbEstado);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(crearLabel("Asignar mesero:"));
        JComboBox<String> cbMesero = new JComboBox<>();
        cbMesero.addItem("— Sin mesero —");
        try {
            for (Mesero m : meseroDAO.listarActivos()) {
                cbMesero.addItem(m.getIdMesero() + " - " + m.getNombreCompleto());
            }
        } catch (Exception ignored) {
        }
        cbMesero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cbMesero.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(cbMesero);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnAbrir = crearBotonAccion("📝 Abrir Cuenta", ThemeManager.success());
        btnAbrir.addActionListener(e -> {
            try {
                if (cuentaDAO.tieneCuentaAbierta(mesa.getIdMesa())) {
                    JOptionPane.showMessageDialog(dialog, "Esta mesa ya tiene una cuenta abierta.");
                    return;
                }
                Cuenta c = new Cuenta(mesa.getIdMesa(), 1);
                cuentaDAO.insertar(c);
                mesaDAO.actualizarEstado(mesa.getIdMesa(), "Ocupada");
                cbEstado.setSelectedItem("Ocupada");
                JOptionPane.showMessageDialog(dialog, "Cuenta #" + c.getIdCuenta() + " abierta.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        panel.add(btnAbrir);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnPedido = crearBotonAccion("🛒 Agregar Pedido", ThemeManager.accent());
        btnPedido.addActionListener(e -> {
            try {
                int idCuenta = cuentaDAO.obtenerIdCuentaAbierta(mesa.getIdMesa());
                if (idCuenta < 0) {
                    JOptionPane.showMessageDialog(dialog, "Primero debe abrir una cuenta para esta mesa.");
                    return;
                }
                abrirDialogoPedido(dialog, idCuenta);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        panel.add(btnPedido);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnGuardar = crearBotonAccion("💾 Guardar Cambios", ThemeManager.info());
        btnGuardar.addActionListener(e -> {
            try {
                mesaDAO.actualizarEstado(mesa.getIdMesa(), (String) cbEstado.getSelectedItem());
                String meseroSel = (String) cbMesero.getSelectedItem();
                if (meseroSel != null && meseroSel.startsWith("—")) {
                    mesaDAO.desasignarMesero(mesa.getIdMesa());
                } else if (meseroSel != null) {
                    int idMesero = Integer.parseInt(meseroSel.split(" - ")[0]);
                    mesaDAO.asignarMesero(mesa.getIdMesa(), idMesero);
                }
                JOptionPane.showMessageDialog(dialog, "Cambios guardados.");
                dialog.dispose();
                cargarMesas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });
        panel.add(btnGuardar);

        dialog.setContentPane(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private void abrirDialogoPedido(JDialog parent, int idCuenta) {
        JDialog d = new JDialog(parent, "Agregar Pedido", true);
        d.setSize(420, 320);
        d.setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.bgCard());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        panel.add(crearLabel("Producto:"));
        JComboBox<String> cbProducto = new JComboBox<>();
        try {
            for (Producto p : productoDAO.listarDisponibles()) {
                cbProducto.addItem(p.getId() + " - " + p.getNombre()
                        + " ($" + String.format("%.2f", p.getPrecio()) + ") [Stock: " + p.getStock() + "]");
            }
        } catch (Exception ignored) {
        }
        cbProducto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cbProducto.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(cbProducto);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(crearLabel("Cantidad:"));
        JSpinner spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spCantidad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        spCantidad.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(spCantidad);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnEnviar = crearBotonAccion("Enviar Pedido", ThemeManager.success());
        btnEnviar.addActionListener(e -> {
            try {
                String sel = (String) cbProducto.getSelectedItem();
                if (sel == null)
                    return;
                int idProducto = Integer.parseInt(sel.split(" - ")[0]);
                int cantidad = (int) spCantidad.getValue();
                // Validación de cantidad
                if (cantidad <= 0 || cantidad > 100) {
                    JOptionPane.showMessageDialog(d, "Cantidad inválida (1-100).");
                    return;
                }
                pedidoDAO.agregar(idCuenta, idProducto, cantidad);
                JOptionPane.showMessageDialog(d, "Pedido enviado a cocina.");
                d.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, "Error: " + ex.getMessage());
            }
        });
        panel.add(btnEnviar);

        d.setContentPane(panel);
        d.setVisible(true);
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.textSecondary());
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        return lbl;
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
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return btn;
    }

    private Color getColorEstado(String estado) {
        switch (estado) {
            case "Ocupada":
                return ThemeManager.danger();
            case "Reservada":
                return ThemeManager.warning();
            case "Limpieza":
                return ThemeManager.info();
            default:
                return ThemeManager.success();
        }
    }

    @Override
    public void refrescar() {
        cargarMesas();
    }
}
