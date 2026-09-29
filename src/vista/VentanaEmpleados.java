package vista;

import controlador.EmpleadoControlador;
import modelo.EmpleadoAdministrativo;
import modelo.EmpleadoBase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class VentanaEmpleados  extends JFrame {

    private final EmpleadoControlador controlador;

    private final JTextField txtCedula = new JTextField();
    private final JTextField txtNombre = new JTextField();
    private final JTextField txtSalario = new JTextField();
    private final JTextField txtBonificacion = new JTextField();
    private final JComboBox<String> cmbTipo =
            new JComboBox<>(EmpleadoControlador.TIPOS_EMPLEADO);

    private final JButton btnAgregar = new JButton("Agregar");
    private final JButton btnBuscar = new JButton("Buscar");
    private final JButton btnActualizar = new JButton("Actualizar");
    private final JButton btnEliminar = new JButton("Eliminar");
    private final JButton btnLimpiar = new JButton("Limpiar");
    private final JButton btnHistorial = new JButton("Historial");

    private DefaultTableModel datosTabla;
    private final JLabel lblResumen = new JLabel();

    public VentanaEmpleados(EmpleadoControlador controlador) {
        super("Sistema CRUD de talento Humano");
        this.controlador = controlador;

        setLayout(new BorderLayout(10, 10));
        add(construirFormulario(), BorderLayout.NORTH);
        add(construirTabla(), BorderLayout.CENTER);
        lblResumen.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        add(lblResumen, BorderLayout.SOUTH);

        conectarEventos();
        refrescarTabla();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 540);
        setLocationRelativeTo(null);
    }

    private JPanel construirFormulario() {
        JPanel campos = new JPanel(new GridLayout(5, 2, 8, 8));
        campos.add(new JLabel("Cedula"));
        campos.add(txtCedula);
        campos.add(new JLabel("Nombre completo"));
        campos.add(txtNombre);
        campos.add(new JLabel("Salario base"));
        campos.add(txtSalario);
        campos.add(new JLabel("Tipo de empleado"));
        campos.add(cmbTipo);
        campos.add(new JLabel("Bonificacion (solo administrativos):"));
        campos.add(txtBonificacion);
        txtBonificacion.setEnabled(false);

        JPanel botones = new JPanel(new FlowLayout());
        JButton[] listaBotones = {btnAgregar, btnBuscar, btnActualizar, btnEliminar, btnLimpiar, btnHistorial};
        for (JButton boton : listaBotones) {
            botones.add(boton);
        }

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        panel.add(campos, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private String texto(JTextField campo) {
        return campo.getText().trim();
    }

    private String tipoSeleccionado() {
        return (String) cmbTipo.getSelectedItem();
    }

    private JScrollPane construirTabla() {
        String[] columnas = {"Cedula", "Nombre", "Tipo", "Salario base", "Salario total"};

        datosTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        JTable tabla = new JTable(datosTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Empleados registrados"));
        return scroll;
    }

    private void refrescarTabla() {
        datosTabla.setRowCount(0);
        for (EmpleadoBase empleado : controlador.obtenerEmpleados()) {
            Object[] fila = {
                    empleado.getCedula(),
                    empleado.getNombre(),
                    empleado.getTipo(),
                    formatoPesos(empleado.getSalarioBase()),
                    formatoPesos(empleado.calcularSalarioTotal())
            };
            datosTabla.addRow(fila);
        }
        lblResumen.setText("Empleados:" + datosTabla.getRowCount()
        + "  |  Total nomina:"
        + formatoPesos(controlador.calcularTotalNomina()));
    }

    private String formatoPesos(double valor) {
        return String.format("$ %, .0f", valor);
    }

    private void conectarEventos() {
        cmbTipo.addActionListener(e -> {
            boolean esAdministrativo = tipoSeleccionado().equals("Administrativo");
            txtBonificacion.setEnabled(esAdministrativo);
            if (!esAdministrativo) {
                txtBonificacion.setText("");
            }
        });

        btnAgregar.addActionListener(e -> mostrarResultado(controlador.agregarEmpleado(
                texto(txtCedula), texto(txtNombre), texto(txtSalario),
                tipoSeleccionado(), texto(txtBonificacion))));

        btnActualizar.addActionListener(e -> mostrarResultado(controlador.agregarEmpleado(
                texto(txtCedula), texto(txtNombre), texto(txtSalario),
                tipoSeleccionado(), texto(txtBonificacion))));

        btnBuscar.addActionListener(e -> buscar ());
        btnEliminar.addActionListener(e -> eliminar());
        btnBuscar.addActionListener(e -> limpiarFormulario ());
        btnEliminar.addActionListener(e -> mostrarHistorial());
    }

    private void mostrarResultado(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
        refrescarTabla();
    }

    private void buscar() {
        String cedula = texto(txtCedula);
        if (cedula.isEmpty()) {
            JOptionPane.showMessageDialog(this, "escribe una cedula para buscar");
            return;
        }
        EmpleadoBase empleado = controlador.buscarEmpleado(cedula);
        if (empleado == null) {
            JOptionPane.showMessageDialog(this,
                    "no se encontro ningun empleado con la cedula" + cedula + ".");
            return;
        }
        txtNombre.setText(empleado.getNombre());
        txtSalario.setText(String.format("%.0f", empleado.getSalarioBase()));
        cmbTipo.setSelectedItem(empleado.getTipo());
        if (empleado instanceof  EmpleadoAdministrativo) {
            EmpleadoAdministrativo administrativo = (EmpleadoAdministrativo) empleado;
            txtBonificacion.setText(String.format("%.0f", administrativo.getBonificacion()));
        }
    }

    private void eliminar() {
        String cedula = texto(txtCedula);
        int respuesta = JOptionPane.showConfirmDialog(this,
                "seguro que deseas eliminar al empleado con cedula" + cedula + "?",
                "confiar eliminacion", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            mostrarResultado(controlador.eliminarEmpleado(cedula));
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        txtCedula.setText("");
        txtNombre.setText("");
        txtSalario.setText("");
        txtBonificacion.setText("");
        cmbTipo.setSelectedItem(0);
        txtCedula.requestFocus();
    }

    private void mostrarHistorial() {
        ArrayList<String> historial = controlador.obtenerHistorial();
        if (historial.isEmpty()) {
            JOptionPane.showMessageDialog(this, "aun no hay operaciones registradas");
            return;
        }
        String texto = "";
        for (int i = 0; i < historial.size(); i++) {
            texto += (i + 1) + ". " + historial.get(i) + "/n";
        }
        JOptionPane.showMessageDialog(this, texto, "historial de operaciones",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
