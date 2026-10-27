package ude.bdpractico5.logica;

import java.io.Serializable;
import java.util.List;

import ude.bdpractico5.logica.excepciones.PersistenciaException;
import ude.bdpractico5.logica.valueObjects.VOActividad;
import ude.bdpractico5.logica.valueObjects.VORegistro;
import ude.bdpractico5.logica.valueObjects.VORegistroListado;
import ude.bdpractico5.persistencia.daos.DAORegistros;
import ude.bdpractico5.persistencia.poolConexiones.IConexion;

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

    public int cantidadRegistros(IConexion icon) throws PersistenciaException {
        return secuencia.largo(icon);
    }

    public void agregarRegistro(IConexion icon, Registro reg) throws PersistenciaException {
        secuencia.insback(icon, reg);
    }

    public VORegistro obtenRegistro(IConexion icon, int numR) throws PersistenciaException {
        Registro reg = secuencia.kesimo(icon, numR);
        if (reg == null) 
            return null;

        return new VORegistro(reg.getActividad(), reg.getNota());
    }

    public List<VORegistroListado> listarRegistros(IConexion icon) throws PersistenciaException {
        return secuencia.listarRegistros(icon);
    }

    public double promedioNotas(IConexion icon) throws PersistenciaException {
        return secuencia.promedioNotas(icon);
    }

    public VOActividad actividadMasCursada(IConexion icon) throws PersistenciaException {
        return secuencia.actividadMasCursada(icon);
    }
}
