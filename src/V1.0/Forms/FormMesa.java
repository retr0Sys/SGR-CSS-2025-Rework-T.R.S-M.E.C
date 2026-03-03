package Forms;

import Clases.concret.Cuenta;
import Clases.concret.Mesa;
import Clases.concret.Pedido;
import Clases.concret.Reserva;
import Clases.concret.Mesero;
import DAO.CuentaDAO;
import DAO.MesaDAO;
import DAO.PedidoDAO;
import DAO.ReservaDAO;
import DAO.MeseroDAO;

import com.toedter.calendar.JCalendar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.SQLException;
import java.util.List;

//prueba
import java.awt.event.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase principal del formulario de gestión de mesas del sistema de restaurante.
 *
 * Permite gestionar:
 * - Estados de las mesas (libre, ocupada, reservada, etc.).
 * - Asignación y desasignación de meseros.
 * - Apertura y cierre de cuentas.
 * - Pedidos y consumo por mesa.
 * - Registro y eliminación de reservas.
 *
 * Forma parte de la capa de presentación del sistema, conectándose con la capa DAO
 * para realizar operaciones sobre la base de datos.
 */
public class FormMesa extends JFrame {

    // Map que asocia cada mesa con el modelo de su tabla de pedidos
    private Map<Integer, DefaultTableModel> mesaPedidosMap = new HashMap<>();
    private int mesaSeleccionada = -1;

    public JPanel JPMesasIni;
    public  JComboBox<String> CBSelecEstado;
    private JComboBox<Integer> CBmesa;
    private JLabel lblEstado;
    private JTable TBPedidosMesa;
    private JButton cambiarEstadoButton;
    private JButton btnAtras;
    private JTabbedPane JPPedidos;
    private JPanel JPprincipal;
    private JSplitPane SplitPrincipal;
    private JPanel JParribaPri;
    private JPanel JPabajoPri;
    private JScrollPane ScrollAbajoPri;
    private JLabel lblPedidoMesa;
    private JPanel JPasignar;
    private JPanel JPpedidosRealizar;
    private JPanel JPReservas;
    private JLabel lblMesas;
    private JLabel lblMesa2;
    private JComboBox CBmesa2;
    private JComboBox CBMesero;
    private JLabel lblMesero;
    private JButton BtnAsignar;
    private JLabel lblMesa3;
    private JLabel lblOperacion;
    protected JTable TBProductosMesa;
    private JLabel lblSubtotal;
    JLabel txtSubtotal;
    private JButton BtnEnviar;
    private JLabel lblPedidosMesaPedidos;
    private JComboBox CBmesa3;
    private JLabel lblMesa4;
    private JLabel lblOperacionReserva;
    private JLabel lblFecha;
    private JLabel lblMesa;
    private JLabel lblSelecEstado;
    private JComboBox CBmesa4;
    private JButton btnAbrirCuenta;
    private JButton btnCerrarCuenta;
    private JButton btnEliminarProd;
    private JButton btnAñadirProd;
    private JLabel lblCuenta;
    private JPanel JPCalendario;
    private JButton añadirButton;
    private JButton eliminarButton;
    private JPanel JPTablaReservas;
    private JTable TBReservas;
    private JLabel lblNombre;
    private JTextField txtApellido;
    private JLabel lblApellido;
    private JTextField txtNombre;
    private JLabel lblMeseroAsignado;
    private JLabel lblNombreMesero;
    private JButton btnDesasignar;
    private JLabel lblEstadoCuenta;
    private JScrollPane JSPaneReservas;
    private JLabel lblEstadoAct;
    private JLabel lblCuentaActiva;
    private JScrollPane JscrollPedidos;
    private JTable TBCuentas;
    private MesaDAO mesaDAO = new MesaDAO();
    private PedidoDAO pedidoDAO = new PedidoDAO();
    private CuentaDAO cuentaDAO = new CuentaDAO();
    private ReservaDAO reservaDAO = new ReservaDAO();

    /**
     * Constructor principal del formulario.
     * Configura los componentes, estilos y eventos.
     * Carga las mesas, meseros, pedidos y reservas desde la base de datos.
     */
    public FormMesa()
    {


        // Botón para asignar mesero
        BtnAsignar.addActionListener(e ->
        {
            try
            {
                int idMesa = (int) CBmesa2.getSelectedItem();
                String seleccionado = (String) CBMesero.getSelectedItem();

                if (seleccionado == null || seleccionado.isEmpty())
                {
                    JOptionPane.showMessageDialog(this, "Debe seleccionar un mesero.", "Atención", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Extraer el ID del mesero desde el ComboBox (formato: "1 - Ana Pérez")
                int idMesero = Integer.parseInt(seleccionado.split(" - ")[0]);

                MesaDAO mesaDAO = new MesaDAO();
                mesaDAO.asignarMesero(idMesa, idMesero);

                // Mostrar el mesero asignado
                lblNombreMesero.setText(seleccionado.split(" - ")[1]);
                JOptionPane.showMessageDialog(this, "Mesero asignado correctamente.");
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(this, "Error al asignar mesero: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


// Botón para desasignar mesero
        btnDesasignar.addActionListener(e ->
        {
            try
            {
                int idMesa = (int) CBmesa2.getSelectedItem();

                MesaDAO mesaDAO = new MesaDAO();
                mesaDAO.desasignarMesero(idMesa);

                lblNombreMesero.setText("Sin asignar");
                JOptionPane.showMessageDialog(this, "Mesero desasignado correctamente.");
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(this, "Error al desasignar mesero: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        CBmesa2.addActionListener(e ->
        {
            try
            {
                int idMesa = (int) CBmesa2.getSelectedItem();
                MesaDAO mesaDAO = new MesaDAO();
                String mesero = mesaDAO.obtenerNombreMesero(idMesa);

                if (mesero != null)
                    lblNombreMesero.setText(mesero);
                else
                    lblNombreMesero.setText("Sin asignar");
            }
            catch (Exception ex)
            {
                lblNombreMesero.setText("Sin asignar");
            }
        });

        try
        {
            cargarReservasEnTabla();
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, "Error al cargar las reservas: " + e.getMessage());
        }

        setContentPane(JPMesasIni);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();

        // Estética general
        Color fondo = new Color(245, 245, 245);
        Color acento = new Color(255, 159, 101);
        Font fuenteTitulo = new Font("Segoe UI", Font.BOLD, 20);
        Font fuenteGeneral = new Font("Segoe UI", Font.PLAIN, 14);

        JPMesasIni.setBackground(fondo);
        JPMesasIni.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPabajoPri.setBackground(new Color(250, 250, 250));
        JPabajoPri.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Tablas estilizadas
        TBPedidosMesa.setFont(fuenteGeneral);
        TBPedidosMesa.setRowHeight(28);
        TBPedidosMesa.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        TBPedidosMesa.getTableHeader().setBackground(acento);
        TBPedidosMesa.getTableHeader().setForeground(Color.WHITE);
        TBPedidosMesa.setShowGrid(false);
        TBPedidosMesa.setIntercellSpacing(new Dimension(0, 0));
        TBPedidosMesa.setSelectionBackground(new Color(255, 224, 178)); // tono suave

        TBProductosMesa.setShowGrid(false);
        TBProductosMesa.setIntercellSpacing(new Dimension(0, 0));
        TBProductosMesa.setSelectionBackground(new Color(255, 224, 178));
        TBProductosMesa.setFont(fuenteGeneral);
        TBProductosMesa.setRowHeight(28);
        TBProductosMesa.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        TBProductosMesa.getTableHeader().setBackground(acento);
        TBProductosMesa.getTableHeader().setForeground(Color.WHITE);

        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblEstado.setForeground(new Color(80, 80, 80));
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);

        lblNombreMesero.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombreMesero.setForeground(new Color(80, 80, 80));
        lblNombreMesero.setHorizontalAlignment(SwingConstants.CENTER);

        lblEstado.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        lblEstadoCuenta.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        lblEstadoCuenta.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblEstadoCuenta.setOpaque(true);
        lblEstadoCuenta.setBackground(new Color(240, 240, 240));
        lblEstadoCuenta.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        lblEstadoCuenta.setHorizontalAlignment(SwingConstants.CENTER);

        TBReservas.setFont(fuenteGeneral);
        TBReservas.setRowHeight(28);
        TBReservas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        TBReservas.getTableHeader().setBackground(acento);
        TBReservas.getTableHeader().setForeground(Color.WHITE);

        lblMesas.setFont(fuenteTitulo);
        lblMesas.setHorizontalAlignment(SwingConstants.CENTER);
        lblMesas.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));



        lblEstadoAct.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCuentaActiva.setFont(new Font("Segoe UI", Font.BOLD, 18));


        lblPedidoMesa.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPedidoMesa.setForeground(new Color(80, 80, 80));
        lblPedidoMesa.setHorizontalAlignment(SwingConstants.LEFT);
        lblPedidoMesa.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        lblPedidoMesa.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));



        // Subtotal visual

        txtSubtotal.setForeground(new Color(124, 126, 124));
        txtSubtotal.setHorizontalAlignment(JTextField.CENTER);

        txtSubtotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txtSubtotal.setForeground(new Color(51, 102, 153));
        txtSubtotal.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        txtSubtotal.setBackground(new Color(255, 255, 255));

        // Combos estilizados
        CBmesa.setFont(fuenteGeneral);
        CBmesa.setBackground(Color.WHITE);
        CBmesa.setMaximumRowCount(6); // para evitar que se extienda demasiado
        CBmesa3.setFont(fuenteGeneral);
        CBSelecEstado.setFont(fuenteGeneral);
        CBmesa.setBorder(BorderFactory.createLineBorder(acento, 2));
        CBmesa3.setBorder(BorderFactory.createLineBorder(acento, 2));
        CBSelecEstado.setBorder(BorderFactory.createLineBorder(acento, 2));

        // Botones con cursor y sombra
        inicializarIconos();
        aplicarHover(btnAñadirProd);
        aplicarHover(btnEliminarProd);
        aplicarHover(btnAbrirCuenta);
        aplicarHover(btnCerrarCuenta);
        aplicarHover(añadirButton);
        aplicarHover(eliminarButton);
        aplicarHover(cambiarEstadoButton);


        btnAbrirCuenta.setFocusPainted(false);
        btnAbrirCuenta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        cambiarEstadoButton.setFocusPainted(false);
        cambiarEstadoButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        if (TBProductosMesa.getModel() == null || TBProductosMesa.getColumnCount() == 0) {
            TBProductosMesa.setModel(new DefaultTableModel(
                    new Object[]{"ID", "Producto", "Cantidad", "Subtotal"}, 0
            ));
        }

        // Inicializar subtotal
        if (txtSubtotal.getText().isEmpty()) {
            txtSubtotal.setText("0");
        }

        cargarMeseros();
        cargarMesas();
        cargarEstados();

        // Para el combo de mesa en el apartado principal
        CBmesa.addActionListener(e -> {
            if (CBmesa.getSelectedItem() != null) {
                int idMesa = (int) CBmesa.getSelectedItem();
                actualizarMesaSeleccionada(idMesa);
                actualizarEstadoCuenta(idMesa);
            }
        });

// Para el combo de mesa en el apartado de pedidos
        CBmesa3.addActionListener(e -> {
            if (CBmesa3.getSelectedItem() != null) {
                int idMesa = (int) CBmesa3.getSelectedItem();
                actualizarMesaSeleccionada(idMesa);
            }
        });


        //Acciones de los botones
        añadirButton.addActionListener(e -> guardarReserva());
        eliminarButton.addActionListener(e -> eliminarReserva());

        CBmesa.addActionListener(e -> mostrarDatosMesa());
        cambiarEstadoButton.addActionListener(e -> cambiarEstadoMesa());

        btnAbrirCuenta.addActionListener(e -> abrirCuenta());
        btnCerrarCuenta.addActionListener(e -> cerrarCuenta());

        btnAñadirProd.addActionListener(e -> abrirDialogoAgregarPedido());
        btnEliminarProd.addActionListener(e -> eliminarProductoSeleccionado());

        BtnEnviar.addActionListener(e -> enviarPedidos());

        btnAtras.addActionListener(e -> {
            // Cerrar la ventana actual
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(JPMesasIni);
            topFrame.dispose();

            // Crear y mostrar la ventana anterior en pantalla completa
            MenuPuntoVenta menu = new MenuPuntoVenta();
            menu.setContentPane(menu.JPMenuPrinc);
            menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            menu.setExtendedState(JFrame.MAXIMIZED_BOTH);
            menu.setVisible(true);
        });


// CONFIGURACIÓN DEL JCALENDAR
        JPCalendario.setLayout(new BorderLayout());
        JCalendar calendario = new JCalendar();
        PanelHora panelHora = new PanelHora(); // Clase interna que ya tenés

// Estilo visual al calendario
        calendario.setWeekOfYearVisible(false);
        calendario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        calendario.setBackground(Color.WHITE);
        calendario.setDecorationBackgroundColor(new Color(255, 159, 101)); // Encabezado
        calendario.setSundayForeground(new Color(220, 20, 60));
        calendario.setWeekdayForeground(new Color(80, 80, 80));
        calendario.setTodayButtonText("Hoy");
        calendario.setNullDateButtonText("Sin fecha");
        calendario.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JPCalendario.setBackground(new Color(245, 245, 245));
        JPCalendario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPCalendario.add(calendario, BorderLayout.CENTER);
        JPCalendario.add(panelHora, BorderLayout.SOUTH);
        JPCalendario.revalidate();
        JPCalendario.repaint();

    }
    /**
     * Sobrecarga del constructor que permite abrir el formulario
     * con una mesa específica seleccionada al iniciar.
     */
    public FormMesa(int idMesaSeleccionada) {
        this(); // inicializa componentes y carga combos

        try {
            // Selecciona en los tres ComboBox (si existen)
            seleccionarMesaEnCombo(CBmesa, idMesaSeleccionada);
            seleccionarMesaEnCombo(CBmesa2, idMesaSeleccionada);
            seleccionarMesaEnCombo(CBmesa3, idMesaSeleccionada);
            seleccionarMesaEnCombo(CBmesa4, idMesaSeleccionada);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al seleccionar la mesa: " + e.getMessage());
        }
    }


    /**
     * Selecciona una mesa en un combo box según su ID.
     */
    private void seleccionarMesaEnCombo(JComboBox<?> combo, int idMesa) {
        if (combo == null) return; // evita nullpointer si el combo no está en el form

        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            String texto = item.toString().toLowerCase();

            // Si los ítems son del tipo "Mesa 1", "Mesa 2", etc.
            if (texto.contains(String.valueOf(idMesa).toLowerCase())) {
                combo.setSelectedIndex(i);
                break;
            }

        }
    }


    //Efecto al pasar el mouse
    private void aplicarHover(JButton boton) {
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(100, 100, 100)));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBorder(BorderFactory.createEmptyBorder());
            }
        });
    }

    //Asepctos visaules para cargar íconos con sus configuraciones
    private void inicializarIconos() {
        btnAtras.setIcon(new ImageIcon(new ImageIcon("imagenes/Atras.png").getImage().getScaledInstance(170, 170, Image.SCALE_SMOOTH)));
        btnAtras.setBorderPainted(false);
        btnAtras.setContentAreaFilled(false);
        btnAtras.setFocusPainted(false);


        BtnAsignar.setIcon(new ImageIcon(new ImageIcon("imagenes/Asignar.png").getImage().getScaledInstance(170, 170, Image.SCALE_SMOOTH)));
        BtnAsignar.setBorderPainted(false);
        BtnAsignar.setContentAreaFilled(false);
        BtnAsignar.setFocusPainted(false);

        BtnEnviar.setIcon(new ImageIcon(new ImageIcon("imagenes/Enviar.png").getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH)));
        BtnEnviar.setBorderPainted(false);
        BtnEnviar.setContentAreaFilled(false);
        BtnEnviar.setFocusPainted(false);

        btnDesasignar.setIcon(new ImageIcon(new ImageIcon("imagenes/Designar.png").getImage().getScaledInstance(170, 170, Image.SCALE_SMOOTH)));
        btnDesasignar.setBorderPainted(false);
        btnDesasignar.setContentAreaFilled(false);
        btnDesasignar.setFocusPainted(false);
    }
    /**
     * Carga la lista de meseros desde la base de datos para poder asignarlos.
     */
    private void cargarMeseros()
    {
        CBMesero.removeAllItems();

        try
        {
            MeseroDAO meseroDAO = new MeseroDAO();
            List<Mesero> meseros = meseroDAO.listar();

            for (Mesero m : meseros)
            {
                CBMesero.addItem(m.getIdMesero() + " - " + m.getNombre() + " " + m.getApellido());
            }
        }
        catch (SQLException e)
        {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar los meseros: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Carga las mesas disponibles desde la base de datos.
     * Llena los distintos JComboBox con sus IDs.
     */
    private void cargarMesas() {
        try {
            List<Mesa> mesas = mesaDAO.listar();
            CBmesa.removeAllItems();
            CBmesa2.removeAllItems();
            CBmesa3.removeAllItems();
            CBmesa4.removeAllItems();
            for (Mesa m : mesas) {
                CBmesa.addItem(m.getIdMesa());
                CBmesa2.addItem(m.getIdMesa());
                CBmesa3.addItem(m.getIdMesa());
                CBmesa4.addItem(m.getIdMesa());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando mesas: " + e.getMessage());
        }
    }
    /**
     * Carga los estados posibles de una mesa (por ejemplo: libre, ocupada, reservada).
     */
    private void cargarEstados() {
        CBSelecEstado.addItem("Disponible");
        CBSelecEstado.addItem("Ocupada");
        CBSelecEstado.addItem("Reservada");
        CBSelecEstado.addItem("Limpieza");
    }
    //Métodos para mostrar los datos de la mesa por sus ID
    private void mostrarDatosMesa() {
        try {
            int idMesa = (int) CBmesa.getSelectedItem();
            Mesa m = mesaDAO.buscarPorId(idMesa);
            lblEstado.setText(m.getEstado());
            cargarPedidosMesa(idMesa);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error mostrando datos: " + e.getMessage());
        }
    }
    /**
     * Cambia el estado de la mesa seleccionada según la opción del combo.
     */

    private void cambiarEstadoMesa() {
        try {
            int idMesa = (int) CBmesa.getSelectedItem();
            String nuevoEstado = (String) CBSelecEstado.getSelectedItem();
            Mesa m = mesaDAO.buscarPorId(idMesa);
            m.setEstado(nuevoEstado);
            mesaDAO.actualizar(m);
            lblEstado.setText(nuevoEstado);
            JOptionPane.showMessageDialog(this, "Estado actualizado correctamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error actualizando estado: " + e.getMessage());
        }
    }
    private void cambiarEstadoMesaAutomatico(int idMesa, String nuevoEstado) {
        try {
            Mesa m = mesaDAO.buscarPorId(idMesa);
            if (m != null) {
                m.setEstado(nuevoEstado);
                mesaDAO.actualizar(m);
                lblEstado.setText(nuevoEstado);
                System.out.println("Mesa " + idMesa + " actualizada a estado: " + nuevoEstado);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar estado de la mesa: " + e.getMessage());
        }
    }
    /**
     * Abre una nueva cuenta para la mesa seleccionada.
     */
    private void abrirCuenta() {
        try {
            int idMesa = (int) CBmesa.getSelectedItem();

            if (cuentaDAO.tieneCuentaAbierta(idMesa)) {
                JOptionPane.showMessageDialog(this, "La mesa ya tiene una cuenta abierta.");
                return;
            }

            // Crear nueva cuenta
            Cuenta nuevaCuenta = new Cuenta(idMesa, 1);
            cuentaDAO.insertar(nuevaCuenta);

            // Cambiar estado automáticamente a "Ocupada"
            cambiarEstadoMesaAutomatico(idMesa, "Ocupada");

            JOptionPane.showMessageDialog(this, "Cuenta abierta para la mesa " + idMesa + ". Estado: Ocupada.");

            // Recargar tabla de pedidos (vacía al inicio)
            cargarPedidosMesa(idMesa);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al abrir cuenta: " + e.getMessage());
        }
    }

    /**
     * Cierra la cuenta activa de una mesa.
     */
    private void cerrarCuenta() {
        try {
            int idMesa = (int) CBmesa.getSelectedItem();

            if (!cuentaDAO.tieneCuentaAbierta(idMesa)) {
                JOptionPane.showMessageDialog(this, "No hay cuenta abierta para esta mesa.");
                return;
            }

            cuentaDAO.cerrarCuenta(idMesa);

            // Cambiar estado automáticamente a "Disponible"
            cambiarEstadoMesaAutomatico(idMesa, "Disponible");

            JOptionPane.showMessageDialog(this, "Cuenta cerrada. Mesa disponible nuevamente.");

            // Limpiar tabla de pedidos
            TBPedidosMesa.setModel(new DefaultTableModel(
                    new Object[]{"ID Pedido", "Producto", "Cantidad", "Fecha"}, 0
            ));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cerrar cuenta: " + e.getMessage());
        }
    }

    //Metodos para cargar pedidos en las mesas

    private void cargarPedidosMesa(int idMesa) {
        try {
            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Producto", "Cantidad", "Fecha"}, 0
            );

            int idCuenta = cuentaDAO.obtenerIdCuentaAbierta(idMesa);
            if (idCuenta == -1) {
                TBPedidosMesa.setModel(model);
                return;
            }

            // Pedidos con nombre y precio ya incluidos
            List<Pedido> pedidos = pedidoDAO.listarPorCuenta(idCuenta);

            for (Pedido p : pedidos) {
                model.addRow(new Object[]{
                        p.getNombreProducto(),
                        p.getCantidad(),
                        p.getFechaHora()
                });
            }

            TBPedidosMesa.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando pedidos: " + e.getMessage());
        }
    }


    //Metodo para agregar pedidos
    private void abrirDialogoAgregarPedido() {
        if (mesaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una mesa antes de agregar productos.");
            return;
        }

        DefaultTableModel model = mesaPedidosMap.get(mesaSeleccionada);
        if (model == null) {
            model = new DefaultTableModel(new String[]{"ID Producto", "Nombre", "Cantidad", "Subtotal"}, 0);
            mesaPedidosMap.put(mesaSeleccionada, model);
            TBProductosMesa.setModel(model);
        }

        // Aquí abrís tu diálogo de agregar pedido y agregás filas al model
        DialogoAgregarPedido dialog = new DialogoAgregarPedido(this, TBProductosMesa, txtSubtotal);
        dialog.setVisible(true);

        actualizarSubtotal(model);
    }

    // Metodo para enviar los pedidos a la BD
    private void enviarPedidos() {
        if (mesaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una mesa para enviar los pedidos.");
            return;
        }

        try {
            int idCuenta = cuentaDAO.obtenerIdCuentaAbierta(mesaSeleccionada);
            if (idCuenta == -1) {
                JOptionPane.showMessageDialog(this, "No hay cuenta abierta para esta mesa. Abra la cuenta primero.");
                return;
            }

            DefaultTableModel model = mesaPedidosMap.get(mesaSeleccionada);
            if (model == null || model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No hay productos para enviar.");
                return;
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                int idProducto = Integer.parseInt(model.getValueAt(i, 0).toString());
                int cantidad = Integer.parseInt(model.getValueAt(i, 2).toString());
                pedidoDAO.agregarPedido(idCuenta, idProducto, cantidad);
            }

            JOptionPane.showMessageDialog(this, "Pedidos enviados correctamente.");
            model.setRowCount(0);
            actualizarSubtotal(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al enviar los pedidos: " + e.getMessage());
        }
    }

    //Pantalla completa
    public static void mostrarPantallaCompleta(JFrame ventana) {
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximiza
        ventana.setUndecorated(true); // Quita bordes y barra de título
        ventana.setVisible(true); // Muestra la ventana
    }
    //Manejo de fecha y hora con Spinners
    public class PanelHora extends JPanel {

        public JSpinner spinnerHora;
        public JSpinner spinnerMinuto;

        public PanelHora() {
            setLayout(new GridBagLayout());
            setBackground(new Color(250, 250, 250));
            setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    "Seleccionar hora",
                    0, 0,
                    new Font("Segoe UI", Font.BOLD, 14),
                    new Color(100, 100, 100)
            ));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 10, 5, 10);
            gbc.anchor = GridBagConstraints.WEST;

            JLabel lblHora = new JLabel("Hora:");
            lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            gbc.gridx = 0;
            gbc.gridy = 0;
            add(lblHora, gbc);

            spinnerHora = new JSpinner(new SpinnerNumberModel(12, 0, 23, 1));
            spinnerHora.setPreferredSize(new Dimension(60, 25));
            spinnerHora.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            gbc.gridx = 1;
            add(spinnerHora, gbc);

            JLabel lblMinuto = new JLabel("Minutos:");
            lblMinuto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            gbc.gridx = 2;
            add(lblMinuto, gbc);

            spinnerMinuto = new JSpinner(new SpinnerNumberModel(30, 0, 59, 1));
            spinnerMinuto.setPreferredSize(new Dimension(60, 25));
            spinnerMinuto.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            gbc.gridx = 3;
            add(spinnerMinuto, gbc);
        }
    }
    /**
     * Guarda una nueva reserva para la mesa seleccionada.
     * Valida los datos del formulario antes de insertarlos.
     */
    private void guardarReserva() {
        try {
            if (CBmesa4.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una mesa válida.");
                return;
            }

            int idMesa = (int) CBmesa4.getSelectedItem();
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();

            // Buscar el calendario y el panel de hora dentro del contenedor
            JCalendar calendario = null;
            PanelHora panelHora = null;
            for (Component c : JPCalendario.getComponents()) {
                if (c instanceof JCalendar) calendario = (JCalendar) c;
                if (c instanceof PanelHora) panelHora = (PanelHora) c;
            }

            // Validación de campos obligatorios y formato de texto
            StringBuilder errores = new StringBuilder();
            if (nombre.isEmpty()) errores.append("- El nombre está vacío.\n");
            else if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+"))
                errores.append("- El nombre solo puede contener letras.\n");

            if (apellido.isEmpty()) errores.append("- El apellido está vacío.\n");
            else if (!apellido.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+"))
                errores.append("- El apellido solo puede contener letras.\n");

            if (calendario == null || calendario.getDate() == null) errores.append("- Debe seleccionar una fecha.\n");
            if (panelHora == null) errores.append("- No se pudo acceder al panel de hora.\n");

            if (errores.length() > 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Debe completar los datos correctamente:\n\n" + errores.toString(),
                        "Datos incompletos",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Obtener fecha y hora seleccionadas
            java.util.Date fecha = calendario.getDate();
            int hora = (int) panelHora.spinnerHora.getValue();
            int minuto = (int) panelHora.spinnerMinuto.getValue();

            // Validar que la fecha no sea anterior al día actual (solo fecha, sin hora)
            java.util.Calendar calActual = java.util.Calendar.getInstance();
            calActual.set(java.util.Calendar.HOUR_OF_DAY, 0);
            calActual.set(java.util.Calendar.MINUTE, 0);
            calActual.set(java.util.Calendar.SECOND, 0);
            calActual.set(java.util.Calendar.MILLISECOND, 0);
            java.util.Date hoy = calActual.getTime();

            java.util.Calendar calSeleccionada = java.util.Calendar.getInstance();
            calSeleccionada.setTime(fecha);
            calSeleccionada.set(java.util.Calendar.HOUR_OF_DAY, 0);
            calSeleccionada.set(java.util.Calendar.MINUTE, 0);
            calSeleccionada.set(java.util.Calendar.SECOND, 0);
            calSeleccionada.set(java.util.Calendar.MILLISECOND, 0);
            java.util.Date fechaSinHora = calSeleccionada.getTime();

            if (fechaSinHora.before(hoy)) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se puede registrar una reserva en una fecha anterior a la actual.",
                        "Fecha inválida",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Validar que la hora no sea anterior a la actual si la reserva es para hoy
            if (fechaSinHora.equals(hoy)) {
                java.util.Calendar ahora = java.util.Calendar.getInstance();
                int horaActual = ahora.get(java.util.Calendar.HOUR_OF_DAY);
                int minutoActual = ahora.get(java.util.Calendar.MINUTE);
                if (hora < horaActual || (hora == horaActual && minuto <= minutoActual)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "No se puede registrar una reserva en una hora pasada del día actual.",
                            "Hora inválida",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
            }

            Date fechaSQL = new Date(fecha.getTime());
            Time horaSQL = Time.valueOf(String.format("%02d:%02d:00", hora, minuto));

            // Verificar conflicto horario (45 minutos entre reservas en la misma mesa)
            List<Reserva> reservasExistentes = reservaDAO.obtenerPorMesaYFecha(idMesa, fechaSQL);
            long nuevaHoraMs = horaSQL.getTime();

            for (Reserva r : reservasExistentes) {
                long horaExistenteMs = r.getHora().getTime();
                long diferenciaMin = Math.abs(nuevaHoraMs - horaExistenteMs) / (60 * 1000);

                if (diferenciaMin < 45) {
                    JOptionPane.showMessageDialog(
                            this,
                            "No se puede registrar la reserva.\nDebe haber al menos 45 minutos de diferencia con otra reserva en la misma mesa.",
                            "Conflicto de horario",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
            }

            // Guardar la reserva
            Reserva reserva = new Reserva();
            reserva.setIdMesa(idMesa);
            reserva.setNombre(nombre);
            reserva.setApellido(apellido);
            reserva.setFecha(fechaSQL);
            reserva.setHora(horaSQL);

            reservaDAO.insertar(reserva);

            // Actualizar estado de la mesa a "Reservada"
            //MesaDAO mesaDAO = new MesaDAO();
            //mesaDAO.actualizarEstado(idMesa, "Reservada");

            JOptionPane.showMessageDialog(this, "Reserva guardada correctamente.");

            // Recargar tabla y limpiar campos
            cargarReservasEnTabla();
            limpiarCamposReserva();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar la reserva: " + e.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage());
        }
    }
    /**
     * Elimina la reserva seleccionada en la tabla de reservas.
     */
    private void eliminarReserva() {
        try {
            int filaSeleccionada = TBReservas.getSelectedRow();

            // Validación unificada (un solo mensaje si no hay selección)
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Debe seleccionar una reserva para eliminar.",
                        "Ninguna reserva seleccionada",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Confirmar antes de eliminar
            int confirmar = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de que desea eliminar la reserva seleccionada?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmar != JOptionPane.YES_OPTION) return;

            // Obtener el idReserva y el idMesa desde la tabla
            int idReserva = (int) TBReservas.getValueAt(filaSeleccionada, 0);
            int idMesa = (int) TBReservas.getValueAt(filaSeleccionada, 1); // Asegúrate que la columna 1 sea la mesa

            // Eliminar la reserva
            reservaDAO.eliminar(idReserva);

            // Cambiar el estado de la mesa a "Disponible"
            MesaDAO mesaDAO = new MesaDAO();
            mesaDAO.actualizarEstado(idMesa, "Disponible");

            JOptionPane.showMessageDialog(this, "Reserva eliminada correctamente.");
            cargarReservasEnTabla();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar la reserva: " + e.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error inesperado: " + ex.getMessage());
        }
    }
    //Metodo para cargar las reservas en la tabla correspondiente
    private void cargarReservasEnTabla() {
        try {
            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Mesa", "Nombre", "Apellido", "Fecha", "Hora"}, 0
            );
            TBReservas.setModel(model);

// Ocultar columna de ID
            TBReservas.getColumnModel().getColumn(0).setMinWidth(0);
            TBReservas.getColumnModel().getColumn(0).setMaxWidth(0);
            TBReservas.getColumnModel().getColumn(0).setWidth(0);

// Coloca la tabla dentro de un JScrollPane para que se vean los encabezados
            JScrollPane scroll = new JScrollPane(TBReservas);
            JPTablaReservas.removeAll(); // Limpiar cualquier contenido previo
            JPTablaReservas.setLayout(new BorderLayout());
            JPTablaReservas.add(scroll, BorderLayout.CENTER);
            JPTablaReservas.revalidate();
            JPTablaReservas.repaint();



            List<Reserva> reservas = reservaDAO.listar();

            for (Reserva r : reservas) {
                model.addRow(new Object[]{
                        r.getIdReserva(),
                        r.getIdMesa(),
                        r.getNombre(),
                        r.getApellido(),
                        r.getFecha(),
                        r.getHora()
                });
            }

            TBReservas.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error cargando reservas: " + e.getMessage());
        }
    }

    private void limpiarCamposReserva() {
        txtNombre.setText("");
        txtApellido.setText("");
    }
    //Metodo para cambiar la mesa seleccionada
    private void cambiarMesaSeleccionada() {
        if (CBmesa3.getSelectedItem() == null) return;

        int idMesa = Integer.parseInt(CBmesa3.getSelectedItem().toString());
        mesaSeleccionada = idMesa;

        // Buscar si la mesa ya tiene modelo de pedidos guardado
        DefaultTableModel model = mesaPedidosMap.get(idMesa);

        if (model == null) {
            // Crear uno nuevo si no existe
            model = new DefaultTableModel(new String[]{"ID Producto", "Nombre", "Cantidad", "Subtotal"}, 0);
            mesaPedidosMap.put(idMesa, model);
        }

        // Mostrar el modelo de esta mesa en la tabla
        TBProductosMesa.setModel(model);
    }
    //Metodo para actualizar el subtotal
    private void actualizarSubtotal(DefaultTableModel model) {
        double total = 0.0;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object value = model.getValueAt(i, 3);
            if (value != null) {
                total += Double.parseDouble(value.toString());
            }
        }
        txtSubtotal.setText(String.format("%.2f", total));
    }

    //Metodo para eliminar productos selecionados de la tabla
    private void eliminarProductoSeleccionado() {
        int filaSeleccionada = TBProductosMesa.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.");
            return;
        }

        // Confirmar antes de eliminar
        int opcion = JOptionPane.showOptionDialog(
                this,
                "¿Está seguro de que desea eliminar el producto seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, null, null
        );

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) TBProductosMesa.getModel();

        try {
            // Subtotal actual
            double subtotalActual = 0;
            String txtSubStr = txtSubtotal.getText().trim().replace(",", ".");
            if (!txtSubStr.isEmpty()) {
                subtotalActual = Double.parseDouble(txtSubStr);
            }

            // Subtotal del producto a eliminar
            Object valorSubtotalProd = model.getValueAt(filaSeleccionada, 3);
            double subtotalProd = 0;
            if (valorSubtotalProd != null) {
                String prodStr = valorSubtotalProd.toString().trim().replace(",", ".");
                if (!prodStr.isEmpty()) {
                    subtotalProd = Double.parseDouble(prodStr);
                }
            }

            // Nuevo subtotal
            double nuevoSubtotal = subtotalActual - subtotalProd;
            if (nuevoSubtotal < 0) nuevoSubtotal = 0;
            txtSubtotal.setText(String.format("%.2f", nuevoSubtotal));

            // Eliminar fila
            model.removeRow(filaSeleccionada);

            JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al calcular subtotal: " + e.getMessage());
        }
    }

    // Metodo para actualizar la mesa seleccionada
    private void actualizarMesaSeleccionada(int idMesa) {
        mesaSeleccionada = idMesa;

        // Cargar pedidos enviados desde la BD
        cargarPedidosMesa(idMesa);

        // Cargar productos temporales (aún no enviados) para la mesa
        DefaultTableModel modelProductos = mesaPedidosMap.get(idMesa);
        if (modelProductos == null) {
            modelProductos = new DefaultTableModel(new String[]{"ID Producto", "Nombre", "Cantidad", "Subtotal"}, 0);
            mesaPedidosMap.put(idMesa, modelProductos);
        }

        // Mostrar modelo de productos en la tabla
        TBProductosMesa.setModel(modelProductos);

        // Actualizar subtotal de los productos temporales
        actualizarSubtotal(modelProductos);
    }
    //Metodo para cambiar el estado de la mesa
    private void actualizarEstadoCuenta(int idMesa) {
        try {
            int idCuenta = cuentaDAO.obtenerIdCuentaAbierta(idMesa);

            if (idCuenta != -1) {
                lblEstadoCuenta.setText("Abierta");
                lblEstadoCuenta.setForeground(new java.awt.Color(0, 128, 0)); // verde
            } else {
                lblEstadoCuenta.setText("Cerrada");
                lblEstadoCuenta.setForeground(new java.awt.Color(200, 0, 0)); // rojo
            }

        } catch (SQLException e) {
            lblEstadoCuenta.setText("Error");
            lblEstadoCuenta.setForeground(new java.awt.Color(200, 0, 0));
            System.err.println("Error al consultar estado de cuenta: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FormMesa form = new FormMesa();

            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            gd.setFullScreenWindow(form); // Esto debe funcionar si no hay interferencias

            form.getRootPane().registerKeyboardAction(e -> {
                gd.setFullScreenWindow(null); // Sale del modo pantalla completa
                form.dispose(); // Cierra la ventana
            }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        });
    }
}