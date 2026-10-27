package ude.bdpractico5.grafica.controladores;

import ude.bdpractico5.grafica.ventanas.VentanaListarAlumnos;
import ude.bdpractico5.logica.IFachada;
import ude.bdpractico5.logica.excepciones.PersistenciaException;

/** Controlador del requerimiento "listar alumnos". */
public class ControladorListarAlumnos {

    private IFachada fachada;
    private VentanaListarAlumnos ventana;

    public ControladorListarAlumnos(IFachada fachada, VentanaListarAlumnos ventana) {
        this.fachada = fachada;
        this.ventana = ventana;
    }

    public void listarAlumnos() {
        try {
            ventana.mostrarAlumnos(fachada.listarAlumnos());
        } catch (PersistenciaException e) {
            ventana.mostrarError("Error de persistencia: " + e.getMessage());
        } catch (java.rmi.RemoteException e) {
            ventana.mostrarError("Error de comunicacion con el servidor: " + e.getMessage());
        }
    }
}
