package ude.obligatorio.persistencia.daos;

import java.util.List;

import ude.obligatorio.logica.valueObjects.VOAlumnoProm;
import ude.obligatorio.logica.Alumno;
import ude.obligatorio.logica.excepciones.PersistenciaException;
import ude.obligatorio.persistencia.poolConexiones.IConexion;

public interface IDAOAlumnos {

    boolean member(IConexion icon, int ced) throws PersistenciaException;

    void insert(IConexion icon, Alumno alumno) throws PersistenciaException;

    Alumno find(IConexion icon, int cedA) throws PersistenciaException;

    boolean esVacio(IConexion icon) throws PersistenciaException;

    List<Alumno> listarAlumnos(IConexion icon) throws PersistenciaException;

    VOAlumnoProm alumnoMayorPromedio(IConexion icon) throws PersistenciaException;

    int contarAlumnos(IConexion icon, String act, int nota) throws PersistenciaException;
}