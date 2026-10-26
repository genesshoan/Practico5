package ude.bdpractico5.logica;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import ude.bdpractico5.logica.accesoBD.AccesoBD;
import ude.bdpractico5.logica.excepciones.AlumnoException;
import ude.bdpractico5.logica.excepciones.NotasException;
import ude.bdpractico5.logica.excepciones.PersistenciaException;
import ude.bdpractico5.logica.excepciones.RegistroException;
import ude.bdpractico5.logica.valueObjects.VOActividad;
import ude.bdpractico5.logica.valueObjects.VOAlumno;
import ude.bdpractico5.logica.valueObjects.VOAlumnoProm;
import ude.bdpractico5.logica.valueObjects.VORegistro;
import ude.bdpractico5.logica.valueObjects.VORegistroListado;

/**
 * Fachada de acceso a la capa de logica y persistencia.
 *
 * Cada requerimiento es una transaccion: pide una IConexion al pool, la usa
 * y la devuelve haciendo commit si todo salio bien o rollback si hubo un error.
 */
public class Fachada extends UnicastRemoteObject implements IFachada {

    private static final long serialVersionUID = 1L;

    private AccesoBD abd;
    private IPoolConexiones pool;

    public Fachada() throws RemoteException, PersistenciaException {
        super();
        abd = new AccesoBD();
        pool = new PoolConexiones();
    }

    // ------------------------------------------------------------------
    // Requerimientos
    // ------------------------------------------------------------------

    @Override
    public void nuevoAlumno(VOAlumno voA)
            throws NotasException, PersistenciaException, RemoteException {
        IConexion con = pool.obtenerConexion(true);
        boolean ok = false;
        try {
            if (abd.existeAlumno(con, voA.getCedula())) {
                throw new AlumnoException("Ya existe un alumno con la cedula " + voA.getCedula());
            }
            abd.insertarAlumno(con, voA.getCedula(), voA.getNombre(), voA.getApellido());
            ok = true;
        } finally {
            pool.liberarConexion(con, ok);
        }
    }

    @Override
    public void registrarNota(int cedA, VORegistro voR)
            throws NotasException, PersistenciaException, RemoteException {
        IConexion con = pool.obtenerConexion(true);
        boolean ok = false;
        try {
            chequearAlumno(con, cedA);
            int numero = abd.siguienteNum(con, cedA);
            abd.insertarRegistro(con, numero, cedA, voR.getActividad(), voR.getNota());
            ok = true;
        } finally {
            pool.liberarConexion(con, ok);
        }
    }

    @Override
    public List<VOAlumno> listarAlumnos()
            throws PersistenciaException, RemoteException {
        IConexion con = pool.obtenerConexion(false);
        boolean ok = false;
        try {
            List<VOAlumno> lista = abd.listarAlumnos(con);
            ok = true;
            return lista;
        } finally {
            pool.liberarConexion(con, ok);
        }
    }

    @Override
    public VORegistro obtenerRegistro(int cedA, int numR)
            throws NotasException, PersistenciaException, RemoteException {
        IConexion con = pool.obtenerConexion(false);
        boolean ok = false;
        try {
            chequearAlumno(con, cedA);
            VORegistro registro = abd.obtenerRegistro(con, cedA, numR);
            if (registro == null) {
                throw new RegistroException(
                        "El alumno " + cedA + " no tiene un registro con numero " + numR);
            }
            ok = true;
            return registro;
        } finally {
            pool.liberarConexion(con, ok);
        }
    }

    @Override
    public List<VORegistroListado> listarRegistros(int cedA)
            throws NotasException, PersistenciaException, RemoteException {
        IConexion con = pool.obtenerConexion(false);
        boolean ok = false;
        try {
            chequearAlumno(con, cedA);
            List<VORegistroListado> lista = abd.listarRegistros(con, cedA);
            ok = true;
            return lista;
        } finally {
            pool.liberarConexion(con, ok);
        }
    }

    @Override
    public double promedioNotas(int cedA)
            throws NotasException, PersistenciaException, RemoteException {
        IConexion con = pool.obtenerConexion(false);
        boolean ok = false;
        try {
            chequearAlumno(con, cedA);
            double promedio = abd.promedioNotas(con, cedA);
            ok = true;
            return promedio;
        } finally {
            pool.liberarConexion(con, ok);
        }
    }

    @Override
    public VOAlumnoProm alumnoMayorPromedio()
            throws NotasException, PersistenciaException, RemoteException {
        IConexion con = pool.obtenerConexion(false);
        boolean ok = false;
        try {
            if (abd.cantidadAlumnos(con) == 0) {
                throw new AlumnoException("No hay alumnos registrados");
            }
            VOAlumnoProm voA = abd.alumnoMayorPromedio(con);
            ok = true;
            return voA;
        } finally {
            pool.liberarConexion(con, ok);
        }
    }

    @Override
    public int contarAlumnos(String act, int nota)
            throws PersistenciaException, RemoteException {
        IConexion con = pool.obtenerConexion(false);
        boolean ok = false;
        try {
            int cant = abd.contarAlumnos(con, act, nota);
            ok = true;
            return cant;
        } finally {
            pool.liberarConexion(con, ok);
        }
    }

    @Override
    public VOActividad actividadMasCursada(int cedA)
            throws NotasException, PersistenciaException, RemoteException {
        IConexion con = pool.obtenerConexion(false);
        boolean ok = false;
        try {
            chequearAlumno(con, cedA);
            VOActividad voAct = abd.actividadMasCursada(con, cedA);
            if (voAct == null) {
                throw new RegistroException(
                        "El alumno " + cedA + " no tiene ninguna actividad registrada");
            }
            ok = true;
            return voAct;
        } finally {
            pool.liberarConexion(con, ok);
        }
    }

    // ------------------------------------------------------------------
    // Metodos auxiliares
    // ------------------------------------------------------------------

    private void chequearAlumno(IConexion con, int cedA)
            throws AlumnoException, PersistenciaException {
        if (!abd.existeAlumno(con, cedA)) {
            throw new AlumnoException("No existe un alumno con la cedula " + cedA);
        }
    }
}
