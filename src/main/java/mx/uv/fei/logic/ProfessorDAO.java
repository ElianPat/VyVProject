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
import mx.uv.fei.interfaces.IProfessor;

/**
 *
 * @author alexs
 */
public class ProfessorDAO implements IProfessor {

    /**
     * obtiene el nombre de los profesores que tenga asignado un cuerpo académico.
     * 
     * @param keyAcademicGroup la clave del cuerpo académico.
     * @return List con los profesores que tenga un cuerpo 
     * académico, null si ese cuerpo académico no tiene ninguno.
     * @throws DAOException 
     */
    
    @Override
    public List<Professor> getProfessorNameOfAcademicGroup(String keyAcademicGroup) throws DAOException {
        List<Professor> listOfProfessor = new ArrayList<>();
        try {
            String query = "SELECT Nombre FROM proyecto.usuario INNER JOIN proyecto.profesor "
                    + "ON profesor.correo = usuario.correo WHERE NOT EXISTS "
                    + "(SELECT * FROM proyecto.cuerpoacademicoprofesor "
                    + "WHERE proyecto.cuerpoacademicoprofesor.Num_personal = proyecto.profesor.Num_personalProfesor)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Professor professor = new Professor();
                professor.setName(result.getString("Nombre"));
                listOfProfessor.add(professor);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener maestros relacionados con el cuerpo", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfProfessor;
    }
    
    /**
     * obtiene el número de personal de un profesor mediante su nombre.
     * 
     * @param name nombre del profesor.
     * @return número de personal del profesor, 0 si el nombre pasado no corresponde
     * a ningun número de personal.
     * @throws DAOException 
     */

    @Override
    public int getProfessorNumber(String name) throws DAOException {
        int professorNumber = 0;
        try {
            String query = "SELECT Num_personalProfesor FROM proyecto.profesor "
                    + "inner Join proyecto.usuario on proyecto.usuario.correo = proyecto.profesor.correo "
                    + "where proyecto.usuario.Nombre = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                professorNumber = result.getInt("Num_personalProfesor");
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return professorNumber;
    }
    
    /**
     * obtiene el nombre y el rol del profesor que este asignado a un cuerpo académico.
     * 
     * @param keyAcademicGroup la clave del cuerpo académico.
     * @return List con los nombres y roles de los profesores del cuerpo 
     * académico, null si no tiene profesores.
     * @throws DAOException 
     */

    @Override
    public List<Professor> getAllProfessorOfAcademicGroup(String keyAcademicGroup) throws DAOException {
        List<Professor> listOfProfessor = new ArrayList<>();
        try {
            String query = "SELECT Rol, Nombre FROM proyecto.cuerpoacademicoprofesor "
                    + "inner join proyecto.profesor "
                    + "on proyecto.profesor.Num_personalProfesor = proyecto.cuerpoacademicoprofesor.Num_personal "
                    + "inner join proyecto.usuario on proyecto.usuario.correo = proyecto.profesor.correo "
                    + "where clave_CA = (?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, keyAcademicGroup);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Professor professor = new Professor();
                professor.setRole(result.getString("Rol"));
                professor.setName(result.getString("Nombre"));
                listOfProfessor.add(professor);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfProfessor;
    }
    
    /**
     * regresa la información de los miembros de un cuerpo académico pasandole el nombre del miembro 
     * y la clave del cuerpo académico.
     * 
     * @param name nombre del profesor.
     * @param keyAcademicGroup clave del cuerpo académico.
     * @return regresa un objeto de tipo profesor con la información del profesor que este 
     * en un cuerpo académico, null si el profesor no corresponde a un cuerpo académico indicado.
     * @throws DAOException 
     */

    @Override
    public Professor modifyMembersAcademicGroup(String name, String keyAcademicGroup) throws DAOException {
        Professor professor = new Professor();
        try {
            String query = "SELECT Rol, Nombre FROM proyecto.cuerpoacademicoprofesor "
                    + "inner join proyecto.profesor "
                    + "on proyecto.profesor.Num_personalProfesor = proyecto.cuerpoacademicoprofesor.Num_personal "
                    + "inner join proyecto.usuario on proyecto.usuario.correo = proyecto.profesor.correo "
                    + "where clave_CA = (?) and Nombre = (?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, keyAcademicGroup);
            statement.setString(2, name);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                professor.setRole(result.getString("Rol"));
                professor.setName(result.getString("Nombre"));
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return professor;
    }

    /**
     * obtiene todos los sinodales.
     * 
     * @return List con los sinodales registrados, null si no hay.
     * @throws DAOException 
     */
    
    @Override
    public List<Professor> getSynodal() throws DAOException {
        List<Professor> listOfSynodal = new ArrayList<>();
        try {
            String query = "SELECT Nombre FROM proyecto.sinodal "
                    + "inner join proyecto.profesor "
                    + "on proyecto.profesor.Num_personalProfesor = proyecto.sinodal.Num_personalSinodal "
                    + "inner join proyecto.usuario on proyecto.usuario.correo = proyecto.profesor.correo";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Professor synodal = new Professor();
                synodal.setName(result.getString("Nombre"));
                listOfSynodal.add(synodal);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfSynodal;
    }
    
    /**
     * obtiene todos los directores registrados.
     * 
     * @return List con todos los directores, null si no hay.
     * @throws DAOException 
     */

    @Override
    public List<Professor> getDirector() throws DAOException {
        List<Professor> listOfDirector = new ArrayList<>();
        try {
            String query = "SELECT Nombre FROM proyecto.director inner join proyecto.profesor "
                    + "on proyecto.profesor.Num_personalProfesor = proyecto.director.Num_personalDirector "
                    + "inner join proyecto.usuario on proyecto.usuario.correo = proyecto.profesor.correo;";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Professor director = new Professor();
                director.setName(result.getString("Nombre"));
                listOfDirector.add(director);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfDirector;
    }
    
    /**
     * regresa el número de personal de un profesor mediante su email.
     * 
     * @param email correo electronico del proesor.
     * @return número de personal de profesor, 0 si no corresponde a níngun profesor el correo.
     * @throws DAOException 
     */

    @Override
    public int getProfessorNumberByEmail(String email) throws DAOException {
        int professorNumber = 0;
        try {
            String query = "SELECT p.Num_personalProfesor FROM proyecto.profesor p "
                    + "inner join proyecto.usuario u on u.correo = p.correo where u.correo = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                professorNumber = result.getInt("Num_personalProfesor");
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return professorNumber;
    }

    /**
     * regresa toda la información de un profesor mediante su correo.
     * 
     * @param email correo electronico del profesor.
     * @return objeto de tipo profesor con toda su información, null si el correo 
     * pasado no corresponde a un profesor.
     * @throws DAOException 
     */
    
    @Override
    public Professor getInformation(String email) throws DAOException {
        Professor professor = new Professor();
        try {
            String query = "Select Nombre_Profesor, Num_personalProfesor from proyecto.profesor where correo = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, email);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                professor.setName(result.getString("Nombre_Profesor"));
                professor.setProfessorNumber(result.getInt("Num_personalProfesor"));
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible recuperar la informacion del coordinador del cuerpo academico", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return professor;
    }

    /**
     * obtiene el email de un director pasandole el nombre de un anteproyecto.
     * 
     * @param title nombre del anteproyecto.
     * @return regresa el correo del profesor, null si no tiene el nombre del anteproyecto
     * un correo. 
     * @throws DAOException 
     */
    
    @Override
    public String getEmailDirector(String title) throws DAOException {
        String email = "";
        try {
            String query = "select u.correo from proyecto.usuario u inner join proyecto.profesor p on u.correo = "
                    + "p.correo inner join proyecto.director d on p.Num_personalProfesor = d.Num_personalDirector "
                    + "inner join proyecto.anteproyecto a on a.N_personalDirector = d.Num_personalDirector "
                    + "where a.Nombre_an = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, title);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                email = result.getString("correo");
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible recuperar el correo del director", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return email;
    }
    
    /**
     * registra un nuevo profesor al sistema.
     * 
     * @param professor objeto tipo profesor.
     * @return 1 si el registro fue exitoso, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public int registerProfessor(Professor professor) throws DAOException {
        int result;
        try {
            String query = "insert into profesor (Num_personalProfesor, correo, Nombre_Profesor) values (?,?,?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, professor.getProfessorNumber());
            statement.setString(2, professor.getEmail());
            statement.setString(3, professor.getName());
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible registrar el profesor", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }

    /**
     * registra un director nuevo al sistema.
     * 
     * @param professor objeto de tipo profesor.
     * @return 1 si el registro fue exitoso, 0 si no lo fue.
     * @throws DAOException 
     */
    
    @Override
    public int registerDirector(Professor professor) throws DAOException {
        int result;
        try {
            String query = "insert into director (Num_personalDirector, Nombre_Director) values (?,?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, professor.getProfessorNumber());
            statement.setString(2, professor.getName());
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible registrar el director", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * registra un codirector nuevo al sistema.
     * 
     * @param professor objeto de tipo profesor.
     * @return 1 si el registro fue exitoso, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public int registerCodirector(Professor professor) throws DAOException {
        int result;
        try {
            String query = "insert into codirector (Num_personalCodirector, Nombre_Codirector) values (?,?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, professor.getProfessorNumber());
            statement.setString(2, professor.getName());
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible registrar el codirector", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * registra un nuevo sinodal a la base de datos.
     * 
     * @param professor objeto de tipo profesor.
     * @return 1 si el registro del sinodal fue exitoso, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public int registerSynodal(Professor professor) throws DAOException {
        int result;
        try {
            String query = "insert into sinodal (Num_personalSinodal, Nombre_Sinodal) values (?,?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, professor.getProfessorNumber());
            statement.setString(2, professor.getName());
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible registrar el sinodal", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }

    /**
     * regresa el nombre y número de personal del profesor a modificar 
     * 
     * @return List con la información del profesor, null si no hay profesores.
     * @throws DAOException 
     */
    
    @Override
    public List<Professor> getProfessorToModify() throws DAOException {
        List<Professor> listOfProfessor = new ArrayList<>();
        try {
            String query = "select Nombre, Num_personalProfesor from  proyecto.usuario INNER JOIN  "
                    + "proyecto.profesor ON proyecto.usuario.correo = proyecto.profesor.correo";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Professor professor = new Professor();
                professor.setName(result.getString("Nombre"));
                professor.setProfessorNumber(result.getInt("Num_personalProfesor"));
                listOfProfessor.add(professor);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible recuperar los profesores para modificar", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfProfessor;
    }

    /**
     * regresa la información del profesor para poder modificar.
     * 
     * @param professorNumber número de personal del profesor.
     * @return objeto de tipo profesor con su información, null si el número de personal 
     * no corresponde a un profesor valido.
     * @throws DAOException 
     */
    
    @Override
    public Professor getInfoToModifyProfessor(int professorNumber) throws DAOException {
        Professor professor = new Professor();
        try {
            String query = "SELECT u.Nombre, p.Num_personalProfesor, p.correo, u.Habilitado FROM proyecto.usuario u "
                    + "INNER JOIN proyecto.profesor p ON u.correo = p.correo WHERE p.Num_personalProfesor = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, professorNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                professor.setName(result.getString("Nombre"));
                professor.setProfessorNumber(result.getInt("Num_personalProfesor"));
                professor.setEmail(result.getString("correo"));
                professor.setEnable(result.getInt("Habilitado"));
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible recuperar la info del profesor a modificar", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return professor;
    }
    
    /**
     * actualiza la información de un profesor.
     * 
     * @param professor objeto de tipo de profesor.
     * @param email el correo electronico del profesor a modificar.
     * @return 1 si la modificación fue exitosa, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public int updateProfessor(Professor professor, String email) throws DAOException {
        int result;
        try {
            String query = "UPDATE proyecto.profesor set Num_personalProfesor = ?, Nombre_Profesor = ? WHERE correo = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, professor.getProfessorNumber());
            statement.setString(2, professor.getName());
            statement.setString(3, email);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible actualizar el numP de profesor en profesor", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }

    /**
     * regresa el nombre de todos los profesores registrados en la base de datos.
     * 
     * @return List con los nombres del profesor, null si no hay profesores.
     * @throws DAOException 
     */
    
    @Override
    public List<Professor> getAllProfessor() throws DAOException {
        List<Professor> listOfProfessor = new ArrayList<>();
        try {
            String query = "select Nombre  from  proyecto.usuario INNER JOIN  proyecto.profesor "
                    + "ON proyecto.usuario.correo = proyecto.profesor.correo";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Professor professor = new Professor();
                professor.setName(result.getString("Nombre"));
                listOfProfessor.add(professor);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener los profesores para registrar curso", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfProfessor;
    }

    /**
     * valida que el número de personal pasado no este registrado en la base de datos.
     * Esto con la finalidad de no duplicar el número de personal.
     * 
     * @param personalNumber número de personal del profesor.
     * @return true si el número de personal ya se encuentra en la base de datos, false si 
     * no es así.
     * @throws DAOException 
     */
    
    @Override
    public boolean isPersonalNumberExisting(int personalNumber) throws DAOException {
        boolean validatePersonalNumber = false;
        try {
            String query = "select * from proyecto.profesor where Num_personalProfesor like (?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, personalNumber);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                validatePersonalNumber = true;
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible validar el numero de personal en el sistema", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return validatePersonalNumber;
    }
}
