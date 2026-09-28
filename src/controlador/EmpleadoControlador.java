package controlador;

import modelo.EmpleadoAdministrativo;
import modelo.EmpleadoBase;
import modelo.RepositorioEmpleados;

import java.util.ArrayList;

public class EmpleadoControlador {

    public static final String[] TIPOS_EMPLEADO = {"Operativo", "Administrativo"};

    private final RepositorioEmpleados repositorio;
    private final ArrayList<String> historial;

    public EmpleadoControlador() {
        repositorio = new RepositorioEmpleados();
        historial = new ArrayList<>();
        cargarDatosDePrueba();
    }

    private void cargarDatosDePrueba() {
        String[] cedulas = {"1001", "1002", "1003", "1004"};
        String[] nombres = {"Ana Torres", "Luis Gomez", "Marta Rios", "Pedro Cano"};
        double[] salarios = {1800000, 2500000, 1750000, 3200000};

        for (int i = 0; i < cedulas.length; i++) {
            EmpleadoBase empleado;
            if (i % 2 == 0) {
                empleado = new EmpleadoBase(cedulas[i], nombres[i], salarios[i]);
            }else{
                empleado = new EmpleadoAdministrativo(cedulas[i], nombres[i],
                        salarios[i], 300000);
            }
            repositorio.agregar(empleado);
        }
    }

    private boolean esNumeroValido(String texto) {
        if (texto.isEmpty() || texto.equals(".")) {
            return false;
        }
        int puntos = 0;
        for (int i = 0; i <texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '.') {
                puntos++;
            } else if (! Character.isDigit(c)) {
                return false;
            }
        }
        return puntos <= 1;
    }

    private String validar(String cedula, String nombre, String salario,
                           String tipo, String bonificacion) {
        if (cedula.isEmpty() || nombre.isEmpty()) {
            return "La cedula y el nombre son obligatorios";
        }
        if (!esNumeroValido(salario)) {
            return "El  salario debe ser un numero positivo (sin puntos de miles)";
        }
        if (tipo.equals("Administrativo") && !esNumeroValido(bonificacion)) {
            return "La bonificacion debe ser un numero positivo";
        }
        return  null;
    }

    private EmpleadoBase construirEmpleado(String cedula, String nombre, String salario,
                                           String tipo, String bonificacion) {
        double salarioBase = Double.parseDouble(salario);
        if (tipo.equals("Administrativo")) {
            double bono = Double.parseDouble(bonificacion);
            return new EmpleadoAdministrativo(cedula, nombre, salarioBase, bono);
        }
        return new EmpleadoBase(cedula, nombre, salarioBase);
    }
}
