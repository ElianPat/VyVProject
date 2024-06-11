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
import mx.uv.fei.interfaces.IEducativeExperience;

/**
 *
 * @author alexs
 */
public class EducativeExperienceDAO implements IEducativeExperience {

    /**
     * se crea una nueva experiencia educativa
     * 
     * @param educativeExperience el objeto de la experiencia educativa
     * @return 1 si se registro exitosamente, 0 si no lo fue.
     * @throws DAOException 
     */
    
    @Override
    public int createEducativeExperience(EducativeExperience educativeExperience) throws DAOException {
        int result = -1;
        try {
            String query = "INSERT INTO experienciaeducativa(Materia) values (?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, educativeExperience.getEducativeExperienceName());
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible registrar la experiencia educativa", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * obtiene el id de la experiencia educativa
     * 
     * @param subject el nombre de la experiencia educativa
     * @return 1 si se regresa exitosamente el id de la experiencia educativa, 0 si no lo fue
     * @throws DAOException 
     */

    @Override
    public int getIdEducativeExperience(String subject) throws DAOException {
        int idEducativeExperience = -1;
        try {
            String query = "select ID_EE from proyecto.experienciaeducativa where Materia = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, subject);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                idEducativeExperience = result.getInt("ID_EE");
            }
        } catch (SQLException ex) {
            App.getLogger().fatal(ex.getMessage());
            throw new DAOException("No fue posible obtener el id de la materia ", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return idEducativeExperience;
    }
    
    /**
     * obtiene todas las experiencias educativas registradas.
     * 
     * @return List si hay experiencias educativas registradas, 
     * null si no lo hay.
     * @throws DAOException 
     */

    @Override
    public List<EducativeExperience> getAllEducativeExperience() throws DAOException {
        List<EducativeExperience> listOfEducativeExperience = new ArrayList<>();
        try {
            String query = "select * from experienciaeducativa";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                EducativeExperience educativeExperience = new EducativeExperience();
                educativeExperience.setEducativeExperienceName(result.getString("Materia"));
                listOfEducativeExperience.add(educativeExperience);
            }
        } catch (SQLException ex) {
            App.getLogger().fatal(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de experiencias educativas para curso", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfEducativeExperience;
    }
}
