package ude.bdpractico5.persistencia.daos;

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


import ude.bdpractico5.logica.Registro;
import ude.bdpractico5.logica.excepciones.PersistenciaException;
import ude.bdpractico5.logica.valueObjects.VOActividad;
import ude.bdpractico5.logica.valueObjects.VORegistroListado;
import ude.bdpractico5.persistencia.consultas.Consultas;

public class DAORegistros 
{

    String url, usr, password;
    private int cedAlumno;

    public DAORegistros(int cedAlumno) throws PersistenciaException {
        this.cedAlumno = cedAlumno;

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("config.properties")) {
            props.load(in);

            Class.forName(props.getProperty("driver"));
            this.url = props.getProperty("url");
            this.usr = props.getProperty("user");
            this.password = props.getProperty("password");

        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer config.properties");
        } catch (ClassNotFoundException e) {
            throw new PersistenciaException("No se encontró el driver de la base de datos");
        }
    }


    private Connection abrirConexion() throws PersistenciaException {
        try {
            return DriverManager.getConnection (url, usr, password);
        }
        catch (SQLException e) { throw new PersistenciaException ("No se pudo ingresar a la bd"); }
    }

    private void cerrarConexion (Connection con) {
        try { if (con != null) con.close (); } catch (SQLException e) { }
    }

    public int largo() throws PersistenciaException {
        Connection con = abrirConexion ();
        int cantidad = 0;
        try
        {
            Consultas consultas = new Consultas ();
            PreparedStatement pstmt = con.prepareStatement (consultas.cantidadRegistros());
            pstmt.setInt (1, cedAlumno);
            ResultSet rs = pstmt.executeQuery ();
            if (rs.next ())
                cantidad = rs.getInt ("cantidad");
            rs.close ();
            pstmt.close ();
        }
        catch (SQLException e) { throw new PersistenciaException ("Error al devolver el largo"); }
        finally { cerrarConexion (con); }
        return cantidad;
    }


    public void insback(Registro reg) throws PersistenciaException {
        try
        { 
             Connection con = DriverManager.getConnection(url, usr, password);
             PreparedStatement pstmt = con.prepareStatement(new Consultas().insertarRegistro());
            pstmt.setInt(1, cedAlumno);
            pstmt.setInt(2, reg.getNumero());
            pstmt.setInt(3, reg.getNota());
            pstmt.setString(4, reg.getActividad());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error de acceso a los datos (insertar registro)");
        }
    }

    public Registro kesimo(int numR) throws PersistenciaException {
        Connection con = abrirConexion ();
        Registro reg = null;
        try
        {
            Consultas consultas = new Consultas ();
            PreparedStatement pstmt = con.prepareStatement (consultas.obtenerRegistro ());
            pstmt.setInt (1, cedAlumno);
            pstmt.setInt (2, numR);
            ResultSet rs = pstmt.executeQuery ();
            if (rs.next ())
                reg = new Registro (rs.getInt ("numero"), rs.getInt ("nota"), rs.getString ("actividad"));
            rs.close ();
            pstmt.close ();
        }
        catch (SQLException e) { throw new PersistenciaException ("Error al devolver el kesimo"); }
        finally { cerrarConexion (con); }
        return reg;
    }

    public List<VORegistroListado> listarRegistros() throws PersistenciaException {
        List<VORegistroListado> lista = new ArrayList<VORegistroListado>();
        try 
        {
        Connection con = DriverManager.getConnection(url, usr, password);
        PreparedStatement pstmt = con.prepareStatement(new Consultas().listarRegistros());
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

    public double promedioNotas() throws PersistenciaException {
        List<VORegistroListado> registros = listarRegistros ();
        if (registros.isEmpty ())
            return 0;

        int suma = 0;

        for (VORegistroListado r : registros)
            suma = suma + r.getNota ();
        
        return (double) suma / registros.size ();
    }

     public VOActividad actividadMasCursada() throws PersistenciaException {
        VOActividad act = null;
        try 
        {
            Connection con = DriverManager.getConnection(url, usr, password);
            PreparedStatement pstmt = con.prepareStatement(new Consultas().actividadMasCursada());
            pstmt.setInt(1, cedAlumno);
            try (ResultSet rs = pstmt.executeQuery();)
            {
                if (rs.next())
                    act = new VOActividad(rs.getString("actividad"), rs.getInt("cant"));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error de acceso a los datos (actividad más cursada)");
        }
        return act;
    }
}
