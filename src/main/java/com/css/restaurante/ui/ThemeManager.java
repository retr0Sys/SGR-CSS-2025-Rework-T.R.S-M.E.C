package com.css.restaurante.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Gestiona el tema visual de la aplicación (Claro/Oscuro).
 * Persiste la preferencia del usuario entre sesiones con java.util.prefs.
 * Todos los colores del sistema se obtienen desde aquí.
 */
public class ThemeManager {

    private static boolean darkMode;
    private static final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    private static final String PREF_KEY = "sgr_dark_mode";
    private static final List<Runnable> listeners = new ArrayList<>();

    // Inicializar tema desde preferencias guardadas
    static {
        darkMode = prefs.getBoolean(PREF_KEY, true); // Oscuro por defecto
    }

    public static boolean isDark() {
        return darkMode;
    }

    /**
     * Alterna entre tema claro y oscuro.
     * Actualiza el LAF globalmente y notifica a todos los listeners.
     */
    public static void toggle() {
        darkMode = !darkMode;
        prefs.putBoolean(PREF_KEY, darkMode);
        aplicarTema();
        notificarListeners();
    }

    /**
     * Aplica el tema actual como Look and Feel global.
     */
    public static void aplicarTema() {
        try {
            if (darkMode) {
                FlatDarkLaf.setup();
            } else {
                FlatLightLaf.setup();
            }
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("TitlePane.unifiedBackground", true);

            // Actualizar todas las ventanas abiertas
            for (Window w : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(w);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Registra un listener que será notificado cuando el tema cambie.
     */
    public static void addThemeListener(Runnable listener) {
        listeners.add(listener);
    }

    private static void notificarListeners() {
        for (Runnable r : listeners) {
            SwingUtilities.invokeLater(r);
        }
    }

    // ══════════════════════════════════════════
    // PALETA DE COLORES DINÁMICA
    // ══════════════════════════════════════════

    public static Color bgDark() {
        return darkMode ? new Color(18, 18, 24) : new Color(245, 245, 250);
    }

    public static Color bgSidebar() {
        return darkMode ? new Color(24, 24, 34) : new Color(255, 255, 255);
    }

    public static Color bgCard() {
        return darkMode ? new Color(30, 30, 42) : new Color(255, 255, 255);
    }

    public static Color bgContent() {
        return darkMode ? new Color(22, 22, 30) : new Color(240, 242, 247);
    }

    public static Color accent() {
        return new Color(215, 155, 0);
        /* Dorado refinado, ambos temas */ }

    public static Color accentSoft() {
        return darkMode ? new Color(215, 155, 0, 25) : new Color(215, 155, 0, 20);
    }

    public static Color textPrimary() {
        return darkMode ? new Color(240, 240, 245) : new Color(28, 28, 36);
    }

    public static Color textSecondary() {
        return darkMode ? new Color(160, 160, 175) : new Color(100, 100, 120);
    }

    public static Color textMuted() {
        return darkMode ? new Color(100, 100, 115) : new Color(150, 150, 170);
    }

    public static Color border() {
        return darkMode ? new Color(50, 50, 65) : new Color(220, 222, 230);
    }

    public static Color success() {
        return new Color(56, 161, 105);
    }

    public static Color danger() {
        return new Color(229, 62, 62);
    }

    public static Color warning() {
        return new Color(236, 178, 46);
    }

    public static Color info() {
        return new Color(49, 130, 206);
    }

    public static Color hoverBg() {
        return darkMode ? new Color(40, 40, 55) : new Color(235, 237, 245);
    }

    public static Color inputBg() {
        return darkMode ? new Color(36, 36, 50) : new Color(250, 250, 255);
    }

    /** Color del texto del botón principal (sobre fondo accent) */
    public static Color accentText() {
        return new Color(18, 18, 24);
    }

    /** Texto del icono del tema */
    public static String themeIcon() {
        return darkMode ? "☀️" : "🌙";
    }

    public static String themeLabel() {
        return darkMode ? "Modo Claro" : "Modo Oscuro";
    }
}
