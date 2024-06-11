/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mx.uv.fei.logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import mx.uv.fei.dataaccess.DataBaseManager;
import mx.uv.fei.gui.controller.App;
import mx.uv.fei.interfaces.IPdf;

/**
 *
 * @author Palom
 */
public class PdfDAO implements IPdf {

    /**
     * añade un documento a un avance en especifico de un estudiante en especifico.
     * 
     * @param pdf el archivo pdf a añadir
     * @param enrollment la matrícula del estudiante el cuál subirá la evidencia.
     * @param advanceNumber el número del avance al cuál se le adjuntará la evidencia.
     * @return 1 si se añadio exitosamente la evidencia, 0 si no lo fue.
     * @throws DAOException 
     */
    
    @Override
    public int addEvidence(Pdf pdf, String enrollment, int advanceNumber) throws DAOException {
        int result;
        try {
            String query = "insert into estudianteavance (evidencia, Matricula_estudiante, numero_avance) "
                    + "values (?,?,?) ";
            DataBaseManager dataBaseManager = new DataBaseManager();
            Connection connection = dataBaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setBytes(1, pdf.getFile());
            statement.setString(2, enrollment);
            statement.setInt(3, advanceNumber);
            result = statement.executeUpdate();
        } catch (SQLException ex) {
            App.getLogger().error(ex.getMessage());
            throw new DAOException("No fue posible registrar el anteproyecto", Status.ERROR);
        } finally {
            DataBaseManager.close();
        }
        return result;
    }
}