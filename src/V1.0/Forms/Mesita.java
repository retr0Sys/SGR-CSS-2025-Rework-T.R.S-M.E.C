package Forms;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import Clases.concret.Mesa;
import DAO.MesaDAO;

/**
 * Clase Mesita
 * -------------------------------
 * Esta clase representa una interfaz gráfica que muestra
 * las mesas del restaurante como imágenes (íconos) organizadas
 * en una cuadrícula. Cada imagen refleja el estado actual
 * de la mesa (libre, ocupada, reservada o en limpieza).
 *
 * Al hacer clic sobre una mesa, se abre el formulario FormMesa
 * correspondiente a esa mesa en particular.
 */
public class Mesita extends JFrame
{
    // Panel principal que contiene todas las mesas
    public JPanel panelMesita;

    // Panel interior dentro de un posible scroll
    private JPanel JPDentroScroll;

    // Botones que representan las mesas
    private JButton btnMesa1, btnMesa2, btnMesa3, btnMesa4, btnMesa5, btnMesa6;
    private JButton btnMesa7, btnMesa8, btnMesa9, btnMesa10, btnMesa11, btnMesa12;

    // Arreglo de botones e íconos para manejar las mesas dinámicamente
    private JButton[] botonesMesa;
    private ImageIcon[] iconosMesa;

    // Objeto de acceso a datos de mesas
    private MesaDAO mesaDAO = new MesaDAO();

    /**
     * Constructor principal de la clase Mesita.
     * Inicializa la interfaz y carga el estado visual de cada mesa.
     */
    public Mesita()
    {
        // Obtener la resolución actual de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int anchoPantalla = screenSize.width;

        // Tamaño relativo del ícono de mesa (10% del ancho total)
        int iconSize = (int) (anchoPantalla * 0.10);

        // Configuración visual del panel principal
        panelMesita = new JPanel(new GridLayout(3, 4, 30, 30)); // 3 filas x 4 columnas
        panelMesita.setBackground(new Color(245, 245, 245));
        panelMesita.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Panel interno decorativo (no obligatorio)
        JPDentroScroll = new JPanel();
        JPDentroScroll.setBackground(Color.WHITE);
        JPDentroScroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Inicialización de los 12 botones de mesa
        btnMesa1 = new JButton(); btnMesa2 = new JButton(); btnMesa3 = new JButton(); btnMesa4 = new JButton();
        btnMesa5 = new JButton(); btnMesa6 = new JButton(); btnMesa7 = new JButton(); btnMesa8 = new JButton();
        btnMesa9 = new JButton(); btnMesa10 = new JButton(); btnMesa11 = new JButton(); btnMesa12 = new JButton();

        // Agrupar los botones en un arreglo para manejarlo de forma uniforme
        botonesMesa = new JButton[]
                {
                        btnMesa1, btnMesa2, btnMesa3, btnMesa4, btnMesa5, btnMesa6,
                        btnMesa7, btnMesa8, btnMesa9, btnMesa10, btnMesa11, btnMesa12
                };

        iconosMesa = new ImageIcon[12]; // Se cargan en actualizarEstadosMesas()

        // Cargar el estado actual de cada mesa desde la base de datos
        actualizarEstadosMesas(iconSize);

        // Configurar cada botón (mesa) y su evento de clic
        for (int i = 0; i < botonesMesa.length; i++)
        {
            final int index = i;
            JButton boton = botonesMesa[i];

            // Ajustes visuales
            boton.setPreferredSize(new Dimension(iconSize, iconSize));
            boton.setBorderPainted(false);
            boton.setContentAreaFilled(false);
            boton.setFocusPainted(false);
            boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Evento al presionar una mesa
            boton.addActionListener(e ->
            {
                int idMesaSeleccionada = index + 1; // número de mesa (1 a 12)
                dispose(); // Cierra la ventana actual

                // Abre el formulario de gestión de esa mesa
                SwingUtilities.invokeLater(() ->
                {
                    JFrame frame = new JFrame("FormMesa");
                    frame.setUndecorated(true);

                    // Se crea la vista del formulario correspondiente a la mesa seleccionada
                    FormMesa vista = new FormMesa(idMesaSeleccionada);

                    adaptarVentanaAResolucion(frame);
                    frame.setContentPane(vista.JPMesasIni);
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                });
            });

            // Agregar el botón al panel
            panelMesita.add(boton);
        }
    }

    /**
     * Actualiza los íconos de las mesas según su estado.
     * Los íconos se obtienen de la carpeta "imagenes/" y cambian
     * dependiendo del estado de la mesa (ocupada, reservada, libre, limpieza).
     */
    private void actualizarEstadosMesas(int iconSize)
    {
        try
        {
            // Obtener lista de mesas desde la base de datos
            List<Mesa> mesas = mesaDAO.listar();

            for (int i = 0; i < botonesMesa.length && i < mesas.size(); i++)
            {
                Mesa mesa = mesas.get(i);

                // Obtener la ruta del ícono según estado
                String ruta = obtenerRutaIcono(mesa.getIdMesa(), mesa.getEstado());

                // Escalar la imagen al tamaño del botón
                iconosMesa[i] = new ImageIcon(
                        new ImageIcon(ruta).getImage()
                                .getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)
                );

                // Asignar el ícono al botón
                botonesMesa[i].setIcon(iconosMesa[i]);

                // Mostrar información como tooltip
                botonesMesa[i].setToolTipText("Mesa " + mesa.getIdMesa() + " - " + mesa.getEstado());
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar las mesas: " + e.getMessage(),
                    "Error de BD",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Devuelve la ruta de la imagen correspondiente al estado de una mesa.
     */
    private String obtenerRutaIcono(int numeroMesa, String estado)
    {
        if (estado == null)
        {
            estado = "Libre";
        }

        switch (estado.toLowerCase())
        {
            case "ocupada":
                return "imagenes/mr" + numeroMesa + ".png";

            case "reservada":
                return "imagenes/ma" + numeroMesa + ".png";

            case "limpieza":
                return "imagenes/mv" + numeroMesa + ".png";

            case "libre":
            default:
                return "imagenes/m" + numeroMesa + ".png";
        }
    }

    /**
     * Adapta una ventana a la resolución visible máxima del sistema operativo.
     * Se usa para que las ventanas se ajusten automáticamente
     * a la pantalla, sin salirse de los límites.
     */
    public static void adaptarVentanaAResolucion(JFrame ventana)
    {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = env.getMaximumWindowBounds();
        ventana.setBounds(bounds);
        ventana.setLocation(bounds.x, bounds.y);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            JFrame frame = new JFrame("Mesita");
            frame.setUndecorated(true);

            Mesita vista = new Mesita();
            adaptarVentanaAResolucion(frame);

            frame.setContentPane(vista.panelMesita);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
