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
import mx.uv.fei.interfaces.IDirector;

/**
 *
 * @author alexs
 */
public class DirectorDAO implements IDirector {

    /**
     * regresa todos los directores registrados
     * 
     * @return List con los directores registrados, null si no lo hay.
     * @throws DAOException 
     */
    
    @Override
    public List<Director> getAllDirector() throws DAOException {
        List<Director> listOfDirector = new ArrayList<>();
        try {
            String query = "SELECT proyecto.usuario.Nombre, proyecto.director.Num_personalDirector  \n"
                    + "FROM  proyecto.usuario \n"
                    + "INNER JOIN  proyecto.profesor\n"
                    + "ON proyecto.usuario.Correo = proyecto.profesor.correo\n"
                    + "inner join proyecto.director\n"
                    + "on proyecto.director.Num_personalDirector = proyecto.profesor.Num_personalProfesor";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Director director = new Director();
                director.setName(result.getString("Nombre"));
                director.setDirectorNumber(result.getInt("Num_personalDirector"));
                listOfDirector.add(director);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de directores", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfDirector;
    }
    
    /**
     * regresa la información del director
     * 
     * @param email el correco del director 
     * @return objeto director con todos sus datos, null si no tiene nada.
     * @throws DAOException 
     */

    @Override
    public Director getDirectorData(String email) throws DAOException {
        Director director = new Director();
        try {
            String query = "Select d.Num_personalDirector\n"
                    + "from proyecto.director d inner join proyecto.profesor p \n"
                    + "on p.Num_personalProfesor = d.Num_personalDirector Where p.correo = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                director.setDirectorNumber(result.getInt("Num_personalDirector"));
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible recuperar la informacion del director", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return director;
    }
}
