package logicaPersistencia.accesoBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import logicaPersistencia.excepciones.PersistenciaException;
import logicaPersistencia.poolConexiones.Conexion;
import logicaPersistencia.poolConexiones.IConexion;
import logicaPersistencia.valueObjects.VOActividad;
import logicaPersistencia.valueObjects.VOAlumno;
import logicaPersistencia.valueObjects.VOAlumnoProm;
import logicaPersistencia.valueObjects.VORegistro;
import logicaPersistencia.valueObjects.VORegistroListado;

/**
 * Encapsula el acceso a la base de datos: ejecuta las sentencias definidas
 * en Consultas y traduce cualquier SQLException a PersistenciaException.
 *
 * La conexion le llega siempre por parametro (obtenida del pool por la
 * Fachada): esta clase no abre, cierra ni confirma transacciones.
 */
public class AccesoBD {

    private Consultas consultas = new Consultas();

    /** Castea la IConexion recibida a la Conexion concreta del pool. */
    private Connection getConnection(IConexion icon) {
        return ((Conexion) icon).getConnection();
    }

    public boolean existeAlumno(IConexion con, int ced) throws PersistenciaException {
        boolean existe = false;
        try {
            PreparedStatement pstmt = getConnection(con).prepareStatement(consultas.existeAlumno());
            pstmt.setInt(1, ced);
            ResultSet rs = pstmt.executeQuery();
            existe = rs.next();
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar el alumno: " + e.getMessage());
        }
        return existe;
    }

    public void insertarAlumno(IConexion con, int cedula, String nombre, String apellido)
            throws PersistenciaException {
        try {
            PreparedStatement pstmt = getConnection(con).prepareStatement(consultas.insertarAlumno());
            pstmt.setInt(1, cedula);
            pstmt.setString(2, nombre);
            pstmt.setString(3, apellido);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar el alumno: " + e.getMessage());
        }
    }

    public List<VOAlumno> listarAlumnos(IConexion con) throws PersistenciaException {
        List<VOAlumno> lista = new LinkedList<VOAlumno>();
        try {
            Statement stmt = getConnection(con).createStatement();
            ResultSet rs = stmt.executeQuery(consultas.listarAlumnos());
            while (rs.next()) {
                int cedula = rs.getInt("cedula");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                lista.add(new VOAlumno(cedula, nombre, apellido));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar los alumnos: " + e.getMessage());
        }
        return lista;
    }

    public int cantidadAlumnos(IConexion con) throws PersistenciaException {
        int cantidad = 0;
        try {
            Statement stmt = getConnection(con).createStatement();
            ResultSet rs = stmt.executeQuery(consultas.cantidadAlumnos());
            if (rs.next()) {
                cantidad = rs.getInt("cantidad");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al contar los alumnos: " + e.getMessage());
        }
        return cantidad;
    }

    public int siguienteNum(IConexion con, int ced) throws PersistenciaException {
        int siguiente = 1;
        try {
            PreparedStatement pstmt = getConnection(con).prepareStatement(consultas.siguienteNum());
            pstmt.setInt(1, ced);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                siguiente = rs.getInt("siguiente");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al calcular el numero de registro: " + e.getMessage());
        }
        return siguiente;
    }

    public void insertarRegistro(IConexion con, int numero, int cedula, String actividad, int nota)
            throws PersistenciaException {
        try {
            PreparedStatement pstmt = getConnection(con).prepareStatement(consultas.insertarRegistro());
            pstmt.setInt(1, numero);
            pstmt.setInt(2, cedula);
            pstmt.setString(3, actividad);
            pstmt.setInt(4, nota);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar el registro: " + e.getMessage());
        }
    }

    /** Devuelve null si el alumno no tiene un registro con ese numero. */
    public VORegistro obtenerRegistro(IConexion con, int ced, int num) throws PersistenciaException {
        VORegistro voR = null;
        try {
            PreparedStatement pstmt = getConnection(con).prepareStatement(consultas.obtenerRegistro());
            pstmt.setInt(1, ced);
            pstmt.setInt(2, num);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String actividad = rs.getString("actividad");
                int nota = rs.getInt("nota");
                voR = new VORegistro(actividad, nota);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al obtener el registro: " + e.getMessage());
        }
        return voR;
    }

    public List<VORegistroListado> listarRegistros(IConexion con, int ced) throws PersistenciaException {
        List<VORegistroListado> registros = new LinkedList<VORegistroListado>();
        try {
            PreparedStatement pstmt = getConnection(con).prepareStatement(consultas.listarRegistros());
            pstmt.setInt(1, ced);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int numero = rs.getInt("numero");
                String actividad = rs.getString("actividad");
                int nota = rs.getInt("nota");
                registros.add(new VORegistroListado(actividad, nota, numero));
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar los registros: " + e.getMessage());
        }
        return registros;
    }

    public double promedioNotas(IConexion con, int ced) throws PersistenciaException {
        double promedio = 0;
        try {
            PreparedStatement pstmt = getConnection(con).prepareStatement(consultas.promedioNotas());
            pstmt.setInt(1, ced);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Si el alumno no tiene notas, AVG devuelve NULL y getDouble da 0.0
                promedio = rs.getDouble("promedio");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al calcular el promedio: " + e.getMessage());
        }
        return promedio;
    }

    /** Devuelve null si no hay ningun alumno en la base. */
    public VOAlumnoProm alumnoMayorPromedio(IConexion con) throws PersistenciaException {
        VOAlumnoProm voA = null;
        try {
            Statement stmt = getConnection(con).createStatement();
            ResultSet rs = stmt.executeQuery(consultas.alumnoMayorPromedio());
            if (rs.next()) {
                int cedula = rs.getInt("cedula");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                double promedio = rs.getDouble("promedio");
                voA = new VOAlumnoProm(cedula, nombre, apellido, promedio);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al obtener el alumno con mayor promedio: " + e.getMessage());
        }
        return voA;
    }

    public int contarAlumnos(IConexion con, String act, int nota) throws PersistenciaException {
        int cant = 0;
        try {
            PreparedStatement pstmt = getConnection(con).prepareStatement(consultas.contarAlumnos());
            pstmt.setString(1, act);
            pstmt.setInt(2, nota);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                cant = rs.getInt("cantidad");
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al contar los alumnos: " + e.getMessage());
        }
        return cant;
    }

    /** Devuelve null si el alumno no tiene ninguna actividad registrada. */
    public VOActividad actividadMasCursada(IConexion con, int ced) throws PersistenciaException {
        VOActividad voAct = null;
        try {
            PreparedStatement pstmt = getConnection(con).prepareStatement(consultas.actividadMasCursada());
            pstmt.setInt(1, ced);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String actividad = rs.getString("actividad");
                int veces = rs.getInt("veces");
                voAct = new VOActividad(actividad, veces);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al obtener la actividad mas cursada: " + e.getMessage());
        }
        return voAct;
    }
}
