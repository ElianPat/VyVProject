/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.uv.fei.dataaccess.DataBaseManager;
import mx.uv.fei.gui.controller.App;
import mx.uv.fei.interfaces.IAdvance;

/**
 *
 * @author alexs
 */
public class AdvanceDAO implements IAdvance {

    /**
     * Se programa un avance.
     *  
     * @param advance el objeto avance a guardar.
     * @return 1 si el avance fue programado exitosamente, 0 si no lo fue.
     * @throws DAOException 
     */
    
    @Override
    public int scheduleAdvance(Advance advance) throws DAOException {
        int result = -1;
        try {
            String query = "insert into avance(titulo, descripcion, fecha_inicio, "
                    + "fecha_fin, hora_fin)"
                    + "values (?,?,?,?,?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, advance.getTitle());
            statement.setString(2, advance.getDescription());
            statement.setDate(3, advance.getStartDate());
            statement.setDate(4, advance.getEndDate());
            statement.setString(5, advance.getEndTime());
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible programar el avance", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * Se guarda el avance programado por el estudiante.
     * 
     * @param enrollment la matrícula del estudiante que programará el avance.
     * @param advanceNumber el número del avance que se programo.
     * @return 1 si el estudiante programo su avance exitosamente, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public int studentAdvance(String enrollment, int advanceNumber) throws DAOException {
        int result = -1;
        try {
            String query = "insert into estudianteAvance(Matricula_estudiante, numero_avance)"
                    + "values (?,?)";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, enrollment);
            statement.setInt(2, advanceNumber);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible programar el avance del estudiante", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * muestra los documentos guardados en un avance.
     * 
     * @param advanceNumber el número del avance programado
     * @return List con los documentos del avance, null si no tiene documentos el avance.
     * @throws DAOException 
     */

    @Override
    public List<Blob> showAdvanceDocuments(int advanceNumber) throws DAOException {
        List<Blob> listOfDocument = new ArrayList<>();
        try {
            String query = "SELECT DISTINCT evidencia FROM proyecto.estudianteavance WHERE numero_avance = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, advanceNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Blob document = result.getBlob("evidencia");
                if (document != null) {
                    listOfDocument.add(document);
                }
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener los avances", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfDocument;
    }
    
    /**
     * recupera todos los nombres de los avances de un estudiante.
     * 
     * @param enrollment la matrícula del estudiante
     * @return List con los avances, null si no hay avances.
     * @throws DAOException 
     */

    @Override
    public List<Advance> showNameAdvance(String enrollment) throws DAOException {
        List<Advance> listOfAdvance = new ArrayList<>();
        try {
            String query = "SELECT DISTINCT a.titulo FROM proyecto.avance a\n"
                    + "INNER JOIN proyecto.estudianteavance ea ON ea.numero_avance = a.num_avance\n"
                    + "WHERE ea.Matricula_estudiante= ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, enrollment);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Advance advance = new Advance();
                advance.setTitle(result.getString("titulo"));
                listOfAdvance.add(advance);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener los avances", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfAdvance;
    }
    
    /**
     * obtiene el id del avance
     * 
     * @param title el título del avance programado
     * @return 1 si se obtiene el id del avance, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public int getIdAdvance(String title) throws DAOException {
        int result;
        try {
            String query = "SELECT a.num_avance FROM proyecto.avance a INNER JOIN proyecto.estudianteavance ea "
                    + "ON ea.numero_avance = a.num_avance WHERE a.titulo = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, title);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt("num_avance");
            } else {
                throw new DAOException("No se encontró el avance con el título especificado", Status.ERROR);
            }
        } catch (SQLException ex) {
            throw new DAOException("No fue posible obtener el Id del avance", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * regresa los datos del avance
     * 
     * @param advanceNumber el número del avance
     * @return 1 si se pudo obtener los datos del avance correctamente, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public Advance getDataAdvance(int advanceNumber) throws DAOException {
        Advance advance = new Advance();
        try {
            String query = "SELECT descripcion, fecha_inicio, fecha_fin, titulo, hora_fin, Validado_director FROM proyecto.avance where num_avance = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, advanceNumber);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                advance.setDescription(result.getString("descripcion"));
                advance.setStartDate(result.getDate("fecha_inicio"));
                advance.setEndDate(result.getDate("fecha_fin"));
                advance.setTitle(result.getString("titulo"));
                advance.setEndTime(result.getString("hora_fin"));
                advance.setAdvanceState(result.getString("Validado_director"));
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible establecer la conexion con la base de datos", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return advance;
    }
    
    /**
     * muestra los avances hecho por un estudiante.
     * 
     * @param enrollment matrícula del estudiante 
     * @return List con los avances programados por un estudiante, null si no recupero nada.
     * @throws DAOException 
     */

    @Override
    public List<Advance> showAdvanceFromStudent(String enrollment) throws DAOException {
        List<Advance> listOfAdvance = new ArrayList<>();
        try {
            String query = "SELECT DISTINCT a.titulo, a.fecha_fin, a.Validado_director FROM proyecto.avance a\n"
                    + "inner join proyecto.estudianteavance ea on a.num_avance = ea.numero_avance\n"
                    + "where ea.Matricula_estudiante = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, enrollment);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                Advance advance = new Advance();
                advance.setTitle(result.getString("titulo"));
                advance.setEndDate(result.getDate("fecha_fin"));
                advance.setAdvanceState(result.getString("Validado_director"));
                listOfAdvance.add(advance);
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener los avances", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return listOfAdvance;
    }
    
    /**
     * el director valida un avance programado.
     * 
     * @param advanceNumber el número del avance
     * @param validation la validación del director
     * @return 1 si la validación fue exitosa, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public int validationAdvanceDirector(int advanceNumber, String validation) throws DAOException {
        int result = -1;
        try {
            String query = "UPDATE proyecto.avance\n"
                    + "SET Validado_director = ?\n"
                    + "WHERE num_avance = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, validation);
            statement.setInt(2, advanceNumber);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible validar el avance", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * el codirector valida un avance programado
     *  
     * @param advanceNumber el número del avance
     * @param validation la validación del director 
     * @return 1 si la validación fue exitosa, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public int validationAdvanceCodirector(int advanceNumber, String validation) throws DAOException {
        int result = -1;
        try {
            String query = "UPDATE proyecto.avance\n"
                    + "SET validado_codirector = ?\n"
                    + "WHERE num_avance = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, validation);
            statement.setInt(2, advanceNumber);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible validar el avance", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * obtiene el número del avance 
     * 
     * @param title el título del avance
     * @return 1 si se obtuvo el número del avance exitosamente, 0 si no lo fue.
     * @throws DAOException 
     */

    @Override
    public int getAdvanceNumber(String title) throws DAOException {
        int advanceNumber = 0;
        try {
            String query = "SELECT num_avance FROM proyecto.avance where proyecto.avance.titulo=?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, title);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                advanceNumber = result.getInt("num_avance");
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible obtener el número del avance", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return advanceNumber;
    }

    /**
     * actualiza las evidencias (documentos) de un avance.
     *  
     * @param advanceNumber número del avance programado.
     * @param evidence el documento
     * @return 1 si la actualización del documento fue exitoso, 0 si no lo fue.
     * @throws DAOException 
     */
    
    @Override
    public int updateEvidence(int advanceNumber, byte[] evidence) throws DAOException {
        int result;
        try {
            String query = "UPDATE proyecto.estudianteavance SET evidencia = ? WHERE numero_avance = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setBytes(1, evidence);
            statement.setInt(2, advanceNumber);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible actualizar la evidencia", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }

    /**
     * actualiza la descripción de un avance
     *  
     * @param newDescription la descripción por la que se actualizará el avance.
     * @param advanceNumber número del avance
     * @return 1 si la descripción del avance se modifico exitosamente, 0 si no lo fue.
     * @throws DAOException 
     */
    
    @Override
    public int updateAdvance(String newDescription, int advanceNumber) throws DAOException {
        int result;
        try {
            String query = "UPDATE proyecto.avance SET descripcion = ? WHERE num_avance = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, newDescription);
            statement.setInt(2, advanceNumber);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible actualizar la evidencia", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
    
    /**
     * valida que un avance no este programado previamente dado su título, esto
     * para no registrar avances con el mismo título de un estudiante (se puede 
     * repetir títulos siempre y cuando no corresponda al mismo estudiante).
     * 
     * @param advanceTitle título del avance.
     * @param enrollment matrícula del estudiante.
     * @return true si el título del avance esta duplicado, false si no lo esta.
     * @throws DAOException 
     */

    @Override
    public boolean isAdvanceDuplication(String advanceTitle, String enrollment) throws DAOException {
        boolean validateAdvance = false;
        try {
            String query = "select * From proyecto.avance a\n"
                    + "inner join proyecto.estudianteavance ea on a.num_avance = ea.numero_avance\n"
                    + "where a.titulo = ? and ea.matricula_estudiante = ?";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, advanceTitle);
            statement.setString(2, enrollment);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                validateAdvance = true;
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible validar la duplicidad del avance", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return validateAdvance;
    }

    /**
     * valida si el avance se ha entregado o no.
     *  
     * @param advanceTitle título del avance
     * @param enrollment matrícula del estudiante para validar su avance.
     * @return true si el avance programado ya fue enviado, false si no lo fue.
     * @throws DAOException 
     */
    
    @Override
    public boolean isDeliverAdvance(String advanceTitle, String enrollment) throws DAOException {
        boolean validateDeliverAdvance = false;
        try {
            String query = "SELECT 1 FROM proyecto.estudianteavance ea\n"
                    + "INNER JOIN proyecto.avance a ON a.num_avance = ea.numero_avance\n"
                    + "WHERE a.titulo = ? AND ea.matricula_estudiante = ? AND ea.evidencia IS NOT NULL LIMIT 1";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, advanceTitle);
            statement.setString(2, enrollment);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                validateDeliverAdvance = true;
            }
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible validar el envío del avance", Status.ERROR);
        }
        return validateDeliverAdvance;
    }
}
