package com.css.restaurante.ui;

import com.css.restaurante.dao.EmpleadoDAO;
import com.css.restaurante.modelo.CargoEmpleado;
import com.css.restaurante.modelo.Empleado;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Panel de gestión de Empleados del restaurante.
 * Permite listar, crear y eliminar empleados (Solo Administradores).
 */
public class PanelEmpleados extends JPanel implements MenuPuntoVenta.Refrescable {

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private JTable tabla;
    private DefaultTableModel modelo;

    private JTextField txtUsuario, txtNombre, txtApellido;
    private JPasswordField txtContrasena;
    private JComboBox<String> cbCargo, cbEstado;
    private JButton btnCrear, btnEliminar, btnAlternarEstado, btnCambiarContrasena;
    private int empleadoSeleccionadoId = -1;

    public PanelEmpleados() {
        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeManager.bgContent());
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // ====== Panel izquierdo: Tabla ======
        JPanel panelTabla = new JPanel(new BorderLayout(0, 10));
        panelTabla.setBackground(ThemeManager.bgContent());

        // Tabla
        modelo = new DefaultTableModel(
                new String[] { "ID", "Usuario", "Nombre", "Apellido", "Cargo", "Estado" }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        tabla = new JTable(modelo);
        estilizarTabla(tabla);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() >= 0) {
                cargarEmpleadoSeleccionado();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBackground(ThemeManager.bgCard());
        scroll.getViewport().setBackground(ThemeManager.bgCard());
        scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.border()));
        panelTabla.add(scroll, BorderLayout.CENTER);

        // ====== Panel derecho: Formulario ======
        JPanel panelForm = crearFormulario();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelTabla, panelForm);
        split.setDividerLocation(650);
        split.setDividerSize(4);
        split.setBackground(ThemeManager.bgContent());
        split.setBorder(null);
        add(split, BorderLayout.CENTER);

        cargarEmpleados();
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.bgCard());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.border()),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        panel.setPreferredSize(new Dimension(300, 0));

        JLabel titulo = new JLabel("Empleado");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(ThemeManager.textPrimary());
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        txtUsuario = agregarCampo(panel, "Usuario");
        
        panel.add(crearLabel("Contraseña"));
        txtContrasena = new JPasswordField();
        txtContrasena.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtContrasena.setAlignmentX(LEFT_ALIGNMENT);
        txtContrasena.setBackground(ThemeManager.inputBg());
        txtContrasena.setForeground(ThemeManager.textPrimary());
        txtContrasena.setCaretColor(ThemeManager.textPrimary());
        panel.add(txtContrasena);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        txtNombre = agregarCampo(panel, "Nombre");
        txtApellido = agregarCampo(panel, "Apellido");

        panel.add(crearLabel("Cargo"));
        cbCargo = new JComboBox<>(new String[] { "mesero", "cajero", "gerente" });
        cbCargo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cbCargo.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(cbCargo);
        panel.add(Box.createRigidArea(new Dimension(0, 12)));

        panel.add(crearLabel("Estado"));
        cbEstado = new JComboBox<>(new String[] { "Activo", "Inactivo" });
        cbEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cbEstado.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(cbEstado);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        btnCrear = crearBotonAccion("✚ Crear Empleado", ThemeManager.success());
        btnCrear.addActionListener(e -> crearEmpleado());
        panel.add(btnCrear);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        btnAlternarEstado = crearBotonAccion("⟲ Alternar Estado", ThemeManager.accent());
        btnAlternarEstado.setEnabled(false);
        btnAlternarEstado.addActionListener(e -> alternarEstadoEmpleado());
        panel.add(btnAlternarEstado);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        btnCambiarContrasena = crearBotonAccion("🔑 Cambiar Contraseña", ThemeManager.accentSoft());
        btnCambiarContrasena.setForeground(ThemeManager.textPrimary());
        btnCambiarContrasena.setEnabled(false);
        btnCambiarContrasena.addActionListener(e -> cambiarContrasenaEmpleado());
        panel.add(btnCambiarContrasena);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        btnEliminar = crearBotonAccion("🗑 Eliminar Empleado", ThemeManager.danger());
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(e -> eliminarEmpleado());
        panel.add(btnEliminar);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnLimpiar = crearBotonAccion("✕ Limpiar", ThemeManager.textMuted());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        panel.add(btnLimpiar);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JTextField agregarCampo(JPanel contenedor, String etiqueta) {
        contenedor.add(crearLabel(etiqueta));
        JTextField txt = new JTextField();
        txt.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txt.setAlignmentX(LEFT_ALIGNMENT);
        txt.setBackground(ThemeManager.inputBg());
        txt.setForeground(ThemeManager.textPrimary());
        txt.setCaretColor(ThemeManager.textPrimary());
        contenedor.add(txt);
        contenedor.add(Box.createRigidArea(new Dimension(0, 12)));
        return txt;
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(ThemeManager.textSecondary());
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    private JButton crearBotonAccion(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return btn;
    }

    private void estilizarTabla(JTable t) {
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

        t.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(CENTER);
                setForeground("Activo".equals(value) ? ThemeManager.success() : ThemeManager.danger());
                setBackground(isSelected ? ThemeManager.accentSoft() : ThemeManager.bgCard());
                return this;
            }
        });
    }

    private void cargarEmpleados() {
        try {
            List<Empleado> empleados = empleadoDAO.listar();
            modelo.setRowCount(0);
            for (Empleado e : empleados) {
                modelo.addRow(new Object[] {
                        e.getIdEmpleado(), e.getUsuario(),
                        e.getNombre(), e.getApellido(),
                        e.getCargo().getValor(),
                        e.isActivo() ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar empleados: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarEmpleadoSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row < 0) return;
        empleadoSeleccionadoId = (int) modelo.getValueAt(row, 0);
        txtUsuario.setText((String) modelo.getValueAt(row, 1));
        txtNombre.setText((String) modelo.getValueAt(row, 2));
        txtApellido.setText((String) modelo.getValueAt(row, 3));
        cbCargo.setSelectedItem(modelo.getValueAt(row, 4));
        cbEstado.setSelectedItem(modelo.getValueAt(row, 5));
        
        btnEliminar.setEnabled(true);
        btnAlternarEstado.setEnabled(true);
        btnCambiarContrasena.setEnabled(true);
    }

    private void crearEmpleado() {
        String usuario = InputValidator.sanitizar(txtUsuario.getText());
        String nombre = InputValidator.sanitizar(txtNombre.getText());
        String apellido = InputValidator.sanitizar(txtApellido.getText());
        char[] contrasena = txtContrasena.getPassword();
        
        if (usuario.isEmpty() || contrasena.length == 0 || nombre.isEmpty() || apellido.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos (incluyendo contraseña) son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Validación de duplicados (insensible a mayúsculas/minúsculas)
            Empleado existente = empleadoDAO.buscarPorUsuario(usuario);
            if (existente != null) {
                JOptionPane.showMessageDialog(this, "Ya existe un empleado con el usuario: " + usuario,
                        "Usuario Duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            CargoEmpleado cargo = CargoEmpleado.fromString((String) cbCargo.getSelectedItem());
            boolean activo = "Activo".equals(cbEstado.getSelectedItem());

            Empleado e = new Empleado(0, usuario, nombre, apellido, cargo, activo);
            empleadoDAO.crear(e, contrasena);
            
            JOptionPane.showMessageDialog(this, "Empleado creado exitosamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            limpiarFormulario();
            cargarEmpleados();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al crear empleado: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alternarEstadoEmpleado() {
        if (empleadoSeleccionadoId < 0) return;

        // Validar no desactivarse a sí mismo
        if (empleadoSeleccionadoId == SesionManager.getEmpleado().getIdEmpleado()) {
            JOptionPane.showMessageDialog(this, "No puede cambiar el estado de su propia cuenta mientras esté en uso.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean estadoActualEsActivo = "Activo".equals(cbEstado.getSelectedItem());
        boolean nuevoEstado = !estadoActualEsActivo;
        String accion = nuevoEstado ? "activar" : "desactivar";

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea " + accion + " este empleado?",
                "Confirmar Cambio de Estado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                empleadoDAO.cambiarEstado(empleadoSeleccionadoId, nuevoEstado);
                JOptionPane.showMessageDialog(this, "Estado del empleado actualizado exitosamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarEmpleados();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cambiar estado: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cambiarContrasenaEmpleado() {
        if (empleadoSeleccionadoId < 0) return;

        char[] nuevaContrasena = txtContrasena.getPassword();
        if (nuevaContrasena.length == 0) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese una nueva contraseña en el campo correspondiente.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea cambiar la contraseña de este empleado?",
                "Confirmar Cambio de Contraseña", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                empleadoDAO.cambiarContrasena(empleadoSeleccionadoId, nuevaContrasena);
                JOptionPane.showMessageDialog(this, "Contraseña actualizada exitosamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                txtContrasena.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cambiar contraseña: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Arrays.fill(nuevaContrasena, '\0');
        }
    }

    private void eliminarEmpleado() {
        if (empleadoSeleccionadoId < 0) return;

        // Validar no eliminarse a sí mismo
        if (empleadoSeleccionadoId == SesionManager.getEmpleado().getIdEmpleado()) {
            JOptionPane.showMessageDialog(this, "No puede eliminar su propia cuenta mientras esté en uso.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar este empleado?\nEsta acción no se puede deshacer.",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                empleadoDAO.eliminar(empleadoSeleccionadoId);
                JOptionPane.showMessageDialog(this, "Empleado eliminado exitosamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarEmpleados();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void limpiarFormulario() {
        txtUsuario.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtContrasena.setText("");
        cbCargo.setSelectedIndex(0);
        cbEstado.setSelectedIndex(0);
        empleadoSeleccionadoId = -1;
        btnEliminar.setEnabled(false);
        btnAlternarEstado.setEnabled(false);
        btnCambiarContrasena.setEnabled(false);
        tabla.clearSelection();
    }

    @Override
    public void refrescar() {
        cargarEmpleados();
    }
}
