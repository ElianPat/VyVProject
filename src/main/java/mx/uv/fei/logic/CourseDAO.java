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
import mx.uv.fei.interfaces.ICourse;

/**
 *
 * @author aresj
 */
public class CourseDAO implements ICourse {

    /**
     * regresa todos los nrc de todos los cursos
     * 
     * @return List con todos los nrc, null si no hay.
     * @throws DAOException 
     */
    
    @Override
    public List<Course> getAllNrc() throws DAOException {
        List<Course> listOfCourse = new ArrayList<>();
        try {
            String query = "SELECT NRC FROM proyecto.curso";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Course course = new Course();
                course.setNrc(result.getString("NRC"));
                listOfCourse.add(course);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfCourse;
    }

    /**
     * obtiene todos los cursos que tenga un profesor a su cargo.
     * 
     * @param personalNumber número del personal del profesor.
     * @return List con todos los cursos que tenga un profesor, null si no tiene cursos
     * @throws DAOException 
     */
    
    @Override
    public List<Course> getCoursesFromProfessor(int personalNumber) throws DAOException {
        List<Course> listOfCourse = new ArrayList<>();
        try {
            String query = "SELECT NRC FROM proyecto.curso where Num_personal = ? ";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, personalNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Course course = new Course();
                course.setNrc(result.getString("NRC"));
                listOfCourse.add(course);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfCourse;
    }
    
    /**
     * obtiene todos los cursos que hay.
     * 
     * @return List con todos los cursos, null si no hay cursos.
     * @throws DAOException 
     */

    @Override
    public List<Course> getAllCourse() throws DAOException {
        List<Course> listOfCourse = new ArrayList<>();
        String query = "SELECT * FROM proyecto.curso";
        DataBaseManager dataBaseManager = new DataBaseManager();
        try {
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Course course = new Course();
                course.setNrc(result.getString("NRC"));
                listOfCourse.add(course);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener los datos del curso", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfCourse;
    }
    
    /**
     * regresa los nrc que tenga un profesor a su cargo.
     * 
     * @param personalNumber el número de personal del profesor.
     * @return List con los nrc del profesor, null si no tiene.
     * @throws DAOException 
     */

    @Override
    public List<Course> getNrcPerProfessor(int personalNumber) throws DAOException {
        List<Course> listOfNrc = new ArrayList<>();
        try {
            String query = "SELECT NRC FROM proyecto.curso where Num_personal = ? ";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, personalNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Course course = new Course();
                course.setNrc(result.getString("NRC"));
                listOfNrc.add(course);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de NRC para el profesor", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfNrc;
    }
    
    /**
     * registra un curso con toda la información.
     * 
     * @param course objeto curso
     * @return 1 si el curso se registro exitosamente, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public int addCourse(Course course) throws DAOException {
        int result = -1;
        try {
            String query = "insert into curso  (NRC, Periodo,Experiencia_Educativa, Num_personal) values (?,?,?,?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, course.getNrc());
            statement.setString(2, course.getPeriod());
            statement.setInt(3, course.getIdEducativeExperience());
            statement.setInt(4, course.getProfessorNumber());
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible registrar curso", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }

    /**
     * obtiene la información de todos los cursos para poder modificarlos.
     * 
     * @return List con todos los cursos, null si no hay.
     * @throws DAOException 
     */
    
    @Override
    public List<Course> getCoursesToModify() throws DAOException {
        List<Course> listOfCourse = new ArrayList<>();
        try {
            String query = "SELECT NRC, Materia from proyecto.curso inner JOIN proyecto.experienciaeducativa ON "
                    + "proyecto.curso.Experiencia_Educativa = proyecto.experienciaeducativa.ID_EE";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Course course = new Course();
                course.setNrc(result.getString("NRC"));
                course.setSubject(result.getString("Materia"));
                listOfCourse.add(course);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de cursos registrados", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfCourse;
    }
    
    /**
     * actualiza un curso en especifico.
     * 
     * @param course objeto curso a modificar.
     * @param nrc nrc que tenga el curso.
     * @return 1 si la actualización fue exitosa, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public int updateCourse(Course course, String nrc) throws DAOException {
        int result = -1;
        try {
            String query = "UPDATE proyecto.curso set NRC =?, Periodo=?,Experiencia_Educativa =?, "
                    + "Num_personal =?  WHERE NRC =?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, course.getNrc());
            statement.setString(2, course.getPeriod());
            statement.setInt(3, course.getIdEducativeExperience());
            statement.setInt(4, course.getProfessorNumber());
            statement.setString(5, nrc);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible actualizar el curso", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * obtiene la información que tenga un curso.
     * 
     * @param nrc el nrc que tenga el curso.
     * @return objeto curso con la información de este, null si no tiene.
     * @throws DAOException 
     */

    @Override
    public Course getInfoCourse(String nrc) throws DAOException {
        Course course = new Course();
        try {
            String query = "SELECT curso.NRC, curso.Periodo,proyecto.experienciaeducativa.Materia, usuario.Nombre FROM proyecto.curso INNER JOIN proyecto.experienciaeducativa ON proyecto.experienciaeducativa.ID_EE = proyecto.curso.Experiencia_Educativa INNER JOIN proyecto.profesor ON proyecto.curso.Num_personal = proyecto.profesor.Num_personalProfesor INNER JOIN proyecto.usuario ON proyecto.usuario.correo = proyecto.profesor.correo WHERE proyecto.curso.NRC = ? ";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nrc);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                course.setNrc(result.getString("NRC"));
                course.setPeriod(result.getString("Periodo"));
                course.setSubject(result.getString("Materia"));
                course.setProfessor(result.getString("Nombre"));
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible actualizar el curso", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return course;
    }
    
    /**
     * valida que exita un curso registrado en la base de datos, esto para que cuando
     * se registre uno no se duplique.
     * 
     * @param nrc nrc del curso.
     * @return true si el curso ya estaba registrado, false si esto no es así.
     * @throws DAOException 
     */

    @Override
    public boolean isNrcExisting(String nrc) throws DAOException {
        boolean validateNrc = false;
        try {
            String query = "select * from curso where NRC like (?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nrc);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                validateNrc = true;
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible validar el usuario en el sistema", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return validateNrc;
    }

    /**
     * regresa los periodos que hay.
     * 
     * @return List con los periodos, null si no hay periodos registrados.
     * @throws DAOException 
     */
    
    @Override
    public List<Course> getPeriod() throws DAOException {
        List<Course> listOfPeriods = new ArrayList<>();
        try {
            String query = "SELECT periodoEscolar FROM proyecto.periodo";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Course course = new Course();
                course.setPeriod(result.getString("periodoEscolar"));
                listOfPeriods.add(course);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista de peridos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfPeriods;
    }
}
