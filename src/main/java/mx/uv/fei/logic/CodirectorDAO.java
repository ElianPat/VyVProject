/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.uv.fei.dataaccess.DataBaseManager;
import mx.uv.fei.gui.controller.App;
import mx.uv.fei.interfaces.ICodirector;

/**
 *
 * @author alexs
 */
public class CodirectorDAO implements ICodirector {

    /**
     * obtiene todos los codirectores registrados en la base de datos.
     * 
     * @return List si hay registros, null si no hay.
     * @throws DAOException 
     */
    
    @Override
    public List<Codirector> getAllCodirector() throws DAOException {
        List<Codirector> listOfCodirector = new ArrayList<>();
        try {
            String query = "SELECT proyecto.usuario.Nombre, proyecto.codirector.Num_personalCodirector  \n"
                    + "FROM  proyecto.usuario \n"
                    + "INNER JOIN  proyecto.profesor\n"
                    + "ON proyecto.usuario.Correo = proyecto.profesor.correo\n"
                    + "inner join proyecto.codirector\n"
                    + "on proyecto.codirector.Num_personalCodirector = proyecto.profesor.Num_personalProfesor";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Codirector codirector = new Codirector();
                codirector.setName(result.getString("Nombre"));
                codirector.setCodirectorNumber(result.getInt("Num_personalCodirector"));
                listOfCodirector.add(codirector);
            }
        } catch (SQLException ex) {
            App.getLogger().fatal(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de codirectores", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfCodirector;
    }

    /**
     * obtiene el número de un codirector.
     * 
     * @param name el nombre del codirector.
     * @return 1 si se regreso el número del codirector exitosamente, 0 si no lo fue.
     * @throws DAOException 
     */
    
    @Override
    public int getCodirectorNumber(String name) throws DAOException {
        int professorNumber = 0;
        try {
            String query = "select Num_personalCodirector from proyecto.codirector where Nombre_Codirector = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                professorNumber = result.getInt("Num_personalCodirector");
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return professorNumber;
    }
}
