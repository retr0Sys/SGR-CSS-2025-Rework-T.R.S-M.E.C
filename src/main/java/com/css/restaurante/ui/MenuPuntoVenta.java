package com.css.restaurante.ui;

import com.css.restaurante.dao.ConexionDB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Ventana principal tipo Dashboard con navegacion lateral.
 * Usa CardLayout para cambiar entre modulos sin abrir ventanas nuevas.
 * Soporte completo para tema claro/oscuro con toggle en la barra lateral.
 * Iconos del sidebar generados programaticamente via IconFactory.
 */
public class MenuPuntoVenta extends JFrame {

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JLabel lblHeaderTitle;
    private JButton activeButton;

    // Modulos del sistema
    private static final String PANEL_CARTA = "carta";
    private static final String PANEL_MESAS = "mesas";
    private static final String PANEL_COCINA = "cocina";
    private static final String PANEL_FACTURACION = "facturacion";
    private static final String PANEL_RESUMEN = "resumen";
    private static final String PANEL_DESEMPENO = "desempeno";
    private static final String PANEL_EMPLEADOS = "empleados";
    private static final String PANEL_ACERCA = "acerca";

    public MenuPuntoVenta() {
        setTitle("SGR - Punto de Venta");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 700));

        // Icono de barra de tareas — CSSLogo.jpg
        try {
            java.net.URL iconUrl = getClass().getResource("/imagenes/CSSLogo.jpg");
            if (iconUrl != null)
                setIconImage(new ImageIcon(iconUrl).getImage());
        } catch (Exception ignored) {
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int op = JOptionPane.showConfirmDialog(
                        MenuPuntoVenta.this, "Desea cerrar el sistema?",
                        "Confirmar salida", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (op == JOptionPane.YES_OPTION) {
                    ConexionDB.shutdown();
                    System.exit(0);
                }
            }
        });

        construirUI();
    }

    private void construirUI() {
        setLayout(new BorderLayout());
        add(crearSidebar(), BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(ThemeManager.bgContent());
        rightPanel.add(crearHeader(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ThemeManager.bgContent());

        contentPanel.add(new PanelCarta(), PANEL_CARTA);
        contentPanel.add(new PanelMesas(), PANEL_MESAS);
        contentPanel.add(new PanelCocina(), PANEL_COCINA);
        contentPanel.add(new PanelFacturacion(), PANEL_FACTURACION);
        contentPanel.add(new PanelResumen(), PANEL_RESUMEN);
        if (SesionManager.esGerente()) {
            contentPanel.add(new PanelDesempeno(), PANEL_DESEMPENO);
            contentPanel.add(new PanelEmpleados(), PANEL_EMPLEADOS);
        }
        contentPanel.add(crearPanelAcerca(), PANEL_ACERCA);

        rightPanel.add(contentPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);

        cambiarModulo(PANEL_MESAS, "Mesas");
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(ThemeManager.bgSidebar());
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeManager.border()));

        // ── Logo ──
        JPanel logoPanel = new JPanel();
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBackground(ThemeManager.bgSidebar());
        logoPanel.setBorder(BorderFactory.createEmptyBorder(14, 10, 10, 10));
        logoPanel.setMaximumSize(new Dimension(220, 130));

        JLabel lblLogoImg = new JLabel();
        lblLogoImg.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            java.net.URL url = getClass().getResource("/imagenes/CSSLogo.jpg");
            if (url != null) {
                Image scaled = new ImageIcon(url).getImage().getScaledInstance(72, 72, Image.SCALE_SMOOTH);
                lblLogoImg.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ignored) {
        }
        logoPanel.add(lblLogoImg);
        logoPanel.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel lblLogoText = new JLabel("Restaurante CSS");
        lblLogoText.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblLogoText.setForeground(ThemeManager.accent());
        lblLogoText.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(lblLogoText);

        JLabel lblLogoSub = new JLabel("Sistema de Gestion");
        lblLogoSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblLogoSub.setForeground(ThemeManager.textMuted());
        lblLogoSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(lblLogoSub);

        sidebar.add(logoPanel);
        sidebar.add(crearSeparador());
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        // Seccion: Operaciones
        sidebar.add(crearSeccionLabel("OPERACIONES"));
        JButton btnMesas = crearBotonSidebar("Mesas", "mesas", PANEL_MESAS, "Mesas");
        sidebar.add(btnMesas);
        sidebar.add(crearBotonSidebar("Carta", "carta", PANEL_CARTA, "Carta"));
        sidebar.add(crearBotonSidebar("Cocina", "cocina", PANEL_COCINA, "Cocina"));

        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(crearSeparador());
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        // Seccion: Finanzas
        sidebar.add(crearSeccionLabel("FINANZAS"));
        if (SesionManager.puedeFacturar()) {
            sidebar.add(crearBotonSidebar("Facturacion", "facturacion", PANEL_FACTURACION, "Facturacion"));
        }
        sidebar.add(crearBotonSidebar("Resumen", "resumen", PANEL_RESUMEN, "Resumen"));
        if (SesionManager.esGerente()) {
            sidebar.add(crearBotonSidebar("Desempeño", "desempeno", PANEL_DESEMPENO, "Desempeño de Meseros"));
            sidebar.add(crearBotonSidebar("Empleados", "empleados", PANEL_EMPLEADOS, "Gestión de Empleados"));
        }

        sidebar.add(Box.createVerticalGlue());

        // ── Toggle tema ──
        sidebar.add(crearSeparador());
        sidebar.add(Box.createRigidArea(new Dimension(0, 4)));

        String temaIconKey = ThemeManager.isDark() ? "tema_sol" : "tema_luna";
        JButton btnTema = new JButton(" " + ThemeManager.themeLabel());
        btnTema.setIcon(IconFactory.crear(temaIconKey, 18, ThemeManager.textSecondary()));
        btnTema.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnTema.setForeground(ThemeManager.textSecondary());
        btnTema.setBackground(ThemeManager.bgSidebar());
        btnTema.setBorderPainted(false);
        btnTema.setFocusPainted(false);
        btnTema.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTema.setHorizontalAlignment(SwingConstants.LEFT);
        btnTema.setMaximumSize(new Dimension(220, 38));
        btnTema.setPreferredSize(new Dimension(220, 38));
        btnTema.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 8));
        btnTema.addActionListener(e -> {
            ThemeManager.toggle();
            getContentPane().removeAll();
            construirUI();
            revalidate();
            repaint();
        });
        sidebar.add(btnTema);
        sidebar.add(Box.createRigidArea(new Dimension(0, 2)));

        sidebar.add(crearBotonSidebar("Acerca de", "acerca", PANEL_ACERCA, "Acerca de"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 2)));

        // ── Cerrar Sesión ──
        JButton btnLogout = crearBotonSidebarAction("Cerrar Sesión", "logout");
        btnLogout.addActionListener(e -> {
            int resp = JOptionPane.showConfirmDialog(this, "¿Estás seguro que deseas cerrar sesión?",
                    "Cerrar Sesión", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (resp == JOptionPane.YES_OPTION) {
                AuditLogger.cerrarSesion(SesionManager.getNombreDisplay());
                SesionManager.cerrarSesion();
                dispose();
                SwingUtilities.invokeLater(() -> new PanelLogin().setVisible(true));
            }
        });
        sidebar.add(btnLogout);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        // Boton activo por defecto = Mesas
        activeButton = btnMesas;
        btnMesas.setBackground(ThemeManager.accentSoft());
        btnMesas.setForeground(ThemeManager.accent());

        return sidebar;
    }

    private JLabel crearSeccionLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(ThemeManager.textMuted());
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(220, 22));
        return lbl;
    }

    private JButton crearBotonSidebar(String texto, String iconKey, String panelKey, String headerTitle) {
        JButton btn = new JButton(" " + texto);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(ThemeManager.textSecondary());
        btn.setBackground(ThemeManager.bgSidebar());
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setPreferredSize(new Dimension(220, 40));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 8));

        // Icono programatico
        btn.setIcon(IconFactory.crear(iconKey, 18, ThemeManager.textSecondary()));

        btn.addActionListener(e -> {
            cambiarModulo(panelKey, headerTitle);
            if (activeButton != null) {
                activeButton.setBackground(ThemeManager.bgSidebar());
                activeButton.setForeground(ThemeManager.textSecondary());
            }
            btn.setBackground(ThemeManager.accentSoft());
            btn.setForeground(ThemeManager.accent());
            activeButton = btn;
        });

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != activeButton)
                    btn.setBackground(ThemeManager.hoverBg());
            }

            public void mouseExited(MouseEvent e) {
                if (btn != activeButton)
                    btn.setBackground(ThemeManager.bgSidebar());
            }
        });
        return btn;
    }

    private JButton crearBotonSidebarAction(String texto, String iconKey) {
        JButton btn = new JButton(" " + texto);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(ThemeManager.danger());
        btn.setBackground(ThemeManager.bgSidebar());
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(220, 40));
        btn.setPreferredSize(new Dimension(220, 40));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 8));

        btn.setIcon(IconFactory.crear(iconKey, 18, ThemeManager.danger()));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ThemeManager.hoverBg());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(ThemeManager.bgSidebar());
            }
        });
        return btn;
    }

    private JSeparator crearSeparador() {
        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeManager.border());
        sep.setMaximumSize(new Dimension(220, 1));
        return sep;
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeManager.bgDark());
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.border()),
                BorderFactory.createEmptyBorder(14, 25, 14, 25)));

        lblHeaderTitle = new JLabel("Mesas");
        lblHeaderTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblHeaderTitle.setForeground(ThemeManager.textPrimary());
        header.add(lblHeaderTitle, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeader.setBackground(ThemeManager.bgDark());

        JLabel lblUsuario = new JLabel("👤 " + SesionManager.getNombreDisplay());
        lblUsuario.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblUsuario.setForeground(ThemeManager.accentText());
        rightHeader.add(lblUsuario);

        JLabel lblDiv = new JLabel(" | ");
        lblDiv.setForeground(ThemeManager.textMuted());
        rightHeader.add(lblDiv);

        JLabel lblClock = new JLabel();
        lblClock.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblClock.setForeground(ThemeManager.textMuted());
        Timer clockTimer = new Timer(1000,
                e -> lblClock.setText(new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss").format(new Date())));
        clockTimer.start();
        clockTimer.getActionListeners()[0].actionPerformed(null);
        rightHeader.add(lblClock);

        header.add(rightHeader, BorderLayout.EAST);
        return header;
    }

    private void cambiarModulo(String panelKey, String titulo) {
        // SEC-3: Verificar sesión antes de cambiar de módulo
        if (!SesionManager.verificarSesion()) {
            JOptionPane.showMessageDialog(this,
                    "Su sesión ha expirado por inactividad.\nPor favor, inicie sesión nuevamente.",
                    "Sesión Expirada", JOptionPane.WARNING_MESSAGE);
            dispose();
            SwingUtilities.invokeLater(() -> new PanelLogin().setVisible(true));
            return;
        }
        SesionManager.registrarActividad();

        // SEC-5: Verificar permisos RBAC para paneles restringidos
        if ((PANEL_EMPLEADOS.equals(panelKey) || PANEL_DESEMPENO.equals(panelKey))
                && !SesionManager.esGerente()) {
            JOptionPane.showMessageDialog(this,
                    "No tiene permisos para acceder a este módulo.",
                    "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (PANEL_FACTURACION.equals(panelKey) && !SesionManager.puedeFacturar()) {
            JOptionPane.showMessageDialog(this,
                    "No tiene permisos para acceder a este módulo.",
                    "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        cardLayout.show(contentPanel, panelKey);
        if (lblHeaderTitle != null)
            lblHeaderTitle.setText(titulo);
        for (Component c : contentPanel.getComponents()) {
            if (c.isVisible() && c instanceof Refrescable) {
                ((Refrescable) c).refrescar();
            }
        }
    }

    private JPanel crearPanelAcerca() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeManager.bgContent());

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(ThemeManager.bgCard());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border(), 1),
                BorderFactory.createEmptyBorder(40, 55, 40, 55)));

        JLabel logoAcerca = new JLabel();
        logoAcerca.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            java.net.URL url = getClass().getResource("/imagenes/CSSLogo.jpg");
            if (url != null) {
                Image scaled = new ImageIcon(url).getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
                logoAcerca.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ignored) {
        }
        card.add(logoAcerca);
        card.add(Box.createRigidArea(new Dimension(0, 18)));

        JLabel titulo = new JLabel("Sistema de Gestion de Restaurantes");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(ThemeManager.textPrimary());
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titulo);
        card.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel version = new JLabel("Version 2.0.0");
        version.setFont(new Font("SansSerif", Font.BOLD, 14));
        version.setForeground(ThemeManager.accent());
        version.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(version);
        card.add(Box.createRigidArea(new Dimension(0, 22)));

        String[] lineas = {
                "Desarrollado para el Restaurante CSS",
                "Proyecto Academico - CFE 2025", "",
                "Equipo de desarrollo:",
                "Thiago Sosa  |  Ezequiel Costa"
        };
        for (String l : lineas) {
            JLabel lbl = new JLabel(l);
            lbl.setFont(new Font("SansSerif", l.startsWith("Equipo") ? Font.BOLD : Font.PLAIN, 13));
            lbl.setForeground(l.isEmpty() ? ThemeManager.bgCard() : ThemeManager.textSecondary());
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(lbl);
            card.add(Box.createRigidArea(new Dimension(0, 4)));
        }

        card.add(Box.createRigidArea(new Dimension(0, 22)));
        JLabel tech = new JLabel("Java 25 | FlatLaf | PostgreSQL | HikariCP | Maven");
        tech.setFont(new Font("SansSerif", Font.ITALIC, 11));
        tech.setForeground(ThemeManager.textMuted());
        tech.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(tech);

        panel.add(card);
        return panel;
    }

    /** Interfaz para paneles que necesitan refrescar datos. */
    public interface Refrescable {
        void refrescar();
    }

    public static void main(String[] args) {
        ThemeManager.aplicarTema();
        SwingUtilities.invokeLater(() -> new MenuPuntoVenta().setVisible(true));
    }
}
