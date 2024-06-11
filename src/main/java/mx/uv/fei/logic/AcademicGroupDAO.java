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
import mx.uv.fei.interfaces.IAcademicGroup;

/**
 *
 * @author alexs
 */
public class AcademicGroupDAO implements IAcademicGroup {

    /**
     * Recupera todas las claves y los nombres del cuerpo académico. 
     *
     * @return List si existen registros en la base de datos, null si no lo hay.
     * @throws DAOException
     */
    
    @Override
    public List<AcademicGroup> getAllAcademicGroup() throws DAOException {
        List<AcademicGroup> listOfAcademicGroup = new ArrayList<>();
        try {
            String query = "SELECT * FROM  proyecto.cuerpoacademico";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                AcademicGroup academicGroup = new AcademicGroup();
                academicGroup.setKey(result.getString("Clave"));
                academicGroup.setNameAcademicGroup(result.getString("Nombre_CA"));
                listOfAcademicGroup.add(academicGroup);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la lista del grupo academico", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfAcademicGroup;
    }
    
    /**
     * Registra las claves y nombres de cuerpos académicos.
     * 
     * @param academicGroup El objeto cuerpo académico a registrar.
     * @return 1 si el registro del cuerpo académico es exitoso, 0 si no lo es.
     * @throws DAOException 
     */

    @Override
    public int registerAcademicGroup(AcademicGroup academicGroup) throws DAOException {
        int result;
        try {
            String query = "insert into cuerpoacademico(Clave, Nombre_CA) values (?,?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, academicGroup.getKey());
            statement.setString(2, academicGroup.getNameAcademicGroup());
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible agregar el cuerpo academico", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * Añade los profesores que se necesiten en el cuerpo académico.
     * 
     * @param academicGroup El objeto del cuerpo académico a agregar.
     * @param professor El objeto profesor que se añadirá al cuerpo académico.
     * @return 1 si registro fue exitoso, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public int addProfessorInAcademicGroup(AcademicGroup academicGroup, Professor professor) throws DAOException {
        int result;
        try {
            String query = "INSERT INTO proyecto.cuerpoacademicoprofesor ( Rol, clave_CA, Num_personal) VALUES (?,?,?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, professor.getRole());
            statement.setString(2, academicGroup.getKey());
            statement.setInt(3, professor.getProfessorNumber());
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible agregar al profesor en el cuerpo academico", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * Verifica que un cuerpo académico esta previamente registrado para así
     * validarlo en el controlador y que no se añadan cuerpos académicos que 
     * esten registrados.
     * 
     * @param keyAcademicGroup la clave del cuerpo académico
     * @return true si el cuerpo académico ya esta registrado, false si no lo esta.
     * @throws DAOException 
     */

    @Override
    public boolean isAcademicGroupExisting(String keyAcademicGroup) throws DAOException {
        boolean validateAcademicGroup = false;
        try {
            String query = "select * from proyecto.cuerpoacademico where Clave = (?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, keyAcademicGroup);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                validateAcademicGroup = true;
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible conectar con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return validateAcademicGroup;
    }
    
    /***
     * Elimina un profesor que este registrado en algún cuerpo académico
     * 
     * @param personalNumber el número de personal del profesor a eliminar
     * @param keyAcademicGroup la clave del cuerpo académico donde este el profesor
     * a eliminar.
     * @return 1 si el proceso de borrado fue exitoso, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public int deleteProfessorOfAcademicGroup(int personalNumber, String keyAcademicGroup) throws DAOException {
        int result;
        try {
            String query = "DELETE FROM proyecto.cuerpoacademicoprofesor WHERE Num_Personal = (?) and clave_CA =(?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, personalNumber);
            statement.setString(2, keyAcademicGroup);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible eliminar al miembro del cuerpo academico", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * Se modifica el rol de un profesor que este en un cuerpo académico.
     * 
     * @param professor el objeto profesor que se modificará.
     * @param personalNumber el número del personal del profesor.
     * @param keyAcademicGroup la clave del cuerpo académico donde esta el profesor
     * a modificar.
     * @return 1 si el proceso de modificar fue exitoso, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public int modifyMember(Professor professor, int personalNumber, String keyAcademicGroup) throws DAOException {
        int result;
        try {
            String query = "UPDATE proyecto.cuerpoacademicoprofesor SET Rol = (?), Num_personal = (?) "
                    + "WHERE Num_personal = (?) and clave_CA=(?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, professor.getRole());
            statement.setInt(2, professor.getProfessorNumber());
            statement.setInt(3, personalNumber);
            statement.setString(4, keyAcademicGroup);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible actualizar los datos del miembro del cuerpo academico", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * Obtiene la clave del cuerpo académico donde este un profesor.
     * 
     * @param personalNumber el número de personal del profesor
     * @return la clave del cuerpo académico si fue exitoso, null si no lo fue.
     * @throws DAOException 
     */

    @Override
    public String getKeyAcademicGroup(int personalNumber) throws DAOException {
        String resultKey = "";
        try {
            String query = "select clave_CA from proyecto.cuerpoacademicoprofesor where Num_personal = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, personalNumber);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                resultKey = result.getString("clave_CA");
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener la clave del cuerpo academico", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return resultKey;
    }
    
    /**
     * Verifica que un profesor sea miembro de algún cuerpo académico.
     * 
     * @param personalNumber el número de personal de profesor que se verificará.
     * @param keyAcademicGroup la clave del cuerpo académico que se verificará.
     * @return true si el profesor esta en el cuerpo académico, false si no es miembro.
     * @throws DAOException 
     */

    @Override
    public boolean isProfessorMember(int personalNumber, String keyAcademicGroup) throws DAOException {
        boolean validateMember = false;
        try {
            String query = "SELECT ID_miembro FROM proyecto.cuerpoacademicoprofesor where Num_personal = ? and clave_CA= ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, personalNumber);
            statement.setString(2, keyAcademicGroup);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                validateMember = true;
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible validar si el miembro ya existe en el CA", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return validateMember;
    }
    
    /**
     * Obtiene la clave del cuerpo académico pasandole como parámetro el nombre de este.
     * 
     * @param name nombre del cuerpo académico.
     * @return la clave del cuerpo académico, null si no lo encontro. 
     * @throws DAOException 
     */

    @Override
    public String getKeyAcademicGroupByName(String name) throws DAOException {
        String keyAcademicGroup = "";
        try {
            String query = "select Clave from proyecto.cuerpoacademico where Nombre_CA =  ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, name);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                keyAcademicGroup = result.getString("Clave");
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return keyAcademicGroup;
    }
}
