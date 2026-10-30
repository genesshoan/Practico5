package ude.obligatorio.logica;

import java.io.Serializable;

public class Registro implements Serializable {

    private static final long serialVersionUID = 1L;

    private String actividad;
    private int nota, numero;

    public Registro(int numero, int nota, String actividad) {
        this.numero = numero;
        this.nota = nota;
        this.actividad = actividad;
    }

    public String getActividad() {
        return actividad;
    }

    public int getNota() {
        return nota;
    }
    public int getNumero() {
        return numero;
    }
}
