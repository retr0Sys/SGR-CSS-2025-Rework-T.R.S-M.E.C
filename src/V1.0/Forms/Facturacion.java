package Forms;
//imports
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import DAO.CuentaDAO;
import DAO.MesaDAO;
import DAO.PedidoDAO;
import DAO.ConexionDB;
import Clases.concret.Mesa;
import Clases.concret.Pedido;
import Exepciones.NoHayMesasValidasException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class Facturacion extends JFrame {
    public JPanel ventanaFact;
    private JComboBox<String> cboxMesa;
    private JLabel lblSubtotal;
    private JLabel lblSubtotalNumero;
    private JLabel lblDescuento;
    private JComboBox<String> cboxDescuento;
    private JLabel lblMetPago;
    private JComboBox<String> cboxMetPago;
    private JLabel lblTotal;
    private JLabel lblTotalNum;
    private JButton btnComprobante;
    private JLabel lblFact;
    private JLabel lblMesa;
    private JButton btnAtras;
    private double subtotal = 0;
    private static int contadorFactura = 1;


    public Facturacion() {
        // Obtener resolución de pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int anchoPantalla = screenSize.width;
        int iconSize = (int) (anchoPantalla * 0.10);

        // Estética general
        Color fondo = new Color(245, 245, 245);
        Color acento = new Color(255, 159, 101);
        Font fuenteTitulo = new Font("Segoe UI", Font.BOLD, 22);
        Font fuenteGeneral = new Font("Segoe UI", Font.PLAIN, 14);

        ventanaFact.setBackground(fondo);
        ventanaFact.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(acento, 3),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        // Botón atrás
        ImageIcon imagen = new ImageIcon(new ImageIcon("imagenes/Atras.png")
                .getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        btnAtras.setIcon(imagen);
        btnAtras.setBorderPainted(false);
        btnAtras.setContentAreaFilled(false);
        btnAtras.setFocusPainted(false);
        btnAtras.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAtras.setPreferredSize(new Dimension(iconSize, iconSize));
        btnAtras.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Botón comprobante
        ImageIcon imagen2 = new ImageIcon(new ImageIcon("imagenes/Comprobante.png")
                .getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH));
        btnComprobante.setIcon(imagen2);
        btnComprobante.setBorderPainted(false);
        btnComprobante.setContentAreaFilled(false);
        btnComprobante.setFocusPainted(false);
        btnComprobante.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnComprobante.setPreferredSize(new Dimension(iconSize, iconSize));
        btnComprobante.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        ImageIcon iconoFact = new ImageIcon(
                new ImageIcon("imagenes/LogoFactura.png")
                        .getImage()
                        .getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH)
        );
        lblFact.setIcon(iconoFact);

        // Etiquetas
        lblSubtotal.setFont(fuenteGeneral);
        lblSubtotalNumero.setFont(fuenteGeneral);
        lblDescuento.setFont(fuenteGeneral);
        lblMetPago.setFont(fuenteGeneral);
        lblTotal.setFont(fuenteGeneral);
        lblTotalNum.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalNum.setForeground(new Color(124, 126, 124));
        lblMesa.setFont(fuenteGeneral);

        // Combos
        cboxMesa.setFont(fuenteGeneral);
        cboxDescuento.setFont(fuenteGeneral);
        cboxMetPago.setFont(fuenteGeneral);

        cboxMesa.setBorder(BorderFactory.createLineBorder(acento, 2));
        cboxDescuento.setBorder(BorderFactory.createLineBorder(acento, 2));
        cboxMetPago.setBorder(BorderFactory.createLineBorder(acento, 2));

        // Cargar mesas desde BD
        try {
            cargarMesasDesdeBD();
        } catch (NoHayMesasValidasException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Sin mesas disponibles", JOptionPane.WARNING_MESSAGE);
        }

        // Cargar descuentos
        cboxDescuento.addItem("0%");
        cboxDescuento.addItem("5%");
        cboxDescuento.addItem("10%");
        cboxDescuento.addItem("15%");

        // Métodos de pago
        cboxMetPago.addItem("Efectivo");
        cboxMetPago.addItem("Tarjeta de débito");
        cboxMetPago.addItem("Tarjeta de crédito");
        cboxMetPago.addItem("Transferencia");

        lblSubtotalNumero.setText("$ " + subtotal);

        // Actualizar total cuando cambia el descuento
        cboxDescuento.addActionListener(e -> actualizarTotal());

        // Cargar pedidos al seleccionar mesa
        cboxMesa.addActionListener(e -> {
            if (cboxMesa.getSelectedItem() != null) {
                cargarPedidosDeMesa();
            }
        });

        // Botón generar comprobante
        btnComprobante.addActionListener(e -> {
            String mesa = (String) cboxMesa.getSelectedItem();
            String descuento = (String) cboxDescuento.getSelectedItem();
            String metodoPago = (String) cboxMetPago.getSelectedItem();
            String total = lblTotalNum.getText();

            if (mesa == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar una mesa antes de generar el comprobante.");
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "📋 Comprobante\n" +
                            "Mesa: " + mesa + "\n" +
                            "Subtotal: $" + subtotal + "\n" +
                            "Descuento: " + descuento + "\n" +
                            "Método de pago: " + metodoPago + "\n" +
                            "Total: " + total,
                    "Factura generada",
                    JOptionPane.INFORMATION_MESSAGE);

            crearPDF(mesa, descuento, metodoPago, total);

            try {
                int idMesa = Integer.parseInt(mesa.replace("Mesa ", "").trim());
                cerrarCuentaDeMesa(idMesa);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cerrar la cuenta: " + ex.getMessage());
                ex.printStackTrace();
            }

            try {
                cargarMesasDesdeBD();
            } catch (NoHayMesasValidasException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Sin mesas disponibles", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Botón atrás
        btnAtras.addActionListener(e -> {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(ventanaFact);
            if (topFrame != null) topFrame.dispose();

            MenuPuntoVenta menu = new MenuPuntoVenta();
            menu.setContentPane(menu.JPMenuPrinc);
            menu.setUndecorated(true);
            menu.pack();
            menu.setLocationRelativeTo(null);
            menu.setVisible(true);
            menu.setExtendedState(JFrame.MAXIMIZED_BOTH);
        });
    }

    // ====================== Cargar mesas desde BD ======================
    private void cargarMesasDesdeBD() throws NoHayMesasValidasException {
        try {
            MesaDAO mesaDAO = new MesaDAO();
            CuentaDAO cuentaDAO = new CuentaDAO();

            cboxMesa.removeAllItems();

            List<Mesa> mesas = mesaDAO.listar();
            boolean hayMesasValidas = false;

            for (Mesa mesa : mesas) {
                int idMesa = mesa.getIdMesa();
                if (cuentaDAO.tieneCuentaAbierta(idMesa)) {
                    int idCuenta = cuentaDAO.obtenerIdCuentaAbierta(idMesa);
                    double subtotalMesa = 0;
                    List<Pedido> pedidos = PedidoDAO.listarPorCuenta(idCuenta);

                    for (Pedido p : pedidos) {
                        double precio = obtenerPrecioProducto(p.getIdProducto());
                        subtotalMesa += precio * p.getCantidad();
                    }

                    if (subtotalMesa > 0) {
                        cboxMesa.addItem("Mesa " + idMesa);
                        hayMesasValidas = true;
                    }
                }
            }

            if (!hayMesasValidas) {
                throw new NoHayMesasValidasException();
            }

        } catch (NoHayMesasValidasException e) {
            throw e;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error cargando mesas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ====================== Cargar pedidos de la mesa seleccionada ======================
    private void cargarPedidosDeMesa() {
        try {
            String seleccion = (String) cboxMesa.getSelectedItem();
            if (seleccion == null || seleccion.isEmpty()) return;

            int idMesa = Integer.parseInt(seleccion.replace("Mesa ", "").trim());
            CuentaDAO cuentaDAO = new CuentaDAO();
            int idCuenta = cuentaDAO.obtenerIdCuentaAbierta(idMesa);

            if (idCuenta == -1) {
                JOptionPane.showMessageDialog(this, "La mesa no tiene una cuenta activa.");
                lblSubtotalNumero.setText("$ 0");
                lblTotalNum.setText("$ 0");
                subtotal = 0;
                return;
            }

            List<Pedido> pedidos = PedidoDAO.listarPorCuenta(idCuenta);
            double nuevoSubtotal = 0;
            for (Pedido p : pedidos) {
                double precio = obtenerPrecioProducto(p.getIdProducto());
                nuevoSubtotal += precio * p.getCantidad();
            }

            subtotal = nuevoSubtotal;
            lblSubtotalNumero.setText("$ " + subtotal);
            actualizarTotal();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error cargando pedidos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ====================== Obtener precio del producto ======================
    private double obtenerPrecioProducto(int idCatalogoProducto) {
        double precio = 0;
        try {
            String sql = "SELECT precio FROM catalogoproducto WHERE IdCatalogoProducto = ?";
            try (java.sql.Connection con = ConexionDB.getConnection();
                 java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idCatalogoProducto);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        precio = rs.getDouble("precio");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo precio: " + e.getMessage());
        }
        return precio;
    }

// ====================== Crear PDF ======================
    private void crearPDF(String mesa, String propina, String metodoPago, String total)
    {
        String ruta= "";
        PDDocument documento = null;
        PDPageContentStream contenido = null;
        try {
            // --- obtener idMesa de forma segura ---
            Object sel = cboxMesa.getSelectedItem();
            if (sel == null) {
                JOptionPane.showMessageDialog(this, "No hay ninguna mesa seleccionada para generar la factura.");
                return;
            }
            String seleccionMesa = sel.toString();
            // asumimos formato "Mesa X"
            int idMesa = Integer.parseInt(seleccionMesa.replace("Mesa ", "").trim());

            // --- preparar documento y página ---
            documento = new PDDocument();
            PDPage pagina = new PDPage(PDRectangle.LETTER);
            documento.addPage(pagina);
            contenido = new PDPageContentStream(documento, pagina);

            float anchoPagina = pagina.getMediaBox().getWidth();
            float altoPagina = pagina.getMediaBox().getHeight();
            float margenIzq = 50;
            float y = altoPagina - 50;

            // Barras laterales y logo
            contenido.setNonStrokingColor(new Color(240, 240, 255));
            contenido.addRect(20, 0, 10, altoPagina); contenido.fill();
            contenido.addRect(anchoPagina - 30, 0, 10, altoPagina); contenido.fill();

            PDImageXObject logo = PDImageXObject.createFromFile("imagenes/CSSLogoENPNG.png", documento);
            contenido.drawImage(logo, margenIzq, y - 110, 130, 130);

            // Información del local
            float infoX = anchoPagina - 200;
            float infoY = y - 10;
            String[] info = {"RestaurantesCSS.SA", "Uruguay - 1020",
                    "RUT: 22.343542.001-6", "Telf: 47642135", "Móvil: +59892533006"};
            contenido.setNonStrokingColor(Color.BLACK);
            contenido.setFont(PDType1Font.HELVETICA, 10);
            for (String linea : info) {
                contenido.beginText();
                contenido.newLineAtOffset(infoX, infoY);
                contenido.showText(linea);
                contenido.endText();
                infoY -= 12;
            }

            // e-Ticket
            float ticketY = y - 150;
            String ticket = "e - Ticket A - " + contadorFactura;
            contenido.beginText();
            contenido.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contenido.newLineAtOffset(margenIzq, ticketY);
            contenido.showText(ticket);
            contenido.endText();

            contenido.beginText();
            contenido.setFont(PDType1Font.HELVETICA, 10);
            contenido.newLineAtOffset(margenIzq, ticketY - 15);
            contenido.showText("moneda: UYU");
            contenido.endText();

            // Metodo de pago, fecha y mesero
            contenido.beginText();
            contenido.setFont(PDType1Font.HELVETICA, 10);
            contenido.newLineAtOffset(anchoPagina - 200, ticketY);
            contenido.showText("Método de pago: " + metodoPago);
            contenido.endText();

            String fechaHora = new SimpleDateFormat("dd/MM/yy HH:mm").format(new Date());
            contenido.beginText();
            contenido.setFont(PDType1Font.HELVETICA, 10);
            contenido.newLineAtOffset(anchoPagina - 200, ticketY - 15);
            contenido.showText("Fecha: " + fechaHora);
            contenido.endText();

            // Obtener y mostrar el mesero asignado
            String nombreMesero = obtenerNombreMeseroPorMesa(idMesa);
            contenido.beginText();
            contenido.setFont(PDType1Font.HELVETICA, 10);
            contenido.newLineAtOffset(anchoPagina - 200, ticketY - 30);
            contenido.showText("Mesero: " + nombreMesero);
            contenido.endText();

            // Línea divisoria
            y = ticketY - 35;
            contenido.setStrokingColor(new Color(180, 180, 220));
            contenido.setLineWidth(1f);
            contenido.moveTo(margenIzq, y);
            contenido.lineTo(anchoPagina - margenIzq, y);
            contenido.stroke();
            y -= 35;

            // Mesa (centrado)
            String mesaTexto = mesa;
            float mesaTextoWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(mesaTexto) / 1000 * 14;
            float mesaTextoX = (anchoPagina - mesaTextoWidth) / 2;
            contenido.setNonStrokingColor(new Color(220, 220, 240));
            contenido.addRect(mesaTextoX - 10, y - 5, mesaTextoWidth + 20, 20);
            contenido.fill();
            contenido.setNonStrokingColor(Color.BLACK);
            contenido.beginText();
            contenido.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contenido.newLineAtOffset(mesaTextoX, y);
            contenido.showText(mesaTexto);
            contenido.endText();

            // Línea divisoria debajo de mesa
            y -= 25;
            contenido.setStrokingColor(new Color(180, 180, 220));
            contenido.setLineWidth(1f);
            contenido.moveTo(margenIzq, y);
            contenido.lineTo(anchoPagina - margenIzq, y);
            contenido.stroke();
            y -= 25;

            // Título "Consumo Final"
            String titulo = "Consumo Final";
            float tituloWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(titulo) / 1000 * 14;
            float tituloX = (anchoPagina - tituloWidth) / 2;
            contenido.setNonStrokingColor(new Color(180, 180, 220));
            contenido.addRect(tituloX - 10, y - 5, tituloWidth + 20, 20);
            contenido.fill();
            contenido.setNonStrokingColor(Color.BLACK);
            contenido.beginText();
            contenido.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contenido.newLineAtOffset(tituloX, y);
            contenido.showText(titulo);
            contenido.endText();

            // Espacio entre título y tabla
            y -= 40;

            // Tabla de pedidos reales
            float rowHeight = 20;
            float[] colWidths = {200, 100, 100};
            float tableWidth = colWidths[0] + colWidths[1] + colWidths[2];
            float tableX = (anchoPagina - tableWidth) / 2;
            float tableY = y;

            // Obtener pedidos de la mesa (usando idMesa ya calculado)
            CuentaDAO cuentaDAO = new CuentaDAO();
            int idCuenta = cuentaDAO.obtenerIdCuentaAbierta(idMesa);
            List<Pedido> pedidos = PedidoDAO.listarPorCuenta(idCuenta);

            String[][] datos = new String[pedidos.size() + 1][3];
            datos[0] = new String[]{"Producto", "Cantidad", "Precio"};
            for (int i = 0; i < pedidos.size(); i++) {
                Pedido p = pedidos.get(i);
                String nombreProd = p.getNombreProducto();
                int cant = p.getCantidad();
                double precio = obtenerPrecioProducto(p.getIdProducto());
                datos[i + 1] = new String[]{nombreProd, String.valueOf(cant), String.format("$ %.2f", precio)};
            }

            // Dibujar tabla
            contenido.setStrokingColor(Color.BLACK);
            contenido.setLineWidth(1f);
            for (int i = 0; i < datos.length; i++) {
                for (int j = 0; j < colWidths.length; j++) {
                    float cellX = tableX + sum(colWidths, j);
                    float cellY = tableY - i * rowHeight;
                    contenido.addRect(cellX, cellY, colWidths[j], rowHeight);
                    contenido.beginText();
                    contenido.setFont(PDType1Font.HELVETICA, 10);
                    contenido.newLineAtOffset(cellX + 5, cellY + 5);
                    contenido.showText(datos[i][j]);
                    contenido.endText();
                }
            }
            contenido.stroke();

            // Línea divisoria debajo de tabla
            y = tableY - datos.length * rowHeight - 20;
            contenido.setStrokingColor(new Color(180, 180, 220));
            contenido.setLineWidth(1f);
            contenido.moveTo(margenIzq, y);
            contenido.lineTo(anchoPagina - margenIzq, y);
            contenido.stroke();
            y -= 20;

            // Facturación en dos columnas (descuento / IVA / total)
            String seleccion = (String) cboxDescuento.getSelectedItem();
            int porcentajeDescuento = 0;
            if (seleccion != null) {
                if (seleccion.equals("5%")) porcentajeDescuento = 5;
                else if (seleccion.equals("10%")) porcentajeDescuento = 10;
                else if (seleccion.equals("15%")) porcentajeDescuento = 15;
            }

            double descuento = subtotal * porcentajeDescuento / 100.0;
            double subtotalConDescuento = subtotal - descuento;
            double iva = subtotalConDescuento * 0.22;
            double totalFinal = subtotalConDescuento + iva;

            String[] etiquetas = {"Subtotal:", "Descuento (" + porcentajeDescuento + "%):", "IVA (22%):", "Total:"};
            String[] valores = {
                    String.format("$ %.2f", subtotal),
                    String.format("-$ %.2f", descuento),
                    String.format("$ %.2f", iva),
                    String.format("$ %.2f", totalFinal)
            };

            for (int i = 0; i < etiquetas.length; i++) {
                contenido.beginText();
                contenido.setFont(PDType1Font.HELVETICA, 12);
                contenido.newLineAtOffset(margenIzq, y);
                contenido.showText(etiquetas[i]);
                contenido.endText();

                contenido.beginText();
                contenido.newLineAtOffset(anchoPagina - margenIzq - 100, y);
                contenido.showText(valores[i]);
                contenido.endText();

                y -= 18;
            }

            // Línea divisoria debajo de facturación
            y -= 10;
            contenido.setStrokingColor(new Color(180, 180, 220));
            contenido.setLineWidth(1f);
            contenido.moveTo(margenIzq, y);
            contenido.lineTo(anchoPagina - margenIzq, y);
            contenido.stroke();
            y -= 30;

            // Bloque legal y agradecimiento
            String legal1 = "Documento legal aprobado y en regla por DGI";
            String legal2 = "N° Legal de 1000 a 3500 Vencimiento 31/12/2025";

            contenido.beginText();
            contenido.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
            float legal1Width = PDType1Font.HELVETICA_OBLIQUE.getStringWidth(legal1) / 1000 * 10;
            contenido.newLineAtOffset((anchoPagina - legal1Width) / 2, 60);
            contenido.showText(legal1);
            contenido.endText();

            contenido.beginText();
            contenido.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
            float legal2Width = PDType1Font.HELVETICA_OBLIQUE.getStringWidth(legal2) / 1000 * 10;
            contenido.newLineAtOffset((anchoPagina - legal2Width) / 2, 45);
            contenido.showText(legal2);
            contenido.endText();

            String agradecimiento = "Gracias por su preferencia, lo esperamos pronto";
            contenido.beginText();
            contenido.setFont(PDType1Font.HELVETICA_OBLIQUE, 12);
            float agradecimientoWidth = PDType1Font.HELVETICA_OBLIQUE.getStringWidth(agradecimiento) / 1000 * 12;
            contenido.newLineAtOffset((anchoPagina - agradecimientoWidth) / 2, 30);
            contenido.showText(agradecimiento);
            contenido.endText();

            // QR
            PDImageXObject qr = PDImageXObject.createFromFile("imagenes/qr.png", documento);
            float qrSize = 130;
            float qrX = anchoPagina - margenIzq - qrSize;
            contenido.drawImage(qr, qrX, 30, qrSize, qrSize);

            String ivaTexto = "IVA al día";
            contenido.beginText();
            contenido.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
            float ivaTextoWidth = PDType1Font.HELVETICA_OBLIQUE.getStringWidth(ivaTexto) / 1000 * 10;
            contenido.newLineAtOffset(qrX + (qrSize - ivaTextoWidth) / 2, 20);
            contenido.showText(ivaTexto);
            contenido.endText();

            // cerrar stream antes de guardar
            contenido.close();
            contenido = null;

            // asegurar que la carpeta exista
            java.io.File dir = new java.io.File("src/ProgramaPrincipal/Facturas");
            if (!dir.exists()) dir.mkdirs();

            // guardar documento
            ruta = "src/ProgramaPrincipal/Facturas/e-Ticket_A_N°_" + contadorFactura + ".pdf";
            documento.save(ruta);
            documento.close();
            documento = null;
            contadorFactura++;

            // informar al usuario
            JOptionPane.showMessageDialog(this, "Factura generada: " + ruta);

        } catch (Exception x) {
            // imprimir traza completa para debugging
            x.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar PDF: " + x.getMessage());
        } finally {
            try {
                if (contenido != null) contenido.close();
            } catch (Exception ignored) {}
            try {
                if (documento != null) documento.close();
            } catch (Exception ignored) {}
        }

        // Abre el PDF
        try {
            java.io.File archivoPDF = new java.io.File(ruta);
            if (archivoPDF.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(archivoPDF);
                } else {
                    JOptionPane.showMessageDialog(this, "La apertura automática no es compatible en este sistema.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró el archivo PDF para abrirlo.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al intentar abrir el PDF: " + e.getMessage());
        }
    }


    private float sum(float[] arr, int hasta) {
        float total = 0;
        for (int i = 0; i < hasta; i++) total += arr[i];
        return total;
    }
    //Operaciones con los valores obtenidos.
    // ====================== Actualizar total con descuento e IVA ======================
    private void actualizarTotal() {
        String seleccion = (String) cboxDescuento.getSelectedItem();
        int porcentajeDescuento = 0;

        if (seleccion != null) {
            if (seleccion.equals("5%")) porcentajeDescuento = 5;
            else if (seleccion.equals("10%")) porcentajeDescuento = 10;
            else if (seleccion.equals("15%")) porcentajeDescuento = 15;
        }

        double descuento = subtotal * porcentajeDescuento / 100.0;
        double subtotalConDescuento = subtotal - descuento;
        double iva = subtotalConDescuento * 0.22; // IVA del 22%
        double total = subtotalConDescuento + iva;

        // Mostrar en pantalla
        lblTotalNum.setText(String.format("$ %.2f", total));
    }
    // ====================== Obtener nombre del mesero asignado ======================
    private String obtenerNombreMeseroPorMesa(int idMesa) {
        String nombre = "Desconocido";
        try {
            String sql = "SELECT CONCAT(mesero.nombre, ' ', mesero.apellido) AS nombreCompleto " +
                    "FROM mesa " +
                    "JOIN mesero ON mesa.idMesero = mesero.idMesero " +
                    "WHERE mesa.idMesa = ?";
            try (java.sql.Connection con = ConexionDB.getConnection();
                 java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idMesa);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        nombre = rs.getString("nombreCompleto");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error obteniendo mesero: " + e.getMessage());
        }
        return nombre;
    }

    private void cerrarCuentaDeMesa(int idMesa) {
        try {
            CuentaDAO cuentaDAO = new CuentaDAO();
            MesaDAO mesaDAO = new MesaDAO();

            int idCuenta = cuentaDAO.obtenerIdCuentaAbierta(idMesa);

            if (idCuenta != -1) {
                // Cierra la cuenta en la BD
                cuentaDAO.cerrarCuenta(idMesa);

                // Cambia el estado de la mesa a "Disponible"
                Mesa mesa = mesaDAO.buscarPorId(idMesa);
                if (mesa != null) {
                    mesa.setEstado("Disponible");
                    mesaDAO.actualizar(mesa);
                    System.out.println("Mesa " + idMesa + " actualizada a estado: Disponible");
                }

                JOptionPane.showMessageDialog(this,
                        "Cuenta de la mesa " + idMesa + " cerrada y mesa marcada como disponible.");

            } else {
                JOptionPane.showMessageDialog(this,
                        "⚠️ No se encontró una cuenta abierta para la mesa " + idMesa);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cerrar cuenta: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // ====================== Cerrar cuenta en la BD ======================



    public static void adaptarVentanaAResolucion(JFrame ventana) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = env.getMaximumWindowBounds(); // área útil sin superponer barra de tareas

        ventana.setBounds(bounds); // adapta tamaño
        ventana.setLocation(bounds.x, bounds.y); // asegura posición correcta
    }

    public static void main(String[] args) {
        Facturacion ventana = new Facturacion();
        adaptarVentanaAResolucion(ventana);
        ventana.setContentPane(ventana.ventanaFact);
        ventana.setUndecorated(true); //  sin bordes
        ventana.setLocationRelativeTo(null); // centrado
        ventana.setVisible(true);     // mostrar primero
        ventana.setExtendedState(JFrame.MAXIMIZED_BOTH); // luego maximizar
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
