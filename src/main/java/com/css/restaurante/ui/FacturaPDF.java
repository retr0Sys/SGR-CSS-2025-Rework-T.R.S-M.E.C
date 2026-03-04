package com.css.restaurante.ui;

import com.css.restaurante.modelo.Pedido;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Genera facturas PDF minimalistas estilo ticket.
 * Sin imagenes, solo texto limpio y separadores.
 * Guarda en Documentos/Facturas.
 */
public class FacturaPDF {

    private static final float MARGIN = 50;

    @SuppressWarnings("deprecation")
    public static String generar(int idMesa, List<Pedido> pedidos,
            double subtotal, double descuento,
            double iva, double total,
            String metodoPago) throws Exception {

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        PDPageContentStream cs = new PDPageContentStream(doc, page);
        float pw = page.getMediaBox().getWidth();
        float y = page.getMediaBox().getHeight() - MARGIN;

        PDType1Font bold = PDType1Font.HELVETICA_BOLD;
        PDType1Font normal = PDType1Font.HELVETICA;
        PDType1Font italic = PDType1Font.HELVETICA_OBLIQUE;

        String fecha = new SimpleDateFormat("dd/MM/yyyy  HH:mm").format(new Date());
        String nro = "FAC-" + idMesa + "-" + System.currentTimeMillis() % 100000;

        // ── Encabezado ──
        centrar(cs, bold, 16, "RESTAURANTE CSS", pw, y);
        y -= 16;
        centrar(cs, normal, 9, "Sistema de Gestion de Restaurantes", pw, y);
        y -= 20;
        linea(cs, MARGIN, pw - MARGIN, y);
        y -= 14;

        // ── Datos factura ──
        texto(cs, normal, 9, "N: " + nro, MARGIN, y);
        texto(cs, normal, 9, fecha, pw - MARGIN - 100, y);
        y -= 13;
        texto(cs, normal, 9, "Mesa: " + idMesa, MARGIN, y);
        texto(cs, normal, 9, "Pago: " + metodoPago, pw - MARGIN - 100, y);
        y -= 13;
        texto(cs, normal, 9, "Moneda: UYU", MARGIN, y);
        y -= 15;

        linea(cs, MARGIN, pw - MARGIN, y);
        y -= 13;
        centrar(cs, bold, 9, "CONSUMO FINAL", pw, y);
        y -= 8;
        linea(cs, MARGIN, pw - MARGIN, y);
        y -= 16;

        // ── Tabla header ──
        float c1 = MARGIN, c2 = MARGIN + 220, c3 = MARGIN + 290, c4 = pw - MARGIN - 50;
        texto(cs, bold, 9, "PRODUCTO", c1, y);
        texto(cs, bold, 9, "CANT", c2, y);
        texto(cs, bold, 9, "P.UN", c3, y);
        texto(cs, bold, 9, "TOTAL", c4, y);
        y -= 5;
        linea(cs, MARGIN, pw - MARGIN, y);
        y -= 14;

        // ── Filas ──
        for (Pedido p : pedidos) {
            if ("Cancelado".equals(p.getEstado().getValor()))
                continue;
            String nom = p.getNombreProducto();
            if (nom != null && nom.length() > 28)
                nom = nom.substring(0, 25) + "...";
            texto(cs, normal, 9, nom != null ? nom : "", c1, y);
            texto(cs, normal, 9, String.valueOf(p.getCantidad()), c2, y);
            texto(cs, normal, 9, fmt(p.getPrecioProducto()), c3, y);
            texto(cs, normal, 9, fmt(p.getSubtotal()), c4, y);
            y -= 15;
        }

        y -= 3;
        linea(cs, MARGIN, pw - MARGIN, y);
        y -= 16;

        // ── Totales ──
        par(cs, normal, 10, "Subtotal:", fmt(subtotal), MARGIN, c4, y);
        y -= 15;
        if (descuento > 0) {
            par(cs, normal, 10, "Descuento:", "-" + fmt(descuento), MARGIN, c4, y);
            y -= 15;
        }
        double neto = subtotal - descuento;
        par(cs, normal, 10, "Neto Gravado 22%:", fmt(neto), MARGIN, c4, y);
        y -= 15;
        par(cs, normal, 10, "IVA 22%:", fmt(iva), MARGIN, c4, y);
        y -= 8;
        linea(cs, MARGIN, pw - MARGIN, y);
        y -= 18;

        par(cs, bold, 13, "TOTAL:", "UYU " + fmt(total), MARGIN, c4 - 20, y);
        y -= 28;

        // ── Cierre ──
        linea(cs, MARGIN, pw - MARGIN, y);
        y -= 16;
        centrar(cs, italic, 10, "Gracias por su visita!", pw, y);
        y -= 20;
        linea(cs, MARGIN, pw - MARGIN, y);
        y -= 12;
        centrar(cs, bold, 8, "Adenda", pw, y);
        y -= 8;
        linea(cs, MARGIN, pw - MARGIN, y);
        y -= 14;

        texto(cs, normal, 8, "MESA: " + idMesa + "     FOLIO: " + nro + "     HORA: "
                + new SimpleDateFormat("HH:mm:ss").format(new Date()), MARGIN, y);
        y -= 12;
        texto(cs, normal, 8, "Fin Adenda", MARGIN, y);

        cs.close();

        // ── Guardar en Documentos/Facturas ──
        String dir = Paths.get(System.getProperty("user.home"), "Documents", "Facturas").toString();
        new File(dir).mkdirs();
        String path = Paths.get(dir, "Factura_Mesa" + idMesa + "_"
                + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf").toString();
        doc.save(path);
        doc.close();
        return path;
    }

    // ── Helpers compactos ──

    @SuppressWarnings("deprecation")
    private static void centrar(PDPageContentStream cs, PDType1Font f, float sz, String t, float pw, float y)
            throws Exception {
        float w = f.getStringWidth(t) / 1000f * sz;
        cs.setNonStrokingColor(0, 0, 0);
        cs.beginText();
        cs.setFont(f, sz);
        cs.newLineAtOffset((pw - w) / 2, y);
        cs.showText(t);
        cs.endText();
    }

    private static void texto(PDPageContentStream cs, PDType1Font f, float sz, String t, float x, float y)
            throws Exception {
        cs.setNonStrokingColor(0, 0, 0);
        cs.beginText();
        cs.setFont(f, sz);
        cs.newLineAtOffset(x, y);
        cs.showText(t);
        cs.endText();
    }

    private static void par(PDPageContentStream cs, PDType1Font f, float sz, String lbl, String val, float lx, float vx,
            float y) throws Exception {
        texto(cs, f, sz, lbl, lx, y);
        texto(cs, f, sz, val, vx, y);
    }

    private static void linea(PDPageContentStream cs, float x1, float x2, float y) throws Exception {
        cs.setStrokingColor(180, 180, 180);
        cs.setLineWidth(0.5f);
        cs.moveTo(x1, y);
        cs.lineTo(x2, y);
        cs.stroke();
    }

    private static String fmt(double v) {
        return String.format("%.0f", v);
    }
}
