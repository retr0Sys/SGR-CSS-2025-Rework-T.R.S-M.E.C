package com.css.restaurante.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/**
 * Genera iconos vectoriales programaticos para el sidebar.
 * Los iconos son transparentes y se adaptan al color del tema.
 */
public class IconFactory {

    /**
     * Genera un ImageIcon del modulo solicitado.
     * 
     * @param nombre nombre del modulo (mesas, carta, cocina, facturacion, resumen,
     *               acerca, tema)
     * @param size   tamano del icono en px
     * @param color  color del trazo
     */
    public static ImageIcon crear(String nombre, int size, Color color) {
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setColor(color);
        float s = size / 20f; // escala base 20

        switch (nombre) {
            case "mesas" -> dibujarMesas(g, s);
            case "carta" -> dibujarCarta(g, s);
            case "cocina" -> dibujarCocina(g, s);
            case "facturacion" -> dibujarFacturacion(g, s);
            case "resumen" -> dibujarResumen(g, s);
            case "acerca" -> dibujarAcerca(g, s);
            case "tema_sol" -> dibujarSol(g, s);
            case "tema_luna" -> dibujarLuna(g, s);
        }
        g.dispose();
        return new ImageIcon(img);
    }

    // Mesa: rectangulo con 4 patas
    private static void dibujarMesas(Graphics2D g, float s) {
        g.setStroke(new BasicStroke(1.6f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Tablero
        g.draw(new RoundRectangle2D.Float(3 * s, 6 * s, 14 * s, 2.5f * s, 1.5f * s, 1.5f * s));
        // Patas
        g.draw(new Line2D.Float(5 * s, 8.5f * s, 5 * s, 15 * s));
        g.draw(new Line2D.Float(15 * s, 8.5f * s, 15 * s, 15 * s));
        // Silla izquierda
        g.draw(new Line2D.Float(2 * s, 10 * s, 2 * s, 14 * s));
        g.draw(new Line2D.Float(2 * s, 10 * s, 5 * s, 10 * s));
        // Silla derecha
        g.draw(new Line2D.Float(18 * s, 10 * s, 18 * s, 14 * s));
        g.draw(new Line2D.Float(15 * s, 10 * s, 18 * s, 10 * s));
    }

    // Carta: libro abierto
    private static void dibujarCarta(Graphics2D g, float s) {
        g.setStroke(new BasicStroke(1.6f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Pagina izquierda
        g.draw(new Rectangle2D.Float(2 * s, 3 * s, 7 * s, 14 * s));
        // Pagina derecha
        g.draw(new Rectangle2D.Float(9 * s, 3 * s, 7 * s, 14 * s));
        // Lomo central
        g.draw(new Line2D.Float(9 * s, 3 * s, 9 * s, 17 * s));
        // Lineas de texto izquierda
        g.setStroke(new BasicStroke(1f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Float(4 * s, 6 * s, 7.5f * s, 6 * s));
        g.draw(new Line2D.Float(4 * s, 8.5f * s, 7.5f * s, 8.5f * s));
        g.draw(new Line2D.Float(4 * s, 11 * s, 7.5f * s, 11 * s));
        // Lineas de texto derecha
        g.draw(new Line2D.Float(11 * s, 6 * s, 14.5f * s, 6 * s));
        g.draw(new Line2D.Float(11 * s, 8.5f * s, 14.5f * s, 8.5f * s));
        g.draw(new Line2D.Float(11 * s, 11 * s, 14.5f * s, 11 * s));
    }

    // Cocina: llama
    private static void dibujarCocina(Graphics2D g, float s) {
        g.setStroke(new BasicStroke(1.6f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Llama externa
        GeneralPath flame = new GeneralPath();
        flame.moveTo(10 * s, 2 * s);
        flame.curveTo(10 * s, 2 * s, 16 * s, 8 * s, 14 * s, 13 * s);
        flame.curveTo(13 * s, 16 * s, 7 * s, 16 * s, 6 * s, 13 * s);
        flame.curveTo(4 * s, 8 * s, 10 * s, 2 * s, 10 * s, 2 * s);
        g.draw(flame);
        // Llama interna
        GeneralPath inner = new GeneralPath();
        inner.moveTo(10 * s, 7 * s);
        inner.curveTo(10 * s, 7 * s, 13 * s, 10 * s, 12 * s, 13 * s);
        inner.curveTo(11.5f * s, 15 * s, 8.5f * s, 15 * s, 8 * s, 13 * s);
        inner.curveTo(7 * s, 10 * s, 10 * s, 7 * s, 10 * s, 7 * s);
        g.draw(inner);
        // Base (hornalla)
        g.draw(new Line2D.Float(4 * s, 17 * s, 16 * s, 17 * s));
    }

    // Facturacion: documento con lineas
    private static void dibujarFacturacion(Graphics2D g, float s) {
        g.setStroke(new BasicStroke(1.6f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Hoja
        GeneralPath doc = new GeneralPath();
        doc.moveTo(4 * s, 2 * s);
        doc.lineTo(12 * s, 2 * s);
        doc.lineTo(16 * s, 6 * s);
        doc.lineTo(16 * s, 18 * s);
        doc.lineTo(4 * s, 18 * s);
        doc.closePath();
        g.draw(doc);
        // Doblez
        g.draw(new Line2D.Float(12 * s, 2 * s, 12 * s, 6 * s));
        g.draw(new Line2D.Float(12 * s, 6 * s, 16 * s, 6 * s));
        // Lineas de texto
        g.setStroke(new BasicStroke(1f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Float(6.5f * s, 9 * s, 13.5f * s, 9 * s));
        g.draw(new Line2D.Float(6.5f * s, 11.5f * s, 13.5f * s, 11.5f * s));
        g.draw(new Line2D.Float(6.5f * s, 14 * s, 11 * s, 14 * s));
    }

    // Resumen: grafico de barras
    private static void dibujarResumen(Graphics2D g, float s) {
        g.setStroke(new BasicStroke(1.6f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Barras
        float bw = 3 * s;
        g.fill(new Rectangle2D.Float(3 * s, 10 * s, bw, 7 * s));
        g.fill(new Rectangle2D.Float(8.5f * s, 5 * s, bw, 12 * s));
        g.fill(new Rectangle2D.Float(14 * s, 8 * s, bw, 9 * s));
        // Eje X
        g.draw(new Line2D.Float(2 * s, 17 * s, 18 * s, 17 * s));
    }

    // Acerca: circulo con i
    private static void dibujarAcerca(Graphics2D g, float s) {
        g.setStroke(new BasicStroke(1.6f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Ellipse2D.Float(3 * s, 3 * s, 14 * s, 14 * s));
        // Punto de la i
        g.fill(new Ellipse2D.Float(9 * s, 6 * s, 2 * s, 2 * s));
        // Cuerpo de la i
        g.setStroke(new BasicStroke(2f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Float(10 * s, 10 * s, 10 * s, 14.5f * s));
    }

    // Sol
    private static void dibujarSol(Graphics2D g, float s) {
        g.setStroke(new BasicStroke(1.5f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Ellipse2D.Float(6 * s, 6 * s, 8 * s, 8 * s));
        // Rayos
        float[][] rays = {
                { 10, 2, 10, 4 }, { 10, 16, 10, 18 },
                { 2, 10, 4, 10 }, { 16, 10, 18, 10 },
                { 4.5f, 4.5f, 6, 6 }, { 14, 14, 15.5f, 15.5f },
                { 15.5f, 4.5f, 14, 6 }, { 6, 14, 4.5f, 15.5f }
        };
        for (float[] r : rays) {
            g.draw(new Line2D.Float(r[0] * s, r[1] * s, r[2] * s, r[3] * s));
        }
    }

    // Luna
    private static void dibujarLuna(Graphics2D g, float s) {
        g.setStroke(new BasicStroke(1.5f * s, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        GeneralPath moon = new GeneralPath();
        // Arco externo
        Arc2D outer = new Arc2D.Float(4 * s, 3 * s, 12 * s, 14 * s, -90, 180, Arc2D.OPEN);
        moon.append(outer, false);
        // Arco interno (creciente)
        Arc2D inner = new Arc2D.Float(6 * s, 3 * s, 10 * s, 14 * s, 90, -180, Arc2D.OPEN);
        moon.append(inner, true);
        g.draw(moon);
    }
}
