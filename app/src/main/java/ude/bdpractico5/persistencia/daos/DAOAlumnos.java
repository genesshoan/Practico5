package ude.bdpractico5.persistencia.daos;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import ude.bdpractico5.logica.Alumno;
import ude.bdpractico5.logica.excepciones.PersistenciaException;
import ude.bdpractico5.logica.valueObjects.VOAlumnoProm;
import ude.bdpractico5.persistencia.consultas.Consultas;

public class DAOAlumnos {

    String url, usr, password;

    public DAOAlumnos() throws PersistenciaException {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("config.properties")) {
            props.load(in);

            this.url = props.getProperty("url");
            this.usr = props.getProperty("user");
            this.password = props.getProperty("password");

        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer config.properties");
        }
    }

    public boolean member(int ced) throws PersistenciaException {
        boolean existeCedula = false;
        try (Connection con = DriverManager.getConnection(url, usr, password);
            PreparedStatement pstmt = con.prepareStatement(new Consultas().existeAlumno());) {
                           
            pstmt.setInt(1, ced);

            try (ResultSet rs = pstmt.executeQuery();) {
                existeCedula = rs.next();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error de acceso a los datos (member alumno)");
        }

        return existeCedula;
    }

    public void insert(Alumno alumno) throws PersistenciaException {
        try (Connection con = DriverManager.getConnection(url, usr, password);
            PreparedStatement pstmt = con.prepareStatement(new Consultas().insertarAlumno());) {
            
            pstmt.setInt(1, alumno.getCedula());
            pstmt.setString(2, alumno.getNombre());
            pstmt.setString(3, alumno.getApellido());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error de acceso a los datos (insert)");
        }
    }

    public Alumno find(int cedA) throws PersistenciaException {
        Alumno alu = null;

        try (Connection con = DriverManager.getConnection(url, usr, password);
            PreparedStatement pstmt = con.prepareStatement(new Consultas().obtenerAlumno());) {

            pstmt.setInt(1, cedA);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) alu = new Alumno(rs.getInt("cedula"), rs.getString("nombre"), rs.getString("apellido"));


        } catch (SQLException e) {
            throw new PersistenciaException("Error al obtener alumno");
        }

        return alu;
    }

    public boolean esVacio() throws PersistenciaException {
        boolean vacio = true;
        
        try (Connection con = DriverManager.getConnection(url, usr, password);
            PreparedStatement pstmt = con.prepareStatement(new Consultas().cantidadAlumnos());
            ResultSet rs = pstmt.executeQuery();) {
            
            if (rs.next()) vacio = rs.getInt(1) == 0;

        } catch (SQLException e) {
            throw new PersistenciaException("Error de acceso a los datos (esVacio)");
        }

        return vacio;
    }

    public List<Alumno> listarAlumnos() throws PersistenciaException {
        List<Alumno> lista = new LinkedList<Alumno>();

        try (Connection con = DriverManager.getConnection(url, usr, password);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(new Consultas().listarAlumnos());) {
            
            while (rs.next()) {
                lista.add(new Alumno(
                    rs.getInt("cedula"), 
                    rs.getString("nombre"), 
                    rs.getString("apellido")));
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error de acceso a los datos (listarAlumno())");
        }

        return lista;
    }

    public VOAlumnoProm alumnoMayorPromedio() throws PersistenciaException {
        Alumno mejor = null;
        double mayor = 0;
        List<Alumno> alumnos = listarAlumnos();
        try (Connection con =  DriverManager.getConnection(url, usr, password);
            PreparedStatement pstmt = con.prepareStatement(new Consultas().promedioNotas())) {

            for (Alumno a : alumnos) {
                double prom;
                pstmt.setInt(1, a.getCedula());

                try (ResultSet rs = pstmt.executeQuery()) {
                    prom = rs.next() ? rs.getDouble("promedio") : 0;
                }

                if (mejor == null || prom > mayor) {
                    mejor = a;
                    mayor = prom;
                }
            }
        } catch (SQLException e) {
           throw new PersistenciaException("Error al devolver el mayor promedio)");
        }

        if (mejor == null) return null;
        
        return new VOAlumnoProm(mejor.getCedula(), mejor.getNombre(), mejor.getApellido(), mayor);
    }

    public int contarAlumnos(String act, int nota) throws PersistenciaException {
        int cant = 0;
        try (Connection con = DriverManager.getConnection(url, usr, password);
            PreparedStatement pstmt = con.prepareStatement(new Consultas().contarAlumnos());) {

            pstmt.setString(1, act);
            pstmt.setInt(2, nota);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) cant = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error de acceso a los datos (contar alumnos)");
        }
        return cant;
    }
}
