package ude.bdpractico5.logica;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import ude.bdpractico5.logica.excepciones.AlumnoException;
import ude.bdpractico5.logica.excepciones.NotasException;
import ude.bdpractico5.logica.excepciones.PersistenciaException;
import ude.bdpractico5.logica.excepciones.RegistroException;
import ude.bdpractico5.logica.valueObjects.VOActividad;
import ude.bdpractico5.logica.valueObjects.VOAlumno;
import ude.bdpractico5.logica.valueObjects.VOAlumnoProm;
import ude.bdpractico5.logica.valueObjects.VORegistro;
import ude.bdpractico5.logica.valueObjects.VORegistroListado;
import ude.bdpractico5.persistencia.daos.*;

/**
 * Fachada de acceso a la capa de logica y persistencia.
 *
 * Cada requerimiento es una transaccion: pide una IConexion al pool, la usa
 * y la devuelve haciendo commit si todo salio bien o rollback si hubo un error.
 */
public class Fachada extends UnicastRemoteObject implements IFachada {

    private static final long serialVersionUID = 1L;

    public Fachada() throws RemoteException, PersistenciaException {
        super();
    }

    // ------------------------------------------------------------------
    // Requerimientos
    // ------------------------------------------------------------------

    @Override
    public void nuevoAlumno(VOAlumno voA)
            throws NotasException, PersistenciaException, RemoteException {
        DAOAlumnos dao = new DAOAlumnos();
        if (dao.member(voA.getCedula())) {
            throw new AlumnoException("Ya existe un alumno con la cedula " + voA.getCedula());
        }
        
        dao.insert(new Alumno(voA.getCedula(), voA.getNombre(), voA.getApellido()));
    }

    @Override
    public void registrarNota(int cedA, VORegistro voR)
            throws NotasException, PersistenciaException, RemoteException {
        DAOAlumnos dao = new DAOAlumnos();

        if (!dao.member(cedA)) {
            throw new AlumnoException("No existe un alumno con la cedula " + cedA);
        }

        Alumno alu = dao.find(cedA);

        int numero = alu.cantidadRegistros() + 1;
        Registro reg = new Registro(numero, voR.getNota(), voR.getActividad());
        alu.agregarRegistro(reg);
    }

    @Override
    public List<VOAlumno> listarAlumnos()
            throws PersistenciaException, RemoteException {
        DAOAlumnos dao = new DAOAlumnos();
        return dao.listarAlumnos().stream()
                .map(a -> new VOAlumno(
                        a.getCedula(), 
                        a.getNombre(), 
                        a.getApellido()))
                .toList();
    }

    @Override
    public VORegistro obtenerRegistro(int cedA, int numR)
            throws NotasException, PersistenciaException, RemoteException {
        DAOAlumnos dao = new DAOAlumnos();

        if (!dao.member(cedA)) {
            throw new AlumnoException("No existe un alumno con la cedula " + cedA);
        }

        Alumno alu = dao.find(cedA);

        if (numR < 1 || numR > alu.cantidadRegistros()) {
            throw new RegistroException("El alumno " + cedA + " no tiene un registro con numero " + numR);
        }

        return alu.obtenRegistro(numR);
    }

    @Override
    public List<VORegistroListado> listarRegistros(int cedA)
            throws NotasException, PersistenciaException, RemoteException {
        DAOAlumnos dao = new DAOAlumnos();

        if (!dao.member(cedA)) {
            throw new AlumnoException("No existe un alumno con la cedula " + cedA);
        }
        
        return new DAOAlumnos().find(cedA).listarRegistros();
    }

    @Override
    public double promedioNotas(int cedA)
            throws NotasException, PersistenciaException, RemoteException {
        DAOAlumnos dao = new DAOAlumnos();

        if (!dao.member(cedA)) {
            throw new AlumnoException("No existe un alumno con la cedula " + cedA);
        }

        Alumno alu = dao.find(cedA);
        return alu.promedioNotas();
    }

    @Override
    public VOAlumnoProm alumnoMayorPromedio()
            throws NotasException, PersistenciaException, RemoteException {
        DAOAlumnos dao = new DAOAlumnos();

        if (dao.esVacio()) {
            throw new AlumnoException("No hay alumnos registrados");
        }

        return dao.alumnoMayorPromedio();
    }

    @Override
    public int contarAlumnos(String act, int nota)
            throws PersistenciaException, RemoteException {
        return new DAOAlumnos().contarAlumnos(act, nota);
    }

    @Override
    public VOActividad actividadMasCursada(int cedA)
            throws NotasException, PersistenciaException, RemoteException {
        DAOAlumnos dao = new DAOAlumnos();

        if (!dao.member(cedA)) {
            throw new AlumnoException("No existe un alumno con la cedula " + cedA);
        }

        Alumno alu = dao.find(cedA);

        if (alu.cantidadRegistros() == 0) {
            throw new RegistroException("El alumno " + cedA + " no tiene ninguna actividad registrada");
        }

        return alu.actividadMasCursada();
    }
}
