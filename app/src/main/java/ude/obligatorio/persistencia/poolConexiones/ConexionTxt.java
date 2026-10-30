package ude.obligatorio.persistencia.poolConexiones;

import java.io.BufferedReader;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;

public class ConexionTxt implements IConexion {

    private final Path directorio;
    private final boolean escribiendo;

    public ConexionTxt(Path directorio, boolean escribiendo) {
        this.directorio = directorio;
        this.escribiendo = escribiendo;
    }

    public boolean estaEscribiendo() {
        return  escribiendo;
    }

    public Path getArchivoAlumnos() {
        return directorio.resolve("alumnos.txt");
    }

    public Path getArchivoRegistros(String cedula) {
        return directorio.resolve("registros" + cedula + ".txt");
    }
}