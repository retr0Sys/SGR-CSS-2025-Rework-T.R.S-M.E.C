package com.css.restaurante.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Utilidad para estilizar tablas de forma consistente en todo el SGR.
 * Elimina la duplicación del método estilizarTabla() presente en 6 paneles.
 */
public final class TablaEstilizador {

    private TablaEstilizador() {
    }

    /**
     * Aplica el estilo estándar del SGR a una JTable.
     */
    public static void aplicar(JTable t) {
        t.setBackground(ThemeManager.bgCard());
        t.setForeground(ThemeManager.textPrimary());
        t.setGridColor(ThemeManager.border());
        t.setSelectionBackground(ThemeManager.accentSoft());
        t.setSelectionForeground(ThemeManager.accent());
        t.setRowHeight(34);
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.getTableHeader().setBackground(ThemeManager.bgDark());
        t.getTableHeader().setForeground(ThemeManager.textSecondary());
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        t.setShowHorizontalLines(true);
        t.setShowVerticalLines(false);
    }
}
