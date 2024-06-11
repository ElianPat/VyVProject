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
import mx.uv.fei.interfaces.IReceptionalWork;

/**
 *
 * @author aresj
 */
public class ReceptionalWorkDAO implements IReceptionalWork {

    /**
     * registra un nuevo anteproyecto a la base de datos.
     * 
     * @param receptionalWork objeto de tipo trabajo recepcional.
     * @param idPreliminaryProject id de un anteproyecto.
     * @param directorNumber número de personal del director.
     * @param synodalNumber número de personal del sinodal.
     * @return 1 si el registro fue exitoso, 0 si no lo fue.
     * @throws DAOException 
     */
    
    @Override
    public int registerReceptionalWork(ReceptionalWork receptionalWork, int idPreliminaryProject, int directorNumber, int synodalNumber) throws DAOException {
        int result;
        try {
            String query = "INSERT INTO proyecto.trabajorecepcional "
                    + "(Nombre, Fecha_inicio, ID_anteproyecto, N_personalDirector, N_personalSinodal) "
                    + "VALUES (?,?,?,?,?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, receptionalWork.getReceptionalWorkName());
            statement.setDate(2, receptionalWork.getStartDate());
            statement.setInt(3, idPreliminaryProject);
            statement.setInt(4, directorNumber);
            statement.setInt(5, synodalNumber);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible registrar el trabajo recepcional", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * muestra el nombre y la fecha de inicio de un trabajo recepcional de un director.
     * 
     * @param directorNumber número de personal de director.
     * @return List con todos los trabajos recepcionales que tenga
     * un director en especifico, null si no tiene ninguno.
     * @throws DAOException 
     */

    @Override
    public List<ReceptionalWork> showReceptionalWorkFromDirector(int directorNumber) throws DAOException {
        List<ReceptionalWork> listOfReceptionalWork = new ArrayList<>();
        try {
            String query = "SELECT Nombre, Fecha_inicio FROM proyecto.trabajorecepcional where N_personalDirector = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, directorNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                ReceptionalWork recepcionalWork = new ReceptionalWork();
                recepcionalWork.setReceptionalWorkName(result.getString("Nombre"));
                recepcionalWork.setStartDate(result.getDate("Fecha_inicio"));
                listOfReceptionalWork.add(recepcionalWork);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfReceptionalWork;
    }

    /**
     * obtiene el nombre de un director que tenga asignado el nombre de un trabajo
     * recepcional.
     * 
     * @param receptionalWorkName nombre de un trabajo recepcional.
     * @return el nombre del director que tenga un trabajo recepcional, null si 
     * el trabajo recepcional no esta asociado a un director.
     * @throws DAOException 
     */
    
    @Override
    public String getDirector(String receptionalWorkName) throws DAOException {
        Professor professor = new Professor();
        try {
            String query = "SELECT proyecto.usuario.Nombre FROM  proyecto.usuario INNER JOIN  proyecto.profesor "
                    + "ON proyecto.usuario.Correo = proyecto.profesor.correo inner join proyecto.director "
                    + "on proyecto.profesor.Num_personalProfesor = proyecto.director.Num_personalDirector "
                    + "inner join proyecto.trabajorecepcional "
                    + "on proyecto.trabajorecepcional.N_personalDirector = proyecto.director.Num_personalDirector "
                    + "where proyecto.trabajorecepcional.Nombre = (?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, receptionalWorkName);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                professor.setName(result.getString("Nombre"));
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return professor.getName();
    }

    
    /**
     * obtiene el nombre del sinodal asignado a un trabajo recepcional.
     * 
     * @param receptionalWorkName nombre del trabajo recepcional.
     * @return nombre del sinodal si tiene un trabajo recepcional asignado, null 
     * si esto no es así.
     * @throws DAOException 
     */
    
    @Override
    public String getSynodal(String receptionalWorkName) throws DAOException {
        Professor professor = new Professor();
        try {
            String query = "SELECT proyecto.usuario.Nombre FROM  proyecto.usuario "
                    + "INNER JOIN  proyecto.profesor ON proyecto.usuario.Correo = proyecto.profesor.correo "
                    + "inner join proyecto.sinodal "
                    + "on proyecto.profesor.Num_personalProfesor = proyecto.sinodal.Num_personalSinodal "
                    + "inner join proyecto.trabajorecepcional "
                    + "on proyecto.trabajorecepcional.N_personalSinodal = proyecto.sinodal.Num_personalSinodal "
                    + "where proyecto.trabajorecepcional.Nombre = (?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, receptionalWorkName);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                professor.setName(result.getString("Nombre"));
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return professor.getName();
    }

    /**
     * actualiza la información de un trabajo recepcional
     * 
     * @param receptionalWork objeto de tipo trabajo recepcional.
     * @param idPreliminaryProject id del anteproyecto asociado.
     * @param directorNumber número del director encargado del trabajo recepcional.
     * @param synodalNumber número de sinodal asociado.
     * @param idReceptionalWork id del trabajo recepcional.
     * @return 1 si el trabajo recepcional se modifico exitosamente, 0 si no es así.
     * @throws DAOException 
     */
    
    @Override
    public int updateReceptionalWork(ReceptionalWork receptionalWork, int idPreliminaryProject, int directorNumber, int synodalNumber, int idReceptionalWork) throws DAOException {
        int result;
        try {
            String query = "UPDATE proyecto.trabajorecepcional\n"
                    + "SET Nombre = (?), Fecha_inicio = (?), Fecha_fin = (?), ID_anteproyecto = (?),\n"
                    + "N_personalDirector = (?), N_personalSinodal = (?) WHERE ID_TrabajoRecepcional = (?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, receptionalWork.getReceptionalWorkName());
            statement.setDate(2, receptionalWork.getStartDate());
            statement.setDate(3, receptionalWork.getEndDate());
            statement.setInt(4, idPreliminaryProject);
            statement.setInt(5, directorNumber);
            statement.setInt(6, synodalNumber);
            statement.setInt(7, idReceptionalWork);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible actualizar los datos del trabajo recepcional", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }

    /**
     * regresa el id del trabajo recepcional pasandole el nombre de este.
     * 
     * @param receptionalWorkName nombre del trabajo recepcional.
     * @return id del trabajo recepcional si su nombre corresponde a uno valido, 
     * -1 si no es así.
     * @throws DAOException 
     */
    
    @Override
    public int getIdReceptionalWork(String receptionalWorkName) throws DAOException {
        int idReceptionalWork = 0;
        try {
            String query = "SELECT ID_TrabajoRecepcional FROM proyecto.trabajorecepcional where Nombre = ? ";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, receptionalWorkName);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                idReceptionalWork = result.getInt("ID_TrabajoRecepcional");
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return idReceptionalWork;
    }

    /**
     * obtiene la información de un trabajo recepcional mediante el id de este.
     * 
     * @param IdReceptionalWork id del trabajo recepcional.
     * @return regresa un objeto de tipo trabajo recepcional si el id colocado corresponde
     * a un trabajo recepcional, regresa nulo si esto no es así.
     * @throws DAOException 
     */
    
    @Override
    public ReceptionalWork getDataReceptionalWork(int IdReceptionalWork) throws DAOException {
        ReceptionalWork receptionalWork = new ReceptionalWork();
        try {
            String query = "SELECT Nombre, Fecha_inicio, Fecha_fin FROM proyecto.trabajorecepcional where ID_TrabajoRecepcional = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, IdReceptionalWork);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                receptionalWork.setReceptionalWorkName(result.getString("Nombre"));
                receptionalWork.setStartDate(result.getDate("Fecha_inicio"));
                receptionalWork.setEndDate(result.getDate("Fecha_Fin"));
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return receptionalWork;
    }

    /**
     * valida si un trabajo recepcional esta registrado en la base de datos, esto para
     * validar al momento de registrar que no se duplique el nombre del trabajo recepcional.
     * 
     * @param receptionalWorkName nombre del trabajo recepcional.
     * @param directorNumber número de personal del director.
     * @return true si el trabajo recepcional esta previamente registrado en la base 
     * de datos, false si esto no es así.
     * @throws DAOException 
     */
    
    @Override
    public boolean isReceptionalWorkIsExisting(String receptionalWorkName, int directorNumber) throws DAOException {
         boolean validateReceptionalWork = false;
        try {
            String query = "SELECT * From proyecto.trabajorecepcional t\n"
                    + "inner join proyecto.director d on t.N_personalDirector = d.Num_personalDirector\n"
                    + "where t.Nombre = ? and d.Num_personalDirector = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, receptionalWorkName);
            statement.setInt(2, directorNumber);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                validateReceptionalWork = true;
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible validar el trabajo recepcional", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return validateReceptionalWork;
    }
}
