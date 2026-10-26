package ude.bdpractico5.logica.excepciones;

/** Error de comunicacion con la base de datos. */
public class PersistenciaException extends Exception {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String msj) {
        super(msj);
    }
}
