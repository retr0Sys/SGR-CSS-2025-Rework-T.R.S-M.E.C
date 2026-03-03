package ProgramaPrincipal;

import Forms.MenuPuntoVenta;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.sound.sampled.*;

public class BienvenidaMenuInicial extends JFrame
{
    public JPanel JPBienvenida;
    private JLabel lblBienvenida;
    private JLabel lblSubtitulo;
    private JButton BtnIngresar;
    private JButton BtnSalir;
    private JLabel lblFoto;
    private JFrame ventanaPrincipal;

    public BienvenidaMenuInicial(JFrame frame)
    {
        // Obtiene el tamaño de la pantalla del sistema
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        this.ventanaPrincipal = frame;

        // --- Ajustes visuales del panel principal ---
        JPBienvenida = new JPanel();
        JPBienvenida.setOpaque(false);
        JPBienvenida.setLayout(new BoxLayout(JPBienvenida, BoxLayout.Y_AXIS));
        JPBienvenida.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        Font fuenteTitulo = new Font("SansSerif", Font.BOLD, 30);
        Font fuenteSubtitulo = new Font("SansSerif", Font.PLAIN, 18);

        // --- Título principal ---
        lblBienvenida = new JLabel("¡Bienvenido al Punto de Venta!");
        lblBienvenida.setFont(fuenteTitulo);
        lblBienvenida.setForeground(new Color(30, 30, 30));
        lblBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblBienvenida.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        // --- Subtítulo ---
        lblSubtitulo = new JLabel("Sistema de gestión para restaurantes");
        lblSubtitulo.setFont(fuenteSubtitulo);
        lblSubtitulo.setForeground(new Color(100, 100, 100));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // --- Imagen/logo central ---
        lblFoto = new JLabel();
        int logoSize = (int) (screenHeight * 0.3);
        ImageIcon iconoCSS = new ImageIcon(new ImageIcon("imagenes/CSSLogo.jpg").getImage().getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH));
        lblFoto.setIcon(iconoCSS);
        lblFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFoto.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // --- Botón de ingreso ---
        BtnIngresar = new JButton();
        int btnSize = (int) (screenHeight * 0.25);
        ImageIcon iconoIngresar = new ImageIcon(new ImageIcon("imagenes/Ingresar.png").getImage().getScaledInstance(btnSize, btnSize, Image.SCALE_SMOOTH));
        BtnIngresar.setIcon(iconoIngresar);
        BtnIngresar.setPreferredSize(new Dimension(btnSize, btnSize));
        BtnIngresar.setMaximumSize(new Dimension(btnSize, btnSize));
        BtnIngresar.setContentAreaFilled(false);
        BtnIngresar.setBorderPainted(false);
        BtnIngresar.setFocusPainted(false);
        BtnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        BtnIngresar.setAlignmentX(Component.CENTER_ALIGNMENT);
        BtnIngresar.setToolTipText("Haz clic para comenzar");

        // Acción al hacer clic en "Ingresar"
        BtnIngresar.addActionListener(e ->
        {
            reproducirSonido("sonido/music.wav");
            ventanaPrincipal.dispose();
            MenuPuntoVenta menu = new MenuPuntoVenta();
            menu.setContentPane(menu.JPMenuPrinc);
            menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            menu.setExtendedState(JFrame.MAXIMIZED_BOTH);
            menu.setLocationRelativeTo(null);
            menu.setVisible(true);
        });

        // Efectos visuales al pasar el mouse sobre el botón
        BtnIngresar.addMouseListener(new MouseAdapter()
        {
            public void mouseEntered(MouseEvent evt)
            {
                BtnIngresar.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2));
            }

            public void mouseExited(MouseEvent evt)
            {
                BtnIngresar.setBorder(BorderFactory.createEmptyBorder());
            }
        });

        // --- Botón de salida ---
        BtnSalir = new JButton("Salir");
        BtnSalir.setFont(new Font("SansSerif", Font.BOLD, 14));
        BtnSalir.setForeground(Color.WHITE);
        BtnSalir.setBackground(new Color(180, 50, 50));
        BtnSalir.setFocusPainted(false);
        BtnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        BtnSalir.setPreferredSize(new Dimension(100, 35));

        // Acción al hacer clic en "Salir"
        BtnSalir.addActionListener(e ->
        {
            reproducirSonido("sonido/button_09-190435.wav");
            System.exit(0);
        });

        // --- Agrega los elementos al panel ---
        JPBienvenida.add(lblBienvenida);
        JPBienvenida.add(lblSubtitulo);
        JPBienvenida.add(lblFoto);
        JPBienvenida.add(Box.createRigidArea(new Dimension(0, 10)));
        JPBienvenida.add(BtnIngresar);
        JPBienvenida.add(Box.createRigidArea(new Dimension(0, 10)));
        JPBienvenida.add(BtnSalir);
    }

    //Reproduccion de sonidos
    private void reproducirSonido(String ruta)
    {
        try
        {
            File archivoSonido = new File(ruta);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoSonido);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        }
        catch (Exception ex)
        {
            System.out.println("No se pudo reproducir el sonido: " + ex.getMessage());
        }
    }

    public static void main(String[] args)
    {
        // --- Configuración inicial de la ventana principal ---
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setSize(screenSize);
        frame.setLocation(0, 0);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BienvenidaMenuInicial bienvenida = new BienvenidaMenuInicial(frame);

        // --- Fondo con imagen ---
        JLabel fondo = new JLabel(new ImageIcon("imagenes/madera.jpg"));
        fondo.setLayout(new BorderLayout());

        // --- Panel central ---
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBackground(new Color(255, 255, 255, 200));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        panelCentral.add(bienvenida.JPBienvenida, BorderLayout.CENTER);

        // --- Panel inferior con botón "Salir" ---
        JPanel panelSalir = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSalir.setOpaque(false);
        panelSalir.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 10));
        panelSalir.add(bienvenida.BtnSalir);

        // --- Ensambla tod-o ---
        fondo.add(panelCentral, BorderLayout.CENTER);
        fondo.add(panelSalir, BorderLayout.SOUTH);

        frame.setContentPane(fondo);
        frame.setVisible(true);

        // --- Animación de entrada ---
        SwingUtilities.invokeLater(() ->
        {
            Point start = bienvenida.JPBienvenida.getLocation();
            bienvenida.JPBienvenida.setLocation(start.x, start.y + 100);

            Timer anim = new Timer(10, null);
            anim.addActionListener(new ActionListener()
            {
                int y = start.y + 100;

                public void actionPerformed(ActionEvent e)
                {
                    if (y > start.y)
                    {
                        y -= 4;
                        bienvenida.JPBienvenida.setLocation(start.x, y);
                    }
                    else
                    {
                        bienvenida.JPBienvenida.setLocation(start.x, start.y);
                        anim.stop();
                    }
                }
            });
            anim.start();
        });
    }
}
