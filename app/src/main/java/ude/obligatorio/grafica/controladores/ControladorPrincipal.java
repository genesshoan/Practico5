package ude.obligatorio.grafica.controladores;

import ude.obligatorio.grafica.ventanas.VentanaActividadMasCursada;
import ude.obligatorio.grafica.ventanas.VentanaAlumnoMayorPromedio;
import ude.obligatorio.grafica.ventanas.VentanaContarAlumnos;
import ude.obligatorio.grafica.ventanas.VentanaListarAlumnos;
import ude.obligatorio.grafica.ventanas.VentanaListarRegistros;
import ude.obligatorio.grafica.ventanas.VentanaNuevoAlumno;
import ude.obligatorio.grafica.ventanas.VentanaObtenerRegistro;
import ude.obligatorio.grafica.ventanas.VentanaPromedioNotas;
import ude.obligatorio.grafica.ventanas.VentanaRegistrarNota;
import ude.obligatorio.logica.IFachada;

/**
 * Controlador del menu principal: abre la ventana de cada requerimiento,
 * pasandole la referencia remota a la Fachada.
 */
public class ControladorPrincipal {

    private IFachada fachada;

    public ControladorPrincipal(IFachada fachada) {
        this.fachada = fachada;
    }

    public void abrirNuevoAlumno() {
        new VentanaNuevoAlumno(fachada).setVisible(true);
    }

    public void abrirRegistrarNota() {
        new VentanaRegistrarNota(fachada).setVisible(true);
    }

    public void abrirListarAlumnos() {
        new VentanaListarAlumnos(fachada).setVisible(true);
    }

    public void abrirObtenerRegistro() {
        new VentanaObtenerRegistro(fachada).setVisible(true);
    }

    public void abrirListarRegistros() {
        new VentanaListarRegistros(fachada).setVisible(true);
    }

    public void abrirPromedioNotas() {
        new VentanaPromedioNotas(fachada).setVisible(true);
    }

    public void abrirAlumnoMayorPromedio() {
        new VentanaAlumnoMayorPromedio(fachada).setVisible(true);
    }

    public void abrirContarAlumnos() {
        new VentanaContarAlumnos(fachada).setVisible(true);
    }

    public void abrirActividadMasCursada() {
        new VentanaActividadMasCursada(fachada).setVisible(true);
    }
}
