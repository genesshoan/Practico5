package ude.bdpractico5.logica;

import java.io.Serializable;

public class Alumno implements Serializable {

    private static final long serialVersionUID = 1L;

    private int cedula;
    private String nombre;
    private String apellido;

    public Alumno(int cedula, String nombre, String apellido) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
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
}
