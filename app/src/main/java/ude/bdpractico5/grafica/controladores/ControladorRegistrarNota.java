package ude.bdpractico5.grafica.controladores;

import ude.bdpractico5.grafica.ventanas.VentanaRegistrarNota;
import ude.bdpractico5.logica.IFachada;
import ude.bdpractico5.logica.excepciones.NotasException;
import ude.bdpractico5.logica.excepciones.PersistenciaException;
import ude.bdpractico5.logica.valueObjects.VORegistro;

/** Controlador del requerimiento "registrar nota". */
public class ControladorRegistrarNota {

    private IFachada fachada;
    private VentanaRegistrarNota ventana;

    public ControladorRegistrarNota(IFachada fachada, VentanaRegistrarNota ventana) {
        this.fachada = fachada;
        this.ventana = ventana;
    }

    public void registrarNota(String cedula, String actividad, String nota) {
        int ced;
        int nro;
        try {
            ced = Integer.parseInt(cedula.trim());
        } catch (NumberFormatException e) {
            ventana.mostrarError("La cedula debe ser un numero entero.");
            return;
        }
        try {
            nro = Integer.parseInt(nota.trim());
        } catch (NumberFormatException e) {
            ventana.mostrarError("La nota debe ser un numero entero.");
            return;
        }
        if (actividad.trim().isEmpty()) {
            ventana.mostrarError("La actividad no puede quedar vacia.");
            return;
        }

        try {
            fachada.registrarNota(ced, new VORegistro(actividad.trim(), nro));
            ventana.mostrarMensaje("Nota registrada correctamente.");
            ventana.limpiar();
        } catch (NotasException e) {
            ventana.mostrarError(e.getMessage());
        } catch (PersistenciaException e) {
            ventana.mostrarError("Error de persistencia: " + e.getMessage());
        } catch (java.rmi.RemoteException e) {
            ventana.mostrarError("Error de comunicacion con el servidor: " + e.getMessage());
        }
    }
}
