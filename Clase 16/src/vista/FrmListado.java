package vista;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

/** Pantalla 1: listado de empleados (ventana principal). */
public class FrmListado extends JFrame {

    // ---------- Paleta ----------
    static final Color NEGRO    = Color.BLACK;
    static final Color FILA     = new Color(0x6C84F0);
    static final Color SEL      = new Color(0x4F63D2);
    static final Color TEXTO    = new Color(0x10142E);
    static final Color NARANJA  = new Color(0xFF8A3D);
    static final Color CIAN     = new Color(0x38BDF8);
    static final Color VERDE    = new Color(0x22C55E);
    static final Color AMBAR    = new Color(0xF59E0B);
    static final Color ROJO     = new Color(0xEF4444);
    static final Color GRIS     = new Color(0x94A3B8);
    static final Color FONDO_OSCURO = new Color(0x0B0B14);

    private static final String[] COLUMNAS = {
        "LEGAJO", "NOMBRE Y APELLIDO", "CORREO ELECTRÓNICO",
        "ROL / PUESTO", "ESTADO", "FECHA ALTA", "ACCIONES"
    };
    private static final int COL_ESTADO = 4;
    private static final int COL_ACCIONES = 6;

    private final List<Empleado> datos = new ArrayList<>();
    private int proximoLegajo = 146;

    private DefaultTableModel modelo;
    private JTable tabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;
    private JComboBox<String> cmbEstado;
    private JLabel lblContador;

    public FrmListado() {
        super("Sistema de Gestión");
        cargarDatosDeEjemplo();
        armarPantalla();
        refrescar();
    }

    // ======================================================
    //  Datos de ejemplo
    // ======================================================
    private void cargarDatosDeEjemplo() {
        datos.add(new Empleado("#00142", "Agustin Benitez", "agustin.b@ies21.edu.ar", "Desarrollador Web", "ACTIVO", "12/03/2026"));
        datos.add(new Empleado("#00143", "Camila Morales", "camila.m@empresa.com", "Diseñadora UI/UX", "ACTIVO", "14/03/2026"));
        datos.add(new Empleado("#00144", "Lucas Romero", "lucas.r@empresa.com", "Project Manager", "PENDIENTE", "20/03/2026"));
        datos.add(new Empleado("#00145", "Valeria Gomez", "valeria.g@empresa.com", "QA Analyst", "INACTIVO", "05/04/2026"));
    }

    // ======================================================
    //  Armado de la interfaz
    // ======================================================
    private void armarPantalla() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1144, 635);
        setMinimumSize(new Dimension(1000, 500));
        setLocationRelativeTo(null);
        getContentPane().setBackground(NEGRO);
        setLayout(new BorderLayout());

        add(crearHeader(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(NEGRO);
        centro.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 28));
        centro.add(crearToolbar(), BorderLayout.NORTH);
        centro.add(crearTabla(), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        add(crearFooter(), BorderLayout.SOUTH);
    }

    private JPanel crearHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(NEGRO);
        p.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));

        JLabel lblTitulo = new JLabel("Sistema de Gestión", new RayoIcon(), SwingConstants.LEFT);
        lblTitulo.setIconTextGap(10);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));

        JLabel lblUsuario = new JLabel("<html><font color='white'>Agustin Benitez</font> "
                + "<b><font color='#38BDF8'>(Admin)</font></b></html>");
        lblUsuario.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));

        p.add(lblTitulo, BorderLayout.WEST);
        p.add(lblUsuario, BorderLayout.EAST);
        return p;
    }

    private JPanel crearToolbar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(NEGRO);
        p.setBorder(BorderFactory.createEmptyBorder(14, 0, 28, 0));

        // --- Buscador con placeholder ---
        txtBuscar = new JTextField(28) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(GRIS);
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    Insets in = getInsets();
                    g2.drawString("Buscar por nombre, legajo o email...", in.left,
                            (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                    g2.dispose();
                }
            }
        };
        txtBuscar.setBackground(NEGRO);
        txtBuscar.setForeground(Color.WHITE);
        txtBuscar.setCaretColor(Color.WHITE);
        txtBuscar.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x334155)),
                BorderFactory.createEmptyBorder(6, 2, 6, 2)));
        txtBuscar.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { txtBuscar.repaint(); }
            @Override public void focusLost(FocusEvent e) { txtBuscar.repaint(); }
        });
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aplicarFiltro(); }
            public void removeUpdate(DocumentEvent e) { aplicarFiltro(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltro(); }
        });

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izq.setBackground(NEGRO);
        izq.add(txtBuscar);

        // --- Filtro + botón nuevo ---
        JLabel lblFiltrar = new JLabel("Filtrar por:");
        lblFiltrar.setForeground(GRIS);

        cmbEstado = new JComboBox<>(new String[]{"Todos los estados", "Activo", "Pendiente", "Inactivo"});
        cmbEstado.setBackground(new Color(0x111827));
        cmbEstado.setForeground(Color.WHITE);
        cmbEstado.addActionListener(e -> aplicarFiltro());

        JButton btnNuevo = new JButton("+ Nuevo empleado");
        btnNuevo.setBackground(FILA);
        btnNuevo.setForeground(Color.WHITE);
        btnNuevo.setFont(btnNuevo.getFont().deriveFont(Font.BOLD));
        btnNuevo.setFocusPainted(false);
        btnNuevo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnNuevo.addActionListener(e -> nuevo());

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        der.setBackground(NEGRO);
        der.add(lblFiltrar);
        der.add(cmbEstado);
        der.add(btnNuevo);

        p.add(izq, BorderLayout.WEST);
        p.add(der, BorderLayout.EAST);
        return p;
    }

    private JScrollPane crearTabla() {
        modelo = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        tabla.setRowHeight(52);
        tabla.setBackground(FILA);
        tabla.setForeground(TEXTO);
        tabla.setGridColor(NEGRO);
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setSelectionBackground(SEL);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFillsViewportHeight(false);

        // Renderers
        CeldaTexto texto = new CeldaTexto();
        for (int i = 0; i < COLUMNAS.length; i++) {
            if (i == COL_ESTADO) tabla.getColumnModel().getColumn(i).setCellRenderer(new CeldaEstado());
            else if (i == COL_ACCIONES) tabla.getColumnModel().getColumn(i).setCellRenderer(new CeldaAcciones());
            else tabla.getColumnModel().getColumn(i).setCellRenderer(texto);
        }

        // Anchos de columna
        int[] anchos = {90, 190, 230, 170, 130, 110, 130};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        tabla.getColumnModel().getColumn(COL_ACCIONES).setMinWidth(130);
        tabla.getColumnModel().getColumn(COL_ACCIONES).setMaxWidth(130);

        // Encabezado
        JTableHeader h = tabla.getTableHeader();
        h.setReorderingAllowed(false);
        h.setBackground(NEGRO);
        h.setPreferredSize(new Dimension(0, 44));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                super.getTableCellRendererComponent(t, v, false, false, r, c);
                setBackground(NEGRO);
                setForeground(Color.WHITE);
                setFont(getFont().deriveFont(Font.BOLD, 12f));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        });

        // Clic en los íconos de acciones / doble clic en la fila
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.rowAtPoint(e.getPoint());
                int col = tabla.columnAtPoint(e.getPoint());
                if (fila < 0 || col < 0) return;
                int m = tabla.convertRowIndexToModel(fila);
                if (col == COL_ACCIONES) {
                    Rectangle r = tabla.getCellRect(fila, col, false);
                    int relX = e.getX() - r.x;
                    if (relX < 6 || relX >= 114) return;
                    int icono = (relX - 6) / 36;   // 0 = editar, 1 = ver, 2 = eliminar
                    if (icono == 0) editar(m);
                    else if (icono == 1) ver(m);
                    else eliminar(m);
                } else if (e.getClickCount() == 2) {
                    ver(m);
                }
            }
        });
        // Cursor de mano sobre los íconos
        tabla.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int col = tabla.columnAtPoint(e.getPoint());
                tabla.setCursor(Cursor.getPredefinedCursor(
                        col == COL_ACCIONES ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(NEGRO);
        scroll.setBackground(NEGRO);
        return scroll;
    }

    private JPanel crearFooter() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(NEGRO);
        p.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));

        lblContador = new JLabel();
        lblContador.setForeground(GRIS);

        JPanel pag = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        pag.setBackground(NEGRO);
        pag.add(botonPlano("◀ Anterior", GRIS));
        pag.add(botonPlano("1", Color.WHITE));
        pag.add(botonPlano("2", new Color(0x475569)));
        pag.add(botonPlano("3", new Color(0x475569)));
        pag.add(botonPlano("Siguiente ▶", GRIS));

        p.add(lblContador, BorderLayout.WEST);
        p.add(pag, BorderLayout.EAST);
        return p;
    }

    private JButton botonPlano(String texto, Color fg) {
        JButton b = new JButton(texto);
        b.setForeground(fg);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ======================================================
    //  Lógica
    // ======================================================
    private void refrescar() {
        modelo.setRowCount(0);
        for (Empleado e : datos) {
            modelo.addRow(new Object[]{e.legajo, e.nombre, e.correo, e.rol, e.estado, e.fecha, ""});
        }
        actualizarContador();
    }

    private void actualizarContador() {
        int visibles = tabla.getRowCount();
        lblContador.setText("<html>Mostrando <b>" + (visibles == 0 ? "0" : "1-" + visibles)
                + "</b> de <b>" + datos.size() + "</b> registros</html>");
    }

    private void aplicarFiltro() {
        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> en) {
                String q = txtBuscar.getText().trim().toLowerCase();
                boolean okTexto = q.isEmpty()
                        || en.getStringValue(0).toLowerCase().contains(q)
                        || en.getStringValue(1).toLowerCase().contains(q)
                        || en.getStringValue(2).toLowerCase().contains(q);
                boolean okEstado = cmbEstado.getSelectedIndex() == 0
                        || en.getStringValue(COL_ESTADO).equalsIgnoreCase((String) cmbEstado.getSelectedItem());
                return okTexto && okEstado;
            }
        });
        actualizarContador();
    }

    private void nuevo() {
        String legajo = String.format("#%05d", proximoLegajo);
        DlgEmpleado d = new DlgEmpleado(this, null, legajo);
        d.setVisible(true);
        if (d.isGuardado()) {
            datos.add(d.getEmpleado());
            proximoLegajo++;
            refrescar();
        }
    }

    private void editar(int indice) {
        DlgEmpleado d = new DlgEmpleado(this, datos.get(indice), null);
        d.setVisible(true);
        if (d.isGuardado()) refrescar();
    }

    private void ver(int indice) {
        DlgDetalle d = new DlgDetalle(this, datos.get(indice));
        d.setVisible(true);
        if ("EDITAR".equals(d.getAccion())) editar(indice);
        else if ("ELIMINAR".equals(d.getAccion())) eliminar(indice);
    }

    private void eliminar(int indice) {
        Empleado e = datos.get(indice);
        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar a " + e.nombre + "? Esta acción no se puede deshacer.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            datos.remove(indice);
            refrescar();
        }
    }

    static Color colorEstado(String estado) {
        if ("ACTIVO".equalsIgnoreCase(estado)) return VERDE;
        if ("PENDIENTE".equalsIgnoreCase(estado)) return AMBAR;
        return ROJO;
    }

    // ======================================================
    //  Componentes de apoyo (renderers, badge, íconos)
    // ======================================================

    /** Etiqueta tipo "pill" negra con texto de color (badge de estado). */
    static class Pill extends JLabel {
        Pill(String texto, Color color) {
            super(texto, SwingConstants.CENTER);
            setForeground(color);
            setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(NEGRO);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class CeldaTexto extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, false, r, c);
            setBackground(sel ? SEL : FILA);
            setForeground(sel ? Color.WHITE : (c == 0 || c == 5 ? new Color(0x2A3170) : TEXTO));
            setFont(t.getFont().deriveFont(c == 1 ? Font.BOLD : Font.PLAIN, 13f));
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            return this;
        }
    }

    private static class CeldaEstado implements TableCellRenderer {
        private final JPanel panel = new JPanel(new GridBagLayout());

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            panel.removeAll();
            panel.setBackground(sel ? SEL : FILA);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.weightx = 1;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(0, 8, 0, 0);
            String s = String.valueOf(v);
            panel.add(new Pill(s, colorEstado(s)), gbc);
            return panel;
        }
    }

    /** Dibuja los 3 íconos (editar, ver, eliminar) con Graphics2D, sin depender de imágenes ni emojis. */
    private static class CeldaAcciones extends JPanel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            setBackground(sel ? SEL : FILA);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int cy = getHeight() / 2;

            // Editar (lápiz)
            int cx = 24;
            g2.setColor(NARANJA);
            g2.drawLine(cx - 6, cy + 6, cx + 5, cy - 5);
            g2.fillPolygon(new int[]{cx - 8, cx - 6, cx - 3}, new int[]{cy + 8, cy + 3, cy + 6}, 3);

            // Ver (ojo)
            cx = 60;
            g2.setColor(Color.WHITE);
            g2.drawOval(cx - 9, cy - 5, 18, 10);
            g2.fillOval(cx - 3, cy - 3, 6, 6);

            // Eliminar (papelera)
            cx = 96;
            g2.setColor(Color.WHITE);
            g2.drawLine(cx - 7, cy - 5, cx + 7, cy - 5);
            g2.drawLine(cx - 2, cy - 8, cx + 2, cy - 8);
            g2.drawRoundRect(cx - 5, cy - 3, 10, 11, 3, 3);
            g2.drawLine(cx, cy, cx, cy + 5);
            g2.dispose();
        }
    }

    /** Ícono del rayo del logo. */
    private static class RayoIcon implements Icon {
        public int getIconWidth() { return 14; }
        public int getIconHeight() { return 18; }
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(NARANJA);
            g2.fillPolygon(new int[]{x + 9, x + 1, x + 6, x + 4, x + 13, x + 8, x + 11},
                           new int[]{y, y + 10, y + 10, y + 18, y + 7, y + 7, y}, 7);
            g2.dispose();
        }
    }

    // ======================================================
    //  main
    // ======================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FrmListado().setVisible(true));
    }
}
