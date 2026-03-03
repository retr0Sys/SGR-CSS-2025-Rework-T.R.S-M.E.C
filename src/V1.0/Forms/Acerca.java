package Forms;

import javax.swing.*;
import java.awt.*;

// Ventana que muestra información sobre los creadores y la versión del sistema
public class Acerca extends JFrame
{
    public JPanel JPacerca;
    private JLabel lblCreadores;
    private JLabel lblVersion;
    private JLabel lblCopy;
    private JButton btnAtras;

    public Acerca()
    {
        // Estética general
        Color fondo = new Color(245, 245, 245);
        Color acento = new Color(255, 159, 101);
        Font fuenteTitulo = new Font("Segoe UI", Font.BOLD, 22);
        Font fuenteGeneral = new Font("Segoe UI", Font.PLAIN, 16);

        JPacerca = new JPanel();
        JPacerca.setBackground(fondo);
        JPacerca.setLayout(new BoxLayout(JPacerca, BoxLayout.Y_AXIS));
        JPacerca.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));

        // Etiquetas
        lblCreadores = new JLabel("Creado por: Ezequiel Costa, Thiago Sosa", SwingConstants.CENTER);
        lblCreadores.setFont(fuenteGeneral);
        lblCreadores.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCreadores.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        lblVersion = new JLabel("Versión: 1.0.0", SwingConstants.CENTER);
        lblVersion.setFont(fuenteGeneral);
        lblVersion.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblVersion.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        lblCopy = new JLabel("<html><div style='text-align: center;'>Software bajo licencia de CopyRight<br>Todos los derechos reservados<br></div></html>");
        lblCopy.setFont(fuenteGeneral);
        lblCopy.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCopy.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));

        // Botón Atrás
        btnAtras = new JButton();
        ImageIcon icono = new ImageIcon(
                new ImageIcon("imagenes/Atras.png")
                        .getImage()
                        .getScaledInstance(150, 150, Image.SCALE_SMOOTH)
        );
        btnAtras.setIcon(icono);
        btnAtras.setBorderPainted(false);
        btnAtras.setContentAreaFilled(false);
        btnAtras.setFocusPainted(false);
        btnAtras.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAtras.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnAtras.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseEntered(java.awt.event.MouseEvent evt)
            {
                btnAtras.setBorder(
                        BorderFactory.createMatteBorder(0, 0, 5, 0, new Color(100, 100, 100))
                );
            }

            public void mouseExited(java.awt.event.MouseEvent evt)
            {
                btnAtras.setBorder(BorderFactory.createEmptyBorder());
            }
        });

        btnAtras.addActionListener(e ->
        {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(JPacerca);
            topFrame.dispose();
        });

        // Agregar componentes
        JPacerca.add(lblCreadores);
        JPacerca.add(lblVersion);
        JPacerca.add(lblCopy);
        JPacerca.add(btnAtras);

        // Configuración de ventana
        setContentPane(JPacerca);
        setTitle("Acerca del sistema");
        pack();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(Acerca::new);
    }
}
