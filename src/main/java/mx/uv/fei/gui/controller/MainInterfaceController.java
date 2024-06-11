/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.fei.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import mx.uv.fei.logic.DAOException;
import mx.uv.fei.logic.Status;
import mx.uv.fei.logic.UserDAO;
import mx.uv.fei.logic.UserRole;

/**
 * FXML Controller class
 * Este controlador aloja los tabs. Es el contenedor principal que aloja 
 * el resto de tabs.
 * @author alexs
 */
public class MainInterfaceController implements Initializable {
    
    @FXML
    private Tab tabProfessor;
    @FXML
    private Tab tabDirector;
    @FXML
    private Tab tabCodirector;
    @FXML
    private Tab tabCuerpoAcademico;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    public void enableTabs(String email, String password) throws IOException {
        try {
            UserDAO userDAO = new UserDAO();
            Map<UserRole, Boolean> userRoles = userDAO.validateUser(email);
            for (Map.Entry<UserRole, Boolean> entry : userRoles.entrySet()) {
                UserRole role = entry.getKey();
                boolean isEnabled = entry.getValue();
                if (isEnabled) {
                    switch (role) {
                        case PROFESSORWITHCOURSE:
                            tabProfessor.setDisable(false);
                            break;
                        case PROFESSORWITHOUTCOURSE:
                            tabProfessor.setDisable(true);
                            break;
                        case DIRECTOR:
                            tabDirector.setDisable(false);
                            break;
                        case CODIRECTOR:
                            tabCodirector.setDisable(false);
                            break;
                        case ACADEMICGROUP:
                            tabCuerpoAcademico.setDisable(false);
                            
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HomeAcademicGroup.fxml"));
                            Parent root = loader.load();
                            
                            HomeAcademicGroupController controller = loader.getController();
                            controller.initComponents(email);
                            
                            break;
                        default:
                            throw new IllegalArgumentException("No se pudo habilitar el tab");
                    }
                }
            }
        } catch (DAOException ex) {
            App.getLogger().warn(ex.getMessage());
            DialogGenerator.getDialog(ex.getMessage(), Status.WARNING);
        }
    }
}
