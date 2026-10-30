package ude.obligatorio.persistencia.daos;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import ude.obligatorio.logica.Registro;
import ude.obligatorio.logica.excepciones.PersistenciaException;
import ude.obligatorio.logica.valueObjects.VOActividad;
import ude.obligatorio.logica.valueObjects.VORegistroListado;
import ude.obligatorio.persistencia.consultas.Consultas;
import ude.obligatorio.persistencia.poolConexiones.IConexion;
import ude.obligatorio.persistencia.poolConexiones.Conexion;

public class DAORegistros implements IDAORegistros {
    private int cedAlumno;

    public DAORegistros(int cedAlumno) throws PersistenciaException {
        this.cedAlumno = cedAlumno;
    }

    @Override 
    public int largo(IConexion icon) throws PersistenciaException {
        Connection con = ((Conexion) icon).getConnection();
        int cantidad = 0;
        
        try (PreparedStatement pstmt = con.prepareStatement (new Consultas().cantidadRegistros());) 
        {
            pstmt.setInt (1, cedAlumno);
            try (ResultSet rs = pstmt.executeQuery ()) {
                if (rs.next ())
                    cantidad = rs.getInt ("cantidad");
            }
        }
        catch (SQLException e) { 
            throw new PersistenciaException ("Error al devolver el largo"); 
        }

        return cantidad;
    }

    @Override 
    public void insback(IConexion icon, Registro reg) throws PersistenciaException {
        Connection con = ((Conexion) icon).getConnection();

        try (
             PreparedStatement pstmt = con.prepareStatement(new Consultas().insertarRegistro());)
        {
            pstmt.setInt(1, reg.getNumero());
            pstmt.setInt(2, cedAlumno);
            pstmt.setString(3, reg.getActividad());
            pstmt.setInt(4, reg.getNota());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error de acceso a los datos (insertar registro)");
        }
    }

    @Override 
    public Registro kesimo(IConexion icon, int numR) throws PersistenciaException {
        Connection con = ((Conexion) icon).getConnection();
        Registro reg = null;
        try (PreparedStatement pstmt = con.prepareStatement (new Consultas().obtenerRegistro ());) {
            pstmt.setInt (1, cedAlumno);
            pstmt.setInt (2, numR);
            try (ResultSet rs = pstmt.executeQuery ();) {
                if (rs.next ()) {
                    reg = new Registro (
                        rs.getInt ("numero"), 
                        rs.getInt ("nota"), 
                        rs.getString ("actividad"));
                }
            }
        }
        catch (SQLException e) { 
            throw new PersistenciaException ("Error al devolver el kesimo"); 
        }

        return reg;
    }

    @Override 
    public List<VORegistroListado> listarRegistros(IConexion icon) throws PersistenciaException {
        List<VORegistroListado> lista = new ArrayList<VORegistroListado>();
        Connection con = ((Conexion) icon).getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(new Consultas().listarRegistros()))
        {
            pstmt.setInt(1, cedAlumno);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next())
                    lista.add(
                            new VORegistroListado(
                                rs.getInt("numero"), 
                                rs.getInt("nota"), 
                                rs.getString("actividad")));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error de acceso a los datos (listar registros)");
        }
        return lista;
    }

    @Override 
    public double promedioNotas(IConexion icon) throws PersistenciaException {
        Connection con = ((Conexion) icon).getConnection();
        List<VORegistroListado> registros = listarRegistros(icon);
        
        if (registros.isEmpty ())
            return 0;

        int suma = 0;

        for (VORegistroListado r : registros)
            suma = suma + r.getNota ();
        
        return (double) suma / registros.size ();
    }

    @Override 
    public VOActividad actividadMasCursada(IConexion icon) throws PersistenciaException {
        VOActividad act = null;
        Connection con = ((Conexion) icon).getConnection();

        try (PreparedStatement pstmt = con.prepareStatement(new Consultas().actividadMasCursada())) {
            pstmt.setInt(1, cedAlumno);
            try (ResultSet rs = pstmt.executeQuery();)
            {
                if (rs.next())
                    act = new VOActividad(rs.getString("actividad"), rs.getInt("veces"));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error de acceso a los datos (actividad más cursada)");
        }
        return act;
    }
}
