package ude.obligatorio.grafica.controladores;

import ude.obligatorio.grafica.ventanas.VentanaContarAlumnos;
import ude.obligatorio.logica.IFachada;
import ude.obligatorio.logica.excepciones.PersistenciaException;

/** Controlador del requerimiento "contar alumnos". */
public class ControladorContarAlumnos {

    private IFachada fachada;
    private VentanaContarAlumnos ventana;

    public ControladorContarAlumnos(IFachada fachada, VentanaContarAlumnos ventana) {
        this.fachada = fachada;
        this.ventana = ventana;
    }

    public void contarAlumnos(String actividad, String nota) {
        int nro;
        if (actividad.trim().isEmpty()) {
            ventana.mostrarError("La actividad no puede quedar vacia.");
            return;
        }
        try {
            nro = Integer.parseInt(nota.trim());
        } catch (NumberFormatException e) {
            ventana.mostrarError("La nota debe ser un numero entero.");
            return;
        }

        try {
            ventana.mostrarCantidad(fachada.contarAlumnos(actividad.trim(), nro));
        } catch (PersistenciaException e) {
            ventana.limpiarResultado();
            ventana.mostrarError("Error de persistencia: " + e.getMessage());
        } catch (java.rmi.RemoteException e) {
            ventana.limpiarResultado();
            ventana.mostrarError("Error de comunicacion con el servidor: " + e.getMessage());
        }
    }
}
