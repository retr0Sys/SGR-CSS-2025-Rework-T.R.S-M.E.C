package Forms;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;
import DAO.ConexionDB;
import DAO.MesaDAO;
import Clases.concret.Mesa;

/**
 * Clase Resumen
 * -------------------------------------------
 * Esta clase muestra un panel de resumen general del restaurante,
 * con distintas funcionalidades de consulta:
 *  - Ventas del día actual.
 *  - Pedidos realizados por mesa.
 *  - Top 3 productos más vendidos.
 *
 * La interfaz utiliza botones con íconos para ejecutar las consultas,
 * y una tabla que presenta los resultados.
 */
public class Resumen extends JFrame
{
    // Panel principal del formulario
    public JPanel ventanaResumen;

    // Componentes gráficos
    private JLabel lblTitulo;
    private JButton btnVentasDelDia;
    private JButton btnpedidosPorMesa;
    private JComboBox<String> cboxMesa;
    private JButton btnTop3;
    private JTable tblTabla;
    private JButton btnAtras;

    // Modelo de la tabla donde se cargan los datos de las consultas
    private DefaultTableModel modelo;

    /**
     * Constructor principal del formulario Resumen.
     * Configura la interfaz, estilos visuales y acciones de los botones.
     */
    public Resumen()
    {
        // --- Estética general ---
        Color fondo = new Color(245, 245, 245);
        Color acento = new Color(255, 159, 101);
        Font fuenteTitulo = new Font("Segoe UI", Font.BOLD, 22);
        Font fuenteGeneral = new Font("Segoe UI", Font.PLAIN, 14);

        // Calcular tamaño relativo de íconos según resolución de pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int anchoPantalla = screenSize.width;
        int iconSize = (int) (anchoPantalla * 0.08);

        // --- Panel principal ---
        ventanaResumen = new JPanel(new BorderLayout());
        ventanaResumen.setBackground(fondo);
        ventanaResumen.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // --- Título ---
        lblTitulo = new JLabel("Resumen del Restaurante", SwingConstants.CENTER);
        lblTitulo.setFont(fuenteTitulo);
        lblTitulo.setForeground(acento);
        ventanaResumen.add(lblTitulo, BorderLayout.NORTH);

        // --- Tabla principal ---
        String[] columnas = {"Mesa", "Producto", "Cantidad", "Total"};
        modelo = new DefaultTableModel(columnas, 0);
        tblTabla = new JTable(modelo);

        tblTabla.setFont(fuenteGeneral);
        tblTabla.setRowHeight(28);
        tblTabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblTabla.getTableHeader().setBackground(acento);
        tblTabla.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollTabla = new JScrollPane(tblTabla);
        ventanaResumen.add(scrollTabla, BorderLayout.CENTER);

        // --- Panel inferior de controles ---
        JPanel panelControles = new JPanel(new GridLayout(1, 5, 20, 10));
        panelControles.setBackground(fondo);

        // ComboBox para seleccionar una mesa
        cboxMesa = new JComboBox<>();
        cboxMesa.setFont(fuenteGeneral);
        cboxMesa.setBorder(BorderFactory.createLineBorder(acento, 2));
        cargarMesasDesdeBD(); // carga nombres de mesas desde la tabla "mesa"

        // Inicialización de botones
        btnVentasDelDia = new JButton();
        btnpedidosPorMesa = new JButton();
        btnTop3 = new JButton();
        btnAtras = new JButton();

        // Asignar íconos a los botones
        estilizarBoton(btnVentasDelDia, "imagenes/Ventas diarias.png", iconSize);
        estilizarBoton(btnpedidosPorMesa, "imagenes/Pedidos x mesa.png", iconSize);
        estilizarBoton(btnTop3, "imagenes/Top 3.png", iconSize);
        estilizarBoton(btnAtras, "imagenes/Atras.png", iconSize);

        // Agregar componentes al panel inferior
        panelControles.add(btnVentasDelDia);
        panelControles.add(btnpedidosPorMesa);
        panelControles.add(cboxMesa);
        panelControles.add(btnTop3);
        panelControles.add(btnAtras);

        ventanaResumen.add(panelControles, BorderLayout.SOUTH);

        // --- Eventos de botones ---
        btnVentasDelDia.addActionListener(e -> cargarVentasDelDia());

        btnpedidosPorMesa.addActionListener(e ->
        {
            String mesaSeleccionada = (String) cboxMesa.getSelectedItem();
            if (mesaSeleccionada != null)
            {
                cargarPedidosPorMesa(mesaSeleccionada);
            }
        });

        btnTop3.addActionListener(e -> cargarTop3());
        btnAtras.addActionListener(e -> volverAlMenu());

        // --- Configuración final de la ventana ---
        setContentPane(ventanaResumen);
        setTitle("Resumen");
    }

    /**
     * Carga los nombres de las mesas desde la base de datos.
     * Los agrega al ComboBox de selección.
     */
    private void cargarMesasDesdeBD()
    {
        try
        {
            MesaDAO mesaDAO = new MesaDAO();
            List<Mesa> mesas = mesaDAO.listar();

            for (Mesa m : mesas)
            {
                cboxMesa.addItem("Mesa " + m.getIdMesa());
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar mesas: " + e.getMessage());
        }
    }

    /**
     * Consulta SQL para mostrar las ventas del día actual.
     * Agrupa los pedidos realizados hoy, mostrando por mesa y producto.
     */
    private void cargarVentasDelDia()
    {
        modelo.setRowCount(0); // limpiar tabla

        String sql = """
            SELECT m.idMesa, cp.nombre, SUM(p.cantidad) AS cantidad, 
                   SUM(cp.precio * p.cantidad) AS total
            FROM pedido p
            INNER JOIN cuenta c ON p.idCuenta = c.idCuenta
            INNER JOIN mesa m ON c.idMesa = m.idMesa
            INNER JOIN catalogoProducto cp ON p.idProducto = cp.idCatalogoProducto
            WHERE DATE(p.fechaHora) = CURDATE()
            GROUP BY m.idMesa, cp.nombre
            ORDER BY m.idMesa
        """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                modelo.addRow(new Object[]
                        {
                                "Mesa " + rs.getInt("idMesa"),
                                rs.getString("nombre"),
                                rs.getInt("cantidad"),
                                "$" + rs.getDouble("total")
                        });
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar ventas del día: " + e.getMessage());
        }
    }

    /**
     * Muestra los pedidos asociados a una mesa específica.
     * Se filtra por el id de la mesa seleccionada en el ComboBox.
     */
    private void cargarPedidosPorMesa(String mesa)
    {
        modelo.setRowCount(0);

        // Extraer número de mesa desde el texto "Mesa X"
        int idMesa = Integer.parseInt(mesa.replace("Mesa ", ""));

        String sql = """
            SELECT cp.nombre, p.cantidad, (cp.precio * p.cantidad) AS total
            FROM pedido p
            INNER JOIN cuenta c ON p.idCuenta = c.idCuenta
            INNER JOIN catalogoProducto cp ON p.idProducto = cp.idCatalogoProducto
            WHERE c.idMesa = ?
            ORDER BY p.fechaHora DESC
        """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, idMesa);
            ResultSet rs = ps.executeQuery();

            while (rs.next())
            {
                modelo.addRow(new Object[]
                        {
                                "Mesa " + idMesa,
                                rs.getString("nombre"),
                                rs.getInt("cantidad"),
                                "$" + rs.getDouble("total")
                        });
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar pedidos: " + e.getMessage());
        }
    }

    /**
     * Consulta SQL que obtiene el Top 3 de productos más vendidos.
     * Se agrupan las ventas por nombre del producto.
     */
    private void cargarTop3()
    {
        modelo.setRowCount(0);

        String sql = """
            SELECT cp.nombre, SUM(p.cantidad) AS cantidad, 
                   SUM(cp.precio * p.cantidad) AS total
            FROM pedido p
            INNER JOIN catalogoProducto cp ON p.idProducto = cp.idCatalogoProducto
            GROUP BY cp.nombre
            ORDER BY cantidad DESC
            LIMIT 3
        """;

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
        {
            while (rs.next())
            {
                modelo.addRow(new Object[]
                        {
                                "-",
                                rs.getString("nombre"),
                                rs.getInt("cantidad"),
                                "$" + rs.getDouble("total")
                        });
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar Top 3: " + e.getMessage());
        }
    }

    /**
     * Cierra la ventana actual y regresa al menú principal.
     */
    private void volverAlMenu()
    {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(ventanaResumen);
        topFrame.dispose();

        MenuPuntoVenta menu = new MenuPuntoVenta();
        menu.setContentPane(menu.JPMenuPrinc);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menu.setExtendedState(JFrame.MAXIMIZED_BOTH);
        menu.setUndecorated(true);
        menu.setVisible(true);

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = env.getMaximumWindowBounds();
        menu.setBounds(bounds);
    }

    /**
     * Aplica estilo visual a los botones (asigna ícono, elimina bordes, etc.).
     */
    private void estilizarBoton(JButton boton, String rutaIcono, int size)
    {
        ImageIcon iconoOriginal = new ImageIcon(rutaIcono);
        Image imagenEscalada = iconoOriginal.getImage()
                .getScaledInstance(size, size, Image.SCALE_SMOOTH);
        boton.setIcon(new ImageIcon(imagenEscalada));

        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
    }

    /**
     * Ajusta la ventana a los límites visibles del entorno gráfico.
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
            Resumen resumen = new Resumen();
            adaptarVentanaAResolucion(resumen);
            resumen.setUndecorated(true);
            resumen.setExtendedState(JFrame.MAXIMIZED_BOTH);
            resumen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            resumen.setVisible(true);
        });
    }
}
