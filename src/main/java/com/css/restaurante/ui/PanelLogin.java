package com.css.restaurante.ui;

import com.css.restaurante.dao.ConexionDB;
import com.css.restaurante.dao.EmpleadoDAO;
import com.css.restaurante.modelo.Empleado;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Pantalla de login para empleados del restaurante.
 * Campos: usuario y contraseña. Mensajes de error genéricos.
 * Indicador visual de cuenta bloqueada. Toggle de tema.
 */
public class PanelLogin extends JFrame {

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JLabel lblError;

    public PanelLogin() {
        setTitle("SGR - Iniciar Sesión");
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

        JPanel card = crearCardLogin();
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
            SwingUtilities.invokeLater(PanelLogin::new);
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

    private JPanel crearCardLogin() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (ThemeManager.isDark()) {
                    g2.setColor(new Color(0, 0, 0, 40));
                    g2.fill(new RoundRectangle2D.Double(4, 4, getWidth() - 4, getHeight() - 4, 24, 24));
                }
                g2.setColor(ThemeManager.bgCard());
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 4, getHeight() - 4, 20, 20));
                g2.setColor(ThemeManager.border());
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 5, getHeight() - 5, 20, 20));
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(45, 55, 45, 55));

        // Logo
        JLabel lblLogo = new JLabel();
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            java.net.URL url = getClass().getResource("/imagenes/CSSLogoENPNG.png");
            if (url != null) {
                Image scaled = new ImageIcon(url).getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ignored) {
        }
        card.add(lblLogo);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Línea decorativa
        JPanel lineaDeco = crearLineaDecorativa();
        card.add(lineaDeco);
        card.add(Box.createRigidArea(new Dimension(0, 18)));

        // Título
        JLabel lblTitulo = new JLabel("Iniciar Sesión");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitulo.setForeground(ThemeManager.textPrimary());
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblTitulo);
        card.add(Box.createRigidArea(new Dimension(0, 6)));

        JLabel lblSub = new JLabel("Sistema de Gestión — Restaurante CSS");
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSub.setForeground(ThemeManager.textMuted());
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblSub);
        card.add(Box.createRigidArea(new Dimension(0, 30)));

        // Campo usuario
        card.add(crearLabelCampo("👤 Usuario"));
        txtUsuario = crearCampoTexto();
        card.add(txtUsuario);
        card.add(Box.createRigidArea(new Dimension(0, 15)));

        // Campo contraseña
        card.add(crearLabelCampo("🔒 Contraseña"));
        txtContrasena = new JPasswordField();
        estilizarCampo(txtContrasena);
        card.add(txtContrasena);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Label de error
        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblError.setForeground(ThemeManager.danger());
        lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblError.setMaximumSize(new Dimension(320, 25));
        lblError.setPreferredSize(new Dimension(320, 25));
        card.add(lblError);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Botón ingresar
        JButton btnLogin = crearBotonPrincipal("Iniciar Sesión");
        btnLogin.addActionListener(e -> intentarLogin());
        card.add(btnLogin);
        card.add(Box.createRigidArea(new Dimension(0, 12)));

        // Botón salir
        JButton btnSalir = crearBotonSecundario("Salir");
        btnSalir.addActionListener(e -> {
            ConexionDB.shutdown();
            System.exit(0);
        });
        card.add(btnSalir);

        // Enter = login
        txtContrasena.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    intentarLogin();
                }
            }
        });
        txtUsuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtContrasena.requestFocusInWindow();
                }
            }
        });

        return card;
    }

    private void intentarLogin() {
        String usuario = InputValidator.sanitizar(txtUsuario.getText());
        char[] contrasena = txtContrasena.getPassword();

        if (usuario.isEmpty() || contrasena.length == 0) {
            mostrarError("Ingrese usuario y contrase\u00f1a.");
            return;
        }

        try {
            // Verificar bloqueo antes de intentar
            if (empleadoDAO.verificarBloqueo(usuario)) {
                AuditLogger.cuentaBloqueada(usuario);
                mostrarError("\u26A0 Cuenta bloqueada temporalmente. Intente en 1 minuto.");
                return;
            }

            Empleado empleado = empleadoDAO.autenticar(usuario, contrasena);
            // contrasena ya fue limpiado internamente por EmpleadoDAO
            if (empleado != null) {
                AuditLogger.loginExitoso(usuario);
                SesionManager.iniciarSesion(empleado);
                dispose();
                SwingUtilities.invokeLater(() -> new MenuPuntoVenta().setVisible(true));
            } else {
                AuditLogger.loginFallido(usuario);
                // Mensaje gen\u00e9rico \u2014 no revelar si el usuario existe o no
                mostrarError("Credenciales inv\u00e1lidas.");
                txtContrasena.setText("");
                txtContrasena.requestFocusInWindow();
            }
        } catch (Exception ex) {
            mostrarError("Error de conexi\u00f3n. Intente m\u00e1s tarde.");
            System.err.println("[SGR] Error en autenticación.");
        }
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        // Animación de sacudida en el label de error
        Timer shake = new Timer(30, null);
        final int[] steps = { -5, 5, -3, 3, -1, 1, 0 };
        final int[] idx = { 0 };
        Point original = lblError.getLocation();
        shake.addActionListener(e -> {
            if (idx[0] < steps.length) {
                lblError.setLocation(original.x + steps[idx[0]], original.y);
                idx[0]++;
            } else {
                lblError.setLocation(original);
                shake.stop();
            }
        });
        shake.start();
    }

    // ===== Componentes de UI =====

    private JLabel crearLabelCampo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.textSecondary());
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(320, 25));
        lbl.setPreferredSize(new Dimension(320, 25));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 4, 5, 0));
        return lbl;
    }

    private JTextField crearCampoTexto() {
        JTextField field = new JTextField();
        estilizarCampo(field);
        return field;
    }

    private void estilizarCampo(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(ThemeManager.textPrimary());
        field.setBackground(ThemeManager.inputBg());
        field.setCaretColor(ThemeManager.accent());
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border(), 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(new Dimension(320, 44));
        field.setPreferredSize(new Dimension(320, 44));

        // Efecto de foco
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeManager.accent(), 2),
                        BorderFactory.createEmptyBorder(9, 13, 9, 13)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ThemeManager.border(), 1),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)));
            }
        });
    }

    private JPanel crearLineaDecorativa() {
        JPanel linea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, new Color(215, 155, 0, 0),
                        getWidth() / 2, 0, ThemeManager.accent(), false));
                g2.fillRect(0, 0, getWidth() / 2, 2);
                g2.setPaint(new GradientPaint(getWidth() / 2, 0, ThemeManager.accent(),
                        getWidth(), 0, new Color(215, 155, 0, 0), false));
                g2.fillRect(getWidth() / 2, 0, getWidth() / 2, 2);
                g2.dispose();
            }
        };
        linea.setOpaque(false);
        linea.setMaximumSize(new Dimension(280, 2));
        linea.setPreferredSize(new Dimension(280, 2));
        linea.setAlignmentX(Component.CENTER_ALIGNMENT);
        return linea;
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
}
