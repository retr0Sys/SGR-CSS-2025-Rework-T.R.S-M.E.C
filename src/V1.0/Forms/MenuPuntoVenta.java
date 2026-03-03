package Forms;

import Clases.concret.Mesa;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Clase principal del sistema de Punto de Venta.
 * Representa el menú principal desde donde el usuario puede acceder
 * a las diferentes secciones del sistema: Carta, Cocina, Facturación, Mesas,
 * Resumen y Acerca de.
 *
 * Implementa una interfaz gráfica con botones iconográficos y efectos visuales,
 * además de reproducir sonidos al interactuar con los elementos.
 */
public class MenuPuntoVenta extends JFrame
{
    // Panel principal del menú
    public JPanel JPMenuPrinc;

    // Botones principales
    private JButton btnCarta, btnCocina, btnFact, btnMesa, btnResumen, btnAcerca, btnSalir;

    // Etiqueta informativa con el nombre del sistema
    private JLabel lblCSS;

    /**
     * Constructor principal.
     * Configura la ventana del menú, los botones, sus imágenes y las acciones.
     */
    public MenuPuntoVenta()
    {
        // Fondo visual con textura de madera
        JLabel fondo = new JLabel(new ImageIcon("imagenes/madera.jpg"));
        fondo.setLayout(new BorderLayout());

        // Panel contenedor principal
        JPMenuPrinc = new JPanel();
        JPMenuPrinc.setOpaque(false);
        JPMenuPrinc.setLayout(new BoxLayout(JPMenuPrinc, BoxLayout.Y_AXIS));
        JPMenuPrinc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(40, 40, 40, 40),
                BorderFactory.createMatteBorder(2, 2, 6, 6, new Color(120, 120, 120, 60))
        ));

        // Título del menú principal
        JLabel lblTitulo = new JLabel("Menú Principal");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 30));
        lblTitulo.setForeground(new Color(60, 60, 60));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));

        // Panel que contiene los botones principales dispuestos en una cuadrícula
        JPanel panelBotones = new JPanel(new GridLayout(2, 3, 30, 30));
        panelBotones.setOpaque(false);
        panelBotones.setBackground(new Color(255, 255, 255, 80));
        panelBotones.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));

        // Creación de los botones con imágenes
        btnCarta = crearBoton("imagenes/Menu.png");
        btnCocina = crearBoton("imagenes/Cocina.png");
        btnFact = crearBoton("imagenes/Facturas.png");
        btnMesa = crearBoton("imagenes/Mesas.png");
        btnResumen = crearBoton("imagenes/Resumen.png");
        btnAcerca = crearBoton("imagenes/Acerca.png");

        // Agrega los botones al panel
        panelBotones.add(btnCarta);
        panelBotones.add(btnCocina);
        panelBotones.add(btnFact);
        panelBotones.add(btnMesa);
        panelBotones.add(btnResumen);
        panelBotones.add(btnAcerca);

        // Etiqueta inferior con logo y nombre del sistema
        lblCSS = new JLabel("Sistema CSS - Punto de Venta");
        lblCSS.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblCSS.setForeground(new Color(80, 80, 80));
        lblCSS.setIcon(new ImageIcon("imagenes/iconoCSS.png"));
        lblCSS.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCSS.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Configuración del botón "Salir"
        btnSalir = new JButton("Salir");
        btnSalir.setIcon(new ImageIcon(
                new ImageIcon("imagenes/Salir.png")
                        .getImage()
                        .getScaledInstance(40, 40, Image.SCALE_SMOOTH)
        ));
        btnSalir.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setBackground(new Color(180, 50, 50));
        btnSalir.setFocusPainted(false);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.setPreferredSize(new Dimension(200, 60));
        btnSalir.setMaximumSize(new Dimension(200, 60));
        btnSalir.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Acción del botón Salir: reproduce sonido y cierra la aplicación
        btnSalir.addActionListener(e ->
        {
            reproducirSonido("sonido/button_09-190435.wav");
            System.exit(0);
        });

        // Panel contenedor del botón salir
        JPanel panelSalir = new JPanel();
        panelSalir.setOpaque(false);
        panelSalir.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        panelSalir.add(btnSalir);
        panelSalir.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Asignación de acciones a los demás botones
        btnCarta.addActionListener(e ->
        {
            reproducirSonido("sonido/button_09-190435.wav");
            abrirVentanaMax(new Carta(), "ventanaCarta");
        });

        btnCocina.addActionListener(e ->
        {
            reproducirSonido("sonido/button_09-190435.wav");
            abrirVentanaMax(new Cocina(), "ventanaCocina");
        });

        btnFact.addActionListener(e ->
        {
            reproducirSonido("sonido/button_09-190435.wav");
            abrirVentanaMax(new Facturacion(), "ventanaFact");
        });

        btnMesa.addActionListener(e ->
        {
            reproducirSonido("sonido/button_09-190435.wav");
            abrirVentanaMax(new Mesita(), "panelMesita");
        });

        btnResumen.addActionListener(e ->
        {
            reproducirSonido("sonido/button_09-190435.wav");
            abrirVentanaMax(new Resumen(), "ventanaResumen");
        });

        btnAcerca.addActionListener(e ->
        {
            reproducirSonido("sonido/button_09-190435.wav");
            abrirVentanaAcerca(new Acerca(), "JPacerca");
        });

        // Ensamblaje final de los componentes en el panel principal
        JPMenuPrinc.add(lblTitulo);
        JPMenuPrinc.add(panelBotones);
        JPMenuPrinc.add(lblCSS);
        JPMenuPrinc.add(panelSalir);

        // Se agrega el panel al fondo y se configura la ventana principal
        fondo.add(JPMenuPrinc, BorderLayout.CENTER);
        setContentPane(fondo);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setVisible(true);
    }

    /**
     * Reproduce un archivo de sonido (.wav) al realizar acciones en la interfaz.
     */
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

    /**
     * Crea un botón personalizado con una imagen y efecto al pasar el mouse.
     */
    private JButton crearBoton(String rutaImagen)
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int anchoPantalla = screenSize.width;
        int iconSize = (int) (anchoPantalla * 0.12); // 12% del ancho de la pantalla
        int hoverSize = iconSize + 10; // tamaño al pasar el ratón

        JButton boton = new JButton();
        ImageIcon icono = new ImageIcon(
                new ImageIcon(rutaImagen)
                        .getImage()
                        .getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)
        );
        boton.setIcon(icono);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Efectos visuales al pasar el ratón
        boton.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                boton.setIcon(new ImageIcon(
                        new ImageIcon(rutaImagen)
                                .getImage()
                                .getScaledInstance(hoverSize, hoverSize, Image.SCALE_SMOOTH)
                ));
                boton.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(100, 100, 100)));
            }

            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                boton.setIcon(new ImageIcon(
                        new ImageIcon(rutaImagen)
                                .getImage()
                                .getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)
                ));
                boton.setBorder(BorderFactory.createEmptyBorder());
            }
        });

        return boton;
    }

    /**
     * Abre una nueva ventana a pantalla completa y cierra la actual.
     */
    private void abrirVentanaMax(JFrame ventana, String panelNombre)
    {
        dispose(); // cerrar la ventana actual

        JPanel panel = (JPanel) getPanelPorNombre(ventana, panelNombre);
        ventana.setContentPane(panel);
        ventana.setUndecorated(true);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Abre una ventana informativa tipo "Acerca de".
     */
    private void abrirVentanaAcerca(JFrame ventana, String panelNombre)
    {
        ventana.setContentPane((JPanel) getPanelPorNombre(ventana, panelNombre));
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventana.pack();
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    /**
     * Obtiene un panel interno de una ventana por su nombre.
     * Se usa reflexión para acceder a los campos internos del JFrame.
     */
    private Component getPanelPorNombre(JFrame ventana, String nombreCampo)
    {
        try
        {
            return (Component) ventana.getClass().getDeclaredField(nombreCampo).get(ventana);
        }
        catch (Exception e)
        {
            throw new RuntimeException("No se pudo acceder al panel: " + nombreCampo);
        }
    }

    /**
     * Ajusta la ventana para adaptarse a la resolución de pantalla del dispositivo.
     */
    public static void adaptarVentanaAResolucion(JFrame ventana)
    {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = env.getMaximumWindowBounds(); // área visible sin superponer la barra de tareas

        ventana.setBounds(bounds);
        ventana.setLocation(bounds.x, bounds.y);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            MenuPuntoVenta menu = new MenuPuntoVenta();

            // Adapta la interfaz a la pantalla del usuario
            MenuPuntoVenta.adaptarVentanaAResolucion(menu);

            // Configuración final de la ventana
            menu.setContentPane(menu.JPMenuPrinc);
            menu.setUndecorated(true);
            menu.setLocationRelativeTo(null);
            menu.setVisible(true);
            menu.setExtendedState(JFrame.MAXIMIZED_BOTH);
            menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        });
    }
}
