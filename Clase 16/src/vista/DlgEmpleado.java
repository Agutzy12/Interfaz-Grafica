package vista;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;

/** Pantalla 2: formulario de alta / edición de empleado. */
public class DlgEmpleado extends JDialog {

    private static final SimpleDateFormat FMT = new SimpleDateFormat("dd/MM/yyyy");

    private final Empleado emp;
    private boolean guardado = false;

    private JTextField txtLegajo, txtNombre, txtCorreo;
    private JComboBox<String> cmbRol, cmbEstado;
    private JSpinner spnFecha;
    private JLabel lblError;

    /**
     * @param e            empleado a editar, o null para crear uno nuevo
     * @param legajoNuevo  legajo a asignar cuando e == null
     */
    public DlgEmpleado(Frame owner, Empleado e, String legajoNuevo) {
        super(owner, true);
        boolean nuevo = (e == null);
        emp = nuevo
                ? new Empleado(legajoNuevo, "", "", "Desarrollador Web", "PENDIENTE", FMT.format(new Date()))
                : e;

        setTitle(nuevo ? "Nuevo empleado" : "Editar empleado");
        setSize(480, 540);
        setResizable(false);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(FrmListado.FONDO_OSCURO);
        setLayout(new BorderLayout());

        // ----- Título -----
        JLabel lblTitulo = new JLabel(nuevo ? "Nuevo empleado" : "Editar empleado");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(22, 28, 6, 28));
        add(lblTitulo, BorderLayout.NORTH);

        // ----- Formulario -----
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));

        txtLegajo = campoTexto(emp.legajo);
        txtLegajo.setEnabled(false);               // el legajo no se edita
        txtNombre = campoTexto(emp.nombre);
        txtCorreo = campoTexto(emp.correo);

        cmbRol = new JComboBox<>(new String[]{
            "Desarrollador Web", "Diseñadora UI/UX", "Project Manager", "QA Analyst"});
        cmbRol.setEditable(true);                  // permite escribir otro puesto
        cmbRol.setSelectedItem(emp.rol);

        cmbEstado = new JComboBox<>(new String[]{"Activo", "Pendiente", "Inactivo"});
        for (int i = 0; i < cmbEstado.getItemCount(); i++) {
            if (cmbEstado.getItemAt(i).equalsIgnoreCase(emp.estado)) cmbEstado.setSelectedIndex(i);
        }

        spnFecha = new JSpinner(new SpinnerDateModel());
        spnFecha.setEditor(new JSpinner.DateEditor(spnFecha, "dd/MM/yyyy"));
        try {
            spnFecha.setValue(FMT.parse(emp.fecha));
        } catch (ParseException ex) {
            spnFecha.setValue(new Date());
        }

        agregarFila(form, 0, "Legajo", txtLegajo);
        agregarFila(form, 1, "Nombre y apellido", txtNombre);
        agregarFila(form, 2, "Correo electrónico", txtCorreo);
        agregarFila(form, 3, "Rol / Puesto", cmbRol);
        agregarFila(form, 4, "Estado", cmbEstado);
        agregarFila(form, 5, "Fecha de alta", spnFecha);

        lblError = new JLabel(" ");
        lblError.setForeground(FrmListado.ROJO);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 0, 0);
        form.add(lblError, gbc);

        add(form, BorderLayout.CENTER);

        // ----- Botones -----
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(FrmListado.FONDO_OSCURO);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.addActionListener(ev -> dispose());

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(FrmListado.FILA);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(btnGuardar.getFont().deriveFont(Font.BOLD));
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(ev -> guardar());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);
        botones.setBorder(BorderFactory.createEmptyBorder(10, 28, 22, 28));
        botones.add(btnCancelar);
        botones.add(btnGuardar);
        add(botones, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnGuardar);
    }

    private JTextField campoTexto(String valor) {
        JTextField t = new JTextField(valor, 20);
        t.setBackground(new Color(0x111827));
        t.setForeground(Color.WHITE);
        t.setDisabledTextColor(FrmListado.GRIS);
        t.setCaretColor(Color.WHITE);
        return t;
    }

    private void agregarFila(JPanel form, int fila, String etiqueta, JComponent campo) {
        JLabel l = new JLabel(etiqueta);
        l.setForeground(FrmListado.GRIS);

        GridBagConstraints gl = new GridBagConstraints();
        gl.gridx = 0; gl.gridy = fila;
        gl.anchor = GridBagConstraints.WEST;
        gl.insets = new Insets(8, 0, 8, 16);
        form.add(l, gl);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 1; gc.gridy = fila;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(8, 0, 8, 0);
        form.add(campo, gc);
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String rol = String.valueOf(cmbRol.getSelectedItem()).trim();

        if (nombre.isEmpty()) {
            error("El nombre y apellido es obligatorio.");
            txtNombre.requestFocus();
            return;
        }
        if (!correo.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            error("Ingresá un correo electrónico válido.");
            txtCorreo.requestFocus();
            return;
        }
        if (rol.isEmpty()) {
            error("El rol / puesto es obligatorio.");
            return;
        }

        emp.nombre = nombre;
        emp.correo = correo;
        emp.rol = rol;
        emp.estado = ((String) cmbEstado.getSelectedItem()).toUpperCase();
        emp.fecha = FMT.format((Date) spnFecha.getValue());
        guardado = true;
        dispose();
    }

    private void error(String mensaje) {
        lblError.setText(mensaje);
    }

    public boolean isGuardado() { return guardado; }

    public Empleado getEmpleado() { return emp; }
}
