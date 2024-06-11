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
import mx.uv.fei.interfaces.IPreliminaryProject;

/**
 *
 * @author alexs
 */
public class PreliminaryProjectDAO implements IPreliminaryProject {

    /**
     * registra un nuevo anteproyecto en el sistema.
     * 
     * @param preliminaryProject el objeto de tipo anteproyecto.
     * @return 1 si el registro del anteproyecto fue exitoso, 0 si no lo fue.
     * @throws DAOException 
     */
    
    @Override
    public int createPreliminaryProject(PreliminaryProject preliminaryProject) throws DAOException {
        int result = -1;
        try {
            String query = "insert into anteproyecto(Nombre_An, Descripcion, lgac, Objetivo, "
                    + "Linea_investigacion, Perfil_estudiante, Duracion, Bibliografia, cupo,"
                    + "Notas, N_personalDirector,N_personalCodirector, Clave_CuerpoAcademico) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, preliminaryProject.getProjectName());
            statement.setString(2, preliminaryProject.getDescription());
            statement.setString(3, preliminaryProject.getLgac());
            statement.setString(4, preliminaryProject.getExpectedResults());
            statement.setString(5, preliminaryProject.getLineOfResearch());
            statement.setString(6, preliminaryProject.getStudentRequirements());
            statement.setInt(7, preliminaryProject.getDuration());
            statement.setString(8, preliminaryProject.getRecommendedBibliography());
            statement.setInt(9, preliminaryProject.getQuota());
            statement.setString(10, preliminaryProject.getNotes());
            statement.setInt(11, preliminaryProject.getDirectorNumber());
            statement.setInt(12, preliminaryProject.getCodirectorNumber());
            statement.setString(13, preliminaryProject.getKeyAcademicGroup());
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible registrar el anteproyecto", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * verifica si el anteproyecto esta registrado en el sistema para que al registrar
     * uno nuevo no se duplique (verifica por nombre del anteproyecto).
     * 
     * @param preliminaryProjectName nombre del anteproyecto.
     * @return true si el nombre del anteproyecto ya esta registrado, false si esto no es así.
     * @throws DAOException 
     */

    @Override
    public boolean isPreliminaryProjectExisting(String preliminaryProjectName) throws DAOException {
        boolean validatePreliminaryProject = false;
        try {
            String query = "SELECT * FROM proyecto.anteproyecto WHERE anteproyecto.Nombre_An like (?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, preliminaryProjectName);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                validatePreliminaryProject = true;
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible validar el anteproyecto en el sistema", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return validatePreliminaryProject;
    }
    
    /**
     * obtiene todos los anteproyectos que tenga un director en especifico registrado.
     * 
     * @param directorNumber número de personal del director.
     * @return List con los anteproyectos que tenga ese director, 
     * null si este no tiene.
     * @throws DAOException 
     */

    @Override
    public List<PreliminaryProject> getPreliminaryProjectsFromDirector(int directorNumber) throws DAOException {
        List<PreliminaryProject> listOfPreliminaryProject = new ArrayList<>();
        try {
            String query = "SELECT Nombre_An FROM proyecto.anteproyecto WHERE ID_Anteproyecto "
                    + "NOT IN (SELECT ID_Anteproyecto FROM proyecto.TrabajoRecepcional "
                    + "WHERE ID_Anteproyecto IS NOT NULL) AND N_personalDirector = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, directorNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                PreliminaryProject preliminaryProject = new PreliminaryProject();
                preliminaryProject.setProjectName(result.getString("Nombre_An"));
                listOfPreliminaryProject.add(preliminaryProject);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible conectarse a la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfPreliminaryProject;
    }

    /**
     * regresa el id del anteproyecto mediante el nombre de este.
     * 
     * @param preliminaryProjectName el nombre del anteproyecto.
     * @return regresa el id del anteproyecto, 0 si no se encontro un id asignado a 
     * ese nombre de anteproyecto.
     * @throws DAOException 
     */
    
    @Override
    public int getIdPreliminaryProject(String preliminaryProjectName) throws DAOException {
        int idPreliminaryProject = 0;
        try {
            String query = "SELECT ID_Anteproyecto FROM proyecto.anteproyecto where Nombre_An = (?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, preliminaryProjectName);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                idPreliminaryProject = result.getInt("ID_Anteproyecto");
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible conectarse a la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return idPreliminaryProject;
    }

    /**
     * regresa el nombre del anteproyecto que tenga un trabajo recepcional relacionado.
     * 
     * @param receptionalWorkName nombre del trabajo recepcional.
     * @return nombre del anteproyecto, null si el trabajo recepcional no tiene ningún 
     * anteproyecto asignado.
     * @throws DAOException 
     */
    
    @Override
    public String getPreliminaryProjectNameWithReceptionalWork(String receptionalWorkName) throws DAOException {
        PreliminaryProject preliminaryProject = new PreliminaryProject();
        try {
            String query = "SELECT proyecto.anteproyecto.Nombre_An FROM proyecto.anteproyecto "
                    + "inner join proyecto.trabajorecepcional "
                    + "on proyecto.anteproyecto.ID_Anteproyecto = proyecto.trabajorecepcional.ID_anteproyecto "
                    + "where proyecto.trabajorecepcional.Nombre =(?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, receptionalWorkName);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                preliminaryProject.setProjectName(result.getString("Nombre_An"));
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible conectarse a la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return preliminaryProject.getProjectName();
    }
    
    /**
     * regresa los anteproyectos que no han sido asignados para un director.
     * 
     * @param directorNumber número de personal del director.
     * @return List con todos los anteproyectos faltantes por asignar.
     * null si ya fueron asignados todos.
     * @throws DAOException 
     */

    @Override
    public List<PreliminaryProject> getPreliminaryProjectsForAssignament(int directorNumber) throws DAOException {
        List<PreliminaryProject> listOfPreliminaryProject = new ArrayList<>();
        try {
            String query = "SELECT a.Nombre_An, a.cupo FROM proyecto.anteproyecto a "
                    + "INNER JOIN proyecto.trabajorecepcional t ON t.ID_anteproyecto = a.ID_Anteproyecto "
                    + "WHERE t.N_personalDirector = (?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, directorNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                PreliminaryProject preliminaryProject = new PreliminaryProject();
                preliminaryProject.setProjectName(result.getString("Nombre_An"));
                preliminaryProject.setQuota(result.getInt("cupo"));
                listOfPreliminaryProject.add(preliminaryProject);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la informacion de los anteproyectos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfPreliminaryProject;
    }
    
    /**
     * regresa el id del trabajo recepcional que tenga asignado un anteproyecto.
     * 
     * @param idPreliminaryProject id del anteproyecto.
     * @return id del trabajo recepcional, 0 si no tiene un anteproyecto relacionado.
     * @throws DAOException 
     */

    @Override
    public int getReceptionalWorkWithPreliminaryProject(int idPreliminaryProject) throws DAOException {
        int idRecetionalWork = 0;
        try {
            String query = "Select ID_TrabajoRecepcional from proyecto.trabajorecepcional "
                    + "where proyecto.trabajorecepcional.ID_anteproyecto = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idPreliminaryProject);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                idRecetionalWork = result.getInt("ID_TrabajoRecepcional");
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible conectarse a la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return idRecetionalWork;
    }

    /**
     * modifica el cupo de un anteproyecto cuando se le asigna un estudiante.
     * 
     * @param idPreliminaryProject id del anteproyecto.
     * @return 1 si fue posible modificar el cupo del anteproyecto, 0 si esto no fue así.
     * @throws DAOException 
     */
    
    @Override
    public int modifyQuotaPreliminaryProject(int idPreliminaryProject) throws DAOException {
        int result;
        try {
            String query = "UPDATE proyecto.anteproyecto SET cupo = cupo - 1 where ID_Anteproyecto = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idPreliminaryProject);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible modificar el cupo del anteproyecto", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * obtiene el nombre de todos los anteproyectos que tenga un director.
     * 
     * @param directorNumber número de personal del director.
     * @return List con los nombres de los anteproyectos que tenga
     * ese director, null si no tiene asignado ninguno.
     * @throws DAOException 
     */

    @Override
    public List<PreliminaryProject> getNameProjectPerDirector(int directorNumber) throws DAOException {
        List<PreliminaryProject> listPreliminaryProjectName = new ArrayList<>();
        try {
            String query = "SELECT DISTINCT a.Nombre_An FROM proyecto.estudiante"
                    + " e inner join proyecto.usuario u on u.correo = e.correo inner join proyecto.anteproyecto"
                    + " a on e.ID_anteproyecto = a.ID_Anteproyecto where a.N_personalDirector = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, directorNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                PreliminaryProject preliminaryProject = new PreliminaryProject();
                preliminaryProject.setProjectName(result.getString("Nombre_An"));
                listPreliminaryProjectName.add(preliminaryProject);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de proyectos para el director ", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listPreliminaryProjectName;
    }

    /**
     * regresa una lista de todos los anteproyectos que tenga un codirector asignado.
     * 
     * @param codirectorNumber número de personal del codirector.
     * @return List con los nombres de los anteproyectos que tenga
     * un codirector asignado, null si el codirector no tiene anteproyectos asignados.
     * @throws DAOException 
     */
    
    @Override
    public List<PreliminaryProject> getNameProjectPerCodirector(int codirectorNumber) throws DAOException {
        List<PreliminaryProject> listPreliminaryProjectName = new ArrayList<>();
        try {
            String query = "select Nombre_An from proyecto.anteproyecto where N_personalCodirector = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, codirectorNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                PreliminaryProject preliminaryProject = new PreliminaryProject();
                preliminaryProject.setProjectName(result.getString("Nombre_An"));
                listPreliminaryProjectName.add(preliminaryProject);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de proyectos para el director ", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listPreliminaryProjectName;
    }

    /**
     * regresa toda la información de un anteproyecto dado su nombre.
     * 
     * @param title nombre del anteproyecto.
     * @return objeto de tipo anteproyecto con la información de este.
     * @throws DAOException 
     */
    
    @Override
    public PreliminaryProject getInformation(String title) throws DAOException {
        PreliminaryProject preliminaryProject = new PreliminaryProject();
        try {
            String query = "SELECT proyecto.anteproyecto.Nombre_An, descripcion, lgac, Objetivo, "
                    + "Linea_investigacion, Perfil_estudiante, Duracion, Bibliografia, cupo, Notas, "
                    + "proyecto.director.Nombre_director, proyecto.cuerpoacademico.Nombre_CA, "
                    + "proyecto.codirector.Nombre_Codirector FROM proyecto.anteproyecto "
                    + "inner join proyecto.cuerpoacademico on proyecto.anteproyecto.Clave_CuerpoAcademico = "
                    + "proyecto.cuerpoacademico.Clave inner join proyecto.codirector on "
                    + "proyecto.anteproyecto.N_personalCodirector = proyecto.codirector.Num_personalCodirector "
                    + "inner join proyecto.director on proyecto.anteproyecto.N_personalDirector = "
                    + "proyecto.director.Num_personalDirector inner join proyecto.profesor "
                    + "on proyecto.profesor.Num_personalProfesor = proyecto.codirector.Num_personalCodirector "
                    + "inner join proyecto.usuario on proyecto.usuario.correo = proyecto.profesor.correo where "
                    + "proyecto.anteproyecto.Nombre_An = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, title);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                preliminaryProject.setProjectName(result.getString("Nombre_An"));
                preliminaryProject.setDescription(result.getString("Descripcion"));
                preliminaryProject.setLgac(result.getString("lgac"));
                preliminaryProject.setExpectedResults(result.getString("Objetivo"));
                preliminaryProject.setLineOfResearch(result.getString("Linea_investigacion"));
                preliminaryProject.setStudentRequirements(result.getString("Perfil_estudiante"));
                preliminaryProject.setDuration(result.getInt("Duracion"));
                preliminaryProject.setRecommendedBibliography(result.getString("Bibliografia"));
                preliminaryProject.setQuota(result.getInt("Cupo"));
                preliminaryProject.setNotes(result.getString("Notas"));
                preliminaryProject.setAcademicGroupName(result.getString("Nombre_CA"));
                preliminaryProject.setCodirectorName(result.getString("Nombre_Codirector"));
                preliminaryProject.setDirectorName(result.getString("Nombre_director"));
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener el liso de anteproyectos para el CA lo valide", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return preliminaryProject;
    }

    /**
     * actualiza el estado que tenga un anteproyecto, si fue validado o rechazado.
     * 
     * @param state el estado del anteproyecto que se le asignará.
     * @param title nombre del anteproyecto a actualizar.
     * @return 1 si la actualización fue exitosa, 0 si no lo fue.
     * @throws DAOException 
     */
    
    @Override
    public int updateStatePreliminaryProject(String state, String title) throws DAOException {
        int result;
        try {
            String query = "update proyecto.anteproyecto set Estado = ? where Nombre_An = ? ";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, state);
            statement.setString(2, title);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible actualizar el estado del proyecto.", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * obtiene los anteproyectos que se le haya asignado a un cuerpo académico.
     * 
     * 
     * @param keyAcademicGroup la clave del cuerpo académico.
     * @return List con todos los anteproyectos que tenga ese cuerpo 
     * académico, null si no tiene asignado alguno.
     * @throws DAOException 
     */

    @Override
    public List<PreliminaryProject> getPreliminaryProjectsForAcademicGroup(String keyAcademicGroup) throws DAOException {
        List<PreliminaryProject> listOfPreliminaryProject = new ArrayList<>();
        try {
            String query = "Select Nombre_An, Estado from proyecto.anteproyecto where Clave_CuerpoAcademico = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, keyAcademicGroup);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                PreliminaryProject preliminaryProject = new PreliminaryProject();
                preliminaryProject.setProjectName(result.getString("Nombre_An"));
                preliminaryProject.setState(result.getString("Estado"));
                listOfPreliminaryProject.add(preliminaryProject);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de anteproyectos para el CA", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfPreliminaryProject;
    }
    
    /**
     * regresa todos los anteproyectos mediante el número de personal del director.
     * 
     * @param directorNumber número de personal del director.
     * @return List con todos los anteproyectos que haya hecho el 
     * director, null si no ha registrado alguno.
     * @throws DAOException 
     */

    @Override
    public List<PreliminaryProject> getAllPreliminaryProject(int directorNumber) throws DAOException {
        List<PreliminaryProject> listOfPreliminaryProject = new ArrayList<>();
        try {
            String query = "select Nombre_An, Estado from proyecto.anteproyecto where N_personalDirector = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, directorNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                PreliminaryProject preliminaryProject = new PreliminaryProject();
                preliminaryProject.setProjectName(result.getString("Nombre_An"));
                preliminaryProject.setState(result.getString("Estado"));
                listOfPreliminaryProject.add(preliminaryProject);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de anteproyectos para el director", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfPreliminaryProject;
    }
    
    /**
     * actualiza la información de un anteproyecto.
     * 
     * @param preliminaryProject el objeto de tipo anteproyecto con los datos del anteproyecto.
     * @param preliminaryProjectName el nombre del anteproyecto.
     * @return 1 si la modificación fue exitosa, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public int updatePreliminaryProject(PreliminaryProject preliminaryProject, String preliminaryProjectName) throws DAOException {
        int result = -1;
        try {
            String query = "UPDATE proyecto.anteproyecto set Nombre_An = ?, Descripcion = ?, lgac = ?, Objetivo = ?, "
                    + "Linea_investigacion = ?, Perfil_estudiante = ?, Duracion = ?, Bibliografia = ?, cupo = ?, Notas = ?, "
                    + "N_personalCodirector = ?, Clave_CuerpoAcademico = ?, Estado = ? WHERE Nombre_An = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, preliminaryProject.getProjectName());
            statement.setString(2, preliminaryProject.getDescription());
            statement.setString(3, preliminaryProject.getLgac());
            statement.setString(4, preliminaryProject.getExpectedResults());
            statement.setString(5, preliminaryProject.getLineOfResearch());
            statement.setString(6, preliminaryProject.getStudentRequirements());
            statement.setInt(7, preliminaryProject.getDuration());
            statement.setString(8, preliminaryProject.getRecommendedBibliography());
            statement.setInt(9, preliminaryProject.getQuota());
            statement.setString(10, preliminaryProject.getNotes());
            statement.setInt(11, preliminaryProject.getCodirectorNumber());
            statement.setString(12, preliminaryProject.getKeyAcademicGroup());
            statement.setString(13, "Propuesto");
            statement.setString(14, preliminaryProjectName);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible modificar el anteproyecto", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
}
