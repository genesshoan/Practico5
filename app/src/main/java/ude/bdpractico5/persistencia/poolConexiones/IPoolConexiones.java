package ude.bdpractico5.persistencia.poolConexiones;

import ude.bdpractico5.logica.excepciones.PersistenciaException;

public interface IPoolConexiones {

    IConexion obtenerConexion(boolean modifica) throws PersistenciaException;
    void liberarConexion(IConexion conexion, boolean ok) throws PersistenciaException;
}
