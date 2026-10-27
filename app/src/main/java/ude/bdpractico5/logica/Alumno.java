package ude.bdpractico5.logica;

import java.io.Serializable;
import java.util.List;

import ude.bdpractico5.logica.excepciones.PersistenciaException;
import ude.bdpractico5.logica.valueObjects.VOActividad;
import ude.bdpractico5.logica.valueObjects.VORegistro;
import ude.bdpractico5.logica.valueObjects.VORegistroListado;
import ude.bdpractico5.persistencia.daos.DAORegistros;

public class Alumno implements Serializable {

    private static final long serialVersionUID = 1L;

    private int cedula;
    private String nombre;
    private String apellido;
    private DAORegistros secuencia;

    public Alumno(int cedula, String nombre, String apellido) throws PersistenciaException {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.secuencia = new DAORegistros(cedula);
    }

    public int getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public int cantidadRegistros() throws PersistenciaException {
        return secuencia.largo();
    }

    public void agregarRegistro(Registro reg) throws PersistenciaException {
        secuencia.insback(reg);
    }

    public VORegistro obtenRegistro(int numR) throws PersistenciaException {
        Registro reg = secuencia.kesimo(numR);
        if (reg == null) 
            return null;

        return new VORegistro(reg.getActividad(), reg.getNota());
    }

    public List<VORegistroListado> listarRegistros() throws PersistenciaException {
        return secuencia.listarRegistros();
    }

    public double promedioNotas() throws PersistenciaException {
        return secuencia.promedioNotas();
    }

    public VOActividad actividadMasCursada() throws PersistenciaException {
        return secuencia.actividadMasCursada();
    }
}
