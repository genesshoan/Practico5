package ude.obligatorio.persistencia.poolConexiones;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Properties;

import ude.obligatorio.logica.excepciones.PersistenciaException;

public class PoolConexionesTxt implements IPoolConexiones {

    private final Path directorio;
    private int numeroDeLectores;
    private boolean estaEscribiendo;

    public PoolConexionesTxt() throws PersistenciaException {
        try {
            Properties p = new Properties();
            p.load(new FileInputStream("config.properties"));
            directorio = Path.of(p.getProperty("directorio"));
        } catch (Exception e) {
            throw new PersistenciaException("Error al inicializar el pool de conexiones");
        }      
    }

    @Override 
    public synchronized IConexion obtenerConexion(boolean modifica) throws PersistenciaException {
        if (modifica) {
            adquirirEscritura();
        } else {
            adquirirLectura();
        }

        return new ConexionTxt(directorio, modifica);
    }

    @Override
    public synchronized void liberarConexion(IConexion icon, boolean ok) throws PersistenciaException {
        boolean escribiendo = ((ConexionTxt) icon).estaEscribiendo();

        if (escribiendo) {
            liberarEscritura();
        } else {
            liberarLectura();
        }
    }

    public void adquirirLectura() {
        while (estaEscribiendo) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        numeroDeLectores++;
    }

    public void liberarLectura() {
        numeroDeLectores--;
        if (numeroDeLectores == 0) {
            this.notifyAll();
        }
    }

    public void adquirirEscritura() {
        while (estaEscribiendo || numeroDeLectores > 0) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        estaEscribiendo = true;
    }

    public void liberarEscritura() {
        estaEscribiendo = false;
        this.notifyAll();
    }
}