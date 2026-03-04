package com.css.restaurante.principal;

import com.css.restaurante.ui.PanelLogin;
import com.css.restaurante.ui.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Pantalla de bienvenida del SGR.
 * Splash screen moderno con soporte tema claro/oscuro, logo del restaurante,
 * y animación de fade-in. Sin sonido al ingresar.
 */
public class BienvenidaMenuInicial extends JFrame {

    public BienvenidaMenuInicial() {
        setTitle("SGR - Sistema de Gestión de Restaurantes");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(MAXIMIZED_BOTH);

        // Ícono de barra de tareas
        try {
            java.net.URL iconUrl = getClass().getResource("/imagenes/CSSLogo.jpg");
            if (iconUrl != null)
                setIconImage(new ImageIcon(iconUrl).getImage());
        } catch (Exception ignored) {
        }

        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradiente sutil de fondo
                Color c1 = ThemeManager.bgDark();
                Color c2 = ThemeManager.isDark()
                        ? new Color(25, 22, 38)
                        : new Color(230, 235, 245);
                g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);

        // Card central con sombra visual
        JPanel card = crearCardCentral();
        mainPanel.add(card);

        // Toggle tema en esquina superior derecha
        JButton btnTema = new JButton(ThemeManager.themeIcon() + " " + ThemeManager.themeLabel());
        btnTema.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnTema.setForeground(ThemeManager.textSecondary());
        btnTema.setBackground(ThemeManager.bgCard());
        btnTema.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        btnTema.setFocusPainted(false);
        btnTema.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTema.addActionListener(e -> {
            ThemeManager.toggle();
            dispose();
            SwingUtilities.invokeLater(BienvenidaMenuInicial::new);
        });

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        topBar.setOpaque(false);
        topBar.add(btnTema);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(topBar, BorderLayout.NORTH);
        wrapper.add(mainPanel, BorderLayout.CENTER);

        setContentPane(wrapper);

        // Animación de fade-in
        setOpacity(0f);
        setVisible(true);
        Timer fadeIn = new Timer(12, null);
        fadeIn.addActionListener(e -> {
            float opacity = getOpacity();
            if (opacity < 1f) {
                setOpacity(Math.min(opacity + 0.05f, 1f));
            } else {
                fadeIn.stop();
            }
        });
        fadeIn.start();
    }

    private JPanel crearCardCentral() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Sombra suave
                if (ThemeManager.isDark()) {
                    g2.setColor(new Color(0, 0, 0, 40));
                    g2.fill(new RoundRectangle2D.Double(4, 4, getWidth() - 4, getHeight() - 4, 24, 24));
                }
                // Fondo card
                g2.setColor(ThemeManager.bgCard());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 4, getHeight() - 4, 20, 20));
                // Borde sutil
                g2.setColor(ThemeManager.border());
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 5, getHeight() - 5, 20, 20));
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(50, 65, 50, 65));

        // Logo del restaurante
        JLabel lblLogo = new JLabel();
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            java.net.URL url = getClass().getResource("/imagenes/CSSLogoENPNG.png");
            if (url != null) {
                ImageIcon original = new ImageIcon(url);
                // Logo circular con borde dorado
                int size = 200;
                Image scaled = original.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ignored) {
        }
        card.add(lblLogo);
        card.add(Box.createRigidArea(new Dimension(0, 28)));

        // Línea decorativa ámbar
        JPanel lineaDecorator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(215, 155, 0, 0), getWidth() / 2, 0,
                        ThemeManager.accent(), false));
                g2.fillRect(0, 0, getWidth() / 2, 2);
                g2.setPaint(new GradientPaint(getWidth() / 2, 0, ThemeManager.accent(),
                        getWidth(), 0, new Color(215, 155, 0, 0), false));
                g2.fillRect(getWidth() / 2, 0, getWidth() / 2, 2);
                g2.dispose();
            }
        };
        lineaDecorator.setOpaque(false);
        lineaDecorator.setMaximumSize(new Dimension(300, 2));
        lineaDecorator.setPreferredSize(new Dimension(300, 2));
        lineaDecorator.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lineaDecorator);
        card.add(Box.createRigidArea(new Dimension(0, 22)));

        // Título
        JLabel lblTitulo = new JLabel("Restaurante CSS");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 34));
        lblTitulo.setForeground(ThemeManager.textPrimary());
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitulo);
        card.add(Box.createRigidArea(new Dimension(0, 6)));

        // Subtítulo
        JLabel lblSub = new JLabel("Sistema de Gestión de Punto de Venta");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 15));
        lblSub.setForeground(ThemeManager.textSecondary());
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblSub);
        card.add(Box.createRigidArea(new Dimension(0, 40)));

        // Botón Ingresar (sin sonido)
        JButton btnIngresar = crearBotonPrincipal("Ingresar al Sistema");
        btnIngresar.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new PanelLogin().setVisible(true));
        });
        card.add(btnIngresar);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // Botón Salir
        JButton btnSalir = crearBotonSecundario("Salir");
        btnSalir.addActionListener(e -> {
            com.css.restaurante.dao.ConexionDB.shutdown();
            System.exit(0);
        });
        card.add(btnSalir);
        card.add(Box.createRigidArea(new Dimension(0, 30)));

        // Versión
        JLabel lblVersion = new JLabel("v2.0.0 — Proyecto Académico CSS • 2025");
        lblVersion.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblVersion.setForeground(ThemeManager.textMuted());
        lblVersion.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblVersion);

        return card;
    }

    private JButton crearBotonPrincipal(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(ThemeManager.accent().brighter());
                } else {
                    g2.setPaint(new GradientPaint(0, 0, ThemeManager.accent(),
                            0, getHeight(), ThemeManager.accent().darker()));
                }
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 15));
        btn.setForeground(ThemeManager.accentText());
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(320, 48));
        btn.setPreferredSize(new Dimension(320, 48));
        return btn;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(ThemeManager.textSecondary());
        btn.setBackground(ThemeManager.bgCard());
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border(), 1),
                BorderFactory.createEmptyBorder(8, 30, 8, 30)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(320, 40));
        btn.setPreferredSize(new Dimension(320, 40));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ThemeManager.hoverBg());
                btn.setForeground(ThemeManager.textPrimary());
            }

            public void mouseExited(MouseEvent e) {
                btn.setBackground(ThemeManager.bgCard());
                btn.setForeground(ThemeManager.textSecondary());
            }
        });
        return btn;
    }

    public static void main(String[] args) {
        ThemeManager.aplicarTema();
        SwingUtilities.invokeLater(BienvenidaMenuInicial::new);
    }
}
