package vista;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** Pantalla 3: ficha de detalle del empleado (Datos / Historial). */
public class DlgDetalle extends JDialog {

    private final Empleado emp;
    private String accion = null;   // "EDITAR", "ELIMINAR" o null (solo volver)

    public DlgDetalle(Frame owner, Empleado e) {
        super(owner, "Detalle del empleado", true);
        this.emp = e;

        setSize(580, 470);
        setResizable(false);
        setLocationRelativeTo(owner);
        getContentPane().setBackground(FrmListado.FONDO_OSCURO);
        setLayout(new BorderLayout());

        add(crearPerfil(), BorderLayout.NORTH);
        add(crearTabs(), BorderLayout.CENTER);
        add(crearBotones(), BorderLayout.SOUTH);
    }

    // ---------- Encabezado: avatar + nombre + badge ----------
    private JPanel crearPerfil() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(22, 28, 10, 28));

        p.add(new Avatar(iniciales(emp.nombre)));

        JLabel lblNombre = new JLabel(emp.nombre);
        lblNombre.setForeground(Color.WHITE);
        lblNombre.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));

        JLabel lblRol = new JLabel(emp.rol);
        lblRol.setForeground(FrmListado.GRIS);

        FrmListado.Pill pill = new FrmListado.Pill(emp.estado, FrmListado.colorEstado(emp.estado));

        JPanel texto = new JPanel();
        texto.setOpaque(false);
        texto.setLayout(new BoxLayout(texto, BoxLayout.Y_AXIS));
        lblNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblRol.setAlignmentX(Component.LEFT_ALIGNMENT);
        pill.setAlignmentX(Component.LEFT_ALIGNMENT);
        texto.add(lblNombre);
        texto.add(Box.createVerticalStrut(2));
        texto.add(lblRol);
        texto.add(Box.createVerticalStrut(8));
        texto.add(pill);

        p.add(texto);
        return p;
    }

    // ---------- Pestañas ----------
    private JTabbedPane crearTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        tabs.addTab("Datos", crearDatos());
        tabs.addTab("Historial", crearHistorial());
        return tabs;
    }

    private JPanel crearDatos() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(new Color(0x111827));
        p.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        String[][] filas = {
            {"Legajo", emp.legajo},
            {"Correo electrónico", emp.correo},
            {"Rol / Puesto", emp.rol},
            {"Estado", emp.estado},
            {"Fecha de alta", emp.fecha}
        };

        for (int i = 0; i < filas.length; i++) {
            JLabel etiqueta = new JLabel(filas[i][0]);
            etiqueta.setForeground(FrmListado.GRIS);
            JLabel valor = new JLabel(filas[i][1]);
            valor.setForeground(Color.WHITE);
            valor.setFont(valor.getFont().deriveFont(Font.BOLD, 14f));

            GridBagConstraints gl = new GridBagConstraints();
            gl.gridx = 0; gl.gridy = i;
            gl.anchor = GridBagConstraints.WEST;
            gl.insets = new Insets(8, 0, 8, 30);
            p.add(etiqueta, gl);

            GridBagConstraints gv = new GridBagConstraints();
            gv.gridx = 1; gv.gridy = i;
            gv.weightx = 1;
            gv.anchor = GridBagConstraints.WEST;
            gv.insets = new Insets(8, 0, 8, 0);
            p.add(valor, gv);
        }
        // Empuja todo hacia arriba
        GridBagConstraints relleno = new GridBagConstraints();
        relleno.gridx = 0; relleno.gridy = filas.length; relleno.weighty = 1;
        p.add(Box.createGlue(), relleno);
        return p;
    }

    private JScrollPane crearHistorial() {
        DefaultTableModel m = new DefaultTableModel(new String[]{"Fecha", "Acción", "Usuario"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        m.addRow(new Object[]{emp.fecha, "Alta del empleado", "Agustin Benitez"});
        m.addRow(new Object[]{emp.fecha, "Asignación de rol: " + emp.rol, "Agustin Benitez"});
        m.addRow(new Object[]{emp.fecha, "Estado: " + emp.estado, "Sistema"});

        JTable t = new JTable(m);
        t.setRowHeight(32);
        t.setBackground(new Color(0x111827));
        t.setForeground(Color.WHITE);
        t.setGridColor(new Color(0x1F2937));
        t.setSelectionBackground(FrmListado.SEL);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(tb, v, false, false, r, c);
                setBackground(FrmListado.NEGRO);
                setForeground(Color.WHITE);
                setFont(getFont().deriveFont(Font.BOLD, 12f));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(null);
        sp.getViewport().setBackground(new Color(0x111827));
        return sp;
    }

    // ---------- Botones ----------
    private JPanel crearBotones() {
        JButton btnVolver = new JButton("← Volver");
        btnVolver.setFocusPainted(false);
        btnVolver.addActionListener(ev -> dispose());

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBackground(FrmListado.FILA);
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFocusPainted(false);
        btnEditar.addActionListener(ev -> { accion = "EDITAR"; dispose(); });

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(FrmListado.ROJO);
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(ev -> { accion = "ELIMINAR"; dispose(); });

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izq.setOpaque(false);
        izq.add(btnVolver);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        der.setOpaque(false);
        der.add(btnEditar);
        der.add(btnEliminar);

        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 28, 20, 28));
        p.add(izq, BorderLayout.WEST);
        p.add(der, BorderLayout.EAST);
        return p;
    }

    private static String iniciales(String nombre) {
        String[] partes = nombre.trim().split("\\s+");
        String r = partes[0].isEmpty() ? "?" : partes[0].substring(0, 1);
        if (partes.length > 1) r += partes[1].substring(0, 1);
        return r.toUpperCase();
    }

    /** Devuelve "EDITAR", "ELIMINAR" o null si solo se cerró la ventana. */
    public String getAccion() { return accion; }

    /** Avatar circular con las iniciales. */
    private static class Avatar extends JComponent {
        private final String iniciales;

        Avatar(String iniciales) {
            this.iniciales = iniciales;
            setPreferredSize(new Dimension(80, 80));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(FrmListado.FILA);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.setColor(Color.WHITE);
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(iniciales,
                    (getWidth() - fm.stringWidth(iniciales)) / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}
