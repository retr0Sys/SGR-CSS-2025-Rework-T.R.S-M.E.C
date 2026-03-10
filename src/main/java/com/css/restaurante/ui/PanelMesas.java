package com.css.restaurante.ui;

import com.css.restaurante.dao.*;
import com.css.restaurante.modelo.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
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
            System.err.println("[SGR] Error al cargar mesas: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Error al cargar mesas. Contacte al administrador.");
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

        JLabel lblCap = new JLabel("\uD83D\uDC65 Capacidad: " + mesa.getCapacidad());
        lblCap.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblCap.setForeground(ThemeManager.textSecondary());
        lblCap.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblCap);
        card.add(Box.createRigidArea(new Dimension(0, 4)));

        JLabel lblEstado = new JLabel("\u25CF " + mesa.getEstado());
        lblEstado.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblEstado.setForeground(colorEstado);
        lblEstado.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblEstado);
        card.add(Box.createRigidArea(new Dimension(0, 4)));

        try {
            String mesero = mesaDAO.obtenerNombreMesero(mesa.getIdMesa());
            if (mesero != null && !mesero.trim().isEmpty()) {
                JLabel lblMesero = new JLabel("\uD83D\uDC64 " + mesero);
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
        dialog.setSize(520, 650);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.bgCard());
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel titulo = new JLabel("Mesa " + mesa.getIdMesa() + " \u2014 " + mesa.getEstado());
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

        boolean tieneCuenta = false;
        try {
            tieneCuenta = cuentaDAO.tieneCuentaAbierta(mesa.getIdMesa());
        } catch (Exception ignored) {
        }

        // Bloquear cambio de estado si la mesa ya está ocupada y tiene cuenta activa
        if ("Ocupada".equals(mesa.getEstado()) && tieneCuenta) {
            cbEstado.setEnabled(false);
            cbEstado.setToolTipText("Debe cerrar la mesa desde Facturación para cambiar su estado.");
        }

        panel.add(cbEstado);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(crearLabel("Asignar mesero:"));
        JComboBox<String> cbMesero = new JComboBox<>();
        cbMesero.addItem("\u2014 Sin mesero \u2014");
        try {
            for (Mesero m : meseroDAO.listarActivos()) {
                String item = m.getIdMesero() + " - " + m.getNombreCompleto();
                cbMesero.addItem(item);
                if (mesa.getIdMesero() != null && mesa.getIdMesero() == m.getIdMesero()) {
                    cbMesero.setSelectedItem(item);
                }
            }
        } catch (Exception ignored) {
        }
        cbMesero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cbMesero.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(cbMesero);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnAbrir = crearBotonAccion("\uD83D\uDCDD Abrir Cuenta", ThemeManager.success());
        btnAbrir.addActionListener(e -> {
            try {
                if (cuentaDAO.tieneCuentaAbierta(mesa.getIdMesa())) {
                    JOptionPane.showMessageDialog(dialog, "Esta mesa ya tiene una cuenta abierta.");
                    return;
                }
                // Obtener el id del mesero seleccionado para persistirlo en la cuenta
                String meseroSel = (String) cbMesero.getSelectedItem();
                Integer idMeseroSel = null;
                if (meseroSel != null && !meseroSel.startsWith("\u2014")) {
                    idMeseroSel = Integer.parseInt(meseroSel.split(" - ")[0]);
                }
                Cuenta c = new Cuenta(mesa.getIdMesa(), 1, idMeseroSel);
                cuentaDAO.insertar(c);
                mesaDAO.actualizarEstado(mesa.getIdMesa(), "Ocupada");
                cbEstado.setSelectedItem("Ocupada");
                cbEstado.setEnabled(false); // Bloquear una vez abierta la cuenta
                JOptionPane.showMessageDialog(dialog, "Cuenta #" + c.getIdCuenta() + " abierta.");
            } catch (Exception ex) {
                System.err.println("[SGR] Error al abrir cuenta: " + ex.getMessage());
                JOptionPane.showMessageDialog(dialog, "Error al abrir cuenta. Contacte al administrador.");
            }
        });
        panel.add(btnAbrir);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // ── Sección de Pedidos Actuales ──
        panel.add(new JSeparator());
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(crearLabel("Pedidos Actuales:"));

        DefaultListModel<String> pedidosDisplayModel = new DefaultListModel<>();
        JList<String> listaPedidos = new JList<>(pedidosDisplayModel);
        listaPedidos.setBackground(ThemeManager.bgContent());
        listaPedidos.setForeground(ThemeManager.textPrimary());
        listaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaPedidos.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JScrollPane scrollPedidos = new JScrollPane(listaPedidos);
        scrollPedidos.setPreferredSize(new Dimension(0, 100));
        scrollPedidos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        scrollPedidos.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(scrollPedidos);

        // Almacenar los IDs de pedidos para poder eliminar
        List<Pedido> pedidosCargados = new ArrayList<>();
        Runnable recargarPedidos = () -> {
            pedidosDisplayModel.clear();
            pedidosCargados.clear();
            try {
                int idCuenta = cuentaDAO.obtenerIdCuentaAbierta(mesa.getIdMesa());
                if (idCuenta > 0) {
                    for (Pedido p : pedidoDAO.listarPorCuenta(idCuenta)) {
                        pedidosCargados.add(p);
                        String display = p.getNombreProducto() + " x" + p.getCantidad()
                                + " ($" + String.format("%.0f", p.getSubtotal()) + ") ["
                                + p.getEstado().getValor() + "]";
                        pedidosDisplayModel.addElement(display);
                    }
                }
            } catch (Exception ignored) {
            }
        };
        recargarPedidos.run();

        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        // FIX: Centrar botones debajo de Pedidos Actuales
        JPanel panelBotonesPedido = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        panelBotonesPedido.setBackground(ThemeManager.bgCard());
        panelBotonesPedido.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnEliminar = crearBotonAccion("\uD83D\uDDD1 Eliminar Pendiente", ThemeManager.danger());
        btnEliminar.setMaximumSize(new Dimension(180, 36));
        btnEliminar.setPreferredSize(new Dimension(180, 36));
        btnEliminar.addActionListener(e -> {
            // FIX: Solo permitir si mesa está Ocupada y con mesero asignado
            String estadoActual = (String) cbEstado.getSelectedItem();
            String meseroActual = (String) cbMesero.getSelectedItem();
            if (!"Ocupada".equals(estadoActual) || meseroActual == null || meseroActual.startsWith("\u2014")) {
                JOptionPane.showMessageDialog(dialog,
                        "La mesa debe estar Ocupada y con un mesero asignado para gestionar pedidos.");
                return;
            }

            int idx = listaPedidos.getSelectedIndex();
            if (idx < 0) {
                JOptionPane.showMessageDialog(dialog, "Seleccione un pedido para eliminar.");
                return;
            }
            Pedido sel = pedidosCargados.get(idx);
            String estadoPedido = sel.getEstado().getValor();

            // FIX: Permitir eliminar Pendiente y Cancelado (sincronización con cocina)
            if (!"Pendiente".equalsIgnoreCase(estadoPedido) && !"Cancelado".equalsIgnoreCase(estadoPedido)) {
                JOptionPane.showMessageDialog(dialog,
                        "Solo se pueden eliminar pedidos en estado Pendiente o Cancelado.");
                return;
            }

            int resp = JOptionPane.showConfirmDialog(dialog,
                    "\u00BFEliminar " + sel.getNombreProducto() + " x" + sel.getCantidad() + " del pedido?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (resp == JOptionPane.YES_OPTION) {
                try {
                    if ("Cancelado".equalsIgnoreCase(estadoPedido)) {
                        // Pedido cancelado: eliminar sin devolver stock (ya devuelto al cancelar)
                        pedidoDAO.eliminarCancelado(sel.getIdPedido());
                    } else {
                        // Pedido pendiente: eliminar y devolver stock
                        pedidoDAO.eliminar(sel.getIdPedido());
                    }
                    recargarPedidos.run();
                    JOptionPane.showMessageDialog(dialog, "Pedido eliminado.");
                } catch (Exception ex) {
                    System.err.println("[SGR] Error al eliminar pedido: " + ex.getMessage());
                    JOptionPane.showMessageDialog(dialog, "Error al eliminar pedido. Contacte al administrador.");
                }
            }
        });
        panelBotonesPedido.add(btnEliminar);

        JButton btnAgregar = crearBotonAccion("\uD83D\uDED2 Agregar Pedido", ThemeManager.accent());
        btnAgregar.setMaximumSize(new Dimension(170, 36));
        btnAgregar.setPreferredSize(new Dimension(170, 36));
        btnAgregar.addActionListener(e -> {
            // FIX: Solo permitir si mesa está Ocupada y con mesero asignado
            String estadoActual = (String) cbEstado.getSelectedItem();
            String meseroActual = (String) cbMesero.getSelectedItem();
            if (!"Ocupada".equals(estadoActual) || meseroActual == null || meseroActual.startsWith("\u2014")) {
                JOptionPane.showMessageDialog(dialog,
                        "La mesa debe estar Ocupada y con un mesero asignado para agregar pedidos.");
                return;
            }

            try {
                int idCuenta = cuentaDAO.obtenerIdCuentaAbierta(mesa.getIdMesa());
                if (idCuenta < 0) {
                    JOptionPane.showMessageDialog(dialog, "Primero debe abrir una cuenta para esta mesa.");
                    return;
                }
                abrirDialogoPedido(dialog, idCuenta);
                recargarPedidos.run();
            } catch (Exception ex) {
                System.err.println("[SGR] Error al agregar pedido: " + ex.getMessage());
                JOptionPane.showMessageDialog(dialog, "Error al agregar pedido. Contacte al administrador.");
            }
        });
        panelBotonesPedido.add(btnAgregar);
        panel.add(panelBotonesPedido);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnGuardar = crearBotonAccion("\uD83D\uDCBE Guardar Cambios", ThemeManager.info());
        btnGuardar.addActionListener(e -> {
            try {
                String nuevoEstado = (String) cbEstado.getSelectedItem();
                boolean tieneCuentaActiva = cuentaDAO.tieneCuentaAbierta(mesa.getIdMesa());

                // FIX: No permitir cambiar de Ocupada si hay cuenta abierta
                if ("Ocupada".equals(mesa.getEstado()) && tieneCuentaActiva && !"Ocupada".equals(nuevoEstado)) {
                    JOptionPane.showMessageDialog(dialog,
                            "No se puede cambiar el estado de la mesa mientras tiene una cuenta abierta.\n"
                                    + "Cierre la cuenta primero desde Facturación.");
                    return;
                }

                // FIX: No permitir cambiar manualmente a Ocupada sin abrir cuenta
                if ("Ocupada".equals(nuevoEstado) && !tieneCuentaActiva && !"Ocupada".equals(mesa.getEstado())) {
                    JOptionPane.showMessageDialog(dialog,
                            "Para ocupar una mesa, debe utilizar el botón 'Abrir Cuenta'.");
                    cbEstado.setSelectedItem(mesa.getEstado());
                    return;
                }

                // FIX: Al cambiar a Libre/Reservada/Limpieza, limpiar pedidos de la cuenta
                if (!"Ocupada".equals(nuevoEstado) && tieneCuentaActiva) {
                    int idCuenta = cuentaDAO.obtenerIdCuentaAbierta(mesa.getIdMesa());
                    if (idCuenta > 0) {
                        pedidoDAO.eliminarPorCuenta(idCuenta);
                        cuentaDAO.cerrarCuenta(mesa.getIdMesa());
                    }
                }

                mesaDAO.actualizarEstado(mesa.getIdMesa(), nuevoEstado);
                String meseroSel = (String) cbMesero.getSelectedItem();
                if (meseroSel != null && meseroSel.startsWith("\u2014")) {
                    mesaDAO.desasignarMesero(mesa.getIdMesa());
                    if (tieneCuentaActiva) {
                        cuentaDAO.desasignarMesero(mesa.getIdMesa());
                    }
                } else if (meseroSel != null) {
                int idMesero;
                try {
                    idMesero = Integer.parseInt(meseroSel.split(" - ")[0]);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(dialog, "Error al procesar mesero seleccionado.");
                    return;
                }
                    mesaDAO.asignarMesero(mesa.getIdMesa(), idMesero);
                    if (tieneCuentaActiva) {
                        cuentaDAO.actualizarMesero(mesa.getIdMesa(), idMesero);
                    }
                }
                JOptionPane.showMessageDialog(dialog, "Cambios guardados.");
                dialog.dispose();
                cargarMesas();
            } catch (Exception ex) {
                System.err.println("[SGR] Error al guardar cambios: " + ex.getMessage());
                JOptionPane.showMessageDialog(dialog, "Error al guardar cambios. Contacte al administrador.");
            }
        });
        panel.add(btnGuardar);

        dialog.setContentPane(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    /**
     * Diálogo para agregar múltiples items a un pedido.
     * Permite seleccionar producto y cantidad, acumularlos en una lista
     * temporal, y enviarlos todos a cocina al confirmar.
     */
    private void abrirDialogoPedido(JDialog parent, int idCuenta) {
        JDialog d = new JDialog(parent, "Agregar Pedido", true);
        d.setSize(500, 450);
        d.setLocationRelativeTo(parent);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.bgCard());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Panel de selección: producto + cantidad en una fila
        JPanel panelInput = new JPanel(new BorderLayout(10, 0));
        panelInput.setBackground(ThemeManager.bgCard());
        panelInput.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panelInput.setAlignmentX(LEFT_ALIGNMENT);

        JComboBox<String> cbProducto = new JComboBox<>();
        try {
            for (Producto p : productoDAO.listarDisponibles()) {
                cbProducto.addItem(p.getId() + " - " + p.getNombre()
                        + " ($" + String.format("%.0f", p.getPrecio()) + ") [Stock: " + p.getStock() + "]");
            }
        } catch (Exception ignored) {
        }
        panelInput.add(cbProducto, BorderLayout.CENTER);

        JSpinner spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spCantidad.setPreferredSize(new Dimension(60, 35));
        panelInput.add(spCantidad, BorderLayout.EAST);

        panel.add(crearLabel("Producto y Cantidad:"));
        panel.add(panelInput);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Lista temporal de items a enviar
        panel.add(crearLabel("Items a enviar:"));
        DefaultListModel<String> itemsModel = new DefaultListModel<>();
        JList<String> listaItems = new JList<>(itemsModel);
        listaItems.setBackground(ThemeManager.bgContent());
        listaItems.setForeground(ThemeManager.textPrimary());
        listaItems.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JScrollPane scrollItems = new JScrollPane(listaItems);
        scrollItems.setPreferredSize(new Dimension(0, 120));
        scrollItems.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        scrollItems.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(scrollItems);

        panel.add(Box.createRigidArea(new Dimension(0, 8)));

        // Estructura interna para items pendientes
        List<int[]> itemsPendientes = new ArrayList<>(); // [idProducto, cantidad]

        JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelControles.setBackground(ThemeManager.bgCard());
        panelControles.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnAdd = crearBotonAccion("\u2795 A\u00F1adir", ThemeManager.info());
        btnAdd.setMaximumSize(new Dimension(120, 36));
        btnAdd.setPreferredSize(new Dimension(120, 36));
        btnAdd.addActionListener(e -> {
            String sel = (String) cbProducto.getSelectedItem();
            if (sel == null)
                return;

            int idProducto = Integer.parseInt(sel.split(" - ")[0].trim());
            String nombreCorto = sel.split("\\[")[0].trim(); // hasta [Stock: ...]
            int cantidad = (int) spCantidad.getValue();

            itemsPendientes.add(new int[] { idProducto, cantidad });
            itemsModel.addElement(cantidad + "x " + nombreCorto);
            spCantidad.setValue(1);
        });
        panelControles.add(btnAdd);

        JButton btnRemove = crearBotonAccion("Quitar", ThemeManager.danger());
        btnRemove.setMaximumSize(new Dimension(100, 36));
        btnRemove.setPreferredSize(new Dimension(100, 36));
        btnRemove.addActionListener(e -> {
            int idx = listaItems.getSelectedIndex();
            if (idx >= 0) {
                itemsPendientes.remove(idx);
                itemsModel.remove(idx);
            }
        });
        panelControles.add(btnRemove);
        panel.add(panelControles);

        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnEnviar = crearBotonAccion("\uD83D\uDE80 Enviar Todo a Cocina", ThemeManager.success());
        btnEnviar.addActionListener(e -> {
            if (itemsPendientes.isEmpty()) {
                JOptionPane.showMessageDialog(d, "No hay productos en la lista.");
                return;
            }
            try {
                for (int[] item : itemsPendientes) {
                    pedidoDAO.agregar(idCuenta, item[0], item[1]);
                }
                JOptionPane.showMessageDialog(d,
                        itemsPendientes.size() + " producto(s) enviados a cocina.");
                d.dispose();
            } catch (Exception ex) {
                System.err.println("[SGR] Error en pedido: " + ex.getMessage());
                JOptionPane.showMessageDialog(d, "Error al procesar pedido. Contacte al administrador.");
            }
        });
        panel.add(btnEnviar);

        d.setContentPane(new JScrollPane(panel));
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
